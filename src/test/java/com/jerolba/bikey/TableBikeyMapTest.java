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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TableBikeyMapTest extends BikeyMapTest {

    public BikeyMap<String, String, String> getNewBikeyMap() {
        return new TableBikeyMap<>();
    }

    @Nested
    class CopyMap {

        @BeforeEach
        void beforeEachTest() {
            map.put("1", "one", "1-one");
            map.put("1", "1", "1-1");
            map.put("35", "thirtyfive", "35-thirtyfive");
            map.put("100", "hundred", "100-hundred");
        }

        @Test
        public void testConstructor() {
            BikeyMap<String, String, String> newOne = new TableBikeyMap<>(map);
            assertContainsAll(newOne);
        }

        @Test
        @SuppressWarnings("unchecked")
        public void testClone() {
            Object clone = ((TableBikeyMap<String, String, String>) map).clone();
            BikeyMap<String, String, String> cloned = (BikeyMap<String, String, String>) clone;
            assertContainsAll(cloned);
        }

        @Test
        public void testPutAll() {
            BikeyMap<String, String, String> copy = new TableBikeyMap<>();
            copy.putAll(map);
            assertContainsAll(copy);
        }

        void assertContainsAll(BikeyMap<String, String, String> copy) {
            assertEquals("1-one", copy.get("1", "one"));
            assertEquals("1-1", copy.get("1", "1"));
            assertEquals("35-thirtyfive", copy.get("35", "thirtyfive"));
            assertEquals("100-hundred", copy.get("100", "hundred"));
            assertEquals(4, copy.size());
        }

    }
}
