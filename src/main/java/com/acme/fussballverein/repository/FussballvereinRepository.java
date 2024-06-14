/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.acme.fussballverein.repository;

import com.acme.fussballverein.entity.Fussballverein;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import static com.acme.fussballverein.entity.Fussballverein.MANNSCHAFT_GRAPH;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository für den DB-Zugriff bei Fussballvereinen.
 */
@Repository
public interface FussballvereinRepository extends JpaRepository<Fussballverein, UUID>,
                                                QuerydslPredicateExecutor<Fussballverein> {
    @EntityGraph(MANNSCHAFT_GRAPH)
    @Override
    List<Fussballverein> findAll();

    @EntityGraph(MANNSCHAFT_GRAPH)
    @Override
    List<Fussballverein> findAll(Predicate predicate);

    @EntityGraph(MANNSCHAFT_GRAPH)
    @Override
    Optional<Fussballverein> findById(UUID id);

    /**
     * Fussballverein zu gegebener Emailadresse aus der DB ermitteln.
     *
     * @param email Emailadresse für die Suche
     * @return Optional mit dem gefundenen Fussballverein oder leeres Optional
     */
    @Query("""
        SELECT f
        FROM   Fussballverein f
        WHERE  lower(f.email) LIKE concat(lower(:email), '%')
        """)
    @EntityGraph(MANNSCHAFT_GRAPH)
    Optional<Fussballverein> findByEmail(String email);

    /**
     * Fussballverein zu gegebener Emailadresse aus der DB ermitteln.
     *
     * @param plz Emailadresse für die Suche
     * @return Optional mit dem gefundenen Fussballverein oder leeres Optional
     */
    @Query("""
        SELECT f
        FROM   Fussballverein f
        WHERE  lower(f.plz) LIKE concat('%',lower(:plz), '%')
        """)
    @EntityGraph(MANNSCHAFT_GRAPH)
    Collection<Fussballverein> findByPlz(String plz);

    /**
     * Abfrage, ob es einen Fussballvereine mit gegebener Emailadresse gibt.
     *
     * @param email Emailadresse für die Suche
     * @return true, falls es einen solchen Fussballvereine gibt, sonst false
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    boolean existsByEmail(String email);

    /**
     * Fussballverein anhand des Namens suchen.
     *
     * @param name Der (Teil-) Name der gesuchten Fussballvereine
     * @return Die gefundenen Fussballvereine oder eine leere Collection
     */
    @Query("""
        SELECT   f
        FROM     Fussballverein f
        WHERE    lower(f.name) LIKE concat('%', lower(:name), '%')
        ORDER BY f.id
        """)
    @EntityGraph(MANNSCHAFT_GRAPH)
    Collection<Fussballverein> findByName(CharSequence name);

    /**
     * Abfrage, welche Namen es zu einem Präfix gibt.
     *
     * @param prefix Name-Präfix.
     * @return Die passenden Name oder eine leere Collection.
     */
    @Query("""
        SELECT DISTINCT f.name
        FROM     Fussballverein f
        WHERE    lower(f.name) LIKE concat(lower(:prefix), '%')
        ORDER BY f.name
        """)
    Collection<String> findNamenByPrefix(String prefix);

    /**
     * Fussballvereine zu gegebener Trainer-ID aus der DB ermitteln.
     *
     * @param trainerId Kunde-ID für die Suche
     * @return Liste der gefundenen Fussballvereine
     */
    @EntityGraph(MANNSCHAFT_GRAPH)
    List<Fussballverein> findByTrainerId(UUID trainerId);
}
