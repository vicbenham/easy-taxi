package org.easygroup.easytaxi.persistence;

import org.easygroup.easytaxi.persistence.entity.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<DriverEntity, Long> {
}
