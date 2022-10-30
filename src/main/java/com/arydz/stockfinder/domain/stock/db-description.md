# Brief Description

When saving stock data in the database, for better performance a JdbcTemplate with bulk insert is used (go to
application.properties)

## Classes used during the persistence process

- StockEntity
- StockDao
- StockFields
- StockTableDefinition
- SqlUtils

## How we can use bulk insert?

Bulk insert is not supported by those generation types:

- IDENTITY,
- AUTO,
- TABLE

So the only solution that remains is `SEQUENCE` generator, which will use sequence prepared on database side (PostgreSQL
provide `SERIAL` field type)

## Description of available generation types:

- **AUTO**, selects generation basing on database dialect (mostly GenerationType.SEQUENCE)
- **IDENTITY**, it uses auto-increment column on the database side (without requiring other statements). But it affects
  Hibernate, which need id for entity to perform specific statement.
  Because of that we cannot use it for JDBC batching.
- **SEQUENCE**, it needs the select statement for the succeeding value for the id column. Hibernate will retrieve value
  from such sequent during performing specific statements.
- **TABLE**, the slowest solution, because of the use of pessimistic locks. Another table is generated, that "gives" to
  hibernate a generated id.

## Why JdbcTemplate instead of JpaRepository or EntityManager?

- Spring Data Jpa repository does not allow to use of a native query for processing lists, like:

```java

@Repository
interface StockRepository extends JpaRepository<StockEntity, Long> {
    @Modifying
    @Query("INSERT INTO STOCK ID, TICKER VALUES (?,?) ON CONFLICT (TICKER) DO NOTHING")
    void upsert(@Param("list") List<StockEntity> entityList);
}
```

- Entity manager supports a bulk inserts for ready on java side entities (using `persist` method)
- When using entity manager with native query (upsert SQL method), I was not able to use batch inserts, so all SQL
  requests were processed at the same time instead of splitting into smaller SQL batches.
- Additionally, due to the implementation of JdbcTemplates, provides the best performance as native results.

## Sample implementations

- I was not able to achieve bulk insert with those implementations:

```java
    @Transactional
public void saveAll(List<StockEntity> entityList){

        String upsertQuery=sqlUtils.prepareUpsert("STOCK",stockTableDefinition,EXCLUDED_STOCK_FIELDS);
        int size=entityList.size();
        for(int i=0;i<size; i++){
        if(i>0&&i%batchSize==0){
        entityManager.flush();
        entityManager.clear();
        }
        StockEntity entity=entityList.get(i);

        Query query=entityManager.createNativeQuery(upsertQuery);
        query.setParameter(StockFields.TICKER.name(),entity.getTicker());
        query.setParameter(StockFields.TITLE.name(),entity.getTitle());
        query.setParameter(StockFields.EDGAR_CIK.name(),entity.getEdgarCik());

        query.executeUpdate();
        }
        }
```

```java
    @Transactional
public void saveAll(List<StockEntity> entityList){

        String upsertQuery=sqlUtils.prepareUpsert("STOCK",stockTableDefinition,EXCLUDED_STOCK_FIELDS);
        for(StockEntity entity:entityList){

        Query query=entityManager.createNativeQuery(upsertQuery);
        query.setParameter(StockFields.TICKER.name(),entity.getTicker());
        query.setParameter(StockFields.TITLE.name(),entity.getTitle());
        query.setParameter(StockFields.EDGAR_CIK.name(),entity.getEdgarCik());
        query.executeUpdate();
        }
        }
```

- With Spring Data JPA repository it was easy to achieve bulk inserts, but it does not allow to use of a native query
  for processing lists (an upsert approach is required here)

```java

@Repository
interface StockRepository extends JpaRepository<StockEntity, Long> {

}
// with usage
stockRepository.saveAll(sotckEntityList)
```

## PostgreSQL

PostgreSQL supports creating sequences in a very convenient way. For the primary key column, we have to use **SERIAL**
keyword. This will generate the appropriate sequence in the database.

## Hibernate - useful properties

Print detailed information about executed sql statements when using JPA

```text
spring.jpa.properties.hibernate.generate_statistics=true
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Print detailed information about executed sql statements when using JDBC (and JPA)

```text
logging.level.org.springframework.jdbc.core.JdbcTemplate=DEBUG
logging.level.org.springframework.jdbc.core.StatementCreatorUtils=TRACE
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.level.org.hibernate.type=trace
```

## Possible another solutions

1. Provide own implementation of `JpaRepositoryImplementation` (`SimpleJpaRepository` could be extended). It would
   require own `JpaRepositoryFactory` and `JpaRepositoryFactoryBean`
2. Change approach and as the first thing to do, load data from DB, next get rid of existing entities from the new list
   provided by Web.

```java
@Transactional
public void saveAll(List<Entity> entitiesToSave){
        List<Entity> existingEntties=repository.findAll();
        entitiesToSave.stream()
        .filter(entityToSave->!existingEntties.contains(entityToSave))
        .collect(Collectors.toList());
        repository.saveAll(entitiesToSave);
        }
```

## Reactive repositories
Spring Data R2DBC could be used. 
It provides reactive connectivity to the database, which could be useful in more advanced projects, where finding a list of elements is somehow processed as an intermediate step.
R2DBC also supports pagination with sorting, and batching.

```java
    public Mono<Page<Stock>>findAll(int page,int size){

        PageRequest pageable=PageRequest.of(page,size);
        return repository.findAllBy(pageable)
        .collectList()
        .zipWith(this.repository.count())
        .map(tuple->new PageImpl<>(tuple.getT1(),pageable,tuple.getT2()))
        .map(entityPages->entityPages.map(stockMapper::mapEntityToStock));
        }
```

## Batch size value
This project illustrates the usage of bulk inserts, but the suggested value of 250 batch size can be overkill for production applications.
Bigger batch size values require bigger RAM availability. The Hibernate Community recommendation is between 10 and 50.