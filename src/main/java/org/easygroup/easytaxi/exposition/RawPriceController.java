package org.easygroup.easytaxi.exposition;

import org.easygroup.easytaxi.services.RawPriceUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RawPriceController {

    private final RawPriceUseCase rawPriceUseCase;

    public RawPriceController(RawPriceUseCase rawPriceUseCase) {
        this.rawPriceUseCase = rawPriceUseCase;
    }

    @GetMapping("/price")
    public double calculatePrice(@RequestParam String origin, @RequestParam String destination) {
        return rawPriceUseCase.calculatePrice(origin, destination);
    }
}
