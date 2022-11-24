package com.arydz.stockfinder.domain.housekeeping;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public final class FileDownloadEvent extends ApplicationEvent {

    private final String pathFileName;

    public FileDownloadEvent(Object source, String pathFileName) {
        super(source);
        this.pathFileName = pathFileName;
    }
}
