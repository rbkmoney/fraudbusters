package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.woody.api.flow.WFlow;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class TomcatEmbeddedConfiguration {

    private static final String HEALTH = "/actuator/health";

    @Value("${server.rest.port}")
    private int restPort;

    @Value("/${server.rest.endpoint}/")
    private String restEndpoint;

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        Connector connector = new Connector();
        connector.setPort(restPort);
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }

    @Bean
    public FilterRegistrationBean externalPortRestrictingFilter() {
        Filter filter = new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                String servletPath = request.getServletPath();
                if ((request.getLocalPort() == restPort)
                        && !(servletPath.startsWith(restEndpoint) || servletPath.startsWith(HEALTH))) {
                    response.sendError(404, "Unknown address");
                    return;
                }
                filterChain.doFilter(request, response);
            }
        };

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(-100);
        filterRegistrationBean.setName("httpPortFilter");
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean woodyFilter() {
        WFlow wFlow = new WFlow();
        Filter filter = new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                if ((request.getLocalPort() == restPort)
                        && request.getServletPath().startsWith(restEndpoint)) {
                    wFlow.createServiceFork(() -> {
                        try {
                            filterChain.doFilter(request, response);
                        } catch (IOException | ServletException e) {
                            sneakyThrow(e);
                        }
                    }).run();
                    return;
                }
                filterChain.doFilter(request, response);
            }

            private <E extends Throwable, T> T sneakyThrow(Throwable t) throws E {
                throw (E) t;
            }
        };

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(-50);
        filterRegistrationBean.setName("woodyFilter");
        filterRegistrationBean.addUrlPatterns(restEndpoint + "*");
        return filterRegistrationBean;
    }
}
