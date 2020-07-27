package com.pacific.volcano.campsitereservations;

import com.pacific.volcano.campsitereservations.api.ErrorResponse;
import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import com.pacific.volcano.campsitereservations.api.ReservationResponse;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CampsiteReservationsValidationTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setup() {
        RestAssuredMockMvc.config = RestAssuredMockMvcConfig.newConfig()
                .encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));
    }

    @Test
    public void testFindAvailability_statusOk() {
        ArrayList<String> availableDates = given().mockMvc(mockMvc).log().all()
                .queryParam("from", LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE))
                .queryParam("to", LocalDate.now().plusDays(4).format(DateTimeFormatter.ISO_DATE))
                .when()
                .get("/availability")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().path("availableDates");
        assertThat(availableDates.size()).isEqualTo(4);

    }

    @Test
    public void testFindAvailability_default_statusOk() {
        ArrayList<String> availableDates = given().mockMvc(mockMvc).log().all()
                .when()
                .get("/availability")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().path("availableDates");
        assertThat(availableDates).isNotEmpty();

    }

    //testing reservation constraints
    @Test
    public void testReservation_datesOutOfBounds() {
        ReservationRequest testReservationRequest = ReservationRequest.builder()
                .arrivalDate(LocalDate.now().plusDays(32))
                .departureDate(LocalDate.now().plusDays(34))
                .email("testEmail")
                .fullname("testName").build();
        ValidatableMockMvcResponse validatableResponse = given().mockMvc(mockMvc)
                .accept(ContentType.JSON).log().all()
                .contentType(ContentType.JSON)
                .body(testReservationRequest)
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        validatableResponse.log().all();
        ErrorResponse errorResponse = validatableResponse.extract().body().as(ErrorResponse.class);
        assertThat(errorResponse.getErrors()).isNotEmpty();
        assertThat(errorResponse.getErrors())
                .contains(
                        "reservationRequest: Maximum anticipation for a reservation cannot be more than 30 days ahead of arrival");

    }

    @Test
    public void testReservation_minAnticipation() {
        ReservationRequest testReservationRequest = ReservationRequest.builder()
                .arrivalDate(LocalDate.now())
                .departureDate(LocalDate.now().plusDays(2))
                .email("testEmail")
                .fullname("testName").build();

        ValidatableMockMvcResponse validatableResponse = given().mockMvc(mockMvc)
                .accept(ContentType.JSON).log().all()
                .contentType(ContentType.JSON)
                .body(testReservationRequest)
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        validatableResponse.log().all();
        ErrorResponse errorResponse = validatableResponse.extract().body().as(ErrorResponse.class);
        assertThat(errorResponse.getErrors()).isNotEmpty();
        assertThat(errorResponse.getErrors())
                .contains("reservationRequest: Minimum anticipation for a reservation is 1 day before arrival");
    }

    @Test
    public void testReservation_maxAnticipation() {
        ReservationRequest testReservationRequest = ReservationRequest.builder()
                .arrivalDate(LocalDate.now().plusMonths(1))
                .departureDate(LocalDate.now().plusMonths(1).plusDays(2))
                .email("testEmail")
                .fullname("testName").build();

        ValidatableMockMvcResponse validatableResponse = given().mockMvc(mockMvc)
                .accept(ContentType.JSON).log().all()
                .contentType(ContentType.JSON)
                .body(testReservationRequest)
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        validatableResponse.log().all();
        ErrorResponse errorResponse = validatableResponse.extract().body().as(ErrorResponse.class);
        assertThat(errorResponse.getErrors()).isNotEmpty();
        assertThat(errorResponse.getErrors())
                .contains(
                        "reservationRequest: Maximum anticipation for a reservation cannot be more than 30 days ahead of arrival");
    }

    @Test
    public void testReservation_minStay() {
        ReservationRequest testReservationRequest = ReservationRequest.builder()
                .arrivalDate(LocalDate.now().plusDays(2))
                .departureDate(LocalDate.now().plusDays(2))
                .email("testEmail")
                .fullname("testName").build();

        ValidatableMockMvcResponse validatableResponse = given().mockMvc(mockMvc)
                .accept(ContentType.JSON).log().all()
                .contentType(ContentType.JSON)
                .body(testReservationRequest)
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        validatableResponse.log().all();
        ErrorResponse errorResponse = validatableResponse.extract().body().as(ErrorResponse.class);
        assertThat(errorResponse.getErrors()).isNotEmpty();
        assertThat(errorResponse.getErrors()).contains("reservationRequest: minimum stay is 1 day");
    }

    @Test
    public void testReservation_maxStay() {
        ReservationRequest testReservationRequest = ReservationRequest.builder()
                .arrivalDate(LocalDate.now().plusDays(2))
                .departureDate(LocalDate.now().plusDays(7))
                .email("testEmail")
                .fullname("testName").build();

        ValidatableMockMvcResponse validatableResponse = given().mockMvc(mockMvc)
                .accept(ContentType.JSON).log().all()
                .contentType(ContentType.JSON)
                .body(testReservationRequest)
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        validatableResponse.log().all();
        ErrorResponse errorResponse = validatableResponse.extract().body().as(ErrorResponse.class);
        assertThat(errorResponse.getErrors()).isNotEmpty();
        assertThat(errorResponse.getErrors()).contains("reservationRequest: maximum stay is 3 days");
    }

    //testing a creation and a cancellation
    @Test
    public void testReservation_AndAlreadyReserved() {
        ReservationRequest testReservationRequest = ReservationRequest.builder()
                .arrivalDate(LocalDate.now().plusDays(15)).departureDate(LocalDate.now().plusDays(18))
                .email("testEmail").fullname("testName").build();
        ValidatableMockMvcResponse response = given().mockMvc(mockMvc)
                .accept(ContentType.JSON).log().all()
                .contentType(ContentType.JSON)
                .body(testReservationRequest)
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.OK.value());
        response.log().all();
        ValidatableMockMvcResponse response2 = given().mockMvc(mockMvc)
                .accept(ContentType.JSON).log().all()
                .contentType(ContentType.JSON)
                .body(testReservationRequest)
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
        ErrorResponse errorResponse = response2.extract().body().as(ErrorResponse.class);
        assertThat(errorResponse.getMessage()).contains("No availability for selected dates");


    }

    //testing a creation and a cancellation
    @Test
    public void testReservation_AndCancel() {
        ReservationRequest testReservationRequest = ReservationRequest.builder()
                .arrivalDate(LocalDate.now().plusDays(10)).departureDate(LocalDate.now().plusDays(13))
                .email("testEmail").fullname("testName").build();
        ValidatableMockMvcResponse response = given().mockMvc(mockMvc)
                .accept(ContentType.JSON).log().all()
                .contentType(ContentType.JSON)
                .body(testReservationRequest)
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.OK.value());
        response.log().all();
        ReservationResponse reservationResponse = response.extract().body().as(ReservationResponse.class);
        assertThat(reservationResponse.getId()).isNotNull();
        given().mockMvc(mockMvc)
                .accept(ContentType.JSON).log().all()
                .when()
                .delete("/reservations/{id}", reservationResponse.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("active", is(false));
    }
}
