package com.acme.fussballverein.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import static com.acme.fussballverein.entity.Fussballverein.MANNSCHAFT_GRAPH;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

/**
 * Klasse Fußballverein.
 * <img src="..\..\..\..\..\asciidoc\Fussballverein.svg" alt="Klassendiagramm">
 */
@Entity
@Table(name = "fussballverein")
@NamedEntityGraph(name = MANNSCHAFT_GRAPH, attributeNodes = @NamedAttributeNode("mannschaften"))
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@ToString
@Builder
@SuppressWarnings("ClassFanOutComplexity")
public class Fussballverein {
    /**
     * NamedEntityGraph für das Attribut "mannschaft".
     */
    public static final String MANNSCHAFT_GRAPH = "Fussballverein.mannschaften";
    /**
     * Muster für Vereinsnamen.
     */
    public static final String VEREINSNAME_PATTERN =
        "[A-ZÄÖÜ][a-zäöü A-ZÄÖÜ]+";
    /**
     * Muster für PLZ.
     */
    public static final String PLZ_PATTERN =
        "^[0-9]{5}$";
    /**
     * Muster für Telefonnummern.
     */
    public static final String TELEFON_PATTERN =
        "^0[1-9]{1,4} ?[0-9]{4,9}$";

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Version
    private int version;

    @NotBlank
    @NotNull
    @Pattern(regexp = VEREINSNAME_PATTERN)
    private String name;

    @Email
    @NotNull
    private String email;

    @PastOrPresent
    private LocalDate gruendungsdatum;

    @NotNull
    @Pattern(regexp = PLZ_PATTERN)
    private String plz;

    @Pattern(regexp = TELEFON_PATTERN)
    private String telefonnummer;

    @CreationTimestamp
    private LocalDateTime erzeugt;

    @UpdateTimestamp
    private LocalDateTime aktualisiert;

    // der Spaltenwert referenziert einen Wert aus einer anderen DB
    @Column(name = "trainer_id")
    private UUID trainerId;

    @Transient
    private String trainerName;

    @Transient
    private String trainerEmail;

    @OneToMany(mappedBy = "fussballverein",
        cascade = {PERSIST, REMOVE},
        orphanRemoval = true)
    @OrderColumn(name = "idx", nullable = false)
    @ToString.Exclude
    private List<Mannschaft> mannschaften;

    /**
     * Fussballvereindaten überschreiben.
     *
     * @param fussballverein Neue Fussballvreindaten.
     */
    public void set(final Fussballverein fussballverein) {
        name = fussballverein.name;
        email = fussballverein.email;
        gruendungsdatum = fussballverein.gruendungsdatum;
        plz = fussballverein.plz;
        telefonnummer = fussballverein.telefonnummer;
    }
}
