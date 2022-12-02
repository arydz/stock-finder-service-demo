package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.common.EnvProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultExtractArchiveService implements ExtractArchiveService {

    private static final String DATE_DIR_FORMAT = "yyyyMMdd";

    private final EnvProperties properties;

    @Override
    public String extractZipFile(Path temporaryZipPath, ChartTimeframeType chartTimeframeType, ExtractionMode extractionMode) {

        log.info("About do extract zip file {}", temporaryZipPath);
        String zipExtractionPath = getZipExtractionPath(chartTimeframeType);
        try {
            ZipFile zipFile = new ZipFile(temporaryZipPath.toString());
            extractZipByFolders(zipFile, zipExtractionPath, extractionMode);
        } catch (ZipException e) {
            throw new IllegalArgumentException(String.format("Couldn't extract zip %s. Error message: %s", temporaryZipPath, e.getMessage()), e);
        }

        return zipExtractionPath;
    }

    private String getZipExtractionPath(ChartTimeframeType vendorChartTimeframe) {

        String chartTimeframeName = vendorChartTimeframe.name().toLowerCase(Locale.ROOT);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_DIR_FORMAT);
        LocalDate localDate = LocalDate.now();
        String dateDir = formatter.format(localDate);

        return properties.getSourcePath().resolve(chartTimeframeName).resolve(dateDir).toString();
    }

    private void extractZipByFolders(ZipFile zipFile, String extractionPath, ExtractionMode extractionMode) throws ZipException {

        List<FileHeader> filteredFileHeaderList = prepareListOfHeaders(zipFile, extractionMode);

        log.info("About to extract files");

        CountDownLatch countDownLatch = new CountDownLatch(filteredFileHeaderList.size());
        int cpu = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService executor = Executors.newFixedThreadPool(cpu);

        for (FileHeader fileHeader : filteredFileHeaderList) {
            ExtractZipFolderTask task = new ExtractZipFolderTask(zipFile, fileHeader, extractionPath, countDownLatch);
            executor.execute(task);
        }
        tryToShutdown(countDownLatch, executor);
        log.info("Extracted zip file successfully: {}", extractionPath);
    }

    private List<FileHeader> prepareListOfHeaders(ZipFile zipFile, ExtractionMode extractionMode) throws ZipException {
        List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
        return excludeDirectories(extractionMode, fileHeaderList);
    }

    private List<FileHeader> excludeDirectories(ExtractionMode extractionMode, List<FileHeader> fileHeaders) {

        return fileHeaders.stream()
                .filter(fh -> extractionMode.getExtractable().test(fh))
                .collect(Collectors.toList());
    }

    private void tryToShutdown(CountDownLatch countDownLatch, ExecutorService executor) {
        executor.shutdown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            // Method interrupt() sets the thread's interruption status, so other parts of code will notice it and can handle it appropriately (the task from a pool can cancel itself)
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Application state interrupted", e);
        }
    }
}
