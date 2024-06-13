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
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.FriendshipRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
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
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
            String[] firstLine = firstLineRaw.split(";");
            importExportValidator.validateSplitwiseFirstLine(firstLine);
            for (int i = 5; i < firstLine.length; i++) {
                if (user.getEmail().toLowerCase().contains(firstLine[i].toLowerCase())) {
                    emailSuggestions.put(firstLine[i], user.getEmail());
                    continue;
                }
                String similarEmail = friendshipRepository.findFriendWithSimilarEmail(firstLine[i], user);
                emailSuggestions.put(firstLine[i], similarEmail);
            }
            String line;
            while ((line = reader.readLine()) != null) {
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
        String[] firstLine = sanitizeCsvField(lines[0]).split(";");
        importExportValidator.validateSplitwiseFirstLine(firstLine);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 1; i < lines.length; i++) {
            String[] line = lines[i].split(";");


            // Sanitize each field of the CSV
            for (int j = 0; j < line.length; j++) {
                line[j] = sanitizeCsvField(line[j]);
            }

            Expense expense = Expense.builder()
                .name(line[1])
                .category(getEnumConstantOrDefault(line[2], Category.class, Category.Other))
                //.amount(exchangeRateServiceImpl.convertToEuro(Double.parseDouble(line[3]), line[4]))
                .amount(Double.parseDouble(line[3]))
                .date(LocalDate.parse(line[0], dateTimeFormatter).atStartOfDay())
                .payer(userRepository.findByEmail(firstLine[getPayerIndex(line)]))
                .group(group)
                .participants(getParticipants(line, firstLine))
                .deleted(false)
                .archived(false)
                .build();

            Expense expenseSaved = expenseRepository.save(expense);

            Activity activityForExpense = Activity.builder()
                .category(ActivityCategory.EXPENSE)
                .expense(expenseSaved)
                .timestamp(expenseSaved.getDate())
                .group(expenseSaved.getGroup())
                .user(user)
                .build();

            activityRepository.save(activityForExpense);
        }
    }

    private int getPayerIndex(String[] line) throws ValidationException {
        for (int i = 5; i < line.length; i++) {
            if (Double.parseDouble(line[i]) > 0) {
                return i;
            }
        }
        throw new ValidationException("Validation for import failed!", List.of("No payer found"));
    }

    private <T extends Enum<T>> T getEnumConstantOrDefault(String input, Class<T> enumClass, T defaultValue) {
        try {
            return Enum.valueOf(enumClass, input);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    private Map<ApplicationUser, Double> getParticipants(String[] line, String[] firstLine) {
        double amount = Double.parseDouble(line[3]);
        Map<ApplicationUser, Double> participants = new HashMap<>();
        for (int i = 5; i < line.length; i++) {
            participants.put(userRepository.findByEmail(firstLine[i]), Math.abs(Double.parseDouble(line[i])) / amount);
        }
        return participants;
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
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write("Date;Description;Category;Cost;Currency;".getBytes());
            for (ApplicationUser u : group.getUsers()) {
                baos.write((u.getEmail() + ";").getBytes());
            }
            baos.write("\n".getBytes());
            for (Expense expense : expenses) {
                baos.write((expense.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ";").getBytes());
                baos.write((expense.getName() + ";").getBytes());
                baos.write((expense.getCategory().name() + ";").getBytes());
                baos.write((expense.getAmount() + ";").getBytes());
                baos.write("EUR;".getBytes());

                for (ApplicationUser u : group.getUsers()) {
                    if (u.equals(expense.getPayer())) {
                        baos.write((String.format("%.2f", expense.getAmount() - expense.getParticipants().get(u) * expense.getAmount()) + ";").getBytes());
                    } else if (expense.getParticipants().containsKey(u)) {
                        baos.write((String.format("%.2f", expense.getParticipants().get(u) * expense.getAmount() * (-1)) + ";").getBytes());
                    } else {
                        baos.write("0;".getBytes());
                    }
                }
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

