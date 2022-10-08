# What is this project about?
It's a demo of stock finder application (backoffice), which is focusing on:
- Spring and Spark integration
- simple integration with external sites (SEC.gov)
- learning new things (libraries / architectural approaches)
- implementing effective sql scripts
- providing unit / integration tests with good coverage (focus on checking valuable logic, not on percent) 
Features:
- import stocks from EDGAR (Company Filings - SEC.gov)
- provide list of available stocks
- find stocks from the available database
- import candle data from filesystem
- provide data for drawing candle based charts on web application
- todo...

**In this project, some packages contain specific description.MD files with descriptions of selected solutions.**

# License
This project is licensed under the terms of the Creative Commons Attribution-ShareAlike 4.0 International license.

# 1. Getting Started

### 1.1 Required installations
- Java 15
- Gradle 6.8.2
- Docker (Docker Compose)
  - PostgreSQL (with admin)
  - Apache Spark

### 1.2 Set environment variables
For linux based. In your /etc/environment add environment variable for Spark Home dir
```text
JAVA_HOME="/home/${user}/libraries/java"
MAVEN_HOME="/home/${user}/libraries/maven"
GRADLE_HOME="/home/${user}/libraries/gradle"
SPARK_HOME="/home/${user}/utils/spark-3.3.0-bin-hadoop3"
PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin:$JAVA_HOME/bin:$GRADLE_HOME/bin:$SPARK_HOME/bin"
```
Where $SPARK_HOME is required when working with gradle scripts and docker


### 1.3 Run Docker containers
Required environment variables (PC restart might be required):
- `POSTGRES_PASSWORD` for Postgres
- `PGADMIN_DEFAULT_PASSWORD` for PGadmin
In the command line run
`docker compose up`

### 1.4 Login to PgAdmin (optional)
- Open http://localhost:5454/browser/ in browser
- Login with a credentials: `sf_admin@stockfinder.com` and password setup in your env variable `PGADMIN_DEFAULT_PASSWORD` (1.3 point)
- Choose `Add New Server`, new wizard window will be shown
- In general tab provide: `Name: Stock Finder` (or whatever you want)
- In connection tab provide: `Host: postgres`, `Maintenance database: stock_finder` `User name: sf_admin`, `Password: -password from POSTGRES_PASSWORD variable-`
- Click on `Save` button
- From available databases select `stock_finder` 

# 2. Technical description

### 2.1 Project structure
Package by Feature, that reflect the feature set. The packages are coherent and highly modular, in comparison with Package by Layer which causes high coupling between packages.
So this results in:
- reduces the complexity of code
- better reusability of code
- make easier maintainability of code
- makes easier reading of code 

Of course Package by Layer, shouldn't be totally discredited. In **small microservices** or **simple libraries** that are focusing on one domain, it **might** be the best option.  

### 2.2 JMapper
Elegance, high performance and robustness all in one java bean mapper.
JMapper achieves high-performance results since it applies a number of optimizations, that developers normally don't focus on (Javassist library).
It provides easy-to-use feature mappings (annotation-based, config classes, or XML files). Because of its nature, some problems during debugging can occurs. 
https://github.com/jmapper-framework/jmapper-core/wiki

### 2.3 Webflux
Provides reactive features for web applications development. It's based on non-blocking reactive streams,
supports back pressure (subscriber informs the publisher about required by it the pace of producing data).
So when the subscriber cannot consume the received amount of data, signals the publisher to emit fewer elements.
**A good solution for overwhelmed services.**

### 2.4 Tests
This project is covered by unit and integration tests. 
- Library `spring-cloud-starter-openfeign` is used to provide a ready implementation of the page and sort objects that helps parse JSON
- Postgres `testcontainers` provides ready SQL databases for integration tests only. This gives the ability to fully verify the behavior of native queries. For example, the H2 database doesn't support some of Postgres features.  