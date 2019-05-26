[![Maven Central](https://img.shields.io/maven-central/v/com.jerolba/bikey.svg)](https://maven-badges.herokuapp.com/maven-central/com.jerolba/bikey)
[![Build Status](https://circleci.com/gh/jerolba/bikey.svg?style=shield)](https://circleci.com/gh/jerolba/bikey) 
[![Download](https://api.bintray.com/packages/jerolba/maven/bikey/images/download.svg)](https://bintray.com/jerolba/maven/bikey/_latestVersion)
[![Codecov](https://codecov.io/gh/jerolba/bikey/branch/master/graph/badge.svg)](https://codecov.io/gh/jerolba/bikey/)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Javadocs](https://javadoc.io/badge/com.jerolba/bikey.svg)](https://javadoc.io/doc/com.jerolba/bikey)

<div align="center">
	<br>
	<img src="./config/bikey-logo.svg" width="140"/>	
    <span style="font-size: 25px"><h1>Bikey</h1></span>
</div>

Bikey implements Map and Set data structures with two keys minimizing memory consumption.

## Why Bikey collections?

Current collections libraries ([Guava](https://github.com/google/guava), [Commons Collection](https://commons.apache.org/proper/commons-collections/), [Eclipse Collections](https://github.com/eclipse/eclipse-collections)) have poor or not support to Maps and Sets with two keys.

Implementing it manually with a `Map<R, Map<C, V>>`, `Map<Tuple<R, C>, V>` or a `Set<Tuple<R, C>>` consumes a lot of memory, and [choosing an incorrect hashCode function](https://medium.com/@jerolba/hashing-and-maps-87950eed673f) for Tuple (or equivalent) class can [penalize memory and CPU consumption](https://medium.com/@jerolba/composite-key-hashmaps-1422e2e6cdbc).

**Bikey Map collection can reduce to a 15%-30% of consumed memory** by a traditional double map (depending on the map _fill rate_) and **Bikey Set collection can reduce to a 1% of consumed memory by a Set\<Tuple\>**, with none or low penalization in access time.

## Some Quick Examples

`BikeyMap` API is defined like the [Map](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html) interface but everywhere a key is needed, you must provide both key values.

To simplify the example `String`s has been used as keys, but any object that implements `equals` and `hashCode` can be used as row or column key. You can also use any kind of object as value. I've used Integer to simplify the following code:

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

//Get all product/stores presents in the map
BikeySet<String, String> productStores = map.bikeySet();

//BikeySet<R, C> also implements Set<Bikey<R, C>>
Set<Bikey<String, String>> productStoresSet = map.bikeySet();

//Get products and stores with stock
BikeySet<String, String> withStock = stock.entrySet().stream()
    .filter(entry -> entry.getValue() > 0)
    .map(BikeyEntry::getKey)
    .collect(BikeyCollectors.toSet());

//Do something with each element in the map
stock.forEach((product, store, units) -> {
    System.out.println("Product " + product + " has " + units + " in store " + store);
});
```


`BikeySet` API is defined like the [Set](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html) interface but everywhere an element is used, changes to two values:

```java
BikeySet<String, String> avengerFilms = new TableBikeySet<>();
avengerFilms.add("Hulk", "The Avengers");
avengerFilms.add("Iron Man", "The Avengers");
avengerFilms.add("Thor", "Avengers: Age of Ultron");
avengerFilms.add("Thor", "Thor: Ragnarok");
avengerFilms.add("Captain America", "Avengers: Infinity War");
....

if (avengerFilms.contains("Iron Man", "Black Panther")) {
    ....
}

//Films in the Set
Set<String> filmsInSet = avengerFilms.columnKeySet();

//Avengers in the Set
Set<String> avengersInSet = avengerFilms.rowKeySet();

//Films with Iron Man
List<String> ironManFilms = avengerFilms.stream()
    .filter(entry -> entry.getRow().equals("Iron Man"))
    .map(Bikey::getColumn)
    .collect(toList());

//Call to a BiFunction for each element in the Set
bikeySet.forEach(this::doSomething);

public void doSomething(String avenger, String film) {
  ....
}
```

## Implementations

`BikeyMap<R, C ,V>` has two implementations:

- `TableBikeyMap<R, C ,V>`: optimized for memory consumption, and with performance similar to a double map or tuple map version.
- `MatrixBikeyMap<R, C V`: optimizes performance, but with the disadvantage of consuming a little more memory with low fill rates.

depending on your business logic, you can use one or the other. 

`MatrixBikeyMap` behaves like a matrix and grows quickly in memory consumption, but then it remains stable. It's recommended only if the fill rate is greater than 60% or access time to their elements is important. By default we recommend to use `TableBikey` implementation. 


## Dependency

Bikey is uploaded to Maven Central Repository and to use it, you need to add the following Maven dependency:

```xml
<dependency>
  <groupId>com.jerolba</groupId>
  <artifactId>bikey</artifactId>
  <version>0.9.0</version>
</dependency>
```

in Gradle:

`implementation 'com.jerolba:bikey:0.9.0'`

or download the single [jar](http://central.maven.org/maven2/com/jerolba/bikey/0.9.0/bikey-0.9.0.jar) from Maven Central Repository.

## Benchmarks

Execute your own benchmarks before deciding to use this library, but as a reference you can start with these numbers:

### Memory

Compared to `Map<R, Map<C, V>>` and `Map<Tuple<R, C>, V>`, the memory consumed filling a map with 10.000 x 1.000 elements is:

<img src="https://docs.google.com/spreadsheets/d/e/2PACX-1vSQ28bJxu3RYU0WwBWKmm1_d6sLM0I3aPvr5bctzsblGgHRvfvOSkczdoT-JXpAmXrD74DShTlzo1Um/pubchart?oid=2140734164&format=image"/>

Compared to `HashMap<R, HashSet<C>>` and `HashSet<Tuple<R, C>>` implementations, the memory consumed filling a Set with 10.000 x 1.000 elements is: 

<img src="https://docs.google.com/spreadsheets/d/e/2PACX-1vSQ28bJxu3RYU0WwBWKmm1_d6sLM0I3aPvr5bctzsblGgHRvfvOSkczdoT-JXpAmXrD74DShTlzo1Um/pubchart?oid=635532048&format=image"/>

### Performance

To create and fill randomly different maps in each implementation, the time spent is:

<img src="https://docs.google.com/spreadsheets/d/e/2PACX-1vRiwv5Uo_b2c7jklJn59b__EaUnNfnhakDaZUgjMue7tE9OL0IQPbwFmY7QR42VGCEH4jJJkHLIPpk2/pubchart?oid=1182671191&format=image"/>

To find randomly different maps in each implementation, the time spent is:

<img src="https://docs.google.com/spreadsheets/d/e/2PACX-1vRiwv5Uo_b2c7jklJn59b__EaUnNfnhakDaZUgjMue7tE9OL0IQPbwFmY7QR42VGCEH4jJJkHLIPpk2/pubchart?oid=1247212528&format=image"/>

To create and fill randomly a Set with 10.000 x 1.000 elements, the time spent is:

<img src="https://docs.google.com/spreadsheets/d/e/2PACX-1vSQ28bJxu3RYU0WwBWKmm1_d6sLM0I3aPvr5bctzsblGgHRvfvOSkczdoT-JXpAmXrD74DShTlzo1Um/pubchart?oid=817188927&format=image"/>

To check randomly the existence of each element in a Set with 10.000 x 1.000 elements, the time spent is:

<img src="https://docs.google.com/spreadsheets/d/e/2PACX-1vSQ28bJxu3RYU0WwBWKmm1_d6sLM0I3aPvr5bctzsblGgHRvfvOSkczdoT-JXpAmXrD74DShTlzo1Um/pubchart?oid=1242227435&format=image"/>
 

## Contribute
Feel free to dive in! [Open an issue](https://github.com/jerolba/bikey/issues/new) or submit PRs.

Any contributor and maintainer of this project follows the [Contributor Covenant Code of Conduct](https://github.com/jerolba/bikey/blob/master/CODE_OF_CONDUCT.md).

## License
[Apache 2](https://github.com/jerolba/bikey/blob/master/LICENSE.txt) © Jerónimo López
