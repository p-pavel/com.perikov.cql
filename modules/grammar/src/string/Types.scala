package com.perikov.cql.grammar.string

import com.perikov.cql.grammar as gm

trait NativeTypes extends gm.NativeTypes:
  override type TypeBase        = String
  override type Type[-Constant] = TypeBase
  override type DateString      = String
  override type TimestampString = String
  override type DurationString  = String
  override type TimeString      = String
  override def ASCII     = "ASCII"
  override def BIGINT    = "BIGINT"
  override def BLOB      = "BLOB"
  override def BOOLEAN   = "BOOLEAN"
  override def COUNTER   = "COUNTER"
  override def DATE      = "DATE"
  override def DECIMAL   = "DECIMAL"
  override def DOUBLE    = "DOUBLE"
  override def DURATION  = "DURATION"
  override def FLOAT     = "FLOAT"
  override def INET      = "INET"
  override def INT       = "INT"
  override def SMALLINT  = "SMALLINT"
  override def TEXT      = "TEXT"
  override def TIME      = "TIME"
  override def TIMESTAMP = "TIMESTAMP"
  override def TIMEUUID  = "TIMEUUID"
  override def TINYINT   = "TINYINT"
  override def UUID      = "UUID"
  override def VARCHAR   = "VARCHAR"
  override def VARINT    = "VARINT"
  override def VECTOR    = "VECTOR"
end NativeTypes

trait Collections extends NativeTypes, gm.Collections:
  override def MAP[K, V](key: Type[K], value: Type[V]) = s"MAP<$key, $value>"
  override def LIST[V](value: Type[V])                 = s"LIST<$value>"
  override def SET[V](value: Type[V])                  = s"SET<$value>"

trait Tuples extends NativeTypes, gm.Tuples:
  override def TUPLE[T <: Tuple](values: Tuple.Map[T, Type]) = values.toList.mkString("TUPLE<", ", ", ">")

trait Types extends Collections, Tuples, gm.UDTs
