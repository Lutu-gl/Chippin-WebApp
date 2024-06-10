package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.importExport.EmailSuggestionsAndContentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.importExport.ImportDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ImportExportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    private final ObjectMapper objectMapper;

    @Secured("ROLE_USER")
    @PostMapping(value = "import/splitwise/email-suggestions")
    public EmailSuggestionsAndContentDto getEmailSuggestions(@RequestParam("file") MultipartFile file) throws IOException, ValidationException {
        LOGGER.trace("POST /api/v1/import/splitwise/email-suggestions");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return importExportService.getEmailSuggestions(file, authentication.getName());
    }

    @Secured("ROLE_USER")
    @PostMapping("/import/splitwise")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void importSplitwise(@Valid @RequestBody ImportDto importDto) throws ValidationException {
        LOGGER.trace("POST: /api/v1/import/splitwise : {}", importDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        importExportService.importSplitwise(importDto, authentication.getName());
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@securityService.canAccessRecipe(#id)")
    @GetMapping(value = "recipe/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] exportRecipe(@PathVariable long id) {
        LOGGER.trace("GET /api/v1/recipe/{}/pdf", id);


        return importExportService.exportRecipe(id);
    }
}
