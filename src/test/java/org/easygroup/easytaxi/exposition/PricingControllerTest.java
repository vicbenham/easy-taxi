package org.easygroup.easytaxi.exposition;

import org.easygroup.easytaxi.domain.ServiceType;
import org.easygroup.easytaxi.services.PricingUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class PricingControllerTest {

    private final PricingUseCase pricingUseCase = mock(PricingUseCase.class);
    private final MockMvc mockMvc = standaloneSetup(new PricingController(pricingUseCase)).build();

    @Test
    void calculatesThePriceWithTheSelectedServiceAndRiderBirthday() throws Exception {
        when(pricingUseCase.calculatePrice(10.0, ServiceType.UBER_X, LocalDate.of(1990, 1, 1)))
                .thenReturn(15.0);

        mockMvc.perform(post("/api/pricing")
                        .contentType("application/json")
                        .content("""
                                {
                                  "basePrice": 10.0,
                                  "serviceType": "UBER_X",
                                  "riderBirthDate": "1990-01-01"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().json("15.0"));

        verify(pricingUseCase).calculatePrice(10.0, ServiceType.UBER_X, LocalDate.of(1990, 1, 1));
    }

    @Test
    void rejectsRequestsWithoutRequiredPricingData() throws Exception {
        mockMvc.perform(post("/api/pricing")
                        .contentType("application/json")
                        .content("""
                                {
                                  "basePrice": 10.0,
                                  "serviceType": "UBER_X"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
