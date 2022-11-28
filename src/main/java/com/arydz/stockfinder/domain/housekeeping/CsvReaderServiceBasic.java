package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.chart.model.ChartSimple;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

@Service
public class CsvReaderServiceBasic implements BasicFileReader<ChartSimple> {

    @Override
    public List<ChartSimple> readAll(String pathToDir) {


        try (Reader reader = Files.newBufferedReader(Paths.get(pathToDir))) {
            CsvToBean<ChartSimple> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(ChartSimple.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(',')
                    .build();

            Iterator<ChartSimple> csvUserIterator = csvToBean.iterator();

            while (csvUserIterator.hasNext()) {
                ChartSimple chartSimple = csvUserIterator.next();
                System.out.println(chartSimple.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public ChartSimple read(String pathToFile) {
        return null;
    }
}
