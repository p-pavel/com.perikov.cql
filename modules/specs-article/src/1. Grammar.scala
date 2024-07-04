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
  def partitionKey(
      column: Identifier,
      columns: Identifier*
  ): PartitionKey
  def primaryKey(
      partitionKey: PartitionKey,
      clusteringColumns: Identifier*
  ): PrimaryKey
end Schema

trait Types:
  type Type >: "text" | "int" | "uuid" // ...
  def map(k: Type, v: Type): Type
  def set(v: Type): Type
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
