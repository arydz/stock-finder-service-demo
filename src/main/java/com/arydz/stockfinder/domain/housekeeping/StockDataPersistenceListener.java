package com.arydz.stockfinder.domain.housekeeping;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class StockDataPersistenceListener {

    ApplicationEventPublisher eventPublisher;

    ExtractZipService extractZipService;

//    StooqService stooqService;

    @EventListener
    private void extractZipFile(FileDownloadEvent fileDownloadEvent) {

        log.info("About to extract zip file");

        String pathFileName = fileDownloadEvent.getPathFileName();

        String pathToExtract = extractZipService.extractZipFile(pathFileName);

        // todo
        SaveStockParams saveStockParams = SaveStockParams.builder().path(pathToExtract).build();

        log.info("Finished extraction of zip file");

//        eventPublisher.publishEvent(new SaveStockEvent(this, saveStockParams));
    }

    /*@EventListener
    private void saveStockList(SaveStockEvent saveStockEvent) {

        log.info("About to save stock data from extracted zip file");

        SaveStockParams saveStockParams = saveStockEvent.getSaveStockParams();

        stooqService.saveStockFromFiles(saveStockParams, stockCandleAuditId);
    }*/
}
