package br.com.barreto.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.barreto.configs.TestsConfigs;
import br.com.barreto.data.vo.v1.security.TokenVO;
import br.com.barreto.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.barreto.integrationtests.testcontainers.AbstracticIntegrationTest;
import br.com.barreto.integrationtests.vo.AccountCredentialVO;
import br.com.barreto.integrationtests.vo.PersonVO;
import br.com.barreto.integrationtests.vo.pagedmodels.PagedModelPerson;
import br.com.barreto.integrationtests.vo.wrappers.WrapperPersonVO;
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
public class PersonControllerYAMLTest extends AbstracticIntegrationTest {

	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	
	private static PersonVO person;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new YMLMapper();
	
		
		person = new  PersonVO();
		
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialVO user = new AccountCredentialVO("leandro", "admin123");
		var accessToken =
				given()
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.basePath("/auth/signin")
				.port(TestsConfigs.SERVER_PORT)
				.contentType(TestsConfigs.CONTENT_TYPE_YML)
				.accept(TestsConfigs.CONTENT_TYPE_YML)
				.body(user,objectMapper)
				.when()
				   .post()
				.then()
				   .statusCode(200)
				.extract()
				    .body()
				        .as(TokenVO.class,objectMapper)
				    .getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestsConfigs.HEADER_PARAM_AUTORIZATION, "bEARER "+ accessToken)
				.setBasePath("/api/person/v1")
				.setPort(TestsConfigs.SERVER_PORT)
				   .addFilter(new RequestLoggingFilter(LogDetail.ALL))
				   .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
	}
	
		@Test
		@Order(1)
		public void TestCreate() throws JsonMappingException, JsonProcessingException {
		
		mockPerson();
		
		
		
		var persistedPerson =
					given().spec(specification)
					.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
					.contentType(TestsConfigs.CONTENT_TYPE_YML)	
					.accept(TestsConfigs.CONTENT_TYPE_YML)
					.body(person,objectMapper)
					.when()
					   .post()
					.then()
					   .statusCode(200)
					.extract()
					    .body()
					        .as(PersonVO.class,objectMapper);
		
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());
		
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Nelson",persistedPerson.getFirstName());
		assertEquals("Piquet",persistedPerson.getLastName());
		assertEquals("Brasilia - Df",persistedPerson.getAddress());
		assertEquals("Male",persistedPerson.getGender());
	}
		
		@Test
		@Order(2)
		public void TestUpdate() throws JsonMappingException, JsonProcessingException {
		person.setLastName("Piquet Souto maior");
		var persistedPerson =
					given().spec(specification)
					.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
					.contentType(TestsConfigs.CONTENT_TYPE_YML)	
					.accept(TestsConfigs.CONTENT_TYPE_YML)
					.body(person,objectMapper)
					.when()
					   .post()
					.then()
					   .statusCode(200)
					.extract()
					.body()
			        .as(PersonVO.class,objectMapper);
		
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());
		
		assertEquals(person.getId(), persistedPerson.getId());
		
		assertEquals("Nelson",persistedPerson.getFirstName());
		assertEquals("Piquet Souto maior",persistedPerson.getLastName());
		assertEquals("Brasilia - Df",persistedPerson.getAddress());
		assertEquals("Male",persistedPerson.getGender());
	}

		@Test
		@Order(3)
		public void TestDisablePersonById() throws JsonMappingException, JsonProcessingException {
			

			var persistedPerson =
					given().spec(specification)
					.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
					.contentType(TestsConfigs.CONTENT_TYPE_YML)	
					.accept(TestsConfigs.CONTENT_TYPE_YML)
					.header(TestsConfigs.HEADER_PARAM_ORIGIN, TestsConfigs.ORIGIN_BARRETO)	
						.pathParam("id", person.getId())
						.when()
						   .patch("{id}")
						.then()
						   .statusCode(200)
							.extract()
							.body()
					        .as(PersonVO.class,objectMapper);
			
			
			person = persistedPerson;
			assertEquals(person.getId(), persistedPerson.getId());
			assertNotNull(persistedPerson);
			assertNotNull(persistedPerson.getId());
			assertNotNull(persistedPerson.getFirstName());
			assertNotNull(persistedPerson.getLastName());
			assertNotNull(persistedPerson.getAddress());
			assertNotNull(persistedPerson.getGender());
			assertNotNull(persistedPerson.getGender());
			assertFalse(persistedPerson.getEnabled());
			
			assertTrue(persistedPerson.getId() > 0);
			
			assertEquals("Nelson",persistedPerson.getFirstName());
			assertEquals("Piquet Souto maior",persistedPerson.getLastName());
			assertEquals("Brasilia - Df",persistedPerson.getAddress());
			assertEquals("Male",persistedPerson.getGender());
		}

	@Test
	@Order(4)
	public void TestFindById() throws JsonMappingException, JsonProcessingException {
		
		mockPerson();
		
	
		var persistedPerson =
				given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YML)	
				.accept(TestsConfigs.CONTENT_TYPE_YML)
				.header(TestsConfigs.HEADER_PARAM_ORIGIN, TestsConfigs.ORIGIN_BARRETO)	
					.pathParam("id", person.getId())
					.when()
					   .get("{id}")
					.then()
					   .statusCode(200)
					.extract()
					.body()
			        .as(PersonVO.class,objectMapper);
		
		
		person = persistedPerson;
		assertEquals(person.getId(), persistedPerson.getId());
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Nelson",persistedPerson.getFirstName());
		assertEquals("Piquet Souto maior",persistedPerson.getLastName());
		assertEquals("Brasilia - Df",persistedPerson.getAddress());
		assertEquals("Male",persistedPerson.getGender());
	}
	
	@Test
	@Order(5)
	public void TestDelete() throws JsonMappingException, JsonProcessingException {

		
				given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YML)	
				.accept(TestsConfigs.CONTENT_TYPE_YML)
					.pathParam("id", person.getId())
					.when()
					   .delete("{id}")
					.then()
					   .statusCode(204);
					
	}
	
	@Test
	@Order(6)
	public void TestFindAll() throws JsonMappingException, JsonProcessingException {

	var wrapper =
				given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YML)	
				.accept(TestsConfigs.CONTENT_TYPE_YML)	
				.queryParams("page",3,"size",10,"direction","asc")
				.when()
				   .post()
				.then()
				   .statusCode(200)
				.extract()
				.body()
		        .as(PagedModelPerson.class,objectMapper);
				       // .as(new TypeRef<List<PersonVO>>() {});
	
	
	
	var people = wrapper.getContent();
	PersonVO foundPersonOne = people.get(0);
	assertNotNull(foundPersonOne.getId());
	assertNotNull(foundPersonOne.getFirstName());
	assertNotNull(foundPersonOne.getLastName());
	assertNotNull(foundPersonOne.getAddress());
	assertNotNull(foundPersonOne.getGender());
	assertTrue(foundPersonOne.getEnabled());
	
	assertEquals(677,foundPersonOne.getId());
	
	assertEquals("Alic",foundPersonOne.getFirstName());
	assertEquals("Terbrug",foundPersonOne.getLastName());
	assertEquals("3 Eagle Crest Court",foundPersonOne.getAddress());
	assertEquals("Male",foundPersonOne.getGender());
	
	PersonVO foundPersonSix = people.get(5);
	assertNotNull(foundPersonSix.getId());
	assertNotNull(foundPersonSix.getFirstName());
	assertNotNull(foundPersonSix.getLastName());
	assertNotNull(foundPersonSix.getAddress());
	assertNotNull(foundPersonSix.getGender());
	assertTrue(foundPersonSix.getEnabled());
	
	assertEquals(911,foundPersonSix.getId());
	
	assertEquals("Allegra",foundPersonSix.getFirstName());
	assertEquals("Dome",foundPersonSix.getLastName());
	assertEquals("57 Roxbury Pass",foundPersonSix.getAddress());
	assertEquals("Female",foundPersonSix.getGender());
}
	@Test
	@Order(7)
	public void TestFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

	RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/person/v1")
			.setPort(TestsConfigs.SERVER_PORT)
			   .addFilter(new RequestLoggingFilter(LogDetail.ALL))
			   .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
	
	
				given().spec(specificationWithoutToken)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YML)	
				.accept(TestsConfigs.CONTENT_TYPE_YML)
				.when()
				   .post()
				.then()
				   .statusCode(403);

}
	
	@Test
	@Order(9)
	public void TestHATEOAS() throws JsonMappingException, JsonProcessingException {

	var unthreatedContent =
				given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestsConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestsConfigs.CONTENT_TYPE_YML)	
				.accept(TestsConfigs.CONTENT_TYPE_YML)	
				.queryParams("page",3,"size",10,"direction","asc")
				.when()
				   .post()
				.then()
				   .statusCode(200)
				.extract()
				.body()
		        .asString();
				       // .as(new TypeRef<List<PersonVO>>() {});
	
	var content = unthreatedContent.replace("\n", "").replace("\r", "");
	
	assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/apperson/v1/677\"}}}"));
	assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/apperson/v1/846\"}}}"));
	assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/apperson/v1/714\"}}}"));
}

	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasilia - Df");
		person.setGender("Male");
		person.setEnabled(true);
		
	}

}
