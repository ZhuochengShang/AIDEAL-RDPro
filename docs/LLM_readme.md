# RDPro official sixteen-API Original R0 — LLM_readme

LLM-facing API documentation. Generated from the API surface, project profile, and distilled docs.

## API Test: `allMetadata`

### Signature
```scala
def allMetadata[T](raster: RDD[ITile[T]]): Array[RasterMetadata]
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:337_

_Source doc:_ Return a list of all distinct RasterMetadata in the input @param raster a set of tiles @return a list of distinct metadata in all input tiles

### Goal
Extracts a unique set of `RasterMetadata` objects from an RDD of tiles to identify the distinct spatial and coordinate reference system properties present across the raster dataset.

### Parameters
- `raster` (`RDD[ITile[T]]`): An RDD containing raster tiles of type `T` (e.g., `Int`, `Float`, `Array[Int]`, or `Array[Float]`).

### Input
- **Data**: An `RDD[ITile[T]]` typically loaded via `sc.geoTiff` or other raster I/O operations.
- **Preconditions**: The input RDD must be initialized with a valid type parameter `T` corresponding to the pixel data type. The tiles must contain embedded metadata that the function can aggregate.

### Output
Returns `Array[RasterMetadata]` — An array containing all distinct `RasterMetadata` instances found across the distributed tiles in the input RDD.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadataArray: Array[RasterMetadata] = allMetadata(rasterRDD)
```

### LLM Instruction Prompt
- Always ensure the `raster` RDD is typed correctly (e.g., `RDD[ITile[Float]]`) before passing it to `allMetadata`.
- Use this function when you need to verify if a raster dataset consists of tiles with uniform metadata or to inspect the variety of coordinate systems/extents present in a collection.

### Prompt Snippet
```text
val metadata = allMetadata(myRasterRDD)
```

### Common Failure Modes
- **Type Mismatch**: Passing an RDD that does not contain `ITile` objects will result in a compilation error.
- **Empty RDD**: If the input RDD is empty, the function will return an empty array.
- **Serialization Issues**: Since this operation triggers a distributed scan of the RDD, it may be slow on very large datasets; ensure the Spark context is active and resources are allocated.

### Fix Code Hint
```scala
// Ensure the RDD is properly typed and loaded
val raster: RDD[ITile[Float]] = sc.geoTiff("path/to/raster")
val metadata = allMetadata(raster)
```

## API Test: `append`

### Signature
```scala
def append(feature: IFeature, value: Any, name: String = null, dataType: DataType = null): IFeature
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:405_

_Source doc:_ Appends an additional attribute to the given feature and returns a new feature @param feature the feature to append to. This feature is not modified. @param value the value to append. @param name (Optional) the name of the new attribute @param dataType (Optional) the type of the additional attribute. @return a new feature that contains the geometry and all attributes of the input feature + the new attribute.

### Goal
Extends an existing `IFeature` by adding a new attribute value, returning a new immutable feature instance that preserves the original geometry and existing attributes.

### Parameters
- `feature` (`IFeature`): The source vector feature to which the new attribute will be added.
- `value` (`Any`): The data value to be stored as the new attribute.
- `name` (`String`), default `null`: The optional identifier/key for the new attribute.
- `dataType` (`DataType`), default `null`: The optional Spark `DataType` definition for the new attribute.

### Input
The function accepts an `IFeature` object, which typically originates from spatial data sources like Shapefiles, GeoJSON, or results from a spatial join (e.g., `raptorJoin`). The input feature is treated as immutable; the operation does not modify the original object.

### Output
Returns `IFeature` — A new feature instance containing the original geometry and all previous attributes, augmented with the newly appended attribute.

### Valid Call Patterns
```scala
// Appending an attribute to a feature using a specific name
val newFeature = Feature.append(point, polygon.getAs[String]("NAME"), "state")
```

### LLM Instruction Prompt
- Use `Feature.append` when you need to enrich a vector feature with metadata or join results.
- Always assign the result to a new variable, as the original `IFeature` remains unchanged.
- If the attribute name is required for downstream filtering or serialization, ensure the `name` parameter is provided.

### Prompt Snippet
```text
val enrichedFeature = Feature.append(originalFeature, attributeValue, "attributeName")
```

### Common Failure Modes
- **Null Pointer Exceptions**: Passing a `null` feature as the first argument will result in a runtime error.
- **Type Mismatch**: Providing a `value` that is incompatible with the specified `dataType` (if provided) may cause issues during subsequent serialization or Spark schema enforcement.
- **Immutability Misconception**: Attempting to use the original `feature` variable expecting it to contain the new attribute; always use the returned `IFeature` from the `append` call.

### Fix Code Hint
```scala
// Ensure you capture the returned feature
val updatedFeature = Feature.append(myFeature, myValue, "myAttribute")
// Use updatedFeature for subsequent operations
```

## API Test: `concat`

### Signature
```scala
def concat(feature: IFeature, row: Row): IFeature
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:391_

_Source doc:_ Concatenates two rows together to form a feature. @param feature a row that contains a geometry @param row another row to append at the end of the feature @return a new feature that combines the values and schema from both

### Goal
Combines an existing `IFeature` (which contains spatial geometry) with a Spark `Row` to produce a new `IFeature` containing the merged attributes and schema of both inputs.

### Parameters
- `feature` (`IFeature`): The base feature containing the spatial geometry and initial attribute set.
- `row` (`Row`): The Spark `Row` containing additional attributes to be appended to the feature.

### Input
- **Data**: Requires an `IFeature` instance (typically derived from spatial RDDs like Shapefile or GeoJSON) and a standard Spark `Row`.
- **Preconditions**: The caller must ensure the `IFeature` is valid and contains the necessary geometry, as the function is designed to extend the feature's attribute schema.

### Output
Returns `IFeature` — A new feature object that merges the geometry and attributes of the input `feature` with the data fields provided in the `row`.

### Valid Call Patterns
```scala
// Inferred from signature:
val newFeature = concat(existingFeature, additionalRow)
```

### LLM Instruction Prompt
- Use `concat` when you need to enrich an existing `IFeature` with additional non-spatial data stored in a Spark `Row`.
- Ensure the first argument is an `IFeature` and the second is a `Row`.
- The function returns a new `IFeature` object; do not expect the original `feature` to be mutated in place.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.beast.geolite.IFeature
import org.apache.spark.sql.Row

// Example usage:
val combinedFeature: IFeature = concat(myFeature, myRow)
```

### Common Failure Modes
- **Type Mismatch**: Passing an object that is not an `IFeature` as the first argument or a non-`Row` type as the second.
- **Schema Incompatibility**: If the `Row` structure is incompatible with the expected feature schema, downstream operations on the resulting `IFeature` may fail.
- **Null Inputs**: Passing `null` for either parameter will result in a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure inputs are correctly typed before concatenation
val feature: IFeature = ...
val row: Row = ...
val result = concat(feature, row)
```

## API Test: `copy`

### Signature
```scala
def copy(): Row
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:125_

_Source doc:_ Make a copy of this row. Since Feature is immutable, we just return the same object. @return the same object

### Goal
Returns a reference to the current `Row` object, facilitating compatibility with Spark's `Row` interface while acknowledging the immutability of Beast `Feature` objects.

### Parameters
_None._

### Input
The caller must have an existing `Row` or `Feature` object instance. No specific file formats or external data are required as this is an in-memory operation on an existing object.

### Output
Returns `Row` — The same object instance, as `Feature` objects in Beast are immutable.

### Valid Call Patterns
```scala
// Inferred from signature:
val rowCopy = feature.copy()
```

### LLM Instruction Prompt
- Use `copy()` only when an API requires a `Row` type and you have an existing `Feature` instance.
- Do not expect a new memory allocation; the method returns the same object reference.
- Ensure the receiver is an instance of a class implementing the `Feature` trait or `Row` interface.

### Prompt Snippet
```text
val rowCopy = myFeature.copy()
```

### Common Failure Modes
- **NullPointerException**: Calling `copy()` on a null reference.
- **Type Mismatch**: Attempting to call `copy()` on an object that does not implement the `Row` interface or `Feature` trait.
- **Redundant Usage**: Calling `copy()` expecting a deep copy; since the object is immutable, modifications to the "copy" will affect the original if the underlying data structure is shared.

### Fix Code Hint
```scala
// Since the object is immutable, simply reference the original object 
// instead of calling copy() if a deep copy is not required.
val original = feature
```

## API Test: `create`

### Signature
```scala
def create(row: Row, geometry: Geometry): Feature
def create(geometry: Geometry, _names: Array[String], _types: Array[DataType], _values: Array[Any]): Feature
def create(x1: Double, y1:Double, x2: Double, y2: Double, srid: Int, rasterWidth: Int, rasterHeight: Int, tileWidth: Int, tileHeight: Int): RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:324  (+2 more definition site/overload)_

_Source doc:_ Create a raster metadata that represents a geographical region provided by a rectangle. @param x1 the x-coordinate of the left edge of the pixel at (0, 0) @param y1 the y-coordinate of the top edge of the pixel at (0, 0) @param x2 the x-coordinate of the right edge of the pixel at (rasterWidth - 1, rasterHeight - 1) @param y2 the y-coordinate of the bottom edge of the pixel at (rasterWidth - 1, rasterHeight - 1) @param srid the SRID that represents the coordinate reference system of the raster @param rasterWidth the number of columns in the entire raster @param rasterHeight the number of rows in the entire raster @param tileWidth the width of each tile in pixels @param tileHeight the height of each tile in pixels @return a raster metadata wit the given information

### Goal
Initializes `RasterMetadata` to define the spatial extent, coordinate reference system, and tiling structure required for raster processing operations in Beast.

### Parameters
- `x1` (`Double`): The x-coordinate of the left edge of the pixel at (0, 0).
- `y1` (`Double`): The y-coordinate of the top edge of the pixel at (0, 0).
- `x2` (`Double`): The x-coordinate of the right edge of the pixel at (rasterWidth - 1, rasterHeight - 1).
- `y2` (`Double`): The y-coordinate of the bottom edge of the pixel at (rasterWidth - 1, rasterHeight - 1).
- `srid` (`Int`): The SRID representing the coordinate reference system of the raster.
- `rasterWidth` (`Int`): The total number of columns in the raster.
- `rasterHeight` (`Int`): The total number of rows in the raster.
- `tileWidth` (`Int`): The width of each individual tile in pixels.
- `tileHeight` (`Int`): The height of each individual tile in pixels.

### Input
The caller must provide valid spatial bounds and dimensions. The `rasterWidth` and `rasterHeight` must be positive integers. The `tileWidth` and `tileHeight` should be chosen to align with the desired tiling strategy for the Spark RDD.

### Output
Returns `RasterMetadata` — An object containing the spatial and structural configuration used to define the geometry and tiling of a raster dataset.

### Valid Call Patterns
```scala
val metadata = RasterMetadata.create(x1 = -50, y1 = 40, x2 = -60, y2 = 30, srid = 4326,
  rasterWidth = 10, rasterHeight = 10, tileWidth = 10, tileHeight = 10)
```

### LLM Instruction Prompt
- Use `RasterMetadata.create` to define the spatial context for raster operations.
- Ensure `x1, y1` and `x2, y2` correctly represent the top-left and bottom-right corners of the raster grid.
- Always provide all 9 parameters when initializing `RasterMetadata`.

### Prompt Snippet
```text
val metadata = RasterMetadata.create(x1, y1, x2, y2, srid, rasterWidth, rasterHeight, tileWidth, tileHeight)
```

### Common Failure Modes
- Providing non-positive values for `rasterWidth`, `rasterHeight`, `tileWidth`, or `tileHeight`, which will cause invalid metadata generation.
- Mismatched coordinate systems (SRID) between the metadata and the actual raster data, leading to incorrect spatial alignment during `rasterizePixels` or `raptorJoin`.

### Fix Code Hint
```scala
// Ensure dimensions are positive and coordinates match the intended CRS
val metadata = RasterMetadata.create(-180, 90, 180, -90, 4326, 1024, 1024, 256, 256)
```

## API Test: `detectType`

### Signature
```scala
def detectType(value: Any): DataType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:108_

_Source doc:_ Detect the data type for the given value. @param value A value to detect its type @return a detected data type for the given value.

### Goal
Determines the appropriate Spark `DataType` for a given input value to facilitate schema definition and data processing within the Beast framework.

### Parameters
- `value` (`Any`): The input value (e.g., a primitive, array, or object) for which the corresponding Spark SQL data type is to be identified.

### Input
- **Data**: Any Scala object or primitive.
- **Preconditions**: The input must be a type supported by the Beast/Spark type system. This function is typically used when preparing schemas for raster or vector data structures.

### Output
Returns `DataType` — The Spark SQL `DataType` that corresponds to the provided input value, allowing for consistent type handling in Spark RDDs and DataFrames.

### Valid Call Patterns
```scala
// Inferred from signature
val myType = detectType(myValue)
```

### LLM Instruction Prompt
- Use `detectType` when you need to programmatically determine the Spark `DataType` for a variable before creating a schema or initializing a `RasterRDD`.
- Ensure the input `value` is a standard Scala type compatible with Spark's type system.

### Prompt Snippet
```text
val dataType = detectType(sampleValue)
```

### Common Failure Modes
- **Unsupported Types**: Passing a complex custom object that does not map to a standard Spark `DataType` may result in an error or an unexpected type return.
- **Null Values**: Passing `null` may lead to runtime exceptions depending on the underlying implementation of the type detection logic.

### Fix Code Hint
```scala
// Ensure the value is a supported primitive or collection type
val sampleValue: Int = 10
val dataType = detectType(sampleValue)
```

## API Test: `envelope`

### Signature
```scala
def envelope: Envelope
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:170_

_Source doc:_ An envelope that represents the boundaries of the raster data in the model space @return

### Goal
Retrieves the spatial extent of raster data, providing the minimum bounding box in the model coordinate space.

### Parameters
_None._

### Input
The caller must provide a valid raster object (e.g., `ITile` or `RasterRDD`) that has been successfully loaded via `sc.geoTiff` or similar raster I/O operations. The raster must be initialized and contain valid metadata defining its spatial boundaries.

### Output
Returns `Envelope` — A geometric object representing the spatial boundaries (minX, minY, maxX, maxY) of the raster data in the model space.

### Valid Call Patterns
```scala
// Inferred from signature:
val rasterEnvelope = raster.envelope
```

### LLM Instruction Prompt
- Use `envelope` only on initialized raster objects (e.g., `ITile` or `RasterRDD`).
- Do not attempt to call `envelope` on raw vector features or uninitialized RDDs.
- The returned `Envelope` object is typically used for spatial filtering or calculating the MBR of a raster dataset.

### Prompt Snippet
```text
val raster = sc.geoTiff("path/to/raster")
val bounds = raster.envelope
```

### Common Failure Modes
- **Null Pointer Exception**: Calling `envelope` on a raster object that failed to load or is uninitialized.
- **Coordinate Mismatch**: Assuming the `Envelope` coordinates are in a specific projection (e.g., WGS84) without verifying the raster's internal CRS metadata.
- **Type Mismatch**: Attempting to call `envelope` on a vector `IFeature` RDD; ensure the receiver is a raster type.

### Fix Code Hint
```scala
// Ensure the raster is loaded before accessing metadata
val raster = sc.geoTiff("data.tif")
if (raster != null) {
  val bounds = raster.envelope
  println(s"Raster bounds: ${bounds.getMinX}, ${bounds.getMinY}")
}
```

## API Test: `equals`

### Signature
```scala
def equals(obj: Any): Boolean
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:196_

### Goal
Determines whether the current object is structurally and semantically equivalent to another provided object within the Beast geospatial framework.

### Parameters
- `obj` (`Any`): The object to compare against the current instance.

### Input
- **Data**: Any object, typically another instance of a Beast spatial type (e.g., `IFeature` or `ITile` metadata).
- **Preconditions**: The caller should ensure that the object being compared is of a compatible type, as `equals` performs a type-safe comparison.

### Output
Returns `Boolean` — `true` if the objects are equivalent according to the internal Beast implementation, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature: comparing two spatial objects
val isEqual = objectA.equals(objectB)
```

### LLM Instruction Prompt
- When using `equals`, always ensure the receiver is a valid Beast spatial object (e.g., a metadata or feature instance).
- Do not assume `equals` performs deep geometric equality unless the specific implementation documentation confirms it; it is primarily used for object identity or metadata property comparison.

### Prompt Snippet
```text
// Example of checking equality between two Beast objects
if (metadataA.equals(metadataB)) {
  // Proceed with aligned raster operations
}
```

### Common Failure Modes
- **Type Mismatch**: Passing an object of an incompatible type may return `false` rather than throwing an exception, which can lead to silent logic errors in spatial processing pipelines.
- **Null Pointer**: Calling `equals` on a null reference will throw a `NullPointerException`. Always verify the receiver is non-null before invocation.
- **Reference vs. Value**: In some Beast implementations, `equals` may check for reference equality; ensure the objects are fully materialized if comparing metadata derived from different RDD partitions.

### Fix Code Hint
```scala
// Use a null-safe check before comparing
if (obj != null && obj.equals(other)) {
  // Logic for equal objects
}
```

## API Test: `extents`

### Signature
```scala
def extents: Geometry
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:181_

_Source doc:_ The extents of the RasterMetadata in model space as a rectangular polygon @return

### Goal
Retrieves the spatial boundary of a `RasterMetadata` object as a rectangular `Geometry` polygon defined in model space.

### Parameters
_None._

### Input
The caller must have an initialized `RasterMetadata` object, typically obtained from a raster data source (e.g., GeoTIFF or HDF) loaded via the Beast API. No specific file format is required at the time of calling, as the metadata is already resident in memory.

### Output
Returns `Geometry` — A rectangular polygon representing the spatial coverage (MBR) of the raster in the coordinate system defined by the model space.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ...
val boundary: Geometry = metadata.extents
```

### LLM Instruction Prompt
- Use `extents` only when operating on an instance of `RasterMetadata`.
- Do not attempt to pass arguments to this function, as it takes no parameters.
- Ensure the `RasterMetadata` object is fully initialized before accessing the property.

### Prompt Snippet
```text
// Accessing the spatial boundary of raster metadata
val rasterBounds = metadata.extents
```

### Common Failure Modes
- **Null Metadata**: Calling `extents` on an uninitialized or null `RasterMetadata` reference will result in a `NullPointerException`.
- **Coordinate System Mismatch**: The returned `Geometry` is in model space; failure to account for the specific coordinate reference system (CRS) of the model space may lead to incorrect spatial joins or overlays with vector data.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from a valid raster source
val rasterMetadata = rasterRDD.metadata
val bounds = rasterMetadata.extents
```

## API Test: `fieldIndex`

### Signature
```scala
def fieldIndex(name: String): Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:57_

### Goal
Retrieves the zero-based index of a feature attribute by its string name within an `IFeature` object.

### Parameters
- `name` (`String`): The name of the attribute field to look up.

### Input
The caller must provide an `IFeature` instance that contains a schema with the specified field name. The feature must be initialized and the schema must be accessible to the object.

### Output
Returns `Int` — The zero-based index of the field in the feature's attribute array.

### Valid Call Patterns
```scala
// Inferred from signature:
val index = feature.fieldIndex("attribute_name")
```

### LLM Instruction Prompt
- When working with `IFeature` objects in Beast, use `fieldIndex` to resolve attribute names to integer indices before accessing values via `getAs` or similar methods.
- Ensure the `name` string matches the schema definition exactly, as this lookup is case-sensitive and requires the field to exist in the feature's metadata.

### Prompt Snippet
```text
// Accessing a feature attribute by name
val fieldIdx = feature.fieldIndex("population")
val populationValue = feature.getAs[Int](fieldIdx)
```

### Common Failure Modes
- **NoSuchElementException**: Occurs if the provided `name` does not exist in the feature's schema.
- **NullPointerException**: Occurs if the feature object itself is null or if the schema has not been properly initialized.
- **IndexOutOfBoundsException**: While `fieldIndex` returns the index, subsequent use of this index in `getAs` will fail if the feature's underlying attribute array is smaller than expected.

### Fix Code Hint
```scala
// Verify field existence before access
val fieldName = "target_field"
val idx = feature.fieldIndex(fieldName)
if (idx >= 0) {
  val value = feature.getAs[String](idx)
} else {
  println(s"Field $fieldName not found.")
}
```

## API Test: `forOneTile`

### Signature
```scala
def forOneTile(tileID: Int): RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:300_

_Source doc:_ Returns a new [[RasterMetadata]] that contains only the given tile ID which will have a tile ID zero in the result @param tileID the tile ID to keep in the resulting metadata @return a new metadata that contains a single tile that points to the same geographical location pointed by the given tile.

### Goal
Filters a `RasterMetadata` object to isolate a single tile, re-indexing it to ID zero while preserving its original geographical spatial extent.

### Parameters
- `tileID` (`Int`): The identifier of the specific tile within the current metadata to be extracted.

### Input
- **Data**: An existing `RasterMetadata` object.
- **Preconditions**: The `tileID` provided must exist within the current `RasterMetadata` instance.

### Output
Returns `RasterMetadata` — A new metadata instance containing exactly one tile (assigned ID 0) that corresponds to the geographical location of the original tile specified by `tileID`.

### Valid Call Patterns
```scala
// Inferred from signature:
val singleTileMetadata = metadata.forOneTile(targetTileID)
```

### LLM Instruction Prompt
- Use `forOneTile` when you need to isolate a specific tile's spatial metadata from a larger raster dataset.
- Ensure the `tileID` passed is an integer present in the source metadata.
- Note that the resulting `RasterMetadata` will always have its tile ID reset to 0.

### Prompt Snippet
```text
val metadata: RasterMetadata = ...
val isolatedMetadata = metadata.forOneTile(tileID)
```

### Common Failure Modes
- **Invalid Tile ID**: Passing a `tileID` that does not exist in the source metadata may result in an index out of bounds or an empty/invalid metadata state.
- **Metadata Mismatch**: Attempting to use the resulting metadata with operations expecting the original multi-tile structure will fail, as this function reduces the metadata to a single-tile scope.

### Fix Code Hint
```scala
// Ensure the tileID is valid before calling
if (metadata.contains(tileID)) {
  val newMetadata = metadata.forOneTile(tileID)
}
```

## API Test: `get`

### Signature
```scala
def get(i: Int): Any
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:119_

### Goal
Retrieves the value of a specific attribute at the given index from an `IFeature` object within the Beast spatial data framework.

### Parameters
- `i` (`Int`): The zero-based index of the attribute to retrieve from the feature's internal attribute collection.

### Input
The caller must provide an initialized `IFeature` object. The index `i` must be within the bounds of the feature's attribute schema (0 to N-1, where N is the number of attributes).

### Output
Returns `Any` — The value stored at the specified attribute index. The actual type of the returned object depends on the underlying schema of the feature (e.g., `String`, `Double`, `Int`).

### Valid Call Patterns
```scala
// Inferred from signature: accessing an attribute from an IFeature
val feature: IFeature = ...
val attributeValue: Any = feature.get(0)
```

### LLM Instruction Prompt
- When calling `get`, ensure the `IFeature` instance is fully initialized and that the index `i` is validated against the feature's schema length to avoid `IndexOutOfBoundsException`. Always cast the returned `Any` to the expected type based on the known schema of the dataset.

### Prompt Snippet
```text
// Accessing the first attribute of a feature
val value = feature.get(0)
```

### Common Failure Modes
- `IndexOutOfBoundsException`: Occurs if the provided index `i` is negative or greater than or equal to the number of attributes defined in the feature's schema.
- `ClassCastException`: Occurs if the returned `Any` is cast to an incorrect type (e.g., attempting to cast a `String` attribute to an `Int`).

### Fix Code Hint
```scala
// Ensure index is within bounds before access
if (i >= 0 && i < feature.getNumAttributes) {
  val value = feature.get(i)
}
```

## API Test: `getPixelScaleX`

### Signature
```scala
def getPixelScaleX: Double
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:93_

### Goal
Retrieves the horizontal pixel scale (width of a single pixel in the coordinate reference system units) from the raster metadata.

### Parameters
_None._

### Input
The caller must have an initialized raster metadata object (typically associated with a `RasterRDD` or `ITile` instance). The raster must be correctly loaded from supported formats like GeoTIFF or HDF, ensuring the spatial metadata is parsed.

### Output
Returns `Double` — The width of a pixel in the units of the raster's coordinate reference system.

### Valid Call Patterns
```scala
// Inferred from signature: assuming 'metadata' is an instance of RasterMetadata
val scaleX: Double = metadata.getPixelScaleX
```

### LLM Instruction Prompt
- Use `getPixelScaleX` when you need to determine the spatial resolution of a raster along the X-axis.
- Ensure the object being queried is a valid `RasterMetadata` instance derived from a loaded raster dataset.
- Do not assume the return value is in meters; it is in the units defined by the raster's CRS.

### Prompt Snippet
```text
// Example usage:
val pixelWidth = rasterMetadata.getPixelScaleX
```

### Common Failure Modes
- **Null Metadata**: Calling this on an uninitialized or null metadata object will result in a `NullPointerException`.
- **CRS Mismatch**: Assuming the scale is in meters when the underlying CRS uses degrees (e.g., WGS84) can lead to incorrect spatial calculations.
- **Missing Metadata**: If the raster file (GeoTIFF/HDF) lacks georeferencing information, the metadata may contain default or zero values.

### Fix Code Hint
```scala
// Ensure the raster is properly loaded and metadata is accessible
val metadata = rasterRDD.metadata
if (metadata != null) {
  val scaleX = metadata.getPixelScaleX
  // Proceed with spatial operations
}
```

## API Test: `getTileIDAtPixel`

### Signature
```scala
def getTileIDAtPixel(iPixel: Int, jPixel: Int): Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:69_

_Source doc:_ Computes the ID of the tile that contains the given pixel. Tiles are numbered in row-wise ordering. @param iPixel the position of the column of the pixel @param jPixel the position of the row of the pixel @return a unique identifier for the tile that contains this pixel location

### Goal
Determines the unique identifier of a raster tile based on its specific pixel coordinate within a row-wise ordered tiling scheme.

### Parameters
- `iPixel` (`Int`): The column index (x-coordinate) of the pixel within the raster.
- `jPixel` (`Int`): The row index (y-coordinate) of the pixel within the raster.

### Input
The function operates on pixel coordinates relative to the raster grid. It assumes the caller is working within the context of a raster metadata object or a structure that defines the tiling layout. No specific file format is required as input, but the coordinates must be valid indices within the raster's dimensions.

### Output
Returns `Int` — A unique identifier representing the specific tile that encompasses the provided pixel location, calculated based on row-wise tile ordering.

### Valid Call Patterns
```scala
// Inferred from signature: assumes an instance of a class containing raster metadata
val tileID = rasterMetadata.getTileIDAtPixel(iPixel, jPixel)
```

### LLM Instruction Prompt
- Use `getTileIDAtPixel` when you need to map a specific pixel coordinate to its corresponding tile ID in a row-wise tiled raster dataset.
- Ensure `iPixel` and `jPixel` are non-negative integers within the bounds of the raster.
- This function is typically used during raster processing or when performing spatial indexing operations on raster data.

### Prompt Snippet
```text
val tileID = metadata.getTileIDAtPixel(col, row)
```

### Common Failure Modes
- **Out of Bounds**: Providing `iPixel` or `jPixel` values that exceed the raster dimensions will result in an incorrect or invalid tile ID.
- **Context Mismatch**: Calling this on an object that does not represent the correct raster metadata or tiling configuration will lead to logical errors in tile identification.

### Fix Code Hint
```scala
// Ensure coordinates are within valid raster bounds before calling
if (iPixel >= 0 && iPixel < rasterWidth && jPixel >= 0 && jPixel < rasterHeight) {
  val tileID = metadata.getTileIDAtPixel(iPixel, jPixel)
}
```

## API Test: `getTileIDAtPoint`

### Signature
```scala
def getTileIDAtPoint(x: Double, y: Double): Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:81_

_Source doc:_ Returns the ID of the tile that contains the given point location in model (world) space @param x the x-coordinate of the point, e.g., longitude @param y the y-coordinate of the point, e.g., latitude @return the ID of the tile that contains this pixel or -1 if the point is outside the input space

### Goal
Determines the unique identifier of the raster tile containing a specific coordinate in model (world) space to facilitate spatial lookups within Beast raster datasets.

### Parameters
- `x` (`Double`): The x-coordinate of the point in model space (e.g., longitude).
- `y` (`Double`): The y-coordinate of the point in model space (e.g., latitude).

### Input
The function operates on an initialized raster metadata context. The coordinates must be provided in the same coordinate reference system (CRS) as the model space of the raster dataset.

### Output
Returns `Int` — The ID of the tile containing the specified point. Returns `-1` if the point falls outside the defined input space of the raster.

### Valid Call Patterns
```scala
// Inferred from signature: assumes an instance of a class containing raster metadata
val tileID = rasterMetadata.getTileIDAtPoint(x, y)
```

### LLM Instruction Prompt
- Use `getTileIDAtPoint` when you need to map a specific geographic or model-space coordinate to a discrete tile ID within a Beast raster structure.
- Always check if the returned value is `-1`, which indicates the point is outside the raster's spatial bounds.
- Ensure the `x` and `y` coordinates are in the correct model space units before calling.

### Prompt Snippet
```text
val tileID = metadata.getTileIDAtPoint(longitude, latitude)
if (tileID == -1) {
  // Handle point outside of raster bounds
}
```

### Common Failure Modes
- **Coordinate Mismatch**: Providing coordinates in a different CRS than the raster's model space will result in incorrect tile IDs or a `-1` return value.
- **Out of Bounds**: Passing coordinates outside the raster's spatial extent will return `-1`, which may cause logic errors if not explicitly handled in the calling code.

### Fix Code Hint
```scala
val tileID = metadata.getTileIDAtPoint(x, y)
val result = if (tileID != -1) tileID else throw new IllegalArgumentException("Point outside raster bounds")
```

## API Test: `getTileX1`

### Signature
```scala
def getTileX1(tileID: Int): Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:107_

_Source doc:_ Computes the low index of a column in the given tile (inclusive) @param tileID the ID of the tile @return

### Goal
Calculates the inclusive starting column index (low index) for a specified raster tile within a grid-based coordinate system.

### Parameters
- `tileID` (`Int`): The unique identifier of the tile for which the column range is being queried.

### Input
- **Preconditions**: The `tileID` must correspond to a valid tile within the current raster metadata context. This function assumes the raster has been partitioned or indexed into a grid structure where tiles are identifiable by integer IDs.

### Output
Returns `Int` — The inclusive low index (starting column) of the specified tile.

### Valid Call Patterns
```scala
// Inferred from signature: assumes an instance of a class containing raster metadata
val lowColumnIndex = rasterMetadata.getTileX1(tileID)
```

### LLM Instruction Prompt
- Use `getTileX1` when you need to determine the horizontal spatial bounds of a specific tile in a raster grid.
- Ensure the `tileID` provided is an `Int` consistent with the indexing scheme of the active `RasterMetadata` or `ITile` context.
- This function returns the inclusive start of the column range; use it in conjunction with other metadata methods to define the full spatial extent of a tile.

### Prompt Snippet
```text
val tileID: Int = ... // ID of the target tile
val startColumn = metadata.getTileX1(tileID)
```

### Common Failure Modes
- **Invalid Tile ID**: Passing a `tileID` that does not exist within the current raster's grid configuration may result in an out-of-bounds error or an incorrect index calculation.
- **Context Mismatch**: Calling this method on a metadata object that does not correspond to the current raster dataset will return values inconsistent with the actual spatial data.

### Fix Code Hint
```scala
// Ensure the tileID is within the valid range of the raster metadata
if (tileID >= 0 && tileID < totalTiles) {
  val x1 = metadata.getTileX1(tileID)
}
```

## API Test: `getTileX2`

### Signature
```scala
def getTileX2(tileID: Int): Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:118_

_Source doc:_ Computes the high index of a column in the given tile (inclusive). If this tile is in the last column, the returned value will be equal to the last column in the raster. @param tileID the ID of the tile @return

### Goal
Calculates the inclusive high-index column boundary for a specific raster tile within the Beast spatial grid.

### Parameters
- `tileID` (`Int`): The unique identifier of the tile for which the column boundary is requested.

### Input
- **Preconditions**: The caller must be operating within a context where `RasterMetadata` is initialized. This function assumes the `tileID` corresponds to a valid tile index within the current raster's grid structure.

### Output
Returns `Int` — The inclusive high index of the column for the specified tile. If the tile is located in the final column of the raster, the returned value matches the index of the raster's last column.

### Valid Call Patterns
```scala
// Inferred from signature: assumes an instance of a class containing this method
val highColumnIndex = rasterMetadata.getTileX2(tileID)
```

### LLM Instruction Prompt
- Use `getTileX2` when you need to determine the spatial column extent of a specific tile.
- Ensure the `tileID` provided is an integer representing a valid tile within the current raster metadata scope.
- Note that the returned value is inclusive; use it for boundary checks or range calculations in pixel-level operations.

### Prompt Snippet
```text
val highCol = metadata.getTileX2(tileID)
```

### Common Failure Modes
- **Invalid Tile ID**: Passing a `tileID` that does not exist within the current raster metadata will result in an index out of bounds or an incorrect calculation.
- **Context Mismatch**: Calling this method on a `RasterMetadata` object that has not been properly initialized or is associated with a different raster dataset.

### Fix Code Hint
```scala
// Ensure the tileID is within the valid range of the raster grid
if (tileID >= 0 && tileID < totalTiles) {
  val highCol = metadata.getTileX2(tileID)
} else {
  throw new IllegalArgumentException("Invalid tileID provided for metadata lookup")
}
```

## API Test: `getTileY1`

### Signature
```scala
def getTileY1(tileID: Int): Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:128_

_Source doc:_ Computes the low index of a row (scanline) in the given tile (inclusive) @param tileID the ID of the tile @return

### Goal
Calculates the inclusive starting row index (scanline) for a specific tile within a raster dataset to support spatial indexing and pixel-level coordinate mapping.

### Parameters
- `tileID` (`Int`): The unique identifier of the tile for which the starting row index is requested.

### Input
- **Preconditions**: The `tileID` must correspond to a valid tile within the current raster metadata context. This function is typically used when processing `ITile` or `RasterRDD` data structures where the raster has been partitioned into a grid.

### Output
Returns `Int` — The low index (inclusive) of the row (scanline) corresponding to the provided `tileID`.

### Valid Call Patterns
```scala
// Inferred from signature: usage within a class or object context containing raster metadata
val y1 = getTileY1(tileID)
```

### LLM Instruction Prompt
- Use `getTileY1` when you need to determine the vertical boundary of a specific raster tile.
- Ensure the `tileID` provided is an integer representing a valid tile index within the raster's grid structure.
- The returned value is an inclusive row index; use it for calculating pixel offsets or spatial bounds within the raster.

### Prompt Snippet
```text
// Calculate the starting row index for a given tile ID
val startRow = getTileY1(tileID)
```

### Common Failure Modes
- **Invalid Tile ID**: Passing a `tileID` that does not exist within the raster's defined grid will result in an incorrect or out-of-bounds index.
- **Context Mismatch**: Calling this function outside of a scope where raster metadata is initialized or available.

### Fix Code Hint
```scala
// Ensure the tileID is within the valid range of the raster's metadata
if (isValidTile(tileID)) {
  val y1 = getTileY1(tileID)
} else {
  throw new IllegalArgumentException(s"Invalid tileID: $tileID")
}
```

## API Test: `getTileY2`

### Signature
```scala
def getTileY2(tileID: Int): Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:139_

_Source doc:_ Computes the high index of a row (scanline) in the given tile (inclusive). If this tile is in the last row, the returned value will be the last row in the entire raster @param tileID the ID of the tile @return

### Goal
Calculates the inclusive high-index row (scanline) boundary for a specific raster tile within the global raster coordinate system.

### Parameters
- `tileID` (`Int`): The unique identifier of the tile for which the row boundary is requested.

### Input
- **Data**: Requires an initialized raster metadata context where tiles are indexed.
- **Preconditions**: The `tileID` must correspond to a valid tile within the current raster's grid structure.

### Output
Returns `Int` — The inclusive high index of the row (scanline) for the specified tile. If the tile is located in the final row of the raster, the returned value corresponds to the last row of the entire raster.

### Valid Call Patterns
```scala
// Inferred from signature: assumes an instance of a class containing raster metadata
val highRowIndex = rasterMetadata.getTileY2(tileID)
```

### LLM Instruction Prompt
- Use `getTileY2` when you need to determine the vertical extent of a specific tile in a raster grid.
- Ensure the `tileID` provided is an integer representing a valid tile index within the raster's metadata.
- Note that the returned value is inclusive and represents the scanline index.

### Prompt Snippet
```text
// Example of calculating the vertical boundary for a tile
val tileID = 5
val y2 = rasterMetadata.getTileY2(tileID)
```

### Common Failure Modes
- **Invalid Tile ID**: Passing a `tileID` that does not exist in the current raster metadata will result in an index out of bounds or an incorrect calculation.
- **Context Mismatch**: Calling this method on an object that has not been initialized with valid raster metadata.

### Fix Code Hint
```scala
// Ensure the tileID is within the valid range of the raster grid
if (tileID >= 0 && tileID < totalTiles) {
  val y2 = rasterMetadata.getTileY2(tileID)
} else {
  // Handle invalid tile ID
}
```

## API Test: `gridToGridTransform`

### Signature
```scala
def gridToGridTransform(sourceMetadata: RasterMetadata, targetMetadata: RasterMetadata): MathTransform
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:425_

_Source doc:_ Create a math transform that transforms grid coordinates from the source metadata to the target metadata @param sourceMetadata @param targetMetadata @return

### Goal
Generates a `MathTransform` object to map pixel-based grid coordinates from one raster's spatial reference system and resolution to another.

### Parameters
- `sourceMetadata` (`RasterMetadata`): The metadata object defining the grid structure, extent, and coordinate system of the input raster.
- `targetMetadata` (`RasterMetadata`): The metadata object defining the grid structure, extent, and coordinate system of the output raster.

### Input
Requires two valid `RasterMetadata` instances. These are typically derived from raster data sources (e.g., GeoTIFF or HDF files) loaded via the Beast API. The caller must ensure that both metadata objects are initialized and represent valid spatial extents.

### Output
Returns `MathTransform` — A coordinate transformation object that can be used to convert grid indices (row, column) between the source and target raster grids.

### Valid Call Patterns
```scala
// Inferred from signature:
val transform = RasterMetadata.gridToGridTransform(sourceMeta, targetMeta)
```

### LLM Instruction Prompt
- Use `gridToGridTransform` when you need to align or reproject grid coordinates between two different raster datasets.
- Always pass two `RasterMetadata` objects as arguments.
- The returned `MathTransform` is a standard geospatial utility for coordinate mapping; ensure the resulting transform is applied to the correct coordinate space.

### Prompt Snippet
```text
val transform = RasterMetadata.gridToGridTransform(sourceMetadata, targetMetadata)
```

### Common Failure Modes
- **Null Metadata**: Passing uninitialized or null `RasterMetadata` objects will result in a `NullPointerException`.
- **Incompatible Projections**: If the source and target metadata represent coordinate systems that cannot be mathematically transformed into one another, the resulting `MathTransform` may produce invalid coordinates or runtime errors during execution.
- **Coordinate Mismatch**: Attempting to use the transform on coordinates outside the defined bounds of the `sourceMetadata` may lead to unexpected results or out-of-bounds errors.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from valid raster sources
val sourceMeta = rasterRDD.metadata
val targetMeta = targetRaster.metadata
val transform = RasterMetadata.gridToGridTransform(sourceMeta, targetMeta)
```

## API Test: `gridToModel`

### Signature
```scala
def gridToModel(i: Double, j: Double, outPoint: Point2D.Double): Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:150_

_Source doc:_ Converts a point location from the grid (pixel) space to the model (world) space @param i the position of the column @param j the position of the row @param outPoint the output point that contains the model coordinates

### Goal
Converts a specific pixel coordinate (column, row) into its corresponding geographic or projected world coordinate within the model space.

### Parameters
- `i` (`Double`): The column index (x-coordinate) in the raster grid space.
- `j` (`Double`): The row index (y-coordinate) in the raster grid space.
- `outPoint` (`Point2D.Double`): A mutable `Point2D.Double` object that will be populated with the resulting model coordinates.

### Input
The caller must provide a valid `Point2D.Double` instance to serve as the output container. This function assumes the context of a raster metadata object that defines the transformation mapping between grid space and model space.

### Output
Returns `Unit`. The result is stored in the provided `outPoint` object, which is updated in-place with the calculated model (world) coordinates.

### Valid Call Patterns
```scala
// Inferred from signature:
val outPoint = new Point2D.Double()
rasterMetadata.gridToModel(i, j, outPoint)
```

### LLM Instruction Prompt
- When calling `gridToModel`, you must instantiate a `java.awt.geom.Point2D.Double` object first to act as the output buffer.
- The function does not return a value; always access the `outPoint` object after the call to retrieve the transformed coordinates.
- Ensure `i` and `j` are provided as `Double` types.

### Prompt Snippet
```text
val outPoint = new Point2D.Double()
[instance].gridToModel(i, j, outPoint)
// Use outPoint.x and outPoint.y for the model coordinates
```

### Common Failure Modes
- **Null Pointer Exception**: Passing a `null` object as the `outPoint` parameter.
- **Coordinate Out of Bounds**: Providing `i` or `j` values that fall outside the defined raster dimensions, which may result in model coordinates outside the valid geographic extent.
- **Mutable State Misuse**: Assuming the function returns a new point rather than modifying the existing `outPoint` instance.

### Fix Code Hint
```scala
// Ensure the output point is initialized before passing it to the function
val resultPoint = new Point2D.Double()
rasterMetadata.gridToModel(col, row, resultPoint)
println(s"Model coordinates: ${resultPoint.x}, ${resultPoint.y}")
```

## API Test: `hashCode`

### Signature
```scala
def hashCode(): Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:204_

### Goal
Computes a hash code value for the object, enabling efficient storage and retrieval in hash-based collections within Beast's spatio-temporal processing workflows.

### Parameters
_None._

### Input
The caller must provide an initialized Beast object (e.g., a raster metadata or feature instance). No specific file format is required as this operates on in-memory objects.

### Output
Returns `Int` — A hash code value representing the object's state, consistent with the standard Java/Scala `hashCode` contract.

### Valid Call Patterns
```scala
// Inferred from signature:
val hash = instance.hashCode()
```

### LLM Instruction Prompt
- Use `hashCode()` only on initialized Beast objects.
- Do not assume the hash code is stable across different JVM executions or different versions of the Beast library.
- Ensure the object is not null before invoking the method to avoid `NullPointerException`.

### Prompt Snippet
```text
val hash = [instance].hashCode()
```

### Common Failure Modes
- Invoking `hashCode()` on a null reference, resulting in a `NullPointerException`.
- Expecting the hash code to be persistent across different application runs or serialized data, which is not guaranteed by the underlying implementation.

### Fix Code Hint
```scala
// Ensure the object is initialized before calling hashCode
if (obj != null) {
  val h = obj.hashCode()
}
```

## API Test: `inferSchema`

### Signature
```scala
def inferSchema(names: Array[String], values: Array[Any]): StructType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:101_

_Source doc:_ Infer schema from the values. If a value is `null`, the type is inferred as [[BinaryType]] @param values the array of values @return

### Goal
Automatically generates a Spark `StructType` schema by inspecting the provided array of values and their corresponding field names.

### Parameters
- `names` (`Array[String]`): An array of strings representing the column names for the schema.
- `values` (`Array[Any]`): An array of values used to determine the data type for each corresponding column.

### Input
The function requires two arrays of equal length: `names` and `values`. The `values` array can contain any object type supported by Spark. If an element in the `values` array is `null`, the corresponding field in the resulting `StructType` will be assigned `BinaryType`.

### Output
Returns `StructType` — A Spark SQL schema definition derived from the provided values, suitable for creating DataFrames or RDDs of features.

### Valid Call Patterns
```scala
// Inferred from signature:
val names = Array("id", "value", "metadata")
val values = Array(1, 2.5, null)
val schema = inferSchema(names, values)
```

### LLM Instruction Prompt
- When calling `inferSchema`, ensure that the `names` and `values` arrays are of the same length.
- Use this function when you need to dynamically define a schema for spatial data processing where types are not known at compile time.
- Note that `null` values in the `values` array will result in a `BinaryType` schema assignment.

### Prompt Snippet
```text
val schema = inferSchema(Array("col1", "col2"), Array(val1, val2))
```

### Common Failure Modes
- **Array Mismatch**: Providing `names` and `values` arrays of different lengths will likely result in an `ArrayIndexOutOfBoundsException` or an incomplete schema.
- **Null Type Inference**: Relying on `null` values in the input array will force the schema to `BinaryType`, which may cause downstream type-mismatch errors if the actual data contains specific types like `IntegerType` or `DoubleType`.

### Fix Code Hint
```scala
// Ensure arrays are aligned and handle nulls if specific types are required
val names = Array("name", "value")
val values = Array("example", 10.0) // Avoid nulls to ensure specific type inference
val schema = inferSchema(names, values)
```

## API Test: `isPixelInRange`

### Signature
```scala
def isPixelInRange(i: Int, j: Int)
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:194_

_Source doc:_ Returns `true` iff the given pixel location falls within the raster boundary

### Goal
Determines if a specific pixel coordinate (i, j) lies within the spatial extent of a raster object to prevent out-of-bounds access during pixel-level operations.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel.
- `j` (`Int`): The row index (y-coordinate) of the pixel.

### Input
The function operates on an instance of a raster metadata object (e.g., associated with an `ITile`). The caller must ensure the raster has been initialized or loaded via `sc.geoTiff` or similar raster-loading APIs.

### Output
Returns `Boolean`: `true` if the pixel location is within the raster boundary, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
rasterMetadata.isPixelInRange(i, j)
```

### LLM Instruction Prompt
- Use `isPixelInRange` to validate pixel coordinates before performing map, filter, or reshape operations on `ITile` data.
- Always verify the coordinate system alignment if performing concurrent raster-vector operations (Raptor).

### Prompt Snippet
```text
val inBounds = rasterMetadata.isPixelInRange(col, row)
if (inBounds) {
  // perform pixel operation
}
```

### Common Failure Modes
- **Out-of-bounds access**: Calling pixel-level operations without checking `isPixelInRange` may result in runtime exceptions if the indices exceed the raster dimensions.
- **Coordinate Mismatch**: Passing indices that are valid for one raster but not another when working with multiple `ITile` datasets in a `raptorJoin`.

### Fix Code Hint
```scala
if (rasterMetadata.isPixelInRange(i, j)) {
  val pixelValue = raster.getPixel(i, j)
  // process value
} else {
  // handle out-of-bounds case
}
```

## API Test: `length`

### Signature
```scala
def length: Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:117_

### Goal
Returns the number of attributes (fields) contained within an `IFeature` object.

### Parameters
_None._

### Input
The caller must provide an initialized `IFeature` object. No specific file format is required as this operates on the in-memory representation of a feature after it has been loaded or created within the Spark environment.

### Output
Returns `Int` — The total count of attributes associated with the feature.

### Valid Call Patterns
```scala
// Inferred from signature:
val feature: IFeature = ...
val numAttributes: Int = feature.length
```

### LLM Instruction Prompt
- Use `length` to determine the attribute count of an `IFeature` instance before iterating through its fields or performing schema-dependent operations.
- Ensure the `IFeature` instance is not null before calling `length` to avoid `NullPointerException`.

### Prompt Snippet
```text
// Example usage:
val feature: IFeature = ...
val attrCount = feature.length
```

### Common Failure Modes
- **Null Reference**: Calling `length` on an uninitialized or null `IFeature` variable will result in a runtime exception.
- **Schema Mismatch**: Assuming a fixed `length` for features when processing datasets with heterogeneous schemas (e.g., mixed feature types in a single RDD) can lead to `IndexOutOfBoundsException` if the code expects a specific number of attributes.

### Fix Code Hint
```scala
// Always verify the feature is not null and check length before accessing indices
if (feature != null) {
  val count = feature.length
  // Proceed with attribute access
}
```

## API Test: `modelToGrid`

### Signature
```scala
def modelToGrid(x: Double, y: Double, outPoint: Point2D.Double): Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:161_

_Source doc:_ Converts a point location from model (world) space to grid (pixel) space @param x the x-coordinate in the model space (e.g., longitude) @param y the y-coordinate in the model space (e.g., latitude) @param outPoint the output point that contains the grid coordinates

### Goal
Converts a specific geographic or projected coordinate (model space) into its corresponding pixel coordinate (grid space) within a raster dataset.

### Parameters
- `x` (`Double`): The x-coordinate in the model space (e.g., longitude or projected easting).
- `y` (`Double`): The y-coordinate in the model space (e.g., latitude or projected northing).
- `outPoint` (`Point2D.Double`): A mutable `Point2D.Double` object that will be updated with the calculated grid coordinates.

### Input
The caller must provide valid model-space coordinates corresponding to the spatial extent of the raster. This function is typically called on a `RasterMetadata` object associated with a loaded raster (e.g., GeoTIFF).

### Output
Returns `Unit`. The result is stored in the provided `outPoint` object, where `outPoint.x` and `outPoint.y` represent the pixel column and row indices, respectively.

### Valid Call Patterns
```scala
// Inferred from signature: assumes an instance of RasterMetadata
val metadata: RasterMetadata = ... 
val gridPoint = new Point2D.Double()
metadata.modelToGrid(modelX, modelY, gridPoint)
```

### LLM Instruction Prompt
- When calling `modelToGrid`, ensure you have an initialized `Point2D.Double` object to act as the output container.
- The function does not return a value; you must access the `x` and `y` fields of the `outPoint` object after the call.
- Ensure the `x` and `y` inputs are in the same coordinate reference system (CRS) as the raster metadata.

### Prompt Snippet
```text
val gridPoint = new java.awt.geom.Point2D.Double()
metadata.modelToGrid(x, y, gridPoint)
// gridPoint now contains the pixel coordinates
```

### Common Failure Modes
- **Null Pointer Exception**: Passing a `null` object for `outPoint` will cause a runtime error.
- **Coordinate Mismatch**: Providing coordinates outside the raster's spatial extent may result in grid values that fall outside the valid pixel range (e.g., negative indices or indices exceeding image dimensions).
- **CRS Mismatch**: Using model coordinates in a different CRS than the raster metadata will result in incorrect grid mapping.

### Fix Code Hint
```scala
// Ensure the point is instantiated before passing to the function
val outPoint = new java.awt.geom.Point2D.Double()
metadata.modelToGrid(x, y, outPoint)
```

## API Test: `numTiles`

### Signature
```scala
def numTiles: Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:61_

_Source doc:_ Total number of tiles in the raster layer

### Goal
Retrieves the total count of individual tiles contained within a raster layer to assist in metadata inspection and workload estimation.

### Parameters
_None._

### Input
The caller must have an initialized raster object or metadata instance (e.g., `RasterMetadata` or a loaded `RasterRDD`) that has been successfully parsed from a supported format such as GeoTIFF or HDF.

### Output
Returns `Int` — The total number of tiles that constitute the raster layer.

### Valid Call Patterns
```scala
// Inferred from signature:
val count: Int = rasterMetadata.numTiles
```

### LLM Instruction Prompt
- Use `numTiles` only when operating on a valid raster metadata object or raster layer.
- Do not attempt to pass arguments to this function as it takes none.
- Ensure the raster data has been loaded via `sc.geoTiff` or similar I/O methods before accessing metadata.

### Prompt Snippet
```text
// Accessing the tile count from a raster metadata object
val tileCount = raster.numTiles
```

### Common Failure Modes
- **Null Reference**: Calling `numTiles` on an uninitialized or null raster object will result in a `NullPointerException`.
- **Unloaded Data**: Attempting to access metadata before the raster file has been fully loaded into the Spark context will result in incomplete or inaccurate tile counts.
- **Type Mismatch**: Ensure the object being queried is a valid raster type (e.g., `ITile` or `RasterMetadata`) as defined in the Beast API.

### Fix Code Hint
```scala
// Ensure the raster is loaded before calling metadata methods
val raster = sc.geoTiff("path/to/raster.tif")
val count = raster.metadata.numTiles
```

## API Test: `numTilesX`

### Signature
```scala
def numTilesX: Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:55_

_Source doc:_ Number of tiles per row

### Goal
Retrieves the total number of tiles along the horizontal axis (columns) for a given raster dataset.

### Parameters
_None._

### Input
The caller must have an initialized raster metadata object or a raster structure that provides access to tiling configuration. This function is typically used in the context of `RasterRDD` or raster metadata processing within the Beast framework.

### Output
Returns `Int` — The count of tiles spanning the width of the raster grid.

### Valid Call Patterns
```scala
// Inferred from signature:
val tilesPerRow = rasterMetadata.numTilesX
```

### LLM Instruction Prompt
- Use `numTilesX` when you need to determine the horizontal dimension of a raster's tiling scheme.
- Ensure the object being called is a valid raster metadata instance or a type that exposes the `numTilesX` property.
- This is a read-only property; do not attempt to pass arguments to it.

### Prompt Snippet
```text
// Accessing raster tiling metadata
val xTiles = metadata.numTilesX
```

### Common Failure Modes
- Calling `numTilesX` on a null or uninitialized raster metadata object, resulting in a `NullPointerException`.
- Assuming the value represents pixel width rather than tile count; ensure the distinction between tile dimensions and pixel dimensions is maintained.

### Fix Code Hint
```scala
// Ensure the metadata is loaded before accessing tiling properties
if (metadata != null) {
  val xTiles = metadata.numTilesX
} else {
  // Handle uninitialized metadata
}
```

## API Test: `numTilesY`

### Signature
```scala
def numTilesY: Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:58_

_Source doc:_ Number of tiles per column

### Goal
Retrieves the total number of vertical tiles (rows of tiles) defined in the raster metadata to support spatial partitioning and tiling operations.

### Parameters
_None._

### Input
The caller must have an initialized raster metadata object (e.g., associated with a `RasterRDD` or GeoTIFF source). The raster must be properly loaded via `sc.geoTiff` or similar Beast I/O methods to ensure metadata is populated.

### Output
Returns `Int` — The count of tiles along the Y-axis (column-wise) of the raster grid.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata = raster.metadata
val yTiles = metadata.numTilesY
```

### LLM Instruction Prompt
- Use `numTilesY` only when interacting with `RasterMetadata` objects within the Beast framework.
- Do not confuse this with pixel-level dimensions; this returns the count of tiles, not the count of pixels.
- Ensure the raster source has been initialized via `SparkContext` spatial methods before accessing metadata.

### Prompt Snippet
```text
// Accessing raster metadata to determine grid structure
val yCount = rasterMetadata.numTilesY
```

### Common Failure Modes
- **Null Metadata**: Calling `numTilesY` on an uninitialized or null metadata object will result in a `NullPointerException`.
- **Context Mismatch**: Attempting to call this on a raw RDD without first accessing the associated `RasterMetadata` or `ITile` properties.
- **Assumption of Uniformity**: Assuming `numTilesY` is equivalent to the total number of tiles; it only represents the vertical dimension of the tile grid.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the raster RDD or source
val metadata = rasterRDD.metadata
val yTiles = metadata.numTilesY
```

## API Test: `rasterHeight`

### Signature
```scala
def rasterHeight: Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:52_

_Source doc:_ Total number of rows (scanlines) in the raster layer

### Goal
Retrieves the vertical dimension of a raster layer, expressed as the total number of rows or scanlines.

### Parameters
_None._

### Input
The caller must have an initialized raster object or metadata instance (e.g., `ITile` or associated metadata) derived from supported formats such as GeoTIFF or HDF. The raster must be successfully loaded into the Spark environment via `SparkContext` methods like `sc.geoTiff`.

### Output
Returns `Int` — The total number of rows (scanlines) present in the raster layer.

### Valid Call Patterns
```scala
// Inferred from signature:
val height: Int = raster.rasterHeight
```

### LLM Instruction Prompt
- Use `rasterHeight` to inspect the vertical resolution of a raster object.
- Ensure the receiver is a valid Beast raster type (e.g., `ITile` or a metadata-aware raster object).
- Do not pass arguments to this function as it takes none.

### Prompt Snippet
```text
// Accessing raster dimensions
val rows = raster.rasterHeight
```

### Common Failure Modes
- **Null Pointer Exception**: Calling `rasterHeight` on an uninitialized or null raster object.
- **Type Mismatch**: Attempting to call this on a vector `IFeature` or non-raster RDD; ensure the object is a valid `ITile` or raster metadata type.
- **Contextual Scope**: Calling this outside of a Spark-enabled environment where the raster data has not been properly loaded or partitioned.

### Fix Code Hint
```scala
// Ensure the raster is loaded correctly before accessing metadata
val raster = sc.geoTiff("path/to/raster")
val height = raster.rasterHeight
```

## API Test: `rasterWidth`

### Signature
```scala
def rasterWidth: Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:49_

_Source doc:_ Total number of columns in the raster layer

### Goal
Retrieves the total number of columns (width) in a raster layer to facilitate spatial indexing and coordinate transformation within the Beast framework.

### Parameters
_None._

### Input
The caller must have an initialized raster object or metadata structure that conforms to the Beast raster API. The raster data must be loaded via supported formats such as GeoTIFF or HDF.

### Output
Returns `Int` — The total count of pixels along the horizontal axis (columns) of the raster layer.

### Valid Call Patterns
```scala
// Inferred from signature:
val width: Int = raster.rasterWidth
```

### LLM Instruction Prompt
- Use `rasterWidth` when you need to determine the horizontal dimension of a raster layer for spatial calculations or grid-based processing.
- Ensure the raster object is properly initialized before calling this method.
- This method returns an `Int` representing the column count; do not assume it represents the total pixel count (which would be width * height).

### Prompt Snippet
```text
// To get the width of a raster layer:
val width = raster.rasterWidth
```

### Common Failure Modes
- **Null Pointer Exception**: Calling `rasterWidth` on an uninitialized or null raster object.
- **Context Mismatch**: Attempting to call this on a vector-based `IFeature` rather than a raster-based type.
- **Invalid Raster State**: Calling this on a raster that failed to load or was improperly partitioned, leading to undefined metadata.

### Fix Code Hint
```scala
// Ensure the raster is loaded and valid before accessing metadata
if (raster != null) {
  val width = raster.rasterWidth
  println(s"Raster width is: $width")
} else {
  throw new IllegalStateException("Raster object is not initialized.")
}
```

## API Test: `read`

### Signature
```scala
def read(kryo: Kryo, in: Input): Unit
def read(kryo: Kryo, input: Input): Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:115  (+1 more definition site/overload)_

### Goal
Deserializes a feature object from a Kryo input stream for efficient storage and retrieval within the Beast framework.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for the deserialization process.
- `in` (`Input`): The input stream containing the serialized feature data.

### Input
The caller must provide a valid `Kryo` instance and an `Input` stream that contains data previously serialized by a corresponding `write` operation. This function assumes the input stream is positioned at the start of the serialized feature record.

### Output
Returns `Unit` — The function performs an in-place update of the object state by reading the serialized data from the provided `Input` stream.

### Valid Call Patterns
```scala
// Inferred from signature:
feature.read(kryo, input)
```

### LLM Instruction Prompt
- Use `read` only when deserializing Beast feature objects within a Kryo-based serialization context.
- Ensure the `kryo` instance is properly configured for the Beast environment.
- Always pass a valid `Input` stream that corresponds to the expected feature data format.

### Prompt Snippet
```text
// Deserializing a feature using Kryo
val feature = new Feature()
feature.read(kryo, input)
```

### Common Failure Modes
- **Stream Position**: Calling `read` when the `Input` stream is not positioned at the beginning of a valid serialized feature record, leading to `KryoException` or corrupted object state.
- **Incompatible Kryo Instance**: Using a `Kryo` instance that has not been registered with the necessary Beast feature classes, resulting in serialization errors.
- **Null Arguments**: Passing a `null` `kryo` or `input` object, which will trigger a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure the input stream is valid and the Kryo instance is initialized
if (input != null && kryo != null) {
  feature.read(kryo, input)
} else {
  throw new IllegalArgumentException("Kryo and Input must not be null")
}
```

## API Test: `readExternal`

### Signature
```scala
def readExternal(in: ObjectInput): Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:93  (+1 more definition site/overload)_

### Goal
Deserializes a feature object from an `ObjectInput` stream, restoring its state within the Beast spatial data framework.

### Parameters
- `in` (`ObjectInput`): The input stream from which the feature's serialized state is read.

### Input
The caller must provide a valid `java.io.ObjectInput` stream containing a previously serialized Beast feature. The stream must be positioned at the start of the feature's serialized data block.

### Output
Returns `Unit` — The method updates the internal state of the current object instance based on the data read from the stream.

### Valid Call Patterns
```scala
// Inferred from signature:
featureInstance.readExternal(in)
```

### LLM Instruction Prompt
- When using `readExternal`, ensure the object instance is already initialized before calling this method.
- The `ObjectInput` must be sourced from a valid serialization context compatible with Beast's `IFeature` implementations.
- This method is intended for low-level serialization/deserialization workflows; prefer high-level `SparkContext` I/O methods (e.g., `sc.shapefile`) for standard data loading.

### Prompt Snippet
```text
// Example of manual deserialization using readExternal
val in: ObjectInput = ... // obtained from a stream
val feature = new MyFeature()
feature.readExternal(in)
```

### Common Failure Modes
- `EOFException`: Occurs if the `ObjectInput` stream is empty or does not contain the expected number of bytes for the feature.
- `ClassCastException`: Occurs if the data in the stream does not correspond to the expected feature type.
- `NullPointerException`: Occurs if the provided `ObjectInput` is null.

### Fix Code Hint
```scala
// Ensure the stream is valid and the object is instantiated
if (in != null) {
  feature.readExternal(in)
} else {
  throw new IllegalArgumentException("ObjectInput stream cannot be null")
}
```

## API Test: `readExternalObj`

### Signature
```scala
def readExternalObj(in: ObjectInput): StructType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:46_

### Goal
Deserializes a `StructType` schema definition from an `ObjectInput` stream, typically used for reconstructing spatial data schemas in Beast's serialization workflows.

### Parameters
- `in` (`ObjectInput`): An input stream providing the serialized representation of a Spark `StructType`.

### Input
The caller must provide an `ObjectInput` stream that contains a previously serialized `StructType`. This is typically used in scenarios where schema metadata is persisted alongside spatial datasets or transmitted across Spark nodes.

### Output
Returns `StructType` — The reconstructed Spark SQL schema representing the structure of the spatial features or raster metadata.

### Valid Call Patterns
```scala
// Inferred from signature:
val schema = readExternalObj(in)
```

### LLM Instruction Prompt
- Use `readExternalObj` when you need to reconstruct a `StructType` from an `ObjectInput` stream during custom serialization or deserialization tasks within the Beast framework.
- Ensure the `ObjectInput` provided is valid and contains a serialized `StructType` to avoid stream corruption errors.

### Prompt Snippet
```text
val schema = readExternalObj(in)
```

### Common Failure Modes
- **EOFException**: Occurs if the `ObjectInput` stream is empty or does not contain a valid serialized object.
- **ClassCastException**: Occurs if the object serialized in the stream is not an instance of `StructType`.
- **Stream Corruption**: If the `ObjectInput` was not written using the corresponding `writeExternal` method, deserialization will fail.

### Fix Code Hint
```scala
import java.io.ObjectInput
import org.apache.spark.sql.types.StructType

// Ensure the stream is properly initialized and contains the expected schema data
try {
  val schema: StructType = readExternalObj(in)
} catch {
  case e: Exception => println(s"Failed to deserialize schema: ${e.getMessage}")
}
```

## API Test: `readIn`

### Signature
```scala
def readIn(kryo:Kryo,in: Input): StructType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:86_

### Goal
Deserializes a `StructType` schema definition from a serialized input stream using the Kryo serialization framework.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for deserialization, required to manage the object graph and type registration.
- `in` (`Input`): The input stream containing the serialized `StructType` data.

### Input
The caller must provide a valid `Kryo` instance and an `Input` stream that contains a previously serialized Spark `StructType`. This function assumes the input stream is positioned at the start of the serialized schema data.

### Output
Returns `StructType` — The Spark SQL schema definition reconstructed from the input stream.

### Valid Call Patterns
```scala
// Inferred from signature:
val schema = readIn(kryoInstance, inputStream)
```

### LLM Instruction Prompt
- Use `readIn` when you need to reconstruct a Spark `StructType` schema that was previously serialized using Kryo.
- Ensure the `Kryo` instance is properly initialized and that the `Input` stream is valid and contains the expected schema bytes.

### Prompt Snippet
```text
val schema = readIn(kryo, input)
```

### Common Failure Modes
- **KryoException**: Occurs if the `Input` stream does not contain valid serialized data or if the `Kryo` instance is not configured to handle the `StructType` class.
- **NullPointerException**: Occurs if either the `kryo` or `in` arguments are passed as `null`.
- **Stream Corruption**: If the `Input` stream is truncated or contains malformed data, the deserialization process will fail.

### Fix Code Hint
```scala
// Ensure the input stream is correctly initialized before calling readIn
val input = new Input(new FileInputStream("schema.bin"))
val schema = readIn(new Kryo(), input)
```

## API Test: `readType`

### Signature
```scala
def readType(in: ObjectInput): DataType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:191  (+1 more definition site/overload)_

_Source doc:_ Read a data type from an input stream that was written with the function [[writeType()]] @param in the input stream to read from @return the created data type

### Goal
Deserializes a `DataType` from an `ObjectInput` stream that was previously serialized using the `writeType()` function.

### Parameters
- `in` (`ObjectInput`): The input stream containing the serialized `DataType` information.

### Input
The caller must provide an `ObjectInput` stream that contains data specifically written by the `writeType()` method. This function assumes the stream is positioned at the start of the serialized `DataType` record.

### Output
Returns `DataType` — The reconstructed `DataType` object representing the schema or type definition stored in the stream.

### Valid Call Patterns
```scala
// Inferred from signature:
val dataType = readType(in)
```

### LLM Instruction Prompt
- Use `readType` only when deserializing `DataType` objects from an `ObjectInput` stream.
- Ensure the stream provided to `in` was populated by a corresponding `writeType()` call to avoid stream corruption or `EOFException`.
- This function is typically used within custom serialization logic for Beast spatial types.

### Prompt Snippet
```text
val in: ObjectInput = ... // initialized input stream
val dataType = readType(in)
```

### Common Failure Modes
- **Stream Mismatch**: Calling `readType` on a stream that does not contain data written by `writeType()` will result in an `IOException` or incorrect type reconstruction.
- **EOFException**: Occurs if the `ObjectInput` stream is empty or if the pointer is not at the beginning of the serialized `DataType` record.
- **Closed Stream**: Attempting to read from an `ObjectInput` that has already been closed by another process.

### Fix Code Hint
```scala
// Ensure the stream is valid and contains the expected serialized data
try {
  val dataType = readType(in)
} catch {
  case e: java.io.IOException => println("Failed to deserialize DataType: " + e.getMessage)
}
```

## API Test: `readValue`

### Signature
```scala
def readValue(in: ObjectInput, t: DataType): Any
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:238_

_Source doc:_ Read a single value from an input stream according to the given data type. @param in the input stream to read from @param t the type of the attribute @return the attribute value read from the input stream

### Goal
Deserializes a single attribute value from a serialized input stream based on a specified Beast `DataType`.

### Parameters
- `in` (`ObjectInput`): The input stream containing the serialized attribute data.
- `t` (`DataType`): The schema definition representing the type of the attribute to be read.

### Input
The caller must provide an `ObjectInput` stream that has been positioned at the start of the attribute data. The `DataType` provided must match the schema used during the serialization of the input stream to ensure correct byte interpretation.

### Output
Returns `Any` — The deserialized attribute value, which must be cast to the appropriate Scala type corresponding to the provided `DataType`.

### Valid Call Patterns
```scala
// Inferred from signature:
val value = readValue(in, dataType)
```

### LLM Instruction Prompt
- When calling `readValue`, ensure the `DataType` passed matches the schema of the serialized data in the `ObjectInput`.
- Always cast the returned `Any` value to the expected type (e.g., `Int`, `Double`, `String`) based on the `DataType` used.

### Prompt Snippet
```text
val attributeValue = readValue(inputStream, attributeType).asInstanceOf[ExpectedType]
```

### Common Failure Modes
- **Type Mismatch**: Providing a `DataType` that does not match the actual serialized bytes in the `ObjectInput` will result in corrupted data or runtime exceptions.
- **Stream Exhaustion**: Calling `readValue` when the `ObjectInput` has reached the end of the stream or is improperly positioned.
- **Null Input**: Passing a null `ObjectInput` will trigger a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure the stream is valid and the type matches the schema
try {
  val value = readValue(in, t)
  // Cast to the specific type expected for the attribute
  val typedValue = value.asInstanceOf[Int] 
} catch {
  case e: Exception => println(s"Failed to read attribute: ${e.getMessage}")
}
```

## API Test: `reproject`

### Signature
```scala
def reproject(targetCRS: CoordinateReferenceSystem): RasterMetadata
def reproject(metadata: Array[RasterMetadata], targetCRS: CoordinateReferenceSystem): RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:385  (+1 more definition site/overload)_

_Source doc:_ Creates a new [[RasterMetadata]] that reprojects all the given metadata into one with the target CRS @param metadata the list of metadata to combine @param targetCRS the target coordinate reference system (CRS) @return a unified metadata that covers the space of all input metadata with a reasonable resolution

### Goal
Transforms raster metadata into a new coordinate reference system, providing a unified spatial definition that covers the extent of input metadata at a calculated resolution.

### Parameters
- `metadata` (`Array[RasterMetadata]`): An array of existing raster metadata objects to be combined and reprojected into a single unified spatial reference.
- `targetCRS` (`CoordinateReferenceSystem`): The destination coordinate reference system to which the raster metadata should be transformed.

### Input
The function requires valid `RasterMetadata` objects, typically derived from raster data sources like GeoTIFF or HDF files. Preconditions include ensuring the `targetCRS` is correctly initialized (e.g., via `CRS.decode("EPSG:XXXX")`). When using the array-based overload, all input metadata must be spatially compatible for unification.

### Output
Returns `RasterMetadata` — A new, unified metadata object representing the spatial extent and resolution of the reprojected raster data in the target CRS.

### Valid Call Patterns
```scala
// Reprojecting existing raster data to a specific EPSG code
val temperature: RasterRDD[Float] = sc.hdfFile("data.hdf", "LST_Day_1km")
temperature.reproject(CRS.decode("EPSG:4326"))

// Combining multiple metadata objects into a single reprojected metadata
val unifiedMetadata = RasterMetadata.reproject(metadataArray, targetCRS)
```

### LLM Instruction Prompt
- Use `reproject` when you need to align raster datasets to a common coordinate system before performing spatial operations like `raptorJoin`.
- Always provide a valid `CoordinateReferenceSystem` object; use `CRS.decode` for standard EPSG codes.
- When working with `RasterRDD`, ensure the operation is applied to the metadata associated with the raster.

### Prompt Snippet
```text
// Example of reprojecting to EPSG:4326
val target = CRS.decode("EPSG:4326")
val newMetadata = raster.reproject(target)
```

### Common Failure Modes
- **Invalid CRS**: Passing a null or improperly formatted CRS string to `CRS.decode` will cause a runtime exception.
- **Metadata Mismatch**: Providing an empty `Array[RasterMetadata]` to the static-style overload may result in an undefined or empty spatial extent.
- **Type Mismatch**: Attempting to call `reproject` on non-raster types; ensure the receiver is a `RasterRDD` or `RasterMetadata` object.

### Fix Code Hint
```scala
// Ensure CRS is valid before calling reproject
val crs = try { CRS.decode("EPSG:4326") } catch { case e: Exception => null }
if (crs != null) {
  val reprojected = raster.reproject(crs)
}
```

## API Test: `rescale`

### Signature
```scala
def rescale(newRasterWidth: Int, newRasterHeight: Int): RasterMetadata
def rescale(metadata: Array[RasterMetadata], rasterWidth: Int, rasterHeight: Int): RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:354  (+1 more definition site/overload)_

_Source doc:_ Creates a new RasterMetadata that scales the input space covered by all input metadata into the given width and height in pixels @param metadata a list of metadata to combine and rescale @return one raster metadata that combines all inputs

### Goal
Calculates the new spatial metadata required to transform or resample a raster dataset into a specific pixel width and height.

### Parameters
- `metadata` (`Array[RasterMetadata]`): An array of existing raster metadata objects to be aggregated and scaled.
- `rasterWidth` (`Int`): The target width in pixels for the rescaled raster.
- `rasterHeight` (`Int`): The target height in pixels for the rescaled raster.

### Input
The function operates on `RasterMetadata` objects, which are typically derived from `RasterRDD` or loaded via `sc.geoTiff` / `sc.hdfFile`. Precondition: The input metadata must represent valid spatial extents; when using the array-based overload, all metadata in the array should share a compatible coordinate reference system to ensure a meaningful combined extent.

### Output
Returns `RasterMetadata` — A new metadata object representing the combined spatial extent mapped to the specified pixel dimensions.

### Valid Call Patterns
```scala
// Rescaling an existing RasterRDD
val rescaled = raster.rescale(360, 180)

// Combining multiple metadata objects into a new target resolution
val combinedMetadata = RasterMetadata.rescale(Array(meta1, meta2), 360, 180)
```

### LLM Instruction Prompt
- Use `rescale` when you need to define the target dimensions for a raster transformation.
- If working with a `RasterRDD`, call the instance method `rescale(width, height)`.
- If aggregating multiple metadata sources, use the static-like `RasterMetadata.rescale` overload.
- Ensure `rasterWidth` and `rasterHeight` are positive integers.

### Prompt Snippet
```text
val rescaledMeta = raster.rescale(targetWidth, targetHeight)
```

### Common Failure Modes
- **Invalid Dimensions**: Providing non-positive integers for `rasterWidth` or `rasterHeight` will result in an invalid metadata object.
- **Coordinate Mismatch**: Attempting to combine `RasterMetadata` objects with incompatible coordinate systems (CRS) may lead to unexpected spatial extents in the resulting metadata.
- **Type Mismatch**: Ensure the receiver is an instance of `RasterMetadata` or `RasterRDD` when using the instance-based overload.

### Fix Code Hint
```scala
// Ensure dimensions are positive and metadata is initialized
val width = 360
val height = 180
val rescaled = if (width > 0 && height > 0) raster.rescale(width, height) else raster.metadata
```

## API Test: `retile`

### Signature
```scala
def retile(newTileWidth: Int, newTileHeight: Int): RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:291_

_Source doc:_ Creates a new raster metadata with the same information as this one but with different tile width and height @param newTileWidth the new tile width in the new raster metadata @param newTileHeight the new tile height in the new raster metadata @return a new raster metadata that is similar to this one except for the tile width and height

### Goal
Adjusts the tiling configuration of a raster dataset by creating a new `RasterMetadata` object with specified tile dimensions while preserving all other spatial properties.

### Parameters
- `newTileWidth` (`Int`): The desired width of each tile in pixels for the new raster metadata.
- `newTileHeight` (`Int`): The desired height of each tile in pixels for the new raster metadata.

### Input
The function operates on an existing `RasterMetadata` instance. It is typically used in conjunction with `RasterRDD` operations to prepare raster data for storage or downstream processing where specific tile sizes are required for performance or compatibility.

### Output
Returns `RasterMetadata` — A new metadata object containing the updated tile dimensions, which can be used to reconfigure the structure of the associated raster data.

### Valid Call Patterns
```scala
val raster: RasterRDD[Int] = sc.geoTiff[Int]("glc2000_v1_1.tif")
val retiled = raster.retile(64, 64)
```

### LLM Instruction Prompt
- Use `retile` when you need to change the internal tiling structure of a `RasterRDD` or `RasterMetadata` object.
- Ensure the `newTileWidth` and `newTileHeight` are positive integers.
- Note that `retile` returns metadata; if you intend to apply this change to the actual raster data, ensure you chain it with appropriate RDD transformations or save operations.

### Prompt Snippet
```text
// Example of retiling a raster to 64x64 tiles
val retiledMetadata = raster.retile(64, 64)
```

### Common Failure Modes
- **Invalid Dimensions**: Providing non-positive integers for `newTileWidth` or `newTileHeight` may result in invalid raster structures.
- **Metadata Mismatch**: Attempting to use the returned `RasterMetadata` on a dataset that does not align with the new tiling scheme without performing a corresponding data transformation (e.g., `explode`).
- **Performance Overhead**: Setting tile sizes too small can lead to excessive overhead in Spark due to the high number of small partitions.

### Fix Code Hint
```scala
// Ensure dimensions are appropriate for your data scale
val retiled = raster.retile(128, 128)
```

## API Test: `schema`

### Signature
```scala
def schema: StructType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:41_

### Goal
Retrieves the Spark `StructType` definition that describes the schema of the spatial features, including both geometric and attribute fields.

### Parameters
_None._

### Input
The caller must be operating on a dataset or object that implements the `IFeature` interface or a compatible Beast spatial data structure. No specific file format is required as the schema is derived from the in-memory representation of the feature data.

### Output
Returns `StructType` — A Spark SQL schema object representing the structure of the feature, mapping attribute names to their corresponding Spark data types.

### Valid Call Patterns
```scala
// Inferred from signature:
val featureSchema: StructType = feature.schema
```

### LLM Instruction Prompt
- Use `schema` to inspect the structure of `IFeature` objects when you need to programmatically determine the column names or data types of spatial attributes.
- Ensure the object being queried is an instance of a Beast-compatible feature type.

### Prompt Snippet
```text
val mySchema = feature.schema
```

### Common Failure Modes
- **Type Mismatch**: Calling `schema` on an object that does not implement the `IFeature` interface or is not a valid Beast spatial type.
- **Null Reference**: Attempting to access `schema` on a null feature object, which will result in a `NullPointerException`.
- **Contextual Scope**: Attempting to use the returned `StructType` in Spark operations without ensuring the Spark session is active and the necessary Beast dependencies are on the classpath.

### Fix Code Hint
```scala
// Ensure the object is a valid IFeature before accessing the schema
if (feature != null) {
  val s = feature.schema
  println(s"Feature schema: ${s.treeString}")
}
```

## API Test: `this`

### Signature
```scala
def this()
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:53_

_Source doc:_ Default constructor for serialization/deserialization

### Goal
Initializes a new instance of a Beast feature object using the default constructor, primarily intended for framework-level serialization and deserialization processes.

### Parameters
_None._

### Input
No input parameters are required. This constructor is typically invoked by Spark's internal serialization mechanisms (e.g., Kryo) when reconstructing feature objects from distributed storage or network buffers.

### Output
Returns `null` (as per API definition) — The constructor initializes the internal state of the feature object to a default, empty, or uninitialized state suitable for subsequent population by the deserialization framework.

### Valid Call Patterns
```scala
// Inferred from signature: typically invoked by the framework, 
// but can be called in custom implementations of serializable classes.
new Feature()
```

### LLM Instruction Prompt
- Use this constructor only when implementing custom classes that extend Beast feature types and require support for Spark serialization.
- Do not use this constructor to create features for standard data processing workflows; use the appropriate factory methods or `IFeature` implementations provided by the `beast-spark` API instead.

### Prompt Snippet
```text
// Example of using the default constructor in a custom serializable class
class MyCustomFeature extends Feature {
  def this() = {
    this()
    // Initialization logic for deserialization
  }
}
```

### Common Failure Modes
- **Incomplete Initialization**: Calling this constructor manually without subsequently populating the feature's fields (e.g., geometry, attributes) will result in a feature object with null or invalid state, leading to `NullPointerException` or runtime errors during spatial operations.
- **Serialization Mismatch**: If used in a custom class that does not correctly implement the required serialization interfaces, Spark may fail to distribute the object across the cluster.

### Fix Code Hint
```scala
// Instead of manual instantiation, use the provided SparkContext loaders
val features: RDD[IFeature] = sc.shapefile("path/to/data.shp")
```

## API Test: `tileIDs`

### Signature
```scala
def tileIDs: Iterator[Int]
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:91_

_Source doc:_ An iterators that goes over all tile IDs @return an iterator that iterates over all tile IDs in this raster

### Goal
Retrieves an iterator over all unique tile identifiers contained within a raster dataset to facilitate sequential access or metadata inspection.

### Parameters
_None._

### Input
The caller must have an initialized raster object (e.g., an `ITile` or raster-backed structure) that contains defined tile metadata. No specific file format is required as the operation acts on the in-memory raster metadata representation.

### Output
Returns `Iterator[Int]` — An iterator providing access to the integer IDs of all tiles present in the raster.

### Valid Call Patterns
```scala
// Inferred from signature:
val ids: Iterator[Int] = raster.tileIDs
```

### LLM Instruction Prompt
- Use `tileIDs` only when working with raster objects that support metadata iteration.
- Ensure the raster object is fully initialized before calling `tileIDs`.
- Do not assume the order of the returned `Iterator[Int]` is sorted unless specified by the underlying raster implementation.

### Prompt Snippet
```text
val raster = ... // ITile or Raster object
val allTileIds = raster.tileIDs
```

### Common Failure Modes
- **Null Pointer Exception**: Calling `tileIDs` on an uninitialized or null raster object.
- **Empty Iterator**: If the raster has not been populated or contains no tiles, the returned iterator will be empty; ensure logic handles empty results to avoid `NoSuchElementException` when calling `.next()`.

### Fix Code Hint
```scala
val raster = ...
if (raster != null) {
  val ids = raster.tileIDs
  while (ids.hasNext) {
    val id = ids.next()
    // Process tile ID
  }
}
```

## API Test: `toInternalRow`

### Signature
```scala
def toInternalRow: InternalRow
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:134_

_Source doc:_ Convert it to an [[InternalRow]] to work with the DataSource API. Notice that [[Row]] and [[InternalRow]] are not compatible with each other due to the conflicting return data type of the function copy(). So, there is no way to create one class that implements both. @return an [[InternalRow]] representation of this feature, i.e., values without schema

### Goal
Converts a Beast feature into a Spark `InternalRow` to facilitate integration with the Spark DataSource API.

### Parameters
_None._

### Input
The caller must provide an initialized feature object (e.g., `IFeature`). No specific file format is required as the conversion operates on the in-memory feature representation.

### Output
Returns `InternalRow` — An `InternalRow` representation of the feature containing the raw values without an associated schema.

### Valid Call Patterns
```scala
// Inferred from signature:
val internalRow = feature.toInternalRow
```

### LLM Instruction Prompt
- Use `toInternalRow` only when the target workflow requires Spark `InternalRow` compatibility, such as when writing to a Spark DataSource.
- Do not attempt to use the returned `InternalRow` as a standard Spark `Row`, as they are incompatible due to conflicting `copy()` method signatures.

### Prompt Snippet
```text
// Convert feature to InternalRow for Spark DataSource API
val row: InternalRow = feature.toInternalRow
```

### Common Failure Modes
- **Type Mismatch**: Attempting to pass the returned `InternalRow` to functions expecting a standard Spark `Row` will result in compilation or runtime errors due to the incompatibility of the `copy()` method.
- **Schema Loss**: The returned `InternalRow` does not contain schema information; ensure the downstream Spark process has the necessary schema context to interpret the row values.

### Fix Code Hint
```scala
// If you need to work with Spark Row, do not use toInternalRow.
// If you must use InternalRow, ensure the downstream API explicitly accepts InternalRow types.
val internalRow = feature.toInternalRow
```

## API Test: `toString`

### Signature
```scala
def toString: String
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:209_

### Goal
Returns a human-readable string representation of the object, typically used for debugging or logging metadata associated with spatial objects.

### Parameters
_None._

### Input
The caller must provide an initialized Beast object (e.g., a raster metadata instance or feature) that is currently in scope. No specific file formats are required as this operates on in-memory objects.

### Output
Returns `String` — A string representation of the object's internal state or metadata properties.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadataString = myObject.toString
```

### LLM Instruction Prompt
- When calling `toString`, ensure the receiver is a valid Beast object (such as a raster metadata instance).
- Do not pass any arguments to this function, as it takes no parameters.
- Use this method primarily for logging or inspecting object state during debugging of spatial processing pipelines.

### Prompt Snippet
```text
val obj = ... // An instance of a Beast object
val info = obj.toString
```

### Common Failure Modes
- **NullPointerException**: Calling `toString` on a null object reference.
- **Contextual Misuse**: Attempting to call `toString` on a distributed `RDD` object rather than an individual element or metadata object; `toString` on an `RDD` will return the Spark RDD string representation rather than the spatial metadata.

### Fix Code Hint
```scala
// Ensure the object is initialized before calling
if (obj != null) {
  println(obj.toString)
}
```

## API Test: `write`

### Signature
```scala
def write(kryo: Kryo, out: Output): Unit
def write(schema:StructType,kryo: Kryo, out: Output): Unit
def write(kryo: Kryo, output: Output): Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:84  (+2 more definition site/overload)_

### Goal
Serializes spatial data structures or schema information into a binary format using the Kryo serialization framework for efficient storage or network transmission within the Beast ecosystem.

### Parameters
- `schema` (`StructType`): The Spark SQL schema definition describing the structure of the spatial data being serialized.
- `kryo` (`Kryo`): The Kryo instance used to manage the serialization process and object graph.
- `out` (`Output`): The Kryo `Output` stream where the serialized bytes will be written.

### Input
The function requires a valid `Kryo` instance and an initialized `Output` stream. When using the overload including `StructType`, the schema must accurately reflect the structure of the spatial features or raster metadata being processed.

### Output
Returns `Unit` — The function performs a side-effecting operation, writing the serialized representation of the provided schema or object state directly to the `Output` stream.

### Valid Call Patterns
```scala
// Inferred usage pattern for serializing schema information
write(schema, kryo, out)

// Inferred usage pattern for serializing objects via Kryo
write(kryo, out)
```

### LLM Instruction Prompt
- Use `write` when you need to manually serialize Beast spatial types or schemas for custom I/O operations.
- Ensure the `Kryo` instance is properly configured for the Beast environment before calling.
- Always provide the `StructType` if the serialization target requires explicit schema metadata to be reconstructed upon reading.

### Prompt Snippet
```text
// Example of serializing a schema to a Kryo output stream
val out = new Output(new FileOutputStream("metadata.bin"))
write(mySchema, myKryo, out)
out.close()
```

### Common Failure Modes
- **Kryo Exception**: Occurs if the `Kryo` instance is not registered with the necessary Beast spatial classes.
- **Stream Closed**: Attempting to write to an `Output` stream that has already been closed or is null.
- **Schema Mismatch**: Providing a `StructType` that does not match the actual data structure being serialized, leading to deserialization errors later.

### Fix Code Hint
```scala
// Ensure the output stream is flushed and closed after writing
val out = new Output(buffer)
try {
  write(schema, kryo, out)
} finally {
  out.flush()
  out.close()
}
```

## API Test: `writeExternal`

### Signature
```scala
def writeExternal(out: ObjectOutput): Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:70  (+1 more definition site/overload)_

_Source doc:_ Efficient Java serialization/deserialization. A feature is serialized as follows. - Total number of attributes including the geometry, i.e., [[length]] - The names of the attributes in order. If the name does not exist, an empty string is written. - The types of the attributes, each written as a single byte. - A compact bit mask of which attribute values are null. - The values of the attributes in Java serialization form. - The geometry is written using GeometryWriter with the SRID in its position in the list of values - null values are skipped @param out the output to write to

### Goal
Serializes a feature object into a compact binary format using Java serialization for efficient storage or network transmission within the Beast framework.

### Parameters
- `out` (`ObjectOutput`): The output stream or object where the serialized feature data will be written.

### Input
- **Data**: An `IFeature` instance containing attributes and geometry.
- **Preconditions**: The feature must be fully initialized. The `ObjectOutput` stream must be open and writable.
- **Format**: The serialization process writes the total attribute count, attribute names, attribute types (as bytes), a null-value bit mask, attribute values, and the geometry (via `GeometryWriter`).

### Output
Returns `Unit` — The method performs a side-effect by writing the serialized representation of the feature to the provided `ObjectOutput`.

### Valid Call Patterns
```scala
// Inferred from signature:
feature.writeExternal(out)
```

### LLM Instruction Prompt
- Use `writeExternal` when you need to manually serialize a Beast `IFeature` for custom I/O operations.
- Ensure the `ObjectOutput` provided is valid and initialized before calling this method.
- Note that this method handles the geometry serialization internally using `GeometryWriter`, so no manual geometry conversion is required.

### Prompt Snippet
```text
// Example of serializing a feature to an ObjectOutput
val feature: IFeature = ...
val out: ObjectOutput = ...
feature.writeExternal(out)
```

### Common Failure Modes
- **IOException**: Occurs if the underlying `ObjectOutput` stream is closed, unreachable, or encounters a write error.
- **NullPointerException**: Occurs if the `out` parameter is null.
- **Serialization Errors**: May occur if the feature contains non-serializable attribute types that do not conform to the expected Java serialization form.

### Fix Code Hint
```scala
try {
  feature.writeExternal(out)
} catch {
  case e: java.io.IOException => println(s"Failed to serialize feature: ${e.getMessage}")
}
```

## API Test: `writeExternalObj`

### Signature
```scala
def writeExternalObj(schema:StructType, out: ObjectOutput): Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:34_

### Goal
Serializes a Spark `StructType` schema to an `ObjectOutput` stream for persistent storage or network transmission within the Beast framework.

### Parameters
- `schema` (`StructType`): The Spark SQL schema definition to be serialized.
- `out` (`ObjectOutput`): The output stream where the serialized schema bytes will be written.

### Input
The caller must provide a valid `StructType` instance representing the data structure and an initialized `ObjectOutput` stream. This function is typically used during custom serialization processes for Beast-specific data types.

### Output
Returns `Unit` — The function performs a side-effect by writing the serialized schema to the provided `ObjectOutput` stream.

### Valid Call Patterns
```scala
// Inferred from signature:
writeExternalObj(schema, out)
```

### LLM Instruction Prompt
- Use `writeExternalObj` when implementing custom serialization logic for Beast components that require Spark `StructType` persistence.
- Ensure the `ObjectOutput` stream is open and writable before invocation.
- This function does not return a value; ensure the `out` stream is flushed or closed by the caller after the operation completes if necessary.

### Prompt Snippet
```text
// Example usage of writeExternalObj
val schema: StructType = ...
val out: ObjectOutput = ...
writeExternalObj(schema, out)
```

### Common Failure Modes
- `IOException`: Occurs if the `ObjectOutput` stream is closed, unreachable, or lacks sufficient permissions.
- `NullPointerException`: Occurs if either the `schema` or `out` arguments are null.
- Serialization errors: Occurs if the `StructType` contains complex types that are not supported by the underlying serialization mechanism.

### Fix Code Hint
```scala
try {
  writeExternalObj(schema, out)
} catch {
  case e: IOException => println(s"Failed to write schema: ${e.getMessage}")
}
```

## API Test: `writeType`

### Signature
```scala
def writeType(t: DataType, out: ObjectOutput): Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:175_

_Source doc:_ Writes the given data type to the given output so that it can be read back using the function [[readType()]] @param t the SparkSQL data type of the value to write @param out the output to write the type to.

### Goal
Serializes a SparkSQL `DataType` to an `ObjectOutput` stream to enable consistent schema persistence and subsequent reconstruction via `readType()`.

### Parameters
- `t` (`DataType`): The SparkSQL data type definition (e.g., `IntegerType`, `StringType`, `StructType`) to be serialized.
- `out` (`ObjectOutput`): The output stream or object output destination where the type metadata will be written.

### Input
The caller must provide a valid SparkSQL `DataType` instance and an initialized `ObjectOutput` stream. This function is typically used during custom serialization of spatial features or metadata where schema preservation is required for later deserialization.

### Output
Returns `Unit` — The function performs a side-effect by writing the serialized type information to the provided `ObjectOutput`.

### Valid Call Patterns
```scala
// Inferred from signature:
writeType(dataType, objectOutput)
```

### LLM Instruction Prompt
- Use `writeType` when implementing custom serialization logic for Beast spatial features that require SparkSQL schema persistence.
- Always pair `writeType` with the corresponding `readType()` function to ensure the schema is correctly reconstructed.
- Ensure the `ObjectOutput` is open and writable before invoking this function.

### Prompt Snippet
```text
// Example of writing a SparkSQL type to an output stream
writeType(myDataType, myObjectOutput)
```

### Common Failure Modes
- **Stream Closed**: Attempting to write to an `ObjectOutput` that has already been closed or is null.
- **Incompatible Read**: Using a different deserialization method instead of `readType()`, which will result in corrupted schema reconstruction.
- **Type Mismatch**: Passing a null `DataType` if the implementation does not explicitly handle nullability for the schema.

### Fix Code Hint
```scala
import java.io.ObjectOutput
import org.apache.spark.sql.types.DataType

// Ensure the stream is valid before writing
if (out != null) {
  writeType(t, out)
}
```

## API Test: `writeValue`

### Signature
```scala
def writeValue(out: ObjectOutput, value: Any, t: DataType): Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/Feature.scala:212_

_Source doc:_ Writes the given value with the corresponding data type to the output stream @param out the output stream to write to @param t the data type of the given value as specified in the Row schema

### Goal
Serializes a specific feature attribute value to an output stream using a defined Spark `DataType` for consistent binary encoding.

### Parameters
- `out` (`ObjectOutput`): The target output stream where the serialized value will be written.
- `value` (`Any`): The actual data value to be serialized.
- `t` (`DataType`): The Spark `DataType` corresponding to the value, as defined in the associated `Row` schema.

### Input
The caller must provide a valid `ObjectOutput` stream and a `value` that is compatible with the provided `DataType` `t`. The `DataType` must match the schema definition used in the Beast `IFeature` or `Row` context to ensure correct binary representation.

### Output
Returns `Unit` — The method performs a side-effect by writing the serialized bytes of the value to the provided `ObjectOutput`.

### Valid Call Patterns
```scala
// Inferred from signature:
writeValue(out, value, t)
```

### LLM Instruction Prompt
- Use `writeValue` when manually serializing feature attributes for custom I/O operations.
- Always ensure the `t` parameter matches the `DataType` defined in the `Row` schema to prevent serialization mismatches.
- The `value` must be an object type compatible with the provided `DataType`.

### Prompt Snippet
```text
// Example of using writeValue to serialize a feature attribute
val out: ObjectOutput = ...
val value: Any = feature.getAs(index)
val t: DataType = schema.fields(index).dataType
writeValue(out, value, t)
```

### Common Failure Modes
- **Type Mismatch**: Providing a `value` that does not conform to the provided `DataType` `t` will result in serialization errors or corrupted output.
- **Null Stream**: Passing a null `ObjectOutput` will throw a `NullPointerException`.
- **Schema Inconsistency**: Using a `DataType` that does not match the original schema definition can lead to incorrect data interpretation during deserialization.

### Fix Code Hint
```scala
// Ensure the DataType is retrieved directly from the schema
val schema = feature.schema
val dataType = schema.fields(i).dataType
writeValue(out, feature.get(i), dataType)
```

