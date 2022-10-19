package br.com.barreto.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.barreto.configs.TestsConfigs;
import br.com.barreto.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.barreto.integrationtests.testcontainers.AbstracticIntegrationTest;
import br.com.barreto.integrationtests.vo.AccountCredentialVO;
import br.com.barreto.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerYamlTest extends AbstracticIntegrationTest{
	
	
	
	private static YMLMapper objectMapper;
	private static TokenVO tokenVO;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new YMLMapper();
		
	}
	
	@Test
	@Order(1)
	public void testSignin() throws JsonMappingException, JsonProcessingException {
		
		AccountCredentialVO user = new AccountCredentialVO("leandro", "admin123");
		
		RequestSpecification specification = new RequestSpecBuilder()
				
				   .addFilter(new RequestLoggingFilter(LogDetail.ALL))
				   .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		tokenVO =
				given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestsConfigs.CONTENT_TYPE_YML)
				.basePath("/auth/signin")
				.port(TestsConfigs.SERVER_PORT)
				.contentType(TestsConfigs.CONTENT_TYPE_YML)
				.body(user,objectMapper)
				.when()
				   .post()
				.then()
				   .statusCode(200)
				.extract()
				    .body()
				        .as(TokenVO.class,objectMapper);
		
		 assertNotNull(tokenVO.getAccessToken());  
		 assertNotNull(tokenVO.getRefreshToken());  

	}
	@Test
	@Order(2)
	public void testRefresh() throws JsonMappingException, JsonProcessingException {
		
		AccountCredentialVO user = new AccountCredentialVO("leandro", "admin123");
		var newTokenVO =
				given()
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.accept(TestsConfigs.CONTENT_TYPE_YML)
				.basePath("/auth/refresh")
				.port(TestsConfigs.SERVER_PORT)
				.contentType(TestsConfigs.CONTENT_TYPE_YML)
				.pathParam("username", tokenVO.getUsername())
				.header(TestsConfigs.HEADER_PARAM_AUTORIZATION,"Bearer "+ tokenVO.getRefreshToken())
				.body(user)
				.when()
				.put("username")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(TokenVO.class,objectMapper);
		
		assertNotNull(newTokenVO.getAccessToken());  
		assertNotNull(newTokenVO.getRefreshToken());  
		
	}
}
