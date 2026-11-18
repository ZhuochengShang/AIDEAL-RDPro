# RDPro Raptor raster scope — Beast 0.10.1 — LLM_readme

LLM-facing API documentation. Generated from the API surface, project profile, and distilled docs.

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry`

### Signature
```java
(int keyID,int tagLoc,int count,int offset):null
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:56_

### Goal
Represents a GeoTIFF GeoKey entry, used for mapping specific geospatial metadata tags to their corresponding data locations and counts within a raster file.

### Parameters
- `keyID` (`int`): The unique identifier for the GeoKey (e.g., ModelType, RasterType).
- `tagLoc` (`int`): The location of the tag in the GeoTIFF metadata structure.
- `count` (`int`): The number of values associated with this specific GeoKey.
- `offset` (`int`): The byte offset or index where the actual data for this key begins.

### Input
The caller must provide integer values corresponding to the internal structure of a GeoTIFF metadata header. This class is typically used during the parsing of raster metadata headers when reading GeoTIFF files.

### Output
Returns `null` — This is a constructor that initializes a `GeoKeyEntry` object; it does not return a value.

### Valid Call Patterns
```java
// Example inferred from signature
edu.ucr.cs.bdlab.raptor.GeoKeyEntry entry = new edu.ucr.cs.bdlab.raptor.GeoKeyEntry(keyID, tagLoc, count, offset);
```

### LLM Instruction Prompt
- When generating code that uses `GeoKeyEntry`, ensure the parameters are derived from valid GeoTIFF metadata parsing logic.
- This class is part of the low-level Raptor raster processing stack; it should be used when manually inspecting or constructing GeoTIFF metadata structures.

### Prompt Snippet
```text
// Constructing a GeoKeyEntry for a specific GeoTIFF tag
edu.ucr.cs.bdlab.raptor.GeoKeyEntry geoKey = new edu.ucr.cs.bdlab.raptor.GeoKeyEntry(keyID, tagLoc, count, offset);
```

### Common Failure Modes
- Providing negative values for `count` or `offset`, which are expected to be non-negative indices/lengths.
- Mismatched `count` values relative to the actual data size, which may lead to `IndexOutOfBoundsException` or corrupted metadata interpretation during subsequent raster processing.
- Attempting to use this class for non-GeoTIFF raster formats (e.g., HDF4), as the GeoKey structure is specific to the GeoTIFF specification.

### Fix Code Hint
```java
// Ensure parameters are validated against the GeoTIFF specification before instantiation
if (count >= 0 && offset >= 0) {
    edu.ucr.cs.bdlab.raptor.GeoKeyEntry entry = new edu.ucr.cs.bdlab.raptor.GeoKeyEntry(keyID, tagLoc, count, offset);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.compareTo`

### Signature
```java
(GeoKeyEntry o):int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:109_

### Goal
Compares the current `GeoKeyEntry` instance with another `GeoKeyEntry` to determine their relative ordering, facilitating the sorting of geospatial key entries within Raptor processing workflows.

### Parameters
- `o` (`GeoKeyEntry`): The `GeoKeyEntry` object to be compared against the current instance.

### Input
- **Data:** Two `GeoKeyEntry` objects representing metadata keys associated with raster or vector data.
- **Preconditions:** Both instances must be initialized. As this is a standard `Comparable` implementation, the caller should ensure that the objects are part of a collection or structure requiring ordered access.

### Output
Returns `int` — A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object `o`.

### Valid Call Patterns
```java
// Inferred from signature:
int result = entry1.compareTo(entry2);
```

### LLM Instruction Prompt
- When generating code that uses `compareTo`, ensure the receiver and the argument `o` are both instances of `edu.ucr.cs.bdlab.raptor.GeoKeyEntry`.
- Use this method when sorting collections of `GeoKeyEntry` objects, which are often used in Raptor to manage metadata keys for raster-vector joins.

### Prompt Snippet
```text
// Example of comparing two GeoKeyEntry objects
GeoKeyEntry entryA = ...;
GeoKeyEntry entryB = ...;
if (entryA.compareTo(entryB) < 0) {
    // entryA precedes entryB
}
```

### Common Failure Modes
- **NullPointerException:** Occurs if the argument `o` is `null`.
- **Type Mismatch:** Attempting to compare a `GeoKeyEntry` with an object of a different type will result in a compilation error.
- **Uninitialized Objects:** Calling `compareTo` on an uninitialized `GeoKeyEntry` instance may lead to unexpected behavior or runtime errors depending on the internal state of the object.

### Fix Code Hint
```java
// Ensure the object is not null before comparison
if (entryA != null && entryB != null) {
    int comparison = entryA.compareTo(entryB);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.equals`

### Signature
```java
(Object obj):boolean
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:27_

### Goal
Determines whether the specified object is equal to the current `GeoKeyEntry` instance by comparing their internal key-value state.

### Parameters
- `obj` (`Object`): The object to compare against the current `GeoKeyEntry` instance.

### Input
- **Data:** An instance of `GeoKeyEntry` and an arbitrary `Object`.
- **Preconditions:** The `GeoKeyEntry` object must be initialized. No specific file formats are required as this is an in-memory comparison.

### Output
Returns `boolean` — `true` if the provided object is an instance of `GeoKeyEntry` and contains equivalent key and value data; `false` otherwise.

### Valid Call Patterns
```java
// Inferred from signature:
GeoKeyEntry entry1 = new GeoKeyEntry(key, value);
GeoKeyEntry entry2 = new GeoKeyEntry(key, value);
boolean isEqual = entry1.equals(entry2);
```

### LLM Instruction Prompt
- When comparing `GeoKeyEntry` objects, always use the `.equals()` method to ensure structural equality rather than reference equality.
- Ensure the input `obj` is checked for nullability before calling this method to avoid `NullPointerException` if the context does not guarantee non-null inputs.

### Prompt Snippet
```text
// Example of using GeoKeyEntry.equals for validation
if (currentEntry.equals(targetEntry)) {
    // Handle matching GeoKeyEntry
}
```

### Common Failure Modes
- **Type Mismatch:** Passing an object that is not an instance of `GeoKeyEntry` will return `false` rather than throwing an exception.
- **Null Pointer:** Calling `.equals()` on a null `GeoKeyEntry` reference will result in a `NullPointerException`.
- **State Inconsistency:** If the `GeoKeyEntry` objects were created with different key or value types, the equality check will return `false`.

### Fix Code Hint
```java
// Always verify the object type before comparison if the source is unknown
if (obj instanceof GeoKeyEntry) {
    boolean match = myGeoKeyEntry.equals(obj);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.getCount`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:77_

### Goal
Retrieves the count of values associated with a specific GeoKey entry within a raster metadata structure.

### Parameters
_None._

### Input
The caller must have an initialized `GeoKeyEntry` object. This object is typically part of the metadata parsing process for raster files (such as GeoTIFF or HDF4) handled by the Raptor component. No specific file format is required as input to this method, as it operates on an in-memory object.

### Output
Returns `int` — The number of elements or values stored within the specific GeoKey entry.

### Valid Call Patterns
```java
// Inferred from signature:
int count = geoKeyEntry.getCount();
```

### LLM Instruction Prompt
- When generating code that uses `edu.ucr.cs.bdlab.raptor.GeoKeyEntry`, ensure the object is properly instantiated from the raster metadata parsing workflow.
- Use the `getCount()` method to determine the size of the entry before attempting to access or iterate over its associated data.

### Prompt Snippet
```text
// Accessing GeoKeyEntry count
int entrySize = myGeoKeyEntry.getCount();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `GeoKeyEntry` object has not been initialized or is null.
- **Invalid State**: Calling this method on a `GeoKeyEntry` that was not correctly populated during the raster metadata extraction process may return unexpected or default values.

### Fix Code Hint
```java
if (geoKeyEntry != null) {
    int count = geoKeyEntry.getCount();
    // Proceed with processing based on count
} else {
    // Handle uninitialized entry
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.getKeyID`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:69_

### Goal
Retrieves the unique integer identifier associated with a specific GeoKey entry within a raster metadata structure.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.GeoKeyEntry`. This object is typically populated during the parsing of raster metadata (such as GeoTIFF or HDF4 headers) within the Raptor framework.

### Output
Returns `int` — The unique numeric ID assigned to the GeoKey, which identifies the specific geospatial parameter (e.g., projection, datum, or unit definition) stored in the entry.

### Valid Call Patterns
```java
// Inferred from signature:
int keyID = geoKeyEntryInstance.getKeyID();
```

### LLM Instruction Prompt
- When generating code that uses `getKeyID`, ensure the `GeoKeyEntry` instance is properly initialized from a raster metadata source.
- Do not assume the returned integer corresponds to a specific standard (like EPSG) without verifying the context of the `GeoKeyEntry` source.

### Prompt Snippet
```text
// Accessing the ID of a GeoKeyEntry
int id = entry.getKeyID();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `GeoKeyEntry` instance has not been initialized or is null.
- **Invalid Context**: Calling this method on an entry that does not belong to a valid, parsed raster metadata object may result in unexpected or default integer values.

### Fix Code Hint
```java
if (geoKeyEntry != null) {
    int id = geoKeyEntry.getKeyID();
    // Process the ID
} else {
    // Handle uninitialized entry
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.getTiffTagLocation`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:73_

### Goal
Retrieves the TIFF tag location identifier associated with a specific GeoKey entry within a GeoTIFF metadata structure.

### Parameters
_None._

### Input
The caller must have an initialized `GeoKeyEntry` object, which is typically parsed from the metadata of a GeoTIFF file during raster processing workflows in the Raptor component.

### Output
Returns `int` — The integer identifier representing the TIFF tag location where the GeoKey data is stored.

### Valid Call Patterns
```java
// Inferred from signature:
int tagLocation = geoKeyEntry.getTiffTagLocation();
```

### LLM Instruction Prompt
- When generating code that uses `getTiffTagLocation`, ensure the `GeoKeyEntry` instance is already instantiated from a valid GeoTIFF metadata source.
- Do not assume the returned integer maps to a specific TIFF tag without verifying the context of the GeoTIFF specification (e.g., GeoKeyDirectoryTag).

### Prompt Snippet
```text
// Accessing the TIFF tag location from a GeoKeyEntry
int location = entry.getTiffTagLocation();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `GeoKeyEntry` object has not been properly initialized or populated from the raster metadata.
- **Invalid Metadata Context**: If the underlying GeoTIFF file is malformed or lacks standard GeoKey directory tags, the returned value may be undefined or represent an invalid tag location.

### Fix Code Hint
```java
// Ensure the GeoKeyEntry is not null before accessing its properties
if (geoKeyEntry != null) {
    int location = geoKeyEntry.getTiffTagLocation();
    // Proceed with processing
} else {
    throw new IllegalStateException("GeoKeyEntry is not initialized.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.getValueOffset`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:81_

### Goal
Retrieves the byte offset of the value associated with a specific GeoKey entry within a raster metadata structure.

### Parameters
_None._

### Input
The caller must have an initialized `GeoKeyEntry` object, typically obtained during the parsing of raster metadata (such as GeoTIFF tags). No specific file format is required as input to this method, as it operates on an in-memory object representation of a GeoKey.

### Output
Returns `int` — The offset value indicating the position of the data associated with this GeoKey within the underlying byte stream or metadata buffer.

### Valid Call Patterns
```java
// Inferred from signature:
int offset = geoKeyEntry.getValueOffset();
```

### LLM Instruction Prompt
- When working with `GeoKeyEntry` objects in the `edu.ucr.cs.bdlab.raptor` package, use `getValueOffset()` to determine the data location for a specific key.
- This method does not take arguments; ensure the `GeoKeyEntry` instance is fully initialized before invocation.

### Prompt Snippet
```text
// Accessing the value offset from a GeoKeyEntry instance
int offset = entry.getValueOffset();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `GeoKeyEntry` instance has not been properly instantiated or initialized from the raster metadata source.
- **Invalid State**: Calling this method on an empty or malformed `GeoKeyEntry` that does not contain valid offset information may return unexpected or default integer values.

### Fix Code Hint
```java
// Ensure the entry is not null before accessing the offset
if (geoKeyEntry != null) {
    int offset = geoKeyEntry.getValueOffset();
    // Proceed with processing using the retrieved offset
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.getValues`

### Signature
```java
():int[]
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:105_

### Goal
Retrieves the array of integer values associated with a specific GeoKey entry within a raster metadata structure.

### Parameters
_None._

### Input
The caller must have an initialized `GeoKeyEntry` object, typically obtained during the parsing of raster metadata (such as GeoTIFF tags). No external file inputs are required for this method call, as it operates on the in-memory state of the `GeoKeyEntry` instance.

### Output
Returns `int[]` — An array of integers representing the values stored in the GeoKey entry.

### Valid Call Patterns
```java
// Inferred from signature:
int[] values = geoKeyEntry.getValues();
```

### LLM Instruction Prompt
- Use this method only when interacting with `edu.ucr.cs.bdlab.raptor.GeoKeyEntry` objects.
- Ensure the `GeoKeyEntry` instance is fully populated from the raster source before calling this method to avoid empty or null-pointer results.
- The returned `int[]` should be treated as read-only unless the specific implementation context allows for modification.

### Prompt Snippet
```text
// Accessing GeoKey values from a GeoKeyEntry instance
int[] keyValues = entry.getValues();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `GeoKeyEntry` instance has not been properly initialized or if the underlying data structure is null.
- **Empty Array**: If the specific GeoKey entry contains no data, the method may return an empty array; ensure your logic handles zero-length arrays to prevent `ArrayIndexOutOfBoundsException`.
- **Type Mismatch**: This method specifically returns `int[]`. If the underlying GeoKey data is stored as a different type (e.g., double or string), this method will not provide the expected data; verify the GeoKey type before invocation.

### Fix Code Hint
```java
// Ensure the entry is not null before accessing values
if (geoKeyEntry != null) {
    int[] values = geoKeyEntry.getValues();
    if (values != null && values.length > 0) {
        // Process values
    }
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.hashCode`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:38_

### Goal
Computes a hash code value for a `GeoKeyEntry` object to support efficient storage and retrieval in hash-based collections within Raptor raster-vector processing workflows.

### Parameters
_None._

### Input
The caller must provide an initialized `GeoKeyEntry` instance. No specific file formats are required as this is an in-memory object operation.

### Output
Returns `int` — A hash code value derived from the internal state of the `GeoKeyEntry` instance, consistent with the `equals` contract for the class.

### Valid Call Patterns
```java
// Inferred from signature:
GeoKeyEntry entry = new GeoKeyEntry(...);
int hash = entry.hashCode();
```

### LLM Instruction Prompt
- When generating code that uses `GeoKeyEntry` in hash-based collections (e.g., `HashMap`, `HashSet`), ensure `hashCode()` is called to maintain consistency with the object's `equals` implementation.
- Do not assume the hash value is stable across different JVM executions or versions of the Beast library.

### Prompt Snippet
```text
// Example usage of GeoKeyEntry hashing
int entryHash = myGeoKeyEntry.hashCode();
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `hashCode()` on a null `GeoKeyEntry` reference.
- **Collection Inconsistency:** If the internal fields of `GeoKeyEntry` are modified after being added to a `HashSet` or as a key in a `HashMap`, the object will become unreachable, as the hash code will no longer match the bucket index.

### Fix Code Hint
```java
// Ensure the object is not null before calling hashCode
if (entry != null) {
    int h = entry.hashCode();
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.setCount`

### Signature
```java
(int count):void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:85_

### Goal
Sets the frequency or occurrence count associated with a specific geographic key entry within the Raptor raster processing metadata.

### Parameters
- `count` (`int`): The integer value representing the number of occurrences or the frequency count to be assigned to this `GeoKeyEntry`.

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.GeoKeyEntry`. This method is typically used during the construction or population of metadata objects that track raster-vector intersection statistics.

### Output
Returns `void` — updates the internal state of the `GeoKeyEntry` object.

### Valid Call Patterns
```java
// Inferred from signature:
GeoKeyEntry entry = new GeoKeyEntry();
entry.setCount(10);
```

### LLM Instruction Prompt
- When generating code that uses `GeoKeyEntry.setCount`, ensure the `GeoKeyEntry` object is already instantiated.
- Use this method to update the count metadata after processing raster-vector intersections or zonal statistics calculations.
- The `count` parameter must be a non-negative integer representing the frequency of the associated geographic key.

### Prompt Snippet
```text
// Example of setting the count for a GeoKeyEntry
GeoKeyEntry entry = new GeoKeyEntry();
entry.setCount(countValue);
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `GeoKeyEntry` instance has not been initialized before calling `setCount`.
- **Invalid Count**: Passing a negative integer may lead to logical errors in downstream zonal statistics or raster-vector join summaries, as counts typically represent non-negative frequencies.

### Fix Code Hint
```java
// Ensure the object is initialized and the count is valid
if (entry != null && count >= 0) {
    entry.setCount(count);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.setKeyID`

### Signature
```java
(int keyID):void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:90_

### Goal
Sets the unique identifier for a GeoKey entry within the Raptor raster metadata processing framework.

### Parameters
- `keyID` (`int`): The integer identifier to assign to the GeoKey entry, typically corresponding to specific TIFF or HDF4 tag IDs.

### Input
- **Data:** An instance of `edu.ucr.cs.bdlab.raptor.GeoKeyEntry`.
- **Preconditions:** The `GeoKeyEntry` object must be initialized before invoking this setter. This method is typically used during the parsing or construction of raster metadata structures from GeoTIFF or HDF4 files.

### Output
Returns `void` — This method performs an in-place update of the `GeoKeyEntry` object state.

### Valid Call Patterns
```java
// Inferred from signature:
GeoKeyEntry entry = new GeoKeyEntry();
entry.setKeyID(1024);
```

### LLM Instruction Prompt
- When generating code that uses `setKeyID`, ensure the receiver is an instance of `edu.ucr.cs.bdlab.raptor.GeoKeyEntry`.
- Use this method only when manually constructing or modifying raster metadata entries for Raptor-compatible datasets.
- Ensure the `keyID` provided is a valid integer representing a standard geospatial key identifier.

### Prompt Snippet
```text
// Example of setting a GeoKey ID in Raptor
GeoKeyEntry entry = new GeoKeyEntry();
entry.setKeyID(keyID);
```

### Common Failure Modes
- **NullPointerException:** Occurs if the `GeoKeyEntry` instance has not been instantiated prior to calling `setKeyID`.
- **Invalid Key Mapping:** Providing an arbitrary `keyID` that does not correspond to the expected schema of the raster file (e.g., GeoTIFF tags) may lead to metadata corruption or failure during subsequent raster-vector join operations.

### Fix Code Hint
```java
// Ensure the object is initialized
GeoKeyEntry entry = new GeoKeyEntry();
// Verify the keyID is within the expected range for the specific raster format
if (isValidKey(id)) {
    entry.setKeyID(id);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.setTiffTagLocation`

### Signature
```java
(int tagLoc):void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:95_

### Goal
Sets the location index of a TIFF tag within a `GeoKeyEntry` object to facilitate proper parsing of geospatial metadata from raster files.

### Parameters
- `tagLoc` (`int`): The integer index or offset representing the location of the TIFF tag associated with this GeoKey.

### Input
- **Data:** An initialized `GeoKeyEntry` object.
- **Preconditions:** The caller must have a valid `GeoKeyEntry` instance. The `tagLoc` value should correspond to a valid tag location index as defined by the GeoTIFF specification or the specific raster file structure being processed.

### Output
Returns `void` — This method updates the internal state of the `GeoKeyEntry` object and does not return a value.

### Valid Call Patterns
```java
// Inferred from signature:
GeoKeyEntry entry = new GeoKeyEntry();
entry.setTiffTagLocation(1024); // Example tag location index
```

### LLM Instruction Prompt
- When using `setTiffTagLocation`, ensure the `GeoKeyEntry` object is properly initialized.
- The `tagLoc` parameter must be a non-negative integer representing the specific TIFF tag location.
- This method is typically used during the manual construction or modification of GeoKey metadata for raster datasets.

### Prompt Snippet
```text
// Setting the TIFF tag location for a GeoKeyEntry
geoKeyEntry.setTiffTagLocation(tagIndex);
```

### Common Failure Modes
- **NullPointerException:** Occurs if the `GeoKeyEntry` instance has not been instantiated before calling the method.
- **Invalid Index:** Providing a negative integer or an index that does not map to a valid TIFF tag location may result in downstream errors when the raster metadata is read or processed by the Raptor engine.

### Fix Code Hint
```java
// Ensure the object is instantiated and the index is valid
if (geoKeyEntry != null && tagIndex >= 0) {
    geoKeyEntry.setTiffTagLocation(tagIndex);
} else {
    throw new IllegalArgumentException("Invalid GeoKeyEntry or tag location index.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.setValueOffset`

### Signature
```java
(int valueOffset):void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:100_

### Goal
Sets the byte offset for the value associated with this GeoKeyEntry within the underlying raster metadata structure.

### Parameters
- `valueOffset` (`int`): The integer byte offset indicating the position of the value in the associated data stream or buffer.

### Input
The caller must have an initialized `GeoKeyEntry` object. This method is typically used during the parsing or construction of GeoTIFF/HDF4 metadata structures where specific key values are stored at defined offsets within the file header or metadata blocks.

### Output
Returns `void` — This method performs an in-place update of the `GeoKeyEntry` state.

### Valid Call Patterns
```java
// Inferred from signature:
GeoKeyEntry entry = new GeoKeyEntry();
entry.setValueOffset(1024);
```

### LLM Instruction Prompt
- When generating code that uses `setValueOffset`, ensure the `GeoKeyEntry` instance is already instantiated.
- The `valueOffset` must be a non-negative integer representing a valid byte position within the raster metadata context.
- This method is a setter; it does not return a value and should be called to configure the entry before it is processed by the Raptor metadata reader.

### Prompt Snippet
```text
// Setting the value offset for a GeoKeyEntry
entry.setValueOffset(offsetValue);
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `GeoKeyEntry` instance has not been initialized before calling `setValueOffset`.
- **Invalid Offset**: Providing a negative integer may lead to undefined behavior or errors during subsequent metadata parsing if the underlying reader expects a valid memory or file pointer.
- **Context Mismatch**: Calling this on an entry that is not part of a valid GeoTIFF/HDF4 metadata sequence may result in logical errors when the system attempts to read the value at the specified offset.

### Fix Code Hint
```java
// Ensure the entry is initialized and the offset is valid
if (entry != null && offset >= 0) {
    entry.setValueOffset(offset);
} else {
    throw new IllegalArgumentException("Invalid GeoKeyEntry or negative offset");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoKeyEntry.toString`

### Signature
```java
():String
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoKeyEntry.java:46_

### Goal
Returns a string representation of a `GeoKeyEntry` object, typically used for debugging or logging metadata associated with raster geospatial keys.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.GeoKeyEntry`. No specific file formats or external data are required as input to this method, as it operates on the internal state of the object.

### Output
Returns `String` — A string representation of the `GeoKeyEntry` instance, containing its internal key data.

### Valid Call Patterns
```java
// Inferred from signature:
GeoKeyEntry entry = new GeoKeyEntry(...);
String info = entry.toString();
```

### LLM Instruction Prompt
- When generating code that uses `GeoKeyEntry.toString`, ensure the `GeoKeyEntry` object is properly instantiated.
- Use this method primarily for logging or diagnostic purposes to inspect the contents of a raster's geospatial key entry.
- Do not rely on the specific format of the returned string for programmatic parsing, as it is intended for human-readable representation.

### Prompt Snippet
```text
// Example of using GeoKeyEntry.toString for debugging
GeoKeyEntry entry = ...; // Obtained from raster metadata
System.out.println("GeoKeyEntry details: " + entry.toString());
```

### Common Failure Modes
- **NullPointerException**: Calling `toString()` on a null `GeoKeyEntry` reference. Ensure the object is initialized before invocation.
- **Uninitialized State**: If the `GeoKeyEntry` was created but not populated with valid key data, the returned string may reflect empty or default values, which might be misleading during debugging.

### Fix Code Hint
```java
// Ensure the object is not null before calling toString
if (entry != null) {
    logger.debug("Processing GeoKeyEntry: {}", entry.toString());
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffException`

### Signature
```java
(GeoTiffMetadata metadata,String msg,Throwable t):null
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffException.java:43_

### Goal
Provides a specialized exception class for handling errors encountered during the processing or parsing of GeoTIFF raster metadata within the Raptor framework.

### Parameters
- `metadata` (`GeoTiffMetadata`): The associated `GeoTiffMetadata` object that was being processed when the error occurred.
- `msg` (`String`): A descriptive error message explaining the nature of the failure.
- `t` (`Throwable`): The underlying cause of the exception (e.g., an `IOException` or `ParseException`).

### Input
- **Data:** Requires a valid `GeoTiffMetadata` instance and a `Throwable` cause.
- **Preconditions:** This exception is typically thrown during raster ingestion or metadata extraction phases in Raptor workflows when the GeoTIFF file is malformed, inaccessible, or incompatible with the expected pixel type.

### Output
Returns `null` — This is a constructor for an exception class; it does not return a value but rather initializes an error state to be thrown by the Raptor processing engine.

### Valid Call Patterns
```java
// Inferred from signature:
throw new edu.ucr.cs.bdlab.raptor.GeoTiffException(metadata, "Failed to parse GeoTIFF headers", cause);
```

### LLM Instruction Prompt
- When generating code that involves raster processing, use `edu.ucr.cs.bdlab.raptor.GeoTiffException` to wrap errors related to `GeoTiffMetadata` operations.
- Always provide the `GeoTiffMetadata` context object to assist in debugging spatial data ingestion issues.
- Ensure the `Throwable` cause is passed to maintain the stack trace for diagnostic purposes.

### Prompt Snippet
```text
// Example of throwing the exception during metadata validation
if (metadata == null) {
    throw new edu.ucr.cs.bdlab.raptor.GeoTiffException(null, "Metadata is null", new NullPointerException());
}
```

### Common Failure Modes
- **Null Metadata:** Passing a `null` metadata object if the exception is triggered before the object is successfully initialized.
- **Type Mismatch:** Attempting to process a GeoTIFF where the pixel type does not match the expected generic type (e.g., `[Int]` vs `[Float]`), leading to metadata parsing errors.
- **Missing File Access:** Triggering this exception when the underlying GeoTIFF file is missing or locked by another process.

### Fix Code Hint
```java
try {
    // Raster processing logic
} catch (Exception e) {
    throw new edu.ucr.cs.bdlab.raptor.GeoTiffException(currentMetadata, "Error processing raster tile", e);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffException.getMessage`

### Signature
```java
():String
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffException.java:70_

### Goal
Retrieves the descriptive error message associated with a `GeoTiffException` encountered during raster processing or metadata extraction within the Raptor framework.

### Parameters
_None._

### Input
The caller must have an instance of `edu.ucr.cs.bdlab.raptor.GeoTiffException` caught within a `try-catch` block. This exception is typically thrown when operations on GeoTIFF files fail due to invalid file formats, corrupted headers, or incompatible raster-vector configurations.

### Output
Returns `String` — A human-readable string detailing the specific cause of the GeoTIFF processing failure.

### Valid Call Patterns
```java
try {
    // Raptor raster processing operation
} catch (GeoTiffException e) {
    String errorMsg = e.getMessage();
    System.err.println("Raptor processing error: " + errorMsg);
}
```
*(Note: This call pattern is inferred from the API signature as no explicit test suite example was provided.)*

### LLM Instruction Prompt
- When handling raster data errors in Beast/Raptor, always catch `GeoTiffException` and use `getMessage()` to log the specific failure reason.
- Do not attempt to recover from a `GeoTiffException` without inspecting the message, as it often indicates underlying file corruption or CRS mismatch.

### Prompt Snippet
```text
// Example of handling Raptor GeoTIFF errors
catch (edu.ucr.cs.bdlab.raptor.GeoTiffException e) {
    String msg = e.getMessage();
    // ... handle error
}
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `getMessage()` on a null reference if the exception object was not properly initialized.
- **Contextual Misinterpretation:** Assuming the error is related to file I/O when it may actually be a CRS incompatibility issue; always verify the message content to distinguish between file corruption and projection alignment errors.
- **Silent Failure:** Ignoring the exception message, which prevents debugging of raster-vector join failures or invalid pixel type configurations.

### Fix Code Hint
```java
// Ensure the exception is caught and the message is logged for debugging
catch (edu.ucr.cs.bdlab.raptor.GeoTiffException e) {
    logger.error("Failed to process GeoTIFF: " + e.getMessage());
    throw e; 
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata`

### Signature
```java
(ITiffReader reader,TiffRaster raster):null
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:46_

### Goal
Initializes a metadata container for a GeoTIFF raster by associating a specific `ITiffReader` with a `TiffRaster` instance to facilitate raster-plus-vector processing within the Raptor framework.

### Parameters
- `reader` (`ITiffReader`): The active TIFF reader instance used to access the underlying GeoTIFF file stream.
- `raster` (`TiffRaster`): The `TiffRaster` object representing the loaded raster data structure to be associated with the metadata.

### Input
- **Data:** Requires an initialized `ITiffReader` and a `TiffRaster` object.
- **Preconditions:** The `TiffRaster` must be compatible with the provided `ITiffReader`. Ensure that the raster data has been correctly loaded into the Raptor environment before instantiation.

### Output
Returns `null` — This constructor initializes the internal state of the `GeoTiffMetadata` object; it does not return a value.

### Valid Call Patterns
```java
// Inferred from signature:
new edu.ucr.cs.bdlab.raptor.GeoTiffMetadata(reader, raster);
```

### LLM Instruction Prompt
- When using `GeoTiffMetadata`, ensure that both the `ITiffReader` and `TiffRaster` are non-null and properly initialized. This class is intended for internal Raptor metadata management; do not attempt to use the return value as it is `null`.

### Prompt Snippet
```text
// Example usage of GeoTiffMetadata
ITiffReader reader = ...; // Initialized reader
TiffRaster raster = ...;  // Initialized raster
new edu.ucr.cs.bdlab.raptor.GeoTiffMetadata(reader, raster);
```

### Common Failure Modes
- **NullPointerException:** Occurs if either the `reader` or `raster` arguments are passed as `null`.
- **Incompatibility:** If the `TiffRaster` does not correspond to the file accessed by the `ITiffReader`, subsequent raster operations may fail or produce incorrect metadata results.
- **Initialization Order:** Attempting to use the metadata object before the underlying raster data is fully loaded or indexed by the Raptor system.

### Fix Code Hint
```java
// Ensure objects are validated before instantiation
if (reader != null && raster != null) {
    GeoTiffMetadata meta = new GeoTiffMetadata(reader, raster);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata.getGeoKey`

### Signature
```java
(final int keyID):String
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:227_

### Goal
Retrieves the string value associated with a specific GeoTIFF metadata key identifier from the parsed raster metadata.

### Parameters
- `keyID` (`final int`): The integer identifier corresponding to a specific GeoTIFF GeoKey (e.g., projection parameters or model transformation keys).

### Input
The caller must have an initialized `GeoTiffMetadata` object that has successfully parsed a GeoTIFF file. The `keyID` must correspond to a valid key defined in the GeoTIFF specification that is present in the loaded metadata.

### Output
Returns `String` — The value associated with the requested GeoKey, or null if the key is not found or not defined in the metadata.

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = new GeoTiffMetadata(tiffFilePath);
String value = metadata.getGeoKey(keyID);
```

### LLM Instruction Prompt
- When calling `getGeoKey`, ensure the `GeoTiffMetadata` instance is already populated from a valid GeoTIFF source.
- Use integer constants corresponding to standard GeoTIFF GeoKey IDs when passing the `keyID` parameter.
- Always handle potential null returns if the specific key is missing from the raster header.

### Prompt Snippet
```text
// Example of retrieving a GeoKey from metadata
String projectionInfo = geoTiffMetadata.getGeoKey(keyID);
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `GeoTiffMetadata` object was not properly initialized or the underlying file failed to load.
- **Invalid Key ID**: Passing an integer that does not map to a valid GeoKey will result in a null return or unexpected behavior depending on the internal implementation of the metadata map.
- **Missing Metadata**: If the GeoTIFF file lacks the specific GeoKey requested, the method will return null, which must be checked before further string processing.

### Fix Code Hint
```java
// Ensure metadata is loaded and check for null before usage
String result = metadata.getGeoKey(keyID);
if (result != null) {
    // Process the metadata value
} else {
    // Handle missing key case
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata.getGeoKeyDirectoryVersion`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:195_

### Goal
Retrieves the version number of the GeoKey directory structure from the associated GeoTIFF metadata.

### Parameters
_None._

### Input
The caller must have an initialized `GeoTiffMetadata` object. This object is typically populated by parsing a GeoTIFF file header. Ensure the GeoTIFF file is accessible and follows standard GeoTIFF specifications, as the metadata extraction relies on the internal structure of the TIFF tags.

### Output
Returns `int` — The version number of the GeoKey directory, which indicates the revision of the GeoTIFF specification used to encode the spatial reference information.

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = new GeoTiffMetadata(tiffFilePath);
int version = metadata.getGeoKeyDirectoryVersion();
```

### LLM Instruction Prompt
- Use this method only when working with `edu.ucr.cs.bdlab.raptor.GeoTiffMetadata` instances.
- Do not pass any arguments to this method.
- Ensure the `GeoTiffMetadata` object has been successfully initialized from a valid GeoTIFF source before calling this method to avoid null pointer or uninitialized state exceptions.

### Prompt Snippet
```text
// Example of accessing GeoKey directory version
int version = geoTiffMetadata.getGeoKeyDirectoryVersion();
```

### Common Failure Modes
- **Uninitialized Metadata:** Calling this method on a `GeoTiffMetadata` object that was not properly loaded from a file will result in an error or invalid return value.
- **Invalid File Format:** If the underlying file is not a valid GeoTIFF or lacks the required GeoKey directory tags, the metadata extraction may fail or return default/zero values.
- **Compatibility:** Ensure the project environment is configured with `beast-spark` dependencies, as this class is part of the Raptor module.

### Fix Code Hint
```java
// Ensure the metadata object is fully loaded before access
if (metadata != null) {
    int version = metadata.getGeoKeyDirectoryVersion();
    System.out.println("GeoKey Directory Version: " + version);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata.getGeoKeyMinorRevision`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:213_

### Goal
Retrieves the minor revision number of the GeoTIFF GeoKey directory associated with the metadata object.

### Parameters
_None._

### Input
The caller must have an initialized `edu.ucr.cs.bdlab.raptor.GeoTiffMetadata` object. This object is typically populated by parsing a GeoTIFF file header. No specific file format conversion is required, but the metadata must be successfully loaded from a valid GeoTIFF source.

### Output
Returns `int` — The minor revision number of the GeoKey directory as defined in the GeoTIFF specification.

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = new GeoTiffMetadata(tiffFilePath);
int minorRevision = metadata.getGeoKeyMinorRevision();
```

### LLM Instruction Prompt
- Use this method only when working with `edu.ucr.cs.bdlab.raptor.GeoTiffMetadata` instances.
- Do not assume the return value is a version number for the entire file; it specifically refers to the GeoKey directory revision.
- This method takes no arguments and returns a primitive `int`.

### Prompt Snippet
```text
// Accessing GeoTIFF metadata minor revision
int revision = metadata.getGeoKeyMinorRevision();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `GeoTiffMetadata` object was not properly initialized or if the underlying GeoTIFF header parsing failed.
- **Invalid Metadata State**: If the GeoTIFF file is malformed or lacks a valid GeoKey directory, the returned value may be undefined or represent a default state (e.g., 0). Ensure the file is a valid GeoTIFF before accessing metadata.

### Fix Code Hint
```java
// Ensure the metadata object is not null before calling
if (metadata != null) {
    int revision = metadata.getGeoKeyMinorRevision();
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata.getGeoKeyRevision`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:204_

### Goal
Retrieves the revision number of the GeoKey directory associated with the GeoTIFF metadata to ensure compatibility with GeoTIFF specification versions.

### Parameters
_None._

### Input
The caller must have an initialized `GeoTiffMetadata` object derived from a valid GeoTIFF file. No specific file format conversion is required, but the metadata must be successfully parsed from the source raster file before this method is invoked.

### Output
Returns `int` — The revision number of the GeoKey directory, which indicates the version of the GeoTIFF specification used to encode the spatial reference information.

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = new GeoTiffMetadata(tiffFilePath);
int revision = metadata.getGeoKeyRevision();
```

### LLM Instruction Prompt
- Use this method only when inspecting the internal structure of a GeoTIFF file's metadata.
- Ensure the `GeoTiffMetadata` instance is fully initialized before calling this method to avoid null pointer exceptions or uninitialized state errors.
- This method takes no arguments and returns a primitive integer.

### Prompt Snippet
```text
// Accessing GeoTIFF metadata revision
int revision = geoTiffMetadata.getGeoKeyRevision();
```

### Common Failure Modes
- **Uninitialized Metadata:** Calling this method on a `GeoTiffMetadata` object that failed to load or parse the source GeoTIFF file will result in an error or invalid return value.
- **Non-GeoTIFF Input:** If the underlying file is not a valid GeoTIFF, the metadata object may not contain a valid GeoKey directory, potentially leading to unexpected behavior.
- **Missing File Access:** Ensure the file path provided to the `GeoTiffMetadata` constructor is accessible and the process has sufficient read permissions.

### Fix Code Hint
```java
// Ensure the metadata object is valid before accessing properties
if (metadata != null) {
    int revision = metadata.getGeoKeyRevision();
    System.out.println("GeoKey Revision: " + revision);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata.getModelPixelScales`

### Signature
```java
():PixelScale
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:332_

### Goal
Retrieves the ModelPixelScaleTag values from a GeoTIFF metadata object, which define the scale factors for mapping pixel coordinates to model space coordinates.

### Parameters
_None._

### Input
The caller must have an initialized `GeoTiffMetadata` object derived from a valid GeoTIFF file. The GeoTIFF must contain valid GeoKey directory tags (specifically the ModelPixelScaleTag) to return meaningful scale data.

### Output
Returns `PixelScale` — An object containing the scale factors (typically representing the size of a pixel in the model coordinate system) extracted from the GeoTIFF metadata.

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = new GeoTiffMetadata(tiffPath);
PixelScale scale = metadata.getModelPixelScales();
```

### LLM Instruction Prompt
- When using `getModelPixelScales`, ensure the `GeoTiffMetadata` instance has been successfully parsed from a GeoTIFF file.
- Do not assume the returned `PixelScale` object is non-null; check for nullability if the GeoTIFF lacks the specific ModelPixelScaleTag.
- Use this method when performing coordinate transformations or calculating spatial resolution for raster-vector operations.

### Prompt Snippet
```text
// Accessing GeoTIFF pixel scale metadata
PixelScale scale = geoTiffMetadata.getModelPixelScales();
```

### Common Failure Modes
- **Null Metadata:** Calling this on an uninitialized or improperly loaded `GeoTiffMetadata` object will result in a `NullPointerException`.
- **Missing Tags:** If the underlying GeoTIFF file does not contain the ModelPixelScaleTag, the method may return `null` or an empty `PixelScale` object, leading to errors in downstream spatial calculations.
- **Coordinate Mismatch:** Using these scales without verifying that the raster and vector datasets share the same CRS can lead to incorrect spatial joins or alignment issues.

### Fix Code Hint
```java
// Ensure metadata is loaded and check for null before accessing scales
if (metadata != null) {
    PixelScale scale = metadata.getModelPixelScales();
    if (scale != null) {
        // Proceed with spatial calculations
    }
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata.getModelTiePoints`

### Signature
```java
():TiePoint[]
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:341_

### Goal
Retrieves the array of GeoTIFF ModelTiepointTag values from the metadata, which define the mapping between raster pixel coordinates and geographic or projected coordinate system locations.

### Parameters
_None._

### Input
The caller must have an initialized `GeoTiffMetadata` object derived from a valid GeoTIFF file. The file must contain valid GeoTIFF tags; if the ModelTiepointTag is missing or malformed in the source file, the behavior depends on the underlying GeoTIFF reader implementation.

### Output
Returns `TiePoint[]` — An array of `TiePoint` objects, where each object represents a tie point mapping a pixel (I, J, K) to a model coordinate (X, Y, Z).

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = new GeoTiffMetadata(tiffFilePath);
TiePoint[] tiePoints = metadata.getModelTiePoints();
```

### LLM Instruction Prompt
- Use this method only when working with `edu.ucr.cs.bdlab.raptor.GeoTiffMetadata` instances.
- Do not assume the returned array is non-null; check for null or empty arrays if the metadata source does not contain tie point information.
- Ensure the `GeoTiffMetadata` object is fully initialized before calling this method.

### Prompt Snippet
```text
// Accessing tie points from GeoTiffMetadata
TiePoint[] points = metadata.getModelTiePoints();
```

### Common Failure Modes
- **Null Metadata:** Calling this on an uninitialized or null `GeoTiffMetadata` object will result in a `NullPointerException`.
- **Missing Tags:** If the underlying GeoTIFF file lacks the ModelTiepointTag, the method may return an empty array or null, leading to potential index errors if not handled.
- **Coordinate Mismatch:** Relying on tie points without verifying that the raster and vector data share the same CRS can lead to spatial misalignment during `raptorJoin` operations.

### Fix Code Hint
```java
TiePoint[] tiePoints = metadata.getModelTiePoints();
if (tiePoints != null && tiePoints.length > 0) {
    // Process tie points
} else {
    // Handle case where no tie points are defined
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata.getModelTransformation`

### Signature
```java
():AffineTransform
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:316_

### Goal
Retrieves the affine transformation matrix associated with a GeoTIFF file, which maps pixel coordinates (column, row) to projected or geographic coordinates (x, y).

### Parameters
_None._

### Input
The caller must have an initialized `GeoTiffMetadata` object derived from a valid GeoTIFF file. The metadata must be successfully parsed from the raster source before calling this method.

### Output
Returns `AffineTransform` — A standard Java `java.awt.geom.AffineTransform` object representing the transformation from image space to the coordinate reference system (CRS) defined in the GeoTIFF metadata.

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = new GeoTiffMetadata(tiffPath);
AffineTransform transform = metadata.getModelTransformation();
```

### LLM Instruction Prompt
- When generating code that uses `getModelTransformation`, ensure the `GeoTiffMetadata` instance is already instantiated and populated from a valid GeoTIFF source.
- Use the returned `AffineTransform` to perform coordinate conversions between pixel space and map space.
- Do not assume the transformation is identity; always use the returned object for spatial calculations.

### Prompt Snippet
```text
// Accessing the transformation matrix from GeoTIFF metadata
AffineTransform transform = geoTiffMetadata.getModelTransformation();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `GeoTiffMetadata` object was not properly initialized or if the underlying GeoTIFF file lacks valid transformation tags (e.g., ModelTiepointTag or ModelPixelScaleTag).
- **Coordinate Mismatch**: If the raster and vector data are in different coordinate systems, the `AffineTransform` will map to the wrong spatial location. Ensure both datasets are aligned or reprojected before use.

### Fix Code Hint
```java
// Ensure metadata is valid before accessing the transform
if (metadata != null) {
    AffineTransform transform = metadata.getModelTransformation();
    // Proceed with coordinate mapping
} else {
    throw new IllegalStateException("GeoTiffMetadata not initialized.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata.hasModelTrasformation`

### Signature
```java
():boolean
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:360_

### Goal
Determines whether the associated GeoTIFF metadata contains a valid model transformation matrix, which is required for mapping pixel coordinates to geographic coordinates.

### Parameters
_None._

### Input
The caller must have an initialized `GeoTiffMetadata` object derived from a valid GeoTIFF file. The metadata must be successfully parsed from the raster source before invoking this method.

### Output
Returns `boolean` — `true` if a model transformation (such as a ModelTiepointTag or ModelPixelScaleTag) is present and valid in the GeoTIFF metadata; `false` otherwise.

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = new GeoTiffMetadata(tiffPath);
if (metadata.hasModelTrasformation()) {
    // Proceed with coordinate transformations
}
```

### LLM Instruction Prompt
- When generating code that performs spatial operations on GeoTIFFs, always check `hasModelTrasformation()` before attempting to map pixel-space coordinates to geographic coordinates.
- Do not assume a GeoTIFF contains spatial reference information; use this method to validate the presence of transformation metadata.

### Prompt Snippet
```text
// Check if the GeoTIFF metadata supports spatial transformation
if (metadata.hasModelTrasformation()) {
    // Perform spatial operations
}
```

### Common Failure Modes
- **Null Metadata:** Calling this method on an uninitialized or null `GeoTiffMetadata` instance will result in a `NullPointerException`.
- **Missing Spatial Reference:** If this returns `false`, subsequent attempts to perform `raptorJoin` or coordinate reprojection may fail or produce incorrect results because the raster lacks the necessary georeferencing information.
- **Incompatible Formats:** Ensure the input file is a valid GeoTIFF; standard TIFF files without GeoTIFF tags will return `false`.

### Fix Code Hint
```java
// Ensure metadata is loaded and validated before use
if (metadata != null && metadata.hasModelTrasformation()) {
    // Safe to proceed with spatial operations
} else {
    throw new RuntimeException("Raster metadata missing required model transformation tags.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata.hasPixelScales`

### Signature
```java
():boolean
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:369_

### Goal
Checks whether the GeoTIFF metadata contains explicit pixel scale information (ModelPixelScaleTag), which is used to define the spatial resolution of the raster data.

### Parameters
_None._

### Input
The caller must have an initialized `GeoTiffMetadata` object derived from a valid GeoTIFF file. The metadata must be successfully parsed from the raster source before invoking this method.

### Output
Returns `boolean` — `true` if the GeoTIFF file contains a `ModelPixelScaleTag` defining the pixel dimensions in the model coordinate system; `false` otherwise.

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = new GeoTiffMetadata(tiffPath);
if (metadata.hasPixelScales()) {
    // Proceed with spatial resolution-dependent logic
}
```

### LLM Instruction Prompt
- When generating code that processes GeoTIFFs using `edu.ucr.cs.bdlab.raptor.GeoTiffMetadata`, use `hasPixelScales()` to verify the presence of spatial resolution tags before attempting to access pixel scale values.
- This method is a predicate; use it in conditional blocks to ensure the metadata is sufficient for coordinate transformations.

### Prompt Snippet
```text
// Check for pixel scale metadata before processing
if (geoTiffMetadata.hasPixelScales()) {
    // Access pixel scale properties
}
```

### Common Failure Modes
- **Null Metadata:** Calling this method on an uninitialized or null `GeoTiffMetadata` object will result in a `NullPointerException`.
- **Missing Tags:** If the GeoTIFF is malformed or lacks standard GeoTIFF tags, this will return `false`, potentially causing downstream logic that expects pixel scales to fail or produce incorrect results.
- **Type Mismatch:** Ensure the `GeoTiffMetadata` instance corresponds to a valid GeoTIFF file; HDF4 or other raster formats may not populate this specific tag in the same manner.

### Fix Code Hint
```java
// Ensure metadata is initialized before checking
if (metadata != null && metadata.hasPixelScales()) {
    // Safe to proceed
} else {
    // Handle case where pixel scale information is missing
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata.hasTiePoints`

### Signature
```java
():boolean
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata.java:350_

### Goal
Determines whether the associated GeoTIFF metadata contains tie-point information, which is used to define the spatial transformation between pixel coordinates and geographic coordinates.

### Parameters
_None._

### Input
The caller must have an initialized `GeoTiffMetadata` object. This object is typically populated by parsing a GeoTIFF file header. No specific file format is required as an argument, as the state is internal to the `GeoTiffMetadata` instance.

### Output
Returns `boolean` — `true` if the GeoTIFF metadata contains valid tie-point tags (ModelTiepointTag), indicating that the raster has georeferencing information defined by tie points; `false` otherwise.

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = new GeoTiffMetadata(tiffFilePath);
boolean hasTiePoints = metadata.hasTiePoints();
```

### LLM Instruction Prompt
- When generating code that uses `hasTiePoints`, ensure the `GeoTiffMetadata` object has been successfully initialized from a valid GeoTIFF source.
- Use this method to verify georeferencing availability before attempting to perform spatial operations like `raptorJoin` or coordinate transformations.

### Prompt Snippet
```text
// Check if the GeoTIFF has tie points before proceeding with spatial operations
if (metadata.hasTiePoints()) {
    // Proceed with georeferenced raster processing
}
```

### Common Failure Modes
- **Null Pointer Exception:** Calling this method on an uninitialized or null `GeoTiffMetadata` instance.
- **Misinterpretation:** Assuming `false` means the raster is not georeferenced; the raster might still be georeferenced via other tags (e.g., ModelPixelScaleTag or ModelTransformationTag). Always check other metadata properties if `hasTiePoints` returns `false`.

### Fix Code Hint
```java
// Ensure the metadata object is not null before checking
if (metadata != null && metadata.hasTiePoints()) {
    // Logic for tie-point based georeferencing
} else {
    // Fallback or error handling for non-tie-point georeferencing
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter`

### Signature
```java
(Hints hints):null
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata2CRSAdapter.java:140_

### Goal
Adapts GeoTIFF metadata into a Coordinate Reference System (CRS) representation using the provided configuration hints.

### Parameters
- `hints` (`Hints`): A configuration object containing settings or parameters required to interpret the GeoTIFF metadata and resolve the target CRS.

### Input
The caller must provide a `Hints` object. This adapter assumes the input is derived from GeoTIFF metadata structures within the Raptor framework. Preconditions include ensuring that the underlying GeoTIFF metadata is accessible and that the `Hints` object is correctly populated with necessary environment or projection parameters.

### Output
Returns `null` — The method signature indicates a `null` return type, suggesting that the adapter likely performs internal state updates or registration of the CRS within the provided `Hints` context rather than returning a new object.

### Valid Call Patterns
```java
// Inferred from signature:
Hints myHints = new Hints();
new edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter(myHints);
```

### LLM Instruction Prompt
- When using `GeoTiffMetadata2CRSAdapter`, always instantiate it with a valid `Hints` object.
- Note that this API returns `null`; do not attempt to assign the result of the constructor to a variable expecting a CRS object.
- Ensure the `Hints` object is configured with relevant spatial parameters before passing it to the constructor.

### Prompt Snippet
```text
// Example of initializing the adapter
Hints hints = new Hints();
// ... populate hints with necessary metadata ...
new edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter(hints);
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `hints` parameter is null.
- **Invalid Metadata**: Failure to resolve the CRS if the `Hints` object lacks the necessary keys or if the underlying GeoTIFF metadata is malformed or missing required spatial tags.
- **Compatibility Issues**: If the CRS defined in the metadata is incompatible with the target spatial operations, subsequent `raptorJoin` or spatial transformations may fail. Ensure coordinate systems are aligned before processing.

### Fix Code Hint
```java
// Ensure Hints is initialized and populated before passing to the adapter
Hints hints = new Hints();
hints.put("some_key", "some_value"); // Populate with required metadata keys
new edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter(hints);
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter.createCoordinateSystem`

### Signature
```java
(final GeoTiffMetadata metadata):CoordinateReferenceSystem
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata2CRSAdapter.java:169_

### Goal
Extracts and constructs a `CoordinateReferenceSystem` object from the provided `GeoTiffMetadata` to facilitate spatial alignment in Raptor raster-vector operations.

### Parameters
- `metadata` (`final GeoTiffMetadata`): The metadata object extracted from a GeoTIFF file containing the spatial reference information required to define the coordinate system.

### Input
- **Data:** A `GeoTiffMetadata` instance.
- **Preconditions:** The `GeoTiffMetadata` must contain valid spatial reference tags (e.g., EPSG codes or WKT strings) extracted from the source GeoTIFF file.
- **Compatibility:** Ensure the resulting `CoordinateReferenceSystem` is compatible with any vector datasets intended for use in subsequent `raptorJoin` operations, as raster and vector data must share the same CRS.

### Output
Returns `CoordinateReferenceSystem` — A standard geospatial coordinate reference system object representing the spatial projection and datum defined in the input metadata.

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = ...; // Obtained from a GeoTIFF source
CoordinateReferenceSystem crs = edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter.createCoordinateSystem(metadata);
```

### LLM Instruction Prompt
- When generating code that uses this API, ensure the `GeoTiffMetadata` object is properly initialized from a valid GeoTIFF source.
- Always assign the returned `CoordinateReferenceSystem` to a variable for use in subsequent spatial operations, such as reprojection or spatial joins.

### Prompt Snippet
```text
// Use the GeoTiffMetadata2CRSAdapter to resolve the CRS from metadata
CoordinateReferenceSystem crs = GeoTiffMetadata2CRSAdapter.createCoordinateSystem(metadata);
```

### Common Failure Modes
- **Null Metadata:** Passing a null or uninitialized `GeoTiffMetadata` object will result in a `NullPointerException`.
- **Missing CRS Tags:** If the source GeoTIFF lacks valid spatial reference information, the adapter may return an invalid or default CRS, leading to incorrect spatial alignment during `raptorJoin`.
- **CRS Mismatch:** Failing to align the CRS of the raster with the vector dataset before performing a `raptorJoin` will cause spatial processing errors. Use `reproject` or `reshape` to align datasets if they differ.

### Fix Code Hint
```java
// Ensure metadata is valid before conversion
if (metadata != null) {
    CoordinateReferenceSystem crs = GeoTiffMetadata2CRSAdapter.createCoordinateSystem(metadata);
    // Proceed with spatial operations using the resolved crs
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter.getRasterToModel`

### Signature
```java
(final GeoTiffMetadata metadata):MathTransform
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata2CRSAdapter.java:428_

### Goal
Extracts the coordinate transformation matrix from GeoTIFF metadata to map raster pixel coordinates to their corresponding model (geographic or projected) coordinate system.

### Parameters
- `metadata` (`final GeoTiffMetadata`): The metadata object extracted from a GeoTIFF file, containing the georeferencing information (e.g., ModelTiepointTag, ModelPixelScaleTag, or ModelTransformationTag).

### Input
- **Data:** A `GeoTiffMetadata` object derived from a valid GeoTIFF file.
- **Preconditions:** The input `GeoTiffMetadata` must contain valid georeferencing tags. If the metadata is incomplete or lacks spatial reference information, the resulting `MathTransform` may be identity-based or invalid for spatial operations.

### Output
Returns `MathTransform` — A GeoTools `MathTransform` object that defines the affine transformation required to convert pixel/line coordinates (raster space) into the target CRS (model space).

### Valid Call Patterns
```java
// Inferred from signature
GeoTiffMetadata metadata = ...; // Obtained from GeoTIFF reader
MathTransform transform = edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter.getRasterToModel(metadata);
```

### LLM Instruction Prompt
- Use `edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter.getRasterToModel` to retrieve the transformation matrix when performing manual coordinate conversions or aligning raster data with vector features in Raptor workflows.
- Ensure the `GeoTiffMetadata` object is fully initialized from the source raster before passing it to this method.

### Prompt Snippet
```text
// Example usage of getRasterToModel
MathTransform rasterToModel = GeoTiffMetadata2CRSAdapter.getRasterToModel(tiffMetadata);
```

### Common Failure Modes
- **Null Metadata:** Passing a null or uninitialized `GeoTiffMetadata` object will result in a `NullPointerException`.
- **Missing Georeferencing:** If the GeoTIFF lacks standard georeferencing tags, the adapter may return a default transform that does not accurately represent the spatial location of the pixels.
- **CRS Mismatch:** Using the resulting `MathTransform` to join data with a vector dataset that uses a different CRS without prior reprojection will lead to incorrect spatial alignment during `raptorJoin`.

### Fix Code Hint
```java
// Ensure metadata is valid before transformation
if (metadata != null) {
    MathTransform transform = edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter.getRasterToModel(metadata);
    // Use transform for coordinate mapping
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter.getRasterType`

### Signature
```java
(GeoTiffMetadata metadata):PixelInCell
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/GeoTiffMetadata2CRSAdapter.java:550_

### Goal
Determines the raster registration type (pixel-in-cell or pixel-is-point) from the provided GeoTIFF metadata to ensure correct spatial alignment during Raptor processing.

### Parameters
- `metadata` (`GeoTiffMetadata`): The metadata object extracted from a GeoTIFF file, containing the raster's spatial reference and geotransform information.

### Input
- **Data:** A `GeoTiffMetadata` object initialized from a valid GeoTIFF file.
- **Preconditions:** The metadata must be successfully parsed from the source raster. Ensure the raster is compatible with the target vector dataset's coordinate system before performing downstream operations like `raptorJoin`.

### Output
Returns `PixelInCell` — An enumeration value indicating whether the raster coordinates represent the center of the pixel (pixel-is-point) or the entire area of the cell (pixel-in-cell).

### Valid Call Patterns
```java
// Inferred from signature:
GeoTiffMetadata metadata = ...; // Obtained from GeoTIFF source
PixelInCell type = edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter.getRasterType(metadata);
```

### LLM Instruction Prompt
- When generating code that uses this API, ensure the `GeoTiffMetadata` object is properly instantiated from a GeoTIFF source.
- Use the returned `PixelInCell` value to inform subsequent spatial operations, particularly when performing `raptorJoin` or coordinate reprojections, as misaligned registration types can lead to spatial offsets.

### Prompt Snippet
```text
// Example usage of getRasterType to check registration
GeoTiffMetadata meta = ...;
PixelInCell registration = edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter.getRasterType(meta);
```

### Common Failure Modes
- **Null Metadata:** Passing a null or uninitialized `GeoTiffMetadata` object will result in a `NullPointerException`.
- **Incompatible CRS:** If the raster and vector data have different coordinate systems, simply identifying the `PixelInCell` type is insufficient; you must use `reproject` or `reshape` to align the datasets before joining.
- **Invalid Metadata:** If the GeoTIFF file is malformed or lacks standard georeferencing tags, the metadata object may not contain sufficient information to determine the `PixelInCell` type.

### Fix Code Hint
```java
// Ensure metadata is valid before calling
if (metadata != null) {
    PixelInCell type = edu.ucr.cs.bdlab.raptor.GeoTiffMetadata2CRSAdapter.getRasterType(metadata);
    // Proceed with spatial logic
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Intersections.compute`

### Signature
```java
(Geometry[] geometries,long[] featureIDs,RasterMetadata metadata):void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Intersections.java:155_

### Goal
Computes the spatial intersections between a set of vector geometries and a raster dataset defined by its metadata to facilitate raster-vector overlay operations.

### Parameters
- `geometries` (`Geometry[]`): An array of JTS or Beast-compatible geometries representing the vector features to be intersected with the raster.
- `featureIDs` (`long[]`): An array of unique identifiers corresponding to each geometry in the `geometries` array.
- `metadata` (`RasterMetadata`): The metadata object describing the raster's spatial extent, resolution, and coordinate reference system.

### Input
- **Data:** Requires a set of vector geometries and their associated IDs, along with the `RasterMetadata` of the target raster.
- **Preconditions:** The vector geometries and the raster must share the same coordinate reference system (CRS). If they differ, the geometries must be reprojected to match the raster's CRS before calling this method.

### Output
Returns `void` — This method performs the intersection computation internally; results are typically used to drive subsequent raster-vector join or zonal statistics workflows within the Raptor framework.

### Valid Call Patterns
```java
// Inferred usage pattern based on signature
edu.ucr.cs.bdlab.raptor.Intersections.compute(geometries, featureIDs, metadata);
```

### LLM Instruction Prompt
- When calling `edu.ucr.cs.bdlab.raptor.Intersections.compute`, ensure that the `geometries` array and `featureIDs` array are of the same length.
- Verify that the `RasterMetadata` provided is initialized and matches the spatial domain of the input geometries.
- This method is a low-level utility; ensure that the calling context is prepared for the memory overhead associated with processing large geometry arrays.

### Prompt Snippet
```text
// Ensure geometries and featureIDs are aligned
Geometry[] geoms = ...;
long[] ids = ...;
RasterMetadata meta = ...;
edu.ucr.cs.bdlab.raptor.Intersections.compute(geoms, ids, meta);
```

### Common Failure Modes
- **CRS Mismatch:** If the vector geometries and raster metadata are in different coordinate systems, the intersection results will be spatially incorrect.
- **Array Length Mismatch:** Providing `geometries` and `featureIDs` arrays of different lengths will result in an `ArrayIndexOutOfBoundsException`.
- **Null Metadata:** Passing a null `RasterMetadata` object will cause a `NullPointerException` during the intersection calculation.

### Fix Code Hint
```java
// Ensure CRS alignment before calling
if (!geometry.getSRID().equals(metadata.getCRS())) {
    // Perform reprojection logic here
}
edu.ucr.cs.bdlab.raptor.Intersections.compute(geometries, featureIDs, metadata);
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Intersections.getFeatureID`

### Signature
```java
(int i):long
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Intersections.java:121_

### Goal
Retrieves the unique identifier associated with a specific feature index within a Raptor intersection result set.

### Parameters
- `i` (`int`): The zero-based index of the feature within the current intersection context.

### Input
- **Preconditions:** This method is intended for use within the context of a Raptor processing workflow where an intersection operation has been performed. The caller must ensure the index `i` is within the bounds of the features identified during the intersection process.

### Output
Returns `long` — The unique feature ID corresponding to the provided index.

### Valid Call Patterns
```java
// Inferred usage: Accessing a feature ID by index from an Intersections instance
long featureId = intersections.getFeatureID(i);
```

### LLM Instruction Prompt
- When generating code that uses `getFeatureID`, ensure the context is an instance of `edu.ucr.cs.bdlab.raptor.Intersections`.
- Always validate that the index `i` is non-negative and less than the total number of features in the intersection set to avoid `ArrayIndexOutOfBoundsException`.

### Prompt Snippet
```text
// Accessing feature ID from Raptor intersection
long id = intersections.getFeatureID(index);
```

### Common Failure Modes
- **IndexOutOfBoundsException:** Occurs if the provided integer `i` is negative or exceeds the number of features available in the intersection object.
- **NullPointerException:** Occurs if the `Intersections` object has not been initialized or is null.
- **Context Mismatch:** Calling this method outside of a valid Raptor join or intersection processing pipeline where the feature list has not been populated.

### Fix Code Hint
```java
// Ensure index is within bounds before retrieval
if (i >= 0 && i < intersections.size()) {
    long id = intersections.getFeatureID(i);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Intersections.getNumIntersections`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Intersections.java:81_

### Goal
Retrieves the total count of intersection events calculated within the current `Intersections` object context during a Raptor raster-vector processing operation.

### Parameters
_None._

### Input
The caller must ensure that an `Intersections` object has been initialized and populated through a prior Raptor-based spatial join or intersection analysis. No external file inputs are required for this method, as it operates on the internal state of the `Intersections` instance.

### Output
Returns `int` — The total number of intersections identified between the raster and vector datasets processed by the instance.

### Valid Call Patterns
```java
// Inferred from signature:
int count = intersectionsInstance.getNumIntersections();
```

### LLM Instruction Prompt
- When generating code that uses `getNumIntersections`, ensure the `Intersections` object has already been instantiated and populated via a Raptor processing workflow.
- Do not pass any arguments to this method, as it takes no parameters.
- Use the returned `int` value to verify the results of spatial join operations or to report metadata about the processed geometry intersections.

### Prompt Snippet
```text
// Ensure the Intersections object is populated before calling
int intersectionCount = intersectionsInstance.getNumIntersections();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `Intersections` object has not been initialized or is null.
- **Stale Data**: Calling this method before the underlying Raptor join or intersection logic has completed will return an incorrect count (typically 0).
- **Context Mismatch**: Attempting to use this method outside of a valid Raptor processing lifecycle where the `Intersections` state is maintained.

### Fix Code Hint
```java
// Ensure the object is properly initialized
Intersections intersections = new Intersections(...);
// Perform the necessary processing to populate the object
// ...
int count = intersections.getNumIntersections();
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Intersections.getPolygonIndex`

### Signature
```java
(int i):int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Intersections.java:117_

### Goal
Retrieves the index of a polygon associated with a specific intersection record within the Raptor processing workflow.

### Parameters
- `i` (`int`): The index of the intersection record to query.

### Input
- **Preconditions:** This method is intended for use within the context of `Raptor` raster-vector join operations. The caller must ensure that the `Intersections` object has been initialized and populated with valid intersection data resulting from a spatial join between raster tiles and vector features.

### Output
Returns `int` — The identifier or index of the polygon feature corresponding to the provided intersection record index.

### Valid Call Patterns
```java
// Inferred from signature:
int polygonIndex = edu.ucr.cs.bdlab.raptor.Intersections.getPolygonIndex(i);
```

### LLM Instruction Prompt
- When generating code that uses this method, ensure the context is within a `Raptor` processing loop where intersection data is available.
- Do not assume the index `i` is valid; verify that `i` is within the bounds of the current intersection set before calling.

### Prompt Snippet
```text
// Ensure the index i is within the bounds of the intersection results
if (i >= 0 && i < totalIntersections) {
    int polyIdx = edu.ucr.cs.bdlab.raptor.Intersections.getPolygonIndex(i);
}
```

### Common Failure Modes
- **IndexOutOfBoundsException:** Occurs if the provided integer `i` does not correspond to a valid record in the current intersection set.
- **NullPointerException:** Occurs if the `Intersections` context has not been properly initialized or is null during the execution of the Raptor join.
- **Context Mismatch:** Calling this method outside of a valid `raptorJoin` workflow where the `Intersections` state is not maintained.

### Fix Code Hint
```java
// Always validate the index against the size of the intersection collection
if (intersections != null && i < intersections.size()) {
    int polyIdx = edu.ucr.cs.bdlab.raptor.Intersections.getPolygonIndex(i);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Intersections.getTileID`

### Signature
```java
(int i):int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Intersections.java:125_

### Goal
Retrieves the unique identifier for a specific tile index within the Raptor raster processing framework.

### Parameters
- `i` (`int`): The index of the tile for which the identifier is requested.

### Input
- **Data:** Requires an integer index representing a position within a raster tile set.
- **Preconditions:** The index `i` must be within the valid range of tiles defined by the current raster dataset or spatial partition being processed by the Raptor component.

### Output
Returns `int` — The unique tile ID associated with the provided index.

### Valid Call Patterns
```java
// Inferred from signature:
int tileId = edu.ucr.cs.bdlab.raptor.Intersections.getTileID(index);
```

### LLM Instruction Prompt
- Use `edu.ucr.cs.bdlab.raptor.Intersections.getTileID(int i)` to map a sequential index to a specific tile identifier.
- Ensure the input `i` is a valid integer index corresponding to the raster data structure currently in scope.
- This method is a static utility; call it directly via the class name.

### Prompt Snippet
```text
// Example of retrieving a tile ID for a given index
int myTileId = edu.ucr.cs.bdlab.raptor.Intersections.getTileID(tileIndex);
```

### Common Failure Modes
- **IndexOutOfBounds:** Providing an index that does not correspond to a valid tile in the current raster dataset will result in an invalid or unexpected ID.
- **Context Mismatch:** Calling this method without an active or initialized raster context may lead to logical errors if the index is not relative to the expected dataset.

### Fix Code Hint
```java
// Ensure the index is validated against the total number of tiles before calling
if (index >= 0 && index < totalTiles) {
    int tileId = edu.ucr.cs.bdlab.raptor.Intersections.getTileID(index);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Intersections.getX1`

### Signature
```java
(int i):int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Intersections.java:90_

### Goal
Retrieves the starting X-coordinate (column index) for a specific intersection segment identified by the index `i` within the `Intersections` object.

### Parameters
- `i` (`int`): The index of the intersection segment to query.

### Input
The caller must provide an instance of `edu.ucr.cs.bdlab.raptor.Intersections` that has been initialized with intersection data between raster tiles and vector features. The index `i` must be within the valid range of stored intersections (0 to N-1).

### Output
Returns `int` — The starting X-coordinate (column) of the intersection segment at the specified index.

### Valid Call Patterns
```java
// Inferred from signature:
int x1 = intersections.getX1(i);
```

### LLM Instruction Prompt
- Use this method when processing the results of a `raptorJoin` to extract the spatial bounds of a specific intersection segment.
- Ensure the index `i` is validated against the total number of intersections available in the `Intersections` object to avoid `ArrayIndexOutOfBoundsException`.

### Prompt Snippet
```text
// Example of accessing intersection bounds
int x1 = intersections.getX1(i);
int x2 = intersections.getX2(i);
```

### Common Failure Modes
- `ArrayIndexOutOfBoundsException`: Occurs if the provided index `i` is negative or greater than or equal to the number of intersections stored in the object.
- Null Pointer Exception: Occurs if the `Intersections` object has not been properly initialized or is null.

### Fix Code Hint
```java
// Always check bounds before accessing
if (i >= 0 && i < intersections.size()) {
    int x1 = intersections.getX1(i);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Intersections.getX2`

### Signature
```java
(int i):int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Intersections.java:99_

### Goal
Retrieves the x-coordinate value at the specified index from an intersection data structure within the Raptor raster-vector processing engine.

### Parameters
- `i` (`int`): The index of the intersection point to retrieve.

### Input
The caller must ensure that the `Intersections` object has been initialized and populated with intersection data, typically resulting from a `raptorJoin` operation between raster tiles and vector features. The index `i` must be within the bounds of the internal intersection list.

### Output
Returns `int` — The x-coordinate value associated with the intersection at the provided index.

### Valid Call Patterns
```java
// Inferred from signature:
int xValue = edu.ucr.cs.bdlab.raptor.Intersections.getX2(i);
```

### LLM Instruction Prompt
- When generating code that uses `edu.ucr.cs.bdlab.raptor.Intersections.getX2`, ensure the index `i` is validated against the size of the intersection collection to prevent `ArrayIndexOutOfBoundsException`. This method is intended for low-level access to intersection geometry during custom raster-vector processing tasks.

### Prompt Snippet
```text
// Accessing intersection coordinate
int x = edu.ucr.cs.bdlab.raptor.Intersections.getX2(index);
```

### Common Failure Modes
- `ArrayIndexOutOfBoundsException`: Occurs if the provided index `i` is negative or greater than or equal to the number of intersections stored in the object.
- Null Pointer/Uninitialized State: Calling this method on an `Intersections` instance that has not been properly populated by a Raptor join operation.

### Fix Code Hint
```java
// Ensure index bounds checking before access
if (i >= 0 && i < intersections.size()) {
    int x = edu.ucr.cs.bdlab.raptor.Intersections.getX2(i);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Intersections.getY`

### Signature
```java
(int i):int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Intersections.java:108_

### Goal
Retrieves the Y-coordinate component associated with a specific intersection index within a Raptor processing operation.

### Parameters
- `i` (`int`): The index of the intersection point for which the Y-coordinate is requested.

### Input
- **Preconditions:** The caller must ensure that the `Intersections` object has been initialized and that the index `i` is within the valid range of computed intersections for the current raster-vector join operation.

### Output
Returns `int` — The Y-coordinate value corresponding to the intersection at the provided index.

### Valid Call Patterns
```java
// Inferred from signature:
int yCoord = edu.ucr.cs.bdlab.raptor.Intersections.getY(i);
```

### LLM Instruction Prompt
- When generating code that uses `getY`, ensure the index `i` is validated against the total number of intersections available in the `Intersections` object to prevent `ArrayIndexOutOfBoundsException`.

### Prompt Snippet
```text
// Ensure index i is within bounds before calling getY
if (i >= 0 && i < intersections.size()) {
    int y = edu.ucr.cs.bdlab.raptor.Intersections.getY(i);
}
```

### Common Failure Modes
- **IndexOutOfBoundsException:** Occurs if the provided index `i` is negative or exceeds the number of intersections stored in the object.
- **Uninitialized State:** Calling this method on an `Intersections` object that has not been populated by a `raptorJoin` or similar spatial operation will result in invalid or default data retrieval.

### Fix Code Hint
```java
// Always verify the intersection count before accessing specific indices
if (i < totalIntersections) {
    int y = edu.ucr.cs.bdlab.raptor.Intersections.getY(i);
} else {
    // Handle invalid index
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale`

### Signature
```java
(double scaleX,double scaleY,double scaleZ):null
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:52_

### Goal
Defines the scaling factors for raster pixel dimensions along the X, Y, and Z axes to support spatial transformations and coordinate alignment within the Raptor processing framework.

### Parameters
- `scaleX` (`double`): The scaling factor applied to the pixel width (X-axis).
- `scaleY` (`double`): The scaling factor applied to the pixel height (Y-axis).
- `scaleZ` (`double`): The scaling factor applied to the pixel depth or value magnitude (Z-axis).

### Input
The caller must provide three `double` values representing the desired scaling ratios. This constructor is typically used when configuring raster transformation parameters for operations that require spatial alignment or unit normalization between datasets.

### Output
Returns `null` — This is a constructor that initializes a `PixelScale` object; it does not return a value.

### Valid Call Patterns
```java
// Example inferred from signature
edu.ucr.cs.bdlab.raptor.PixelScale scale = new edu.ucr.cs.bdlab.raptor.PixelScale(1.0, 1.0, 1.0);
```

### LLM Instruction Prompt
- When generating code that uses `edu.ucr.cs.bdlab.raptor.PixelScale`, always instantiate it using the `new` keyword as it is a constructor.
- Ensure all three arguments are provided as `double` types.
- Use this class when preparing raster metadata or transformation configurations for Raptor-based spatial workflows.

### Prompt Snippet
```text
// Initialize pixel scaling for raster processing
edu.ucr.cs.bdlab.raptor.PixelScale pixelScale = new edu.ucr.cs.bdlab.raptor.PixelScale(scaleX, scaleY, scaleZ);
```

### Common Failure Modes
- **Type Mismatch:** Passing integer values instead of `double` may cause compilation errors; ensure values are explicitly cast or defined as doubles (e.g., `1.0`).
- **Invalid Scaling Factors:** Providing zero or negative values for scaling factors may lead to undefined behavior in subsequent raster processing or geometric transformations.
- **Contextual Misuse:** Attempting to use this object in a context where a return value is expected, as the constructor returns `null`.

### Fix Code Hint
```java
// Ensure explicit double literals for scaling parameters
edu.ucr.cs.bdlab.raptor.PixelScale scale = new edu.ucr.cs.bdlab.raptor.PixelScale(1.0, 1.0, 1.0);
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.equals`

### Signature
```java
(Object that):boolean
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:25_

### Goal
Determines whether the current `PixelScale` instance is semantically equivalent to another object by comparing their underlying scale properties.

### Parameters
- `that` (`Object`): The object to compare against the current `PixelScale` instance.

### Input
- **Data:** An instance of `edu.ucr.cs.bdlab.raptor.PixelScale`.
- **Preconditions:** The caller must ensure the `PixelScale` object is initialized. No specific file formats are required as this is an in-memory object comparison.

### Output
Returns `boolean` — `true` if the provided object is an instance of `PixelScale` and contains identical scale values; `false` otherwise.

### Valid Call Patterns
```java
// Inferred from signature:
PixelScale scale1 = new PixelScale(xScale, yScale);
PixelScale scale2 = new PixelScale(xScale, yScale);
boolean isEqual = scale1.equals(scale2);
```

### LLM Instruction Prompt
- When comparing two `PixelScale` objects in Beast/Raptor workflows, use the `.equals()` method to verify coordinate alignment before performing raster-vector joins or spatial operations. Do not use `==` for value comparison.

### Prompt Snippet
```text
// Check if two raster pixel scales are identical before processing
if (scaleA.equals(scaleB)) {
    // Proceed with aligned raster operations
}
```

### Common Failure Modes
- **Type Mismatch:** Passing an object that is not an instance of `PixelScale` will return `false` rather than throwing an exception.
- **Null Pointer:** Calling `.equals()` on a `null` `PixelScale` reference will result in a `NullPointerException`.
- **Precision Issues:** If the `PixelScale` values are derived from floating-point calculations, minor precision differences may cause `equals()` to return `false` even if the values are mathematically similar.

### Fix Code Hint
```java
// Ensure the object is not null before comparison
if (scale != null && scale.equals(otherScale)) {
    // Logic for matching scales
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.getScaleX`

### Signature
```java
():double
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:64_

### Goal
Retrieves the horizontal pixel scale (resolution) value from a `PixelScale` object, representing the width of a single pixel in the coordinate system units.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.PixelScale`. This object is typically derived from raster metadata (e.g., GeoTIFF or HDF4 headers) during the ingestion or processing phase of a Raptor workflow.

### Output
Returns `double` — The horizontal scale factor (ScaleX) of the raster data, representing the distance between adjacent pixels along the X-axis in the raster's coordinate reference system.

### Valid Call Patterns
```java
// Inferred from signature:
PixelScale scale = new PixelScale(scaleX, scaleY);
double xRes = scale.getScaleX();
```

### LLM Instruction Prompt
- When generating code that uses `getScaleX`, ensure the `PixelScale` object is already instantiated.
- Use this method when performing raster-to-vector alignment or calculating spatial extents for `raptorJoin` operations.
- Do not assume the scale is 1.0; always retrieve it dynamically from the raster metadata.

### Prompt Snippet
```text
// Retrieve the horizontal resolution to align raster with vector features
double xResolution = pixelScale.getScaleX();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `PixelScale` object has not been initialized or is null.
- **Invalid Raster Metadata**: If the underlying raster file (GeoTIFF/HDF4) contains malformed or missing georeferencing information, the `PixelScale` object may contain default or zero values, leading to incorrect spatial calculations.
- **Coordinate Mismatch**: Using the scale value without verifying that the raster and vector datasets share the same Coordinate Reference System (CRS), which can lead to spatial join inaccuracies.

### Fix Code Hint
```java
// Ensure the PixelScale is retrieved from valid raster metadata
PixelScale ps = rasterMetadata.getPixelScale();
if (ps != null) {
    double scaleX = ps.getScaleX();
    // Proceed with spatial calculations
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.getScaleY`

### Signature
```java
():double
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:72_

### Goal
Retrieves the vertical pixel scale factor (Y-axis resolution) from a `PixelScale` object, representing the height of a single pixel in the coordinate system units.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.PixelScale`. This object is typically derived from raster metadata (e.g., GeoTIFF or HDF4 headers) during the raster ingestion process in a Raptor workflow.

### Output
Returns `double` — The vertical scale value, representing the spatial dimension of a pixel along the Y-axis.

### Valid Call Patterns
```java
// Inferred from signature:
PixelScale scale = ...; // Obtained from raster metadata
double scaleY = scale.getScaleY();
```

### LLM Instruction Prompt
- When generating code that uses `getScaleY`, ensure the `PixelScale` object is properly initialized from a raster source.
- Use this method when performing spatial calculations or transformations that require pixel-to-coordinate unit conversion.
- Do not assume the scale is 1.0; always retrieve it dynamically from the raster metadata.

### Prompt Snippet
```text
// Accessing raster metadata for spatial calculations
PixelScale pixelScale = rasterMetadata.getPixelScale();
double yRes = pixelScale.getScaleY();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `PixelScale` object is not initialized or if the underlying raster metadata failed to load.
- **Invalid Raster Context**: If the raster data is not properly aligned or the CRS is undefined, the returned scale value may not represent the expected physical dimensions.
- **Type Mismatch**: Ensure the `PixelScale` instance is correctly associated with the specific raster layer being processed in the Raptor join.

### Fix Code Hint
```java
// Ensure the raster is loaded and metadata is accessible before calling
if (rasterMetadata != null && rasterMetadata.getPixelScale() != null) {
    double yRes = rasterMetadata.getPixelScale().getScaleY();
} else {
    throw new IllegalStateException("Raster metadata or PixelScale is missing.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.getScaleZ`

### Signature
```java
():double
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:80_

### Goal
Retrieves the vertical scaling factor (Z-scale) associated with the pixel dimensions, typically used for normalizing or transforming elevation or depth values in raster datasets.

### Parameters
_None._

### Input
The caller must ensure that the `PixelScale` object has been correctly initialized or populated with metadata from a raster source (e.g., GeoTIFF or HDF4). No specific file format is required at the time of calling, but the object state must be valid.

### Output
Returns `double` — The vertical scale factor as a double-precision floating-point value.

### Valid Call Patterns
```java
// Inferred from signature:
PixelScale pixelScale = new PixelScale(); // Or retrieved from a raster metadata context
double zScale = pixelScale.getScaleZ();
```

### LLM Instruction Prompt
- When generating code that uses `getScaleZ`, ensure the `PixelScale` instance is properly initialized.
- Do not assume the return value is 1.0; always treat it as a retrieved metadata property.
- This method is part of the `edu.ucr.cs.bdlab.raptor` package; ensure appropriate imports are included.

### Prompt Snippet
```text
// Accessing the Z-scale from a PixelScale instance
double zScale = pixelScale.getScaleZ();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `PixelScale` object has not been instantiated or if the underlying metadata source failed to load.
- **Uninitialized State**: If the `PixelScale` object is created but not populated with raster metadata, the returned value may be a default (e.g., 0.0 or 1.0) depending on the implementation, which may lead to incorrect scaling calculations.

### Fix Code Hint
```java
// Ensure the PixelScale object is fully initialized before accessing metadata
if (pixelScale != null) {
    double zScale = pixelScale.getScaleZ();
    // Proceed with calculations
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.getValues`

### Signature
```java
():double[]
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:88_

### Goal
Retrieves the underlying scale values associated with a `PixelScale` object, typically used to define the mapping between pixel coordinates and physical spatial units in raster processing.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.PixelScale`. This object is typically derived from raster metadata (e.g., GeoTIFF or HDF4 headers) during the ingestion or processing phase of a Raptor workflow.

### Output
Returns `double[]` — An array of double-precision floating-point values representing the scale factors (e.g., pixel width and height in the coordinate reference system units).

### Valid Call Patterns
```java
// Inferred from signature:
PixelScale pixelScale = ...; // Obtained from raster metadata
double[] values = pixelScale.getValues();
```

### LLM Instruction Prompt
- When generating code that uses `PixelScale.getValues()`, ensure the `PixelScale` object is properly instantiated from a valid raster source (GeoTIFF/HDF4).
- The return type is a primitive `double[]`; ensure the calling code handles potential null or empty arrays if the `PixelScale` object was not initialized with valid metadata.

### Prompt Snippet
```text
PixelScale scale = rasterMetadata.getPixelScale();
double[] values = scale.getValues();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `PixelScale` object is null or if the underlying metadata source failed to populate the scale values during raster ingestion.
- **Empty Array**: If the raster source lacks defined pixel scale information, the method may return an empty array, which can cause index-out-of-bounds errors if the caller assumes a specific array length (e.g., expecting 2 values for X and Y).
- **Type Mismatch**: Ensure that the raster data being processed is compatible with the expected coordinate system, as `PixelScale` values are dependent on the raster's CRS.

### Fix Code Hint
```java
// Always check for null or empty before accessing indices
double[] values = pixelScale.getValues();
if (values != null && values.length >= 2) {
    double xScale = values[0];
    double yScale = values[1];
    // Proceed with spatial calculations
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.hashCode`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:36_

### Goal
Computes a hash code value for the `PixelScale` object to support efficient storage and retrieval in hash-based collections within Raptor raster processing workflows.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.PixelScale`. No specific file formats or external data are required as input, as this method operates on the internal state of the object.

### Output
Returns `int` — A hash code value derived from the internal state of the `PixelScale` instance.

### Valid Call Patterns
```java
// Inferred from signature:
PixelScale scale = new PixelScale(xScale, yScale);
int hash = scale.hashCode();
```

### LLM Instruction Prompt
- When generating code that uses `PixelScale`, ensure the object is properly instantiated before calling `hashCode()`.
- Use this method when storing `PixelScale` objects in `HashMap` or `HashSet` structures to ensure correct object identity and lookup performance.

### Prompt Snippet
```text
// Example of using PixelScale.hashCode() for collection management
PixelScale scale = new PixelScale(0.5, 0.5);
int hash = scale.hashCode();
```

### Common Failure Modes
- **NullPointerException**: Calling `hashCode()` on a `null` reference of `PixelScale`. Always verify the object is initialized before invocation.
- **Inconsistent Hashing**: If the internal state of the `PixelScale` object is modified after being added to a hash-based collection, the object may become unreachable; ensure `PixelScale` instances are treated as immutable where possible when used as keys.

### Fix Code Hint
```java
// Ensure the object is not null before calling hashCode
if (pixelScale != null) {
    int hash = pixelScale.hashCode();
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.isSet`

### Signature
```java
():boolean
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:92_

### Goal
Checks whether the pixel scale values (typically representing spatial resolution in X and Y dimensions) have been explicitly initialized or set within the `PixelScale` object.

### Parameters
_None._

### Input
The caller must have an instantiated `edu.ucr.cs.bdlab.raptor.PixelScale` object. No specific file formats are required as input, as this is a state-check method on an existing object instance.

### Output
Returns `boolean` — `true` if the pixel scale dimensions have been defined; `false` otherwise.

### Valid Call Patterns
```java
// Inferred from signature:
PixelScale scale = new PixelScale();
boolean initialized = scale.isSet();
```

### LLM Instruction Prompt
- Use `isSet()` to verify the initialization state of a `PixelScale` object before attempting to retrieve or use scale values in raster processing workflows.
- Do not assume the object is initialized upon instantiation; always check `isSet()` to avoid potential null or default-value logic errors.

### Prompt Snippet
```text
// Check if PixelScale is initialized before use
if (pixelScale.isSet()) {
    // Proceed with raster operations
}
```

### Common Failure Modes
- **Uninitialized State:** Calling methods that rely on pixel scale values (like resolution-dependent calculations) when `isSet()` returns `false`, potentially leading to unexpected default behavior or arithmetic errors.
- **Context Mismatch:** Assuming `isSet()` returns `true` immediately after object creation without explicitly calling a setter or constructor that populates the scale values.

### Fix Code Hint
```java
// Ensure the object is properly configured before checking
PixelScale scale = new PixelScale(xScale, yScale);
if (scale.isSet()) {
    // Safe to use scale
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.isSetExtended`

### Signature
```java
():boolean
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:96_

### Goal
Determines whether the `PixelScale` object is configured to use extended pixel scaling parameters.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.PixelScale`. No specific file formats are required as this is a state-check method on an in-memory object.

### Output
Returns `boolean` — `true` if the `PixelScale` instance is set to extended mode, `false` otherwise.

### Valid Call Patterns
```java
// Inferred from signature
PixelScale pixelScale = new PixelScale();
boolean isExtended = pixelScale.isSetExtended();
```

### LLM Instruction Prompt
- When generating code that uses `PixelScale`, first ensure the object is instantiated.
- Use `isSetExtended()` to verify the configuration state before performing operations that depend on extended scaling logic.
- This method takes no arguments and returns a primitive `boolean`.

### Prompt Snippet
```text
// Check if the current PixelScale configuration supports extended scaling
if (pixelScale.isSetExtended()) {
    // Perform extended scaling logic
}
```

### Common Failure Modes
- **NullPointerException**: Calling `isSetExtended()` on a `null` `PixelScale` reference. Ensure the object is properly initialized before invocation.
- **Misinterpretation of State**: Assuming `isSetExtended()` implies a specific coordinate system or data type; this method only reflects the internal configuration state of the `PixelScale` object, not the underlying raster data properties.

### Fix Code Hint
```java
// Ensure the object is not null before checking the state
if (pixelScale != null && pixelScale.isSetExtended()) {
    // Proceed with extended logic
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.setScaleX`

### Signature
```java
(double scaleX):void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:68_

### Goal
Sets the horizontal scale factor for a `PixelScale` object, defining the spatial resolution or pixel width in the coordinate reference system.

### Parameters
- `scaleX` (`double`): The horizontal scale value to be assigned to the pixel dimension.

### Input
- **Preconditions:** The caller must have an instantiated `edu.ucr.cs.bdlab.raptor.PixelScale` object.
- **Data:** A `double` representing the desired horizontal resolution.

### Output
Returns `void` — This method updates the internal state of the `PixelScale` instance.

### Valid Call Patterns
```java
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.PixelScale pixelScale = new edu.ucr.cs.bdlab.raptor.PixelScale();
pixelScale.setScaleX(0.0001);
```

### LLM Instruction Prompt
- Use this method to configure the horizontal resolution of a `PixelScale` object before using it in raster processing workflows.
- Ensure the `scaleX` value is consistent with the coordinate reference system (CRS) of the raster data being processed.

### Prompt Snippet
```text
// Example of setting pixel scale
edu.ucr.cs.bdlab.raptor.PixelScale ps = new edu.ucr.cs.bdlab.raptor.PixelScale();
ps.setScaleX(value);
```

### Common Failure Modes
- **Invalid Scale:** Providing a non-positive value for `scaleX` may lead to logical errors in subsequent raster operations or spatial joins.
- **Uninitialized Object:** Calling this method on a null reference will result in a `NullPointerException`.
- **CRS Mismatch:** Setting a scale that does not align with the raster's actual spatial resolution will cause incorrect spatial calculations during `raptorJoin` or other raster-vector operations.

### Fix Code Hint
```java
// Ensure the scale is positive and matches the raster metadata
if (newScaleX > 0) {
    pixelScale.setScaleX(newScaleX);
} else {
    throw new IllegalArgumentException("ScaleX must be a positive value.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.setScaleY`

### Signature
```java
(double scaleY):void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:76_

### Goal
Sets the vertical scale factor for a `PixelScale` object, defining the spatial resolution or pixel dimension along the Y-axis for raster processing operations.

### Parameters
- `scaleY` (`double`): The vertical scale value to be applied to the pixel dimension.

### Input
- **Preconditions:** The caller must have an instantiated `edu.ucr.cs.bdlab.raptor.PixelScale` object.
- **Data:** A `double` representing the vertical resolution of the raster data.

### Output
Returns `void` — This method updates the internal state of the `PixelScale` instance.

### Valid Call Patterns
```java
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.PixelScale pixelScale = new edu.ucr.cs.bdlab.raptor.PixelScale();
pixelScale.setScaleY(0.0001);
```

### LLM Instruction Prompt
- When generating code that uses `setScaleY`, ensure the `PixelScale` object is properly initialized.
- Use this method when configuring raster metadata or defining spatial transformations for Raptor operations.
- Ensure the `scaleY` value is a valid `double` representing the intended vertical resolution.

### Prompt Snippet
```text
// Configure pixel scale for raster processing
edu.ucr.cs.bdlab.raptor.PixelScale scale = new edu.ucr.cs.bdlab.raptor.PixelScale();
scale.setScaleY(value);
```

### Common Failure Modes
- **Null Pointer Exception:** Occurs if the `PixelScale` object has not been instantiated before calling `setScaleY`.
- **Invalid Scale Values:** Providing non-positive values for `scaleY` may lead to logical errors in subsequent raster-vector join or transformation operations, as pixel dimensions must be positive.

### Fix Code Hint
```java
// Ensure the object is instantiated and the value is positive
if (pixelScale != null && scaleY > 0) {
    pixelScale.setScaleY(scaleY);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.setScaleZ`

### Signature
```java
(double scaleZ):void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:84_

### Goal
Sets the vertical scaling factor (Z-scale) used for processing raster data within the Raptor framework.

### Parameters
- `scaleZ` (`double`): The multiplier applied to the vertical dimension (Z-axis) of the raster data, typically used to adjust elevation or depth values during spatial analysis.

### Input
- **Preconditions:** The `PixelScale` object must be initialized. This method is typically used in workflows involving elevation models or 3D raster data where vertical units differ from horizontal units.

### Output
Returns `void` — This method updates the internal state of the `PixelScale` instance.

### Valid Call Patterns
```java
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.PixelScale pixelScale = new edu.ucr.cs.bdlab.raptor.PixelScale();
pixelScale.setScaleZ(1.0);
```

### LLM Instruction Prompt
- When generating code that uses `setScaleZ`, ensure the `PixelScale` object is instantiated first.
- Use this method only when vertical unit normalization is required for raster-vector operations.
- The `scaleZ` parameter must be a `double`.

### Prompt Snippet
```text
// Set the vertical scale for the raster processor
pixelScale.setScaleZ(value);
```

### Common Failure Modes
- **Uninitialized Object:** Calling `setScaleZ` on a null `PixelScale` reference will throw a `NullPointerException`.
- **Incorrect Scaling:** Providing a `scaleZ` of `0.0` may result in flattened or invalid vertical data during subsequent raster processing steps.
- **Context Mismatch:** Applying a Z-scale to raster data that does not represent elevation or depth may lead to incorrect analytical results in zonal statistics or raster-vector joins.

### Fix Code Hint
```java
// Ensure the instance is not null before setting the scale
if (pixelScale != null) {
    pixelScale.setScaleZ(1.0);
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.PixelScale.toString`

### Signature
```java
():String
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/PixelScale.java:43_

### Goal
Returns a string representation of the `PixelScale` object, typically used for logging or debugging the spatial resolution settings of a raster dataset.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.PixelScale`. No specific file formats are required as this is an in-memory object operation.

### Output
Returns `String` — A human-readable string representation of the pixel scale values (e.g., horizontal and vertical resolution).

### Valid Call Patterns
```java
// Inferred from signature:
PixelScale scale = new PixelScale(xScale, yScale);
String scaleInfo = scale.toString();
```

### LLM Instruction Prompt
- When generating code that uses `PixelScale`, ensure the object is instantiated with valid numeric scale values before calling `toString()`.
- Use this method primarily for diagnostic output or logging within Raptor-based raster processing workflows.

### Prompt Snippet
```text
// Example of using PixelScale.toString() for logging
PixelScale scale = new PixelScale(0.0001, 0.0001);
System.out.println("Current raster pixel scale: " + scale.toString());
```

### Common Failure Modes
- **NullPointerException**: Calling `toString()` on a `PixelScale` reference that has not been initialized.
- **Misinterpretation**: Assuming the returned string format is machine-parseable; the output is intended for human-readable representation and may change between versions.

### Fix Code Hint
```java
// Ensure the object is not null before calling toString
if (pixelScale != null) {
    logger.info("Raster scale: " + pixelScale.toString());
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.RaptorSplit`

### Signature
```java
(Path rasterPath,Path intersectionFilePath,int tileIDStart,int tileIDEnd,long pixelsPerTile):null
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/RaptorSplit.java:43_

### Goal
Splits a large raster dataset into smaller, manageable tiles based on pre-calculated intersection metadata to facilitate parallel processing within the Raptor framework.

### Parameters
- `rasterPath` (`Path`): The file system path to the source raster file (e.g., GeoTIFF or HDF4).
- `intersectionFilePath` (`Path`): The path to the intersection metadata file generated by previous spatial join or indexing operations.
- `tileIDStart` (`int`): The starting index of the tile range to process.
- `tileIDEnd` (`int`): The ending index of the tile range to process.
- `pixelsPerTile` (`long`): The target number of pixels per output tile, used to control the granularity of the split.

### Input
- **Data Formats:** Supports standard raster formats including GeoTIFF and HDF4.
- **Preconditions:** The `intersectionFilePath` must contain valid spatial intersection data between the raster and the target vector features. The raster and vector data must share the same coordinate reference system (CRS) to ensure accurate splitting.

### Output
Returns `null`. The operation performs side-effect-based file generation, writing split raster tiles to the configured output directory.

### Valid Call Patterns
```java
// Inferred from signature:
new edu.ucr.cs.bdlab.raptor.RaptorSplit(rasterPath, intersectionFilePath, 0, 10, 1024L);
```

### LLM Instruction Prompt
- When calling `RaptorSplit`, ensure the `rasterPath` and `intersectionFilePath` are valid `org.apache.hadoop.fs.Path` objects.
- The `tileIDStart` and `tileIDEnd` must define a valid range within the available intersection metadata.
- Always verify that the output directory is empty or use the `-overwrite` flag if the environment supports it, as Beast operations typically fail if the output path already exists.

### Prompt Snippet
```text
// Example usage of RaptorSplit for raster partitioning
Path raster = new Path("hdfs:///data/input.tif");
Path intersections = new Path("hdfs:///data/intersections.bin");
new edu.ucr.cs.bdlab.raptor.RaptorSplit(raster, intersections, 0, 100, 256L);
```

### Common Failure Modes
- **File Existence:** The operation will fail if the output directory already exists; ensure the environment is clean or configured to overwrite.
- **CRS Mismatch:** If the raster and the intersection metadata were generated using different coordinate systems, the resulting tiles will be spatially incorrect.
- **Type Mismatch:** Ensure the `pixelsPerTile` value is appropriate for the memory constraints of the Spark executors to avoid `OutOfMemoryError` during processing.

### Fix Code Hint
```java
// Ensure paths are absolute and accessible by the Spark cluster
Path rasterPath = new Path("path/to/raster.tif");
Path intersectionPath = new Path("path/to/intersections");
// Use a reasonable pixelsPerTile based on available executor memory
new edu.ucr.cs.bdlab.raptor.RaptorSplit(rasterPath, intersectionPath, startID, endID, 1024L);
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.RaptorSplit.getLength`

### Signature
```java
():long
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/RaptorSplit.java:51_

### Goal
Retrieves the total length or size associated with a `RaptorSplit` object, typically representing the number of elements or the extent of the split segment within a raster processing workflow.

### Parameters
_None._

### Input
The caller must have an initialized instance of `edu.ucr.cs.bdlab.raptor.RaptorSplit`. This object is typically generated during the partitioning phase of a `raptorJoin` operation where raster data is split into manageable chunks for distributed processing.

### Output
Returns `long` — The length of the split, representing the count of units (e.g., pixels or records) contained within this specific split segment.

### Valid Call Patterns
```java
// Inferred from signature:
RaptorSplit split = ...; // Obtained from Raptor processing pipeline
long length = split.getLength();
```

### LLM Instruction Prompt
- When generating code that uses `RaptorSplit`, ensure the object is retrieved from a valid Raptor-based RDD operation (e.g., `raptorJoin`).
- Use `getLength()` to determine the size of a split when implementing custom logic for load balancing or progress tracking in Spark tasks.
- Do not assume the return value is a byte size; it represents the logical length of the split segment.

### Prompt Snippet
```text
// Accessing the length of a RaptorSplit segment
long splitSize = raptorSplitInstance.getLength();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `RaptorSplit` instance has not been initialized or is null.
- **Invalid Context**: Calling this method outside of a Spark task or a Raptor processing pipeline where the split metadata has not been populated.
- **Misinterpretation**: Confusing the returned `long` value with file size in bytes; this value is specific to the internal partitioning logic of the Raptor component.

### Fix Code Hint
```java
// Ensure the RaptorSplit object is not null before accessing its length
if (raptorSplit != null) {
    long length = raptorSplit.getLength();
    // Proceed with processing
} else {
    throw new IllegalStateException("RaptorSplit instance is null; cannot retrieve length.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.RaptorSplit.getLocations`

### Signature
```java
():String[]
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/RaptorSplit.java:56_

### Goal
Retrieves the list of physical storage locations or file paths associated with a `RaptorSplit` instance for distributed raster processing.

### Parameters
_None._

### Input
The `RaptorSplit` object must be initialized and represent a valid split of a raster dataset (e.g., GeoTIFF or HDF4). No specific preconditions are required for the method call itself, but the underlying `RaptorSplit` must have been successfully created by the Beast raster partitioning logic.

### Output
Returns `String[]` — An array of strings representing the file paths or URI locations where the data for this specific split resides.

### Valid Call Patterns
```java
// Inferred from signature:
RaptorSplit split = ...; // Obtained from a Raptor partitioner
String[] locations = split.getLocations();
```

### LLM Instruction Prompt
- Use `java:edu.ucr.cs.bdlab.raptor.RaptorSplit.getLocations` only when working with `RaptorSplit` objects derived from Beast's raster-vector join or partitioning workflows.
- Do not assume the returned array is non-empty; always check for null or empty arrays before iterating.
- This method is intended for internal metadata inspection of raster splits.

### Prompt Snippet
```text
// Accessing split locations for a Raptor task
String[] locations = raptorSplit.getLocations();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `RaptorSplit` instance is null or was not properly initialized by the Spark spatial partitioning process.
- **Empty Array**: If the split does not map to any physical file locations, the method may return an empty array, which can cause index-out-of-bounds errors if not handled.
- **Context Mismatch**: Calling this on a split that has not been correctly associated with a raster source may return unexpected or empty results.

### Fix Code Hint
```java
String[] locations = split.getLocations();
if (locations != null && locations.length > 0) {
    // Process locations
} else {
    // Handle case where no locations are defined for this split
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.RasterHelper.createRasterReader`

### Signature
```java
T(FileSystem rasterFS,Path rasterFile,BeastOptions opts):IRasterReader<T>
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/RasterHelper.java:10_

### Goal
Initializes a raster reader instance to facilitate pixel-level data access from supported raster formats within the Raptor framework.

### Parameters
- `rasterFS` (`FileSystem`): The Hadoop FileSystem instance where the raster data resides.
- `rasterFile` (`Path`): The path to the raster file (e.g., GeoTIFF or HDF4) to be read.
- `opts` (`BeastOptions`): Configuration options for the reader, including necessary type parameters for pixel data.

### Input
- **Data Formats:** Supports GeoTIFF and HDF4 files.
- **Preconditions:** The `rasterFile` must be accessible via the provided `rasterFS`. The `BeastOptions` must be correctly configured to match the pixel data type (e.g., `Int`, `Float`) of the source raster.

### Output
Returns `IRasterReader<T>` — An interface providing access to the raster data, where `T` represents the pixel data type (e.g., `Float`, `Integer`).

### Valid Call Patterns
```java
// Inferred from signature:
FileSystem fs = FileSystem.get(conf);
Path path = new Path("data/input.tif");
BeastOptions opts = new BeastOptions();
IRasterReader<Float> reader = edu.ucr.cs.bdlab.raptor.RasterHelper.createRasterReader(fs, path, opts);
```

### LLM Instruction Prompt
- When calling `createRasterReader`, ensure the `BeastOptions` object is initialized and populated with the correct pixel type parameter corresponding to the input raster file.
- Always verify that the `FileSystem` is properly configured for the environment before passing it to the helper.

### Prompt Snippet
```text
edu.ucr.cs.bdlab.raptor.RasterHelper.createRasterReader(fs, path, opts)
```

### Common Failure Modes
- **Type Mismatch:** Providing a `BeastOptions` configuration that does not match the actual pixel type of the GeoTIFF/HDF4 file will cause runtime errors during data access.
- **File Access:** Failure to provide a valid `FileSystem` or `Path` will result in an `IOException`.
- **Compatibility:** Attempting to read a raster that is not in a supported format (GeoTIFF/HDF4) will fail.

### Fix Code Hint
```java
// Ensure the pixel type matches the raster data
BeastOptions opts = new BeastOptions();
// Example: setting a type parameter if required by the specific reader implementation
// opts.set("type", "Float"); 
IRasterReader<Float> reader = edu.ucr.cs.bdlab.raptor.RasterHelper.createRasterReader(fs, path, opts);
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Statistics.accumulate`

### Signature
```java
(Collector c):Statistics
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Statistics.java:106_

### Goal
Aggregates raster pixel data into a `Statistics` object using the provided `Collector` to compute summary metrics over a spatial region.

### Parameters
- `c` (`Collector`): A `Collector` instance responsible for defining the accumulation logic (e.g., sum, count, min, max) applied to the raster values.

### Input
- **Data:** Requires a valid raster dataset (GeoTIFF or HDF4) that has been processed or joined via Raptor.
- **Preconditions:** The raster data must be loaded and accessible within the current Spark context. Ensure that the pixel type of the raster matches the expected input type of the `Collector`.

### Output
Returns `Statistics` — A summary object containing the accumulated results (e.g., total count, sum, or other statistical measures) derived from the raster pixels processed by the collector.

### Valid Call Patterns
```java
// Example inferred from signature
Statistics stats = statisticsInstance.accumulate(collector);
```

### LLM Instruction Prompt
- When using `accumulate`, ensure the `Collector` is initialized with the appropriate data type matching the raster's pixel depth (e.g., `Float`, `Int`). The `Statistics` object returned is typically used for zonal statistics or raster-vector join summaries.

### Prompt Snippet
```text
// Use this to aggregate raster statistics
Statistics stats = myStatistics.accumulate(myCollector);
```

### Common Failure Modes
- **Type Mismatch:** Passing a `Collector` that expects a different numeric type than the underlying raster pixels (e.g., `Float` vs `Int`) will result in runtime errors.
- **Uninitialized Collector:** Passing a null or improperly configured `Collector` will cause the accumulation process to fail.
- **Coordinate System Mismatch:** If the raster data has not been properly aligned or reprojected to match the vector features being analyzed, the resulting statistics may be inaccurate or empty.

### Fix Code Hint
```java
// Ensure the collector is typed correctly for the raster data
Collector<Float> floatCollector = new FloatCollector(); 
Statistics stats = statisticsInstance.accumulate(floatCollector);
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Statistics.collect`

### Signature
```java
(int column,int row,int width,int height,int[] values):Statistics
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Statistics.java:90_

### Goal
Computes descriptive statistical metrics for a specified rectangular region of pixel data extracted from a raster.

### Parameters
- `column` (`int`): The starting column index (x-coordinate) of the region within the raster.
- `row` (`int`): The starting row index (y-coordinate) of the region within the raster.
- `width` (`int`): The width of the rectangular region in pixels.
- `height` (`int`): The height of the rectangular region in pixels.
- `values` (`int[]`): A flat array containing the pixel values for the specified region.

### Input
The caller must provide a valid rectangular window defined by `column`, `row`, `width`, and `height`, along with an `int[]` array containing the corresponding pixel data. The `values` array must contain exactly `width * height` elements. This method is typically used after extracting pixel data from a raster source (e.g., GeoTIFF or HDF4) via a Raptor join or direct raster read.

### Output
Returns `Statistics` — An object containing calculated statistical summaries (such as count, sum, min, max, or mean) for the provided pixel values.

### Valid Call Patterns
```java
// Inferred usage based on signature
int[] pixelData = {10, 20, 30, 40};
Statistics stats = edu.ucr.cs.bdlab.raptor.Statistics.collect(0, 0, 2, 2, pixelData);
```

### LLM Instruction Prompt
- When calling `edu.ucr.cs.bdlab.raptor.Statistics.collect`, ensure the `values` array length matches the product of `width` and `height`.
- Use this method to perform local pixel-level analysis after retrieving raster data from a `RaptorJoinFeature` or similar raster-processing workflow.

### Prompt Snippet
```text
Statistics stats = edu.ucr.cs.bdlab.raptor.Statistics.collect(col, row, w, h, dataArray);
```

### Common Failure Modes
- **ArrayIndexOutOfBoundsException**: Occurs if the `values` array length is less than `width * height`.
- **Invalid Dimensions**: Providing negative values for `width` or `height` will result in an invalid statistics calculation.
- **Type Mismatch**: This method expects `int[]`. If the source raster uses `float` or `double` pixel types, the data must be cast or converted to `int` before calling this method, which may result in precision loss.

### Fix Code Hint
```java
// Ensure array size matches dimensions
if (values.length == width * height) {
    Statistics stats = edu.ucr.cs.bdlab.raptor.Statistics.collect(column, row, width, height, values);
} else {
    throw new IllegalArgumentException("Values array size does not match width * height");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Statistics.getNumBands`

### Signature
```java
():int
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Statistics.java:121_

### Goal
Retrieves the total number of spectral or data bands contained within the associated raster dataset.

### Parameters
_None._

### Input
The caller must ensure that the `Statistics` object has been correctly initialized and associated with a valid raster source (e.g., GeoTIFF or HDF4). No specific file format is required as an argument, as the object state must already reflect the loaded raster metadata.

### Output
Returns `int` — The count of bands present in the raster file.

### Valid Call Patterns
```java
// Inferred from signature:
int bands = statisticsInstance.getNumBands();
```

### LLM Instruction Prompt
- When generating code that uses `getNumBands`, ensure the `Statistics` object is instantiated and populated with raster metadata before calling this method.
- Do not pass any arguments to this method, as it takes zero parameters.
- Use the returned integer to validate raster dimensions or to iterate through band-specific operations in Raptor workflows.

### Prompt Snippet
```text
// Ensure the statistics object is initialized from a raster source
Statistics stats = new Statistics(rasterPath);
int bandCount = stats.getNumBands();
```

### Common Failure Modes
- **NullPointerException**: Occurs if the `Statistics` object was not properly initialized or if the underlying raster source failed to load.
- **Invalid State**: Calling this method on an empty or uninitialized `Statistics` instance may return an unexpected value or throw an exception depending on the internal implementation state.
- **Type Mismatch**: Ensure that the raster file format (GeoTIFF/HDF4) is supported by the Beast/Raptor environment, as unsupported formats may result in metadata parsing failures.

### Fix Code Hint
```java
// Ensure the raster is loaded and statistics are computed before access
if (statisticsInstance != null) {
    int bands = statisticsInstance.getNumBands();
} else {
    throw new IllegalStateException("Statistics object is not initialized.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Statistics.invalidate`

### Signature
```java
():void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Statistics.java:126_

### Goal
Invalidates the current cached statistics state within the `Statistics` utility, forcing a recalculation or reset of stored raster metadata.

### Parameters
_None._

### Input
No parameters are required. This method operates on the internal state of the `edu.ucr.cs.bdlab.raptor.Statistics` class. Ensure that any previous raster processing operations that populated the statistics cache are complete before calling this method if a fresh state is required.

### Output
Returns `void` — The method performs an in-place modification of the internal state, clearing existing cached statistical data.

### Valid Call Patterns
```java
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.Statistics.invalidate();
```

### LLM Instruction Prompt
- When generating code that uses `edu.ucr.cs.bdlab.raptor.Statistics.invalidate`, ensure it is called as a static method on the `Statistics` class.
- Use this method when you need to ensure that subsequent raster-vector join operations or zonal statistics calculations do not rely on stale metadata or cached values from previous processing steps.

### Prompt Snippet
```text
// Reset the statistics cache before performing a new zonal statistics operation
edu.ucr.cs.bdlab.raptor.Statistics.invalidate();
```

### Common Failure Modes
- **State Inconsistency:** Calling this method while a distributed Spark task is actively reading from the same `Statistics` instance may lead to race conditions or inconsistent results across executors.
- **Redundant Calls:** Calling this method unnecessarily may degrade performance in high-throughput pipelines by forcing the re-computation of expensive raster metadata (e.g., min/max pixel values or histograms).

### Fix Code Hint
```java
// Ensure the statistics cache is cleared before processing a new raster dataset
try {
    edu.ucr.cs.bdlab.raptor.Statistics.invalidate();
    // Proceed with new raster-vector join or statistics calculation
} catch (Exception e) {
    // Handle potential state access errors
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Statistics.isValid`

### Signature
```java
():boolean
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Statistics.java:131_

### Goal
Checks if the current `Statistics` object contains valid, computed statistical data ready for retrieval or further processing.

### Parameters
_None._

### Input
The caller must have an initialized `edu.ucr.cs.bdlab.raptor.Statistics` object. This method is typically called after a raster-vector join or a zonal statistics operation has been performed on the object to ensure the computation completed successfully.

### Output
Returns `boolean` — `true` if the statistics have been successfully calculated and are valid; `false` otherwise.

### Valid Call Patterns
```java
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.Statistics stats = new edu.ucr.cs.bdlab.raptor.Statistics();
// ... perform operations ...
if (stats.isValid()) {
    // Access statistical results
}
```

### LLM Instruction Prompt
- When generating code that accesses statistical results from an `edu.ucr.cs.bdlab.raptor.Statistics` instance, always wrap the access in a conditional check using `isValid()` to prevent processing uninitialized or invalid data.

### Prompt Snippet
```text
if (statisticsInstance.isValid()) {
    // Proceed with data extraction
}
```

### Common Failure Modes
- **Uninitialized State:** Calling `isValid()` on a `Statistics` object that has not yet been populated by a Raptor operation will return `false`.
- **Post-Processing Errors:** If a raster-vector join fails due to CRS mismatch or incompatible data types, the associated `Statistics` object may remain in an invalid state. Always verify `isValid()` before attempting to read values from the object.

### Fix Code Hint
```java
// Ensure the statistics object is valid before usage
if (!stats.isValid()) {
    throw new IllegalStateException("Statistics computation failed or was not performed.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Statistics.setNumBands`

### Signature
```java
(int n):void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Statistics.java:52_

### Goal
Sets the number of bands for the `Statistics` object to define the dimensionality of the raster data being processed.

### Parameters
- `n` (`int`): The total number of bands (layers) in the raster dataset.

### Input
- **Preconditions:** The `Statistics` object must be instantiated before calling this method. The value `n` should correspond to the actual band count of the target GeoTIFF or HDF4 raster file to ensure subsequent statistical calculations are accurate.

### Output
Returns `void` — This method updates the internal state of the `Statistics` instance.

### Valid Call Patterns
```java
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.Statistics stats = new edu.ucr.cs.bdlab.raptor.Statistics();
stats.setNumBands(3);
```

### LLM Instruction Prompt
- When generating code that uses `edu.ucr.cs.bdlab.raptor.Statistics`, always call `setNumBands` with the correct integer representing the raster's band count before performing statistical operations. Ensure the integer provided is positive.

### Prompt Snippet
```text
// Example of setting band count for Raptor statistics
edu.ucr.cs.bdlab.raptor.Statistics stats = new edu.ucr.cs.bdlab.raptor.Statistics();
stats.setNumBands(n);
```

### Common Failure Modes
- **Incorrect Band Count:** Providing a value that does not match the actual raster file metadata will lead to inaccurate statistical results or potential index out-of-bounds errors during processing.
- **Uninitialized Object:** Calling this method on a null reference will throw a `NullPointerException`.

### Fix Code Hint
```java
// Ensure the Statistics object is initialized and the band count matches the raster metadata
edu.ucr.cs.bdlab.raptor.Statistics stats = new edu.ucr.cs.bdlab.raptor.Statistics();
int actualBands = rasterMetadata.getNumBands(); 
stats.setNumBands(actualBands);
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.Statistics.toString`

### Signature
```java
():String
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/Statistics.java:45_

### Goal
Returns a string representation of the calculated raster statistics, typically including metrics such as count, sum, mean, or variance derived from a Raptor join operation.

### Parameters
_None._

### Input
The caller must have an initialized `edu.ucr.cs.bdlab.raptor.Statistics` object that has been populated with data, typically resulting from a raster-vector join or a zonal statistics calculation performed via the Raptor component.

### Output
Returns `String` — A human-readable string containing the statistical summary of the processed raster data.

### Valid Call Patterns
```java
// Inferred from signature:
Statistics stats = new Statistics();
// ... populate stats via Raptor operations ...
String summary = stats.toString();
```

### LLM Instruction Prompt
- When generating code that uses `java:edu.ucr.cs.bdlab.raptor.Statistics.toString`, ensure the `Statistics` object has been initialized and processed by a Raptor operation (e.g., `raptorJoin`).
- Do not assume the format of the returned string is machine-parseable; it is intended for logging or human-readable output.

### Prompt Snippet
```text
// Example of retrieving statistics summary
String statsSummary = myStatisticsInstance.toString();
System.out.println("Raster Statistics: " + statsSummary);
```

### Common Failure Modes
- **NullPointerException**: Calling `toString()` on an uninitialized or null `Statistics` object.
- **Empty Statistics**: Calling `toString()` before any raster-vector join or aggregation has occurred, which may result in a string indicating zero values or default/empty states.

### Fix Code Hint
```java
// Ensure the statistics object is not null before calling toString
if (stats != null) {
    System.out.println(stats.toString());
} else {
    System.err.println("Statistics object is null; ensure Raptor join completed successfully.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.TiePoint`

### Signature
```java
(double i,double j,double k,double x,double y,double z):null
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/TiePoint.java:26_

### Goal
Defines a tie point mapping between a 3D coordinate in a raster space (i, j, k) and a corresponding 3D coordinate in a geographic or projected coordinate system (x, y, z).

### Parameters
- `i` (`double`): The column index (or first dimension) in the raster grid.
- `j` (`double`): The row index (or second dimension) in the raster grid.
- `k` (`double`): The depth or band index (or third dimension) in the raster grid.
- `x` (`double`): The geographic or projected X-coordinate (e.g., longitude).
- `y` (`double`): The geographic or projected Y-coordinate (e.g., latitude).
- `z` (`double`): The elevation or vertical coordinate.

### Input
The caller must provide six `double` values representing the spatial correspondence between the raster grid indices and the target coordinate system. This constructor is typically used when manually defining georeferencing parameters for raster datasets (e.g., HDF4 or custom raster formats) where standard metadata might be missing or require explicit mapping.

### Output
Returns `null` — This is a constructor that initializes a `TiePoint` object instance.

### Valid Call Patterns
```java
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.TiePoint tiePoint = new edu.ucr.cs.bdlab.raptor.TiePoint(i, j, k, x, y, z);
```

### LLM Instruction Prompt
- When generating code using `TiePoint`, ensure all six parameters are provided as `double` types.
- Use this class when performing manual georeferencing or spatial alignment of raster data within the Raptor framework.
- Ensure the coordinate system of the (x, y, z) values matches the target CRS of the raster processing workflow.

### Prompt Snippet
```text
// Example of initializing a TiePoint for a raster-to-geo mapping
edu.ucr.cs.bdlab.raptor.TiePoint tp = new edu.ucr.cs.bdlab.raptor.TiePoint(0.0, 0.0, 0.0, -117.0, 34.0, 0.0);
```

### Common Failure Modes
- **Coordinate Mismatch:** Providing (x, y, z) coordinates in a different CRS than the rest of the Raptor workflow, leading to incorrect spatial joins or overlays.
- **Index Out of Bounds:** Providing (i, j, k) values that do not correspond to the actual dimensions of the raster being processed.
- **Type Mismatch:** Passing integer values that cannot be implicitly cast to `double` without explicit conversion.

### Fix Code Hint
```java
// Ensure all inputs are explicitly cast to double if they are integers
double i = 0.0;
double j = 0.0;
double k = 0.0;
double x = -117.39;
double y = 33.97;
double z = 0.0;
edu.ucr.cs.bdlab.raptor.TiePoint tp = new edu.ucr.cs.bdlab.raptor.TiePoint(i, j, k, x, y, z);
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.TiePoint.equals`

### Signature
```java
(Object obj):boolean
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/TiePoint.java:65_

### Goal
Determines whether the current `TiePoint` instance is semantically equivalent to another object by comparing their coordinate and pixel mapping properties.

### Parameters
- `obj` (`Object`): The object to compare against the current `TiePoint` instance.

### Input
- **Data:** An instance of `edu.ucr.cs.bdlab.raptor.TiePoint`.
- **Preconditions:** The caller must ensure the `TiePoint` objects are initialized. No specific file format is required as this is an in-memory object comparison.

### Output
Returns `boolean` — `true` if the provided object is an instance of `TiePoint` and contains identical coordinate and pixel mapping values; `false` otherwise.

### Valid Call Patterns
```java
// Inferred from signature:
TiePoint point1 = new TiePoint(x1, y1, lat1, lon1);
TiePoint point2 = new TiePoint(x1, y1, lat1, lon1);
boolean isEqual = point1.equals(point2);
```

### LLM Instruction Prompt
- When comparing two `TiePoint` objects in the Raptor framework, use the `.equals()` method to verify equality of their spatial and pixel mappings. Do not rely on `==` as it checks for reference equality rather than value equality.

### Prompt Snippet
```text
// Example of comparing two TiePoints
if (tiePointA.equals(tiePointB)) {
    // Handle matching tie points
}
```

### Common Failure Modes
- **Null Pointer Exception:** Calling `.equals()` on a `null` `TiePoint` reference will throw a `NullPointerException`. Always check for null before comparison.
- **Type Mismatch:** Passing an object that is not an instance of `TiePoint` will return `false` rather than throwing an exception, which is standard Java behavior but may lead to logic errors if the caller expects a specific type.

### Fix Code Hint
```java
// Always perform a null check before calling equals
if (point1 != null && point1.equals(point2)) {
    // Logic for equal points
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.TiePoint.getValueAt`

### Signature
```java
(int index):double
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/TiePoint.java:39_

### Goal
Retrieves the specific coordinate or attribute value associated with a tie point at the requested index within the `TiePoint` object.

### Parameters
- `index` (`int`): The zero-based index of the value to retrieve from the tie point's internal data structure.

### Input
- **Preconditions:** The `TiePoint` object must be initialized and populated with data. The `index` must be within the valid range of the underlying data array stored within the `TiePoint` instance.

### Output
Returns `double` — The numerical value stored at the specified index, typically representing a coordinate component or a raster-associated attribute value.

### Valid Call Patterns
```java
// Inferred from signature:
double value = tiePointInstance.getValueAt(index);
```

### LLM Instruction Prompt
- When using `getValueAt`, ensure the `index` is validated against the size of the `TiePoint` data to avoid `ArrayIndexOutOfBoundsException`. This method is used for low-level access to tie point data within the Raptor raster processing framework.

### Prompt Snippet
```text
// Accessing a value from a TiePoint object
double val = tiePoint.getValueAt(0);
```

### Common Failure Modes
- **IndexOutOfBoundsException:** Occurs if the provided `index` is negative or greater than or equal to the number of values stored in the `TiePoint`.
- **NullPointerException:** Occurs if the `TiePoint` instance has not been properly instantiated or initialized before calling the method.

### Fix Code Hint
```java
// Always check bounds if the index is dynamic
if (index >= 0 && index < tiePoint.size()) {
    double val = tiePoint.getValueAt(index);
} else {
    // Handle invalid index
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.TiePoint.isSet`

### Signature
```java
():boolean
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/TiePoint.java:51_

### Goal
Checks whether the `TiePoint` instance has been initialized with valid coordinate data.

### Parameters
_None._

### Input
The caller must have an instantiated `edu.ucr.cs.bdlab.raptor.TiePoint` object. No specific file formats are required as input, as this is an internal state check for the object.

### Output
Returns `boolean` — `true` if the tie point coordinates have been set, `false` otherwise.

### Valid Call Patterns
```java
// Inferred from signature
TiePoint tp = new TiePoint();
if (tp.isSet()) {
    // Proceed with processing
}
```

### LLM Instruction Prompt
- Use `isSet()` to verify the initialization status of a `TiePoint` object before attempting to access its coordinate values.
- Do not assume a `TiePoint` is valid upon instantiation; always check `isSet()` to avoid processing uninitialized or null-state points.

### Prompt Snippet
```text
// Check if the TiePoint is initialized before use
if (myTiePoint.isSet()) {
    // ...
}
```

### Common Failure Modes
- **NullPointerException**: Calling `isSet()` on a null `TiePoint` reference.
- **Logic Errors**: Proceeding with spatial transformations or raster-vector joins using a `TiePoint` that returns `false` for `isSet()`, leading to undefined behavior in coordinate mapping.

### Fix Code Hint
```java
// Ensure the point is set before usage
if (!tiePoint.isSet()) {
    throw new IllegalStateException("TiePoint must be initialized before use.");
}
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.TiePoint.set`

### Signature
```java
(double i,double j,double k,double x,double y,double z):void
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/TiePoint.java:30_

### Goal
Sets the coordinate mapping for a tie point, defining the relationship between a 3D point in image/raster space (i, j, k) and its corresponding 3D point in geographic/projected space (x, y, z).

### Parameters
- `i` (`double`): The column index (or first dimension) in the raster/image coordinate system.
- `j` (`double`): The row index (or second dimension) in the raster/image coordinate system.
- `k` (`double`): The depth or band index (or third dimension) in the raster/image coordinate system.
- `x` (`double`): The projected or geographic X-coordinate (e.g., longitude or Easting).
- `y` (`double`): The projected or geographic Y-coordinate (e.g., latitude or Northing).
- `z` (`double`): The elevation or vertical coordinate (e.g., altitude).

### Input
The caller must provide six `double` values representing the transformation mapping. This method is typically used when defining spatial reference transformations for raster data (e.g., GeoTIFF or HDF4) within the Raptor framework.

### Output
Returns `void` — This method updates the internal state of the `TiePoint` object instance.

### Valid Call Patterns
```java
// Inferred from signature:
edu.ucr.cs.bdlab.raptor.TiePoint tiePoint = new edu.ucr.cs.bdlab.raptor.TiePoint();
tiePoint.set(0.0, 0.0, 0.0, -117.3, 33.9, 300.0);
```

### LLM Instruction Prompt
- When generating code for `TiePoint.set`, ensure the first three arguments (`i, j, k`) represent the raster-space coordinates and the last three (`x, y, z`) represent the corresponding geographic/projected coordinates.
- Always instantiate the `TiePoint` object before calling `set`.

### Prompt Snippet
```text
// Example of setting a tie point for a raster coordinate
tiePoint.set(i, j, k, x, y, z);
```

### Common Failure Modes
- **Coordinate Mismatch:** Providing coordinates that do not align with the raster's CRS (Coordinate Reference System) will lead to incorrect spatial joins or georeferencing errors during `raptorJoin` operations.
- **Uninitialized Object:** Calling `set` on a null `TiePoint` reference will throw a `NullPointerException`.
- **Dimension Mismatch:** Using 2D coordinates in a 3D context (or vice versa) may result in unexpected behavior if the underlying raster data expects specific dimensionality.

### Fix Code Hint
```java
// Ensure the TiePoint is initialized before setting values
edu.ucr.cs.bdlab.raptor.TiePoint tp = new edu.ucr.cs.bdlab.raptor.TiePoint();
tp.set(col, row, band, easting, northing, elevation);
```

## API Test: `java:edu.ucr.cs.bdlab.raptor.TiePoint.toString`

### Signature
```java
():String
```
_Source: source/raptor/src/main/java/edu/ucr/cs/bdlab/raptor/TiePoint.java:80_

### Goal
Returns a string representation of a `TiePoint` object, typically used for debugging or logging the coordinate mapping between raster and vector spaces.

### Parameters
_None._

### Input
The caller must have an initialized `edu.ucr.cs.bdlab.raptor.TiePoint` object. No specific file formats are required as this is an in-memory object method.

### Output
Returns `String` — A human-readable string containing the internal state of the `TiePoint`, which typically includes the mapping coordinates or indices used in raster-vector alignment.

### Valid Call Patterns
```java
// Inferred from signature:
TiePoint tp = new TiePoint(...);
String info = tp.toString();
```

### LLM Instruction Prompt
- When generating code that uses `TiePoint`, use `toString()` only for logging or diagnostic purposes.
- Do not rely on the specific format of the returned string for programmatic parsing, as it is intended for human-readable output.

### Prompt Snippet
```text
// Example of logging a TiePoint
TiePoint tiePoint = ...;
System.out.println("Processing tie point: " + tiePoint.toString());
```

### Common Failure Modes
- **NullPointerException**: Calling `toString()` on a `null` `TiePoint` reference. Ensure the object is instantiated before invocation.
- **Misinterpretation**: Assuming the string format is stable across different versions of Beast; always treat the output as opaque diagnostic text.

### Fix Code Hint
```java
// Ensure the object is not null before calling toString
if (tiePoint != null) {
    logger.debug("TiePoint details: {}", tiePoint.toString());
}
```

