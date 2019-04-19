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

public class IntObjectEntry<T> implements Map.Entry<Integer, T> {

    private final int key;
    private final T value;

    public IntObjectEntry(int key, T value) {
        this.key = key;
        this.value = value;
    }

    public int getIntKey() {
        return key;
    }

    @Override
    public Integer getKey() {
        return key;
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
    public T setValue(T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return 31 * key + ((value == null) ? 0 : value.hashCode());
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
        IntObjectEntry<?> other = (IntObjectEntry<?>) obj;
        if (key != other.key) {
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
        return key + "=" + value;
    }

}
