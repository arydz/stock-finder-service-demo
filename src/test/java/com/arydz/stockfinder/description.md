# Why openfeign is used in test scope?
The spring-cloud-starter-openfeign library provides useful classes:
- PageJacksonModule
- SortJacksonModule
Default Page implementation doesn't provide a constructor that can be used when parsing JSON responses into Page<?> (and Sort) objects during tests. Without this library, a custom implementation of Page interface would be required.  