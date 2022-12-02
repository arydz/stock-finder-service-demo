package com.arydz.stockfinder.domain.housekeeping;

import java.nio.file.Path;

public interface FilesToDatabaseService {

    void run(Path folderPath);
}
