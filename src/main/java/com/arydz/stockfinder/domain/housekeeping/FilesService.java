package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.housekeeping.api.FileParams;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FilesService {

    private final ExtractStooqFileService extractStooqFileService;

    public void importFile(FileParams fileParams) {

        extractStooqFileService.performFileProcessing(fileParams);
    }

}
