package dialogs;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import resources.Alerts;
import resources.Contacts;
import resources.IAlerts;
import resources.ISQLDatabase;
import resources.SQLDatabase;

/**
 * The first dialog to open, it shows login dialog window
 *
 * @author Antrophy
 */
public class LoginDialogController implements Initializable {

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField textFieldName;

    public static Stage mainWindow;
    public static Stage registerWindow;
    private static final IAlerts ALERT = new Alerts();
    private static ISQLDatabase database = new SQLDatabase();
    public static final Connection CONN = database.getConnection();

    public static Contacts loggedContact;
    public static Contacts previousContact = null;
    public static boolean guestLogin;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnSignUp;
    @FXML
    private Button btnContinueWithoutLogin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

	setButtonVisuals(btnLogin);
	setButtonVisuals(btnSignUp);
	setButtonVisuals(btnContinueWithoutLogin);
	textFieldName.setOnAction(event -> {
	    handleButtonLoginAction();
	});
	passwordField.setOnAction(event -> {
	    handleButtonLoginAction();
	});
    }

    /**
     * Clicking login button will compare the credentials entered with the ones
     * in database, if it matches, show main window
     */
    @FXML
    private void handleButtonLoginAction() {
	guestLogin = false;
	try {
	    String queryLogin = "SELECT u.blokace, u.id_uzivatel, u.login,u.heslo, u.jmeno, u.prijmeni, k.id_kontakt\n"
		    + "FROM uzivatele u\n"
		    + "INNER JOIN kontakty k\n"
		    + "ON u.id_uzivatel=k.uzivatele_id_uzivatel";
	    Statement st = CONN.createStatement();

	    ResultSet rs = st.executeQuery(queryLogin);

	    while (rs.next()) {
		String login = rs.getString("login");
		String password = rs.getString("heslo");

		if (login.equals(textFieldName.getText()) && password.equals(passwordField.getText())) {
		    int ban = rs.getInt("blokace");
		    if (ban == 1) {
			ALERT.showBannedAccountAlert();
			rs.close();
			return;
		    }
		    int userID = rs.getInt("id_uzivatel");
		    int contactID = rs.getInt("id_kontakt");
		    String name = rs.getString("jmeno");
		    String lastName = rs.getString("prijmeni");

		    loggedContact = new Contacts(userID, contactID, login, name, lastName, ban);

		    textFieldName.setText("");
		    passwordField.setText("");
		    Stage stage = new Stage();
		    stage.setMinHeight(700);
		    stage.setMinWidth(800);
		    Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));

		    Scene scene = new Scene(root);
		    stage.setResizable(true);
		    stage.setMaximized(true);
		    mainWindow = stage;
		    stage.setScene(scene);
		    stage.show();
		    rs.close();
		    return;
		}
	    }
	    rs.close();
	    st.close();
	    ALERT.showIncorrectCredentialsAlert();
	} catch (SQLException ex) {
	    ALERT.showCouldNotExecuteCommand();
	} catch (IOException ex) {
	    Logger.getLogger(LoginDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    /**
     * Clicking sign up button will open up sign up dialog where user can sign
     * up to the network
     *
     * @param event
     */
    @FXML
    private void handleButtonSignUpAction(ActionEvent event) {
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignUpDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    registerWindow = stage;
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    ALERT.showCouldNotLoadWindowAlert();
	}
    }

    /**
     * Clicking will allow an unregistered user to enter the network with
     * limited visibility
     *
     * @param event
     */
    @FXML
    private void handleButtonContinueWOloginAction(ActionEvent event) {
	try {
	    guestLogin = true;
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));

	    Scene scene = new Scene(root);
	    stage.setResizable(true);
	    stage.setMaximized(true);
	    mainWindow = stage;
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    Logger.getLogger(LoginDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Sets custom visuals for buttons in this dialog
     *
     * @param button
     */
    private void setButtonVisuals(Button button) {
	button.setBackground(new Background(new BackgroundFill(Color.web("aaaabb"), CornerRadii.EMPTY, Insets.EMPTY)));

	button.addEventHandler(MouseEvent.MOUSE_ENTERED,
		new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		button.setBackground(new Background(new BackgroundFill(Color.web("ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
	    }
	});
	button.addEventHandler(MouseEvent.MOUSE_EXITED,
		new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		button.setBackground(new Background(new BackgroundFill(Color.web("aaaabb"), CornerRadii.EMPTY, Insets.EMPTY)));
	    }
	});
    }

}
