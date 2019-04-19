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

import static com.jerolba.bikey.RadixHamTrie.newLeafNode;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.jerolba.bikey.RadixHamTrie.RadixHamTrieNode;

public class RadixHamTrieNodeTest {

    private static final int NVALUES = 1 << 5;

    @Nested
    class BasicOperations {

        private Random rnd = new Random();
        private int barKey = rnd.nextInt(32);
        private int fooKey = (barKey + 5) % 32;

        private RadixHamTrieNode node = newLeafNode(barKey, "bar");

        @Test
        public void mustBeCreatedWithValue() {
            assertFalse(node.isEmpty());
            assertEquals("bar", node.get(barKey));
            assertEquals(1, node.size());
        }

        @Test
        public void aJustCreatedCanBeEmptied() {
            assertEquals("bar", node.remove(barKey));
            assertTrue(node.isEmpty());
            assertEquals(0, node.size());
            assertNull(node.get(barKey));
        }

        @Test
        public void canAddAValue() {
            assertNull(node.set(fooKey, "foo"));
        }

        @Test
        public void inEmptiedOneCanAddNewValue() {
            node.remove(barKey);
            assertNull(node.set(1, "foo"));
            assertNotNull(node.get(1));
        }

        @Test
        public void canGetStoredValue() {
            node.set(fooKey, "foo");
            assertEquals("foo", node.get(fooKey));
        }

        @Test
        public void canRemoveCreatedValue() {
            assertEquals("bar", node.get(barKey));
            assertNotNull(node.remove(barKey));
            assertNull(node.get(barKey));
            assertTrue(node.isEmpty());
        }

        @Test
        public void canRemoveStoredValue() {
            node.set(fooKey, "foo");
            assertEquals("foo", node.get(fooKey));
            assertNotNull(node.remove(fooKey));
            assertNull(node.get(fooKey));
            assertFalse(node.isEmpty());
        }

        @Test
        public void removeReturnsPreviousValue() {
            assertEquals("bar", node.remove(barKey));
        }

        @Test
        public void anInexistentElementCanNotBeRemoved() {
            assertNull(node.remove(fooKey));
        }

    }

    @Nested
    class FillOperations {

        private Random rnd = new Random();
        private int itKey = rnd.nextInt(NVALUES);

        @Test
        public void canFillTheArray() {
            RadixHamTrieNode node = newLeafNode(itKey, itKey);
            for (int i = 1; i < NVALUES; i++) {
                itKey = (itKey + 1) % NVALUES;
                assertNull(node.set(itKey, itKey));
            }
            for (int i = 0; i < NVALUES; i++) {
                assertEquals(i, node.get(i));
            }
        }

        @Test
        public void canFillTheArrayInAnyOrder() {
            List<Integer> all = range(0, NVALUES).boxed().collect(toList());
            Collections.shuffle(all);
            Iterator<Integer> it = all.iterator();
            int first = it.next();
            RadixHamTrieNode node = newLeafNode(first, first);
            while (it.hasNext()) {
                int next = it.next();
                assertNull(node.set(next, next));
            }
            for (int i = 1; i < NVALUES; i++) {
                assertEquals(i, node.get(i));
            }
        }

        @Test
        public void aFilledArrayCanBeEmptied() {
            RadixHamTrieNode node = newLeafNode(itKey, itKey);
            for (int i = 1; i < NVALUES; i++) {
                itKey = (itKey + 1) % NVALUES;
                assertNull(node.set(itKey, itKey));
            }

            for (int i = 0; i < NVALUES; i++) {
                assertEquals(i, node.remove(i));
                // Remaining elements are still pressent
                for (int j = i + 1; j < NVALUES; j++) {
                    assertEquals(j, node.get(j));
                }
            }
            assertTrue(node.isEmpty());
            for (int i = 0; i < NVALUES; i++) {
                assertNull(node.get(i));
            }
        }

        @Test
        public void aFilledArrayCanBeEmptiedBackward() {
            RadixHamTrieNode node = newLeafNode(itKey, itKey);
            for (int i = 1; i < NVALUES; i++) {
                itKey = (itKey + 1) % NVALUES;
                assertNull(node.set(itKey, itKey));
            }
            for (int i = NVALUES - 1; i >= 0; i--) {
                assertEquals(i, node.remove(i));
                // Remaining elements are still pressent
                for (int j = 1; j < i; j++) {
                    assertEquals(j, node.get(j));
                }
            }
            assertTrue(node.isEmpty());
            for (int i = 0; i < NVALUES; i++) {
                assertNull(node.get(i));
            }
        }

        @Test
        public void aFilledArrayCanBeEmptiedRandomly() {
            List<Integer> all = range(0, NVALUES).boxed().collect(toList());
            Collections.shuffle(all);

            RadixHamTrieNode node = newLeafNode(itKey, itKey);
            for (int i = 1; i < NVALUES; i++) {
                itKey = (itKey + 1) % NVALUES;
                assertNull(node.set(itKey, itKey));
            }
            for (int it = 0; it < all.size(); it++) {
                assertEquals(all.get(it), node.remove(all.get(it)));
                // Remaining elements are still pressent
                for (int i = it + 1; i < all.size(); i++) {
                    assertEquals(all.get(i), node.get(all.get(i)));
                }

            }
            assertTrue(node.isEmpty());
            for (int i = 0; i < NVALUES; i++) {
                assertNull(node.get(i));
            }
        }

    }

    @Nested
    class Iteration {

        private RadixHamTrieNode node;

        @BeforeEach
        void before() {
            node = newLeafNode(0, "zero");
            node.set(2, "two");
            node.set(5, "five");
            node.set(10, "ten");
            node.set(31, "thirty one");
        }

        @Test
        public void foreachWithConsumerHasAllElements() {
            Set<String> found = new HashSet<>();
            node.forEach((i, v) -> {
                found.add(i + " - " + v);
            });
            assertContainsAll(found);
        }

        @Test
        public void iterateValues() {
            Set<String> found = new HashSet<>();
            Iterator<IntObjectEntry<?>> it = node.iterator();
            while (it.hasNext()) {
                IntObjectEntry<?> next = it.next();
                found.add(next.getIntKey() + " - " + next.getValue());
            }
            assertContainsAll(found);
        }

        @Test
        public void iterateWithoutHasNext() {
            Iterator<IntObjectEntry<?>> it = node.iterator();
            IntObjectEntry<?> v = it.next();
            assertEquals(0, v.getIntKey());
            assertEquals("zero", v.getValue());

            v = it.next();
            assertEquals(2, v.getIntKey());
            assertEquals("two", v.getValue());

            v = it.next();
            assertEquals(5, v.getIntKey());
            assertEquals("five", v.getValue());

            v = it.next();
            assertEquals(10, v.getIntKey());
            assertEquals("ten", v.getValue());

            v = it.next();
            assertEquals(31, v.getIntKey());
            assertEquals("thirty one", v.getValue());

            assertFalse(it.hasNext());

            assertThrows(NoSuchElementException.class, () -> {
                it.next();
            });
        }

        @Test
        public void forEachKeyGetsAllKeys() {
            Set<Integer> found = new HashSet<>();
            node.forEachKey(k -> found.add(k));
            assertEquals(5, found.size());
            assertTrue(found.containsAll(Arrays.asList(0, 2, 5, 10, 31)));
        }

        @Test
        public void emptyNodeDoesNotHaveNextIteration() {
            RadixHamTrieNode node = newLeafNode(0, "foo");
            Iterator<IntObjectEntry<?>> it = node.iterator();
            assertTrue(it.hasNext());
            assertEquals("foo", it.next().getValue());

            node.remove(0);
            Iterator<IntObjectEntry<?>> itEmpty = node.iterator();
            assertFalse(itEmpty.hasNext());
            assertThrows(NoSuchElementException.class, () -> {
                itEmpty.next();
            });

        }

        void assertContainsAll(Set<String> found) {
            assertTrue(found.contains("0 - zero"));
            assertTrue(found.contains("2 - two"));
            assertTrue(found.contains("5 - five"));
            assertTrue(found.contains("10 - ten"));
            assertTrue(found.contains("31 - thirty one"));
            assertEquals(5, found.size());
        }

    }

    @Test
    void testToStringOneElement() {
        RadixHamTrieNode node = newLeafNode(2, "foo");
        assertEquals("{2=foo}", node.toString());
    }

    @Test
    void testToStringEmpty() {
        RadixHamTrieNode node = newLeafNode(2, "foo");
        node.remove(2);
        assertEquals("{}", node.toString());
    }

    @Test
    void testToStringMultipleElements() {
        RadixHamTrieNode node = newLeafNode(2, "foo");
        node.set(10, "bar");
        assertEquals("{2=foo, 10=bar}", node.toString());
    }

    @Test
    void testMultipleLevels() {
        RadixHamTrieNode node = newLeafNode(2, "foo");
        node.set(10, "bar");
        RadixHamTrieNode parent = node.createParentNodeWith(34, "baz", 5);
        assertEquals("{{2=foo, 10=bar}, {34=baz}}", parent.toString());
        RadixHamTrieNode newLeaf = (RadixHamTrieNode) parent.get(1);
        assertEquals("{34=baz}", newLeaf.toString());
    }

}
