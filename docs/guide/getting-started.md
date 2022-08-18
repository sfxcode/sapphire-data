# Getting started

## SBT

Add dependency to your build.sbt file

```
libraryDependencies += "com.sfxcode.sapphire" % "sapphire-data" % "2.0.0"
```

## Create Data Adapter

```scala
val book = DataAdapter[Book](scalaBook)
```

## Access and update wrapped object data

```scala
val title = book.value("title")
book.updateValue("title", "Programming In Scala 3.0")
```

