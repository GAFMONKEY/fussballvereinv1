package com.acme.fussballverein.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

/**
 * Klasse Mannschaft.
 */
@Entity
@Table(name = "mannschaft")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Mannschaft {
    /**
     * Muster für die Jugenden einer Vereinsmannschaft.
     */
    public static final String JUGEND_PATTERN =
        "(Bambini|F|E|D|C|B|A)";

    @Id
    @GeneratedValue
    @JsonIgnore
    @EqualsAndHashCode.Include
    private  UUID id;

    @Pattern(regexp = JUGEND_PATTERN)
    private String jugend;

    @NotNull
    @Min(0)
    private int anzahlMitglieder;

    @ManyToOne(optional = false, fetch = LAZY, cascade = {PERSIST, REMOVE})
    @JoinColumn
    @JsonIgnore
    @ToString.Exclude
    private Fussballverein fussballverein;
}
