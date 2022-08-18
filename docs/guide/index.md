---
title: Guide
---

# Data Adapter

DataAdapter is one of the core concepts of this framework.
It is an adapter for Java/Scala Beans and Maps.



## Features

- Every Java / Scala Bean  can be used for DataAdapter
- Bean member value lookup by java reflection
- DataAdapter has additional support for Java / Scala Maps
- DataAdapter can resolve Expressions on bean
- DataAdapter has change management by default
- DataAdapter implements java.util.Interface => Scala classes and maps can be used like java maps

## Example

```scala

import com.sfxcode.sapphire.data.DataAdapter

case class Author(name: String)
case class Book(id: Long, title: String, pages: Int, author: Author)

// create DataAdapter for sample case class
val scalaBook = Book(1, "Programming In Scala", 852, Author("Martin Odersky"))
val book = DataAdapter[Book](scalaBook)

// value(key) and updateValue(key, newValue) are used for bean property access and modification

val title = book.value("title")
// "Programming In Scala"

val titleString:String = book.stringValue("title")

book.updateValue("title", "Programming In Scala 3.0")
// title is updated

val newTitle = book.value("title")
// "Programming In Scala 3.0"

// getValue and updateValue for underlying class by dot syntax
val authorName = book.value("author.name")  // "Martin Odersky"


```


