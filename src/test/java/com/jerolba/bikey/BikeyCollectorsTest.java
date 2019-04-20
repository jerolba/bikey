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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BikeyCollectorsTest {

    ProductStoreInfo p1s1day1 = new ProductStoreInfo("shirt-ref-1", "store-ref-1", 2, "2019-01-14", 1);
    ProductStoreInfo p1s2day1 = new ProductStoreInfo("shirt-ref-1", "store-ref-2", 1, "2019-01-14", 1);
    ProductStoreInfo p4s1day1 = new ProductStoreInfo("pants-ref-4", "store-ref-1", 0, "2019-01-14", 0);
    ProductStoreInfo p4s2day1 = new ProductStoreInfo("pants-ref-4", "store-ref-2", 4, "2019-01-14", 3);
    ProductStoreInfo p4s3day1 = new ProductStoreInfo("pants-ref-4", "store-ref-3", 5, "2019-01-14", 2);
    ProductStoreInfo p4s3day2 = new ProductStoreInfo("pants-ref-4", "store-ref-3", 3, "2019-01-15", 1);
    ProductStoreInfo p4s3day3 = new ProductStoreInfo("pants-ref-4", "store-ref-3", 2, "2019-01-16", 2);

    List<ProductStoreInfo> allInfo = asList(p1s1day1, p1s2day1, p4s1day1, p4s2day1, p4s3day1, p4s3day2,
            p4s3day3);

    @Nested
    class ToMapCollectorWithBikey {

        @Test
        public void mapEmtpyCollection() {
            Collection<ProductStoreInfo> empty = Collections.emptyList();
            BikeyMap<String, String, Integer> collect = empty.stream()
                    .collect(BikeyCollectors.toMap(
                            ProductStoreInfo::getProductStore,
                            ProductStoreInfo::getStock));
            assertTrue(collect.isEmpty());
        }

        @Test
        public void mapFilledCollection() {
            BikeyMap<String, String, Integer> totalStock = allInfo.stream()
                    .filter(ps -> ps.getDate().equals("2019-01-14"))
                    .collect(BikeyCollectors.toMap(
                            ProductStoreInfo::getProductStore,
                            ps -> ps.getStock()));
            assertStocks(totalStock);
        }

        @Test
        public void mapFilledCollectionWithSuppliedMapConstructor() {
            BikeyMap<String, String, Integer> totalStock = allInfo.stream()
                    .filter(ps -> ps.getDate().equals("2019-01-14"))
                    .collect(BikeyCollectors.toMap(
                            ProductStoreInfo::getProductStore,
                            ps -> ps.getStock(),
                            () -> new MatrixBikeyMap<>(10)));
            assertStocks(totalStock);
        }

        @Test
        public void defaultMapRaiseErrorIfRepeatedKey() {
            assertThrows(IllegalStateException.class, () -> {
                allInfo.stream()
                        .collect(BikeyCollectors.toMap(
                                ProductStoreInfo::getProductStore,
                                ProductStoreInfo::getStock));
            });
        }

        @Test
        public void mapFilledCollectionWithMerge() {
            BikeyMap<String, String, Integer> sales = allInfo.stream()
                    .collect(BikeyCollectors.toMap(
                            ProductStoreInfo::getProductStore,
                            ProductStoreInfo::getSales,
                            (a, b) -> a + b));
            assertSales(sales);
        }

        @Test
        public void mapFilledCollectionWithMergerAndSuppliedMapConstructor() {
            BikeyMap<String, String, Integer> sales = allInfo.stream()
                    .collect(BikeyCollectors.toMap(
                            ProductStoreInfo::getProductStore,
                            ProductStoreInfo::getSales,
                            (a, b) -> a + b,
                            () -> new MatrixBikeyMap<>(10)));
            assertSales(sales);
        }

    }

    @Nested
    class ToMapCollectorWithKeys {

        @Test
        public void mapEmtpyCollection() {
            Collection<ProductStoreInfo> empty = Collections.emptyList();
            BikeyMap<String, String, Integer> collect = empty.stream()
                    .collect(BikeyCollectors.toMap(
                            ProductStoreInfo::getProductRef,
                            ProductStoreInfo::getStoreRef,
                            ProductStoreInfo::getStock));
            assertTrue(collect.isEmpty());
        }

        @Test
        public void mapFilledCollection() {
            BikeyMap<String, String, Integer> totalStock = allInfo.stream()
                    .filter(ps -> ps.getDate().equals("2019-01-14"))
                    .collect(BikeyCollectors.toMap(
                            ProductStoreInfo::getProductRef,
                            ProductStoreInfo::getStoreRef,
                            ps -> ps.getStock()));
            assertStocks(totalStock);
        }

        @Test
        public void mapFilledCollectionWithSuppliedMapConstructor() {
            BikeyMap<String, String, Integer> totalStock = allInfo.stream()
                    .filter(ps -> ps.getDate().equals("2019-01-14"))
                    .collect(BikeyCollectors.toMap(
                            ProductStoreInfo::getProductRef,
                            ProductStoreInfo::getStoreRef,
                            ps -> ps.getStock(),
                            () -> new MatrixBikeyMap<>(10)));
            assertStocks(totalStock);
        }

        @Test
        public void defaultMapRaiseErrorIfRepeatedKey() {
            assertThrows(IllegalStateException.class, () -> {
                allInfo.stream()
                        .collect(BikeyCollectors.toMap(
                                ProductStoreInfo::getProductRef,
                                ProductStoreInfo::getStoreRef,
                                ProductStoreInfo::getStock));
            });
        }

        @Test
        public void mapFilledCollectionWithMerge() {
            BikeyMap<String, String, Integer> sales = allInfo.stream()
                    .collect(BikeyCollectors.toMap(
                            ProductStoreInfo::getProductRef,
                            ProductStoreInfo::getStoreRef,
                            ProductStoreInfo::getSales,
                            (a, b) -> a + b));
            assertSales(sales);
        }

        @Test
        public void mapFilledCollectionWithMergerAndSuppliedMapConstructor() {
            BikeyMap<String, String, Integer> sales = allInfo.stream()
                    .collect(BikeyCollectors.toMap(
                            ProductStoreInfo::getProductRef,
                            ProductStoreInfo::getStoreRef,
                            ProductStoreInfo::getSales,
                            (a, b) -> a + b,
                            () -> new MatrixBikeyMap<>(10)));
            assertSales(sales);
        }

    }

    private void assertStocks(BikeyMap<String, String, Integer> totalStock) {
        assertEquals(5, totalStock.size());
        assertEquals(2, totalStock.get("shirt-ref-1", "store-ref-1"));
        assertEquals(1, totalStock.get("shirt-ref-1", "store-ref-2"));
        assertEquals(0, totalStock.get("pants-ref-4", "store-ref-1"));
        assertEquals(4, totalStock.get("pants-ref-4", "store-ref-2"));
        assertEquals(5, totalStock.get("pants-ref-4", "store-ref-3"));
    }

    private void assertSales(BikeyMap<String, String, Integer> sales) {
        assertEquals(5, sales.size());
        assertEquals(1, sales.get("shirt-ref-1", "store-ref-1"));
        assertEquals(1, sales.get("shirt-ref-1", "store-ref-2"));
        assertEquals(0, sales.get("pants-ref-4", "store-ref-1"));
        assertEquals(3, sales.get("pants-ref-4", "store-ref-2"));
        assertEquals(5, sales.get("pants-ref-4", "store-ref-3"));
    }

    @Nested
    class ToSetCollector {

        @Test
        public void canCollectEmptyMapKeys() {
            BikeyMap<String, String, ProductStoreInfo> map = new TableBikeyMap<>();
            BikeySet<String, String> set = map.entrySet().stream()
                    .map(BikeyEntry::getKey)
                    .collect(BikeyCollectors.toSet());
            assertTrue(set.isEmpty());
        }

        @Test
        public void canCollectFilledMapKeys() {
            BikeySet<String, String> set = allInfo.stream()
                    .map(ProductStoreInfo::getProductStore)
                    .collect(BikeyCollectors.toSet());
            assertTrue(set.contains("shirt-ref-1", "store-ref-1"));
            assertTrue(set.contains("shirt-ref-1", "store-ref-2"));
            assertTrue(set.contains("pants-ref-4", "store-ref-1"));
            assertTrue(set.contains("pants-ref-4", "store-ref-2"));
            assertTrue(set.contains("pants-ref-4", "store-ref-3"));
            assertEquals(5, set.size());
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

    private static class ProductStoreInfo {

        private String productRef;
        private String storeRef;
        private int stock;
        private String date;
        private int sales;

        ProductStoreInfo(String productRef, String storeRef, int stock, String date, int sales) {
            this.productRef = productRef;
            this.storeRef = storeRef;
            this.stock = stock;
            this.date = date;
            this.sales = sales;
        }

        public Bikey<String, String> getProductStore() {
            return new Bikey<>(getProductRef(), getStoreRef());
        }

        public String getProductRef() {
            return productRef;
        }

        public String getStoreRef() {
            return storeRef;
        }

        public int getStock() {
            return stock;
        }

        public String getDate() {
            return date;
        }

        public int getSales() {
            return sales;
        }

    }
}
