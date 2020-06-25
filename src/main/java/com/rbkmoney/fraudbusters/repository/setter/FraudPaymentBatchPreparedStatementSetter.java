package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.damsel.domain.ClientInfo;
import com.rbkmoney.damsel.domain.ContactInfo;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class FraudPaymentBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private final List<FraudPayment> payments;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        FraudPayment payment = payments.get(i);
        PaymentTool paymentTool = extractPaymentTool(payment);
        ClientInfo clientInfo = extractClientInfo(payment);
        ContactInfo contactInfo = extractContactInfo(payment);
        int l = 1;
        ps.setString(l++, payment.getId());
        ps.setString(l++, payment.getLastChangeTime());
        ps.setString(l++, payment.getPartyId());
        ps.setString(l++, payment.getShopId());
        ps.setLong(l++, payment.getCost().getAmount());
        ps.setString(l++, payment.getCost().getCurrency().getSymbolicCode());
        ps.setString(l++, payment.getPayer().getSetField().getFieldName());
        ps.setString(l++, paymentTool.getSetField().getFieldName());
        ps.setString(l++, paymentTool.getBankCard().getToken());
        ps.setString(l++, paymentTool.getBankCard().getPaymentSystem().name());
        ps.setString(l++, paymentTool.getBankCard().getBin() + paymentTool.getBankCard().getLastDigits());
        ps.setString(l++, paymentTool.getBankCard().getIssuerCountry().name());
        ps.setString(l++, contactInfo != null ? contactInfo.getEmail() : null);
        ps.setString(l++, clientInfo != null ? clientInfo.getIpAddress() : null);
        ps.setString(l++, clientInfo != null ? clientInfo.getFingerprint() : null);
        ps.setString(l++, payment.getStatus().name());
        ps.setString(l++, payment.getRrn());
        ps.setLong(l++, payment.getRoute().getProvider().getId());
        ps.setLong(l++, payment.getRoute().getTerminal().getId());
        ps.setString(l++, payment.getFraudInfo().getTempalteId());
        ps.setString(l, payment.getFraudInfo().getDescription());
    }

    private ContactInfo extractContactInfo(FraudPayment payment) {
        if (payment.getPayer().isSetPaymentResource()) {
            return payment.getPayer().getPaymentResource().getContactInfo();
        } else if (payment.getPayer().isSetRecurrent()) {
            return payment.getPayer().getRecurrent().getContactInfo();
        }
        throw new IllegalStateException("ContactInfo must be set for " + payment.getId());
    }

    private ClientInfo extractClientInfo(FraudPayment payment) {
        if (payment.getPayer().isSetPaymentResource()) {
            return payment.getPayer().getPaymentResource().getResource().getClientInfo();
        }
        throw new IllegalStateException("ClientInfo must be set for " + payment.getId());
    }

    private PaymentTool extractPaymentTool(FraudPayment payment) {
        if (payment.getPayer().isSetPaymentResource()) {
            return payment.getPayer().getPaymentResource().getResource().getPaymentTool();
        } else if (payment.getPayer().isSetRecurrent()) {
            return payment.getPayer().getRecurrent().getPaymentTool();
        }
        throw new IllegalStateException("PaymentTool must be set for " + payment.getId());
    }

    @Override
    public int getBatchSize() {
        return payments.size();
    }
}
