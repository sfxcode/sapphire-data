# Sapphire Data

A Framework for data handling. It provides an adapter for beans and maps with JavaFX Property support.

## Sapphire Frameworks
Sapphire Data is heavily used in the sapphire ui frameworks:

* [sapphire-core](https://sfxcode.github.io/sapphire-core/)
* [sapphire-extension](https://sfxcode.github.io/sapphire-extension/)


## Cross Build

Build and tested against Scala 2.12/2.13 and JDK 11 - 14

## Framework Dependencies


### JavaFX

Java UI Application Framework as replacement for Swing.

Sapphire depends on OpenJFX base package for properties handling.

[https://openjfx.io](https://openjfx.io)

### Expression Language

Expressions are resolved by EL 3 [Tomcat Expression Language](https://tomcat.apache.org/tomcat-8.0-doc/elapi/index.html).

## Maven

Sapphire is published to Bintray and linked to Maven Central.

### Repository

```
resolvers += "sfxcode-bintray" at "https://dl.bintray.com/sfxcode/maven"

```

### Artifact

@@dependency[sbt,Maven,Gradle] {
  group="com.sfxcode.sapphire"
  artifact="sapphire-data_2.12"
  version="$project.version$"
}

## Licence

[Apache 2](https://github.com/sfxcode/sapphire-data/blob/master/LICENSE)

@@@ index

 - [adapter](adapter.md)
 - [expression_language](expression_language.md)
 - [reports](report/index.md)
 - [Changes ](changes.md)

@@@
