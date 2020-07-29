package com.rbkmoney.fraudbusters.resource.payment;

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

    private final PaymentServiceSrv.Iface paymentServiceHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(PaymentServiceSrv.Iface.class, paymentServiceHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
