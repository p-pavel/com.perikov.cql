package com.perikov.cql.grammar

/** @see
  *   https://cassandra.apache.org/doc/latest/cassandra/developing/cql/types.html#native-types
  * @todo
  *   verify each type
  */
trait NativeTypes:
  type TypeBase
  type Type[-Constant] <: TypeBase
  type DateString
  type TimestampString
  type DurationString
  type TimeString
  def ASCII: Type[String]
  def BIGINT: Type[Long]
  def BLOB: Type[IArray[Byte]]
  def BOOLEAN: Type[Boolean]
  def COUNTER: Type[Long]
  def DATE: Type[Long]
  def DECIMAL: Type[Long | BigDecimal | BigInt | Double]
  def DOUBLE: Type[Double]
  def DURATION: Type[(Long, Long, Long) | DurationString]
  def FLOAT: Type[Float]
  def INET: Type[String]
  def INT: Type[Int]
  def SMALLINT: Type[Short]
  def TEXT: Type[String]
  def TIME: Type[Long | TimeString]
  def TIMESTAMP: Type[Long | TimestampString]
  def TIMEUUID: Type[java.util.UUID]
  def TINYINT: Type[String]
  def UUID: Type[java.util.UUID]
  def VARCHAR: Type[String]
  def VARINT: Type[BigInt]
  def VECTOR: Type[IArray[Float]]
end NativeTypes

trait Collections extends NativeTypes:
  def MAP[K, V](key: Type[K], value: Type[V]): Type[Map[K, V]]
  def LIST[V](value: Type[V]): Type[Seq[V]]
  def SET[V](value: Type[V]): Type[Set[V]]

trait Tuples extends NativeTypes:
  def TUPLE[T <: Tuple](values: Tuple.Map[T, Type]): Type[T]

/** @todo specify */
trait UDTs extends NativeTypes

trait Types extends Collections, Tuples, UDTs
