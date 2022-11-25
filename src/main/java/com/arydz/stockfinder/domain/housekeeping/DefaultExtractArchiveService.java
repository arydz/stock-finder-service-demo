package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.common.EnvProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.AbstractFileHeader;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultExtractArchiveService implements ExtractArchiveService {

    private static final String DATE_DIR_FORMAT = "yyyyMMdd";
    private static final String ARCHIVE_ROOT_FOLDER = "stock_market_data";

    private final EnvProperties properties;

    @Override
    public String extractZipFile(MultipartFile uploadedZipFile, ChartTimeframeType chartTimeframeType, ExtractionMode extractionMode) {

        String pathFileName = uploadedZipFile.getName();
        String zipExtractionPath = getZipExtractionPath(chartTimeframeType);
        log.info("About do extract zip file: {} into {}", pathFileName, zipExtractionPath);
        try {
            ZipFile zipFile = new ZipFile(pathFileName);
            extractZipByFolders(zipFile, zipExtractionPath, extractionMode);
        } catch (ZipException e) {
            throw new IllegalArgumentException(String.format("Couldn't extract zip %s", pathFileName), e);
        }
        return zipExtractionPath;
    }

    private String getZipExtractionPath(ChartTimeframeType vendorChartTimeframe) {

        String chartTimeframeName = vendorChartTimeframe.name().toLowerCase(Locale.ROOT);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_DIR_FORMAT);
        LocalDate localDate = LocalDate.now();
        String dateDir = formatter.format(localDate);

        return new StringJoiner(File.separator)
                .add(properties.getSourcePath())
                .add(chartTimeframeName)
                .add(dateDir)
                .toString();
    }

    private void extractZipByFolders(ZipFile zipFile, String pathToExtract, ExtractionMode extractionMode) throws ZipException {

        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        flattensZipStructure(zipFile, fileHeaders);
        excludeDirectories(zipFile, extractionMode, fileHeaders);

        for (FileHeader fileHeader : fileHeaders) {
            if (fileHeader.isDirectory()) {
                String fileName = fileHeader.getFileName();
                log.info("Extracting folder from archive: {}", fileName);
                zipFile.extractFile(fileName, pathToExtract);
            }
        }
        log.info("Extracted zip file successfully: {}", pathToExtract);
    }

    private void excludeDirectories(ZipFile zipFile, ExtractionMode extractionMode, List<FileHeader> fileHeaders) throws ZipException {

        for (FileHeader header : fileHeaders) {
            if (extractionMode.getDirectoryFilter().test(header)) {
                zipFile.removeFile(header);
                fileHeaders.remove(header);
            }
        }
    }

    private void flattensZipStructure(ZipFile zipFile, List<FileHeader> fileHeaders) {
        fileHeaders.stream()
                .filter(AbstractFileHeader::isDirectory)
                .filter(header -> {
                    String fileName = header.getFileName();
                    return fileName.startsWith(ARCHIVE_ROOT_FOLDER);
                })
                .findFirst()
                .ifPresent(header -> prepareArchiveFolder(zipFile, header));
    }

    private void prepareArchiveFolder(ZipFile zipFile, FileHeader fileHeader) {

        String fileName = fileHeader.getFileName();
        fileName = fileName.substring(0, fileName.length() - 1);
        int index = fileName.lastIndexOf("/") + 1;
        fileName = fileName.substring(index);
        try {
            zipFile.renameFile(fileHeader, fileName);
        } catch (ZipException e) {
            throw new IllegalArgumentException(String.format("Could not prepare archive folder %s", fileName));
        }
    }
}
