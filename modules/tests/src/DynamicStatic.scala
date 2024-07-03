import scala.compiletime.*
trait SchemaValidation:
  type ValidIdentifier[_ <: String]
  inline given validIdentier[s <: String]: ValidIdentifier[s]

object StaticSchemaValidation extends SchemaValidation:
  opaque type ValidIdentifier[_ <: String] = Unit
  inline given validIdentier[s <: String]: ValidIdentifier[s] = 
    import compiletime.ops.string.*
    inline constValueOpt[Matches[s, "[a-zA-Z_][a-zA-Z0-9_]*"]] match
      case Some(true) => ()
      case Some(false) => compiletime.error("Identifier is invalid")
      case None => compiletime.error("Could not determine if identifier is valid")
    


trait Schema:
  type Identifier
  type Table
  type Type
  type ColumnDef
  type ColumnDefList
  type PrimaryKey
  type PartitionKey
  val schemaValidation: SchemaValidation
  export schemaValidation.*

  def table(
      name: Identifier,
      columns: ColumnDefList,
      PrimaryKey: PrimaryKey
  ): Table

  extension (s: String) def identifier(using ValidIdentifier[s.type]): Identifier

  extension (i: Identifier) def ::(t: Type): ColumnDef

  def columnDefList(column: ColumnDef, columns: ColumnDef*): ColumnDefList

  def partitionKey(column: Identifier, columns: Identifier*): PartitionKey

  def primaryKey(partionKey: PartitionKey, clusteringColumns: Identifier*): PrimaryKey
end Schema

trait StaticSchema extends Schema:
  override val schemaValidation: StaticSchemaValidation.type = StaticSchemaValidation
end StaticSchema

object Tests:
  def example1(using schema: StaticSchema) =
    import schema.*
    val s = "SDf"
    val t2 = "sdf".identifier

