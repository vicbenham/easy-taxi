package org.easygroup.easytaxi.persistence;

import org.easygroup.easytaxi.persistence.entity.RiderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<RiderEntity, Long> {
}
