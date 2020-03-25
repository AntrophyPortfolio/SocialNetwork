package dialogs;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * Opens up a dialog that allows users to set their notification
 *
 * @author Antrophy
 */
public class AddNotificationDialogController implements Initializable {

    @FXML
    private TextField textFieldNameNotify;
    @FXML
    private DatePicker datePickerNotify;

    private final Connection conn = LoginDialogController.CONN;
    @FXML
    private TextField textFieldDescriptionNotify;

    private static int notificationID = 0;
    @FXML
    private Button buttonRemove;
    @FXML
    private Button btnNotifyMe;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	loadData();
	setButtonVisuals(buttonRemove);
	setButtonVisuals(btnNotifyMe);
    }

    /**
     * Loads all the notification information existing and shows in the text
     * fields
     */
    private void loadData() {

	if (MainWindowController.notifyIsSet) {
	    buttonRemove.setVisible(true);
	    try {

		Statement st = conn.createStatement();

		String query = "SELECT p.nazev, TO_CHAR(p.datum, 'yyyy-MM-dd') AS datum, p.poznamka\n"
			+ "FROM pripominky p\n"
			+ "INNER JOIN pripominky_kontaktu pk\n"
			+ "ON p.id_pripominky=pk.pripominky_id_pripominky\n"
			+ "WHERE pk.id_adresat=" + MainWindowController.currentContact.getContactID() + ""
			+ " AND pk.kontakt_id_kontakt=" + LoginDialogController.loggedContact.getContactID() + "\n"
			+ "OR pk.id_adresat=" + LoginDialogController.loggedContact.getContactID() + " AND pk.kontakt_id_kontakt=" + MainWindowController.currentContact.getContactID();

		ResultSet rsNotify = st.executeQuery(query);
		while (rsNotify.next()) {
		    String notifyName = rsNotify.getString("nazev");
		    String date = rsNotify.getString("datum");
		    String description = rsNotify.getNString("poznamka");

		    textFieldNameNotify.setText(notifyName);
		    textFieldDescriptionNotify.setText(description);
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		    LocalDate notificationDate = LocalDate.parse(date, formatter);
		    datePickerNotify.setValue(notificationDate);
		}
		rsNotify.close();
		// FIND LATEST INSERTED ROW
		String selectLatestRow = "SELECT p.id_pripominky\n"
			+ "FROM pripominky p\n"
			+ "INNER JOIN pripominky_kontaktu pk\n"
			+ "ON p.id_pripominky=pk.pripominky_id_pripominky\n"
			+ "WHERE pk.id_adresat=" + MainWindowController.currentContact.getContactID() + ""
			+ " AND pk.kontakt_id_kontakt=" + LoginDialogController.loggedContact.getContactID() + "\n"
			+ "OR pk.id_adresat=" + LoginDialogController.loggedContact.getContactID() + ""
			+ " AND pk.kontakt_id_kontakt=" + MainWindowController.currentContact.getContactID();

		ResultSet rs = st.executeQuery(selectLatestRow);
		while (rs.next()) {
		    notificationID = rs.getInt("id_pripominky");
		}
		rs.close();
		st.close();

	    } catch (SQLException ex) {
		Logger.getLogger(AddNotificationDialogController.class.getName()).log(Level.SEVERE, null, ex);
	    }
	} else {
	    buttonRemove.setVisible(false);
	}
    }

    /**
     * Saves a notification to the database and shows in the application
     *
     * @param event
     */
    @FXML
    private void handleButtonNotifyAction(ActionEvent event) {

	String nameNotify = textFieldNameNotify.getText();
	String descriptionNotify = textFieldDescriptionNotify.getText();
	LocalDate dateNotify = datePickerNotify.getValue();

	try {
	    Statement st = conn.createStatement();

	    if (MainWindowController.notifyIsSet) {

		String updateNotification = "UPDATE pripominky SET datum=TO_DATE('" + dateNotify + "', 'YYYY-MM-DD'), nazev='" + nameNotify + "', poznamka='" + descriptionNotify + "' WHERE id_pripominky=" + notificationID;
		st.executeQuery(updateNotification);
	    } else {
		String insertNotification = "INSERT INTO pripominky (datum, nazev, poznamka)"
			+ " VALUES(TO_DATE('" + dateNotify + "', 'YYYY-MM-DD'),"
			+ "'" + nameNotify + "', "
			+ "'" + descriptionNotify + "')";
		st.executeQuery(insertNotification);

		String lastRowQuery = "SELECT MAX(id_pripominky) AS id_pripominky FROM pripominky";

		ResultSet rs = st.executeQuery(lastRowQuery);

		while (rs.next()) {
		    notificationID = rs.getInt("id_pripominky");
		}

		rs.close();
		String queryToNotifyContacts = "INSERT INTO pripominky_kontaktu (pripominky_id_pripominky, kontakt_id_kontakt, id_adresat)"
			+ " VALUES (" + notificationID + ", " + LoginDialogController.loggedContact.getContactID() + ","
			+ " " + MainWindowController.currentContact.getContactID() + ")";
		st.executeQuery(queryToNotifyContacts);
	    }

	    MainWindowController.addNotification.close();
	    loadData();

	    st.close();
	} catch (SQLException ex) {
	    Logger.getLogger(AddNotificationDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Completely removes a notification between two users
     *
     * @param event
     */
    @FXML
    private void handleButtonRemoveAction(ActionEvent event) {
	try {

	    Statement st = conn.createStatement();
	    String queryDeleteNotifyContacts = "DELETE FROM pripominky_kontaktu WHERE pripominky_id_pripominky=" + notificationID;
	    st.executeQuery(queryDeleteNotifyContacts);

	    String query = "DELETE FROM pripominky WHERE id_pripominky=" + notificationID;
	    st.executeQuery(query);

	    st.close();
	    MainWindowController.notifyIsSet = false;
	    MainWindowController.addNotification.close();
	} catch (SQLException ex) {
	    Logger.getLogger(AddNotificationDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Sets a custom button visuals in this dialog
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
