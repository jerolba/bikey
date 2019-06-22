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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IntArrayMapTest extends IntKeyMapTest {

    private static int MAX_SIZE = 10_000;

    public IntKeyMap<String> getNewIntKeyMap() {
        return new IntArrayMap<>();
    }

    @Test
    public void doesNotAcceptNegative() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            map.put(-1, "-one");
        });
    }

    @Test
    public void doesNotAcceptNearMaxIntValue() {
        assertThrows(OutOfMemoryError.class, () -> {
            map.put(Integer.MAX_VALUE - 1, "Error");
        });
    }

    @Test
    public void canGrowFromInitialCapacity() {
        IntArrayMap<String> map = new IntArrayMap<>(100);
        map.put(1, "1");
        map.put(100, "100");
        map.put(250, "250");
        assertTrue(map.containsKey(1));
        assertTrue(map.containsKey(100));
        assertTrue(map.containsKey(250));
    }

    @Test
    public void randomlyAddAndRemoveValuesSparse() {
        randomlyAddAndRemoveValues(MAX_SIZE, MAX_SIZE / 10);
    }

    @Test
    public void randomlyAddAndRemoveValuesDense() {
        randomlyAddAndRemoveValues(MAX_SIZE, MAX_SIZE);
    }

    @Test
    public void randomlyAddAndRemoveValuesRepeated() {
        randomlyAddAndRemoveValues(MAX_SIZE, MAX_SIZE * 4);
    }

    @Test
    public void hasEqualsToOtherImplementation() {
        IntKeyMap<String> other = new RadixTrie<>();

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
    @SuppressWarnings("unchecked")
    public void cloneTest() {
        map.put(10, "10");
        map.put(32, "32");
        map.put(63, "63");
        map.put(100, "100");
        Object clone = ((IntArrayMap<String>) map).clone();
        IntArrayMap<String> cloned = (IntArrayMap<String>) clone;
        assertEquals(map.size(), cloned.size());
        map.forEach((i, v) -> assertEquals(v, cloned.get(i)));
    }

    @Nested
    class CopyMap {

        @BeforeEach
        void beforeEachTest() {
            map.put(10, "10");
            map.put(32, "32");
            map.put(63, "63");
            map.put(100, "100");
        }

        @Test
        public void testIntArrayMapConstructor() {
            IntKeyMap<String> newOne = new IntArrayMap<>((IntArrayMap<String>) map);
            assertContainsAll(newOne);
        }

        @Test
        public void testIntKeyMapConstructor() {
            IntKeyMap<String> newOne = new IntArrayMap<>(map);
            assertContainsAll(newOne);
        }

        @Test
        @SuppressWarnings("unchecked")
        public void testClone() {
            IntKeyMap<String> cloned = (IntKeyMap<String>) ((IntArrayMap<String>) map).clone();
            assertContainsAll(cloned);
        }

        @Test
        public void testPutAll() {
            IntKeyMap<String> copy = new IntArrayMap<>(MAX_SIZE);
            copy.putAll(map);
            assertContainsAll(copy);
        }

        void assertContainsAll(IntKeyMap<String> copy) {
            assertEquals(map.size(), copy.size());
            map.forEach((i, v) -> assertEquals(v, copy.get(i)));
        }

    }

}
