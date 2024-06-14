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

//import com.acme.fussballverein.entity.QFussballverein;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Locale.GERMAN;

/**
 * Singleton-Klasse, um Prädikate durch QueryDSL für eine WHERE-Klausel zu bauen.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Component
@Slf4j
public class PredicateBuilder {
    /**
     * Prädikate durch QueryDSL für eine WHERE-Klausel zu bauen.
     *
     * @param queryParams als MultiValueMap
     * @return Predicate in QueryDSL für eine WHERE-Klausel
     *
    @SuppressWarnings("ReturnCount")
    public Optional<Predicate> build(final Map<String, ? extends List<String>> queryParams) {
        log.debug("build: queryParams={}", queryParams);

        final var qFussballverein = QFussballverein.fussballverein;
        final var booleanExprList = queryParams
            .entrySet()
            .stream()
            .map(entry -> toBooleanExpression(entry.getKey(), entry.getValue(), qFussballverein))
            .toList();
        if (booleanExprList.isEmpty() || booleanExprList.contains(null)) {
            return Optional.empty();
        }

        final var result = booleanExprList
            .stream()
            .reduce(booleanExprList.get(0), BooleanExpression::and);
        return Optional.of(result);
    }

    @SuppressWarnings({"CyclomaticComplexity", "UnnecessaryParentheses"})
    private BooleanExpression toBooleanExpression(
        final String paramName,
        final List<String> paramValues,
        final QFussballverein qFussballverein
    ) {
        log.trace("toSpec: paramName={}, paramValues={}", paramName, paramValues);

        final var value = paramValues.get(0);
        return switch (paramName) {
            case "name" -> name(value, qFussballverein);
            case "email" ->  email(value, qFussballverein);
            case "plz" -> plz(value, qFussballverein);
            case "telefonnummer" -> telefonnummer(value, qFussballverein);
            default -> null;
        };
    }

    private BooleanExpression name(final String teil, final QFussballverein qFussballverein) {
        return qFussballverein.name.toLowerCase().matches("%" + teil.toLowerCase(GERMAN) + '%');
    }

    private BooleanExpression email(final String teil, final QFussballverein qFussballverein) {
        return qFussballverein.email.toLowerCase().matches("%" + teil.toLowerCase(GERMAN) + '%');
    }

    private BooleanExpression plz(final String plz, final QFussballverein qFussballverein) {
        return qFussballverein.plz.toLowerCase().matches("%" + plz.toLowerCase(GERMAN) + '%');
    }

    private BooleanExpression telefonnummer(final String teil, final QFussballverein qFussballverein) {
        return qFussballverein.telefonnummer.toLowerCase().matches("%" + teil.toLowerCase(GERMAN) + '%');


    }*/
}

