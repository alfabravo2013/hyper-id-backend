package com.github.alfabravo2013.hyperidbackend.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        var maybeConv = converters
                .stream()
                .filter(c -> c instanceof AbstractJackson2HttpMessageConverter)
                .findFirst();
        if (maybeConv.isPresent()) {
            var converter = (AbstractJackson2HttpMessageConverter) maybeConv.get();
            converter.getObjectMapper().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }
    }
}
