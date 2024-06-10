package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.importExport.EmailSuggestionsAndContentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.importExport.ImportDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImportExportService {

    /**
     * This function takes a .csv file and returns a map of email suggestions for the import of splitwise.
     * As well as the content of the file.
     *
     * @return a EmailSuggestionsAndContent dto object
     */
    EmailSuggestionsAndContentDto getEmailSuggestions(MultipartFile file, String username) throws IOException, ValidationException;

    /**
     * This function takes the content of a .csv file and imports it into the database.
     *
     * @param importDto the content of the file and the group id
     * @param username  the email of the user who requests the import
     */
    void importSplitwise(ImportDto importDto, String username) throws ValidationException;

    /**
     * Export a recipe with the given id as a pdf file.
     *
     * @param recipeId the id of the recipe to export
     * @return the Pdf as ByeArrayInputStream
     */
    byte[] exportRecipe(long recipeId);
}
