package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/fraud_payment/v1/")
@RequiredArgsConstructor
public class PaymentServiceServlet extends GenericServlet {

    private Servlet thriftServlet;

    private final PaymentServiceSrv.Iface paymentTemplateValidatorHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(PaymentServiceSrv.Iface.class, paymentTemplateValidatorHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
