package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.damsel.fraudbusters.PaymentValidateServiceSrv;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/fraud_payment_validator/v1/")
@RequiredArgsConstructor
public class PaymentValidateTemplateServlet extends GenericServlet {

    private Servlet thriftServlet;

    private final PaymentValidateServiceSrv.Iface paymentTemplateValidatorHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(PaymentValidateServiceSrv.Iface.class, paymentTemplateValidatorHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
