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

public interface Bikey<R, C> {

    /**
     * Returns the key row corresponding to this key.
     *
     * @return the key row corresponding to this key
     */
    R getRow();

    /**
     * Returns the key column corresponding to this key.
     *
     * @return the key column corresponding to this key
     */
    C getColumn();

    /**
     * Compares the specified object with this entry for equality. Returns
     * <tt>true</tt> if the given object is also a bikey and the two entries
     * represent the same key.
     *
     * @param o
     *            object to be compared for equality with this map entry
     * @return <tt>true</tt> if the specified object is equal to this bikey
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns the hash code value for this bikey.
     *
     * @return the hash code value for this bikey
     * @see Object#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    @Override
    int hashCode();

}
