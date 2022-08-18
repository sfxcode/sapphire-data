# Reports

For Report Generation [JasperReports](https://de.wikipedia.org/wiki/JasperReports) can be used.
It is based on the [itext library](https://de.wikipedia.org/wiki/IText).

For PDF reports the PdfExporter can be used.
As DataSource implementation is a AdapterDataSource available.

As visual report editor you can use [Jaspersoft Studio (Community Edition)](https://community.jaspersoft.com/project/jaspersoft-studio/releases).

## AdapterDataSource

DataAdapter is used for building a rewindable JasperReports DataSource.

In the Templates (.jrxml) items can be accessed like maps.

## PDFExporter

PDFExporter simplifies the export to PDF task. You need only a JasperReports File (compiled or uncompiled).

* Report Parameter are optional by creation a Map of key/values.
* DataSource is optional (use a AdapterDataSource if needed)
* optional exporter / report configuration

### Expressions in Fields

Because of Field validation in reports a workaround is needed for Expression values.

To use a Field with:

```
${_self.getValue('name')}
```

you have to replace Expression start with **#** and expression end with **@** .

```
#_self.getValue('name')@
```
### Example





