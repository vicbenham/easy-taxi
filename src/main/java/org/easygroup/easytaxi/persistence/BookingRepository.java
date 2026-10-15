package org.easygroup.easytaxi.persistence;

import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Query("""
            select booking
            from BookingEntity booking
            join fetch booking.driver
            join fetch booking.journey
            where booking.rider.id = :riderId
            order by booking.id asc
            """)
    List<BookingEntity> findHistoryByRiderIdOrderByIdAsc(@Param("riderId") Long riderId);
}
