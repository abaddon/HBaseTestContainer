package io.github.abaddon.testcontanier;

import com.github.dockerjava.api.command.CreateContainerCmd;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;

public class HBaseContainer extends GenericContainer<HBaseContainer> {
    private final static String DOCKER_IMAGE = "dajobe/hbase:latest";
    private final static String CONTAINER_DATA_PATH = "/data";
    private final String HOSTNAME;
    private final String ZOOKEEPER_PORT = "2181";
    private final String REST_API_PORT = "8080";
    private final String REST_UI_PORT = "8085";
    private final String THRIFT_API_PORT = "9090";
    private final String THRIFT_UI_PORT = "9095";
    private final String MASTER_UI_PORT = "16010";


    public HBaseContainer(String hostname) throws IllegalStateException {
        super(DockerImageName.parse(DOCKER_IMAGE));
        this.HOSTNAME = hostname;
        setPortBindings(List.of(
                String.format("%s:%s", REST_API_PORT, REST_API_PORT),
                String.format("%s:%s", REST_UI_PORT, REST_UI_PORT),
                String.format("%s:%s", THRIFT_API_PORT, THRIFT_API_PORT),
                String.format("%s:%s", THRIFT_UI_PORT, THRIFT_UI_PORT),
                String.format("%s:%s", ZOOKEEPER_PORT, ZOOKEEPER_PORT),
                String.format("%s:%s", MASTER_UI_PORT, MASTER_UI_PORT),
                "16020:16020",
                "16000:16000")
        );
        withCreateContainerCmdModifier(cmd -> ((CreateContainerCmd) cmd).withHostName(HOSTNAME));
        waitingFor(new LogMessageWaitStrategy()
                .withRegEx(".*Master has completed initialization.*")
                .withStartupTimeout(Duration.of(5L, HOURS))
        );
    }

    public HBaseContainer withDataFolderBind(String hostPath) {
        File hbaseDir = new File(hostPath);
        switch (hbaseDir.exists() ? "EXIST" : "MISSING") {
            case "EXIST":
                deleteDir(hbaseDir);
            case "MISSING":
                hbaseDir.mkdirs();
        }
        return withFileSystemBind(hostPath, CONTAINER_DATA_PATH, BindMode.READ_WRITE);
    }

    public String zookeeperPort() {
        return ZOOKEEPER_PORT;
    }

    public String restAPIPort() {
        return REST_API_PORT;
    }

    public String restUIPort() {
        return REST_UI_PORT;
    }

    public String thriftAPIPort() {
        return THRIFT_API_PORT;
    }

    public String thriftUIPort() {
        return THRIFT_UI_PORT;
    }

    public String masterUIPort() {
        return MASTER_UI_PORT;
    }

    public String toString() {
        StringBuilder urls = new StringBuilder("URLS:");
        urls.append(String.format("REST API: %s:%s", HOSTNAME, REST_API_PORT));
        urls.append(String.format("REST UI: http://%s:%s", HOSTNAME, REST_UI_PORT));
        urls.append(String.format("Thrift API: %s:%s", HOSTNAME, THRIFT_API_PORT));
        urls.append(String.format("Thrift UI: http://%s:%s", HOSTNAME, THRIFT_UI_PORT));
        urls.append(String.format("Zookeeper API: http://%s:%s", HOSTNAME, ZOOKEEPER_PORT));
        urls.append(String.format("Master UI: http://%s:%s", HOSTNAME, MASTER_UI_PORT));
        return urls.toString();
    }

    private static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

}
