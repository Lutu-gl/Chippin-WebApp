package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.entity.ExchangeRate;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExchangeRateRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ExchangeRateService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ImportExportServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.RecipeServiceImpl;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A simple test to see if getting the exchange rate getting works.
 * Do not use this in the default Test setting as the API Usage is limited per month
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImportExportServiceTest {


    @Autowired
    private RecipeServiceImpl recipeServiceImpl;
    @Autowired
    private ImportExportServiceImpl importExportServiceImpl;
    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    public void givenRecipeId_whenCreatePdf_ThenCreatedPdfContainsKeywords() throws IOException {


        byte[] pdfContent = importExportServiceImpl.exportRecipe(recipeRepository.findAll().getFirst().getId());

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfContent)));
        String text = PdfTextExtractor.getTextFromPage(pdfDoc.getFirstPage());

        assertAll(
            () -> assertTrue(text.contains("Owner:")),
            () -> assertTrue(text.contains("Ingredients for")),
            () -> assertFalse(text.contains("Not in the recipe"))
        );

    }
}
