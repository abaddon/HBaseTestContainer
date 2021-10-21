image:https://maven-badges.herokuapp.com/maven-central/io.github.abaddon.testcontainer/hbasetestcontainer/badge.svg[Maven Central,link=https://maven-badges.herokuapp.com/maven-central/io.github.abaddon.testcontainer/hbasetestcontainer]

# HBaseTestContainer

The scope of this project is offer a simple way to execute tests with HBase.

HBase uses hostnames to pass connection data back out of the container (from it's internal Zookeeper) and this behaviour creates 2 problems:
1. a hostname is needed
2. TestContainer follow this principle: _From the host's perspective Testcontainers actually exposes this on a random free port. This is by design, to avoid port collisions that may arise with locally running software or in between parallel test runs._

The class HBase Container breaks the Testcontainers' principle, mapping exposed ports to the same container ports.
With this workaround we can call HBase from the host or from the container using the same address.

### Important Note
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

 
