package com.arydz.stockfinder.domain.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CountDownLatch;

@Slf4j
@RequiredArgsConstructor
public class ExtractZipFolderTask implements Runnable {

    private static final String ZIP_PATH_SEPARATOR = "/";
    private static final String ARCHIVE_ROOT_FOLDER = "stock_market_data";

    private final ZipFile zipFile;
    private final FileHeader fileHeader;
    private final String extractionPath;
    private final CountDownLatch countDownLatch;

    @Override
    public void run() {

        String folderPath = fileHeader.getFileName();
        log.info("Extracting folder from archive: {}", folderPath);
        try {
            String newFolderPath = flattensPathToExtractedFile(folderPath);
            zipFile.extractFile(folderPath, extractionPath, newFolderPath);
        } catch (ZipException e) {
            throw new IllegalStateException(String.format("Couldn't extract folder %s from zip. Details: %s", folderPath, e.getMessage()));
        } finally {
            countDownLatch.countDown();
        }
    }

    private String flattensPathToExtractedFile(String fileNamePath) {

        String flatPath = fileNamePath.replace(ARCHIVE_ROOT_FOLDER, StringUtils.EMPTY);
        flatPath = flatPath.substring(0, flatPath.length() - 1);
        return flatPath.substring(0, flatPath.lastIndexOf(ZIP_PATH_SEPARATOR));
    }
}
