package org.papz06;

import java.sql.*;

public class Function {
    Connection con;

    public static String getSecret() {
        return System.getenv("KEY");
    }

    public static String getEnv(String key) {
        return System.getenv(key);
    }

    public ResultSet executeQuery(String sql) throws SQLException, ClassNotFoundException {
        // System.out.println(sql);
        // Class.forName("oracle.jdbc.driver.OracleDriver");
        try {
            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@194.29.167.132:1521/pdb1.ii.pw.edu.pl", "z06", System.getenv("KEY_DATA_BASE"));
        } catch (Exception e) {
            System.out.println(e);
        }
        Statement stmt = con.createStatement();
        // String sql = "select * from " + tableName;
        return stmt.executeQuery(sql);
    }

    public void closeQuery() throws Exception {
        con.close();
    }
}
