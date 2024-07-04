# General approach to using Scala for specifications

This paper is a draft written in a rush. It's probably full of mistakes and cut corners. I hope I can effectively communicate the main idea now and improve and expand upon it later.

## Motivation

- started thinking ~ 2012
- Scala is a powerful language
- became obsessive
- what I have now
- everyone tells me not needed and too complex
- show us BNF or textual desrciption
- running example: CQL schema

## Grammar-like specification

Ok, people keep telling me that Scala is too complex and simple BNF will be enough. Or better natural language (brrr) Please recall that BNF is essentially a notation for context-free grammars.

Let's go forward and write a model for Cassandra Schema in Scala in BNF style (the information in mix of natural language and BNF is taken from [official documentation](https://cassandra.apache.org/doc/latest/cassandra/developing/cql/index.html)):

```scala
trait Schema:
  type Table
  type Keyspace
  type Identifier
  type ColumnDefs
  type PrimaryKey

  def table(
      keyspace: Keyspace,
      name: Identifier,
      columns: ColumnDefs,
      primaryKey: PrimaryKey
  ): Table
  ```

We're just writing down our knowledge on what table definition is (let's focus on tables for now, we will need them to build queries at some 
point in the future).

So, let's continue our "recursive descent", defining some of the abstract types we used and adding more abstract types as we go:

```scala
trait Schema:
  type Table
  type Keyspace
  type Identifier
  type ColumnDefs
  type PrimaryKey
  type PartitionKey
  type ColumnDef
  type Type

  def table(
      keyspace: Keyspace,
      name: Identifier,
      columns: ColumnDefs,
      primaryKey: PrimaryKey
  ): Table

  def keyspace(name: Identifier): Keyspace
  def columndDefs(column: ColumnDef, columns: ColumnDef*): ColumnDefs
  def columnDef(name: Identifier, tpe: Type): ColumnDef
  def partitionKey(column: Identifier, columns: Identifier*): PartitionKey
  def primaryKey(partitionKey: PartitionKey, clusteringColumns: Identifier*): PrimaryKey
```

Does it look too complex to you compared to BNF? Does it communicate the idea? I think it does. Being formally checked, refinable
code as a bonus.

Here we have grammar rules represented as methods and grammar symbols represented as abstract types.

We have non-terminals (types that are used as return types) and non-terminals (types that are used as parameters only).

Non-terminals here are `Identifier` and `Type`, complex enough concepts on their own, presumed starting non-teminal is `Table`.

Our "grammars" are composable, terminals for one part beeing non-terminals for another.

For example, we can imagine something like `Type` grammar: 

```scala
trait Types:
  type Type >: "text" | "int" | "uuid" // ...
  def map(k: Type, v: Type): Type
  def set(v: Type): Type
end Types

trait Identifiers:
  type Identifier
  def identifier(s: String): Identifier

trait Grammar extends Schema, Types, Identifiers

def example1(using g: Grammar): g.Table =
  import g.*
  table(
    keyspace(identifier("my_keyspace")),
    name = identifier("my_table"),
    columns = columndDefs(
      columnDef(identifier("id"), "uuid"),
      columnDef(identifier("name"), "text"),
      columnDef(identifier("age"), "int")
    ),
    primaryKey(
      partitionKey(identifier("id")),
      identifier("name"),
      identifier("age")
    )
  )
```

`example1` can be seen as a [tagles-final](https://okmij.org/ftp/tagless-final/index.html) representation of a table schema info.

Due to Scala's flexibility there're lots of ways to achieve the same result. We can happily use composition instead of inheritance, for example.

We also completely ignored syntactic sweetness at this point, it can be added later if needed with extension methods etc, we prefer 
the most straitforward "grammar rules" for now.

What is missing? Let's look again at our `table` and `primaryKey` rules:

```scala
def table(
    keyspace: Keyspace,
    name: Identifier,
    columns: ColumnDefs,
    primaryKey: PrimaryKey,
): Table

def primaryKey(partitionKey: PartitionKey, clusteringColumns: Identifier*): PrimaryKey
```

Just looking at the definition we can immediatelly tell that they are wrong. To be correct we need some constraints on parameters.

Fore example, sets of identifiers in partition key and clustering columns should be disjoint in `primaryKey` rule.

All identifiers in `primaryKey` should be present in `columns` in `table` rule.

Context-free grammars are not enough to express these constraints. We need something more. Something resembling [attribute grammars](https://en.wikipedia.org/wiki/Attribute_grammar).

We will return to this idea in the following sections.

As a side note: Knuth considered attribute grammars as a general computational model, I should find his saying later :)

## Union-product types

- great way to model the problem, including handling different cases
- Scala traits as union-product types
- Flexibility of Scala traits and subtyping
- We need something more to be more precise as with context-free grammars
- We need dependent types, more precisely Σ-types

## Extending our specification

- Add validators
- they are like adding attributes to context-free grammar or like  Σ types
- we can separate validation into separate trait
- example of compile-time validation

## Talk abount refinement

- We can refine our specification
- Can be used for concern separation, specialisation and evolution

## Topics not covered

- lots actually, will put them here if there will be interest
- using with extended tagless final

## Conclusion

- everybody is doing essentially the same things, leaving no traces for community in business environment
- hope the approach here can provide general framework for problem analysis, design and implementation
- api's developed this way can be made precise, abstract enough to have little dependencies and shared in Scala community as a separate product

## Future plans

- want to finish CQL specs
- want to pindown some Cassandra patterns
- want to work on meta-programming tool for this framework 
- want to pindown some design patterns like event sourcing, CQRS, etc in this framework
- want to work on tools for running on OSGi

## About the author

- 30 years in software development
- became obsessed with specs and general approaches
- have 40 cents left
- join the discussion
- become a sponsor
- contact me at <pavel@perikov.com>
