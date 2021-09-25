# Changes

## Versions

### 1.2.1
* Dependency Updates

### 1.2.0
* Scala 3 Support
* moved from specs2 to munit

### 1.1.2
* moved from bintray to sonatype

### 1.1.1
* DataAdapter: updateValues (Map) added
* Data Adapter: automatic type conversion for update value

### 1.1.0
* DataAdapter: getValue returns null if exception occurs (error logged as warning)
* Added ValueHelper to DataAdapter
* Refactored: FieldProperties to package com.sfxcode.sapphire.data.wrapper

### 1.0.7
* DataAdapter supports now update with object value

### 1.0.6
* Bugfix for getValue and deeper hirachy than one
* Changes in childs now detected for deeper hirachy level

### 1.0.5
* BUGFIX Exception in ReflectionTools if name not present
* JavaFX 16

### 1.0.4
* revert jasper-el jakarta changes (not needed to use jakarta version now)

### 1.0.3
* scala 2.13.5
* Some dependency Updates

### 1.0.2
* jasper-el -> 10.0.0 (javax -> jakarta)

### 1.0.1
* added optional report support (jasperreports)

### 1.0.0
* Data Functions extracted from sapphire-core
* DataAdapter with java.util.Map Support

