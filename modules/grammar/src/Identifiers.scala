package com.perikov.cql.grammar

import scala.compiletime.ops
import ops.string.*
import ops.any.*
import ops.int.{`+` as _, ToString as _, *}

trait Identifiers:
  type Identifier
  type MaxIdentifierLength    = 48
  type UnqotedIdentifierRegex = "[a-zA-Z_][a-zA-Z0-9_]{0," + ToString[MaxIdentifierLength - 1] + "}"

  extension (s: String)
    def identifier: Identifier
    def quotedIdentifier: Identifier
