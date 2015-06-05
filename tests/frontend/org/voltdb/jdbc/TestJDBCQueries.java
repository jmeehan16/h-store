/* This file is part of VoltDB.
 * Copyright (C) 2008-2015 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.voltdb.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.voltdb.CatalogContext;
import org.voltdb.ServerThread;
import org.voltdb.benchmark.tpcc.TPCCClient;
import org.voltdb.compiler.VoltProjectBuilder;

import edu.brown.catalog.CatalogUtil;
import edu.brown.hstore.conf.HStoreConf;

/**
 * Class to test that all JDBC queries function as expected.
 * Modified to work with HStore.
 */
public class TestJDBCQueries {
	static String testjar;
    static ServerThread server;
    static Connection conn;
    static Connection myconn;
    static VoltProjectBuilder pb;

    static class Data
    {
        final String typename;
        final int dimension;
        final String tablename;
        final String typedecl;
        final String[] good;
        final String[] bad;

        Data(String typename, int dimension, String[] good, String[] bad)
        {
            this.typename = typename;
            this.dimension = dimension;
            this.good = new String[good.length];
            for (int i = 0; i < this.good.length; ++i) {
                this.good[i] = good[i];
            }
            if (bad != null) {
                this.bad = new String[bad.length];
                for (int i = 0; i < this.bad.length; ++i) {
                    this.bad[i] = bad[i];
                }
            }
            else {
                this.bad = null;
            }
            this.tablename = String.format("T_%s", this.typename);
            if (dimension > 0) {
                this.typedecl = String.format("%s(%d)", this.typename, this.dimension);
            }
            else {
                this.typedecl = this.typename;
            }
        }
    };

    static Data[] data = new Data[] {
            new Data("TINYINT", 0,
                        new String[] {"11", "22", "33"},
                        new String[] {"abc"}),
            new Data("SMALLINT", 0,
                        new String[] {"-11", "-22", "-33"},
                        new String[] {"3.2", "blah"}),
            new Data("INTEGER", 0,
                        new String[] {"0", "1", "2"},
                        new String[] {""}),
            new Data("BIGINT", 0,
                        new String[] {"9999999999999", "8888888888888", "7777777777777"},
                        new String[] {"Jan 23 2011"}),
            new Data("FLOAT", 0,
                        new String[] {"3.1415926", "2.81828", "-9.0"},
                        new String[] {"x"}),
            new Data("DECIMAL", 0,
                        new String[] {"1111.2222", "-3333.4444", "5555.6666"},
                        new String[] {""}),
            new Data("VARCHAR", 100,
                        new String[] {"abcdefg", "hijklmn", "opqrstu"},
                        null),
            new Data("TIMESTAMP", 0,
                        new String[] {"9999999999999", "0", "1"},
                        new String[] {""}),
    };

    // Define Voter schema as well.
    public static final String voter_schema =
            "CREATE TABLE contestants" +
            "(" +
            "  contestant_number integer     NOT NULL" +
            ", contestant_name   varchar(50) NOT NULL" +
            ", CONSTRAINT PK_contestants PRIMARY KEY" +
            "  (" +
            "    contestant_number" +
            "  )" +
            ");" +
            "CREATE TABLE votes" +
            "(" +
            "  phone_number       bigint     NOT NULL" +
            ", state              varchar(2) NOT NULL" +
            ", contestant_number  integer    NOT NULL" +
            ");" +
            "PARTITION TABLE votes ON COLUMN phone_number;" +
            "CREATE TABLE area_code_state" +
            "(" +
            "  area_code smallint   NOT NULL" +
            ", state     varchar(2) NOT NULL" +
            ", CONSTRAINT PK_area_code_state PRIMARY KEY" +
            "  (" +
            "    area_code" +
            "  )" +
            ");" +
            "CREATE VIEW v_votes_by_phone_number" +
            "(" +
            "  phone_number" +
            ", num_votes" +
            ")" +
            "AS" +
            "   SELECT phone_number" +
            "        , COUNT(*)" +
            "     FROM votes" +
            " GROUP BY phone_number" +
            ";" +
            "CREATE VIEW v_votes_by_contestant_number_state" +
            "(" +
            "  contestant_number" +
            ", state" +
            ", num_votes" +
            ")" +
            "AS" +
            "   SELECT contestant_number" +
            "        , state" +
            "        , COUNT(*)" +
            "     FROM votes" +
            " GROUP BY contestant_number" +
            "        , state;";

    public static final String drop_table =
            "CREATE TABLE drop_table" +
            "(" +
            "  contestant_number integer     NOT NULL" +
            ", contestant_name   varchar(50) NOT NULL" +
            ");" +
            "CREATE TABLE drop_table1" +
            "(" +
            "  contestant_number integer     NOT NULL" +
            ", contestant_name   varchar(50) NOT NULL" +
            ");" +
            "CREATE TABLE drop_table2" +
            "(" +
            "  contestant_number integer     NOT NULL" +
            ", contestant_name   varchar(50) NOT NULL" +
            ");";

    @SuppressWarnings("unused")
    @BeforeClass
    public static void setUp() throws Exception {
        // Add one T_<type> table for each data type.
        String ddl = "";
        for (Data d : data) {
            ddl += String.format("CREATE TABLE %s(ID %s, VALUE VARCHAR(255)); ",
                                 d.tablename, d.typedecl);
        }
        ddl += voter_schema;
        ddl += drop_table;
        
        URL url = TPCCClient.class.getResource("sqljdbc.sql");
        String schemaPath = URLDecoder.decode(url.getPath(), "UTF-8");
        pb = new VoltProjectBuilder("jdbcQueries");
        pb.addProcedures(org.voltdb.compiler.procedures.TPCCTestProc.class);
        pb.addSchema(schemaPath);
        testjar = "/tmp/jdbcdrivertest.jar";
        boolean success = pb.compile(testjar);
        assert(success);
     
        // Set up ServerThread and Connection
        startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        stopServer();
        File f = new File(testjar);
        f.delete();
    }

    @Before
    public void populateTables()
    {
        // Populate tables.
        for (Data d : data) {
            String q = String.format("insert into %s values(?, ?)", d.tablename);
            for (String id : d.good) {
                try {
                    PreparedStatement sel = conn.prepareStatement(q);
                    sel.setString(1, id);
                    sel.setString(2, String.format("VALUE:%s:%s", d.tablename, id));
                    sel.execute();
                    int count = sel.getUpdateCount();
                    assertTrue(count==1);
                }
                catch(SQLException e) {
                    System.err.printf("ERROR(INSERT): %s value='%s': %s\n",
                            d.typename, d.good[0], e.getMessage());
                    fail();
                }
            }
        }
    }

    @After
    public void clearTables()
    {
        for (Data d : data) {
            try {
                PreparedStatement sel =
                        conn.prepareStatement(String.format("delete from %s", d.tablename));
                sel.execute();
            } catch (SQLException e) {
                System.err.printf("ERROR(DELETE): %s: %s\n", d.tablename, e.getMessage());
                fail();
            }
        }
    }

    private static void startServer() throws ClassNotFoundException, SQLException {
        CatalogContext catalogContext = CatalogUtil.loadCatalogContextFromJar(new File(testjar)) ;
        HStoreConf hstore_conf = HStoreConf.singleton(HStoreConf.isInitialized()==false);
        Map<String, String> confParams = new HashMap<String, String>();
        hstore_conf.loadFromArgs(confParams);
        server = new ServerThread(catalogContext, hstore_conf, 0);
        
        server.start();
        server.waitForInitialization();

        Class.forName("org.voltdb.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:voltdb://localhost:21212");
        myconn = null;
    }

    private static void stopServer() throws SQLException {
        if (conn != null) {
            conn.close();
            conn = null;
        }
        if (myconn != null) {
            myconn.close();
            myconn = null;
        }
        if (server != null) {
            try { server.shutdown(); } catch (InterruptedException e) { /*empty*/ }
            server = null;
        }
    }

    @Test
    public void testSimpleStatement() throws Exception
    {
        for (Data d : data) {
            try {
                String q = String.format("select * from %s", d.tablename);
                Statement sel = conn.createStatement();
                sel.execute(q);
                ResultSet rs = sel.getResultSet();
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                }
                assertEquals(d.good.length, rowCount);
            }
            catch(SQLException e) {
                System.err.printf("ERROR(SELECT): %s: %s\n", d.typename, e.getMessage());
                fail();
            }
        }
    }

    @Test
    public void testQueryBatch() throws Exception
    {
        Statement batch = conn.createStatement();
        for (Data d : data) {
            String q = String.format("update %s set value='%s'", d.tablename, "whatever");
            batch.addBatch(q);
        }
        try {
            int[] resultCodes = batch.executeBatch();
            assertEquals(data.length, resultCodes.length);
            int total_cnt = 0;
            for (int i = 0; i < data.length; ++i) {
                assertEquals(data[i].good.length, resultCodes[i]);
                total_cnt += data[i].good.length;
            }
            //Test update count
            assertEquals(total_cnt, batch.getUpdateCount());
        }
        catch(SQLException e) {
            System.err.printf("ERROR: %s\n", e.getMessage());
            fail();
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void testSelect() throws Exception
    {
        try
        {
            // This query does work, per ENG-7306.
            String sql = "select * from votes;";
            java.sql.Statement query = conn.createStatement();
            ResultSet rs = query.executeQuery(sql);
        }
        catch (SQLException e) {
            System.err.println("ERROR(BASIC SELECT): " + e.getMessage());
            fail();
        }

        // execute() - Any valid SQL/DDL statement - should succeed
        try
        {
            String sql = "select * from contestants;";
            java.sql.Statement query = conn.createStatement();
            query.execute(sql);
        }
        catch (SQLException e) {
            System.err.println("ERROR(execute(SELECT)): " + e.getMessage());
            fail();
        }

        // executeUpdate() - Any valid SQL/DDL statement except SELECT - should fail
        try
        {
            String sql = "select * from contestant;";
            java.sql.Statement query = conn.createStatement();
            query.executeUpdate(sql);
            System.err.println("ERROR(executeUpdate(SELECT)): should have failed but did not.");
            fail();
        }
        catch (SQLException e) {
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void testTruncate() throws Exception
    {
        // execute() - Any valid SQL/DDL statement - should succeed
        try
        {
            String sql = "truncate table votes;";
            java.sql.Statement query = conn.createStatement();
            query.execute(sql);
        }
        catch (SQLException e) {
            System.err.println("ERROR(execute(TRUNCATE TABLE)): " + e.getMessage());
            fail();
        }

        // executeQuery() - Only SELECT - should fail
        try
        {
            String sql = "truncate table votes;";
            java.sql.Statement query = conn.createStatement();
            ResultSet rs = query.executeQuery(sql);
            System.err.println("ERROR(executeQuery(TRUNCATE TABLE) succeeded, should have failed)");
            fail();
        }
        catch (SQLException e) {
        }

        // executeUpdate() - Any valid SQL/DDL statement except SELECT - should succeed
        try
        {
            String sql = "truncate table votes;";
            java.sql.Statement query = conn.createStatement();
            query.executeUpdate(sql);
        }
        catch (SQLException e) {
            System.err.println("ERROR(executeUpdate(TRUNCATE TABLE)): " + e.getMessage());
            fail();
        }

    }

    @SuppressWarnings("unused")
    @Test
    public void testUpdate() throws Exception
    {
        // execute() - Any valid SQL/DDL statement - should succeed
        try
        {
            String sql = "update votes set CONTESTANT_NUMBER = 7 where PHONE_NUMBER = 2150002906;";
            java.sql.Statement query = conn.createStatement();
            query.execute(sql);
        }
        catch (SQLException e) {
            System.err.println("ERROR(execute(UPDATE)): " + e.getMessage());
            fail();
        }

        // executeQuery() - Only SELECT - should fail
        try
        {
            String sql = "update votes set CONTESTANT_NUMBER = 7 where PHONE_NUMBER = 2150002906;";
            java.sql.Statement query = conn.createStatement();
            ResultSet rs = query.executeQuery(sql);
            System.err.println("ERROR(executeQuery(UPDATE) succeeded, should have failed)");
            fail();
        }
        catch (SQLException e) {
        }

        // executeUpdate() - Any valid SQL/DDL statement except SELECT - should succeed
        try
        {
            String sql = "update votes set CONTESTANT_NUMBER = 7 where PHONE_NUMBER = 2150002906;";
            java.sql.Statement query = conn.createStatement();
            query.executeUpdate(sql);
        }
        catch (SQLException e) {
            System.err.println("ERROR(executeUpdate(UPDATE)): " + e.getMessage());
            fail();
        }

    }

    @SuppressWarnings("unused")
    @Test
    public void testDelete() throws Exception
    {
        // execute() - Any valid SQL/DDL statement - should succeed
        try
        {
            String sql = "delete from votes where   PHONE_NUMBER = 3082086134      ";
            java.sql.Statement query = conn.createStatement();
            query.execute(sql);
        }
        catch (SQLException e) {
            System.err.println("ERROR(execute(DELETE)): " + e.getMessage());
            fail();
        }

        // executeQuery() - Only SELECT - should fail
        try
        {
            String sql = "delete from votes where   PHONE_NUMBER = 3082086134      ";
            java.sql.Statement query = conn.createStatement();
            ResultSet rs = query.executeQuery(sql);
            System.err.println("ERROR(executeQuery(DELETE) succeeded, should have failed)");
            fail();
        }
        catch (SQLException e) {
        }

        // executeUpdate() - Any valid SQL/DDL statement except SELECT - should succeed
        try
        {
            String sql = "delete from votes where   PHONE_NUMBER = 3082086134      ";
            java.sql.Statement query = conn.createStatement();
            query.executeUpdate(sql);
        }
        catch (SQLException e) {
            System.err.println("ERROR(executeUpdate(DELETE)): " + e.getMessage());
            fail();
        }
    }

}
