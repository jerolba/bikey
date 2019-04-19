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

public class BikeyEntryTest {

    @Test
    public void hashCodeTest() {
        BikeyEntry<String, String, String> someNull = new BikeyEntry<>(null, null, null);
        BikeyEntry<String, String, String> some = new BikeyEntry<>("1", "2", "3");
        BikeyEntry<String, String, String> other = new BikeyEntry<>("1", "2", "4");
        assertNotEquals(someNull.hashCode(), some.hashCode());
        assertNotEquals(someNull.hashCode(), other.hashCode());
        assertNotEquals(some.hashCode(), other.hashCode());
        assertNotEquals(some.hashCode(), new BikeyEntry<>("1", "2", "3"));
    }

    @Test
    public void equalsTest() {
        BikeyEntry<String, String, String> some = new BikeyEntry<>("1", "2", "3");
        assertTrue(some.equals(some));
        assertFalse(some.equals(null));
        assertFalse(some.equals("foo"));
        assertFalse(some.equals(new BikeyEntry<>("0", "1", "2")));
        assertFalse(some.equals(new BikeyEntry<>("1", "3", "2")));
        assertFalse(some.equals(new BikeyEntry<>("1", "2", "0")));
        assertFalse(some.equals(new BikeyEntry<>(null, "2", "3")));
        assertFalse(some.equals(new BikeyEntry<>("1", null, "3")));
        assertFalse(some.equals(new BikeyEntry<>("1", "2", null)));
        assertTrue(some.equals(new BikeyEntry<>("1", "2", "3")));

        BikeyEntry<String, String, String> withNulls = new BikeyEntry<>(null, null, null);
        assertFalse(withNulls.equals(new BikeyEntry<>("1", "2", "3")));
        assertFalse(withNulls.equals(new BikeyEntry<>(null, "2", "3")));
        assertFalse(withNulls.equals(new BikeyEntry<>(null, null, "3")));
        assertTrue(withNulls.equals(new BikeyEntry<>(null, null, null)));
    }

    @Test
    public void toStringTest() {
        BikeyEntry<String, String, String> some = new BikeyEntry<>("1", "2", "3");
        assertEquals("[1, 2]=3", some.toString());
    }

}
