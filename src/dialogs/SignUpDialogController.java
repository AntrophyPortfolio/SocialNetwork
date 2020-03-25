package dialogs;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import resources.Alerts;
import resources.IAlerts;
import run.Main;

/**
 * Opens up a dialog for signing up to a network
 *
 * @author Antrophy
 */
public class SignUpDialogController implements Initializable {

    private IAlerts ALERT = new Alerts();
    @FXML
    private TextField textFieldLogin;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldSurname;
    @FXML
    private TextArea textAreaDescription;
    @FXML
    private PasswordField passFieldPassword;
    @FXML
    private PasswordField passFieldConfirm;
    private final Connection conn = LoginDialogController.CONN;
    @FXML
    private ComboBox<String> comboBoxStudyPlan = new ComboBox();
    @FXML
    private ComboBox<String> comboBoxFieldOfStudy = new ComboBox();
    @FXML
    private Spinner<Integer> spinnerStartedSchool;

    private ObservableList<String> fields = FXCollections.observableArrayList();
    private ObservableList<String> degrees = FXCollections.observableArrayList();
    @FXML
    private Button btnSignUp;
    @FXML
    private Button btnCancel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	setButtonVisuals(btnSignUp);
	setButtonVisuals(btnCancel);
	spinnerStartedSchool.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1970, 2020));

	comboBoxStudyPlan.setDisable(true);
	// GET FIELDS FROM DATABASE AND PASS THEM TO LIST FOR DISPLAY
	try {
	    fields.clear();
	    String query = "SELECT DISTINCT nazev_oboru from studijni_obory_a_plany";
	    PreparedStatement prst = conn.prepareStatement(query);

	    ResultSet rs = prst.executeQuery();
	    while (rs.next()) {
		String field = rs.getString("nazev_oboru");
		fields.add(field);
	    }
	    rs.close();
	    prst.close();

	    comboBoxFieldOfStudy.setItems(fields);

	    ChangeListener<String> listener = (observable, oldValue, newValue) -> {
		try {
		    // GET AVAILABLLE STUDIES FOR SELECTED FIELD
		    degrees.clear();
		    String queryDegree = "SELECT nazev_planu FROM studijni_obory_a_plany WHERE nazev_oboru=?";

		    PreparedStatement prstDegree = conn.prepareStatement(queryDegree);

		    prstDegree.setString(1, comboBoxFieldOfStudy.getValue());

		    ResultSet rsDegree = prstDegree.executeQuery();
		    while (rsDegree.next()) {
			String dgree = rsDegree.getString("nazev_planu");

			degrees.add(dgree);
		    }
		    rsDegree.close();
		    prstDegree.close();

		    comboBoxStudyPlan.setItems(degrees);
		    comboBoxStudyPlan.setDisable(false);

		} catch (SQLException ex) {
		    Logger.getLogger(SignUpDialogController.class.getName()).log(Level.SEVERE, null, ex);
		}
	    };

	    comboBoxFieldOfStudy.valueProperty().addListener(listener);

	} catch (SQLException ex) {
	    Logger.getLogger(SignUpDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * When sign up is clicked, get all info into the database and save it
     *
     * @param event
     */
    @FXML
    private void handleButtonSignUpAction(ActionEvent event) {
	try {
	    if (textFieldLogin.getText().isEmpty()
		    || textFieldName.getText().isEmpty()
		    || textFieldSurname.getText().isEmpty()
		    || passFieldConfirm.getText().isEmpty()
		    || passFieldPassword.getText().isEmpty()
		    || comboBoxFieldOfStudy.getSelectionModel().isEmpty()
		    || comboBoxStudyPlan.getSelectionModel().isEmpty()) {

		ALERT.showValuesNotFilledAlert();
		return;
	    }
	    if (!passFieldConfirm.getText().equals(passFieldPassword.getText())) {
		ALERT.showPasswordsDontMatchAlert();
		return;
	    }
	    if (textFieldLogin.getText().length() != 6) {
		ALERT.showIncorrectLoginLengthAlert();
		return;
	    }

	    // CHECK WHETHER LOGIN ALREADY EXISTS
	    Statement st = conn.createStatement();
	    String checkLoginUsed = "SELECT login FROM uzivatele";

	    ResultSet rsLogin = st.executeQuery(checkLoginUsed);
	    while (rsLogin.next()) {
		if (textFieldLogin.getText().equals(rsLogin.getString("login"))) {
		    ALERT.showLoginAlreadyInUseAlert();
		    return;
		}
	    }
	    st.close();
	    rsLogin.close();

	    CallableStatement clStInsert = conn.prepareCall("{call proc_vloz_novy_uzivatel(?,?,?,?,?,?,?,?,?)}");
	    clStInsert.setString(1, textFieldLogin.getText());
	    clStInsert.setString(2, passFieldPassword.getText());
	    clStInsert.setInt(3, 0);
	    clStInsert.setString(4, textFieldName.getText());
	    clStInsert.setString(5, textFieldSurname.getText());
	    clStInsert.setString(6, spinnerStartedSchool.getValue().toString());
	    clStInsert.setString(7, textAreaDescription.getText());
	    clStInsert.setString(8, comboBoxStudyPlan.getValue());
	    clStInsert.setString(9, comboBoxFieldOfStudy.getValue());
	    clStInsert.execute();
	    clStInsert.close();
	} catch (SQLException ex) {
	    Logger.getLogger(SignUpDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
	handleButtonCancelAction(null);
    }

    /**
     * When cancel button is pressed, return to the login dialog window
     * @param event 
     */
    @FXML
    private void handleButtonCancelAction(ActionEvent event) {
	dialogs.LoginDialogController.registerWindow.close();
	Main.loginStage.show();
    }

    /**
     * Sets a dark theme visuals for buttons.
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
