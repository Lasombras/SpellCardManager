package spell.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import spell.Activator;

public class SessionFactory {

	public SessionFactory() {
	}

	public Session openSession() throws ClassNotFoundException, SQLException {
	    Class.forName("org.sqlite.JDBC");
	    Connection conn = DriverManager.getConnection("jdbc:sqlite:" + Activator.getPath() + Activator.getDataFolder() + Activator.DATABASE_FILENAME);
		return new Session(conn);
	}
}
