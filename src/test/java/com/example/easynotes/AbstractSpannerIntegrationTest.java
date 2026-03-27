package com.example.easynotes;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.InstanceId;
import com.google.cloud.spanner.InstanceInfo;
import com.google.cloud.spanner.InstanceConfigId;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Database;

@SpringBootTest
public abstract class AbstractSpannerIntegrationTest {

    @DynamicPropertySource
    static void spannerProperties(DynamicPropertyRegistry registry) {
        // the correct driver format is autoConfigEmulator=true separated by semi-colon
        String spannerUrl = "jdbc:cloudspanner://localhost:9010/projects/test-project/instances/test-instance/databases/test-db;autoConfigEmulator=true";
        registry.add("spring.datasource.url", () -> spannerUrl);
        registry.add("spring.datasource.driver-class-name", () -> "com.google.cloud.spanner.jdbc.JdbcDriver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "com.google.cloud.spanner.hibernate.SpannerDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @BeforeAll
    static void setupSpanner() throws Exception {
        System.setProperty("spanner.emulator.host", "127.0.0.1:9010");
        SpannerOptions options = SpannerOptions.newBuilder()
                .setProjectId("test-project")
                .setEmulatorHost("127.0.0.1:9010")
                .setCredentials(com.google.cloud.NoCredentials.getInstance())
                .build();
        Spanner spannerClient = options.getService();

        InstanceId instanceId = InstanceId.of("test-project", "test-instance");

        try {
             spannerClient.getInstanceAdminClient().getInstance("test-instance");
        } catch (Exception e) {
             spannerClient.getInstanceAdminClient().createInstance(
                InstanceInfo.newBuilder(instanceId)
                        .setInstanceConfigId(InstanceConfigId.of("test-project", "emulator-config"))
                        .setNodeCount(1)
                        .setDisplayName("Test Instance")
                        .build()
             ).get();
        }

        DatabaseId databaseId = DatabaseId.of(instanceId, "test-db");
        try {
            spannerClient.getDatabaseAdminClient().getDatabase("test-instance", "test-db");
        } catch (Exception e) {
            spannerClient.getDatabaseAdminClient().createDatabase(
                databaseId.getInstanceId().getInstance(),
                databaseId.getDatabase(),
                java.util.Collections.emptyList()
            ).get();
        }
    }
}
