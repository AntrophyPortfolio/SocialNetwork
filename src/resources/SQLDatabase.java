package resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDatabase implements ISQLDatabase {
private static final IAlerts ALERT = new Alerts(); 
    @Override
    public Connection getConnection() {
	try {
	    // create our mysql database connection
	    String myDriver = "oracle.jdbc.OracleDriver";
	    String myUrl = "jdbc:oracle:thin:@fei-sql1.upceucebny.cz:1521:IDAS";
	    Class.forName(myDriver);
	    
	    Connection conn = DriverManager.getConnection(myUrl, "ST55447", "hesloheslo");
	    return conn;
	} catch (ClassNotFoundException | SQLException ex) {
	    ALERT.showCouldNotEstablishConnectionAlert();
	}
	return null;
	
    }

}
