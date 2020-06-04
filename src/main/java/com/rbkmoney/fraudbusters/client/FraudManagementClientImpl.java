package com.rbkmoney.fraudbusters.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.client.model.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.exception.RemoteClientException;
import com.rbkmoney.fraudbusters.exception.UnknownReferenceException;
import lombok.SneakyThrows;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class FraudManagementClientImpl implements FraudManagementClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CloseableHttpClient client = HttpClients.createDefault();

    @Value("${fraud.management.url}")
    private String url;

    public static final String TEMPLATE_DEFAULT_PATH = "/template/default";
    public static final String REFERENCE_PATH = "/reference";


    @SneakyThrows
    @Override
    public String createDefaultReference(String partyId, String shopId) {
        String requestJson = buildRequestJson(partyId, shopId);
        HttpPost httpPost = buildHttpPost(requestJson);
        CloseableHttpResponse httpResponse = client.execute(httpPost);
        if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String[] ids = objectMapper.readValue(httpResponse.getEntity().getContent(), String[].class);
            if (ids == null || ids.length != 1) {
                throw new UnknownReferenceException();
            }
            return ids[0];
        } else {
            throw new RemoteClientException("With status code: " + httpResponse.getStatusLine().getStatusCode());
        }
    }

    @SneakyThrows
    @Override
    public List<PaymentReferenceModel> getReferences(String partyId, String shopId) {
        HttpGet httpGet = buildHttpGet(partyId, shopId);
        CloseableHttpResponse httpResponse = client.execute(httpGet);
        if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            var paymentReferenceModels = objectMapper.readValue(httpResponse.getEntity().getContent(), PaymentReferenceModel[].class);
            return List.of(paymentReferenceModels);
        } else {
            throw new RemoteClientException("With status code: " + httpResponse.getStatusLine().getStatusCode());
        }
    }

    private HttpGet buildHttpGet(String partyId, String shopId) throws URISyntaxException {
        URI uri = new URIBuilder(url + REFERENCE_PATH)
                .setParameter("partyId", partyId)
                .setParameter("shopId", shopId)
                .setParameter("isGlobal", "false")
                .setParameter("isDefault", "false")
                .build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
        return httpGet;
    }

    private HttpPost buildHttpPost(String json) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(json);
        HttpPost httpPost = new HttpPost(url + TEMPLATE_DEFAULT_PATH);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
        httpPost.setHeader("Content-type", MediaType.APPLICATION_JSON_VALUE);
        return httpPost;
    }

    private String buildRequestJson(String partyId, String shopId) throws JsonProcessingException {
        var referenceModel = PaymentReferenceModel.builder()
                .partyId(partyId)
                .shopId(shopId)
                .isGlobal(false)
                .build();
        return objectMapper.writeValueAsString(List.of(referenceModel));
    }
}
