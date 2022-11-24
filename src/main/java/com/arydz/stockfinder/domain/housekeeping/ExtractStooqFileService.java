package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.common.EnvProperties;
import com.arydz.stockfinder.domain.housekeeping.api.FileParams;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@AllArgsConstructor
public class ExtractStooqFileService implements PerformFile {

    private static final String TEMP_FILE_PATTERN = "%s/temp_%s";

    private final EnvProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void performFileProcessing(FileParams fileParams) {

        MultipartFile uploadedFile = fileParams.getFile();
        String pathToZipFile = String.format(TEMP_FILE_PATTERN, properties.getSourcePath(), uploadedFile);
        log.info("Path to zip file: {}", pathToZipFile);
        File zipFile = getUploadedFile(fileParams, pathToZipFile);

        // todo use MONO

        eventPublisher.publishEvent(new FileDownloadEvent(this, pathToZipFile));

        if (zipFile.exists()) {
            zipFile.delete();
        }
    }

    private File getUploadedFile(FileParams fileParams, String pathToZipFile) {

        try {
            InputStream inputStream = fileParams.getFile()
                    .getResource()
                    .getInputStream();

            File file = new File(pathToZipFile);
            FileUtils.copyInputStreamToFile(inputStream, file);

            return file;
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }
}
