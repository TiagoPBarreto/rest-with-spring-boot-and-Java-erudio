package br.com.barreto.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.barreto.configs.TestsConfigs;
import br.com.barreto.integrationtests.testcontainers.AbstracticIntegrationTest;
import br.com.barreto.integrationtests.vo.AccountCredentialVO;
import br.com.barreto.integrationtests.vo.TokenVO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerXmlTest extends AbstracticIntegrationTest{
	private static TokenVO tokenVO;
	
	@Test
	@Order(1)
	public void testSignin() throws JsonMappingException, JsonProcessingException {
		
		AccountCredentialVO user = new AccountCredentialVO("leandro", "admin123");
		tokenVO =
				given()
				.basePath("/auth/signin")
				.port(TestsConfigs.SERVER_PORT)
				.contentType(TestsConfigs.CONTENT_TYPE_XML)
				
				.body(user)
				.when()
				   .post()
				.then()
				   .statusCode(200)
				.extract()
				    .body()
				        .as(TokenVO.class);
		
		 assertNotNull(tokenVO.getAccessToken());  
		 assertNotNull(tokenVO.getRefreshToken());  

	}
	@Test
	@Order(2)
	public void testRefresh() throws JsonMappingException, JsonProcessingException {
		
		AccountCredentialVO user = new AccountCredentialVO("leandro", "admin123");
		var newTokenVO =
				given()
				.basePath("/auth/refresh")
				.port(TestsConfigs.SERVER_PORT)
				.contentType(TestsConfigs.CONTENT_TYPE_XML)
				.pathParam("username", tokenVO.getUsername())
				.header(TestsConfigs.HEADER_PARAM_AUTORIZATION,"Bearer "+ tokenVO.getRefreshToken())
				.body(user)
				.when()
				.put("username")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(TokenVO.class);
		
		assertNotNull(newTokenVO.getAccessToken());  
		assertNotNull(newTokenVO.getRefreshToken());  
		
	}
}
