package com.naskoni.usermanager.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    Optional<HttpMessageConverter<?>> jsonConverterOptional =
        converters.stream()
            .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
            .findFirst();

    if (jsonConverterOptional.isPresent()) {
      MappingJackson2HttpMessageConverter converter =
          (MappingJackson2HttpMessageConverter) jsonConverterOptional.get();
      converter.getObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
      converter.getObjectMapper().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      converter.getObjectMapper().setTimeZone(TimeZone.getDefault());
    }
  }

  @Bean
  public ShallowEtagHeaderFilter etagFilter() {
    return new ShallowEtagHeaderFilter();
  }
}
