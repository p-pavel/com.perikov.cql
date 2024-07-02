# CQL support for Scala 3

This project demonstrates the approach to problem space modeling with Scala.

With starts with CQL "grammar" project describing CQL itself.

Then we provide a couple of implentations:

- "grammar-validated" wraps existing implementation of Grammar with cats' Validated
- "compiletime" uses metarprogramming facilities to check schema and statement descriptions in compile time
- "datastax" provides concrete implementation for working with statements over Datastax driver

We also plan compiletime extraction of schema from running Cassandra instance

