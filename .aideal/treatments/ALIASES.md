# Alias interfaces

## Proposal batch 1

### `edu.ucr.cs.bdlab.beast.geolite.AidealBroadAliases0001`

Thin wrappers over `Feature` serialization helpers.

- `serializeDataType(t: DataType, out: ObjectOutput): Unit`
  - Delegates to `Feature.writeType`.
  - Writes the type descriptor for later reading by `deserializeDataType`.

- `deserializeDataType(in: ObjectInput): DataType`
  - Delegates to `Feature.readType`.
  - Reads a type descriptor from an `ObjectInput`.

- `serializeValue(out: ObjectOutput, value: Any, t: DataType): Unit`
  - Delegates to `Feature.writeValue`.
  - Writes `value` according to the provided Spark SQL `DataType`.

- `deserializeValue(in: ObjectInput, t: DataType): Any`
  - Delegates to `Feature.readValue`.
  - Reads one value according to the provided Spark SQL `DataType`.

All wrappers preserve the underlying argument requirements, return types, side effects, and exceptions.

## Proposal batch 2

### AidealBroadAliases0002

- `def concatFeatureAndRow(feature: IFeature, row: Row): IFeature`  
  Thin wrapper over `Feature.concat(feature, row)`. Returns the same result and propagates the same runtime errors.

- `def appendAttribute(feature: IFeature, value: Any, name: String = null, dataType: DataType = null): IFeature`  
  Thin wrapper over `Feature.append(feature, value, name, dataType)`. Default arguments and type expectations are unchanged.

- `def totalTiles(metadata: RasterMetadata): Int`  
  Thin wrapper over instance accessor `metadata.numTiles`.

- `def tileIdForPixel(metadata: RasterMetadata, iPixel: Int, jPixel: Int): Int`  
  Thin wrapper over `metadata.getTileIDAtPixel(iPixel, jPixel)` with identical tile indexing behavior.

## Proposal batch 3

### `edu.ucr.cs.bdlab.beast.geolite.AidealBroadAliases0003`

Thin usability aliases over `RasterMetadata` instance methods.

- `worldToTileID(metadata: RasterMetadata, x: Double, y: Double): Int`
  - Delegates to `metadata.getTileIDAtPoint(x, y)`.
  - Returns tile ID for model/world coordinates, or `-1` when outside input space (per canonical method docs).

- `pixelToWorld(metadata: RasterMetadata, i: Double, j: Double, outPoint: Point2D.Double): Unit`
  - Delegates to `metadata.gridToModel(i, j, outPoint)`.
  - Mutates `outPoint` in place with model/world coordinates.

- `worldToPixel(metadata: RasterMetadata, x: Double, y: Double, outPoint: Point2D.Double): Unit`
  - Delegates to `metadata.modelToGrid(x, y, outPoint)`.
  - Mutates `outPoint` in place with grid/pixel coordinates.

- `worldEnvelope(metadata: RasterMetadata): Envelope`
  - Delegates to `metadata.envelope`.
  - Returns raster bounds envelope in model/world space.


## Proposal batch 4

### AidealBroadAliases0004

- `pixelInRange(metadata: RasterMetadata, i: Int, j: Int): Boolean`  
  Thin wrapper over `RasterMetadata.isPixelInRange`. Returns `true` iff `(i, j)` is inside raster bounds.

- `rescalePixels(metadata: RasterMetadata, newRasterWidth: Int, newRasterHeight: Int): RasterMetadata`  
  Thin wrapper over `RasterMetadata.rescale`. Width/height arguments must be `Int`, and behavior/errors are unchanged from the canonical method.

- `inferSchemaFromNamesAndValues(names: Array[String], values: Array[Any]): StructType`  
  Thin wrapper over `RasterSchemaHelper.inferSchema`. Expects `names` typed as `Array[String]` and delegates schema inference exactly.

- `detectSparkType(value: Any): DataType`  
  Thin wrapper over `RasterSchemaHelper.detectType`, returning Spark SQL `DataType` according to the underlying implementation.