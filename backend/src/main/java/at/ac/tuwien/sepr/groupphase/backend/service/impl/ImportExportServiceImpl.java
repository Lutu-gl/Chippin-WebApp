package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ImportExportService;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImportExportServiceImpl implements ImportExportService {

    private final GroupRepository groupRepository;

    private final ExpenseRepository expenseRepository;

    private final RecipeRepository recipeRepository;


    @Transactional
    public GroupDetailDto importSplitwise(MultipartFile file) throws IOException {


        if (file.isEmpty()) {
            throw new IOException("Could not find file");
        }

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            //firstLine[0]...Date
            //firstLine[1]...Description
            //firstLine[2]...Category
            //firstLine[3]...Cost
            //firstLine[4]...Currency
            //firstLine[5] - firstLine[max] are the Usernames and need to be transformed
            String[] firstLine = reader.readLine().split(",");
            String line;
            String[] linearray;
            while ((line = reader.readLine()) != null) {
                linearray = line.split(",");
                //TODO Create expense here, dont forget to use currency converter

            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        return null;
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

            // Add portion size
            Paragraph portionSizeParagraph = new Paragraph("For " + recipe.getPortionSize() + " " + (recipe.getPortionSize() == 1 ? "person" : "people"))
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12)
                .setFontColor(ColorConstants.BLACK)
                .setMultipliedLeading(1.5f);
            document.add(portionSizeParagraph);


            Paragraph descriptionParagraph = new Paragraph("\n" + recipe.getDescription())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12)
                .setMultipliedLeading(1.5f)
                .setFontColor(ColorConstants.BLACK);
            document.add(descriptionParagraph);

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


            Paragraph ownerEmailParagraph = new Paragraph("Owner: " + recipe.getOwner().getEmail())
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


}

