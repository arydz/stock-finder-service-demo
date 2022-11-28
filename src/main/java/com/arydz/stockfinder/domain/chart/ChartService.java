package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.domain.common.EnvProperties;
import com.arydz.stockfinder.domain.housekeeping.CsvToDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final EnvProperties envProperties;
//    private final CsvReaderServiceBasic csvReaderService;
    private final CsvToDatabaseService csvToDatabaseService;
    private final ChartRepository chartRepository;

    public void save(ChartTimeframeType chartTimeframeType, String zipExtractionPath) {

//        csvReaderService.readAll(zipExtractionPath);


        csvToDatabaseService.run(zipExtractionPath);


//        String table = chartTimeframe.getTable();
//        chartRepository.insertAll(stooqCandleWithStockDataset, table, stockCandleChartTableDefinition);
//        refreshQuoteDescriptiveView(chartTimeframe);
    }

    private void refreshQuoteDescriptiveView() {
        chartRepository.refreshQuoteDescriptiveView();
    }
}
