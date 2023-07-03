package ee.kemit.aks.service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ee.kemit.aks.dto.inads.AddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdsService {

    private final WebClient genericWebClient;

    @Value("${ads.url}")
    private String adsUrl;

    public AddressResponse getAddressDataByAdsOid(String adsOid) {
        try {
            return genericWebClient.get()
                    .uri(new URI(adsUrl.replace("{param}", adsOid)))
                    .retrieve()
                    .bodyToMono(AddressResponse.class).block();
        } catch (Exception e) {
            log.error("Caught exception querying in-ADS", e);
        }
        return null;
    }
}