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

import org.junit.jupiter.api.Test;

public class IntObjectEntryTest {

    @Test
    public void hashCodeTest() {
        IntObjectEntry<String> someNull = new IntObjectEntry<>(0, null);
        IntObjectEntry<String> some = new IntObjectEntry<>(1, "1");
        IntObjectEntry<String> other = new IntObjectEntry<>(2, "2");
        assertNotEquals(someNull.hashCode(), some.hashCode());
        assertNotEquals(someNull.hashCode(), other.hashCode());
        assertNotEquals(some.hashCode(), other.hashCode());
        assertNotEquals(some.hashCode(), new IntObjectEntry<>(1, "1"));
    }

    @Test
    public void equalsTest() {
        IntObjectEntry<String> some = new IntObjectEntry<>(1, "1");
        assertTrue(some.equals(some));
        assertFalse(some.equals(null));
        assertFalse(some.equals("foo"));
        assertFalse(some.equals(new IntObjectEntry<>(1, null)));
        assertFalse(some.equals(new IntObjectEntry<>(0, "1")));
        assertFalse(some.equals(new IntObjectEntry<>(2, "2")));
        assertTrue(some.equals(new IntObjectEntry<>(1, "1")));

        IntObjectEntry<String> withNull = new IntObjectEntry<>(1, null);
        assertFalse(withNull.equals(new IntObjectEntry<>(2, null)));
        assertFalse(withNull.equals(new IntObjectEntry<>(1, "1")));
        assertTrue(withNull.equals(new IntObjectEntry<>(1, null)));

    }

    @Test
    public void toStringTest() {
        IntObjectEntry<String> some = new IntObjectEntry<>(1, "2");
        assertEquals("1=2", some.toString());
    }

}
