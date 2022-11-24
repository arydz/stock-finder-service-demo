package com.arydz.stockfinder.domain.housekeeping;


import com.arydz.stockfinder.domain.housekeeping.api.FileParams;

interface PerformFile {

    /**
     * Perform downloading file (implementation depends on Data Provider)
     *
     */
    void performFileProcessing(FileParams fileParams);
}
