package com.arydz.stockfinder.domain.housekeeping;

import java.util.List;

public interface BasicFileReader<T> {

    List<T> readAll(String pathToDir);

    T read(String pathToFile);

}
