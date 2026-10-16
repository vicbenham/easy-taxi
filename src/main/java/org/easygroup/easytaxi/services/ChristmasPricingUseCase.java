package org.easygroup.easytaxi.services;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(2)
public class ChristmasPricingUseCase implements PricingRule {

    @Override
    public double apply(double price, PricingContext context) {
        var dateTime = context.dateTime();
        boolean christmasPeriod = dateTime.getMonthValue() == 12 &&
                (dateTime.getDayOfMonth() == 25);
        return christmasPeriod ? price * 2 : price;
    }
}
