package org.easygroup.easytaxi.exposition;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.domain.ServiceType;
import org.easygroup.easytaxi.services.BookRideUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class BookRideControllerTest {

    private final BookRideUseCase bookRideUseCase = mock(BookRideUseCase.class);
    private final MockMvc mockMvc = standaloneSetup(new BookRideController(bookRideUseCase)).build();

    @Test
    void booksARideAndReturnsItsPrice() throws Exception {
        when(bookRideUseCase.bookRide(
                any(Driver.class), any(Rider.class), any(Journey.class), eq(ServiceType.UBER_X)))
                .thenReturn(new Booking(new Journey("PARIS", "LYON", 10.0), 20.0));

        mockMvc.perform(post("/api/rides/book")
                        .contentType("application/json")
                        .content("""
                                {
                                  "riderMoney": 100.0,
                                  "riderBirthDate": "1990-01-01",
                                  "driverAvailable": true,
                                  "departure": "PARIS",
                                  "arrival": "LYON",
                                  "kilometers": 10.0,
                                  "serviceType": "UBER_X"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(20.0));

        verify(bookRideUseCase).bookRide(
                any(Driver.class), any(Rider.class), any(Journey.class), eq(ServiceType.UBER_X));
    }

    @Test
    void rejectsRequestsMissingRequiredRideData() throws Exception {
        mockMvc.perform(post("/api/rides/book")
                        .contentType("application/json")
                        .content("""
                                {
                                  "riderMoney": 100.0,
                                  "driverAvailable": true,
                                  "departure": "PARIS",
                                  "arrival": "LYON",
                                  "kilometers": 10.0,
                                  "serviceType": "STANDARD"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
