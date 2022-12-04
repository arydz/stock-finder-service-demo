package com.arydz.stockfinder.domain.chart;

import com.arydz.stockfinder.domain.chart.db.ChartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartRepository<T extends ChartEntity> extends JpaRepository<T, Long> {

}
