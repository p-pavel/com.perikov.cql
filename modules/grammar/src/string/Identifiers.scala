package com.perikov.cql.grammar.string

import com.perikov.cql.grammar as gm

trait Identifiers extends gm.Identifiers:
  type Identifier = String

  extension (s: String)
    def identifier       = s
    def quotedIdentifier =
      val sym = "\""
      sym + s.replace(sym, sym + sym).nn + sym
end Identifiers
