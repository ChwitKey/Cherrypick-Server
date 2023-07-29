package com.example.cherrypickserver.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI openAPI(@Value("${springdoc.version}") String version) {
    Info info = new Info()
            .title("Cherrypick API Document")
            .version(version)
            .description("cherrypick 프로젝트의 API 명세서입니다.");
    return new OpenAPI()
            .components(new Components())
            .info(info);
  }
}