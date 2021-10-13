package com.rbkmoney.fraudbusters.config.dgraph;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateConfig {

    @Bean
    public VelocityEngine velocityEngine() {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("resource.loader", "class");
        String resourceLoader = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";
        engine.setProperty("class.resource.loader.class", resourceLoader);
        engine.init();
        return engine;
    }

}
