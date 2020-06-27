*NOT PRODUCTION READY, INITIAL RELEASE IS COMING SOON*
# Calcite Pilosa Adapter
> Plugin for [Apache Calcite](http://calcite.apache.org/) to query [Pilosa](https://www.pilosa.com/) distributed index using SQL.

### General information
Pilosa is a high-performance distributed bitmap index. It has been successfully used in many data-intensive projects. 
One of the notable applications is the [facts table in the analytical database](https://www.pilosa.com/use-cases/retail-analytics/). 
As the core of the analytical database, Pilosa solves the computational problem efficiently. 
One more step is required to link results with dimension tables, which is usually a part of the ETL workflow. 
In such a scenario, the data query pattern is defined in advance. 
Though feasible in many situations, this approach denies the client the freedom to design the query based on complementary fields in dimension tables.
Calcite Pilosa Adapter aims to fill this gap.  

### How it works?
Calcite Pilosa adapter works as a proxy. Clients connect with JDBC compatible driver and query the data with SQL,
adapter translates queries into Pilosa Query language ([PQL](https://www.pilosa.com/docs/latest/query-language/)),
and then translates results into JDBC ResultSet to send them back to the client.


### Installation
TDB: publish artifacts first.  

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

3.) Connect from you favourite JDBC client.    
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
postgres.dimenion_table dimension
on dimension.X = facts.X
where 
dimension.Y = [value]
```
Please, check [wiki](https://github.com/alex-rnv/calcite-pilosa/wiki) for examples and available options.  


