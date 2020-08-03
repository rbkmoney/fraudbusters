package com.rbkmoney.fraudbusters.resource.p2p;

import com.rbkmoney.damsel.fraudbusters.P2PServiceSrv;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/fraud_p2p/v1/")
@RequiredArgsConstructor
public class P2PServiceServlet extends GenericServlet {

    private Servlet thriftServlet;

    private final P2PServiceSrv.Iface p2pTemplateValidatorHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(P2PServiceSrv.Iface.class, p2pTemplateValidatorHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
