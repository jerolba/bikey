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

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface BikeyMap<R, C, V> extends Iterable<BikeyEntry<R, C, V>> {

    /**
     * Associates the specified value with the specified bikey in this map. If
     * the map previously contained a mapping for the key, the old value is
     * replaced by the specified value.
     *
     * @param row
     *            row with which the specified value is to be associated
     * @param column
     *            column with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified bikey
     * @return the previous value associated with <tt>bikey</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>bikey</tt>.
     * @throws NullPointerException
     *             if the specified bikey or value is null
     */
    V put(R row, C column, V value);

    /**
     * Copies all of the mappings from the specified map to this map. The effect
     * of this call is equivalent to that of calling
     * {@code put(row, column, value)} on this map once for each mapping from
     * bikey {@code row} and {@code column} to value {@code v} in the specified
     * map. The behavior of this operation is undefined if the specified map is
     * modified while the operation is in progress.
     *
     * @param m
     *            mappings to be stored in this map
     * @throws NullPointerException
     *             if the specified map is null, or if the specified map
     *             contains null keys or values
     * @throws IllegalArgumentException
     *             if some property of a key or value in the specified map
     *             prevents it from being stored in this map
     */
    default void putAll(BikeyMap<? extends R, ? extends C, ? extends V> m) {
        m.forEach(this::put);
    }

    /**
     * Returns the value to which the specified bikey is mapped, or {@code null}
     * if this map contains no mapping for the bikey.
     *
     * @param row
     *            row whose associated value with column value is to be returned
     * @param column
     *            column whose associated value with row value is to be returned
     * @return the value to which the specified bikey is mapped, or {@code null}
     *         if this map contains no mapping for the key
     * @throws NullPointerException
     *             if the specified row or column is null
     */
    V get(R row, C column);

    /**
     * Removes the mapping for a bikey from this map if it is present.
     *
     * <p>
     * Returns the value to which this map previously associated the bikey, or
     * <tt>null</tt> if the map contained no mapping for the key.
     *
     * <p>
     * The map will not contain a mapping for the specified bikey once the call
     * returns.
     *
     * @param row
     *            row whose mapping is to be removed from the map
     * @param column
     *            column whose mapping is to be removed from the map
     * @return the previous value associated with <tt>bikey</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * @throws NullPointerException
     *             if the specified bikey is null.
     */
    V remove(R row, C column);

    /**
     * Returns the number of bikey-value mappings in this map.
     *
     * @return the number of bikey-value mappings in this map
     */
    int size();

    /**
     * Removes all of the mappings from this map. The map will be empty after
     * this call returns.
     */
    void clear();

    /**
     * Returns a {@link Set} view of the bikeys contained in this map. The set
     * is backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. If the map is modified while an iteration over the set is in
     * progress, the results of the iteration are undefined.
     *
     * @return a set view of the keys contained in this map
     */
    Set<Bikey<R, C>> keySet();

    /**
     * Returns a {@link BikeySet} view of the bikeys contained in this map. The
     * set is backed by the map, so changes to the map are reflected in the set.
     * If the map is modified while an iteration over the set is in progress,
     * the results of the iteration are undefined.
     *
     * @return a set view of the keys contained in this map
     */
    BikeySet<R, C> bikeySet();

    /**
     * Returns a set of row keys that have one or more values in the map.
     * Changes to the set will update the underlying table.
     *
     * @return set of row keys
     */
    Set<R> rowKeySet();

    /**
     * Returns a set of column keys that have one or more values in the map.
     * Changes to the set will update the underlying table.
     *
     * @return set of column keys
     */
    Set<C> columnKeySet();

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are reflected
     * in the collection. If the map is modified while an iteration over the
     * collection is in progress, the results of the iteration are undefined.
     *
     * @return a collection view of the values contained in this map
     */
    Collection<V> values();

    /**
     * Returns a {@link Set} view of the mappings contained in this map. The set
     * is backed by the map, so changes to the map are reflected in the set.
     *
     * @return a set view of the mappings contained in this map
     */
    Set<BikeyEntry<R, C, V>> entrySet();

    /**
     * Compares the specified object with this map for equality. Returns
     * <tt>true</tt> if the given object is also a map and the two maps
     * represent the same mappings. More formally, two maps <tt>m1</tt> and
     * <tt>m2</tt> represent the same mappings if
     * <tt>m1.entrySet().equals(m2.entrySet())</tt>. This ensures that the
     * <tt>equals</tt> method works properly across different implementations of
     * the <tt>BikeyMap</tt> interface.
     *
     * @param o
     *            object to be compared for equality with this map
     * @return <tt>true</tt> if the specified object is equal to this map
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns the hash code value for this map. The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * <tt>entrySet()</tt> view. This ensures that <tt>m1.equals(m2)</tt>
     * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
     * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
     * {@link Object#hashCode}.
     *
     * @return the hash code value for this map
     * @see BikeyEntry#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    @Override
    int hashCode();

    /**
     * Performs the given action for each bikey in this map until all entries
     * have been processed or the action throws an exception
     *
     * @param action
     *            The action to be performed for each bikey
     * @throws NullPointerException
     *             if the specified action is null
     */
    void forEachBikey(BiConsumer<? super R, ? super C> action);

    /**
     * Performs the given action for each entry in this map until all entries
     * have been processed or the action throws an exception.
     *
     * @param action
     *            The action to be performed for each entry
     * @throws NullPointerException
     *             if the specified action is null
     */
    default void forEach(BiConsumer<Bikey<R, C>, ? super V> action) {
        Objects.requireNonNull(action);
        forEach((r, c, v) -> action.accept(new BikeyImpl<>(r, c), v));
    }

    /**
     * Performs the given action for each entry in this map until all entries
     * have been processed or the action throws an exception.
     *
     * <p>
     * The action processes bikey as a {@link TriConsumer} with a broken down
     * key.
     *
     * @param action
     *            The action to be performed for each entry
     * @throws NullPointerException
     *             if the specified action is null
     */
    void forEach(TriConsumer<? super R, ? super C, ? super V> action);

    @Override
    default Spliterator<BikeyEntry<R, C, V>> spliterator() {
        return Spliterators.spliterator(iterator(), size(),
                Spliterator.DISTINCT + Spliterator.NONNULL + Spliterator.SIZED);
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more bikeys to the
     * specified value. More formally, returns <tt>true</tt> if and only if this
     * map contains at least one mapping to a value <tt>v</tt> such that
     * <tt>(value==null ? v==null : value.equals(v))</tt>. This operation
     * require time linear in the map size.
     *
     * @param value
     *            value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more bikeys to the
     *         specified value
     * @throws NullPointerException
     *             if the specified value is null
     */
    boolean containsValue(Object value);

    /**
     * Returns <tt>true</tt> if the map contains a mapping with the specified
     * row key.
     *
     * @param row
     *            key of row to search for
     * @return <tt>true</tt> if the map contains a mapping with the specified
     *         row.
     */
    boolean containsRow(Object row);

    /**
     * Returns <tt>true</tt> if the map contains a mapping with the specified
     * column.
     *
     * @param column
     *            key of column to search for
     * @return <tt>true</tt> if the map contains a mapping with the specified
     *         column.
     */
    boolean containsColumn(Object column);

    /**
     * Returns <tt>true</tt> if this map contains no bikey-value mappings.
     *
     * @return <tt>true</tt> if this map contains no bikey-value mappings
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * bikey.
     *
     * @param row
     *            row whose presence with column in this map are to be tested
     * @param column
     *            column whose presence with row in this map are to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         bikey
     * @throws NullPointerException
     *             if the specified row or column is null
     */
    default boolean containsKey(R row, C column) {
        return get(row, column) != null;
    }

    /**
     * Returns the value to which the specified bikey is mapped, or
     * {@code defaultValue} if this map contains no mapping for the bikey.
     *
     * @param row
     *            row whose associated value is to be returned
     * @param column
     *            column whose associated value is to be returned
     * @param defaultValue
     *            the default mapping of the bikey
     * @return the value to which the specified bikey is mapped, or
     *         {@code defaultValue} if this map contains no mapping for the
     *         bikey
     */
    default V getOrDefault(R row, C column, V defaultValue) {
        V v = get(row, column);
        return v == null ? defaultValue : v;
    }

    /**
     * If the specified bikey is not already associated with a value associates
     * it with the given value and returns {@code null}, else returns the
     * current value.
     *
     * @param row
     *            row with which the specified value is to be associated
     * @param column
     *            column with which the specified value is to be associated
     * @param defaultValue
     *            value to be associated with the specified key
     * @return the previous value associated with the specified bikey
     * @throws NullPointerException
     *             if the specified keys or value is null
     */
    default V putIfAbsent(R row, C column, V defaultValue) {
        V v = get(row, column);
        return v == null ? put(row, column, defaultValue) : v;
    }

    /**
     * Removes the entry for the specified bikey only if it is currently mapped
     * to the specified value.
     *
     * @param row
     *            row with which the specified value is to be associated
     * @param column
     *            column with which the specified value is to be associated
     * @param value
     *            value expected to be associated with the specified key
     * @return {@code true} if the value was removed
     * @throws NullPointerException
     *             if the specified keys or value is null
     */
    default boolean remove(R row, C column, Object value) {
        Object currentValue = get(row, column);
        if (!Objects.equals(currentValue, value) || currentValue == null) {
            return false;
        }
        remove(row, column);
        return true;
    }

    /**
     * Replaces the entry for the specified bikey only if currently mapped to
     * the specified value.
     *
     * @param row
     *            row with which the specified value is to be associated
     * @param column
     *            column with which the specified value is to be associated
     * @param oldValue
     *            value expected to be associated with the specified bikey
     * @param newValue
     *            value to be associated with the specified bikey
     * @return {@code true} if the value was replaced
     * @throws NullPointerException
     *             if a specified key or newValue is null
     * @throws NullPointerException
     *             if oldValue is null
     */
    default boolean replace(R row, C column, V oldValue, V newValue) {
        Object currentValue = get(row, column);
        if (!Objects.equals(currentValue, oldValue) || currentValue == null) {
            return false;
        }
        put(row, column, newValue);
        return true;
    }

    /**
     * Replaces the entry for the specified bikey only if it is currently mapped
     * to some value.
     *
     * @param row
     *            row with which the specified value is to be associated
     * @param column
     *            column with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         {@code null} if there was no mapping for the key
     * @throws NullPointerException
     *             if the specified key or value is null
     */
    default V replace(R row, C column, V value) {
        V currentValue = get(row, column);
        return currentValue != null ? put(row, column, value) : currentValue;
    }

    /**
     * If the specified bikey is not already associated with a value, attempts
     * to compute its value using the given mapping function and enters it into
     * this map unless {@code null}.
     *
     * <p>
     * If the mapping function returns {@code null}, no mapping is recorded. If
     * the mapping function itself throws an (unchecked) exception, the
     * exception is rethrown, and no mapping is recorded. The mapping function
     * should not modify this map during computation.
     *
     * @param row
     *            row with which the specified value is to be associated
     * @param column
     *            column with which the specified value is to be associated
     * @param mappingFunction
     *            the mapping function to compute a value
     * @return the current (existing or computed) value associated with the
     *         specified bikey, or null if the computed value is null
     * @throws NullPointerException
     *             if the specified bikey is null or the mappingFunction is null
     */
    default V computeIfAbsent(R row, C column, BiFunction<R, C, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v = get(row, column);
        if (v == null) {
            V newValue = mappingFunction.apply(row, column);
            if (newValue != null) {
                put(row, column, newValue);
                return newValue;
            }
        }
        return v;
    }

    /**
     * If the value for the specified bikey is present and non-null, attempts to
     * compute a new mapping given the bikey and its current mapped value.
     *
     * <p>
     * If the remapping function returns {@code null}, the mapping is removed.
     * If the remapping function itself throws an (unchecked) exception, the
     * exception is rethrown, and the current mapping is left unchanged.
     *
     * <p>
     * The remapping function should not modify this map during computation.
     *
     * @param row
     *            row with which the specified value is to be associated
     * @param column
     *            column with which the specified value is to be associated
     * @param remappingFunction
     *            the remapping function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException
     *             if the specified bikey is null or the mappingFunction is null
     */
    default V computeIfPresent(R row, C column,
            TriFunction<? super R, ? super C, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(row, column);
        if (oldValue != null) {
            V newValue = remappingFunction.apply(row, column, oldValue);
            if (newValue != null) {
                put(row, column, newValue);
                return newValue;
            }
            remove(row, column);
        }
        return null;
    }

    /**
     * Attempts to compute a mapping for the specified bikey and its current
     * mapped value or {@code null} if there is no current mapping.
     *
     * <p>
     * If the function returns {@code null}, the mapping is removed (or remains
     * absent if initially absent). If the function itself throws an (unchecked)
     * exception, the exception is rethrown, and the current mapping is left
     * unchanged.
     *
     * @param row
     *            row with which the specified value is to be associated
     * @param column
     *            column with which the specified value is to be associated
     * @param remappingFunction
     *            the function to compute a value
     * @return the new value associated with the specified bikey, or null if
     *         none
     * @throws NullPointerException
     *             if the specified key is null or the remappingFunction is null
     */
    default V compute(R row, C column, TriFunction<? super R, ? super C, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(row, column);

        V newValue = remappingFunction.apply(row, column, oldValue);
        if (newValue != null) {
            put(row, column, newValue);
            return newValue;
        }
        if (oldValue != null) {
            remove(row, column);
        }
        return null;
    }

    /**
     * If the specified bikey is not already associated with a value associates
     * it with the given non-null value. Otherwise, replaces the associated
     * value with the results of the given remapping function, or removes if the
     * result is {@code null}. This method may be of use when combining multiple
     * mapped values for a bikey.
     *
     * <p>
     * If the function returns {@code null} the mapping is removed. If the
     * function itself throws an (unchecked) exception, the exception is
     * rethrown, and the current mapping is left unchanged.
     *
     * @param row
     *            row with which the specified value is to be associated
     * @param column
     *            column with which the specified value is to be associated
     * @param value
     *            the non-null value to be merged with the existing value
     *            associated with the bikey or, if no existing value, to be
     *            associated with the key
     * @param remappingFunction
     *            the function to recompute a value if present
     * @return the new value associated with the specified bikey, or null if no
     *         value is associated with the key
     * @throws NullPointerException
     *             if the specified key is null or the value or
     *             remappingFunction is null
     */
    default V merge(R row, C column, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = get(row, column);
        V newValue = value;
        if (oldValue != null) {
            newValue = remappingFunction.apply(oldValue, value);
        }
        if (newValue == null) {
            remove(row, column);
        } else {
            put(row, column, newValue);
        }
        return newValue;
    }
}
