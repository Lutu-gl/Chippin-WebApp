package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImportExportService {

    /**
     * This function takes in a .csv file with the export standard of splitwise and creates a group with the given information.
     *
     * @param file the file to import
     * @return a GroupDetailDto of the created file
     */
    GroupDetailDto importSplitwise(MultipartFile file) throws IOException;


}
