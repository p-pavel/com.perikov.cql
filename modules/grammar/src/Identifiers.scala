package com.perikov.cql.grammar
import language.`future-migration`

import scala.compiletime.ops
import ops.string.*
import ops.any.*
import ops.int.{`+` as _, ToString as _, *}
import scala.annotation.implicitNotFound

trait Identifiers:
  type Identifier
  type MaxIdentifierLength    = 48
  type UnqotedIdentifierRegex = "[a-zA-Z_][a-zA-Z0-9_]{0," + ToString[MaxIdentifierLength - 1] + "}"
  type ValidIdentifier

  extension (s: String)
    def identifier: Identifier
    def quotedIdentifier: Identifier

