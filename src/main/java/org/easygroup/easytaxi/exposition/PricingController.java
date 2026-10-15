package org.easygroup.easytaxi.exposition;

import org.easygroup.easytaxi.domain.ServiceType;
import org.easygroup.easytaxi.services.PricingUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingUseCase pricingUseCase;

    public PricingController(PricingUseCase pricingUseCase) {
        this.pricingUseCase = pricingUseCase;
    }
    @PostMapping
    public double calculatePrice(@Valid @RequestBody PricingRequest request) {
        return pricingUseCase.calculatePrice(
                request.basePrice(), request.serviceType(), request.riderBirthDate());
    }

    public record PricingRequest(
            @NotNull @PositiveOrZero Double basePrice,
            @NotNull ServiceType serviceType,
            @NotNull LocalDate riderBirthDate) {
    }
}
