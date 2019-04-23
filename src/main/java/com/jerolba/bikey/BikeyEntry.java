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

import java.util.Map;

public class BikeyEntry<R, C, V> implements Map.Entry<Bikey<R, C>, V>, Bikey<R, C> {

    private final R row;
    private final C column;
    private final V value;

    public BikeyEntry(R row, C column, V value) {
        this.row = row;
        this.column = column;
        this.value = value;
    }

    @Override
    public Bikey<R, C> getKey() {
        return new BikeyImpl<>(row, column);
    }

    @Override
    public R getRow() {
        return row;
    }

    @Override
    public C getColumn() {
        return column;
    }

    @Override
    public V getValue() {
        return value;
    }

    /**
     * Because IntObjectEntry has no reference to original map node, is not
     * possible to modify its value.
     *
     * @param value
     *            new value to be stored in this entry
     * @return old value corresponding to the entry
     * @throws UnsupportedOperationException
     *             because it has no reference to original map
     */
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = prime + ((column == null) ? 0 : column.hashCode());
        result = prime * result + ((row == null) ? 0 : row.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BikeyEntry<?, ?, ?> other = (BikeyEntry<?, ?, ?>) obj;
        if (row == null) {
            if (other.row != null) {
                return false;
            }
        } else if (!row.equals(other.row)) {
            return false;
        }
        if (column == null) {
            if (other.column != null) {
                return false;
            }
        } else if (!column.equals(other.column)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[" + row + ", " + column + "]=" + value;
    }

}
