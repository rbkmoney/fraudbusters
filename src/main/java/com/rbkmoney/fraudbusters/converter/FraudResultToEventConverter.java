package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.domain.Metadata;
import com.rbkmoney.fraudo.model.PaymentModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudResultToEventConverter implements Converter<FraudResult, Event> {

    private final GeoIpServiceSrv.Iface geoIpService;

    @Override
    public Event convert(FraudResult fraudResult) {
        Event event = new Event();
        PaymentModel paymentModel = fraudResult.getFraudRequest().getPaymentModel();
        event.setAmount(paymentModel.getAmount());
        event.setBin(paymentModel.getBin());
        event.setEmail(paymentModel.getEmail());
        event.setEventTime(getEventTime(fraudResult));
        event.setTimestamp(java.sql.Date.valueOf(LocalDateTime.now().toLocalDate()));
        event.setFingerprint(paymentModel.getFingerprint());
        String ip = paymentModel.getIp();
        String country = getCountryCode(ip);
        event.setCountry(country);
        event.setIp(ip);
        event.setPartyId(paymentModel.getPartyId());
        CheckedResultModel resultModel = fraudResult.getResultModel();
        event.setCheckedTemplate(resultModel.getCheckedTemplate());
        Optional.ofNullable(resultModel.getResultModel())
                .ifPresent(result -> {
                    event.setCheckedRule(result.getRuleChecked());
                    event.setResultStatus(result.getResultStatus().name());
                });
        event.setShopId(paymentModel.getShopId());
        event.setBankCountry(paymentModel.getBinCountryCode());
        event.setCardToken(paymentModel.getCardToken());
        Metadata metadata = fraudResult.getFraudRequest().getMetadata();
        if (metadata != null) {
            event.setBankName(metadata.getBankName());
            event.setCurrency(metadata.getCurrency());
            event.setInvoiceId(metadata.getInvoiceId());
            event.setMaskedPan(metadata.getMaskedPan());
            event.setPaymentId(metadata.getPaymentId());
        }
        return event;
    }

    private String getCountryCode(String ip) {
        String country = null;
        try {
            country = geoIpService.getLocationIsoCode(ip);
        } catch (TException e) {
            log.error("Error when getCountryCode e: ", e);
        }
        return country != null ? country : ClickhouseUtilsValue.UNKNOWN;
    }

    @NotNull
    private Long getEventTime(FraudResult fraudResult) {
        if (fraudResult.getFraudRequest().getMetadata() != null) {
            Long timestamp = fraudResult.getFraudRequest().getMetadata().getTimestamp();
            return timestamp != null ? timestamp : generateNow();
        }
        return generateNow();
    }

    private long generateNow() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    public List<Event> convertBatch(List<FraudResult> fraudResults) {
        return fraudResults.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
}
