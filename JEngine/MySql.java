package JEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

public class MySql {
    private static final ThreadLocal<Connection> con = new ThreadLocalConnection();
    public static void Close() throws SQLException{
        for (final Connection conn : ThreadLocalConnection.allConnections) {
            System.out.println("MySql 연결종료");
	    conn.close();
	}
    }
    public static Connection GetConnection(){
        return con.get();
    }
}
   final class ThreadLocalConnection extends ThreadLocal<Connection> {
        public static final Collection<Connection> allConnections = new LinkedList<Connection>();
	@Override
	protected final Connection initialValue() {
	    try {
		Class.forName("com.mysql.jdbc.Driver"); // touch the mysql driver
	    } catch (final ClassNotFoundException e) {
		System.err.println("[오류] MYSQL 클래스를 발견할 수 없습니다.");
                if(MainServer.ShowError)e.printStackTrace();
	    }
	    try {
		final Connection con = DriverManager.getConnection(
			"jdbc:mysql://localhost:3306/GNE",
			"root",
			"root");
                System.out.println("MySql 연결완료");
                allConnections.add(con);
		return con;
	    } catch (SQLException e) {
		System.err.println("[오류] 데이터베이스 연결에 오류가 발생했습니다.");
                if(MainServer.ShowError)e.printStackTrace();
		return null;
	    }
	}
    }
