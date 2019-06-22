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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RadixTrieTest extends IntKeyMapTest {

    @Override
    public IntKeyMap<String> getNewIntKeyMap() {
        return new RadixTrie<>();
    }

    @Test
    public void negative() {
        map.put(-1, "-one");
        assertTrue(map.containsKey(-1));
        assertEquals("-one", map.get(-1));
    }

    @Test
    public void testPutRangeNegative() {
        for (int i = 0; i < 100_000; ++i) {
            map.put(-i, Integer.toString(-i));
            assertEquals(i + 1, map.size());
            assertEquals(Integer.toString(-i), map.get(-i));
        }
        for (int i = 0; i < 100_000; ++i) {
            assertEquals(Integer.toString(-i), map.get(-i));
        }
    }

    @Test
    public void testPutRangeNegativeReverse() {
        for (int i = 100_000; i >= 0; --i) {
            map.put(-i, Integer.toString(-i));
            assertEquals(Integer.toString(-i), map.get(-i));
        }
        for (int i = 0; i <= 100_000; ++i) {
            assertEquals(Integer.toString(-i), map.get(-i));
        }
    }

    @Test
    public void randomlyAddAndRemoveValues() {
        randomlyAddAndRemoveValues(0, 10_000);
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
        public void testConstructor() {
            IntKeyMap<String> newOne = new RadixTrie<>(map);
            assertContainsAll(newOne);
        }

        @Test
        @SuppressWarnings("unchecked")
        public void testClone() {
            IntKeyMap<String> cloned = (IntKeyMap<String>) ((RadixTrie<String>) map).clone();
            assertContainsAll(cloned);
        }

        @Test
        public void testPutAll() {
            IntKeyMap<String> copy = new RadixTrie<>();
            copy.putAll(map);
            assertContainsAll(copy);
        }

        void assertContainsAll(IntKeyMap<String> copy) {
            assertEquals(map.size(), copy.size());
            map.forEach((i, v) -> assertEquals(v, copy.get(i)));
        }

    }

}
