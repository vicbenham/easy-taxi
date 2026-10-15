package org.easygroup.easytaxi.services;

import java.time.LocalDateTime;

@FunctionalInterface
public interface PricingRule {

    double apply(double price, LocalDateTime dateTime);
}
