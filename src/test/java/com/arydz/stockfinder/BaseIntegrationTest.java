package com.arydz.stockfinder;

import com.github.dockerjava.api.DockerClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.net.InetAddresses;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles(value = "test")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/01_create_schema.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/02_insert_market_index.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/03_insert_stock.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:/db/999_clear.sql"),
})
public abstract class BaseIntegrationTest {

    private static final String JDBC_URL_PATTERN = "jdbc:postgresql://%1$s:%2$d/test_database?loggerLevel=OFF";

    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("user")
            .withPassword("password")
            .withDatabaseName("test_database")
            .withNetworkAliases("postgres")
            .withExposedPorts(5432);

    @DynamicPropertySource
    static void setupProperties(DynamicPropertyRegistry registry) {

        String ipAddress = getIpAddress();
        String hostname = container.getHost();

        final String jdbcUrl;
        if (InetAddresses.isUriInetAddress(hostname)) {
            jdbcUrl = "jdbc:postgresql://" + ipAddress + ":5432/test_database?loggerLevel=OFF";
        } else {
            jdbcUrl =  String.format(JDBC_URL_PATTERN, hostname, container.getMappedPort(5432));
        }

        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    private static String getIpAddress() {
        String containerId = container.getContainerId();

        DockerClient dockerClient = container.getDockerClient();

        String ipv4Address = dockerClient
                .inspectNetworkCmd()
                .withNetworkId("bridge")
                .exec()
                .getContainers()
                .get(containerId)
                .getIpv4Address();

        return StringUtils.substringBefore(ipv4Address, "/");
    }

    @Autowired
    protected WireMockServer wireMockServer;

    @AfterEach
    public void tearDown() {
        wireMockServer.resetToDefaultMappings();
    }
}
