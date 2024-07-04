# General approach to using Scala for specifications

This paper is a draft written in rush. It's probably fool of mistakes and cut corners. I hope I can communicate the main 
idea now and fix and extend it later.

## Motivation

- started thinking ~ 2012
- Scala is a powerful language
- became obsessive
- what I have now
- everyone tells me not needed and too complex
- show us BNF or textual desrciption
- running example: CQL schema

## Grammar-like specification

- People are talking about BNF, which is a notation for context-free grammar. Let's do in Scala
- example of schema definition
- talk about terminal and non-terminal symbols
- composability
- something is missed from expressiveness. We need attribute grammars to become more expressive

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
- want to pindown some design patterns like event sourcing, CQRS, etc in this framework
- want to work on tools for running on OSGi

## About the author

- 30 years in software development
- became obsessed with specs and general approaches
- have 40 cents left
- join the discussion
- become a sponsor
- contact me at pavel@perikov.com