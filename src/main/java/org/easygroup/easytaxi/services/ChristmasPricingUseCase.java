package org.easygroup.easytaxi.services;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChristmasPricingUseCase implements PricingRule {

    @Override
    public double apply(double price, LocalDateTime dateTime) {
        boolean christmasPeriod = dateTime.getMonthValue() == 12 &&
                (dateTime.getDayOfMonth() == 25 ||
                        (dateTime.getDayOfMonth() == 24 && dateTime.getHour() >= 18));
        return christmasPeriod ? price * 2 : price;
    }
}
