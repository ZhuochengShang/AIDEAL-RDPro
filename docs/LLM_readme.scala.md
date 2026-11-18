# RDPro Raptor raster scope — Beast 0.10.1 — LLM_readme

LLM-facing API documentation. Generated from the API surface, project profile, and distilled docs.

## API Test: `scala:class:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.reproject`

### Signature
```scala
[](targetCRS:CoordinateReferenceSystem):RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:237_

### Goal
Transforms the spatial reference system of a `RasterMetadata` object to a specified target coordinate reference system.

### Parameters
- `targetCRS` (`CoordinateReferenceSystem`): The destination coordinate reference system to which the raster metadata should be reprojected.

### Input
- **Data:** An existing `RasterMetadata` instance associated with a raster dataset (e.g., GeoTIFF or HDF4).
- **Preconditions:** The input `RasterMetadata` must be valid and initialized. Ensure the `targetCRS` is correctly defined using standard geospatial libraries compatible with the Beast framework.

### Output
Returns `RasterMetadata` — A new metadata object reflecting the updated spatial reference system, which can be used to align rasters for subsequent operations like `overlay` or `raptorJoin`.

### Valid Call Patterns
```scala
// Inferred from signature:
val newMetadata = originalMetadata.reproject(targetCRS)
```

### LLM Instruction Prompt
- When calling `reproject`, ensure the `targetCRS` is a valid `org.opengis.referencing.crs.CoordinateReferenceSystem` object.
- Use this method to prepare `RasterMetadata` before performing operations that require spatial alignment, such as `overlay` or `raptorJoin`.

### Prompt Snippet
```text
// Example of reprojecting metadata
val targetCRS = CRS.decode("EPSG:3857")
val reprojectedMeta = rasterMetadata.reproject(targetCRS)
```

### Common Failure Modes
- **CRS Mismatch:** Attempting to perform `overlay` or `raptorJoin` operations without first ensuring all involved rasters share the same `RasterMetadata` (including CRS and resolution) will cause runtime errors.
- **Invalid CRS:** Providing a null or unsupported `CoordinateReferenceSystem` will result in an exception.
- **Metadata Inconsistency:** If the `RasterMetadata` is not updated via `reproject` before processing, spatial joins may produce incorrect results due to coordinate system misalignment.

### Fix Code Hint
```scala
// Ensure metadata alignment before joining
val alignedMetadata = sourceRaster.metadata.reproject(targetVector.metadata.crs)
// Proceed with raster-vector operations using alignedMetadata
```

## API Test: `scala:class:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.rescale`

### Signature
```scala
[](newRasterWidth:Int,newRasterHeight:Int):RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:219_

### Goal
Creates a new `RasterMetadata` instance with updated dimensions, typically used to prepare raster metadata for alignment or reshaping operations.

### Parameters
- `newRasterWidth` (`Int`): The desired number of pixels along the horizontal axis (width) for the resulting raster metadata.
- `newRasterHeight` (`Int`): The desired number of pixels along the vertical axis (height) for the resulting raster metadata.

### Input
The caller must provide an existing `RasterMetadata` object. This operation is typically performed as a prerequisite for raster alignment, such as when preparing multiple rasters for `overlay` operations, which require identical metadata (resolution, CRS, and tile size).

### Output
Returns `RasterMetadata` — A new metadata object reflecting the updated width and height dimensions while maintaining the original spatial reference system and coordinate bounds.

### Valid Call Patterns
```scala
// Inferred from signature:
val newMetadata = originalMetadata.rescale(newWidth, newHeight)
```

### LLM Instruction Prompt
- When generating code that involves raster alignment or `overlay` operations, use `rescale` to ensure the `RasterMetadata` of the input rasters matches the target dimensions.
- Always ensure the `newRasterWidth` and `newRasterHeight` are positive integers.
- Note that this method only updates the metadata; it does not perform the actual pixel-level resampling of the underlying `RasterRDD`.

### Prompt Snippet
```text
// Ensure metadata alignment before raster operations
val alignedMetadata = rasterMetadata.rescale(targetWidth, targetHeight)
```

### Common Failure Modes
- **Dimension Mismatch:** Attempting to perform `overlay` operations on rasters with different metadata dimensions without first using `rescale` or similar reshaping methods.
- **Invalid Dimensions:** Providing non-positive integers for width or height, which may lead to runtime exceptions during subsequent raster processing.
- **Metadata Inconsistency:** Assuming that calling `rescale` on metadata automatically updates the associated `RasterRDD` pixel data; the pixel data must be transformed separately to match the new metadata.

### Fix Code Hint
```scala
// Example of preparing metadata for alignment
val metadata = raster.metadata
val rescaledMetadata = metadata.rescale(1024, 1024)
// Follow with a transformation that aligns the actual pixel data to rescaledMetadata
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.DefaultReadOnlyTile`

### Signature
```scala
[](tileID:Int,metadata:RasterMetadata,numComponents:Int,componentType:DataType,pixelExists:Array[Byte],pixelData:Array[Byte],compression:Int,rasterFeature:RasterFeature):<type ?>
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/DefaultReadOnlyTile.scala:38_

### Goal
Constructs an immutable, read-only representation of a raster tile within the Beast framework, enabling efficient pixel-level access for Raptor raster-vector processing.

### Parameters
- `tileID` (`Int`): A unique identifier for the tile within the raster dataset.
- `metadata` (`RasterMetadata`): The spatial and structural metadata (CRS, resolution, dimensions) associated with the tile.
- `numComponents` (`Int`): The number of data bands or components per pixel.
- `componentType` (`DataType`): The data type of each pixel component (e.g., IntegerType, FloatType).
- `pixelExists` (`Array[Byte]`): A bitmask or byte array indicating which pixels are valid or present.
- `pixelData` (`Array[Byte]`): The raw byte buffer containing the actual pixel values.
- `compression` (`Int`): An integer code representing the compression algorithm applied to the pixel data.
- `rasterFeature` (`RasterFeature`): The associated raster feature metadata defining the tile's spatial extent.

### Input
The caller must provide valid `RasterMetadata` that matches the global raster configuration. Pixel data must be provided as raw byte arrays, and the `componentType` must be consistent with the data stored in `pixelData`. Precondition: The raster must be properly aligned; if performing overlay operations, ensure metadata matches other tiles in the `RasterRDD`.

### Output
Returns an instance of `DefaultReadOnlyTile` (type inferred as the class itself), which provides a read-only interface for accessing pixel values and spatial properties of the tile.

### Valid Call Patterns
```scala
// Example inferred from signature
val tile = new edu.ucr.cs.bdlab.beast.geolite.DefaultReadOnlyTile(
  tileID, metadata, numComponents, componentType, pixelExists, pixelData, compression, rasterFeature
)
```

### LLM Instruction Prompt
- When calling `DefaultReadOnlyTile`, ensure that `metadata` is pre-validated against the target `RasterRDD` structure.
- Always verify that `pixelData` length corresponds to the dimensions defined in `metadata` and the `componentType` size.
- Use this constructor only when manually instantiating tiles for custom raster data ingestion or testing.

### Prompt Snippet
```text
val tile = new edu.ucr.cs.bdlab.beast.geolite.DefaultReadOnlyTile(tileID, metadata, numComponents, componentType, pixelExists, pixelData, compression, rasterFeature)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not match the global raster resolution or CRS, leading to errors in subsequent `raptorJoin` or `mapPixels` operations.
- **Buffer Overflow/Underflow:** Providing `pixelData` arrays that do not match the expected size calculated from tile dimensions and `componentType`.
- **Type Incompatibility:** Using a `DataType` that does not match the byte-width of the provided `pixelData` array.

### Fix Code Hint
```scala
// Ensure metadata alignment before tile creation
val alignedMetadata = originalMetadata.reshape(targetResolution)
val tile = new edu.ucr.cs.bdlab.beast.geolite.DefaultReadOnlyTile(id, alignedMetadata, ...)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.DefaultReadOnlyTile.copy`

### Signature
```scala
[]():Row
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/DefaultReadOnlyTile.scala:108_

### Goal
Creates a Spark SQL `Row` representation of the current `DefaultReadOnlyTile` instance to facilitate integration with Spark's row-based processing pipelines.

### Parameters
_None._

### Input
The caller must have an initialized `DefaultReadOnlyTile` object. As this is a read-only tile implementation, the tile must already be populated with raster data (e.g., from a GeoTIFF or HDF4 source) before calling this method.

### Output
Returns `Row` — A Spark SQL `Row` object containing the tile's data, which can be used for further transformation or storage within Spark DataFrames or RDDs.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: DefaultReadOnlyTile = ...
val row: Row = tile.copy()
```

### LLM Instruction Prompt
- When using `DefaultReadOnlyTile.copy`, ensure the tile instance is fully initialized.
- Use this method when you need to convert raster tile data into a Spark `Row` format for interoperability with non-raster-specific Spark transformations.
- Note that this method does not accept arguments; it operates strictly on the state of the existing `DefaultReadOnlyTile` instance.

### Prompt Snippet
```text
val tile: DefaultReadOnlyTile = ...
val row = tile.copy()
```

### Common Failure Modes
- **Null Pointer Exception:** Occurs if the `DefaultReadOnlyTile` instance is not properly initialized or if the underlying raster data buffer is null.
- **Type Mismatch:** Attempting to use the returned `Row` in contexts expecting specific schema structures without verifying the `Row` contents, as the internal structure of the `Row` is determined by the tile's metadata.
- **Contextual Misuse:** Calling this on a tile that has been closed or cleared, leading to empty or invalid `Row` data.

### Fix Code Hint
```scala
// Ensure the tile is valid before copying
if (tile != null) {
  val row = tile.copy()
  // Process the row as needed
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.DefaultReadOnlyTile.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):T
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/DefaultReadOnlyTile.scala:54_

### Goal
Retrieves the raw pixel value at a specific coordinate within a read-only raster tile in the Beast framework.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide a `DefaultReadOnlyTile` instance. The indices `i` and `j` must be within the bounds of the tile dimensions (0 to width-1 and 0 to height-1, respectively). The tile must be initialized and loaded into memory as part of a `RasterRDD` or similar Beast raster structure.

### Output
Returns `T` — The value of the pixel at the specified location, where `T` corresponds to the underlying data type of the raster (e.g., `Float`, `Int`, `Double`).

### Valid Call Patterns
```scala
// Inferred from signature: accessing a pixel at (10, 20)
val pixelValue = tile.getPixelValue(10, 20)
```

### LLM Instruction Prompt
- When generating code that uses `getPixelValue`, ensure the `DefaultReadOnlyTile` instance is already available in the scope.
- Always verify that the indices `i` and `j` are within the tile's valid range to avoid `ArrayIndexOutOfBoundsException`.
- Ensure the type `T` matches the expected pixel type of the raster (e.g., `Float` for temperature data, `Int` for categorical data).

### Prompt Snippet
```text
// Accessing pixel value from a DefaultReadOnlyTile instance
val value = tile.getPixelValue(col, row)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if `i` or `j` are negative or exceed the tile's width/height dimensions.
- **Type Mismatch**: Occurs if the expected return type `T` does not match the actual pixel data type stored in the tile (e.g., expecting `Float` when the tile contains `Int`).
- **Null Pointer**: Occurs if the `DefaultReadOnlyTile` instance has not been properly initialized or is null.

### Fix Code Hint
```scala
// Ensure bounds checking before access
if (i >= 0 && i < tile.getWidth && j >= 0 && j < tile.getHeight) {
  val value = tile.getPixelValue(i, j)
} else {
  // Handle out-of-bounds case
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.DefaultReadOnlyTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/DefaultReadOnlyTile.scala:47_

### Goal
Checks if a specific pixel at the given column and row coordinates within a `DefaultReadOnlyTile` is considered empty or null.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide a valid `DefaultReadOnlyTile` instance. The indices `i` and `j` must be within the bounds of the tile dimensions. This method is typically used during raster processing workflows where pixel-level filtering or validation is required.

### Output
Returns `Boolean` — `true` if the pixel at the specified `(i, j)` coordinate is empty (e.g., represents a NoData value or is uninitialized), and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: DefaultReadOnlyTile = ...
val isPixelEmpty: Boolean = tile.isEmpty(i, j)
```

### LLM Instruction Prompt
- When generating code that uses `isEmpty`, ensure the `i` and `j` indices are validated against the tile's width and height to prevent `ArrayIndexOutOfBoundsException`.
- Use this method to filter out NoData pixels before performing arithmetic operations on raster data.

### Prompt Snippet
```text
val tile: DefaultReadOnlyTile = ...
if (!tile.isEmpty(i, j)) {
  // Perform pixel-level computation
}
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if `i` or `j` are negative or exceed the tile's dimensions.
- **NullPointerException**: Occurs if the `DefaultReadOnlyTile` instance itself is null.
- **Logic Errors**: Assuming `isEmpty` checks for a specific value (like 0) rather than the tile's internal definition of "empty" or "NoData".

### Fix Code Hint
```scala
// Ensure indices are within bounds before calling
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  val empty = tile.isEmpty(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.extents`

### Signature
```scala
[]:Geometry
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:152_

### Goal
Retrieves the spatial extent of an `ITile` as a `Geometry` object, defining the bounding box or footprint of the raster tile within the coordinate reference system.

### Parameters
_None._

### Input
The caller must have an initialized `ITile` instance (e.g., from a `RasterRDD` or loaded via `sc.geoTiff`). No specific file format is required at the time of calling, as the operation acts on the in-memory tile representation.

### Output
Returns `Geometry` — A geometry object representing the spatial boundary (envelope) of the tile.

### Valid Call Patterns
```scala
// Inferred from signature:
val tileExtents: Geometry = myTile.extents()
```

### LLM Instruction Prompt
- When working with `ITile` objects in Beast, use the `.extents()` method to retrieve the spatial boundary of the tile.
- Ensure the `ITile` is fully initialized before calling this method.
- The returned `Geometry` can be used for spatial intersection checks or metadata reporting.

### Prompt Snippet
```text
val tile: ITile[T] = ...
val bounds: Geometry = tile.extents()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `.extents()` on an uninitialized or null `ITile` reference.
- **Coordinate Mismatch:** Assuming the returned `Geometry` is in a specific CRS without verifying the `ITile` metadata, which may lead to errors during subsequent spatial joins or projections.
- **Empty Tile:** If the tile contains no valid data, the returned geometry may represent an empty or null extent depending on the specific implementation of the tile.

### Fix Code Hint
```scala
// Ensure the tile is valid before accessing extents
if (tile != null) {
  val bounds = tile.extents()
  // Proceed with spatial operations
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.get`

### Signature
```scala
[](i:Int):Any
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:181_

### Goal
Retrieves the pixel value or data element at the specified linear index `i` from an `ITile` raster data structure.

### Parameters
- `i` (`Int`): The zero-based linear index of the pixel within the tile's internal data array.

### Input
The caller must provide an instance of `ITile` (typically part of a `RasterRDD[T]` or `RDD[ITile[T]]`). The index `i` must be within the bounds of the tile's data array (0 to `tile.size - 1`).

### Output
Returns `Any` — The value stored at the specified index. The actual type (e.g., `Float`, `Int`) depends on the underlying raster data type defined when the tile was initialized.

### Valid Call Patterns
```scala
// Inferred from signature: accessing a pixel at index 0
val tile: ITile = ...
val pixelValue = tile.get(0)
```

### LLM Instruction Prompt
- When using `ITile.get`, ensure the index `i` is validated against the tile's dimensions to prevent `ArrayIndexOutOfBoundsException`.
- Since the return type is `Any`, the LLM should suggest casting the result to the expected pixel type (e.g., `Float` or `Int`) based on the `RasterRDD` type parameter.

### Prompt Snippet
```text
val tile: ITile = ...
val index: Int = ...
val value = tile.get(index)
```

### Common Failure Modes
- **IndexOutOfBounds:** Providing an index `i` that is negative or greater than or equal to the total number of pixels in the tile.
- **Type Mismatch:** Attempting to perform arithmetic on the returned `Any` value without casting it to the appropriate numeric type (e.g., `Float`, `Double`, `Int`).
- **Null/Empty Tiles:** Calling `get` on an uninitialized or empty tile object.

### Fix Code Hint
```scala
// Ensure index is within bounds and cast to expected type
if (i >= 0 && i < tile.size) {
  val value = tile.get(i).asInstanceOf[Float]
  // Process value...
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.getPointValue`

### Signature
```scala
[](x:Double,y:Double):T
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:93_

### Goal
Retrieves the pixel value at a specific geographic coordinate from an `ITile` instance within the Beast raster processing framework.

### Parameters
- `x` (`Double`): The x-coordinate (longitude or column-based spatial coordinate) in the tile's coordinate reference system.
- `y` (`Double`): The y-coordinate (latitude or row-based spatial coordinate) in the tile's coordinate reference system.

### Input
The caller must provide an initialized `ITile[T]` object. The coordinates `(x, y)` must fall within the spatial extent (MBR) of the tile. If the tile is part of a larger raster dataset, ensure the tile has been correctly loaded or partitioned via Beast's raster ingestion methods (e.g., `sc.geoTiff`).

### Output
Returns `T` — The value stored at the specified spatial location, where `T` corresponds to the pixel data type (e.g., `Int`, `Float`, `Double`) defined during the raster ingestion.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: ITile[Float] = ...
val value: Float = tile.getPointValue(x, y)
```

### LLM Instruction Prompt
- When using `getPointValue`, ensure the `ITile` instance is of the correct generic type `T` matching the underlying raster data (e.g., `Float` for temperature, `Int` for land cover).
- Always verify that the `(x, y)` coordinates are within the tile's spatial bounds before calling to avoid potential out-of-bounds errors.

### Prompt Snippet
```text
val tile: ITile[T] = ...
val result = tile.getPointValue(x, y)
```

### Common Failure Modes
- **Out of Bounds:** Providing `x` or `y` coordinates outside the tile's spatial extent may result in undefined behavior or exceptions depending on the specific `ITile` implementation.
- **Type Mismatch:** Attempting to assign the result to a variable of the wrong type (e.g., expecting `Double` when the tile contains `Int`) will cause a Scala compilation error.
- **Uninitialized Tile:** Calling this method on a null or uninitialized `ITile` reference will throw a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure coordinates are within bounds
if (tile.getMBR.contains(x, y)) {
  val value = tile.getPointValue(x, y)
} else {
  // Handle coordinate outside of tile
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.isDefined`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:126_

### Goal
Checks whether a specific pixel at the given column and row coordinates within an `ITile` contains valid data or is defined.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide an instance of `ITile`. The indices `i` and `j` must be within the bounds of the tile's dimensions. This method is typically used when iterating over pixels in a `RasterRDD` or during custom pixel-level transformations.

### Output
Returns `Boolean` — `true` if the pixel at the specified coordinates is defined (contains a valid value), and `false` otherwise (e.g., if the pixel represents NoData).

### Valid Call Patterns
```scala
// Inferred from signature: checking if a pixel is defined within a tile
val tile: ITile[Float] = ...
val isPixelValid = tile.isDefined(col, row)
```

### LLM Instruction Prompt
- When generating code that processes raster data using `ITile`, use `isDefined(i, j)` to verify pixel validity before accessing the value to avoid processing NoData values.
- Ensure `i` and `j` are within the tile's bounds to prevent `ArrayIndexOutOfBoundsException`.

### Prompt Snippet
```text
// Check if pixel is defined before processing
if (tile.isDefined(i, j)) {
  val value = tile.get(i, j)
  // ... process value
}
```

### Common Failure Modes
- **Out of Bounds:** Passing `i` or `j` values that exceed the tile's width or height will result in an `ArrayIndexOutOfBoundsException`.
- **NoData Handling:** Failing to check `isDefined` before performing arithmetic on raster pixels may lead to incorrect results if the tile contains NoData values that are not explicitly handled.
- **Type Mismatch:** Ensure the `ITile` instance is correctly typed (e.g., `ITile[Float]` or `ITile[Int]`) to match the underlying raster data format.

### Fix Code Hint
```scala
// Ensure indices are within bounds before calling isDefined
if (i >= 0 && i < tile.getWidth && j >= 0 && j < tile.getHeight) {
  if (tile.isDefined(i, j)) {
    // Safe to process
  }
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.isEmptyAt`

### Signature
```scala
[](x:Double,y:Double):Boolean
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:114_

### Goal
Determines whether a specific spatial coordinate within an `ITile` contains no data (is empty).

### Parameters
- `x` (`Double`): The x-coordinate (e.g., longitude or column index) in the spatial reference system of the tile.
- `y` (`Double`): The y-coordinate (e.g., latitude or row index) in the spatial reference system of the tile.

### Input
- **Data:** An instance of `ITile` (typically part of a `RasterRDD[T]`).
- **Preconditions:** The coordinates `(x, y)` should fall within the spatial extent of the tile. The tile must be initialized and loaded into memory.

### Output
Returns `Boolean` — `true` if the pixel at the specified coordinate is considered empty (e.g., NoData value), and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: ITile[Float] = ...
val isEmpty: Boolean = tile.isEmptyAt(x, y)
```

### LLM Instruction Prompt
- When using `isEmptyAt`, ensure the `x` and `y` coordinates are provided as `Double` types.
- Always verify that the `ITile` instance is not null before invoking this method.
- Use this method to filter out NoData pixels during custom raster transformations or when performing manual pixel-level analysis.

### Prompt Snippet
```text
// Check if the current coordinate in the raster tile is empty
if (tile.isEmptyAt(coordX, coordY)) {
  // Handle empty pixel case
}
```

### Common Failure Modes
- **Coordinate Out of Bounds:** Providing `x` or `y` values outside the tile's spatial extent may lead to unexpected results or exceptions depending on the specific `ITile` implementation.
- **Uninitialized Tiles:** Calling this method on a tile that has not been properly loaded or is null will result in a `NullPointerException`.
- **Coordinate System Mismatch:** Passing coordinates in a different CRS than the one defined for the `ITile` will result in incorrect spatial lookups. Ensure coordinates are transformed to the tile's native CRS first.

### Fix Code Hint
```scala
// Ensure coordinates are within bounds before checking
if (tile.getEnvelope.contains(x, y)) {
  val isEmpty = tile.isEmptyAt(x, y)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.isPixelInRange`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:188_

### Goal
Determines whether a specific pixel coordinate (i, j) falls within the valid spatial bounds of the current `ITile` instance.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must have an initialized `ITile` object. This method is typically used during custom pixel-level transformations or when iterating over raster data to ensure coordinate safety before accessing pixel values.

### Output
Returns `Boolean` — `true` if the provided (i, j) coordinates are within the tile's dimensions, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature: checking bounds before pixel access
if (tile.isPixelInRange(i, j)) {
  val pixelValue = tile.getPixel(i, j)
}
```

### LLM Instruction Prompt
- When generating code that iterates over `ITile` pixels, always wrap coordinate-based access in a conditional check using `isPixelInRange` to prevent `ArrayIndexOutOfBoundsException` or similar runtime errors.
- Ensure `i` and `j` are non-negative integers.

### Prompt Snippet
```text
// Use isPixelInRange to validate coordinates before accessing ITile data
if (tile.isPixelInRange(col, row)) {
  // process pixel
}
```

### Common Failure Modes
- **Out-of-bounds access:** Attempting to retrieve pixel data without checking `isPixelInRange` can lead to runtime exceptions if the indices exceed the tile's width or height.
- **Negative indices:** Passing negative values for `i` or `j` will return `false`, but failing to handle this result may lead to logic errors in custom raster processing algorithms.

### Fix Code Hint
```scala
// Safe pixel access pattern
val safeValue = if (tile.isPixelInRange(i, j)) {
  tile.getPixel(i, j)
} else {
  // Handle out-of-bounds, e.g., return a NoData value
  Float.NaN 
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.pixelLocations`

### Signature
```scala
[]:Iterator[scala.Tuple2[Int, Int]]
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:81_

### Goal
Returns an iterator over all coordinate pairs (column, row) representing the pixel locations within the bounds of the `ITile`.

### Parameters
_None._

### Input
The caller must have an initialized `ITile` object (e.g., from a `RasterRDD` or loaded via `sc.geoTiff`). No specific file format is required as this operates on the in-memory `ITile` representation.

### Output
Returns `Iterator[scala.Tuple2[Int, Int]]` — An iterator where each `Tuple2` contains the `(x, y)` or `(column, row)` index of a pixel within the tile.

### Valid Call Patterns
```scala
// Inferred from signature: iterating over pixel coordinates of a tile
val tile: ITile[_] = ...
val locations: Iterator[(Int, Int)] = tile.pixelLocations()
```

### LLM Instruction Prompt
- Use `pixelLocations()` when you need to iterate over the grid indices of a raster tile.
- Do not pass any arguments to this method.
- The returned iterator yields `(Int, Int)` tuples representing the column and row indices respectively.

### Prompt Snippet
```text
val tile: ITile[_] = ...
val coords = tile.pixelLocations()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling this method on an uninitialized or null `ITile` reference.
- **Performance Overhead:** Using this method to iterate over very large tiles in a tight loop; consider if a vectorized operation or `mapPixels` is more appropriate for the transformation.
- **Type Mismatch:** Attempting to use this on non-raster data types; ensure the object is an instance of `ITile`.

### Fix Code Hint
```scala
// Ensure the tile is properly loaded before accessing locations
val tile = myRasterRDD.first() 
val pixelCoords = tile.pixelLocations().toList
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.pixelType`

### Signature
```scala
[]:DataType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:138_

### Goal
Retrieves the underlying data type of the pixels contained within an `ITile` instance to ensure compatibility with raster processing operations.

### Parameters
_None._

### Input
The caller must have an initialized `ITile` object, typically obtained from a `RasterRDD` or by loading a raster file (GeoTIFF or HDF4) via the `SparkContext`. No specific preconditions are required to call this method, as it inspects the metadata of an existing tile.

### Output
Returns `DataType` — An object representing the pixel data type (e.g., `IntegerType`, `FloatType`, or `ArrayType`), which indicates how the raster values are stored and should be interpreted.

### Valid Call Patterns
```scala
val raster = sc.geoTiff("glc2000_v1_1.tif")
val pType = raster.first.pixelType
println(pType)
```

### LLM Instruction Prompt
- When a user is unsure of the pixel format of a raster file, instruct them to load the file into a `RasterRDD` and call `pixelType` on the first tile to determine the correct type parameter for subsequent `sc.geoTiff[T]` or `sc.hdfFile[T]` calls.

### Prompt Snippet
```text
// Determine the pixel type of a raster file
val raster = sc.geoTiff("path/to/raster.tif")
val typeInfo = raster.first.pixelType
```

### Common Failure Modes
- **Type Mismatch:** Attempting to load a raster using a type parameter (e.g., `sc.geoTiff[Float]`) that does not match the actual `pixelType` of the file, leading to runtime errors or corrupted data interpretation.
- **Empty RDD:** Calling `.first` on an empty `RasterRDD` will throw an exception; ensure the RDD contains data before invoking `pixelType`.
- **Metadata Incompatibility:** Assuming all tiles in a `RasterRDD` share the same `pixelType` without verification, though typically consistent for a single file.

### Fix Code Hint
```scala
// If pixelType returns IntegerType, ensure your load call matches:
val raster: RasterRDD[Int] = sc.geoTiff("data.tif")
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.pixels`

### Signature
```scala
[]:Iterator[scala.Tuple3[Int, Int, T]]
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:84_

### Goal
Provides an iterator over all pixels within an `ITile`, returning each pixel's column index, row index, and its corresponding value.

### Parameters
_None._

### Input
The caller must have an instance of `ITile[T]`. The tile must be fully initialized and loaded into memory. No specific file format is required as input, as this method operates on the in-memory `ITile` object representation.

### Output
Returns `Iterator[scala.Tuple3[Int, Int, T]]` — An iterator where each element is a `Tuple3` containing:
*   `_1`: The column index (Int) of the pixel.
*   `_2`: The row index (Int) of the pixel.
*   `_3`: The pixel value (T) of type `T`.

### Valid Call Patterns
```scala
// Inferred from signature: iterating over pixels in a tile
val tile: ITile[Float] = ...
val pixelIterator = tile.pixels()
pixelIterator.foreach { case (col, row, value) =>
  println(s"Pixel at ($col, $row) has value $value")
}
```

### LLM Instruction Prompt
- When using `pixels()`, ensure the `ITile` type parameter `T` matches the expected data type of the raster (e.g., `Float`, `Int`).
- The returned iterator is a standard Scala `Iterator`; consume it carefully as it may represent a large number of pixels depending on the tile dimensions.
- Do not assume the order of iteration unless specified by the underlying `ITile` implementation.

### Prompt Snippet
```text
val pixels = tile.pixels()
```

### Common Failure Modes
- **Type Mismatch:** Attempting to process the iterator with an incorrect type `T` (e.g., expecting `Float` when the tile contains `Int`) will result in runtime class cast exceptions.
- **Memory Exhaustion:** Converting the iterator to a collection (e.g., `.toList`) for very large tiles may cause `OutOfMemoryError` if the tile dimensions are significant.
- **Uninitialized Tiles:** Calling `pixels()` on a null or uninitialized `ITile` reference will throw a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure the tile is properly typed and iterate safely
tile.pixels().foreach { case (c, r, v) => 
  // Process pixel (c, r) with value v
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.rasterMetadataToRow`

### Signature
```scala
[](metadata:RasterMetadata):Row
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:220_

### Goal
Converts a `RasterMetadata` object into a Spark `Row` representation to facilitate the integration of raster metadata into Spark DataFrames or tabular datasets.

### Parameters
- `metadata` (`RasterMetadata`): The raster metadata object containing spatial properties such as coordinate reference system (CRS), resolution, and tile dimensions.

### Input
The caller must provide a valid `RasterMetadata` instance. This object is typically obtained from existing raster datasets (e.g., GeoTIFF or HDF4) loaded via Beast's raster ingestion methods. No specific file format is required at the time of calling, as the input is an in-memory metadata object.

### Output
Returns `Row` — A Spark `Row` object containing the flattened metadata fields, suitable for inclusion in a Spark `DataFrame` or `Dataset`.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ... // obtained from a raster source
val row: Row = edu.ucr.cs.bdlab.beast.geolite.ITile.rasterMetadataToRow(metadata)
```

### LLM Instruction Prompt
- Use `edu.ucr.cs.bdlab.beast.geolite.ITile.rasterMetadataToRow` when you need to convert raster metadata into a Spark-compatible `Row` format for schema-based processing.
- Ensure the `RasterMetadata` object is fully initialized before passing it to this function.

### Prompt Snippet
```text
val metadata = myRasterRDD.metadata
val row = edu.ucr.cs.bdlab.beast.geolite.ITile.rasterMetadataToRow(metadata)
```

### Common Failure Modes
- **Null Metadata:** Passing a null `RasterMetadata` object will result in a `NullPointerException`.
- **Schema Mismatch:** If the resulting `Row` is used to create a `DataFrame`, ensure the schema matches the structure of the fields extracted from the `RasterMetadata`.
- **Context:** This function is intended for metadata serialization; it does not process pixel data. Attempting to use it for pixel-level transformations will fail to achieve the desired result.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from a valid RasterRDD or source
val metadata = rasterRDD.metadata
val row = edu.ucr.cs.bdlab.beast.geolite.ITile.rasterMetadataToRow(metadata)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.schema`

### Signature
```scala
[]:StructType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:40_

### Goal
Retrieves the Spark SQL `StructType` schema definition associated with an `ITile` instance, describing the structure of the raster data and its metadata.

### Parameters
_None._

### Input
The caller must have an initialized `ITile` object. No specific file format is required as input to this method, as it operates on the in-memory representation of the tile metadata.

### Output
Returns `StructType` — A Spark SQL schema object that defines the fields and data types contained within the `ITile`, typically used for integration with Spark DataFrames or when converting raster data to structured tabular formats.

### Valid Call Patterns
```scala
// Inferred from signature:
val tileSchema = myTile.schema()
```

### LLM Instruction Prompt
- When generating code that interacts with `ITile` objects, use the `.schema()` method to retrieve the `StructType` whenever a Spark SQL schema is required for downstream processing or serialization.
- Do not attempt to pass arguments to this method, as it takes no parameters.
- Ensure the `ITile` instance is fully initialized before calling this method.

### Prompt Snippet
```text
// Example of accessing the schema of an ITile instance
val tile: ITile = ... // initialized ITile
val schema: StructType = tile.schema()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `.schema()` on an uninitialized or null `ITile` reference.
- **Type Mismatch:** Attempting to use the returned `StructType` in contexts expecting a different schema format without proper Spark SQL conversion.
- **Contextual Misuse:** Assuming the schema is static across different raster types; always retrieve the schema dynamically from the specific `ITile` instance being processed.

### Fix Code Hint
```scala
import org.apache.spark.sql.types.StructType
import edu.ucr.cs.bdlab.beast.geolite.ITile

// Ensure the tile is valid before accessing the schema
val tileSchema: StructType = Option(myTile) match {
  case Some(t) => t.schema()
  case None => throw new IllegalArgumentException("ITile instance is null")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.setAttribute`

### Signature
```scala
[](name:String,value:Any):ITile[T]
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:196_

### Goal
Associates a custom metadata attribute with an `ITile` instance to store auxiliary information alongside the raster pixel data.

### Parameters
- `name` (`String`): The key identifier for the attribute to be stored.
- `value` (`Any`): The value to associate with the specified key; can be any object type.

### Input
The caller must have an existing `ITile[T]` instance. This method is typically used during raster processing workflows where custom metadata (such as source identifiers, processing timestamps, or sensor parameters) needs to be attached to individual tiles within an `RDD[ITile[T]]` or `RasterRDD[T]`.

### Output
Returns `ITile[T]` — A new or modified `ITile` instance containing the updated attribute mapping.

### Valid Call Patterns
```scala
// Inferred from signature:
val updatedTile = tile.setAttribute("sensor_id", "Landsat-8")
```

### LLM Instruction Prompt
- When generating code that uses `setAttribute`, ensure the receiver is an instance of `ITile[T]`.
- The `name` parameter should be a unique string key.
- Since the return type is `ITile[T]`, ensure the result is captured or chained, as the original `ITile` instance remains immutable in most Spark-based raster workflows.

### Prompt Snippet
```text
// Example of attaching metadata to a raster tile
val tileWithMeta = tile.setAttribute("processing_level", "L1C")
```

### Common Failure Modes
- **Type Mismatch:** Attempting to use the returned `ITile[T]` in an operation that expects a specific subtype if the attribute modification causes a type erasure or mismatch in the generic `T`.
- **Immutability:** Assuming the original `tile` object is modified in-place; always use the returned `ITile[T]` reference.
- **Serialization:** If the `value` provided is not serializable, it may cause runtime failures when the `RDD[ITile[T]]` is distributed across the Spark cluster.

### Fix Code Hint
```scala
// Ensure the result is assigned to a new variable
val myTile: ITile[Float] = ...
val taggedTile = myTile.setAttribute("key", "value")
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITile.toString`

### Signature
```scala
[]:String
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITile.scala:145_

### Goal
Returns a string representation of an `ITile` object, typically used for debugging or logging the metadata and state of a raster tile within the Beast framework.

### Parameters
_None._

### Input
The caller must provide an initialized `ITile` instance. As `ITile` is a core component of Beast's raster processing (Raptor), the instance should be part of a valid `RasterRDD[T]` or `RDD[ITile[T]]` workflow. No specific file format is required as this operates on in-memory objects.

### Output
Returns `String` — A string representation of the tile, which generally includes internal metadata such as the tile's spatial extent, dimensions, and pixel type information.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: ITile[Float] = ...
val tileInfo: String = tile.toString()
```

### LLM Instruction Prompt
- When generating code that uses `ITile.toString`, ensure the `ITile` instance is already initialized within a Spark context or a raster processing pipeline.
- Use this method primarily for logging or diagnostic purposes when inspecting raster data structures.
- Do not rely on the specific format of the returned string for programmatic parsing, as it is intended for human-readable representation.

### Prompt Snippet
```text
// Example of logging an ITile instance
val tile: ITile[Float] = ...
println(s"Processing tile: ${tile.toString()}")
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `toString` on an uninitialized or null `ITile` reference within a distributed Spark task.
- **Contextual Misuse:** Attempting to use the output of `toString` to reconstruct or serialize the tile; use appropriate `saveAsGeoTiff` or similar persistence methods for data storage instead.

### Fix Code Hint
```scala
// Ensure the tile is not null before calling toString
if (tile != null) {
  log.info(s"Tile details: ${tile.toString()}")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITileSerializer`

### Signature
```scala
[]():<type ?>
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITileSerializer.scala:30_

### Goal
Provides a serialization mechanism for `ITile` objects within the Beast framework to facilitate the distributed storage or transmission of raster tile data.

### Parameters
_None._

### Input
The caller must ensure that the `ITile` objects being processed are compatible with the framework's raster data structures. As this is a low-level serialization component, it is typically invoked internally by Beast's raster I/O operations (e.g., `saveAsGeoTiff` or `RasterRDD` persistence).

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it represents the serialized byte representation or the serializer instance used for `ITile` data.

### Valid Call Patterns
```scala
// Inferred usage: instantiation of the serializer for raster tile data
val serializer = new edu.ucr.cs.bdlab.beast.geolite.ITileSerializer()
```

### LLM Instruction Prompt
- When generating code involving raster data persistence, use `ITileSerializer` if manual control over the serialization of `ITile` objects is required.
- Note that this API is a low-level component; prefer high-level `RasterRDD` methods like `saveAsGeoTiff` whenever possible.

### Prompt Snippet
```text
// Example of initializing the ITileSerializer
val tileSerializer = new edu.ucr.cs.bdlab.beast.geolite.ITileSerializer()
```

### Common Failure Modes
- **Type Mismatch:** Attempting to serialize objects that do not conform to the `ITile` interface.
- **Contextual Dependency:** Invoking this outside of a Spark environment where the necessary raster metadata (CRS, resolution) is not properly initialized.
- **Undocumented Return:** Since the return type is not specified in the API facts, avoid relying on specific return values in custom logic without inspecting the object type at runtime.

### Fix Code Hint
```scala
// Ensure the raster data is properly partitioned and typed before serialization
val rasterRDD: RasterRDD[Float] = ... 
val serializer = new edu.ucr.cs.bdlab.beast.geolite.ITileSerializer()
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITileSerializer.read`

### Signature
```scala
[](kryo:Kryo,input:Input,tileClass:Class[ITile[T]]):ITile[T]
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITileSerializer.scala:74_

### Goal
Deserializes a raster tile from a Kryo input stream within the Beast geospatial framework.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for the deserialization process.
- `input` (`Input`): The input stream containing the serialized tile data.
- `tileClass` (`Class[ITile[T]]`): The class type of the `ITile` to be instantiated.

### Input
The caller must provide a valid `Kryo` instance and an `Input` stream that contains a previously serialized `ITile` object. The `tileClass` must match the type of the tile stored in the input stream. This method is typically used within custom Kryo serializers for raster data types in Beast.

### Output
Returns `ITile[T]` — The deserialized raster tile object of type `T`.

### Valid Call Patterns
```scala
// Inferred usage within a custom Serializer implementation
val tile = serializer.read(kryo, input, classOf[ITile[Float]])
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure the `kryo` and `input` objects are properly initialized from the current serialization context.
- Always ensure the `tileClass` parameter matches the expected pixel data type (e.g., `Float`, `Int`) to avoid class cast exceptions during deserialization.

### Prompt Snippet
```text
// Example of using ITileSerializer.read inside a custom Kryo serializer
override def read(kryo: Kryo, input: Input, tileClass: Class[ITile[T]]): ITile[T] = {
  // Implementation logic using the provided API
  val tile = edu.ucr.cs.bdlab.beast.geolite.ITileSerializer.read(kryo, input, tileClass)
  tile
}
```

### Common Failure Modes
- **Type Mismatch:** Providing a `tileClass` that does not match the actual serialized data type will result in a runtime exception.
- **Stream Exhaustion:** If the `Input` stream does not contain a complete serialized tile, the method will throw an exception.
- **Context Errors:** Using a `Kryo` instance that has not been properly configured with the necessary Beast serializers for raster types.

### Fix Code Hint
```scala
// Ensure the input stream is positioned correctly before calling read
if (input.position() < input.limit()) {
  val tile = serializer.read(kryo, input, classOf[ITile[Float]])
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.ITileSerializer.write`

### Signature
```scala
[](kryo:Kryo,output:Output,tile:ITile[T]):Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/ITileSerializer.scala:31_

### Goal
Serializes an `ITile[T]` object into a binary stream using the Kryo serialization framework for efficient storage or network transmission within the Beast raster processing pipeline.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for object graph serialization.
- `output` (`Output`): The Kryo `Output` stream where the serialized tile data will be written.
- `tile` (`ITile[T]`): The raster tile object of type `T` to be serialized.

### Input
- **Data:** An instance of `ITile[T]`, which represents a portion of a raster dataset in the Beast framework.
- **Preconditions:** The `kryo` instance must be properly configured to handle `ITile` types. The `output` stream must be open and writable.
- **Formats:** This method is agnostic to the underlying raster file format (e.g., GeoTIFF, HDF4) as it operates on the in-memory `ITile` representation.

### Output
Returns `Unit` — The method performs a side-effect by writing the binary representation of the tile to the provided `Output` stream.

### Valid Call Patterns
```scala
// Inferred usage pattern for custom Kryo serialization
val serializer = new ITileSerializer[T]()
serializer.write(kryo, output, tile)
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure the `kryo` instance is available in the current scope (typically provided by Spark's serialization context).
- Always ensure the `output` stream is flushed or closed by the caller after the serialization process is complete.
- Ensure the type parameter `T` of the `ITile` matches the expected pixel data type (e.g., `Float`, `Int`) used in the broader `RasterRDD` workflow.

### Prompt Snippet
```text
// Example of using ITileSerializer within a custom serializer
val tileSerializer = new edu.ucr.cs.bdlab.beast.geolite.ITileSerializer[Float]()
tileSerializer.write(kryo, output, myTile)
```

### Common Failure Modes
- **Serialization Errors:** Occur if the `kryo` instance is not registered with the necessary classes for the specific `ITile` implementation.
- **Buffer Overflow:** If the `output` stream buffer is too small for the serialized tile data, an exception may be thrown by the Kryo `Output` object.
- **Type Mismatch:** Passing an `ITile` with a pixel type `T` that is incompatible with the serializer's expected type.

### Fix Code Hint
```scala
// Ensure the output stream is sufficiently sized or auto-flushing
val output = new com.esotericsoftware.kryo.io.Output(4096, -1)
serializer.write(kryo, output, tile)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterFeature`

### Signature
```scala
[](values:Array[Any],schema:StructType):<type ?>
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterFeature.scala:7_

### Goal
Constructs a `RasterFeature` object by mapping an array of values to a defined Spark `StructType` schema for use within Beast's geospatial processing workflows.

### Parameters
- `values` (`Array[Any]`): An array containing the raw data values corresponding to the fields defined in the provided schema.
- `schema` (`StructType`): A Spark `StructType` defining the structure, field names, and data types of the feature.

### Input
The caller must provide a valid Spark `StructType` that matches the length and data types of the `values` array. This is typically used when creating feature representations for raster-vector integration or custom data ingestion pipelines within the Beast framework.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it represents an instance of a `RasterFeature` used to encapsulate spatial data attributes.

### Valid Call Patterns
```scala
// Example inferred from signature (not verified)
val schema = StructType(Array(StructField("id", IntegerType), StructField("value", FloatType)))
val values = Array[Any](1, 25.5f)
val feature = new edu.ucr.cs.bdlab.beast.geolite.RasterFeature(values, schema)
```

### LLM Instruction Prompt
- When using `RasterFeature`, ensure the `values` array length strictly matches the number of fields in the `schema`.
- Ensure that the data types in the `values` array are compatible with the corresponding `DataType` defined in the `StructType` (e.g., `Float` for `FloatType`).
- Use this class when you need to manually instantiate feature objects for custom raster-vector processing logic.

### Prompt Snippet
```text
val feature = new edu.ucr.cs.bdlab.beast.geolite.RasterFeature(values, schema)
```

### Common Failure Modes
- **Schema Mismatch:** Providing a `values` array with a different length than the `schema` fields will result in runtime errors.
- **Type Incompatibility:** Passing a value that cannot be cast to the type defined in the `StructType` (e.g., a `String` where a `Float` is expected) will cause a casting exception.
- **Null Values:** If the schema does not allow nulls, providing `null` in the `values` array may lead to downstream processing failures.

### Fix Code Hint
```scala
// Ensure schema and values are aligned
val schema = StructType(List(StructField("pixel_val", FloatType)))
val values = Array[Any](10.5f)
val feature = new edu.ucr.cs.bdlab.beast.geolite.RasterFeature(values, schema)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterFeature.append`

### Signature
```scala
[](rasterFeature:RasterFeature,name:String,value:Any):RasterFeature
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterFeature.scala:59_

### Goal
Appends a new attribute field with a specified name and value to an existing `RasterFeature` object, returning a new `RasterFeature` containing the additional data.

### Parameters
- `rasterFeature` (`RasterFeature`): The source `RasterFeature` instance to which the new attribute will be added.
- `name` (`String`): The key or identifier for the new attribute being appended.
- `value` (`Any`): The data value to associate with the provided name; can be any type supported by the underlying feature storage.

### Input
- **Data:** An existing `RasterFeature` object.
- **Preconditions:** The `RasterFeature` must be initialized. This operation is typically used during raster-vector processing workflows (Raptor) to enrich raster-derived features with additional metadata or join results.

### Output
Returns `RasterFeature` — A new `RasterFeature` instance that includes the original attributes plus the newly appended key-value pair.

### Valid Call Patterns
```scala
// Inferred from signature:
val updatedFeature = edu.ucr.cs.bdlab.beast.geolite.RasterFeature.append(originalFeature, "new_attribute", 123.45)
```

### LLM Instruction Prompt
- When calling `edu.ucr.cs.bdlab.beast.geolite.RasterFeature.append`, ensure the first argument is a valid `RasterFeature` object.
- The `name` parameter should be a unique string identifier for the attribute.
- The `value` parameter accepts `Any`, but ensure the type is compatible with downstream processing or serialization requirements (e.g., when saving to GeoJSON or Shapefile).

### Prompt Snippet
```text
val feature = ... // existing RasterFeature
val enrichedFeature = edu.ucr.cs.bdlab.beast.geolite.RasterFeature.append(feature, "attributeName", attributeValue)
```

### Common Failure Modes
- **Null Inputs:** Passing a null `RasterFeature` will result in a `NullPointerException`.
- **Type Mismatch:** Providing a `value` type that is incompatible with the expected schema of the output format (e.g., if saving to a strict format like Shapefile) may cause errors during the `saveAs...` phase.
- **Immutability:** Note that this function returns a new `RasterFeature`; failing to capture the return value will result in the original feature remaining unchanged.

### Fix Code Hint
```scala
// Ensure the returned RasterFeature is captured
val myFeature = ...
val myEnrichedFeature = edu.ucr.cs.bdlab.beast.geolite.RasterFeature.append(myFeature, "density", 0.85)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterFeature.setOrAppend`

### Signature
```scala
[](attr:String,value:Any):RasterFeature
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterFeature.scala:35_

### Goal
Updates an existing attribute value or appends a new attribute-value pair to a `RasterFeature` object within the Beast geospatial framework.

### Parameters
- `attr` (`String`): The name of the attribute key to be set or added to the feature.
- `value` (`Any`): The value to associate with the specified attribute key.

### Input
The caller must provide an initialized `RasterFeature` instance. This method is typically used during the processing of raster-vector joins or when enriching raster metadata within a Spark transformation pipeline. No specific file format is required as input, as the operation acts on in-memory feature objects.

### Output
Returns `RasterFeature` — The modified `RasterFeature` instance containing the updated or newly appended attribute.

### Valid Call Patterns
```scala
// Inferred from signature:
val updatedFeature = rasterFeature.setOrAppend("attributeName", "attributeValue")
```

### LLM Instruction Prompt
- When calling `setOrAppend`, ensure the receiver is an instance of `edu.ucr.cs.bdlab.beast.geolite.RasterFeature`.
- The `attr` parameter must be a non-null `String`.
- The `value` parameter accepts any type (`Any`), but ensure the downstream consumers of the `RasterFeature` are compatible with the type provided.

### Prompt Snippet
```text
// Example usage of setOrAppend on a RasterFeature
val feature: RasterFeature = ...
val modifiedFeature = feature.setOrAppend("density", 0.75f)
```

### Common Failure Modes
- **Null Pointer Exceptions:** Passing a null `attr` string will result in a runtime error.
- **Type Mismatch:** If downstream logic expects a specific type (e.g., `Double`) for an attribute, passing an incompatible type (e.g., `String`) via `value` will cause failures during subsequent feature processing or serialization.
- **Immutability Assumptions:** If the `RasterFeature` implementation is immutable, ensure the returned `RasterFeature` is captured, as the original object will not be modified in-place.

### Fix Code Hint
```scala
// Ensure the returned instance is used
val myFeature = new RasterFeature(...)
val updated = myFeature.setOrAppend("key", "value")
// Use 'updated' for subsequent operations
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer`

### Signature
```scala
[]():<type ?>
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterFeatureSerializer.scala:25_

### Goal
Provides a serialization mechanism for raster features within the Beast framework to facilitate the conversion of raster data into a format suitable for distributed processing or storage.

### Parameters
_None._

### Input
This class is intended for use within the Beast Spark-based ecosystem. It assumes the caller is working with raster data structures (such as `RasterRDD` or `ITile`) that are compatible with the Raptor processing engine. No specific file format is required as input to the constructor, but the serializer is designed to handle raster-based feature data.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it represents an instance of the `RasterFeatureSerializer` class used for serializing raster-related geospatial features.

### Valid Call Patterns
```scala
// Inferred from signature:
val serializer = new edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer()
```

### LLM Instruction Prompt
- When generating code involving `RasterFeatureSerializer`, ensure it is instantiated as a standard Scala class.
- Use this class when you need to handle the serialization of raster features in custom Spark transformations or when extending Beast's internal data handling.
- Do not assume specific return types for methods within this class as they are not provided in the authoritative API facts.

### Prompt Snippet
```text
val serializer = new edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer()
```

### Common Failure Modes
- **Instantiation Errors:** Attempting to use the class without proper Spark context or within an environment where the Beast dependencies are not correctly linked in the classpath.
- **Type Mismatch:** Attempting to pass non-raster feature types to methods of this serializer, which may lead to runtime serialization exceptions.
- **Missing Dependencies:** Failure to include `beast-spark` in the project's `pom.xml` or `build.sbt`, causing the class to be unresolved.

### Fix Code Hint
```scala
// Ensure the dependency is present in your build configuration:
// <dependency>
//   <groupId>edu.ucr.cs.bdlab</groupId>
//   <artifactId>beast-spark</artifactId>
//   <version>0.10.1</version>
// </dependency>

import edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer

val serializer = new RasterFeatureSerializer()
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer.read`

### Signature
```scala
[](kryo:Kryo,input:Input,klass:Class[RasterFeature]):RasterFeature
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterFeatureSerializer.scala:31_

### Goal
Deserializes a `RasterFeature` object from a binary stream using the Kryo serialization framework within the Beast geospatial processing pipeline.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for object graph serialization and deserialization.
- `input` (`Input`): The binary input stream containing the serialized `RasterFeature` data.
- `klass` (`Class[RasterFeature]`): The class type of the object to be deserialized, specifically `RasterFeature`.

### Input
The caller must provide a valid `Kryo` instance and an `Input` stream that contains data previously serialized by a corresponding `RasterFeatureSerializer.write` operation. This method is typically invoked within the context of Spark's Kryo-based serialization for distributed RDDs containing raster-vector features.

### Output
Returns `RasterFeature` — The reconstructed `RasterFeature` object containing the spatial geometry and associated raster pixel data.

### Valid Call Patterns
```scala
// Inferred usage pattern for custom Kryo serializers
val serializer = new edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer()
val feature = serializer.read(kryo, input, classOf[RasterFeature])
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure it is wrapped within a custom Kryo `Serializer` implementation.
- Always pass the `kryo` and `input` objects provided by the `read` method signature of the `Serializer` class.
- Use `classOf[RasterFeature]` as the third argument to satisfy the `klass` parameter requirement.

### Prompt Snippet
```text
val serializer = new edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer()
val feature = serializer.read(kryo, input, classOf[RasterFeature])
```

### Common Failure Modes
- **Serialization Mismatch:** Attempting to read data that was not serialized using the `RasterFeatureSerializer` will result in a `KryoException`.
- **Stream Exhaustion:** Providing an `Input` stream that is empty or truncated before the full `RasterFeature` is read will cause an `EOFException`.
- **Context Errors:** Calling this method outside of a valid Kryo serialization context (e.g., without a properly initialized `Kryo` instance) will lead to null pointer exceptions or runtime crashes.

### Fix Code Hint
```scala
// Ensure the input stream is positioned correctly before calling read
if (!input.end()) {
  val feature = serializer.read(kryo, input, classOf[RasterFeature])
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer.write`

### Signature
```scala
[](kryo:Kryo,output:Output,rasterFeature:RasterFeature):Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterFeatureSerializer.scala:26_

### Goal
Serializes a `RasterFeature` object into a byte stream using the Kryo serialization framework for efficient storage or network transmission within the Beast framework.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for managing the serialization process.
- `output` (`Output`): The Kryo `Output` stream where the serialized bytes of the `RasterFeature` will be written.
- `rasterFeature` (`RasterFeature`): The specific `RasterFeature` instance to be serialized.

### Input
The caller must provide a valid, initialized `Kryo` instance and an open `Output` stream. The `RasterFeature` must be a valid instance compatible with the Beast `geolite` package. No specific file format is required as input, as this operates on in-memory objects.

### Output
Returns `Unit`. The side effect is that the serialized representation of the `RasterFeature` is written to the provided `Output` stream.

### Valid Call Patterns
```scala
// Inferred from signature:
val serializer = new edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer()
serializer.write(kryo, output, rasterFeature)
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure that the `Kryo` instance is properly configured to handle `RasterFeature` types.
- Always ensure the `Output` stream is flushed or closed appropriately after the `write` operation is completed to ensure data integrity.
- This method is intended for low-level serialization; ensure it is used within the context of a Spark serialization workflow or custom Kryo-based persistence logic.

### Prompt Snippet
```text
// Example of using RasterFeatureSerializer to write to a Kryo output
val serializer = new edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer()
serializer.write(kryo, output, myRasterFeature)
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `kryo`, `output`, or `rasterFeature` arguments are null.
- **KryoException**: Occurs if the `Output` stream is closed, full, or if the `RasterFeature` contains data that cannot be serialized by the current Kryo configuration.
- **Serialization Mismatch**: If the `RasterFeature` structure has changed or is incompatible with the expected schema, serialization may fail or produce corrupted bytes.

### Fix Code Hint
```scala
// Ensure the output stream is valid before writing
if (output != null && rasterFeature != null) {
  new edu.ucr.cs.bdlab.beast.geolite.RasterFeatureSerializer().write(kryo, output, rasterFeature)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata`

### Signature
```scala
[](x1:Int,y1:Int,x2:Int,y2:Int,tileWidth:Int,tileHeight:Int,srid:Int,g2m:AffineTransform):<type ?>
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:45_

### Goal
Defines the spatial and structural metadata for a raster dataset, including its coordinate extent, tiling configuration, and the affine transformation mapping pixel coordinates to map coordinates.

### Parameters
- `x1` (`Int`): The minimum x-coordinate (column) of the raster extent.
- `y1` (`Int`): The minimum y-coordinate (row) of the raster extent.
- `x2` (`Int`): The maximum x-coordinate (column) of the raster extent.
- `y2` (`Int`): The maximum y-coordinate (row) of the raster extent.
- `tileWidth` (`Int`): The width of individual tiles in pixels.
- `tileHeight` (`Int`): The height of individual tiles in pixels.
- `srid` (`Int`): The Spatial Reference System Identifier (EPSG code) for the raster.
- `g2m` (`AffineTransform`): The transformation matrix used to convert grid (pixel) coordinates to map (geospatial) coordinates.

### Input
The caller must provide valid integer bounds for the raster grid and a properly initialized `java.awt.geom.AffineTransform` object. This metadata is typically required when initializing custom raster sources or performing manual raster reshaping operations within the Raptor framework.

### Output
Returns an instance of `RasterMetadata` (type inferred from class context) representing the spatial configuration of the raster.

### Valid Call Patterns
```scala
// Example inferred from signature
val metadata = new edu.ucr.cs.bdlab.beast.geolite.RasterMetadata(
  0, 0, 1024, 1024, 256, 256, 4326, new java.awt.geom.AffineTransform()
)
```

### LLM Instruction Prompt
- When generating code using `RasterMetadata`, ensure the `AffineTransform` is correctly configured to map the grid space defined by `(x1, y1)` to `(x2, y2)` into the target map projection.
- Always verify that `tileWidth` and `tileHeight` are positive integers, as these define the internal tiling structure used by Beast's Raptor join operations.

### Prompt Snippet
```text
// Constructing RasterMetadata for a 1024x1024 raster with 256x256 tiles in EPSG:4326
val meta = new edu.ucr.cs.bdlab.beast.geolite.RasterMetadata(0, 0, 1024, 1024, 256, 256, 4326, transform)
```

### Common Failure Modes
- **Coordinate Mismatch:** Providing `x1, y1, x2, y2` values that do not align with the actual pixel dimensions of the underlying raster data, leading to incorrect spatial joins.
- **Invalid AffineTransform:** Passing an identity transform when the raster requires specific scaling or rotation, resulting in misaligned geospatial data.
- **Incompatible Metadata:** Attempting to perform `overlay` or `raptorJoin` operations between two rasters with differing `RasterMetadata` configurations; use `reshape` to align them first.

### Fix Code Hint
```scala
// Ensure the AffineTransform matches the raster's resolution
val g2m = new java.awt.geom.AffineTransform(pixelSizeX, 0, 0, -pixelSizeY, minX, maxY)
val meta = new edu.ucr.cs.bdlab.beast.geolite.RasterMetadata(0, 0, width, height, 256, 256, srid, g2m)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.allMetadata`

### Signature
```scala
[type T](raster:RDD[ITile[T]]):Array[RasterMetadata]
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:337_

### Goal
Extracts the metadata for all tiles within a distributed raster RDD to provide a comprehensive summary of the raster's spatial and structural properties.

### Parameters
- `raster` (`RDD[ITile[T]]`): The distributed raster dataset, represented as an RDD of tiles, where `T` is the pixel data type (e.g., `Int`, `Float`).

### Input
- **Data:** An `RDD[ITile[T]]` representing raster data (e.g., loaded from GeoTIFF or HDF4).
- **Preconditions:** The raster must be properly initialized as an RDD of tiles. Ensure the pixel type `T` matches the underlying data type of the raster to avoid type mismatch errors during processing.

### Output
Returns `Array[RasterMetadata]` — An array containing the metadata objects for the tiles present in the RDD, which includes information such as the spatial extent, resolution, and coordinate reference system (CRS) of the tiles.

### Valid Call Patterns
```scala
// Example inferred from signature
val metadataArray: Array[RasterMetadata] = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.allMetadata(rasterRDD)
```

### LLM Instruction Prompt
- When calling `allMetadata`, ensure the input is an `RDD[ITile[T]]`.
- The function returns an `Array` of `RasterMetadata` objects; be prepared to handle an array of size equal to the number of partitions or tiles in the RDD.
- Use this function when you need to inspect the spatial properties of a raster dataset before performing operations like `raptorJoin` or `reshape`.

### Prompt Snippet
```text
val metadata = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.allMetadata(myRasterRDD)
```

### Common Failure Modes
- **Type Mismatch:** Providing an RDD with an incompatible pixel type `T` (e.g., passing an `RDD[ITile[Float]]` when the system expects `RDD[ITile[Int]]`).
- **Empty RDD:** Calling the function on an empty RDD may return an empty array, which should be handled to avoid index-out-of-bounds errors in downstream logic.
- **Serialization Issues:** Since this returns an `Array` to the driver, calling this on an extremely large RDD with millions of tiles may lead to `OutOfMemoryError` on the Spark driver.

### Fix Code Hint
```scala
// Ensure the RDD is not empty before processing metadata
if (!rasterRDD.isEmpty()) {
  val metadata = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.allMetadata(rasterRDD)
  // Process metadata...
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.create`

### Signature
```scala
[](x1:Double,y1:Double,x2:Double,y2:Double,srid:Int,rasterWidth:Int,rasterHeight:Int,tileWidth:Int,tileHeight:Int):RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:324_

### Goal
Creates a `RasterMetadata` object that defines the geographic extent, coordinate reference system, and pixel/tile resolution for a raster dataset in the RDPro framework.

### Parameters
- `x1` (`Double`): The x-coordinate (longitude/easting) of the north-west corner of the raster.
- `y1` (`Double`): The y-coordinate (latitude/northing) of the north-west corner of the raster.
- `x2` (`Double`): The x-coordinate (longitude/easting) of the south-east corner of the raster.
- `y2` (`Double`): The y-coordinate (latitude/northing) of the south-east corner of the raster.
- `srid` (`Int`): The Spatial Reference System Identifier (e.g., 4326 for WGS84).
- `rasterWidth` (`Int`): The total width of the raster dataset in pixels.
- `rasterHeight` (`Int`): The total height of the raster dataset in pixels.
- `tileWidth` (`Int`): The width of an individual tile in pixels.
- `tileHeight` (`Int`): The height of an individual tile in pixels.

### Input
The caller must provide valid geographic bounds and integer dimensions. Precondition: In most standard coordinate systems, `y1` (north) should be greater than `y2` (south). This metadata is required as an input for `sc.rasterizePixels` or `sc.rasterizePoints` to define the spatial context of the pixel data.

### Output
Returns `RasterMetadata` — An object containing the spatial and structural configuration used to interpret raw pixel values as a georeferenced raster.

### Valid Call Patterns
```scala
val metadata = RasterMetadata.create(x1 = -50, y1 = 40, x2 = -60, y2 = 30, srid = 4326,
  rasterWidth = 10, rasterHeight = 10, tileWidth = 10, tileHeight = 10)
```

### LLM Instruction Prompt
- When generating code for `RasterMetadata.create`, ensure all 9 parameters are provided as named arguments for clarity.
- Always verify that the `srid` corresponds to a valid EPSG code (e.g., 4326).
- Ensure `rasterWidth` and `rasterHeight` are positive integers.

### Prompt Snippet
```text
RasterMetadata.create(x1 = <Double>, y1 = <Double>, x2 = <Double>, y2 = <Double>, srid = <Int>, rasterWidth = <Int>, rasterHeight = <Int>, tileWidth = <Int>, tileHeight = <Int>)
```

### Common Failure Modes
- **Coordinate Inversion:** Providing `y1 < y2` when the coordinate system expects the north-west corner to have a higher y-value than the south-east corner, leading to inverted or incorrectly mapped rasters.
- **Dimension Mismatch:** Providing `tileWidth` or `tileHeight` that are not factors of the total `rasterWidth` or `rasterHeight` may cause issues in downstream tiling operations.
- **SRID Mismatch:** Using an incorrect `srid` will result in spatial joins (e.g., `raptorJoin`) failing to align correctly with vector data.

### Fix Code Hint
```scala
// Ensure y1 > y2 for standard geographic projections
val metadata = RasterMetadata.create(-50.0, 40.0, -60.0, 30.0, 4326, 1000, 1000, 256, 256)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.envelope`

### Signature
```scala
[]:Envelope
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:170_

### Goal
Retrieves the spatial extent (Minimum Bounding Rectangle) of a raster dataset as defined by its associated `RasterMetadata`.

### Parameters
_None._

### Input
The caller must have an initialized `RasterMetadata` object, typically obtained from a raster source (e.g., GeoTIFF or HDF4) loaded into the Beast framework. No specific file format is required at the time of calling, as the metadata object must already be instantiated in memory.

### Output
Returns `Envelope` — A spatial envelope object representing the minimum and maximum coordinate bounds (xmin, ymin, xmax, ymax) of the raster in its native coordinate reference system.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ... // Obtained from a raster source
val extent: Envelope = metadata.envelope()
```

### LLM Instruction Prompt
- When calling `envelope()` on a `RasterMetadata` instance, ensure the metadata object has been correctly initialized from a valid raster source (GeoTIFF or HDF4).
- The returned `Envelope` object is a standard Beast spatial type; do not attempt to cast it to other geometry types without checking the API.
- This method takes no arguments; do not pass parameters.

### Prompt Snippet
```text
val metadata = rasterRDD.metadata
val bounds = metadata.envelope()
```

### Common Failure Modes
- **Null Metadata:** Calling `envelope()` on an uninitialized or null `RasterMetadata` reference will result in a `NullPointerException`.
- **Coordinate System Mismatch:** The returned `Envelope` is in the raster's native CRS; if performing spatial joins with vector data, ensure the vector data is in the same CRS or reprojected accordingly.
- **Missing Metadata:** If the raster source was not correctly loaded or the metadata was not extracted, the `RasterMetadata` object may be incomplete.

### Fix Code Hint
```scala
// Ensure the raster is loaded and metadata is accessible
val rasterMetadata = myRasterRDD.metadata
val rasterBounds = rasterMetadata.envelope()
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.equals`

### Signature
```scala
[](obj:Any):Boolean
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:196_

### Goal
Determines whether the current `RasterMetadata` instance is structurally and semantically equivalent to another object.

### Parameters
- `obj` (`Any`): The object to compare against the current `RasterMetadata` instance.

### Input
- **Data:** An instance of `RasterMetadata` (the receiver) and an arbitrary object `obj`.
- **Preconditions:** The receiver must be a valid `RasterMetadata` object initialized from a raster source (e.g., GeoTIFF or HDF4).

### Output
Returns `Boolean` — `true` if the provided object is an instance of `RasterMetadata` and contains identical metadata properties (such as resolution, CRS, and tile dimensions) as the receiver; `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature: comparing two RasterMetadata instances
val metadataA: RasterMetadata = ...
val metadataB: RasterMetadata = ...
val isSame: Boolean = metadataA.equals(metadataB)
```

### LLM Instruction Prompt
- When comparing raster datasets for compatibility (e.g., before performing `overlay` or `raptorJoin` operations), use `equals` to verify that the `RasterMetadata` of the involved rasters match.
- Ensure the receiver is not null before invoking this method to avoid `NullPointerException`.

### Prompt Snippet
```text
// Check if two raster metadata objects are identical
val match = rasterMetadata1.equals(rasterMetadata2)
```

### Common Failure Modes
- **Type Mismatch:** Passing an object that is not an instance of `RasterMetadata` will return `false` rather than throwing an error, which may lead to logical bugs if not handled.
- **Metadata Incompatibility:** Relying on `equals` to determine if two rasters can be combined; note that `overlay` operations require identical metadata, and if `equals` returns `false`, you must use `reshape` to align the rasters before proceeding.

### Fix Code Hint
```scala
// If metadata does not match, reshape the raster to match the target metadata
if (!rasterA.metadata.equals(rasterB.metadata)) {
  val reshapedRaster = rasterA.reshape(rasterB.metadata)
  // Proceed with operation using reshapedRaster
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.extents`

### Signature
```scala
[]:Geometry
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:181_

### Goal
Retrieves the spatial extent of a raster dataset as a `Geometry` object, defining the bounding box or footprint of the raster in its coordinate reference system.

### Parameters
_None._

### Input
The caller must have an initialized `RasterMetadata` object associated with a valid raster source (e.g., GeoTIFF or HDF4). No specific file format is required at the time of calling, as the metadata object must already be populated from the source raster.

### Output
Returns `Geometry` — A geometry object representing the spatial boundary (typically the Minimum Bounding Rectangle) of the raster data.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ... // Obtained from a RasterRDD or GeoTiff source
val rasterExtents: Geometry = metadata.extents()
```

### LLM Instruction Prompt
- When using `extents()`, ensure the `RasterMetadata` instance is already initialized from a valid raster source.
- The returned `Geometry` is typically used for spatial filtering, intersection checks, or aligning rasters during `reshape` operations.
- Do not attempt to modify the returned `Geometry` directly if it is intended to represent the immutable bounds of the source raster.

### Prompt Snippet
```text
val metadata = rasterRDD.metadata
val bounds = metadata.extents()
```

### Common Failure Modes
- **Uninitialized Metadata:** Calling `extents()` on a `RasterMetadata` object that has not been properly loaded from a raster file will result in a null or empty geometry.
- **Coordinate Mismatch:** If performing spatial joins or overlays, ensure the `Geometry` returned by `extents()` is in the same CRS as the target vector or raster data; use `reshape` if alignment is required.
- **Null Pointer:** If the raster source was not correctly parsed, the metadata object itself may be null, leading to a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure metadata is valid before accessing extents
Option(rasterMetadata).map(_.extents()).getOrElse(throw new Exception("Metadata not initialized"))
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.forOneTile`

### Signature
```scala
[](tileID:Int):RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:300_

### Goal
Retrieves the spatial and structural metadata for a specific raster tile identified by its unique integer ID within a Beast raster dataset.

### Parameters
- `tileID` (`Int`): The unique identifier of the raster tile for which metadata is requested.

### Input
- **Data:** Requires an existing raster dataset context (e.g., GeoTIFF or HDF4) that has been indexed or partitioned such that tiles are addressable by an integer ID.
- **Preconditions:** The `tileID` must correspond to a valid tile index within the current raster partition or dataset.

### Output
Returns `RasterMetadata` — An object containing the spatial extent, resolution, coordinate reference system (CRS), and pixel dimensions associated with the specified tile.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.forOneTile(tileID)
```

### LLM Instruction Prompt
- When generating code that uses `forOneTile`, ensure the `tileID` is derived from a valid raster indexing operation.
- Always treat the returned `RasterMetadata` as immutable and use it to verify compatibility (e.g., resolution or CRS matching) before performing `overlay` or `raptorJoin` operations.

### Prompt Snippet
```text
// Example of retrieving metadata for a specific tile
val tileMetadata = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.forOneTile(targetTileId)
```

### Common Failure Modes
- **Invalid ID:** Providing a `tileID` that does not exist in the current dataset will result in an index-out-of-bounds or lookup error.
- **Metadata Mismatch:** Attempting to use the returned `RasterMetadata` for `overlay` operations without ensuring the target raster shares identical resolution and CRS will cause runtime failures; use `reshape` to align rasters first.
- **Contextual Scope:** Calling this function outside of a scope where the raster dataset is initialized or indexed.

### Fix Code Hint
```scala
// Ensure the tileID is valid and the raster is properly indexed
val tileID = 0 // Example ID
try {
  val meta = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.forOneTile(tileID)
  println(s"Tile extent: ${meta.getExtent}")
} catch {
  case e: Exception => println("Failed to retrieve metadata: " + e.getMessage)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.getPixelScaleX`

### Signature
```scala
[]:Double
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:93_

### Goal
Retrieves the horizontal pixel scale (resolution) value from the raster metadata, representing the width of a single pixel in the coordinate reference system units.

### Parameters
_None._

### Input
The caller must have an initialized `RasterMetadata` object, typically obtained from a `RasterRDD` or a loaded GeoTIFF/HDF4 file. No specific preconditions are required other than the metadata being successfully parsed from the source raster.

### Output
Returns `Double` — The horizontal scale factor of the raster pixels.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = rasterRDD.metadata
val pixelScaleX: Double = metadata.getPixelScaleX()
```

### LLM Instruction Prompt
- When generating code that requires raster resolution, use `getPixelScaleX()` to access the horizontal component of the pixel scale.
- Ensure the `RasterMetadata` instance is available from the `RasterRDD` before calling this method.
- Do not assume the scale is 1.0; always retrieve it dynamically from the metadata object.

### Prompt Snippet
```text
val metadata = raster.metadata
val scaleX = metadata.getPixelScaleX()
```

### Common Failure Modes
- **Null Metadata:** Calling this on a `RasterMetadata` object that was not properly initialized or extracted from a valid raster source will result in a `NullPointerException`.
- **Coordinate System Mismatch:** If the raster is not properly georeferenced, the returned scale may not reflect the intended physical units (e.g., degrees vs. meters).
- **Reshape Requirement:** If performing `overlay` operations, ensure that the `getPixelScaleX()` values of the involved rasters match; if they differ, use `reshape` to align the rasters before processing.

### Fix Code Hint
```scala
// Ensure metadata is valid before access
if (metadata != null) {
  val scaleX = metadata.getPixelScaleX()
  // Proceed with spatial calculations
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.getTileIDAtPixel`

### Signature
```scala
[](iPixel:Int,jPixel:Int):Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:69_

### Goal
Calculates the unique identifier of the raster tile that contains the specified pixel coordinate within the raster grid.

### Parameters
- `iPixel` (`Int`): The column index (x-coordinate) of the pixel in the raster.
- `jPixel` (`Int`): The row index (y-coordinate) of the pixel in the raster.

### Input
The caller must provide an instance of `RasterMetadata` that has been initialized with the correct raster dimensions and tiling scheme. The pixel coordinates `iPixel` and `jPixel` must be within the bounds of the raster defined by the metadata.

### Output
Returns `Int` — The unique ID of the tile containing the specified pixel.

### Valid Call Patterns
```scala
// Example inferred from signature
val tileID = rasterMetadata.getTileIDAtPixel(i, j)
```

### LLM Instruction Prompt
- When using `getTileIDAtPixel`, ensure the `RasterMetadata` object is fully initialized.
- Always validate that `iPixel` and `jPixel` are non-negative and within the raster's width and height before calling this method to avoid out-of-bounds logic errors.
- Use this method when performing manual tile-based indexing or debugging spatial partitioning of raster data.

### Prompt Snippet
```text
val tileID = metadata.getTileIDAtPixel(pixelX, pixelY)
```

### Common Failure Modes
- **Out of Bounds:** Providing `iPixel` or `jPixel` values that exceed the raster dimensions defined in the metadata, which may lead to incorrect tile ID lookups or runtime exceptions.
- **Uninitialized Metadata:** Calling the method on a `RasterMetadata` instance that has not been properly populated with the raster's tiling configuration.
- **Coordinate Mismatch:** Using global coordinates instead of local pixel coordinates; ensure inputs are relative to the raster grid origin.

### Fix Code Hint
```scala
// Ensure coordinates are within bounds before lookup
if (iPixel >= 0 && iPixel < metadata.width && jPixel >= 0 && jPixel < metadata.height) {
  val tileID = metadata.getTileIDAtPixel(iPixel, jPixel)
  // Proceed with tile-specific logic
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.getTileIDAtPoint`

### Signature
```scala
[](x:Double,y:Double):Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:81_

### Goal
Determines the unique identifier of the raster tile that contains a specific geographic coordinate point within the defined raster metadata.

### Parameters
- `x` (`Double`): The x-coordinate (longitude or column index) of the point in the raster's coordinate system.
- `y` (`Double`): The y-coordinate (latitude or row index) of the point in the raster's coordinate system.

### Input
The caller must have an initialized `RasterMetadata` object. This method assumes the coordinate system of the input `(x, y)` matches the spatial reference system defined in the metadata.

### Output
Returns `Int` — The integer ID of the tile covering the specified coordinate.

### Valid Call Patterns
```scala
// Example inferred from signature
val tileID = rasterMetadata.getTileIDAtPoint(x, y)
```

### LLM Instruction Prompt
- Use this method when you need to map a specific geographic point to its corresponding tile index within a raster dataset.
- Ensure the `x` and `y` values are provided as `Double` types.
- The method is called on an instance of `RasterMetadata`.

### Prompt Snippet
```text
val tileID = metadata.getTileIDAtPoint(x, y)
```

### Common Failure Modes
- **Coordinate Out of Bounds:** If the provided `(x, y)` point falls outside the spatial extent defined by the `RasterMetadata`, the behavior depends on the implementation (may return an invalid ID or throw an exception).
- **CRS Mismatch:** Providing coordinates in a different projection than the one defined in the `RasterMetadata` will result in incorrect tile identification.
- **Uninitialized Metadata:** Calling this on a null or uninitialized `RasterMetadata` object will result in a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure coordinates are within the MBR of the raster before querying
if (metadata.getMBR.contains(x, y)) {
  val tileID = metadata.getTileIDAtPoint(x, y)
} else {
  // Handle out-of-bounds case
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.getTileX1`

### Signature
```scala
[](tileID:Int):Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:107_

### Goal
Retrieves the starting X-coordinate (column index) of a specific tile within the raster grid based on its unique tile identifier.

### Parameters
- `tileID` (`Int`): The unique identifier of the tile within the raster metadata structure.

### Input
- **Preconditions:** The `RasterMetadata` object must be initialized and populated with valid tiling information. This method is typically used when iterating over tiles in a `RasterRDD` or performing spatial indexing operations where tile-to-pixel coordinate mapping is required.

### Output
Returns `Int` — The starting X-coordinate (column index) of the specified tile in the raster's global coordinate system.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ...
val startX = metadata.getTileX1(tileID)
```

### LLM Instruction Prompt
- When using `getTileX1`, ensure the `tileID` provided is within the valid range defined by the `RasterMetadata` instance. This method is intended for low-level coordinate calculations during custom raster processing tasks.

### Prompt Snippet
```text
// Example usage of RasterMetadata.getTileX1
val tileX = metadata.getTileX1(currentTileID)
```

### Common Failure Modes
- **IndexOutOfBounds:** Providing a `tileID` that does not exist within the current `RasterMetadata` configuration may result in unexpected values or runtime exceptions depending on the internal implementation of the metadata object.
- **Uninitialized Metadata:** Calling this method on a `RasterMetadata` object that has not been properly loaded or initialized from a raster source (e.g., GeoTIFF or HDF4) will lead to invalid results.

### Fix Code Hint
```scala
// Ensure the metadata is valid before accessing tile coordinates
if (metadata != null) {
  val x1 = metadata.getTileX1(tileID)
  // Proceed with coordinate-based logic
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.getTileX2`

### Signature
```scala
[](tileID:Int):Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:118_

### Goal
Retrieves the X-coordinate index of a specific tile within the raster grid based on its unique tile identifier.

### Parameters
- `tileID` (`Int`): The unique identifier of the tile within the raster metadata structure.

### Input
- **Preconditions:** The `RasterMetadata` object must be initialized and associated with a valid raster dataset (e.g., GeoTIFF or HDF4). The `tileID` must be a valid index within the range defined by the raster's tiling scheme.

### Output
Returns `Int` — The X-coordinate index of the tile in the raster's grid layout.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ... // Obtained from a RasterRDD or GeoTiff
val tileX = metadata.getTileX2(tileID)
```

### LLM Instruction Prompt
- Use this method when you need to map a flat `tileID` to its corresponding column position in a 2D raster grid.
- Ensure the `RasterMetadata` instance is correctly loaded from the target raster source before invoking this method.
- This method is typically used in low-level pixel-to-tile coordinate transformations.

### Prompt Snippet
```text
val tileX = rasterMetadata.getTileX2(tileID)
```

### Common Failure Modes
- **Invalid Tile ID:** Providing a `tileID` that is out of bounds for the specific raster metadata will result in an incorrect coordinate or an `ArrayIndexOutOfBoundsException` depending on the underlying implementation.
- **Uninitialized Metadata:** Calling this on a `RasterMetadata` object that has not been properly populated from a raster source will lead to unpredictable results.

### Fix Code Hint
```scala
// Ensure the tileID is within the valid range before calling
if (tileID >= 0 && tileID < metadata.numTiles) {
  val x = metadata.getTileX2(tileID)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.getTileY1`

### Signature
```scala
[](tileID:Int):Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:128_

### Goal
Retrieves the starting Y-coordinate (row index) of a specific raster tile within the grid defined by the `RasterMetadata` instance.

### Parameters
- `tileID` (`Int`): The unique identifier of the raster tile for which the Y-coordinate is requested.

### Input
The caller must have an initialized `RasterMetadata` object associated with a raster dataset (e.g., GeoTIFF or HDF4). The `tileID` must be a valid index within the range of tiles defined by the raster's spatial partitioning.

### Output
Returns `Int` — The Y-coordinate (row index) of the specified tile in the raster grid.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ... // obtained from a RasterRDD or GeoTiff
val y1: Int = metadata.getTileY1(tileID)
```

### LLM Instruction Prompt
- Use this method when calculating the spatial extent or grid position of a specific tile in a raster processing workflow.
- Ensure the `tileID` provided is within the bounds of the raster's tiling scheme to avoid logical errors.
- This method is part of the `RasterMetadata` class; ensure the instance is correctly initialized from the source raster data.

### Prompt Snippet
```text
val y1 = metadata.getTileY1(tileID)
```

### Common Failure Modes
- **Invalid Tile ID:** Providing a `tileID` that does not exist in the current metadata context will result in incorrect coordinate calculations or potential out-of-bounds errors.
- **Uninitialized Metadata:** Calling this on a `RasterMetadata` object that has not been properly populated from a source file (GeoTIFF/HDF4) will lead to runtime exceptions.
- **Coordinate Mismatch:** Assuming the returned value is in projected units rather than tile-grid indices; this method returns the grid-based row index.

### Fix Code Hint
```scala
// Ensure the tileID is valid before retrieval
if (tileID >= 0 && tileID < metadata.numTiles) {
  val y1 = metadata.getTileY1(tileID)
  // Proceed with spatial calculation
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.getTileY2`

### Signature
```scala
[](tileID:Int):Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:139_

### Goal
Retrieves the Y-coordinate index of a specific tile within the raster grid based on its unique tile identifier.

### Parameters
- `tileID` (`Int`): The unique identifier of the raster tile for which the Y-coordinate index is requested.

### Input
- **Preconditions:** The `RasterMetadata` object must be initialized and associated with a valid raster dataset (e.g., GeoTIFF or HDF4). The `tileID` must be a valid index within the range defined by the raster's tiling scheme.

### Output
Returns `Int` — The Y-coordinate index of the tile in the raster's grid structure.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ... // Obtained from a RasterRDD or GeoTiff
val yIndex = metadata.getTileY2(tileID)
```

### LLM Instruction Prompt
- When using `getTileY2`, ensure the `tileID` is derived from the same `RasterMetadata` instance to maintain consistency with the raster's specific tiling geometry. This method is typically used during pixel-level transformations or when mapping spatial coordinates to specific tile indices in Raptor workflows.

### Prompt Snippet
```text
val y = metadata.getTileY2(tileID)
```

### Common Failure Modes
- **Invalid Tile ID:** Providing a `tileID` that falls outside the bounds of the raster's grid will result in an incorrect index or potential runtime exceptions depending on the underlying implementation.
- **Metadata Mismatch:** Calling this method on a `RasterMetadata` object that does not match the current raster partition can lead to logical errors in spatial joins or pixel-level processing.

### Fix Code Hint
```scala
// Ensure the tileID is within the valid range for the metadata
if (tileID >= 0 && tileID < metadata.numTiles) {
  val y = metadata.getTileY2(tileID)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.gridToGridTransform`

### Signature
```scala
[](sourceMetadata:RasterMetadata,targetMetadata:RasterMetadata):MathTransform
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:425_

### Goal
Calculates the affine transformation required to map pixel coordinates from a source raster grid to a target raster grid.

### Parameters
- `sourceMetadata` (`RasterMetadata`): The metadata object defining the grid structure, CRS, and extent of the source raster.
- `targetMetadata` (`RasterMetadata`): The metadata object defining the grid structure, CRS, and extent of the target raster.

### Input
The caller must provide two valid `RasterMetadata` objects. These objects are typically derived from existing raster datasets (e.g., GeoTIFF or HDF4) loaded into the Beast framework. Ensure that both metadata objects are fully initialized with correct coordinate reference systems and grid dimensions before invoking this method.

### Output
Returns `MathTransform` — A GeoTools-compatible `MathTransform` object that encapsulates the mathematical mapping between the two grid coordinate systems.

### Valid Call Patterns
```scala
// Inferred from signature:
val transform = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.gridToGridTransform(sourceMeta, targetMeta)
```

### LLM Instruction Prompt
- Use `edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.gridToGridTransform` when you need to align two different raster grids or perform coordinate conversion between raster spaces.
- Always pass two `RasterMetadata` instances as arguments.
- The returned `MathTransform` can be used for subsequent pixel-level coordinate transformations.

### Prompt Snippet
```text
val transform = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.gridToGridTransform(sourceMetadata, targetMetadata)
```

### Common Failure Modes
- **CRS Mismatch:** If the source and target rasters are in incompatible coordinate reference systems, the resulting `MathTransform` may produce invalid coordinates.
- **Null Metadata:** Passing uninitialized or null `RasterMetadata` objects will result in a `NullPointerException`.
- **Incompatible Grids:** If the grids are not properly defined (e.g., zero dimensions), the transformation logic may fail to compute a valid mapping.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from valid RasterRDDs or files
val sourceMeta = rasterRDD1.metadata
val targetMeta = rasterRDD2.metadata
val transform = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.gridToGridTransform(sourceMeta, targetMeta)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.gridToModel`

### Signature
```scala
[](i:Double,j:Double,outPoint:Point2D.Double):Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:150_

### Goal
Converts raster grid coordinates (pixel indices) to model coordinates (geospatial coordinates) using the metadata defined in the `RasterMetadata` instance.

### Parameters
- `i` (`Double`): The column index (x-axis) in the raster grid.
- `j` (`Double`): The row index (y-axis) in the raster grid.
- `outPoint` (`Point2D.Double`): A mutable `Point2D.Double` object that will be updated with the resulting model coordinates.

### Input
- **Preconditions:** The `RasterMetadata` instance must be correctly initialized with the raster's affine transformation parameters (e.g., resolution, origin).
- **Data:** Requires valid grid indices `i` and `j` corresponding to the raster dimensions.

### Output
Returns `Unit` — The operation performs an in-place update on the provided `outPoint` object, setting its `x` and `y` values to the corresponding model coordinates.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ... // Obtained from a raster source
val gridX = 10.0
val gridY = 20.0
val modelPoint = new java.awt.geom.Point2D.Double()
metadata.gridToModel(gridX, gridY, modelPoint)
println(s"Model coordinates: ${modelPoint.x}, ${modelPoint.y}")
```

### LLM Instruction Prompt
- When calling `gridToModel`, always instantiate a `java.awt.geom.Point2D.Double` to serve as the `outPoint` container.
- Ensure the `RasterMetadata` instance is derived from the same raster source being processed to maintain coordinate system consistency.
- Note that this method is side-effecting; it does not return a new point but modifies the existing `outPoint` instance.

### Prompt Snippet
```text
val point = new java.awt.geom.Point2D.Double()
metadata.gridToModel(i, j, point)
// point now contains the model coordinates
```

### Common Failure Modes
- **NullPointerException:** Occurs if the `outPoint` argument is passed as `null`.
- **Coordinate Mismatch:** Using `gridToModel` on a `RasterMetadata` instance that does not match the current raster's CRS or affine transform will result in incorrect geospatial coordinates.
- **Out of Bounds:** While the method accepts `Double`, providing indices significantly outside the raster's defined grid dimensions may result in model coordinates outside the expected spatial extent.

### Fix Code Hint
```scala
// Ensure the point is initialized before passing to the method
val outPoint = new java.awt.geom.Point2D.Double()
metadata.gridToModel(i, j, outPoint)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.hashCode`

### Signature
```scala
[]():Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:204_

### Goal
Computes a hash code value for the `RasterMetadata` object, enabling its use in hash-based collections or for equality comparisons within raster processing workflows.

### Parameters
_None._

### Input
The caller must have an initialized `RasterMetadata` instance. This object typically encapsulates spatial properties such as the Coordinate Reference System (CRS), resolution, and tile dimensions of a raster dataset (e.g., GeoTIFF or HDF4).

### Output
Returns `Int` — A hash code integer derived from the internal state of the `RasterMetadata` instance.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ...
val hash: Int = metadata.hashCode()
```

### LLM Instruction Prompt
- When generating code that uses `RasterMetadata`, use the `.hashCode()` method to facilitate object comparison or storage in hash-based structures.
- Ensure the `RasterMetadata` instance is fully initialized before invoking this method.
- Do not assume the hash code is stable across different versions of the Beast framework or different JVM executions.

### Prompt Snippet
```text
val metadata = rasterRDD.metadata
val hash = metadata.hashCode()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `hashCode()` on an uninitialized or null `RasterMetadata` reference.
- **Logic Errors:** Relying on the hash code for persistent storage or cross-session identification, as the implementation may change between framework versions.
- **Compatibility Mismatch:** Attempting to compare hash codes of metadata from different rasters that have not been aligned via `reshape`, which may lead to unexpected inequality even if the rasters appear similar.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from a valid RasterRDD
val metadata = myRasterRDD.metadata
if (metadata != null) {
  val hash = metadata.hashCode()
  println(s"Metadata hash: $hash")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.isPixelInRange`

### Signature
```scala
[](i:Int,j:Int):<type ?>
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:194_

### Goal
Determines whether a specific pixel coordinate (i, j) falls within the valid spatial bounds defined by the `RasterMetadata` instance.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel.
- `j` (`Int`): The row index (y-coordinate) of the pixel.

### Input
The caller must have an initialized `RasterMetadata` object, typically derived from a raster source (GeoTIFF or HDF4). This method is used to validate pixel indices before performing pixel-level transformations or accessing data within a `RasterRDD`.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; however, based on the method name, it is expected to return a `Boolean` indicating if the provided (i, j) indices are within the valid range of the raster dimensions.

### Valid Call Patterns
```scala
// Inferred call pattern based on signature
val metadata: RasterMetadata = ...
val isValid = metadata.isPixelInRange(i, j)
```

### LLM Instruction Prompt
- When generating code that accesses raster pixels, use `isPixelInRange` to verify indices before attempting to read or write to the raster to prevent out-of-bounds errors.
- Ensure the `i` and `j` parameters are non-negative integers.

### Prompt Snippet
```text
// Check if pixel is within bounds before processing
if (metadata.isPixelInRange(col, row)) {
  // Perform pixel operation
}
```

### Common Failure Modes
- **Out-of-Bounds Access:** Passing negative indices or indices exceeding the raster width/height will result in logic errors or exceptions if not checked via this method.
- **Metadata Mismatch:** Calling this method on a `RasterMetadata` object that does not correspond to the current raster partition or tile being processed.

### Fix Code Hint
```scala
// Ensure indices are validated against the metadata
if (metadata.isPixelInRange(i, j)) {
  // Safe to proceed with pixel-level logic
} else {
  // Handle out-of-bounds case
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.modelToGrid`

### Signature
```scala
[](x:Double,y:Double,outPoint:Point2D.Double):Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:161_

### Goal
Converts a coordinate from the raster's model space (e.g., geographic or projected coordinates) to the corresponding grid space (pixel coordinates) using the metadata's transformation parameters.

### Parameters
- `x` (`Double`): The x-coordinate in the model space (e.g., longitude or Easting).
- `y` (`Double`): The y-coordinate in the model space (e.g., latitude or Northing).
- `outPoint` (`Point2D.Double`): A mutable `Point2D.Double` object that will be populated with the resulting grid coordinates (column, row).

### Input
The caller must have an initialized `RasterMetadata` instance. This method assumes the metadata has been correctly populated from a raster source (e.g., GeoTIFF or HDF4). No specific file format is required at the time of calling, but the metadata must accurately reflect the spatial reference system and affine transformation of the target raster.

### Output
Returns `Unit` — The result is stored in the provided `outPoint` object by updating its `x` and `y` fields to represent the grid column and row indices.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ... // Obtained from a RasterRDD or GeoTiff
val modelX = -117.3
val modelY = 33.9
val gridPoint = new java.awt.geom.Point2D.Double()
metadata.modelToGrid(modelX, modelY, gridPoint)
println(s"Grid coordinates: ${gridPoint.x}, ${gridPoint.y}")
```

### LLM Instruction Prompt
- When using `modelToGrid`, always instantiate a `java.awt.geom.Point2D.Double` to serve as the `outPoint` container.
- Ensure the `RasterMetadata` instance is valid and associated with the raster being processed.
- Remember that this method performs an in-place update on the `outPoint` object; it does not return a new object.

### Prompt Snippet
```text
val gridPoint = new java.awt.geom.Point2D.Double()
metadata.modelToGrid(x, y, gridPoint)
// gridPoint now contains the transformed coordinates
```

### Common Failure Modes
- **Null Pointer Exception:** Occurs if the `outPoint` argument is passed as `null`.
- **Coordinate Mismatch:** If the `RasterMetadata` does not match the actual raster data (e.g., due to incorrect CRS or affine transform), the resulting grid coordinates will be inaccurate.
- **Out of Bounds:** The resulting grid coordinates may fall outside the valid pixel range of the raster if the input model coordinates are outside the raster's extent.

### Fix Code Hint
```scala
// Ensure the point is initialized before passing to the method
val outPoint = new java.awt.geom.Point2D.Double()
metadata.modelToGrid(x, y, outPoint)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.numTiles`

### Signature
```scala
[]:Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:61_

### Goal
Retrieves the total number of tiles associated with the raster metadata object within the Beast framework.

### Parameters
_None._

### Input
The caller must have an initialized `edu.ucr.cs.bdlab.beast.geolite.RasterMetadata` object. This object is typically derived from raster data sources such as GeoTIFF or HDF4 files processed within the Raptor workflow.

### Output
Returns `Int` — The total count of tiles that constitute the raster dataset described by the metadata.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ... // Obtained from a RasterRDD or file header
val tileCount: Int = metadata.numTiles()
```

### LLM Instruction Prompt
- Use this method only when working with `RasterMetadata` instances.
- Do not confuse this with pixel-level counts; this returns the number of discrete tiles in the raster structure.
- Ensure the `RasterMetadata` object is fully initialized from a valid raster source (GeoTIFF/HDF4) before calling.

### Prompt Snippet
```text
val metadata = rasterRDD.metadata
val count = metadata.numTiles()
```

### Common Failure Modes
- **Null Metadata:** Calling `numTiles()` on an uninitialized or null `RasterMetadata` reference will throw a `NullPointerException`.
- **Incompatible Raster:** If the metadata was not correctly parsed from the source file (e.g., corrupted GeoTIFF header), the returned value may not reflect the actual data structure.
- **Context Mismatch:** Attempting to use this on vector-based metadata; ensure the object is specifically of type `RasterMetadata`.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from a valid RasterRDD
val rasterRDD = sc.geoTiff("path/to/raster.tif")
val metadata = rasterRDD.metadata
val totalTiles = metadata.numTiles()
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.numTilesX`

### Signature
```scala
[]:Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:55_

### Goal
Retrieves the total number of tiles along the X-axis (horizontal dimension) for a raster dataset as defined in its metadata.

### Parameters
_None._

### Input
The caller must have an initialized `RasterMetadata` object, typically obtained from a raster source (e.g., GeoTIFF or HDF4) or an existing `RasterRDD`. No specific file format is required as input to this method, as it operates on the metadata object already loaded into memory.

### Output
Returns `Int` — The count of tiles spanning the width of the raster grid.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = rasterRDD.metadata
val xTiles: Int = metadata.numTilesX
```

### LLM Instruction Prompt
- When generating code that requires raster dimensions, use `numTilesX` to determine the horizontal tiling structure.
- Ensure the `RasterMetadata` instance is correctly initialized from a valid raster source before calling this method.
- This method returns a primitive `Int`; do not expect a collection or complex object.

### Prompt Snippet
```text
val metadata = raster.metadata
val tilesX = metadata.numTilesX
```

### Common Failure Modes
- **Null Metadata:** Calling this on an uninitialized or null `RasterMetadata` object will result in a `NullPointerException`.
- **Context Mismatch:** Attempting to use this value for operations on a different raster dataset without verifying that the metadata matches (e.g., after a `reshape` or `reproject` operation) may lead to index-out-of-bounds errors during processing.
- **Invalid Raster Source:** If the underlying raster file is corrupted or improperly parsed, the metadata may contain default or zero values for tile counts.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the RDD before accessing tile dimensions
val metadata = rasterRDD.metadata
if (metadata != null) {
  val xTiles = metadata.numTilesX
  // Proceed with tile-based logic
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.numTilesY`

### Signature
```scala
[]:Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:58_

### Goal
Retrieves the total number of tiles along the Y-axis (vertical dimension) for a raster dataset as defined in its metadata.

### Parameters
_None._

### Input
The caller must have an initialized `RasterMetadata` object. This object is typically obtained from a `RasterRDD` or by reading the header of a supported raster file (GeoTIFF or HDF4). No specific file format is required at the time of calling, as the metadata is already loaded into memory.

### Output
Returns `Int` — The count of tiles spanning the vertical extent of the raster grid.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = rasterRDD.metadata
val yTiles: Int = metadata.numTilesY
```

### LLM Instruction Prompt
- When generating code that involves raster dimensions, use `numTilesY` to determine the vertical tiling structure.
- Ensure the `RasterMetadata` instance is available before accessing this property.
- This method does not take arguments; do not attempt to pass dimensions or indices.

### Prompt Snippet
```text
// Accessing vertical tile count from raster metadata
val yCount = metadata.numTilesY
```

### Common Failure Modes
- **Null Metadata:** Calling this on a `RasterMetadata` reference that has not been initialized or is null will result in a `NullPointerException`.
- **Misinterpretation:** Confusing `numTilesY` (the count of tiles) with the total number of pixels along the Y-axis (which would be `numTilesY * tileHeight`).
- **Context Mismatch:** Attempting to use this value for operations requiring identical metadata (like `overlay`) without first ensuring the raster has been reshaped to match the target grid.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the RDD before access
val metadata = myRasterRDD.metadata
val yTiles = metadata.numTilesY
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.rasterHeight`

### Signature
```scala
[]:Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:52_

### Goal
Retrieves the total vertical dimension (number of rows) of the raster dataset as defined in its metadata.

### Parameters
_None._

### Input
The caller must have an initialized `RasterMetadata` object, typically obtained from a raster source (e.g., GeoTIFF or HDF4) or an existing `RasterRDD`. No specific file format is required at the time of calling, as the metadata is already loaded into the object.

### Output
Returns `Int` — The total number of pixels along the Y-axis (height) of the raster.

### Valid Call Patterns
```scala
// Inferred from signature:
val height: Int = rasterMetadata.rasterHeight()
```

### LLM Instruction Prompt
- When generating code that uses `rasterHeight`, ensure the `RasterMetadata` instance is already initialized.
- Do not attempt to pass arguments to this method, as it takes no parameters.
- Use this method to verify raster dimensions before performing `overlay` or `reshape` operations, which require metadata alignment.

### Prompt Snippet
```text
// Example usage of rasterHeight to check dimensions
val metadata = rasterRDD.metadata
val height = metadata.rasterHeight()
```

### Common Failure Modes
- **Null Metadata:** Calling `rasterHeight()` on an uninitialized or null `RasterMetadata` object will result in a `NullPointerException`.
- **Metadata Mismatch:** In `overlay` operations, assuming the `rasterHeight` of two different rasters are identical without explicit verification; always check `rasterHeight` and `rasterWidth` before combining datasets.
- **Contextual Scope:** Attempting to call this method outside of a Spark executor or driver context where the `RasterMetadata` object has not been properly serialized or broadcasted.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the RDD before accessing dimensions
val metadata = myRasterRDD.metadata
val h = metadata.rasterHeight()
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.rasterWidth`

### Signature
```scala
[]:Int
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:49_

### Goal
Retrieves the total width of the raster in pixels as defined in the associated `RasterMetadata` object.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.beast.geolite.RasterMetadata`. This metadata is typically extracted from raster data sources such as GeoTIFF or HDF4 files processed within the Beast framework.

### Output
Returns `Int` — The total number of pixels along the horizontal axis (width) of the raster.

### Valid Call Patterns
```scala
// Inferred from signature:
val width: Int = rasterMetadata.rasterWidth()
```

### LLM Instruction Prompt
- When accessing raster dimensions in Beast, use `rasterWidth()` on a `RasterMetadata` instance to obtain the horizontal pixel count.
- Ensure the `RasterMetadata` object is fully initialized from the source raster before calling this method.

### Prompt Snippet
```text
val metadata: RasterMetadata = ... // obtained from raster source
val width = metadata.rasterWidth()
```

### Common Failure Modes
- **Null Reference:** Calling `rasterWidth()` on an uninitialized or null `RasterMetadata` object will throw a `NullPointerException`.
- **Metadata Mismatch:** If the metadata does not correctly reflect the underlying raster file (e.g., due to manual construction), the returned width may not match the actual pixel data, leading to errors in subsequent `raptorJoin` or `mapPixels` operations.
- **Compatibility:** Ensure that if you are performing `overlay` operations, the `rasterWidth` matches the target raster; otherwise, use `reshape` to align the metadata before processing.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the raster RDD or file header
val metadata = rasterRDD.metadata
val width = metadata.rasterWidth()
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.tileIDs`

### Signature
```scala
[]:Iterator[Int]
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:91_

### Goal
Retrieves an iterator over the unique identifiers of the tiles associated with the current `RasterMetadata` instance.

### Parameters
_None._

### Input
The caller must have an initialized `RasterMetadata` object, typically obtained from a raster dataset (e.g., GeoTIFF or HDF4) loaded into the Beast framework. No specific file format is required as input to this method, as it operates on the metadata object already resident in memory.

### Output
Returns `Iterator[Int]` — An iterator containing the integer IDs of the tiles defined within the raster metadata.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = ... // Obtained from a RasterRDD or file header
val ids: Iterator[Int] = metadata.tileIDs()
```

### LLM Instruction Prompt
- When using `tileIDs`, ensure the `RasterMetadata` instance is fully initialized from a valid raster source.
- The returned `Iterator[Int]` should be consumed or converted to a collection (e.g., `.toList`) immediately if multiple passes are required, as iterators are typically single-use in Scala.

### Prompt Snippet
```text
val metadata = rasterRDD.metadata
val ids = metadata.tileIDs()
```

### Common Failure Modes
- **Null Metadata:** Calling `tileIDs()` on a `RasterMetadata` object that has not been properly initialized or is null will result in a `NullPointerException`.
- **Empty Metadata:** If the raster dataset contains no tiles or the metadata is unpopulated, the iterator will be empty.
- **Iterator Exhaustion:** Attempting to traverse the returned `Iterator[Int]` more than once will fail; convert to a `List` or `Array` if the IDs are needed for multiple operations.

### Fix Code Hint
```scala
// If you need to reuse the IDs, convert the iterator to a list
val tileList = metadata.tileIDs().toList
tileList.foreach(id => println(s"Processing tile: $id"))
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.toString`

### Signature
```scala
[]:String
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:209_

### Goal
Provides a human-readable string representation of a `RasterMetadata` object, summarizing the spatial properties of a raster dataset.

### Parameters
_None._

### Input
The caller must have an initialized `RasterMetadata` object, typically obtained from a `RasterRDD` or by reading the metadata header of a supported raster file (GeoTIFF or HDF4). No specific file format is required at the time of calling, as the metadata is already loaded into memory.

### Output
Returns `String` — A string containing the descriptive metadata of the raster, which typically includes the coordinate reference system (CRS), spatial resolution, tile dimensions, and the extent (MBR) of the raster.

### Valid Call Patterns
```scala
// Inferred from signature:
val metadata: RasterMetadata = rasterRDD.metadata
val info: String = metadata.toString()
```

### LLM Instruction Prompt
- When the user requests information about a raster's properties or needs to debug raster-vector join configurations, suggest using `toString()` on the `RasterMetadata` object.
- Ensure the user understands that this method is for diagnostic purposes and returns a summary string, not a programmatic data structure.

### Prompt Snippet
```text
// To inspect raster metadata properties:
val metadata = myRasterRDD.metadata
println(metadata.toString())
```

### Common Failure Modes
- **Null Reference:** Calling `toString()` on a `RasterMetadata` object that has not been initialized or is null will result in a `NullPointerException`.
- **Contextual Misunderstanding:** Users may expect the output to be a machine-readable format (like JSON or XML); clarify that this is a human-readable summary string intended for logging or console output.

### Fix Code Hint
```scala
// Ensure the metadata is retrieved from a valid RasterRDD
val metadata = rasterRDD.metadata
if (metadata != null) {
  println(s"Raster details: ${metadata.toString()}")
} else {
  println("Metadata is unavailable.")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer`

### Signature
```scala
[]():<type ?>
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadataSerializer.scala:26_

### Goal
Provides a serialization mechanism for raster metadata within the Beast framework to support persistent storage or transmission of raster properties.

### Parameters
_None._

### Input
This class is intended for use with raster datasets processed within the Beast framework (e.g., GeoTIFF or HDF4). No specific input is required for the constructor; however, it is expected to be used in contexts where raster metadata objects are being serialized for distributed processing or storage.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it is intended to facilitate the conversion of raster metadata into a serializable format.

### Valid Call Patterns
```scala
// Inferred usage:
val serializer = new edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer()
```

### LLM Instruction Prompt
- When generating code involving `RasterMetadataSerializer`, ensure it is instantiated as a standard Scala class.
- Use this class only when handling metadata for `RasterRDD` or related raster structures in Beast.
- Do not assume specific return types for methods within this class as they are not provided in the authoritative API facts.

### Prompt Snippet
```text
val serializer = new edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer()
```

### Common Failure Modes
- **Instantiation Errors:** Attempting to use the class without proper import of `edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer`.
- **Context Mismatch:** Attempting to serialize metadata for non-raster data types, which may lead to runtime exceptions or serialization failures.
- **Missing Dependencies:** Failure to include `beast-spark` in the project's `pom.xml` or build configuration, preventing access to the `geolite` package.

### Fix Code Hint
```scala
import edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer

// Ensure the serializer is used within a Spark context where raster data is available
val serializer = new RasterMetadataSerializer()
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer.isImmutable`

### Signature
```scala
[]:Boolean
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadataSerializer.scala:53_

### Goal
Determines whether the current `RasterMetadataSerializer` instance is configured as immutable, indicating that its metadata state cannot be modified after initialization.

### Parameters
_None._

### Input
No input parameters are required. The caller must ensure that the `RasterMetadataSerializer` instance has been properly instantiated within the context of a raster processing workflow.

### Output
Returns `Boolean` — `true` if the serializer is immutable, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val serializer = new edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer()
val isReadOnly = serializer.isImmutable
```

### LLM Instruction Prompt
- When generating code involving `RasterMetadataSerializer`, use `isImmutable` to check the mutability state of the serializer before attempting any configuration changes.
- Do not pass any arguments to this method as it takes zero parameters.

### Prompt Snippet
```text
val serializer = new edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer()
if (!serializer.isImmutable) {
  // Perform configuration
}
```

### Common Failure Modes
- **Null Pointer Exception:** Calling this method on an uninitialized or null `RasterMetadataSerializer` reference.
- **Assumption of Mutability:** Attempting to modify metadata on a serializer instance where `isImmutable` returns `true`, which may lead to runtime exceptions depending on the specific implementation of the serializer.

### Fix Code Hint
```scala
// Ensure the serializer is mutable before applying changes
if (!serializer.isImmutable) {
  // Apply metadata updates here
} else {
  throw new IllegalStateException("Cannot modify immutable RasterMetadataSerializer")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer.read`

### Signature
```scala
[](kryo:Kryo,input:Input,klass:Class[RasterMetadata]):RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadataSerializer.scala:40_

### Goal
Deserializes a `RasterMetadata` object from a binary stream using the Kryo serialization framework to support efficient storage and transmission of raster spatial properties.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for the deserialization process.
- `input` (`Input`): The binary input stream containing the serialized `RasterMetadata` data.
- `klass` (`Class[RasterMetadata]`): The class type of the object being deserialized, used by Kryo for type identification.

### Input
The caller must provide a valid `Kryo` instance and an `Input` stream that contains data previously serialized by the corresponding `write` method of `RasterMetadataSerializer`. This operation assumes the input stream is positioned at the start of the serialized `RasterMetadata` record.

### Output
Returns `RasterMetadata` — An instance containing the spatial properties of a raster, such as its coordinate reference system (CRS), resolution, and tile dimensions.

### Valid Call Patterns
```scala
// Inferred usage pattern for custom Kryo deserialization
val serializer = new RasterMetadataSerializer()
val metadata = serializer.read(kryo, input, classOf[RasterMetadata])
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure the `Kryo` instance is properly initialized and the `Input` stream is correctly positioned.
- Always pass `classOf[RasterMetadata]` as the third argument to satisfy the signature requirements.
- This method is intended for low-level serialization tasks; ensure that the `RasterMetadata` object is compatible with the current Beast version (0.10.1).

### Prompt Snippet
```text
val metadata = new edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer().read(kryo, input, classOf[edu.ucr.cs.bdlab.beast.geolite.RasterMetadata])
```

### Common Failure Modes
- **KryoException:** Occurs if the `Input` stream does not contain valid serialized `RasterMetadata` data or if the stream is corrupted.
- **NullPointerException:** Occurs if the `kryo` or `input` arguments are null.
- **Version Mismatch:** If the serialized data was produced by a different version of the Beast framework, deserialization may fail or produce inconsistent metadata.

### Fix Code Hint
```scala
// Ensure the input stream is valid and the serializer is correctly instantiated
try {
  val metadata = new RasterMetadataSerializer().read(kryo, input, classOf[RasterMetadata])
} catch {
  case e: com.esotericsoftware.kryo.KryoException => 
    println("Failed to deserialize RasterMetadata: " + e.getMessage)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterMetadataSerializer.write`

### Signature
```scala
[](kryo:Kryo,output:Output,metadata:RasterMetadata):Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadataSerializer.scala:27_

### Goal
Serializes `RasterMetadata` objects into a binary stream using the Kryo serialization framework for efficient storage or network transmission within the Beast framework.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for managing the serialization process.
- `output` (`Output`): The Kryo `Output` stream where the serialized metadata bytes will be written.
- `metadata` (`RasterMetadata`): The raster metadata object containing spatial reference, resolution, and tiling information to be serialized.

### Input
The caller must provide a valid, initialized `Kryo` instance and an open `Output` stream. The `RasterMetadata` object should be fully populated with valid raster properties (e.g., CRS, dimensions, and tile size) as required by the Raptor processing pipeline.

### Output
Returns `Unit` — The function performs a side-effecting write operation to the provided `Output` stream.

### Valid Call Patterns
```scala
// Inferred usage pattern for custom Kryo serialization
val serializer = new RasterMetadataSerializer()
serializer.write(kryo, output, metadata)
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure the `Kryo` instance is properly configured for the Beast environment.
- Always ensure the `Output` stream is flushed or closed by the caller after the serialization process is complete.
- This method is intended for low-level serialization tasks; ensure that the `RasterMetadata` object is not null before invocation.

### Prompt Snippet
```text
// Example of using RasterMetadataSerializer to persist metadata
val kryo = new Kryo()
val output = new Output(new FileOutputStream("metadata.bin"))
val metadata: RasterMetadata = ... // existing metadata object
new RasterMetadataSerializer().write(kryo, output, metadata)
output.close()
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `metadata` object or the `output` stream is null.
- **KryoException**: Occurs if the `kryo` instance is not properly initialized or if the `output` stream is closed/inaccessible during the write operation.
- **Serialization Mismatch**: If the `RasterMetadata` structure changes, reading the serialized output with a different version of the serializer may result in data corruption.

### Fix Code Hint
```scala
// Ensure the output stream is valid before writing
if (output != null && metadata != null) {
  new RasterMetadataSerializer().write(kryo, output, metadata)
} else {
  throw new IllegalArgumentException("Metadata and Output stream must not be null")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.detectType`

### Signature
```scala
[](value:Any):DataType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:108_

### Goal
Determines the appropriate Spark `DataType` for a given input value to facilitate schema definition during raster data processing.

### Parameters
- `value` (`Any`): The input object or data sample for which the corresponding Spark `DataType` is to be inferred.

### Input
The function accepts any object (`Any`). It is typically used when inspecting raw pixel values or metadata attributes to ensure they align with the expected `RasterRDD[T]` type requirements (e.g., `IntegerType`, `FloatType`) before performing raster operations.

### Output
Returns `DataType` — The Spark SQL `DataType` that corresponds to the provided input value, used for defining schemas in Beast raster workflows.

### Valid Call Patterns
```scala
// Inferred from signature:
import edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper
val dataType = RasterSchemaHelper.detectType(sampleValue)
```

### LLM Instruction Prompt
- Use `RasterSchemaHelper.detectType` when you need to programmatically determine the Spark schema type for a raster-related value.
- Ensure the returned `DataType` is compatible with the expected pixel type of your `RasterRDD` (e.g., `FloatType` for `RasterRDD[Float]`).

### Prompt Snippet
```text
val schemaType = edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.detectType(myValue)
```

### Common Failure Modes
- **Type Mismatch:** Providing a value that does not map to a supported Spark `DataType` may result in unexpected schema inference, causing downstream `RasterRDD` operations to fail.
- **Null Inputs:** Passing `null` may lead to runtime exceptions depending on the underlying implementation of `detectType`.
- **Raster Compatibility:** If the detected type does not match the pixel type required by `sc.geoTiff[T]` or other raster loaders, the system will throw an error during execution. Always verify that the detected type matches the generic type `T` used in your `RasterRDD[T]`.

### Fix Code Hint
```scala
// Ensure the detected type matches the expected RasterRDD type
val detected = RasterSchemaHelper.detectType(pixelValue)
require(detected == FloatType, "RasterRDD[Float] requires FloatType pixels")
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.inferSchema`

### Signature
```scala
[](names:Array[String],values:Array[Any]):StructType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:101_

### Goal
Infers a Spark `StructType` schema by mapping a set of field names to their corresponding sample values, facilitating the creation of structured metadata for raster-related datasets.

### Parameters
- `names` (`Array[String]`): An array of strings representing the column or field names to be included in the schema.
- `values` (`Array[Any]`): An array of sample values corresponding to the `names` array, used to determine the appropriate Spark data types for each field.

### Input
The caller must provide two arrays of equal length. The `values` array should contain representative data objects (e.g., `Int`, `Float`, `String`) that reflect the intended data types for the resulting `StructType`.

### Output
Returns `StructType` — A Spark SQL schema definition derived from the provided sample values, suitable for use in creating DataFrames or defining RDD schemas within the Beast framework.

### Valid Call Patterns
```scala
import edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper
import org.apache.spark.sql.types.StructType

val names = Array("pixel_value", "band_id", "description")
val values = Array(125.5f, 1, "temperature_data")
val schema: StructType = RasterSchemaHelper.inferSchema(names, values)
```
*(Note: This call pattern is inferred from the signature as no explicit test-suite example was provided.)*

### LLM Instruction Prompt
- When generating code that uses `inferSchema`, ensure the `names` and `values` arrays are of the same length.
- Use this function when you need to programmatically define a schema for raster metadata or associated attribute data where the types are known from sample records.
- The returned `StructType` is compatible with standard Spark SQL operations.

### Prompt Snippet
```text
val schema = edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.inferSchema(Array("name1", "name2"), Array(val1, val2))
```

### Common Failure Modes
- **Array Mismatch:** Providing `names` and `values` arrays of different lengths will likely result in an `ArrayIndexOutOfBoundsException` or an incomplete schema.
- **Unsupported Types:** Passing `Any` values that do not map to standard Spark SQL types may cause runtime errors during schema inference or subsequent DataFrame operations.
- **Null Values:** If a value in the `values` array is `null`, the inference logic may fail to determine the correct type; ensure sample values are non-null instances of the target types.

### Fix Code Hint
```scala
// Ensure arrays are aligned and non-empty
val names = Array("id", "value")
val values = Array(1, 0.5f)
require(names.length == values.length, "Names and values must have the same length")
val schema = edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.inferSchema(names, values)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.readExternalObj`

### Signature
```scala
[](in:ObjectInput):StructType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:46_

### Goal
Deserializes a `StructType` schema definition from an `ObjectInput` stream, typically used for reconstructing raster metadata schemas within the Beast framework.

### Parameters
- `in` (`ObjectInput`): An input stream providing the serialized representation of a `StructType` object.

### Input
- **Data:** A serialized `StructType` object previously written to an `ObjectOutput` stream.
- **Preconditions:** The `ObjectInput` stream must be positioned at the start of the serialized `StructType` data. The stream must be valid and accessible.

### Output
Returns `StructType` — The reconstructed Spark SQL `StructType` schema representing the structure of the raster data or associated metadata.

### Valid Call Patterns
```scala
// Inferred from signature:
val schema = edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.readExternalObj(in)
```

### LLM Instruction Prompt
- When calling `edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.readExternalObj`, ensure the `ObjectInput` is properly initialized and points to a valid serialized schema. This method is intended for internal framework use during deserialization of raster metadata.

### Prompt Snippet
```text
val schema = edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.readExternalObj(inputStream)
```

### Common Failure Modes
- **EOFException:** Occurs if the `ObjectInput` stream is empty or does not contain a valid serialized `StructType`.
- **Stream Corruption:** If the data in the `ObjectInput` was not written by a compatible `writeExternal` method, deserialization will fail with an `IOException` or `ClassNotFoundException`.
- **Invalid State:** Attempting to read from a closed `ObjectInput` stream will result in an `IOException`.

### Fix Code Hint
```scala
import java.io.ObjectInput
import org.apache.spark.sql.types.StructType
import edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper

// Ensure the stream is open and contains the expected schema data
val schema: StructType = RasterSchemaHelper.readExternalObj(in)
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.readType`

### Signature
```scala
[](in:ObjectInput):DataType
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:68_

### Goal
Deserializes a `DataType` from an `ObjectInput` stream, facilitating the reconstruction of raster schema metadata during distributed processing.

### Parameters
- `in` (`ObjectInput`): An input stream containing serialized schema information, typically used during the reconstruction of raster metadata in Spark tasks.

### Input
The caller must provide an `ObjectInput` stream that has been previously populated with serialized `DataType` information. This is typically handled internally by Beast when reading raster metadata from distributed storage or shuffle partitions.

### Output
Returns `DataType` — The deserialized Spark `DataType` object representing the pixel type or schema structure of the raster data.

### Valid Call Patterns
```scala
// Inferred from signature:
val dataType = edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.readType(in)
```

### LLM Instruction Prompt
- Use `edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.readType` only when deserializing raster schema metadata from an `ObjectInput` stream.
- Ensure the `ObjectInput` provided is valid and corresponds to a previously serialized `DataType`.
- This method is a low-level utility; prefer higher-level `RasterRDD` or `geoTiff` entry points for standard raster loading.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper
// ... inside a deserialization context
val dataType = RasterSchemaHelper.readType(in)
```

### Common Failure Modes
- **EOFException**: Occurs if the `ObjectInput` stream is empty or does not contain a valid serialized `DataType`.
- **ClassCastException**: May occur if the stream contains data that does not conform to the expected `DataType` structure.
- **Serialization Mismatch**: If the `DataType` was serialized with a different version of the Beast framework, deserialization may fail or produce inconsistent results.

### Fix Code Hint
```scala
// Ensure the stream is properly initialized and points to the correct schema data
try {
  val dataType = edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.readType(in)
} catch {
  case e: Exception => println(s"Failed to read DataType: ${e.getMessage}")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper.writeExternalObj`

### Signature
```scala
[](schema:StructType,out:ObjectOutput):Unit
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterSchemaHelper.scala:34_

### Goal
Serializes a Spark `StructType` schema to an `ObjectOutput` stream to facilitate the persistence or transmission of raster metadata within the Beast framework.

### Parameters
- `schema` (`StructType`): The Spark SQL schema definition representing the structure of the raster data or associated metadata.
- `out` (`ObjectOutput`): The output stream (e.g., `ObjectOutputStream`) where the serialized schema information will be written.

### Input
The caller must provide a valid `org.apache.spark.sql.types.StructType` object and an initialized `java.io.ObjectOutput` stream. This method is typically used during custom serialization processes where raster metadata must be preserved alongside binary data.

### Output
Returns `Unit` — The method performs a side-effect by writing the serialized schema bytes to the provided output stream.

### Valid Call Patterns
```scala
// Inferred from signature:
import edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper
import java.io.ObjectOutput
import org.apache.spark.sql.types.StructType

RasterSchemaHelper.writeExternalObj(mySchema, objectOutputStream)
```

### LLM Instruction Prompt
- When generating code that uses `RasterSchemaHelper.writeExternalObj`, ensure the `schema` is a valid Spark `StructType` and the `out` parameter is an open, writable `ObjectOutput` instance. This method is intended for low-level serialization of raster schemas.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.beast.geolite.RasterSchemaHelper
// ...
RasterSchemaHelper.writeExternalObj(schema, out)
```

### Common Failure Modes
- **IOException**: Occurs if the `ObjectOutput` stream is closed, unreachable, or encounters a disk/network error during the write operation.
- **NullPointerException**: Occurs if either the `schema` or `out` arguments are passed as `null`.
- **Serialization Errors**: If the `StructType` contains complex or unsupported nested types that cannot be handled by the underlying serialization logic.

### Fix Code Hint
```scala
// Ensure the stream is flushed or closed after the write operation
try {
  RasterSchemaHelper.writeExternalObj(schema, out)
  out.flush()
} catch {
  case e: java.io.IOException => println(s"Failed to write schema: ${e.getMessage}")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.AbstractConvolutionTile.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTile.scala:113_

### Goal
Retrieves the `DataType` associated with the pixels or components processed by the `AbstractConvolutionTile` instance.

### Parameters
_None._

### Input
The caller must have an initialized instance of a class extending `AbstractConvolutionTile`. No specific file formats are required as input to this method, as it operates on the metadata of an existing tile object in memory.

### Output
Returns `DataType` — The data type (e.g., IntegerType, FloatType) representing the numerical precision and storage format of the tile's components.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: AbstractConvolutionTile = ... // initialized tile
val dataType: DataType = tile.componentType()
```

### LLM Instruction Prompt
- When calling `componentType`, ensure the `AbstractConvolutionTile` instance is fully initialized.
- Use the returned `DataType` to verify compatibility if performing subsequent raster operations or `raptorJoin` tasks that require matching pixel types.

### Prompt Snippet
```text
val tile: AbstractConvolutionTile = ...
val dtype = tile.componentType()
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `AbstractConvolutionTile` instance has not been properly initialized or is null.
- **Type Mismatch**: If the returned `DataType` is used to cast or process data, ensure it matches the expected type of the underlying raster data (e.g., `FloatType` for temperature data vs `IntegerType` for categorical land-cover data).

### Fix Code Hint
```scala
// Ensure the tile is not null before accessing metadata
if (tile != null) {
  val dtype = tile.componentType()
  println(s"Tile component type: $dtype")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.AbstractConvolutionTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTile.scala:44_

### Goal
Determines whether the pixel at the specified coordinate within a convolution tile is considered empty or null.

### Parameters
- `i` (`Int`): The row index of the pixel within the tile.
- `j` (`Int`): The column index of the pixel within the tile.

### Input
The caller must provide a valid instance of `AbstractConvolutionTile`. The indices `i` and `j` must be within the bounds of the tile dimensions. Preconditions include ensuring the tile has been properly initialized as part of a Raptor raster processing workflow.

### Output
Returns `Boolean` — `true` if the pixel at the given `(i, j)` coordinate is empty, and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: AbstractConvolutionTile = ...
val isPixelEmpty: Boolean = tile.isEmpty(i, j)
```

### LLM Instruction Prompt
- When generating code that uses `isEmpty`, ensure the `i` and `j` indices are validated against the tile's dimensions to prevent `ArrayIndexOutOfBoundsException`.
- Use this method to filter out NoData or empty pixels during custom convolution or pixel-level transformation operations.

### Prompt Snippet
```text
// Check if the pixel at (i, j) is empty before applying convolution logic
if (!tile.isEmpty(i, j)) {
  // Perform calculation
}
```

### Common Failure Modes
- **Index Out of Bounds:** Providing `i` or `j` values that exceed the tile's width or height will result in a runtime exception.
- **Uninitialized Tile:** Calling this method on a tile that has not been properly loaded or populated from a raster source (GeoTIFF/HDF4) may lead to unexpected behavior.

### Fix Code Hint
```scala
// Ensure indices are within bounds before calling
if (i >= 0 && i < tile.getHeight && j >= 0 && j < tile.getWidth) {
  val empty = tile.isEmpty(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.AbstractConvolutionTile.merge`

### Signature
```scala
[](other:AbstractConvolutionTile[T]):AbstractConvolutionTile[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTile.scala:77_

### Goal
Merges two `AbstractConvolutionTile` instances of the same type `T` into a single tile by combining their underlying raster data.

### Parameters
- `other` (`AbstractConvolutionTile[T]`): The secondary convolution tile to be merged into the current instance.

### Input
- **Data:** Two `AbstractConvolutionTile[T]` objects.
- **Preconditions:** Both tiles must share identical metadata, including spatial resolution, coordinate reference system (CRS), and tile dimensions. If tiles are not aligned, they must be processed via `reshape` or similar alignment operations prior to merging.
- **Type Selection:** The type parameter `T` must be consistent across both tiles (e.g., `Float`, `Int`).

### Output
Returns `AbstractConvolutionTile[T]` — A new `AbstractConvolutionTile` containing the merged pixel data from the original and the provided `other` tile.

### Valid Call Patterns
```scala
// Inferred from signature: merging two tiles of the same type
val mergedTile = tileA.merge(tileB)
```

### LLM Instruction Prompt
- When calling `merge`, ensure that the receiver and the `other` argument are both instances of `AbstractConvolutionTile` with the same generic type `T`.
- Verify that the spatial metadata (resolution, CRS, dimensions) of both tiles match before invoking this method to avoid runtime inconsistencies.

### Prompt Snippet
```text
// Ensure tiles are compatible before merging
val tileA: AbstractConvolutionTile[Float] = ...
val tileB: AbstractConvolutionTile[Float] = ...
val result = tileA.merge(tileB)
```

### Common Failure Modes
- **Metadata Mismatch:** Attempting to merge tiles with different resolutions or CRS will result in invalid raster operations or runtime errors.
- **Type Mismatch:** Passing an `AbstractConvolutionTile` with a different type parameter `T` (e.g., `Int` vs `Float`) will cause a compilation error.
- **Uninitialized Tiles:** Calling `merge` on a null or uninitialized tile instance will trigger a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure alignment before merging if metadata is unknown
val alignedTileB = tileB.reshape(tileA.metadata)
val merged = tileA.merge(alignedTileB)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.AbstractGeoTiffTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/AbstractGeoTiffTile.scala:35_

### Goal
Determines whether a specific pixel at the given coordinates within a GeoTIFF tile is considered empty or contains no valid data.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide an instance of `AbstractGeoTiffTile` (or a subclass). The coordinates `(i, j)` must be within the bounds of the tile dimensions. Precondition: The tile must be initialized and loaded into memory as part of a Raptor raster processing workflow.

### Output
Returns `Boolean` — `true` if the pixel at the specified location is empty (e.g., represents a NoData value or is outside the valid data area), and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature: checking if a pixel at (10, 20) is empty
val tile: AbstractGeoTiffTile = ...
val isPixelEmpty = tile.isEmpty(10, 20)
```

### LLM Instruction Prompt
- When using `isEmpty`, ensure the `i` and `j` indices are within the tile's width and height boundaries to avoid `ArrayIndexOutOfBoundsException`.
- Use this method to filter out NoData pixels during custom pixel-level transformations or when performing zonal statistics.

### Prompt Snippet
```text
// Example usage of isEmpty to filter valid pixels
val tile: AbstractGeoTiffTile = ...
if (!tile.isEmpty(x, y)) {
  val pixelValue = tile.getPixel(x, y)
  // process valid pixel
}
```

### Common Failure Modes
- **IndexOutOfBounds:** Providing `i` or `j` values that exceed the tile's dimensions will result in a runtime exception.
- **Uninitialized Tile:** Calling this method on a tile object that has not been properly loaded or is null will cause a `NullPointerException`.
- **Metadata Mismatch:** If the tile is part of a larger raster operation, ensure that the tile's coordinate system and resolution are aligned with the global raster metadata; otherwise, the interpretation of "empty" may be inconsistent with the rest of the dataset.

### Fix Code Hint
```scala
// Ensure bounds checking before calling isEmpty
if (i >= 0 && i < tile.getWidth && j >= 0 && j < tile.getHeight) {
  if (!tile.isEmpty(i, j)) {
    // Safe to access pixel
  }
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.CompactIntersectionsIterator`

### Signature
```scala
[](intersections:Iterator[scala.Tuple2[Long, CompactIntersections]]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/CompactIntersectionsIterator.scala:7_

### Goal
Provides an iterator interface to process and traverse compact intersection data between raster tiles and vector geometries within the Raptor framework.

### Parameters
- `intersections` (`Iterator[scala.Tuple2[Long, CompactIntersections]]`): An iterator containing tuples where the `Long` key typically represents a tile or feature identifier, and `CompactIntersections` contains the pre-calculated intersection data between raster pixels and vector features.

### Input
- **Data:** Requires an `Iterator` of `CompactIntersections` objects, which are typically generated during the `raptorJoin` process.
- **Preconditions:** The input must be derived from a valid Raptor join operation where raster and vector data have been spatially aligned.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it is intended to facilitate the sequential consumption of intersection records for downstream pixel-level analysis or zonal statistics.

### Valid Call Patterns
```scala
// Inferred usage pattern based on constructor signature
val iterator = new edu.ucr.cs.bdlab.raptor.CompactIntersectionsIterator(intersections)
```

### LLM Instruction Prompt
- When generating code using this class, ensure the `intersections` iterator is correctly typed as `Iterator[scala.Tuple2[Long, CompactIntersections]]`.
- This class is a low-level component of the Raptor engine; ensure that the input iterator is not empty and that the underlying `CompactIntersections` objects are properly initialized by a preceding `raptorJoin` operation.

### Prompt Snippet
```text
val intersections: Iterator[scala.Tuple2[Long, CompactIntersections]] = ...
val compactIterator = new edu.ucr.cs.bdlab.raptor.CompactIntersectionsIterator(intersections)
```

### Common Failure Modes
- **Null Pointer Exceptions:** Passing a null or uninitialized iterator to the constructor.
- **Type Mismatch:** Providing an iterator that does not match the `Tuple2[Long, CompactIntersections]` structure, often occurring if the Raptor join output is transformed incorrectly before reaching this iterator.
- **Empty Iterator:** Attempting to iterate over an empty set, which may cause unexpected behavior if the downstream logic assumes the presence of intersection data.

### Fix Code Hint
```scala
// Ensure the input is properly mapped from a Raptor join result
val raptorJoinResult: RDD[RaptorJoinFeature[Float]] = raster.raptorJoin(vector)
// Convert the RDD or collection to the required Iterator format before instantiation
val intersections = raptorJoinResult.toLocalIterator
val compactIterator = new edu.ucr.cs.bdlab.raptor.CompactIntersectionsIterator(intersections)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.CompactIntersectionsIterator.hasNext`

### Signature
```scala
[]:Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/CompactIntersectionsIterator.scala:40_

### Goal
Checks if there are additional intersection elements remaining in the `CompactIntersectionsIterator` during raster-vector join operations.

### Parameters
_None._

### Input
The caller must have an initialized `CompactIntersectionsIterator` instance. This iterator is typically used internally by the Raptor join process to traverse intersections between raster tiles and vector geometries. No specific file format is required as input to this method, as it operates on the in-memory state of the iterator.

### Output
Returns `Boolean` — `true` if there are more intersections to process, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val iterator: CompactIntersectionsIterator = ... // obtained from Raptor join process
if (iterator.hasNext) {
  val nextIntersection = iterator.next()
}
```

### LLM Instruction Prompt
- Use `hasNext` to guard calls to `next()` when iterating over `CompactIntersectionsIterator` objects.
- Do not assume the iterator is thread-safe; it is intended for sequential traversal of intersection data within a single Spark task.

### Prompt Snippet
```text
val iterator = ... // CompactIntersectionsIterator
while (iterator.hasNext) {
  val item = iterator.next()
  // process item
}
```

### Common Failure Modes
- **Concurrent Modification:** Calling `hasNext` or `next` while the underlying data structure is being modified by another process.
- **Exhausted Iterator:** Calling `next()` without checking `hasNext` may result in a `NoSuchElementException` if the iterator is already empty.
- **Initialization Errors:** Attempting to call `hasNext` on an uninitialized or null iterator reference.

### Fix Code Hint
```scala
// Always check hasNext before calling next()
if (iterator != null && iterator.hasNext) {
  val intersection = iterator.next()
  // ...
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.CompactIntersectionsIterator.moveToNextIntersections`

### Signature
```scala
[]:Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/CompactIntersectionsIterator.scala:25_

### Goal
Advances the internal state of the `CompactIntersectionsIterator` to the next set of intersections between raster tiles and vector geometries during a Raptor join operation.

### Parameters
_None._

### Input
The iterator must be initialized and associated with a valid Raptor join context where raster data and vector features have been spatially aligned. This method is typically called within an iteration loop over the intersection results.

### Output
Returns `Unit` — The method updates the internal pointer of the iterator to the next available intersection record; no value is returned.

### Valid Call Patterns
```scala
// Inferred usage pattern for the iterator
val iterator: CompactIntersectionsIterator = ... // initialized iterator
while (iterator.hasNext) {
  iterator.moveToNextIntersections()
  // process current intersection
}
```

### LLM Instruction Prompt
- When generating code that uses `CompactIntersectionsIterator`, ensure `moveToNextIntersections` is called within a loop controlled by `hasNext` to safely traverse the intersection set. Do not attempt to access intersection data before ensuring the iterator has been advanced or initialized.

### Prompt Snippet
```text
// Example of advancing the iterator
iterator.moveToNextIntersections()
```

### Common Failure Modes
- **Illegal State:** Calling `moveToNextIntersections` when `hasNext` is false, which may lead to index out-of-bounds errors or undefined behavior in the underlying iterator.
- **Uninitialized Iterator:** Attempting to call the method on an iterator that has not been properly linked to a raster-vector join result set.

### Fix Code Hint
```scala
if (iterator.hasNext) {
  iterator.moveToNextIntersections()
} else {
  // Handle end of iteration
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.CompactIntersectionsIterator.next`

### Signature
```scala
[]():scala.Tuple2[Long, PixelRange]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/CompactIntersectionsIterator.scala:42_

### Goal
Retrieves the next intersection element from the iterator, returning a tuple containing a feature identifier and its corresponding range of raster pixels.

### Parameters
_None._

### Input
The iterator must be initialized and contain remaining intersection data. This function is typically used within a `while` or `for` loop iterating over a `CompactIntersectionsIterator` instance generated during a Raptor join operation.

### Output
Returns `scala.Tuple2[Long, PixelRange]` — A tuple where the `Long` represents the unique identifier of the spatial feature, and the `PixelRange` represents the contiguous set of raster pixels that intersect with that feature.

### Valid Call Patterns
```scala
// Inferred from signature:
val iterator: CompactIntersectionsIterator = ...
while (iterator.hasNext) {
  val (featureId, pixelRange) = iterator.next()
  // Process featureId and pixelRange
}
```

### LLM Instruction Prompt
- Use this method only when iterating over a `CompactIntersectionsIterator` object.
- Always check `hasNext` before calling `next()` to avoid `NoSuchElementException`.
- The returned `PixelRange` is specific to the Raptor raster-vector join context and should be used for subsequent pixel-level extraction or zonal statistics.

### Prompt Snippet
```text
val (id, range) = iterator.next()
```

### Common Failure Modes
- **NoSuchElementException**: Occurs if `next()` is called when `hasNext` is false.
- **Iterator Exhaustion**: Attempting to reuse an iterator that has already been fully traversed.
- **Null Pointer**: If the `CompactIntersectionsIterator` instance was not properly initialized by the Raptor join process.

### Fix Code Hint
```scala
if (iterator.hasNext) {
  val result = iterator.next()
  // Handle result
} else {
  // Handle end of iteration
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.CompactIntersectionsTileBreaker`

### Signature
```scala
[](intersections:Iterator[scala.Tuple2[Int, Intersections]]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/CompactIntersectionsTileBreaker.scala:14_

### Goal
Processes an iterator of raster-vector intersections to consolidate and compact tile-based intersection data for efficient downstream Raptor join operations.

### Parameters
- `intersections` (`Iterator[scala.Tuple2[Int, Intersections]]`): An iterator containing tuples where the first element is an integer tile ID and the second is the corresponding `Intersections` object representing the spatial overlap between vector geometries and raster pixels.

### Input
- **Data:** Requires an `Iterator` of `(Int, Intersections)` pairs, typically generated during the initial stages of a `raptorJoin` operation.
- **Preconditions:** The input `Intersections` objects must be derived from spatially aligned raster and vector datasets. Ensure that the raster data has been properly partitioned or indexed if processing in a distributed Spark environment.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it is expected to return a compacted representation of the intersection data suitable for further pixel-level processing or aggregation.

### Valid Call Patterns
```scala
// Inferred call pattern based on signature
val breaker = new edu.ucr.cs.bdlab.raptor.CompactIntersectionsTileBreaker()
val result = breaker.apply(intersectionsIterator)
```

### LLM Instruction Prompt
- When using `CompactIntersectionsTileBreaker`, ensure the input is an `Iterator` of `Tuple2[Int, Intersections]`.
- This class is intended for internal Raptor processing; ensure that the `Intersections` objects are correctly initialized from the `raptorJoin` workflow before passing them to this breaker.

### Prompt Snippet
```text
val intersections: Iterator[Tuple2[Int, Intersections]] = ... // obtained from raptorJoin
val breaker = new edu.ucr.cs.bdlab.raptor.CompactIntersectionsTileBreaker()
val compacted = breaker.apply(intersections)
```

### Common Failure Modes
- **Type Mismatch:** Passing an iterator that does not strictly match the `Tuple2[Int, Intersections]` type signature.
- **Empty Iterator:** Providing an empty iterator may result in unexpected behavior or null returns depending on the internal implementation of the `apply` method.
- **Memory Constraints:** Processing extremely large iterators without proper Spark partitioning may lead to `OutOfMemoryError` if the compaction process attempts to materialize the entire iterator in memory.

### Fix Code Hint
```scala
// Ensure the iterator is properly typed and non-empty before processing
if (intersections.hasNext) {
  val breaker = new edu.ucr.cs.bdlab.raptor.CompactIntersectionsTileBreaker()
  breaker.apply(intersections)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.CompactIntersectionsTileBreaker.hasNext`

### Signature
```scala
[]:Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/CompactIntersectionsTileBreaker.scala:40_

### Goal
Checks if there are additional intersection segments remaining to be processed within the current tile during a Raptor raster-vector join operation.

### Parameters
_None._

### Input
The caller must have an initialized `CompactIntersectionsTileBreaker` instance. This class is used internally by the Raptor framework to iterate through intersections between vector geometries and raster tiles. No specific file format is required as input to this method, as it operates on the in-memory state of the tile breaker.

### Output
Returns `Boolean` — `true` if there are more intersections to process in the current sequence, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature: usage within a loop
val breaker = new CompactIntersectionsTileBreaker(...)
while (breaker.hasNext) {
  val intersection = breaker.next()
  // process intersection
}
```

### LLM Instruction Prompt
- Use `hasNext` as a guard condition for a `while` loop when iterating over `CompactIntersectionsTileBreaker`.
- Do not call `next()` without first verifying `hasNext` returns `true` to avoid `NoSuchElementException`.
- This method is intended for internal Raptor processing workflows; ensure the `CompactIntersectionsTileBreaker` is properly initialized with valid raster and vector geometry data before iteration.

### Prompt Snippet
```text
val breaker: CompactIntersectionsTileBreaker = ...
if (breaker.hasNext) {
  // Proceed with iteration
}
```

### Common Failure Modes
- **Uninitialized State:** Calling `hasNext` on a breaker that has not been correctly initialized with spatial data may lead to unexpected behavior or runtime exceptions.
- **Concurrent Modification:** Modifying the underlying raster or vector data structures while the `CompactIntersectionsTileBreaker` is iterating will invalidate the iterator state.
- **Exhausted Iterator:** Calling `next()` after `hasNext` returns `false` will throw an exception; always gate calls to `next()` with `hasNext`.

### Fix Code Hint
```scala
// Ensure the breaker is checked before accessing elements
if (breaker.hasNext) {
  val item = breaker.next()
  // ...
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.CompactIntersectionsTileBreaker.moveToNextIntersections`

### Signature
```scala
[]:Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/CompactIntersectionsTileBreaker.scala:26_

### Goal
Advances the internal state of the `CompactIntersectionsTileBreaker` to the next set of intersections between raster tiles and vector geometries during a Raptor join operation.

### Parameters
_None._

### Input
The caller must have an initialized instance of `CompactIntersectionsTileBreaker`. This method assumes that the underlying intersection data has been loaded or prepared within the instance, typically as part of the Raptor raster-vector join workflow.

### Output
Returns `Unit` — The method performs an in-place update of the internal state of the `CompactIntersectionsTileBreaker` instance to point to the subsequent intersection record.

### Valid Call Patterns
```scala
// Inferred from signature:
val breaker: CompactIntersectionsTileBreaker = ... // initialized instance
breaker.moveToNextIntersections()
```

### LLM Instruction Prompt
- When using `CompactIntersectionsTileBreaker`, ensure the instance is properly initialized before calling `moveToNextIntersections`.
- This method is intended for internal iteration during raster-vector processing; do not call it if the iterator has reached the end of the intersection set.

### Prompt Snippet
```text
// Example of advancing the tile breaker state
breaker.moveToNextIntersections()
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `CompactIntersectionsTileBreaker` instance is not properly initialized or if the underlying data structures are null.
- **State Exhaustion**: Calling this method after all intersections have been processed may lead to undefined behavior or errors depending on the internal implementation of the iterator.
- **Context Mismatch**: Attempting to use this method outside of a valid Raptor join context where the tile breaker has not been populated with intersection data.

### Fix Code Hint
```scala
// Ensure the breaker is in a valid state before advancing
if (breaker.hasNext) {
  breaker.moveToNextIntersections()
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.CompactIntersectionsTileBreaker.next`

### Signature
```scala
[]():scala.Tuple2[Long, CompactIntersections]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/CompactIntersectionsTileBreaker.scala:42_

### Goal
Retrieves the next available tile index and its corresponding `CompactIntersections` object during the raster-vector intersection breaking process.

### Parameters
_None._

### Input
The caller must ensure the `CompactIntersectionsTileBreaker` instance has been properly initialized with the necessary raster and vector datasets. This method is typically used within an iterator-like pattern to traverse intersections partitioned by tile.

### Output
Returns `scala.Tuple2[Long, CompactIntersections]` — A tuple where the `Long` represents the unique tile ID and the `CompactIntersections` object contains the spatial intersection data for that specific tile.

### Valid Call Patterns
```scala
// Inferred from signature:
val (tileID, intersections) = tileBreaker.next()
```

### LLM Instruction Prompt
- When generating code that uses `CompactIntersectionsTileBreaker.next`, ensure it is called within a loop or iterator context that checks for the existence of further elements (e.g., `hasNext`).
- Do not assume the existence of parameters; this method takes no arguments.
- Always handle the returned `Tuple2` by destructuring it into the tile ID and the `CompactIntersections` object.

### Prompt Snippet
```text
val tileBreaker: CompactIntersectionsTileBreaker = ...
while (tileBreaker.hasNext) {
  val (tileID, intersections) = tileBreaker.next()
  // Process intersections for tileID
}
```

### Common Failure Modes
- **NoSuchElementException**: Calling `next()` when `hasNext` is false. Always verify the state of the iterator before invocation.
- **Uninitialized State**: Attempting to call `next()` on a `CompactIntersectionsTileBreaker` that has not been correctly configured with raster/vector data, leading to runtime errors during the intersection calculation.

### Fix Code Hint
```scala
// Ensure the breaker is checked before calling next()
if (tileBreaker.hasNext) {
  val result = tileBreaker.next()
  val tileID = result._1
  val intersections = result._2
  // Perform operations
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ConvolutionTileMultiBand`

### Signature
```scala
[](tileID:Int,metadata:RasterMetadata,rasterFeature:RasterFeature,numComponents:Int,w:Int,weights:Array[Float],stid:Int,values:Array[Float],exists:BitArray):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTile.scala:143_

### Goal
Performs a multi-band convolution operation on a specific raster tile by applying a set of weights to pixel values to support advanced raster filtering and transformation within the Raptor framework.

### Parameters
- `tileID` (`Int`): The unique identifier for the raster tile being processed.
- `metadata` (`RasterMetadata`): The metadata object containing spatial reference, resolution, and tiling information for the raster.
- `rasterFeature` (`RasterFeature`): The feature object representing the raster data structure.
- `numComponents` (`Int`): The number of bands or components present in the multi-band raster.
- `w` (`Int`): The width of the convolution kernel/window.
- `weights` (`Array[Float]`): The array of floating-point values representing the convolution kernel weights.
- `stid` (`Int`): The spatial tile identifier used for indexing the operation.
- `values` (`Array[Float]`): The raw pixel values for the tile, flattened into a single array.
- `exists` (`BitArray`): A bitmask indicating which pixels are valid or exist within the tile.

### Input
The caller must provide a valid `RasterMetadata` object and a corresponding `RasterFeature` that matches the input raster's geometry and CRS. The `values` array must be correctly sized to accommodate the tile dimensions and the number of components. Precondition: The raster must be properly partitioned and aligned; use `reshape` if metadata does not match expected tiling.

### Output
Returns `unspecified` — The function returns the result of the convolution operation applied to the multi-band raster tile.

### Valid Call Patterns
```scala
// Example inferred from signature (not verified)
val result = new edu.ucr.cs.bdlab.raptor.ConvolutionTileMultiBand(
  tileID, metadata, rasterFeature, numComponents, w, weights, stid, values, exists
)
```

### LLM Instruction Prompt
- When calling `ConvolutionTileMultiBand`, ensure that `numComponents` matches the structure of the `values` array.
- The `weights` array must correspond to the kernel size `w` defined for the convolution.
- Always verify that the `exists` `BitArray` matches the dimensions of the tile to avoid index out-of-bounds errors during pixel processing.

### Prompt Snippet
```text
// Ensure the convolution kernel weights are normalized if required by the specific filter type
val weights: Array[Float] = Array(0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f)
val result = new edu.ucr.cs.bdlab.raptor.ConvolutionTileMultiBand(tileID, metadata, rasterFeature, numComponents, 3, weights, stid, values, exists)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not align with the `RasterFeature` will cause processing errors.
- **Array Size Mismatch:** If the `values` array length does not match the expected tile size (width * height * numComponents), the operation will fail.
- **Invalid BitArray:** Providing an `exists` `BitArray` with a length inconsistent with the tile dimensions will result in runtime exceptions.

### Fix Code Hint
```scala
// Ensure metadata alignment before convolution
val alignedRaster = inputRaster.reshape(targetMetadata)
// Proceed with convolution using aligned metadata
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ConvolutionTileMultiBand.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):Array[Float]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTile.scala:163_

### Goal
Retrieves the multi-band pixel values at a specific coordinate within a `ConvolutionTileMultiBand` raster tile.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must have an initialized `ConvolutionTileMultiBand` object. This method assumes the tile has been populated with raster data (e.g., from GeoTIFF or HDF4 sources). The indices `i` and `j` must be within the bounds of the tile dimensions; otherwise, an `ArrayIndexOutOfBoundsException` may occur.

### Output
Returns `Array[Float]` — An array where each element corresponds to the value of a specific band at the requested pixel location.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: ConvolutionTileMultiBand = ...
val pixelBands: Array[Float] = tile.getPixelValue(i, j)
```

### LLM Instruction Prompt
- When using `getPixelValue`, ensure the `i` and `j` coordinates are validated against the tile's width and height to prevent runtime exceptions.
- The returned `Array[Float]` contains values for all bands present in the multi-band raster; ensure your logic accounts for the array length matching the expected band count.

### Prompt Snippet
```text
// Accessing multi-band pixel data at (i, j)
val values = tile.getPixelValue(i, j)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if the provided `i` or `j` values fall outside the tile's spatial dimensions.
- **NullPointerException**: Occurs if the `ConvolutionTileMultiBand` instance has not been properly initialized or the underlying data buffer is null.
- **Type Mismatch**: Attempting to interpret the `Array[Float]` as a single value without accounting for the multi-band structure.

### Fix Code Hint
```scala
// Bounds-checked access example
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  val bands = tile.getPixelValue(i, j)
  // Process bands...
} else {
  // Handle out-of-bounds
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ConvolutionTileSerializer`

### Signature
```scala
[]():<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTileSerializer.scala:30_

### Goal
Provides a serialization mechanism for raster tiles processed within the Raptor framework, specifically intended for handling convolution-based operations on spatial data.

### Parameters
_None._

### Input
This class is intended for use within the Raptor raster-vector processing pipeline. It expects to operate on raster data structures (such as `ITile[T]`) that have been prepared for convolution operations. Preconditions include ensuring that the raster data is correctly partitioned and that the pixel type matches the expected input for the convolution kernel.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it typically facilitates the serialization of tile data for distributed processing or storage within the Spark/Beast ecosystem.

### Valid Call Patterns
```scala
// Inferred usage:
val serializer = new edu.ucr.cs.bdlab.raptor.ConvolutionTileSerializer()
```

### LLM Instruction Prompt
- When generating code involving `ConvolutionTileSerializer`, ensure it is instantiated within a context where raster tiles are being processed or serialized for distributed tasks.
- Do not assume specific return types for this constructor; treat the instance as a utility for tile serialization within the Raptor pipeline.

### Prompt Snippet
```text
val serializer = new edu.ucr.cs.bdlab.raptor.ConvolutionTileSerializer()
```

### Common Failure Modes
- **Instantiation Errors:** Attempting to use the serializer outside of a Spark-compatible environment where the necessary raster metadata is unavailable.
- **Type Mismatch:** Providing tile data that does not conform to the expected pixel types (e.g., `FloatType` vs `IntegerType`) required by the underlying convolution logic.
- **Missing Context:** Attempting to serialize tiles before they have been properly partitioned or aligned via `reshape` or `spatialPartition` operations.

### Fix Code Hint
```scala
// Ensure the raster is properly prepared before serialization
val reshapedRaster = inputRaster.reshape(targetMetadata)
val serializer = new edu.ucr.cs.bdlab.raptor.ConvolutionTileSerializer()
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ConvolutionTileSerializer.read`

### Signature
```scala
[](kryo:Kryo,input:Input,klass:Class[AbstractConvolutionTile[Any]]):AbstractConvolutionTile[Any]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTileSerializer.scala:50_

### Goal
Deserializes an `AbstractConvolutionTile` from a binary stream using the Kryo serialization framework within the Raptor raster processing pipeline.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for object graph serialization and deserialization.
- `input` (`Input`): The binary input stream containing the serialized tile data.
- `klass` (`Class[AbstractConvolutionTile[Any]]`): The class type of the convolution tile to be instantiated.

### Input
The function requires a valid `Input` stream previously populated by a corresponding `write` operation from a `ConvolutionTileSerializer`. The input must represent a serialized `AbstractConvolutionTile` compatible with the provided `klass`.

### Output
Returns `AbstractConvolutionTile[Any]` — An instance of the deserialized convolution tile, ready for use in raster pixel-level transformations or spatial join operations.

### Valid Call Patterns
```scala
// Inferred usage pattern for custom Kryo serialization
val serializer = new edu.ucr.cs.bdlab.raptor.ConvolutionTileSerializer()
val tile = serializer.read(kryo, input, classOf[AbstractConvolutionTile[Any]])
```

### LLM Instruction Prompt
- When generating code that uses this API, ensure the `kryo` instance is properly initialized and the `input` stream is positioned at the start of the serialized tile data.
- Always pass the correct `Class` object corresponding to the specific implementation of `AbstractConvolutionTile` being deserialized.

### Prompt Snippet
```text
val serializer = new edu.ucr.cs.bdlab.raptor.ConvolutionTileSerializer()
val tile = serializer.read(kryo, input, classOf[AbstractConvolutionTile[Any]])
```

### Common Failure Modes
- **KryoException**: Occurs if the `input` stream is corrupted, incomplete, or does not match the expected binary format of the `AbstractConvolutionTile`.
- **ClassCastException**: Occurs if the `klass` parameter does not match the actual type of the object stored in the `input` stream.
- **NullPointerException**: Occurs if the `kryo` or `input` instances are not initialized before being passed to the method.

### Fix Code Hint
```scala
// Ensure the input stream is valid and the class type matches the serialized data
try {
  val tile = serializer.read(kryo, input, classOf[AbstractConvolutionTile[Any]])
} catch {
  case e: com.esotericsoftware.kryo.KryoException => 
    println("Failed to deserialize tile: " + e.getMessage)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ConvolutionTileSerializer.write`

### Signature
```scala
[](kryo:Kryo,output:Output,tile:AbstractConvolutionTile[Any]):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTileSerializer.scala:32_

### Goal
Serializes an `AbstractConvolutionTile` instance into a binary format using the Kryo serialization framework for efficient storage or network transmission within Spark-based raster workflows.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for object graph serialization.
- `output` (`Output`): The Kryo `Output` stream where the serialized tile data will be written.
- `tile` (`AbstractConvolutionTile[Any]`): The specific convolution tile object to be serialized.

### Input
The caller must provide a valid, initialized `Kryo` instance and an open `Output` stream. The `tile` must be an instance of `AbstractConvolutionTile[Any]`. This function is typically invoked within a custom Kryo `Serializer` implementation for raster data types in Beast.

### Output
Returns `Unit` — The function performs a side-effecting write operation to the provided `Output` stream; no value is returned.

### Valid Call Patterns
```scala
// Inferred usage within a custom Serializer class
val serializer = new ConvolutionTileSerializer()
serializer.write(kryo, output, tile)
```

### LLM Instruction Prompt
- When generating code that uses this function, ensure the `kryo` and `output` objects are properly initialized within the context of a Spark serialization routine.
- Always ensure the `tile` object is not null before passing it to the `write` method.
- This method is intended for internal framework serialization; do not attempt to use it for general-purpose file I/O.

### Prompt Snippet
```text
// Example of using ConvolutionTileSerializer.write within a custom serializer
val serializer = new edu.ucr.cs.bdlab.raptor.ConvolutionTileSerializer()
serializer.write(kryo, output, tile)
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `kryo`, `output`, or `tile` arguments are null.
- **KryoException**: Occurs if the `output` stream is closed, full, or if the `tile` contains data structures that cannot be serialized by the current Kryo configuration.
- **Serialization Mismatch**: Attempting to write a tile type that is incompatible with the `AbstractConvolutionTile` hierarchy.

### Fix Code Hint
```scala
// Ensure the output stream is valid before writing
if (output != null && tile != null) {
  new edu.ucr.cs.bdlab.raptor.ConvolutionTileSerializer().write(kryo, output, tile)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ConvolutionTileSingleBand`

### Signature
```scala
[](tileID:Int,metadata:RasterMetadata,rasterFeature:RasterFeature,w:Int,weights:Array[Float],stid:Int,values:Array[Float],exists:BitArray):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTile.scala:117_

### Goal
Performs a single-band convolution operation on a specific raster tile by applying a weighted kernel to the provided pixel values.

### Parameters
- `tileID` (`Int`): The unique identifier for the raster tile being processed.
- `metadata` (`RasterMetadata`): The metadata object containing spatial reference and grid information for the raster.
- `rasterFeature` (`RasterFeature`): The feature context associated with the raster data.
- `w` (`Int`): The width of the convolution kernel (e.g., 3 for a 3x3 kernel).
- `weights` (`Array[Float]`): A flat array containing the convolution kernel weights.
- `stid` (`Int`): The source tile ID or index identifier.
- `values` (`Array[Float]`): The raw pixel values of the tile to be convolved.
- `exists` (`BitArray`): A bitmask indicating which pixels are valid/present in the tile.

### Input
The caller must provide a valid `RasterMetadata` object and a corresponding `BitArray` to define the spatial extent and validity of the pixels. The `values` array must correspond to the dimensions defined in the metadata. This function is typically used within the Raptor framework for pixel-level transformations.

### Output
Returns `unspecified` — The function returns the result of the convolution operation applied to the input pixel values.

### Valid Call Patterns
```scala
// Inferred call pattern based on constructor signature
new edu.ucr.cs.bdlab.raptor.ConvolutionTileSingleBand(tileID, metadata, rasterFeature, w, weights, stid, values, exists)
```

### LLM Instruction Prompt
- When calling `ConvolutionTileSingleBand`, ensure that the `weights` array size matches the square of `w` (e.g., `w*w`).
- Ensure the `values` array length is consistent with the tile dimensions specified in `metadata`.
- The `exists` `BitArray` must be initialized to match the number of pixels in the tile to prevent index out-of-bounds errors during convolution.

### Prompt Snippet
```text
val convolution = new edu.ucr.cs.bdlab.raptor.ConvolutionTileSingleBand(tileID, metadata, rasterFeature, w, weights, stid, values, exists)
```

### Common Failure Modes
- **Dimension Mismatch:** Providing a `weights` array that does not match the kernel width `w` will cause runtime errors.
- **Metadata Incompatibility:** Using `metadata` that does not align with the `values` array dimensions will lead to incorrect spatial calculations or array index exceptions.
- **Null BitArray:** Passing a null or incorrectly sized `exists` `BitArray` will cause the convolution logic to fail when checking pixel validity.

### Fix Code Hint
```scala
// Ensure weights are normalized and dimensions are correct
val w = 3
val weights = Array(1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9)
val result = new edu.ucr.cs.bdlab.raptor.ConvolutionTileSingleBand(tileID, metadata, rasterFeature, w, weights, stid, values, exists)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ConvolutionTileSingleBand.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):Float
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTile.scala:135_

### Goal
Retrieves the floating-point value of a specific pixel at the given coordinate indices within a single-band convolution tile.

### Parameters
- `i` (`Int`): The row index (vertical coordinate) of the pixel within the tile.
- `j` (`Int`): The column index (horizontal coordinate) of the pixel within the tile.

### Input
The caller must provide an instance of `ConvolutionTileSingleBand`. This method assumes the tile has been initialized and populated with raster data (e.g., from GeoTIFF or HDF4 sources). The indices `i` and `j` must be within the bounds of the tile dimensions; otherwise, an `ArrayIndexOutOfBoundsException` may occur.

### Output
Returns `Float` — The pixel value at the specified location, represented as a single-precision floating-point number.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: ConvolutionTileSingleBand = ...
val pixelValue: Float = tile.getPixelValue(row, col)
```

### LLM Instruction Prompt
- When using `getPixelValue`, ensure the `i` and `j` parameters are within the valid range of the tile's dimensions.
- This method is specific to `ConvolutionTileSingleBand` and returns a `Float` regardless of the underlying source raster's original bit depth (e.g., Byte, Short, or Int), as it performs the necessary conversion.

### Prompt Snippet
```text
val tile = ... // instance of ConvolutionTileSingleBand
val value = tile.getPixelValue(i, j)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if `i` or `j` are negative or exceed the tile's width/height boundaries.
- **NullPointerException**: Occurs if the `ConvolutionTileSingleBand` instance has not been properly initialized or the underlying data buffer is null.
- **Type Mismatch**: Attempting to call this on a multi-band tile or a different tile implementation that does not extend `ConvolutionTileSingleBand`.

### Fix Code Hint
```scala
// Ensure indices are within bounds before access
if (i >= 0 && i < tile.getHeight && j >= 0 && j < tile.getWidth) {
  val value = tile.getPixelValue(i, j)
} else {
  // Handle out-of-bounds error
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ConvolutionTileSingleBand.numComponents`

### Signature
```scala
[]:Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ConvolutionTile.scala:137_

### Goal
Returns the number of components (bands) present in the single-band convolution tile.

### Parameters
_None._

### Input
The caller must have an initialized instance of `ConvolutionTileSingleBand`. As this is a single-band implementation, the tile must be prepared for raster processing operations within the Raptor framework.

### Output
Returns `Int` — The count of components in the tile, which for `ConvolutionTileSingleBand` is typically 1.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile = new edu.ucr.cs.bdlab.raptor.ConvolutionTileSingleBand(...)
val count = tile.numComponents()
```

### LLM Instruction Prompt
- Use this method to verify the band count of a `ConvolutionTileSingleBand` object before performing pixel-level transformations or raster-vector joins.
- Do not assume the return value is always 1 if the object type is polymorphic; always check the specific instance.

### Prompt Snippet
```text
val tile: ConvolutionTileSingleBand = ...
val numBands = tile.numComponents()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling this method on an uninitialized or null `ConvolutionTileSingleBand` instance.
- **Type Mismatch:** Attempting to call this on a generic `ConvolutionTile` that is not specifically a `ConvolutionTileSingleBand` without proper casting or type checking.

### Fix Code Hint
```scala
// Ensure the tile is initialized before accessing metadata
if (tile != null) {
  val bands = tile.numComponents()
  // Proceed with processing
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.EmptyTile`

### Signature
```scala
[](tileID:Int,rasterMetadata:RasterMetadata,numComponents:Int,rasterFeature:RasterFeature):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/EmptyTile.scala:15_

### Goal
Initializes an empty raster tile structure within the Raptor framework, typically used as a placeholder or initialization step during raster-vector join operations.

### Parameters
- `tileID` (`Int`): The unique identifier for the tile within the raster dataset.
- `rasterMetadata` (`RasterMetadata`): The metadata object defining the spatial reference, resolution, and dimensions of the raster.
- `numComponents` (`Int`): The number of bands or components (e.g., 1 for grayscale, 3 for RGB) to allocate for the tile.
- `rasterFeature` (`RasterFeature`): The associated raster feature context required for spatial alignment.

### Input
The caller must provide a valid `RasterMetadata` object that matches the target raster's coordinate system and resolution. The `rasterFeature` must be compatible with the spatial context of the operation.

### Output
Returns an instance of `EmptyTile` (type unspecified in signature), representing a blank raster tile initialized according to the provided metadata and component count.

### Valid Call Patterns
```scala
// Example inferred from signature
val emptyTile = new edu.ucr.cs.bdlab.raptor.EmptyTile(tileID, rasterMetadata, numComponents, rasterFeature)
```

### LLM Instruction Prompt
- When calling `EmptyTile`, ensure that `rasterMetadata` is pre-validated against the target raster dataset to prevent spatial mismatch errors.
- The `numComponents` must match the expected band count of the raster processing pipeline.
- This constructor is intended for internal Raptor framework use; ensure it is called within a Spark transformation context where `RasterMetadata` is available.

### Prompt Snippet
```text
val tile = new edu.ucr.cs.bdlab.raptor.EmptyTile(id, metadata, bands, feature)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not align with the global raster grid, causing downstream `raptorJoin` operations to fail.
- **Component Mismatch:** Setting `numComponents` to a value inconsistent with the expected raster band count, leading to index out-of-bounds errors during pixel-level processing.
- **Null References:** Passing a null `RasterFeature` or `RasterMetadata` will result in a `NullPointerException` during tile initialization.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the source RasterRDD
val metadata = rasterRDD.metadata
val tile = new edu.ucr.cs.bdlab.raptor.EmptyTile(id, metadata, 1, feature)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.EmptyTile.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/EmptyTile.scala:23_

### Goal
Retrieves the `DataType` associated with an `EmptyTile`, identifying the underlying pixel data type (e.g., Integer, Float) that the tile represents despite containing no actual pixel values.

### Parameters
_None._

### Input
No input parameters are required. The caller must have an instance of `edu.ucr.cs.bdlab.raptor.EmptyTile`. This method is typically used when inspecting the metadata of a raster partition or tile that has been initialized as empty during a Raptor join or raster transformation workflow.

### Output
Returns `DataType` — The specific data type (e.g., `IntegerType`, `FloatType`) that the `EmptyTile` is configured to represent.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile = new edu.ucr.cs.bdlab.raptor.EmptyTile(...)
val dataType = tile.componentType
```

### LLM Instruction Prompt
- When generating code involving `EmptyTile`, use `componentType` to verify the expected pixel type before performing operations that assume a specific numeric format.
- Do not assume `EmptyTile` contains data; it is a placeholder used in Raptor workflows to represent missing or non-existent raster coverage.

### Prompt Snippet
```text
val tile: EmptyTile = ...
val pixelType = tile.componentType
```

### Common Failure Modes
- **Type Mismatch:** Attempting to perform pixel-level arithmetic on an `EmptyTile` without checking the `componentType` or ensuring the tile is replaced by a valid `ITile` containing data.
- **Contextual Misuse:** Calling this on an uninitialized `EmptyTile` reference, which will result in a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure the tile is not null before accessing metadata
if (tile != null) {
  val dtype = tile.componentType
  println(s"Tile is empty but expects data type: $dtype")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.EmptyTile.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):T
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/EmptyTile.scala:21_

### Goal
Retrieves the pixel value at a specific coordinate within an `EmptyTile`, which represents a raster tile containing no data or a uniform background value.

### Parameters
- `i` (`Int`): The row index (vertical coordinate) of the pixel within the tile.
- `j` (`Int`): The column index (horizontal coordinate) of the pixel within the tile.

### Input
The caller must have an instance of `EmptyTile[T]`. As this class represents an empty or placeholder tile in the Raptor framework, no specific file-based input is required to instantiate it, but it is typically used in raster processing workflows where spatial coverage is incomplete.

### Output
Returns `T` — The value associated with the pixel at the specified coordinates. For an `EmptyTile`, this typically returns the default "no-data" or background value defined for the raster type `T`.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile = new edu.ucr.cs.bdlab.raptor.EmptyTile[Float](width, height, noDataValue)
val value = tile.getPixelValue(i, j)
```

### LLM Instruction Prompt
- When using `getPixelValue` on an `EmptyTile`, assume the tile is a placeholder for missing data.
- Ensure the indices `i` and `j` are within the bounds of the tile dimensions to avoid potential runtime exceptions, although `EmptyTile` behavior is generally uniform across all coordinates.
- The type `T` must be consistent with the raster data type being processed in the broader `RasterRDD[T]` workflow.

### Prompt Snippet
```text
val tile = new edu.ucr.cs.bdlab.raptor.EmptyTile[Float](100, 100, -9999.0f)
val pixel = tile.getPixelValue(50, 50)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if the provided `i` or `j` coordinates exceed the defined dimensions of the tile.
- **Type Mismatch**: Attempting to use an `EmptyTile` with a type `T` that does not match the expected pixel type of the surrounding `RasterRDD` operations.
- **Logic Errors**: Assuming `EmptyTile` contains actual data from a source file; it is strictly a synthetic representation of empty space.

### Fix Code Hint
```scala
// Ensure indices are validated against tile dimensions before access
if (i >= 0 && i < tile.getHeight && j >= 0 && j < tile.getWidth) {
  val value = tile.getPixelValue(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.EmptyTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/EmptyTile.scala:18_

### Goal
Determines if a specific pixel coordinate within an `EmptyTile` instance is considered empty or null within the Raptor raster processing framework.

### Parameters
- `i` (`Int`): The row index of the pixel within the tile.
- `j` (`Int`): The column index of the pixel within the tile.

### Input
The caller must have an instance of `edu.ucr.cs.bdlab.raptor.EmptyTile`. This method is typically used during raster-vector join operations or pixel-level transformations where the system needs to verify if a tile position contains valid data or represents an empty/no-data region.

### Output
Returns `Boolean` — Returns `true` if the pixel at the specified `(i, j)` coordinate is empty, and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile = new edu.ucr.cs.bdlab.raptor.EmptyTile(...)
val isPixelEmpty = tile.isEmpty(i, j)
```

### LLM Instruction Prompt
- When generating code that uses `EmptyTile.isEmpty`, ensure the `i` and `j` indices are within the bounds of the tile dimensions.
- Use this method to check for empty pixels before performing arithmetic operations on raster data to avoid processing no-data values.

### Prompt Snippet
```text
// Check if pixel (i, j) is empty before processing
if (!tile.isEmpty(i, j)) {
  val pixelValue = tile.get(i, j)
  // ... process value
}
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if the provided `i` or `j` coordinates exceed the defined dimensions of the `EmptyTile`.
- **Incorrect Usage**: Calling this on a non-empty tile type; ensure the object is explicitly an `EmptyTile` or a compatible raster tile type that implements the `isEmpty` check.

### Fix Code Hint
```scala
// Ensure indices are validated against tile width and height
if (i >= 0 && i < tile.getHeight && j >= 0 && j < tile.getWidth) {
  val empty = tile.isEmpty(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ExplodedTile`

### Signature
```scala
[](tile:ITile[T]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ExplodedTile.scala:29_

### Goal
Transforms a single `ITile[T]` into an exploded representation, typically used to flatten or decompose raster tile data for pixel-level processing within the Raptor framework.

### Parameters
- `tile` (`ITile[T]`): The input raster tile of type `T` (e.g., `Float`, `Int`) to be processed.

### Input
- **Data:** An `ITile[T]` object representing a portion of a raster dataset (e.g., GeoTIFF or HDF4).
- **Preconditions:** The tile must be properly initialized and match the expected pixel type `T` of the raster workflow. Ensure the raster metadata is consistent with the overall `RasterRDD` structure.

### Output
Returns `unspecified` — The exploded representation of the tile, which facilitates granular access to individual pixel values or spatial attributes during raster-vector join operations.

### Valid Call Patterns
```scala
// Inferred from signature:
val exploded = new edu.ucr.cs.bdlab.raptor.ExplodedTile(tile)
```

### LLM Instruction Prompt
- When using `ExplodedTile`, ensure the input `tile` is an instance of `ITile[T]`.
- This class is part of the Raptor component; it is intended for use in pixel-level transformations or when decomposing tiles for spatial analysis.
- Always verify that the generic type `T` matches the underlying raster data type (e.g., `FloatType`, `IntegerType`) to avoid runtime type mismatches.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.ExplodedTile
import edu.ucr.cs.bdlab.raptor.ITile

// Example usage:
val tile: ITile[Float] = ... // Obtained from RasterRDD
val exploded = new ExplodedTile(tile)
```

### Common Failure Modes
- **Type Mismatch:** Providing a tile with a pixel type `T` that does not match the expected processing logic (e.g., passing an `Int` tile to a function expecting `Float`).
- **Null Input:** Passing a null or uninitialized `ITile` will result in a `NullPointerException`.
- **Metadata Inconsistency:** If the tile is part of a larger `RasterRDD`, ensure that the tile's metadata (resolution, CRS) is aligned with the rest of the dataset before explosion, as `ExplodedTile` does not perform automatic resampling or reprojection.

### Fix Code Hint
```scala
// Ensure the tile is valid and the type is explicitly defined
val tile: ITile[Float] = myRasterRDD.first() 
val exploded = new edu.ucr.cs.bdlab.raptor.ExplodedTile[Float](tile)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ExplodedTile.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):T
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ExplodedTile.scala:30_

### Goal
Retrieves the pixel value at a specific coordinate within an `ExplodedTile` instance during raster processing workflows.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide an initialized `ExplodedTile` object. The indices `i` and `j` must be within the bounds of the tile dimensions. This method is typically used during pixel-level transformations or custom raster analysis tasks within the Raptor framework.

### Output
Returns `T` — The value of the pixel at the specified location, where `T` corresponds to the underlying data type of the raster (e.g., `Float`, `Int`).

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: ExplodedTile[Float] = ...
val value: Float = tile.getPixelValue(i, j)
```

### LLM Instruction Prompt
- Use `getPixelValue(i, j)` to access individual pixel data from an `ExplodedTile`.
- Ensure `i` and `j` are within the valid range of the tile's dimensions to avoid index out-of-bounds errors.
- The return type `T` is generic and depends on the specific `ExplodedTile` instance type.

### Prompt Snippet
```text
val pixel = explodedTile.getPixelValue(col, row)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if the provided `i` or `j` coordinates fall outside the tile's defined width or height.
- **Type Mismatch**: Occurs if the expected return type `T` does not match the actual pixel data type stored in the `ExplodedTile`.
- **Uninitialized Tile**: Attempting to call `getPixelValue` on a null or improperly initialized `ExplodedTile` object.

### Fix Code Hint
```scala
// Ensure indices are within bounds before access
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  val value = tile.getPixelValue(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ExplodedTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ExplodedTile.scala:32_

### Goal
Checks if a specific pixel at the given coordinates within an `ExplodedTile` is empty or contains no valid data.

### Parameters
- `i` (`Int`): The row index of the pixel within the tile.
- `j` (`Int`): The column index of the pixel within the tile.

### Input
The caller must provide an instance of `ExplodedTile`. The indices `i` and `j` must be within the bounds of the tile dimensions.

### Output
Returns `Boolean` — `true` if the pixel at the specified `(i, j)` coordinate is empty (e.g., represents a NoData value), and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: ExplodedTile = ...
val isPixelEmpty: Boolean = tile.isEmpty(i, j)
```

### LLM Instruction Prompt
- When using `isEmpty`, ensure the `i` and `j` indices are validated against the tile's dimensions to avoid `ArrayIndexOutOfBoundsException`.
- Use this method to filter out NoData pixels during raster-vector join operations or pixel-level transformations.

### Prompt Snippet
```text
val tile: ExplodedTile = ...
val isEmpty = tile.isEmpty(row, col)
```

### Common Failure Modes
- **IndexOutOfBounds:** Providing `i` or `j` values that exceed the tile's width or height.
- **Null Reference:** Calling `isEmpty` on an uninitialized `ExplodedTile` object.
- **Logic Errors:** Assuming `isEmpty` checks for zero-value pixels; it specifically checks for the "empty" state defined by the tile's metadata (NoData).

### Fix Code Hint
```scala
// Ensure indices are within bounds before checking
if (i >= 0 && i < tile.height && j >= 0 && j < tile.width) {
  if (!tile.isEmpty(i, j)) {
    // Process valid pixel
  }
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.FilterTile`

### Signature
```scala
[](tile:ITile[T],filter:_root_.scala.Function1[T, Boolean]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/FilterTile.scala:27_

### Goal
Applies a predicate function to the pixels of an `ITile` to filter or evaluate raster data values within the Raptor framework.

### Parameters
- `tile` (`ITile[T]`): The input raster tile containing pixel data of type `T`.
- `filter` (`_root_.scala.Function1[T, Boolean]`): A predicate function that takes a pixel value of type `T` and returns `true` to retain or satisfy the condition, or `false` otherwise.

### Input
- **Data:** An `ITile[T]` object, typically derived from GeoTIFF or HDF4 raster sources processed via `RasterRDD`.
- **Preconditions:** The pixel type `T` must be consistent with the underlying raster data (e.g., `Float`, `Int`). Ensure the tile is properly loaded into memory or available as part of a `RasterRDD` partition.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; typically, this operation results in a processed tile or a boolean mask depending on the specific implementation of the `FilterTile` class.

### Valid Call Patterns
```scala
// Inferred usage pattern based on signature
val filteredTile = new edu.ucr.cs.bdlab.raptor.FilterTile(myTile, (pixel: Float) => pixel > 0.0f)
```

### LLM Instruction Prompt
- When using `FilterTile`, ensure the type parameter `T` of the `ITile` matches the input type expected by the `filter` function.
- This API is intended for pixel-level operations within the Raptor raster-vector processing pipeline.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.FilterTile
// ... inside a Spark transformation or map operation
val result = new FilterTile(tile, (v: Float) => v != noDataValue)
```

### Common Failure Modes
- **Type Mismatch:** Providing a filter function that expects a different type than the pixel type `T` of the `ITile`.
- **Null Tiles:** Passing a null `ITile` reference will result in a `NullPointerException`.
- **Incompatible Types:** Attempting to use this on non-raster RDD types; ensure the input is specifically an `ITile[T]`.

### Fix Code Hint
```scala
// Ensure the filter function matches the tile's pixel type
val myFilter: Float => Boolean = (v: Float) => v >= 0 && v <= 100
val processor = new edu.ucr.cs.bdlab.raptor.FilterTile(tile, myFilter)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.FilterTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/FilterTile.scala:30_

### Goal
Determines if a specific pixel at the given coordinates within a `FilterTile` is considered empty or null.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide a valid `FilterTile` instance. This method assumes the coordinates `(i, j)` are within the bounds of the tile dimensions.

### Output
Returns `Boolean` — `true` if the pixel at the specified `(i, j)` location is empty, and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred usage based on signature
val tile: FilterTile = ...
val isPixelEmpty: Boolean = tile.isEmpty(i, j)
```

### LLM Instruction Prompt
- When using `isEmpty`, ensure the `i` and `j` indices are within the bounds of the `FilterTile` to avoid `ArrayIndexOutOfBoundsException`.
- This method is typically used during raster-vector processing or pixel-level filtering workflows within the Raptor framework.

### Prompt Snippet
```text
val tile = ... // FilterTile instance
val isEmpty = tile.isEmpty(col, row)
```

### Common Failure Modes
- **Out of Bounds:** Providing `i` or `j` values that exceed the tile's width or height will result in a runtime exception.
- **Uninitialized Tile:** Calling this method on a null or uninitialized `FilterTile` reference will throw a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure bounds checking before calling isEmpty
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  val empty = tile.isEmpty(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffConstants.initializeTagNames`

### Signature
```scala
[]():Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffConstants.scala:165_

### Goal
Initializes the internal mapping of GeoTIFF tag identifiers to their respective string names within the Raptor raster processing framework.

### Parameters
_None._

### Input
No input parameters are required. This method is typically invoked during the initialization phase of the Raptor component to ensure that GeoTIFF metadata tags are correctly registered and accessible for subsequent raster processing tasks.

### Output
Returns `Unit` — The method performs a side-effecting operation to populate internal static structures; it does not return a value.

### Valid Call Patterns
```scala
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.GeoTiffConstants.initializeTagNames()
```

### LLM Instruction Prompt
- When generating code that involves low-level GeoTIFF metadata parsing or custom raster tag handling within the Raptor framework, ensure `edu.ucr.cs.bdlab.raptor.GeoTiffConstants.initializeTagNames()` is called during the application startup or before accessing GeoTIFF tag constants to avoid uninitialized state errors.

### Prompt Snippet
```text
// Ensure GeoTIFF constants are initialized before processing
edu.ucr.cs.bdlab.raptor.GeoTiffConstants.initializeTagNames()
```

### Common Failure Modes
- **Uninitialized Access:** Attempting to resolve GeoTIFF tag names before this method has been executed may result in missing metadata mappings or runtime exceptions when the framework attempts to interpret specific GeoTIFF headers.
- **Redundant Initialization:** While calling this method multiple times is generally safe due to its idempotent nature, it is unnecessary and may cause minor performance overhead if placed inside tight loops or frequently called Spark transformations.

### Fix Code Hint
```scala
// Ensure the GeoTiffConstants are ready for use
if (/* check if tags are not initialized */) {
  edu.ucr.cs.bdlab.raptor.GeoTiffConstants.initializeTagNames()
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffReader`

### Signature
```scala
[](filename:String):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffReader.scala:68_

### Goal
Provides a mechanism to read and parse GeoTIFF raster data files within the RDPro/Raptor framework for subsequent spatial processing.

### Parameters
- `filename` (`String`): The path to the GeoTIFF file on the local file system or distributed storage accessible by the Spark cluster.

### Input
- **Data Format:** GeoTIFF.
- **Preconditions:** The file must be a valid GeoTIFF. Ensure the file path is accessible to all Spark executors if running in a distributed environment.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; typically, this reader is used internally by Beast to initialize raster data structures for Raptor operations.

### Valid Call Patterns
```scala
// Example inferred from signature
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader("path/to/raster.tif")
```

### LLM Instruction Prompt
- When using `GeoTiffReader`, ensure the `filename` points to a valid GeoTIFF file. 
- Note that this API is a low-level component; for standard Spark workflows, prefer using `SparkContext` extensions like `sc.geoTiff[T]` which handle RDD creation and type-parameter selection (e.g., `FloatType`, `IntegerType`) automatically.

### Prompt Snippet
```text
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader(filename)
```

### Common Failure Modes
- **File Not Found:** Providing a path that does not exist or is inaccessible to the Spark driver/executors.
- **Format Mismatch:** Attempting to read non-GeoTIFF files or corrupted headers will result in runtime exceptions.
- **Type Mismatch:** If using this to feed into `RasterRDD`, ensure the pixel type matches the expected data depth (e.g., `Float` vs `Int`). Use `reshape` if metadata alignment is required for subsequent `raptorJoin` operations.

### Fix Code Hint
```scala
// If GeoTiffReader fails due to missing metadata or partitioning, 
// ensure the raster is properly indexed or reshaped before processing.
val raster = sc.geoTiff[Float]("path/to/raster.tif")
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffReader.close`

### Signature
```scala
[]():Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffReader.scala:205_

### Goal
Closes the `GeoTiffReader` instance, releasing any underlying file handles or system resources associated with the open GeoTIFF file.

### Parameters
_None._

### Input
The caller must have an active `GeoTiffReader` instance that has been previously initialized to read a GeoTIFF file. No specific file format preconditions are required beyond the reader being in an open state.

### Output
Returns `Unit` — The method performs a side-effect of closing the resource and returns no value.

### Valid Call Patterns
```scala
// Inferred from signature:
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader(path)
// ... perform read operations ...
reader.close()
```

### LLM Instruction Prompt
- When generating code that uses `GeoTiffReader`, ensure that `close()` is called to prevent resource leaks, preferably within a `finally` block or similar resource management pattern.
- Do not attempt to use the `GeoTiffReader` instance for further read operations after `close()` has been invoked.

### Prompt Snippet
```text
// Ensure resource cleanup after processing GeoTIFF
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader(tiffPath)
try {
  // ... process raster data ...
} finally {
  reader.close()
}
```

### Common Failure Modes
- **Double Closing:** Calling `close()` multiple times on the same instance may lead to unexpected behavior depending on the underlying implementation.
- **Use-After-Close:** Attempting to read pixel data or metadata from the `GeoTiffReader` after calling `close()` will result in an exception or runtime error.
- **Resource Leaks:** Failing to call `close()` in long-running Spark applications or batch jobs can lead to file descriptor exhaustion.

### Fix Code Hint
```scala
// Use a try-finally block to ensure the reader is closed even if an exception occurs
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader(path)
try {
  // Perform operations
} finally {
  reader.close()
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffReader.getRasterMetadata`

### Signature
```scala
[](fileSystem:FileSystem,path:Path):RasterMetadata
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffReader.scala:225_

### Goal
Retrieves the spatial and structural metadata (such as dimensions, CRS, and resolution) from a GeoTIFF file without loading the full raster data into memory.

### Parameters
- `fileSystem` (`FileSystem`): The Hadoop `FileSystem` instance used to access the storage layer where the raster file resides.
- `path` (`Path`): The Hadoop `Path` object pointing to the specific GeoTIFF file.

### Input
- **Data Format:** The target file must be a valid GeoTIFF.
- **Preconditions:** The `FileSystem` must be initialized and have read permissions for the provided `Path`. The file must be accessible via the Hadoop API.

### Output
Returns `RasterMetadata` — An object containing the raster's spatial extent, pixel dimensions, coordinate reference system (CRS), and tile configuration.

### Valid Call Patterns
```scala
// Inferred from signature:
val fs = FileSystem.get(sparkContext.hadoopConfiguration)
val path = new Path("hdfs:///data/raster.tif")
val metadata = edu.ucr.cs.bdlab.raptor.GeoTiffReader.getRasterMetadata(fs, path)
```

### LLM Instruction Prompt
- When calling `getRasterMetadata`, ensure the `FileSystem` is derived from the active Spark configuration to maintain consistency with the cluster environment.
- Always verify that the `Path` points to a valid GeoTIFF file before invocation to avoid `IOException` or format-parsing errors.

### Prompt Snippet
```text
val fs = FileSystem.get(sc.hadoopConfiguration)
val path = new Path("path/to/raster.tif")
val metadata = edu.ucr.cs.bdlab.raptor.GeoTiffReader.getRasterMetadata(fs, path)
```

### Common Failure Modes
- **FileNotFoundException:** Occurs if the provided `Path` does not exist or is inaccessible.
- **Invalid Format:** Thrown if the file at the specified path is not a valid GeoTIFF or is corrupted.
- **FileSystem Mismatch:** Using a `FileSystem` instance not compatible with the URI scheme of the `Path` (e.g., local vs. HDFS).

### Fix Code Hint
```scala
// Ensure the path is absolute and the filesystem is correctly initialized
val conf = sc.hadoopConfiguration
val path = new Path(inputPath)
val fs = path.getFileSystem(conf)
val metadata = edu.ucr.cs.bdlab.raptor.GeoTiffReader.getRasterMetadata(fs, path)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffReader.getSampleComponentType`

### Signature
```scala
[](sampleFormat:Int):DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffReader.scala:213_

### Goal
Maps a GeoTIFF sample format integer code to its corresponding Spark `DataType` for use in raster data ingestion and pixel-level processing.

### Parameters
- `sampleFormat` (`Int`): The integer identifier representing the sample format as defined in the GeoTIFF specification (e.g., 1 for unsigned integer, 2 for signed integer, 3 for IEEE floating point).

### Input
The caller must provide an integer representing a valid GeoTIFF sample format code. This function is typically used internally by `GeoTiffReader` when parsing raster metadata to determine the appropriate Spark `DataType` for the underlying pixel values.

### Output
Returns `DataType` — The Spark SQL `DataType` (e.g., `IntegerType`, `FloatType`) that corresponds to the provided GeoTIFF sample format, enabling correct type mapping for `RasterRDD` creation.

### Valid Call Patterns
```scala
// Inferred from signature:
val dataType = edu.ucr.cs.bdlab.raptor.GeoTiffReader.getSampleComponentType(sampleFormat)
```

### LLM Instruction Prompt
- Use `edu.ucr.cs.bdlab.raptor.GeoTiffReader.getSampleComponentType` when you need to resolve the Spark data type for a raster based on its GeoTIFF metadata.
- Ensure the `sampleFormat` integer is derived from the GeoTIFF header information.
- Do not assume the return type is a primitive; it returns a Spark `DataType`.

### Prompt Snippet
```text
val sampleFormatCode: Int = // ... extracted from GeoTIFF header
val sparkType = edu.ucr.cs.bdlab.raptor.GeoTiffReader.getSampleComponentType(sampleFormatCode)
```

### Common Failure Modes
- **Unsupported Format:** Passing an integer code that does not map to a standard Spark `DataType` may result in unexpected behavior or runtime exceptions during raster ingestion.
- **Type Mismatch:** Using the wrong `DataType` for a `RasterRDD` (e.g., treating a `Float` raster as `Integer`) will lead to incorrect pixel value interpretation during `mapPixels` or `raptorJoin` operations.

### Fix Code Hint
```scala
// Ensure the sample format code is validated against GeoTIFF standards before calling
val dataType = edu.ucr.cs.bdlab.raptor.GeoTiffReader.getSampleComponentType(sampleFormat)
// Use the resulting dataType to define the schema for your RasterRDD
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffReader.initialize`

### Signature
```scala
[](fileSystem:FileSystem,path:String,layer:String,opts:BeastOptions):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffReader.scala:74_

### Goal
Initializes the `GeoTiffReader` instance to prepare for reading raster data from a specified GeoTIFF file within the Beast Raptor framework.

### Parameters
- `fileSystem` (`FileSystem`): The Hadoop `FileSystem` object used to access the underlying storage where the raster data resides.
- `path` (`String`): The file path string pointing to the GeoTIFF file.
- `layer` (`String`): The specific layer or band identifier to be accessed within the GeoTIFF file.
- `opts` (`BeastOptions`): Configuration options for the reader, such as performance tuning or format-specific settings.

### Input
- **Data Format:** GeoTIFF.
- **Preconditions:** The `fileSystem` must be correctly initialized and have read access to the provided `path`. The `layer` must exist within the target GeoTIFF file.

### Output
Returns `Unit` — This method performs internal state initialization for the reader instance and does not return a value.

### Valid Call Patterns
```scala
// Inferred from signature:
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader()
reader.initialize(fileSystem, path, layer, opts)
```

### LLM Instruction Prompt
- When generating code that uses `GeoTiffReader.initialize`, ensure the `FileSystem` is obtained from the current Spark context or Hadoop configuration.
- Always verify that the `path` points to a valid GeoTIFF file before calling `initialize`.
- Ensure the `BeastOptions` object is properly instantiated to handle any required raster processing configurations.

### Prompt Snippet
```text
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader()
reader.initialize(fs, "/path/to/raster.tif", "band1", new BeastOptions())
```

### Common Failure Modes
- **FileNotFoundException:** Occurs if the `path` provided is incorrect or inaccessible via the provided `FileSystem`.
- **Invalid Layer:** Occurs if the specified `layer` string does not correspond to a valid band or sub-dataset in the GeoTIFF.
- **Initialization Order:** Calling read operations before `initialize` will result in an uninitialized state error.

### Fix Code Hint
```scala
// Ensure the reader is initialized before attempting to access metadata or pixel data
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader()
try {
  reader.initialize(fs, path, layer, new BeastOptions())
} catch {
  case e: Exception => println(s"Failed to initialize GeoTiffReader: ${e.getMessage}")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffReader.isValidTile`

### Signature
```scala
[](tileID:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffReader.scala:201_

### Goal
Determines whether a specific tile index exists and is valid within the context of the current `GeoTiffReader` instance.

### Parameters
- `tileID` (`Int`): The unique identifier of the raster tile to verify.

### Input
- **Data:** An initialized `GeoTiffReader` instance.
- **Preconditions:** The reader must be successfully initialized with a valid GeoTIFF source. The `tileID` must correspond to the internal indexing scheme of the loaded raster.

### Output
Returns `Boolean` — `true` if the `tileID` is within the valid range of tiles for the loaded GeoTIFF, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader(path)
val exists = reader.isValidTile(tileID)
```

### LLM Instruction Prompt
- When using `isValidTile`, ensure the `GeoTiffReader` has been instantiated with a valid file path.
- Use this method to perform bounds checking before attempting to access specific tile data to avoid index-out-of-bounds exceptions.

### Prompt Snippet
```text
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader("path/to/raster.tif")
if (reader.isValidTile(myTileID)) {
  // Proceed with tile processing
}
```

### Common Failure Modes
- **Uninitialized Reader:** Calling `isValidTile` on a reader that has not been properly initialized or points to a non-existent file.
- **Negative Indices:** Passing a negative `tileID`, which will return `false` as it is outside the valid range of tile identifiers.
- **Metadata Mismatch:** Assuming a `tileID` is valid across different raster files; always verify against the specific `GeoTiffReader` instance associated with the target file.

### Fix Code Hint
```scala
// Ensure the reader is valid before checking
if (reader != null && reader.isValidTile(tileID)) {
  val tile = reader.readTile(tileID)
} else {
  println(s"Tile $tileID is invalid or reader is not initialized.")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffReader.metadata`

### Signature
```scala
[]:RasterMetadata
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffReader.scala:165_

### Goal
Retrieves the spatial and structural metadata of a GeoTIFF file, such as dimensions, resolution, and coordinate reference system, required for raster processing workflows.

### Parameters
_None._

### Input
The `GeoTiffReader` instance must be initialized with a valid GeoTIFF file path. No specific preconditions are required for the metadata call itself, but the reader must have successfully opened the target raster file.

### Output
Returns `RasterMetadata` — An object containing the raster's spatial extent, pixel dimensions, cell size (resolution), and CRS information.

### Valid Call Patterns
```scala
// Inferred from signature:
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader(path)
val meta: RasterMetadata = reader.metadata()
```

### LLM Instruction Prompt
- When using `metadata()`, ensure the `GeoTiffReader` has been instantiated with a valid file path.
- Use the returned `RasterMetadata` to verify compatibility before performing `overlay` or `raptorJoin` operations, as these require identical metadata (resolution, CRS, tile size).

### Prompt Snippet
```text
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader("path/to/raster.tif")
val metadata = reader.metadata()
```

### Common Failure Modes
- **Initialization Failure:** Calling `metadata()` on a reader that failed to open the file due to an invalid path or unsupported format.
- **Metadata Mismatch:** Attempting to use the returned metadata to perform an `overlay` operation with another raster that has different resolution or CRS; always use `reshape` to align rasters if metadata differs.

### Fix Code Hint
```scala
// Ensure the reader is properly initialized before accessing metadata
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader(filePath)
val meta = reader.metadata()
// If metadata is incompatible with other rasters, use reshape before processing
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffReader.readTile`

### Signature
```scala
[](tileID:Int):ITile[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffReader.scala:167_

### Goal
Reads a specific raster tile from a GeoTIFF dataset by its unique identifier for pixel-level access or processing.

### Parameters
- `tileID` (`Int`): The unique index or identifier of the tile to be retrieved from the GeoTIFF source.

### Input
- **Data Source:** A GeoTIFF file initialized within a `GeoTiffReader` instance.
- **Preconditions:** The `GeoTiffReader` must be correctly instantiated and point to a valid GeoTIFF file. The `tileID` must be within the valid range of tiles defined by the raster's tiling scheme.

### Output
Returns `ITile[T]` — An `ITile` object containing the pixel data for the requested tile, where `T` represents the pixel data type (e.g., `Float`, `Int`).

### Valid Call Patterns
```scala
// Inferred from signature:
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader(path)
val tile: ITile[Float] = reader.readTile(tileID)
```

### LLM Instruction Prompt
- When using `readTile`, ensure the `GeoTiffReader` is already initialized.
- The `tileID` must be a valid integer corresponding to the internal tiling structure of the GeoTIFF.
- Ensure the generic type `T` of the `ITile` matches the underlying data type of the raster (e.g., `Float` for elevation or temperature data).

### Prompt Snippet
```text
val reader = new edu.ucr.cs.bdlab.raptor.GeoTiffReader(tiffPath)
val tile = reader.readTile(tileIndex)
```

### Common Failure Modes
- **IndexOutOfBoundsException:** Occurs if the provided `tileID` does not exist within the GeoTIFF file.
- **Type Mismatch:** Occurs if the expected `ITile[T]` type does not match the actual pixel data type stored in the GeoTIFF.
- **Initialization Error:** Occurs if the `GeoTiffReader` was not properly initialized or the file path is inaccessible.

### Fix Code Hint
```scala
// Ensure the tileID is within bounds before calling
if (tileID >= 0 && tileID < reader.getNumTiles) {
  val tile = reader.readTile(tileID)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileDouble`

### Signature
```scala
[](tileID:Int,tiffTile:AbstractTiffTile,fillValue:Int,pixelDefined:Array[Byte],metadata:RasterMetadata,rasterFeatures:RasterFeature):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileDouble.scala:18_

### Goal
Constructs a `GeoTiffTileDouble` object to represent a specific raster tile containing double-precision pixel values within the Raptor processing framework.

### Parameters
- `tileID` (`Int`): A unique identifier for the specific raster tile being processed.
- `tiffTile` (`AbstractTiffTile`): The underlying TIFF tile data structure containing the raw raster information.
- `fillValue` (`Int`): The integer value used to represent "no-data" or empty pixels within the tile.
- `pixelDefined` (`Array[Byte]`): A byte array acting as a mask to indicate which pixels in the tile are valid (defined) versus undefined.
- `metadata` (`RasterMetadata`): The metadata object containing spatial reference, resolution, and extent information for the raster.
- `rasterFeatures` (`RasterFeature`): The feature set associated with the raster, used for raster-vector join operations.

### Input
The caller must provide a valid `AbstractTiffTile` and corresponding `RasterMetadata`. Preconditions include ensuring the `RasterMetadata` is aligned with the tile's coordinate system and that the `pixelDefined` array matches the dimensions of the `tiffTile`.

### Output
Returns an instance of `GeoTiffTileDouble` (type inferred as the class itself), which encapsulates the tile data for double-precision raster operations.

### Valid Call Patterns
```scala
// Example inferred from constructor signature
val tile = new edu.ucr.cs.bdlab.raptor.GeoTiffTileDouble(
  tileID, 
  tiffTile, 
  fillValue, 
  pixelDefined, 
  metadata, 
  rasterFeatures
)
```

### LLM Instruction Prompt
- When generating code using this class, ensure all six parameters are provided in the exact order specified.
- This class is intended for internal Raptor processing; ensure that the `metadata` provided is consistent with the `tiffTile` to avoid spatial misalignment during raster-vector joins.

### Prompt Snippet
```text
// Constructing a GeoTiffTileDouble for Raptor processing
val myTile = new edu.ucr.cs.bdlab.raptor.GeoTiffTileDouble(id, tiff, fill, mask, meta, features)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not match the `tiffTile` resolution or CRS will lead to incorrect spatial indexing or join results.
- **Array Size Mismatch:** If the `pixelDefined` array length does not correspond to the number of pixels in the `tiffTile`, the system may throw an `ArrayIndexOutOfBoundsException` during processing.
- **Type Incompatibility:** Attempting to use this class for non-double raster data; ensure the source raster is compatible with double-precision representation.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the source RasterRDD before instantiation
val metadata = rasterRDD.metadata
val tile = new edu.ucr.cs.bdlab.raptor.GeoTiffTileDouble(id, tiff, fill, mask, metadata, features)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileDouble.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileDouble.scala:24_

### Goal
Retrieves the underlying `DataType` associated with the `GeoTiffTileDouble` instance, identifying the primitive data type used for pixel storage.

### Parameters
_None._

### Input
The caller must have an initialized instance of `GeoTiffTileDouble`. This class is specifically designed for handling double-precision floating-point raster data within the Raptor framework.

### Output
Returns `DataType` — An object representing the data type of the tile's components, which for this class is fixed to the double-precision floating-point type.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile = new GeoTiffTileDouble(...)
val dataType = tile.componentType()
```

### LLM Instruction Prompt
- When generating code involving `GeoTiffTileDouble`, use `componentType()` to verify the pixel data type if the tile is being passed to generic raster processing functions that require explicit type checking.
- Do not attempt to change the component type of a `GeoTiffTileDouble` instance, as it is internally fixed to `Double`.

### Prompt Snippet
```text
// Accessing the component type of a GeoTiffTileDouble
val tile: GeoTiffTileDouble = ...
val dtype = tile.componentType()
```

### Common Failure Modes
- **Type Mismatch:** Attempting to use this tile in operations expecting `IntegerType` or `FloatType` without explicit conversion, as `GeoTiffTileDouble` is strictly typed for `Double`.
- **Null Reference:** Calling `componentType()` on an uninitialized or null `GeoTiffTileDouble` object will result in a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure the tile is properly initialized before accessing metadata
val tile = new GeoTiffTileDouble(width, height, data)
val dtype = tile.componentType()
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileDoubleArray`

### Signature
```scala
[](tileID:Int,tiffTile:AbstractTiffTile,fillValue:Int,pixelDefined:Array[Byte],metadata:RasterMetadata,rasterFeatures:RasterFeature):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileDoubleArray.scala:19_

### Goal
Constructs a double-precision array representation of a GeoTIFF tile for use in Raptor raster-vector processing workflows.

### Parameters
- `tileID` (`Int`): The unique identifier for the specific raster tile being processed.
- `tiffTile` (`AbstractTiffTile`): The source GeoTIFF tile object containing raw raster data.
- `fillValue` (`Int`): The value used to represent NoData or empty pixels within the tile.
- `pixelDefined` (`Array[Byte]`): A byte array mask indicating which pixels are valid (defined) versus NoData.
- `metadata` (`RasterMetadata`): The metadata object containing spatial reference, resolution, and extent information for the raster.
- `rasterFeatures` (`RasterFeature`): The feature set associated with the raster for join or extraction operations.

### Input
Requires a valid `AbstractTiffTile` extracted from a GeoTIFF source. Preconditions include ensuring the `RasterMetadata` matches the tile's spatial properties. The `pixelDefined` array must correspond to the dimensions of the `tiffTile`.

### Output
Returns `unspecified` — The function returns an object representing the tile's pixel data as a double-precision array, typically used for subsequent numerical analysis or raster-vector joins.

### Valid Call Patterns
```scala
// Example inferred from signature; requires instantiation of required Raptor components
val tileArray = new edu.ucr.cs.bdlab.raptor.GeoTiffTileDoubleArray(
  tileID, 
  tiffTile, 
  fillValue, 
  pixelDefined, 
  metadata, 
  rasterFeatures
)
```

### LLM Instruction Prompt
- When calling `GeoTiffTileDoubleArray`, ensure that the `metadata` provided is consistent with the `tiffTile` dimensions and CRS.
- The `fillValue` must match the expected NoData convention of the source raster.
- This class is intended for internal Raptor processing; ensure it is used within the context of a Spark transformation or Raptor join pipeline.

### Prompt Snippet
```text
val tileData = new edu.ucr.cs.bdlab.raptor.GeoTiffTileDoubleArray(tileID, tiffTile, fillValue, pixelDefined, metadata, rasterFeatures)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not align with the `tiffTile` resolution or extent will cause downstream processing errors.
- **Array Size Mismatch:** If the `pixelDefined` array length does not match the total number of pixels in the `tiffTile`, an `ArrayIndexOutOfBoundsException` or logic error may occur.
- **Type Incompatibility:** Attempting to use this with non-GeoTIFF raster sources or incompatible `AbstractTiffTile` implementations.

### Fix Code Hint
```scala
// Ensure metadata is aligned before tile processing
val alignedMetadata = metadata.reshape(tiffTile.extent)
val tileArray = new edu.ucr.cs.bdlab.raptor.GeoTiffTileDoubleArray(tileID, tiffTile, fillValue, pixelDefined, alignedMetadata, rasterFeatures)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileDoubleArray.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileDoubleArray.scala:30_

### Goal
Retrieves the underlying `DataType` of the pixels stored within a `GeoTiffTileDoubleArray` instance.

### Parameters
_None._

### Input
The caller must have an initialized instance of `GeoTiffTileDoubleArray`. No specific file format input is required at the time of calling, as this method operates on the in-memory representation of the tile.

### Output
Returns `DataType` — The data type definition (e.g., representing the numeric precision of the raster pixels) associated with the tile array.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: GeoTiffTileDoubleArray = ...
val dataType: DataType = tile.componentType()
```

### LLM Instruction Prompt
- When generating code that interacts with `GeoTiffTileDoubleArray`, use `componentType()` to inspect the pixel data type before performing arithmetic or transformation operations.
- Ensure the `DataType` returned is compatible with the expected pixel type of subsequent `RasterRDD` operations (e.g., `IntegerType`, `FloatType`).

### Prompt Snippet
```text
val tile = new GeoTiffTileDoubleArray(...)
val dtype = tile.componentType()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `componentType()` on an uninitialized or null `GeoTiffTileDoubleArray` reference.
- **Type Mismatch:** Assuming a specific `DataType` (e.g., `FloatType`) without verifying the return value of `componentType()`, which may lead to runtime errors during pixel-level transformations or `mapPixels` operations.

### Fix Code Hint
```scala
// Always verify the component type before processing
val tile = ...
if (tile.componentType() == FloatType) {
  // Perform float-specific raster processing
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileDoubleArray.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):Array[Double]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileDoubleArray.scala:23_

### Goal
Retrieves the pixel values at a specific coordinate location from a `GeoTiffTileDoubleArray` instance, returning the data as an array of doubles.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide an initialized `GeoTiffTileDoubleArray` object. This class is part of the Raptor raster processing module and is typically used when working with raster data loaded via Beast's raster ingestion methods. The indices `i` and `j` must be within the bounds of the tile dimensions.

### Output
Returns `Array[Double]` — An array containing the pixel values at the specified (i, j) location. The array length corresponds to the number of bands or spectral components stored at that pixel location.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: GeoTiffTileDoubleArray = ... // initialized tile
val pixelValues: Array[Double] = tile.getPixelValue(i, j)
```

### LLM Instruction Prompt
- Use this method only when working with `GeoTiffTileDoubleArray` objects in the `edu.ucr.cs.bdlab.raptor` package.
- Ensure that `i` and `j` are validated against the tile's width and height before invocation to prevent index out-of-bounds exceptions.
- The return type is always `Array[Double]`, regardless of the original raster bit depth, as this class handles the conversion to double-precision.

### Prompt Snippet
```text
val tile = ... // GeoTiffTileDoubleArray
val values = tile.getPixelValue(x, y)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if the provided `i` or `j` coordinates fall outside the spatial extent of the tile.
- **NullPointerException**: Occurs if the `GeoTiffTileDoubleArray` instance has not been properly initialized or loaded from the source GeoTIFF.
- **Type Mismatch**: Attempting to use this on non-double raster types without ensuring the underlying data structure is indeed a `GeoTiffTileDoubleArray`.

### Fix Code Hint
```scala
// Ensure bounds checking before access
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  val values = tile.getPixelValue(i, j)
} else {
  // Handle out of bounds
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileFloat`

### Signature
```scala
[](tileID:Int,tiffTile:AbstractTiffTile,fillValue:Int,pixelDefined:Array[Byte],metadata:RasterMetadata,rasterFeatures:RasterFeature):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileFloat.scala:17_

### Goal
Constructs a `GeoTiffTileFloat` object to represent a single raster tile with floating-point pixel values within the Raptor processing framework.

### Parameters
- `tileID` (`Int`): A unique identifier for the specific raster tile being processed.
- `tiffTile` (`AbstractTiffTile`): The underlying TIFF tile data structure containing the raw raster information.
- `fillValue` (`Int`): The integer value used to represent "no-data" or empty pixels within the tile.
- `pixelDefined` (`Array[Byte]`): A byte array acting as a mask, where each byte indicates whether the corresponding pixel is defined (valid) or undefined.
- `metadata` (`RasterMetadata`): The metadata object containing spatial reference, resolution, and extent information for the raster.
- `rasterFeatures` (`RasterFeature`): The feature set associated with the raster, used for raster-vector join operations.

### Input
The caller must provide a valid `AbstractTiffTile` and matching `RasterMetadata`. Preconditions include ensuring the raster data is compatible with floating-point operations. If the raster is not already in the required format, use `reshape` or `mapPixels` to align metadata and pixel types before instantiation.

### Output
Returns an instance of `GeoTiffTileFloat` (type unspecified in signature), which encapsulates the tile data for use in Raptor join operations or pixel-level transformations.

### Valid Call Patterns
```scala
// Example inferred from signature
val tile = new edu.ucr.cs.bdlab.raptor.GeoTiffTileFloat(
  tileID, 
  tiffTile, 
  fillValue, 
  pixelDefined, 
  metadata, 
  rasterFeatures
)
```

### LLM Instruction Prompt
- When calling `GeoTiffTileFloat`, ensure all parameters are initialized.
- The `fillValue` must be consistent with the `RasterMetadata` defined for the dataset.
- Ensure `pixelDefined` array length matches the dimensions specified in `metadata`.

### Prompt Snippet
```text
val tile = new edu.ucr.cs.bdlab.raptor.GeoTiffTileFloat(tileID, tiffTile, fillValue, pixelDefined, metadata, rasterFeatures)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not match the `tiffTile` dimensions or CRS will cause downstream processing errors in Raptor joins.
- **Type Mismatch:** Attempting to use this class for non-floating-point raster data; ensure the source raster is compatible with `Float` types.
- **Null Pointers:** Passing a null `pixelDefined` array or `metadata` object will result in a runtime exception during tile access.

### Fix Code Hint
```scala
// Ensure metadata is aligned before creating the tile
val alignedMetadata = originalMetadata.reshape(targetResolution)
val tile = new edu.ucr.cs.bdlab.raptor.GeoTiffTileFloat(id, tiff, fill, mask, alignedMetadata, features)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileFloat.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileFloat.scala:23_

### Goal
Retrieves the underlying `DataType` associated with the `GeoTiffTileFloat` instance to identify the pixel data representation.

### Parameters
_None._

### Input
The caller must have an initialized instance of `GeoTiffTileFloat`. This class is specifically designed for handling raster data where pixels are represented as floating-point values.

### Output
Returns `DataType` — The metadata object representing the primitive data type of the raster pixels (typically corresponding to `FloatType` in the Beast framework).

### Valid Call Patterns
```scala
// Inferred from signature:
val tile = new GeoTiffTileFloat(...)
val dataType = tile.componentType()
```

### LLM Instruction Prompt
- When generating code involving `GeoTiffTileFloat`, use `componentType()` to verify the pixel data type before performing arithmetic transformations (e.g., `mapPixels`).
- Ensure the `DataType` returned is compatible with the expected `Float` operations required by the Raptor processing pipeline.

### Prompt Snippet
```text
val tile: GeoTiffTileFloat = ...
val dtype = tile.componentType()
```

### Common Failure Modes
- **Type Mismatch:** Attempting to perform operations assuming a different `DataType` (e.g., `IntegerType`) when the tile is explicitly a `GeoTiffTileFloat`.
- **Null Reference:** Calling `componentType()` on an uninitialized or null `GeoTiffTileFloat` object.
- **Contextual Incompatibility:** Using this tile in an `overlay` operation without ensuring metadata alignment (resolution, CRS) via `reshape` first, as required by Beast raster processing constraints.

### Fix Code Hint
```scala
// Ensure the tile is of the expected float type before processing
if (tile.componentType() == FloatType) {
  val processed = tile.mapPixels(p => p * 1.0f)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileFloatArray`

### Signature
```scala
[](tileID:Int,tiffTile:AbstractTiffTile,fillValue:Int,pixelDefined:Array[Byte],metadata:RasterMetadata,rasterFeatures:RasterFeature):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileFloatArray.scala:18_

### Goal
Constructs a float-based representation of a GeoTIFF tile for use in Raptor raster-vector processing workflows.

### Parameters
- `tileID` (`Int`): The unique identifier for the specific raster tile being processed.
- `tiffTile` (`AbstractTiffTile`): The source GeoTIFF tile data structure.
- `fillValue` (`Int`): The integer value used to represent NoData or empty pixels within the tile.
- `pixelDefined` (`Array[Byte]`): A byte array mask indicating which pixels are valid (defined) versus NoData.
- `metadata` (`RasterMetadata`): The metadata object containing spatial reference, resolution, and extent information for the raster.
- `rasterFeatures` (`RasterFeature`): The feature set associated with the raster for join operations.

### Input
Requires a valid `AbstractTiffTile` extracted from a GeoTIFF source. The `RasterMetadata` must be consistent with the tile's dimensions and spatial properties. Precondition: The raster data should be pre-aligned or reshaped if participating in multi-raster overlay operations.

### Output
Returns an object of an unspecified type representing the float-converted tile data, suitable for downstream Raptor operations.

### Valid Call Patterns
```scala
// Example inferred from signature (not verified)
val tileArray = new edu.ucr.cs.bdlab.raptor.GeoTiffTileFloatArray(
  tileID, 
  tiffTile, 
  fillValue, 
  pixelDefined, 
  metadata, 
  rasterFeatures
)
```

### LLM Instruction Prompt
- When calling `GeoTiffTileFloatArray`, ensure that the `fillValue` matches the expected NoData convention of the source GeoTIFF.
- Ensure the `pixelDefined` array length corresponds to the dimensions defined in `metadata`.
- This class is intended for internal Raptor processing; ensure it is used within the context of a Spark-based raster-vector join workflow.

### Prompt Snippet
```text
val tileArray = new edu.ucr.cs.bdlab.raptor.GeoTiffTileFloatArray(tileID, tiffTile, fillValue, pixelDefined, metadata, rasterFeatures)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not match the `tiffTile` dimensions will result in index out-of-bounds errors during processing.
- **Type Incompatibility:** Attempting to use this with non-float raster workflows may lead to unexpected casting behavior.
- **Missing NoData Handling:** Incorrectly setting the `fillValue` can cause NoData pixels to be processed as valid numerical values, skewing spatial analysis results.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the source RasterRDD
val metadata = rasterRDD.metadata
// Ensure pixelDefined mask is correctly generated from the tile's NoData values
val pixelDefined = tiffTile.getPixelDefinedMask(fillValue)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileFloatArray.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileFloatArray.scala:29_

### Goal
Retrieves the `DataType` associated with the pixel values stored within a `GeoTiffTileFloatArray` instance.

### Parameters
_None._

### Input
The caller must have an initialized instance of `GeoTiffTileFloatArray`. This class is typically used within the Raptor framework when handling raster data where pixel values are represented as floating-point numbers.

### Output
Returns `DataType` — An object representing the underlying data type of the raster tile's components (e.g., `FloatType`).

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: GeoTiffTileFloatArray = ...
val dataType: DataType = tile.componentType()
```

### LLM Instruction Prompt
- Use this method to inspect the metadata of a `GeoTiffTileFloatArray` object to ensure type compatibility before performing pixel-level transformations or raster-vector joins.
- Do not assume the return value; always query the instance directly if the specific `DataType` implementation is required for downstream Spark operations.

### Prompt Snippet
```text
val tile = ... // GeoTiffTileFloatArray
val typeInfo = tile.componentType()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling this method on an uninitialized or null `GeoTiffTileFloatArray` reference.
- **Type Mismatch:** Attempting to use the returned `DataType` in operations that expect a different primitive type (e.g., `IntegerType`) without proper casting or conversion, which may lead to runtime errors in Spark transformations.

### Fix Code Hint
```scala
// Ensure the tile is initialized before accessing metadata
if (tile != null) {
  val dt = tile.componentType()
  // Proceed with type-safe operations
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileFloatArray.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):Array[Float]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileFloatArray.scala:22_

### Goal
Retrieves the pixel values at a specific coordinate location within a `GeoTiffTileFloatArray` instance, returning the data as an array of floating-point numbers.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide an initialized `GeoTiffTileFloatArray` object. This class is intended for use within the Raptor framework for handling raster data. The indices `i` and `j` must be within the bounds of the tile dimensions; otherwise, an `ArrayIndexOutOfBoundsException` may occur.

### Output
Returns `Array[Float]` — An array containing the pixel values at the specified (i, j) location. The array length corresponds to the number of bands or depth of the raster data at that pixel.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: GeoTiffTileFloatArray = ... // initialized tile
val pixelValues: Array[Float] = tile.getPixelValue(i, j)
```

### LLM Instruction Prompt
- When using `getPixelValue`, ensure the `i` and `j` parameters are within the valid range of the tile's width and height.
- The return type is `Array[Float]`, which may contain multiple values if the underlying raster data is multi-band.
- This method is specific to `GeoTiffTileFloatArray`; ensure the raster data has been loaded into this specific type before invocation.

### Prompt Snippet
```text
val tile = new GeoTiffTileFloatArray(...)
val values = tile.getPixelValue(x, y)
```

### Common Failure Modes
- **Index Out of Bounds:** Providing `i` or `j` values that exceed the tile's dimensions will result in a runtime exception.
- **Type Mismatch:** Attempting to call this on a tile that is not a `GeoTiffTileFloatArray` (e.g., an integer-based tile) will cause a compilation error.
- **Uninitialized Tile:** Calling the method on a null or uninitialized `GeoTiffTileFloatArray` reference will throw a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure indices are within bounds before access
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  val values = tile.getPixelValue(i, j)
} else {
  // Handle out-of-bounds error
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileInt`

### Signature
```scala
[](tileID:Int,tiffTile:AbstractTiffTile,fillValue:Int,pixelDefined:Array[Byte],metadata:RasterMetadata,rasterFeatures:RasterFeature):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileInt.scala:17_

### Goal
Constructs an integer-based GeoTIFF tile representation within the Raptor framework to facilitate concurrent raster-vector processing and pixel-level analysis.

### Parameters
- `tileID` (`Int`): A unique identifier for the specific tile within the raster dataset.
- `tiffTile` (`AbstractTiffTile`): The underlying raw GeoTIFF tile data structure.
- `fillValue` (`Int`): The integer value used to represent NoData or undefined pixels within the tile.
- `pixelDefined` (`Array[Byte]`): A byte array mask indicating which pixels are valid (defined) versus those that are empty or NoData.
- `metadata` (`RasterMetadata`): The spatial metadata associated with the raster, including CRS, resolution, and extent.
- `rasterFeatures` (`RasterFeature`): The associated raster features or attributes required for Raptor join operations.

### Input
The caller must provide a valid `AbstractTiffTile` and corresponding `RasterMetadata`. Preconditions include ensuring the raster data is compatible with the Raptor processing pipeline. If performing an `overlay` or `join` operation, the `metadata` must match the target vector or raster dataset's resolution and CRS; use `reshape` if alignment is required.

### Output
Returns an instance of `GeoTiffTileInt` (type inferred from class name), which encapsulates the tile data, mask, and metadata for use in Spark-based raster-vector operations.

### Valid Call Patterns
```scala
// Example inferred from signature; requires instantiation of required Raptor components
val tile = new edu.ucr.cs.bdlab.raptor.GeoTiffTileInt(
  tileID, 
  tiffTile, 
  fillValue, 
  pixelDefined, 
  metadata, 
  rasterFeatures
)
```

### LLM Instruction Prompt
- When using `GeoTiffTileInt`, ensure the `fillValue` matches the integer type expected by the raster processing pipeline.
- Always verify that the `metadata` provided is consistent with the `tiffTile` dimensions to prevent index-out-of-bounds errors during raster-vector joins.
- This class is intended for internal Raptor processing; ensure it is used within the context of a `RasterRDD` workflow.

### Prompt Snippet
```text
// Constructing a GeoTiffTileInt for Raptor processing
val myTile = new edu.ucr.cs.bdlab.raptor.GeoTiffTileInt(id, tile, -9999, mask, meta, features)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not align with the `tiffTile` resolution or CRS, causing failures in subsequent `raptorJoin` operations.
- **Type Incompatibility:** Passing non-integer data to a constructor expecting `Int` types, which will result in compilation errors or runtime casting exceptions.
- **Missing NoData Handling:** Failing to correctly set the `fillValue` or `pixelDefined` mask, leading to incorrect results in zonal statistics or spatial joins.

### Fix Code Hint
```scala
// Ensure metadata alignment before tile construction
val alignedMetadata = originalMetadata.reshape(targetResolution, targetCRS)
val tile = new edu.ucr.cs.bdlab.raptor.GeoTiffTileInt(id, tiffTile, -9999, mask, alignedMetadata, features)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileInt.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileInt.scala:23_

### Goal
Retrieves the `DataType` associated with the pixel values stored within a `GeoTiffTileInt` instance.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.GeoTiffTileInt`. This class is typically used when working with integer-based raster data loaded via Beast's raster processing workflows.

### Output
Returns `DataType` — The specific data type definition (e.g., integer-based types) that describes the underlying pixel representation of the tile.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: GeoTiffTileInt = ... // initialized GeoTiffTileInt
val dataType: DataType = tile.componentType()
```

### LLM Instruction Prompt
- When generating code involving `GeoTiffTileInt`, use `componentType()` to inspect the pixel data format.
- Ensure the `GeoTiffTileInt` instance is properly initialized from a GeoTIFF source before calling this method.
- Do not assume the return type is a primitive; it returns a `DataType` object as defined in the Beast framework.

### Prompt Snippet
```text
val tile = new GeoTiffTileInt(...)
val typeInfo = tile.componentType()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `componentType()` on an uninitialized or null `GeoTiffTileInt` reference.
- **Type Mismatch:** Attempting to use this method on non-integer raster tiles; ensure the raster source is compatible with `GeoTiffTileInt` (which expects integer-based pixel data).
- **Contextual Misuse:** Attempting to call this method outside of a Spark executor or driver context where the `GeoTiffTileInt` object has not been correctly deserialized or loaded.

### Fix Code Hint
```scala
// Ensure the tile is valid before accessing metadata
if (tile != null) {
  val dt = tile.componentType()
  println(s"Tile component type: $dt")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileIntArray`

### Signature
```scala
[](tileID:Int,tiffTile:AbstractTiffTile,fillValue:Int,pixelDefined:Array[Byte],rasterMetadata:RasterMetadata,rasterFeatures:RasterFeature):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileIntArray.scala:17_

### Goal
Constructs an integer-based representation of a GeoTIFF tile for use within the Raptor raster-vector processing framework.

### Parameters
- `tileID` (`Int`): A unique identifier for the specific raster tile being processed.
- `tiffTile` (`AbstractTiffTile`): The source GeoTIFF tile data to be processed.
- `fillValue` (`Int`): The integer value used to represent NoData or undefined pixels within the tile.
- `pixelDefined` (`Array[Byte]`): A byte array mask indicating which pixels are valid (defined) versus undefined.
- `rasterMetadata` (`RasterMetadata`): Metadata describing the raster's spatial properties, including resolution, CRS, and dimensions.
- `rasterFeatures` (`RasterFeature`): Feature-specific configuration or attributes associated with the raster processing task.

### Input
The caller must provide a valid `AbstractTiffTile` derived from a GeoTIFF source. Preconditions include ensuring that the `rasterMetadata` is consistent with the tile's dimensions and that the `pixelDefined` array matches the expected pixel count of the tile.

### Output
Returns `unspecified` — The function returns an object representing the processed integer array of the tile, typically used for subsequent pixel-level transformations or raster-vector join operations.

### Valid Call Patterns
```scala
// Example usage inferred from signature
val tileArray = new edu.ucr.cs.bdlab.raptor.GeoTiffTileIntArray(
  tileID, 
  tiffTile, 
  fillValue, 
  pixelDefined, 
  rasterMetadata, 
  rasterFeatures
)
```

### LLM Instruction Prompt
- When using `GeoTiffTileIntArray`, ensure that the `fillValue` matches the expected integer representation of the raster data.
- Always verify that the `rasterMetadata` provided is aligned with the `tiffTile` to prevent spatial mismatch errors during processing.

### Prompt Snippet
```text
val tileArray = new edu.ucr.cs.bdlab.raptor.GeoTiffTileIntArray(tileID, tiffTile, fillValue, pixelDefined, rasterMetadata, rasterFeatures)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not match the resolution or CRS of the `tiffTile` will cause downstream processing errors.
- **Array Size Mismatch:** If the `pixelDefined` array length does not correspond to the dimensions defined in `rasterMetadata`, the operation may throw an index out of bounds exception.
- **Type Incompatibility:** Attempting to use this with non-integer raster data without proper casting or conversion.

### Fix Code Hint
```scala
// Ensure metadata is aligned before tile construction
val alignedMetadata = rasterMetadata.reshape(tiffTile.width, tiffTile.height)
val tileArray = new edu.ucr.cs.bdlab.raptor.GeoTiffTileIntArray(tileID, tiffTile, fillValue, pixelDefined, alignedMetadata, rasterFeatures)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileIntArray.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileIntArray.scala:28_

### Goal
Retrieves the `DataType` associated with the pixel components stored within a `GeoTiffTileIntArray` instance.

### Parameters
_None._

### Input
The caller must have an initialized instance of `GeoTiffTileIntArray`. This class is typically used within the Raptor framework for handling raster data where pixel values are represented as integer arrays. No specific file input is required to call this method, as it operates on the in-memory metadata of the tile object.

### Output
Returns `DataType` — The specific data type (e.g., integer-based types) defining the underlying component structure of the raster tile.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: GeoTiffTileIntArray = ... // initialized tile
val dataType: DataType = tile.componentType()
```

### LLM Instruction Prompt
- When generating code involving `GeoTiffTileIntArray`, use `componentType()` to inspect the underlying data structure of the tile.
- Ensure the `DataType` returned is compatible with any subsequent pixel-level transformations or Spark `mapPixels` operations.
- Do not assume the return type is a primitive; it returns a `DataType` object as defined in the Beast framework.

### Prompt Snippet
```text
val tile = new GeoTiffTileIntArray(...)
val dType = tile.componentType()
```

### Common Failure Modes
- **Null Reference:** Calling `componentType()` on an uninitialized or null `GeoTiffTileIntArray` instance will result in a `NullPointerException`.
- **Type Mismatch:** Attempting to perform arithmetic operations on the result of `componentType()` without checking the `DataType` properties can lead to runtime errors if the tile contains unexpected integer bit-depths.
- **Contextual Misuse:** Using this method outside of the Raptor raster processing workflow where `GeoTiffTileIntArray` is expected may lead to integration issues with Spark RDDs.

### Fix Code Hint
```scala
// Ensure the tile is properly instantiated before accessing metadata
val tile = GeoTiffTileIntArray(...) 
if (tile != null) {
  val dType = tile.componentType()
  println(s"Tile component type: $dType")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileIntArray.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):Array[Int]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileIntArray.scala:21_

### Goal
Retrieves the pixel values at a specific coordinate within a `GeoTiffTileIntArray` raster tile.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide an instance of `GeoTiffTileIntArray` that has been initialized with raster data. The indices `i` and `j` must be within the bounds of the tile dimensions; otherwise, an `ArrayIndexOutOfBoundsException` may occur.

### Output
Returns `Array[Int]` — An array containing the pixel values at the specified location. For multi-band rasters, this array contains the values for each band at that pixel.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: GeoTiffTileIntArray = ...
val pixelValues: Array[Int] = tile.getPixelValue(i, j)
```

### LLM Instruction Prompt
- When using `getPixelValue`, ensure the `i` and `j` coordinates are validated against the tile's width and height to prevent runtime exceptions.
- This method is intended for use with `GeoTiffTileIntArray` objects; ensure the raster data type is compatible with integer-based pixel storage.

### Prompt Snippet
```text
val tile = ... // GeoTiffTileIntArray
val values = tile.getPixelValue(x, y)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if `i` or `j` are negative or exceed the tile's dimensions.
- **NullPointerException**: Occurs if the `GeoTiffTileIntArray` instance has not been properly initialized or loaded.
- **Type Mismatch**: Attempting to use this method on a raster tile that stores data in a format other than `Int` (e.g., `Float` or `Double` tiles).

### Fix Code Hint
```scala
// Ensure bounds checking before access
if (i >= 0 && i < tileWidth && j >= 0 && j < tileHeight) {
  val values = tile.getPixelValue(i, j)
} else {
  // Handle out-of-bounds case
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileSerializer`

### Signature
```scala
[]():<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileSerializer.scala:28_

### Goal
Provides a serialization mechanism for GeoTIFF tiles within the Raptor raster-vector processing framework to facilitate distributed raster data handling.

### Parameters
_None._

### Input
This class is intended for use within the Beast Raptor workflow. It expects raster data compatible with the GeoTIFF format. Preconditions include ensuring that the raster data has been properly initialized within a `RasterRDD` or `ITile` structure, as the serializer is designed to handle the underlying tile data during Spark-based distributed operations.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it represents a serializer instance used by the framework to encode/decode GeoTIFF tile data for storage or network transmission.

### Valid Call Patterns
```scala
// Inferred usage: Instantiating the serializer for use in Raptor raster workflows
val serializer = new edu.ucr.cs.bdlab.raptor.GeoTiffTileSerializer()
```

### LLM Instruction Prompt
- When generating code involving `GeoTiffTileSerializer`, ensure it is used within the context of Beast's Raptor raster processing pipelines.
- Do not attempt to pass arguments to the constructor, as the API signature indicates zero parameters.
- Note that this is a low-level component; prefer higher-level abstractions like `saveAsGeoTiff` on `RasterRDD` where possible.

### Prompt Snippet
```text
// Example of initializing the GeoTiffTileSerializer
val serializer = new edu.ucr.cs.bdlab.raptor.GeoTiffTileSerializer()
```

### Common Failure Modes
- **Type Mismatch:** Attempting to use this serializer with non-GeoTIFF raster data or incompatible pixel types (e.g., mismatching the `T` in `RasterRDD[T]`).
- **Contextual Misuse:** Attempting to use the serializer outside of a Spark-based Raptor workflow, which may lead to serialization errors if the underlying Spark environment is not configured for custom raster types.
- **Missing Dependencies:** Failure to include the `raptor` module in the project's build configuration, as this class is specific to the `edu.ucr.cs.bdlab.raptor` package.

### Fix Code Hint
```scala
// Ensure the project includes the raptor module and the raster data 
// is correctly typed before serialization.
import edu.ucr.cs.bdlab.raptor.GeoTiffTileSerializer

val serializer = new GeoTiffTileSerializer()
// Use the serializer within the appropriate Spark RDD transformation context
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileSerializer.read`

### Signature
```scala
[](kryo:Kryo,input:Input,tileClass:Class[AbstractGeoTiffTile[T]]):AbstractGeoTiffTile[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileSerializer.scala:46_

### Goal
Deserializes a GeoTIFF tile from a Kryo input stream for efficient storage and retrieval within the Raptor raster processing framework.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for the deserialization process.
- `input` (`Input`): The input stream containing the serialized tile data.
- `tileClass` (`Class[AbstractGeoTiffTile[T]]`): The specific class type of the `AbstractGeoTiffTile` to be instantiated.

### Input
The caller must provide a valid `Kryo` instance and an `Input` stream that contains data previously serialized by the corresponding `GeoTiffTileSerializer.write` method. The `tileClass` must match the expected pixel type (e.g., `FloatType`, `IntegerType`) of the raster data being deserialized.

### Output
Returns `AbstractGeoTiffTile[T]` — An instance of the specified `AbstractGeoTiffTile` subclass containing the raster pixel data and associated metadata.

### Valid Call Patterns
```scala
// Inferred usage pattern for custom Kryo serialization
val serializer = new GeoTiffTileSerializer[Float]()
val tile = serializer.read(kryo, input, classOf[AbstractGeoTiffTile[Float]])
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure the `tileClass` parameter matches the generic type `T` of the `AbstractGeoTiffTile` being processed.
- This method is intended for low-level serialization tasks within the Beast framework; ensure the `Input` stream is correctly positioned at the start of the serialized tile object.

### Prompt Snippet
```text
val tile = new GeoTiffTileSerializer[T]().read(kryo, input, classOf[AbstractGeoTiffTile[T]])
```

### Common Failure Modes
- **Type Mismatch:** Providing a `tileClass` that does not match the actual serialized data type will result in a `ClassCastException` or deserialization error.
- **Stream Corruption:** If the `Input` stream is not correctly positioned or contains incomplete data, the deserialization will fail.
- **Kryo Configuration:** Failure to register the `GeoTiffTileSerializer` with the Kryo instance before attempting to read may lead to serialization exceptions.

### Fix Code Hint
```scala
// Ensure the serializer is used with the correct type parameter T
val serializer = new GeoTiffTileSerializer[Float]()
val tile = serializer.read(kryo, input, classOf[AbstractGeoTiffTile[Float]])
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffTileSerializer.write`

### Signature
```scala
[](kryo:Kryo,output:Output,tile:AbstractGeoTiffTile[T]):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffTileSerializer.scala:29_

### Goal
Serializes an `AbstractGeoTiffTile` into a binary format using the Kryo serialization framework for efficient storage or network transmission within Spark-based raster workflows.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for object graph serialization.
- `output` (`Output`): The Kryo output stream where the serialized tile data will be written.
- `tile` (`AbstractGeoTiffTile[T]`): The specific GeoTIFF tile instance to be serialized.

### Input
- **Data:** An instance of `AbstractGeoTiffTile[T]`.
- **Preconditions:** The `tile` must be fully initialized and compatible with the expected pixel type `T` (e.g., `Int`, `Float`). The `output` stream must be open and writable.
- **Context:** This method is typically invoked within a custom Kryo serializer implementation for Beast raster types.

### Output
Returns `Unit` — The method performs a side-effect by writing the binary representation of the tile to the provided `Output` stream.

### Valid Call Patterns
```scala
// Inferred from signature:
val serializer = new GeoTiffTileSerializer[T]()
serializer.write(kryo, output, tile)
```

### LLM Instruction Prompt
- Use this method only when implementing custom Kryo serialization logic for `AbstractGeoTiffTile` subclasses.
- Ensure the `kryo` instance is correctly configured for the Beast environment.
- Always verify that the `tile` object is not null before invoking the write operation.

### Prompt Snippet
```text
val serializer = new edu.ucr.cs.bdlab.raptor.GeoTiffTileSerializer[Float]()
serializer.write(kryo, output, myTile)
```

### Common Failure Modes
- **Serialization Errors:** Occur if the `tile` contains unsupported data types or if the `kryo` instance is not registered with the necessary Beast classes.
- **Buffer Overflow:** The `output` stream may throw an exception if the buffer size is insufficient for the serialized tile data.
- **Type Mismatch:** Passing a tile with a pixel type `T` that does not match the serializer's expected type parameters.

### Fix Code Hint
```scala
// Ensure the output buffer is sufficiently sized for the tile data
val output = new com.esotericsoftware.kryo.io.Output(4096, -1)
serializer.write(kryo, output, tile)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter`

### Signature
```scala
[](outPath:Path,rasterMetadata:RasterMetadata,bare:Boolean,opts:BeastOptions):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:48_

### Goal
Writes raster data to a GeoTIFF file on the underlying file system using specified metadata and configuration options.

### Parameters
- `outPath` (`Path`): The destination file path where the GeoTIFF will be saved.
- `rasterMetadata` (`RasterMetadata`): The metadata object defining the raster's spatial extent, resolution, CRS, and tile structure.
- `bare` (`Boolean`): A flag indicating whether to write the file in "bare" mode (typically omitting auxiliary metadata or specific header structures).
- `opts` (`BeastOptions`): Configuration options for the writer, such as compression settings or block sizes.

### Input
The caller must provide a valid `Path` and a `RasterMetadata` object that accurately describes the raster data being written. Preconditions include ensuring the output directory exists (or is writable) and that the `RasterMetadata` is consistent with the data being processed.

### Output
Returns `unspecified` — The function performs a side-effect operation by writing a GeoTIFF file to the specified `outPath`.

### Valid Call Patterns
```scala
// Inferred usage pattern based on signature
val writer = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter(outPath, rasterMetadata, bare, opts)
```

### LLM Instruction Prompt
- When using `GeoTiffWriter`, ensure the `RasterMetadata` is correctly initialized to match the raster's spatial properties.
- Always verify that the `outPath` does not already exist unless the environment is configured to overwrite, as Beast operations fail if the output directory exists.
- Use `BeastOptions` to pass specific tuning parameters required for the output format.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.GeoTiffWriter
import org.apache.hadoop.fs.Path
// ... initialize metadata and options
val writer = new GeoTiffWriter(new Path("output.tif"), metadata, false, beastOpts)
```

### Common Failure Modes
- **File Exists:** The operation will fail if the `outPath` already exists; use `-overwrite` or ensure the path is unique.
- **Metadata Mismatch:** Providing `RasterMetadata` that does not align with the actual pixel data dimensions or CRS will result in a corrupted or unreadable GeoTIFF.
- **Permission Denied:** Failure to write to the specified `outPath` due to HDFS or local file system permission constraints.

### Fix Code Hint
```scala
// Ensure metadata is aligned before writing
val alignedMetadata = raster.metadata.reshape(...) 
val writer = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter(path, alignedMetadata, false, new BeastOptions())
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry`

### Signature
```scala
[](tag:Short,valueType:Short,value:Any,offset:Long):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:856_

### Goal
Represents a temporary Image File Directory (IFD) entry used during the construction of GeoTIFF metadata within the RDPro raster writing pipeline.

### Parameters
- `tag` (`Short`): The TIFF tag identifier corresponding to the specific metadata field (e.g., ModelPixelScaleTag, ModelTiepointTag).
- `valueType` (`Short`): The TIFF data type code indicating the format of the value (e.g., SHORT, LONG, RATIONAL).
- `value` (`Any`): The actual data associated with the tag, which will be serialized into the GeoTIFF header.
- `offset` (`Long`): The byte offset within the file where the value is stored or referenced.

### Input
The caller must provide valid TIFF tag and type identifiers as defined by the GeoTIFF specification. This is an internal utility class used by `GeoTiffWriter` during the raster serialization process; it is not intended for direct end-user manipulation of raster data.

### Output
Returns an instance of `TempIFDEntry` (type inferred as the class itself), which encapsulates the metadata entry for subsequent writing to the GeoTIFF file structure.

### Valid Call Patterns
```scala
// Example inferred from signature:
val entry = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry(tag, valueType, value, offset)
```

### LLM Instruction Prompt
- When generating code involving `GeoTiffWriter`, do not instantiate `TempIFDEntry` directly unless implementing custom low-level GeoTIFF serialization.
- Ensure `tag` and `valueType` conform to standard TIFF/GeoTIFF specifications.
- Use `GeoTiffWriter` high-level methods (e.g., `saveAsGeoTiff`) for standard raster output tasks instead of manual IFD entry management.

### Prompt Snippet
```text
// Use GeoTiffWriter.TempIFDEntry only for low-level metadata construction
val ifdEntry = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry(tag, vType, valData, byteOffset)
```

### Common Failure Modes
- **Invalid Tag/Type:** Providing a `valueType` that does not match the expected TIFF specification for a given `tag` will result in malformed GeoTIFF headers.
- **Offset Mismatch:** Providing an incorrect `offset` will lead to corrupted metadata, rendering the resulting GeoTIFF unreadable by standard GIS software.
- **Direct Usage:** Attempting to use this class for general raster processing instead of using `RasterRDD.saveAsGeoTiff` is an anti-pattern and will likely lead to runtime errors or invalid file outputs.

### Fix Code Hint
```scala
// Instead of manual IFD entry, use the high-level API:
val rasterRDD: RasterRDD[Float] = ...
rasterRDD.saveAsGeoTiff("output_path")
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry.count`

### Signature
```scala
[]:Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:860_

### Goal
Retrieves the count of values associated with a specific Image File Directory (IFD) entry during the GeoTIFF writing process in the Raptor raster processing workflow.

### Parameters
_None._

### Input
The caller must have an initialized `TempIFDEntry` object, which is used internally by `GeoTiffWriter` to manage metadata tags during the construction of GeoTIFF files. No external file input is required for this method; it operates on the state of the `TempIFDEntry` instance.

### Output
Returns `Int` — The number of data elements (count) defined for the specific IFD entry.

### Valid Call Patterns
```scala
// Inferred from signature:
val entry: edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry = ...
val count: Int = entry.count
```

### LLM Instruction Prompt
- When generating code that interacts with `GeoTiffWriter`, use `count` to verify the number of elements in an IFD entry before writing metadata tags to a GeoTIFF file.
- This method is part of the internal Raptor GeoTIFF serialization logic; ensure the `TempIFDEntry` instance is properly populated before calling.

### Prompt Snippet
```text
val entry = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry(...)
val numElements = entry.count
```

### Common Failure Modes
- **Null Reference:** Calling `count` on an uninitialized or null `TempIFDEntry` instance will result in a `NullPointerException`.
- **State Mismatch:** If the `TempIFDEntry` has not been fully populated with data values, the returned `count` may not reflect the intended metadata structure, potentially leading to malformed GeoTIFF headers.

### Fix Code Hint
```scala
// Ensure the entry is fully initialized before accessing metadata properties
val entry = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry(tag, typeCode, count, offset)
if (entry.count > 0) {
  // Proceed with writing logic
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry.getValueOrOffsetBig`

### Signature
```scala
[](bigEndian:Boolean):Long
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:882_

### Goal
Retrieves the value or the file offset for a temporary Image File Directory (IFD) entry during the GeoTIFF writing process, accounting for byte order.

### Parameters
- `bigEndian` (`Boolean`): Specifies the byte order for the returned value; `true` for Big-Endian, `false` for Little-Endian.

### Input
The caller must have an initialized `TempIFDEntry` object within the context of a `GeoTiffWriter` operation. This method is used internally during the serialization of raster metadata to a GeoTIFF file.

### Output
Returns `Long` — The raw value stored in the IFD entry if it fits within the entry size, or the file offset pointing to the location of the data in the GeoTIFF file.

### Valid Call Patterns
```scala
// Inferred from signature:
val entry: TempIFDEntry = ... // obtained from GeoTiffWriter
val valueOrOffset = entry.getValueOrOffsetBig(bigEndian = true)
```

### LLM Instruction Prompt
- When generating code that interacts with `GeoTiffWriter`, use `getValueOrOffsetBig` to resolve the physical location or value of an IFD entry.
- Always provide a `Boolean` value for the `bigEndian` parameter to ensure correct byte interpretation for the target GeoTIFF file format.

### Prompt Snippet
```text
// Example of retrieving IFD entry data with specific endianness
val isBigEndian: Boolean = true
val dataLocation = tempIFDEntry.getValueOrOffsetBig(isBigEndian)
```

### Common Failure Modes
- **Invalid State:** Calling this method on an uninitialized or improperly constructed `TempIFDEntry` may result in incorrect offsets or runtime exceptions.
- **Endianness Mismatch:** Providing the incorrect `bigEndian` flag relative to the GeoTIFF header configuration will result in corrupted metadata values in the output file.

### Fix Code Hint
```scala
// Ensure the endianness matches the GeoTIFF header requirements
val entryValue = tempIFDEntry.getValueOrOffsetBig(bigEndian = false)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry.getValueOrOffsetNormal`

### Signature
```scala
[](bigEndian:Boolean):Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:871_

### Goal
Retrieves the value or the file offset for a temporary Image File Directory (IFD) entry during the GeoTIFF writing process, accounting for byte order.

### Parameters
- `bigEndian` (`Boolean`): A flag indicating the byte order of the GeoTIFF file; set to `true` for Big-Endian (Motorola) byte order or `false` for Little-Endian (Intel) byte order.

### Input
The caller must have an initialized `TempIFDEntry` object within the `GeoTiffWriter` context. This method is typically invoked during the serialization of raster metadata into the GeoTIFF structure.

### Output
Returns `Int` — The raw value of the IFD entry if it fits within the entry's data field, or the file offset pointing to the location where the actual data is stored.

### Valid Call Patterns
```scala
// Inferred from signature:
val entry = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry(...)
val result = entry.getValueOrOffsetNormal(bigEndian = false)
```

### LLM Instruction Prompt
- When generating code that interacts with `GeoTiffWriter`, ensure the `bigEndian` parameter matches the byte order configuration of the target GeoTIFF file being written. This method is internal to the `TempIFDEntry` class and should be used when manually constructing or inspecting IFD entries during low-level raster output operations.

### Prompt Snippet
```text
// Ensure the byte order matches the GeoTIFF specification
val isBigEndian: Boolean = ... 
val entryValue = tempIFDEntry.getValueOrOffsetNormal(isBigEndian)
```

### Common Failure Modes
- **Incorrect Byte Order:** Providing the wrong `bigEndian` value will result in an incorrectly interpreted integer, leading to corrupted IFD offsets or invalid metadata in the generated GeoTIFF.
- **Context Mismatch:** Calling this on an uninitialized or improperly configured `TempIFDEntry` may return unexpected values or offsets that do not point to valid data locations.

### Fix Code Hint
```scala
// Ensure the GeoTiffWriter is configured for the correct endianness before calling
val writer = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter(...)
val entry = writer.createIFDEntry(...)
val value = entry.getValueOrOffsetNormal(writer.isBigEndian)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry.toString`

### Signature
```scala
[]:String
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:892_

### Goal
Provides a string representation of a `TempIFDEntry` object, which is used internally by `GeoTiffWriter` to manage Image File Directory (IFD) entries during the raster writing process.

### Parameters
_None._

### Input
The caller must have an instantiated `edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry` object. This class is an internal component of the Raptor raster writing workflow and is typically managed by the `GeoTiffWriter` during the serialization of raster data to GeoTIFF format.

### Output
Returns `String` — A string representation of the IFD entry, typically containing metadata or tag information associated with the GeoTIFF structure.

### Valid Call Patterns
```scala
// Inferred from signature:
val entry: edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry = ...
val description: String = entry.toString()
```

### LLM Instruction Prompt
- Use this method only for debugging or logging purposes when inspecting the internal state of a `GeoTiffWriter` instance.
- Do not rely on the specific format of the returned string for programmatic parsing, as it is intended for human-readable representation of IFD metadata.

### Prompt Snippet
```text
val entry = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry(...)
val info = entry.toString()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `toString()` on an uninitialized or null `TempIFDEntry` reference.
- **Scope Access:** Since `TempIFDEntry` is an internal class of `GeoTiffWriter`, it may not be accessible outside of the `edu.ucr.cs.bdlab.raptor` package or the `GeoTiffWriter` scope depending on the specific visibility modifiers in the compiled version.

### Fix Code Hint
```scala
// Ensure the object is properly initialized within the GeoTiffWriter context
val entry = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry(...)
println(s"IFD Entry details: ${entry.toString}")
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry.valueLength`

### Signature
```scala
[]:Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:868_

### Goal
Retrieves the length of the value associated with a temporary Image File Directory (IFD) entry during the GeoTIFF writing process.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry`. This class is used internally by the `GeoTiffWriter` to manage metadata tags before they are serialized into the final GeoTIFF file structure.

### Output
Returns `Int` — The length of the entry's value, typically representing the number of bytes or the count of elements for the specific TIFF tag being processed.

### Valid Call Patterns
```scala
// Inferred from signature:
val entry: edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry = ...
val length: Int = entry.valueLength
```

### LLM Instruction Prompt
When generating code that interacts with `GeoTiffWriter`, use `valueLength` to inspect the size of metadata entries. Note that this is an internal helper class within the `GeoTiffWriter` scope; ensure the context is within a block where `TempIFDEntry` is accessible.

### Prompt Snippet
```text
val entry = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry(...)
val size = entry.valueLength
```

### Common Failure Modes
- **Access Scope:** Attempting to access `TempIFDEntry` from outside the `GeoTiffWriter` package or scope, as it is an internal helper class.
- **Uninitialized Entry:** Calling `valueLength` on an entry that has not been properly populated with data, potentially returning an unexpected or zero length.

### Fix Code Hint
```scala
// Ensure the entry is fully constructed before querying its length
val entry = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter.TempIFDEntry(tag, typeCode, count, value)
val len = entry.valueLength
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.close`

### Signature
```scala
[]():Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:203_

### Goal
Closes the `GeoTiffWriter` instance, flushing any remaining buffered data and releasing system resources associated with the GeoTIFF file output.

### Parameters
_None._

### Input
The caller must have an active `GeoTiffWriter` instance that has been initialized for writing raster data. No specific input parameters are required for the `close` method itself.

### Output
Returns `Unit` — The method performs a side effect of finalizing the file stream and closing the writer; it does not return a value.

### Valid Call Patterns
```scala
// Inferred from signature:
val writer = new GeoTiffWriter(...)
// ... perform write operations ...
writer.close()
```

### LLM Instruction Prompt
- When generating code that uses `GeoTiffWriter`, ensure that `close()` is called after all write operations are complete to prevent data loss or file handle leaks.
- Do not attempt to use the `GeoTiffWriter` instance after calling `close()`.

### Prompt Snippet
```text
// Ensure the writer is closed after processing
writer.close()
```

### Common Failure Modes
- **Resource Leaks:** Failing to call `close()` may leave file handles open, potentially causing issues in long-running Spark applications or when processing large batches of raster files.
- **Post-Close Access:** Attempting to write data to a `GeoTiffWriter` after `close()` has been invoked will result in runtime exceptions.
- **Incomplete Writes:** If the application terminates unexpectedly before `close()` is called, the resulting GeoTIFF file may be corrupted or incomplete.

### Fix Code Hint
```scala
// Use a try-finally block to ensure the writer is closed even if an error occurs
val writer = new GeoTiffWriter(path)
try {
  // Perform write operations
} finally {
  writer.close()
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.closeTileDataOnly`

### Signature
```scala
[]():Path
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:196_

### Goal
Finalizes the writing process for a GeoTIFF file by closing the tile data stream while preserving the existing file structure.

### Parameters
_None._

### Input
The caller must have an active `GeoTiffWriter` instance that has been initialized and populated with raster tile data. This method assumes that the underlying file stream is currently open and ready for finalization.

### Output
Returns `Path` — The file system path to the successfully closed GeoTIFF file.

### Valid Call Patterns
```scala
// Inferred from signature:
val writer = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter(...)
val outputPath: Path = writer.closeTileDataOnly()
```

### LLM Instruction Prompt
- When generating code that uses `GeoTiffWriter`, ensure that `closeTileDataOnly` is called only after all raster tiles have been written to the buffer.
- Do not attempt to write further data to the `GeoTiffWriter` instance after this method has been invoked, as the stream will be closed.

### Prompt Snippet
```text
// Ensure the GeoTiffWriter is closed after processing
val finalPath = writer.closeTileDataOnly()
```

### Common Failure Modes
- **Illegal State Exception:** Calling this method on a `GeoTiffWriter` that has already been closed or was never properly initialized.
- **IO Errors:** Failure to finalize the file due to insufficient permissions or disk space in the output directory.
- **Incomplete Data:** Invoking this method before all required tiles for the raster extent have been processed, resulting in an incomplete or invalid GeoTIFF file.

### Fix Code Hint
```scala
// Ensure proper resource management
try {
  // ... write tiles ...
} finally {
  writer.closeTileDataOnly()
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.makeIFDEntry`

### Signature
```scala
[](tag:Short,valueType:Short,value:Any):TempIFDEntry
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:254_

### Goal
Creates a temporary Image File Directory (IFD) entry for a GeoTIFF file, used to define custom metadata tags during the raster writing process.

### Parameters
- `tag` (`Short`): The unique identifier for the GeoTIFF tag (e.g., standard TIFF tags or GeoTIFF-specific keys).
- `valueType` (`Short`): The data type identifier corresponding to the TIFF specification (e.g., SHORT, LONG, RATIONAL, ASCII).
- `value` (`Any`): The actual data value to be stored in the IFD entry, which must be compatible with the specified `valueType`.

### Input
The caller must provide valid TIFF tag and type codes. This function is typically used internally by `GeoTiffWriter` when constructing the header and metadata structures for raster files being saved to disk.

### Output
Returns `TempIFDEntry` — A temporary object representing a single entry in the GeoTIFF IFD, which can be collected and written into the final GeoTIFF file structure.

### Valid Call Patterns
```scala
// Example inferred from signature (not verified)
val ifdEntry = edu.ucr.cs.bdlab.raptor.GeoTiffWriter.makeIFDEntry(tag, valueType, value)
```

### LLM Instruction Prompt
- When generating code that uses `makeIFDEntry`, ensure the `tag` and `valueType` are valid `Short` values according to the TIFF 6.0 or GeoTIFF specification.
- The `value` parameter is `Any`, but it must be castable or convertible to the format expected by the `valueType` provided.
- This function is a low-level utility; ensure it is used within the context of a `GeoTiffWriter` workflow.

### Prompt Snippet
```text
// Constructing a custom IFD entry for a GeoTIFF
val myTag: Short = 33550 // ModelPixelScaleTag
val myType: Short = 12   // DOUBLE
val myValue: Any = Array(1.0, 1.0, 0.0)
val entry = edu.ucr.cs.bdlab.raptor.GeoTiffWriter.makeIFDEntry(myTag, myType, myValue)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `value` that cannot be serialized into the format specified by `valueType` will cause runtime errors during the writing phase.
- **Invalid Tag/Type:** Using undefined or reserved TIFF tags may result in malformed GeoTIFF files that are unreadable by standard GIS software.
- **Contextual Misuse:** Attempting to use this outside of the `GeoTiffWriter` lifecycle may result in orphaned entries that are never flushed to the output file.

### Fix Code Hint
```scala
// Ensure the value type matches the expected TIFF data type
val entry = edu.ucr.cs.bdlab.raptor.GeoTiffWriter.makeIFDEntry(tag, valueType, value.asInstanceOf[Any])
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.prependGeoTiffHeader`

### Signature
```scala
[](rasterMetadata:RasterMetadata,bitsPerSample:Array[Int],sampleFormats:Array[Int],compression:Int,tileOffsetsLengthsPath:Path,fillValue:Int,outPath:Path,_tileDataPath:Array[Path],opts:BeastOptions):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:545_

### Goal
Constructs a valid GeoTIFF file by prepending a standard header to a collection of raw raster tile data files.

### Parameters
- `rasterMetadata` (`RasterMetadata`): The metadata object defining the raster's spatial extent, CRS, and dimensions.
- `bitsPerSample` (`Array[Int]`): An array specifying the number of bits per sample for each band.
- `sampleFormats` (`Array[Int]`): An array defining the data format (e.g., unsigned integer, float) for each band.
- `compression` (`Int`): The compression scheme identifier to be applied to the output GeoTIFF.
- `tileOffsetsLengthsPath` (`Path`): A path to a file containing the byte offsets and lengths of individual tiles.
- `fillValue` (`Int`): The integer value used to represent NoData pixels in the raster.
- `outPath` (`Path`): The destination path where the final GeoTIFF file will be written.
- `_tileDataPath` (`Array[Path]`): An array of paths pointing to the raw binary tile data files.
- `opts` (`BeastOptions`): Configuration options for the Beast framework.

### Input
The caller must provide pre-processed raw tile data files and a corresponding offsets/lengths file. The `RasterMetadata` must be consistent with the dimensions and tiling scheme of the provided `_tileDataPath` files.

### Output
Returns `Unit` — The function performs a file-system operation to write the final GeoTIFF to `outPath`.

### Valid Call Patterns
```scala
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.GeoTiffWriter.prependGeoTiffHeader(
  metadata, bits, formats, comp, offsetsPath, fill, outPath, tilePaths, options
)
```

### LLM Instruction Prompt
- When calling this method, ensure that `outPath` does not already exist unless the `BeastOptions` are configured to allow overwriting, as standard RDPro operations fail on existing output directories. Ensure `bitsPerSample` and `sampleFormats` arrays match the band count defined in `rasterMetadata`.

### Prompt Snippet
```text
Use edu.ucr.cs.bdlab.raptor.GeoTiffWriter.prependGeoTiffHeader(rasterMetadata, bitsPerSample, sampleFormats, compression, tileOffsetsLengthsPath, fillValue, outPath, _tileDataPath, opts) to finalize a GeoTIFF from raw tiles.
```

### Common Failure Modes
- **File Existence:** The operation will fail if `outPath` already exists and the environment is not configured to overwrite.
- **Metadata Mismatch:** Providing `RasterMetadata` that does not align with the actual byte-size or count of the files in `_tileDataPath` will result in a corrupted or unreadable GeoTIFF.
- **Path Errors:** Providing invalid or inaccessible `Path` objects for the tile data or output destination.

### Fix Code Hint
```scala
// Ensure output directory is cleared if necessary
if (fs.exists(outPath)) fs.delete(outPath, true)
edu.ucr.cs.bdlab.raptor.GeoTiffWriter.prependGeoTiffHeader(meta, bits, formats, comp, offsets, fill, outPath, tiles, opts)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.saveAsGeoTiff`

### Signature
```scala
[type T](rasterRDD:RDD[ITile[T]],outPath:String,opts:BeastOptions):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:476_

### Goal
Persists a distributed raster dataset (`RDD[ITile[T]]`) to storage as a GeoTIFF file or directory of tiles, supporting various compression and compatibility modes.

### Parameters
- `rasterRDD` (`RDD[ITile[T]]`): The distributed raster data to be saved; the type `T` must match the pixel data type (e.g., `Float`, `Int`).
- `outPath` (`String`): The destination file path or directory where the GeoTIFF output will be written.
- `opts` (`BeastOptions`): Configuration options for the writer, such as compression, write mode, or bit compaction settings.

### Input
- **Data:** An `RDD[ITile[T]]` representing the raster.
- **Preconditions:** The output directory must not exist unless the `-overwrite` flag is used.
- **Compatibility:** For `overlay` operations, ensure the raster metadata (resolution, CRS, tile size) is aligned using `reshape` before saving.
- **Type Selection:** The generic type `T` must be consistent with the pixel values (e.g., `IntegerType` or `FloatType`). Bit compaction is only supported for integer pixel values.

### Output
Returns `Unit` — The function writes the raster data to the specified `outPath` in GeoTIFF format. Depending on the `WriteMode` option, this results in either a single file (compatibility mode) or multiple files (distributed mode).

### Valid Call Patterns
```scala
// Basic usage
raster.saveAsGeoTiff("output_path", new BeastOptions())

// Using compression and write mode options
val opts = new BeastOptions()
opts.set(GeoTiffWriter.Compression, TiffConstants.COMPRESSION_LZW)
opts.set(GeoTiffWriter.WriteMode, "compatibility")
raster.saveAsGeoTiff("temperature_f", opts)
```

### LLM Instruction Prompt
- When generating code for `saveAsGeoTiff`, always instantiate a `BeastOptions` object to pass as the third argument.
- If the user specifies compression or write modes, ensure they are set on the `BeastOptions` object using the constants provided by `GeoTiffWriter`.
- Remind the user that `distributed` write mode is more efficient for large datasets, while `compatibility` mode is required for traditional GIS software.

### Prompt Snippet
```text
val opts = new BeastOptions()
// Set options here
raster.saveAsGeoTiff(path, opts)
```

### Common Failure Modes
- **Directory Exists:** The operation will fail if the output directory already exists; use `-overwrite` to force.
- **Type Mismatch:** Attempting to use bit compaction on `Float` pixel types will fail, as it is only supported for integers.
- **Metadata Mismatch:** If the raster RDD was created from multiple sources without proper alignment, the resulting GeoTIFF may have invalid spatial headers.
- **Missing Dependencies:** Ensure `beast-spark` is included in the project dependencies.

### Fix Code Hint
```scala
// Ensure the output directory is cleared if it exists
val opts = new BeastOptions()
opts.set("overwrite", "true")
raster.saveAsGeoTiff("path/to/output", opts)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.setFillValue`

### Signature
```scala
[](value:Int):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:86_

### Goal
Sets the fill value (nodata value) for the `GeoTiffWriter` to be used when writing raster data to disk.

### Parameters
- `value` (`Int`): The integer value to be designated as the "no data" or "fill" value in the output GeoTIFF file.

### Input
- **Preconditions:** The `GeoTiffWriter` instance must be initialized and ready for configuration before the write operation is triggered.
- **Data:** The provided `Int` should correspond to the pixel value intended to represent missing or invalid data in the target raster dataset.

### Output
Returns `Unit` — This method performs an internal state update on the `GeoTiffWriter` instance and does not return a value.

### Valid Call Patterns
```scala
// Inferred from signature:
val writer = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter(...)
writer.setFillValue(-9999)
```

### LLM Instruction Prompt
- When generating code that uses `GeoTiffWriter`, ensure `setFillValue` is called after instantiation but before the final write operation.
- Ensure the `Int` provided matches the expected pixel data type of the raster being processed.

### Prompt Snippet
```text
// Set the nodata value for the GeoTiffWriter
writer.setFillValue(fillValue)
```

### Common Failure Modes
- **Type Mismatch:** Providing a value that does not fit the underlying raster's pixel type (e.g., using a large integer for a byte-based raster).
- **Ordering:** Calling `setFillValue` after the write process has already commenced, which may result in the setting being ignored.
- **Initialization:** Attempting to call this method on a null or uninitialized `GeoTiffWriter` instance.

### Fix Code Hint
```scala
// Ensure the writer is configured before saving
val writer = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter(outputFile)
writer.setFillValue(0) // Set nodata to 0
writer.write(rasterRDD)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.GeoTiffWriter.write`

### Signature
```scala
[](tile:ITile[T]):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/GeoTiffWriter.scala:124_

### Goal
Writes a single raster tile (`ITile[T]`) to a GeoTIFF file format within the RDPro raster processing workflow.

### Parameters
- `tile` (`ITile[T]`): The raster tile data to be serialized, where `T` represents the pixel data type (e.g., `Int`, `Float`).

### Input
- **Data:** An `ITile[T]` object containing the pixel values and associated spatial metadata.
- **Preconditions:** The `GeoTiffWriter` instance must be correctly initialized to point to a valid output path. The pixel type `T` must be compatible with the writer's configuration.
- **Formats:** The operation targets GeoTIFF output.

### Output
Returns `Unit` — The function performs a side-effecting write operation to the filesystem; no value is returned.

### Valid Call Patterns
```scala
// Inferred from signature:
val writer = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter(outputPath)
writer.write(tile)
```

### LLM Instruction Prompt
- When using `GeoTiffWriter.write`, ensure the `ITile[T]` instance is fully materialized or available in the current scope.
- Always verify that the output directory is writable and does not conflict with existing files unless an overwrite policy is explicitly managed.
- Ensure the generic type `T` of the `ITile` matches the expected pixel depth of the target GeoTIFF.

### Prompt Snippet
```text
val writer = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter(path)
writer.write(myTile)
```

### Common Failure Modes
- **Directory Conflicts:** The operation may fail if the output path already exists and the system is not configured to overwrite.
- **Type Mismatch:** Providing an `ITile[T]` with a pixel type incompatible with the writer's internal GeoTIFF encoding settings.
- **Initialization Errors:** Attempting to call `write` on a `GeoTiffWriter` instance that was not properly initialized with a valid file path.

### Fix Code Hint
```scala
// Ensure the output directory is handled and the writer is initialized
val writer = new edu.ucr.cs.bdlab.raptor.GeoTiffWriter("output/path/raster.tif")
try {
  writer.write(tile)
} finally {
  // Close resources if the writer implements AutoCloseable
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDF4Reader`

### Signature
```scala
[]():<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDF4Reader.scala:42_

### Goal
Provides a mechanism for reading and parsing HDF4 formatted raster data within the Raptor framework for subsequent geospatial processing.

### Parameters
_None._

### Input
The caller must provide access to HDF4 files. While Beast supports auto-detection of formats, HDF4 files should be verified for compatibility with the Raptor processing pipeline. Preconditions include ensuring the environment has the necessary HDF4 library support and that the raster data is accessible via the Spark context.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it is expected to provide an interface or object capable of streaming or accessing pixel data from an HDF4 source.

### Valid Call Patterns
```scala
// Example inferred from class signature
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader()
```

### LLM Instruction Prompt
- When using `HDF4Reader`, ensure the target file is a valid HDF4 raster dataset.
- Note that this API is part of the Raptor component; ensure your workflow aligns with Raptor's raster-vector processing requirements.
- Since the return type is unspecified in the provided facts, verify the object's methods via reflection or IDE inspection if the specific return type is required for further processing.

### Prompt Snippet
```text
val hdf4Reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader()
```

### Common Failure Modes
- **Missing Dependencies:** Failure to include the necessary HDF4 native libraries in the Spark executor classpath.
- **Format Incompatibility:** Attempting to read non-HDF4 files or corrupted HDF4 structures.
- **Metadata Mismatch:** If the HDF4 data is used in `overlay` or `raptorJoin` operations, failure to ensure the raster metadata (CRS, resolution) matches other datasets will cause runtime errors. Use `reshape` to align rasters before processing.

### Fix Code Hint
```scala
// Ensure the HDF4 file is accessible and the reader is instantiated correctly
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader()
// Proceed with raster data extraction using the reader instance
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDF4Reader.createDateFilter`

### Signature
```scala
[](dateStart:String,dateEnd:String):PathFilter
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDF4Reader.scala:299_

### Goal
Creates a `PathFilter` to restrict HDF4 file processing to a specific temporal range based on date metadata.

### Parameters
- `dateStart` (`String`): The inclusive start date for the filter, typically formatted as a string (e.g., "YYYY-MM-DD").
- `dateEnd` (`String`): The inclusive end date for the filter, typically formatted as a string (e.g., "YYYY-MM-DD").

### Input
- **Data:** HDF4 raster datasets.
- **Preconditions:** The HDF4 files must contain embedded date metadata that the reader can parse and compare against the provided string range.

### Output
Returns `PathFilter` — A filter object that can be applied to file system operations or directory scans to include only those HDF4 files whose dates fall within the specified range.

### Valid Call Patterns
```scala
// Inferred from signature:
val dateFilter = edu.ucr.cs.bdlab.raptor.HDF4Reader.createDateFilter("2023-01-01", "2023-12-31")
```

### LLM Instruction Prompt
- Use `edu.ucr.cs.bdlab.raptor.HDF4Reader.createDateFilter` when you need to filter a collection of HDF4 files by date before loading them into a `RasterRDD`.
- Ensure the `dateStart` and `dateEnd` strings match the date format expected by the HDF4 metadata headers in your specific dataset.

### Prompt Snippet
```text
val filter = edu.ucr.cs.bdlab.raptor.HDF4Reader.createDateFilter(startDate, endDate)
```

### Common Failure Modes
- **Invalid Date Format:** Providing date strings that do not match the internal metadata format of the HDF4 files, resulting in an empty filter or runtime parsing errors.
- **Missing Metadata:** Applying the filter to HDF4 files that lack the necessary date attributes, which may cause the filter to exclude all files.
- **Incorrect Range:** Setting `dateStart` later than `dateEnd`, which will result in an empty `PathFilter` that matches no files.

### Fix Code Hint
```scala
// Ensure dates are formatted correctly for your HDF4 source
val start = "2023-01-01"
val end = "2023-01-31"
val filter = edu.ucr.cs.bdlab.raptor.HDF4Reader.createDateFilter(start, end)
// Use the filter when listing or reading files from a directory
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDF4Reader.createTileIDFilter`

### Signature
```scala
[](rect:Rectangle2D):PathFilter
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDF4Reader.scala:270_

### Goal
Creates a `PathFilter` that restricts HDF4 file processing to tiles intersecting a specified spatial bounding box.

### Parameters
- `rect` (`Rectangle2D`): A `java.awt.geom.Rectangle2D` representing the spatial extent (bounding box) used to filter the raster tiles.

### Input
- **Data:** HDF4 raster datasets.
- **Preconditions:** The caller must provide a valid `Rectangle2D` object defining the area of interest. This filter is typically used when scanning directories or file lists to avoid loading HDF4 tiles that fall outside the target region.

### Output
Returns `PathFilter` — A filter object that can be applied to file system operations to select only those HDF4 files whose associated tiles overlap with the provided `Rectangle2D`.

### Valid Call Patterns
```scala
import edu.ucr.cs.bdlab.raptor.HDF4Reader
import java.awt.geom.Rectangle2D

val filterRect = new Rectangle2D.Double(x, y, width, height)
val pathFilter = HDF4Reader.createTileIDFilter(filterRect)
```
*(Note: This call pattern is inferred from the API signature as no existing test suite example was found.)*

### LLM Instruction Prompt
- When using `HDF4Reader.createTileIDFilter`, ensure the `Rectangle2D` coordinates align with the coordinate reference system (CRS) of the HDF4 dataset.
- Use this filter to optimize I/O by excluding irrelevant tiles before initiating raster processing or `raptorJoin` operations.

### Prompt Snippet
```text
val filter = edu.ucr.cs.bdlab.raptor.HDF4Reader.createTileIDFilter(new java.awt.geom.Rectangle2D.Double(x, y, w, h))
```

### Common Failure Modes
- **Coordinate Mismatch:** Providing a `Rectangle2D` in a different CRS than the HDF4 data will result in an empty filter or incorrect tile selection.
- **Null Input:** Passing a `null` rectangle will likely trigger a `NullPointerException` during filter evaluation.
- **Unsupported Format:** This filter is specific to HDF4; attempting to use it on non-HDF4 file paths may result in unexpected behavior or failure to match any files.

### Fix Code Hint
```scala
// Ensure the rectangle is properly initialized before passing to the filter
val rect = new java.awt.geom.Rectangle2D.Double(minX, minY, width, height)
val filter = edu.ucr.cs.bdlab.raptor.HDF4Reader.createTileIDFilter(rect)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDF4Reader.getTileOffset`

### Signature
```scala
[](tileID:Int):Long
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDF4Reader.scala:187_

### Goal
Retrieves the byte offset within an HDF4 file for a specific raster tile identified by its unique integer ID.

### Parameters
- `tileID` (`Int`): The unique identifier of the raster tile for which the file offset is requested.

### Input
- **Data Source:** An HDF4 formatted raster file.
- **Preconditions:** The `HDF4Reader` instance must be initialized and successfully associated with a valid HDF4 file. The `tileID` must correspond to a valid tile index within the HDF4 structure.

### Output
Returns `Long` — The absolute byte offset in the HDF4 file where the data for the specified `tileID` begins.

### Valid Call Patterns
```scala
// Inferred from signature:
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader(path)
val offset: Long = reader.getTileOffset(tileID)
```

### LLM Instruction Prompt
- When using `getTileOffset`, ensure the `HDF4Reader` has been properly instantiated with a valid file path.
- The `tileID` must be a non-negative integer representing a valid tile index within the HDF4 dataset.
- Use the returned `Long` value to perform low-level seek operations or direct byte-range reads if necessary.

### Prompt Snippet
```text
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader(hdf4FilePath)
val offset = reader.getTileOffset(tileIndex)
```

### Common Failure Modes
- **Invalid Tile ID:** Providing a `tileID` that does not exist in the HDF4 file may result in an `ArrayIndexOutOfBoundsException` or an incorrect offset value.
- **Uninitialized Reader:** Calling this method on an `HDF4Reader` instance that has not been correctly pointed to a valid HDF4 file will result in runtime errors.
- **File Access Errors:** If the underlying HDF4 file is closed or inaccessible, the method may fail.

### Fix Code Hint
```scala
// Ensure the reader is initialized and the ID is within bounds
if (tileID >= 0 && tileID < reader.getNumTiles) {
  val offset = reader.getTileOffset(tileID)
  // Proceed with file operations
} else {
  throw new IllegalArgumentException("Invalid tileID provided for HDF4Reader")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDF4Reader.initialize`

### Signature
```scala
[](fileSystem:FileSystem,path:String,layer:String,opts:BeastOptions):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDF4Reader.scala:75_

### Goal
Initializes the `HDF4Reader` to prepare for reading specific raster data layers from an HDF4 file within the Raptor framework.

### Parameters
- `fileSystem` (`FileSystem`): The Hadoop `FileSystem` instance used to access the storage where the HDF4 file resides.
- `path` (`String`): The full path to the HDF4 file on the provided `FileSystem`.
- `layer` (`String`): The specific data layer or sub-dataset name within the HDF4 file to be accessed.
- `opts` (`BeastOptions`): Configuration options for the reader, such as specific processing parameters or environment settings.

### Input
- **Data Format:** HDF4 raster files.
- **Preconditions:** The caller must have an active `FileSystem` connection. The `path` must point to a valid HDF4 file, and the `layer` must exist within that file.
- **Compatibility:** As part of the Raptor component, ensure that the raster metadata (resolution, CRS) is compatible with any subsequent `raptorJoin` or overlay operations.

### Output
Returns `Unit` — This method performs internal state initialization for the reader instance and does not return a value.

### Valid Call Patterns
```scala
// Inferred from signature:
val fs = FileSystem.get(new Configuration())
val reader = new HDF4Reader()
reader.initialize(fs, "path/to/data.hdf", "layer_name", new BeastOptions())
```

### LLM Instruction Prompt
- When using `HDF4Reader.initialize`, ensure the `FileSystem` is correctly initialized for the target environment (e.g., HDFS or local).
- Always verify that the `layer` string matches the internal identifier of the sub-dataset in the HDF4 file.
- This method must be called before attempting to read pixel data or metadata from the HDF4 source.

### Prompt Snippet
```text
val reader = new HDF4Reader()
reader.initialize(fileSystem, path, layer, opts)
```

### Common Failure Modes
- **FileNotFoundException:** Occurs if the `path` provided does not exist on the `FileSystem`.
- **Invalid Layer Name:** If the `layer` string does not correspond to a valid sub-dataset in the HDF4 file, the reader may fail to initialize or throw an exception during subsequent read operations.
- **Metadata Mismatch:** If the initialized raster is later used in `raptorJoin` without being reshaped to match other datasets, the join operation may fail due to incompatible grid alignment.

### Fix Code Hint
```scala
// Ensure the path is absolute and the layer name is verified
val fs = FileSystem.get(sparkContext.hadoopConfiguration)
val reader = new HDF4Reader()
try {
  reader.initialize(fs, hdfPath, layerName, new BeastOptions())
} catch {
  case e: Exception => println(s"Failed to initialize HDF4 reader: ${e.getMessage}")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDF4Reader.loadRasterMetadata`

### Signature
```scala
[]():Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDF4Reader.scala:112_

### Goal
Initializes or refreshes the internal metadata state for an HDF4 raster reader instance by parsing the header information of the associated HDF4 file.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.HDF4Reader`. The underlying HDF4 file must be accessible to the reader instance. No specific parameters are passed to this method, as it relies on the state already configured within the `HDF4Reader` object.

### Output
Returns `Unit` — The method performs an in-place update of the reader's internal metadata fields (such as dimensions, coordinate reference system, and data type).

### Valid Call Patterns
```scala
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader(path)
reader.loadRasterMetadata()
```
*(Note: This call pattern is inferred from the signature as no explicit test-suite or README example was provided.)*

### LLM Instruction Prompt
- When generating code that uses `HDF4Reader`, ensure `loadRasterMetadata()` is called after the reader is instantiated but before attempting to access raster properties or perform pixel-level operations.
- Do not pass any arguments to this method.

### Prompt Snippet
```text
// Ensure metadata is loaded before processing
val hdf4Reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader(hdf4FilePath)
hdf4Reader.loadRasterMetadata()
```

### Common Failure Modes
- **Uninitialized Metadata:** Attempting to access raster dimensions or pixel data before calling `loadRasterMetadata()` may result in null pointer exceptions or incorrect default values.
- **File Access Errors:** If the HDF4 file path is invalid or the file is corrupted, this method may throw an exception during the metadata parsing phase.
- **Compatibility:** Ensure the HDF4 file format is supported by the current Beast/Raptor version, as HDF4 support is more limited compared to GeoTIFF.

### Fix Code Hint
```scala
// If metadata access fails, verify the file path and ensure the HDF4 file is not locked or corrupted.
try {
  reader.loadRasterMetadata()
} catch {
  case e: Exception => println(s"Failed to load HDF4 metadata: ${e.getMessage}")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDF4Reader.metadata`

### Signature
```scala
[]:RasterMetadata
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDF4Reader.scala:66_

### Goal
Retrieves the structural and spatial metadata from an HDF4 raster file, providing essential information such as dimensions, resolution, and coordinate reference system (CRS) required for subsequent raster processing.

### Parameters
_None._

### Input
The `HDF4Reader` instance must be initialized with a valid HDF4 file path. The file must be a standard HDF4 raster format supported by the Beast framework. No specific preconditions are required for the metadata call itself, provided the reader has been successfully instantiated.

### Output
Returns `RasterMetadata` — An object containing the raster's spatial extent, pixel dimensions, tile size, and CRS information, which is necessary for aligning rasters during `overlay` or `raptorJoin` operations.

### Valid Call Patterns
```scala
// Inferred from signature:
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader(path)
val meta = reader.metadata()
```

### LLM Instruction Prompt
- When generating code that uses `metadata()`, ensure the `HDF4Reader` is already instantiated with a valid file path.
- Use the returned `RasterMetadata` to verify compatibility (e.g., resolution and CRS) before performing `overlay` or `raptorJoin` operations with other raster datasets.

### Prompt Snippet
```text
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader("path/to/data.hdf")
val metadata = reader.metadata()
```

### Common Failure Modes
- **Initialization Failure:** Calling `metadata()` on a reader that failed to open the HDF4 file due to an invalid path or unsupported HDF4 internal structure.
- **Metadata Mismatch:** Attempting to use the returned metadata to perform an `overlay` operation with another raster that has different resolution or CRS; always use `reshape` to align rasters if metadata does not match.
- **Null Pointer:** If the reader instance is not properly initialized, the call may throw an exception.

### Fix Code Hint
```scala
// Ensure the reader is initialized and metadata is checked for compatibility
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader(hdfPath)
val meta = reader.metadata()
// If meta does not match target raster, use reshape before processing
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDF4Reader.readTile`

### Signature
```scala
[](tileID:Int):ITile[Float]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDF4Reader.scala:163_

### Goal
Reads a specific raster tile from an HDF4 data source into an `ITile[Float]` representation for pixel-level processing.

### Parameters
- `tileID` (`Int`): The unique identifier or index of the tile to be retrieved from the HDF4 dataset.

### Input
- **Data Source:** An HDF4 formatted raster file.
- **Preconditions:** The `HDF4Reader` instance must be initialized and pointed to a valid HDF4 file. The caller must ensure the `tileID` is within the valid range of tiles defined by the HDF4 file's internal structure.

### Output
Returns `ITile[Float]` — A tile object containing pixel data represented as 32-bit floating-point values.

### Valid Call Patterns
```scala
// Inferred from signature:
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader(path)
val tile: ITile[Float] = reader.readTile(tileID)
```

### LLM Instruction Prompt
- When using `readTile`, ensure the `HDF4Reader` is correctly instantiated with the target HDF4 file path.
- Always treat the returned `ITile[Float]` as a read-only structure unless performing explicit pixel-level transformations.
- Ensure the `tileID` is validated against the dataset's metadata to prevent out-of-bounds access.

### Prompt Snippet
```text
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader("path/to/data.hdf")
val tile = reader.readTile(0)
```

### Common Failure Modes
- **Invalid Tile ID:** Providing a `tileID` that does not exist in the HDF4 file will result in an index out-of-bounds exception.
- **Type Mismatch:** This method specifically returns `ITile[Float]`; attempting to cast or treat the output as a different pixel type (e.g., `Int`) will cause runtime errors.
- **Initialization Errors:** Calling `readTile` on an uninitialized or improperly closed `HDF4Reader` instance will fail.

### Fix Code Hint
```scala
// Ensure the reader is initialized and the ID is valid
val reader = new edu.ucr.cs.bdlab.raptor.HDF4Reader(hdfPath)
try {
  val tile = reader.readTile(targetID)
  // Process tile...
} catch {
  case e: IndexOutOfBoundsException => println(s"Tile ID $targetID is invalid.")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDFTile`

### Signature
```scala
[](tileID:Int,data:Array[Byte],valueSize:Int,fillValue:Int,scaleFactor:Double,metadata:RasterMetadata,rasterFeature:RasterFeature):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDFTile.scala:33_

### Goal
Constructs an `HDFTile` object to represent a specific spatial data tile within the Raptor raster-vector processing framework.

### Parameters
- `tileID` (`Int`): A unique identifier for the specific raster tile.
- `data` (`Array[Byte]`): The raw byte array containing the pixel values for the tile.
- `valueSize` (`Int`): The size in bytes of each individual pixel value.
- `fillValue` (`Int`): The integer value used to represent "no data" or empty pixels within the tile.
- `scaleFactor` (`Double`): A multiplier applied to the raw pixel values to convert them to their actual physical units.
- `metadata` (`RasterMetadata`): The metadata object defining the spatial reference, resolution, and dimensions of the raster.
- `rasterFeature` (`RasterFeature`): The associated raster feature context for the tile.

### Input
The caller must provide a valid `RasterMetadata` object and a `RasterFeature` instance. The `data` array must be consistent with the dimensions and pixel depth defined in the `metadata`. This class is specifically designed for HDF4 raster data integration within the Beast framework.

### Output
Returns an instance of `HDFTile` (type unspecified in signature). This object encapsulates the tile's raw data and metadata, enabling it to be used in subsequent Raptor join or transformation operations.

### Valid Call Patterns
```scala
// Example inferred from signature (not verified)
val tile = new edu.ucr.cs.bdlab.raptor.HDFTile(
  tileID, 
  dataArray, 
  valueSize, 
  fillValue, 
  scaleFactor, 
  metadata, 
  rasterFeature
)
```

### LLM Instruction Prompt
- When generating code using `HDFTile`, ensure that the `metadata` and `rasterFeature` are correctly initialized from the source raster dataset.
- Always verify that the `valueSize` matches the expected byte-depth of the raster data type (e.g., 4 bytes for Float).
- Ensure the `scaleFactor` is correctly retrieved from the HDF4 file attributes to maintain data accuracy.

### Prompt Snippet
```text
// Constructing an HDFTile for Raptor processing
val hdfTile = new edu.ucr.cs.bdlab.raptor.HDFTile(id, bytes, size, fill, scale, meta, feature)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not align with the actual dimensions or CRS of the `data` array will cause downstream processing errors in `raptorJoin`.
- **Incorrect Value Size:** Specifying an incorrect `valueSize` will lead to corrupted pixel interpretation during raster-vector joins.
- **Missing Dependencies:** Attempting to instantiate this class without the necessary `beast-spark` dependencies in the classpath.

### Fix Code Hint
```scala
// Ensure metadata is aligned before tile creation
val alignedMetadata = originalMetadata.reshape(targetResolution)
val tile = new edu.ucr.cs.bdlab.raptor.HDFTile(id, data, 4, -9999, 1.0, alignedMetadata, feature)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDFTile.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDFTile.scala:61_

### Goal
Retrieves the underlying data type of the pixels stored within an `HDFTile` instance to ensure compatibility with subsequent raster processing operations.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.HDFTile`. This class is used for handling HDF4 raster data within the Raptor framework. No specific preconditions are required to call this method, as it simply inspects the metadata of the existing tile object.

### Output
Returns `DataType` — The specific data type (e.g., IntegerType, FloatType) of the raster components, which is essential for determining how to interpret pixel values during `mapPixels` or `raptorJoin` operations.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: HDFTile = ... // An initialized HDFTile
val dataType: DataType = tile.componentType()
```

### LLM Instruction Prompt
- When generating code that processes `HDFTile` objects, use `componentType()` to verify the pixel data type before performing arithmetic transformations.
- Ensure that the retrieved `DataType` matches the expected type parameter (e.g., `Float`) used in `RasterRDD[T]` operations to avoid runtime type mismatches.

### Prompt Snippet
```text
val tile = new HDFTile(...)
val dtype = tile.componentType()
// Use dtype to validate or cast pixel operations
```

### Common Failure Modes
- **Type Mismatch:** Attempting to perform floating-point operations on a tile that returns an `IntegerType` via `componentType()`, leading to unexpected truncation or runtime errors.
- **Uninitialized Tile:** Calling `componentType()` on a null or improperly initialized `HDFTile` reference will result in a `NullPointerException`.
- **Compatibility Errors:** Failing to check the `componentType` before `overlay` or `reshape` operations, which require identical metadata and types across raster inputs.

### Fix Code Hint
```scala
// Ensure type safety before processing
val tile = hdfRDD.first()
if (tile.componentType() == FloatType) {
  // Safe to perform float-based transformations
} else {
  // Handle or convert integer-based tiles
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDFTile.getInt`

### Signature
```scala
[](i:Int):Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDFTile.scala:67_

### Goal
Retrieves the integer value of a specific pixel or data element at the given index from an HDF4 raster tile.

### Parameters
- `i` (`Int`): The zero-based index of the pixel or data element within the `HDFTile` structure.

### Input
- **Data:** An initialized `edu.ucr.cs.bdlab.raptor.HDFTile` object.
- **Preconditions:** The index `i` must be within the valid bounds of the tile's data array (0 <= i < total number of pixels in the tile). The underlying HDF4 data must be accessible and correctly loaded into the tile instance.

### Output
Returns `Int` — The integer value stored at the specified index `i` within the raster tile.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: edu.ucr.cs.bdlab.raptor.HDFTile = ...
val pixelValue: Int = tile.getInt(i)
```

### LLM Instruction Prompt
- When generating code that uses `getInt`, ensure the index `i` is validated against the tile's dimensions to prevent `ArrayIndexOutOfBoundsException`.
- Use this method only when working with `HDFTile` objects derived from HDF4 raster sources within the Raptor framework.

### Prompt Snippet
```text
val tile = ... // HDFTile instance
val value = tile.getInt(index)
```

### Common Failure Modes
- **IndexOutOfBoundsException:** Occurs if the provided index `i` is negative or greater than or equal to the total number of elements in the tile.
- **NullPointerException:** Occurs if the `HDFTile` instance has not been properly initialized or the underlying data buffer is null.
- **Type Mismatch:** Attempting to use this method on non-integer raster data may result in unexpected values or runtime errors depending on the underlying HDF4 data type mapping.

### Fix Code Hint
```scala
// Ensure index is within bounds before access
if (i >= 0 && i < tile.length) {
  val value = tile.getInt(i)
} else {
  // Handle invalid index
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDFTile.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):Float
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDFTile.scala:39_

### Goal
Retrieves the raw pixel value at a specific coordinate within an HDF4 raster tile managed by the Raptor framework.

### Parameters
- `i` (`Int`): The row index (vertical coordinate) of the pixel within the tile.
- `j` (`Int`): The column index (horizontal coordinate) of the pixel within the tile.

### Input
The caller must have an initialized `HDFTile` object. This method assumes the tile has been successfully loaded from an HDF4 source. No specific spatial partitioning is required for this individual tile access, but the indices `i` and `j` must be within the bounds of the tile's dimensions.

### Output
Returns `Float` — The pixel value at the specified location, represented as a single-precision floating-point number.

### Valid Call Patterns
```scala
// Inferred from signature: Accessing a pixel at (row, col)
val tile: edu.ucr.cs.bdlab.raptor.HDFTile = ...
val value: Float = tile.getPixelValue(row, col)
```

### LLM Instruction Prompt
- When using `getPixelValue`, ensure the indices `i` and `j` are within the valid range of the `HDFTile` dimensions to avoid `ArrayIndexOutOfBoundsException`.
- This method is intended for direct pixel access; for bulk operations, prefer `mapPixels` or `raptorJoin` on the `RasterRDD` to leverage Spark's distributed processing.

### Prompt Snippet
```text
val tile = ... // HDFTile instance
val pixel = tile.getPixelValue(row, col)
```

### Common Failure Modes
- **Index Out of Bounds:** Providing `i` or `j` values that exceed the tile's internal dimensions will result in a runtime exception.
- **Uninitialized Tile:** Calling this method on a null or improperly initialized `HDFTile` instance will cause a `NullPointerException`.
- **Type Mismatch:** While the return type is `Float`, ensure the underlying HDF4 data is compatible with floating-point representation to avoid unexpected data truncation or interpretation errors.

### Fix Code Hint
```scala
// Ensure bounds checking before access
if (i >= 0 && i < tile.height && j >= 0 && j < tile.width) {
  val value = tile.getPixelValue(i, j)
} else {
  // Handle out-of-bounds case
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDFTile.getShort`

### Signature
```scala
[](i:Int):Short
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDFTile.scala:63_

### Goal
Retrieves the pixel value at a specific linear index from an HDF4 raster tile as a 16-bit signed integer.

### Parameters
- `i` (`Int`): The zero-based linear index of the pixel within the tile's internal data buffer.

### Input
- **Data:** An instance of `edu.ucr.cs.bdlab.raptor.HDFTile` initialized from an HDF4 raster source.
- **Preconditions:** The index `i` must be within the bounds of the tile's data array (0 <= i < total pixels in tile). The underlying HDF4 data must be compatible with `Short` representation.

### Output
Returns `Short` — The pixel value stored at the specified index.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: edu.ucr.cs.bdlab.raptor.HDFTile = ...
val pixelValue: Short = tile.getShort(index)
```

### LLM Instruction Prompt
- When using `getShort`, ensure the index `i` is validated against the tile dimensions to prevent `ArrayIndexOutOfBoundsException`.
- This method is specific to `HDFTile` objects; ensure the raster data source is correctly identified as HDF4 before invoking.

### Prompt Snippet
```text
val tile: edu.ucr.cs.bdlab.raptor.HDFTile = ...
val value = tile.getShort(index)
```

### Common Failure Modes
- **IndexOutOfBoundsException:** Occurs if the provided index `i` is negative or exceeds the total number of pixels in the tile.
- **Type Mismatch:** Attempting to read data that is not stored as 16-bit integers in the HDF4 file may result in unexpected values or data truncation.
- **Null Pointer:** Occurs if the `HDFTile` instance has not been properly initialized or the underlying data buffer is null.

### Fix Code Hint
```scala
// Ensure index is within bounds before access
if (index >= 0 && index < tile.length) {
  val value = tile.getShort(index)
} else {
  // Handle invalid index
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDFTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDFTile.scala:48_

### Goal
Checks if a specific pixel at the given coordinates within an HDF4 raster tile is considered empty or contains no valid data.

### Parameters
- `i` (`Int`): The row index (vertical coordinate) of the pixel within the tile.
- `j` (`Int`): The column index (horizontal coordinate) of the pixel within the tile.

### Input
- **Data:** An instance of `edu.ucr.cs.bdlab.raptor.HDFTile`.
- **Preconditions:** The provided `i` and `j` coordinates must be within the bounds of the tile dimensions. The HDF4 file must be correctly loaded into the `HDFTile` structure.

### Output
Returns `Boolean` — `true` if the pixel at the specified `(i, j)` location is empty (e.g., represents a NoData value or null), and `false` otherwise.

### Valid Call Patterns
```scala
// Example inferred from signature
val tile: HDFTile = ...
val isPixelEmpty: Boolean = tile.isEmpty(row, col)
```

### LLM Instruction Prompt
- When using `isEmpty`, ensure the `i` and `j` indices are validated against the tile's width and height to prevent `ArrayIndexOutOfBoundsException`.
- This method is intended for pixel-level inspection within the Raptor raster processing workflow.

### Prompt Snippet
```text
val tile = ... // HDFTile instance
val isEmpty = tile.isEmpty(i, j)
```

### Common Failure Modes
- **Index Out of Bounds:** Providing `i` or `j` values that exceed the tile's dimensions will result in a runtime exception.
- **Uninitialized Tile:** Calling this method on an `HDFTile` object that has not been properly initialized or populated from the source HDF4 file.
- **Raster Compatibility:** If performing operations across multiple rasters, ensure the `HDFTile` metadata is aligned with other datasets using `reshape` before checking for empty pixels.

### Fix Code Hint
```scala
// Ensure indices are within bounds before checking
if (i >= 0 && i < tile.getHeight && j >= 0 && j < tile.getWidth) {
  val empty = tile.isEmpty(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.HDFTile.numComponents`

### Signature
```scala
[]:Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/HDFTile.scala:59_

### Goal
Retrieves the number of data components (bands or channels) present in the HDF4 raster tile.

### Parameters
_None._

### Input
The caller must have an initialized `edu.ucr.cs.bdlab.raptor.HDFTile` object. The tile must be successfully loaded from a valid HDF4 source. No specific preconditions are required for this method call other than the object being instantiated.

### Output
Returns `Int` — The total count of components (e.g., spectral bands) contained within the HDF4 tile.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: edu.ucr.cs.bdlab.raptor.HDFTile = ...
val components: Int = tile.numComponents()
```

### LLM Instruction Prompt
- When working with `HDFTile` objects in the Raptor framework, use `numComponents()` to determine the dimensionality of the raster data before performing pixel-level transformations or zonal statistics.
- Ensure the `HDFTile` is properly initialized from an HDF4 source before invoking this method.

### Prompt Snippet
```text
val hdfTile = new edu.ucr.cs.bdlab.raptor.HDFTile(...)
val bandCount = hdfTile.numComponents()
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `HDFTile` instance was not correctly initialized or if the underlying HDF4 file handle is closed.
- **Invalid File Format**: If the source file is not a valid HDF4 structure, the tile may fail to report component information correctly.
- **Context Mismatch**: Attempting to use this on non-HDF4 raster types; ensure the object is specifically an instance of `HDFTile`.

### Fix Code Hint
```scala
// Ensure the tile is valid before accessing metadata
if (tile != null) {
  val count = tile.numComponents()
  println(s"Raster contains $count components.")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IRasterReader.getFilePath`

### Signature
```scala
[]:String
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IRasterReader.scala:66_

### Goal
Retrieves the file system path associated with the current `IRasterReader` instance to identify the source of the raster data.

### Parameters
_None._

### Input
The caller must have an initialized `IRasterReader` instance. The reader must have been successfully opened or associated with a valid raster source (e.g., GeoTIFF or HDF4 file) supported by the Beast framework.

### Output
Returns `String` — The absolute or relative file path string of the raster file currently being read by the `IRasterReader`.

### Valid Call Patterns
```scala
// Inferred from signature:
val reader: IRasterReader = ... // initialized reader
val path: String = reader.getFilePath()
```

### LLM Instruction Prompt
- Use this method only when you need to verify or log the source file location of an active `IRasterReader` object.
- Do not assume the path is a local file system path if the reader is configured for distributed storage (e.g., HDFS/S3); treat the return value as an opaque string identifier for the data source.

### Prompt Snippet
```text
val reader: IRasterReader = ...
val sourcePath = reader.getFilePath()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling this method on an uninitialized or null `IRasterReader` reference.
- **Unbound Reader:** If the reader was instantiated without a backing file path, the behavior is implementation-dependent and may return an empty string or null.
- **Context Mismatch:** Attempting to use the returned path string for file I/O operations in a distributed Spark environment where the file may not be locally accessible on all worker nodes.

### Fix Code Hint
```scala
// Ensure the reader is initialized before accessing metadata
if (reader != null) {
  val path = reader.getFilePath()
  println(s"Processing raster from: $path")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IRasterReader.getLayerName`

### Signature
```scala
[]:String
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IRasterReader.scala:72_

### Goal
Retrieves the identifier or name associated with the specific raster layer currently being accessed by the `IRasterReader` instance.

### Parameters
_None._

### Input
The caller must have an initialized instance of a class implementing `edu.ucr.cs.bdlab.raptor.IRasterReader` (e.g., readers for GeoTIFF or HDF4 formats). No specific preconditions are required to call this method, provided the reader has been successfully instantiated and initialized.

### Output
Returns `String` — The name of the raster layer. The format and content of this string are dependent on the underlying file metadata (e.g., the internal layer name defined in a GeoTIFF or HDF4 file).

### Valid Call Patterns
```scala
// Inferred from signature:
val reader: IRasterReader = ... // initialized reader
val layerName: String = reader.getLayerName()
```

### LLM Instruction Prompt
- When using `getLayerName`, ensure the `IRasterReader` instance is fully initialized.
- Do not assume the returned string follows a specific naming convention, as it reflects the source file's internal metadata.
- This method is a parameterless accessor; do not attempt to pass arguments.

### Prompt Snippet
```text
val reader: IRasterReader = ...
val name = reader.getLayerName()
```

### Common Failure Modes
- **NullPointerException:** Occurs if the `IRasterReader` instance is null or has not been properly initialized before calling the method.
- **Metadata Access Errors:** If the underlying raster file is corrupted or lacks a defined layer name, the behavior depends on the specific implementation of the reader (may return an empty string or a default identifier).

### Fix Code Hint
```scala
// Ensure the reader is initialized before access
if (reader != null) {
  val name = reader.getLayerName()
  println(s"Processing layer: $name")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IRasterReader.getTileOffsets`

### Signature
```scala
[]:Array[Long]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IRasterReader.scala:85_

### Goal
Retrieves the byte offsets for each tile within the underlying raster data source to facilitate direct random access or parallel reading of specific raster tiles.

### Parameters
_None._

### Input
The caller must have an initialized `IRasterReader` instance (e.g., for GeoTIFF or HDF4 formats). The reader must be successfully opened and associated with a valid raster file path. No specific preconditions on the Spark environment are required, but the reader must be in a state where the file header has been parsed to identify tile locations.

### Output
Returns `Array[Long]` — An array containing the starting byte offsets for each tile in the raster file, where each index corresponds to the sequential tile ID.

### Valid Call Patterns
```scala
// Inferred from signature:
val reader: edu.ucr.cs.bdlab.raptor.IRasterReader = ...
val offsets: Array[Long] = reader.getTileOffsets()
```

### LLM Instruction Prompt
- When calling `getTileOffsets`, ensure the `IRasterReader` instance is fully initialized.
- Do not assume the array is sorted by spatial location; it is indexed by the internal tile sequence defined by the file format (e.g., GeoTIFF strip or tile order).
- Use this method only when performing low-level I/O or custom indexing operations outside of standard `RasterRDD` transformations.

### Prompt Snippet
```text
val reader = ... // IRasterReader instance
val tileOffsets = reader.getTileOffsets()
```

### Common Failure Modes
- **Uninitialized Reader:** Calling this method on a reader that has not been opened or has been closed will result in a `NullPointerException` or an `IOException`.
- **Unsupported Format:** If the underlying raster format does not support explicit tile offsets (e.g., certain non-tiled raw formats), the method may return an empty array or throw an `UnsupportedOperationException`.
- **Memory Constraints:** For extremely large rasters with millions of tiles, the returned `Array[Long]` may consume significant heap space.

### Fix Code Hint
```scala
// Ensure the reader is valid before accessing offsets
if (reader != null) {
  val offsets = reader.getTileOffsets()
  // Process offsets...
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IRasterReader.initialize`

### Signature
```scala
[](fileSystem:FileSystem,path:String,layer:String,opts:BeastOptions):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IRasterReader.scala:43_

### Goal
Initializes a raster reader instance to prepare for pixel-level data access or metadata extraction from a specified raster file within the Beast framework.

### Parameters
- `fileSystem` (`FileSystem`): The Hadoop `FileSystem` object used to access the underlying storage where the raster data resides.
- `path` (`String`): The file path string pointing to the raster dataset (e.g., GeoTIFF or HDF4 file).
- `layer` (`String`): The specific layer or band identifier within the raster file to be accessed.
- `opts` (`BeastOptions`): Configuration options for the reader, such as processing parameters or format-specific settings.

### Input
The caller must provide a valid `FileSystem` instance and a path to a supported raster format (GeoTIFF or HDF4). The `layer` parameter must correspond to a valid band or sub-dataset within the target file. Preconditions include ensuring the file exists and is accessible via the provided `FileSystem`.

### Output
Returns `Unit` — The method performs an in-place initialization of the `IRasterReader` instance, setting up internal state for subsequent read operations.

### Valid Call Patterns
```scala
// Example inferred from signature (not verified)
val reader: IRasterReader = ... // implementation instance
reader.initialize(fs, "/path/to/raster.tif", "band1", new BeastOptions())
```

### LLM Instruction Prompt
- When calling `initialize`, ensure the `FileSystem` is correctly instantiated for the target storage (e.g., HDFS or local).
- Always provide a valid `BeastOptions` object, even if empty, to satisfy the parameter requirement.
- Ensure the `layer` string matches the internal structure of the raster file; if the file is a single-band GeoTIFF, verify the expected layer naming convention.

### Prompt Snippet
```text
val fs = FileSystem.get(sparkContext.hadoopConfiguration)
val opts = new BeastOptions()
reader.initialize(fs, path, layer, opts)
```

### Common Failure Modes
- **FileNotFoundException**: Occurs if the `path` is incorrect or the `FileSystem` does not have permissions to access the file.
- **Invalid Layer**: Providing a `layer` name that does not exist in the HDF4 or GeoTIFF file will cause initialization to fail.
- **Configuration Mismatch**: Passing `BeastOptions` that conflict with the raster's metadata (e.g., incorrect pixel type assumptions) may lead to errors during subsequent read operations.

### Fix Code Hint
```scala
// Ensure the file system is properly configured before initialization
val conf = sc.hadoopConfiguration
val fs = FileSystem.get(conf)
val reader = new MyRasterReader() // Replace with concrete implementation
reader.initialize(fs, "hdfs:///data/raster.tif", "1", new BeastOptions())
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IRasterReader.isValidTile`

### Signature
```scala
[](tileID:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IRasterReader.scala:94_

### Goal
Determines whether a specific tile index is within the valid range of the raster dataset managed by the `IRasterReader`.

### Parameters
- `tileID` (`Int`): The unique identifier of the raster tile to validate.

### Input
- **Data:** An initialized `IRasterReader` instance (e.g., reading GeoTIFF or HDF4 formats).
- **Preconditions:** The reader must be successfully initialized and associated with a raster source. The `tileID` must correspond to the indexing scheme defined by the specific raster implementation.

### Output
Returns `Boolean` — `true` if the `tileID` exists within the bounds of the raster dataset, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val reader: IRasterReader = ...
val isValid: Boolean = reader.isValidTile(tileID)
```

### LLM Instruction Prompt
- When using `isValidTile`, ensure the `IRasterReader` has been properly initialized.
- Use this method to perform bounds checking before attempting to access or process specific tiles to avoid index-out-of-bounds exceptions.
- The `tileID` is expected to be a non-negative integer consistent with the raster's internal tiling structure.

### Prompt Snippet
```text
val reader: IRasterReader = ...
if (reader.isValidTile(myTileID)) {
  // Proceed with tile processing
}
```

### Common Failure Modes
- **Uninitialized Reader:** Calling `isValidTile` on a reader that has not been opened or has been closed will result in a `NullPointerException` or runtime error.
- **Invalid Indexing:** Passing a negative `tileID` or an index exceeding the total number of tiles in the raster will return `false`.
- **Metadata Mismatch:** If the raster metadata is corrupted or the file is inaccessible, the validation logic may return unexpected results.

### Fix Code Hint
```scala
// Always validate the tile ID before retrieval
if (reader.isValidTile(tileID)) {
  val tile = reader.readTile(tileID)
  // process tile
} else {
  throw new IllegalArgumentException(s"Tile ID $tileID is out of bounds.")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IntersectionsIterator`

### Signature
```scala
[](FileIDs:Array[Int],intersectionsList:Array[Intersections]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IntersectionsIterator.scala:15_

### Goal
Provides an iterator to traverse spatial intersections between raster files and vector geometries within the Raptor framework.

### Parameters
- `FileIDs` (`Array[Int]`): An array of integer identifiers corresponding to the raster files involved in the intersection operation.
- `intersectionsList` (`Array[Intersections]`): An array of `Intersections` objects representing the calculated spatial overlaps between the vector features and the specified raster files.

### Input
The caller must provide valid `FileIDs` that map to the raster datasets currently loaded in the Raptor context and a pre-computed `intersectionsList` derived from a spatial join or intersection analysis. Ensure that the raster data is properly indexed and aligned before invoking this iterator.

### Output
Returns an iterator (type unspecified in signature) that yields intersection data points or segments, allowing for pixel-level access to the overlapping regions between the vector features and the raster files.

### Valid Call Patterns
```scala
// Example inferred from signature
val iterator = new edu.ucr.cs.bdlab.raptor.IntersectionsIterator(fileIDs, intersectionsList)
```

### LLM Instruction Prompt
- When using `IntersectionsIterator`, ensure that the `FileIDs` array matches the indices used during the raster-vector join process.
- The `intersectionsList` must be populated with valid `Intersections` objects; do not pass null or empty arrays.
- This class is intended for internal Raptor processing; ensure the Spark environment is initialized before instantiation.

### Prompt Snippet
```text
val iterator = new edu.ucr.cs.bdlab.raptor.IntersectionsIterator(fileIDs, intersectionsList)
```

### Common Failure Modes
- **Index Mismatch:** Providing `FileIDs` that do not correspond to the raster files used to generate the `intersectionsList` will result in runtime errors or empty results.
- **Uninitialized Context:** Attempting to use the iterator outside of a Spark-enabled Raptor workflow may lead to null pointer exceptions or missing metadata.
- **Memory Constraints:** Large `intersectionsList` arrays may cause memory pressure; ensure the input is partitioned appropriately if working with massive datasets.

### Fix Code Hint
```scala
// Ensure FileIDs and intersectionsList are correctly aligned
val fileIDs: Array[Int] = Array(0, 1)
val intersectionsList: Array[Intersections] = ... // Result from spatial join
val iterator = new edu.ucr.cs.bdlab.raptor.IntersectionsIterator(fileIDs, intersectionsList)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IntersectionsIterator.hasNext`

### Signature
```scala
[]:Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IntersectionsIterator.scala:32_

### Goal
Checks if there are additional intersection elements remaining in the `IntersectionsIterator` during a raster-vector join operation.

### Parameters
_None._

### Input
The caller must have an initialized `IntersectionsIterator` instance. This iterator is typically used internally within the Raptor join workflow to traverse intersections between raster tiles and vector geometries. No specific file formats are required as input to this method, as it operates on an existing in-memory iterator state.

### Output
Returns `Boolean` — `true` if there are more intersections to process, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val iterator: IntersectionsIterator = ... // obtained from Raptor join process
if (iterator.hasNext) {
  val nextIntersection = iterator.next()
}
```

### LLM Instruction Prompt
- Use this method to guard calls to `next()` when iterating over raster-vector intersection results.
- Do not assume the iterator is thread-safe; it is intended for sequential traversal of intersection data.
- Ensure the `IntersectionsIterator` has been properly initialized by a Raptor join operation before calling `hasNext`.

### Prompt Snippet
```text
val iterator = ... // IntersectionsIterator instance
while (iterator.hasNext) {
  val intersection = iterator.next()
  // process intersection
}
```

### Common Failure Modes
- **NullPointerException:** Occurs if the `IntersectionsIterator` instance is null or was not correctly initialized by the Raptor join process.
- **Concurrent Modification:** Attempting to iterate while the underlying raster or vector data structures are being modified by another Spark task.
- **Exhausted Iterator:** Calling `next()` without checking `hasNext()` may result in a `NoSuchElementException` if the iterator is already empty.

### Fix Code Hint
```scala
// Always wrap iterator access in a hasNext check
if (iterator != null && iterator.hasNext) {
  val item = iterator.next()
  // ...
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IntersectionsIterator.next`

### Signature
```scala
[]:scala.Tuple2[Long, PixelRange]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IntersectionsIterator.scala:40_

### Goal
Retrieves the next intersection between a vector geometry and the raster grid, returning the associated feature ID and the corresponding range of pixels.

### Parameters
_None._

### Input
The iterator must be initialized within a Raptor join context where a vector dataset is being processed against a raster dataset. No explicit parameters are passed to `next()`, as the state is maintained internally by the `IntersectionsIterator` instance.

### Output
Returns `scala.Tuple2[Long, PixelRange]` — A tuple where the `Long` represents the unique identifier of the intersecting vector feature, and the `PixelRange` defines the specific spatial extent of pixels covered by that feature within the raster.

### Valid Call Patterns
```scala
// Inferred usage pattern for iterating through intersections
val iterator: IntersectionsIterator = ... // obtained from Raptor join logic
while (iterator.hasNext) {
  val (featureId, pixelRange) = iterator.next()
  // Process the intersection
}
```

### LLM Instruction Prompt
- Use `scala:edu.ucr.cs.bdlab.raptor.IntersectionsIterator.next` only when iterating over a pre-initialized `IntersectionsIterator` object.
- Always check `hasNext` before calling `next` to avoid `NoSuchElementException`.
- The returned `Long` corresponds to the feature ID, and `PixelRange` provides the pixel-level mapping for the current intersection.

### Prompt Snippet
```text
val (featureId, pixelRange) = iterator.next()
```

### Common Failure Modes
- **NoSuchElementException**: Occurs if `next()` is called when `hasNext` is false.
- **Iterator State**: Calling `next()` on an iterator that has not been properly initialized or has already been exhausted.
- **Context Mismatch**: Attempting to use this iterator outside of the Raptor raster-vector join workflow where the underlying raster metadata is not available.

### Fix Code Hint
```scala
if (iterator.hasNext) {
  val result = iterator.next()
  // Handle result
} else {
  // Handle end of iteration
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IntersectionsIterator.orderingByPolygonID`

### Signature
```scala
[type A <: IntersectionsIterator]:Ordering[A]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IntersectionsIterator.scala:56_

### Goal
Provides an `Ordering` implementation to sort `IntersectionsIterator` instances based on their associated polygon ID.

### Parameters
_None._

### Input
The caller must have an instance or collection of `IntersectionsIterator` (or its subclasses). This method is typically used when preparing intersection data for operations that require sorted input, such as grouping or merging intersections by their spatial feature identifier.

### Output
Returns `Ordering[A]` — A standard Scala `Ordering` object that defines the comparison logic for sorting `IntersectionsIterator` objects by their polygon ID field.

### Valid Call Patterns
```scala
// Inferred from signature:
val ordering = edu.ucr.cs.bdlab.raptor.IntersectionsIterator.orderingByPolygonID[IntersectionsIterator]
```

### LLM Instruction Prompt
- Use this method when you need to sort a collection of `IntersectionsIterator` objects by their polygon ID.
- Ensure the type parameter `A` is a subtype of `IntersectionsIterator`.
- This method does not take arguments; it returns a comparator that can be passed to standard Scala collection sorting methods (e.g., `.sorted(ordering)`).

### Prompt Snippet
```text
val polygonOrdering = edu.ucr.cs.bdlab.raptor.IntersectionsIterator.orderingByPolygonID[IntersectionsIterator]
val sortedIntersections = intersections.sorted(polygonOrdering)
```

### Common Failure Modes
- **Type Mismatch:** Attempting to use the returned `Ordering` on objects that do not extend `IntersectionsIterator`.
- **Null Pointer:** If the `IntersectionsIterator` objects being sorted have uninitialized or null polygon IDs, the comparison logic may throw an exception depending on the underlying implementation of the ID field.
- **Contextual Sorting:** Forgetting that this only sorts by ID; if secondary sorting (e.g., by pixel value or coordinate) is required, this `Ordering` must be chained using `Ordering.thenBy`.

### Fix Code Hint
```scala
import edu.ucr.cs.bdlab.raptor.IntersectionsIterator

// Ensure the iterator type is correctly specified
val myOrdering: Ordering[IntersectionsIterator] = IntersectionsIterator.orderingByPolygonID[IntersectionsIterator]
val sortedData = myIteratorList.sorted(myOrdering)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IntersectionsIterator1`

### Signature
```scala
[](FileIDs:util.ArrayList[Int],intersectionsList:util.ArrayList[Intersections]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IntersectionsIterator1.scala:6_

### Goal
Provides an iterator to traverse intersection data between raster files and vector geometries within the Raptor processing framework.

### Parameters
- `FileIDs` (`util.ArrayList[Int]`): A list of integer identifiers corresponding to the raster files involved in the intersection operation.
- `intersectionsList` (`util.ArrayList[Intersections]`): A list of `Intersections` objects representing the calculated spatial overlaps between the vector features and the specified raster files.

### Input
The caller must provide valid `ArrayList` collections containing initialized `FileIDs` and `Intersections` objects. This function assumes that the spatial intersection calculations have already been performed and the resulting `Intersections` objects are populated.

### Output
Returns an iterator (type unspecified in API metadata) that allows sequential access to the intersection data points.

### Valid Call Patterns
```scala
// Example inferred from signature
val fileIds = new java.util.ArrayList[Int]()
val intersections = new java.util.ArrayList[edu.ucr.cs.bdlab.raptor.Intersections]()
// ... populate lists ...
val iterator = new edu.ucr.cs.bdlab.raptor.IntersectionsIterator1(fileIds, intersections)
```

### LLM Instruction Prompt
- When using `IntersectionsIterator1`, ensure that the `FileIDs` list corresponds exactly to the raster sources used to generate the `intersectionsList`.
- This class is a low-level iterator; ensure that the input `ArrayList` objects are not null to avoid `NullPointerException`.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.IntersectionsIterator1
import java.util.ArrayList
// Initialize lists and instantiate IntersectionsIterator1
```

### Common Failure Modes
- **Empty Lists:** Passing empty `ArrayList` objects may result in an iterator that yields no elements or throws an exception depending on the internal implementation.
- **Mismatched IDs:** Providing `FileIDs` that do not align with the data contained in the `intersectionsList` will lead to incorrect processing results during iteration.
- **Null Inputs:** Passing `null` for either parameter will cause a runtime exception.

### Fix Code Hint
```scala
// Ensure lists are initialized before passing to the constructor
val fileIds = new java.util.ArrayList[Int]()
val intersections = new java.util.ArrayList[edu.ucr.cs.bdlab.raptor.Intersections]()
// Populate lists before instantiation
val it = new edu.ucr.cs.bdlab.raptor.IntersectionsIterator1(fileIds, intersections)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IntersectionsIterator1.hasNext`

### Signature
```scala
[]:Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IntersectionsIterator1.scala:19_

### Goal
Checks if there are additional intersection elements remaining in the `IntersectionsIterator1` sequence during raster-vector processing.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.IntersectionsIterator1`. This iterator is typically used internally within the Raptor join workflow to traverse intersections between raster tiles and vector geometries.

### Output
Returns `Boolean` — `true` if there are more elements to iterate over, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred usage pattern
val iterator: IntersectionsIterator1 = ... // obtained from Raptor processing
if (iterator.hasNext) {
  val nextItem = iterator.next()
}
```

### LLM Instruction Prompt
- When generating code that uses `IntersectionsIterator1`, always wrap the call to `next()` with a check to `hasNext()` to prevent `NoSuchElementException`.
- Ensure the iterator is properly initialized within the context of a Raptor raster-vector join operation.

### Prompt Snippet
```text
// Check for remaining intersections before calling next()
if (iterator.hasNext) {
  val intersection = iterator.next()
  // process intersection
}
```

### Common Failure Modes
- **Empty Iterator:** Calling `next()` without verifying `hasNext()` will result in a `NoSuchElementException` if the iterator is exhausted.
- **Lifecycle Mismatch:** Attempting to use the iterator after the underlying raster-vector join RDD partition has been closed or cleared.

### Fix Code Hint
```scala
// Always guard iteration with hasNext
while (iterator.hasNext) {
  val item = iterator.next()
  // Logic here
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IntersectionsIterator1.next`

### Signature
```scala
[]():scala.Tuple2[Long, PixelRange]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IntersectionsIterator1.scala:21_

### Goal
Retrieves the next intersection element from the iterator, providing a mapping between a feature identifier and its corresponding range of raster pixels.

### Parameters
_None._

### Input
The caller must ensure the `IntersectionsIterator1` instance has been initialized and that `hasNext` has been checked to confirm the availability of further elements. This iterator is typically used within the Raptor join workflow where vector geometries intersect with raster tiles.

### Output
Returns `scala.Tuple2[Long, PixelRange]` — A tuple where the `Long` represents the unique identifier of the intersecting feature, and the `PixelRange` defines the specific subset of pixels within the raster that overlap with that feature.

### Valid Call Patterns
```scala
// Inferred from signature:
val iterator: IntersectionsIterator1 = ... // initialized iterator
if (iterator.hasNext) {
  val nextIntersection: (Long, PixelRange) = iterator.next()
}
```

### LLM Instruction Prompt
- When using `IntersectionsIterator1.next`, always wrap the call in a conditional check using `hasNext` to prevent `NoSuchElementException`.
- Treat the returned `Long` as the feature ID and the `PixelRange` as the spatial extent of the raster data associated with that feature.

### Prompt Snippet
```text
val (featureId, pixelRange) = iterator.next()
```

### Common Failure Modes
- **NoSuchElementException**: Occurs if `next()` is called when `hasNext` is false.
- **Iterator Exhaustion**: Attempting to iterate beyond the bounds of the intersection set.
- **Uninitialized Iterator**: Calling `next()` on an iterator that has not been properly linked to a raster-vector join context.

### Fix Code Hint
```scala
// Always guard with hasNext
while (iterator.hasNext) {
  val (id, range) = iterator.next()
  // Process intersection
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.IntersectionsIterator1.orderingByPolygonID`

### Signature
```scala
[type A <: IntersectionsIterator1]:Ordering[A]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/IntersectionsIterator1.scala:48_

### Goal
Provides a standard Scala `Ordering` implementation to sort `IntersectionsIterator1` instances based on their associated polygon ID.

### Parameters
_None._

### Input
The caller must be working with a collection or iterator of `IntersectionsIterator1` objects (or subclasses thereof). No specific file format is required as this is an in-memory utility for sorting existing iterator objects.

### Output
Returns `Ordering[A]` — A comparator that defines the natural sort order for `IntersectionsIterator1` objects based on the underlying polygon identifier.

### Valid Call Patterns
```scala
// Inferred from signature:
val ordering = edu.ucr.cs.bdlab.raptor.IntersectionsIterator1.orderingByPolygonID[IntersectionsIterator1]
val sortedIntersections = myIntersections.toList.sorted(ordering)
```

### LLM Instruction Prompt
- Use this method when you need to sort or group `IntersectionsIterator1` objects by their polygon ID.
- Ensure the type parameter `A` is a subtype of `IntersectionsIterator1`.
- This is a static utility method; call it directly from the `IntersectionsIterator1` companion object.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.IntersectionsIterator1
val polyOrdering = IntersectionsIterator1.orderingByPolygonID[IntersectionsIterator1]
```

### Common Failure Modes
- **Type Mismatch:** Attempting to use the ordering on objects that do not extend `IntersectionsIterator1`.
- **Null Pointer:** If the `IntersectionsIterator1` instances being sorted have uninitialized or null polygon IDs, the `Ordering` may throw an exception during comparison.
- **Performance:** Sorting large collections of iterators in-memory can be expensive; ensure the dataset size is appropriate for the driver memory if collecting to a list.

### Fix Code Hint
```scala
// Ensure the type is correctly specified to satisfy the generic constraint
val ordering = edu.ucr.cs.bdlab.raptor.IntersectionsIterator1.orderingByPolygonID[MyIntersectionsSubclass]
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MapPixelsTile`

### Signature
```scala
[](tile:ITile[T],mapFunction:_root_.scala.Function1[T, U])(evidence$1:ClassTag[U],u:ClassTag[U]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MapPixelsTile.scala:33_

### Goal
Applies a transformation function to every pixel value within a single raster tile, producing a new tile with the mapped values.

### Parameters
- `tile` (`ITile[T]`): The input raster tile containing pixels of type `T`.
- `mapFunction` (`_root_.scala.Function1[T, U]`): A function that defines the transformation to be applied to each pixel value of type `T`, resulting in a new value of type `U`.
- `evidence$1` (`ClassTag[U]`): A `ClassTag` required by the Scala compiler to handle the generic type `U` at runtime.
- `u` (`ClassTag[U]`): A `ClassTag` required by the Scala compiler to handle the generic type `U` at runtime.

### Input
- **Data:** An `ITile[T]` object representing a portion of a raster dataset (e.g., GeoTIFF or HDF4).
- **Preconditions:** The input `tile` must be initialized and contain valid pixel data. The `mapFunction` must be serializable if used within a Spark transformation context.

### Output
Returns `unspecified` — The resulting `ITile[U]` containing the transformed pixel values.

### Valid Call Patterns
```scala
// Inferred usage pattern based on signature
val transformedTile = new edu.ucr.cs.bdlab.raptor.MapPixelsTile(tile, (pixel: T) => transformedValue)
```

### LLM Instruction Prompt
- When using `MapPixelsTile`, ensure the input `tile` is of the correct pixel type `T` expected by your `mapFunction`.
- Always provide the `ClassTag` for the output type `U` if the compiler cannot infer it automatically.
- This operation is intended for pixel-level transformations; for global raster operations, consider `RasterRDD.mapPixels`.

### Prompt Snippet
```text
val result = new edu.ucr.cs.bdlab.raptor.MapPixelsTile(myTile, (p: Float) => p * 2.0f)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `mapFunction` that expects a type different from the pixel type `T` stored in the `ITile`.
- **Missing ClassTag:** Failing to provide the `ClassTag` for the output type `U` will result in a compilation error regarding implicit evidence.
- **Null Tiles:** Passing a null or uninitialized `ITile` will cause a `NullPointerException` during the mapping process.

### Fix Code Hint
```scala
import edu.ucr.cs.bdlab.raptor.MapPixelsTile
import scala.reflect.ClassTag

// Example: Doubling pixel values in a Float tile
val mapper = new MapPixelsTile(inputTile, (p: Float) => p * 2.0f)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MapPixelsTile.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):U
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MapPixelsTile.scala:37_

### Goal
Retrieves the raw pixel value at a specific coordinate within a `MapPixelsTile` instance during raster processing workflows.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must have an initialized `MapPixelsTile` object. This method is typically used within pixel-level transformation functions or custom raster processing logic where individual pixel access is required. No specific file format is required at the time of calling, as the tile is already loaded into memory.

### Output
Returns `U` — The value of the pixel at the specified `(i, j)` location. The type `U` corresponds to the underlying pixel data type of the tile (e.g., `Float`, `Int`).

### Valid Call Patterns
```scala
// Inferred from signature: accessing a pixel at (10, 20)
val pixelValue = tile.getPixelValue(10, 20)
```

### LLM Instruction Prompt
- When generating code that uses `getPixelValue`, ensure the `i` and `j` indices are within the bounds of the tile dimensions to avoid `ArrayIndexOutOfBoundsException`.
- Use this method only when iterating over or accessing specific pixels within a `MapPixelsTile` object.

### Prompt Snippet
```text
val tile: MapPixelsTile = ... // initialized tile
val value = tile.getPixelValue(i, j)
```

### Common Failure Modes
- **IndexOutOfBounds:** Providing `i` or `j` values that exceed the tile's width or height dimensions.
- **Type Mismatch:** Expecting a specific numeric type (e.g., `Float`) when the underlying `MapPixelsTile` contains a different type (e.g., `Int`), as `U` is generic.
- **Null Pointer:** Calling this method on an uninitialized or null `MapPixelsTile` reference.

### Fix Code Hint
```scala
// Ensure bounds checking before access
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  val value = tile.getPixelValue(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MaskTile`

### Signature
```scala
[](tile:ITile[T],mask:Array[Long]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MaskTile.scala:28_

### Goal
Applies a bitmask to a raster tile to filter or flag pixel values based on the provided `Long` array mask.

### Parameters
- `tile` (`ITile[T]`): The input raster tile of type `T` to be processed.
- `mask` (`Array[Long]`): An array of `Long` values representing the mask to be applied to the tile pixels.

### Input
- **Data:** Requires an `ITile[T]` object (e.g., from a `RasterRDD[T]`) and a corresponding `Array[Long]` mask.
- **Preconditions:** The `tile` must be compatible with the mask structure. As with other RDPro raster operations, ensure the tile metadata (resolution, CRS, tile size) is aligned before processing.

### Output
Returns `unspecified` — The result of the masking operation on the input tile.

### Valid Call Patterns
```scala
// Example inferred from signature
val maskedTile = new edu.ucr.cs.bdlab.raptor.MaskTile(tile, mask)
```

### LLM Instruction Prompt
- When using `MaskTile`, ensure the `tile` type `T` matches the expected pixel data type (e.g., `Float`, `Int`).
- The `mask` parameter must be an `Array[Long]`.
- This operation is typically used within pixel-level transformation workflows in RDPro.

### Prompt Snippet
```text
val maskedTile = new edu.ucr.cs.bdlab.raptor.MaskTile(tile, mask)
```

### Common Failure Modes
- **Type Mismatch:** Providing a tile with a pixel type that does not align with the expected `ITile[T]` generic constraints.
- **Metadata Incompatibility:** Attempting to mask tiles that have not been reshaped or aligned to the same grid/CRS as the mask source.
- **Null Inputs:** Passing a null `tile` or `mask` will result in a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure the tile is properly initialized and the mask is correctly sized
val tile: ITile[Float] = ... 
val mask: Array[Long] = ... 
val masked = new edu.ucr.cs.bdlab.raptor.MaskTile(tile, mask)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MaskTile.getPixelOffset`

### Signature
```scala
[](i:Int,j:Int):Long
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MaskTile.scala:40_

### Goal
Calculates the linear memory offset for a specific pixel coordinate within a `MaskTile` to facilitate efficient pixel-level access.

### Parameters
- `i` (`Int`): The row index (y-coordinate) of the pixel within the tile.
- `j` (`Int`): The column index (x-coordinate) of the pixel within the tile.

### Input
The caller must have an initialized `MaskTile` instance. This method assumes the provided `i` and `j` coordinates are within the bounds of the tile dimensions.

### Output
Returns `Long` — The calculated linear index (offset) corresponding to the 2D coordinate `(i, j)` within the underlying data structure of the tile.

### Valid Call Patterns
```scala
// Inferred from signature:
val offset = maskTile.getPixelOffset(i, j)
```

### LLM Instruction Prompt
- When generating code that accesses pixels in a `MaskTile`, use `getPixelOffset(i, j)` to map 2D coordinates to a 1D array index.
- Ensure that `i` and `j` are validated against the tile's width and height before calling this method to avoid out-of-bounds errors.

### Prompt Snippet
```text
// Accessing a pixel in a MaskTile
val offset = tile.getPixelOffset(row, col)
val pixelValue = tile.data(offset.toInt)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if `i` or `j` are negative or exceed the dimensions of the `MaskTile`.
- **Invalid Tile State**: Calling this on an uninitialized or null `MaskTile` instance will result in a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure bounds checking before offset calculation
if (i >= 0 && i < tile.height && j >= 0 && j < tile.width) {
  val offset = tile.getPixelOffset(i, j)
  // Proceed with access
} else {
  // Handle out-of-bounds coordinate
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MaskTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MaskTile.scala:46_

### Goal
Determines whether a specific pixel at the given coordinates within a `MaskTile` is marked as empty or masked out.

### Parameters
- `i` (`Int`): The row index of the pixel within the tile.
- `j` (`Int`): The column index of the pixel within the tile.

### Input
The caller must provide a valid `MaskTile` instance. The indices `i` and `j` must be within the bounds of the tile dimensions. This method is typically used during raster-vector processing (Raptor) to check the validity of pixels before performing zonal statistics or pixel-level transformations.

### Output
Returns `Boolean` — `true` if the pixel at `(i, j)` is considered empty (masked), and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val maskTile: MaskTile = ... // Obtained from a Raptor operation
val isPixelEmpty: Boolean = maskTile.isEmpty(i, j)
```

### LLM Instruction Prompt
- When using `isEmpty`, ensure the `i` and `j` coordinates are validated against the tile's dimensions to avoid `ArrayIndexOutOfBoundsException`.
- Use this method to filter out NoData or masked pixels when performing custom pixel-level calculations on `RasterRDD` data.

### Prompt Snippet
```text
val isMasked = tile.isEmpty(row, col)
if (!isMasked) {
  // Perform calculation on valid pixel
}
```

### Common Failure Modes
- **Out of Bounds:** Providing `i` or `j` values that exceed the tile's width or height will result in a runtime exception.
- **Uninitialized Tile:** Calling this method on a null or uninitialized `MaskTile` reference will throw a `NullPointerException`.
- **Metadata Mismatch:** If the `MaskTile` does not align with the expected raster grid (e.g., after a transformation without proper `reshape`), the results of `isEmpty` may not reflect the intended spatial data.

### Fix Code Hint
```scala
// Ensure indices are within bounds before checking
if (i >= 0 && i < tile.getHeight && j >= 0 && j < tile.getWidth) {
  val empty = tile.isEmpty(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MaskTile.setMask`

### Signature
```scala
[](i:Int,j:Int):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MaskTile.scala:42_

### Goal
Sets the mask value for a specific pixel coordinate within a `MaskTile` to indicate that the pixel should be excluded or masked during subsequent raster processing operations.

### Parameters
- `i` (`Int`): The row index (y-coordinate) of the pixel within the tile.
- `j` (`Int`): The column index (x-coordinate) of the pixel within the tile.

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.MaskTile`. The indices `i` and `j` must be within the bounds of the tile dimensions; otherwise, an `ArrayIndexOutOfBoundsException` may occur.

### Output
Returns `Unit` — The method performs an in-place update to the internal mask state of the `MaskTile` object.

### Valid Call Patterns
```scala
// Inferred from signature:
val maskTile = new edu.ucr.cs.bdlab.raptor.MaskTile(width, height)
maskTile.setMask(i, j)
```

### LLM Instruction Prompt
- When generating code that uses `setMask`, ensure the `MaskTile` instance is already instantiated.
- Always verify that the `i` and `j` coordinates are within the valid range `[0, width-1]` and `[0, height-1]` for the specific tile to avoid runtime exceptions.
- Use this method when performing pixel-level filtering or masking within a Raptor raster-vector workflow.

### Prompt Snippet
```text
// Example of masking a pixel at (i, j) in a MaskTile
maskTile.setMask(i, j)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if the provided `i` or `j` values exceed the dimensions of the `MaskTile`.
- **NullPointerException**: Occurs if the `MaskTile` instance has not been properly initialized before calling `setMask`.
- **Logical Errors**: Masking pixels that are required for subsequent `raptorJoin` or raster transformation operations, leading to unexpected empty results in the output.

### Fix Code Hint
```scala
// Ensure bounds checking before calling setMask
if (i >= 0 && i < tileHeight && j >= 0 && j < tileWidth) {
  maskTile.setMask(i, j)
} else {
  // Handle out-of-bounds index
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MaskTileSerializer`

### Signature
```scala
[]():<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MaskTileSerializer.scala:26_

### Goal
Provides a serialization mechanism for mask tiles within the Raptor raster-vector processing framework to facilitate data transfer or storage of raster mask information.

### Parameters
_None._

### Input
The caller must ensure that the environment is configured for Raptor raster processing. As this is a serializer class, it is typically invoked within the context of a Spark transformation or a raster-vector join operation where tile data is being processed or persisted.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it represents the serialized representation of a mask tile used within the Beast framework.

### Valid Call Patterns
```scala
// Inferred from signature: instantiation of the serializer class
val serializer = new edu.ucr.cs.bdlab.raptor.MaskTileSerializer()
```

### LLM Instruction Prompt
- When generating code involving `MaskTileSerializer`, ensure it is used within the context of Raptor raster processing workflows.
- Do not assume specific return types for this constructor; treat it as an internal utility for tile serialization.
- Ensure that any raster data being processed is compatible with Beast's `RasterRDD` requirements (e.g., matching metadata/resolution).

### Prompt Snippet
```text
val serializer = new edu.ucr.cs.bdlab.raptor.MaskTileSerializer()
```

### Common Failure Modes
- **Context Mismatch:** Attempting to use the serializer outside of a Spark/Beast raster processing pipeline, which may lead to serialization errors if the underlying tile data is not properly initialized.
- **Metadata Incompatibility:** If used as part of a larger raster operation, failure to ensure raster metadata (resolution, CRS, tile size) matches across datasets will cause downstream failures in `overlay` or `raptorJoin` operations.
- **Missing Dependencies:** Failure to include the `beast-spark` dependency in the project's `pom.xml` or build configuration will result in `ClassNotFoundException`.

### Fix Code Hint
```scala
// Ensure the project is configured with beast-spark dependencies
// and that raster data is properly aligned before serialization
import edu.ucr.cs.bdlab.raptor.MaskTileSerializer

val serializer = new MaskTileSerializer()
// Use serializer within the appropriate Raptor processing context
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MaskTileSerializer.read`

### Signature
```scala
[](kryo:Kryo,input:Input,tileClass:Class[MaskTile[T]]):MaskTile[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MaskTileSerializer.scala:32_

### Goal
Deserializes a `MaskTile[T]` object from a Kryo input stream, enabling efficient binary serialization of masked raster tiles within the Raptor framework.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for the deserialization process.
- `input` (`Input`): The input stream containing the serialized binary data of the `MaskTile`.
- `tileClass` (`Class[MaskTile[T]]`): The class type of the `MaskTile` to be instantiated, ensuring type safety for the generic pixel type `T`.

### Input
The function expects a valid `Input` stream previously populated by a corresponding `MaskTileSerializer.write` operation. The `MaskTile` must contain valid pixel data and mask information consistent with the expected generic type `T` (e.g., `Float`, `Int`).

### Output
Returns `MaskTile[T]` — A reconstructed `MaskTile` object containing the pixel values and associated mask metadata.

### Valid Call Patterns
```scala
// Inferred usage within a custom Kryo Serializer implementation
val serializer = new MaskTileSerializer[T]()
val tile = serializer.read(kryo, input, classOf[MaskTile[T]])
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure the `kryo` and `input` objects are provided by the surrounding Kryo serialization context.
- Always specify the generic type `T` to match the pixel data type of the raster being processed.
- This method is intended for internal serialization logic; do not attempt to call it directly on standard RDD operations.

### Prompt Snippet
```text
val tile = new MaskTileSerializer[T]().read(kryo, input, classOf[MaskTile[T]])
```

### Common Failure Modes
- **Kryo Exception:** Occurs if the `input` stream is corrupted or does not contain a valid `MaskTile` binary representation.
- **Type Mismatch:** Occurs if the `tileClass` provided does not match the actual serialized data type, leading to `ClassCastException`.
- **Serialization Context:** Attempting to call this outside of a Kryo-managed serialization block will result in null pointer or stream access errors.

### Fix Code Hint
```scala
// Ensure the serializer is registered with Kryo if using custom serialization
kryo.register(classOf[MaskTile[T]], new MaskTileSerializer[T]())
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MaskTileSerializer.write`

### Signature
```scala
[](kryo:Kryo,output:Output,tile:MaskTile[T]):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MaskTileSerializer.scala:27_

### Goal
Serializes a `MaskTile[T]` instance into a binary stream using the Kryo serialization framework for efficient storage or network transmission within the Raptor raster processing pipeline.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for object graph serialization.
- `output` (`Output`): The Kryo `Output` stream where the serialized tile data will be written.
- `tile` (`MaskTile[T]`): The `MaskTile` object containing the raster data and associated mask to be serialized.

### Input
The caller must provide a valid `MaskTile[T]` instance, where `T` represents the pixel data type (e.g., `Int`, `Float`). The `kryo` and `output` objects must be initialized and ready for writing. This function is typically invoked within a custom Kryo serializer implementation for Beast's raster data structures.

### Output
Returns `Unit` — The function performs a side-effecting write operation to the provided `Output` stream; no value is returned.

### Valid Call Patterns
```scala
// Inferred usage within a custom Serializer class
val serializer = new MaskTileSerializer[T]()
serializer.write(kryo, output, tile)
```

### LLM Instruction Prompt
- When generating code that uses this API, ensure the `kryo` and `output` instances are properly managed within the context of a Kryo serialization routine.
- Always ensure the `MaskTile[T]` type parameter `T` matches the expected pixel data type of the raster being processed.
- This method is intended for internal framework serialization; do not attempt to use it for general file I/O.

### Prompt Snippet
```text
// Example of using MaskTileSerializer within a custom Kryo write method
override def write(kryo: Kryo, output: Output, tile: MaskTile[T]): Unit = {
  // Implementation logic for serializing MaskTile components
  edu.ucr.cs.bdlab.raptor.MaskTileSerializer.write(kryo, output, tile)
}
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `kryo`, `output`, or `tile` arguments are null.
- **Serialization Errors**: Occurs if the `MaskTile` contains data types that are not registered or supported by the current Kryo configuration.
- **Buffer Overflow**: Occurs if the `Output` stream buffer is insufficient for the size of the `MaskTile` being serialized.

### Fix Code Hint
```scala
// Ensure the output stream is flushed or closed after serialization if necessary
val output = new Output(new FileOutputStream("tile.bin"))
val kryo = new Kryo()
// ... register classes ...
new MaskTileSerializer[Float]().write(kryo, output, myMaskTile)
output.close()
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile`

### Signature
```scala
[](tileID:Int,metadata:RasterMetadata,_rasterFeature:RasterFeature)(t:ClassTag[T]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile.scala:40_

### Goal
Constructs an in-memory representation of a raster tile for use within the Raptor (Raster + Vector) processing framework.

### Parameters
- `tileID` (`Int`): A unique identifier for the specific raster tile being instantiated.
- `metadata` (`RasterMetadata`): The metadata object defining the spatial reference, resolution, and dimensions of the raster.
- `_rasterFeature` (`RasterFeature`): The underlying raster feature data associated with this tile.
- `t` (`ClassTag[T]`): The class tag for the pixel data type (e.g., `Float`, `Int`), required for runtime type information in Spark.

### Input
- **Data:** Requires valid `RasterMetadata` and `RasterFeature` objects.
- **Preconditions:** The `RasterMetadata` must be consistent with the `RasterFeature` dimensions. If performing operations with other rasters, ensure metadata alignment (resolution, CRS) using `reshape` prior to tile creation.

### Output
Returns `unspecified` — An instance of `MemoryTile` representing a single raster tile in memory, suitable for pixel-level transformations or integration into `RasterRDD` workflows.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile = new edu.ucr.cs.bdlab.raptor.MemoryTile[Float](tileID, metadata, rasterFeature)
```

### LLM Instruction Prompt
- When using `MemoryTile`, ensure the `ClassTag[T]` matches the pixel data type of the `RasterFeature` (e.g., `Float` for `FloatType`).
- Always verify that the `RasterMetadata` provided is compatible with the spatial context of the `RasterFeature`.

### Prompt Snippet
```text
val tile = new edu.ucr.cs.bdlab.raptor.MemoryTile[Float](tileID, metadata, rasterFeature)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `ClassTag` that does not match the underlying pixel data type of the `RasterFeature` will cause runtime errors.
- **Metadata Incompatibility:** Using `MemoryTile` with metadata that does not match the actual dimensions or CRS of the `RasterFeature` will lead to incorrect spatial indexing or processing results.
- **Memory Constraints:** Creating too many `MemoryTile` objects in a single Spark task can lead to `OutOfMemoryError` if the heap size is insufficient for the raster data volume.

### Fix Code Hint
```scala
// Ensure the type parameter matches the raster data type
val tile = new edu.ucr.cs.bdlab.raptor.MemoryTile[Float](id, meta, feature)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile.bitsPerPixel`

### Signature
```scala
[]:Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile.scala:62_

### Goal
Retrieves the number of bits used to represent each pixel within a `MemoryTile` instance.

### Parameters
_None._

### Input
The caller must have an initialized `edu.ucr.cs.bdlab.raptor.MemoryTile` object. No specific file format is required as input, as this method operates on the in-memory representation of a raster tile.

### Output
Returns `Int` — The bit depth of the raster data stored in the tile (e.g., 8, 16, 32).

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: MemoryTile = ... // initialized MemoryTile
val bpp: Int = tile.bitsPerPixel()
```

### LLM Instruction Prompt
- When generating code that interacts with `MemoryTile`, use `bitsPerPixel()` to determine the storage depth of the raster data before performing pixel-level transformations or memory allocation.
- Ensure the `MemoryTile` instance is fully initialized before calling this method.

### Prompt Snippet
```text
val bpp = tile.bitsPerPixel()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `bitsPerPixel()` on an uninitialized or null `MemoryTile` reference.
- **Context Mismatch:** Assuming a specific bit depth (e.g., 8-bit) without verifying the return value of `bitsPerPixel()`, which may lead to incorrect pixel value interpretation or overflow during arithmetic operations.

### Fix Code Hint
```scala
// Always verify bit depth before processing pixel values
val tile = ... 
val bpp = tile.bitsPerPixel()
if (bpp == 32) {
  // Handle 32-bit float/int processing
} else {
  // Handle other bit depths
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile.scala:177_

### Goal
Retrieves the `DataType` associated with the pixels stored within a `MemoryTile`, identifying the underlying numeric or data representation of the raster content.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.MemoryTile`. No specific file format is required as the input, as this method operates on the in-memory representation of the tile.

### Output
Returns `DataType` — An object representing the data type of the tile's components (e.g., IntegerType, FloatType), which is essential for ensuring compatibility during raster-vector joins or pixel-level transformations.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: MemoryTile = ... // initialized MemoryTile
val dataType: DataType = tile.componentType()
```

### LLM Instruction Prompt
- When working with `MemoryTile` objects in RDPro, use `componentType()` to inspect the pixel data type before performing arithmetic operations or `mapPixels` transformations. Ensure the returned `DataType` matches the expected type for your specific raster processing workflow (e.g., `FloatType` for temperature data).

### Prompt Snippet
```text
val tile: MemoryTile = ...
val dtype = tile.componentType()
```

### Common Failure Modes
- **Type Mismatch:** Attempting to perform arithmetic operations (e.g., `mapPixels`) assuming a `FloatType` when the `componentType()` returns an `IntegerType`, leading to precision loss or runtime errors.
- **Uninitialized Tile:** Calling `componentType()` on a null or uninitialized `MemoryTile` reference will result in a `NullPointerException`.
- **Incompatible Raster Operations:** Failing to verify the `componentType` before `overlay` or `reshape` operations, which require identical metadata and compatible data types across rasters.

### Fix Code Hint
```scala
// Ensure the tile data type is compatible before processing
val tile = ...
if (tile.componentType() == FloatType) {
  // Proceed with floating-point transformations
} else {
  // Handle or cast the data type accordingly
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):T
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile.scala:106_

### Goal
Retrieves the raw pixel value at a specific coordinate within a `MemoryTile` instance during raster processing operations.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must have an initialized `MemoryTile[T]` object. The indices `i` and `j` must be within the bounds of the tile dimensions (0 to width-1 and 0 to height-1, respectively).

### Output
Returns `T` — The value of the pixel at the specified location, where `T` corresponds to the pixel data type (e.g., `Float`, `Int`) defined when the `MemoryTile` was instantiated.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: MemoryTile[Float] = ...
val value: Float = tile.getPixelValue(i, j)
```

### LLM Instruction Prompt
- When using `getPixelValue`, ensure the `MemoryTile` instance is already loaded into memory.
- Always verify that `i` and `j` are within the tile's bounds to avoid `ArrayIndexOutOfBoundsException`.
- Ensure the type `T` matches the expected raster data type (e.g., `Float` for temperature or elevation data).

### Prompt Snippet
```text
val pixel = tile.getPixelValue(col, row)
```

### Common Failure Modes
- **Out of Bounds:** Providing `i` or `j` values that exceed the tile's dimensions will throw an `ArrayIndexOutOfBoundsException`.
- **Type Mismatch:** Attempting to assign the returned value to an incompatible type if the `MemoryTile` generic type `T` is not correctly inferred or specified.
- **Uninitialized Tile:** Calling this method on a null or uninitialized `MemoryTile` reference will result in a `NullPointerException`.

### Fix Code Hint
```scala
// Bounds checking before access
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  val value = tile.getPixelValue(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile.scala:175_

### Goal
Checks if a specific pixel at the given coordinates within a `MemoryTile` is considered empty or contains no valid data.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide a valid `MemoryTile` instance. The coordinates `i` and `j` must be within the bounds of the tile dimensions (0 to width-1 and 0 to height-1, respectively).

### Output
Returns `Boolean` — `true` if the pixel at the specified location is empty (e.g., represents a NoData value), and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: MemoryTile = ...
val isPixelEmpty = tile.isEmpty(i, j)
```

### LLM Instruction Prompt
- Use `isEmpty(i, j)` to verify pixel validity before performing arithmetic or transformation operations on raster data.
- Ensure `i` and `j` are within the tile's defined dimensions to avoid `ArrayIndexOutOfBoundsException`.

### Prompt Snippet
```text
// Check if pixel is empty before processing
if (!tile.isEmpty(col, row)) {
  val value = tile.get(col, row)
  // process value
}
```

### Common Failure Modes
- **Out of Bounds:** Providing `i` or `j` values that exceed the tile's width or height will result in an `ArrayIndexOutOfBoundsException`.
- **Uninitialized Tile:** Calling this method on a null or uninitialized `MemoryTile` reference will throw a `NullPointerException`.
- **Metadata Mismatch:** If the `MemoryTile` was created from a raster source with incorrect NoData definitions, `isEmpty` may return incorrect results. Ensure the raster metadata is correctly parsed during ingestion.

### Fix Code Hint
```scala
// Ensure coordinates are within bounds
if (i >= 0 && i < tile.getWidth && j >= 0 && j < tile.getHeight) {
  if (!tile.isEmpty(i, j)) {
    // Safe to access pixel
  }
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile.setPixelValue`

### Signature
```scala
[](i:Int,j:Int,value:T):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile.scala:147_

### Goal
Updates the value of a specific pixel at the given column and row coordinates within a `MemoryTile` instance.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel to update.
- `j` (`Int`): The row index (y-coordinate) of the pixel to update.
- `value` (`T`): The new value to assign to the pixel, where `T` must match the underlying data type of the `MemoryTile`.

### Input
The caller must provide a valid `MemoryTile` instance. The indices `i` and `j` must be within the bounds of the tile dimensions. The `value` type `T` must be consistent with the tile's initialized pixel type (e.g., `Float`, `Int`).

### Output
Returns `Unit` — The operation modifies the `MemoryTile` in-place; no value is returned.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile = new MemoryTile[Float](width, height)
tile.setPixelValue(x, y, 10.5f)
```

### LLM Instruction Prompt
- When calling `setPixelValue`, ensure the `MemoryTile` has been initialized with the correct generic type `T`.
- Verify that `i` and `j` are within the range `[0, width-1]` and `[0, height-1]` respectively to avoid `ArrayIndexOutOfBoundsException`.
- Use this method for manual pixel-level manipulation within local raster processing tasks.

### Prompt Snippet
```text
// Example usage of setPixelValue on a MemoryTile
tile.setPixelValue(colIndex, rowIndex, newValue)
```

### Common Failure Modes
- **Index Out of Bounds:** Providing `i` or `j` values that exceed the tile's dimensions will throw an `ArrayIndexOutOfBoundsException`.
- **Type Mismatch:** Attempting to set a value of a type incompatible with the `MemoryTile` generic type `T` will result in a compilation error.
- **Uninitialized Tile:** Calling this method on a null or uninitialized `MemoryTile` reference will result in a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure bounds checking before setting pixel values
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  tile.setPixelValue(i, j, value)
} else {
  // Handle out-of-bounds error
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile2`

### Signature
```scala
[](tileID:Int,rasterMetadata:RasterMetadata,rasterFeature:RasterFeature)(t:ClassTag[T]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile2.scala:13_

### Goal
Initializes an in-memory raster tile representation within the Raptor framework, facilitating pixel-level access and processing for a specific spatial feature.

### Parameters
- `tileID` (`Int`): A unique identifier for the specific raster tile being instantiated.
- `rasterMetadata` (`RasterMetadata`): The metadata object defining the raster's spatial reference, resolution, and dimensions.
- `rasterFeature` (`RasterFeature`): The feature object containing the actual raster data or reference to the underlying pixel values.
- `t` (`ClassTag[T]`): The class tag for the pixel data type (e.g., `Float`, `Int`), required by Spark for type-safe serialization and processing.

### Input
- **Data:** Requires valid `RasterMetadata` and `RasterFeature` objects, typically derived from GeoTIFF or HDF4 sources.
- **Preconditions:** The `rasterMetadata` must be consistent with the `rasterFeature` geometry. If performing operations across multiple tiles, ensure metadata alignment (resolution and CRS) using `reshape` before instantiation.

### Output
Returns `unspecified` — An instance of `MemoryTile2` representing the raster data in memory for local pixel-level transformations.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile = new edu.ucr.cs.bdlab.raptor.MemoryTile2(tileID, metadata, feature)(scala.reflect.classTag[Float])
```

### LLM Instruction Prompt
- When generating code using `MemoryTile2`, always include the `ClassTag[T]` context bound or explicit parameter to ensure Spark type compatibility.
- Ensure the `rasterMetadata` provided matches the source raster's coordinate system and resolution.

### Prompt Snippet
```text
// Example of initializing a MemoryTile2 for a Float-based raster
val tile = new edu.ucr.cs.bdlab.raptor.MemoryTile2(id, meta, feature)(scala.reflect.classTag[Float])
```

### Common Failure Modes
- **Type Mismatch:** Providing a `ClassTag` that does not match the underlying pixel data type (e.g., using `Int` for a `Float` raster) will cause runtime casting errors.
- **Metadata Inconsistency:** Attempting to join or process tiles with mismatched `RasterMetadata` (e.g., different CRS or cell sizes) will lead to incorrect spatial alignment.
- **Memory Constraints:** Instantiating large numbers of `MemoryTile2` objects in memory can lead to `OutOfMemoryError` in Spark executors; consider using `RasterRDD` for distributed processing instead.

### Fix Code Hint
```scala
// Ensure the ClassTag matches the pixel type of your RasterRDD
import scala.reflect.classTag
val tile = new edu.ucr.cs.bdlab.raptor.MemoryTile2(tileID, meta, feature)(classTag[Float])
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile2.componentType`

### Signature
```scala
[]:DataType
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile2.scala:53_

### Goal
Retrieves the underlying `DataType` of the pixels stored within a `MemoryTile2` instance, identifying the primitive type (e.g., Integer, Float) used for raster data representation.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.MemoryTile2`. No specific file format is required as this operates on an in-memory raster tile object.

### Output
Returns `DataType` — An object representing the data type of the raster components, which is essential for ensuring type compatibility during pixel-level transformations or raster-vector joins.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: MemoryTile2 = ...
val dataType: DataType = tile.componentType()
```

### LLM Instruction Prompt
- When generating code involving `MemoryTile2`, use `componentType()` to inspect the pixel data type before performing arithmetic operations or transformations.
- Ensure that the `DataType` returned matches the expected type for subsequent `mapPixels` or `raptorJoin` operations to avoid runtime type mismatches.

### Prompt Snippet
```text
val tile = new MemoryTile2(...)
val pixelType = tile.componentType()
```

### Common Failure Modes
- **Type Mismatch:** Attempting to perform arithmetic on pixels (e.g., `mapPixels`) using a type that does not match the `componentType` (e.g., treating an `IntegerType` tile as `FloatType`).
- **Null Reference:** Calling `componentType()` on an uninitialized or null `MemoryTile2` object.
- **Compatibility:** Failing to verify `componentType` before `overlay` or `reshape` operations, which require identical metadata and type alignment between raster tiles.

### Fix Code Hint
```scala
// Ensure type safety before processing
val tile = ...
if (tile.componentType() == FloatType) {
  val processed = tile.mapPixels(p => p.asInstanceOf[Float] * 1.5f)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile2.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):T
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile2.scala:24_

### Goal
Retrieves the pixel value at a specific coordinate within a `MemoryTile2` raster tile instance.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must have an initialized `MemoryTile2[T]` object. The indices `i` and `j` must be within the bounds of the tile dimensions (0 to width-1 and 0 to height-1, respectively).

### Output
Returns `T` — The value of the pixel at the specified location, where `T` corresponds to the pixel data type (e.g., `Int`, `Float`) defined during the tile's instantiation.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: MemoryTile2[Float] = ...
val value: Float = tile.getPixelValue(i, j)
```

### LLM Instruction Prompt
- When calling `getPixelValue`, ensure the indices `i` and `j` are validated against the tile's dimensions to prevent `ArrayIndexOutOfBoundsException`.
- Ensure the type `T` matches the expected pixel type of the raster data (e.g., `Float` for temperature, `Int` for categorical data).

### Prompt Snippet
```text
val pixelValue = tile.getPixelValue(colIndex, rowIndex)
```

### Common Failure Modes
- **IndexOutOfBounds:** Providing `i` or `j` values that fall outside the tile's defined width or height.
- **Type Mismatch:** Attempting to assign the returned value to a variable of an incompatible type if the `MemoryTile2` was initialized with a different generic type `T`.
- **Uninitialized Tile:** Calling the method on a null or improperly initialized `MemoryTile2` object.

### Fix Code Hint
```scala
// Ensure bounds checking before access
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  val value = tile.getPixelValue(i, j)
} else {
  // Handle out of bounds
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile2.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile2.scala:39_

### Goal
Determines whether a specific pixel at the given coordinates within a `MemoryTile2` instance is considered empty or contains no valid data.

### Parameters
- `i` (`Int`): The row index (vertical coordinate) of the pixel within the tile.
- `j` (`Int`): The column index (horizontal coordinate) of the pixel within the tile.

### Input
The caller must provide a valid `MemoryTile2` instance. The indices `i` and `j` must be within the bounds of the tile dimensions; otherwise, an `ArrayIndexOutOfBoundsException` may occur.

### Output
Returns `Boolean` — `true` if the pixel at the specified `(i, j)` location is empty (e.g., represents a NoData value), and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: MemoryTile2 = ...
val isPixelEmpty: Boolean = tile.isEmpty(row, col)
```

### LLM Instruction Prompt
- When generating code that uses `MemoryTile2.isEmpty`, ensure the `i` and `j` indices are validated against the tile's dimensions to prevent runtime exceptions.
- Use this method to filter out NoData pixels during raster-vector join operations or pixel-level transformations.

### Prompt Snippet
```text
// Check if pixel is empty before processing
if (!tile.isEmpty(i, j)) {
  val value = tile.get(i, j)
  // process value
}
```

### Common Failure Modes
- **IndexOutOfBounds:** Providing `i` or `j` values that exceed the tile's width or height.
- **Uninitialized Tile:** Calling `isEmpty` on a `MemoryTile2` object that has not been properly populated or initialized from a raster source.
- **Assumption of NoData:** Relying on `isEmpty` without verifying how the specific raster source defines "empty" or "NoData" values (e.g., some rasters use specific sentinel values).

### Fix Code Hint
```scala
// Ensure indices are within bounds
if (i >= 0 && i < tile.getHeight && j >= 0 && j < tile.getWidth) {
  if (!tile.isEmpty(i, j)) {
    // Perform operation
  }
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile2.numComponents`

### Signature
```scala
[]:Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile2.scala:63_

### Goal
Returns the number of data components (bands or channels) present in the `MemoryTile2` instance.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.MemoryTile2`. This class is typically used within the Raptor framework for in-memory raster tile representation. No specific file format is required as input, as the tile is already loaded into memory.

### Output
Returns `Int` — The total count of components (e.g., 1 for grayscale, 3 for RGB) stored within the tile.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: edu.ucr.cs.bdlab.raptor.MemoryTile2 = ...
val components: Int = tile.numComponents()
```

### LLM Instruction Prompt
- When generating code that uses `MemoryTile2`, use `numComponents()` to determine the depth of the raster data before performing pixel-level transformations or array indexing.
- Ensure the `MemoryTile2` instance is fully initialized before calling this method.

### Prompt Snippet
```text
val tile = new edu.ucr.cs.bdlab.raptor.MemoryTile2(...)
val numBands = tile.numComponents()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `numComponents()` on an uninitialized or null `MemoryTile2` reference.
- **Logic Errors:** Assuming a single-band raster when the tile contains multiple components (e.g., multi-spectral data), leading to index-out-of-bounds errors during subsequent pixel processing.

### Fix Code Hint
```scala
// Ensure the tile is not null before accessing metadata
if (tile != null) {
  val bands = tile.numComponents()
  // Proceed with band-aware processing
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTile2.setPixelValue`

### Signature
```scala
[](i:Int,j:Int,value:T):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTile2.scala:29_

### Goal
Updates the pixel value at a specific coordinate within a `MemoryTile2` instance during in-memory raster processing.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.
- `value` (`T`): The new value to assign to the pixel, where `T` must match the tile's underlying data type (e.g., `Float`, `Int`).

### Input
The caller must have an initialized `MemoryTile2[T]` object. The indices `i` and `j` must be within the bounds of the tile dimensions. The `value` type `T` must be consistent with the type parameter defined during the instantiation of the `MemoryTile2`.

### Output
Returns `Unit` — The operation modifies the internal state of the `MemoryTile2` object in-place.

### Valid Call Patterns
```scala
// Inferred usage pattern for a MemoryTile2 instance
val tile = new MemoryTile2[Float](width, height)
tile.setPixelValue(10, 20, 150.5f)
```

### LLM Instruction Prompt
- When generating code using `setPixelValue`, ensure the `MemoryTile2` instance is already initialized with the correct type parameter `T`.
- Always verify that the indices `i` and `j` are within the tile's bounds to avoid `ArrayIndexOutOfBoundsException`.
- Ensure the `value` provided matches the type `T` of the `MemoryTile2` (e.g., do not pass a `Double` if the tile is `Float`).

### Prompt Snippet
```text
// Example of setting a pixel in a MemoryTile2
tile.setPixelValue(colIndex, rowIndex, pixelValue)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if `i` or `j` are negative or exceed the tile's width/height dimensions.
- **Type Mismatch**: Occurs if the provided `value` type does not match the generic type `T` of the `MemoryTile2` instance.
- **Null Pointer**: Occurs if the `MemoryTile2` instance has not been properly initialized before calling the method.

### Fix Code Hint
```scala
// Ensure bounds checking before assignment
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  tile.setPixelValue(i, j, value)
} else {
  // Handle out-of-bounds error
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTileSerializer`

### Signature
```scala
[]():<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTileSerializer.scala:24_

### Goal
Provides a serialization mechanism for raster tiles held in memory within the Raptor framework to facilitate distributed processing or storage.

### Parameters
_None._

### Input
This class is intended for use within the Raptor raster-vector processing pipeline. It expects raster data that has been loaded into memory as tiles. No specific file format is required as input to the constructor, but the serialized output is typically associated with the internal representation of raster tiles used in `RasterRDD` operations.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it represents a serializer instance used for converting memory-resident raster tiles into a format suitable for transmission or persistence.

### Valid Call Patterns
```scala
// Inferred from signature: instantiation of the serializer
val serializer = new edu.ucr.cs.bdlab.raptor.MemoryTileSerializer()
```

### LLM Instruction Prompt
- When generating code involving `MemoryTileSerializer`, ensure it is used within the context of Raptor raster processing workflows.
- Note that this API is undocumented in the official README; use it only when performing low-level serialization of raster tiles in custom Spark transformations.
- Do not assume specific return types for the constructor; treat the instance as an opaque object for serialization tasks.

### Prompt Snippet
```text
val serializer = new edu.ucr.cs.bdlab.raptor.MemoryTileSerializer()
```

### Common Failure Modes
- **Instantiation Errors:** Attempting to use the serializer outside of a Spark executor context where raster tiles are not properly initialized.
- **Type Mismatch:** Attempting to serialize data structures that do not conform to the expected internal tile format of the Raptor framework.
- **Missing Context:** Calling this in a local environment without the necessary Beast/Raptor dependencies linked in the classpath.

### Fix Code Hint
```scala
// Ensure the Raptor environment is initialized and tiles are in memory
import edu.ucr.cs.bdlab.raptor.MemoryTileSerializer

val tileSerializer = new MemoryTileSerializer()
// Use the serializer instance within your RDD transformation logic
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTileSerializer.read`

### Signature
```scala
[](kryo:Kryo,input:Input,klass:Class[MemoryTile[Any]]):MemoryTile[Any]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTileSerializer.scala:40_

### Goal
Deserializes a `MemoryTile` object from a binary stream using the Kryo serialization framework within the Raptor raster processing pipeline.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for the deserialization process.
- `input` (`Input`): The input stream containing the serialized `MemoryTile` data.
- `klass` (`Class[MemoryTile[Any]]`): The class type of the object to be deserialized.

### Input
The caller must provide a valid `Kryo` instance and an `Input` stream that contains a previously serialized `MemoryTile` object. This function is typically invoked by the Kryo framework during the deserialization of `RasterRDD` or `ITile` data structures within Spark tasks.

### Output
Returns `MemoryTile[Any]` — A reconstructed `MemoryTile` instance containing the raster pixel data and associated metadata.

### Valid Call Patterns
```scala
// Inferred from signature: usage within a custom Kryo Serializer implementation
val serializer = new MemoryTileSerializer()
val tile = serializer.read(kryo, input, classOf[MemoryTile[Any]])
```

### LLM Instruction Prompt
- When generating code that uses this API, ensure it is placed within a context where `Kryo` and `Input` objects are available, typically inside a custom `Serializer` class.
- Always pass `classOf[MemoryTile[Any]]` as the third argument to satisfy the signature requirements.

### Prompt Snippet
```text
// Deserializing a MemoryTile using the Raptor serializer
val tile = new edu.ucr.cs.bdlab.raptor.MemoryTileSerializer().read(kryo, input, classOf[MemoryTile[Any]])
```

### Common Failure Modes
- **KryoException:** Occurs if the `Input` stream does not contain a valid `MemoryTile` binary representation or if the stream is corrupted.
- **ClassCastException:** May occur if the `klass` parameter does not match the expected `MemoryTile` type.
- **Serialization Mismatch:** If the `MemoryTile` was serialized with a different version of the Beast framework, deserialization may fail due to structural changes in the `MemoryTile` class.

### Fix Code Hint
```scala
// Ensure the input stream is positioned correctly before calling read
if (!input.end()) {
  val tile = serializer.read(kryo, input, classOf[MemoryTile[Any]])
  // Process the tile...
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTileSerializer.write`

### Signature
```scala
[](kryo:Kryo,output:Output,tile:MemoryTile[Any]):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTileSerializer.scala:26_

### Goal
Serializes a `MemoryTile` object into a binary stream using the Kryo serialization framework for efficient storage or network transmission within the Raptor raster processing pipeline.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for managing the serialization process.
- `output` (`Output`): The Kryo `Output` stream where the serialized tile data will be written.
- `tile` (`MemoryTile[Any]`): The `MemoryTile` instance containing the raster pixel data and metadata to be serialized.

### Input
The caller must provide a valid, initialized `Kryo` instance and an open `Output` stream. The `MemoryTile` must be fully populated with raster data. As this is a low-level serialization method, ensure that the `MemoryTile` type parameters are consistent with the expected raster data types (e.g., `Float`, `Int`) used elsewhere in the Raptor workflow.

### Output
Returns `Unit` — The method performs a side-effect by writing the binary representation of the `MemoryTile` to the provided `Output` stream.

### Valid Call Patterns
```scala
// Inferred from signature:
val serializer = new edu.ucr.cs.bdlab.raptor.MemoryTileSerializer()
serializer.write(kryo, output, tile)
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure the `Kryo` instance is properly configured for the Beast/Raptor environment.
- Always ensure the `Output` stream is flushed or closed by the caller after the `write` operation completes to ensure data integrity.
- This method is intended for internal serialization; ensure the `MemoryTile` is not null before invocation.

### Prompt Snippet
```text
// Example of using MemoryTileSerializer to write a tile
val serializer = new edu.ucr.cs.bdlab.raptor.MemoryTileSerializer()
serializer.write(kryo, output, myMemoryTile)
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `kryo`, `output`, or `tile` arguments are null.
- **SerializationException**: Occurs if the `MemoryTile` contains data structures that are not registered or supported by the current `Kryo` configuration.
- **BufferOverflow**: Occurs if the `Output` stream buffer is too small to hold the serialized tile data.

### Fix Code Hint
```scala
// Ensure the output stream is sufficiently sized or auto-resizing
val output = new com.esotericsoftware.kryo.io.Output(4096, -1)
val serializer = new edu.ucr.cs.bdlab.raptor.MemoryTileSerializer()
serializer.write(kryo, output, tile)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTileWindow.create`

### Signature
```scala
[type T](tileID:Int,metadata:RasterMetadata,rasterFeature:RasterFeature,numValues:Int)(evidence$1:ClassTag[T]):MemoryTileWindow[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTileWindow.scala:98_

### Goal
Creates a `MemoryTileWindow` instance to facilitate in-memory access and processing of raster pixel data associated with a specific spatial feature.

### Parameters
- `tileID` (`Int`): The unique identifier for the raster tile being accessed.
- `metadata` (`RasterMetadata`): The metadata object defining the raster's spatial reference, resolution, and dimensions.
- `rasterFeature` (`RasterFeature`): The specific raster feature containing the data to be windowed.
- `numValues` (`Int`): The total number of pixel values expected within the window.
- `evidence$1` (`ClassTag[T]`): A `ClassTag` providing runtime type information for the generic type `T` (e.g., `Float`, `Int`).

### Input
- **Data:** Requires valid `RasterMetadata` and `RasterFeature` objects, typically derived from GeoTIFF or HDF4 sources.
- **Preconditions:** The `RasterMetadata` must be consistent with the `RasterFeature` being processed. The type `T` must match the underlying pixel data type of the raster (e.g., `Float` for floating-point rasters).

### Output
Returns `MemoryTileWindow[T]` — A container representing a windowed view of raster data in memory, allowing for pixel-level operations.

### Valid Call Patterns
```scala
// Inferred from signature:
val window = edu.ucr.cs.bdlab.raptor.MemoryTileWindow.create[Float](
  tileID, 
  metadata, 
  rasterFeature, 
  numValues
)
```

### LLM Instruction Prompt
- When calling `create`, ensure the type parameter `T` matches the pixel data type of the `RasterFeature`.
- Always provide a `ClassTag` for the generic type `T` (usually handled automatically by the Scala compiler if the type is specified).
- Ensure `metadata` is correctly initialized for the specific raster source to avoid coordinate mismatch errors.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.MemoryTileWindow
// Ensure T matches the raster pixel type
val window = MemoryTileWindow.create[T](tileID, metadata, rasterFeature, numValues)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `ClassTag` or type `T` that does not match the actual pixel data type (e.g., using `Int` for a `Float` raster) will cause runtime errors during pixel access.
- **Metadata Inconsistency:** Using `RasterMetadata` that does not align with the `RasterFeature` dimensions or CRS will result in incorrect spatial indexing or out-of-bounds errors.
- **Invalid `numValues`:** Providing an incorrect `numValues` count relative to the tile dimensions will lead to buffer overflows or incomplete data processing.

### Fix Code Hint
```scala
// Ensure the type T is explicitly defined and matches the raster's data type
val window = edu.ucr.cs.bdlab.raptor.MemoryTileWindow.create[Float](
  tileID, 
  metadata, 
  rasterFeature, 
  expectedPixelCount
)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTileWindowSerializer`

### Signature
```scala
[]():<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTileWindowSerializer.scala:21_

### Goal
Provides a serialization mechanism for `MemoryTileWindow` objects within the Raptor raster-vector processing framework to facilitate distributed data handling in Spark.

### Parameters
_None._

### Input
This class is intended for internal use within the Raptor processing pipeline. It expects a `MemoryTileWindow` object, which represents a localized window of raster data in memory. Ensure that the raster data has been properly loaded and partitioned before attempting serialization.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it typically produces a serialized byte representation or a serializer instance compatible with Spark's internal serialization framework.

### Valid Call Patterns
```scala
// Inferred usage:
val serializer = new edu.ucr.cs.bdlab.raptor.MemoryTileWindowSerializer()
```

### LLM Instruction Prompt
- When generating code involving `MemoryTileWindowSerializer`, treat it as a utility class for Raptor-based raster operations.
- Do not attempt to pass arguments to the constructor, as the API signature indicates zero parameters.
- Ensure the context involves `RasterRDD` or `ITile` processing, as this serializer is specific to the Raptor module.

### Prompt Snippet
```text
// Use the MemoryTileWindowSerializer to handle raster window serialization
val serializer = new edu.ucr.cs.bdlab.raptor.MemoryTileWindowSerializer()
```

### Common Failure Modes
- **Serialization Errors:** Attempting to serialize a `MemoryTileWindow` that contains null or uninitialized pixel data.
- **Context Mismatch:** Using this serializer outside of a Spark-based Raptor workflow, which may lead to runtime exceptions if the underlying Spark environment is not properly configured.
- **Version Incompatibility:** This API is part of Beast v0.10.1; ensure the project dependencies match this version to avoid binary incompatibility.

### Fix Code Hint
```scala
// Ensure the Raptor environment is initialized before serialization
import edu.ucr.cs.bdlab.raptor.MemoryTileWindowSerializer

val serializer = new MemoryTileWindowSerializer()
// Proceed with serialization logic within the Spark transformation
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTileWindowSerializer.read`

### Signature
```scala
[](kryo:Kryo,input:Input,klass:Class[MemoryTileWindow[T]]):MemoryTileWindow[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTileWindowSerializer.scala:29_

### Goal
Deserializes a `MemoryTileWindow[T]` object from a Kryo input stream, enabling efficient reconstruction of raster tile windows during distributed processing.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for the deserialization process.
- `input` (`Input`): The input stream containing the serialized `MemoryTileWindow` data.
- `klass` (`Class[MemoryTileWindow[T]]`): The class type of the object to be deserialized, ensuring type safety for the generic pixel type `T`.

### Input
The caller must provide a valid `Kryo` instance and an `Input` stream that contains data previously serialized by the corresponding `MemoryTileWindowSerializer.write` method. The generic type `T` must be consistent with the pixel type of the raster data (e.g., `Float`, `Int`).

### Output
Returns `MemoryTileWindow[T]` — A reconstructed `MemoryTileWindow` object containing the raster pixel data and associated spatial metadata.

### Valid Call Patterns
```scala
// Inferred usage within a custom Kryo Serializer implementation
val serializer = new MemoryTileWindowSerializer[T]()
val window = serializer.read(kryo, input, classOf[MemoryTileWindow[T]])
```

### LLM Instruction Prompt
- When generating code that uses this API, ensure the `kryo` and `input` objects are properly initialized within a Kryo-compatible serialization context.
- Always specify the generic type `T` (e.g., `Float`, `Int`) to match the underlying raster pixel data type.
- This method is intended for internal framework serialization; ensure it is called only within the context of a `Serializer` implementation.

### Prompt Snippet
```text
// Example of using MemoryTileWindowSerializer.read
val myWindow = serializer.read(kryo, input, classOf[MemoryTileWindow[Float]])
```

### Common Failure Modes
- **KryoException:** Occurs if the `input` stream is corrupted, does not contain a valid `MemoryTileWindow` serialization, or if the stream position is incorrect.
- **ClassCastException:** Occurs if the generic type `T` provided in `klass` does not match the actual data type stored in the serialized stream.
- **NullPointerException:** Occurs if the `kryo` or `input` arguments are null.

### Fix Code Hint
```scala
// Ensure the input stream is positioned at the start of the serialized object
val window = try {
  serializer.read(kryo, input, classOf[MemoryTileWindow[Float]])
} catch {
  case e: Exception => throw new RuntimeException("Failed to deserialize MemoryTileWindow", e)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.MemoryTileWindowSerializer.write`

### Signature
```scala
[](kryo:Kryo,output:Output,tile:MemoryTileWindow[T]):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/MemoryTileWindowSerializer.scala:23_

### Goal
Serializes a `MemoryTileWindow[T]` object into a byte stream using the Kryo serialization framework for efficient storage or network transmission within Spark-based raster workflows.

### Parameters
- `kryo` (`Kryo`): The Kryo instance used for the serialization process.
- `output` (`Output`): The Kryo `Output` stream where the serialized tile data will be written.
- `tile` (`MemoryTileWindow[T]`): The `MemoryTileWindow` instance containing the raster data to be serialized.

### Input
- **Data:** A `MemoryTileWindow[T]` object, which represents a windowed view of raster data in memory.
- **Preconditions:** The `kryo` instance must be properly configured to handle `MemoryTileWindow` types. The `output` stream must be open and writable.
- **Type Selection:** The generic type `T` must correspond to the pixel data type (e.g., `Float`, `Int`) consistent with the raster's underlying data structure.

### Output
Returns `Unit` — The method performs a side-effect by writing the serialized representation of the `MemoryTileWindow` to the provided `Output` stream.

### Valid Call Patterns
```scala
// Inferred usage pattern for custom Kryo serialization
val serializer = new MemoryTileWindowSerializer[Float]()
serializer.write(kryo, output, tile)
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure the `Kryo` instance is initialized within the context of a Spark serialization configuration.
- Always verify that the `MemoryTileWindow` type `T` matches the expected pixel type of the raster data being processed.
- Use this method only when implementing custom serialization logic for `MemoryTileWindow` objects within the Beast framework.

### Prompt Snippet
```text
// Example of invoking the serializer
val serializer = new edu.ucr.cs.bdlab.raptor.MemoryTileWindowSerializer[Float]()
serializer.write(kryo, output, tile)
```

### Common Failure Modes
- **Serialization Errors:** Occur if the `Kryo` instance is not registered with the `MemoryTileWindow` class or if the generic type `T` is incompatible with the data stored in the tile.
- **Stream Exceptions:** `IOException` or `KryoException` may occur if the `output` stream is closed, full, or lacks sufficient permissions.
- **Type Mismatch:** Attempting to serialize a tile with a type `T` that does not match the expected schema of the downstream consumer.

### Fix Code Hint
```scala
// Ensure the serializer is used within a valid Kryo context
try {
  serializer.write(kryo, output, tile)
} catch {
  case e: Exception => println(s"Failed to serialize tile: ${e.getMessage}")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelIterator`

### Signature
```scala
[](intersections:Iterator[scala.Tuple2[Long, PixelRange]],rasterFiles:Array[String],rasterLayer:String,numTiles:LongAccumulator):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelIterator.scala:20_

### Goal
Provides a mechanism to iterate over specific pixel ranges within raster files, typically used during Raptor join operations to extract pixel values corresponding to spatial intersections.

### Parameters
- `intersections` (`Iterator[scala.Tuple2[Long, PixelRange]]`): An iterator of tuples where each contains a feature ID and the corresponding `PixelRange` defining the spatial extent to extract from the raster.
- `rasterFiles` (`Array[String]`): An array of file paths pointing to the source raster data (e.g., GeoTIFF or HDF4 files).
- `rasterLayer` (`String`): The specific layer or band identifier within the raster files to be processed.
- `numTiles` (`LongAccumulator`): A Spark `LongAccumulator` used to track the total number of tiles processed during the iteration.

### Input
- **Data Formats:** Supports GeoTIFF and HDF4 raster formats.
- **Preconditions:** The caller must provide valid file paths in `rasterFiles`. The `intersections` iterator must be pre-calculated based on the spatial overlap between vector features and the raster grid.
- **Compatibility:** Raster files must be accessible by the Spark executors.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it is expected to yield an iterator or collection of pixel values or tile data extracted from the specified `PixelRange` regions.

### Valid Call Patterns
```scala
// Example inferred from signature (not verified)
val pixelIterator = new edu.ucr.cs.bdlab.raptor.PixelIterator(
  intersections, 
  Array("path/to/raster1.tif", "path/to/raster2.tif"), 
  "layer_1", 
  sc.longAccumulator("tilesProcessed")
)
```

### LLM Instruction Prompt
- When generating code using `PixelIterator`, ensure the `intersections` iterator is correctly mapped from a spatial join operation.
- Always provide a valid `LongAccumulator` to monitor processing progress.
- Ensure the `rasterLayer` string matches the internal metadata of the provided `rasterFiles`.

### Prompt Snippet
```text
// Use PixelIterator to extract raster data for spatial features
val iterator = new edu.ucr.cs.bdlab.raptor.PixelIterator(intersections, rasterPaths, layerName, accumulator)
```

### Common Failure Modes
- **File Access:** Providing paths that are not accessible by all Spark worker nodes.
- **Metadata Mismatch:** Specifying a `rasterLayer` that does not exist within the provided `rasterFiles`.
- **Empty Iterators:** Passing an empty `intersections` iterator, which may result in no data being processed or unexpected behavior in downstream collectors.
- **Accumulator Scope:** Using an accumulator that has not been properly registered with the `SparkContext`.

### Fix Code Hint
```scala
// Ensure raster files are available on the cluster and layer names are verified
val rasterFiles = Array("data/raster.tif")
val layer = "band1"
val acc = sc.longAccumulator("tileCount")
// Ensure intersections are computed via spatial join before passing to PixelIterator
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelIterator.hasNext`

### Signature
```scala
[]:Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelIterator.scala:96_

### Goal
Checks if there are additional pixels available to be retrieved from the current `PixelIterator` during raster processing operations.

### Parameters
_None._

### Input
The caller must have an initialized `edu.ucr.cs.bdlab.raptor.PixelIterator` instance. This iterator is typically used within the Raptor framework to traverse pixel data extracted from raster sources (GeoTIFF or HDF4) during raster-vector join or pixel-level transformation workflows.

### Output
Returns `Boolean` — `true` if the iterator has more pixels to process, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val iterator: edu.ucr.cs.bdlab.raptor.PixelIterator = ... // obtained from raster processing
if (iterator.hasNext) {
  val pixel = iterator.next()
  // process pixel
}
```

### LLM Instruction Prompt
- When generating code that iterates over raster data using `PixelIterator`, always wrap the `next()` call inside a conditional check using `hasNext` to prevent `NoSuchElementException`.
- Ensure the `PixelIterator` is properly initialized from a valid raster source (e.g., GeoTIFF or HDF4) before calling `hasNext`.

### Prompt Snippet
```text
val iterator = ... // PixelIterator instance
while (iterator.hasNext) {
  val pixel = iterator.next()
  // ...
}
```

### Common Failure Modes
- **Empty Iterator:** Calling `next()` without checking `hasNext` when the iterator is exhausted will throw a `NoSuchElementException`.
- **Uninitialized Iterator:** Attempting to call `hasNext` on a null or uninitialized iterator reference will result in a `NullPointerException`.
- **Concurrent Modification:** Modifying the underlying raster data structure while the `PixelIterator` is active may lead to undefined behavior or runtime exceptions.

### Fix Code Hint
```scala
// Always guard access to the iterator
if (iterator != null && iterator.hasNext) {
  val value = iterator.next()
  // Proceed with processing
} else {
  // Handle empty or exhausted iterator
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelIterator.next`

### Signature
```scala
[]:RaptorJoinResult[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelIterator.scala:98_

### Goal
Advances the iterator to the next pixel-feature intersection result within a Raptor join operation.

### Parameters
_None._

### Input
The caller must have an initialized `PixelIterator` instance. This iterator is typically produced as part of the internal execution of a `raptorJoin` operation between a `RasterRDD[T]` and a vector dataset. No external file formats are required as input to this specific method, as it operates on the stateful stream of joined raster-vector data.

### Output
Returns `RaptorJoinResult[T]` — A container representing the intersection of a specific raster pixel and a vector feature, containing the pixel value and associated feature metadata.

### Valid Call Patterns
```scala
// Inferred from signature:
val iterator: PixelIterator[T] = ... // obtained from internal Raptor join process
if (iterator.hasNext) {
  val result: RaptorJoinResult[T] = iterator.next()
}
```

### LLM Instruction Prompt
- When generating code that uses `PixelIterator.next`, ensure it is preceded by a check to `hasNext` to avoid `NoSuchElementException`.
- The type parameter `T` must match the underlying raster data type (e.g., `Float`, `Int`) used in the `RasterRDD`.

### Prompt Snippet
```text
// Ensure the iterator is checked before calling next()
if (pixelIterator.hasNext) {
  val joinResult = pixelIterator.next()
  // Process joinResult...
}
```

### Common Failure Modes
- **NoSuchElementException**: Occurs if `next()` is called when `hasNext` is false.
- **Type Mismatch**: Occurs if the generic type `T` of the `PixelIterator` does not align with the pixel type of the raster data being processed.
- **State Dependency**: Calling `next()` without an active, initialized iterator context will result in a runtime error.

### Fix Code Hint
```scala
// Always guard the call with hasNext
while (iterator.hasNext) {
  val result = iterator.next()
  // Perform logic on result
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelIterator3`

### Signature
```scala
[](intersections:Iterator[scala.Tuple2[Long, PixelRange]],tiles:Iterator[scala.Tuple2[Long, ITile[T]]],numTiles:LongAccumulator,numRanges:LongAccumulator):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelIterator3.scala:19_

### Goal
Provides a mechanism to iterate over raster pixels by joining spatial intersections with corresponding raster tiles, tracking processing progress via accumulators.

### Parameters
- `intersections` (`Iterator[scala.Tuple2[Long, PixelRange]]`): An iterator of tuples containing a spatial identifier (Long) and the associated `PixelRange` defining the area of interest within the raster.
- `tiles` (`Iterator[scala.Tuple2[Long, ITile[T]]]`): An iterator of tuples containing a spatial identifier (Long) and the corresponding `ITile[T]` raster data.
- `numTiles` (`LongAccumulator`): A Spark `LongAccumulator` used to track the total number of tiles processed during the iteration.
- `numRanges` (`LongAccumulator`): A Spark `LongAccumulator` used to track the total number of pixel ranges processed during the iteration.

### Input
- **Data:** Requires pre-aligned raster tiles (`ITile[T]`) and computed spatial intersections (`PixelRange`).
- **Preconditions:** The `tiles` and `intersections` must be spatially aligned or indexed by the same identifier (Long) to ensure correct pixel-to-geometry mapping.
- **Type Selection:** The generic type `T` must match the underlying pixel data type (e.g., `Float`, `Int`) of the `ITile` objects.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it is intended to facilitate internal iteration over pixel data within the Raptor join workflow.

### Valid Call Patterns
```scala
// Inferred from signature:
val pixelIterator = new edu.ucr.cs.bdlab.raptor.PixelIterator3(intersections, tiles, numTiles, numRanges)
```

### LLM Instruction Prompt
- When using `PixelIterator3`, ensure that the `Long` identifiers in both `intersections` and `tiles` iterators are sorted or matched correctly to prevent data misalignment.
- Always initialize `LongAccumulator` objects via the `SparkContext` before passing them to the constructor.
- Ensure the generic type `T` is consistent with the raster data source (e.g., `Float` for temperature rasters).

### Prompt Snippet
```text
val numTiles = sc.longAccumulator("tiles")
val numRanges = sc.longAccumulator("ranges")
val iterator = new edu.ucr.cs.bdlab.raptor.PixelIterator3(intersections, tiles, numTiles, numRanges)
```

### Common Failure Modes
- **Type Mismatch:** Providing `ITile[Float]` when the logic expects `ITile[Int]` will cause runtime type errors.
- **Uninitialized Accumulators:** Passing null or uninitialized accumulators will result in `NullPointerException` during the iteration process.
- **Misaligned Iterators:** If the `Long` keys in `intersections` do not correspond to the `Long` keys in `tiles`, the iterator will produce incorrect pixel values or fail to map geometries to the correct raster data.

### Fix Code Hint
```scala
// Ensure tiles and intersections are sorted by the Long key before iteration
val sortedTiles = tiles.toSeq.sortBy(_._1).iterator
val sortedIntersections = intersections.toSeq.sortBy(_._1).iterator
new edu.ucr.cs.bdlab.raptor.PixelIterator3(sortedIntersections, sortedTiles, numTiles, numRanges)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelIterator3.hasNext`

### Signature
```scala
[]:Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelIterator3.scala:76_

### Goal
Checks if there are additional pixels available to be processed within the current `PixelIterator3` traversal sequence.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.PixelIterator3`. This iterator is typically used during low-level raster processing tasks within the Raptor framework to traverse pixel data extracted from GeoTIFF or HDF4 sources.

### Output
Returns `Boolean` — `true` if the iterator has more pixels to return, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val iterator: edu.ucr.cs.bdlab.raptor.PixelIterator3 = ...
if (iterator.hasNext) {
  val pixel = iterator.next()
}
```

### LLM Instruction Prompt
- Use `hasNext` as a guard condition before calling `next()` on a `PixelIterator3` instance to prevent `NoSuchElementException`.
- Ensure the iterator is properly initialized from a raster source before invoking this method.

### Prompt Snippet
```text
val iterator = new edu.ucr.cs.bdlab.raptor.PixelIterator3(...)
while (iterator.hasNext) {
  // process pixel
}
```

### Common Failure Modes
- **Null Pointer Exception:** Occurs if the `PixelIterator3` instance was not correctly instantiated or if the underlying raster data source is null.
- **Exhausted Iterator:** Calling `next()` without checking `hasNext` when the iterator is empty will result in a runtime exception.
- **State Mismatch:** If the underlying raster metadata (e.g., tile dimensions) is modified or corrupted during iteration, the iterator state may become inconsistent.

### Fix Code Hint
```scala
// Always wrap iteration in a hasNext check
if (iterator != null && iterator.hasNext) {
  val value = iterator.next()
  // handle value
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelIterator3.next`

### Signature
```scala
[]:RaptorJoinResult[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelIterator3.scala:78_

### Goal
Advances the iterator to the next pixel-feature intersection in a Raptor join operation, returning the associated `RaptorJoinResult[T]`.

### Parameters
_None._

### Input
The caller must have an initialized `PixelIterator3` instance. This iterator is typically used within the context of a Raptor join, where raster data (GeoTIFF or HDF4) is being processed against vector geometries. The iterator assumes that the underlying raster data has been properly partitioned and aligned for the join operation.

### Output
Returns `RaptorJoinResult[T]` — A container representing the result of a single pixel-to-feature join, containing the pixel value of type `T` and the corresponding feature information.

### Valid Call Patterns
```scala
// Inferred from signature:
val iterator: PixelIterator3[T] = ...
val result: RaptorJoinResult[T] = iterator.next()
```

### LLM Instruction Prompt
- When using `PixelIterator3.next`, ensure the iterator has been initialized within a valid Raptor join context.
- The type parameter `T` must match the pixel data type of the raster (e.g., `Float`, `Int`).
- Always check if the iterator has more elements (e.g., via `hasNext`) before calling `next()` to avoid `NoSuchElementException`.

### Prompt Snippet
```text
val iterator: PixelIterator3[Float] = ...
if (iterator.hasNext) {
  val result = iterator.next()
  // process result
}
```

### Common Failure Modes
- **NoSuchElementException**: Occurs if `next()` is called when the iterator has no more elements. Always guard with `hasNext`.
- **Type Mismatch**: Occurs if the type parameter `T` does not match the underlying raster pixel type (e.g., attempting to read `Float` pixels as `Int`).
- **Uninitialized Iterator**: Calling `next()` on an iterator that has not been correctly bound to a raster-vector join context will result in runtime errors.

### Fix Code Hint
```scala
// Ensure safe iteration
while (iterator.hasNext) {
  val joinResult = iterator.next()
  // Perform logic on joinResult
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelRange`

### Signature
```scala
[](geometryID:Long,y:Int,x1:Int,x2:Int):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelRange.scala:25_

### Goal
Represents a horizontal segment of pixels within a raster grid associated with a specific vector geometry, used during Raptor raster-vector join operations.

### Parameters
- `geometryID` (`Long`): The unique identifier of the vector geometry associated with this pixel range.
- `y` (`Int`): The row index (y-coordinate) of the pixels in the raster grid.
- `x1` (`Int`): The starting column index (inclusive) of the pixel segment.
- `x2` (`Int`): The ending column index (inclusive) of the pixel segment.

### Input
The caller must provide valid integer coordinates corresponding to the raster grid dimensions. This class is typically instantiated internally by the `raptorJoin` process when intersecting vector features with raster tiles.

### Output
Returns an instance of `edu.ucr.cs.bdlab.raptor.PixelRange`. The specific return type is not explicitly defined in the provided API facts.

### Valid Call Patterns
```scala
// Example inferred from signature (not verified)
val range = new edu.ucr.cs.bdlab.raptor.PixelRange(geometryID, y, x1, x2)
```

### LLM Instruction Prompt
- When generating code involving `PixelRange`, ensure the `geometryID` matches the ID of the feature being joined.
- Ensure `x1` is less than or equal to `x2` to represent a valid horizontal span.
- Use this class only within the context of Raptor raster-vector processing workflows.

### Prompt Snippet
```text
val pixelRange = new edu.ucr.cs.bdlab.raptor.PixelRange(feature.id, row, startCol, endCol)
```

### Common Failure Modes
- **Index Out of Bounds:** Providing `x` or `y` coordinates that fall outside the dimensions of the underlying raster tile.
- **Invalid Range:** Setting `x1 > x2`, which may cause logic errors in downstream pixel processing or aggregation.
- **Type Mismatch:** Passing non-Long values for `geometryID` or non-Int values for coordinate parameters.

### Fix Code Hint
```scala
// Ensure coordinates are within raster bounds before instantiation
if (x1 <= x2 && y >= 0 && x1 >= 0) {
  val range = new edu.ucr.cs.bdlab.raptor.PixelRange(geometryID, y, x1, x2)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelsInside`

### Signature
```scala
[](polygons:Array[_$1] forSome { 
  <synthetic> type _$1 <: Geometry
},metadata:RasterMetadata):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelsInside.scala:19_

### Goal
Identifies the set of raster pixels that fall within the boundaries of the provided array of vector geometries based on the specified raster metadata.

### Parameters
- `polygons` (`Array[_$1] forSome { <synthetic> type _$1 <: Geometry }`): An array of geometry objects (e.g., polygons) defining the spatial regions of interest for pixel extraction.
- `metadata` (`RasterMetadata`): The metadata object associated with the target raster, providing necessary information about resolution, coordinate reference system, and spatial extent.

### Input
The caller must provide valid geometry objects (typically derived from vector datasets like Shapefiles or GeoJSON) and a `RasterMetadata` object that matches the raster source being queried. Precondition: The geometries and the raster must share the same coordinate reference system (CRS) to ensure accurate spatial intersection.

### Output
Returns `unspecified` — The function returns a collection or representation of pixels contained within the specified geometries. Note: The exact return type is not defined in the provided API facts.

### Valid Call Patterns
```scala
// Inferred usage based on signature
val pixels = new edu.ucr.cs.bdlab.raptor.PixelsInside(polygonsArray, rasterMetadata)
```

### LLM Instruction Prompt
- When using `PixelsInside`, ensure the `polygons` array contains valid `Geometry` types.
- Always verify that the `RasterMetadata` provided corresponds to the raster dataset being processed to avoid coordinate mismatch.
- This function is intended for local pixel-level analysis within the Raptor framework.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.PixelsInside
import edu.ucr.cs.bdlab.beast.geolite.Geometry
// Ensure polygons are prepared and metadata is retrieved from the RasterRDD
val result = new PixelsInside(polygons, metadata)
```

### Common Failure Modes
- **CRS Mismatch:** If the `polygons` and the `RasterMetadata` are in different coordinate systems, the function may return empty results or incorrect pixel mappings.
- **Metadata Incompatibility:** Providing `RasterMetadata` that does not match the actual raster source will lead to incorrect spatial indexing.
- **Empty Geometry:** Passing an empty array or null geometries will result in an empty output or potential runtime exceptions.

### Fix Code Hint
```scala
// Ensure metadata is aligned with the raster source
val metadata = rasterRDD.metadata
// Ensure geometries are in the same CRS as the raster
val alignedPolygons = polygons.map(_.reproject(rasterCRS))
val pixels = new edu.ucr.cs.bdlab.raptor.PixelsInside(alignedPolygons, metadata)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelsInside.Intersection`

### Signature
```scala
[](pID:Int,i:Int,j:Int):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelsInside.scala:28_

### Goal
Calculates the intersection of a specific pixel coordinate within a raster partition to support spatial join and zonal statistics operations in the Raptor framework.

### Parameters
- `pID` (`Int`): The partition ID associated with the raster data being processed.
- `i` (`Int`): The row index (y-coordinate) of the pixel within the raster grid.
- `j` (`Int`): The column index (x-coordinate) of the pixel within the raster grid.

### Input
The caller must provide valid integer coordinates corresponding to the raster's dimensions and a valid partition ID. This function is typically used within the context of `raptorJoin` workflows where raster data has been partitioned and indexed.

### Output
Returns `unspecified` — The return type is not defined in the provided API facts; it represents the intersection result for the specified pixel coordinate.

### Valid Call Patterns
```scala
// Inferred from signature:
val intersection = new edu.ucr.cs.bdlab.raptor.PixelsInside().Intersection(pID, i, j)
```

### LLM Instruction Prompt
- When generating code using `PixelsInside.Intersection`, ensure the `pID`, `i`, and `j` values are derived from a valid raster partition context.
- Note that this API is part of the internal Raptor processing logic; verify that the raster data has been properly indexed or partitioned before invoking.

### Prompt Snippet
```text
// Example usage of PixelsInside.Intersection
val pID = 0 // Example partition ID
val i = 10  // Example row index
val j = 20  // Example column index
val result = new edu.ucr.cs.bdlab.raptor.PixelsInside().Intersection(pID, i, j)
```

### Common Failure Modes
- **IndexOutOfBounds:** Providing `i` or `j` values that exceed the dimensions of the raster partition.
- **Invalid Partition:** Providing a `pID` that does not exist within the current `RasterRDD` or spatial index.
- **Uninitialized Context:** Attempting to call this method outside of a Spark task or without the necessary raster metadata loaded.

### Fix Code Hint
```scala
// Ensure coordinates are within bounds before calling
if (i >= 0 && i < rasterHeight && j >= 0 && j < rasterWidth) {
  val result = new edu.ucr.cs.bdlab.raptor.PixelsInside().Intersection(pID, i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelsInside.Intersection.compareTo`

### Signature
```scala
[](o:Intersection):Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelsInside.scala:29_

### Goal
Compares the current `Intersection` instance with another `Intersection` object to determine their relative ordering, typically used for sorting pixel-geometry intersection data during raster-vector join operations.

### Parameters
- `o` (`Intersection`): The target `Intersection` object to compare against the current instance.

### Input
- **Data:** Requires two initialized `Intersection` objects.
- **Preconditions:** The objects must be part of a valid `PixelsInside` calculation context within a Raptor join workflow. No specific file format is required as this operates on in-memory objects.

### Output
Returns `Int` — A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object, respectively.

### Valid Call Patterns
```scala
// Inferred from signature:
val intersection1: Intersection = ...
val intersection2: Intersection = ...
val result: Int = intersection1.compareTo(intersection2)
```

### LLM Instruction Prompt
- When generating code that uses `compareTo`, ensure the receiver and the argument `o` are both instances of `edu.ucr.cs.bdlab.raptor.PixelsInside.Intersection`.
- Use this method primarily when implementing custom sorting logic for `RaptorJoinFeature` collections or when manually ordering intersection results.

### Prompt Snippet
```text
val result = intersectionA.compareTo(intersectionB)
```

### Common Failure Modes
- **NullPointerException:** Occurs if the provided `o` argument is `null`.
- **Type Mismatch:** Attempting to compare an `Intersection` object with an object of a different type, which will fail at compile time.
- **Contextual Invalidity:** Calling this on uninitialized or partially constructed `Intersection` objects that do not contain valid spatial metadata.

### Fix Code Hint
```scala
// Ensure both objects are non-null before comparison
if (intersectionA != null && intersectionB != null) {
  val comparison = intersectionA.compareTo(intersectionB)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelsInside.IntersectionRange`

### Signature
```scala
[](pID:Int,j:Int,i1:Int,i2:Int):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelsInside.scala:46_

### Goal
Calculates the intersection range of pixels within a specific raster partition for a given row index, facilitating efficient raster-vector join operations.

### Parameters
- `pID` (`Int`): The partition ID of the raster data being processed.
- `j` (`Int`): The row index (y-coordinate) within the raster tile.
- `i1` (`Int`): The starting column index (x-coordinate) of the range.
- `i2` (`Int`): The ending column index (x-coordinate) of the range.

### Input
The function operates on raster data structures managed by the Raptor component. Preconditions include that the raster must be partitioned and indexed appropriately for the current Spark context. This function is typically invoked during the internal processing of `raptorJoin` operations where raster-vector intersections are computed.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it is expected to return a range or set of pixel coordinates representing the intersection within the specified bounds.

### Valid Call Patterns
```scala
// Example inferred from signature:
val intersection = new edu.ucr.cs.bdlab.raptor.PixelsInside().IntersectionRange(pID, j, i1, i2)
```

### LLM Instruction Prompt
- When generating code using this API, ensure the `pID` corresponds to a valid partition ID within the current `RasterRDD`.
- The indices `j`, `i1`, and `i2` must be within the bounds of the raster tile dimensions.
- This function is intended for internal Raptor processing; ensure it is used within the context of a Spark transformation or a custom Raptor join implementation.

### Prompt Snippet
```text
// Ensure raster indices are within bounds before calling IntersectionRange
val pID = 0
val j = 10
val i1 = 5
val i2 = 20
val result = new edu.ucr.cs.bdlab.raptor.PixelsInside().IntersectionRange(pID, j, i1, i2)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if `j`, `i1`, or `i2` exceed the dimensions of the raster tile associated with `pID`.
- **Invalid Partition ID**: Providing a `pID` that does not exist in the current `RasterRDD` partition map.
- **Context Mismatch**: Attempting to use this function outside of the Raptor processing pipeline where raster metadata is not initialized.

### Fix Code Hint
```scala
// Validate bounds before calling
if (j >= 0 && j < tileHeight && i1 >= 0 && i2 < tileWidth && i1 <= i2) {
  val result = new edu.ucr.cs.bdlab.raptor.PixelsInside().IntersectionRange(pID, j, i1, i2)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelsInside.IntersectionRange.compareTo`

### Signature
```scala
[](o:IntersectionRange):Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelsInside.scala:47_

### Goal
Compares two `IntersectionRange` objects to determine their relative ordering, typically used for sorting pixel intersection intervals during raster-vector join operations.

### Parameters
- `o` (`IntersectionRange`): The `IntersectionRange` instance to compare against the current instance.

### Input
- **Data:** Requires two initialized `IntersectionRange` objects.
- **Preconditions:** The objects must be part of a valid `PixelsInside` calculation context where intersection intervals are defined.

### Output
Returns `Int` — A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object `o`.

### Valid Call Patterns
```scala
// Inferred from signature:
val range1: IntersectionRange = ...
val range2: IntersectionRange = ...
val result: Int = range1.compareTo(range2)
```

### LLM Instruction Prompt
- When generating code involving `IntersectionRange`, ensure the objects are compared using `compareTo` to maintain consistent ordering during spatial join processing.
- Note that this method is part of the `PixelsInside` internal logic for Raptor raster-vector joins.

### Prompt Snippet
```text
// Compare two IntersectionRange objects for sorting
val comparisonResult = rangeA.compareTo(rangeB)
```

### Common Failure Modes
- **NullPointerException:** Occurs if the provided `IntersectionRange` argument `o` is null.
- **Ordering Inconsistency:** If used in custom sorting logic, ensure that the comparison logic aligns with the expected spatial indexing order of the Raptor join algorithm.

### Fix Code Hint
```scala
// Ensure the object is not null before comparison
if (rangeA != null && rangeB != null) {
  val result = rangeA.compareTo(rangeB)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.PixelsInside.next`

### Signature
```scala
[]():scala.Tuple3[Int, Int, Int]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/PixelsInside.scala:207_

### Goal
Retrieves the next pixel coordinate and associated index from the `PixelsInside` iterator during raster-vector intersection processing.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.PixelsInside`. This class is typically used internally within the Raptor join workflow to iterate over pixels that fall within a specific vector geometry. No external file inputs are required for the method call itself, but the parent object must be in a valid state (i.e., the iterator has not been exhausted).

### Output
Returns `scala.Tuple3[Int, Int, Int]` — A tuple containing the (x, y) coordinate of the pixel and an associated index value (often representing the feature ID or a sequence identifier) for the current pixel being processed.

### Valid Call Patterns
```scala
// Inferred from signature:
val pixelsInside: edu.ucr.cs.bdlab.raptor.PixelsInside = ... // initialized instance
val nextPixel: (Int, Int, Int) = pixelsInside.next()
```

### LLM Instruction Prompt
- When generating code that uses `PixelsInside.next()`, ensure the call is wrapped in a loop or conditional check (e.g., `hasNext`) to prevent `NoSuchElementException`.
- Treat the returned `Tuple3` as an immutable coordinate-index triplet.
- Do not attempt to instantiate `PixelsInside` directly unless the context explicitly provides the necessary raster/geometry metadata required by its constructor.

### Prompt Snippet
```text
// Example of safe iteration
while (pixelsInside.hasNext) {
  val (x, y, id) = pixelsInside.next()
  // Process pixel at (x, y) for feature id
}
```

### Common Failure Modes
- **NoSuchElementException**: Occurs if `next()` is called when `hasNext` is false. Always verify the iterator state before calling.
- **NullPointerException**: Occurs if the `PixelsInside` instance was not properly initialized with valid raster metadata or geometry.
- **State Inconsistency**: If the underlying raster or geometry is modified or closed while the iterator is active, the behavior is undefined.

### Fix Code Hint
```scala
// Always check hasNext before calling next()
if (pixelsInside.hasNext) {
  val result = pixelsInside.next()
  // Handle result
} else {
  // Handle end of iteration
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinFeature`

### Signature
```scala
[type T](raster:RasterRDD[T],features:RDD[IFeature],opts:BeastOptions,numTiles:LongAccumulator):RDD[RaptorJoinFeature[T]]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RaptorJoin.scala:73_

### Goal
Performs a concurrent raster-vector join (Raptor join) to associate raster pixel values with overlapping vector geometries, enabling zonal statistics and spatial analysis.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset (e.g., GeoTIFF or HDF4) containing pixel values of type `T`.
- `features` (`RDD[IFeature]`): The input vector dataset (e.g., Shapefile, GeoJSON) containing geometries to be joined with the raster.
- `opts` (`BeastOptions`): Configuration options for the join operation.
- `numTiles` (`LongAccumulator`): An accumulator to track the number of raster tiles processed during the join.

### Input
- **Data Formats:** Raster data must be in a format supported by Beast (GeoTIFF, HDF4). Vector data must be in a format supported by Beast (Shapefile, GeoJSON, WKT).
- **Preconditions:** The `RasterRDD[T]` must be loaded using the appropriate type `T` (e.g., `Int`, `Float`) matching the pixel data. For optimal performance, ensure the `features` RDD is spatially partitioned (e.g., using `RSGrovePartitioner`).

### Output
Returns `RDD[RaptorJoinFeature[T]]` — An RDD containing the joined results, where each record links a vector feature with the corresponding raster pixel value(s) found within its geometry.

### Valid Call Patterns
```scala
val raster: RasterRDD[Float] = sc.geoTiff[Float]("data.tif")
val features: RDD[IFeature] = sc.shapefile("regions.zip")
val opts = new BeastOptions()
val numTiles = sc.longAccumulator("tilesProcessed")
val joinResult = edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinFeature(raster, features, opts, numTiles)
```

### LLM Instruction Prompt
- Always ensure the type parameter `T` of the `RasterRDD` matches the expected pixel data type (e.g., `Float` for continuous data, `Int` for categorical).
- Ensure the `features` RDD is properly loaded and available in the Spark context.
- Use `BeastOptions` to pass any necessary configuration parameters for the join.

### Prompt Snippet
```text
edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinFeature(rasterRDD, featureRDD, beastOptions, longAccumulator)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `RasterRDD` with a type `T` that does not match the actual pixel data (e.g., using `Int` for floating-point satellite data).
- **Missing Partitioning:** Failing to spatially partition the `features` RDD can lead to significant performance degradation or OOM errors on large datasets.
- **CRS Mismatch:** Raster and vector data must be in the same coordinate reference system; otherwise, the join will produce incorrect results or empty sets.

### Fix Code Hint
```scala
// Ensure correct type and partitioning
val raster = sc.geoTiff[Float]("input.tif")
val features = sc.shapefile("input.shp").spatialPartition(classOf[RSGrovePartitioner])
val opts = new BeastOptions()
val acc = sc.longAccumulator("tiles")
val result = edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinFeature(raster, features, opts, acc)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinFeatureM`

### Signature
```scala
[type T](raster:RasterRDD[T],features:RDD[IFeature],opts:BeastOptions,numTiles:LongAccumulator)(t:ClassTag[T]):RDD[scala.Tuple2[IFeature, T]]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RaptorJoin.scala:103_

### Goal
Performs a distributed spatial join between a `RasterRDD` and a vector `RDD[IFeature]`, mapping raster pixel values to the corresponding spatial features.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset containing pixel values of type `T`.
- `features` (`RDD[IFeature]`): The input vector dataset containing geometries to be joined with the raster.
- `opts` (`BeastOptions`): Configuration options for the join operation.
- `numTiles` (`LongAccumulator`): A Spark accumulator used to track the number of raster tiles processed during the join.
- `t` (`ClassTag[T]`): The `ClassTag` for the pixel type `T`, required for Spark serialization.

### Input
- **Data Formats:** Raster data must be in a format supported by `RasterRDD` (e.g., GeoTIFF, HDF4). Vector data must be provided as `RDD[IFeature]`.
- **Preconditions:** The raster and vector datasets should ideally be spatially partitioned to ensure performance. Ensure the pixel type `T` matches the underlying raster data (e.g., `Float`, `Int`).

### Output
Returns `RDD[scala.Tuple2[IFeature, T]]` — An RDD of tuples where each entry pairs an `IFeature` from the input vector RDD with the corresponding raster pixel value `T` found at that location.

### Valid Call Patterns
```scala
// Inferred from signature:
val numTiles = sc.longAccumulator("tilesProcessed")
val opts = new BeastOptions()
val joinedRDD = edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinFeatureM(rasterRDD, featureRDD, opts, numTiles)
```

### LLM Instruction Prompt
- When calling `raptorJoinFeatureM`, ensure the `ClassTag[T]` is implicitly provided or explicitly passed.
- Always initialize a `LongAccumulator` via the `SparkContext` before passing it to the function to monitor progress.
- Ensure the `BeastOptions` object is configured according to the specific requirements of the join (e.g., spatial indexing parameters).

### Prompt Snippet
```text
val numTiles = sc.longAccumulator("tiles")
val result = RaptorJoin.raptorJoinFeatureM(raster, features, new BeastOptions(), numTiles)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `ClassTag` that does not match the actual pixel type `T` of the `RasterRDD` will cause runtime serialization errors.
- **Missing Partitioning:** Failing to spatially partition the `features` RDD (e.g., using `RSGrovePartitioner`) can lead to significant performance degradation or OOM errors on large datasets.
- **Uninitialized Accumulator:** Passing a null or uninitialized `LongAccumulator` will result in a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure proper partitioning before join
val partitionedFeatures = features.spatialPartition(classOf[RSGrovePartitioner])
val result = RaptorJoin.raptorJoinFeatureM(raster, partitionedFeatures, opts, numTiles)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinIDFull`

### Signature
```scala
[type T](raster:RDD[ITile[T]],vector:RDD[scala.Tuple2[Long, IFeature]],opts:BeastOptions,numTiles:LongAccumulator,numRanges:LongAccumulator):RDD[RaptorJoinResult[T]]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RaptorJoin.scala:180_

### Goal
Performs a full raster-vector join operation that associates raster pixel values with specific vector features identified by a unique ID, enabling zonal statistics and pixel-level analysis across distributed datasets.

### Parameters
- `raster` (`RDD[ITile[T]]`): The distributed raster dataset represented as an RDD of tiles, where `T` is the pixel data type (e.g., `Float`, `Int`).
- `vector` (`RDD[scala.Tuple2[Long, IFeature]]`): The distributed vector dataset, where each feature is paired with a `Long` identifier used for the join.
- `opts` (`BeastOptions`): Configuration options for the join operation, including tuning parameters for the Raptor join process.
- `numTiles` (`LongAccumulator`): An accumulator to track the total number of raster tiles processed during the join.
- `numRanges` (`LongAccumulator`): An accumulator to track the total number of spatial ranges or partitions processed.

### Input
- **Data:** Requires a raster dataset (GeoTIFF or HDF4) and a vector dataset (Shapefile, GeoJSON, etc.).
- **Preconditions:** Both datasets should be spatially indexed or partitioned to ensure performance. Raster data must be compatible in terms of resolution and CRS; use `reshape` if alignment is required.
- **Type Selection:** The type parameter `T` must match the underlying pixel data type of the raster (e.g., `FloatType`, `IntegerType`).

### Output
Returns `RDD[RaptorJoinResult[T]]` — A distributed collection of join results, where each record links a vector feature ID to the corresponding raster pixel values found within its geometry.

### Valid Call Patterns
```scala
// Inferred from signature:
val numTiles = sc.longAccumulator("tiles")
val numRanges = sc.longAccumulator("ranges")
val joinResult = edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinIDFull(rasterRDD, vectorRDD, new BeastOptions(), numTiles, numRanges)
```

### LLM Instruction Prompt
- When calling `raptorJoinIDFull`, ensure the `T` type parameter is explicitly defined to match the raster's pixel type.
- Always provide initialized `LongAccumulator` instances to monitor the join progress.
- Ensure the `vector` RDD is correctly mapped to `Tuple2[Long, IFeature]` before invocation.

### Prompt Snippet
```text
val joinResult = edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinIDFull[Float](rasterRDD, vectorRDD, opts, accTiles, accRanges)
```

### Common Failure Modes
- **Type Mismatch:** Providing a type `T` that does not match the raster's pixel data (e.g., using `Double` for an `Int` raster) will cause runtime errors.
- **Uninitialized Accumulators:** Passing null or uninitialized accumulators will result in a `NullPointerException`.
- **Spatial Misalignment:** Failing to align raster metadata (resolution/CRS) before the join will lead to empty results or incorrect spatial associations.

### Fix Code Hint
```scala
// Ensure raster and vector are prepared
val raster = sc.geoTiff[Float](path)
val vector = features.map(f => (f.getId.toLong, f))
val opts = new BeastOptions()
val tilesAcc = sc.longAccumulator("tiles")
val rangesAcc = sc.longAccumulator("ranges")

val result = edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinIDFull[Float](raster, vector, opts, tilesAcc, rangesAcc)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinIDFullTile`

### Signature
```scala
[type T](raster:RDD[ITile[T]],vector:RDD[scala.Tuple2[Long, IFeature]],opts:BeastOptions,numTiles:LongAccumulator,numRanges:LongAccumulator)(t:ClassTag[T]):RDD[RaptorJoinResultTile[T]]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RaptorJoin.scala:126_

### Goal
Performs a high-performance raster-vector join by associating raster tiles with indexed vector features, producing a result set that maps spatial intersections between the two datasets.

### Parameters
- `raster` (`RDD[ITile[T]]`): The input raster data represented as an RDD of tiles.
- `vector` (`RDD[scala.Tuple2[Long, IFeature]]`): The input vector data, where each feature is paired with a unique `Long` identifier.
- `opts` (`BeastOptions`): Configuration options for the join operation.
- `numTiles` (`LongAccumulator`): An accumulator to track the number of raster tiles processed during the join.
- `numRanges` (`LongAccumulator`): An accumulator to track the number of spatial ranges processed.
- `t` (`ClassTag[T]`): The class tag for the pixel type `T` (e.g., `Float`, `Int`), required for Spark serialization.

### Input
- **Data:** Requires a raster dataset (GeoTIFF or HDF4) and a vector dataset (Shapefile, GeoJSON, etc.).
- **Preconditions:** The raster and vector datasets should ideally be spatially partitioned to ensure optimal performance. Raster data must be compatible with the join; use `reshape` if metadata (resolution, CRS, tile size) differs between inputs.
- **Type Selection:** The type parameter `T` must match the underlying pixel data type of the raster (e.g., `Float` for `FloatType` rasters).

### Output
Returns `RDD[RaptorJoinResultTile[T]]` — A distributed collection of join results where each element contains the intersection of raster pixel data and the corresponding vector feature geometry.

### Valid Call Patterns
```scala
// Inferred from signature:
val result = edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinIDFullTile(
  rasterRDD, 
  vectorRDD, 
  beastOpts, 
  sc.longAccumulator("tiles"), 
  sc.longAccumulator("ranges")
)
```

### LLM Instruction Prompt
- When calling `raptorJoinIDFullTile`, ensure the `raster` RDD pixel type `T` matches the `ClassTag[T]` provided.
- Always initialize `LongAccumulator` instances via the `SparkContext` before passing them to the function.
- Ensure the `vector` RDD is correctly mapped to `Tuple2[Long, IFeature]` where the `Long` is a unique identifier for the feature.

### Prompt Snippet
```text
// Example usage of RaptorJoinIDFullTile
val numTiles = sc.longAccumulator("numTiles")
val numRanges = sc.longAccumulator("numRanges")
val joinResult = RaptorJoin.raptorJoinIDFullTile[Float](rasterRDD, vectorRDD, opts, numTiles, numRanges)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `ClassTag` that does not match the actual pixel type `T` of the `ITile` RDD will cause runtime serialization errors.
- **Metadata Incompatibility:** Failing to align raster metadata (CRS/Resolution) before the join will result in empty or incorrect join results.
- **Missing Accumulators:** Passing null or uninitialized accumulators will trigger a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure pixel type consistency
val result = RaptorJoin.raptorJoinIDFullTile[Float](
  rasterRDD, 
  vectorRDD, 
  new BeastOptions(), 
  sc.longAccumulator("tiles"), 
  sc.longAccumulator("ranges")
)(scala.reflect.classTag[Float])
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinLocal`

### Signature
```scala
[type T](rasters:Array[IRasterReader[T]],geometries:Array[Geometry]):Iterator[RaptorJoinResult[T]]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RaptorJoin.scala:252_

### Goal
Performs a local, non-distributed join between an array of raster data sources and an array of vector geometries to extract pixel values intersecting the provided geometries.

### Parameters
- `rasters` (`Array[IRasterReader[T]]`): An array of raster readers (e.g., GeoTIFF or HDF4) containing the pixel data to be queried. The type `T` must match the underlying pixel data type (e.g., `Float`, `Int`).
- `geometries` (`Array[Geometry]`): An array of JTS `Geometry` objects representing the spatial features to be joined against the raster data.

### Input
- **Data:** Requires initialized `IRasterReader` instances and JTS `Geometry` objects.
- **Formats:** Supports standard raster formats (GeoTIFF, HDF4) and vector geometries compatible with the JTS Topology Suite.
- **Preconditions:** The raster data must be accessible locally. Ensure that the coordinate reference systems (CRS) of the rasters and geometries are aligned, as this function performs a local join without automatic reprojection.

### Output
Returns `Iterator[RaptorJoinResult[T]]` — An iterator containing the results of the join, where each `RaptorJoinResult` associates the input geometry with the corresponding pixel values extracted from the provided rasters.

### Valid Call Patterns
```scala
// Example inferred from signature:
val rasters: Array[IRasterReader[Float]] = Array(rasterReader1, rasterReader2)
val geometries: Array[Geometry] = Array(geom1, geom2)
val results = edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinLocal[Float](rasters, geometries)
```

### LLM Instruction Prompt
- When calling `raptorJoinLocal`, ensure the type parameter `T` matches the pixel type of the `IRasterReader` (e.g., `Float`, `Int`).
- Provide an array of `IRasterReader` and an array of JTS `Geometry` objects.
- Use this function only for local, non-distributed processing; for large-scale Spark RDD operations, prefer the `raptorJoin` extension method on `RasterRDD`.

### Prompt Snippet
```text
val joinResults = edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinLocal[T](rasterArray, geometryArray)
```

### Common Failure Modes
- **Type Mismatch:** Providing a type `T` that does not match the raster's internal pixel representation will cause runtime errors.
- **CRS Mismatch:** If the raster and geometry are in different coordinate systems, the join will return empty or incorrect results because the function does not perform coordinate transformation.
- **Memory Constraints:** Since this is a local operation, passing an excessively large array of geometries or high-resolution rasters may lead to `OutOfMemoryError`.

### Fix Code Hint
```scala
// Ensure type consistency and valid geometry inputs
val results = edu.ucr.cs.bdlab.raptor.RaptorJoin.raptorJoinLocal[Float](myRasters, myGeometries)
results.foreach(res => println(s"Result: ${res.toString}"))
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RaptorJoinFeature`

### Signature
```scala
[](feature:IFeature,rasterMetadata:RasterMetadata,x:Int,y:Int,m:T):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RaptorJoinResult.scala:33_

### Goal
Represents a single result record from a Raptor join operation, associating a specific vector feature with a pixel value extracted from a raster at a given coordinate.

### Parameters
- `feature` (`IFeature`): The vector geometry or record associated with the join result.
- `rasterMetadata` (`RasterMetadata`): Metadata describing the source raster, including CRS, resolution, and tile dimensions.
- `x` (`Int`): The column index (pixel coordinate) of the raster data.
- `y` (`Int`): The row index (pixel coordinate) of the raster data.
- `m` (`T`): The pixel value (measure) at the specified (x, y) coordinate, where `T` corresponds to the raster's data type (e.g., `Float`, `Int`).

### Input
The caller must provide a valid `IFeature` (typically from a vector RDD) and `RasterMetadata` derived from the source raster (GeoTIFF or HDF4). This class is typically instantiated internally by the `raptorJoin` transformation on `RasterRDD`.

### Output
Returns an instance of `RaptorJoinFeature[T]`. The exact return type is inferred from the generic type `T` of the pixel value.

### Valid Call Patterns
```scala
// Example of manual instantiation (typically handled by the framework)
val joinResult = new edu.ucr.cs.bdlab.raptor.RaptorJoinFeature(feature, metadata, x, y, pixelValue)
```

### LLM Instruction Prompt
- When generating code involving `RaptorJoinFeature`, ensure the generic type `T` matches the pixel type of the source raster (e.g., `Float` for `FloatType` rasters).
- Do not attempt to instantiate this class directly unless performing custom join logic; prefer using the `raptorJoin` method on `RasterRDD`.

### Prompt Snippet
```text
// Use RaptorJoinFeature to wrap join results
val result = new edu.ucr.cs.bdlab.raptor.RaptorJoinFeature[Float](feature, meta, x, y, value)
```

### Common Failure Modes
- **Type Mismatch:** Providing a value `m` that does not match the expected raster pixel type (e.g., passing `Double` when the raster is `Float`).
- **Metadata Inconsistency:** Using `RasterMetadata` that does not align with the actual raster file, leading to incorrect coordinate mapping.
- **Coordinate Out of Bounds:** Providing `x` or `y` values that fall outside the dimensions defined in `rasterMetadata`.

### Fix Code Hint
```scala
// Ensure type consistency with the raster
val join: RDD[RaptorJoinFeature[Float]] = rasterRDD.raptorJoin(vectorRDD)
// Filter results based on the measure 'm'
val filtered = join.filter(v => v.m >= 0.0f)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RaptorJoinResult`

### Signature
```scala
[](featureID:Long,rasterMetadata:RasterMetadata,x:Int,y:Int,m:T):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RaptorJoinResult.scala:29_

### Goal
Represents a single result record from a Raptor join operation, mapping a specific vector feature to a corresponding pixel value within a raster dataset.

### Parameters
- `featureID` (`Long`): The unique identifier of the vector feature involved in the join.
- `rasterMetadata` (`RasterMetadata`): The metadata object describing the source raster, including CRS, resolution, and dimensions.
- `x` (`Int`): The column index (pixel coordinate) of the raster.
- `y` (`Int`): The row index (pixel coordinate) of the raster.
- `m` (`T`): The pixel value at the specified (x, y) coordinate, where `T` is the raster data type (e.g., `Float`, `Int`).

### Input
The caller must provide a valid `featureID` corresponding to a vector dataset and a `RasterMetadata` object that matches the raster being queried. The `x` and `y` coordinates must fall within the bounds defined by the `rasterMetadata`.

### Output
Returns `unspecified` — This class acts as a container for the join result, typically used within an `RDD` to associate vector geometries with their overlapping raster pixel values.

### Valid Call Patterns
```scala
// Example inferred from signature:
val result = new edu.ucr.cs.bdlab.raptor.RaptorJoinResult(featureID, metadata, x, y, pixelValue)
```

### LLM Instruction Prompt
- When generating code using `RaptorJoinResult`, ensure the type parameter `T` matches the pixel type of the raster (e.g., `Float` for `FloatType` rasters).
- Always ensure the `RasterMetadata` provided is consistent with the raster source used in the join.

### Prompt Snippet
```text
val joinResult = new edu.ucr.cs.bdlab.raptor.RaptorJoinResult[Float](featureId, rasterMeta, x, y, pixelValue)
```

### Common Failure Modes
- **Type Mismatch:** Providing a value `m` that does not match the expected raster pixel type `T` (e.g., passing a `Double` when the raster is `Float`).
- **Out of Bounds:** Providing `x` or `y` coordinates that exceed the dimensions specified in the `RasterMetadata`.
- **Metadata Inconsistency:** Using `RasterMetadata` from a different raster source, which will lead to incorrect spatial mapping.

### Fix Code Hint
```scala
// Ensure the type T is explicitly defined to match the raster pixel type
val result = new edu.ucr.cs.bdlab.raptor.RaptorJoinResult[Float](
  feature.getID, 
  raster.metadata, 
  pixelX, 
  pixelY, 
  pixelValue
)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RaptorJoinResultTile`

### Signature
```scala
[](featureID:Long,tile:ITile[T]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RaptorJoinResult.scala:31_

### Goal
Represents a container for a specific raster tile associated with a unique vector feature ID, used within the Raptor join workflow to link spatial features to their corresponding pixel data.

### Parameters
- `featureID` (`Long`): The unique identifier of the vector feature that is being joined with the raster data.
- `tile` (`ITile[T]`): The raster tile data containing pixel values of type `T` that intersect with the specified feature.

### Input
The caller must provide a valid `Long` identifier and an `ITile[T]` instance. This class is typically used internally within the `raptorJoin` workflow. Preconditions include ensuring the `ITile[T]` is compatible with the raster data source (e.g., matching pixel type `T` such as `Float` or `Int`).

### Output
Returns an instance of `RaptorJoinResultTile` (type inferred from context). This object encapsulates the association between a vector feature and a raster tile for downstream processing or aggregation.

### Valid Call Patterns
```scala
// Example inferred from signature:
val result = new edu.ucr.cs.bdlab.raptor.RaptorJoinResultTile(featureID, tile)
```

### LLM Instruction Prompt
- When generating code that uses `RaptorJoinResultTile`, ensure the `tile` parameter matches the generic type `T` expected by the surrounding `RasterRDD[T]` or `ITile[T]` operations.
- Always treat this as a container object used during the Raptor join process; do not attempt to instantiate it manually unless performing custom low-level raster-vector join logic.

### Prompt Snippet
```text
val joinResult = new edu.ucr.cs.bdlab.raptor.RaptorJoinResultTile(featureID, tile)
```

### Common Failure Modes
- **Type Mismatch:** Providing a tile with a pixel type `T` that does not match the expected type of the raster dataset (e.g., passing `ITile[Int]` when `Float` is required).
- **Invalid Feature ID:** Passing a `featureID` that does not exist in the corresponding vector dataset, leading to empty or incorrect join results.
- **Memory Constraints:** Attempting to instantiate large numbers of these objects in a single Spark partition may lead to `OutOfMemoryError` if the tiles are not processed or spilled correctly.

### Fix Code Hint
```scala
// Ensure the tile type matches the raster source
val tile: ITile[Float] = ... 
val result = new edu.ucr.cs.bdlab.raptor.RaptorJoinResultTile[Float](featureID, tile)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterFilePartition`

### Signature
```scala
[](index:Int,path:String,offset:Long,length:Long,locations:Array[String]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterFileRDD.scala:40_

### Goal
Defines a specific data partition within a raster file for distributed processing within the Raptor framework.

### Parameters
- `index` (`Int`): The unique identifier for this partition within the RDD.
- `path` (`String`): The file system path to the raster data source (e.g., GeoTIFF or HDF4).
- `offset` (`Long`): The starting byte position of this partition within the source file.
- `length` (`Long`): The number of bytes contained in this partition.
- `locations` (`Array[String]`): A list of hostnames or IP addresses where the data partition is localized for data-aware scheduling.

### Input
- **Data:** Raster files in supported formats (GeoTIFF, HDF4).
- **Preconditions:** The file must be accessible at the provided `path` across the Spark cluster. This class is typically used internally by `RasterFileRDD` to split large raster files into manageable chunks for parallel processing.

### Output
Returns `unspecified` — Represents a partition metadata object used by the Spark scheduler to assign specific byte ranges of a raster file to executor nodes.

### Valid Call Patterns
```scala
// Example inferred from signature:
val partition = new edu.ucr.cs.bdlab.raptor.RasterFilePartition(0, "hdfs:///data/raster.tif", 0L, 1024L, Array("node1.cluster"))
```

### LLM Instruction Prompt
- When generating code involving `RasterFilePartition`, ensure the `path` points to a valid raster file accessible by the Spark cluster.
- Use this class only when manually constructing or inspecting the partitioning logic of a `RasterFileRDD`.
- Ensure `offset` and `length` align with the internal structure of the raster file to avoid data corruption during pixel-level operations.

### Prompt Snippet
```text
val partition = new edu.ucr.cs.bdlab.raptor.RasterFilePartition(index, path, offset, length, locations)
```

### Common Failure Modes
- **FileNotFoundException:** Occurs if the `path` is incorrect or inaccessible from the worker nodes.
- **IndexOutOfBounds:** Providing an `offset` and `length` that exceed the actual file size will cause errors during subsequent read operations.
- **Data Misalignment:** Incorrect byte offsets for specific raster formats (like HDF4) can lead to invalid pixel values or parsing failures.

### Fix Code Hint
```scala
// Ensure the path is fully qualified and accessible
val partition = new edu.ucr.cs.bdlab.raptor.RasterFilePartition(
  index = 0, 
  path = "file:///path/to/raster.tif", 
  offset = 0L, 
  length = fileSize, 
  locations = Array("localhost")
)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterFilePartition.toString`

### Signature
```scala
[]:String
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterFileRDD.scala:42_

### Goal
Returns a string representation of a `RasterFilePartition` instance, typically used for debugging or logging the partition's metadata within a `RasterFileRDD`.

### Parameters
_None._

### Input
The caller must have an instance of `edu.ucr.cs.bdlab.raptor.RasterFilePartition`. This object is typically generated internally by the `RasterFileRDD` during the spatial partitioning of raster data (GeoTIFF or HDF4). No specific file format is required as input to this method, as it operates on an already instantiated partition object.

### Output
Returns `String` — A string containing descriptive information about the partition, which may include details such as the file path, offset, or spatial extent covered by the partition.

### Valid Call Patterns
```scala
// Inferred from signature:
val partition: RasterFilePartition = ... // obtained from RasterFileRDD
val partitionInfo: String = partition.toString()
```

### LLM Instruction Prompt
- When generating code that involves `RasterFilePartition`, use `toString()` only for logging or diagnostic purposes.
- Do not rely on the specific format of the returned string for programmatic logic, as it is intended for human-readable output.
- Ensure the `RasterFilePartition` instance is accessed from a valid `RasterFileRDD` context.

### Prompt Snippet
```text
// Accessing partition metadata for logging
val partition = rdd.partitions(0).asInstanceOf[RasterFilePartition]
println(s"Processing partition: ${partition.toString()}")
```

### Common Failure Modes
- **NullPointerException**: Calling `toString()` on a null reference if the partition was not correctly initialized or retrieved from the RDD.
- **ClassCastException**: Attempting to cast a generic `Partition` object to `RasterFilePartition` when the underlying RDD is not a `RasterFileRDD`.
- **Contextual Misuse**: Attempting to use the output string to reconstruct the partition or perform file I/O, which is not supported by this method.

### Fix Code Hint
```scala
// Ensure the partition is of the correct type before calling toString
rdd.partitions.foreach {
  case p: RasterFilePartition => println(p.toString())
  case _ => // Handle non-raster partitions
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterFileRDD`

### Signature
```scala
[](sc:SparkContext,path:String,_opts:BeastOptions):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterFileRDD.scala:48_

### Goal
Initializes a distributed raster dataset from a file or directory path for use within the Raptor (Raster + Vector) processing framework.

### Parameters
- `sc` (`SparkContext`): The active SparkContext used to distribute the raster data across the cluster.
- `path` (`String`): The file system path (local or distributed) to the raster file or directory containing raster files.
- `_opts` (`BeastOptions`): Configuration options for the raster ingestion process.

### Input
- **Formats:** Supports GeoTIFF and HDF4 raster formats.
- **Preconditions:** The input path must be accessible by the Spark executors. If the raster data is intended for use in `raptorJoin` operations, ensure the raster metadata (resolution, CRS, tile size) is consistent with other datasets involved in the join.

### Output
Returns `unspecified` — An RDD-based representation of the raster data, typically used as a `RasterRDD[T]` or `RDD[ITile[T]]` for subsequent pixel-level transformations or spatial joins.

### Valid Call Patterns
```scala
// Inferred usage pattern based on Raptor framework initialization
val rasterRDD = new edu.ucr.cs.bdlab.raptor.RasterFileRDD(sc, "path/to/raster.tif", new BeastOptions())
```

### LLM Instruction Prompt
- When generating code for `RasterFileRDD`, always ensure the `SparkContext` is initialized.
- Use `BeastOptions` to pass specific ingestion parameters if required by the environment.
- Note that the return type is generic; ensure the downstream code handles the specific pixel type (e.g., `Int`, `Float`) consistent with the source file.

### Prompt Snippet
```text
val raster = new edu.ucr.cs.bdlab.raptor.RasterFileRDD(sc, path, opts)
```

### Common Failure Modes
- **Type Mismatch:** Providing the wrong type parameter for the pixel data (e.g., `Int` vs `Float`) will cause runtime errors during pixel access. Verify the file's pixel type using `raster.first.pixelType` if unknown.
- **Metadata Incompatibility:** Attempting to perform `raptorJoin` or `overlay` operations on rasters with mismatched resolution or CRS will fail; use `reshape` to align rasters before processing.
- **Path Errors:** Providing a path that does not exist or is inaccessible to the Spark cluster will result in an `IOException`.

### Fix Code Hint
```scala
// Ensure pixel type matches the source file
val raster = new edu.ucr.cs.bdlab.raptor.RasterFileRDD(sc, "data.tif", new BeastOptions())
// If performing joins, verify metadata alignment
val alignedRaster = raster.reshape(referenceRaster)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterFileRDD.buildIndex`

### Signature
```scala
[](sparkContext:SparkContext,dir:String,indexFile:String):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterFileRDD.scala:186_

### Goal
Generates a spatial index for a collection of raster files located in a directory to enable efficient querying and Raptor join operations.

### Parameters
- `sparkContext` (`SparkContext`): The active Spark context used to distribute the indexing process across the cluster.
- `dir` (`String`): The path to the directory containing the source raster files (e.g., GeoTIFF or HDF4).
- `indexFile` (`String`): The destination path and filename for the generated index file.

### Input
- **Data:** A directory containing raster files.
- **Formats:** Supports standard raster formats including GeoTIFF and HDF4.
- **Preconditions:** The directory specified by `dir` must contain valid raster files readable by the Beast framework. The user must have write permissions to the location specified by `indexFile`.

### Output
Returns `Unit` — The function performs a side-effect operation, creating an index file at the specified `indexFile` path on the file system.

### Valid Call Patterns
```scala
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.RasterFileRDD.buildIndex(sc, "path/to/rasters", "path/to/output.index")
```

### LLM Instruction Prompt
- When calling `buildIndex`, ensure the `sparkContext` is active and initialized.
- The `dir` parameter must point to a valid directory containing raster data.
- The `indexFile` path should not already exist unless the environment supports overwriting, as standard Beast operations may fail if the output path is occupied.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.RasterFileRDD
// ... inside a Spark application
RasterFileRDD.buildIndex(sc, inputDirectory, outputIndexPath)
```

### Common Failure Modes
- **Path Conflicts:** The operation may fail if the `indexFile` path already exists (Beast operations often require `-overwrite` or manual deletion of existing output directories).
- **Format Incompatibility:** Providing a directory that contains non-raster files or unsupported formats may result in runtime exceptions during the indexing process.
- **Permissions:** Failure to write to the target `indexFile` location due to HDFS or local file system permission restrictions.

### Fix Code Hint
```scala
// Ensure the output path is clear before indexing
val fs = org.apache.hadoop.fs.FileSystem.get(sc.hadoopConfiguration)
val path = new org.apache.hadoop.fs.Path(indexFile)
if (fs.exists(path)) fs.delete(path, true)

edu.ucr.cs.bdlab.raptor.RasterFileRDD.buildIndex(sc, dir, indexFile)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterFileRDD.compute`

### Signature
```scala
[](split:Partition,context:TaskContext):Iterator[ITile[T]]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterFileRDD.scala:110_

### Goal
Computes the raster data for a specific partition by reading and decoding the underlying raster file (GeoTIFF or HDF4) into an iterator of tiles.

### Parameters
- `split` (`Partition`): The Spark partition defining the specific spatial extent or file segment to be processed by the current task.
- `context` (`TaskContext`): The Spark task context providing information about the execution environment and task lifecycle.

### Input
- **Data:** Raster data stored in supported formats (GeoTIFF or HDF4).
- **Preconditions:** The `RasterFileRDD` must be initialized with valid file paths. The pixel type `T` must be consistent with the underlying raster data (e.g., `Float`, `Int`).
- **Compatibility:** Ensure the raster metadata (CRS, resolution) is aligned if performing subsequent `overlay` or `raptorJoin` operations.

### Output
Returns `Iterator[ITile[T]]` — An iterator containing the raster tiles (`ITile`) corresponding to the requested partition, where `T` represents the pixel data type.

### Valid Call Patterns
```scala
// Inferred from signature:
val iterator = rasterFileRDD.compute(split, context)
```

### LLM Instruction Prompt
- When generating code that calls `compute`, ensure it is executed within the context of a Spark RDD transformation or action.
- Always verify that the `split` and `context` objects are provided by the Spark framework (e.g., within a `mapPartitions` block).
- Do not attempt to instantiate `Partition` or `TaskContext` manually; these are provided by the Spark runtime.

### Prompt Snippet
```text
// Example of using compute within a custom RDD transformation
rdd.mapPartitionsWithIndex { (index, _) =>
  val split = rdd.partitions(index)
  val context = TaskContext.get()
  val tiles = rdd.compute(split, context)
  tiles
}
```

### Common Failure Modes
- **Type Mismatch:** Providing a type `T` that does not match the actual pixel data type of the source raster (e.g., using `Float` for an `Integer` raster).
- **Partition Mismatch:** Passing a `Partition` object that does not belong to the `RasterFileRDD` instance.
- **Missing Context:** Calling `compute` outside of a Spark task execution environment where `TaskContext` is unavailable.

### Fix Code Hint
```scala
// Ensure the type parameter T matches the raster's pixel type
val tiles: Iterator[ITile[Float]] = rasterFileRDD.compute(split, context)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterFileRDD.selectFiles`

### Signature
```scala
[](fileSystem:FileSystem,dir:String,range:Geometry):Array[String]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterFileRDD.scala:233_

### Goal
Filters a directory of raster files to identify those that intersect with a specified spatial geometry.

### Parameters
- `fileSystem` (`FileSystem`): The Hadoop `FileSystem` instance used to access the directory containing the raster files.
- `dir` (`String`): The path to the directory containing the raster files (e.g., GeoTIFF or HDF4 files).
- `range` (`Geometry`): A JTS `Geometry` object representing the spatial area of interest used to filter the raster files.

### Input
- **Data:** A directory containing raster files (GeoTIFF or HDF4).
- **Preconditions:** The `FileSystem` must be initialized and have read access to the provided `dir`. The `range` geometry should be defined in the same coordinate reference system as the raster files to ensure accurate intersection detection.

### Output
Returns `Array[String]` — An array of file paths (Strings) corresponding to the raster files within the specified directory that intersect with the provided `range` geometry.

### Valid Call Patterns
```scala
// Example inferred from signature
val fs = FileSystem.get(sparkContext.hadoopConfiguration)
val files = edu.ucr.cs.bdlab.raptor.RasterFileRDD.selectFiles(fs, "/path/to/rasters", myGeometry)
```

### LLM Instruction Prompt
- When calling `selectFiles`, ensure the `FileSystem` is properly configured for the target storage (e.g., HDFS, S3, or local).
- Always provide a valid JTS `Geometry` for the `range` parameter.
- The function returns an array of strings; ensure the downstream processing handles potential empty arrays if no files intersect the geometry.

### Prompt Snippet
```text
val fs = FileSystem.get(sc.hadoopConfiguration)
val selected = edu.ucr.cs.bdlab.raptor.RasterFileRDD.selectFiles(fs, directoryPath, geometry)
```

### Common Failure Modes
- **FileSystem Exception:** Occurs if the `dir` path is invalid or the `FileSystem` lacks permissions to list the directory contents.
- **Coordinate Mismatch:** If the `range` geometry is in a different CRS than the raster files, the intersection logic may return incorrect results or empty sets.
- **Unsupported Format:** If the directory contains non-raster files that the underlying implementation cannot parse, it may lead to unexpected behavior or errors during the selection process.

### Fix Code Hint
```scala
// Ensure the geometry is in the correct CRS before calling
val geometryInRasterCRS = geometry.reproject(targetCRS)
val selectedFiles = edu.ucr.cs.bdlab.raptor.RasterFileRDD.selectFiles(fs, dir, geometryInRasterCRS)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile`

### Signature
```scala
[](existingTile:ITile[T],newMetadata:RasterMetadata):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:579_

### Goal
Reshapes or re-tiles an existing raster tile to conform to a new set of raster metadata specifications.

### Parameters
- `existingTile` (`ITile[T]`): The source raster tile containing pixel data of type `T`.
- `newMetadata` (`RasterMetadata`): The target metadata defining the desired resolution, coordinate reference system, and tile dimensions for the output.

### Input
- **Data:** Requires an `ITile[T]` object (e.g., from a `RasterRDD[T]`) and a valid `RasterMetadata` instance.
- **Preconditions:** The `newMetadata` must be compatible with the spatial extent of the `existingTile`. Ensure that the pixel type `T` matches the expected data type of the raster workflow (e.g., `Float`, `Int`).

### Output
Returns `unspecified` — The function returns a re-tiled representation of the input data adjusted to the geometry and resolution defined in `newMetadata`.

### Valid Call Patterns
```scala
// Example inferred from signature
val reTiled = new edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile(existingTile, newMetadata)
```

### LLM Instruction Prompt
- When calling `ReTile`, ensure the `newMetadata` object has been correctly initialized to match the target grid alignment.
- Always verify that the pixel type `T` of the `existingTile` is consistent with the downstream raster processing requirements (e.g., `FloatType` for continuous data).

### Prompt Snippet
```text
val reTiled = new edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile(tile, metadata)
```

### Common Failure Modes
- **Metadata Mismatch:** Attempting to re-tile using metadata that is incompatible with the source tile's spatial extent or CRS.
- **Type Mismatch:** Providing a tile with a pixel type `T` that does not align with the expected input type of the `RasterMetadata` or subsequent operations.
- **Initialization Errors:** Calling the constructor without ensuring the `RasterMetadata` object is fully populated with valid spatial parameters.

### Fix Code Hint
```scala
// Ensure metadata is aligned before re-tiling
val alignedMetadata = existingTile.metadata.reshape(targetResolution)
val reTiled = new edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile(existingTile, alignedMetadata)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile.copy`

### Signature
```scala
[]():Row
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:609_

### Goal
Creates a shallow or deep copy of the current `ReTile` instance, returning the state encapsulated as a Spark `Row` object.

### Parameters
_None._

### Input
The caller must be an instance of `edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile`. No additional arguments are required.

### Output
Returns `Row` — A Spark SQL `Row` object containing the serialized state or metadata of the `ReTile` operation.

### Valid Call Patterns
```scala
// Inferred from signature:
val reTileInstance: edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile = ...
val row: Row = reTileInstance.copy()
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure the receiver is an initialized `edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile` object.
- Do not attempt to pass arguments to the `copy()` method, as it takes zero parameters.
- The return type is `org.apache.spark.sql.Row`.

### Prompt Snippet
```text
val reTile = new edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile(...)
val result = reTile.copy()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `copy()` on an uninitialized or null `ReTile` reference.
- **Type Mismatch:** Attempting to assign the returned `Row` to a type other than `org.apache.spark.sql.Row`.
- **Contextual Scope:** Calling this method outside of a valid Spark execution context where `Row` is available.

### Fix Code Hint
```scala
import org.apache.spark.sql.Row
import edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile

// Ensure the instance is properly initialized before calling copy
val reTile = new ReTile() 
val row: Row = reTile.copy()
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile.getPixelValue`

### Signature
```scala
[](i:Int,j:Int):T
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:599_

### Goal
Retrieves the raw pixel value at a specific coordinate within a re-tiled raster segment during focal operations.

### Parameters
- `i` (`Int`): The row index (vertical coordinate) within the current tile.
- `j` (`Int`): The column index (horizontal coordinate) within the current tile.

### Input
The caller must be operating within the context of a `ReTile` instance, which is typically generated during focal raster processing workflows. The raster data must be properly initialized and partitioned for the focal operation.

### Output
Returns `T` — The pixel value at the specified `(i, j)` location, where `T` corresponds to the underlying data type of the raster (e.g., `Float`, `Int`).

### Valid Call Patterns
```scala
// Inferred from signature:
val pixelValue = reTileInstance.getPixelValue(i, j)
```

### LLM Instruction Prompt
- Use this method only when processing focal raster operations where a `ReTile` object is available.
- Ensure `i` and `j` are within the bounds of the current tile to avoid index out-of-bounds exceptions.
- The return type `T` must match the expected pixel type of the raster (e.g., `Float` for temperature data).

### Prompt Snippet
```text
val reTile: edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile = ...
val value = reTile.getPixelValue(row, col)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if `i` or `j` are outside the dimensions of the current tile.
- **Type Mismatch**: Occurs if the expected type `T` does not match the actual pixel data type stored in the raster (e.g., expecting `Float` when the raster contains `Int`).
- **Null Context**: Calling this method on an uninitialized or null `ReTile` instance.

### Fix Code Hint
```scala
// Ensure indices are within bounds before access
if (i >= 0 && i < tileHeight && j >= 0 && j < tileWidth) {
  val value = reTile.getPixelValue(i, j)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:603_

### Goal
Determines whether a specific tile at the given grid coordinates within a `ReTile` structure is empty or contains no valid data.

### Parameters
- `i` (`Int`): The row index of the tile within the `ReTile` grid.
- `j` (`Int`): The column index of the tile within the `ReTile` grid.

### Input
The caller must provide a valid instance of `edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile`. This method assumes the `ReTile` object has been initialized and populated with raster data (e.g., from GeoTIFF or HDF4 sources). The indices `i` and `j` must be within the bounds of the tile grid defined by the `ReTile` instance.

### Output
Returns `Boolean` — `true` if the tile at the specified `(i, j)` coordinates is empty (contains no valid pixel data), and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred usage based on class structure
val reTile: edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile = ...
val isTileEmpty: Boolean = reTile.isEmpty(i, j)
```

### LLM Instruction Prompt
- When calling `isEmpty`, ensure the `ReTile` instance is fully initialized.
- Always validate that `i` and `j` are within the bounds of the `ReTile` grid before invocation to avoid `ArrayIndexOutOfBoundsException`.
- Use this method to filter out empty tiles during focal operations or raster-vector joins to optimize processing performance.

### Prompt Snippet
```text
// Check if the tile at (i, j) is empty before processing
if (!reTile.isEmpty(i, j)) {
  // Perform focal operation on the tile
}
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if the provided `i` or `j` values exceed the dimensions of the `ReTile` grid.
- **NullPointerException**: Occurs if the `ReTile` instance itself has not been initialized or is null.
- **Logical Errors**: Assuming a tile is empty based on metadata without verifying via `isEmpty` can lead to processing null or default-value pixels, potentially skewing zonal statistics.

### Fix Code Hint
```scala
// Ensure bounds checking before calling isEmpty
if (i >= 0 && i < reTile.numRows && j >= 0 && j < reTile.numCols) {
  if (!reTile.isEmpty(i, j)) {
    // Process tile
  }
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile.toString`

### Signature
```scala
[]:String
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:611_

### Goal
Returns a string representation of the `ReTile` operation configuration for debugging or logging purposes.

### Parameters
_None._

### Input
No input parameters are required. This method is called on an instance of `edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile`.

### Output
Returns `String` — A string containing the internal state or configuration details of the `ReTile` object.

### Valid Call Patterns
```scala
// Inferred from signature:
val reTileOp = new edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile(/* parameters */)
val description = reTileOp.toString()
```

### LLM Instruction Prompt
- Use this method only to retrieve a human-readable description of a `ReTile` instance.
- Do not rely on the returned string format for programmatic parsing, as it is intended for logging and debugging.

### Prompt Snippet
```text
val reTileOp = new edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile(...)
val info = reTileOp.toString()
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `toString()` on an uninitialized or null reference of `ReTile`.
- **Contextual Misuse:** Attempting to use the returned string as a configuration input for other Beast raster operations; the string is for display only and does not represent a serialized format compatible with `beast` CLI or other API loaders.

### Fix Code Hint
```scala
// Ensure the ReTile instance is properly instantiated before calling toString
val reTileOp = new edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.ReTile()
println(s"Operation details: ${reTileOp.toString}")
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.convolution`

### Signature
```scala
[type T,type U](raster:RasterRDD[T],w:Int,weights:Array[Float])(evidence$10:ClassTag[T],evidence$11:ClassTag[U]):RasterRDD[U]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:671_

### Goal
Performs a focal convolution operation on a `RasterRDD` by applying a square kernel of size `w` with specified weights to each pixel neighborhood.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset to be processed.
- `w` (`Int`): The width (and height) of the square convolution kernel.
- `weights` (`Array[Float]`): A flat array of size `w * w` containing the convolution kernel weights.
- `evidence$10` (`ClassTag[T]`): Implicit class tag for the input pixel type `T`.
- `evidence$11` (`ClassTag[U]`): Implicit class tag for the output pixel type `U`.

### Input
The input must be a `RasterRDD[T]` (e.g., loaded from GeoTIFF or HDF4). The operation assumes the raster is spatially partitioned for distributed processing. Ensure that the input raster is properly aligned; if multiple rasters are involved in a workflow, use `reshape` to ensure identical metadata (resolution, CRS, tile size) before processing.

### Output
Returns `RasterRDD[U]` — A new raster dataset containing the result of the convolution, where each pixel value is the weighted sum of its `w x w` neighborhood.

### Valid Call Patterns
```scala
// Inferred from signature: Applying a 3x3 blur/filter kernel
val kernelSize = 3
val weights = Array(1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9)
val result = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.convolution(inputRaster, kernelSize, weights)
```

### LLM Instruction Prompt
- When calling `convolution`, ensure the `weights` array length is exactly `w * w`.
- Always provide the implicit `ClassTag` arguments if the Scala compiler cannot infer them automatically.
- Ensure the input `RasterRDD` is compatible with the focal operation (i.e., it is a valid `RasterRDD` type).

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.RasterOperationsFocal
import scala.reflect.ClassTag

val result = RasterOperationsFocal.convolution[T, U](raster, w, weights)
```

### Common Failure Modes
- **Weight Mismatch:** Providing an array of size not equal to `w * w` will likely result in an `ArrayIndexOutOfBoundsException` or incorrect focal calculations.
- **Type Mismatch:** Failing to provide correct `ClassTag` evidence for types `T` and `U` will cause compilation errors.
- **Metadata Inconsistency:** If the input `RasterRDD` has not been properly partitioned or aligned, the focal operation may produce unexpected results or fail during the Spark execution phase.

### Fix Code Hint
```scala
// Ensure weights match kernel dimensions
val w = 3
val weights = Array.fill(w * w)(1.0f / (w * w))
val result = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.convolution[Float, Float](myRaster, w, weights)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.divideScene`

### Signature
```scala
[type T](raster:RasterRDD[T],targetMetadata:RasterMetadata,numTilesX:Int,numTilesY:Int)(evidence$7:ClassTag[T]):RasterRDD[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:625_

### Goal
Divides an existing `RasterRDD` into a specified grid of tiles defined by `numTilesX` and `numTilesY` while reprojecting or aligning the raster to the provided `targetMetadata`.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset to be divided.
- `targetMetadata` (`RasterMetadata`): The metadata defining the target spatial extent, resolution, and CRS for the output raster.
- `numTilesX` (`Int`): The number of tiles to create along the X-axis.
- `numTilesY` (`Int`): The number of tiles to create along the Y-axis.
- `evidence$7` (`ClassTag[T]`): An implicit class tag required by Spark to handle the generic type `T` at runtime.

### Input
- **Data:** `RasterRDD[T]` (e.g., GeoTIFF or HDF4 data loaded into Beast).
- **Preconditions:** The input `raster` must be compatible with the `targetMetadata`. If the raster does not match the target grid, ensure it has been pre-processed using `reshape` or similar alignment operations to avoid metadata mismatch errors.

### Output
Returns `RasterRDD[T]` — A new `RasterRDD` partitioned into the requested grid structure, aligned to the `targetMetadata`.

### Valid Call Patterns
```scala
// Inferred usage pattern
val dividedRaster = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.divideScene(
  inputRaster, 
  targetMeta, 
  10, 
  10
)
```

### LLM Instruction Prompt
- When calling `divideScene`, ensure the `targetMetadata` is correctly initialized to match the desired output geometry.
- Always provide the implicit `ClassTag` if the compiler cannot infer it, though it is typically handled automatically in Scala.
- Ensure `numTilesX` and `numTilesY` are positive integers.

### Prompt Snippet
```text
// Example of dividing a raster into a 5x5 grid
val result = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.divideScene(myRaster, myMetadata, 5, 5)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `targetMetadata` that is incompatible with the input `raster` (e.g., different CRS or resolution) may lead to empty or malformed output.
- **Invalid Grid Dimensions:** Passing non-positive values for `numTilesX` or `numTilesY` will result in runtime exceptions.
- **Type Mismatch:** The generic type `T` must match the pixel type of the `RasterRDD` (e.g., `Float`, `Int`).

### Fix Code Hint
```scala
// Ensure metadata alignment before division
val alignedRaster = inputRaster.reshape(targetMetadata)
val divided = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.divideScene(alignedRaster, targetMetadata, 10, 10)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.reproject`

### Signature
```scala
[type T](raster:RasterRDD[T],targetCRS:CoordinateReferenceSystem,unifiedRaster:Boolean,interpolationMethod:InterpolationMethod.InterpolationMethod)(evidence$4:ClassTag[T]):RasterRDD[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:517_

### Goal
Transforms a `RasterRDD` from its current coordinate reference system to a specified target CRS while preserving the original resolution and tile size.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset to be reprojected.
- `targetCRS` (`CoordinateReferenceSystem`): The destination coordinate reference system for the output raster.
- `unifiedRaster` (`Boolean`): A flag indicating whether to produce a unified raster output.
- `interpolationMethod` (`InterpolationMethod.InterpolationMethod`): The mathematical method used for resampling pixel values during the transformation (e.g., Nearest Neighbor, Bilinear).
- `evidence$4` (`ClassTag[T]`): Implicit class tag required by Spark to handle the generic type `T` at runtime.

### Input
- **Data:** `RasterRDD[T]` loaded from supported formats such as GeoTIFF or HDF4.
- **Preconditions:** The input raster must be properly initialized within the Spark context. Ensure the pixel type `T` (e.g., `Float`, `Int`) matches the data source type (e.g., `sc.hdfFile` or `sc.geoTiff`).

### Output
Returns `RasterRDD[T]` — A new `RasterRDD` containing the pixel data transformed into the target CRS.

### Valid Call Patterns
```scala
// Example of reprojecting a temperature raster to EPSG:4326
val temperature: RasterRDD[Float] = sc.hdfFile("MOD11A1.hdf", "LST_Day_1km")
val reprojected = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.reproject(
  temperature, 
  targetCRS, 
  true, 
  InterpolationMethod.NearestNeighbor
)
```

### LLM Instruction Prompt
- When calling `reproject`, ensure the `targetCRS` is a valid `CoordinateReferenceSystem` object.
- Always provide the `interpolationMethod` explicitly from the `InterpolationMethod` enum.
- The `evidence$4` parameter is handled implicitly by the Scala compiler; do not attempt to pass it manually unless necessary for specific type-erasure scenarios.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.RasterOperationsFocal
import edu.ucr.cs.bdlab.raptor.InterpolationMethod

val result = RasterOperationsFocal.reproject(rasterRDD, crs, true, InterpolationMethod.NearestNeighbor)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `RasterRDD` with a type `T` that does not match the underlying data (e.g., using `Float` for an `Integer` raster) will cause runtime errors.
- **Missing CRS:** Passing an invalid or null `CoordinateReferenceSystem` will result in a projection failure.
- **Resource Constraints:** Reprojection is a computationally intensive operation; ensure the Spark cluster has sufficient memory for the transformation, especially for large rasters.

### Fix Code Hint
```scala
// Ensure the pixel type matches the source data
val raster: RasterRDD[Float] = sc.geoTiff[Float]("input.tif")
// Use the correct interpolation method for the data type
val output = RasterOperationsFocal.reproject(raster, targetCRS, true, InterpolationMethod.Bilinear)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.rescale`

### Signature
```scala
[type T](raster:RasterRDD[T],rasterWidth:Int,rasterHeight:Int,unifiedRaster:Boolean,interpolationMethod:InterpolationMethod.InterpolationMethod)(evidence$5:ClassTag[T]):RasterRDD[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:542_

### Goal
Rescales a `RasterRDD` to a new target pixel resolution (width and height) while maintaining the original coordinate reference system and tile size.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset to be rescaled.
- `rasterWidth` (`Int`): The target number of pixels along the X-axis.
- `rasterHeight` (`Int`): The target number of pixels along the Y-axis.
- `unifiedRaster` (`Boolean`): If true, forces the output into a unified raster structure.
- `interpolationMethod` (`InterpolationMethod.InterpolationMethod`): The algorithm used for pixel value estimation (e.g., Nearest Neighbor, Bilinear).
- `evidence$5` (`ClassTag[T]`): Implicit class tag required by Spark for the generic type `T`.

### Input
- **Data:** `RasterRDD[T]` loaded from supported formats like GeoTIFF or HDF4.
- **Preconditions:** The pixel type `T` must be consistent with the input data (e.g., `Int`, `Float`). Ensure the Spark context is initialized.

### Output
Returns `RasterRDD[T]` — A new `RasterRDD` representing the rescaled version of the input, suitable for further processing or saving via `saveAsGeoTiff`.

### Valid Call Patterns
```scala
// Example of rescaling a raster to a specific dimension
val raster: RasterRDD[Int] = sc.geoTiff[Int]("input.tif")
val rescaled = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.rescale(
  raster, 360, 180, true, InterpolationMethod.NearestNeighbor
)
```

### LLM Instruction Prompt
- When calling `rescale`, always provide the `rasterWidth` and `rasterHeight` as integers.
- Ensure the `interpolationMethod` is selected from the `InterpolationMethod` object.
- The `evidence$5` parameter is handled implicitly by the Scala compiler; do not attempt to pass it manually unless the compiler requires explicit evidence.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.RasterOperationsFocal
import edu.ucr.cs.bdlab.raptor.InterpolationMethod

val rescaledRaster = RasterOperationsFocal.rescale(myRaster, width, height, true, InterpolationMethod.NearestNeighbor)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `RasterRDD` with a type `T` that does not match the expected pixel data type.
- **Invalid Dimensions:** Passing non-positive values for `rasterWidth` or `rasterHeight`.
- **Missing Context:** Attempting to call the operation without an active `SparkContext` or on an uninitialized `RasterRDD`.

### Fix Code Hint
```scala
// Ensure the type T matches the input raster type (e.g., Int or Float)
val rescaled = RasterOperationsFocal.rescale[Int](raster, 360, 180, true, InterpolationMethod.Bilinear)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.reshapeAverage`

### Signature
```scala
[type T](raster:RasterRDD[T],targetMetadataConv:_root_.scala.Function1[RasterMetadata, RasterMetadata],_numPartitions:Int)(evidence$1:ClassTag[T]):RasterRDD[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:58_

### Goal
Performs a spatial reshape of a `RasterRDD` by mapping source pixels to a new grid and computing the average value for each target pixel, suitable for continuous numerical data.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset to be reshaped.
- `targetMetadataConv` (`_root_.scala.Function1[RasterMetadata, RasterMetadata]`): A function that defines the transformation logic to produce the target `RasterMetadata` (e.g., new resolution, CRS, or extent).
- `_numPartitions` (`Int`): The number of partitions to use for the resulting `RasterRDD`.
- `evidence$1` (`ClassTag[T]`): An implicit `ClassTag` required by Spark to handle the generic type `T` at runtime.

### Input
- **Data:** `RasterRDD[T]` containing numerical, continuous data (e.g., temperature, vegetation indices).
- **Formats:** Supports GeoTIFF and HDF4 inputs.
- **Preconditions:** The input data must be numerical. This method is specifically designed for downsampling or reprojecting where multiple source pixels map to a single target pixel. Empty pixels are automatically ignored during the averaging process.

### Output
Returns `RasterRDD[T]` — A new raster dataset aligned to the metadata defined by `targetMetadataConv`, containing averaged pixel values.

### Valid Call Patterns
```scala
// Inferred from signature: Reshaping a RasterRDD[Float]
val reshapedRaster = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.reshapeAverage(
  raster, 
  (meta: RasterMetadata) => meta.setResolution(newResX, newResY), 
  100
)
```

### LLM Instruction Prompt
- Use `reshapeAverage` only when the raster data is continuous and numerical (e.g., `Float` or `Double`).
- Always provide a valid `RasterMetadata` transformation function.
- Ensure the `_numPartitions` is set appropriately for the cluster size to avoid data skew.
- Do not use this for categorical data; use `reshapeNN` instead.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.RasterOperationsFocal
// ...
val result = RasterOperationsFocal.reshapeAverage(inputRaster, metaFunc, numPartitions)
```

### Common Failure Modes
- **Type Mismatch:** Passing categorical data (e.g., land cover classes) will result in mathematically meaningless averages.
- **Empty Output:** If the `targetMetadataConv` results in an extent that does not overlap with the source raster, the resulting RDD will be empty.
- **Performance:** Setting `_numPartitions` too low for large datasets can lead to `OutOfMemoryError` on individual executors.

### Fix Code Hint
```scala
// Ensure the input is numerical and the partition count is tuned to the cluster
val reshaped = RasterOperationsFocal.reshapeAverage(raster, m => m, sc.defaultParallelism)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.reshapeBilinear`

### Signature
```scala
[type T](raster:RasterRDD[T],targetMetadataConv:_root_.scala.Function1[RasterMetadata, RasterMetadata],numPartitions:Int)(evidence$2:ClassTag[T]):RasterRDD[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:374_

### Goal
Performs a bilinear interpolation to resample or reshape a `RasterRDD` into a new grid defined by the provided metadata transformation function.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset to be resampled.
- `targetMetadataConv` (`_root_.scala.Function1[RasterMetadata, RasterMetadata]`): A function that takes the original `RasterMetadata` and returns the new `RasterMetadata` defining the target grid dimensions, resolution, and extent.
- `numPartitions` (`Int`): The number of partitions to use for the resulting `RasterRDD`.
- `evidence$2` (`ClassTag[T]`): Implicit class tag required by Spark to handle the generic pixel type `T`.

### Input
- **Data:** `RasterRDD[T]` (e.g., GeoTIFF or HDF4 data loaded into Beast).
- **Preconditions:** The input raster must be properly initialized as a `RasterRDD`. The `targetMetadataConv` function must return valid `RasterMetadata` that is compatible with the desired output grid.

### Output
Returns `RasterRDD[T]` — A new raster dataset resampled to the target grid using bilinear interpolation.

### Valid Call Patterns
```scala
// Example inferred from signature:
val newRaster = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.reshapeBilinear(
  inputRaster, 
  (meta: RasterMetadata) => meta.setResolution(newX, newY), 
  100
)
```

### LLM Instruction Prompt
- Use `reshapeBilinear` when you need to change the resolution or extent of a raster while maintaining smooth pixel transitions via bilinear interpolation.
- Always provide a valid `RasterMetadata` transformation function.
- Ensure the `numPartitions` is tuned to the size of the input data to maintain Spark performance.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.RasterOperationsFocal
// ...
val resampled = RasterOperationsFocal.reshapeBilinear(raster, meta => meta.setResolution(0.1, 0.1), 50)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing a `targetMetadataConv` that results in invalid or empty extents will cause the operation to fail or produce an empty RDD.
- **Type Mismatch:** The generic type `T` must be consistent with the pixel data (e.g., `Float`, `Int`).
- **Partitioning:** Setting `numPartitions` too low for large datasets can lead to `OutOfMemoryError` during the interpolation process.

### Fix Code Hint
```scala
// Ensure the target metadata is valid before passing to the function
val targetMeta = inputRaster.metadata.setResolution(newResX, newResY)
val result = RasterOperationsFocal.reshapeBilinear(inputRaster, _ => targetMeta, numPartitions)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.reshapeNN`

### Signature
```scala
[type T](raster:RasterRDD[T],targetMetadataConv:_root_.scala.Function1[RasterMetadata, RasterMetadata],numPartitions:Int)(evidence$3:ClassTag[T]):RasterRDD[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:408_

### Goal
Performs a nearest-neighbor resampling of a `RasterRDD` to a new spatial configuration defined by a metadata transformation function.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset to be reshaped.
- `targetMetadataConv` (`_root_.scala.Function1[RasterMetadata, RasterMetadata]`): A function that takes the original `RasterMetadata` and returns the new `RasterMetadata` (defining target CRS, resolution, extent, and tile size).
- `numPartitions` (`Int`): The number of partitions for the resulting `RasterRDD`.
- `evidence$3` (`ClassTag[T]`): Implicit class tag required by Spark to handle the generic type `T` at runtime.

### Input
- **Data:** `RasterRDD[T]` (e.g., loaded via `sc.geoTiff[T]`).
- **Preconditions:** The input raster must be compatible with the pixel type `T` (e.g., `Int`, `Float`). The `targetMetadataConv` must provide valid geospatial parameters for the output grid.

### Output
Returns `RasterRDD[T]` — A new raster dataset aligned to the target metadata, where each pixel value is derived from the nearest neighbor in the source raster.

### Valid Call Patterns
```scala
val raster: RasterRDD[Int] = sc.geoTiff[Int]("glc2000_v1_1.tif")
val reshaped = RasterOperationsFocal.reshapeNN(raster,
  (m: RasterMetadata) => RasterMetadata.create(-124, 42, -114, 32, 4326, 1000, 1000, 100, 100),
  10)
```

### LLM Instruction Prompt
- When calling `reshapeNN`, ensure the `targetMetadataConv` function correctly maps the input `RasterMetadata` to the desired output dimensions and CRS.
- Always provide an explicit `numPartitions` value to optimize Spark execution.
- Ensure the generic type `T` matches the pixel type of the input `RasterRDD`.

### Prompt Snippet
```text
RasterOperationsFocal.reshapeNN(raster, metadataFunc, partitions)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `RasterRDD` with a pixel type that does not match the expected `ClassTag[T]`.
- **Invalid Metadata:** Providing a `targetMetadataConv` that results in an invalid or empty spatial extent, leading to empty output tiles.
- **Resource Exhaustion:** Setting `numPartitions` too high or too low for the cluster size, causing performance degradation or OOM errors.

### Fix Code Hint
```scala
// Ensure the metadata transformation is valid for the target CRS
val reshaped = RasterOperationsFocal.reshapeNN(inputRaster, 
  m => m.copy(width = 500, height = 500), 
  sc.defaultParallelism)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.slidingWindow`

### Signature
```scala
[type T,type U](raster:RasterRDD[T],w:Int,f:_root_.scala.Function2[Array[T], Array[Boolean], U])(evidence$12:ClassTag[T],evidence$13:ClassTag[U]):RasterRDD[U]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:711_

### Goal
Performs a focal operation on a `RasterRDD` by applying a user-defined function to a sliding window of pixels centered at each location.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset to process.
- `w` (`Int`): The size of the sliding window (e.g., a value of 3 implies a 3x3 window).
- `f` (`_root_.scala.Function2[Array[T], Array[Boolean], U]`): A function that takes an array of pixel values and an array of booleans (indicating valid pixels within the window) and returns a single value of type `U`.
- `evidence$12` (`ClassTag[T]`): Implicit class tag for the input pixel type.
- `evidence$13` (`ClassTag[U]`): Implicit class tag for the output pixel type.

### Input
The input must be a `RasterRDD[T]` (e.g., loaded from GeoTIFF or HDF4). The operation assumes the raster is already partitioned appropriately for Spark processing. Ensure that the pixel type `T` matches the data source (e.g., `Float`, `Int`).

### Output
Returns `RasterRDD[U]` — A new raster containing the results of the focal function applied to each window, maintaining the spatial metadata of the input raster.

### Valid Call Patterns
```scala
// Example inferred from signature:
val result: RasterRDD[Float] = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.slidingWindow(
  inputRaster, 
  3, 
  (pixels: Array[Float], valid: Array[Boolean]) => {
    // Custom focal logic here
    pixels.sum / pixels.length
  }
)
```

### LLM Instruction Prompt
- When using `slidingWindow`, ensure the function `f` handles the `Array[Boolean]` mask to account for edge cases or NoData values within the window.
- Always provide explicit type parameters `[T, U]` if the compiler cannot infer them from the function `f`.
- Ensure the `RasterRDD` is properly initialized and that the pixel type `T` is consistent with the source data.

### Prompt Snippet
```text
// Use this pattern for focal operations:
edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.slidingWindow[InputType, OutputType](raster, windowSize, (pixels, mask) => { ... })
```

### Common Failure Modes
- **Type Mismatch:** Providing a function `f` that returns a type incompatible with the expected `RasterRDD[U]`.
- **Memory Overhead:** Using a very large window size `w` can lead to significant memory pressure on Spark executors due to the size of the sliding window arrays.
- **Metadata Inconsistency:** If the input `RasterRDD` is not properly partitioned or aligned, focal operations may produce unexpected results at tile boundaries.

### Fix Code Hint
```scala
// Ensure the function handles the mask to avoid processing invalid pixels
val result = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.slidingWindow[Float, Float](
  myRaster, 
  5, 
  (pixels, mask) => {
    val validPixels = pixels.zip(mask).filter(_._2).map(_._1)
    if (validPixels.isEmpty) 0.0f else validPixels.sum / validPixels.length
  }
)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.slidingWindow2`

### Signature
```scala
[type T,type U](raster:RasterRDD[T],w:Int,winFunc:_root_.scala.Function2[Array[T], Array[Boolean], U])(evidence$8:ClassTag[T],evidence$9:ClassTag[U]):RasterRDD[U]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsFocal.scala:653_

### Goal
Performs a focal (neighborhood) operation on a `RasterRDD` by applying a user-defined function to a sliding window of size `w` centered on each pixel.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset to process.
- `w` (`Int`): The size of the square sliding window (e.g., 3 for a 3x3 neighborhood).
- `winFunc` (`_root_.scala.Function2[Array[T], Array[Boolean], U]`): A function that takes an array of pixel values and an array of booleans (indicating valid pixels within the window) and returns a single value of type `U`.
- `evidence$8` (`ClassTag[T]`): Implicit class tag for the input pixel type `T`.
- `evidence$9` (`ClassTag[U]`): Implicit class tag for the output pixel type `U`.

### Input
- **Data:** A `RasterRDD[T]` loaded from supported formats like GeoTIFF or HDF4.
- **Preconditions:** The raster must be properly partitioned for Spark processing. As a focal operation, this function handles the neighborhood context, but users should ensure the input `RasterRDD` is correctly aligned if performing multi-raster operations prior to this call.

### Output
Returns `RasterRDD[U]` — A new raster containing the results of the focal function applied to each pixel neighborhood, where each pixel value is of type `U`.

### Valid Call Patterns
```scala
// Inferred call pattern based on signature
val result: RasterRDD[Double] = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.slidingWindow2(
  inputRaster, 
  3, 
  (pixels: Array[Float], valid: Array[Boolean]) => pixels.sum.toDouble / pixels.length
)
```

### LLM Instruction Prompt
- When generating code for this API, always provide the `ClassTag` evidence implicitly or explicitly.
- Ensure the `winFunc` correctly handles the `Array[Boolean]` mask to avoid processing invalid or nodata pixels within the window.
- The window size `w` must be an integer representing the side length of the square window.

### Prompt Snippet
```text
// Example of focal mean calculation
val focalMean = edu.ucr.cs.bdlab.raptor.RasterOperationsFocal.slidingWindow2(raster, 3, (vals, mask) => {
  val validVals = vals.zip(mask).filter(_._2).map(_._1)
  validVals.sum / validVals.length
})
```

### Common Failure Modes
- **Type Mismatch:** Providing a `winFunc` that returns a type `U` incompatible with the expected `RasterRDD[U]` output.
- **Memory Overhead:** Large window sizes (`w`) can significantly increase the computational cost per pixel and memory usage during the shuffle phase of the Spark operation.
- **Empty Windows:** If the `winFunc` does not check the `Array[Boolean]` mask, it may attempt to perform calculations on nodata values, leading to incorrect results or runtime exceptions.

### Fix Code Hint
```scala
// Ensure the function handles empty neighborhoods if the window falls outside the raster bounds
(vals: Array[T], mask: Array[Boolean]) => {
  val valid = vals.zip(mask).collect { case (v, true) => v }
  if (valid.isEmpty) defaultValue else compute(valid)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsGlobal.flatten`

### Signature
```scala
[type T](raster:RasterRDD[T]):RDD[scala.Tuple4[Int, Int, RasterMetadata, T]]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsGlobal.scala:69_

### Goal
Extracts all non-empty pixels from a `RasterRDD` into a flat, non-raster RDD to facilitate global statistical analysis and data inspection.

### Parameters
- `raster` (`RasterRDD[T]`): The input raster dataset to be flattened, where `T` represents the pixel value type (e.g., `Int`, `Float`).

### Input
- **Data Formats:** Supports raster data loaded via Beast, such as GeoTIFF or HDF4.
- **Preconditions:** The `RasterRDD[T]` must be initialized. The type parameter `T` must match the pixel data type of the source raster (e.g., `sc.geoTiff[Int]` requires `RasterRDD[Int]`).

### Output
Returns `RDD[scala.Tuple4[Int, Int, RasterMetadata, T]]` — An RDD where each tuple contains:
- `_1` (Int): The pixel x-coordinate (column index).
- `_2` (Int): The pixel y-coordinate (row index).
- `_3` (RasterMetadata): Metadata required to map pixel coordinates to geographical coordinates.
- `_4` (T): The measure value of the pixel.

### Valid Call Patterns
```scala
val raster: RasterRDD[Int] = sc.geoTiff[Int]("glc2000_v1_1.tif")
val flattenedRDD = raster.flatten
```

### LLM Instruction Prompt
- When calling `flatten`, ensure the input is a `RasterRDD[T]`.
- Use the returned RDD to perform global operations like `countByValue()` or `map()` to extract specific pixel values (e.g., `flatten.map(_._4)`).
- Do not attempt to use this on non-raster RDDs.

### Prompt Snippet
```text
// Flatten a RasterRDD to compute global statistics
val flattened = raster.flatten
val stats = flattened.map(_._4).countByValue()
```

### Common Failure Modes
- **Type Mismatch:** Providing a `RasterRDD` with a type `T` that does not match the underlying pixel data (e.g., using `Float` for an `Int` raster) will cause runtime errors during processing.
- **Memory Overhead:** Flattening very large rasters into a single RDD can lead to significant memory pressure on the Spark cluster; consider filtering or aggregating data early if possible.

### Fix Code Hint
```scala
// Ensure the type parameter matches the source raster type
val raster: RasterRDD[Float] = sc.geoTiff[Float]("data.tif")
val flattened = raster.flatten // Correct: T is Float
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsGlobal.rasterizePixels`

### Signature
```scala
[type T](pixels:RDD[scala.Tuple3[Int, Int, T]],metadata:RasterMetadata,rasterFeature:RasterFeature)(evidence$1:ClassTag[T]):RasterRDD[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsGlobal.scala:35_

### Goal
Converts a distributed collection of pixel coordinates and values into a `RasterRDD[T]` based on provided spatial metadata and feature definitions.

### Parameters
- `pixels` (`RDD[scala.Tuple3[Int, Int, T]]`): An RDD containing tuples of `(column, row, value)`, where `(column, row)` represents the discrete pixel location within the raster grid.
- `metadata` (`RasterMetadata`): The spatial metadata defining the geographic extent, coordinate reference system (SRID), and resolution (raster and tile dimensions) of the resulting raster.
- `rasterFeature` (`RasterFeature`): The feature definition associated with the raster dataset.
- `evidence$1` (`ClassTag[T]`): An implicit `ClassTag` required by Spark to handle the generic type `T` at runtime.

### Input
- **Data:** An RDD of pixel values. The `(x, y)` coordinates must be relative to the grid defined in `metadata`.
- **Preconditions:** The `metadata` must accurately reflect the intended geographic bounds and resolution. The type `T` must be compatible with the expected raster data type (e.g., `Int`, `Float`).

### Output
Returns `RasterRDD[T]` — A distributed raster dataset partitioned into tiles as specified by the `metadata`.

### Valid Call Patterns
```scala
// Example of creating a RasterRDD from pixel data
val raster = edu.ucr.cs.bdlab.raptor.RasterOperationsGlobal.rasterizePixels(pixels, metadata, rasterFeature)
```

### LLM Instruction Prompt
- When calling `rasterizePixels`, ensure the `pixels` RDD contains tuples where the first two elements are `Int` representing the column and row indices.
- Always provide an implicit `ClassTag` for the type `T` if not automatically inferred by the Scala compiler.
- Ensure the `metadata` object is correctly initialized with the appropriate `rasterWidth`, `rasterHeight`, `tileWidth`, and `tileHeight` to match the intended output structure.

### Prompt Snippet
```text
val raster = edu.ucr.cs.bdlab.raptor.RasterOperationsGlobal.rasterizePixels(pixels, metadata, rasterFeature)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `metadata` that does not align with the coordinate range of the `pixels` RDD, leading to empty or incorrectly mapped rasters.
- **Type Mismatch:** Using a type `T` that is not supported by the underlying raster storage format (e.g., attempting to store complex objects where only primitives are supported).
- **Missing Implicit:** Failure to provide the `ClassTag` when the compiler cannot infer the type `T` from the `pixels` RDD.

### Fix Code Hint
```scala
import scala.reflect.ClassTag
// Ensure the ClassTag is available for the generic type T
implicit val tag: ClassTag[T] = ClassTag(classOf[T])
val raster = edu.ucr.cs.bdlab.raptor.RasterOperationsGlobal.rasterizePixels(pixels, metadata, rasterFeature)(tag)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsGlobal.rasterizePoints`

### Signature
```scala
[type T](points:RDD[scala.Tuple3[Double, Double, T]],metadata:RasterMetadata,rasterFeature:RasterFeature)(evidence$2:ClassTag[T]):RasterRDD[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsGlobal.scala:55_

### Goal
Converts a distributed collection of geographic points with associated values into a `RasterRDD[T]` by mapping coordinates to the corresponding pixel grid defined by the provided metadata.

### Parameters
- `points` (`RDD[scala.Tuple3[Double, Double, T]]`): An RDD of tuples where each tuple contains the (longitude, latitude) geographic coordinates and the value `T` to be assigned to the pixel covering that location.
- `metadata` (`RasterMetadata`): The spatial definition of the target raster, including bounds (x1, y1, x2, y2), SRID, and resolution (raster/tile dimensions).
- `rasterFeature` (`RasterFeature`): The feature definition representing the raster structure to be populated.
- `evidence$2` (`ClassTag[T]`): A Scala `ClassTag` required by the Spark runtime to handle the generic type `T` during serialization and distribution.

### Input
- **Data:** Requires an `RDD` of geographic points.
- **Preconditions:** The `RasterMetadata` must accurately reflect the geographic extent and resolution of the target output.
- **Compatibility:** The type `T` must be consistent with the expected pixel data type (e.g., `Float`, `Int`).

### Output
Returns `RasterRDD[T]` — A distributed raster dataset where the input points have been aggregated or assigned to their respective pixel locations based on the provided metadata.

### Valid Call Patterns
```scala
// Example usage based on project documentation
val metadata = RasterMetadata.create(x1 = -50, y1 = 40, x2 = -60, y2 = 30, srid = 4326,
  rasterWidth = 10, rasterHeight = 10, tileWidth = 10, tileHeight = 10)
val points = sc.parallelize(Seq(
  (-51.3, 30.4, 100),
  (-55.2, 34.5, 200),
  (-56.4, 39.2, 300)
))
// Note: rasterFeature is required by the signature; ensure it is initialized per project requirements
val raster = edu.ucr.cs.bdlab.raptor.RasterOperationsGlobal.rasterizePoints(points, metadata, rasterFeature)
```

### LLM Instruction Prompt
- When calling `rasterizePoints`, ensure the `points` RDD contains geographic coordinates (longitude, latitude) rather than pixel indices.
- Always provide a `ClassTag[T]` (usually handled implicitly by the compiler if the method is called within a context where `T` is known).
- Ensure the `RasterMetadata` matches the intended geographic area; points falling outside the metadata bounds may be ignored or cause unexpected behavior.

### Prompt Snippet
```text
val raster = edu.ucr.cs.bdlab.raptor.RasterOperationsGlobal.rasterizePoints(pointsRDD, metadata, feature)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing metadata that does not cover the geographic range of the input points, leading to empty or incomplete rasters.
- **Type Mismatch:** Using a type `T` that is incompatible with the downstream raster processing operations (e.g., attempting to perform arithmetic on non-numeric types).
- **Missing Implicit:** Failure to provide the `ClassTag` if the compiler cannot infer it, resulting in a compilation error.

### Fix Code Hint
```scala
// Ensure the type T is explicitly defined or inferable
val raster: RasterRDD[Float] = edu.ucr.cs.bdlab.raptor.RasterOperationsGlobal.rasterizePoints[Float](points, metadata, rasterFeature)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsLocal.explode`

### Signature
```scala
[type T](inputRaster:RasterRDD[T]):RasterRDD[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsLocal.scala:76_

### Goal
Separates individual tiles within a `RasterRDD` into distinct records to facilitate writing each tile as an independent file.

### Parameters
- `inputRaster` (`RasterRDD[T]`): The source raster dataset containing tiled data that needs to be exploded into individual tile records.

### Input
- **Data:** A `RasterRDD[T]` object.
- **Preconditions:** The raster should typically be retiled (e.g., using `.retile(width, height)`) prior to calling `explode` if specific tile dimensions are required for downstream processing or file output.
- **Type Selection:** The type parameter `T` must match the pixel data type (e.g., `Int`, `Float`) of the underlying raster data.

### Output
Returns `RasterRDD[T]` — A new `RasterRDD` where each tile is treated as an individual record, enabling distributed write operations where each tile can be saved to a separate file.

### Valid Call Patterns
```scala
val raster: RasterRDD[Int] = sc.geoTiff[Int]("glc2000_v1_1.tif")
val retiled = raster.retile(64, 64).explode
```

### LLM Instruction Prompt
- When generating code for `explode`, ensure it is chained after a `retile` operation if specific tile sizes are required.
- Always recommend using the `distributed` write mode when saving the result of an `explode` operation to handle the large number of generated files.

### Prompt Snippet
```text
// Explode a RasterRDD to prepare for individual tile file output
val explodedRaster = inputRaster.retile(tileWidth, tileHeight).explode
explodedRaster.saveAsGeoTiff("output_path", GeoTiffWriter.WriteMode -> "distributed")
```

### Common Failure Modes
- **Output Directory Collision:** Operations will fail if the target output directory already exists; use `-overwrite` or ensure the directory is empty.
- **Performance Bottlenecks:** Attempting to save exploded rasters without using the `distributed` write mode can lead to significant performance degradation or memory issues due to the high volume of small files.
- **Type Mismatch:** Using an incorrect type parameter `T` (e.g., `RasterRDD[Float]` for an `Int` GeoTIFF) will cause runtime errors during data access.

### Fix Code Hint
```scala
// Ensure distributed write mode is used for exploded rasters
retiledRaster.saveAsGeoTiff("output_dir", GeoTiffWriter.WriteMode -> "distributed")
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsLocal.filterPixels`

### Signature
```scala
[type T](inputRaster:RasterRDD[T],filter:_root_.scala.Function1[T, Boolean])(evidence$3:ClassTag[T]):RasterRDD[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsLocal.scala:48_

### Goal
Filters a `RasterRDD` by removing pixels that do not satisfy a provided boolean predicate function.

### Parameters
- `inputRaster` (`RasterRDD[T]`): The source raster dataset to be processed.
- `filter` (`_root_.scala.Function1[T, Boolean]`): A predicate function applied to each pixel value of type `T`; pixels for which the function returns `false` are removed.
- `evidence$3` (`ClassTag[T]`): A `ClassTag` required by the Scala compiler to handle the generic type `T` at runtime.

### Input
- **Data:** A `RasterRDD[T]` loaded from supported formats such as GeoTIFF or HDF4.
- **Preconditions:** The raster must be properly initialized as a `RasterRDD`. The type `T` must match the underlying pixel data type (e.g., `Float`, `Int`).

### Output
Returns `RasterRDD[T]` — A new `RasterRDD` containing only the pixels that satisfied the filter condition.

### Valid Call Patterns
```scala
// Example: Keep only pixels with temperature greater than 300K
val temperatureK: RasterRDD[Float] = sc.hdfFile("MOD11A1.hdf", "LST_Day_1km")
val filteredRaster = edu.ucr.cs.bdlab.raptor.RasterOperationsLocal.filterPixels(temperatureK, _ > 300)
```

### LLM Instruction Prompt
- When calling `filterPixels`, ensure the predicate function matches the pixel type `T` of the `RasterRDD`.
- The `evidence$3` parameter is typically handled implicitly by the Scala compiler; if calling from Java or if type inference fails, ensure a `ClassTag` is provided.
- Always chain this operation with an output method like `saveAsGeoTiff` to persist the results.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.RasterOperationsLocal
// ...
val result = RasterOperationsLocal.filterPixels(myRaster, (pixel: Float) => pixel > threshold)
```

### Common Failure Modes
- **Type Mismatch:** Providing a filter function that expects a different type than the `RasterRDD` pixel type (e.g., passing a `Double` predicate to a `Float` raster).
- **Missing ClassTag:** If the compiler cannot infer the `ClassTag`, the code will fail to compile; ensure the context has appropriate implicit scope.
- **Output Overwrite:** Attempting to save the resulting `RasterRDD` to a directory that already exists without using the `-overwrite` flag.

### Fix Code Hint
```scala
// Ensure the predicate matches the RasterRDD type parameter
val filtered = edu.ucr.cs.bdlab.raptor.RasterOperationsLocal.filterPixels[Float](inputRaster, (p: Float) => p > 0.0f)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsLocal.mapPixels`

### Signature
```scala
[type T,type U](inputRaster:RasterRDD[T],f:_root_.scala.Function1[T, U])(evidence$1:ClassTag[T],evidence$2:ClassTag[U]):RasterRDD[U]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsLocal.scala:38_

### Goal
Applies a user-defined mathematical or transformation function to every individual pixel value within a `RasterRDD` to produce a new `RasterRDD` with identical spatial dimensions and metadata.

### Parameters
- `inputRaster` (`RasterRDD[T]`): The source raster dataset to be transformed.
- `f` (`_root_.scala.Function1[T, U]`): A transformation function that maps a single pixel value of type `T` to a new value of type `U`.
- `evidence$1` (`ClassTag[T]`): Implicit class tag for the input pixel type `T`, required by the Spark runtime.
- `evidence$2` (`ClassTag[U]`): Implicit class tag for the output pixel type `U`, required by the Spark runtime.

### Input
- **Data:** A `RasterRDD[T]` loaded from supported formats such as GeoTIFF or HDF4.
- **Preconditions:** The input `RasterRDD` must be initialized via `SparkContext` raster loaders (e.g., `sc.geoTiff` or `sc.hdfFile`). The pixel type `T` must match the underlying data type of the source file (e.g., `Float`, `Int`).

### Output
Returns `RasterRDD[U]` — A new raster dataset containing the transformed pixel values, preserving the original spatial resolution, CRS, and tiling structure of the input.

### Valid Call Patterns
```scala
val temperatureK: RasterRDD[Float] = sc.hdfFile("MOD11A1.hdf", "LST_Day_1km")
val temperatureF: RasterRDD[Float] = temperatureK.mapPixels(k => (k - 273.15f) * 9 / 5 + 32)
```

### LLM Instruction Prompt
- When calling `mapPixels`, ensure the input `RasterRDD` type parameter `T` matches the actual data type of the raster (e.g., `Float` for temperature data).
- The function `f` must be a pure function that operates on a single pixel value.
- The `ClassTag` evidence is typically handled automatically by the Scala compiler; do not attempt to pass these manually unless the compiler fails to infer them.

### Prompt Snippet
```text
// Example of mapping pixel values in a RasterRDD
val outputRaster = inputRaster.mapPixels(pixel => transformFunction(pixel))
```

### Common Failure Modes
- **Type Mismatch:** Providing a function `f` that returns a type incompatible with the expected output `RasterRDD` type.
- **Incorrect Pixel Type:** Using `Int` when the source raster data is stored as `Float`, leading to precision loss or runtime errors.
- **Missing Context:** Attempting to call this on a standard `RDD` instead of a `RasterRDD`.
- **Serialization:** Using a non-serializable function `f` inside the `mapPixels` call, which will cause Spark task serialization failures.

### Fix Code Hint
```scala
// Ensure the input type matches the source data (e.g., Float)
val result: RasterRDD[Float] = input.mapPixels((p: Float) => p * 2.0f)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterOperationsLocal.overlay`

### Signature
```scala
[type T,type V](inputs:_root_.scala.<repeated>[RDD[ITile[T]]])(evidence$4:ClassTag[T],v:ClassTag[V]):RasterRDD[Array[V]]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterOperationsLocal.scala:59_

### Goal
Stacks multiple input rasters into a single `RasterRDD` where each pixel contains an array of values corresponding to the input layers.

### Parameters
- `inputs` (`_root_.scala.<repeated>[RDD[ITile[T]]]`): A variable number of raster RDDs to be stacked.
- `evidence$4` (`ClassTag[T]`): Implicit class tag for the input pixel type `T`.
- `v` (`ClassTag[V]`): Implicit class tag for the output pixel type `V`.

### Input
- **Data/Formats:** Accepts `RasterRDD` or `RDD[ITile[T]]` objects (e.g., loaded via `sc.geoTiff`).
- **Preconditions:** All input rasters must share identical metadata, specifically resolution, Coordinate Reference System (CRS), and tile size. If metadata differs, you must use the `reshape` operation to align the rasters before calling `overlay`.

### Output
Returns `RasterRDD[Array[V]]` — A new raster where each pixel is an `Array[V]` containing the values from each input raster at that spatial location.

### Valid Call Patterns
```scala
val raster1: RasterRDD[Int] = sc.geoTiff[Int]("glc2000_v1_1.tif")
val raster2: RasterRDD[Int] = sc.geoTiff[Int]("vegetation")
val stacked: RasterRDD[Array[Int]] = RasterOperationsLocal.overlay(raster1, raster2)
```

### LLM Instruction Prompt
- When calling `overlay`, ensure all input rasters are pre-aligned using `reshape` if their metadata (CRS, resolution, tile size) does not match.
- The output type `V` must be compatible with the input type `T`.
- Always provide the `ClassTag` implicitly or explicitly if the compiler cannot infer them.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.RasterOperationsLocal
// Ensure rasters are aligned before overlay
val alignedRaster2 = raster2.reshape(raster1)
val result = RasterOperationsLocal.overlay(raster1, alignedRaster2)
```

### Common Failure Modes
- **Metadata Mismatch:** Attempting to overlay rasters with different resolutions or CRS will result in runtime errors or invalid spatial alignment.
- **Missing Reshape:** Failing to call `reshape` when input rasters have different grid definitions.
- **Type Mismatch:** Providing incompatible types for `T` and `V` that cannot be handled by the `ClassTag`.

### Fix Code Hint
```scala
// If rasters have different metadata, align them first:
val raster2Aligned = raster2.reshape(raster1)
val stacked = RasterOperationsLocal.overlay(raster1, raster2Aligned)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterPartitioner`

### Signature
```scala
[](rasterMetadata:RasterMetadata,numPartitions:Int):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterPartitioner.scala:12_

### Goal
Provides a partitioning strategy for raster data to enable balanced distribution across a Spark cluster for concurrent raster-vector processing (Raptor).

### Parameters
- `rasterMetadata` (`RasterMetadata`): The metadata object describing the raster's spatial extent, resolution, and coordinate reference system.
- `numPartitions` (`Int`): The desired number of partitions to divide the raster data into for parallel processing.

### Input
- **Data:** Requires a valid `RasterMetadata` instance derived from the source raster (e.g., GeoTIFF or HDF4).
- **Preconditions:** The raster metadata must be correctly initialized. Ensure the `numPartitions` value is appropriate for the cluster size and data volume to avoid excessive overhead or data skew.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it represents a partitioner object used to distribute raster tiles across Spark executors.

### Valid Call Patterns
```scala
// Inferred from signature:
val partitioner = new edu.ucr.cs.bdlab.raptor.RasterPartitioner(rasterMetadata, numPartitions)
```

### LLM Instruction Prompt
- When using `RasterPartitioner`, ensure the `RasterMetadata` is obtained from the source raster file being processed.
- Use this partitioner to align raster data distribution with the spatial partitioning of vector datasets to optimize `raptorJoin` performance.

### Prompt Snippet
```text
val metadata = rasterRDD.metadata
val partitioner = new edu.ucr.cs.bdlab.raptor.RasterPartitioner(metadata, numPartitions)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing metadata that does not match the actual raster data dimensions or CRS can lead to incorrect spatial indexing or join results.
- **Partition Count:** Setting `numPartitions` to an excessively high value relative to the data size may lead to performance degradation due to task scheduling overhead.
- **Initialization:** Attempting to use the partitioner before the `RasterMetadata` has been fully parsed from the source file.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the source raster
val metadata = myRasterRDD.metadata
val numPartitions = 100 // Adjust based on cluster resources
val partitioner = new edu.ucr.cs.bdlab.raptor.RasterPartitioner(metadata, numPartitions)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.RasterPartitioner.getPartition`

### Signature
```scala
[](tile:Any):Int
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/RasterPartitioner.scala:43_

### Goal
Determines the partition index for a given raster tile to facilitate distributed processing and load balancing within the Raptor raster-vector join framework.

### Parameters
- `tile` (`Any`): The raster tile object to be assigned to a partition.

### Input
- **Data:** Requires a valid raster tile object compatible with the Beast Raptor framework.
- **Preconditions:** The `RasterPartitioner` must be initialized and configured within the context of a Spark job. The input `tile` should correspond to the data type expected by the specific partitioner instance.

### Output
Returns `Int` — The index of the partition to which the provided tile is assigned.

### Valid Call Patterns
```scala
// Inferred from signature:
val partitioner = new edu.ucr.cs.bdlab.raptor.RasterPartitioner(...)
val partitionIndex = partitioner.getPartition(tile)
```

### LLM Instruction Prompt
- When using `getPartition`, ensure the `tile` object is of the type expected by the `RasterPartitioner` instance.
- This method is typically used internally by Spark's partitioning logic; ensure the partitioner is correctly broadcast or available on the executor nodes if called within a transformation.

### Prompt Snippet
```text
val partitioner = new edu.ucr.cs.bdlab.raptor.RasterPartitioner(...)
val index = partitioner.getPartition(myTile)
```

### Common Failure Modes
- **Type Mismatch:** Passing an object that is not a valid raster tile type, resulting in a runtime exception.
- **Uninitialized Partitioner:** Calling `getPartition` on a partitioner instance that has not been properly configured with metadata (e.g., grid dimensions or spatial extent).
- **Null Input:** Passing a `null` tile, which may trigger a `NullPointerException` depending on the implementation.

### Fix Code Hint
```scala
// Ensure the tile is valid and the partitioner is initialized
if (tile != null) {
  val index = partitioner.getPartition(tile)
  // Proceed with processing
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ReshapeTile`

### Signature
```scala
[](tileID:Int,targetMetadata:RasterMetadata,rasterFeature:RasterFeature)(t:ClassTag[T]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ReshapeTile.scala:19_

### Goal
Reshapes a specific raster tile to match a target coordinate system, resolution, and grid alignment defined by the provided metadata.

### Parameters
- `tileID` (`Int`): The unique identifier of the raster tile to be processed.
- `targetMetadata` (`RasterMetadata`): The target raster configuration (resolution, CRS, and tile dimensions) to which the input tile must be aligned.
- `rasterFeature` (`RasterFeature`): The source raster data feature containing the pixel values to be reshaped.
- `t` (`ClassTag[T]`): The class tag for the pixel data type (e.g., `Float`, `Int`), required for Spark's type-safe serialization.

### Input
- **Data:** Requires a `RasterFeature` object and a corresponding `RasterMetadata` object.
- **Preconditions:** The `targetMetadata` must define the desired spatial extent and resolution. This operation is typically used to prepare rasters for `overlay` operations, which require identical metadata across all input rasters.
- **Formats:** Supports standard raster formats loaded into Beast, such as GeoTIFF or HDF4.

### Output
Returns `unspecified` — The function returns a reshaped tile structure aligned to the `targetMetadata` grid.

### Valid Call Patterns
```scala
// Example inferred from signature:
val reshaped = new edu.ucr.cs.bdlab.raptor.ReshapeTile(tileID, targetMetadata, rasterFeature)(scala.reflect.classTag[Float])
```

### LLM Instruction Prompt
- When calling `ReshapeTile`, ensure the `targetMetadata` is derived from the master raster that defines the grid for your join or overlay operation.
- Always provide the correct `ClassTag[T]` matching the pixel type of the `rasterFeature` (e.g., `Float` for continuous data, `Int` for categorical).

### Prompt Snippet
```text
// Use ReshapeTile to align raster features before performing an overlay or join
val reshapedTile = new edu.ucr.cs.bdlab.raptor.ReshapeTile(id, meta, feature)(scala.reflect.classTag[Float])
```

### Common Failure Modes
- **Metadata Mismatch:** Attempting to perform an `overlay` operation without first using `ReshapeTile` to align rasters with different resolutions or CRS will result in runtime errors.
- **Type Mismatch:** Providing a `ClassTag` that does not match the underlying pixel data type of the `RasterFeature` will cause serialization or casting exceptions.
- **Missing Overwrite:** If saving the output of a reshaped raster to a directory that already exists, the operation will fail unless the `-overwrite` flag is used.

### Fix Code Hint
```scala
// Ensure pixel types match the ClassTag
val pixelType = scala.reflect.classTag[Float]
val reshaped = new edu.ucr.cs.bdlab.raptor.ReshapeTile(tileID, targetMeta, sourceFeature)(pixelType)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ReshapeTile.getTargetTilesForSourceTiles`

### Signature
```scala
[](sourceTileID:Int,targetMetadata:RasterMetadata,sourceMetadata:RasterMetadata):Array[Int]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ReshapeTile.scala:150_

### Goal
Determines the set of target raster tiles that overlap with a specific source raster tile during a raster reshaping or reprojection operation.

### Parameters
- `sourceTileID` (`Int`): The unique identifier of the tile in the source raster dataset.
- `targetMetadata` (`RasterMetadata`): The metadata defining the grid, resolution, and coordinate system of the destination raster.
- `sourceMetadata` (`RasterMetadata`): The metadata defining the grid, resolution, and coordinate system of the source raster.

### Input
The caller must provide valid `RasterMetadata` objects for both the source and target rasters. These metadata objects must accurately reflect the spatial extent, pixel dimensions, and CRS of the respective datasets. This function is typically used as a helper during raster-to-raster transformations where the source and target grids are not identical.

### Output
Returns `Array[Int]` — An array containing the IDs of all target tiles that intersect with the spatial footprint of the specified `sourceTileID`.

### Valid Call Patterns
```scala
// Example inferred from signature
val targetTileIDs = edu.ucr.cs.bdlab.raptor.ReshapeTile.getTargetTilesForSourceTiles(
  sourceTileID, 
  targetMetadata, 
  sourceMetadata
)
```

### LLM Instruction Prompt
- When using this function, ensure that `sourceMetadata` and `targetMetadata` are fully initialized with correct spatial bounds and tile dimensions.
- Use this function to map source tile indices to target tile indices when performing custom raster resampling or reprojection workflows.
- The returned `Array[Int]` represents the indices in the target raster that receive data from the specified source tile.

### Prompt Snippet
```text
val targetIndices = edu.ucr.cs.bdlab.raptor.ReshapeTile.getTargetTilesForSourceTiles(sourceTileID, targetMeta, sourceMeta)
```

### Common Failure Modes
- **Metadata Mismatch:** Providing `RasterMetadata` that does not match the actual data dimensions or CRS can lead to empty arrays or incorrect tile mapping.
- **Invalid Tile ID:** Passing a `sourceTileID` that does not exist within the `sourceMetadata` grid may result in unexpected behavior or empty output.
- **Coordinate System Incompatibility:** If the source and target rasters are in different coordinate systems without proper transformation parameters, the overlap calculation may be inaccurate.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the RasterRDD before calling
val sourceMeta = sourceRaster.metadata
val targetMeta = targetRaster.metadata
val affectedTiles = edu.ucr.cs.bdlab.raptor.ReshapeTile.getTargetTilesForSourceTiles(tileID, targetMeta, sourceMeta)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ReshapeTile.merge`

### Signature
```scala
[](other:ReshapeTile[T]):ReshapeTile[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ReshapeTile.scala:29_

### Goal
Merges two `ReshapeTile[T]` instances by combining their pixel data into a single tile structure.

### Parameters
- `other` (`ReshapeTile[T]`): The secondary `ReshapeTile` instance to be merged into the current tile.

### Input
- **Data:** Two `ReshapeTile[T]` objects containing raster pixel data.
- **Preconditions:** Both tiles must share compatible metadata, including identical resolution, coordinate reference systems (CRS), and tile dimensions. If tiles are not aligned, use `reshape` operations prior to merging to ensure spatial consistency.
- **Type Selection:** The type parameter `T` must be consistent across both tiles (e.g., `Float`, `Int`).

### Output
Returns `ReshapeTile[T]` — A new `ReshapeTile` containing the merged pixel values from the original and the provided `other` tile.

### Valid Call Patterns
```scala
// Inferred from signature:
val mergedTile = tileA.merge(tileB)
```

### LLM Instruction Prompt
- When calling `merge`, ensure the receiver and the `other` argument are both instances of `ReshapeTile` with the same generic type `T`.
- Verify that the spatial metadata (CRS, resolution, and bounds) of both tiles are compatible before invoking this method to avoid runtime inconsistencies.

### Prompt Snippet
```text
val tileA: ReshapeTile[Float] = ...
val tileB: ReshapeTile[Float] = ...
val result = tileA.merge(tileB)
```

### Common Failure Modes
- **Metadata Mismatch:** Attempting to merge tiles with different resolutions or CRS will result in invalid spatial data or runtime errors.
- **Type Mismatch:** Providing an `other` tile with a different generic type `T` (e.g., `ReshapeTile[Int]` vs `ReshapeTile[Float]`) will cause a compilation error.
- **Uninitialized Tiles:** Calling `merge` on a null or uninitialized `ReshapeTile` will throw a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure tiles are aligned before merging
val alignedTileB = tileB.reshape(tileA.metadata)
val merged = tileA.merge(alignedTileB)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ReshapeTile.resultTile`

### Signature
```scala
[]:ITile[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ReshapeTile.scala:35_

### Goal
Retrieves the final processed `ITile[T]` object after a reshaping operation has been performed within the `ReshapeTile` utility.

### Parameters
_None._

### Input
The caller must ensure that the `ReshapeTile` instance has been properly initialized and that any necessary reshaping or transformation logic has been executed prior to calling this method. As this is a member of `ReshapeTile`, the state of the object must contain a valid, computed tile.

### Output
Returns `ITile[T]` — The resulting raster tile of type `T` that has been reshaped or transformed according to the internal logic of the `ReshapeTile` instance.

### Valid Call Patterns
```scala
// Inferred from signature:
val reshaper = new edu.ucr.cs.bdlab.raptor.ReshapeTile(...)
val tile: ITile[T] = reshaper.resultTile()
```

### LLM Instruction Prompt
- When using `resultTile`, ensure the `ReshapeTile` instance is fully configured.
- This method does not accept arguments; it acts as a getter for the computed state of the reshaper.
- Ensure the generic type `T` matches the pixel data type of the underlying raster (e.g., `Float`, `Int`).

### Prompt Snippet
```text
val reshaper = new edu.ucr.cs.bdlab.raptor.ReshapeTile(...)
// ... perform operations ...
val result = reshaper.resultTile()
```

### Common Failure Modes
- **Uninitialized State:** Calling `resultTile()` before the reshaping logic has been applied or on an empty `ReshapeTile` instance may return an invalid or null tile.
- **Type Mismatch:** Attempting to assign the result to an `ITile[T]` where `T` does not match the actual pixel type of the processed raster will result in a runtime class cast exception.
- **Missing Context:** If the `ReshapeTile` was not provided with valid raster metadata (resolution, CRS), the returned tile may contain inconsistent or empty data.

### Fix Code Hint
```scala
// Ensure the ReshapeTile is fully prepared before retrieval
val reshaper = new edu.ucr.cs.bdlab.raptor.ReshapeTile(inputTile, targetMetadata)
val finalTile = reshaper.resultTile()
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.SlidingWindowTile`

### Signature
```scala
[](tileID:Int,metadata:RasterMetadata,rasterFeature:RasterFeature,w:Int,winFunc:_root_.scala.Function2[Array[T], Array[Boolean], U])(t:ClassTag[T],u:ClassTag[U]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/SlidingWindowTile.scala:8_

### Goal
Performs a sliding window operation over a raster tile, applying a user-defined function to a local neighborhood of pixels to compute a new value for each position.

### Parameters
- `tileID` (`Int`): The unique identifier for the raster tile being processed.
- `metadata` (`RasterMetadata`): The metadata object containing spatial reference, resolution, and dimensions of the raster.
- `rasterFeature` (`RasterFeature`): The raster data structure containing the pixel values to be processed.
- `w` (`Int`): The window size (radius) for the sliding operation.
- `winFunc` (`_root_.scala.Function2[Array[T], Array[Boolean], U]`): A function that takes an array of pixel values and an array of booleans (indicating valid/masked pixels) within the window and returns a computed value of type `U`.
- `t` (`ClassTag[T]`): The class tag for the input pixel type.
- `u` (`ClassTag[U]`): The class tag for the output computed type.

### Input
- **Data:** Requires a `RasterFeature` containing pixel data and corresponding `RasterMetadata`.
- **Preconditions:** The raster must be properly initialized. The `winFunc` must handle the provided array sizes, which correspond to the neighborhood defined by `w`.

### Output
Returns `unspecified` — The result of the sliding window transformation applied across the tile, typically resulting in a new raster representation or transformed pixel grid.

### Valid Call Patterns
```scala
// Example inferred from signature (not verified)
val result = new edu.ucr.cs.bdlab.raptor.SlidingWindowTile(
  tileID, 
  metadata, 
  rasterFeature, 
  w, 
  (pixels, valid) => { /* custom logic */ }
)
```

### LLM Instruction Prompt
- When using `SlidingWindowTile`, ensure that the `ClassTag` types `T` and `U` are explicitly provided or implicitly available in the scope.
- The `winFunc` must be a function that accepts an `Array[T]` (pixel values) and `Array[Boolean]` (validity mask) and returns a value of type `U`.
- Ensure `w` is a non-negative integer representing the window radius.

### Prompt Snippet
```text
val slidingWindow = new edu.ucr.cs.bdlab.raptor.SlidingWindowTile[T, U](tileID, metadata, rasterFeature, w, winFunc)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `winFunc` that does not match the expected `Array[T]` and `Array[Boolean]` input types.
- **Metadata Incompatibility:** Passing `RasterMetadata` that does not align with the dimensions of the `RasterFeature`.
- **ClassTag Missing:** Failing to provide implicit `ClassTag` evidence for generic types `T` and `U`, leading to compilation errors.

### Fix Code Hint
```scala
// Ensure implicit ClassTags are in scope
import scala.reflect.ClassTag
implicit val tTag: ClassTag[Float] = ClassTag(classOf[Float])
implicit val uTag: ClassTag[Float] = ClassTag(classOf[Float])

val tileProcessor = new edu.ucr.cs.bdlab.raptor.SlidingWindowTile[Float, Float](
  tileID, metadata, rasterFeature, 3, (pixels, valid) => pixels.sum / pixels.length
)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.SlidingWindowTile.addTile`

### Signature
```scala
[](tile:ITile[T]):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/SlidingWindowTile.scala:51_

### Goal
Adds a raster tile to the current sliding window buffer for subsequent spatial processing or windowed analysis.

### Parameters
- `tile` (`ITile[T]`): The raster tile of type `T` to be included in the sliding window. The type `T` must be consistent with the pixel data type of the raster source (e.g., `Float`, `Int`).

### Input
- **Data:** An `ITile[T]` object representing a portion of a raster dataset (e.g., GeoTIFF or HDF4).
- **Preconditions:** The tile must match the metadata (resolution, CRS, and tile size) of the existing tiles in the `SlidingWindowTile` instance. If rasters are misaligned, use `reshape` operations prior to adding tiles to the window.

### Output
Returns `Unit` — This method performs an in-place update to the internal state of the `SlidingWindowTile` object and does not return a value.

### Valid Call Patterns
```scala
// Inferred from signature:
val window = new SlidingWindowTile[Float](...)
window.addTile(myTile)
```

### LLM Instruction Prompt
- When using `addTile`, ensure the `ITile[T]` type parameter matches the pixel type of the raster data being processed.
- Verify that the `SlidingWindowTile` instance has been initialized with the correct dimensions before adding tiles.
- This method is intended for stateful accumulation; ensure it is called within the appropriate lifecycle of the raster processing workflow.

### Prompt Snippet
```text
// Example of adding a tile to a sliding window
val slidingWindow = new edu.ucr.cs.bdlab.raptor.SlidingWindowTile[Float](...)
slidingWindow.addTile(tile)
```

### Common Failure Modes
- **Type Mismatch:** Passing a tile with a pixel type (e.g., `Int`) that differs from the `SlidingWindowTile` generic type (e.g., `Float`).
- **Metadata Incompatibility:** Adding a tile that does not align with the spatial resolution or CRS of the window, leading to incorrect spatial joins or processing errors.
- **Initialization Errors:** Calling `addTile` on an uninitialized or improperly configured `SlidingWindowTile` instance.

### Fix Code Hint
```scala
// Ensure type consistency and metadata alignment
val tile: ITile[Float] = ... 
val window = new SlidingWindowTile[Float](...)
// Ensure tile is compatible before adding
if (tile.metadata == window.metadata) {
  window.addTile(tile)
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.SlidingWindowTile.getTilesInWindow`

### Signature
```scala
[](tileID:Int,metadata:RasterMetadata,w:Int):Array[Int]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/SlidingWindowTile.scala:169_

### Goal
Calculates the set of tile indices that intersect with a sliding window of size `w` centered around a specific `tileID` within a raster dataset.

### Parameters
- `tileID` (`Int`): The identifier of the reference tile around which the sliding window is applied.
- `metadata` (`RasterMetadata`): The metadata object containing the raster's spatial properties, including dimensions and tiling structure.
- `w` (`Int`): The size of the sliding window (typically representing the radius or dimension of the neighborhood).

### Input
The caller must provide a valid `RasterMetadata` object that matches the raster dataset being processed. The `tileID` must be a valid index within the bounds defined by the `metadata`.

### Output
Returns `Array[Int]` — An array containing the identifiers of all tiles that fall within the specified sliding window range.

### Valid Call Patterns
```scala
// Example inferred from signature
val tileIDs = edu.ucr.cs.bdlab.raptor.SlidingWindowTile.getTilesInWindow(tileID, metadata, w)
```

### LLM Instruction Prompt
- Use this function when performing local neighborhood operations or spatial windowing on raster data.
- Ensure the `metadata` object is correctly initialized from the source raster (e.g., GeoTIFF or HDF4) before calling.
- The `w` parameter should be consistent with the desired spatial neighborhood size relative to the tile resolution.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.SlidingWindowTile
import edu.ucr.cs.bdlab.raptor.RasterMetadata

// Usage:
val tiles = SlidingWindowTile.getTilesInWindow(currentTileID, rasterMetadata, windowSize)
```

### Common Failure Modes
- **Invalid Tile ID:** Providing a `tileID` that is out of bounds for the given `RasterMetadata` will result in an `ArrayIndexOutOfBoundsException` or incorrect window calculation.
- **Metadata Mismatch:** Using `RasterMetadata` that does not match the actual raster file (e.g., different tile dimensions or CRS) will lead to incorrect spatial indexing.
- **Null Metadata:** Passing a null `metadata` object will cause a `NullPointerException`.

### Fix Code Hint
```scala
// Ensure metadata is retrieved from the RasterRDD or file header
val metadata = rasterRDD.metadata
val tiles = edu.ucr.cs.bdlab.raptor.SlidingWindowTile.getTilesInWindow(id, metadata, windowSize)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.SlidingWindowTile.merge`

### Signature
```scala
[](other:SlidingWindowTile[T, U]):SlidingWindowTile[T, U]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/SlidingWindowTile.scala:65_

### Goal
Merges two `SlidingWindowTile` instances into a single tile, combining their spatial window data for subsequent raster processing operations.

### Parameters
- `other` (`SlidingWindowTile[T, U]`): The secondary `SlidingWindowTile` instance to be merged with the current instance.

### Input
- **Data:** Two `SlidingWindowTile` objects of the same generic types `T` and `U`.
- **Preconditions:** Both tiles must share compatible metadata (e.g., resolution, CRS, and tile dimensions) to ensure a valid merge. If the tiles are not aligned, use `reshape` or similar RDPro transformation operations prior to merging.

### Output
Returns `SlidingWindowTile[T, U]` — A new `SlidingWindowTile` containing the merged data from both input tiles.

### Valid Call Patterns
```scala
// Inferred from signature:
val mergedTile = tileA.merge(tileB)
```

### LLM Instruction Prompt
- When calling `merge`, ensure that both `SlidingWindowTile` instances have identical spatial metadata.
- The generic types `T` and `U` must match between the receiver and the `other` parameter.
- This operation is typically used within custom raster processing pipelines where sliding window buffers need to be aggregated.

### Prompt Snippet
```text
val tileA: SlidingWindowTile[Float, Int] = ...
val tileB: SlidingWindowTile[Float, Int] = ...
val result = tileA.merge(tileB)
```

### Common Failure Modes
- **Metadata Mismatch:** Attempting to merge tiles with different resolutions or coordinate reference systems will result in inconsistent raster data.
- **Type Mismatch:** Providing an `other` tile with different generic type parameters `T` or `U` will cause a compilation error.
- **Null Pointer:** Passing a null reference as the `other` argument will result in a runtime exception.

### Fix Code Hint
```scala
// Ensure tiles are aligned before merging
val alignedTileB = tileB.reshape(tileA.metadata)
val merged = tileA.merge(alignedTileB)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.SlidingWindowTile.resultTile`

### Signature
```scala
[]:ITile[U]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/SlidingWindowTile.scala:71_

### Goal
Retrieves the resulting `ITile[U]` generated after applying sliding window operations within the `SlidingWindowTile` context.

### Parameters
_None._

### Input
The caller must ensure that the `SlidingWindowTile` instance has been properly initialized and that the sliding window transformation logic has been executed. As this is a stateful operation within the `SlidingWindowTile` class, the tile must be in a state where the result has been computed.

### Output
Returns `ITile[U]` — A raster tile containing the computed values resulting from the sliding window processing, where `U` represents the pixel data type (e.g., `Float`, `Int`).

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: ITile[U] = slidingWindowTileInstance.resultTile()
```

### LLM Instruction Prompt
- When using `resultTile`, ensure the `SlidingWindowTile` instance has already processed the input raster data.
- The type parameter `U` must match the expected pixel type of the resulting raster data (e.g., `Float` for continuous data).
- This method does not accept arguments; it returns the internal state of the processed tile.

### Prompt Snippet
```text
val result = slidingWindowTileInstance.resultTile()
```

### Common Failure Modes
- **Uninitialized State:** Calling `resultTile` before the sliding window computation has been performed, potentially returning an empty or null tile.
- **Type Mismatch:** Attempting to cast the returned `ITile[U]` to an incompatible type if the pixel type `U` does not match the underlying raster data.
- **Memory Constraints:** Large tiles may cause `OutOfMemoryError` if the sliding window operation produces high-resolution output tiles that exceed the available executor memory.

### Fix Code Hint
```scala
// Ensure the sliding window operation is complete before accessing the result
val slidingWindowTile = new SlidingWindowTile(...)
// ... perform window operations ...
val finalTile = slidingWindowTile.resultTile()
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.StackedTile`

### Signature
```scala
[](tiles:_root_.scala.<repeated>[ITile[_$1] forSome { 
  <synthetic> type _$1
}])(t:ClassTag[T]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/StackedTile.scala:29_

### Goal
Creates a composite raster structure by stacking multiple `ITile` objects into a single multi-band or multi-layer tile representation for concurrent raster-vector processing.

### Parameters
- `tiles` (`_root_.scala.<repeated>[ITile[_$1] forSome { <synthetic> type _$1 }`): A variable-length sequence of `ITile` objects to be stacked.
- `t` (`ClassTag[T]`): The `ClassTag` for the pixel data type `T` (e.g., `Float`, `Int`), required to maintain type safety during the stacking operation.

### Input
- **Data:** A collection of `ITile` objects, typically derived from `RasterRDD` or individual raster file reads (GeoTIFF/HDF4).
- **Preconditions:** All input tiles must share identical spatial metadata, including resolution, CRS, and tile dimensions. If tiles are misaligned, they must be processed via `reshape` before stacking.

### Output
Returns `unspecified` — A `StackedTile` object representing the combined raster data, which can be used in subsequent Raptor join or pixel-level transformation operations.

### Valid Call Patterns
```scala
// Inferred usage pattern based on constructor signature
val stacked = new edu.ucr.cs.bdlab.raptor.StackedTile(tile1, tile2)(scala.reflect.classTag[Float])
```

### LLM Instruction Prompt
- When generating code for `StackedTile`, ensure the `ClassTag` is explicitly provided using `scala.reflect.classTag[T]`.
- Verify that all input tiles are of the same pixel type `T` and spatial extent before attempting to stack them.
- Use this API only when performing multi-band raster operations or preparing data for complex Raptor joins.

### Prompt Snippet
```text
// Example of stacking two tiles of type Float
val stackedTile = new edu.ucr.cs.bdlab.raptor.StackedTile(tileA, tileB)(scala.reflect.classTag[Float])
```

### Common Failure Modes
- **Metadata Mismatch:** Attempting to stack tiles with different resolutions or CRS will result in runtime errors or invalid spatial alignment. Use `reshape` to ensure compatibility.
- **Type Mismatch:** Providing tiles with different pixel types or failing to provide the correct `ClassTag` for the intended type `T` will cause compilation or runtime type errors.
- **Empty Input:** Passing an empty sequence of tiles may result in an invalid `StackedTile` instance.

### Fix Code Hint
```scala
// Ensure tiles are aligned before stacking
val alignedTiles = tiles.map(_.reshape(targetMetadata))
val stacked = new edu.ucr.cs.bdlab.raptor.StackedTile(alignedTiles: _*)(scala.reflect.classTag[Float])
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.StackedTile.getPixelValue`

### Signature
```scala
[](x:Int,y:Int):Array[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/StackedTile.scala:39_

### Goal
Retrieves the pixel values across all bands for a specific coordinate within a `StackedTile` in the Raptor raster processing framework.

### Parameters
- `x` (`Int`): The column index (horizontal coordinate) of the pixel within the tile.
- `y` (`Int`): The row index (vertical coordinate) of the pixel within the tile.

### Input
The caller must provide a `StackedTile` instance containing multi-band raster data. The coordinates `(x, y)` must fall within the bounds of the tile dimensions. As this is a component of the Raptor framework, the tile should be part of a `RasterRDD` or a similar structure processed within the Beast environment.

### Output
Returns `Array[T]` — An array containing the pixel values for each band at the specified `(x, y)` location, where `T` corresponds to the underlying data type (e.g., `Float`, `Int`).

### Valid Call Patterns
```scala
// Inferred from signature:
val pixelValues: Array[T] = stackedTile.getPixelValue(x, y)
```

### LLM Instruction Prompt
- When using `getPixelValue`, ensure the `x` and `y` coordinates are within the valid range of the `StackedTile` dimensions to avoid index out-of-bounds errors.
- The returned `Array[T]` contains values for all bands stacked at that specific spatial location.
- Ensure the type `T` matches the raster's pixel type (e.g., `FloatType` or `IntegerType`) as defined in the `RasterRDD` metadata.

### Prompt Snippet
```text
val values = tile.getPixelValue(col, row)
```

### Common Failure Modes
- **IndexOutOfBoundsException**: Occurs if the provided `x` or `y` coordinates are outside the tile's width or height.
- **Type Mismatch**: Occurs if the expected return type `T` does not match the actual data type stored in the `StackedTile` (e.g., attempting to read `Float` values from an `Int` tile).
- **Null Reference**: Occurs if the `StackedTile` instance has not been properly initialized or loaded from the source raster.

### Fix Code Hint
```scala
// Ensure coordinates are within bounds before access
if (x >= 0 && x < tile.width && y >= 0 && y < tile.height) {
  val values = tile.getPixelValue(x, y)
} else {
  // Handle out-of-bounds case
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.StackedTile.isEmpty`

### Signature
```scala
[](i:Int,j:Int):Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/StackedTile.scala:61_

### Goal
Determines whether the pixel at the specified column and row coordinates within a `StackedTile` is considered empty or null.

### Parameters
- `i` (`Int`): The column index (x-coordinate) of the pixel within the tile.
- `j` (`Int`): The row index (y-coordinate) of the pixel within the tile.

### Input
The caller must provide a valid `StackedTile` instance. The indices `i` and `j` must be within the bounds of the tile dimensions. As this is a raster operation within the Raptor framework, ensure the tile has been properly initialized from a supported raster source (e.g., GeoTIFF or HDF4).

### Output
Returns `Boolean` — `true` if the pixel at the given coordinates is empty (e.g., represents a NoData value or is uninitialized), and `false` otherwise.

### Valid Call Patterns
```scala
// Inferred from signature:
val tile: StackedTile = ...
val isPixelEmpty = tile.isEmpty(i, j)
```

### LLM Instruction Prompt
- When checking for pixel validity in a `StackedTile`, use `isEmpty(i, j)` to verify if the pixel contains data before performing arithmetic or transformation operations.
- Ensure `i` and `j` are within the tile's bounds to avoid `ArrayIndexOutOfBoundsException`.

### Prompt Snippet
```text
// Check if pixel is empty before processing
if (!tile.isEmpty(i, j)) {
  val value = tile.get(i, j)
  // process value
}
```

### Common Failure Modes
- **Out of Bounds:** Passing `i` or `j` values that exceed the tile's width or height will result in a runtime exception.
- **Uninitialized Tile:** Calling this method on a `StackedTile` that has not been correctly populated from a raster source may lead to unexpected results or null pointer exceptions.
- **Type Mismatch:** Ensure the `StackedTile` is the correct type expected by your raster workflow, as Raptor operations often require specific pixel types (e.g., `Float`, `Int`).

### Fix Code Hint
```scala
// Ensure indices are within bounds before calling isEmpty
if (i >= 0 && i < tile.width && j >= 0 && j < tile.height) {
  if (!tile.isEmpty(i, j)) {
    // Safe to access pixel data
  }
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.TileIterator`

### Signature
```scala
[](intersections:Iterator[scala.Tuple2[Long, PixelRange]],tiles:Iterator[scala.Tuple2[Long, ITile[T]]],numTiles:LongAccumulator,numRanges:LongAccumulator)(t:ClassTag[T]):<type ?>
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/TileIterator.scala:22_

### Goal
`TileIterator` facilitates the concurrent processing of raster and vector data by iterating over matched raster tiles and their corresponding pixel ranges defined by spatial intersections.

### Parameters
- `intersections` (`Iterator[scala.Tuple2[Long, PixelRange]]`): An iterator of tuples containing a tile ID and the specific `PixelRange` within that tile that intersects with a vector feature.
- `tiles` (`Iterator[scala.Tuple2[Long, ITile[T]]]`): An iterator of tuples containing a tile ID and the corresponding `ITile[T]` raster data.
- `numTiles` (`LongAccumulator`): A Spark `LongAccumulator` used to track the total number of tiles processed.
- `numRanges` (`LongAccumulator`): A Spark `LongAccumulator` used to track the total number of pixel ranges processed.
- `t` (`ClassTag[T]`): The `ClassTag` for the pixel type `T` (e.g., `Float`, `Int`), required for runtime type information in the generic iterator.

### Input
The caller must provide iterators of raster tiles and their associated intersection ranges. Preconditions include ensuring that the `ITile[T]` data matches the expected pixel type `T` and that the tile IDs in both iterators are aligned to allow for correct joining during iteration.

### Output
Returns `unspecified` — The return type is not explicitly defined in the provided API facts; it typically yields an iterator of processed raster-vector intersection results.

### Valid Call Patterns
```scala
// Inferred call pattern based on signature
val tileIterator = new edu.ucr.cs.bdlab.raptor.TileIterator(
  intersections, 
  tiles, 
  numTilesAcc, 
  numRangesAcc
)(scala.reflect.ClassTag(classOf[Float]))
```

### LLM Instruction Prompt
- When using `TileIterator`, ensure that the `ClassTag[T]` matches the underlying data type of the `ITile[T]` objects.
- Always provide initialized `LongAccumulator` instances to monitor the progress of the iteration.
- Ensure that the `intersections` and `tiles` iterators are correctly sorted or aligned by tile ID if the implementation requires ordered access.

### Prompt Snippet
```text
val tileIterator = new edu.ucr.cs.bdlab.raptor.TileIterator(intersections, tiles, numTiles, numRanges)(scala.reflect.ClassTag(classOf[T]))
```

### Common Failure Modes
- **Type Mismatch:** Providing a `ClassTag` that does not match the `ITile[T]` generic type will cause runtime errors.
- **Uninitialized Accumulators:** Passing null or uninitialized `LongAccumulator` objects will result in `NullPointerException` during the iteration process.
- **Alignment Issues:** If the `intersections` and `tiles` iterators are not properly synchronized by tile ID, the iterator may fail to produce correct results or skip data.

### Fix Code Hint
```scala
// Ensure correct type tagging for Float pixels
import scala.reflect.ClassTag
val tileIterator = new edu.ucr.cs.bdlab.raptor.TileIterator[Float](
  intersections, 
  tiles, 
  sc.longAccumulator("tiles"), 
  sc.longAccumulator("ranges")
)(ClassTag(classOf[Float]))
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.TileIterator.hasNext`

### Signature
```scala
[]:Boolean
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/TileIterator.scala:109_

### Goal
Checks if there are additional raster tiles available to be retrieved from the `TileIterator` during a Raptor raster-vector processing workflow.

### Parameters
_None._

### Input
The caller must have an initialized `TileIterator` instance. This iterator is typically used within the Raptor framework to traverse raster data segments (tiles) during spatial join or pixel-level transformation operations.

### Output
Returns `Boolean` — `true` if the iterator has more tiles to process, `false` otherwise.

### Valid Call Patterns
```scala
// Inferred usage pattern for iterating over raster tiles
val iterator: TileIterator = ... // obtained from a Raptor operation
while (iterator.hasNext) {
  val tile = iterator.next()
  // Process tile
}
```

### LLM Instruction Prompt
- When generating code that iterates over raster data in the `edu.ucr.cs.bdlab.raptor` package, use `hasNext` as a guard condition for a `while` loop before calling `next()`.
- Do not assume the iterator is thread-safe; it is intended for sequential access within a single task.

### Prompt Snippet
```text
// Check for remaining tiles before calling next()
if (tileIterator.hasNext) {
  val currentTile = tileIterator.next()
}
```

### Common Failure Modes
- **Concurrent Modification:** Calling `hasNext` or `next` on an iterator while the underlying raster data source is being modified or closed by another Spark task.
- **Exhausted Iterator:** Calling `next()` without verifying `hasNext` returns `true` will result in a `NoSuchElementException`.
- **Uninitialized Iterator:** Attempting to call `hasNext` on a null or uninitialized `TileIterator` reference will throw a `NullPointerException`.

### Fix Code Hint
```scala
// Always wrap in a conditional check
if (iterator != null && iterator.hasNext) {
  val tile = iterator.next()
  // ...
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.TileIterator.next`

### Signature
```scala
[]:RaptorJoinResultTile[T]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/TileIterator.scala:111_

### Goal
Retrieves the next `RaptorJoinResultTile[T]` from the iterator during a raster-vector join operation, providing access to the processed pixel data associated with spatial features.

### Parameters
_None._

### Input
The `TileIterator` must be initialized within a Raptor join context. The caller must ensure that the underlying raster data (GeoTIFF or HDF4) and vector data (Shapefile, GeoJSON, etc.) have been correctly prepared for the join. No specific preconditions are required for the `next` call itself, other than ensuring the iterator has not reached its end (i.e., `hasNext` should be checked if the iterator is not being used in a standard loop).

### Output
Returns `RaptorJoinResultTile[T]` — A container representing a tile of raster data that has been joined with vector geometries, containing the pixel values and associated spatial metadata for the current iteration step.

### Valid Call Patterns
```scala
// Inferred usage pattern for iterating through Raptor join results
val iterator: TileIterator[T] = ... // Obtained from a Raptor join operation
while (iterator.hasNext) {
  val tile: RaptorJoinResultTile[T] = iterator.next()
  // Process the joined tile
}
```

### LLM Instruction Prompt
- When generating code that uses `TileIterator.next`, always wrap the call in a check for `iterator.hasNext` to prevent `NoSuchElementException`.
- Ensure the type parameter `T` matches the pixel type of the source raster (e.g., `Float`, `Int`).
- Use this method only when performing low-level iteration over raster-vector join results where standard `RDD` transformations are insufficient.

### Prompt Snippet
```text
val tile = iterator.next()
```

### Common Failure Modes
- **NoSuchElementException**: Occurs if `next()` is called when `hasNext` is false. Always verify the iterator state before calling.
- **Type Mismatch**: Occurs if the generic type `T` does not match the underlying raster pixel data type (e.g., attempting to read `Float` pixels as `Int`).
- **Iterator Exhaustion**: The iterator is stateful and consumes the underlying data stream; it cannot be reset or traversed multiple times.

### Fix Code Hint
```scala
if (iterator.hasNext) {
  val tile = iterator.next()
  // Safe to process tile
} else {
  // Handle end of iteration
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ZonalStatistics.addDependentClasses`

### Signature
```scala
[](opts:BeastOptions,classes:util.Stack[Class[_$1] forSome { 
  <synthetic> type _$1
}]):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ZonalStatistics.scala:53_

### Goal
Registers additional dependent classes required for zonal statistics computations within the Raptor framework to ensure proper serialization and runtime availability.

### Parameters
- `opts` (`BeastOptions`): The configuration object containing runtime settings and parameters for the Beast execution environment.
- `classes` (`util.Stack[Class[_$1] forSome { <synthetic> type _$1 }]`): A stack of class references that must be registered as dependencies for the zonal statistics operation.

### Input
- **Preconditions:** The caller must provide a valid `BeastOptions` instance and a populated `java.util.Stack` of classes.
- **Context:** This method is typically invoked during the initialization phase of a Raptor-based spatial join or zonal statistics workflow to ensure that custom classes or types used in pixel-level transformations are correctly handled by the Spark/Beast runtime.

### Output
Returns `Unit` — This method performs a side-effect by updating the internal registry of dependent classes used by the `ZonalStatistics` object.

### Valid Call Patterns
```scala
import edu.ucr.cs.bdlab.raptor.ZonalStatistics
import edu.ucr.cs.bdlab.beast.util.BeastOptions
import java.util.Stack

val opts = new BeastOptions()
val classes = new Stack[Class[_]]()
// Add required classes to the stack
classes.push(classOf[MyCustomStatistic])
ZonalStatistics.addDependentClasses(opts, classes)
```
*(Note: This call pattern is inferred from the signature as no existing test suite example was found.)*

### LLM Instruction Prompt
- When calling `addDependentClasses`, ensure the `BeastOptions` object is fully initialized.
- The `classes` parameter must be a `java.util.Stack` containing the specific class types required for the custom zonal statistics logic.
- This method should be called before initiating the `raptorJoin` or zonal statistics execution to avoid runtime class-loading errors.

### Prompt Snippet
```text
// Registering dependent classes for ZonalStatistics
val opts = new BeastOptions()
val classes = new java.util.Stack[Class[_]]()
classes.push(classOf[SomeRequiredClass])
edu.ucr.cs.bdlab.raptor.ZonalStatistics.addDependentClasses(opts, classes)
```

### Common Failure Modes
- **NullPointerException:** Occurs if `opts` or `classes` is null.
- **Class Loading Errors:** If the classes provided in the stack are not available in the classpath of the Spark executors, the subsequent zonal statistics operation will fail during task execution.
- **Configuration Mismatch:** Providing an uninitialized `BeastOptions` object may lead to unexpected behavior if the framework expects specific configuration keys to be present.

### Fix Code Hint
```scala
// Ensure the stack is not empty and classes are accessible
val classes = new java.util.Stack[Class[_]]()
classes.push(classOf[MyCustomClass])
try {
  ZonalStatistics.addDependentClasses(opts, classes)
} catch {
  case e: Exception => println(s"Failed to register classes: ${e.getMessage}")
}
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ZonalStatistics.computeZonalStatisticsNaive`

### Signature
```scala
[type T](geometry:Geometry,raster:IRasterReader[T],result:Collector):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ZonalStatistics.scala:198_

### Goal
Performs a naive zonal statistics calculation by aggregating raster pixel values that intersect with a specific geometry and reporting the results to a collector.

### Parameters
- `geometry` (`Geometry`): The spatial feature (e.g., polygon) used to define the zone for which statistics are calculated.
- `raster` (`IRasterReader[T]`): The raster data source providing pixel values of type `T` (e.g., `Float`, `Int`) to be processed.
- `result` (`Collector`): A collector object used to store or emit the computed statistical results.

### Input
- **Data Formats:** The `geometry` must be a valid JTS `Geometry`. The `raster` must be an `IRasterReader` compatible with the pixel type `T`.
- **Preconditions:** The raster and geometry should ideally be in the same coordinate reference system (CRS). The `IRasterReader` must be initialized and ready to provide pixel data for the spatial extent covered by the geometry.

### Output
Returns `Unit` — The function performs an in-place update or emission to the provided `Collector` object; no direct return value is produced.

### Valid Call Patterns
```scala
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.ZonalStatistics.computeZonalStatisticsNaive[Float](geometry, rasterReader, collector)
```

### LLM Instruction Prompt
- When calling this function, ensure the type parameter `T` matches the underlying pixel data type of the `IRasterReader` (e.g., `Float` for temperature data, `Int` for categorical land cover).
- Always provide a valid `Collector` instance capable of handling the output statistics.
- Ensure the `geometry` is spatially indexed or prepared if performing this operation within a larger Spark transformation loop.

### Prompt Snippet
```text
// Example of invoking computeZonalStatisticsNaive
val myGeometry: Geometry = ...
val myRaster: IRasterReader[Float] = ...
val myCollector: Collector = ...
edu.ucr.cs.bdlab.raptor.ZonalStatistics.computeZonalStatisticsNaive[Float](myGeometry, myRaster, myCollector)
```

### Common Failure Modes
- **Type Mismatch:** Providing a type `T` that does not match the `IRasterReader` pixel type will result in a compilation error or runtime exception.
- **Coordinate Mismatch:** If the `geometry` and `raster` are in different projections, the intersection logic may return empty results or incorrect statistics.
- **Null Collector:** Passing a null or uninitialized `Collector` will cause a `NullPointerException` during the aggregation phase.

### Fix Code Hint
```scala
// Ensure the raster reader is correctly typed
val raster: IRasterReader[Float] = ... 
// Use the correct type parameter T
edu.ucr.cs.bdlab.raptor.ZonalStatistics.computeZonalStatisticsNaive[Float](geometry, raster, collector)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ZonalStatistics.run`

### Signature
```scala
[](opts:BeastOptions,inputs:Array[String],outputs:Array[String],sc:SparkContext):Unit
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ZonalStatistics.scala:59_

### Goal
Computes zonal statistics by aggregating raster pixel values within the boundaries of vector geometries using the Raptor engine.

### Parameters
- `opts` (`BeastOptions`): Configuration options for the zonal statistics operation, including parameters for raster-vector alignment and aggregation methods.
- `inputs` (`Array[String]`): An array containing the file paths for the input datasets; typically the first element is the raster file (GeoTIFF/HDF4) and the second is the vector file (Shapefile/GeoJSON).
- `outputs` (`Array[String]`): An array containing the destination path for the resulting statistics file.
- `sc` (`SparkContext`): The active Spark context used for distributed processing.

### Input
- **Data Formats:** Supports GeoTIFF or HDF4 for raster data and Shapefile or GeoJSON for vector data.
- **Preconditions:** The raster and vector datasets must be spatially aligned. If they are not in the same coordinate reference system (CRS), they must be reprojected prior to this operation.
- **Configuration:** `BeastOptions` must be correctly initialized to define the specific zonal statistics to compute (e.g., mean, sum, count).

### Output
Returns `Unit` — The function writes the computed zonal statistics to the specified output path as a spatial file or tabular summary.

### Valid Call Patterns
```scala
// Inferred from signature:
val opts = new BeastOptions()
val inputs = Array("path/to/raster.tif", "path/to/polygons.shp")
val outputs = Array("path/to/output_stats")
edu.ucr.cs.bdlab.raptor.ZonalStatistics.run(opts, inputs, outputs, sc)
```

### LLM Instruction Prompt
- When calling `ZonalStatistics.run`, ensure the `inputs` array contains exactly two paths: the raster file followed by the vector file.
- Always verify that the output directory does not exist or use `opts` to set an overwrite flag to prevent runtime failures.
- Ensure the `SparkContext` is active and properly configured for the cluster environment.

### Prompt Snippet
```text
// Example of invoking ZonalStatistics
val opts = new BeastOptions()
val inputs = Array("raster.tif", "zones.shp")
val outputs = Array("output_dir")
edu.ucr.cs.bdlab.raptor.ZonalStatistics.run(opts, inputs, outputs, sc)
```

### Common Failure Modes
- **Output Directory Exists:** The operation will fail if the output path already exists; use `-overwrite` in `BeastOptions` to force deletion.
- **CRS Mismatch:** If the raster and vector data are in different coordinate systems, the zonal statistics will be inaccurate or the operation may fail to intersect.
- **Missing Dependencies:** Ensure the `beast-spark` library is included in the project classpath.

### Fix Code Hint
```scala
// Ensure overwrite is enabled if the output directory might exist
val opts = new BeastOptions()
opts.set("overwrite", "true")
edu.ucr.cs.bdlab.raptor.ZonalStatistics.run(opts, Array(rasterPath, vectorPath), Array(outputPath), sc)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ZonalStatistics.zonalStats2`

### Signature
```scala
[type T](zones:RDD[IFeature],raster:RDD[ITile[T]],collectorClass:Class[_$3] forSome { 
  <synthetic> type _$3 <: Collector
},opts:BeastOptions,numTiles:LongAccumulator)(t:ClassTag[T]):RDD[scala.Tuple2[IFeature, Collector]]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ZonalStatistics.scala:128_

### Goal
Computes zonal statistics by aggregating raster pixel values within the boundaries of provided vector features using a specified collector implementation.

### Parameters
- `zones` (`RDD[IFeature]`): An RDD of vector features (e.g., polygons) defining the zones for which statistics are calculated.
- `raster` (`RDD[ITile[T]]`): An RDD of raster tiles containing the pixel data to be aggregated.
- `collectorClass` (`Class[_$3] forSome { <synthetic> type _$3 <: Collector }`): The class of the `Collector` to use for aggregating pixel values (e.g., sum, mean, count).
- `opts` (`BeastOptions`): Configuration options for the zonal statistics operation.
- `numTiles` (`LongAccumulator`): A Spark `LongAccumulator` used to track the number of tiles processed during the operation.
- `t` (`ClassTag[T]`): The `ClassTag` for the pixel type `T` (e.g., `Float`, `Int`).

### Input
- **Data:** Requires vector data (e.g., Shapefile, GeoJSON) as `RDD[IFeature]` and raster data (e.g., GeoTIFF, HDF4) as `RDD[ITile[T]]`.
- **Preconditions:** The raster and vector datasets should ideally be spatially partitioned to ensure efficient join performance. Raster metadata (resolution, CRS) must be consistent across the `raster` RDD.
- **Type Selection:** The type parameter `T` must match the underlying pixel data type of the raster (e.g., `Float`, `Int`).

### Output
Returns `RDD[scala.Tuple2[IFeature, Collector]]` — A distributed collection where each feature is paired with its corresponding `Collector` object containing the computed statistics for that zone.

### Valid Call Patterns
```scala
// Inferred from signature:
val stats = edu.ucr.cs.bdlab.raptor.ZonalStatistics.zonalStats2(
  zonesRDD, 
  rasterRDD, 
  classOf[MyCollector], 
  new BeastOptions(), 
  sc.longAccumulator("tilesProcessed")
)(scala.reflect.classTag[Float])
```

### LLM Instruction Prompt
- When calling `zonalStats2`, ensure the `collectorClass` provided is a valid subclass of `edu.ucr.cs.bdlab.raptor.Collector`.
- Always provide a `ClassTag[T]` that matches the pixel type of the input `raster` RDD.
- Ensure the `numTiles` accumulator is initialized via the `SparkContext` before passing it to the function.

### Prompt Snippet
```text
// Example usage of zonalStats2
val result = ZonalStatistics.zonalStats2(zones, raster, classOf[MeanCollector], opts, acc)(ClassTag(classOf[Float]))
```

### Common Failure Modes
- **Type Mismatch:** Providing a `ClassTag` that does not match the actual pixel type `T` of the `ITile` objects will cause runtime errors.
- **Uninitialized Accumulator:** Passing a null or uninitialized `LongAccumulator` will result in a `NullPointerException`.
- **Metadata Inconsistency:** If the raster tiles are not properly aligned or have mismatched CRS/resolution, the zonal statistics may be inaccurate or fail to compute.

### Fix Code Hint
```scala
// Ensure pixel type consistency
val pixelType = scala.reflect.classTag[Float]
val stats = ZonalStatistics.zonalStats2(zones, raster, classOf[SumCollector], opts, acc)(pixelType)
```

## API Test: `scala:edu.ucr.cs.bdlab.raptor.ZonalStatistics.zonalStatsLocal`

### Signature
```scala
[type T](geometries:Array[Geometry],raster:IRasterReader[T],collectorClass:Class[_$4] forSome { 
  <synthetic> type _$4 <: Collector
}):Array[Collector]
```
_Source: source/raptor/src/main/scala/edu/ucr/cs/bdlab/raptor/ZonalStatistics.scala:162_

### Goal
Computes zonal statistics by aggregating raster pixel values that intersect with a provided array of vector geometries using a specified collector implementation.

### Parameters
- `geometries` (`Array[Geometry]`): An array of JTS Geometry objects defining the zones for which statistics are to be calculated.
- `raster` (`IRasterReader[T]`): An `IRasterReader` instance providing access to the underlying raster data (e.g., GeoTIFF or HDF4) for the specified pixel type `T`.
- `collectorClass` (`Class[_$4] forSome { <synthetic> type _$4 <: Collector }`): The class type of the `Collector` to be instantiated for each geometry to aggregate the intersecting pixel values.

### Input
- **Data:** Requires an array of geometries and a valid `IRasterReader` initialized for the target raster file.
- **Formats:** Supports standard raster formats compatible with `IRasterReader` (GeoTIFF, HDF4).
- **Preconditions:** The raster and geometries should ideally share the same coordinate reference system (CRS). The pixel type `T` must match the data type of the raster (e.g., `Float`, `Int`).

### Output
Returns `Array[Collector]` — An array of `Collector` objects, where each index corresponds to the input geometry at the same index, containing the aggregated results (e.g., sum, mean, count) for that zone.

### Valid Call Patterns
```scala
// Example inferred from signature:
val stats = edu.ucr.cs.bdlab.raptor.ZonalStatistics.zonalStatsLocal(
  geometriesArray, 
  rasterReader, 
  classOf[MyCollectorImplementation]
)
```

### LLM Instruction Prompt
- When calling `zonalStatsLocal`, ensure the `collectorClass` provided is a concrete implementation of the `Collector` trait.
- Ensure the generic type `T` of the `IRasterReader` matches the expected pixel data type of the raster source.
- The function operates locally; ensure the `geometries` array fits within the memory constraints of the driver node.

### Prompt Snippet
```text
import edu.ucr.cs.bdlab.raptor.ZonalStatistics
// Ensure geometries are prepared and raster reader is initialized
val results = ZonalStatistics.zonalStatsLocal(geometries, rasterReader, classOf[SomeCollector])
```

### Common Failure Modes
- **Type Mismatch:** Providing a `collectorClass` that does not extend `Collector` or a `raster` reader with a type `T` incompatible with the actual raster data.
- **Memory Exhaustion:** Passing an excessively large `Array[Geometry]` that exceeds driver memory limits.
- **CRS Mismatch:** If the geometries and raster are in different coordinate systems, the intersection logic may return empty results or incorrect statistics.

### Fix Code Hint
```scala
// Ensure the collector class is correctly passed using classOf
val results = edu.ucr.cs.bdlab.raptor.ZonalStatistics.zonalStatsLocal[Float](
  geometries, 
  rasterReader, 
  classOf[edu.ucr.cs.bdlab.raptor.MeanCollector]
)
```

## API Test: `scala:object:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.reproject`

### Signature
```scala
[](metadata:Array[RasterMetadata],targetCRS:CoordinateReferenceSystem):RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:385_

### Goal
Calculates the unified `RasterMetadata` required to represent a collection of raster tiles when reprojected into a new target Coordinate Reference System.

### Parameters
- `metadata` (`Array[RasterMetadata]`): An array of metadata objects corresponding to the input raster tiles that need to be reprojected.
- `targetCRS` (`CoordinateReferenceSystem`): The destination coordinate reference system to which the raster data will be transformed.

### Input
- **Data:** Requires an array of `RasterMetadata` objects derived from existing raster datasets (e.g., GeoTIFF or HDF4).
- **Preconditions:** The input `metadata` must accurately reflect the spatial extent and projection of the source rasters. The `targetCRS` must be a valid `CoordinateReferenceSystem` object supported by the underlying projection libraries.

### Output
Returns `RasterMetadata` — A new metadata object representing the spatial bounds, resolution, and coordinate system of the raster after it has been reprojected to the `targetCRS`.

### Valid Call Patterns
```scala
// Inferred from signature:
val newMetadata = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.reproject(metadataArray, targetCRS)
```

### LLM Instruction Prompt
- When calling `reproject`, ensure the `metadata` array contains all relevant tiles that form the raster dataset to be transformed.
- Use this function to determine the target grid dimensions and bounds before performing actual pixel-level transformations or resampling.

### Prompt Snippet
```text
val targetCRS = ... // Define your target CoordinateReferenceSystem
val metadataArray = ... // Array of RasterMetadata from your source RDD
val reprojectedMeta = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.reproject(metadataArray, targetCRS)
```

### Common Failure Modes
- **CRS Mismatch:** Providing an invalid or unsupported `CoordinateReferenceSystem` will cause the projection calculation to fail.
- **Empty Metadata:** Passing an empty `Array[RasterMetadata]` will result in an error or an undefined metadata state.
- **Incompatible Projections:** Attempting to reproject between systems where the transformation path is undefined or mathematically impossible for the given extent.

### Fix Code Hint
```scala
// Ensure the target CRS is correctly initialized before calling reproject
import org.opengis.referencing.crs.CoordinateReferenceSystem
// ...
val reprojectedMeta = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.reproject(inputMetadata, targetCRS)
```

## API Test: `scala:object:edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.rescale`

### Signature
```scala
[](metadata:Array[RasterMetadata],rasterWidth:Int,rasterHeight:Int):RasterMetadata
```
_Source: source/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/RasterMetadata.scala:354_

### Goal
Calculates a unified `RasterMetadata` object by rescaling a collection of existing raster metadata entries to a target pixel width and height.

### Parameters
- `metadata` (`Array[RasterMetadata]`): An array of existing `RasterMetadata` objects representing the source raster tiles or datasets to be rescaled.
- `rasterWidth` (`Int`): The target width in pixels for the resulting metadata.
- `rasterHeight` (`Int`): The target height in pixels for the resulting metadata.

### Input
The caller must provide an array of valid `RasterMetadata` objects. This function is typically used when preparing to perform raster operations that require uniform dimensions across multiple tiles or datasets. Ensure that the input metadata objects are correctly initialized from source formats like GeoTIFF or HDF4.

### Output
Returns `RasterMetadata` — A new metadata object representing the rescaled dimensions, which can be used to align subsequent raster processing or `raptorJoin` operations.

### Valid Call Patterns
```scala
// Example inferred from signature
val newMetadata = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.rescale(metadataArray, 1024, 1024)
```

### LLM Instruction Prompt
- Use `edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.rescale` when you need to normalize the dimensions of multiple raster datasets before performing an overlay or join operation.
- Always ensure the `metadata` array is not empty and contains valid `RasterMetadata` instances.
- The `rasterWidth` and `rasterHeight` must be positive integers representing the desired output resolution.

### Prompt Snippet
```text
// Rescale metadata to a standard 1024x1024 grid
val rescaledMeta = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.rescale(sourceMetadata, 1024, 1024)
```

### Common Failure Modes
- **Invalid Dimensions:** Providing non-positive values for `rasterWidth` or `rasterHeight` will result in invalid metadata.
- **Empty Metadata Array:** Passing an empty `Array[RasterMetadata]` may lead to runtime exceptions or undefined behavior during the calculation.
- **Incompatible Metadata:** If the input `metadata` objects have conflicting coordinate reference systems (CRS) that cannot be reconciled by simple rescaling, the resulting metadata may not be suitable for subsequent spatial operations.

### Fix Code Hint
```scala
// Ensure dimensions are valid before calling
val targetWidth = 1024
val targetHeight = 1024
if (metadataArray.nonEmpty && targetWidth > 0 && targetHeight > 0) {
  val rescaled = edu.ucr.cs.bdlab.beast.geolite.RasterMetadata.rescale(metadataArray, targetWidth, targetHeight)
}
```

