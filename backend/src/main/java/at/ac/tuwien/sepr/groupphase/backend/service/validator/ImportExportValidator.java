package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Validator for all import/export related inputs.
 */
@Component
public class ImportExportValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void validateSplitwiseFile(MultipartFile file) throws ValidationException {

        List<String> validationErrors = new ArrayList<>();
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            validationErrors.add("File is empty");
            throw new ValidationException("Validation for import failed!", validationErrors);
        }
        if (!file.getOriginalFilename().endsWith(".csv")) {
            validationErrors.add("File is not a .csv file");
            throw new ValidationException("Validation for import failed!", validationErrors);
        }

    }

    public void validateSplitwiseFirstLine(String[] fields) throws ValidationException {
        if (fields.length < 5) {
            throw new ValidationException("Validation for import failed!", List.of("File does not contain the required fields Date, Description, Category, Cost and Currency", "This is just a test"));
        }
        List<String> validationErrors = new ArrayList<>();
        if (!fields[0].equals("Date")) {
            validationErrors.add("First field must be 'Date'");
        }
        if (!fields[1].equals("Description")) {
            validationErrors.add("Second field must be 'Description'");
        }
        if (!fields[2].equals("Category")) {
            validationErrors.add("Third field must be 'Category'");
        }
        if (!fields[3].equals("Cost")) {
            validationErrors.add("Fourth field must be 'Cost'");
        }
        if (!fields[4].equals("Currency")) {
            validationErrors.add("Fifth field must be 'Currency'");
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation for import failed!", validationErrors);
        }
    }
}
