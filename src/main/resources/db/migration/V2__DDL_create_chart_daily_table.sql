create TABLE IF NOT EXISTS CHART_DAILY (
    ID bigserial,
    FK_STOCK_ID int not null,
    DATE_TIME timestamp not null,
    OPEN numeric(16,4),
    HIGH numeric(16,4),
    LOW numeric(16,4),
    CLOSE numeric(16,4),
    VOLUME bigint,
    PRIMARY KEY(ID),
    FOREIGN KEY (FK_STOCK_ID) REFERENCES STOCK,
    UNIQUE (FK_STOCK_ID, DATE_TIME)
);

create TABLE IF NOT EXISTS CHART_HOURLY (
    ID bigserial,
    FK_STOCK_ID int not null,
    DATE_TIME timestamp not null,
    OPEN numeric(16,4),
    HIGH numeric(16,4),
    LOW numeric(16,4),
    CLOSE numeric(16,4),
    VOLUME bigint,
    PRIMARY KEY(ID),
    FOREIGN KEY (FK_STOCK_ID) REFERENCES STOCK,
    UNIQUE (FK_STOCK_ID, DATE_TIME)
);