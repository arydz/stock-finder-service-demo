package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.chart.ChartService;
import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.common.EnvProperties;
import com.arydz.stockfinder.domain.housekeeping.api.FileParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilesService {

    private final EnvProperties properties;
    private final DefaultExtractArchiveService defaultExtractZipService;
    private final ChartService chartService;

    public Mono<HttpStatus> importChartData(Mono<FileParams> fileParams) {

        return fileParams
                .flatMap(fp -> {
                    log.info("About to import chart data from zip file");
                    FilePart uploadedZipFile = fp.getFile();
                    String fileName = uploadedZipFile.filename();
                    Path temporaryZipPath = getTransferToTemporaryPath(fileName);

                    return uploadedZipFile.transferTo(temporaryZipPath)
                            .then(Mono.zip(Mono.just(fp), Mono.just(temporaryZipPath)));
                })
                .flatMap(zip -> {
                    FileParams fp = zip.getT1();
                    Path temporaryZipPath = zip.getT2();
                    ChartTimeframeType chartTimeframeType = fp.getChartTimeframeType();
                    ExtractionMode extractionMode = fp.getExtractionMode();
                    String zipExtractionPath = defaultExtractZipService.extractZipFile(temporaryZipPath, chartTimeframeType, extractionMode);
                    return Mono.zip(Mono.just(fp), Mono.just(zipExtractionPath));
                })
                .map(zip -> {
                    FileParams fp = zip.getT1();
                    String zipExtractionPath = zip.getT2();
//                    chartService.save(fp.getChartTimeframeType(), "D:\\zip_test\\daily\\20221127\\sp500\\csv\\AAPL.csv");


                    return fp;
                })
                .then(Mono.just(HttpStatus.OK));

        /* todo
        log.info("About to save stock data from extracted zip file");
//        stooqService.saveStockFromFiles(saveStockParams, stockCandleAuditId);

        log.info("Finished importing chart data from zip file");*/
    }

    private Path getTransferToTemporaryPath(String fileName) {
        return Path.of(properties.getSourcePath()).resolve(fileName);
    }

}
