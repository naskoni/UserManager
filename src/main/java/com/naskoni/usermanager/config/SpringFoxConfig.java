package com.naskoni.usermanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Swagger UI: when deployed on external server: {host}:{port}/usermanager/swagger-ui.html; when
 * started on embedded Tomcat: {host}:{port}/swagger-ui.html
 */
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SpringFoxConfig implements WebMvcConfigurer {

  @Value("${api.version}")
  private String apiVersion;

  @Bean
  public Docket api() {
    List<SecurityScheme> schemes = new ArrayList<>();
    schemes.add(new BasicAuth("basicAuth"));

    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.naskoni.usermanager"))
        .paths(PathSelectors.any())
        .build()
        .directModelSubstitute(Date.class, String.class)
        .genericModelSubstitutes(ResponseEntity.class)
        .apiInfo(apiInfo());
  }

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
    registry
        .addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("User Manager Rest API")
        .description("User Manager API Documentation with Swagger")
        .termsOfServiceUrl("Terms of service")
        .version(apiVersion)
        .build();
  }
}
