# shishamo

shishamo is [MySQL](https://www.mysql.com/) metadata visualizer.

"shishamo" means capelin in Japanese, which is known as dolphin's bait.  
Sakila will definitely like it too.

## Build status

Defalut branch:   
* [![CircleCI](https://circleci.com/gh/su-kun1899/shishamo/tree/master.svg?style=svg)](https://circleci.com/gh/su-kun1899/shishamo/tree/master)

## Coverage Report

Default branch:
* [![codecov](https://codecov.io/gh/su-kun1899/shishamo/branch/master/graph/badge.svg)](https://codecov.io/gh/su-kun1899/shishamo)

## Embedded MySql

You can use embedded mysql server for demo, testing, development.

Example:  
- `java -jar -Dshishamo.embedded.mysql=true shishamo.jar`
- `./mvnw spring-boot:run -Dshishamo.embedded.mysql=true`  

Also you can custormize the configuration by `src/main/resources/embedded-mysql.yml`  

Notice:
Command line argument has more priority than configuration file.
