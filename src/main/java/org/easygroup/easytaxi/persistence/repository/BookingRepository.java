package org.easygroup.easytaxi.persistence.repository;

import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.easygroup.easytaxi.persistence.entity.RiderEntity;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByRider(RiderEntity rider);

}
