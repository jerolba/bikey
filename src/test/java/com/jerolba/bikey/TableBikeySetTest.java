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

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TableBikeySetTest {

    private TableBikeySet<String, Integer> set = new TableBikeySet<>();

    @Test
    public void justCreatedIsEmpty() {
        assertEquals(0, set.size());
        assertTrue(set.isEmpty());
    }

    @Test
    public void withAnElementIsNotEmpty() {
        set.add("one", 1);
        assertFalse(set.isEmpty());
    }

    @Test
    public void nullElementsCanNotBeAdded() {
        assertThrows(NullPointerException.class, () -> {
            set.add(null, 1);
        });
        assertThrows(NullPointerException.class, () -> {
            set.add("one", null);
        });
    }

    @Test
    public void nullBikeyCanNotBeAdded() {
        assertThrows(NullPointerException.class, () -> {
            set.add(null);
        });
    }

    @Test
    public void bikeyCanBeAdded() {
        set.add(new BikeyImpl<>("one", 1));
        assertFalse(set.isEmpty());
        assertTrue(set.contains("one", 1));
    }

    @Test
    public void inAnEmptySetCanAddAnInexistentElement() {
        assertTrue(set.add("one", 1));
        assertEquals(1, set.size());
    }

    @Test
    public void ifAnElementExistsIsNotAdded() {
        set.add("one", 1);
        assertFalse(set.add(new String("one"), 1));
        assertEquals(1, set.size());
    }

    @Test
    public void addMoreElementsPerRow() {
        set.add("one", 1);
        set.add("one", 2);
        assertEquals(2, set.size());
    }

    @Test
    public void addMultipleRows() {
        set.add("one", 1);
        set.add("two", 2);
        assertEquals(2, set.size());
    }

    @Test
    public void addMoreElementsInMultipleRows() {
        set.add("one", 1);
        set.add("one", 2);
        set.add("two", 2);
        set.add("two", 3);
        assertEquals(4, set.size());
    }

    @Test
    public void nullElementsCanNotBeRemoved() {
        assertThrows(NullPointerException.class, () -> {
            set.remove(null, 1);
        });
        assertThrows(NullPointerException.class, () -> {
            set.remove("one", null);
        });
    }

    @Test
    public void nullBikeyCanNotBeRemoved() {
        assertThrows(NullPointerException.class, () -> {
            set.remove(null);
        });
    }

    @Test
    public void anExistentElementCanBeRemoved() {
        set.add("one", 1);
        assertEquals(1, set.size());
        assertTrue(set.remove("one", 1));
        assertTrue(set.isEmpty());
    }

    @Test
    public void anExistentBikeyCanBeRemoved() {
        set.add("one", 1);
        assertEquals(1, set.size());
        assertTrue(set.remove(new BikeyImpl<>("one", 1)));
        assertTrue(set.isEmpty());
    }

    @Test
    public void anUnexistentElementCanNotBeRemoved() {
        set.add("one", 1);
        assertEquals(1, set.size());
        assertFalse(set.remove("one", 2));
        assertFalse(set.remove("two", 1));
        assertEquals(1, set.size());
    }

    @Test
    public void multipleElementsFromMultiplerowsCanBeRemoved() {
        set.add("one", 1);
        set.add("one", 2);
        set.add("two", 2);
        set.add("two", 3);
        assertEquals(4, set.size());
        set.remove("one", 1);
        set.remove("one", 2);
        set.remove("two", 2);
        set.remove("two", 3);
        assertTrue(set.isEmpty());
    }

    @Test
    public void anNonAddedElementIsNotCointained() {
        set.add("one", 1);
        assertFalse(set.contains("one", 2));
        assertFalse(set.contains("two", 1));
    }

    @Test
    public void anAddedElementIsCointanied() {
        set.add("one", 1);
        assertTrue(set.contains("one", 1));
    }

    @Test
    public void anAddedBikeyIsCointanied() {
        set.add("one", 1);
        assertTrue(set.contains(new BikeyImpl<>("one", 1)));
    }

    @Test
    public void multipleAddedElementsInMultipleRowsAreContained() {
        set.add("one", 1);
        set.add("one", 2);
        set.add("two", 2);
        set.add("two", 3);
        assertTrue(set.contains("one", 1));
        assertTrue(set.contains("one", 2));
        assertTrue(set.contains("two", 2));
        assertTrue(set.contains("two", 3));
    }

    @Test
    public void canGetDistinctRowsValues() {
        set.add("one", 1);
        set.add("one", 2);
        set.add("two", 3);
        set.add("two", 4);
        set.add("tree", 5);
        set.add("four", 6);
        Set<String> rows = set.rowKeySet();
        assertEquals(4, rows.size());
        assertTrue(rows.contains("one"));
        assertTrue(rows.contains("two"));
        assertTrue(rows.contains("tree"));
        assertTrue(rows.contains("four"));

        set.remove("tree", 5);
        assertEquals(3, rows.size());
        assertFalse(rows.contains("tree"));

        set.remove("four", 6);
        assertEquals(2, rows.size());
        assertFalse(rows.contains("four"));
    }

    @Test
    public void canGetDistinctColumnsValues() {
        set.add("one", 1);
        set.add("two", 2);
        set.add("tree", 1);
        set.add("four", 3);
        set.add("six", 5);
        Set<Integer> cols = set.columnKeySet();
        assertEquals(4, cols.size());
        assertTrue(cols.contains(1));
        assertTrue(cols.contains(2));
        assertTrue(cols.contains(3));
        assertTrue(cols.contains(5));

        set.remove("four", 3);
        assertEquals(3, cols.size());
        assertFalse(cols.contains(3));

        set.remove("six", 5);
        assertEquals(2, cols.size());
        assertFalse(cols.contains(5));
    }

    @Test
    public void aClearedSetHasNoElements() {
        set.add("one", 1);
        set.add("two", 2);
        set.clear();
        assertTrue(set.isEmpty());
        assertFalse(set.contains("one", 1));
        assertFalse(set.contains("two", 2));
    }

    @Test
    public void emptyBikeySetDoesNotHaveNextIteration() {
        Iterator<Bikey<String, Integer>> it = set.iterator();
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, () -> {
            it.next();
        });
    }

    @Test
    public void canCreateNewBikeySetFromOtherOne() {
        set.add("one", 1);
        set.add("two", 2);
        set.add("three", 3);
        BikeySet<String, Integer> copy = new TableBikeySet<>(set);
        assertEquals(set.size(), copy.size());
        set.forEach((r, c) -> assertTrue(copy.contains(r, c)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void canBeCloned() {
        set.add("one", 1);
        set.add("one", 11);
        set.add("two", 2);
        set.add("three", 3);
        BikeySet<String, Integer> clone = (TableBikeySet<String, Integer>) set.clone();
        assertEquals(set.size(), clone.size());
        set.forEach((r, c) -> assertTrue(clone.contains(r, c)));
    }

    @Test
    public void hasHashCode() {
        set.add("one", 1);
        int hashCode1 = set.hashCode();
        set.add("two", 2);
        int hashCode2 = set.hashCode();
        set.add("three", 3);
        int hashCode3 = set.hashCode();
        assertNotEquals(hashCode1, hashCode2);
        assertNotEquals(hashCode2, hashCode3);
        assertNotEquals(hashCode1, hashCode3);
    }

    @Test
    public void twoEmtpyBikeysAreEquals() {
        assertTrue(set.equals(new TableBikeySet<>()));
    }

    @Test
    public void hasEquals() {
        BikeySet<String, Integer> other = new TableBikeySet<>();

        set.add("one", 1);

        assertTrue(set.equals(set));

        BikeySet<String, Integer> sameSize = new TableBikeySet<>();
        sameSize.add("1", 1);
        assertFalse(set.equals(sameSize));

        assertFalse(set.equals(other));
        other.add("one", 1);
        assertTrue(set.equals(other));

        set.add("two", 2);
        assertFalse(set.equals(other));
        other.add("two", 22);
        assertFalse(set.equals(other));
        other.remove("two", 22);
        other.add("two", 2);
        assertTrue(set.equals(other));

        set.add("one", 11);
        assertFalse(set.equals(other));
        other.add("one", 11);
        assertTrue(set.equals(other));

        assertFalse(set.equals(null));
    }

    @Nested
    class Iteration {

        @BeforeEach
        void beforeEachTest() {
            set.add("one", 1);
            set.add("one", 2);
            set.add("two", 2);
            set.add("two", 3);
        }

        @Test
        public void iteratorHasAllElements() {
            Set<String> founded = new HashSet<>();
            Iterator<Bikey<String, Integer>> iterator = set.iterator();
            while (iterator.hasNext()) {
                Bikey<String, Integer> next = iterator.next();
                founded.add(next.getRow() + " - " + next.getColumn());
            }
            assertContainsAll(founded);
        }

        @Test
        public void foreachWithBiConsumerHasAllElements() {
            Set<String> founded = new HashSet<>();
            set.forEach((r, c) -> {
                founded.add(r + " - " + c);
            });
            assertContainsAll(founded);
        }

        @Test
        public void foreachWithConsumerHasAllElements() {
            Set<String> founded = new HashSet<>();
            set.forEach(bikey -> {
                founded.add(bikey.getRow() + " - " + bikey.getColumn());
            });
            assertContainsAll(founded);
        }

        @Test
        public void forLoopHasAllElements() {
            Set<String> founded = new HashSet<>();
            for (Bikey<String, Integer> bikey : set) {
                founded.add(bikey.getRow() + " - " + bikey.getColumn());
            }
            assertContainsAll(founded);
        }

        @Test
        public void streamHasAllElements() {
            Set<String> founded = set.stream().map(bikey -> bikey.getRow() + " - " + bikey.getColumn())
                    .collect(toSet());
            assertContainsAll(founded);
        }

        @Test
        public void iterateWithoutCallingHasNext() {
            Iterator<Bikey<String, Integer>> iterator = set.iterator();
            Set<String> founded = new HashSet<>();
            Bikey<String, Integer> next = iterator.next();
            founded.add(next.getRow() + " - " + next.getColumn());
            next = iterator.next();
            founded.add(next.getRow() + " - " + next.getColumn());
            next = iterator.next();
            founded.add(next.getRow() + " - " + next.getColumn());
            next = iterator.next();
            founded.add(next.getRow() + " - " + next.getColumn());
            assertFalse(iterator.hasNext());
            assertContainsAll(founded);
        }

        @Test
        public void whenNoMoreElementsNextFails() {
            Iterator<Bikey<String, Integer>> iterator = set.iterator();
            while (iterator.hasNext()) {
                iterator.next();
            }
            assertThrows(NoSuchElementException.class, () -> {
                iterator.next();
            });
        }

        void assertContainsAll(Set<String> founded) {
            assertTrue(founded.contains("one - 1"));
            assertTrue(founded.contains("one - 2"));
            assertTrue(founded.contains("two - 2"));
            assertTrue(founded.contains("two - 3"));
            assertEquals(4, founded.size());
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
        randomlyAddAndRemoveValues(10_000, 100_000);
    }

    public void randomlyAddAndRemoveValues(int maxValue, int number) {
        Random rnd = new Random();
        Set<Bikey<String, Integer>> present = new HashSet<>();
        for (int i = 0; i < number; i++) {
            String row = Integer.toString(rnd.nextInt(maxValue));
            Integer col = rnd.nextInt(maxValue / 10);
            boolean added = set.add(row, col);
            if (added) {
                assertTrue(set.contains(new BikeyImpl<>(row, col)));
            }
            present.add(new BikeyImpl<>(row, col));
        }
        assertEquals(present.size(), set.size());
        for (Bikey<String, Integer> item : present) {
            assertTrue(set.contains(item.getRow(), item.getColumn()));
            assertTrue(set.contains(item));
        }

        List<Bikey<String, Integer>> toRemove = present.stream().collect(Collectors.toList());
        Collections.shuffle(toRemove);
        int size = set.size();
        for (Bikey<String, Integer> remove : toRemove) {
            set.remove(remove.getRow(), remove.getColumn());
            assertFalse(set.contains(remove.getRow(), remove.getColumn()));
            assertFalse(set.contains(remove));
            size--;
            assertEquals(size, set.size());
        }
        assertTrue(set.isEmpty());
        assertTrue(set.rowKeySet().isEmpty());
        assertTrue(set.columnKeySet().isEmpty());
    }
}
