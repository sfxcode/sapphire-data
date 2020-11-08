# Adapter

DataAdapter is one of the core concepts of this framework.
It is an adapter for Java/Scala Beans (Maps) with automatic JavaFX Binding.



## Features

- Every Java / Scala Bean  can be used for DataAdapter
- Bean member value lookup by java reflection
- DataAdapter has additional support for Java / Scala Maps
- DataAdapter can resolve Expressions on bean
- DataAdapter creates Properties needed for Binding on demand
- DataAdapter has change management by default
- DataAdapter implements java.util.Interface => Scala classes and maps can be used like java maps

## Example

```scala
case class Author(name: String)
case class Book(id: Long, title: String, pages: Int, author: Author)

// create DataAdapter for sample case class
val scalaBook = Book(1, "Programming In Scala", 852, Author("Martin Odersky"))
val book = DataAdapter[Book](scalaBook)

// getValue and updateValue are used for bean property access and modification
// getProperty, getStringProperty ... 
// are used for automatic create a JavaFX Property
val title = book.getValue("title")
// "Programming In Scala"

val titleProperty = book.getStringProperty("title")

book.updateValue("title", "Programming In Scala 3.0")
// title is updated, titleProperty as well

val newTitle = book.getValue("title")
// "Programming In Scala 3.0"

val newTitleFromProperty = titleProperty.getValue  // "Programming In Scala 3.0"

// getValue and updateValue for underlying class by dot syntax
val authorName = book.getValue("author.name")  // "Martin Odersky"


```



