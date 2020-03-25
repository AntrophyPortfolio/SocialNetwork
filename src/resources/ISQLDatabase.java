package resources;

import java.sql.Connection;


public interface ISQLDatabase {
    
    /**
     * This method starts a connection to the database
     * @return
     */
    Connection getConnection();
    
    
    
}
