-- SERIAL is not a true data type, but is simply shorthand notation that tells Postgres to create a auto incremented,
-- unique identifier for the specified column.

CREATE TABLE IF NOT EXISTS MARKET_INDEX (
    ID serial,
    NAME varchar(64) not null,
    COUNTRY varchar(64) not null,
    PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS STOCK (
    ID serial,
    TICKER varchar(32) not null unique,
    TITLE varchar(128) not null,
    EDGAR_CIK int null,
    MARKET_INDEX_ID int null,
    PRIMARY KEY (ID),
    FOREIGN KEY (MARKET_INDEX_ID) REFERENCES MARKET_INDEX
);

create TABLE IF NOT EXISTS CHART_DAILY (
    ID bigserial,
    FK_STOCK_ID int not null,
    DATE_TIME timestamp not null,
    OPEN numeric(18,4),
    HIGH numeric(18,4),
    LOW numeric(18,4),
    CLOSE numeric(18,4),
    VOLUME bigint,
    PRIMARY KEY(ID),
    FOREIGN KEY (FK_STOCK_ID) REFERENCES STOCK,
    UNIQUE (FK_STOCK_ID, DATE_TIME)
);

create TABLE IF NOT EXISTS CHART_HOURLY (
    ID bigserial,
    FK_STOCK_ID int not null,
    DATE_TIME timestamp not null,
    OPEN numeric(18,4),
    HIGH numeric(18,4),
    LOW numeric(18,4),
    CLOSE numeric(18,4),
    VOLUME bigint,
    PRIMARY KEY(ID),
    FOREIGN KEY (FK_STOCK_ID) REFERENCES STOCK,
    UNIQUE (FK_STOCK_ID, DATE_TIME)
);