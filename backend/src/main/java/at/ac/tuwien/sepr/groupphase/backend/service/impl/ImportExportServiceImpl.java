package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ImportExportService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class ImportExportServiceImpl implements ImportExportService {

    private GroupRepository groupRepository;

    private ExpenseRepository expenseRepository;


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
        }

        return null;
    }


}

