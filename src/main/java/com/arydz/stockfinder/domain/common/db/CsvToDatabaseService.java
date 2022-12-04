package com.arydz.stockfinder.domain.common.db;

import com.arydz.stockfinder.domain.common.EnvProperties;
import com.arydz.stockfinder.domain.stock.model.SimpleStock;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
    private static final String NUMBER_SEPARATOR = ".";
    private static final String DATE_PATTERN = "dd-MM-yyyy";

    private final EnvProperties properties;

    @Override
    public void run(List<SimpleStock> simpleStockList, Path folderPath, PreparedStatement preparedStatement) {

        log.info("About to save data from {} folder", folderPath);

        try (Stream<Path> paths = Files.list(folderPath)) {

            paths.forEach(filePath -> saveCSVInDatabase(simpleStockList, filePath, preparedStatement));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Can't list files in directory %s. Details: %s", folderPath, e.getMessage()), e);
        }
    }


    private void saveCSVInDatabase(List<SimpleStock> simpleStockList, Path filePath, PreparedStatement ps) {

        try (Reader reader = Files.newBufferedReader(filePath); CSVReader csvReader = getCsvReader(reader)) {

            String currentTicker = FilenameUtils.getBaseName(filePath.getFileName().toString());

            simpleStockList.stream()
                    .filter(s -> s.getTicker().equalsIgnoreCase(currentTicker))
                    .map(SimpleStock::getId)
                    .findAny()
                    .ifPresent(stockId -> saveInDatabase(stockId, ps, csvReader));

        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Can't stream CSV file %s. Details: %s", filePath, e.getMessage()), e);
        }
    }

    private void saveInDatabase(Long stockId, PreparedStatement ps, CSVReader csvReader) {

        try {
            int i = 0;
            String[] line;
            while ((line = csvReader.readNext()) != null) {

                boolean isIncorrectCsvLine = Arrays.stream(line).anyMatch(String::isBlank) || line.length > 7;
                String csvDateTime = line[DATE_TIME.getCsvOrder()];
                boolean isIncorrectDateFormat = !GenericValidator.isDate(csvDateTime, DATE_PATTERN, true);
                if (isIncorrectCsvLine || isIncorrectDateFormat) {
                    continue;
                }

                ps.setInt(1, Math.toIntExact(stockId));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
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
        } catch (CsvValidationException | SQLException e) {
            throw new IllegalArgumentException(String.format("Can't save CSV data in database. Details: %s", e.getMessage()), e);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Can't read line in CSV file. Details: %s", e.getMessage()), e);
        }
    }

    private CSVReader getCsvReader(Reader reader) {

        return new CSVReaderBuilder(reader)
                .withSkipLines(1)
                .build();
    }

    private String getNormalizedVolumeString(String[] line) {

        String volumeString = line[VOLUME.getCsvOrder()];
        String normalizedVolumeString = StringUtils.substringBefore(volumeString, NUMBER_SEPARATOR);
        if (StringUtils.EMPTY.equals(normalizedVolumeString)) {
            return volumeString;
        }
        return normalizedVolumeString;
    }
}
