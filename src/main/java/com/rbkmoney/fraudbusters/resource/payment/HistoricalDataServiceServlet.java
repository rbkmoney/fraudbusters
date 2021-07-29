package com.rbkmoney.fraudbusters.resource.payment;

import com.rbkmoney.damsel.fraudbusters.HistoricalDataServiceSrv;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

import java.io.IOException;

@WebServlet("/historical_data/v1/")
@RequiredArgsConstructor
public class HistoricalDataServiceServlet extends GenericServlet {

    private final HistoricalDataServiceSrv.Iface paymentHandler;
    private Servlet thriftServlet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(HistoricalDataServiceSrv.Iface.class, paymentHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
