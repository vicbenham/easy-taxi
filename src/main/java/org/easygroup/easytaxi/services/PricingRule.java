package org.easygroup.easytaxi.services;

@FunctionalInterface
public interface PricingRule {

    double apply(double price, PricingContext context);
}
