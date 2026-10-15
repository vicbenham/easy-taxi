package org.easygroup.easytaxi.exposition;

import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.easygroup.easytaxi.persistence.entity.DriverEntity;
import org.easygroup.easytaxi.persistence.entity.JourneyEntity;
import org.easygroup.easytaxi.persistence.entity.RiderEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:ride-api;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(RideApiIntegrationTest.TestClockConfiguration.class)
class RideApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MutableClock clock;

    @BeforeEach
    void resetClock() {
        clock.reset(Instant.parse("2026-06-01T12:00:00Z"));
    }

    @Test
    void bookingEndpointUsesTheBookingAndPricingUseCases() throws Exception {
        mockMvc.perform(post("/api/rides/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "riderMoney": 100.0,
                                  "riderBirthDate": "1990-01-01",
                                  "driverAvailable": true,
                                  "departure": "PARIS",
                                  "arrival": "PARIS",
                                  "kilometers": 10.0,
                                  "serviceType": "UBER_X"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(12.0));
    }

    @Test
    void pricingEndpointUsesTheRegisteredPricingRules() throws Exception {
        mockMvc.perform(post("/api/pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "basePrice": 10.0,
                                  "serviceType": "UBER_X",
                                  "riderBirthDate": "1990-01-01"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(15.0));
    }

    @Test
    void bookingEndpointRejectsAnUnavailableDriver() throws Exception {
        mockMvc.perform(post("/api/rides/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "riderMoney": 100.0,
                                  "riderBirthDate": "1990-01-01",
                                  "driverAvailable": false,
                                  "departure": "PARIS",
                                  "arrival": "LYON",
                                  "kilometers": 10.0,
                                  "serviceType": "STANDARD"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void estimateReturnsATokenWithoutCreatingABookingAndBooksAtTheEstimatedPrice() throws Exception {
        long initialBookingCount = entityManager.createQuery(
                "select count(booking) from BookingEntity booking", Long.class).getSingleResult();

        String estimateResponse = mockMvc.perform(post("/api/rides/estimate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "riderMoney": 100.0,
                                  "riderBirthDate": "1990-01-01",
                                  "driverAvailable": true,
                                  "departure": "PARIS",
                                  "arrival": "PARIS",
                                  "kilometers": 10.0,
                                  "serviceType": "UBER_X"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(12.0))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(estimateResponse, "$.token");

        assertEquals(initialBookingCount, entityManager.createQuery(
                "select count(booking) from BookingEntity booking", Long.class).getSingleResult());

        mockMvc.perform(post("/api/rides/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"%s\"}".formatted(token)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(12.0));
    }

    @Test
    void publicEstimateAndBookRoutesAcceptFromAndToAndPersistTheRide() throws Exception {
        long initialBookingCount = entityManager.createQuery(
                "select count(booking) from BookingEntity booking", Long.class).getSingleResult();

        String response = mockMvc.perform(post("/rides/estimate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "riderMoney": 100.0,
                                  "riderBirthDate": "1990-01-01",
                                  "driverAvailable": true,
                                  "from": "PARIS",
                                  "to": "PARIS",
                                  "kilometers": 10.0,
                                  "serviceType": "UBER_X"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(12.0))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(response, "$.token");
        String expiresAt = JsonPath.read(response, "$.expiresAt");
        assertEquals(clock.instant().plusSeconds(300), Instant.parse(expiresAt));

        assertEquals(initialBookingCount, entityManager.createQuery(
                "select count(booking) from BookingEntity booking", Long.class).getSingleResult());

        String bookingResponse = mockMvc.perform(post("/rides/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"%s\"}".formatted(token)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.price").value(12.0))
                .andReturn().getResponse().getContentAsString();
        Number bookingIdentifier = JsonPath.read(bookingResponse, "$.id");
        Long bookingId = bookingIdentifier.longValue();
        BookingEntity savedBooking = entityManager.find(BookingEntity.class, bookingId);
        assertEquals(BigDecimal.valueOf(12.0), savedBooking.getPrice());

        assertEquals(initialBookingCount + 1, entityManager.createQuery(
                "select count(booking) from BookingEntity booking", Long.class).getSingleResult());
    }

    @Test
    void bookEndpointReturns404409And410ForInvalidUsedAndExpiredTokens() throws Exception {
        mockMvc.perform(post("/rides/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"unknown-token\"}"))
                .andExpect(status().isNotFound());

        String response = mockMvc.perform(post("/rides/estimate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estimateRequest()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String usedToken = JsonPath.read(response, "$.token");

        mockMvc.perform(post("/rides/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"%s\"}".formatted(usedToken)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/rides/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"%s\"}".formatted(usedToken)))
                .andExpect(status().isConflict());

        String expiringResponse = mockMvc.perform(post("/rides/estimate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estimateRequest()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String expiringToken = JsonPath.read(expiringResponse, "$.token");
        clock.advanceSeconds(300);

        mockMvc.perform(post("/rides/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"%s\"}".formatted(expiringToken)))
                .andExpect(status().isGone());
    }

    private String estimateRequest() {
        return """
                {
                  "riderMoney": 100.0,
                  "riderBirthDate": "1990-01-01",
                  "driverAvailable": true,
                  "from": "PARIS",
                  "to": "PARIS",
                  "kilometers": 10.0,
                  "serviceType": "UBER_X"
                }
                """;
    }

    @Test
    void estimateRequiresFromAndTo() throws Exception {
        String requestWithoutFrom = """
                {
                  "riderMoney": 100.0,
                  "riderBirthDate": "1990-01-01",
                  "driverAvailable": true,
                  "to": "PARIS",
                  "kilometers": 10.0,
                  "serviceType": "STANDARD"
                }
                """;
        mockMvc.perform(post("/rides/estimate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithoutFrom))
                .andExpect(status().isBadRequest());

        String requestWithoutTo = """
                {
                  "riderMoney": 100.0,
                  "riderBirthDate": "1990-01-01",
                  "driverAvailable": true,
                  "from": "PARIS",
                  "kilometers": 10.0,
                  "serviceType": "STANDARD"
                }
                """;
        mockMvc.perform(post("/rides/estimate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithoutTo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void historyEndpointReturnsThePersistedRidesWithTheirDrivers() throws Exception {
        RiderEntity rider = RiderEntity.builder()
                .money(BigDecimal.valueOf(100))
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
        DriverEntity driver = DriverEntity.builder().available(true).build();
        JourneyEntity journey = JourneyEntity.builder()
                .departure("PARIS")
                .arrival("LYON")
                .kilometers(BigDecimal.TEN)
                .build();
        entityManager.persist(rider);
        entityManager.persist(driver);
        entityManager.persist(journey);
        entityManager.persist(BookingEntity.builder()
                .price(BigDecimal.valueOf(15))
                .rider(rider)
                .driver(driver)
                .journey(journey)
                .build());
        entityManager.flush();

        mockMvc.perform(get("/api/riders/{riderId}/history", rider.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(15))
                .andExpect(jsonPath("$[0].driver.id").value(driver.getId()))
                .andExpect(jsonPath("$[0].journey.departure").value("PARIS"))
                .andExpect(jsonPath("$[0].journey.arrival").value("LYON"));
    }

    @TestConfiguration
    static class TestClockConfiguration {

        @Bean
        @Primary
        MutableClock testClock() {
            return new MutableClock(Instant.parse("2026-06-01T12:00:00Z"));
        }
    }

    static class MutableClock extends Clock {

        private Instant instant;

        MutableClock(Instant instant) {
            this.instant = instant;
        }

        void advanceSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }

        void reset(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
