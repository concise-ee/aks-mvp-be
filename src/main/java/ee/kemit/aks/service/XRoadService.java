package ee.kemit.aks.service;

import ee.kemit.aks.model.Accommodation;
import ee.kemit.aks.model.Address;
import ee.kemit.aks.model.County;
import ee.kemit.aks.model.Municipality;
import ee.kemit.aks.repository.AccommodationRepository;
import ee.kemit.aks.repository.AddressRepository;
import ee.kemit.aks.repository.CountyRepository;
import ee.kemit.aks.repository.MunicipalityRepository;
import ee.kemit.aks.xroad.*;
import ee.xroad.ads.ADSobjmuudatusedResponse;
import ee.xroad.ads.ObjSyndmusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Log4j2
public class XRoadService {

    private final AccommodationRepository accommodationRepository;
    private final MunicipalityRepository municipalityRepository;
    private final CountyRepository countyRepository;
    private final AddressRepository addressRepository;
    private final Environment environment;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Transactional
    public void checkUpdatedAddresses() {
        try {

            AdsObjektRequestDto adsObjektRequestDto = new AdsObjektRequestDto();
            adsObjektRequestDto.setChangesFrom(LocalDate.now().minusDays(1));

            ServiceRequest<AdsObjektRequestDto> request = new AdsXTeeClientService(environment).createServiceRequest();
            request.setRequestData(adsObjektRequestDto);

            ClientXRoad6 clientXRoad6 = new ClientXRoad6(environment);

            ServiceResponse<AdsObjektRequestDto, ADSobjmuudatusedResponse> response;

            Future<ServiceResponse> responseFuture = clientXRoad6.send(request,
                    new AdsRequestSerializer(),
                    new AdsResponseDeserializer());

            while (!(responseFuture.isDone())) {
                Thread.sleep(ClientXRoad6.SERVICE_RESPONSE_TIMEOUT);
            }

            response = responseFuture.get();
            checkChangedAddresses(response.getResponseData());
        } catch (Exception e) {
            log.error("Caught exception getting ADS change log", e);
        }
    }

    private void checkChangedAddresses(ADSobjmuudatusedResponse response) {
        List<String> activeAdsOids = addressRepository.findAllActiveAdsOidAddresses();

        List<String> adsDeletedAdsOids = new ArrayList<>();

        for (ADSobjmuudatusedResponse.ObjmuudatusedTulem.Muudatus muudatus : response.getObjmuudatusedTulem()
                .getMuudatus()) {

            boolean isSuccessor = adsDeletedAdsOids.stream()
                    .anyMatch(s -> muudatus.getEellased() != null && muudatus.getEellased().contains(s));

            if (!activeAdsOids.contains(muudatus.getAdsOid()) && !isSuccessor) {
                continue;
            }

            log.info("Found update for ADS_OID that is connected to a property in this system: {}",
                    muudatus.getAdsOid());
            log.info("Update is of type: {}", muudatus.getSyndmus().value());
            log.info("Update change vector is: {}", muudatus.getMuutvektor());

            if (muudatus.getSyndmus().equals(ObjSyndmusType.D) && muudatus.getJarglased() != null) {
                log.info("Found deletion operation for ads_oid: {}", muudatus.getAdsOid());
                adsDeletedAdsOids.add(muudatus.getAdsOid());
                continue;
            }

            String successorForPredecessor = null;

            if (isSuccessor) {
                for (String predecessor : List.of(muudatus.getEellased().split(";"))) {
                    if (adsDeletedAdsOids.contains(predecessor)) {
                        successorForPredecessor = predecessor;
                        log.info("Found successor: {} for predecessor: {}",
                                muudatus.getAdsOid(),
                                muudatus.getEellased());
                    }
                }
            }

            boolean isExistingAdsOidAddressChange =
                    muudatus.getSyndmus().equals(ObjSyndmusType.U) && muudatus.getMuutvektor() != null
                            && muudatus.getMuutvektor().charAt(2) == '1';

            if (isExistingAdsOidAddressChange || (isSuccessor && successorForPredecessor != null)) {

                log.info("Found update of ads_oid where address changes: {}", muudatus.getLogId());

                Optional<Address> persistedAddressEntity = addressRepository.findByAdsOidAndActiveTrue(isSuccessor ?
                        successorForPredecessor :
                        muudatus.getAdsOid());

                if (persistedAddressEntity.isPresent() && muudatus.getAadressid().getAadress().size() > 0) {

                    ADSobjmuudatusedResponse.ObjmuudatusedTulem.Muudatus.Aadressid.Aadress newAddressComponents = muudatus.getAadressid()
                            .getAadress()
                            .get(0);

                    String countyName = newAddressComponents.getAdsTase1().getNimetus();
                    String municipalityName = newAddressComponents.getAdsTase2().getNimetus();

                    Optional<County> county = countyRepository.findByNameEqualsIgnoreCase(countyName);
                    Optional<Municipality> municipality = municipalityRepository.findByNameContainingIgnoreCase(
                            municipalityName);

                    if (county.isEmpty() || municipality.isEmpty()) {
                        log.error("Unable to connect address with components");
                        continue;
                    }

                    Address currentlyActiveEntity = persistedAddressEntity.get();

                    Accommodation accommodation = currentlyActiveEntity.getAccommodation();

                    Address newAddress = new Address();
                    newAddress.setAddress(muudatus.getTaisAadress());
                    newAddress.setActive(true);
                    newAddress.setAdsOid(muudatus.getAdsOid());
                    newAddress.setCounty(county.get());
                    newAddress.setMunicipality(municipality.get());

                    // IN-ADS returns mathematical coordinates which we keep in database, ADS X-road services return geographical coordinates
                    newAddress.setCentroidX(String.valueOf(muudatus.getTsentroidY()));
                    newAddress.setCentroidY(String.valueOf(muudatus.getTsentroidX()));

                    accommodation.getAddresses().forEach(a -> a.setActive(false));
                    accommodation.getAddresses().add(newAddress);
                    newAddress.setAccommodation(accommodation);

                    log.info("Persisting new address for accommodation: {}", accommodation.getId());
                    accommodationRepository.save(accommodation);
                }
            }

            if (isSuccessor && successorForPredecessor != null) {
                final String value = successorForPredecessor;
                log.info("Removing predecessor from list as successor already found");
                adsDeletedAdsOids.removeIf(a -> a.equals(value));
            }
        }
    }
}
