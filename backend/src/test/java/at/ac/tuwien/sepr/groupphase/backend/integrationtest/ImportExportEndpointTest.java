package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.debt.DebtGroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.importexport.EmailSuggestionsAndContentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.importexport.ImportDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.DebtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ImportExportEndpointTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DebtService debtService;

    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };

    private final String FILE_CONTENT = "Date,Description,Category,Cost,Currency,ImportUser1,ImportUser2,ImportUser3,ImportUser4,ImportUser5,ImportUser6\n" +
        "\n" +
        "2024-03-28,drink store bier,Groceries,39.90,EUR,-9.98,39.90,0.00,-9.98,-9.97,-9.97\n" +
        "2024-03-28,Essen ,Dining out,95.00,EUR,0.00,-19.90,-19.40,-17.90,75.10,-17.90\n" +
        "2024-03-28,eurospar milland,General,24.57,EUR,-4.09,-4.10,20.48,-4.09,-4.10,-4.10\n" +
        "2024-03-28,Antonius bierl,General,20.00,EUR,-5.00,0.00,0.00,15.00,-5.00,-5.00\n" +
        "2024-03-29,salot,General,17.27,EUR,-2.88,14.39,-2.88,-2.88,-2.87,-2.88\n" +
        "2024-03-29,Olli paid Peter C.,Payment,17.90,EUR,0.00,0.00,0.00,0.00,-17.90,17.90\n" +
        "2024-04-05,Schulden ausgleichen,General,12.86,EUR,0.00,-12.86,0.00,12.86,0.00,0.00\n" +
        "2024-04-05,Schulden ausgleichen,General,5.00,EUR,0.00,0.00,0.00,-5.00,0.00,5.00\n" +
        "2024-04-05,Schulden ausgleichen,General,12.85,EUR,0.00,-12.85,0.00,0.00,0.00,12.85\n" +
        "2024-04-07,Schulden ausgleichen,General,15.30,EUR,0.00,0.00,15.30,0.00,-15.30,0.00\n" +
        "2024-04-19,Lukas S. paid Eva H.,Payment,4.09,EUR,0.00,0.00,-4.09,4.09,0.00,0.00\n" +
        "2024-05-02,Schulden ausgleichen,General,1.22,EUR,0.00,1.22,-1.22,0.00,0.00,0.00\n" +
        "2024-05-25,Schulden ausgleichen,General,12.86,EUR,12.86,-12.86,0.00,0.00,0.00,0.00\n" +
        "\n" +
        "2024-06-14,Total balance, , ,EUR,-9.09,-7.06,8.19,-7.90,19.96,-4.10";

    private final String FILE_CONTENT_EMAIL = "Date,Description,Category,Cost,Currency,importUser1@example.com,importUser2@example.com,importUser3@example.com,importUser4@example.com,importUser5@example.com,importUser6@example.com\n" +
        "\n" +
        "2024-03-28,drink store bier,Groceries,39.90,EUR,-9.98,39.90,0.00,-9.98,-9.97,-9.97\n" +
        "2024-03-28,Essen ,Dining out,95.00,EUR,0.00,-19.90,-19.40,-17.90,75.10,-17.90\n" +
        "2024-03-28,eurospar milland,General,24.57,EUR,-4.09,-4.10,20.48,-4.09,-4.10,-4.10\n" +
        "2024-03-28,Antonius bierl,General,20.00,EUR,-5.00,0.00,0.00,15.00,-5.00,-5.00\n" +
        "2024-03-29,salot,General,17.27,EUR,-2.88,14.39,-2.88,-2.88,-2.87,-2.88\n" +
        "2024-03-29,Olli paid Peter C.,Payment,17.90,EUR,0.00,0.00,0.00,0.00,-17.90,17.90\n" +
        "2024-04-05,Schulden ausgleichen,General,12.86,EUR,0.00,-12.86,0.00,12.86,0.00,0.00\n" +
        "2024-04-05,Schulden ausgleichen,General,5.00,EUR,0.00,0.00,0.00,-5.00,0.00,5.00\n" +
        "2024-04-05,Schulden ausgleichen,General,12.85,EUR,0.00,-12.85,0.00,0.00,0.00,12.85\n" +
        "2024-04-07,Schulden ausgleichen,General,15.30,EUR,0.00,0.00,15.30,0.00,-15.30,0.00\n" +
        "2024-04-19,Lukas S. paid Eva H.,Payment,4.09,EUR,0.00,0.00,-4.09,4.09,0.00,0.00\n" +
        "2024-05-02,Schulden ausgleichen,General,1.22,EUR,0.00,1.22,-1.22,0.00,0.00,0.00\n" +
        "2024-05-25,Schulden ausgleichen,General,12.86,EUR,12.86,-12.86,0.00,0.00,0.00,0.00\n" +
        "\n" +
        "2024-06-14,Total balance, , ,EUR,-9.09,-7.06,8.19,-7.90,19.96,-4.10";
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetEmailSuggestions() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "import.csv",
            "text/csv",
            new ByteArrayInputStream(FILE_CONTENT.getBytes()));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/import/splitwise/email-suggestions")
                .file(file)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("importUser1@example.com", ADMIN_ROLES))
            )
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        EmailSuggestionsAndContentDto responseDto = objectMapper.readValue(jsonResponse, EmailSuggestionsAndContentDto.class);

        Map<String, String> suggestions = responseDto.getEmailSuggestions();
        for (int i = 0; i < 6; i++) {
            assertEquals("importUser" + (i + 1) + "@example.com", suggestions.get("ImportUser" + (i + 1)));
        }
    }

    @Test
    public void testImportSplitwise() throws Exception {

        GroupEntity group = groupRepository.findByGroupName("ImportTestGroup");

        DebtGroupDetailDto debtDto = debtService.getById("importUser4@example.com", group.getId());
        Map<String, Double> debtsBefore = debtDto.getMembersDebts();

        ImportDto importDto = ImportDto.builder().groupId(group.getId()).content(FILE_CONTENT_EMAIL).build();

        String json = objectMapper.writeValueAsString(importDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/import/splitwise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("importUser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isNoContent());

        debtDto = debtService.getById("importUser4@example.com", group.getId());
        Map<String, Double> debtsAfter = debtDto.getMembersDebts();


        double delta = 0.0001;
        assertEquals(debtsBefore.get("importUser1@example.com") + 5, debtsAfter.get("importUser1@example.com"), delta);
        assertEquals(debtsBefore.get("importUser2@example.com"), debtsAfter.get("importUser2@example.com"), delta);
        assertEquals(debtsBefore.get("importUser3@example.com"), debtsAfter.get("importUser3@example.com"), delta);
        assertEquals(debtsBefore.get("importUser5@example.com") - 12.90, debtsAfter.get("importUser5@example.com"), delta);
        assertEquals(debtsBefore.get("importUser6@example.com"), debtsAfter.get("importUser6@example.com"), delta);

    }

    @Test
    public void testExport() throws Exception {
        GroupEntity group = groupRepository.findByGroupName("ExportTestGroup");

        byte[] response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/export/" + group.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("importUser1@example.com", ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

        String text = new String(response);
        assertNotNull(text);
        String[] lines = text.split("\n");
        assertEquals("Date,Description,Category,Cost,Currency,importUser1@example.com,importUser2@example.com,importUser3@example.com,importUser4@example.com,importUser5@example.com,importUser6@example.com", lines[0]);
        assertEquals("testExpense0,Food,100.00,EUR,40.00,-40.00,0.00,0.00,0.00,0.00", lines[1].substring(11));
        assertEquals("testExpense1,Food,100.00,EUR,50.00,-20.00,-30.00,0.00,0.00,0.00", lines[2].substring(11));
        assertEquals("testExpense2,Food,100.00,EUR,-10.00,90.00,-80.00,0.00,0.00,0.00", lines[3].substring(11));
        assertEquals("Settle debts,Payment,20.00,EUR,-20.00,20.00,0.00,0.00,0.00,0.00", lines[4].substring(11));
        assertEquals("Settle debts,Payment,420.00,EUR,0.00,0.00,0.00,0.00,420.00,-420.00", lines[5].substring(11));
    }
}
