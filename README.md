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
Required (for Docker) environment variables (PC restart might be required):
```text
- `POSTGRES_PASSWORD` for Postgres
- `PGADMIN_DEFAULT_PASSWORD` for PGadmin
```

### 1.3 Login to PgAdmin (optional)
- Open http://localhost:5454/browser/ in browser
- Login with a credentials: `sf_admin@stockfinder.com` and password setup in your env variable `PGADMIN_DEFAULT_PASSWORD` (1.3 point)
- Choose `Add New Server`, new wizard window will be shown
- In general tab provide: `Name: Stock Finder` (or whatever you want)
- In connection tab provide: `Host: postgres`, `Maintenance database: stock_finder` `User name: sf_admin`, `Password: -password from POSTGRES_PASSWORD variable-`
- Click on `Save` button
- From available databases select `stock_finder`

### 1.4 Swagger
Go to: `http://localhost:8080/webjars/swagger-ui/index.html`

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

### 2.3 Docker approach
Docker in version 20.10.17 is used.<br>
This project consist of multiple docker-compose.yaml files:
- `docker-compose.infrastructure.yaml` is for preparing database containers, and network
- `docker-compose.yaml` is only about creating the Stock Finder demo container
  Instead of multiple files, `Docker Profiles` could be used.
  For example:
```yaml
services:
  mq:
    ports:
      - 10:10
  db:
    profiles: ["db"]
    ports:
      - 1234:1234
  app:
    profiles: ["app"]
    ports:
      - 8080:8080
```
Then these commands will result in:<br>
This command will start the **mq and database** service `docker-compose --profile db up` <br>
This command will start the **mq and app** service `docker-compose --profile app up`<br>
This command will start the **app** service `docker-compose run app` <br>
This command will start only your **mq** service `docker-compose up`

### 2.3.1 Docker network
All services are in the scope of the same (custom) network (apart from Jenkins and testcontainers). Static IP addresses are used.

#### 2.3.2 Build images and run Docker containers
First, build the application and generate a docker image with this command:
- `gradle clean build`
- `gradle jibDockerBuild -Djib.container.jvmFlags=-Dsf.database.host="<some-host>",-Dspring.datasource.password="<database-password>"`
  - thanks to that, those properties don't have to be put in container closure:
  ```text
      container {
        jvmFlags = ['-Dsf.database.host=<some-host>', '-Dspring.datasource.password=<database-password>']
        creationTime = 'USE_CURRENT_TIMESTAMP'
      }
  ```
  - so, it mitigates the risk of pushing sensitive data (like passwords) into the code repository
The image will be pushed to your **local** docker image repository
Next, execute in this order (go to docker folder):
- `docker compose -f docker-compose.infrastructure.yaml up`
- `docker compose up`

#### 2.3.3 Dangling images
When generating new images for docker, for example with existing **tag**, a dangling image with the name <none> can occur. Is without a tag, an unused image not referenced by any container.
When using **Google Jib plugin**, images can be generated with tags in at least three ways:
- in `to` closure, adding `tags = ["version-1"]` properties will cause in generating two images with tags: `latest` and `version-1`
  - but, when a generation of the image with the `version-1` tag will be required, then it can cause generating a dangling image (with <none> name).
- instead of that, also in `to` closure, tag can be defined in directly in a name property: `image = "repo/${project.name}:version-1"`
  - this will cause the generation of only one image with the tag `version-1`
- currently in this project, the default tag approach is used: `image = "repo/${project.name}"`
  - this will cause the generation of one image with the tag `latest` and a dangling image (with <none> name).

#### 2.3.4 How to remove dangling images
Simply, use this command: `docker image prune`

#### 2.3.5 Briefly about Docker Socket (not suggested)
It's a UNIX socket, which is used by Docker to listen to containers, so it exposes the Docker API (**it causes high-security risk**)
Docker socket is configured by:
```yaml
services:
  container-name:
    image: some-image
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```
It gives us the benefit of managing Docker (Daemon) via container.
Main Docker Daemon is directly managing images from dockerized Jenkins.
**This approach was chosen for this project, because "easy of use".** 

#### 2.3.6 Docker in Docker instead of Docker Socket
When security is important it's better to use Docker-in-Docker approach. It isolates Jenkins container from host.
```yaml
services:
  dind:
      image: docker:dind
      expose:
        - 2375
      networks:
        - jenkins_net

  jenkins:
    image: jenkins/jenkins:lts
    environment:
      DOCKER_HOST: "tcp://dind:2375"
    networks:
      - jenkins_net

networks:
  jenkins_net:
    driver: bridge
```
If container is privileged, it's good to enable the use of TLS in the Docker server.
```yaml
services:
  container-name:
    image: some-image
    environment:
      - DOCKER_TLS_CERTDIR=/certs
```
It requires creating the volume to share the Docker client TLS certificates needed to connect to the Docker and persist data.
```yaml
services:
  container-name:
    image: some-image
    volumes:
      - docker-certs:/certs/client
  
volumes:
  docker-certs:
```

### 2.3.7 Example docker-compose configurations to play with
```yaml
services:
    linux-simple:
        image: ubuntu:latest
        privileged: true
        tty: true
        stdin_open: true
        volumes:
        - D:/ubuntu/simple-test:/home/test
        - /var/run/docker.sock:/var/run/docker.sock
        - /usr/local/bin/docker:/usr/local/bin/docker
        environment:
        - TESTCONTAINERS_RYUK_DISABLED=true
        network_mode: bridge
    
    gradle-java:
        image: gradle:jdk15
        privileged: true
        tty: true
        stdin_open: true
        network_mode: bridge
        entrypoint: /bin/sh
```

### 2.4 Google Jib
**Google Jib** is a plugin for Gradle, Maven, etc. Is used for building images without Dockerfile, and it doesn't need the installation of Docker.

### 2.5 Webflux
Provides reactive features for web applications development. It's based on non-blocking reactive streams,
supports back pressure (subscriber informs the publisher about required by it the pace of producing data).
So when the subscriber cannot consume the received amount of data, signals the publisher to emit fewer elements.
**A good solution for overwhelmed services.**

### 2.6 Tests
This project is covered by unit and integration tests. 
- Library `spring-cloud-starter-openfeign` is used to provide a ready implementation of the page and sort objects that helps parse JSON
- Postgres `testcontainers` provides ready SQL databases for integration tests only. This gives the ability to fully verify the behavior of native queries. For example, the H2 database doesn't support some of Postgres features.  

#### 2.6.1 Testcontainers with Jenkins on local Docker
When running Jenkins on Docker, custom network for Jenkins container will be created as default. This will makes not possible to run integration tests that need to connect with Testcontainers.
This is why:
- Jenkins container uses **bridge** `network_mode`. Thanks to that, it's in same network as Postgres from Testcontainers
- In `BaseIntegrationTest`, connection to the database is prepared, depending on the host
  - if tests are running locally, then the URL connection is based on localhost and mapped port
  - if tests are running on Jenkins, then the URL connection is based on Testcontainers IP and exposed port.
    - method `container.container.getJdbcUrl()`, will return connection string, built with a gateway address 

Testcontainers challenges (version 1.17.5):
- it's not possible to use a custom network (declared for example in docker-compose.yaml file) with Testcontainers. Testcontainers work only with bridge network 
  ```java
    @ClassRule
    public static DockerComposeContainer environment = new DockerComposeContainer(new File("src/test/resources/docker-compose.test.yml")) 
  ```
- there is an option to extend the `Network` interface and implement an appropriate class that might use existing network **(not verified)**
- Testcontainers doesn't support some docker-compose features (can't name a container)

### 2.6 Flyway
Is a version control for databases, helpful with migration (can be done in SQL and Java).
Since this project already has initialized database, the next SQL script has to have a name with the `V2__` prefix. 