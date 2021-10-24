package io.github.abaddon.testcontainer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HBaseContainerTest {

    private final static String HBASE_HOSTNAME = "hbase-docker";

    @ClassRule
    public static HBaseContainer hbaseContainer = new HBaseContainer(HBASE_HOSTNAME);


    @Test
    public void givenHBaseContainerWhenCreateTableThenTableIsCreated() {
        Admin admin = null;
        TableName tableName = TableName.valueOf("tb_test");
        try {
            admin =  connection().getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue("Admin Client creation",false);
        }

        createTable(admin,tableName);

        boolean tableExist = false;
        try {
            tableExist = admin.tableExists(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue("Table created",tableExist);

    }

    @Test
    public void givenHBaseContainerWhenConvertToStringThenAStringIsReturned() {
        String toStringExpected="URLS:" +
                System.getProperty("line.separator")+ "REST API: hbase-docker:8080" +
                System.getProperty("line.separator")+ "REST UI: http://hbase-docker:8085" +
                System.getProperty("line.separator")+ "Thrift API: hbase-docker:9090" +
                System.getProperty("line.separator")+ "Thrift UI: http://hbase-docker:9095" +
                System.getProperty("line.separator")+ "Zookeeper API: http://hbase-docker:2181" +
                System.getProperty("line.separator")+ "Master UI: http://hbase-docker:16010";

        assertEquals(toStringExpected.toString(),hbaseContainer.toString());
    }


    @Test
    public void testMethodsToGetPorts() {
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


    private void createTable(Admin admin, TableName tableName){
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(tableName);
        List<ColumnFamilyDescriptor> fieldsDescriptors = new ArrayList<>();

        fieldsDescriptors.add(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("data")).build());
        fieldsDescriptors.add(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("meta")).build());

        tableDescriptorBuilder.setColumnFamilies(fieldsDescriptors);
        TableDescriptor tableDescriptor = tableDescriptorBuilder.build();

        try {
            admin.createTable(tableDescriptor);
            System.out.println("Table created");
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue("Table created",false);
        }
    }

    private Connection connection(){
        Connection connection = null;
        try {
            connection =  ConnectionFactory.createConnection(hBaseConfiguration());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue("Connection creation",false);
        }finally {
            return connection;
        }
    }

    private Configuration hBaseConfiguration(){
        Configuration config = HBaseConfiguration.create();
        config.clear();
        config.set("hbase.zookeeper.quorum", hbaseContainer.getHost());
        config.set("hbase.zookeeper.property.clientPort", hbaseContainer.zookeeperPort());
        return config;
    }
}