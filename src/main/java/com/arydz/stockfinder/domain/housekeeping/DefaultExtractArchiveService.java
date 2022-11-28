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

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultExtractArchiveService implements ExtractArchiveService {

    private static final String DATE_DIR_FORMAT = "yyyyMMdd";
    private static final String ARCHIVE_ROOT_FOLDER = "stock_market_data";

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

        return new StringJoiner(File.separator)
                .add(properties.getSourcePath())
                .add(chartTimeframeName)
                .add(dateDir)
                .toString();
    }

    private void extractZipByFolders(ZipFile zipFile, String pathToExtract, ExtractionMode extractionMode) throws ZipException {

        List<FileHeader> filteredFileHeaderList = prepareListOfHeaders(zipFile, extractionMode);

//        filteredFileHeaderList.stream()
//                .filter(AbstractFileHeader::isDirectory)
//                .map(AbstractFileHeader::getFileName)
//                .forEach(System.out::println);

        log.info("About to extract files");
        for (FileHeader fileHeader : filteredFileHeaderList) {
//            boolean isValidFile = !fileHeader.isDirectory() && extractionMode.getExclusion().test(fileHeader);
//            if (isValidFile) {

            if (fileHeader.isDirectory()) {
                String fileName = fileHeader.getFileName();
//                String newFileName = flattensPathToExtractedFile(fileName);
//                zipFile.extractFile(fileName, pathToExtract, newFileName);
                log.info("Extracting folder from archive: {}", fileName);
                zipFile.extractFile(fileName, pathToExtract);
            }
        }
        log.info("Extracted zip file successfully: {}", pathToExtract);
    }

    private List<FileHeader> prepareListOfHeaders(ZipFile zipFile, ExtractionMode extractionMode) throws ZipException {
        List<FileHeader> fileHeaderList = zipFile.getFileHeaders();

        flattensZipStructure(zipFile, fileHeaderList);
        List<FileHeader> filteredFileHeaderList = excludeDirectories(extractionMode, fileHeaderList);
        return filteredFileHeaderList;
    }

    private String flattensPathToExtractedFile(String fileName) {
        return fileName.replace(ARCHIVE_ROOT_FOLDER, "");
    }


    private List<FileHeader> excludeDirectories(ExtractionMode extractionMode, List<FileHeader> fileHeaders) {

        return fileHeaders.stream()
                .filter(fh -> extractionMode.getExtractable().test(fh))
                .collect(Collectors.toList());
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
