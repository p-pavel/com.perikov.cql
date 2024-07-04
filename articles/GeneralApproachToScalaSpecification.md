# General approach to using Scala for specifications

This paper is a draft written in a rush. It's probably full of mistakes and cut corners. I hope I can effectively communicate the main idea now and improve and expand upon it later if there will be interest.

## Motivation

I started thinking about the idea of formalizing the use of programming languages as specification tools around 2012, with Agda as the playground. The idea was to explore
the possibilities for providing specs not for traditional "mathematical" things but for 
business software design patterns.

At that time I just played with the idea (reinventing tagless-final in the process :)) and didn't have any practical use for it, on of the reasons Agda being a bit esoteric beast.  

Over the years, as I continued working in software development and with the emergence of Scala as a general-purpose language that is expressive enough to be used as a specification language, I became increasingly obsessed with the idea.

When looking at what happens in "business software" projects, such as reinventing the wheel repeatedly, oftenl lack of supported documentation, lack of knowledge sharing, and absence of formal specifications, I believe that using Scala as a specification, documentation and 
implementation language can potentially provide significant benefits.

Here are some quick philosophical notes on the topic, which I plan to expand upon.

## Grammar-like specification

People keep telling me that Scala is too complex and that a simple BNF will be enough.

Or that natural language is the best for specs (everyone can understand it, right?).

Or that specifications are of no use at all.

Let's see how complex it is to specify something in Scala compared to BNF that is often mentioned.

Please recall that BNF is essentially a notation for context-free grammars.

Let's move forward and write a model for Cassandra Schema in Scala using BNF style (the information, in a mix of natural language and BNF, is taken from the [official documentation](https://cassandra.apache.org/doc/latest/cassandra/developing/cql/index.html)):

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

We're writing down our knowledge on what table definition is (let's focus on tables for now, we will need them to build queries at some point in the future).

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

Does it look too complex compared to BNF? I think now.

Does it communicate the idea? I think it does. 

Being formally checked, refinable and implementable code as a bonus.

If we copy-paste some original documentatin to scaladoc, we could have a complete source of information.

Here we have grammar rules represented as methods and grammar symbols represented as abstract types.

We have non-terminals (types that are used as return types) and non-terminals (types that are used as parameters only).

Non-terminals here are `Identifier` and `Type`, complex enough concepts on their own, presumed starting non-teminal is `Table`.

Our "grammars" are composable, terminals for one part being non-terminals for another.

For example, we can imagine something like `Type` grammar: 

```scala
trait Types:
  type Type >: "text" | "int" | "uuid" // ...
  def map(k: Type, v: Type): Type
  def set(v: Type): Type
  // ... 
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

We also completely ignored syntactic sweetness at this point, it can be added later if needed with extension methods etc, we prefer the most straitforward "grammar rules" for now.

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

Let's now take another perspective on Scala's traits.

For now we had single "rule" for every non-terminal, but this shouldn't  always be the case.
Let's think what we can do with `Identifier` type.

```scala
trait Identifiers:
  type Identifier
  def quotedIdentifier(s: String): Identifier
  def unqoutedIdentifier(s: String): Identifier
end Identifiers
```

(CQL's quoted and unqouted identifiers actually have different behavior: the latter being case-insensitive, and some of them being reserved words)

We can look at a trait as a union of products type, methods being "variants" and their
arguments forming a product.

Probably `Identifiers` are not the best example (each product have only one type), but you got
the idea.

This approach is much more flexible compared to `enum` types due to the flexibility of Scala's traits, that can be inherited, composed (and `export`ed) etc, so we can easily build complex types from "components".

Union and product types are great tools to model not only data structure but also computation structure: the cases that can occur during execution of your program.

Methods define branches of your program:

```scala
trait MyExecutionBranches:
  type Result
  type Balance <: Result
  def processDeposit(amount: BigDecimal): Result
  def processWithdrawal(amount: BigDecimal): Result
  def checkBalance: Balance
  def unknownRequest: Result

class Program(val use: (b: MyExecutionBranches) ?=> b.Result ) extends AnyVal
```

`Program` being a tagless-final value.

I also did some experiments with macros and annotations to annotate methods and automatically generate dispatchers, pretty printers and other mechanical implementations using macro reflection, but this deserves a separate paper.

Looking at the situation in this perspective, we can notice the same problem as with
grammar-based approach: we need more constraints.

Product types (methods) can't express something more strict that Cartesian products, so what
what can we do? [Σ types](https://en.wikipedia.org/wiki/Dependent_type) to the rescue.

Classically Σ-type is a combination of value `a` of type `A`, the function `Prop` from `A` to the set of types and the value `Prop(a)` being the proof that the proposition is true for `a`.

The formal definition can be found in the link above.

We can generalize this idea. We'll do it in the next section.

## Restricting the specification

TODO: this section should be much longer and have more examples that are better
provided with code. For now, just show the general idea

We need to add constraints to our "rules" or "product types".

Let's see what we can do in Scala:

```scala
trait WithValidators:
  type PrimaryKey
  type PartitionKey
  type Identifier
  type Identifiers

  def identifiers(ids: Identifier*): Identifiers

  type ValidIdentifier[S <: String]
  type ValidPrimaryKey[_ <: Identifiers, _ <: Identifiers]

  def primaryKey(
      partitionKey: Identifiers,
      clusteringColumns: Identifiers
  )(using
      ValidPrimaryKey[partitionKey.type, clusteringColumns.type]
  ): PrimaryKey
  
  def identifier(s: String)(using ValidIdentifier[s.type]): Identifier
  ```

This way we overcome the limitations of context-free grammars and cartesian types,
providing additional constraints that depends on the exact values of the parameters (in theory).
  
please note that here we don't provide any `given` they can be provided in separate 
hierarchy. "Validators" themselves can be moved to separate hierarchy, too, parameterising context-free part with validator part.

There's an important use case for this validator approach: compile-time checks

### Example of compile time validation

Say we start with the following `Identifiers` spec:

```scala
trait Identifiers:
  type Identifier
  type ValidIdentifier[S <: String]
  def identifier(s: String)(using ValidIdentifer[s.type]): Identifier
```

Now we can provide a compile time validateion:

```scala

trait CompiletimeIdentifiers extends Identifiers:
  opaque type Identifier = String
  opaque type ValidIdentifier[S <: String] = Unit
  inline given validIdentifier[S <: String]: ValidIdentifier[S] = 
    import compiletime.*
    import compiletime.ops.string.*
    inline constValueOpt[Matches[S, "[a-zA-Z_][a-zA-Z0-9_]{0,47}"]] match
      case Some(true) =>  ()
      case Some(false) => error("Invalid identifier")
      case None => error("Can't check the identifier at compile time")

def example3(c: CompiletimeIdentifiers) =
  import c.{*, given}
  val i1 = identifier("my_table")
  val i2 = identifier("1a") // fails in runtime
```

## Talk abount refinement

The flexibility of traits opens many possibilities for refining both the specification
and implementation. Starting from the completely abstract types we can further refine them with
type bounds, provide subtyping dependencies, and type level parameters etc.

This way we can provide "specifications for specifications" and pass the work to others to implement/specify/refine parts of the problem.

## Topics not covered

Lots, actually. In the future I hope to:

- provide the complete CQL spec as an example along with compile-time checked schema definition
- talk about the role of tagless-final and interplay with specs described above
- talk more about the role of type bounds and their relation to validators
- phantom types. Database schema doesn't need to present in runtime at all and can be completely put into typelevel.

## Conclusion

From my experience lots of work made in software development leaves no traces that can be represented as formal artefacts independent of particular implementation.

There're also lots of duplication of essentially
the same functionality in different projects, that could be reused if proper abstractions
were available.

I believe that using Scala as your specification language, not just implementation language, can
potentially bring significant benefits:

- consise, complete, compiler checkable documentation
- knowledge and practices sharing
- less errors in implementation
- potential to leverage term deriving
- ability to delegate the work on particular parts of both "specs/interafaces" and implementation
- utilizing the power of Scala's type system to provide more static guarantees more early (
  idealy, at code typing stage with the language server like Metals)

I hope that notes can be a starting point for the discussion.

## Future plans

I personally have the following plans in this area:

- provide specification for CQL queries and type-level CQL schema with compile-time validation
- provide the implementation based on datastax driver
- improve and publish small macro library to extract information from traits and generate
some trivial implementations (like pretty printers, dispatchers etc)
- capture some Cassandra usage patterns (like implementing caches...)
- captue some general design and architecture patterns in modern software development
that are repeated over and over again "by simple textual description". I'm talking things like
event sourcing, CQRS, etc.

My other areas of interest include OSGi and Scala, trying to do something with microservices
catastrophe, provide tool to automate all aspects of targeting OSGi containers for deployment,
tools to automate bringing existing Scala projects to OSGi, etc.

In some distant future I also very interested in researching the possibility of IDE
that takes focus from "library dependency management" to what is really needed: package dependency management.

## About the author

I started working in software development in 1994. Since I worked professionally with 
languages like C++/C, Java, Scala, Smalltalk, Python, Haskell, Agda, C# etc, in roles 
from software developer to the lead for several groups of 80+ developer in total, with systems ranging from distributed realtime simulation to banking software.

If you're interested, please join the [discussion](https://github.com/p-pavel/com.perikov.cql/discussions/2)

If you're interested in my future work in this area, please consider [becoming a sponsor](https://github.com/sponsors/p-pavel).


You can contact me at <pavel@perikov.com>
