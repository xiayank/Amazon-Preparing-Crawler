# Amazon-Preparing-Crawler
This crawler contains two parts: Explore Crawler and Product Crawler.
## Explore Crawler
#### Input File: 
 Manually Create Subcategory URL:
 
 ```text
https://www.amazon.com/Camera-Photo-Film-Canon-Sony/b/ref=nav_shopall_p?ie=UTF8&node=502394
```

#### Output File:
Subcategory URL:

```text
https://www.amazon.com/Cardio-Life-Fitness/b/ref=sv_sv_so_sprtfit_1/141-3986971-7551567?ie=UTF8&node=3407741
```
## Product Crawler
Crawl all the products on subcategory page.

## Development environment
   - Jsoup is used to retrieve web page content, parse the required text from the page.
   - Rabbit MQ
## Getting started

### Rabbit MQ
1. Startup MQ server 
`rabbitmq-server start` or `brew services start rabbitmq`
2. Stop               
`rabbitmq-server stop` or `brew services stop rabbitmq`
3. Login :

```  
  http://127.0.0.1:15672/ 
  username: guest   
  psw: guest
```
4. Add Queue:
Go to `Queue` section in MQ manage page. Add queues named `LevelOne`,`LevelTwo`, `ReducedProducts`.
### Run Application
Build maven application:
```bash
mvn clean install
```
Run the fat jar:

```bash
java -jar crawler-test-1.0-SNAPSHOT-jar-with-dependencies.jar 
```