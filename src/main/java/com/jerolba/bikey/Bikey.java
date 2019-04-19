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

import java.util.Objects;

public class Bikey<R, C> {

    private final R row;
    private final C column;

    public Bikey(R row, C column) {
        this.row = row;
        this.column = column;
    }

    public R getRow() {
        return row;
    }

    public C getColumn() {
        return column;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((column == null) ? 0 : column.hashCode());
        result = prime * result + ((row == null) ? 0 : row.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Bikey) {
            Bikey<?, ?> e = (Bikey<?, ?>) o;
            if (Objects.equals(row, e.getRow()) && Objects.equals(column, e.getColumn())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + row + ", " + column + "]";
    }

}
