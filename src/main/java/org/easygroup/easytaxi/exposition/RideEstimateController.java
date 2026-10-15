package org.easygroup.easytaxi.exposition;

import jakarta.validation.Valid;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.services.RideEstimateUseCase;
import org.easygroup.easytaxi.services.RideEstimateUseCase.PriceEstimate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/rides", "/rides"})
public class RideEstimateController {

    private final RideEstimateUseCase rideEstimateUseCase;

    public RideEstimateController(RideEstimateUseCase rideEstimateUseCase) {
        this.rideEstimateUseCase = rideEstimateUseCase;
    }

    @PostMapping("/estimate")
    public PriceEstimate estimate(@Valid @RequestBody BookRideController.BookRideRequest request) {
        return rideEstimateUseCase.estimate(
                new Driver(request.driverAvailable()),
                new Rider(request.riderMoney(), request.riderBirthDate()),
                new Journey(request.departure(), request.arrival(), request.kilometers()),
                request.serviceType());
    }
}
