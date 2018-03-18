package com.demidovn.fruitbounty.server.config;

import com.demidovn.fruitbounty.server.converters.FruitServerConverter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class ConversionServiceConfig {

  @Autowired
  private List<FruitServerConverter> fruitServerConverters;

  @Bean
  public ConversionService serverConversionService() {
    DefaultConversionService service = new DefaultConversionService();

    fruitServerConverters.forEach(service::addConverter);

    return service;
  }

}
