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
 * Implements a radix hash array mapped trie data structure, with radix=32
 *
 * <p>
 * The key is a primitive <tt>int</tt> and allows all range of integer values.
 *
 * <p>
 * The data structure is optimal when keys are grouped around a limited range of
 * values with low dispersion. The map is designed to store values associated to
 * a set of bounded int keys.
 *
 * <p>
 * Can be used as a map with integers as key ({@code Map<Integer, T>}) or a
 * compact unbounded integer array (Integer[T])
 *
 * <p>
 * The implementation uses a Radix HAMT over int keys. Each node of the HAMT has
 * 5 bits.
 *
 * <p>
 * Iteration over its elements is done in preorder and it is sorted in
 * ascending.
 *
 * @param <V>
 *            Type of the associated element
 */
public class RadixHamTrie<V> implements IntKeyMap<V>, Cloneable {

    private static final int BIT_SIZE = 5;
    private static final int ARR_SIZE = 1 << BIT_SIZE;
    private static final int BIT_SHIFT = ARR_SIZE - BIT_SIZE;
    private static final int MASK_PATH = 0xFFFFFFFF << BIT_SIZE;
    private static final int BIT_MASK = 0xFFFFFFFF >>> BIT_SHIFT;

    private static final int L1 = 0xFFFFFFFF << BIT_SIZE * 2;
    private static final int L2 = 0xFFFFFFFF << BIT_SIZE * 3;
    private static final int L3 = 0xFFFFFFFF << BIT_SIZE * 4;
    private static final int L4 = 0xFFFFFFFF << BIT_SIZE * 5;
    private static final int L5 = 0xFFFFFFFF << BIT_SIZE * 6;

    private RadixHamTrieNode root;
    private int size = 0;

    /**
     * Constructs an empty {@code RadixHamTrie}
     */
    public RadixHamTrie() {
    }

    /**
     * Constructs a new {@code RadixHamTrie} with the same mappings as the
     * specified {@code IntKeyMap}.
     *
     * @param m
     *            the map whose mappings are to be placed in this map
     * @throws NullPointerException
     *             if the specified map is null
     */
    public RadixHamTrie(IntKeyMap<? extends V> m) {
        this();
        putAll(m);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V put(int key, V value) {
        Objects.requireNonNull(value, "Value can not be null");
        if (root == null) {
            root = newLeafNode(key, value);
            return incSize();
        }
        RadixHamTrieNode previousNode = null;
        int previousIndex = 0;
        RadixHamTrieNode currentNode = root;
        for (;;) {
            int numberNonPrefixBits = currentNode.getNumberNonPrefixBits() + BIT_SIZE;
            int currentNodePrefixBits = currentNode.getPrefixBits();
            int keyPrefixBits = getKeyPrefixBits(key, numberNonPrefixBits);
            if (keyPrefixBits != currentNodePrefixBits) {
                int numberOfBitsInXor = nonPrefixBitsSharedInXor(keyPrefixBits ^ currentNodePrefixBits);
                RadixHamTrieNode parentNode = currentNode.createParentNodeWith(key, value, numberOfBitsInXor);
                if (previousNode == null) {
                    root = parentNode;
                } else {
                    previousNode.set(previousIndex, parentNode);
                }
                return incSize();
            }
            if (isLeafNode(numberNonPrefixBits)) {
                return incSize((V) currentNode.set(key & BIT_MASK, value));
            }
            int idx = getIdxInNode(key, numberNonPrefixBits);
            RadixHamTrieNode nextNode = (RadixHamTrieNode) currentNode.get(idx);
            if (nextNode == null) {
                currentNode.set(idx, newLeafNode(key, value));
                return incSize();
            }
            previousNode = currentNode;
            previousIndex = idx;
            currentNode = nextNode;
        }
    }

    private static int nonPrefixBitsSharedInXor(int value) {
        if ((value & L1) == 0) {
            return BIT_SIZE * 1;
        }
        if ((value & L2) == 0) {
            return BIT_SIZE * 2;
        }
        if ((value & L3) == 0) {
            return BIT_SIZE * 3;
        }
        if ((value & L4) == 0) {
            return BIT_SIZE * 4;
        }
        if ((value & L5) == 0) {
            return BIT_SIZE * 5;
        }
        return 30;
    }

    private V incSize() {
        size++;
        return null;
    }

    private V incSize(V previousValue) {
        if (previousValue == null) {
            size++;
        }
        return previousValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(int key) {
        RadixHamTrieNode currentNode = root;
        while (currentNode != null) {
            int numberNonPrefixBits = currentNode.getNumberNonPrefixBits()  + BIT_SIZE;
            if (getKeyPrefixBits(key, numberNonPrefixBits) != currentNode.getPrefixBits()) {
                return null;
            }
            if (isLeafNode(numberNonPrefixBits)) {
                return (V) currentNode.get(key & BIT_MASK);
            }
            currentNode = (RadixHamTrieNode) currentNode.get(getIdxInNode(key, numberNonPrefixBits));
        }
        return null;
    }

    @Override
    public V remove(int key) {
        if (root == null) {
            return null;
        }
        V removed = remove(root, key);
        if (removed != null) {
            if (root.isEmpty()) {
                root = null;
            }
            size--;
        }
        return removed;
    }

    @SuppressWarnings("unchecked")
    private V remove(RadixHamTrieNode currentNode, int key) {
        int numberNonPrefixBits = currentNode.getNumberNonPrefixBits() + BIT_SIZE;
        if (getKeyPrefixBits(key, numberNonPrefixBits) != currentNode.getPrefixBits()) {
            return null;
        }
        if (isLeafNode(numberNonPrefixBits)) {
            return (V) currentNode.remove(key & BIT_MASK);
        }
        int idxInNode = getIdxInNode(key, numberNonPrefixBits);
        RadixHamTrieNode nextNode = (RadixHamTrieNode) currentNode.get(idxInNode);
        if (nextNode == null) {
            return null;
        }
        V removed = remove(nextNode, key);
        if (removed != null) {
            if (nextNode.isEmpty()) {
                currentNode.remove(idxInNode);
            }
            // TODO: if size is 1 and is not leaf collapse it
        }
        return removed;
    }

    private static int getKeyPrefixBits(int key, int numberNonPrefixBits) {
        if (numberNonPrefixBits < ARR_SIZE) {
            return key & (0xFFFFFFFF << numberNonPrefixBits);
        }
        return 0;
    }

    static RadixHamTrieNode newLeafNode(int key, Object value) {
        RadixHamTrieNode node = new RadixHamTrieNode(key & MASK_PATH, 0);
        node.bitmap = 1 << (key & BIT_MASK);
        node.arr = new Object[1];
        node.arr[0] = value;
        return node;
    }

    private static boolean isLeafNode(int numberNonPrefixBits) {
        return numberNonPrefixBits == BIT_SIZE;
    }

    private static int getIdxInNode(int key, int numberNonPrefixBits) {
        return (key >>> (numberNonPrefixBits - BIT_SIZE)) & BIT_MASK;
    }

    @Override
    public void forEachKey(IntConsumer action) {
        Objects.requireNonNull(action);
        forEachKey(root, action);
    }

    private static void forEachKey(RadixHamTrieNode node, IntConsumer action) {
        if (node.isLeaf()) {
            node.forEachKey(action);
        } else {
            node.forEach((idx, value) -> forEachKey((RadixHamTrieNode) value, action));
        }
    }

    @Override
    public boolean containsValue(Object value) {
        Objects.requireNonNull(value, "Value can not be null");
        return containsValue(root, value);
    }

    private static boolean containsValue(RadixHamTrieNode node, Object value) {
        if (node.isLeaf()) {
            for (IntObjectEntry<?> e : node) {
                if (e.getValue().equals(value)) {
                    return true;
                }
            }
        } else {
            for (IntObjectEntry<?> e : node) {
                if (containsValue((RadixHamTrieNode) e.getValue(), value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Returns a shallow copy of this <tt>RadixHamTrie</tt> instance: the
     * elements themselves are not cloned.
     *
     * @return a shallow copy of this map
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            RadixHamTrie<V> newMap = (RadixHamTrie<V>) super.clone();
            forEach(newMap::put);
            return newMap;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public void forEach(IntObjectConsumer<V> action) {
        Objects.requireNonNull(action);
        forEach(root, action);
    }

    @SuppressWarnings("unchecked")
    private void forEach(RadixHamTrieNode node, IntObjectConsumer<V> action) {
        if (node.isLeaf()) {
            node.forEach((idx, value) -> action.accept(idx, (V) value));
        } else {
            node.forEachValue(value -> forEach((RadixHamTrieNode) value, action));
        }
    }

    @Override
    public void forEach(Consumer<? super IntObjectEntry<V>> action) {
        Objects.requireNonNull(action);
        forEach((i, t) -> action.accept(new IntObjectEntry<>(i, t)));
    }

    @Override
    public Iterator<IntObjectEntry<V>> iterator() {
        if (isEmpty()) {
            return Collections.emptyIterator();
        }
        return new EntryIterator();
    }

    @Override
    public Spliterator<IntObjectEntry<V>> spliterator() {
        return Spliterators.spliterator(this.iterator(), this.size,
                Spliterator.DISTINCT + Spliterator.NONNULL + Spliterator.SIZED);
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
    private abstract class IntHamtIterator {

        private IteratorLevel currentLeaf = null;

        IntHamtIterator() {
            IteratorLevel rootLevel = new IteratorLevel(root, null);
            currentLeaf = rootLevel.fillStack();
        }

        public boolean hasNextIt() {
            return currentLeaf != null && currentLeaf.hasNext();
        }

        public IntObjectEntry<V> nextIt() {
            if (!hasNextIt()) {
                throw new NoSuchElementException();
            }
            IntObjectEntry<V> res = currentLeaf.getNextValue();
            if (!currentLeaf.hasNext()) {
                currentLeaf = popAndFillStackToNext();
            }
            return res;
        }

        private IteratorLevel popAndFillStackToNext() {
            currentLeaf = currentLeaf.parent;
            if (currentLeaf == null) {
                return null;
            }
            if (currentLeaf.hasNext()) {
                return currentLeaf.fillStackFromLevel();
            }
            return popAndFillStackToNext();
        }

        private class IteratorLevel {
            private IteratorLevel parent;
            private Iterator<IntObjectEntry<?>> childNodeIterator;
            private boolean isLeaf;

            IteratorLevel(RadixHamTrieNode node, IteratorLevel parent) {
                this.isLeaf = node.isLeaf();
                this.parent = parent;
                this.childNodeIterator = node.iterator();
            }

            public IteratorLevel fillStackFromLevel() {
                IntObjectEntry<?> nextChild = childNodeIterator.next();
                RadixHamTrieNode childNode = (RadixHamTrieNode) nextChild.getValue();
                IteratorLevel lowerLevel = new IteratorLevel(childNode, this);
                return lowerLevel.fillStack();
            }

            private IteratorLevel fillStack() {
                return (isLeaf) ? this : fillStackFromLevel();
            }

            @SuppressWarnings("unchecked")
            public IntObjectEntry<V> getNextValue() {
                IntObjectEntry<?> nextChild = childNodeIterator.next();
                return new IntObjectEntry<>(nextChild.getIntKey(), (V) nextChild.getValue());
            }

            public boolean hasNext() {
                return childNodeIterator.hasNext();
            }
        }

    }

    private final class Values extends AbstractCollection<V> {

        @Override
        public int size() {
            return RadixHamTrie.this.size();
        }

        @Override
        public void clear() {
            RadixHamTrie.this.clear();
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
            RadixHamTrie.this.forEach((key, value) -> action.accept(value));
        }

    }

    private final class KeySet extends AbstractSet<Integer> {

        @Override
        public int size() {
            return RadixHamTrie.this.size();
        }

        @Override
        public void clear() {
            RadixHamTrie.this.clear();
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
            RadixHamTrie.this.forEachKey(key -> action.accept(key));
        }

    }

    private final class EntrySet extends AbstractSet<IntObjectEntry<V>> {

        @Override
        public int size() {
            return RadixHamTrie.this.size();
        }

        @Override
        public void clear() {
            RadixHamTrie.this.clear();
        }

        @Override
        public Iterator<IntObjectEntry<V>> iterator() {
            if (isEmpty()) {
                return Collections.emptyIterator();
            }
            return new EntryIterator();
        }

        @Override
        @SuppressWarnings("unchecked")
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
            RadixHamTrie.this.forEach(action);
        }

    }

    private final class EntryIterator extends IntHamtIterator implements Iterator<IntObjectEntry<V>> {

        @Override
        public boolean hasNext() {
            return hasNextIt();
        }

        @Override
        public IntObjectEntry<V> next() {
            return nextIt();
        }

    }

    private final class ValueIterator extends IntHamtIterator implements Iterator<V> {

        @Override
        public boolean hasNext() {
            return hasNextIt();
        }

        @Override
        public V next() {
            return nextIt().getValue();
        }

    }

    private final class KeyIterator extends IntHamtIterator implements Iterator<Integer> {

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
        try {
            // TODO: implements traversing both trees in parallel if
            // RadixHamTrie
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
     * RadixHamTrieNode implements a radix hash array mapped trie node data
     * structure.
     *
     * <p>
     * The radix is 32, and manage 5 bits of the int key.
     *
     * <p>
     * The prefix bits used to reach each node are stored in <tt>path</tt>
     * variable. Each node stores all bits from parent nodes, and is not
     * necessary to carry it traversing the trie.
     *
     * <p>
     * Each subtree of the RadixHamTrie can be cosidered a valid tree, because
     * each subtree has all needed information needed to traverse and extract
     * its information.
     *
     * <p>
     * To know the number of bits that forms the path,
     * <tt>32 - the number of prefix used bits - 5</tt> is also stored.
     *
     * <p>
     * To save memory storing both values, and because the last five bits are
     * never used in path, both values are combined in a single variable.
     *
     * <p>
     * Up to the highest 27 bits of path contain the prefix and the lowest 5
     * contains the number of bits *not* used by the prefix.
     *
     * <p>
     * "number non prefix bits" term is used as synonymous of "number prefix
     * bits" because initial resulted code operated always with its complement
     * subtracting five. For performance, complement number of bits is used.
     *
     * <pre>
     * {@code
     *
     * +----------------------------------+-------+
     * | Prefix bits used in each node    | #bits |
     * +----------------------------------+-------+
     * | 00 00000 00000 00000 00000 00000 | 00000 |
     * +----------------------------------+-------+
     *
     * }
     * </pre>
     */
    static class RadixHamTrieNode implements Iterable<IntObjectEntry<?>> {

        private Object[] arr;
        private int bitmap = 0;
        private int path;

        /**
         * Creates an empty node with no elements
         *
         * @param prefixBits
         *            prefix bits stored in the node. It's expected that non
         *            prefix bits must be setted to 0
         * @param numberNonPrefixBits
         *            number of bits not used in by de prefix
         */
        private RadixHamTrieNode(int prefixBits, int numberNonPrefixBits) {
            this.path = prefixBits + numberNonPrefixBits;
        }

        /**
         * Returns the number of bits that are not part of the path in this node
         * NumberPrefixBits = 32 - NumberNonPrefixBits - 5
         *
         * @return the number of bits that are not used in the prefix stored in
         *         the node
         */
        public int getNumberNonPrefixBits() {
            return path & BIT_MASK;
        }

        /**
         * Returns true if the node is a leaf node
         *
         * @return true if the node is a leaf node
         */
        public boolean isLeaf() {
            return (path & BIT_MASK) == 0;
        }

        /**
         * Returns prefix bits of the trie node
         *
         * @return prefix bits of the trie node
         */
        public int getPrefixBits() {
            return path & MASK_PATH;
        }

        /**
         * Obtains the value associated to the trie node index
         *
         * @param idx
         *            index of the element in the uncompressed array
         * @return the value associated
         */
        public Object get(int idx) {
            if (isBitPresent(1 << idx)) {
                return arr[getArrIdx(idx)];
            }
            return null;
        }

        /**
         * Stores a value in certain position. If an associated value already
         * exists, overwrite it. If it doesn't exists, the compressed array
         * increase the size in one element.
         *
         * @param idx
         *            index of the element in the uncompressed array
         * @param value
         *            the value to store
         * @return the previous value in <tt>idx</tt> position, or <tt>null</tt>
         *         if there was no value in <tt>idx</tt>.
         */
        public Object set(int idx, Object value) {
            int bitIdx = 1 << idx;
            if (isBitPresent(bitIdx)) {
                int arrIdx = getArrIdx(idx);
                Object previousValue = arr[arrIdx];
                arr[arrIdx] = value;
                return previousValue;
            }
            bitmap = bitmap | bitIdx;
            if (arr == null) {
                arr = new Object[1];
                arr[0] = value;
            } else {
                int arrIdx = getArrIdx(idx);
                int arrLen = arr.length;
                Object[] arrIncreased = new Object[arrLen + 1];
                if (arrIdx == arrLen) {
                    System.arraycopy(arr, 0, arrIncreased, 0, arrLen);
                } else if (arrIdx == 0) {
                    System.arraycopy(arr, 0, arrIncreased, 1, arrLen - arrIdx);
                } else {
                    System.arraycopy(arr, 0, arrIncreased, 0, arrIdx);
                    System.arraycopy(arr, arrIdx, arrIncreased, arrIdx + 1, arrLen - arrIdx);
                }
                arr = arrIncreased;
                arr[arrIdx] = value;
            }
            return null;
        }

        /**
         * Create a new node parent of the current node with two childs: the
         * current node and a new leaf node with the given key and value.
         *
         * The parent node is created using 5 + number of lower shared bits by
         * both childs nodes.
         *
         * @param key
         *            key with which the specified value is to be associated
         * @param value
         *            value to be associated with the specified key
         * @param numberNonPrefixBits
         *            number of non prefix bits used by both childs
         * @return the created parent node with both childs
         */
        public RadixHamTrieNode createParentNodeWith(int key, Object value, int numberNonPrefixBits) {
            int parentNodePrefixBits = getKeyPrefixBits(key, numberNonPrefixBits + BIT_SIZE);
            RadixHamTrieNode parentNode = new RadixHamTrieNode(parentNodePrefixBits, numberNonPrefixBits);
            int idx1 = (path >>> numberNonPrefixBits) & BIT_MASK;
            int idx2 = (key >>> numberNonPrefixBits) & BIT_MASK;
            assert (idx1 != idx2);

            Object[] arr = new Object[2];
            if (idx1 < idx2) {
                arr[0] = this;
                arr[1] = newLeafNode(key, value);
            } else {
                arr[0] = newLeafNode(key, value);
                arr[1] = this;
            }
            parentNode.arr = arr;
            parentNode.bitmap = (1 << idx1) + (1 << idx2);
            return parentNode;
        }

        /**
         * Removes the associated element in certain position and reduce the
         * size of the compressed array if an element exists
         *
         * @param idx
         *            index of the element in the uncompressed array
         * @return the previous value located in index value or <tt>null</tt> if
         *         there was no value associated to this possition
         */
        public Object remove(int idx) {
            int bitIdx = 1 << idx;
            if (!isBitPresent(bitIdx)) {
                return null;
            }
            int arrIdx = getArrIdx(idx);
            Object previousValue = arr[arrIdx];
            bitmap = bitmap & ~bitIdx;
            if (bitmap != 0) {
                int arrLen = arr.length - 1;
                Object[] arrDecreased = new Object[arrLen];
                if (arrIdx == 0) {
                    System.arraycopy(arr, 1, arrDecreased, 0, arrLen);
                } else if (arrIdx == arrLen) {
                    System.arraycopy(arr, 0, arrDecreased, 0, arrLen);
                } else {
                    System.arraycopy(arr, 0, arrDecreased, 0, arrIdx);
                    System.arraycopy(arr, arrIdx + 1, arrDecreased, arrIdx, arrLen - arrIdx);
                }
                arr = arrDecreased;
            } else {
                arr = null;
            }
            return previousValue;
        }

        /**
         * Returns <tt>true</tt> if this array contains no elements.
         *
         * @return <tt>true</tt> if this array contains no elements
         */
        public boolean isEmpty() {
            return bitmap == 0;
        }

        /**
         * Returns the number of elements stored in the node
         *
         * @return the number of elements stored in the node
         */
        public int size() {
            if (arr == null) {
                return 0;
            }
            return arr.length;
        }

        /**
         * Returns <tt>true</tt> if the <tt>bitIdx</tt> is set to 1. This means
         * that a value is stored in the associated array.
         *
         * @param bitIdx
         *            possition in the bitmap
         * @return <tt>true</tt> if the bit is 1
         */
        private boolean isBitPresent(int bitIdx) {
            return (bitmap & bitIdx) != 0;
        }

        /**
         * Returns the associated possition in the array of a bit from the
         * bitmap. It takes the <tt>idx</tt> left bits in the bitmap and counts
         * the number of present 1s.
         *
         * @param idx
         *            index in the bitmap
         * @return the associated value in the compressed array
         */
        private int getArrIdx(int idx) {
            int mask = (0xFFFFFFFF >>> (ARR_SIZE - 1 - idx));
            return Integer.bitCount(mask & bitmap) - 1;
        }

        public void forEach(IntObjectConsumer<Object> action) {
            int it = 0;
            int highBits = getPrefixBits();
            for (int idx = 0, mask = 1; idx < ARR_SIZE; idx++, mask = mask << 1) {
                if (isBitPresent(mask)) {
                    action.accept(highBits + idx, arr[it]);
                    it++;
                }
            }
        }

        public void forEachKey(IntConsumer action) {
            int highBits = getPrefixBits();
            for (int idx = 0, mask = 1; idx < ARR_SIZE; idx++, mask = mask << 1) {
                if (isBitPresent(mask)) {
                    action.accept(highBits + idx);
                }
            }
        }

        public void forEachValue(Consumer<Object> action) {
            int it = 0;
            for (int idx = 0, mask = 1; idx < ARR_SIZE; idx++, mask = mask << 1) {
                if (isBitPresent(mask)) {
                    action.accept(arr[it]);
                    it++;
                }
            }
        }

        @Override
        public Iterator<IntObjectEntry<?>> iterator() {
            if (arr == null) {
                return Collections.emptyIterator();
            }
            return new ArrayIterator();
        }

        private class ArrayIterator implements Iterator<IntObjectEntry<?>> {

            private int it = 0;
            private int idx = 0;
            private int mask = 1;
            private int highBits = getPrefixBits();

            ArrayIterator() {
                gotoNext();
            }

            @Override
            public boolean hasNext() {
                return it < arr.length;
            }

            @Override
            public IntObjectEntry<?> next() {
                if (it >= arr.length) {
                    throw new NoSuchElementException();
                }
                IntObjectEntry<?> value = new IntObjectEntry<>(highBits + idx, arr[it++]);
                nextIndex();
                gotoNext();
                return value;
            }

            private void gotoNext() {
                while (idx < ARR_SIZE && !isBitPresent(mask)) {
                    nextIndex();
                }
            }

            private void nextIndex() {
                idx++;
                mask = mask << 1;
            }
        }

        @Override
        public String toString() {
            if (isEmpty()) {
                return "{}";
            }
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            boolean first = true;
            if (isLeaf()) {
                int prefixBits = getPrefixBits();
                for (int idx = 0; idx < ARR_SIZE; idx++) {
                    Object object = get(idx);
                    if (object != null) {
                        if (!first) {
                            sb.append(", ");
                        } else {
                            first = false;
                        }
                        sb.append(prefixBits + idx).append("=").append(object);
                    }
                }
            } else {
                for (int idx = 0; idx < ARR_SIZE; idx++) {
                    Object object = get(idx);
                    if (object != null) {
                        if (!first) {
                            sb.append(", ");
                        } else {
                            first = false;
                        }
                        sb.append(object);
                    }
                }
            }
            sb.append('}');
            return sb.toString();
        }

    }

}