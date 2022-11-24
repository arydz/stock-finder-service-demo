FROM bitnami/spark

USER root
RUN apt update && apt install -y maven
RUN mvn dependency:get -Dartifact=org.apache.spark:spark-sql_2.12:${spark-version} && \
    \
    rm -rf ~/.m2/repository


USER 1001