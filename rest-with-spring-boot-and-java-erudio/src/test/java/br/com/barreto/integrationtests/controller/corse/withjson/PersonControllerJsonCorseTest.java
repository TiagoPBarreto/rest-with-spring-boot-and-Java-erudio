package br.com.barreto.integrationtests.controller.corse.withjson;

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
import org.springframework.boot.test.context.TestConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import  com.fasterxml.jackson.databind.ObjectMapper;

import br.com.barreto.configs.TestsConfigs;
import br.com.barreto.data.vo.v1.security.TokenVO;
import br.com.barreto.integrationtests.testcontainers.AbstracticIntegrationTest;
import br.com.barreto.integrationtests.vo.AccountCredentialVO;
import br.com.barreto.integrationtests.vo.PersonVO;
import br.com.barreto.integrationtests.vo.wrappers.WrapperPersonVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerJsonCorseTest extends AbstracticIntegrationTest {

	private static RequestSpecification specification;
	private static ObjectMapper objctMapper;
	
	private static PersonVO person;
	
	@BeforeAll
	public static void setup() {
		objctMapper = new ObjectMapper();
		objctMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		person = new  PersonVO();
		
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialVO user = new AccountCredentialVO("leandro", "admin123");
		var accessToken =
				given()
				.basePath("/auth/signin")
				.port(TestsConfigs.SERVER_PORT)
				.contentType(TestsConfigs.CONTENT_TYPE_JSON)
				
				.body(user)
				.when()
				   .post()
				.then()
				   .statusCode(200)
				.extract()
				    .body()
				        .as(TokenVO.class)
				    .getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestsConfigs.HEADER_PARAM_AUTORIZATION, "bearer "+ accessToken)
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
		
		
		
		var content =
					given().spec(specification)
					.contentType(TestsConfigs.CONTENT_TYPE_JSON)	
					.body(person)
					.when()
					   .post()
					.then()
					   .statusCode(200)
					.extract()
					    .body()
					        .asString();
		
		PersonVO persistedPerson = objctMapper.readValue(content, PersonVO.class);
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
		var content =
					given().spec(specification)
					.contentType(TestsConfigs.CONTENT_TYPE_JSON)	
					.body(person)
					.when()
					   .post()
					.then()
					   .statusCode(200)
					.extract()
					    .body()
					        .asString();
		
		PersonVO persistedPerson = objctMapper.readValue(content, PersonVO.class);
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
	@Order(4)
	public void TestFindById() throws JsonMappingException, JsonProcessingException {
		
		mockPerson();
		
	
		var content =
				given().spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_JSON)	
				.header(TestsConfigs.HEADER_PARAM_ORIGIN, TestsConfigs.ORIGIN_BARRETO)	
					.pathParam("id", person.getId())
					.when()
					   .get("{id}")
					.then()
					   .statusCode(200)
					.extract()
					    .body()
					        .asString();
		
		PersonVO persistedPerson = objctMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		assertEquals(person.getId(), persistedPerson.getId());
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Nelson",persistedPerson.getFirstName());
		assertEquals("Piquet Souto maior",persistedPerson.getLastName());
		assertEquals("Brasilia - Df",persistedPerson.getAddress());
		assertEquals("Male",persistedPerson.getGender());
	}
	
	@Test
	@Order(3)
	public void TestDisablePersonById() throws JsonMappingException, JsonProcessingException {
		

		var content =
				given().spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_JSON)	
				.header(TestsConfigs.HEADER_PARAM_ORIGIN, TestsConfigs.ORIGIN_BARRETO)	
					.pathParam("id", person.getId())
					.when()
					   .patch("{id}")
					.then()
					   .statusCode(200)
					.extract()
					    .body()
					        .asString();
		
		PersonVO persistedPerson = objctMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		assertEquals(person.getId(), persistedPerson.getId());
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());
		assertNotNull(persistedPerson.getGender());
		
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
				.contentType(TestsConfigs.CONTENT_TYPE_JSON)		
					.pathParam("id", person.getId())
					.when()
					   .delete("{id}")
					.then()
					   .statusCode(204);
					
	}
	
	@Test
	@Order(6)
	public void TestFindAll() throws JsonMappingException, JsonProcessingException {

	var content =
				given().spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_JSON)	
				.queryParams("page",3,"size",10,"direction","asc")
				.when()
				   .post()
				.then()
				   .statusCode(200)
				.extract()
				    .body()
				    	.asString();
				       // .as(new TypeRef<List<PersonVO>>() {});
	
	
	WrapperPersonVO wrapper = objctMapper.readValue(content, WrapperPersonVO.class);
	var people = wrapper.getEmbedded().getPersons();
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
	public void TestFindByName() throws JsonMappingException, JsonProcessingException {

	var content =
				given().spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_JSON)
				.accept(TestsConfigs.CONTENT_TYPE_JSON)
				.pathParam("firstName", "ayr")
				.queryParams("page",0,"size",6,"direction","asc")
				.when()
				   .get("findPersonByName/{firstName}")
				.then()
				   .statusCode(200)
				.extract()
				    .body()
				    	.asString();
				       // .as(new TypeRef<List<PersonVO>>() {});
	
	
	WrapperPersonVO wrapper = objctMapper.readValue(content, WrapperPersonVO.class);
	var people = wrapper.getEmbedded().getPersons();
	PersonVO foundPersonOne = people.get(0);
	assertNotNull(foundPersonOne.getId());
	assertNotNull(foundPersonOne.getFirstName());
	assertNotNull(foundPersonOne.getLastName());
	assertNotNull(foundPersonOne.getAddress());
	assertNotNull(foundPersonOne.getGender());
	assertTrue(foundPersonOne.getEnabled());
	
	assertEquals(1,foundPersonOne.getId());
	
	assertEquals("Ayrton",foundPersonOne.getFirstName());
	assertEquals("Senna",foundPersonOne.getLastName());
	assertEquals("São Paulo",foundPersonOne.getAddress());
	assertEquals("Male",foundPersonOne.getGender());
	
}
	@Test
	@Order(8)
	public void TestFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

	RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/person/v1")
			.setPort(TestsConfigs.SERVER_PORT)
			   .addFilter(new RequestLoggingFilter(LogDetail.ALL))
			   .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
	
	
				given().spec(specificationWithoutToken)
				.contentType(TestsConfigs.CONTENT_TYPE_JSON)	
				
				.when()
				   .post()
				.then()
				   .statusCode(403);

}
	
	@Test
	@Order(9)
	public void TestHATEOAS() throws JsonMappingException, JsonProcessingException {

	var content =
				given().spec(specification)
				.contentType(TestsConfigs.CONTENT_TYPE_JSON)	
				.queryParams("page",3,"size",10,"direction","asc")
				.when()
				   .post()
				.then()
				   .statusCode(200)
				.extract()
				    .body()
				    	.asString();
				       // .as(new TypeRef<List<PersonVO>>() {});
	
	
	
	assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/apperson/v1/845\"}}}"));
	
}

	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasilia - Df");
		person.setGender("Male");
		person.setEnabled(true);
		
	}

}
