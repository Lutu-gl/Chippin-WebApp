package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.ImportExportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/")
@Slf4j
@RequiredArgsConstructor
public class ImportExportEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ImportExportService importExportService;
    private final ObjectMapper objectMapper;

    @Secured("ROLE_USER")
    @PostMapping("/import/splitwise")
    public GroupDetailDto importSplitwise(@RequestParam("file") MultipartFile file) throws IOException {
        LOGGER.trace("POST: /api/v1/import/splitwise : {}", file.getName());

        return importExportService.importSplitwise(file);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "recipe/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] exportRecipe(@PathVariable long id) {
        LOGGER.trace("GET /api/v1/recipe/{}/pdf", id);


        return importExportService.exportRecipe(id);
    }
}
