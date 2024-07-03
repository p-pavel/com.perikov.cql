package com.perikov.cql.grammar.string
import com.perikov.cql.grammar as gm

trait Schema extends gm.Schema, Identifiers, Types:
  type Keyspace = String
  case class PrimaryKeyData(partitionKeys: Seq[Identifier], clusteringKeys: Seq[Identifier])
  type PrimaryKey[_ <: ColumnDefs] = PrimaryKeyData
  type Table = TableData

  case class TableData(name: Identifier, keyspace: Keyspace, columnDefs: ColumnDefs, primaryKey: PrimaryKeyData)

  type ColumnDefs = Seq[ColumnDef]
  case class ColumnDef(name: Identifier, typ: TypeBase, static: Boolean)

  extension (identifier: Identifier) def keyspace: Keyspace = identifier

  extension (keyspace: Keyspace)
    def table(
        name: Identifier,
        columnDefs: ColumnDefs,
        primaryKey: PrimaryKey[columnDefs.type]
    ): Table = TableData(name, keyspace, columnDefs, primaryKey)

    def column(name: Identifier, typ: TypeBase, static: Boolean = false): ColumnDef = ColumnDef(name, typ, static)

  def columnDefs(columns: ColumnDef*) = columns

  extension (columnDefs: ColumnDefs)
    def primaryKey(
        partitionKeys: Seq[Identifier],
        clusteringKeys: Seq[Identifier]) = PrimaryKeyData(partitionKeys, clusteringKeys)
end Schema


class Grammar extends Types, Schema, DMLStatements
