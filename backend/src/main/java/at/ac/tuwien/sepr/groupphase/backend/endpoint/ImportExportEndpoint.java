package at.ac.tuwien.sepr.groupphase.backend.endpoint;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.ImportExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/")
@Slf4j
@RequiredArgsConstructor
public class ImportExportEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ImportExportService importExportService;

    @Secured("ROLE_USER")
    @PostMapping("/import/splitwise")
    public GroupDetailDto importSplitwise(@RequestParam("file") MultipartFile file) throws IOException {
        LOGGER.trace("POST: /api/v1/import/splitwise : {}", file.getName());

        return importExportService.importSplitwise(file);
    }
}
