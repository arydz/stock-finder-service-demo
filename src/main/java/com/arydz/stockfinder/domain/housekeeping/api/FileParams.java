package com.arydz.stockfinder.domain.housekeeping.api;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.housekeeping.ExtractionMode;
import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;

@Data
public class FileParams {

    private FilePart file;
    private ChartTimeframeType chartTimeframeType;
    private ExtractionMode extractionMode;
}
