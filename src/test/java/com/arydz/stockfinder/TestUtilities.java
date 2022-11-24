package com.arydz.stockfinder;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.file.ExtractionMode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;

public class TestUtilities {

    public static File getTestDataZip() {

        return new File(
                TestUtilities.class.getClassLoader()
                        .getResource("files/test_data.zip")
                        .getFile());
    }

    public static BodyInserters.MultipartInserter getMultipartInserter(File file) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(file));
        builder.part("chartTimeframeType", ChartTimeframeType.DAILY.name());
        builder.part("extractionMode", ExtractionMode.EXCLUDE_JSON_FOLDERS.name());

        return BodyInserters.fromMultipartData(builder.build());
    }
}
