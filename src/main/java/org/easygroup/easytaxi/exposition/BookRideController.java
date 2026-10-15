package org.easygroup.easytaxi.exposition;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.domain.ServiceType;
import org.easygroup.easytaxi.services.BookRideUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rides")
public class BookRideController {

    private final BookRideUseCase bookRideUseCase;

    public BookRideController(BookRideUseCase bookRideUseCase) {
        this.bookRideUseCase = bookRideUseCase;
    }

    @PostMapping("/book")
    public BookingResponse bookRide(@Valid @RequestBody BookRideRequest request) {
        Booking booking = bookRideUseCase.bookRide(
                new Driver(request.driverAvailable()),
                new Rider(request.riderMoney(), request.riderBirthDate()),
                new Journey(request.departure(), request.arrival(), request.kilometers()),
                request.serviceType());
        return new BookingResponse(booking.getPrice());
    }

    public record BookRideRequest(
            @NotNull @PositiveOrZero
            Double riderMoney,
            @NotNull
            LocalDate riderBirthDate,
            @NotNull
            Boolean driverAvailable,
            @NotBlank
            String departure,
            @NotBlank
            String arrival,
            @NotNull @PositiveOrZero
            Double kilometers,
            @NotNull
            ServiceType serviceType) {
    }

    public record BookingResponse(Double price) {
    }
}
