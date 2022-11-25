package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.common.EnvProperties;
import com.arydz.stockfinder.domain.housekeeping.api.FileParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilesService {

    private final EnvProperties properties;
    private final DefaultExtractArchiveService defaultExtractZipService;

    public void importChartData(FileParams fileParams) {

        log.info("About to import chart data from zip file");
        MultipartFile uploadedZipFile = fileParams.getFile();
        ChartTimeframeType chartTimeframeType = fileParams.getChartTimeframeType();
        ExtractionMode extractionMode = fileParams.getExtractionMode();
        String pathToExtract = defaultExtractZipService.extractZipFile(uploadedZipFile, chartTimeframeType, extractionMode);

        // todo
        SaveStockParams saveStockParams = SaveStockParams.builder().path(pathToExtract).build();
        log.info("About to save stock data from extracted zip file");
//        stooqService.saveStockFromFiles(saveStockParams, stockCandleAuditId);

        log.info("Finished importing chart data from zip file");
    }
}
