package com.rbkmoney.fraudbusters.resource.p2p;

import com.rbkmoney.damsel.p2p_insp.InspectorProxySrv;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;

import java.io.IOException;

@WebServlet("/fraud_p2p_inspector/v1")
@RequiredArgsConstructor
public class FraudP2PInspectorServlet extends GenericServlet {

    private final InspectorProxySrv.Iface fraudP2pInspectorHandler;
    private Servlet thriftServlet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(InspectorProxySrv.Iface.class, fraudP2pInspectorHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
