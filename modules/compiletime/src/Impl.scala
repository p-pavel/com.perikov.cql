package com.perikov.cql.compiletime
private object Impl:
  import scala.quoted.*
  opaque type Id = Unit

  opaque type Identifier[S <: String] <: Id = Id 
  def identifierImpl(s: Expr[String])(using Quotes) = ???
