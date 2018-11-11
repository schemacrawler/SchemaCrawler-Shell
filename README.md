[![Build Status](https://travis-ci.org/schemacrawler/SchemaCrawler-Shell.svg?branch=master)](https://travis-ci.org/schemacrawler/SchemaCrawler-Shell)

# ![SchemaCrawler](https://github.com/schemacrawler/SchemaCrawler/raw/master/schemacrawler-docs/logo/schemacrawler_logo.png?raw=true) SchemaCrawler Interactive Shell

> **Please see the [SchemaCrawler website](http://www.schemacrawler.com/) for more details.**

## About

SchemaCrawler is a free database schema discovery and comprehension tool. SchemaCrawler is available under a number of [licenses](http://sualeh.github.io/SchemaCrawler/license.html).

This project provides an interactive shell for SchemaCrawler comamnds. See [information on how to use SchemaCrawler Interactive Shell](https://www.schemacrawler.com/schemacrawler-shell.html) on the SchemaCrawler website.


## To Build and Run

Install [Graphviz](http://www.graphviz.org), which is a prerequisite for SchemaCrawler.

Build
```sh
mvn -Dcomplete clean install
```

Run
```sh
java -jar .\target\schemacrawler-shell-15.01.06.01-exec.jar
```
