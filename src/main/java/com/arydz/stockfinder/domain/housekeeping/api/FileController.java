package com.arydz.stockfinder.domain.housekeeping.api;

import com.arydz.stockfinder.domain.housekeeping.FilesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @PostMapping(value = "import")
    public Mono<HttpStatus> manuallyImport(Mono<FileParams> importFileParams) {

        return filesService.importChartData(importFileParams);
    }
}
