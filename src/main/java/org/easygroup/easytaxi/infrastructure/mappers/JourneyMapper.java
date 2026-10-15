package org.easygroup.easytaxi.infrastructure.mappers;

import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.persistence.entity.JourneyEntity;

import java.math.BigDecimal;

public class JourneyMapper {

    private JourneyMapper() {
    }

    public static Journey toDomain(JourneyEntity entity) {
        return new Journey(
                entity.getDeparture(),
                entity.getArrival(),
                entity.getKilometers().doubleValue(),
                entity.getDateRide());
    }

    public static JourneyEntity toEntity(Journey journey) {
        return JourneyEntity.builder()
                .departure(journey.getDeparture())
                .arrival(journey.getArrival())
                .kilometers(BigDecimal.valueOf(journey.getKilometers()))
                .dateRide(journey.getDateRide())
                .build();
    }
}
