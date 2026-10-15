package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.ServiceType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Order(1)
public class UberXPricingUseCase implements PricingRule {

    private static final double UBER_X_SURCHARGE = 5.0;

    @Override
    public double apply(double price, PricingContext context) {
        if (context.serviceType() != ServiceType.UBER_X || isRiderBirthday(context)) {
            return price;
        }
        return price + UBER_X_SURCHARGE;
    }

    private boolean isRiderBirthday(PricingContext context) {
        LocalDate currentDate = context.dateTime().toLocalDate();
        return context.riderBirthDate()
                .map(birthDate -> birthDate.getMonth() == currentDate.getMonth()
                        && birthDate.getDayOfMonth() == currentDate.getDayOfMonth())
                .orElse(false);
    }
}
