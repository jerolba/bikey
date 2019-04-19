/**
 * Copyright 2019 Jerónimo López Bezanilla
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.jerolba.bikey;

public class MatrixBikeyMap<R, C, V> extends TableBikeyMap<R, C, V> {

    public MatrixBikeyMap(int width) {
        super(() -> new IntArrayMap<>(width));
    }

    /**
     * Constructs a new {@code TableBikeyMap} with the same mappings as the
     * specified {@code BikeyMap}.
     *
     * <p>
     * New {@code BikeyMap} can not have more columns than original map, but can
     * add more rows referencing to existing columns.
     *
     * @param m
     *            the map whose mappings are to be placed in this map
     * @throws NullPointerException
     *             if the specified map is null
     */
    public MatrixBikeyMap(BikeyMap<R, C, V> m) {
        this(m.columnKeySet().size());
        this.putAll(m);
    }

}
