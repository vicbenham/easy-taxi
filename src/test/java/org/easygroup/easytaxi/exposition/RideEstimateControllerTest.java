package org.easygroup.easytaxi.exposition;

import org.easygroup.easytaxi.services.BookRideUseCase;
import org.easygroup.easytaxi.services.ChristmasPricingUseCase;
import org.easygroup.easytaxi.services.PeakHoursPricingUseCase;
import org.easygroup.easytaxi.services.PricingUseCase;
import org.easygroup.easytaxi.services.RideEstimateUseCase;
import org.easygroup.easytaxi.services.RidePersister;
import org.easygroup.easytaxi.services.UberXPricingUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class RideEstimateControllerTest {

    private final RideEstimateUseCase rideEstimateUseCase = createUseCase();
    private final MockMvc mockMvc = standaloneSetup(
            new RideEstimateController(rideEstimateUseCase),
            new BookRideController(new BookRideUseCase(createPricingUseCase()), rideEstimateUseCase)).build();

    @Test
    void estimatesAndBooksArideUsingTheReturnedToken() throws Exception {
        String response = mockMvc.perform(post("/api/rides/estimate")
                        .contentType("application/json")
                        .content(estimateRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(12.0))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        String token = com.jayway.jsonpath.JsonPath.read(response, "$.token");

        mockMvc.perform(post("/api/rides/book")
                        .contentType("application/json")
                        .content("""
                                {"token":"%s"}
                                """.formatted(token)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(12.0));
    }

    @Test
    void estimateRequiresEveryBookingField() throws Exception {
        mockMvc.perform(post("/api/rides/estimate")
                        .contentType("application/json")
                        .content("""
                                {
                                  "riderMoney": 100.0,
                                  "driverAvailable": true,
                                  "departure": "PARIS",
                                  "arrival": "PARIS",
                                  "kilometers": 10.0,
                                  "serviceType": "UBER_X"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidExpiredAndAlreadyUsedTokensReturnTheirHttpStatuses() throws Exception {
        mockMvc.perform(post("/api/rides/book")
                        .contentType("application/json")
                        .content("{\"token\":\"invalid\"}"))
                .andExpect(status().isNotFound());

        String response = mockMvc.perform(post("/api/rides/estimate")
                        .contentType("application/json")
                        .content(estimateRequest()))
                .andReturn().getResponse().getContentAsString();
        String token = com.jayway.jsonpath.JsonPath.read(response, "$.token");

        mockMvc.perform(post("/api/rides/book")
                        .contentType("application/json")
                        .content("{\"token\":\"%s\"}".formatted(token)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/rides/book")
                        .contentType("application/json")
                        .content("{\"token\":\"%s\"}".formatted(token)))
                .andExpect(status().isConflict());

        String expiringResponse = mockMvc.perform(post("/api/rides/estimate")
                        .contentType("application/json")
                        .content(estimateRequest()))
                .andReturn().getResponse().getContentAsString();
        String expiringToken = com.jayway.jsonpath.JsonPath.read(expiringResponse, "$.token");
        AdjustableClock movingClock = clock;
        movingClock.advanceSeconds(300);

        mockMvc.perform(post("/api/rides/book")
                        .contentType("application/json")
                        .content("{\"token\":\"%s\"}".formatted(expiringToken)))
                .andExpect(status().isGone());
    }

    private RideEstimateUseCase createUseCase() {
        return new RideEstimateUseCase(
                new BookRideUseCase(createPricingUseCase()), clock, mock(RidePersister.class));
    }

    private PricingUseCase createPricingUseCase() {
        return new PricingUseCase(clock, List.of(
                new UberXPricingUseCase(),
                new ChristmasPricingUseCase(),
                new PeakHoursPricingUseCase()));
    }

    private static final AdjustableClock clock =
            new AdjustableClock(Instant.parse("2026-06-01T12:00:00Z"));

    private String estimateRequest() {
        return """
                {
                  "riderMoney": 100.0,
                  "riderBirthDate": "1990-01-01",
                  "driverAvailable": true,
                  "departure": "PARIS",
                  "arrival": "PARIS",
                  "kilometers": 10.0,
                  "serviceType": "UBER_X"
                }
                """;
    }

    private static class AdjustableClock extends Clock {
        private Instant instant;

        private AdjustableClock(Instant instant) {
            this.instant = instant;
        }

        private void advanceSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
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
