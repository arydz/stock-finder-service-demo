Method in controller, responsible for updating market data in stocks use annotation `@RequestPart` for its arguments
```java
    @PutMapping(value = "/update/market", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Void> manuallyImport(@RequestPart("file") FilePart file,
                                     @RequestPart("chartTimeframeType") String chartTimeframeTypeName,
                                     @RequestPart("extractionMode") String extractionModeName) {
        // ...
    }
```
Annotation @RequestPart and selecting `MULTIPART_FORM_DATA_VALUE` as the consumed data type, is suggested for uploading files when using WebFlux. Additionally, it doesn't support enum serializations (415 code thrown). This would require implementing special Spring converters for each enum type.
This case could be resolved also with @RequestBody and selecting `APPLICATION_JSON_VALUE` as the consumed data type, where the simple model would be prepared, for example:
```java
public class ImportFileParams {

    private FilePart file;
    private ChartTimeframeType chartTimeframeType;
    private ExtractionMode extractionMode;
}
```
First case (with `@RequestPart`), makes easy to use real files in unit/integration tests:
```java

    public BodyInserters.MultipartInserter fromFile(File file) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(file));
        builder.part("chartTimeframeType", ChartTimeframeType.DAILY);
        builder.part("extractionMode", ExtractionMode.EXCLUDE_JSON_FOLDERS);

        return BodyInserters.fromMultipartData(builder.build());
    }
```
If we would like to use `@RequestBody` and a simple model class, we would need to mock that, since `FilePart` doesn't provide any way to create a java instance.