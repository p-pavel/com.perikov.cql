# CQL support for Scala 3

*There's a small and messy article on the general topic of using Scala as specification language at [](https://github.com/p-pavel/com.perikov.cql/blob/main/articles/GeneralApproachToScalaSpecification.md)*

CQL stands for Cassandra Query Language. It is a SQL-like language for working with Cassandra database.

This project is intended to provide support for working with CQL in Scala 3.

More importantly I want to demonstrate the interplay of the following concepts:

- using Scala's type system to provide complete specification of the problem domain. The user should not
  have to look into any external documentation and autocompletion should lead him. The implementor should get as much compiler support as possible
- show that "interface/implementation" separation is not a duality and we can have an hirerachy of refined specifications
- separation of concerns and dependency management in the presense of "typeclasses" as implemented in Scala
- leveraging Scala's metaprogramming features to push "fail early" principle to the extreme - detect as many errors as possible
  at compile time
- combine all of the above for the usage inside the  OSGi container with proper capabilities/requirements management

If you're interested plesae consider becoming a contributor or [sponsor](https://github.com/sponsors/p-pavel). Sponsorship is highly appreciated and is currently the only way to continue working on the project for me.

## Project structure

We start with CQL "grammar" project describing CQL itself.

Then we provide a couple of implementations:

- "grammar-validated" wraps existing implementation of `Grammar` with cats' `Validated` thus allowing sharing of the validation logic
- "compiletime" uses metarprogramming facilities to check schema and statement descriptions in compile time
- "datastax" provides concrete implementation for working with statements over Datastax driver

## General design approach

We start with "Grammar" of the problem at hanf. This is a set of types and typeclasses that describe the problem domain.

Then we provide a couple of implementations. The first one is "grammar-validated" which wraps existing implementation of `Grammar` with cats' `Validated` thus allowing sharing of the validation logic.

As the last step we provide a compile-time API based on "Validated" version to build literal expressions with errors checked at compile time.

## Future work

- I plan to implement compiletime extraction of schema from running Cassandra instance for producing binary artifact
with query values (or failing at compile time). On schema change just recompile this artifact and get the errors checked
- I plan to implement few patterns for Cassandra usage like "Cache", "Topic" etc.
