package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.chart.db.ChartDailyEntity;
import com.arydz.stockfinder.domain.chart.db.ChartTableDefinition;
import com.arydz.stockfinder.domain.common.EnvProperties;
import com.arydz.stockfinder.domain.common.db.SqlUtils;
import com.arydz.stockfinder.domain.stock.StockService;
import com.arydz.stockfinder.domain.stock.model.SimpleStock;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.arydz.stockfinder.domain.chart.db.ChartFields.CLOSE;
import static com.arydz.stockfinder.domain.chart.db.ChartFields.DATE_TIME;
import static com.arydz.stockfinder.domain.chart.db.ChartFields.HIGH;
import static com.arydz.stockfinder.domain.chart.db.ChartFields.LOW;
import static com.arydz.stockfinder.domain.chart.db.ChartFields.OPEN;
import static com.arydz.stockfinder.domain.chart.db.ChartFields.VOLUME;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvToDatabaseService implements FilesToDatabaseService {

    private static final MathContext MC = new MathContext(4, RoundingMode.HALF_EVEN);

    private final EnvProperties properties;
    private final SqlUtils sqlUtils;
    private final JdbcTemplate jdbcTemplate;
    private final ChartTableDefinition chartTableDefinition;
    private final StockService stockService;

    @Override
    public void run(Path folderPath) {

        // todo get list of stocks

        List<SimpleStock> simpleStockList = stockService.getSimpleStockList();

        simpleStockList.forEach(System.out::println);





        if (Files.isRegularFile(folderPath)) {
            throw new IllegalArgumentException(String.format("Path %s should indicate directory with files and not specific file", folderPath));
        }

        String upsertQuery = sqlUtils.prepareUpsert(ChartDailyEntity.ENTITY_NAME, chartTableDefinition, Collections.emptyList());
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (null == dataSource) {
            throw new IllegalArgumentException("No data source available");
        }

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(upsertQuery)) {

            trySaveData(folderPath, ps);

        } catch (SQLException e) {
            throw new IllegalArgumentException(String.format("Can't prepare SQL statement. Details: %s", e.getMessage()));
        }
    }

    private void trySaveData(Path folderPath, PreparedStatement ps) {
        try (Stream<Path> paths = Files.list(folderPath)) {
            paths.forEach(filePath -> saveCSVInDatabase(filePath, ps));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Can't list files in directory %s. Details: %s", folderPath, e.getMessage()), e);
        }
    }

    private void saveCSVInDatabase(Path filePath, PreparedStatement ps) {
        try (Reader reader = Files.newBufferedReader(filePath); CSVReader csvReader = getCsvReader(reader)) {

            log.info(filePath.getFileName().toString());

            int i = 0;
            String[] line;
            while ((line = csvReader.readNext()) != null) {

                if (Arrays.stream(line).anyMatch(String::isBlank)) {
                    continue;
                }


                // todo
                ps.setInt(1, 1);//FK_STOCK_ID
                String csvDateTime = line[DATE_TIME.getCsvOrder()];

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate dateTime = LocalDate.parse(csvDateTime, formatter);
                Timestamp timestamp = Timestamp.valueOf(dateTime.atTime(LocalTime.MIDNIGHT));

                ps.setTimestamp(2, timestamp);

                BigDecimal low = new BigDecimal(line[LOW.getCsvOrder()], MC);
                ps.setBigDecimal(3, low);
                BigDecimal open = new BigDecimal(line[OPEN.getCsvOrder()], MC);
                ps.setBigDecimal(4, open);

                String normalizedVolumeString = getNormalizedVolumeString(line);
                ps.setLong(5, Long.parseLong(normalizedVolumeString));

                BigDecimal high = new BigDecimal(line[HIGH.getCsvOrder()], MC);
                ps.setBigDecimal(6, high);
                BigDecimal close = new BigDecimal(line[CLOSE.getCsvOrder()], MC);
                ps.setBigDecimal(7, close);

                ps.addBatch();
                if (++i % properties.getBatchSize() == 0) {
                    ps.executeBatch();
                }
            }

            ps.executeBatch();

        } catch (IOException | CsvValidationException | SQLException e) {
            throw new IllegalArgumentException(String.format("Can't persist CSV data in database. Details: %s", e.getMessage()), e);
        }
    }

    private CSVReader getCsvReader(Reader reader) {
        return new CSVReaderBuilder(reader)
                .withSkipLines(1)
                .build();
    }

    private String getNormalizedVolumeString(String[] line) {
        String volumeString = line[VOLUME.getCsvOrder()];
        String normalizedVolumeString = StringUtils.substringBefore(volumeString, ".");
        if (StringUtils.EMPTY.equals(normalizedVolumeString)) {
            return volumeString;
        }
        return normalizedVolumeString;
    }
}
