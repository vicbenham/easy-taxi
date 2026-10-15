package org.easygroup.easytaxi.exposition;

import org.easygroup.easytaxi.services.RawPriceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class RawPriceControllerTest {

    private final RawPriceUseCase rawPriceUseCase = mock(RawPriceUseCase.class);
    private final MockMvc mockMvc = standaloneSetup(new RawPriceController(rawPriceUseCase)).build();

    @Test
    void returnsThePriceCalculatedByTheUseCase() throws Exception {
        when(rawPriceUseCase.calculatePrice("Point A, Paris", "Point B, Lyon")).thenReturn(15.0);

        mockMvc.perform(get("/api/price")
                        .param("origin", "Point A, Paris")
                        .param("destination", "Point B, Lyon"))
                .andExpect(status().isOk())
                .andExpect(content().json("15.0"));

        verify(rawPriceUseCase).calculatePrice("Point A, Paris", "Point B, Lyon");
    }
}
