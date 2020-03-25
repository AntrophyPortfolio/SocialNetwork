package dialogs;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Opens up a dialog that allows administrator to edit details about subject
 *
 * @author Antrophy
 */
public class AdminEditSubjectDialogController implements Initializable {

    @FXML
    private Label labelNameOfSubject;
    @FXML
    private ComboBox<String> comboBoxTeacher = new ComboBox();

    private final Connection conn = LoginDialogController.CONN;
    private ObservableList<String> teachers = FXCollections.observableArrayList();
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldShortName;
    @FXML
    private Label labelStudyFields;
    private static boolean subjectEditing;
    public static Stage changeStudyPlans;

    public static boolean isSubjectEditing() {
	return subjectEditing;
    }
    @FXML
    private Button btnSave;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnChange;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	setButtonVisuals(btnSave);
	setButtonVisuals(btnBack);
	setButtonVisuals(btnChange);
	teachers.clear();
	comboBoxTeacher.setItems(teachers);

	labelNameOfSubject.setText("Add new subject");
	try {
	    // GET ONLY TEACHERS AND LOAD TO COMBOBOX
	    String getTeachersQuery = "SELECT u.login, u.jmeno, u.prijmeni\n"
		    + "FROM uzivatele u\n"
		    + "JOIN uzivatelske_role ur\n"
		    + "ON u.id_uzivatel=ur.uzivatel_id_uzivatel\n"
		    + "WHERE ur.role_id_role = ?";

	    PreparedStatement prstGetTeachers = conn.prepareStatement(getTeachersQuery);

	    prstGetTeachers.setInt(1, 2);
	    ResultSet rsGetTeachers = prstGetTeachers.executeQuery();
	    while (rsGetTeachers.next()) {
		String login = rsGetTeachers.getString("login");
		String name = rsGetTeachers.getString("jmeno");
		String lastName = rsGetTeachers.getString("prijmeni");

		teachers.add(name + " " + lastName + "(" + login + ")");
	    }
	    rsGetTeachers.close();
	    prstGetTeachers.close();

	    if (!ManageSubjectsDialogController.isAddingNew()) {
		labelNameOfSubject.setText(ManageSubjectsDialogController.getEditingSubject().getName());
		refreshPlans();

		// GET INFO ABOUT SUBJECT
		String getSubjectInfo = "SELECT nazev, zkratka FROM predmety WHERE id_predmetu = ?";

		PreparedStatement prstSubInfo;

		prstSubInfo = conn.prepareStatement(getSubjectInfo);

		prstSubInfo.setInt(1, ManageSubjectsDialogController.getEditingSubject().getIdSubject());

		ResultSet getSubjInfo = prstSubInfo.executeQuery();
		while (getSubjInfo.next()) {
		    textFieldName.setText(getSubjInfo.getString("nazev"));
		    textFieldShortName.setText(getSubjInfo.getString("zkratka"));
		}
		getSubjInfo.close();
		prstSubInfo.close();

		// IF I AM ADDING DETAILS AND NOT EDITING ALREADY EXISTING
		if (ManageSubjectsDialogController.addingDetails) {
		    // IF EDITING SUBJECT
		} else {
		    String loadSubjectQuery = "SELECT jmeno, prijmeni, login"
			    + " FROM predmety_veskere_info"
			    + " WHERE id_predmetu = ?";
		    PreparedStatement prstLoadData = conn.prepareStatement(loadSubjectQuery);

		    prstLoadData.setInt(1, ManageSubjectsDialogController.getEditingSubject().getIdSubject());
		    ResultSet rsLoadData = prstLoadData.executeQuery();
		    while (rsLoadData.next()) {
			String teacherName = rsLoadData.getString("jmeno");
			String teacherLastName = rsLoadData.getString("prijmeni");
			String teacherLogin = rsLoadData.getString("login");

			if (teacherName == null) {
			    comboBoxTeacher.setValue("");
			} else {
			    comboBoxTeacher.setValue(teacherName + " " + teacherLastName + "(" + teacherLogin + ")");
			}
		    }
		    rsLoadData.close();
		    prstLoadData.close();
		}
	    }
	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditSubjectDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Saves details into the database and updates values accordingly (or
     * inserts)
     *
     * @param event
     */
    @FXML
    private void handleButtonSaveAction(ActionEvent event) {
	try {
	    if (ManageSubjectsDialogController.isAddingNew()) {
		String addSubject = "INSERT INTO predmety (nazev, zkratka, id_ucitele) VALUES (?, ?, ?)";
		PreparedStatement prstAdd = conn.prepareStatement(addSubject);
		prstAdd.setString(1, textFieldName.getText());
		prstAdd.setString(2, textFieldShortName.getText());
		prstAdd.setInt(3, findUserID(comboBoxTeacher.getValue()));
		prstAdd.executeUpdate();
		prstAdd.close();
	    }
	    // UPDATE TEACHER
	    String updateTeacher = "UPDATE predmety SET id_ucitele=?, nazev = ?, zkratka = ? WHERE id_predmetu = ?";
	    PreparedStatement prstUpdateTeacher = conn.prepareStatement(updateTeacher);

	    prstUpdateTeacher.setInt(1, findUserID(comboBoxTeacher.getValue()));
	    prstUpdateTeacher.setString(2, textFieldName.getText());
	    prstUpdateTeacher.setString(3, textFieldShortName.getText());
	    prstUpdateTeacher.setInt(4, ManageSubjectsDialogController.getEditingSubject().getIdSubject());

	    prstUpdateTeacher.executeUpdate();
	    prstUpdateTeacher.close();

	    handleButtonBackAction(null);

	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditSubjectDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Closes current dialog
     *
     * @param event
     */
    @FXML
    private void handleButtonBackAction(ActionEvent event) {
	ManageSubjectsDialogController.editSubjectDialog.close();
    }

    /**
     * Finds userID from the database according to the given login information
     *
     * @param login
     * @return
     */
    private int findUserID(String login) {
	String userLogin = "";
	boolean readingLogin = false;
	for (int i = 0; i < login.length(); i++) {
	    if (login.charAt(i) == '(') {
		readingLogin = true;
		continue;
	    }
	    if (login.charAt(i) == ')') {
		break;
	    }
	    if (readingLogin) {
		userLogin += login.charAt(i);
	    }
	}
	String getUserIdQuery = "SELECT id_uzivatel FROM uzivatele WHERE login = ?";
	try {
	    PreparedStatement prstUserID = conn.prepareStatement(getUserIdQuery);
	    prstUserID.setString(1, userLogin);

	    ResultSet rsUserID = prstUserID.executeQuery();
	    while (rsUserID.next()) {
		int userID = rsUserID.getInt("id_uzivatel");
		rsUserID.close();
		prstUserID.close();

		return userID;
	    }
	    rsUserID.close();
	    prstUserID.close();
	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditSubjectDialogController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
	return 0;
    }

    /**
     * Changes study plans for the current subject
     *
     * @param event
     */
    @FXML
    private void handleButtonChangeAction(ActionEvent event) {
	subjectEditing = true;
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/EditProfileChangePlanDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    stage.setOnHidden(e -> {
		refreshPlans();
		subjectEditing = false;

	    });
	    changeStudyPlans = stage;
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Reloads all the study plans from the database
     */
    private void refreshPlans() {
	String querySelectPlans = "SELECT nazev_planu FROM predmety_veskere_info WHERE id_predmetu = ?";
	try {
	    PreparedStatement prstSelectPlans = conn.prepareStatement(querySelectPlans);

	    prstSelectPlans.setInt(1, ManageSubjectsDialogController.getEditingSubject().getIdSubject());

	    ResultSet rsPlans = prstSelectPlans.executeQuery();

	    boolean prvni = true;
	    String allPlans = "";
	    while (rsPlans.next()) {
		if (prvni) {
		    allPlans = rsPlans.getString("nazev_planu");
		} else {
		    allPlans += ", " + rsPlans.getString("nazev_planu");
		}
		prvni = false;
	    }
	    rsPlans.close();
	    prstSelectPlans.close();
	    labelStudyFields.setText(allPlans);

	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditUserDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Sets a custom button visuals for this dialog
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
