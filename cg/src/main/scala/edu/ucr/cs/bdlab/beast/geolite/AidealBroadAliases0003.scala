package edu.ucr.cs.bdlab.beast.geolite

import java.awt.geom.Point2D
import org.locationtech.jts.geom.Envelope

object AidealBroadAliases0003 {
  def worldToTileID(metadata: RasterMetadata, x: Double, y: Double): Int =
    metadata.getTileIDAtPoint(x, y)

  def pixelToWorld(metadata: RasterMetadata, i: Double, j: Double, outPoint: Point2D.Double): Unit =
    metadata.gridToModel(i, j, outPoint)

  def worldToPixel(metadata: RasterMetadata, x: Double, y: Double, outPoint: Point2D.Double): Unit =
    metadata.modelToGrid(x, y, outPoint)

  def worldEnvelope(metadata: RasterMetadata): Envelope =
    metadata.envelope
}
