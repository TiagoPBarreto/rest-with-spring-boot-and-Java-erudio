package br.com.barreto.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.barreto.configs.TestsConfigs;
import br.com.barreto.integrationtests.testcontainers.AbstracticIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstracticIntegrationTest {

	@Test
	public void shouldDisplaySwaggerUiPage() {
		
		var content =
					given()
					.basePath("/swagger-ui/index.html")
					.port(TestsConfigs.SERVER_PORT)
					.when()
					.get()
					.then()
					.statusCode(200)
					.extract()
					.body()
					.asString();
		assertTrue(content.contains("Swagger UI"));
	}

}
