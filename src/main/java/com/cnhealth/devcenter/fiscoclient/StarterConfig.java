package com.cnhealth.devcenter.fiscoclient;

import org.fisco.bcos.sdk.BcosSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BcosProperties.class)
@ComponentScan(basePackages = {"com.cnhealth.devcenter.fiscoclient"})
@ConditionalOnProperty(prefix = "bcos", value = "hasInstance", matchIfMissing = false)
public class StarterConfig {
    @Autowired
    FiscoBcos fiscoBcos;

    @Bean
    public BcosSDK bcosSDK() {
        return fiscoBcos.getBcosSDK();
    }

}
