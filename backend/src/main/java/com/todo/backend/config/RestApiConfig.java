package com.todo.backend.config;

import com.todo.backend.entity.Book;
import com.todo.backend.entity.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestApiConfig implements RepositoryRestConfigurer {
    @Value("${server.port}")
    private String port;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry registry) {
        HttpMethod[] unsupportedActions = {
            HttpMethod.PUT,
            HttpMethod.POST,
            HttpMethod.DELETE,
            HttpMethod.PATCH
        };

        config.exposeIdsFor(Book.class);
        config.exposeIdsFor(Review.class);

        disableHttpMethods(config, unsupportedActions);

        /* Configure CORS Mapping */
        var allowedOrigins = "http://localhost:" + port;
        registry
            .addMapping(config.getBasePath() + "/**")
            .allowedOrigins(allowedOrigins);
    }

    private void disableHttpMethods(
        RepositoryRestConfiguration config,
        HttpMethod[] theUnsupportedActions,
        Class<?>... domainTypes
    ) {
        for (Class<?> domainType : domainTypes) {
            config.getExposureConfiguration()
                    .forDomainType(domainType)
                    .withItemExposure(((metadata, httpMethods) ->
                            httpMethods.disable(theUnsupportedActions)))
                    .withCollectionExposure(((metadata, httpMethods) ->
                            httpMethods.disable(theUnsupportedActions)));
        }
    }
}
