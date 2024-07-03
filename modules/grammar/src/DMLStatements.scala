package com.perikov.cql.grammar

trait DMLStatements extends Identifiers, Schema:
  type Statement
  type TableOptions
  def defaultTableOptions: TableOptions
  type KeyspaceOptions
  def defaultKeyspaceOptions: KeyspaceOptions


  trait KeyspaceStatements:
    def create(
        ifNotExists: Boolean,
        options: KeyspaceOptions = defaultKeyspaceOptions): Statement
  end KeyspaceStatements

  trait TableStatements:
    type SelectItem
    type Selector <: SelectItem
    type ColumnName <: Selector

    type SelectClause

    extension (sel: Selector) def as(id: Identifier): SelectItem

    def create(
        ifNotExists: Boolean = false,
        options: TableOptions = defaultTableOptions
    ): Statement

    def drop(ifExists: Boolean = false): Statement

    trait Clause:
      type Self
      def default: Self
    end Clause

    val whereClause: Clause
    val groupByClause: Clause
    val orderByClause: Clause
    export whereClause.Self as WhereClause
    export groupByClause.Self as GroupByClause
    export orderByClause.Self as OrderByClause

    def columnName(id: Identifier): ColumnName
    def selectClause(items: SelectItem*): SelectClause

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
    ): Statement

    // TODO: delete, update, insert, batch

  end TableStatements

  extension (t: Table) def statement: TableStatements

  type Limit

  def noLimit: Limit
  def limit(n: Int): Limit
  def limit(bindName: Identifier): Limit

end DMLStatements
