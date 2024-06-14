/*
 * Copyright (C) 2018 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.fussballverein;

import com.acme.fussballverein.repository.TrainerRestRepository;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

/**
 * Beans für die REST-Schnittstelle zu "trainer" (WebClient) und für die GraphQL-Schnittstelle zu "trainer"
 * (HttpGraphQlClient).
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
interface HttpClientConfig {
    String GRAPHQL_PATH = "/graphql";
    int TRAINER_DEFAULT_PORT = 8080;
    int TIMEOUT_IN_SECONDS = 10;

    @Bean
    default WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    @SuppressWarnings("CallToSystemGetenv")
    default UriComponentsBuilder uriComponentsBuilder() {
        // Umgebungsvariable in Kubernetes
        final var trainerHostEnv = System.getenv("TRAINER_SERVICE_HOST");
        final var trainerPortEnv = System.getenv("TRAINER_SERVICE_PORT");

        @SuppressWarnings("VariableNotUsedInsideIf")
        final var schema = trainerHostEnv == null ? "https" : "http";
        final var trainerHost = trainerHostEnv == null ? "localhost" : trainerHostEnv;
        final int trainerPort = trainerPortEnv == null ? TRAINER_DEFAULT_PORT : Integer.parseInt(trainerPortEnv);

        final var log = LoggerFactory.getLogger(HttpClientConfig.class);
        log.error("trainerHost: {}, trainerPort: {}", trainerHost, trainerPort);

        return UriComponentsBuilder.newInstance()
            .scheme(schema)
            .host(trainerHost)
            .port(trainerPort);
    }

    // siehe org.springframework.web.reactive.function.client.DefaultWebClient
    @Bean
    default WebClient webClient(
        final WebClient.Builder webClientBuilder,
        final UriComponentsBuilder uriComponentsBuilder,
        final WebClientSsl ssl
    ) {
        final var baseUrl = uriComponentsBuilder.build().toUriString();
        return webClientBuilder
            .baseUrl(baseUrl)
            // siehe Property spring.ssl.bundle.jks.microservice
            // https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.ssl
            // https://spring.io/blog/2023/06/07/securing-spring-boot-applications-with-ssl
            .apply(ssl.fromBundle("microservice"))
            .build();
    }

    @Bean
    default TrainerRestRepository trainerRestRepository(final WebClient builder) {
        final var clientAdapter = WebClientAdapter.forClient(builder);
        final var proxyFactory = HttpServiceProxyFactory
            .builder(clientAdapter)
            .blockTimeout(Duration.ofSeconds(TIMEOUT_IN_SECONDS))
            .build();
        return proxyFactory.createClient(TrainerRestRepository.class);
    }

    // siehe org.springframework.graphql.client.DefaultHttpGraphQlClientBuilder.DefaultHttpGraphQlClient
    @Bean
    default HttpGraphQlClient graphQlClient(
        final WebClient.Builder webClientBuilder,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        final var baseUrl = uriComponentsBuilder
            .path(GRAPHQL_PATH)
            .build()
            .toUriString();
        final var webclient = webClientBuilder
            .baseUrl(baseUrl)
            .build();
        return HttpGraphQlClient.builder(webclient).build();
    }
}
