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

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public abstract class BikeyMapTest {

    BikeyMap<String, String, String> map = getNewBikeyMap();

    public abstract BikeyMap<String, String, String> getNewBikeyMap();

    @Test
    public void justCreatedIsEmpty() {
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    @Test
    public void withAnElementIsNotEmpty() {
        map.put("one", "1", "one-1");
        assertFalse(map.isEmpty());
    }

    @Test
    public void doesNotAcceptNullValues() {
        assertThrows(NullPointerException.class, () -> {
            map.put("one", "1", null);
        });
    }

    @Test
    public void doesNotAcceptNullKeys() {
        assertThrows(NullPointerException.class, () -> {
            map.put(null, "1", "one-1");
        });
        assertThrows(NullPointerException.class, () -> {
            map.put("one", null, "one-1");
        });
    }

    @Test
    public void inAnEmptyMapCanAddAnInexistentElement() {
        assertNull(map.put("one", "1", "one-1"));
        assertEquals(1, map.size());
    }

    @Test
    public void anNonAddedElementIsNotCointaned() {
        map.put("one", "1", "one-1");
        assertFalse(map.containsKey("any", "other"));
    }

    @Test
    public void anAddedElementIsCointaned() {
        map.put("one", "1", "one-1");
        assertTrue(map.containsKey("one", "1"));
    }

    @Test
    public void canGetValue() {
        assertNull(map.put("one", "1", "one-1"));
        assertEquals(new String("one-1"), map.get("one", "1"));
    }

    @Test
    public void canNotGetValueIfItDoesNotExists() {
        assertNull(map.put("one", "1", "one-1"));
        assertNull(map.get("any", "other"));
    }

    @Test
    public void ifKeyExistsThePreviousValueIsReturned() {
        map.put("one", "1", "one-1");
        assertEquals("one-1", map.put("one", "1", "one-one"));
        assertEquals(1, map.size());
    }

    @Test
    public void ifKeyExistsTheValueIsReplaced() {
        map.put("one", "1", "one-1");
        map.put("one", "1", "one-one");
        assertEquals("one-one", map.get("one", "1"));
    }

    @Test
    public void aClearedMapHasNoElements() {
        map.put("one", "1", "one-1");
        map.put("two", "2", "two-2");
        map.clear();
        assertTrue(map.isEmpty());
        assertFalse(map.containsKey("one", "1"));
        assertFalse(map.containsKey("two", "2"));
    }

    @Test
    public void canRemoveAnElement() {
        map.put("one", "1", "one-1");
        map.remove("one", "1");
        assertTrue(map.isEmpty());
    }

    @Test
    public void canNotRemoveAnElementWithMissingColumn() {
        map.put("one", "1", "one-1");
        map.remove("one", "2");
        assertTrue(map.containsKey("one", "1"));
    }

    @Test
    public void canNotRemoveAnElementWithMissingRow() {
        map.put("one", "1", "one-1");
        map.remove("two", "1");
        assertTrue(map.containsKey("one", "1"));
    }

    @Test
    public void ifKeyExistsRemoveReturnsTheValue() {
        map.put("one", "1", "one-1");
        assertEquals("one-1", map.remove("one", "1"));
    }

    @Test
    public void testBranchColumnDoesntExist() {
        map.put("one", "1", "one-1");
        map.put("two", "2", "two-2");
        assertNull(map.remove("one", "2"));
    }

    @Test
    public void canAddAnElementAfterBeingEmpty() {
        map.put("one", "1", "one-1");
        map.remove("one", "1");
        assertTrue(map.isEmpty());
        map.put("one", "1", "one-1");
        assertFalse(map.isEmpty());
    }

    @Test
    public void getOrDefaultReturnsValueIfExists() {
        map.put("one", "1", "one-1");
        assertEquals("one-1", map.getOrDefault("one", "1", "other"));
    }

    @Test
    public void getOrDefaultReturnsDefaultIfNotExists() {
        assertEquals("other", map.getOrDefault("one", "1", "other"));
    }

    @Test
    public void putIfAbsentPutsIfNotExists() {
        assertNull(map.putIfAbsent("one", "1", "one-1"));
        assertEquals("one-1", map.get("one", "1"));
    }

    @Test
    public void putIfAbsentDoesNotPutIfExists() {
        map.put("one", "1", "one-1");
        assertEquals("one-1", map.putIfAbsent("one", "1", "othervalue"));
        assertEquals("one-1", map.get("one", "1"));
    }

    @Test
    public void removeValueDoesNotRemoveIfKeyDoesntExists() {
        assertFalse(map.remove("one", "1", "one-1"));
    }

    @Test
    public void removeValueDoesNotRemoveIfKeyAndValueAreNotEquals() {
        map.put("one", "1", "one-1");
        assertFalse(map.remove("one", "1", "1-1"));
        assertTrue(map.containsKey("one", "1"));
    }

    @Test
    public void removeValueRemovesIfKeyAndValueAreEquals() {
        map.put("one", "1", "one-1");
        assertTrue(map.remove("one", "1", "one-1"));
        assertFalse(map.containsKey("one", "1"));
    }

    @Test
    public void replaceValueDoesNotReplaceIfKeyDoesntExists() {
        assertFalse(map.replace("one", "1", "one-1", "other"));
    }

    @Test
    public void replaceValueDoesNotReplaceIfKeyAndOldValueAreNotEquals() {
        map.put("one", "1", "one-1");
        assertFalse(map.replace("one", "1", "1-1", "one-one"));
        assertEquals("one-1", map.get("one", "1"));
    }

    @Test
    public void replaceValueReplacesIfKeyAndValueAreEquals() {
        map.put("one", "1", "one-1");
        assertTrue(map.replace("one", "1", "one-1", "one-one"));
        assertEquals("one-one", map.get("one", "1"));
    }

    @Test
    public void replaceDoesNotReplaceIfKeyDoesntExists() {
        assertNull(map.replace("one", "1", "one-1"));
        assertFalse(map.containsKey("one", "1"));
    }

    @Test
    public void replaceReplacesIfKeyAndValueExists() {
        map.put("one", "1", "one-1");
        assertNotNull(map.replace("one", "1", "1-1"));
        assertEquals("1-1", map.get("one", "1"));
    }

    @Test
    public void computeIfAbsentDoesNothingIfValuePresent() {
        map.put("one", "1", "one-1");
        assertEquals("one-1", map.computeIfAbsent("one", "1", (r, c) -> r + c + r + c));
        assertEquals("one-1", map.get("one", "1"));
    }

    @Test
    public void computeIfAbsentComputesIfValueNotPresent() {
        assertEquals("one1one1", map.computeIfAbsent("one", "1", (r, c) -> r + c + r + c));
        assertEquals("one1one1", map.get("one", "1"));
    }

    @Test
    public void computeIfPresentComputesIfValuePresent() {
        map.put("one", "1", "one-1");
        assertEquals("one-1[one,1]",
                map.computeIfPresent("one", "1", (r, c, value) -> value + "[" + r + "," + c + "]"));
        assertEquals("one-1[one,1]", map.get("one", "1"));
    }

    @Test
    public void computeIfPresentDoesNothingIfValueNotPresent() {
        assertNull(map.computeIfPresent("one", "1", (r, c, value) -> value + "[" + r + "," + c + "]"));
        assertFalse(map.containsKey("one", "1"));
    }

    @Test
    public void computeRemovesKeyIfMappingIsNull() {
        map.put("one", "1", "one-1");
        assertNull(map.compute("one", "1", (r, c, value) -> null));
        assertFalse(map.containsKey("one", "1"));
    }

    @Test
    public void computeDoesNothingIfMappingIsNullAndKeyDoeNotExists() {
        assertNull(map.compute("one", "1", (r, c, value) -> null));
        assertFalse(map.containsKey("one", "1"));
    }

    @Test
    public void computePutsValueIfMappingIsNotNull() {
        map.put("one", "1", "one-1");
        assertEquals("one-1[one,1]", map.compute("one", "1", (r, c, value) -> value + "[" + r + "," + c + "]"));
        assertEquals("one-1[one,1]", map.get("one", "1"));
    }

    @Test
    public void mergeRemovesKeyIfMappingIsNull() {
        map.put("one", "1", "one-1");
        assertNull(map.merge("one", "1", "1", (oldValue, value) -> null));
        assertFalse(map.containsKey("one", "1"));
    }

    @Test
    public void mergePutsMappingIfMappingIsNotNull() {
        map.put("one", "1", "one-1");
        assertEquals("one-11", map.merge("one", "1", "1", (oldValue, value) -> oldValue + value));
        assertEquals("one-11", map.get("one", "1"));
    }

    @Test
    public void nonPresentValueIsNotContained() {
        map.put("one", "1", "one-1");
        map.put("three", "3", "three-3");
        assertFalse(map.containsValue("two-2"));
        assertFalse(map.containsValue("six-6"));
    }

    @Test
    public void presentValueIsContained() {
        map.put("one", "1", "one-1");
        map.put("three", "3", "three-3");
        assertTrue(map.containsValue("one-1"));
        assertTrue(map.containsValue("three-3"));
    }

    @Test
    public void containsRowTests() {
        assertFalse(map.containsRow("one"));
        map.put("one", "1", "one-1");
        assertTrue(map.containsRow("one"));
        map.remove("one", "1");
        assertFalse(map.containsRow("one"));
    }

    @Test
    public void containsColumnsTests() {
        assertFalse(map.containsColumn("1"));
        map.put("one", "1", "one-1");
        assertTrue(map.containsColumn("1"));
        map.remove("one", "1");
        assertFalse(map.containsRow("1"));
    }

    @Test
    public void iterateEmptyMap() {
        assertFalse(map.iterator().hasNext());
        assertFalse(map.keySet().iterator().hasNext());
        assertFalse(map.bikeySet().iterator().hasNext());
        assertFalse(map.values().iterator().hasNext());
        assertFalse(map.entrySet().iterator().hasNext());
        assertFalse(map.rowKeySet().iterator().hasNext());
        assertFalse(map.columnKeySet().iterator().hasNext());
    }

    @Test
    public void singleItemIteration() {
        map.put("one", "1", "one-1");
        Iterator<BikeyEntry<String, String, String>> iterator = map.iterator();

        assertTrue(iterator.hasNext());
        BikeyEntry<String, String, String> next = iterator.next();
        assertEquals("one", next.getRow());
        assertEquals("1", next.getColumn());
        assertEquals("one-1", next.getValue());

        assertFalse(iterator.hasNext());
    }

    @Nested
    class Iteration {

        @BeforeEach
        void beforeEachTest() {
            map.put("0", "1", "0-1");
            map.put("0", "2", "0-2");
            map.put("1", "2", "1-2");
            map.put("1", "5", "1-5");
            map.put("2", "3", "2-3");
            map.put("2", "1", "2-1");
            map.put("3", "6", "3-6");
        }

        @Test
        public void iteratorHasAllElements() {
            Set<String> found = new HashSet<>();
            Iterator<BikeyEntry<String, String, String>> iterator = map.iterator();
            while (iterator.hasNext()) {
                found.add(toString(iterator.next()));
            }
            assertContainsAll(found);
        }

        @Test
        public void foreachBikeyHasAllElements() {
            BikeySet<String, String> found = new TableBikeySet<>();
            map.forEachBikey(found::add);
            assertTrue(found.contains("0", "1"));
            assertTrue(found.contains("0", "2"));
            assertTrue(found.contains("1", "2"));
            assertTrue(found.contains("1", "5"));
            assertTrue(found.contains("2", "3"));
            assertTrue(found.contains("2", "1"));
            assertTrue(found.contains("3", "6"));
        }

        @Test
        public void foreachWithBiConsumerHasAllElements() {
            Set<String> found = new HashSet<>();
            map.forEach((k, v) -> found.add(toString(k, v)));
            assertContainsAll(found);
        }

        @Test
        public void foreachWithTriConsumerHasAllElements() {
            Set<String> found = new HashSet<>();
            map.forEach((r, c, v) -> found.add(toString(r, c, v)));
            assertContainsAll(found);
        }

        @Test
        public void forLoopHasAllElements() {
            Set<String> found = new HashSet<>();
            for (BikeyEntry<String, String, String> bikeyEntry : map) {
                found.add(toString(bikeyEntry));
            }
            assertContainsAll(found);
        }

        @Test
        public void streamHasAllElements() {
            Set<String> found = map.entrySet().stream().map(bikeyEntry -> toString(bikeyEntry)).collect(toSet());
            assertContainsAll(found);
        }

        @Test
        public void iterateWithoutCallingHashNext() {
            Iterator<BikeyEntry<String, String, String>> iterator = map.iterator();
            Set<String> found = new HashSet<>();
            for (int i = 0; i < map.size(); i++) {
                found.add(toString(iterator.next()));
            }
            assertFalse(iterator.hasNext());
            assertContainsAll(found);
        }

        @Test
        public void removalIsNotSupported() {
            Iterator<Bikey<String, String>> iterator = map.keySet().iterator();
            iterator.next();
            assertThrows(UnsupportedOperationException.class, () -> {
                iterator.remove();
            });
        }

        void assertContainsAll(Set<String> found) {
            assertTrue(found.contains("(0,1) = 0-1"));
            assertTrue(found.contains("(0,2) = 0-2"));
            assertTrue(found.contains("(1,2) = 1-2"));
            assertTrue(found.contains("(1,5) = 1-5"));
            assertTrue(found.contains("(2,1) = 2-1"));
            assertTrue(found.contains("(2,3) = 2-3"));
            assertTrue(found.contains("(3,6) = 3-6"));
            assertEquals(7, found.size());
        }

        String toString(BikeyEntry<String, String, String> entry) {
            return toString(entry.getRow(), entry.getColumn(), entry.getValue());
        }

        String toString(Bikey<String, String> key, String value) {
            return toString(key.getRow(), key.getColumn(), value);
        }

        String toString(String row, String col, String value) {
            return "(" + row + "," + col + ") = " + value;
        }
    }

    @Test
    public void rowKeySetContainsAllRowKeys() {
        map.put("one", "1", "one-1");
        map.put("one", "11", "one-11");
        map.put("two", "2", "2");
        Set<String> rowKeySet = map.rowKeySet();
        assertTrue(rowKeySet.contains("one"));
        assertTrue(rowKeySet.contains("two"));
        assertEquals(2, rowKeySet.size());
        map.put("three", "3", "3");
        assertEquals(3, rowKeySet.size());
    }

    @Test
    public void columnKeySetContainsAllColumnKeys() {
        map.put("one", "11", "one-11");
        map.put("eleven", "11", "eleven-11");
        map.put("two", "2", "2");
        Set<String> columnKeySet = map.columnKeySet();
        assertTrue(columnKeySet.contains("11"));
        assertTrue(columnKeySet.contains("2"));
        assertEquals(2, columnKeySet.size());
        map.put("three", "3", "3");
        assertEquals(3, columnKeySet.size());
        assertTrue(columnKeySet.contains("3"));
    }

    @Nested
    public class EntriesIteration {

        @BeforeEach
        void beforeEachTest() {
            map.put("0", "1", "0-1");
            map.put("0", "2", "0-2");
            map.put("1", "2", "1-2");
            map.put("1", "5", "1-5");
            map.put("2", "3", "2-3");
            map.put("2", "1", "2-1");
            map.put("3", "6", "3-6");
        }

        @Test
        public void keySetContainsKeys() {
            assertContainsAllKeys(map.keySet());
        }

        @Test
        public void foreachKeySetContainsKeys() {
            BikeySet<String, String> keys = new TableBikeySet<>();
            map.keySet().forEach(keys::add);
            assertContainsAllKeys(keys);
        }

        @Test
        public void foreachBikeyContainsKeys() {
            BikeySet<String, String> keys = new TableBikeySet<>();
            map.forEachBikey(keys::add);
            assertContainsAllKeys(keys);
        }

        @Test
        public void streamingKeySetContainsKeys() {
            BikeySet<String, String> keys = new TableBikeySet<>();
            map.keySet().stream().forEach(keys::add);
            assertContainsAllKeys(keys);
        }

        @Test
        public void clearingKeySetModifiesTheMap() {
            map.keySet().clear();
            assertTrue(map.isEmpty());
        }

        @Test
        public void valuesContainsAllValues() {
            assertContainsAllValues(map.values());
        }

        @Test
        public void foreachValuesContainsValues() {
            List<String> values = new ArrayList<>();
            map.values().forEach(values::add);
            assertContainsAllValues(values);
        }

        @Test
        public void streamingValuesContainsValues() {
            List<String> values = new ArrayList<>();
            map.values().stream().forEach(values::add);
            assertContainsAllValues(values);
        }

        @Test
        public void clearingValuesModifiesTheMap() {
            map.values().clear();
            assertTrue(map.isEmpty());
        }

        @Test
        public void entrySetContainsEntries() {
            assertContainsAllEntries(map.entrySet());
        }

        @Test
        public void foreachEntrySetContainsEntries() {
            Set<BikeyEntry<String, String, String>> entries = new HashSet<>();
            map.entrySet().forEach(entries::add);
            assertContainsAllEntries(entries);
        }

        @Test
        public void streamingEntrySetContainsEntries() {
            Set<BikeyEntry<String, String, String>> entries = new HashSet<>();
            map.entrySet().stream().forEach(entries::add);
            assertContainsAllEntries(entries);
        }

        @Test
        public void clearingEntrySetModifiesTheMap() {
            map.entrySet().clear();
            assertTrue(map.isEmpty());
        }

        void assertContainsAllKeys(Set<Bikey<String, String>> keySet) {
            assertTrue(keySet.contains(new BikeyImpl<>("0", "1")));
            assertTrue(keySet.contains(new BikeyImpl<>("0", "2")));
            assertTrue(keySet.contains(new BikeyImpl<>("1", "2")));
            assertTrue(keySet.contains(new BikeyImpl<>("1", "5")));
            assertTrue(keySet.contains(new BikeyImpl<>("2", "3")));
            assertTrue(keySet.contains(new BikeyImpl<>("2", "1")));
            assertTrue(keySet.contains(new BikeyImpl<>("3", "6")));
            assertFalse(keySet.contains(new BikeyImpl<>("NONE", "NONE")));
            assertEquals(7, keySet.size());
        }

        void assertContainsAllValues(Collection<String> values) {
            assertTrue(values.contains("0-1"));
            assertTrue(values.contains("0-2"));
            assertTrue(values.contains("1-2"));
            assertTrue(values.contains("1-5"));
            assertTrue(values.contains("2-3"));
            assertTrue(values.contains("2-1"));
            assertTrue(values.contains("3-6"));
            assertFalse(values.contains("NONE-NONE"));
            assertEquals(7, values.size());
        }

        void assertContainsAllEntries(Collection<BikeyEntry<String, String, String>> entries) {
            assertTrue(entries.contains(new BikeyEntry<>("0", "1", "0-1")));
            assertTrue(entries.contains(new BikeyEntry<>("0", "2", "0-2")));
            assertTrue(entries.contains(new BikeyEntry<>("1", "2", "1-2")));
            assertTrue(entries.contains(new BikeyEntry<>("1", "5", "1-5")));
            assertTrue(entries.contains(new BikeyEntry<>("2", "3", "2-3")));
            assertTrue(entries.contains(new BikeyEntry<>("2", "1", "2-1")));
            assertTrue(entries.contains(new BikeyEntry<>("3", "6", "3-6")));
            assertFalse(entries.contains(new BikeyEntry<>("NONE", "NONE", "NONE-NONE")));
            assertEquals(7, entries.size());
        }

    }

    @Test
    public void randomlyAddAndRemoveValuesSparse() {
        randomlyAddAndRemoveValues(100_000, 10_000);
    }

    @Test
    public void randomlyAddAndRemoveValuesDense() {
        randomlyAddAndRemoveValues(10_000, 10_000);
    }

    @Test
    public void randomlyAddAndRemoveValuesRepeated() {
        randomlyAddAndRemoveValues(5_000, 20_000);
    }

    public void randomlyAddAndRemoveValues(int maxValue, int number) {
        Random rnd = new Random();
        BikeySet<String, String> present = new TableBikeySet<>();
        for (int i = 0; i < number; i++) {
            String row = Integer.toString(rnd.nextInt(maxValue));
            String col = Integer.toString(rnd.nextInt(maxValue / 10));
            String put = map.put(row, col, row + "-" + col);
            if (put != null) {
                assertTrue(present.contains(row, col));
            }
            present.add(row, col);
        }
        List<BikeyEntry<String, String, String>> collected = map.entrySet().stream().collect(toList());
        assertEquals(present.size(), collected.size());
        for (BikeyEntry<String, String, String> item : collected) {
            assertEquals(item.getRow() + "-" + item.getColumn(), item.getValue());
            assertTrue(present.contains(item.getRow(), item.getColumn()));
        }

        List<Bikey<String, String>> toRemove = present.stream().collect(toList());
        Collections.shuffle(toRemove);
        int size = map.size();
        for (Bikey<String, String> remove : toRemove) {
            String value = map.remove(remove.getRow(), remove.getColumn());
            assertEquals(remove.getRow() + "-" + remove.getColumn(), value);
            assertFalse(map.containsKey(remove.getRow(), remove.getColumn()));
            size--;
            assertEquals(size, map.size());
        }
        assertTrue(map.isEmpty());
    }

    @Test
    public void hashCodeTests() {
        int hashCode0 = map.hashCode();
        map.put("1", "2", "one-two");
        int hashCode1 = map.hashCode();
        map.put("2", "3", "two-three");
        int hashCode2 = map.hashCode();
        map.put("3", "4", "three-four");
        int hashCode3 = map.hashCode();
        assertNotEquals(hashCode0, hashCode1);
        assertNotEquals(hashCode0, hashCode2);
        assertNotEquals(hashCode0, hashCode3);
        assertNotEquals(hashCode1, hashCode2);
        assertNotEquals(hashCode1, hashCode3);
        assertNotEquals(hashCode2, hashCode3);
    }

    @Test
    public void twoEmtpyMapsAreEquals() {
        assertTrue(map.equals(getNewBikeyMap()));
    }

    @Test
    public void isEqualsOfItsSelf() {
        assertTrue(map.equals(map));
        map.put("1", "2", "one-two");
        assertTrue(map.equals(map));
    }

    @Test
    public void hasEquals() {
        BikeyMap<String, String, String> other = getNewBikeyMap();

        map.put("1", "2", "one-two");
        assertFalse(map.equals(other));
        other.put("1", "2", "one-two");
        assertTrue(map.equals(other));

        map.put("2", "2", "two-two");
        assertFalse(map.equals(other));
        other.put("2", "2", "two-two");
        assertTrue(map.equals(other));

        map.put("2", "3", "two-three");
        assertFalse(map.equals(other));
        other.put("2", "3", "two-three");
        assertTrue(map.equals(other));

        map.put("3", "3", "tree-three");
        other.put("3", "3", "3-3");
        assertFalse(map.equals(other));

        assertFalse(map.equals(null));
    }

    @Test
    public void testToStringEmpty() {
        assertEquals("{}", map.toString());
    }

    @Test
    public void testToStringOneElement() {
        map.put("1", "2", "one-two");
        assertEquals("{[1, 2]=one-two}", map.toString());
    }

    @Test
    public void toStringMultipleElements() {
        map.put("1", "2", "one-two");
        map.put("2", "3", "two-three");
        assertEquals("{[1, 2]=one-two, [2, 3]=two-three}", map.toString());
    }

}
