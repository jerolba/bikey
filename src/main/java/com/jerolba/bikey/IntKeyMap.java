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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An object that maps int keys to values. A map cannot contain duplicate keys;
 * each key can map to at most one value.
 *
 * Has the same behaviour than a {@code Map<Integer,T>}, but with an int
 * primitive value.
 *
 * @param <T>
 *            the type of mapped values
 */
public interface IntKeyMap<T> extends Iterable<IntObjectEntry<T>> {

    /**
     * Associates the specified value with the specified key in this int keyed
     * map If the map previously contained a mapping for the key, the old value
     * is replaced by the specified value. (A map <tt>m</tt> is said to contain
     * a mapping for a key <tt>k</tt> if and only if {@link #containsKey(int)
     * m.containsKey(k)} would return <tt>true</tt>.)
     *
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
     *         if there was no mapping for <tt>key</tt>.
     * @throws NullPointerException
     *             if the specified value is null.
     */
    T put(int key, T value);

    /**
     * Copies all of the mappings from the specified map to this map. The effect
     * of this call is equivalent to that of calling {@code put(key, value)} on
     * this map once for each mapping from key {@code key} to value
     * {@code value} in the specified map. The behavior of this operation is
     * undefined if the specified map is modified while the operation is in
     * progress.
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
    default void putAll(IntKeyMap<? extends T> m) {
        m.forEach(this::put);
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key.
     *
     * @param key
     *            the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null}
     *         if this map contains no mapping for the key
     */
    T get(int key);

    /**
     * Removes the specified element from this map if it is present.
     *
     * @param key
     *            the key to remove
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
     *         if there was no mapping for <tt>key</tt>.
     * @throws NullPointerException
     *             if one of the specified elements are null
     */
    T remove(int key);

    /**
     * Returns <tt>true</tt> if this map contains the specified element.
     *
     * @param key
     *            key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains the specified key
     */
    default boolean containsKey(int key) {
        return get(key) != null;
    }

    /**
     * Returns the number of elements in this map (its cardinality).
     *
     * @return the number of elements in this map
     */
    int size();

    /**
     * Removes all of the elements from this map. The map will be empty after
     * this call returns.
     */
    void clear();

    /**
     * Performs the given action for each pair of element of the map until all
     * elements have been processed or the action throws an exception.
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action
     *            The action to be performed for each pair of elements
     * @throws NullPointerException
     *             if the specified action is null
     */
    void forEach(IntObjectConsumer<T> action);

    /**
     * Performs the given action to each int key element of the map until all
     * elements have been processed or the action throws an exception.
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action
     *            The action to be performed for each int key element
     * @throws NullPointerException
     *             if the specified action is null
     */
    void forEachKey(IntConsumer action);

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the specified
     * value.
     *
     * @param value
     *            value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the specified
     *         value
     */
    boolean containsValue(Object value);

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are reflected
     * in the collection. If the map is modified while an iteration over the
     * collection is in progress, the results of the iteration are undefined.
     * The collection doesn't support element removal.
     *
     * @return a collection view of the values contained in this map
     */
    Collection<T> values();

    /**
     * Returns a {@link Set} view of the keys contained in this map. The set is
     * backed by the map, so changes to the map are reflected in the set. If the
     * map is modified while an iteration over the set is in progress the
     * results of the iteration are undefined. The set doesn't support element
     * removal.
     *
     * @return a set view of the mappings contained in this map
     */
    Set<Integer> keySet();

    /**
     * Returns a {@link Set} view of the mappings contained in this map. The set
     * is backed by the map, so changes to the map are reflected in the set. If
     * the map is modified while an iteration over the set is in progress the
     * results of the iteration are undefined.
     *
     * @return a set view of the mappings contained in this map
     */
    Set<IntObjectEntry<T>> entrySet();

    /**
     * Returns <tt>true</tt> if this map contains no elements.
     *
     * @return <tt>true</tt> if this map contains no elements
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns a sequential {@code Stream} with this collection of key and value
     * pairs as its source.
     *
     * @return a sequential {@code Stream} over the pairs of elements in this
     *         collection as IntObjectEntry
     */
    default Stream<IntObjectEntry<T>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    default Spliterator<IntObjectEntry<T>> spliterator() {
        return Spliterators.spliterator(this.iterator(), this.size(), Spliterator.DISTINCT);
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * @param key
     *            the key whose associated value is to be returned
     * @param defaultValue
     *            the default mapping of the key
     * @return the value to which the specified key is mapped, or
     *         {@code defaultValue} if this map contains no mapping for the key
     * @throws NullPointerException
     *             if the specified key is null
     */
    default T getOrDefault(int key, T defaultValue) {
        T t = get(key);
        return t == null ? defaultValue : t;
    }

    /**
     * If the specified key is not already associated with a value associates it
     * with the given value and returns {@code null}, else returns the current
     * value.
     *
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         {@code null} if there was no mapping for the key.
     * @throws NullPointerException
     *             if the specified value is null
     */
    default T putIfAbsent(int key, T value) {
        T t = get(key);
        return t == null ? put(key, value) : t;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value.
     *
     * @param key
     *            key with which the specified value is associated
     * @param value
     *            value expected to be associated with the specified key
     * @return {@code true} if the value was removed
     * @throws NullPointerException
     *             if the specified value is null
     */
    default boolean remove(int key, Object value) {
        Object currentValue = get(key);
        if (!Objects.equals(currentValue, value) || currentValue == null) {
            return false;
        }
        remove(key);
        return true;
    }

    /**
     * Replaces the entry for the specified key only if currently mapped to the
     * specified value.
     *
     * <p>
     * The implementation makes no guarantees about synchronization or atomicity
     * properties of this method.
     *
     * @param key
     *            key with which the specified value is associated
     * @param oldValue
     *            value expected to be associated with the specified key
     * @param newValue
     *            value to be associated with the specified key
     * @return {@code true} if the value was replaced
     * @throws NullPointerException
     *             if a specified newValue is null
     * @throws NullPointerException
     *             if oldValue is null
     */
    default boolean replace(int key, T oldValue, T newValue) {
        Object currentValue = get(key);
        if (!Objects.equals(currentValue, oldValue) || currentValue == null) {
            return false;
        }
        put(key, newValue);
        return true;
    }

    /**
     * Replaces the entry for the specified key only if it is currently mapped
     * to some value.
     *
     * <p>
     * The implementation makes no guarantees about synchronization or atomicity
     * properties of this method.
     *
     * @param key
     *            key with which the specified value is associated
     * @param value
     *            value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         {@code null} if there was no mapping for the key.
     * @throws NullPointerException
     *             if the specified value is null
     */
    default T replace(int key, T value) {
        T currentValue = get(key);
        return currentValue != null ? put(key, value) : currentValue;
    }

    /**
     * If the specified key is not already associated with a value, attempts to
     * compute its value using the given mapping function and enters it into
     * this map unless {@code null}.
     *
     * <p>
     * If the function returns {@code null} no mapping is recorded. If the
     * function itself throws an (unchecked) exception, the exception is
     * rethrown, and no mapping is recorded. The most common usage is to
     * construct a new object serving as an initial mapped value or memoized
     * result.
     *
     * <p>
     * The implementation makes no guarantees about synchronization or atomicity
     * properties of this method.
     *
     * @param key
     *            key with which the specified value is to be associated
     * @param mappingFunction
     *            the function to compute a value
     * @return the current (existing or computed) value associated with the
     *         specified key
     * @throws NullPointerException
     *             if the mappingFunction is null or cumputed value is null
     */
    default T computeIfAbsent(int key, Function<Integer, ? extends T> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        T t = get(key);
        if (t == null) {
            T newValue = mappingFunction.apply(key);
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            }
        }
        return t;
    }

    /**
     * If the value for the specified key is present, attempts to compute a new
     * mapping given the key and its current mapped value.
     *
     * <p>
     * If the function returns {@code null}, the mapping is removed. If the
     * function itself throws an (unchecked) exception, the exception is
     * rethrown, and the current mapping is left unchanged.
     *
     * <p>
     * The implementation makes no guarantees about synchronization or atomicity
     * properties of this method.
     *
     * @param key
     *            key with which the specified value is to be associated
     * @param remappingFunction
     *            the function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException
     *             if the remappingFunction is null
     */
    default T computeIfPresent(int key, BiFunction<Integer, ? super T, ? extends T> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        T oldValue = get(key);
        if (oldValue != null) {
            T newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            }
            remove(key);
        }
        return null;
    }

    /**
     * Attempts to compute a mapping for the specified key and its current
     * mapped value (or {@code null} if there is no current mapping)
     *
     * (Method {@link #merge merge()} is often simpler to use for such
     * purposes.)
     *
     * <p>
     * If the function returns {@code null}, the mapping is removed (or remains
     * absent if initially absent). If the function itself throws an (unchecked)
     * exception, the exception is rethrown, and the current mapping is left
     * unchanged.
     *
     * <p>
     * The implementation makes no guarantees about synchronization or atomicity
     * properties of this method.
     *
     * @param key
     *            key with which the specified value is to be associated
     * @param remappingFunction
     *            the function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException
     *             if the remappingFunction is null
     */
    default T compute(int key, BiFunction<Integer, ? super T, ? extends T> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        T oldValue = get(key);

        T newValue = remappingFunction.apply(key, oldValue);
        if (newValue != null) {
            put(key, newValue);
            return newValue;
        }
        if (oldValue != null) {
            remove(key);
        }
        return null;
    }

    /**
     * If the specified key is not already associated with a value, associates
     * it with the given non-null value. Otherwise, replaces the associated
     * value with the results of the given remapping function, or removes if the
     * result is {@code null}. This method may be of use when combining multiple
     * mapped values for a key.
     *
     * <p>
     * If the function returns {@code null} the mapping is removed. If the
     * function itself throws an (unchecked) exception, the exception is
     * rethrown, and the current mapping is left unchanged.
     *
     * <p>
     * The implementation makes no guarantees about synchronization or atomicity
     * properties of this method.
     *
     * @param key
     *            key with which the resulting value is to be associated
     * @param value
     *            the non-null value to be merged with the existing value
     *            associated with the key or, if no existing value to be
     *            associated with the key
     * @param remappingFunction
     *            the function to recompute a value if present
     * @return the new value associated with the specified key, or null if no
     *         value is associated with the key
     */
    default T merge(int key, T value, BiFunction<? super T, ? super T, ? extends T> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        T oldValue = get(key);
        T newValue = value;
        if (oldValue != null) {
            newValue = remappingFunction.apply(oldValue, value);
        }
        if (newValue == null) {
            remove(key);
        } else {
            put(key, newValue);
        }
        return newValue;
    }

}
