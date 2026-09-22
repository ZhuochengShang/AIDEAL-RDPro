package edu.ucr.cs.bdlab.beast.geolite

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.DataType

object AidealBroadAliases0002 {

  def concatFeatureAndRow(feature: IFeature, row: Row): IFeature =
    Feature.concat(feature, row)

  def appendAttribute(feature: IFeature, value: Any, name: String = null, dataType: DataType = null): IFeature =
    Feature.append(feature, value, name, dataType)

  def totalTiles(metadata: RasterMetadata): Int =
    metadata.numTiles

  def tileIdForPixel(metadata: RasterMetadata, iPixel: Int, jPixel: Int): Int =
    metadata.getTileIDAtPixel(iPixel, jPixel)
}
