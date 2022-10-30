# Some of the popular exception-handling solutions in Spring

## Exception handler
- method implemented in a specific controller (with @ExceptionHandler annotation)
- handles error in scope of this controller
```java
/* ... */
public class StockController{
    
    /* ... */
    
    @ExceptionHandler({ IllegalArgumentException.class })
    public void handleException() {
        //
    }
} 
```
## HandlerExceptionResolver
- allows to unify exception handling in application
- default implementations do not allow putting anything in the body of the error response
- returns appropriate http status code
- available solutions: 
  - implementing custom HandlerExceptionResolver (DefaultHandlerExceptionResolver), it allows to provide message in the body, but it is limited to ModelAndView
  - adding @ResponseStatus annotation used in custom exception (ResponseStatusExceptionResolver), easy to use but returns only status code
    ```java
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class ElementNotFoundException extends RuntimeException {
        public ElementNotFoundException() {
            super();
        }
    }
    ```
## ControllerAdvice annotation
- global solution, handles exceptions among all controllers
- allows managing status code and response body
```java
@ControllerAdvice
public class RestExceptonHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse onIllegalArgumentException(IllegalArgumentException exception){
    // ...
  } 
```

## ResponseStatusException
- it allows in very simple way to provide Http status, message and a cause
- there is no need to create custom exception classes
- just throw ResponseStatusException in code
   ```java
    if (user == null) {
        throw new ResponseStatusException(SERVICE_UNAVAILABLE, ERROR_MESSAGE));
    }
   ```
  to use custom error message, set this property
   ```properties
   server.error.include-message=always
   ``` 
- such approach can cause code duplication
- it's possible to use @ControllerAdvice annotation as a global solution but also for local situations ResponseStatusExceptions can be provided

# Used classes
- ErrorResponse
- RestErrorHandler

In `StockDao` there is batchSize validation. This has been implemented only for learning purposes (to force throwing IllegalArgumentException)

## RestErrorHandler description
```java
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse serverExceptionHandler(IllegalArgumentException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .build();
    }
```
When using custom response, it's suggested to use @ResponseStatus annotation. Information about the status code is applied to the HTTP response.
Without that client will receive a 200 status code, despite the exception being thrown. 