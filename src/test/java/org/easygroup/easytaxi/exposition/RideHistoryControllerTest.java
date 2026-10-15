package org.easygroup.easytaxi.exposition;

import org.easygroup.easytaxi.services.RideHistoryUseCase;
import org.easygroup.easytaxi.services.RideHistoryUseCase.RideHistoryEntry;
import org.easygroup.easytaxi.services.RideHistoryUseCase.DriverDetails;
import org.easygroup.easytaxi.services.RideHistoryUseCase.JourneyDetails;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class RideHistoryControllerTest {

    private final RideHistoryUseCase rideHistoryUseCase = mock(RideHistoryUseCase.class);
    private final MockMvc mockMvc = standaloneSetup(new RideHistoryController(rideHistoryUseCase)).build();

    @Test
    void returnsTheRidersRideHistoryWithTheDriverForEachRide() throws Exception {
        when(rideHistoryUseCase.getHistoryForRider(42L)).thenReturn(List.of(
                new RideHistoryEntry(
                        7L,
                        BigDecimal.valueOf(12),
                        new DriverDetails(3L, true),
                        new JourneyDetails("PARIS", "LYON", BigDecimal.TEN))));

        mockMvc.perform(get("/api/riders/42/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(7))
                .andExpect(jsonPath("$[0].price").value(12))
                .andExpect(jsonPath("$[0].driver.id").value(3))
                .andExpect(jsonPath("$[0].driver.available").value(true))
                .andExpect(jsonPath("$[0].journey.departure").value("PARIS"))
                .andExpect(jsonPath("$[0].journey.arrival").value("LYON"));

        verify(rideHistoryUseCase).getHistoryForRider(42L);
    }

    @Test
    void returnsAnEmptyListWhenTheRiderHasNoRideHistory() throws Exception {
        when(rideHistoryUseCase.getHistoryForRider(42L)).thenReturn(List.of());

        mockMvc.perform(get("/api/riders/42/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
