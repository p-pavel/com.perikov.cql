package com.perikov.cql.grammar

trait Schema extends Identifiers, Types:
  type Keyspace

  type Table

  type ColumnDefs
  type ColumnDef
  type PrimaryKey[_ <: ColumnDefs]

  extension (identifier: Identifier) def keyspace: Keyspace

  extension (keyspace: Keyspace)
    def table(
        name: Identifier,
        columnDefs: ColumnDefs,
        primaryKey: PrimaryKey[columnDefs.type]
    ): Table

    def column(name: Identifier, typ: TypeBase, static: Boolean = false): ColumnDef

  def columnDefs(columns: ColumnDef*): ColumnDefs

  extension (columnDefs: ColumnDefs)
    def primaryKey(
        partitionKeys: Seq[Identifier],
        clusteringKeys: Seq[Identifier] = Seq.empty
    ): PrimaryKey[columnDefs.type]

end Schema
