package edu.ucr.cs.bdlab.beast.geolite

import org.apache.spark.sql.types.{DataType, StructType}

object AidealBroadAliases0004 {

  def pixelInRange(metadata: RasterMetadata, i: Int, j: Int): Boolean =
    metadata.isPixelInRange(i, j)

  def rescalePixels(metadata: RasterMetadata, newRasterWidth: Int, newRasterHeight: Int): RasterMetadata =
    metadata.rescale(newRasterWidth, newRasterHeight)

  def inferSchemaFromNamesAndValues(names: Array[String], values: Array[Any]): StructType =
    RasterSchemaHelper.inferSchema(names, values)

  def detectSparkType(value: Any): DataType =
    RasterSchemaHelper.detectType(value)
}
