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
package com.acme.fussballverein.graphql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Eine Value-Klasse für Eingabedaten passend zu Suchkriterien aus dem GraphQL-Schema.
 *
 * @param name Name
 * @param plz Postleitzahl
 */
record Suchkriterien(
    String name,
    String plz
) {
    /**
     * Konvertierung in eine Map.
     *
     * @return Das konvertierte Map-Objekt
     */
    Map<String, List<String>> toMap() {
        final Map<String, List<String>> map = new HashMap<>(2, 1);
        if (name != null) {
            map.put("name", List.of(name));
        }
        if (plz != null) {
            map.put("plz", List.of(plz));
        }
        return map;
    }
}
