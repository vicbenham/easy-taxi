package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.ServiceType;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PricingUseCase {

    private final Clock clock;
    private final List<PricingRule> pricingRules;

    public PricingUseCase(Clock clock, List<PricingRule> pricingRules) {
        this.clock = clock;
        this.pricingRules = List.copyOf(pricingRules);
    }

    public double calculatePrice(double basePrice) {
        return applyRules(basePrice, PricingContext.withoutRider(LocalDateTime.now(clock)));
    }

    public double calculatePrice(double basePrice, ServiceType serviceType, LocalDate riderBirthDate) {
        return calculatePrice(basePrice, serviceType, riderBirthDate, LocalDateTime.now(clock));
    }

    public double calculatePrice(
            double basePrice,
            ServiceType serviceType,
            LocalDate riderBirthDate,
            LocalDateTime dateTime) {
        PricingContext context = new PricingContext(
                dateTime, serviceType, Optional.ofNullable(riderBirthDate));
        return applyRules(basePrice, context);
    }

    public double calculatePrice(double basePrice, LocalDateTime dateTime) {
        return applyRules(basePrice, PricingContext.withoutRider(dateTime));
    }

    private double applyRules(double basePrice, PricingContext context) {
        double price = basePrice;
        for (PricingRule pricingRule : pricingRules) {
            price = pricingRule.apply(price, context);
        }
        return price;
    }
}
