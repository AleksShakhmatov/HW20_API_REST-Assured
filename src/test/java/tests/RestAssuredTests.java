package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;

public class RestAssuredTests {

    @BeforeAll
    static void restAssuredBase() {
        RestAssured.baseURI = "https://reqres.in/";
    }

    @Test
    void getSingleResourceWithSchemaTest() {
        Response response = given()
                .log().uri()
                .log().method()
                .when()
                .get("api/unknown/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/single-resource-schema.json"))
                .extract().response();

        assertThat(response.path("data.id"), is(2));
        assertThat(response.path("data.name"), is("fuchsia rose"));
        assertThat(response.path("data.year"), is(2001));
        assertThat(response.path("data.color"), is("#C74375"));
        assertThat(response.path("data.pantone_value"), is("17-2031"));
        assertThat(response.path("support.url"), is("https://reqres.in/#support-heading"));
        assertThat(response.path("support.text"), is("To keep ReqRes free, contributions towards server costs are appreciated!"));

    }

    @Test
    void postCreateUserWithSchemaTest() {
        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"name\": \"morpheus\", \"job\": \"leader\" }")
                .when()
                .post("api/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/create-user-schema.json"))
                .extract().response();

        assertThat(response.path("name"), is("morpheus"));
        assertThat(response.path("job"), is("leader"));
    }

    @Test
    void putUpdateUserWithSchemaTest() {
        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"name\": \"morpheus\", \"job\": \"zion resident\" }")
                .when()
                .put("api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/put-user-schema.json"))
                .extract().response();

        assertThat(response.path("name"), is("morpheus"));
        assertThat(response.path("job"), is("zion resident"));
    }

    @Test
    void patchUpdateUserWithSchemaTest() {
        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"name\": \"morpheus\", \"job\": \"zion resident\" }")
                .when()
                .patch("api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/patch-user-schema.json"))
                .extract().response();

        assertThat(response.path("name"), is("morpheus"));
        assertThat(response.path("job"), is("zion resident"));
    }

    @Test
    void deleteUserWithSchemaTest() {
                given()
                .log().uri()
                .log().method()
                .when()
                .delete("api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(204)
                .extract().response();
    }

    @Test
    void postRegisterUnsuccessfulWithSchemaTest() {
        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"email\": \"sydney@fife\" }")
                .when()
                .post("api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("schemas/register-unsuccessful-schema.json"))
                .extract().response();

        assertThat(response.path("error"), is("Missing password"));
    }
}
