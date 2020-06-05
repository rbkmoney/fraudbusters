package com.rbkmoney.fraudbusters.client;

import com.rbkmoney.fraudbusters.client.model.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudManagementClientImpl implements FraudManagementClient {

    private final RestTemplate restTemplate;

    @Value("${fraud.management.url}")
    private String url;

    public static final String TEMPLATE_DEFAULT_PATH = "/template/default";
    public static final String REFERENCE_PATH = "/reference";


    @SneakyThrows
    @Override
    public String createDefaultReference(String partyId, String shopId) {
        List<PaymentReferenceModel> request = buildRequest(partyId, shopId);
        String[] response = restTemplate.postForObject(url + TEMPLATE_DEFAULT_PATH, request, String[].class);
        if (response == null || response.length != 1) {
            throw new UnknownReferenceException();
        }
        return response[0];
    }

    @SneakyThrows
    @Override
    public boolean isExistReference(String partyId, String shopId) {
        URI uri = builGetURI(partyId, shopId);
        PaymentReferenceModel[] response = restTemplate.getForObject(uri, PaymentReferenceModel[].class);
        return response != null && response.length > 0;
    }

    private URI builGetURI(String partyId, String shopId) {
        return UriComponentsBuilder.fromHttpUrl(url + REFERENCE_PATH)
                .queryParam("partyId", partyId)
                .queryParam("shopId", shopId)
                .queryParam("isGlobal", "false")
                .queryParam("isDefault", "false")
                .build().toUri();
    }

    private List<PaymentReferenceModel> buildRequest(String partyId, String shopId) {
        var referenceModel = PaymentReferenceModel.builder()
                .partyId(partyId)
                .shopId(shopId)
                .isGlobal(false)
                .build();
        return List.of(referenceModel);
    }
}
