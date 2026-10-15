package org.easygroup.easytaxi.persistence;

import org.easygroup.easytaxi.persistence.entity.JourneyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneyRepository extends JpaRepository<JourneyEntity, Long> {
}
