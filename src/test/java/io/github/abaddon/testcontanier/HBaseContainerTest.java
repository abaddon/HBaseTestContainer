package io.github.abaddon.testcontanier;

import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;

public class HBaseContainerTest {

    private final static String HBASE_HOSTNAME = "hbase-docker";

    @ClassRule
    public static HBaseContainer hbaseContainer = new HBaseContainer(HBASE_HOSTNAME);

    @Test
    public void testPorts() {
        String expectedMasterUIPort="16010";
        String expectedRestAPIPort="8080";
        String expectedZookeeperPort="2181";
        String expectedRestUIPort="8085";
        String expectedThriftAPIPort="9090";
        String expectedThriftUIPort="9095";


        assertEquals(expectedMasterUIPort,hbaseContainer.masterUIPort());
        assertEquals(expectedRestAPIPort,hbaseContainer.restAPIPort());
        assertEquals(expectedZookeeperPort,hbaseContainer.zookeeperPort());
        assertEquals(expectedRestUIPort,hbaseContainer.restUIPort());
        assertEquals(expectedThriftAPIPort,hbaseContainer.thriftAPIPort());
        assertEquals(expectedThriftUIPort,hbaseContainer.thriftUIPort());
    }
}