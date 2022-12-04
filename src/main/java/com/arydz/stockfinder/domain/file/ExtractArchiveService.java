package com.arydz.stockfinder.domain.file;

import com.arydz.stockfinder.domain.chart.ChartTimeframeType;

import java.nio.file.Path;

/**
 * Different data vendors provide different bulk data sets (compressed archive files).
 * Because of this reason, provide an implementation for each Zip that contains packed files with different folder structures.
 *
 */
public interface ExtractArchiveService {

    Path extractZipFile(Path temporaryZipPath, ChartTimeframeType chartTimeframeType, ExtractionMode extractionMode);
}
