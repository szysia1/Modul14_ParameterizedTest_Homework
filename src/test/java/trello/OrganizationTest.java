package trello;

import base.Credentials;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

public class OrganizationTest extends Credentials {

    private static final String KEY = Credentials.yourKey;
    private static final String TOKEN = Credentials.yourToken;

    private static Stream<Arguments> createValidOrganizationData() {
        return Stream.of(
                Arguments.of("This is display name", "It's just an example", "akademiaqa", "https://test.pl"),
                Arguments.of("This is display name", "It's just an example", "akademiaqa", "http://test.pl"),
                Arguments.of("This is display name", "It's just an example", "aqa", "http://test.pl"),
                Arguments.of("This is display name", "It's just an example", "akademia_qa", "http://test.pl"),
                Arguments.of("This is display name", "It's just an example", "akademiaqa123", "http://test.pl"));
    }

    private static Stream<Arguments> createInvalidOrganizationData() {
        return Stream.of(
                Arguments.of("", "It's just an example", "akademia qa", "https://test.pl"),
                Arguments.of("This is display name", "It's just an example", "a1", "https://test.pl"),
                Arguments.of("This is display name", "It's just an example", "!@#$%^&*()", "https://test.pl"),
                Arguments.of("This is display name", "It's just an example", "UPPERCASE", "https://test.pl"),
                Arguments.of("This is display name", "It's just an example", "", "https://test.pl"),
                Arguments.of("This is display name", "It's just an example", "akademiaqa", "www.test.pl"));
    }

    @DisplayName("Create organization with valid data")
    @ParameterizedTest(name = "Display name: {0}, desc: {1}, name: {2}, website: {3}")
    @MethodSource("createValidOrganizationData")
    public void createOrganizationWithValidData(String displayName, String desc, String name, String website) {

        Response response = given()
                .contentType(ContentType.JSON)
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .queryParam("displayName", displayName)
                .queryParam("name", name)
                .queryParam("desc", desc)
                .queryParam("website", website)
                .when()
                .post("https://api.trello.com/1/organizations")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(displayName);

        final String organizationId = json.getString("id");

        given()
                .contentType(ContentType.JSON)
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .when()
                .delete("https://api.trello.com/1/organizations/" + organizationId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @DisplayName("Create organization with invalid data")
    @ParameterizedTest(name = "Display name: {0}, desc: {1}, name: {2}, website: {3}")
    @MethodSource("createInvalidOrganizationData")
    public void createOrganizationWithInvalidData(String displayName, String desc, String name, String website) {

        given()
                .contentType(ContentType.JSON)
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .queryParam("displayName", displayName)
                .queryParam("name", name)
                .queryParam("desc", desc)
                .queryParam("website", website)
                .when()
                .post("https://api.trello.com/1/organizations")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
