package ee.kemit.aks.service;

import ee.kemit.aks.dto.*;
import ee.kemit.aks.dto.inads.Address;
import ee.kemit.aks.dto.inads.AddressResponse;
import ee.kemit.aks.exception.AksException;
import ee.kemit.aks.model.Accommodation;
import ee.kemit.aks.model.County;
import ee.kemit.aks.model.Municipality;
import ee.kemit.aks.repository.AccommodationRepository;
import ee.kemit.aks.repository.AddressRepository;
import ee.kemit.aks.repository.CountyRepository;
import ee.kemit.aks.repository.MunicipalityRepository;
import ee.kemit.aks.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AddressRepository addressRepository;
    private final AdsService adsService;
    private final CountyRepository countyRepository;
    private final MunicipalityRepository municipalityRepository;

    public List<AccommodationDto> getAllAccommodations() {
        return ModelMapperUtil.mapAll(accommodationRepository.fetchAccommodationsWithActiveAddresses(),
                AccommodationDto.class);
    }

    public List<AddressDto> getAccommodationHistoricAddresses(Long accommodationId) {
        return ModelMapperUtil.mapAll(addressRepository.findAllByAccommodation_IdOrderByCreatedAtDesc(
                accommodationId), AddressDto.class);
    }

    @Transactional
    public AccommodationDto insertNewAccommodation(NewAccommodationDto newAccommodation) {
        ee.kemit.aks.model.Address address = getNewAddressObject(newAccommodation.getAdsOid());

        Accommodation accommodation = new Accommodation();
        accommodation.setName(newAccommodation.getName());
        accommodation.getAddresses().add(address);
        address.setAccommodation(accommodation);

        accommodation = accommodationRepository.save(accommodation);
        return ModelMapperUtil.map(accommodation, AccommodationDto.class);
    }

    @Transactional
    public AccommodationDto updateAccommodationAddress(Long id, NewAddressDto newAddressDto) {
        Optional<Accommodation> accommodation = accommodationRepository.findById(id);

        if (accommodation.isEmpty()) {
            log.error("Cannot find accommodation: {}", id);
            throw new AksException("Majutuskohta ei leitud");
        }

        Accommodation entity = accommodation.get();

        ee.kemit.aks.model.Address newAddress = getNewAddressObject(newAddressDto.getAdsOid());

        entity.getAddresses().forEach(a -> a.setActive(false));
        entity.getAddresses().add(newAddress);
        newAddress.setAccommodation(entity);

        accommodationRepository.save(entity);

        return ModelMapperUtil.map(entity, AccommodationDto.class);
    }

    @Transactional
    public List<AccommodationDto> getAccommodationsWithFilter(AccommodationFilter filter) {
        List<ee.kemit.aks.model.Address> filteredAddresses = addressRepository.findAddressesByFilter(filter.getSearchString()
                .toLowerCase(), filter.getCountyId(), filter.getMunicipalityId(), filter.getCreatedAt());

        List<Accommodation> relatedProperties = filteredAddresses.stream()
                .map(ee.kemit.aks.model.Address::getAccommodation)
                .toList();

        return ModelMapperUtil.mapAll(relatedProperties, AccommodationDto.class);
    }

    @Transactional
    public List<AccommodationDto> getAccommodationsWithSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return List.of();
        }
        List<ee.kemit.aks.model.Address> filteredAddresses = addressRepository.findAddressesBySearchString(searchTerm);

        List<Accommodation> relatedProperties = filteredAddresses.stream()
                .map(ee.kemit.aks.model.Address::getAccommodation)
                .toList();

        return ModelMapperUtil.mapAll(relatedProperties, AccommodationDto.class);
    }

    private ee.kemit.aks.model.Address getNewAddressObject(String adsOid) {
        AddressResponse response = adsService.getAddressDataByAdsOid(adsOid);

        if (response == null || response.getAddresses().size() < 1) {
            log.error("Address not found by ads-oid: {}", adsOid);
            throw new AksException("Ebakorrektne ADS-OID väärtus");
        }

        Address adsFoundAddress = response.getAddresses().get(0);

        Optional<County> county = countyRepository.findByNameEqualsIgnoreCase(adsFoundAddress.getMaakond());

        Optional<Municipality> municipality = municipalityRepository.findByNameContainingIgnoreCase(adsFoundAddress.getOmavalitsus());

        if (county.isEmpty() || municipality.isEmpty()) {
            log.error("Cannot find county/municipality to join: {}", adsOid);
            throw new AksException("Viga kov/maakonna sidumisel");
        }

        ee.kemit.aks.model.Address address = new ee.kemit.aks.model.Address();
        address.setAddress(adsFoundAddress.getTaisaadress());
        address.setActive(true);
        address.setAdsOid(adsFoundAddress.getAdsOid());
        address.setCounty(county.get());
        address.setMunicipality(municipality.get());
        address.setCentroidX(adsFoundAddress.getViitepunktX());
        address.setCentroidY(adsFoundAddress.getViitepunktY());

        return address;
    }
}