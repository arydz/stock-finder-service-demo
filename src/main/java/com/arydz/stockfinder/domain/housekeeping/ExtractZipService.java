package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.common.EnvProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@AllArgsConstructor
public class ExtractZipService {

    private static final String EXTRACT_PATH_PATTERN = "%s/%s/%s/%s";

    private final EnvProperties properties;

    public String extractZipFile(String pathFileName) {
        String pathToExtract;
        try {
            ZipFile zipFile = new ZipFile(pathFileName);
            pathToExtract = getPathToExtract(vendorChartTimeframe);
            log.info("About do extract zip file: {} at {}", pathFileName, pathToExtract);

            extractZipByFolders(vendorChartTimeframe, zipFile, pathToExtract);
        } catch (ZipException e) {
            throw new IllegalArgumentException(String.format("Couldn't extract archive %s", pathFileName), e);
        }
        return pathToExtract;
    }

    private String getPathToExtract(VendorChartTimeframe vendorChartTimeframe) {
        DataVendor dataVendor = vendorChartTimeframe.getDataVendor();
        String vendorDir = dataVendor.name()
                .toLowerCase(Locale.ROOT);
        ChartTimeframe chartTimeframe = vendorChartTimeframe.getChartTimeframe();
        String candleDir = chartTimeframe.name()
                .toLowerCase(Locale.ROOT);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.now();
        String dateDir = formatter.format(localDate);

        return String.format(EXTRACT_PATH_PATTERN, properties.getSourcePath(), vendorDir, candleDir, dateDir);
    }

    private void extractZipByFolders(VendorChartTimeframe vendorChartTimeframe, ZipFile zipFile, String pathToExtract)
            throws ZipException {
        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        for (FileHeader fileHeader : fileHeaders) {
            if (fileHeader.isDirectory()) {

                String fileName = renameAndMoveFolder(vendorChartTimeframe, zipFile, fileHeader);

                log.info("Extracting folder: {}", fileName);
                zipFile.extractFile(fileHeader, pathToExtract);
            }
        }
        log.info("Extracted zip file successfully: {}", pathToExtract);
    }

    private String renameAndMoveFolder(VendorChartTimeframe vendorChartTimeframe, ZipFile zipFile, FileHeader fileHeader)
            throws ZipException {
        String fileName = fileHeader.getFileName();
        log.info("Rename folder: {}", fileName);
        fileName = fileName.replace(" ", "_");
        if (fileName.startsWith(vendorChartTimeframe.getFoldersToMove())) {
            fileName = fileName.substring(0, fileName.length() - 1);
            int index = fileName.lastIndexOf("/") + 1;
            fileName = fileName.substring(index);
        }
        zipFile.renameFile(fileHeader, fileName);
        return fileName;
    }
}
