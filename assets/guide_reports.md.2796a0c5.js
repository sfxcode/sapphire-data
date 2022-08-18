import{_ as e,c as a,o as r,a as s}from"./app.fbc4f041.js";const x=JSON.parse('{"title":"Reports","description":"","frontmatter":{},"headers":[{"level":2,"title":"AdapterDataSource","slug":"adapterdatasource"},{"level":2,"title":"PDFExporter","slug":"pdfexporter"},{"level":3,"title":"Expressions in Fields","slug":"expressions-in-fields"},{"level":3,"title":"Example","slug":"example"}],"relativePath":"guide/reports.md","lastUpdated":1660861921000}'),t={name:"guide/reports.md"},o=s(`<h1 id="reports" tabindex="-1">Reports <a class="header-anchor" href="#reports" aria-hidden="true">#</a></h1><p>For Report Generation <a href="https://de.wikipedia.org/wiki/JasperReports" target="_blank" rel="noreferrer">JasperReports</a> can be used. It is based on the <a href="https://de.wikipedia.org/wiki/IText" target="_blank" rel="noreferrer">itext library</a>.</p><p>For PDF reports the PdfExporter can be used. As DataSource implementation is a AdapterDataSource available.</p><p>As visual report editor you can use <a href="https://community.jaspersoft.com/project/jaspersoft-studio/releases" target="_blank" rel="noreferrer">Jaspersoft Studio (Community Edition)</a>.</p><h2 id="adapterdatasource" tabindex="-1">AdapterDataSource <a class="header-anchor" href="#adapterdatasource" aria-hidden="true">#</a></h2><p>DataAdapter is used for building a rewindable JasperReports DataSource.</p><p>In the Templates (.jrxml) items can be accessed like maps.</p><h2 id="pdfexporter" tabindex="-1">PDFExporter <a class="header-anchor" href="#pdfexporter" aria-hidden="true">#</a></h2><p>PDFExporter simplifies the export to PDF task. You need only a JasperReports File (compiled or uncompiled).</p><ul><li>Report Parameter are optional by creation a Map of key/values.</li><li>DataSource is optional (use a AdapterDataSource if needed)</li><li>optional exporter / report configuration</li></ul><h3 id="expressions-in-fields" tabindex="-1">Expressions in Fields <a class="header-anchor" href="#expressions-in-fields" aria-hidden="true">#</a></h3><p>Because of Field validation in reports a workaround is needed for Expression values.</p><p>To use a Field with:</p><div class="language-"><button class="copy"></button><span class="lang"></span><pre><code><span class="line"><span style="color:#A6ACCD;">\${_self.getValue(&#39;name&#39;)}</span></span>
<span class="line"><span style="color:#A6ACCD;"></span></span></code></pre></div><p>you have to replace Expression start with <strong>#</strong> and expression end with <strong>@</strong> .</p><div class="language-"><button class="copy"></button><span class="lang"></span><pre><code><span class="line"><span style="color:#A6ACCD;">#_self.getValue(&#39;name&#39;)@</span></span>
<span class="line"><span style="color:#A6ACCD;"></span></span></code></pre></div><h3 id="example" tabindex="-1">Example <a class="header-anchor" href="#example" aria-hidden="true">#</a></h3>`,17),p=[o];function i(n,l,d,c,u,h){return r(),a("div",null,p)}const _=e(t,[["render",i]]);export{x as __pageData,_ as default};