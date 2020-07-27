package com.pacific.volcano.campsitereservations.integration;

import com.pacific.volcano.campsitereservations.CampsiteReservationsApplication;
import com.pacific.volcano.campsitereservations.api.ReservationRequest;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(classes = {CampsiteReservationsApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    public void findAvailabilityStatusOk() {
        ArrayList<String> availablesDates = given()
                .accept(ContentType.JSON).log().all()
                .when()
                .queryParam("from", LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE))
                .queryParam("to", LocalDate.now().plusDays(4).format(DateTimeFormatter.ISO_DATE))
                .get("/availability")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract().path("availableDates");

        assertThat(availablesDates).contains(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE));

    }

    @Test
    public void testPost() {
        ReservationRequest testReservationRequest = ReservationRequest.builder().arrivalDate(LocalDate.now().plusDays(10)).departureDate(LocalDate.now().plusDays(13)).email("testEmail").fullname("testName").build();
        Integer id = given()
                .accept(ContentType.JSON).log().all()
                .contentType(ContentType.JSON)
                .body(testReservationRequest)
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.OK.value())
        .extract().path("id");
        assertThat(id).isNotNull();
        given()
                .accept(ContentType.JSON).log().all()
                .when()
                .pathParam("id",id)
                .delete("/reservations/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("active",is(false));

    }

}
