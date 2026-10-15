package org.easygroup.easytaxi.infrastructure.mappers;

import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.persistence.entity.DriverEntity;

public class DriverMapper {

    private DriverMapper() {
    }

    public static Driver toDomain(DriverEntity entity) {
        return new Driver(entity.isAvailable(), entity.getUserName());
    }

    public static DriverEntity toEntity(Driver driver) {
        return DriverEntity.builder()
                .available(driver.isAvailable())
                .userName(driver.getUserName())
                .build();
    }
}
