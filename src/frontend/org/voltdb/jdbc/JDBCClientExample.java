package org.voltdb.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

/**
 * Example to test JDBC code. needs to be run after loading benchmark data.
 */
public class JDBCClientExample {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Class.forName("org.voltdb.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:voltdb://localhost:21212");
		String sql = "Select top 10 * from orders;";
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery(sql);
		while(results.next()) {	
			System.out.println(results.getInt(6));
		}
	}

}
