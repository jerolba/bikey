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

public abstract class IntKeyMapTest {

    IntKeyMap<String> map = getNewIntKeyMap();

    public abstract IntKeyMap<String> getNewIntKeyMap();

    @Test
    public void justCreatedIsEmpty() {
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    @Test
    public void withAnElementIsNotEmpty() {
        map.put(1, "one");
        assertFalse(map.isEmpty());
    }

    @Test
    public void doesNotAcceptNullValues() {
        assertThrows(NullPointerException.class, () -> {
            map.put(0, null);
        });
    }

    @Test
    public void inAnEmptySetCanAddAnInexistentElement() {
        assertNull(map.put(1, "one"));
        assertEquals(1, map.size());
    }

    @Test
    public void anNonAddedElementIsNotCointaned() {
        map.put(1, "one");
        assertFalse(map.containsKey(2));
    }

    @Test
    public void anAddedElementIsCointaned() {
        map.put(1, "one");
        assertTrue(map.containsKey(1));
    }

    @Test
    public void canGetValue() {
        assertNull(map.put(1, "one"));
        assertEquals(new String("one"), map.get(1));
    }

    @Test
    public void canNotGetValueIfItDoesNotExists() {
        assertNull(map.put(1, "one"));
        assertNull(map.get(2));
    }

    @Test
    public void ifKeyExistsThePreviousValueIsReturned() {
        map.put(1, "one");
        assertEquals("one", map.put(1, "1"));
        assertEquals(1, map.size());
    }

    @Test
    public void ifKeyExistsTheValueIsReplaced() {
        map.put(1, "one");
        map.put(1, "1");
        assertEquals("1", map.get(1));
    }

    @Test
    public void aClearedMapHasNoElements() {
        map.put(1, "one");
        map.put(132, "one hundred thirty two");
        map.clear();
        assertTrue(map.isEmpty());
        assertFalse(map.containsKey(1));
        assertFalse(map.containsKey(132));
    }

    @Test
    public void canRemoveAnElement() {
        map.put(1, "one");
        map.remove(1);
        assertTrue(map.isEmpty());
    }

    @Test
    public void canNotRemoveAnEmptyMap() {
        assertNull(map.remove(1));
        assertTrue(map.isEmpty());
    }

    @Test
    public void testNoNextNodeBranchRemoveMap() {
        map.put(12, "12");
        map.put(32, "32");
        assertNull(map.remove(128));
    }

    @Test
    public void testNotRemovedRecursiveBranchRemoveMap() {
        map.put(12, "12");
        map.put(32, "32");
        assertNull(map.remove(2));
    }

    @Test
    public void ifKeyExistsRemoveReturnsTheValue() {
        map.put(1, "one");
        assertEquals("one", map.remove(1));
    }

    @Test
    public void canNoRemoveAnUnexistentElement() {
        map.put(20, "twenty");
        assertNull(map.remove(50));
        assertNull(map.remove(4));
        assertEquals(1, map.size());
    }

    @Test
    public void canAddAnElementAfterBeingEmpty() {
        map.put(1023, "1023");
        map.remove(1023);
        assertTrue(map.isEmpty());
        map.put(1, "1");
        assertFalse(map.isEmpty());
    }

    @Test
    public void getOrDefaultReturnsValueIfExists() {
        map.put(7890, "7890");
        assertEquals("7890", map.getOrDefault(7890, "other"));
    }

    @Test
    public void getOrDefaultReturnsDefaultIfNotExists() {
        assertEquals("other", map.getOrDefault(120341, "other"));
    }

    @Test
    public void putIfAbsentPutsIfNotExists() {
        assertNull(map.putIfAbsent(132, "132"));
        assertEquals("132", map.get(132));
    }

    @Test
    public void putIfAbsentDoesNotPutIfExists() {
        map.put(132, "132");
        assertEquals("132", map.putIfAbsent(132, "othervalue"));
        assertEquals("132", map.get(132));
    }

    @Test
    public void removeValueDoesNotRemoveIfKeyDoesntExists() {
        assertFalse(map.remove(56, "56"));
    }

    @Test
    public void removeValueDoesNotRemoveIfKeyAndValueAreNotEquals() {
        map.put(56, "56");
        assertFalse(map.remove(56, "fifty six"));
        assertTrue(map.containsKey(56));
    }

    @Test
    public void removeValueRemovesIfKeyAndValueAreEquals() {
        map.put(56, "56");
        assertTrue(map.remove(56, "56"));
        assertFalse(map.containsKey(56));
    }

    @Test
    public void replaceValueDoesNotReplaceIfKeyDoesntExists() {
        assertFalse(map.replace(56, "56", "fifty six"));
    }

    @Test
    public void replaceValueDoesNotReplaceIfKeyAndOldValueAreNotEquals() {
        map.put(56, "56");
        assertFalse(map.replace(56, "+56", "fifty six"));
        assertEquals("56", map.get(56));
    }

    @Test
    public void replaceValueReplacesIfKeyAndValueAreEquals() {
        map.put(56, "56");
        assertTrue(map.replace(56, "56", "fifty six"));
        assertEquals("fifty six", map.get(56));
    }

    @Test
    public void replaceDoesNotReplaceIfKeyDoesntExists() {
        assertNull(map.replace(56, "fifty six"));
        assertFalse(map.containsKey(56));
    }

    @Test
    public void replaceReplacesIfKeyAndValueExists() {
        map.put(56, "56");
        assertNotNull(map.replace(56, "fifty six"));
        assertEquals("fifty six", map.get(56));
    }

    @Test
    public void computeIfAbsentDoesNothingIfValuePresent() {
        map.put(33, "33");
        assertEquals("33", map.computeIfAbsent(33, key -> Integer.toString(key + 1)));
        assertEquals("33", map.get(33));
    }

    @Test
    public void computeIfAbsentComputesIfValueNotPresent() {
        assertEquals("34", map.computeIfAbsent(33, key -> Integer.toString(key + 1)));
        assertEquals("34", map.get(33));
    }

    @Test
    public void computeIfPresentComputesIfValuePresent() {
        map.put(33, "33");
        assertEquals("3334", map.computeIfPresent(33, (key, value) -> value + Integer.toString(key + 1)));
        assertEquals("3334", map.get(33));
    }

    @Test
    public void computeIfPresentDoesNothingIfValueNotPresent() {
        assertNull(map.computeIfPresent(33, (key, value) -> value + Integer.toString(key + 1)));
        assertFalse(map.containsKey(33));
    }

    @Test
    public void computeRemovesKeyIfMappingIsNull() {
        map.put(67, "67");
        assertNull(map.compute(67, (key, value) -> null));
        assertFalse(map.containsKey(67));
    }

    @Test
    public void computeDoesNothingIfMappingIsNullAndKeyDoeNotExists() {
        assertNull(map.compute(67, (key, value) -> null));
        assertFalse(map.containsKey(67));
    }

    @Test
    public void computePutsValueIfMappingIsNotNull() {
        map.put(67, "67");
        assertEquals("671", map.compute(67, (key, value) -> value + "1"));
        assertEquals("671", map.get(67));
    }

    @Test
    public void mergeRemovesKeyIfMappingIsNull() {
        map.put(72, "72");
        assertNull(map.merge(72, "1", (oldValue, value) -> null));
        assertFalse(map.containsKey(72));
    }

    @Test
    public void mergePutsMappingIfMappingIsNotNull() {
        map.put(72, "72");
        assertEquals("721", map.merge(72, "1", (oldValue, value) -> oldValue + value));
        assertEquals("721", map.get(72));
    }

    @Test
    public void nonPresentValueIsNotContained() {
        map.put(1, "one");
        map.put(48, "forty eight");
        assertFalse(map.containsValue("two"));
        assertFalse(map.containsValue("six"));
    }

    @Test
    public void presentValueIsContained() {
        map.put(1, "one");
        map.put(48, "forty eight");
        assertTrue(map.containsValue("one"));
        assertTrue(map.containsValue("forty eight"));
    }

    @Nested
    class SimpleIteration {

        @Test
        public void iterateEmptyMap() {
            assertFalse(map.keySet().iterator().hasNext());
            assertFalse(map.values().iterator().hasNext());
            assertFalse(map.entrySet().iterator().hasNext());
        }

        @Test
        public void onIteratinEndLaunchException() {
            map.put(0, "0");
            Iterator<IntObjectEntry<String>> it = map.iterator();
            it.next();
            assertFalse(it.hasNext());
            assertThrows(NoSuchElementException.class, () -> {
                it.next();
            });
        }

        @Test
        public void singleItemIteration() {
            map.put(0, "0");
            Iterator<IntObjectEntry<String>> iterator = map.iterator();

            assertTrue(iterator.hasNext());
            IntObjectEntry<String> next = iterator.next();
            assertEquals(0, next.getIntKey());
            assertEquals("0", next.getValue());

            assertFalse(iterator.hasNext());
        }

        @Test
        public void singleItemSecondNodeIteration() {
            map.put(32, "32");
            Iterator<IntObjectEntry<String>> iterator = map.iterator();

            assertTrue(iterator.hasNext());
            IntObjectEntry<String> next = iterator.next();
            assertEquals(32, next.getIntKey());
            assertEquals("32", next.getValue());

            assertFalse(iterator.hasNext());
        }

        @Test
        public void singleItemThirdNodeIteration() {
            map.put(64, "64");
            Iterator<IntObjectEntry<String>> iterator = map.iterator();

            assertTrue(iterator.hasNext());
            IntObjectEntry<String> next = iterator.next();
            assertEquals(64, next.getIntKey());
            assertEquals("64", next.getValue());

            assertFalse(iterator.hasNext());
        }

        @Test
        public void twoItemInSameNodeIteration() {
            map.put(0, "0");
            map.put(2, "2");
            Iterator<IntObjectEntry<String>> iterator = map.iterator();

            assertTrue(iterator.hasNext());
            IntObjectEntry<String> next = iterator.next();
            assertEquals(0, next.getIntKey());
            assertEquals("0", next.getValue());

            assertTrue(iterator.hasNext());
            next = iterator.next();
            assertEquals(2, next.getIntKey());
            assertEquals("2", next.getValue());

            assertFalse(iterator.hasNext());
        }

        @Test
        public void twoItemInSiblingNodeIteration() {
            map.put(31, "31");
            map.put(32, "32");
            Iterator<IntObjectEntry<String>> iterator = map.iterator();

            assertTrue(iterator.hasNext());
            IntObjectEntry<String> next = iterator.next();
            assertEquals(31, next.getIntKey());
            assertEquals("31", next.getValue());

            assertTrue(iterator.hasNext());
            next = iterator.next();
            assertEquals(32, next.getIntKey());
            assertEquals("32", next.getValue());

            assertFalse(iterator.hasNext());
        }

        @Test
        public void twoItemInCousinNodeIteration() {
            map.put(1023, "1023");
            map.put(1024, "1024");
            Iterator<IntObjectEntry<String>> iterator = map.iterator();

            assertTrue(iterator.hasNext());
            IntObjectEntry<String> next = iterator.next();
            assertEquals(1023, next.getIntKey());
            assertEquals("1023", next.getValue());

            assertTrue(iterator.hasNext());
            next = iterator.next();
            assertEquals(1024, next.getIntKey());
            assertEquals("1024", next.getValue());

            assertFalse(iterator.hasNext());
        }

    }

    @Nested
    class Iteration {

        @BeforeEach
        void beforeEachTest() {
            map.put(0, "0");
            map.put(1, "1");
            map.put(3, "3");
            map.put(35, "35");
            map.put(127, "127");
            map.put(5432, "5432");
        }

        @Test
        public void iteratorHasAllElements() {
            Set<String> found = new HashSet<>();
            Iterator<IntObjectEntry<String>> iterator = map.iterator();
            while (iterator.hasNext()) {
                found.add(toString(iterator.next()));
            }
            assertContainsAll(found);
        }

        @Test
        public void foreachWithBiConsumerHasAllElements() {
            Set<String> found = new HashSet<>();
            map.forEach((r, c) -> found.add(toString(r, c)));
            assertContainsAll(found);
        }

        @Test
        public void foreachWithConsumerHasAllElements() {
            Set<String> found = new HashSet<>();
            map.forEach(bikey -> found.add(toString(bikey)));
            assertContainsAll(found);
        }

        @Test
        public void forLoopHasAllElements() {
            Set<String> found = new HashSet<>();
            for (IntObjectEntry<String> bikey : map) {
                found.add(toString(bikey));
            }
            assertContainsAll(found);
        }

        @Test
        public void streamHasAllElements() {
            Set<String> found = map.stream().map(bikey -> toString(bikey)).collect(toSet());
            assertContainsAll(found);
        }

        @Test
        public void iterateWithoutCallingHashNext() {
            Iterator<IntObjectEntry<String>> iterator = map.iterator();
            Set<String> found = new HashSet<>();
            for (int i = 0; i < map.size(); i++) {
                found.add(toString(iterator.next()));
            }
            assertFalse(iterator.hasNext());
            assertContainsAll(found);
        }

        @Test
        public void keySetContainsKeys() {
            assertContainsAllKeys(map.keySet());
            assertContainsAllKeys(new HashSet<>(map.keySet()));
        }

        @Test
        public void iteratedKeySetContainsKeys() {
            Set<Integer> keys = new HashSet<>();
            for (Integer key : map.keySet()) {
                keys.add(key);
            }
            assertContainsAllKeys(keys);
        }

        @Test
        public void forEachInKeySetContainsKeys() {
            Set<Integer> keys = new HashSet<>();
            map.keySet().forEach(keys::add);
            assertContainsAllKeys(keys);
        }

        @Test
        public void foreachKeyContainsKeys() {
            Set<Integer> keys = new HashSet<>();
            map.forEachKey(keys::add);
            assertContainsAllKeys(keys);
        }

        @Test
        public void streamingKeySetContainsKeys() {
            Set<Integer> keys = map.keySet().stream().collect(toSet());
            assertContainsAllKeys(keys);
        }

        @Test
        public void clearingKeySetModifiesTheMap() {
            map.keySet().clear();
            assertTrue(map.isEmpty());
        }

        @Test
        public void valuesContainsValues() {
            assertContainsAllValues(map.values());
            assertContainsAllValues(new ArrayList<>(map.values()));
        }

        @Test
        public void iteratedValuesContainsValues() {
            List<String> values = new ArrayList<>();
            for (String value : map.values()) {
                values.add(value);
            }
            assertContainsAllValues(values);
        }

        @Test
        public void forEachInValuesContainsValues() {
            List<String> values = new ArrayList<>();
            map.values().forEach(values::add);
            assertContainsAllValues(values);
        }

        @Test
        public void streamingValuesContainsValues() {
            List<String> values = map.values().stream().collect(toList());
            assertContainsAllValues(values);
        }

        @Test
        public void clearingValuesModifiesTheMap() {
            map.values().clear();
            assertTrue(map.isEmpty());
        }

        @Test
        public void entrySetContainsAllEntries() {
            assertContainsAllEntries(map.entrySet());
            assertContainsAllEntries(new ArrayList<>(map.entrySet()));
        }

        @Test
        public void iteratedEntrySetContainsEntries() {
            List<IntObjectEntry<String>> entries = new ArrayList<>();
            for (IntObjectEntry<String> entry : map.entrySet()) {
                entries.add(entry);
            }
            assertContainsAllEntries(entries);
        }

        @Test
        public void forEachInEntrySetContainsEntries() {
            List<IntObjectEntry<String>> entries = new ArrayList<>();
            map.entrySet().forEach(entries::add);
            assertContainsAllEntries(entries);
        }

        @Test
        public void streamingEntrySetContainsValues() {
            List<IntObjectEntry<String>> entries = map.entrySet().stream().collect(toList());
            assertContainsAllEntries(entries);
        }

        @Test
        public void clearingEntrySetModifiesTheMap() {
            map.entrySet().clear();
            assertTrue(map.isEmpty());
        }

        void assertContainsAll(Set<String> found) {
            assertTrue(found.contains("0 - 0"));
            assertTrue(found.contains("1 - 1"));
            assertTrue(found.contains("3 - 3"));
            assertTrue(found.contains("35 - 35"));
            assertTrue(found.contains("127 - 127"));
            assertTrue(found.contains("5432 - 5432"));
            assertFalse(found.contains("NONE"));
            assertEquals(6, found.size());
        }

        void assertContainsAllKeys(Set<Integer> keySet) {
            assertTrue(keySet.contains(0));
            assertTrue(keySet.contains(1));
            assertTrue(keySet.contains(3));
            assertTrue(keySet.contains(35));
            assertTrue(keySet.contains(127));
            assertTrue(keySet.contains(5432));
            assertFalse(keySet.contains(1234567));
            assertEquals(6, keySet.size());
        }

        void assertContainsAllValues(Collection<String> values) {
            assertTrue(values.contains("0"));
            assertTrue(values.contains("1"));
            assertTrue(values.contains("3"));
            assertTrue(values.contains("35"));
            assertTrue(values.contains("127"));
            assertTrue(values.contains("5432"));
            assertFalse(values.contains("NONE"));
            assertEquals(6, values.size());
        }

        void assertContainsAllEntries(Collection<IntObjectEntry<String>> found) {
            assertTrue(found.contains(new IntObjectEntry<>(0, "0")));
            assertTrue(found.contains(new IntObjectEntry<>(1, "1")));
            assertTrue(found.contains(new IntObjectEntry<>(3, "3")));
            assertTrue(found.contains(new IntObjectEntry<>(35, "35")));
            assertTrue(found.contains(new IntObjectEntry<>(127, "127")));
            assertTrue(found.contains(new IntObjectEntry<>(5432, "5432")));
            assertEquals(6, found.size());
        }

        String toString(IntObjectEntry<String> entry) {
            return toString(entry.getIntKey(), entry.getValue());
        }

        String toString(int key, String value) {
            return key + " - " + value;
        }

    }

    public void randomlyAddAndRemoveValues(int maxValue, int number) {
        Random rnd = new Random();
        Set<Integer> present = new HashSet<>();
        for (int i = 0; i < number; i++) {
            int nextInt = maxValue != 0 ? rnd.nextInt(maxValue) : rnd.nextInt();
            String put = map.put(nextInt, Integer.toString(nextInt));
            if (put != null) {
                assertTrue(present.contains(nextInt));
            }
            present.add(nextInt);
        }

        List<IntObjectEntry<String>> collected = map.stream().collect(toList());
        assertEquals(present.size(), collected.size());
        for (IntObjectEntry<String> item : collected) {
            assertEquals(Integer.toString(item.getIntKey()), item.getValue());
            assertTrue(present.contains(item.getIntKey()));
        }

        List<Integer> toRemove = new ArrayList<>(present);
        Collections.shuffle(toRemove);
        int size = map.size();
        for (Integer remove : toRemove) {
            String value = map.remove(remove);
            assertEquals(Integer.toString(remove), value);
            assertFalse(map.containsKey(remove));
            size--;
            assertEquals(size, map.size());
        }
        assertTrue(map.isEmpty());
    }

    @Test
    public void iterationIsSorted() {
        Random rnd = new Random();
        for (int i = 0; i < 5_000; i++) {
            int nextInt = rnd.nextInt(10_000);
            map.put(nextInt, Integer.toString(nextInt));
        }
        int last = -1;
        for (IntObjectEntry<String> tuple : map) {
            int key = tuple.getIntKey();
            assertTrue(last < key);
            last = key;
        }
    }

    @Test
    public void hasHashCode() {
        int hashCode0 = map.hashCode();
        map.put(1, "one");
        int hashCode1 = map.hashCode();
        map.put(2, "two");
        int hashCode2 = map.hashCode();
        map.put(3, "three");
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
        assertTrue(map.equals(getNewIntKeyMap()));
    }

    @Test
    public void isEqualsOfItsSelf() {
        assertTrue(map.equals(map));
        map.put(1, "one");
        assertTrue(map.equals(map));
    }

    @Test
    public void hasEquals() {
        IntKeyMap<String> other = getNewIntKeyMap();

        map.put(1, "one");
        assertFalse(map.equals(other));
        other.put(1, "one");
        assertTrue(map.equals(other));

        map.put(2, "two");
        assertFalse(map.equals(other));
        other.put(22, "two");
        assertFalse(map.equals(other));
        other.remove(22);
        other.put(2, "two");
        assertTrue(map.equals(other));

        map.put(11, "one");
        assertFalse(map.equals(other));
        other.put(11, "one");
        assertTrue(map.equals(other));

        map.equals(null);
    }

    @Test
    public void testToStringEmpty() {
        assertEquals("{}", map.toString());
    }

    @Test
    public void testToStringOneElement() {
        map.put(1, "one");
        assertEquals("{1=one}", map.toString());
    }

    @Test
    public void toStringMultipleElementsAreSorted() {
        map.put(35, "thirtyfive");
        map.put(1, "one");
        assertEquals("{1=one, 35=thirtyfive}", map.toString());
    }

}
