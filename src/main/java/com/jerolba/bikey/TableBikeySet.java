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
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TableBikeySet<R, C> extends AbstractSet<Bikey<R, C>> implements BikeySet<R, C>, Cloneable {

    private Map<R, BitSet> valuesInRow;
    private List<C> columnsValues;
    private Map<C, ColumnInfo> columnIndex;
    private int size = 0;

    /**
     * Constructs a new, empty set
     */
    public TableBikeySet() {
        this.valuesInRow = new HashMap<>();
        this.columnsValues = new ArrayList<>();
        this.columnIndex = new HashMap<>();
    }

    /**
     * Constructs a new set containing the elements in the specified set.
     *
     * @param bikeySet
     *            the collection whose elements are to be placed into this set
     * @throws NullPointerException
     *             if the specified collection is null
     */
    public TableBikeySet(BikeySet<? extends R, ? extends C> bikeySet) {
        this();
        bikeySet.forEach((r, c) -> this.add(r, c));
    }

    @Override
    public boolean add(R row, C column) {
        Objects.requireNonNull(row, "Row can not be null");
        Objects.requireNonNull(column, "Column can not be null");
        BitSet bitSet = valuesInRow.computeIfAbsent(row, st -> new BitSet());
        ColumnInfo columnInfo = columnIndex.get(column);
        if (columnInfo == null) {
            columnInfo = new ColumnInfo(columnsValues.size());
            columnIndex.put(column, columnInfo);
            columnsValues.add(column);
        }
        if (!bitSet.get(columnInfo.index)) {
            bitSet.set(columnInfo.index);
            columnInfo.inc();
            size++;
            return true;
        }
        return false;
    }

    @Override
    public boolean add(Bikey<R, C> key) {
        Objects.requireNonNull(key, "Key can not be null");
        return add(key.getRow(), key.getColumn());
    }

    @Override
    public boolean remove(R row, C column) {
        Objects.requireNonNull(row, "Row can not be null");
        Objects.requireNonNull(column, "Column can not be null");

        BitSet bitSet = valuesInRow.get(row);
        if (bitSet != null) {
            ColumnInfo columnInfo = columnIndex.get(column);
            if (columnInfo != null && bitSet.get(columnInfo.index)) {
                bitSet.clear(columnInfo.index);
                size--;
                if (bitSet.isEmpty()) {
                    valuesInRow.remove(row);
                }
                columnInfo.dec();
                if (columnInfo.count == 0) {
                    columnsValues.set(columnInfo.index, null);
                    columnIndex.remove(column);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        Objects.requireNonNull(o, "Object can not be null");
        Bikey<? extends R, ? extends C> key = (Bikey<? extends R, ? extends C>) o;
        return remove(key.getRow(), key.getColumn());
    }

    @Override
    public Set<R> rowKeySet() {
        return valuesInRow.keySet();
    }

    public Set<C> columnKeySet() {
        return columnIndex.keySet();
    }

    @Override
    public boolean contains(R row, C column) {
        Objects.requireNonNull(row, "Row can not be null");
        Objects.requireNonNull(column, "Column can not be null");

        BitSet bitSet = valuesInRow.get(row);
        if (bitSet != null) {
            ColumnInfo columnInfo = columnIndex.get(column);
            if (columnInfo != null) {
                return bitSet.get(columnInfo.index);
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        Objects.requireNonNull(o, "Object can not be null");
        Bikey<? extends R, ? extends C> key = (Bikey<? extends R, ? extends C>) o;
        return contains(key.getRow(), key.getColumn());
    }

    @Override
    public void forEach(BiConsumer<? super R, ? super C> action) {
        Objects.requireNonNull(action);
        valuesInRow.entrySet().forEach(entry -> {
            R row = entry.getKey();
            BitSet valuesInRow = entry.getValue();
            for (int idx = valuesInRow.nextSetBit(0); idx >= 0; idx = valuesInRow.nextSetBit(idx + 1)) {
                C column = columnsValues.get(idx);
                action.accept(row, column);
            }
        });
    }

    @Override
    public void forEach(Consumer<? super Bikey<R, C>> action) {
        forEach((r, c) -> action.accept(new Bikey<>(r, c)));
    }

    /**
     * Returns an iterator over the elements in this set. The elements are
     * returned in no particular order.
     *
     * @return an iterator over the pair of elements in this set
     */
    @Override
    public Iterator<Bikey<R, C>> iterator() {
        if (isEmpty()) {
            return Collections.emptyIterator();
        }
        return new BikeySetIterator();
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     *
     * @return the number of elements in this set (its cardinality)
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Removes all of the elements from this set. The set will be empty after
     * this call returns.
     */
    @Override
    public void clear() {
        valuesInRow.clear();
        columnsValues.clear();
        columnIndex.clear();
        size = 0;
    }

    /**
     * Returns a shallow copy of this <tt>TableBikeySet</tt> instance: the
     * elements themselves are not cloned.
     *
     * @return a shallow copy of this set
     */
    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            TableBikeySet<R, C> newSet = (TableBikeySet<R, C>) super.clone();
            newSet.columnIndex = new HashMap<>(this.columnIndex);
            newSet.columnsValues = new ArrayList<>(this.columnsValues);
            newSet.valuesInRow = new HashMap<>(this.valuesInRow.size());
            this.valuesInRow.forEach((row, bitSet) -> newSet.valuesInRow.put(row, (BitSet) bitSet.clone()));
            return newSet;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    private class BikeySetIterator implements Iterator<Bikey<R, C>> {

        private final Iterator<Entry<R, BitSet>> rowsIterator;
        private R currentRowKeyValue = null;
        private BitSet currentBitSet = null;
        private int bitSetIterator = -1;

        BikeySetIterator() {
            this.rowsIterator = valuesInRow.entrySet().iterator();
            iterateMap();
        }

        @Override
        public boolean hasNext() {
            return bitSetIterator != -1;
        }

        @Override
        public Bikey<R, C> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            C column = columnsValues.get(bitSetIterator);
            Bikey<R, C> bikey = new Bikey<>(currentRowKeyValue, column);
            bitSetIterator = currentBitSet.nextSetBit(bitSetIterator + 1);
            if (bitSetIterator == -1) {
                iterateMap();
            }
            return bikey;
        }

        private void iterateMap() {
            if (!rowsIterator.hasNext()) {
                return;
            }
            Entry<R, BitSet> next = rowsIterator.next();
            currentRowKeyValue = next.getKey();
            currentBitSet = next.getValue();
            bitSetIterator = currentBitSet.nextSetBit(0);
            // In theory no bitset is empty
            assert (bitSetIterator >= 0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TableBikeySet)) {
            return false;
        }
        TableBikeySet<?, ?> set = (TableBikeySet<?, ?>) o;
        if (set.size() != size()) {
            return false;
        }
        if (!this.valuesInRow.keySet().equals(set.valuesInRow.keySet())) {
            return false;
        }
        if (!this.columnIndex.keySet().equals(set.columnIndex.keySet())) {
            return false;
        }
        try {
            return containsAll(set);
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        Iterator<Bikey<R, C>> it = iterator();
        while (it.hasNext()) {
            hash += it.next().hashCode();
        }
        return hash;
    }

    private static class ColumnInfo {

        private int index;
        private int count = 0;

        ColumnInfo(int index) {
            this.index = index;
        }

        public void inc() {
            this.count++;
        }

        public void dec() {
            this.count--;
        }

        public ColumnInfo clone() {
            ColumnInfo newOne = new ColumnInfo(this.index);
            newOne.count = this.count;
            return newOne;
        }
    }

}
