package com.arydz.stockfinder.domain.common.db;

import com.arydz.stockfinder.domain.stock.model.SimpleStock;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.util.List;

public interface FilesToDatabaseService {

    void run(List<SimpleStock> simpleStockList, Path folderPath, PreparedStatement preparedStatement);
}
