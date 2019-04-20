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

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class BikeyCollectorsTest {

    @Test
    public void canCollectEmptyMapKeys() {
        BikeyMap<String, String, Integer> map = new TableBikeyMap<>();
        BikeySet<String, String> set = map.entrySet().stream()
                .map(BikeyEntry::getKey)
                .collect(BikeyCollectors.toSet());
        assertTrue(set.isEmpty());
    }

    @Test
    public void canCollectFilledMapKeys() {
        BikeyMap<String, String, Integer> map = new TableBikeyMap<>();
        map.put("1", "2", 3);
        map.put("3", "5", 8);
        map.put("5", "7", 12);
        BikeySet<String, String> set = map.entrySet().stream()
                .map(BikeyEntry::getKey)
                .collect(BikeyCollectors.toSet());
        assertTrue(set.contains("1", "2"));
        assertTrue(set.contains("3", "5"));
        assertTrue(set.contains("5", "7"));
        assertEquals(3, set.size());
    }

    @Test
    public void canCollectWithParallelStream() {
        Set<Bikey<Integer, Integer>> set = new HashSet<>();
        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 20; j++) {
                set.add(new Bikey<>(i, j));
            }
        }
        BikeySet<Integer, Integer> collected = set.parallelStream()
                .filter(bikey -> bikey.getRow() < 100)
                .collect(BikeyCollectors.toSet());
        assertEquals(20 * 100, collected.size());
    }

}
