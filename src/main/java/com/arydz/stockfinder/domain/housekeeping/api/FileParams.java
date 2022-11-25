package com.arydz.stockfinder.domain.housekeeping.api;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import com.arydz.stockfinder.domain.housekeeping.ExtractionMode;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileParams {

    private MultipartFile file;
    private ChartTimeframeType chartTimeframeType;
    private ExtractionMode extractionMode;
}
