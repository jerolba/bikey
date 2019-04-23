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

import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface BikeySet<R, C> extends Set<Bikey<R, C>> {

    /**
     * Adds the specified pair of element to this set if they are not already
     * present.
     *
     * <p>
     * If this set already contains the element, the call leaves the set
     * unchanged and returns <tt>false</tt>. This ensures that sets never
     * contain duplicate elements.
     *
     * @param row
     *            row element to be added to this set
     * @param column
     *            column element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     *         pair of elements
     * @throws NullPointerException
     *             if one of the specified elements are null
     */
    boolean add(R row, C column);

    /**
     * Adds the specified bikey to this set if it is not already present.
     *
     * <p>
     * If this set already contains the element, the call leaves the set
     * unchanged and returns <tt>false</tt>. This ensures that sets never
     * contain duplicate elements.
     *
     * @param key
     *            bikey element to be added to this set
     * @return <tt>true</tt> if this set did not already contain the specified
     *         bikey
     * @throws NullPointerException
     *             if the specified elements is null
     */
    @Override
    boolean add(Bikey<R, C> key);

    /**
     * Removes the specified pair of elements from this set if they are present.
     *
     * <p>
     * If after a removal, one column has no items in any row, its metainfo is
     * not deleted and potentially the set can occupy a lot of space being
     * empty.
     *
     * @param row
     *            row element to be removed from this set, if present
     * @param column
     *            column element to be removed from this set, if present
     * @return <tt>true</tt> if this set contained the specified pair of
     *         elements
     * @throws NullPointerException
     *             if one of the specified elements are null
     */
    boolean remove(R row, C column);

    /**
     * Removes the specified element from this set if it is present
     *
     * @param o
     *            object to be removed from this set, if present
     * @return <tt>true</tt> if this set contained the specified element
     * @throws NullPointerException
     *             if the specified element is null
     */
    @Override
    boolean remove(Object o);

    /**
     * Returns a set of row keys that have one or more values in the
     * <tt>BikeySet</tt>. Changes to the set will update the underlying table.
     *
     * @return set of row keys
     */
    Set<R> rowKeySet();

    /**
     * Returns a set of column keys that have one or more values in the
     * <tt>BikeySet</tt>. Changes to the set will update the underlying table.
     *
     * @return set of column keys
     */
    Set<C> columnKeySet();

    /**
     * Returns <tt>true</tt> if this set contains the specified pair of
     * elements.
     *
     * @param row
     *            row element whose presence in this set is to be tested
     * @param column
     *            column element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified pair of elements
     */
    boolean contains(R row, C column);

    /**
     * Returns <tt>true</tt> if this set contains the specified element.
     *
     * @param o
     *            element whose presence in this set is to be tested
     * @return <tt>true</tt> if this set contains the specified element
     */
    @Override
    boolean contains(Object o);

    /**
     * Performs the given action for each pair of element of the set until all
     * elements have been processed or the action throws an exception.
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action
     *            The action to be performed for each element
     * @throws NullPointerException
     *             if the specified action is null
     */
    void forEach(BiConsumer<? super R, ? super C> action);

    /**
     * Returns a sequential {@code Stream} with this collection of pair values
     * as its source.
     *
     * @return a sequential {@code Stream} over the pairs of elements in this
     *         collection in the tuple Bikey
     */
    @Override
    default Stream<Bikey<R, C>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    default Spliterator<Bikey<R, C>> spliterator() {
        return Spliterators.spliterator(iterator(), size(),
                Spliterator.DISTINCT + Spliterator.NONNULL + Spliterator.SIZED);
    }

}
