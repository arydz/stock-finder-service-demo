# What is this project about?
It's a demo of stock finder application, which is focusing on Spring and Spark integration and learning new things.
Features:
- find stocks from the available database
- provide data for drawing charts on web application
- todo...

# 1. Getting Started

### 1.1 Required installations
- Java 15
- Gradle 6.8.2
- Docker (Docker Compose)

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