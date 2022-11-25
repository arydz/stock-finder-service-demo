package com.arydz.stockfinder.domain.housekeeping.api;

import com.arydz.stockfinder.domain.housekeeping.FilesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/file/")
@AllArgsConstructor
public class FileController {

    private final FilesService filesService;

    @PostMapping(value = "import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<String> manuallyImport(FileParams importFileParams) {

        filesService.importChartData(importFileParams);
        return Mono.just("File imported");
    }
}
