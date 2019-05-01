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
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Implementation of a <tt>Map<Integer, V></tt> where the keys values are
 * límited to [0, maxSize) range.
 *
 * <p>
 * references to values >= maxSize can raise an
 * {@link ArrayIndexOutOfBoundsException}
 *
 * @param <V>
 *            type of the value associated in the map
 */

@SuppressWarnings("unchecked")
class IntArrayMap<V> implements IntKeyMap<V>, Cloneable {

    private static final int DEFAULT_CAPACITY = 10;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private int minCapacity;
    private int size = 0;
    private Object[] array;
    private int maxIndex = -1;

    IntArrayMap(int minCapacity) {
        this.minCapacity = minCapacity;
        this.array = new Object[minCapacity];
    }

    IntArrayMap() {
        this.minCapacity = DEFAULT_CAPACITY;
        this.array = new Object[minCapacity];
    }

    IntArrayMap(IntArrayMap<V> m) {
        this.minCapacity = m.minCapacity;
        this.size = m.size;
        this.maxIndex = m.maxIndex;
        this.array = new Object[m.array.length];
        System.arraycopy(m.array, 0, array, 0, m.array.length);
    }

    /**
     * Constructs a new {@code IntArrayMap} with the same mappings as the
     * specified {@code IntKeyMap}.
     *
     * <p>
     * The new Map will have as size the max key value + 1. To get the max key
     * value, all key set is iterated.
     *
     * @param m
     *            the map whose mappings are to be placed in this map
     * @throws NullPointerException
     *             if the specified map is null
     */
    IntArrayMap(IntKeyMap<V> m) {
        this.minCapacity = DEFAULT_CAPACITY;
        int maxValue = 0;
        for (int i : m.keySet()) {
            if (i > maxValue) {
                maxValue = i;
            }
        }
        this.array = new Object[maxValue + 1];
        m.forEach(this::put);
    }

    @Override
    public Iterator<IntObjectEntry<V>> iterator() {
        if (isEmpty()) {
            return Collections.emptyIterator();
        }
        return new EntryIterator();
    }

    @Override
    public V put(int key, V value) {
        Objects.requireNonNull(value);
        if (key >= array.length) {
            int newCapacity = growCapacity(array.length, key + 1);
            Object[] newArray = new Object[newCapacity];
            System.arraycopy(array, 0, newArray, 0, array.length);
            array = newArray;
            array[key] = value;
            size++;
            maxIndex = key;
            return null;
        }
        Object previous = array[key];
        array[key] = value;
        if (previous == null) {
            size++;
        }
        if (key > maxIndex) {
            maxIndex = key;
        }
        return (V) previous;
    }

    static int growCapacity(int currentCapacity, int neededCapacity) {
        int newCapacity = currentCapacity + (currentCapacity >> 1);
        if (newCapacity < neededCapacity) {
            int fraction = (neededCapacity + 1) / currentCapacity;
            newCapacity = (fraction + 1) * currentCapacity;
        }
        if (newCapacity < 0 || newCapacity >= MAX_ARRAY_SIZE) {
            throw new OutOfMemoryError();
        }
        return newCapacity;
    }

    @Override
    public V get(int key) {
        if (key > maxIndex) {
            return null;
        }
        return (V) array[key];
    }

    @Override
    public V remove(int key) {
        if (key > maxIndex) {
            return null;
        }
        Object previous = array[key];
        if (previous != null) {
            size--;
            array[key] = null;
        }
        return (V) previous;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        size = 0;
        array = new Object[array.length];
    }

    @Override
    public void forEach(IntObjectConsumer<V> action) {
        Objects.requireNonNull(action);
        for (int i = 0; i <= maxIndex; i++) {
            Object v = array[i];
            if (v != null) {
                action.accept(i, (V) v);
            }
        }
    }

    @Override
    public void forEachKey(IntConsumer action) {
        Objects.requireNonNull(action);
        for (int i = 0; i <= maxIndex; i++) {
            if (array[i] != null) {
                action.accept(i);
            }
        }
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i <= maxIndex; i++) {
            Object v = array[i];
            if (v != null && v.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<V> values() {
        return new Values();
    }

    @Override
    public Set<Integer> keySet() {
        return new KeySet();
    }

    @Override
    public Set<IntObjectEntry<V>> entrySet() {
        return new EntrySet();
    }

    /**
     * Abstract implementation of {@code Iterator<IntObjectEntry<T>>} which is
     * extended later by {@link EntryIterator} {@link ValueIterator} and
     * {@link KeyIterator}.
     */
    private abstract class IntArrayIterator {

        private int currentIt = -1;

        IntArrayIterator() {
            moveToNextItem();
        }

        private void moveToNextItem() {
            currentIt++;
            while (currentIt <= maxIndex && array[currentIt] == null) {
                currentIt++;
            }
        }

        public boolean hasNextIt() {
            return currentIt <= maxIndex;
        }

        public IntObjectEntry<V> nextIt() {
            if (!hasNextIt()) {
                throw new NoSuchElementException();
            }
            IntObjectEntry<V> res = new IntObjectEntry<>(currentIt, (V) array[currentIt]);
            moveToNextItem();
            return res;
        }

    }

    private final class Values extends AbstractCollection<V> {

        @Override
        public int size() {
            return IntArrayMap.this.size();
        }

        @Override
        public void clear() {
            IntArrayMap.this.clear();
        }

        @Override
        public Iterator<V> iterator() {
            if (isEmpty()) {
                return Collections.emptyIterator();
            }
            return new ValueIterator();
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @Override
        public Spliterator<V> spliterator() {
            return Spliterators.spliterator(this.iterator(), size, Spliterator.NONNULL + Spliterator.SIZED);
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            Objects.requireNonNull(action);
            IntArrayMap.this.forEach((key, value) -> action.accept(value));
        }

    }

    private final class KeySet extends AbstractSet<Integer> {

        @Override
        public int size() {
            return IntArrayMap.this.size();
        }

        @Override
        public void clear() {
            IntArrayMap.this.clear();
        }

        @Override
        public Iterator<Integer> iterator() {
            if (isEmpty()) {
                return Collections.emptyIterator();
            }
            return new KeyIterator();
        }

        @Override
        public boolean contains(Object o) {
            return containsKey((Integer) o);
        }

        @Override
        public Spliterator<Integer> spliterator() {
            return Spliterators.spliterator(this.iterator(), size, Spliterator.NONNULL + Spliterator.SIZED);
        }

        @Override
        public void forEach(Consumer<? super Integer> action) {
            Objects.requireNonNull(action);
            IntArrayMap.this.forEachKey(key -> action.accept(key));
        }

    }

    private final class EntrySet extends AbstractSet<IntObjectEntry<V>> {

        @Override
        public int size() {
            return IntArrayMap.this.size();
        }

        @Override
        public void clear() {
            IntArrayMap.this.clear();
        }

        @Override
        public Iterator<IntObjectEntry<V>> iterator() {
            if (isEmpty()) {
                return Collections.emptyIterator();
            }
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            Objects.requireNonNull(o, "Value can not be null");
            IntObjectEntry<V> key = (IntObjectEntry<V>) o;
            V value = get(key.getIntKey());
            return (value != null && value.equals(key.getValue()));
        }

        @Override
        public Spliterator<IntObjectEntry<V>> spliterator() {
            return Spliterators.spliterator(this.iterator(), size, Spliterator.NONNULL + Spliterator.SIZED);
        }

        @Override
        public void forEach(Consumer<? super IntObjectEntry<V>> action) {
            Objects.requireNonNull(action);
            IntArrayMap.this.forEach(action);
        }

    }

    private final class EntryIterator extends IntArrayIterator implements Iterator<IntObjectEntry<V>> {

        @Override
        public boolean hasNext() {
            return hasNextIt();
        }

        @Override
        public IntObjectEntry<V> next() {
            return nextIt();
        }

    }

    private final class ValueIterator extends IntArrayIterator implements Iterator<V> {

        @Override
        public boolean hasNext() {
            return hasNextIt();
        }

        @Override
        public V next() {
            return nextIt().getValue();
        }

    }

    private final class KeyIterator extends IntArrayIterator implements Iterator<Integer> {

        @Override
        public boolean hasNext() {
            return hasNextIt();
        }

        @Override
        public Integer next() {
            return nextIt().getIntKey();
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IntKeyMap)) {
            return false;
        }
        IntKeyMap<?> m = (IntKeyMap<?>) o;
        if (m.size() != size()) {
            return false;
        }
        if (o instanceof IntArrayMap) {
            IntArrayMap<?> other = (IntArrayMap<?>) o;
            for (int i = 0; i <= maxIndex; i++) {
                if (!Objects.equals(array[i], other.array[i])) {
                    return false;
                }
            }
            return true;
        }

        try {
            for (IntObjectEntry<V> e : entrySet()) {
                int key = e.getIntKey();
                V value = e.getValue();
                if (!value.equals(m.get(key))) {
                    return false;
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        Iterator<IntObjectEntry<V>> it = iterator();
        while (it.hasNext()) {
            hash += it.next().hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        Iterator<IntObjectEntry<V>> i = iterator();
        if (!i.hasNext()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            IntObjectEntry<V> e = i.next();
            sb.append(e.getIntKey());
            sb.append('=');
            V value = e.getValue();
            sb.append(value == this ? "(this Map)" : value);
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',').append(' ');
        }
    }

    /**
     * Returns a shallow copy of this <tt>IntArrayMap</tt> instance: the
     * elements themselves are not cloned.
     *
     * @return a shallow copy of this map
     */
    @Override
    public Object clone() {
        return new IntArrayMap<>(this);
    }

}