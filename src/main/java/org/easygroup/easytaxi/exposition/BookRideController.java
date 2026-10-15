package org.easygroup.easytaxi.exposition;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.domain.ServiceType;
import org.easygroup.easytaxi.services.BookRideUseCase;
import org.easygroup.easytaxi.services.RideEstimateUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
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
    private final RideEstimateUseCase rideEstimateUseCase;

    public BookRideController(BookRideUseCase bookRideUseCase, RideEstimateUseCase rideEstimateUseCase) {
        this.bookRideUseCase = bookRideUseCase;
        this.rideEstimateUseCase = rideEstimateUseCase;
    }

    @PostMapping("/book")
    public BookingResponse bookRide(@Valid @RequestBody BookRideRequest request) {
        if (request.token() != null) {
            Booking booking = rideEstimateUseCase.book(request.token());
            return new BookingResponse(booking.getPrice());
        }
        Booking booking = bookRideUseCase.bookRide(
                new Driver(request.driverAvailable()),
                new Rider(request.riderMoney(), request.riderBirthDate()),
                new Journey(request.departure(), request.arrival(), request.kilometers()),
                request.serviceType());
        return new BookingResponse(booking.getPrice());
    }

    public record BookRideRequest(
            @PositiveOrZero
            Double riderMoney,
            LocalDate riderBirthDate,
            Boolean driverAvailable,
            String departure,
            String arrival,
            @PositiveOrZero
            Double kilometers,
            ServiceType serviceType,
            String token) {

        @AssertTrue(message = "Provide either a token or complete ride details")
        public boolean isRequestValid() {
            if (token != null) {
                return !token.isBlank() &&
                        riderMoney == null &&
                        riderBirthDate == null &&
                        driverAvailable == null &&
                        departure == null &&
                        arrival == null &&
                        kilometers == null &&
                        serviceType == null;
            }
            return riderMoney != null &&
                    riderBirthDate != null &&
                    driverAvailable != null &&
                    departure != null && !departure.isBlank() &&
                    arrival != null && !arrival.isBlank() &&
                    kilometers != null &&
                    serviceType != null;
        }
    }

    public record BookingResponse(Double price) {
    }
}
