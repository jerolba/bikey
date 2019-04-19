[![Build Status](https://circleci.com/gh/jerolba/bikey.svg?style=shield)](https://circleci.com/gh/jerolba/bikey) 
[![Codecov](https://codecov.io/gh/jerolba/bikey/branch/master/graph/badge.svg)](https://codecov.io/gh/jerolba/bikey/)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

<div align="center">
	<br>
	<img src="./config/bikey-logo.svg" width="140"/>	
    <span style="font-size: 25px"><h1>Bikey</h1></span>
</div>

Bikey implements Map and Set data structures on objects with composited keys **minimizing memory consumption**.

## Why Bikey collections?

Current collections libraries ([Guava](https://github.com/google/guava), [Commons Collection](https://commons.apache.org/proper/commons-collections/), [Eclipse Collections](https://github.com/eclipse/eclipse-collections)) have poor or not support to Maps and Sets of objects with two keys.

Implementing it manually with a `Map<R, Map<C, V>>` or a `Map<Pair<R, C>, V>` consumes a lot of memory and [choosing an incorrect hashCode function](https://medium.com/@jerolba/hashing-and-maps-87950eed673f) for Pair (or equivalent) class can [penalice memory and CPU consumption](https://medium.com/@jerolba/composite-key-hashmaps-1422e2e6cdbc).

**Bikey collection can reduce to 15%-30% of the memory consumed** by a traditional double map (depends on the map _fill rate_) with none or low penalization in access time.

##Some Quick Examples

`BikeyMap` is defined like a `Map` but everywhere a key must be used, you must provide both key values:

```java
BikeyMap<String, String, Integer> stock = new TableBikeyMap<>();
stock.put("shirt-ref-123", "store-76", 10);
stock.put("pants-ref-456", "store-12", 24);
...
stock.put("tie-ref-789", "store-23", 2);

Integer available = stock.get("shirt-ref-1234", "store-45");

//Total stock in store-123
stock.entrySet().stream()
     .filter(entry -> entry.getColumn().equals("store-123"))
     .mapToInt(entry -> entry.getValue())
     .sum();

//Total stock in pants-ref-457
stock.entrySet().stream()
     .filter(entry -> entry.getRow().equals("pants-ref-457"))
     .mapToInt(entry -> entry.getValue())
     .sum();

//All products included
Set<String> products = stock.rowKeySet();

//All stores included
Set<String> stores = stock.columnKeySet();

//Contains a product and store?
if (stock.containsKey("tie-ref-789", "store-23")) {
    ....
}

//Do something with each element in the map
stock.forEach((product, store, units) -> {
    System.out.println("Product " + product + " has " + units + " in store " + store);
});

```

To simplify the example `String`s has been used as keys, but any object that implements `equals` and `hashCode` can be used as row or column key.


## Contribute
Feel free to dive in! [Open an issue](https://github.com/jerolba/jfleet/issues/new) or submit PRs.

Any contributor and maintainer of this project follows the [Contributor Covenant Code of Conduct](https://github.com/jerolba/jfleet/blob/master/CODE_OF_CONDUCT.md).

## License
[Apache 2](https://github.com/jerolba/jfleet/blob/master/LICENSE.txt) © Jerónimo López