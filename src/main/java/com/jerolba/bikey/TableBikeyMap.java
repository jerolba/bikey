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

import static java.util.Objects.requireNonNull;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TableBikeyMap<R, C, V> implements BikeyMap<R, C, V>, Cloneable {

    private Supplier<? extends IntKeyMap<V>> innerMapSupplier;
    private Map<R, IntKeyMap<V>> rows;
    private List<C> columnsValues;
    private Map<C, ColumnInfo> columnIndex;
    private int size = 0;

    public TableBikeyMap() {
        this(() -> new RadixTrie<>());
    }

    public TableBikeyMap(Supplier<? extends IntKeyMap<V>> innerMapSupplier) {
        this.rows = new HashMap<>();
        this.columnsValues = new ArrayList<>();
        this.columnIndex = new HashMap<>();
        this.innerMapSupplier = innerMapSupplier;
    }

    /**
     * Constructs a new {@code TableBikeyMap} with the same mappings as the
     * specified {@code BikeyMap}.
     *
     * @param m
     *            the map whose mappings are to be placed in this map
     * @throws NullPointerException
     *             if the specified map is null
     */
    public TableBikeyMap(BikeyMap<R, C, V> m) {
        this();
        putAll(m);
    }

    @Override
    public V put(R row, C column, V value) {
        requireNonNull(row, "Row can not be null");
        requireNonNull(column, "Column can not be null");
        requireNonNull(value, "Value can not be null");

        IntKeyMap<V> intMap = rows.computeIfAbsent(row, r -> innerMapSupplier.get());
        ColumnInfo columnInfo = columnIndex.get(column);
        if (columnInfo == null) {
            columnInfo = new ColumnInfo(columnsValues.size());
            columnIndex.put(column, columnInfo);
            columnsValues.add(column);
        }
        V prev = intMap.put(columnInfo.index, value);
        if (prev == null) {
            size++;
            columnInfo.inc();
        }
        return prev;
    }

    @Override
    public V get(R row, C column) {
        requireNonNull(row, "Row can not be null");
        requireNonNull(row, "Column can not be null");

        ColumnInfo columnInfo = columnIndex.get(column);
        if (columnInfo != null) {
            IntKeyMap<V> intMap = rows.get(row);
            if (intMap != null) {
                return intMap.get(columnInfo.index);
            }
        }
        return null;
    }

    @Override
    public V remove(R row, C column) {
        requireNonNull(row, "Row can not be null");
        requireNonNull(row, "Column can not be null");

        ColumnInfo columnInfo = columnIndex.get(column);
        if (columnInfo != null) {
            IntKeyMap<V> intMap = rows.get(row);
            if (intMap != null) {
                V prev = intMap.remove(columnInfo.index);
                if (prev != null) {
                    size--;
                    if (intMap.isEmpty()) {
                        rows.remove(row);
                    }
                    columnInfo.dec();
                    if (columnInfo.count == 0) {
                        columnsValues.set(columnInfo.index, null);
                        columnIndex.remove(column);
                    }
                    return prev;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        rows.clear();
        columnsValues.clear();
        columnIndex.clear();
        size = 0;
    }

    /**
     * Returns a shallow copy of this <tt>TableBikeyMap</tt> instance: the
     * elements themselves are not cloned.
     *
     * @return a shallow copy of this set
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            TableBikeyMap<R, C, V> newMap = (TableBikeyMap<R, C, V>) super.clone();
            newMap.columnsValues = new ArrayList<>(this.columnsValues);
            newMap.columnIndex = new HashMap<>(this.columnIndex.size());
            this.columnIndex.forEach((col, index) -> {
                newMap.columnIndex.put(col, index.clone());
            });
            newMap.rows = new HashMap<>(this.rows.size());
            this.rows.forEach((row, innerMap) -> {
                IntKeyMap<V> newOne = innerMapSupplier.get();
                newOne.putAll(innerMap);
                newMap.rows.put(row, newOne);
            });
            return newMap;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        requireNonNull(value, "Value can not be null");
        for (IntKeyMap<V> row : rows.values()) {
            if (row.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsRow(Object row) {
        requireNonNull(row, "row can not be null");
        IntKeyMap<V> intKeyMap = rows.get(row);
        return intKeyMap != null && intKeyMap.size() > 0;
    }

    @Override
    public boolean containsColumn(Object column) {
        requireNonNull(column, "column can not be null");
        ColumnInfo columnInfo = columnIndex.get(column);
        return columnInfo != null && columnInfo.count > 0;
    }

    @Override
    public Set<Bikey<R, C>> keySet() {
        return new KeySet();
    }

    @Override
    public BikeySet<R, C> bikeySet() {
        BikeySet<R, C> result = new TableBikeySet<>();
        forEachBikey(result::add);
        return result;
    }

    @Override
    public Collection<V> values() {
        return new Values();
    }

    @Override
    public Set<R> rowKeySet() {
        return rows.keySet();
    }

    @Override
    public Set<C> columnKeySet() {
        return columnIndex.keySet();
    }

    @Override
    public Set<BikeyEntry<R, C, V>> entrySet() {
        return new EntrySet();
    }

    @Override
    public void forEachBikey(BiConsumer<? super R, ? super C> action) {
        requireNonNull(action);
        rows.entrySet().forEach(entry -> {
            IntKeyMap<V> intKeyMap = entry.getValue();
            if (!intKeyMap.isEmpty()) {
                R r = entry.getKey();
                intKeyMap.forEachKey(key -> action.accept(r, columnsValues.get(key)));
            }
        });
    }

    @Override
    public void forEach(TriConsumer<? super R, ? super C, ? super V> action) {
        requireNonNull(action);
        rows.entrySet().forEach(entry -> {
            IntKeyMap<V> intKeyMap = entry.getValue();
            if (!intKeyMap.isEmpty()) {
                R r = entry.getKey();
                intKeyMap.forEach((idx, v) -> action.accept(r, columnsValues.get(idx), v));
            }
        });
    }

    @Override
    public Iterator<BikeyEntry<R, C, V>> iterator() {
        if (isEmpty()) {
            return Collections.emptyIterator();
        }
        return new BikeyMapEntryIterator();
    }

    private class BikeyMapIterator {

        private final Iterator<Entry<R, IntKeyMap<V>>> rowsIterator = rows.entrySet().iterator();
        private Iterator<IntObjectEntry<V>> columnsIterator = null;

        private R currentRow = null;

        BikeyMapIterator() {
            moveToNextRow();
        }

        public boolean hasNextIt() {
            return columnsIterator != null && columnsIterator.hasNext();
        }

        public BikeyEntry<R, C, V> nextIt() {
            IntObjectEntry<V> next = columnsIterator.next();
            BikeyEntry<R, C, V> res = new SimpleBikeyEntry<>(currentRow, columnsValues.get(
                    next.getIntKey()), next.getValue());
            if (!columnsIterator.hasNext()) {
                moveToNextRow();
            }
            return res;
        }

        private void moveToNextRow() {
            if (rowsIterator.hasNext()) {
                Entry<R, IntKeyMap<V>> next = rowsIterator.next();
                currentRow = next.getKey();
                columnsIterator = next.getValue().iterator();
            } else {
                columnsIterator = null;
            }
        }
    }

    private final class Values extends AbstractCollection<V> {

        @Override
        public int size() {
            return TableBikeyMap.this.size();
        }

        @Override
        public void clear() {
            TableBikeyMap.this.clear();
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
            return Spliterators.spliterator(iterator(), size(),
                    Spliterator.DISTINCT + Spliterator.NONNULL + Spliterator.SIZED);
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            requireNonNull(action);
            rows.values().forEach(rowValue -> rowValue.values().forEach(action));
        }

    }

    private final class KeySet extends AbstractSet<Bikey<R, C>> {

        @Override
        public int size() {
            return TableBikeyMap.this.size();
        }

        @Override
        public void clear() {
            TableBikeyMap.this.clear();
        }

        @Override
        public Iterator<Bikey<R, C>> iterator() {
            if (isEmpty()) {
                return Collections.emptyIterator();
            }
            return new KeyIterator();
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            requireNonNull(o, "Value can not be null");
            Bikey<R, C> key = (Bikey<R, C>) o;
            return containsKey(key.getRow(), key.getColumn());
        }

        @Override
        public Spliterator<Bikey<R, C>> spliterator() {
            return Spliterators.spliterator(iterator(), size(),
                    Spliterator.DISTINCT + Spliterator.NONNULL + Spliterator.SIZED);
        }

        @Override
        public void forEach(Consumer<? super Bikey<R, C>> action) {
            requireNonNull(action);
            TableBikeyMap.this.forEachBikey((r, c) -> action.accept(new BikeyImpl<>(r, c)));
        }

    }

    private final class EntrySet extends AbstractSet<BikeyEntry<R, C, V>> {

        @Override
        public int size() {
            return TableBikeyMap.this.size();
        }

        @Override
        public void clear() {
            TableBikeyMap.this.clear();
        }

        @Override
        public Iterator<BikeyEntry<R, C, V>> iterator() {
            return TableBikeyMap.this.iterator();
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            requireNonNull(o, "Value can not be null");
            BikeyEntry<R, C, V> key = (BikeyEntry<R, C, V>) o;
            V value = get(key.getRow(), key.getColumn());
            return (value != null && value.equals(key.getValue()));
        }

        @Override
        public Spliterator<BikeyEntry<R, C, V>> spliterator() {
            return TableBikeyMap.this.spliterator();
        }

        @Override
        public void forEach(Consumer<? super BikeyEntry<R, C, V>> action) {
            TableBikeyMap.this.forEach((r, c, v) -> action.accept(new SimpleBikeyEntry<>(r, c, v)));
        }

    }

    private final class BikeyMapEntryIterator extends BikeyMapIterator implements Iterator<BikeyEntry<R, C, V>> {

        @Override
        public boolean hasNext() {
            return hasNextIt();
        }

        @Override
        public BikeyEntry<R, C, V> next() {
            return nextIt();
        }

    }

    private final class ValueIterator extends BikeyMapIterator implements Iterator<V> {

        @Override
        public boolean hasNext() {
            return hasNextIt();
        }

        @Override
        public V next() {
            return nextIt().getValue();
        }

    }

    private final class KeyIterator extends BikeyMapIterator implements Iterator<Bikey<R, C>> {

        @Override
        public boolean hasNext() {
            return hasNextIt();
        }

        @Override
        public Bikey<R, C> next() {
            return nextIt();
        }

    }

    @Override
    public int hashCode() {
        int h = 0;
        for (BikeyEntry<R, C, V> entry : entrySet()) {
            h += entry.hashCode();
        }
        return h;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof BikeyMap)) {
            return false;
        }
        BikeyMap<R, C, V> m = (BikeyMap<R, C, V>) o;
        if (m.size() != size()) {
            return false;
        }
        try {
            for (BikeyEntry<R, C, V> e : entrySet()) {
                R row = e.getRow();
                C column = e.getColumn();
                V value = e.getValue();
                if (!value.equals(m.get(row, column))) {
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
    public String toString() {
        Iterator<BikeyEntry<R, C, V>> i = entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            BikeyEntry<R, C, V> e = i.next();
            R row = e.getRow();
            C column = e.getColumn();
            V value = e.getValue();
            sb.append("[");
            sb.append(row == this ? "(this Map)" : row);
            sb.append(',').append(' ');
            sb.append(column == this ? "(this Map)" : column);
            sb.append("]");
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',').append(' ');
        }
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

        @Override
        public ColumnInfo clone() {
            ColumnInfo newOne = new ColumnInfo(this.index);
            newOne.count = this.count;
            return newOne;
        }
    }

    static class SimpleBikeyEntry<R, C, V> implements BikeyEntry<R, C, V> {

        private final R row;
        private final C column;
        private final V value;

        SimpleBikeyEntry(R row, C column, V value) {
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
                if (other.getRow() != null) {
                    return false;
                }
            } else if (!row.equals(other.getRow())) {
                return false;
            }
            if (column == null) {
                if (other.getColumn() != null) {
                    return false;
                }
            } else if (!column.equals(other.getColumn())) {
                return false;
            }
            if (value == null) {
                if (other.getValue() != null) {
                    return false;
                }
            } else if (!value.equals(other.getValue())) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "[" + row + ", " + column + "]=" + value;
        }

    }
}
