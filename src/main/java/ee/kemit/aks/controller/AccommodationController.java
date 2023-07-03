package ee.kemit.aks.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import ee.kemit.aks.dto.AccommodationDto;
import ee.kemit.aks.dto.AccommodationFilter;
import ee.kemit.aks.dto.AddressDto;
import ee.kemit.aks.dto.NewAccommodationDto;
import ee.kemit.aks.dto.NewAddressDto;
import ee.kemit.aks.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("accommodation")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    @GetMapping("/")
    public List<AccommodationDto> getAllAccommodations() {
        return accommodationService.getAllAccommodations();
    }

    @GetMapping("/search")
    public List<AccommodationDto> getAccommodationsBySearchString(@RequestParam String searchString) {
        return accommodationService.getAccommodationsWithSearch(searchString);
    }

    @PostMapping("/add-new")
    public AccommodationDto insertNewAccommodation(@Valid @RequestBody NewAccommodationDto accommodation) {
        return accommodationService.insertNewAccommodation(accommodation);
    }

    @GetMapping("/address-history/{accommodationId}")
    public List<AddressDto> getAccommodationHistoricAddresses(@PathVariable Long accommodationId) {
        return accommodationService.getAccommodationHistoricAddresses(accommodationId);
    }

    @PatchMapping("update-address/{accommodationId}")
    public AccommodationDto updateAccommodationAddress(@PathVariable Long accommodationId, @Valid @RequestBody NewAddressDto newAddress) {
        return accommodationService.updateAccommodationAddress(accommodationId, newAddress);
    }

    @PostMapping("/filter")
    public List<AccommodationDto> getAccommodationsWithFilter(@RequestBody AccommodationFilter accommodationFilter) {
        return accommodationService.getAccommodationsWithFilter(accommodationFilter);
    }
}
