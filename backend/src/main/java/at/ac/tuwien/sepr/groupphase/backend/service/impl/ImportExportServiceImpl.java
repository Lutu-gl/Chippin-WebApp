package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.importexport.EmailSuggestionsAndContentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.importexport.ImportDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PaymentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ImportExportService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.ImportExportValidator;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImportExportServiceImpl implements ImportExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final RecipeRepository recipeRepository;
    private final ImportExportValidator importExportValidator;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ExchangeRateServiceImpl exchangeRateServiceImpl;
    private final PaymentRepository paymentRepository;

    @Override
    public EmailSuggestionsAndContentDto getEmailSuggestions(MultipartFile file, String username) throws IOException, ValidationException {
        LOGGER.trace("getEmailSuggestions({}, {})", file, username);
        importExportValidator.validateSplitwiseFile(file);

        ApplicationUser user = userRepository.findByEmail(username);
        Map<String, String> emailSuggestions = new LinkedHashMap<>();
        List<String> content = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            //firstLine[0]...Date
            //firstLine[1]...Description
            //firstLine[2]...Category
            //firstLine[3]...Cost
            //firstLine[4]...Currency
            //firstLine[5] - firstLine[max] are the Usernames and need to be transformed
            String firstLineRaw = reader.readLine();
            content.add(firstLineRaw);
            List<String> firstLine = splitCsv(firstLineRaw);
            importExportValidator.validateSplitwiseFirstLine(firstLine);
            for (int i = 5; i < firstLine.size(); i++) {
                if (user.getEmail().toLowerCase().contains(firstLine.get(i).toLowerCase())) {
                    emailSuggestions.put(firstLine.get(i), user.getEmail());
                    continue;
                }
                String similarEmail = friendshipRepository.findFriendWithSimilarEmail(firstLine.get(i), user);
                emailSuggestions.put(firstLine.get(i), similarEmail);
            }
            String line;
            reader.readLine(); // skip second line
            content.add("");
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    break;
                }
                importExportValidator.validateSplitwiseLine(splitCsv(line), firstLine.size(), content.size() + 1);
                content.add(line);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        return EmailSuggestionsAndContentDto.builder()
            .emailSuggestions(emailSuggestions)
            .content(content)
            .build();
    }

    @Override
    @Transactional
    public void importSplitwise(ImportDto importDto, String username) throws ValidationException {
        LOGGER.trace("importSplitwise({})", importDto);
        String content = importDto.getContent();

        ApplicationUser user = userRepository.findByEmail(username);
        Long groupId = importDto.getGroupId();
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Could not find Group"));

        if (!group.getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to import into this group!");
        }

        String[] lines = content.split("\n");
        List<String> firstLine = splitCsv(lines[0]);
        importExportValidator.validateSplitwiseFirstLine(firstLine);
        importExportValidator.validateSplitwiseGroupMembers(firstLine, group);

        int realLength = 0;
        for (int i = 2; i < lines.length; i++) {
            // split and validate line
            if (lines[i].trim().isEmpty()) {
                realLength = i;
                break;
            }
            List<String> line = splitCsv(lines[i]);
            importExportValidator.validateSplitwiseLine(line, firstLine.size(), i + 1);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 2; i < realLength; i++) {
            // break on second empty line
            if (lines[i].trim().isEmpty()) {
                break;
            }

            // split line
            List<String> line = splitCsv(lines[i]);

            ApplicationUser payer = userRepository.findByEmail(firstLine.get(getPayerIndex(line, i + 1)));
            Expense expense = Expense.builder()
                .name(line.get(1))
                .category(getEnumConstantOrDefault(line.get(2), Category.class, Category.Other))
                //.amount(exchangeRateServiceImpl.convertToEuro(Double.parseDouble(line[3]), line[4]))
                .amount(Double.parseDouble(line.get(3)))
                .date(LocalDate.parse(line.get(0), dateTimeFormatter).atStartOfDay())
                .payer(payer)
                .group(group)
                .participants(getParticipants(line, firstLine))
                .deleted(false)
                .archived(false)
                .build();

            Expense expenseSaved = expenseRepository.save(expense);

            //BUDET AKTUALISIEREN

            Activity activityForExpense = Activity.builder()
                .category(ActivityCategory.EXPENSE)
                .expense(expenseSaved)
                .timestamp(expenseSaved.getDate())
                .group(expenseSaved.getGroup())
                .user(payer)
                .build();

            activityRepository.save(activityForExpense);
        }
    }

    private int getPayerIndex(List<String> line, int lineNumber) throws ValidationException {
        for (int i = 5; i < line.size(); i++) {
            if (Double.parseDouble(line.get(i)) > 0) {
                return i;
            }
        }
        throw new ValidationException("Validation for import failed!", List.of("No payer found in line " + lineNumber));
    }

    private <T extends Enum<T>> T getEnumConstantOrDefault(String input, Class<T> enumClass, T defaultValue) {
        try {
            return Enum.valueOf(enumClass, input);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    private Map<ApplicationUser, Double> getParticipants(List<String> line, List<String> firstLine) throws ValidationException {
        double totalAmount = Double.parseDouble(line.get(3));
        Map<ApplicationUser, Double> participants = new HashMap<>();
        for (int i = 5; i < line.size(); i++) {
            double amount = Double.parseDouble(line.get(i));
            ApplicationUser user = userRepository.findByEmail(firstLine.get(i));
            if (user == null) {
                throw new ValidationException("Validation for import failed!", List.of("User " + firstLine.get(i) + " not found"));
            }
            if (amount < 0) {
                participants.put(user, Math.abs(amount) / totalAmount);
            } else if (amount > 0) {
                participants.put(user, (totalAmount - amount) / totalAmount);
            }
        }
        return participants;
    }

    private List<String> splitCsv(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int length = line.length();

        for (int i = 0; i < length; i++) {
            char ch = line.charAt(i);
            if (ch == '\"') {
                if (inQuotes && i + 1 < length && line.charAt(i + 1) == '\"') {
                    // Handle escaped double quote
                    current.append('\"');
                    i++; // Skip the next quote
                } else {
                    inQuotes = !inQuotes; // Toggle inQuotes flag
                }
            } else if (ch == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0); // Clear the current StringBuilder
            } else {
                current.append(ch);
            }
        }

        result.add(current.toString().trim()); // Add the last element

        return result;
    }

    @Override
    @Transactional
    public byte[] exportCsv(long groupId, String username) {

        ApplicationUser user = userRepository.findByEmail(username);
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Could not find Group"));
        if (!group.getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to export from this group!");
        }
        List<Expense> expenses = expenseRepository.findAllByGroupId(groupId);
        List<Payment> payments = paymentRepository.findAllByGroupId(groupId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write("Date,Description,Category,Cost,Currency,".getBytes());
            List<String> users = new ArrayList<>();
            for (ApplicationUser u : group.getUsers()) {
                users.add(u.getEmail());
            }
            baos.write(String.join(",", users).getBytes());
            baos.write("\n".getBytes());

            List<List<String>> lines = new ArrayList<>();
            for (Expense expense : expenses) {
                if (expense.isDeleted()) {
                    continue;
                }
                List<String> line = new ArrayList<>();

                line.add(expense.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                line.add(expense.getName());
                line.add(expense.getCategory().name());
                line.add(String.valueOf(expense.getAmount()));
                line.add("EUR");

                for (ApplicationUser u : group.getUsers()) {
                    if (u.equals(expense.getPayer())) {
                        line.add(String.format(Locale.US, "%.2f", expense.getAmount() - expense.getParticipants().get(u) * expense.getAmount()));
                    } else if (expense.getParticipants().containsKey(u)) {
                        line.add(String.format(Locale.US, "%.2f", expense.getParticipants().get(u) * expense.getAmount() * (-1)));
                    } else {
                        line.add("0.00");
                    }
                }

                lines.add(line);
            }

            for (Payment payment : payments) {
                if (payment.isDeleted()) {
                    continue;
                }
                List<String> line = new ArrayList<>();

                line.add(payment.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                line.add("Settle debts");
                line.add("Payment");
                line.add(String.valueOf(payment.getAmount()));
                line.add("EUR");

                for (ApplicationUser u : group.getUsers()) {
                    if (u.equals(payment.getPayer())) {
                        line.add(String.format(Locale.US, "%.2f", payment.getAmount()));
                    } else if (u.equals(payment.getReceiver())) {
                        line.add(String.format(Locale.US, "%.2f", payment.getAmount() * (-1)));
                    } else {
                        line.add("0.00");
                    }
                }

                lines.add(line);
            }

            // sort by date ascending
            lines.sort(Comparator.comparing(List::getFirst));

            for (List<String> line : lines) {
                baos.write(String.join(",", line).getBytes());
                baos.write("\n".getBytes());
            }
            return baos.toByteArray();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public byte[] exportRecipe(long recipeId) {
        Optional<Recipe> optional = recipeRepository.findById(recipeId);

        if (optional.isEmpty()) {
            throw new NotFoundException("Could not find Recipe");
        }
        Recipe recipe = optional.get();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(baos);

            // Initialize PDF document
            PdfDocument pdfDoc = new PdfDocument(writer);

            // Initialize document
            Document document = new Document(pdfDoc);

            // Add recipe name
            Paragraph nameParagraph = new Paragraph(recipe.getName())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(18)
                .setFontColor(ColorConstants.BLACK);
            document.add(nameParagraph);


            Paragraph descriptionParagraph = new Paragraph("\n" + recipe.getDescription())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12)
                .setMultipliedLeading(1.5f)
                .setFontColor(ColorConstants.BLACK);
            document.add(descriptionParagraph);

            // Add portion size
            Paragraph portionSizeParagraph = new Paragraph("Ingredients for " + recipe.getPortionSize() + " " + (recipe.getPortionSize() == 1 ? "person" : "people"))
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12)
                .setFontColor(ColorConstants.BLACK)
                .setMultipliedLeading(1.5f);
            document.add(portionSizeParagraph);

            // Add a table of ingredients
            float[] columnWidths = {3, 2}; // Adjust column widths as necessary
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            DeviceRgb headerBackgroundColor = new DeviceRgb(240, 240, 240);
            table.addHeaderCell(new Cell().add(new Paragraph("Ingredient")).setBackgroundColor(headerBackgroundColor));
            table.addHeaderCell(new Cell().add(new Paragraph("Amount")).setBackgroundColor(headerBackgroundColor));

            boolean alternateColor = true;
            for (Item item : recipe.getIngredients()) {
                Cell cellIngredient = new Cell().add(new Paragraph(item.getDescription()));
                Cell cellAmount = new Cell().add(new Paragraph(item.getAmount() + " " + item.getUnit()));

                if (alternateColor) {
                    cellIngredient.setBackgroundColor(new DeviceRgb(230, 230, 255));
                    cellAmount.setBackgroundColor(new DeviceRgb(230, 230, 255));
                } else {
                    cellIngredient.setBackgroundColor(new DeviceRgb(255, 255, 255));
                    cellAmount.setBackgroundColor(new DeviceRgb(255, 255, 255));
                }

                if (table.getNumberOfRows() == 0) {
                    cellIngredient.setBorderTop(new SolidBorder(1));
                    cellAmount.setBorderTop(new SolidBorder(1));
                }

                table.addCell(cellIngredient);
                table.addCell(cellAmount);

                alternateColor = !alternateColor;
            }
            document.add(table);


            Paragraph ownerEmailParagraph = new Paragraph("Recipe created by " + recipe.getOwner().getEmail())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY);
            document.add(ownerEmailParagraph);


            // Close document
            document.close();


            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    // Sanitize CSV field to prevent CSV Injection
    private String sanitizeCsvField(String field) {

        field = field.replace("=", "");
        field = field.replace("+", "");
        field = field.replace("-", "");
        field = field.replace("@", "");
        field = field.replace("\t", "");
        field = field.replace("\r", "");
        field = field.replace("'", "\"'\"");
        // Wrap each cell field in double quotes, prepend with a single quote, and escape every double quote
        return "\"" + field.replace("\"", "\"\"") + "\"";
    }


}

