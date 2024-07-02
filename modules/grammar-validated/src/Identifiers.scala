package com.perikov.cql.validated

import com.perikov.cql.grammar as gm

import cats.data.ValidatedNec
import cats.implicits.*
import scala.compiletime.*

trait Identifiers(val wrapped: gm.Identifiers) extends gm.Identifiers:
  type Error      = String
  type Identifier = ValidatedNec[String, wrapped.Identifier]
  def isKeyword(s: String): Boolean = false // TODO

  extension (s: String)
    def identifier: Identifier =
      val regex   =
        if (s.matches(constValue[UnqotedIdentifierRegex]))
        then s.validNec[String]
        else s"String '$s' does not match identifier regex".invalidNec
      val keyword =
        if isKeyword(s) then s"String '$s' is a reserved keyword".invalidNec
        else s.validNec[String]

      regex.map(wrapped.identifier)

    def quotedIdentifier: Identifier =
      if s.length() < constValue[MaxIdentifierLength] then wrapped.identifier(s).validNec
      else s"String '$s' is too long for an identifier".invalidNec



