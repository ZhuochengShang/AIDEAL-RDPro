package edu.ucr.cs.bdlab.beast.geolite

import java.io.{ObjectInput, ObjectOutput}
import org.apache.spark.sql.types.DataType

object AidealBroadAliases0001 {
  def serializeDataType(t: DataType, out: ObjectOutput): Unit =
    Feature.writeType(t, out)

  def deserializeDataType(in: ObjectInput): DataType =
    Feature.readType(in)

  def serializeValue(out: ObjectOutput, value: Any, t: DataType): Unit =
    Feature.writeValue(out, value, t)

  def deserializeValue(in: ObjectInput, t: DataType): Any =
    Feature.readValue(in, t)
}
