package com.arydz.stockfinder.domain.file;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.chart.model.ChartDataDefinition;
import com.arydz.stockfinder.domain.common.EnvProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.nio.file.Files.list;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final EnvProperties properties;
    private final DefaultExtractArchiveService defaultExtractZipService;

    public Mono<Path> uploadZipFile(Mono<FilePart> mono) {

        return mono.flatMap(uploadedZipFile -> {
            log.info("About to upload zip file");
            String fileName = uploadedZipFile.filename();
            Path temporaryZipPath = getTransferToTemporaryPath(fileName);
            tryCreateTemporaryDirectories(temporaryZipPath);
            return uploadedZipFile.transferTo(temporaryZipPath).then(Mono.just(temporaryZipPath));
        });
    }

    public Mono<Tuple2<Path, Path>> extractZipFile(Tuple3<Path, ChartTimeframeType, ExtractionMode> zip) {
        log.info("About to extract data from zip file");
        Path temporaryZipPath = zip.getT1();
        ChartTimeframeType chartTimeframeType = zip.getT2();
        ExtractionMode extractionMode = zip.getT3();
        Path zipExtractionPath = defaultExtractZipService.extractZipFile(temporaryZipPath, chartTimeframeType, extractionMode);

        return Mono.just(temporaryZipPath).zipWith(Mono.just(zipExtractionPath));
    }

    public Mono<Long> performExtractedFiles(Path zipExtractionPath, Function<Set<Path>, Mono<Long>> function) {
        try (Stream<Path> pathStream = list(zipExtractionPath)) {
            Set<Path> importableFolderList = getImportableFolders(pathStream);

            Mono<Long> totalCountMono = function.apply(importableFolderList);

            log.info("Finished saving data");
            return totalCountMono;
        } catch (IOException e) {
            throw new IllegalArgumentException(format("Can't list files from %s", zipExtractionPath));
        }
    }

    public Set<Path> getImportableFolders(Stream<Path> pathStream) {
        return pathStream
                .filter(folderPath -> {
                    Path fileName = folderPath.getFileName();
                    return ChartDataDefinition.DEFAULT_VENDOR.isImportable(fileName.toString());
                }).collect(Collectors.toSet());
    }

    private Path getTransferToTemporaryPath(String fileName) {
        return properties.getSourcePath().resolve(fileName);
    }

    private void tryCreateTemporaryDirectories(Path temporaryZipPath) {
        try {
            Files.createDirectories(properties.getSourcePath());
        } catch (IOException e) {
            throw new IllegalArgumentException(format("Couldn't create temporary path %s. Error message: %s", temporaryZipPath, e.getMessage()), e);
        }
    }

    public void deleteTemporaryFiles() {
        Path sourcePath = properties.getSourcePath();
        try {
            FileUtils.forceDelete(sourcePath.toFile());
        } catch (IOException e) {
            throw new IllegalArgumentException(format("Couldn't delete temporary path %s. Error message: %s", sourcePath, e.getMessage()), e);
        }

    }
}
