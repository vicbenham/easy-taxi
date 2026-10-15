package org.easygroup.easytaxi.services;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class PeakHoursPricingUseCase implements PricingRule {

    private static final double PEAK_HOUR_MULTIPLIER = 1.5;

    @Override
    public double apply(double price, PricingContext context) {
        int hour = context.dateTime().getHour();
        boolean isPeakHour = (hour >= 8 && hour < 9) || (hour >= 18 && hour < 20);
        return isPeakHour ? price * PEAK_HOUR_MULTIPLIER : price;
    }
}
