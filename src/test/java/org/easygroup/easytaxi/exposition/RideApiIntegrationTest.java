package org.easygroup.easytaxi.exposition;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.easygroup.easytaxi.persistence.entity.DriverEntity;
import org.easygroup.easytaxi.persistence.entity.JourneyEntity;
import org.easygroup.easytaxi.persistence.entity.RiderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

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
class RideApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

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
}
