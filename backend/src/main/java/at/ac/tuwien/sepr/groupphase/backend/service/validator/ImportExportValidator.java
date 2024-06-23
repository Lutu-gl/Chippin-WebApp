package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ImportExportValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;

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

    public void validateSplitwiseFirstLine(List<String> fields) throws ValidationException {
        if (fields.size() < 5) {
            throw new ValidationException("Validation for import failed!", List.of("File does not contain the required fields Date, Description, Category, Cost and Currency"));
        }
        List<String> validationErrors = new ArrayList<>();
        if (!fields.get(0).equals("Date")) {
            validationErrors.add("First field must be 'Date'");
        }
        if (!fields.get(1).equals("Description")) {
            validationErrors.add("Second field must be 'Description'");
        }
        if (!fields.get(2).equals("Category")) {
            validationErrors.add("Third field must be 'Category'");
        }
        if (!fields.get(3).equals("Cost")) {
            validationErrors.add("Fourth field must be 'Cost'");
        }
        if (!fields.get(4).equals("Currency")) {
            validationErrors.add("Fifth field must be 'Currency'");
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation for import failed!", validationErrors);
        }
    }

    public void validateSplitwiseGroupMembers(List<String> fields, GroupEntity group) throws ValidationException {
        List<String> validationErrors = new ArrayList<>();
        List<String> seenUsers = new ArrayList<>();
        for (int i = 5; i < fields.size(); i++) {
            ApplicationUser user = userRepository.findByEmail(fields.get(i));
            if (user == null) {
                validationErrors.add("User with email " + fields.get(i) + " does not exist");
            }
            if (!group.getUsers().contains(user)) {
                validationErrors.add("User with email " + fields.get(i) + " is not a member of the group");
            }
            if (seenUsers.contains(fields.get(i))) {
                validationErrors.add("User with email " + fields.get(i) + " is listed multiple times");
            }
            seenUsers.add(fields.get(i));
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation for import failed!", validationErrors);
        }
    }

    private boolean checkValidDescription(String desc) {
        if (desc.length() > 255) {
            return false;
        }

        desc = desc.replaceAll("\"{2}", "");

        if (desc.charAt(0) == '\"' && desc.charAt(desc.length() - 1) == '\"') {
            desc = desc.substring(1, desc.length() - 1);
        }

        return !desc.contains("\"");
    }

    public void validateSplitwiseLine(List<String> fields, int lineLength, int lineNumber) throws ValidationException {
        List<String> validationErrors = new ArrayList<>();
        if (fields.size() != lineLength) {
            validationErrors.add("Line " + lineNumber + " does not contain the required number of fields");
        }
        if (!fields.get(0).matches("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")) {
            validationErrors.add("Date in line " + lineNumber + " must be of format yyyy-MM-dd");
        }
        //if (!fields.get(1).matches("\"?((\"{2})|([a-zA-ZäöüÄÖÜß0-9+\\-*!?_,;/ ']))+\"?") || fields.get(1).length() > 255) {
        if (!checkValidDescription(fields.get(1))) {
            validationErrors.add("Description in line " + lineNumber + " was not correct");
        }
        if (!fields.get(2).matches("[a-zA-Z -/]+") || fields.get(2).length() > 255) {
            validationErrors.add("Category in line " + lineNumber + " was not correct");
        }
        if (!fields.get(3).matches("\\d{1,7}(\\.\\d{2})?")) {
            validationErrors.add("Cost in line " + lineNumber + " was not correct. Expected format is 1234.56");
        }
        if (!fields.get(4).matches("[A-Z]{3}")) {
            validationErrors.add("Currency in line " + lineNumber + " was not correct. Expected format is three capital letters");
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation for import failed!", validationErrors);
        }

        double totalAmount = Double.parseDouble(fields.get(3));
        double calculatedAmount = 0.0;
        boolean payerFound = false;
        for (int i = 5; i < lineLength; i++) {
            if (!fields.get(i).matches("-?\\d{1,7}(\\.\\d{2})?") || fields.get(i).length() > 255) {
                validationErrors.add("Field " + (i + 1) + " in line " + lineNumber + " was not correct");
            }

            if (!validationErrors.isEmpty()) {
                throw new ValidationException("Validation for import failed!", validationErrors);
            }

            double amount = Double.parseDouble(fields.get(i));
            if (amount < 0) {
                calculatedAmount += Math.abs(amount);
            } else if (amount > 0) {
                payerFound = true;
                calculatedAmount += totalAmount - amount;
            }
        }

        if (!payerFound) {
            validationErrors.add("No payer found in line " + lineNumber);
        }

        if (Math.abs(totalAmount - calculatedAmount) > 0.001) {
            validationErrors.add("Amounts in line " + lineNumber + " do not add up");
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation for import failed!", validationErrors);
        }
    }
}
