package com.arydz.stockfinder.domain.housekeeping;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Different data vendors provide different bulk data sets (compressed archive files).
 * Because of this reason, provide an implementation for each Zip that contains packed files with different folder structures.
 *
 */
public interface ExtractArchiveService {

    String extractZipFile(MultipartFile uploadedZipFile, ChartTimeframeType chartTimeframeType, ExtractionMode extractionMode);
}
