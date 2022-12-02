package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.chart.ChartService;
import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.chart.model.ChartDataDefinition;
import com.arydz.stockfinder.domain.common.EnvProperties;
import com.arydz.stockfinder.domain.housekeeping.api.FileParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.list;

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


//                    return Mono.zip(Mono.just(fp), Mono.just(temporaryZipPath));

                })
                .flatMap(zip -> {
                    FileParams fp = zip.getT1();
                    Path temporaryZipPath = zip.getT2();
                    ChartTimeframeType chartTimeframeType = fp.getChartTimeframeType();
                    ExtractionMode extractionMode = fp.getExtractionMode();
                    String zipExtractionPath = defaultExtractZipService.extractZipFile(temporaryZipPath, chartTimeframeType, extractionMode);
                    return Mono.zip(Mono.just(fp), Mono.just(zipExtractionPath));
//                    return Mono.zip(Mono.just(fp), Mono.just("D:\\zip_test\\daily\\20221129"));
                })
                .map(zip -> {
                    FileParams fp = zip.getT1();
                    String zipExtractionPath = zip.getT2();

                    try (Stream<Path> pathStream = list(Path.of(zipExtractionPath))) {
                        Set<Path> importableFolderList = getImportableFolders(pathStream);
                        chartService.save(fp.getChartTimeframeType(), fp.getExtractionMode(), importableFolderList);
                    } catch (IOException e) {
                        throw new IllegalArgumentException(String.format("Can't list files from %s", zipExtractionPath));
                    }

                    return fp;
                })
                .doOnError(e -> {

                    e.printStackTrace();

                    System.out.println(e);
                })
                .then(Mono.just(HttpStatus.OK));

        /* todo
        log.info("About to save stock data from extracted zip file");
//        stooqService.saveStockFromFiles(saveStockParams, stockCandleAuditId);

        log.info("Finished importing chart data from zip file");*/
    }

    private static Set<Path> getImportableFolders(Stream<Path> pathStream) {
        return pathStream
                .filter(folderPath -> {
                    Path fileName = folderPath.getFileName();
                    return ChartDataDefinition.DEFAULT_VENDOR.isImportable(fileName.toString());
                }).collect(Collectors.toSet());
    }

    private Path getTransferToTemporaryPath(String fileName) {
        return properties.getSourcePath().resolve(fileName);
    }

}
