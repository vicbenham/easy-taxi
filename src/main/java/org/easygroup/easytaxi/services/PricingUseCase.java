package org.easygroup.easytaxi.services;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PricingUseCase {

    private final Clock clock;
    private final List<PricingRule> pricingRules;

    public PricingUseCase(Clock clock, List<PricingRule> pricingRules) {
        this.clock = clock;
        this.pricingRules = List.copyOf(pricingRules);
    }

    public double calculatePrice(double basePrice) {
        return calculatePrice(basePrice, LocalDateTime.now(clock));
    }

    public double calculatePrice(double basePrice, LocalDateTime dateTime) {
        double price = basePrice;
        for (PricingRule pricingRule : pricingRules) {
            price = pricingRule.apply(price, dateTime);
        }
        return price;
    }
}
