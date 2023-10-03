**Deprecation note:**    
**Pilosa was rebranded to FeatureBase, which comes with its own [SQL support](https://docs.featurebase.com/docs/sql-guide/sql-guide-home/).**    
**Tools like Looker can solve the problem of joining tables from different sources.**    
**This project is no longer actively maintained.**

# Calcite Pilosa Adapter
> Plugin for [Apache Calcite](http://calcite.apache.org/) to query [Pilosa](https://www.pilosa.com/) distributed index using SQL.

### General information
Pilosa is a high-performance distributed bitmap index. It has been successfully used in many data-intensive projects. 
One of the notable applications is the [facts table in the analytical database](https://www.pilosa.com/use-cases/retail-analytics/). 
As the core of the analytical database, Pilosa solves the computational problem efficiently. 
Calcite Pilosa Adapter solves the problem of linking the Pilosa table with supplementary dimension tables from other databases, like Postgres, making Pilosa a powerful 
exploration tool for business intelligence.

### How it works?
The Calcite Pilosa adapter works as a proxy. Clients connect with JDBC-compatible drivers and query the data with SQL,
adapter translates queries into Pilosa Query language ([PQL](https://www.pilosa.com/docs/latest/query-language/)),
and then translates results into JDBC ResultSet to send them back to the client.


### Installation
```json
<dependency>
  <groupId>com.alexrnv.calcite.adapter.pilosa</groupId>
  <artifactId>calcite-pilosa</artifactId>
  <version>0.0.1</version>
</dependency>
```
<sup>note the artifact is hosted in GitHub Packages</sup>

### Usage 
1.) Start from the configuration:
```json
{
  "version": "1.0",
  "defaultSchema": "pilosa",
  "schemas": [
    {
      "name": "pilosa-cluster",
      "type": "custom",
      "factory": "com.alexrnv.calcite.adapter.pilosa.model.PilosaSchemaFactory",
      "operand": {
        "url": "http://localhost:10101"
      }
    }
  ]
} 
```     
Provide your Pilosa server endpoint.    

2.) The following code snippet starts a JDBC server inside your service.
```java
LocalService service = new PilosaServiceFactory(modelFileUri).createLocalService();
HttpServer server = new PilosaHttpServerFactory(service, serverPort).createHttpServer();
server.start();
try {
    server.join();
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    server.stop();
}
```
Your service is now listening for JDBC connections.

3.) Connect from your favourite JDBC client.    
Clients should use [Avatica](https://calcite.apache.org/avatica/) JDBC driver ([mvn](https://mvnrepository.com/artifact/org.apache.calcite.avatica/avatica)). Use version 1.14.0 or later.     
```
jdbc:avatica:remote:url=http://<host>:<port>>/sql/v1
``` 
4.) Run your analysis with SQL
```sql
select 
count(distinct facts._id)
from 
pilosa.facts_table facts
join
postgres.dimension_table dimension
on dimension.X = facts.X
where 
dimension.Y = [value]
```
Please, check [wiki](https://github.com/alex-rnv/calcite-pilosa/wiki) for examples and available options.  


