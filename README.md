[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.abaddon.testcontainer/hbasetestcontainer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.abaddon.testcontainer/hbasetestcontainer)

# HBaseTestContainer

The scope of this project is offer a simple way to execute tests with HBase.

HBase uses hostnames to pass connection data back out of the container (from it's internal Zookeeper) and this behaviour creates 2 problems:
1. a hostname is needed
2. TestContainer follow this principle: _From the host's perspective Testcontainers actually exposes this on a random free port. This is by design, to avoid port collisions that may arise with locally running software or in between parallel test runs._

## How it works

The class HBase Container breaks the Testcontainers' principle, mapping exposed ports to the same container ports.
With this workaround we can call HBase from the host or from the container using the same address.

#### Important Note
Remember to define a custom *hosts* file to use when you run your test.
This file has to contain something like this:

``` 
127.0.0.1   hbase_container_name 
```

You can see an example in the repository.

There are multiple ways to load this files: below some of them:

1. as java option: `-Djdk.net.hosts.file=./path/hosts`
2. you can create a file on the root of the project `.mvn/jvm.config` and add the java option 
3. in the pom.xml, you can add the parameter in the surfer plugin like this: 
```
<properties>
    <jvm.options>-Djdk.net.hosts.file=./path/hosts</jvm.options>
</properties>
...
<plugins>
    ...
    <plugin>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.22.2</version>
        <configuration>
            <argLine>${jvm.options}</argLine>
        </configuration>
    </plugin>
</plugin>
```

## Usage

1. Add the HBaseContainer as dependency in your pom.xml file. The last version is: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.abaddon.testcontainer/hbasetestcontainer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.abaddon.testcontainer/hbasetestcontainer) 
```
<dependency>
  <groupId>io.github.abaddon.testcontainer</groupId>
  <artifactId>hbasetestcontainer</artifactId>
  <version>0.0.2</version>
</dependency>
```
2. Use HBaseContainer class in your test class:
```
public class HBaseStoreTest {

    private final static String HBASE_HOSTNAME = "hbase-docker";

    @ClassRule
    public static HBaseContainer hbaseContainer = new HBaseContainer(HBASE_HOSTNAME);


    @Test
    public void test() throws IOException {
    
    ....
    
    }
```
3. create a `hosts` file and load it when you run your tests (if you need).

## Extra features

### Bind HBase data folder
You can use the method `withDataFolderBind(String hostPath)`. Where hostPath is the folder on the host.
If it's not exist it will be created automatically.
At the moment, the folder is deleted and recreated at each run.
```
    @ClassRule
    public static HBaseContainer hbaseContainer = new HBaseContainer(HBASE_HOSTNAME)
    .withDataFolderBind("./hbase_data");;

```
