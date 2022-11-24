package com.arydz.stockfinder.domain.housekeeping;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class SaveStockEvent extends ApplicationEvent {

    SaveStockParams saveStockParams;

    public SaveStockEvent(Object source, SaveStockParams saveStockParams) {
        super(source);
        this.saveStockParams = saveStockParams;
    }
}
