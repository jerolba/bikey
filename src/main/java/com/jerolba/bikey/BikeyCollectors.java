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

import java.util.function.*;
import java.util.stream.Collector;

public class BikeyCollectors {

    /**
     * Returns a {@code Collector} that accumulates the input elements into a
     * new {@code BikeySet}.
     *
     * @param <R>
     *            row key type of the bikeys
     * @param <C>
     *            column key type of the bikeys
     * @return a {@code Collector} which collects all the input elements into a
     *         {@code BikeySet}
     */
    public static <R, C> Collector<Bikey<R, C>, ?, BikeySet<R, C>> toSet() {
        return Collector.of(
                (Supplier<BikeySet<R, C>>) TableBikeySet::new,
                BikeySet::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code BikeyMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     *
     * <p>
     * If the mapped bikey (row and column) contains duplicates (according to
     * {@link Object#equals(Object)}), the value mapping function is applied to
     * each equal element, and the results are merged using the provided merging
     * function.
     *
     * <p>
     * The returned {@code Collector} is not concurrent and doesn't support
     * parallel stream pipelines.
     *
     * @param <T>
     *            the type of the input elements
     * @param <R>
     *            row key type of the map keys
     * @param <C>
     *            column key type of the map keys
     * @param <U>
     *            the output type of the value mapping function
     * @param keyMapper
     *            a mapping function to produce bikeys
     * @param valueMapper
     *            a mapping function to produce values
     * @return a {@code Collector} which collects elements into a
     *         {@code BikeMap} whose keys are the result of applying a key
     *         mapping function to the input elements.
     *
     * @see #toMap(Function, Function, BinaryOperator)
     * @see #toMap(Function, Function, Supplier)
     * @see #toMap(Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R, C, U> Collector<T, ?, BikeyMap<R, C, U>> toMap(
            Function<? super T, ? extends Bikey<R, C>> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return toMap(keyMapper, valueMapper, throwingMerger(), TableBikeyMap::new);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code BikeyMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     *
     * <p>
     * If the mapped bikey (row and column) contains duplicates (according to
     * {@link Object#equals(Object)}), the value mapping function is applied to
     * each equal element, and the results are merged using the provided merging
     * function.
     *
     * <p>
     * The returned {@code Collector} is not concurrent and doesn't support
     * parallel stream pipelines.
     *
     * @param <T>
     *            the type of the input elements
     * @param <R>
     *            row key type of the map keys
     * @param <C>
     *            column key type of the map keys
     * @param <U>
     *            the output type of the value mapping function
     * @param rowKeyMapper
     *            a mapping function to produce row key
     * @param columnKeyMapper
     *            a mapping function to produce column key
     * @param valueMapper
     *            a mapping function to produce values
     * @return a {@code Collector} which collects elements into a
     *         {@code BikeMap} whose keys are the result of applying a key
     *         mapping function to the input elements.
     *
     * @see #toMap(Function, Function, Function, BinaryOperator)
     * @see #toMap(Function, Function, Function, Supplier)
     * @see #toMap(Function, Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R, C, U> Collector<T, ?, BikeyMap<R, C, U>> toMap(
            Function<? super T, ? extends R> rowKeyMapper,
            Function<? super T, ? extends C> columnKeyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return toMap(rowKeyMapper, columnKeyMapper, valueMapper, throwingMerger(), TableBikeyMap::new);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code BikeyMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     *
     * <p>
     * If the mapped bikey (row and column) contains duplicates (according to
     * {@link Object#equals(Object)}), the value mapping function is applied to
     * each equal element, and the results are merged using the provided merging
     * function.
     *
     * <p>
     * The returned {@code Collector} is not concurrent and doesn't support
     * parallel stream pipelines.
     *
     * @param <T>
     *            the type of the input elements
     * @param <R>
     *            row key type of the map keys
     * @param <C>
     *            column key type of the map keys
     * @param <U>
     *            the output type of the value mapping function
     * @param keyMapper
     *            a mapping function to produce bikeys
     * @param valueMapper
     *            a mapping function to produce values
     * @param mapSupplier
     *            a function which returns a new, empty {@code BikeyMap} into
     *            which the results will be inserted
     * @return a {@code Collector} which collects elements into a
     *         {@code BikeMap} whose keys are the result of applying a key
     *         mapping function to the input elements.
     *
     * @see #toMap(Function, Function)
     * @see #toMap(Function, Function, BinaryOperator)
     * @see #toMap(Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R, C, U, M extends BikeyMap<R, C, U>> Collector<T, ?, M> toMap(
            Function<? super T, ? extends Bikey<R, C>> keyMapper,
            Function<? super T, ? extends U> valueMapper,
            Supplier<M> mapSupplier) {
        return toMap(keyMapper, valueMapper, throwingMerger(), mapSupplier);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code BikeyMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     *
     * <p>
     * If the mapped bikey (row and column) contains duplicates (according to
     * {@link Object#equals(Object)}), the value mapping function is applied to
     * each equal element, and the results are merged using the provided merging
     * function.
     *
     * <p>
     * The returned {@code Collector} is not concurrent and doesn't support
     * parallel stream pipelines.
     *
     * @param <T>
     *            the type of the input elements
     * @param <R>
     *            row key type of the map keys
     * @param <C>
     *            column key type of the map keys
     * @param <U>
     *            the output type of the value mapping function
     * @param rowKeyMapper
     *            a mapping function to produce row key
     * @param columnKeyMapper
     *            a mapping function to produce column key
     * @param valueMapper
     *            a mapping function to produce values
     * @param mapSupplier
     *            a function which returns a new, empty {@code BikeyMap} into
     *            which the results will be inserted
     * @return a {@code Collector} which collects elements into a
     *         {@code BikeMap} whose keys are the result of applying a key
     *         mapping function to the input elements.
     *
     * @see #toMap(Function, Function, Function)
     * @see #toMap(Function, Function, Function, BinaryOperator)
     * @see #toMap(Function, Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R, C, U, M extends BikeyMap<R, C, U>> Collector<T, ?, M> toMap(
            Function<? super T, ? extends R> rowKeyMapper,
            Function<? super T, ? extends C> columnKeyMapper,
            Function<? super T, ? extends U> valueMapper,
            Supplier<M> mapSupplier) {
        return toMap(rowKeyMapper, columnKeyMapper, valueMapper, throwingMerger(), mapSupplier);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code BikeyMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     *
     * <p>
     * If the mapped bikey (row and column) contains duplicates (according to
     * {@link Object#equals(Object)}), the value mapping function is applied to
     * each equal element, and the results are merged using the provided merging
     * function. The {@code BikeyMap} is created by a provided supplier
     * function.
     *
     * <p>
     * The returned {@code Collector} is not concurrent and doesn't support
     * parallel stream pipelines.
     *
     * @param <T>
     *            the type of the input elements
     * @param <R>
     *            row key type of the map keys
     * @param <C>
     *            column key type of the map keys
     * @param <U>
     *            the output type of the value mapping function
     * @param keyMapper
     *            a mapping function to produce bikeys
     * @param valueMapper
     *            a mapping function to produce values
     * @param mergeFunction
     *            a merge function, used to resolve collisions between values
     *            associated with the same key, as supplied to
     *            {@link BikeyMap#merge(Object, Object, Object, BiFunction)}
     * @return a {@code Collector} which collects elements into a
     *         {@code BikeMap} whose keys are the result of applying a key
     *         mapping function to the input elements, and whose values are the
     *         result of applying a value mapping function to all input elements
     *         equal to the key and combining them using the merge function
     *
     * @see #toMap(Function, Function)
     * @see #toMap(Function, Function, Supplier)
     * @see #toMap(Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R, C, U> Collector<T, ?, BikeyMap<R, C, U>> toMap(
            Function<? super T, ? extends Bikey<R, C>> keyMapper,
            Function<? super T, ? extends U> valueMapper,
            BinaryOperator<U> mergeFunction) {
        return toMap(keyMapper, valueMapper, mergeFunction, TableBikeyMap::new);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code BikeyMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     *
     * <p>
     * If the mapped bikey (row and column) contains duplicates (according to
     * {@link Object#equals(Object)}), the value mapping function is applied to
     * each equal element, and the results are merged using the provided merging
     * function. The {@code BikeyMap} is created by a provided supplier
     * function.
     *
     * <p>
     * The returned {@code Collector} is not concurrent and doesn't support
     * parallel stream pipelines.
     *
     * @param <T>
     *            the type of the input elements
     * @param <R>
     *            row key type of the map keys
     * @param <C>
     *            column key type of the map keys
     * @param <U>
     *            the output type of the value mapping function
     * @param rowKeyMapper
     *            a mapping function to produce row key
     * @param columnKeyMapper
     *            a mapping function to produce column key
     * @param valueMapper
     *            a mapping function to produce values
     * @param mergeFunction
     *            a merge function, used to resolve collisions between values
     *            associated with the same key, as supplied to
     *            {@link BikeyMap#merge(Object, Object, Object, BiFunction)}
     * @return a {@code Collector} which collects elements into a
     *         {@code BikeMap} whose keys are the result of applying a key
     *         mapping function to the input elements, and whose values are the
     *         result of applying a value mapping function to all input elements
     *         equal to the key and combining them using the merge function
     *
     * @see #toMap(Function, Function, Function)
     * @see #toMap(Function, Function, Function, Supplier)
     * @see #toMap(Function, Function, Function, BinaryOperator, Supplier)
     */
    public static <T, R, C, U> Collector<T, ?, BikeyMap<R, C, U>> toMap(
            Function<? super T, ? extends R> rowKeyMapper,
            Function<? super T, ? extends C> columnKeyMapper,
            Function<? super T, ? extends U> valueMapper,
            BinaryOperator<U> mergeFunction) {
        return toMap(rowKeyMapper, columnKeyMapper, valueMapper, mergeFunction, TableBikeyMap::new);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code BikeyMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     *
     * <p>
     * If the mapped bikey (row and column) contains duplicates (according to
     * {@link Object#equals(Object)}), the value mapping function is applied to
     * each equal element, and the results are merged using the provided merging
     * function. The {@code BikeyMap} is created by a provided supplier
     * function.
     *
     * <p>
     * The returned {@code Collector} is not concurrent and doesn't support
     * parallel stream pipelines.
     *
     * @param <T>
     *            the type of the input elements
     * @param <R>
     *            the output row type of the key mapping function
     * @param <C>
     *            the output column type of the key mapping function
     * @param <U>
     *            the output type of the value mapping function
     * @param <M>
     *            the type of the resulting {@code BikeMap}
     * @param keyMapper
     *            a mapping function to produce bikeys
     * @param valueMapper
     *            a mapping function to produce values
     * @param mergeFunction
     *            a merge function, used to resolve collisions between values
     *            associated with the same key, as supplied to
     *            {@link BikeyMap#merge(Object, Object, Object, BiFunction)}
     * @param mapSupplier
     *            a function which returns a new, empty {@code BikeyMap} into
     *            which the results will be inserted
     * @return a {@code Collector} which collects elements into a
     *         {@code BikeMap} whose keys are the result of applying a key
     *         mapping function to the input elements, and whose values are the
     *         result of applying a value mapping function to all input elements
     *         equal to the key and combining them using the merge function
     *
     * @see #toMap(Function, Function)
     * @see #toMap(Function, Function, Supplier)
     * @see #toMap(Function, Function, BinaryOperator)
     */
    public static <T, R, C, U, M extends BikeyMap<R, C, U>> Collector<T, ?, M> toMap(
            Function<? super T, ? extends Bikey<R, C>> keyMapper,
            Function<? super T, ? extends U> valueMapper,
            BinaryOperator<U> mergeFunction,
            Supplier<M> mapSupplier) {
        BiConsumer<M, T> accumulator = (map, element) -> map.merge(keyMapper.apply(element).getRow(),
                keyMapper.apply(element).getColumn(), valueMapper.apply(element), mergeFunction);
        return Collector.of(mapSupplier, accumulator, mapMerger(mergeFunction),
                Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code BikeyMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     *
     * <p>
     * If the mapped bikey (row and column) contains duplicates (according to
     * {@link Object#equals(Object)}), the value mapping function is applied to
     * each equal element, and the results are merged using the provided merging
     * function. The {@code BikeyMap} is created by a provided supplier
     * function.
     *
     * <p>
     * The returned {@code Collector} is not concurrent and doesn't support
     * parallel stream pipelines.
     *
     * @param <T>
     *            the type of the input elements
     * @param <R>
     *            the output row type of the key mapping function
     * @param <C>
     *            the output column type of the key mapping function
     * @param <U>
     *            the output type of the value mapping function
     * @param <M>
     *            the type of the resulting {@code BikeMap}
     * @param rowKeyMapper
     *            a mapping function to produce row key
     * @param columnKeyMapper
     *            a mapping function to produce column key
     * @param valueMapper
     *            a mapping function to produce values
     * @param mergeFunction
     *            a merge function, used to resolve collisions between values
     *            associated with the same key, as supplied to
     *            {@link BikeyMap#merge(Object, Object, Object, BiFunction)}
     * @param mapSupplier
     *            a function which returns a new, empty {@code BikeyMap} into
     *            which the results will be inserted
     * @return a {@code Collector} which collects elements into a
     *         {@code BikeMap} whose keys are the result of applying a key
     *         mapping function to the input elements, and whose values are the
     *         result of applying a value mapping function to all input elements
     *         equal to the key and combining them using the merge function
     *
     * @see #toMap(Function, Function, Function)
     * @see #toMap(Function, Function, Function, Supplier)
     * @see #toMap(Function, Function, Function, BinaryOperator)
     */
    public static <T, R, C, U, M extends BikeyMap<R, C, U>> Collector<T, ?, M> toMap(
            Function<? super T, ? extends R> rowKeyMapper,
            Function<? super T, ? extends C> columnKeyMapper,
            Function<? super T, ? extends U> valueMapper,
            BinaryOperator<U> mergeFunction,
            Supplier<M> mapSupplier) {
        BiConsumer<M, T> accumulator = (map, element) -> map.merge(rowKeyMapper.apply(element),
                columnKeyMapper.apply(element), valueMapper.apply(element), mergeFunction);
        return Collector.of(mapSupplier, accumulator, mapMerger(mergeFunction),
                Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH);
    }

    /**
     * {@code BinaryOperator<BikeyMap>} that merges the contents of its right
     * BikeyMap into its left BikeyMap, using the provided merge function to
     * handle duplicate keys.
     *
     * @param <R>
     *            row key type of the map keys
     * @param <C>
     *            column key type of the map keys
     * @param <V>
     *            type of the map values
     * @param <M>
     *            type of the map
     * @param mergeFunction
     *            A merge function suitable for
     *            {@link BikeyMap#merge(Object, Object, Object, BiFunction)
     *            BikeyMap.merge()}
     * @return a merge function for two maps
     */
    private static <R, C, V, M extends BikeyMap<R, C, V>> BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            m2.forEach((r, c, v) -> {
                m1.merge(r, c, v, mergeFunction);
            });
            return m1;
        };
    }

    /**
     * Returns a merge function, to use in
     * {@link #toMap(Function, Function, BinaryOperator) toMap()}, which always
     * throws {@code IllegalStateException}. Elements being collected must be
     * distinct.
     *
     * @param <T>
     *            the type of input arguments to the merge function
     * @return a merge function which always throw {@code IllegalStateException}
     */
    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }

}
