package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.domain.Metadata;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudResultToEventConverter implements BatchConverter<FraudResult, Event> {

    private final GeoIpServiceSrv.Iface geoIpService;

    @Override
    public Event convert(FraudResult fraudResult) {
        Event event = new Event();
        PaymentModel paymentModel = fraudResult.getFraudRequest().getFraudModel();
        event.setAmount(paymentModel.getAmount());
        event.setBin(paymentModel.getBin());
        event.setEmail(paymentModel.getEmail());

        Long timestamp = getEventTime(fraudResult);
        event.setEventTime(timestamp);
        event.setTimestamp(LocalDateTime.now().toLocalDate());
        long eventTimeHour = Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.HOURS).toEpochMilli();
        event.setEventTimeHour(eventTimeHour);

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

}
