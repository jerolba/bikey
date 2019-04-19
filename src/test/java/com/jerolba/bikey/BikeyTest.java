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

public class BikeyTest {

    @Test
    public void hashCodeTest() {
        Bikey<String, String> someNull = new Bikey<>(null, null);
        Bikey<String, String> some = new Bikey<>("1", "2");
        Bikey<String, String> other = new Bikey<>("1", "3");
        assertNotEquals(someNull.hashCode(), some.hashCode());
        assertNotEquals(someNull.hashCode(), other.hashCode());
        assertNotEquals(some.hashCode(), other.hashCode());
        assertNotEquals(some.hashCode(), new Bikey<>("1", "2"));
    }

    @Test
    public void equalsTest() {
        Bikey<String, String> some = new Bikey<>("1", "2");
        assertTrue(some.equals(some));
        assertFalse(some.equals(null));
        assertFalse(some.equals("foo"));
        assertFalse(some.equals(new Bikey<>("0", "1")));
        assertFalse(some.equals(new Bikey<>("1", "3")));
        assertFalse(some.equals(new Bikey<>(null, "2")));
        assertFalse(some.equals(new Bikey<>("1", null)));
        assertTrue(some.equals(new Bikey<>("1", "2")));

        Bikey<String, String> withNulls = new Bikey<>(null, null);
        assertFalse(withNulls.equals(new Bikey<>("1", "2")));
        assertFalse(withNulls.equals(new Bikey<>(null, "2")));
        assertFalse(withNulls.equals(new Bikey<>("1", null)));
        assertTrue(withNulls.equals(new Bikey<>(null, null)));
    }

    @Test
    public void toStringTest() {
        Bikey<String, String> some = new Bikey<>("1", "2");
        assertEquals("[1, 2]", some.toString());
    }

}
