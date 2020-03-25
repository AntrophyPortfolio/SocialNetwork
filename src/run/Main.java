package run;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Antrophy
 */
public class Main extends Application {

    /**
     *
     */
    public static Stage loginStage;

    @Override
    public void start(Stage stage) throws Exception {
	Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginDialog.fxml"));

	Scene scene = new Scene(root);
	loginStage = stage;
	stage.setScene(scene);
	stage.show();
	Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	    try {
		dialogs.LoginDialogController.CONN.close();
	    } catch (SQLException ex) {
		Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}));
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
	launch(args);
    }

}
