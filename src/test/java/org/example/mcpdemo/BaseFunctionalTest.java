package org.example.mcpdemo;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.mcp.api-key.secret=" + BaseFunctionalTest.API_KEY_SECRET)
@Import(TestcontainersConfiguration.class)
@Sql("/db/init.sql")
public abstract class BaseFunctionalTest {
    protected static final String API_KEY_SECRET = "test-secret";
}
