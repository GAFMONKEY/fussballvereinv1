package com.acme.fussballverein.rest;

import com.acme.fussballverein.entity.Fussballverein;
import com.acme.fussballverein.service.FussballvereinReadService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static com.acme.fussballverein.rest.FussballvereinGetController.REST_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

/**
 * GetController für Fussballvereine.
 * <img src="..\..\..\..\..\asciidoc\FussballvereinGetController.svg" alt="Klassendiagramm">
 */
@SuppressWarnings({"unused", "LineLength"})
@Controller
@RequestMapping(REST_PATH)
@ResponseBody
@Slf4j
@OpenAPIDefinition(info = @Info(title = "Fussballverein API", version = "v1"))
@RequiredArgsConstructor
public class FussballvereinGetController {
    /**
     * Konstante für Rest-Path.
     */
    static final String REST_PATH = "/rest";

    /**
     * Pfad, um Nachnamen abzufragen.
     */
    private static final String NAME_PATH = "/name";

    private final UriHelper uriHelper;

    private final FussballvereinReadService service;

    /**
     * Regulärer Ausdruck für IDs.
     */
    @SuppressWarnings("DeclarationOrder")
    static final String ID_PATTERN =
        "[\\dA-Fa-f]{8}-[\\dA-Fa-f]{4}-[\\dA-Fa-f]{4}-[\\dA-Fa-f]{4}-[\\dA-Fa-f]{12}";

    /**
     * Gibt Fussballverein zu gegebener ID zurück.
     *
     * @param id UUID des Fussballvereins
     * @param request Das Request-Objekt, um Links für HATEOAS zu erstellen.
     * @return Ein Fussballverein-Objekt
     */
    @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
    @Operation(summary = "Suche mit der Fussballverein-ID.", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "Kunde gefunden.")
    @ApiResponse(responseCode = "404", description = "Kunde nicht gefunden")
    FussballvereinModel getById(@PathVariable final UUID id,
                                 final HttpServletRequest request) {
        final var fussballverein = service.findByID(id);
        final var model = new FussballvereinModel(fussballverein);
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var idUri = baseUri + "/" + fussballverein.getId();
        final var selfLink = Link.of(idUri);
        final var listLink = Link.of(baseUri, LinkRelation.of("list"));
        final var addLink = Link.of(baseUri, LinkRelation.of("add"));
        final var updateLink = Link.of(idUri, LinkRelation.of("update"));
        final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
        model.add(selfLink, listLink, addLink, updateLink, removeLink);
        return model;
    }

    /**
     * Suche mit diversen Suchkriterien als Query-Parameter.
     *
     * @param queryParams Query-Parameter als Map.
     * @param request Das Request-Objekt, um Links für HATEOAS zu erstellen.
     * @return Gefundenen Kunden als CollectionModel.
     */
    @GetMapping(produces = HAL_JSON_VALUE)
    @Operation(summary = "Suche mit Suchkriterien", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "Liste mit Fussballvereinen")
    @ApiResponse(responseCode = "404", description = "Keine Fussballvereine gefunden")
    ResponseEntity<CollectionModel<? extends FussballvereinModel>> get(
        @RequestParam final Map<String, String> queryParams,
        final HttpServletRequest request
    ) {
        log.debug("get: queryParams={}", queryParams);
        if (queryParams.size() > 1) {
            return notFound().build();
        }

        final Collection<Fussballverein> fussballvereine;
        if (queryParams.isEmpty()) {
            fussballvereine = service.findAll();
        } else {
            final var trainerIdStr = queryParams.get("trainerId");
            if (trainerIdStr == null) {
                return notFound().build();
            }
            final var trainerId = UUID.fromString(trainerIdStr);
            fussballvereine = service.findByTrainerId(trainerId);
        }

        final var baseUri = uriHelper.getBaseUri(request).toString();
        @SuppressWarnings("LambdaBodyLength")
        final var models = fussballvereine
            .stream()
            .map(fussballverein -> {
                final var model = new FussballvereinModel(fussballverein);
                model.add(Link.of(baseUri + '/' + fussballverein.getId()));
                return model;
            })
            .toList();
        log.trace("get: {}", models);
        return ok(CollectionModel.of(models));
    }

    /**
     * Abfrage, welche Nachnamen es zu einem Präfix gibt.
     *
     * @param prefix Nachname-Präfix als Pfadvariable.
     * @return Die passenden Nachnamen oder Statuscode 404, falls es keine gibt.
     */
    @GetMapping(path = NAME_PATH + "/{prefix}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Suche Nachnamen mit Praefix", tags = "Suchen")
    String getNamenByPrefix(@PathVariable final String prefix) {
        return service.findNamenByPrefix(prefix).toString();
    }
}
