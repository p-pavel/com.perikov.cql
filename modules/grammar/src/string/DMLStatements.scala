package com.perikov.cql.grammar.string
import com.perikov.cql.grammar as gm

trait DMLStatements extends Identifiers, Schema, gm.DMLStatements:
  override type Statement    = String
  override type TableOptions = String
  override def defaultTableOptions = ""
  override type KeyspaceOptions = String
  override def defaultKeyspaceOptions = ""



  extension (t: Table) override def statement = 
    new TableStatements:
      override type SelectItem = String
      override type Selector  =  String
      override type ColumnName  = String

      override type SelectClause = String

      extension (sel: Selector) override def as(id: Identifier)= s"$sel AS $id"

      override def create(
          ifNotExists: Boolean = false,
          options: TableOptions = defaultTableOptions
      ) = s"CREATE TABLE ${t.keyspace}.${t.name} ($options)"

      def drop(ifExists: Boolean = false) = 
        val guard = if ifExists then " IF EXISTS " else ""
        s"DROP TABLE $guard ${t.keyspace}.${t.name}"

      class StringClause extends Clause:
        type Self = String
        def default = ""

      val whereClause = StringClause()
      val groupByClause = StringClause()
      val orderByClause = StringClause()

      def columnName(id: Identifier) = id
      def selectClause(items: SelectItem*) = items.mkString(", ")

      def select(
          what: SelectClause,
          distinct: Boolean = false,
          json: Boolean = false,
          where: WhereClause = whereClause.default,
          groupBy: GroupByClause = groupByClause.default,
          orderBy: OrderByClause = orderByClause.default,
          perPartitionLimit: Limit = noLimit,
          limit: Limit = noLimit,
          allowFiltering: Boolean = false
      ): Statement = ???



  type Limit = String

  def noLimit: Limit = "" 
  def limit(n: Int): Limit  = s"LIMIT $n"
  def limit(bindName: Identifier) = s"LIMIT :$bindName"

end DMLStatements
