package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.domain.common.EnvProperties;
import com.arydz.stockfinder.domain.housekeeping.CsvToDatabaseService;
import com.arydz.stockfinder.domain.housekeeping.ExtractionMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final EnvProperties envProperties;
    private final CsvToDatabaseService csvToDatabaseService;
    private final ChartRepository chartRepository;

    public void save(ChartTimeframeType chartTimeframeType, ExtractionMode extractionMode, Set<Path> importableFolderList) {


        // todo

        List<Path> sp500 = importableFolderList.stream().filter(path -> path.toString().contains("sp500")).collect(Collectors.toList());

        for (Path folderPath : sp500) {
//        for (Path folderPath : importableFolderList) {



            csvToDatabaseService.run(folderPath);
        }
    }
}
