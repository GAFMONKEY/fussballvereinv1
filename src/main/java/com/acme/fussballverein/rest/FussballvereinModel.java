package com.acme.fussballverein.rest;

import com.acme.fussballverein.entity.Fussballverein;
import com.acme.fussballverein.entity.Mannschaft;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Model-Klasse für Spring HATEOAS. @lombok.Data fasst die Annotations @ToString, @EqualsAndHashCode, @Getter, @Setter
 * und @RequiredArgsConstructor zusammen.
 * <img src="../../../../../asciidoc/KundeModel.svg" alt="Klassendiagramm">
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@JsonPropertyOrder({
    "name", "email", "gruendungsdatum", "plz", "telefonnummer", "trainerId", "mannschaften"
})
@Relation(collectionRelation = "fussballvereine", itemRelation = "fussballverein")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@ToString(callSuper = true)
class FussballvereinModel extends RepresentationModel<FussballvereinModel> {
    private final String name;
    @EqualsAndHashCode.Include
    private final String email;
    private final LocalDate gruendungsdatum;
    private final String plz;
    private final String telefonnummer;
    private final List<Mannschaft> mannschaften;
    private final UUID trainerId;
    private final String trainerName;
    private final String trainerEmail;

    FussballvereinModel(final Fussballverein verein) {
        name = verein.getName();
        email = verein.getEmail();
        gruendungsdatum = verein.getGruendungsdatum();
        plz = verein.getPlz();
        telefonnummer = verein.getTelefonnummer();
        mannschaften = verein.getMannschaften();
        trainerId = verein.getTrainerId();
        trainerName = verein.getTrainerName();
        trainerEmail = verein.getTrainerEmail();
    }
}
