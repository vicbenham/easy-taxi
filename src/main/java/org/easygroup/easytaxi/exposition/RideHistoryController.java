package org.easygroup.easytaxi.exposition;

import org.easygroup.easytaxi.services.RideHistoryUseCase;
import org.easygroup.easytaxi.services.RideHistoryUseCase.RideHistoryEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/riders")
public class RideHistoryController {

    private final RideHistoryUseCase rideHistoryUseCase;

    public RideHistoryController(RideHistoryUseCase rideHistoryUseCase) {
        this.rideHistoryUseCase = rideHistoryUseCase;
    }

    @GetMapping("/{riderId}/history")
    public List<RideHistoryEntry> getRideHistory(@PathVariable Long riderId) {
        return rideHistoryUseCase.getHistoryForRider(riderId);
    }
}
