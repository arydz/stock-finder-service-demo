package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.domain.chart.db.ChartDailyEntity;
import com.arydz.stockfinder.domain.chart.db.ChartTableDefinition;
import com.arydz.stockfinder.domain.common.db.CsvToDatabaseService;
import com.arydz.stockfinder.domain.common.db.SqlUtils;
import com.arydz.stockfinder.domain.file.ExtractionMode;
import com.arydz.stockfinder.domain.file.FileService;
import com.arydz.stockfinder.domain.stock.StockService;
import com.arydz.stockfinder.domain.stock.model.SimpleStock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartService {

    private final StockService stockService;
    private final CsvToDatabaseService csvToDatabaseService;
    private final SqlUtils sqlUtils;
    private final JdbcTemplate jdbcTemplate;
    private final ChartTableDefinition chartTableDefinition;
    private final FileService fileService;

    public Mono<Long> importChartData(FilePart file, ChartTimeframeType chartTimeframeType, ExtractionMode extractionMode) {

        if (ExtractionMode.EXCLUDE_CSV_FOLDERS == extractionMode) {
            throw new IllegalArgumentException("Currently, JSON files are not supported. Operation cancelled.");
        }

        Mono<Path> uploadFileMono = fileService.uploadZipFile(Mono.just(file));

        return Mono.zip(uploadFileMono, Mono.just(chartTimeframeType), Mono.just(extractionMode))
                .flatMap(fileService::extractZipFile)
                .flatMap(paths -> {
                    Path zipExtractionPath = paths.getT2();
                    return fileService.performExtractedFiles(zipExtractionPath, this::save);
                })
                .doFinally(signalType -> fileService.deleteTemporaryFiles());
    }

    private Mono<Long> save(Set<Path> importableFolderList) {

        List<SimpleStock> simpleStockList = stockService.getSimpleStockList();

        String upsertQuery = sqlUtils.prepareUpsert(ChartDailyEntity.ENTITY_NAME, chartTableDefinition, Collections.emptyList());
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (null == dataSource) {
            throw new IllegalArgumentException("No data source available");
        }

        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(upsertQuery)) {

            long totalCount = 0;

            for (Path folderPath : importableFolderList) {

                if (Files.isRegularFile(folderPath)) {
                    throw new IllegalArgumentException(String.format("Path %s should indicate directory with files and not specific file", folderPath));
                }
                totalCount += csvToDatabaseService.run(simpleStockList, folderPath, preparedStatement);
            }

            return Mono.just(totalCount);
        } catch (SQLException e) {
            throw new IllegalArgumentException(String.format("Can't prepare SQL statement. Details: %s", e.getMessage()));
        }
    }
}
