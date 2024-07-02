package com.perikov.cql.grammar

trait DDLStatements extends Identifiers, Types:
  type Keyspace
  type KeyspaceOptions

  type Table
  type TableOptions

  type ColumnDefs
  type ColumnDef
  type PrimaryKey[_ <: ColumnDefs]

  def defaultKeyspaceOptions: KeyspaceOptions
  def defaultTableOptions: TableOptions

  extension (identifier: Identifier)
    def keyspace(
        ifNotExists: Boolean,
        options: KeyspaceOptions = defaultKeyspaceOptions
    ): Keyspace

    def table(
        columnDefs: ColumnDefs,
        primaryKey: PrimaryKey[columnDefs.type],
        ifNotExists: Boolean = false,
        options: TableOptions = defaultTableOptions
    ): Table

    def column(typ: TypeBase, static: Boolean = false): ColumnDef

  def columnDefs(columns: ColumnDef*): ColumnDefs

  extension (columnDefs: ColumnDefs)
    def primaryKey(
        partitionKeys: Seq[Identifier],
        clusteringKeys: Seq[Identifier] = Seq.empty
    ): PrimaryKey[columnDefs.type]

end DDLStatements
