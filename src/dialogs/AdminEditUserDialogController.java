package dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import resources.Alerts;
import resources.Field;
import resources.IAlerts;

/**
 * Opens up a dialog where adminsitrator can edit details about user
 *
 * @author Antrophy
 */
public class AdminEditUserDialogController implements Initializable {

    @FXML
    private Label labelEditedUsername;
    @FXML
    private TextField textFieldLogin;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private Spinner<Integer> spinnerStartedSchool;
    @FXML
    private TextArea textAreaDescription;

    private final Connection conn = LoginDialogController.CONN;
    @FXML
    private CheckBox checkBoxAdmin;
    @FXML
    private CheckBox checkBoxStudent;
    @FXML
    private CheckBox checkBoxTeacher;

    private boolean adminWasSelected;
    private boolean studentWasSelected;
    private boolean teacherWasSelected;
    private String editingUserField;
    public static Stage changeStudyPlans;
    private static boolean adminEditing;

    private IAlerts ALERT = new Alerts();
    private int photoID;
    private File file;

    @FXML
    private ComboBox<Field> comboBoxField = new ComboBox();
    @FXML
    private Label labelStudyPlans;
    private ObservableList<Field> fields = FXCollections.observableArrayList();
    private static Field chosenField;

    public static Field getChosenField() {
	return chosenField;
    }

    public static boolean isAdminEditing() {
	return adminEditing;
    }
    @FXML
    private ImageView imageViewProfilePicture;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSetNew;
    @FXML
    private Button btnInsertPhoto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	setButtonVisuals(btnSave);
	setButtonVisuals(btnCancel);
	setButtonVisuals(btnSetNew);
	setButtonVisuals(btnInsertPhoto);
	labelEditedUsername.setText(ManageUsersDialogController.getEditingUser().getLogin());

	refreshPlans();
	adminWasSelected = false;
	studentWasSelected = false;
	teacherWasSelected = false;

	checkBoxAdmin.setSelected(false);
	checkBoxStudent.setSelected(false);
	checkBoxTeacher.setSelected(false);

	comboBoxField.setItems(fields);
	try {

	    // LOAD DATA TO COMBOBOX
	    String queryGetFields = "SELECT * FROM studijni_obory";
	    comboBoxField.setItems(fields);

	    PreparedStatement prstGetFields = conn.prepareStatement(queryGetFields);

	    ResultSet rsGetFields = prstGetFields.executeQuery();
	    while (rsGetFields.next()) {
		int fieldID = rsGetFields.getInt("id_oboru");
		String fieldname = rsGetFields.getString("nazev");

		fields.add(new Field(fieldID, fieldname));
	    }
	    rsGetFields.close();
	    prstGetFields.close();
	    spinnerStartedSchool.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1970, 2020));

	    // LOADING EXISTING DATA FROM DATABASE TO SHOW IN TEXTFIELDS
	    String getUserDataQuery = "SELECT u.login, u.heslo, u.jmeno, u.prijmeni, u.poznamka, u.rok_studia, sou.nazev AS nazev_oboru "
		    + "FROM uzivatele u "
		    + "INNER JOIN studijni_obory_uzivatelu sou "
		    + "ON sou.id_uzivatel=u.id_uzivatel "
		    + "WHERE u.id_uzivatel=?";

	    PreparedStatement prstUserData = conn.prepareStatement(getUserDataQuery);
	    prstUserData.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());

	    ResultSet rs = prstUserData.executeQuery();
	    while (rs.next()) {
		String login = rs.getString("login");
		String password = rs.getString("heslo");
		String lastName = rs.getString("prijmeni");
		String name = rs.getString("jmeno");
		int startedSchoolYear = rs.getInt("rok_studia");
		String description = rs.getNString("poznamka");

		textFieldLogin.setText(login);
		textFieldPassword.setText(password);
		textFieldName.setText(name);
		textFieldLastName.setText(lastName);
		spinnerStartedSchool.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1970, 2020, startedSchoolYear));
		textAreaDescription.setText(description);
		fields.forEach(e -> {
		    try {
			if (e.getFieldName().equals(rs.getString("nazev_oboru"))) {
			    comboBoxField.setValue(e);
			}
		    } catch (SQLException ex) {
			Logger.getLogger(AdminEditUserDialogController.class.getName()).log(Level.SEVERE, null, ex);
		    }
		});
	    }
	    prstUserData.close();
	    rs.close();
	    String getRoles = "SELECT role_id_role, uzivatel_id_uzivatel\n"
		    + "FROM uzivatelske_role\n"
		    + "WHERE uzivatel_id_uzivatel=?";

	    PreparedStatement prstm = conn.prepareStatement(getRoles);
	    prstm.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());
	    ResultSet rsRoles = prstm.executeQuery();
	    while (rsRoles.next()) {
		int role = rsRoles.getInt("role_id_role");

		switch (role) {
		    case 1:
			checkBoxAdmin.setSelected(true);
			adminWasSelected = true;
			continue;
		    case 2:
			checkBoxTeacher.setSelected(true);
			teacherWasSelected = true;
			continue;
		    case 3:
			checkBoxStudent.setSelected(true);
			studentWasSelected = true;
		}
	    }
	    prstm.close();
	    rsRoles.close();

	    String getFotky = "SELECT F.ID_FOTKY, F.FOTKA AS FO FROM ST55447.FOTKY F\n"
		    + "INNER JOIN FOTKY_UZIVATELU FU ON FU.FOTKY_ID_FOTKY = F.ID_FOTKY\n"
		    + "WHERE FU.UZIVATELE_ID_UZIVATEL = ? ORDER BY ID_FOTKY";

	    PreparedStatement prstmFotky = conn.prepareStatement(getFotky);
	    prstmFotky.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());
	    ResultSet rsFotky = prstmFotky.executeQuery();
	    while (rsFotky.next()) {
		Blob b = rsFotky.getBlob("fo");
		imageViewProfilePicture.setImage(new Image(b.getBinaryStream()));
	    }
	    prstmFotky.close();
	    rsFotky.close();

	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditUserDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Saves all details into the database's table
     *
     * @param event
     * @throws FileNotFoundException
     */
    @FXML
    private void handleButtonSaveAction(ActionEvent event) throws FileNotFoundException {
	try {
	    Statement st = conn.createStatement();

	    String query = "UPDATE uzivatele "
		    + "SET login='" + textFieldLogin.getText() + "', "
		    + "heslo='" + textFieldPassword.getText() + "', "
		    + "jmeno='" + textFieldName.getText() + "', "
		    + "prijmeni='" + textFieldLastName.getText() + "', "
		    + "rok_studia=" + spinnerStartedSchool.getValue() + ", "
		    + "poznamka='" + textAreaDescription.getText() + "' "
		    + "WHERE id_uzivatel=" + ManageUsersDialogController.getEditingUser().getAccountID();

	    st.executeQuery(query);
	    st.close();

	    updateRoles();
	    refreshPlans();
	    refreshPhoto();
	    handleButtonCancelAction(null);
	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditUserDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Closes current dialog
     *
     * @param event
     */
    @FXML
    private void handleButtonCancelAction(ActionEvent event) {
	ManageUsersDialogController.adminEditUserDialog.close();
    }

    /**
     * Determines the value before and after check boxes value and updates it
     * accordingly in database
     */
    private void updateRoles() {
	try {

	    String removeRolesQuery = "DELETE FROM uzivatelske_role WHERE uzivatel_id_uzivatel=? AND role_id_role=?";
	    PreparedStatement removeRoles = conn.prepareStatement(removeRolesQuery);

	    String insertRolesQuery = "INSERT INTO uzivatelske_role (uzivatel_id_uzivatel, role_id_role) VALUES (?, ?)";
	    PreparedStatement insertRoles = conn.prepareStatement(insertRolesQuery);

	    if (teacherWasSelected) {
		if (!checkBoxTeacher.isSelected()) {

		    removeRoles.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());
		    removeRoles.setInt(2, 2);
		    removeRoles.executeUpdate();
		}
	    } else {
		if (checkBoxTeacher.isSelected()) {
		    insertRoles.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());
		    insertRoles.setInt(2, 2);
		    insertRoles.executeUpdate();

		}
	    }
	    if (adminWasSelected) {
		if (!checkBoxAdmin.isSelected()) {
		    removeRoles.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());
		    removeRoles.setInt(2, 1);
		    removeRoles.executeUpdate();

		}
	    } else {
		if (checkBoxAdmin.isSelected()) {
		    insertRoles.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());
		    insertRoles.setInt(2, 1);
		    insertRoles.executeUpdate();

		}
	    }
	    if (studentWasSelected) {
		if (!checkBoxStudent.isSelected()) {
		    removeRoles.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());
		    removeRoles.setInt(2, 2);
		    removeRoles.executeUpdate();
		}
	    } else {
		if (checkBoxStudent.isSelected()) {
		    insertRoles.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());
		    insertRoles.setInt(2, 3);
		    insertRoles.executeUpdate();
		}
	    }
	    removeRoles.close();
	    insertRoles.close();
	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditUserDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Refreshes study plans for current user
     */
    private void refreshPlans() {
	String querySelectPlans = "SELECT nazev_planu FROM studijni_plany_uzivatelu WHERE id_uzivatel = ?";
	try {
	    PreparedStatement prstSelectPlans = conn.prepareStatement(querySelectPlans);

	    prstSelectPlans.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());

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
	    labelStudyPlans.setText(allPlans);

	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditUserDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Finds an ID of study plan if name entered
     *
     * @param nameOfPlan
     * @return
     */
    private int findStudyPlanID(String nameOfPlan) {
	try {
	    String getIDplanQuery = "SELECT id_studijniho_planu FROM studijni_obory_a_plany WHERE nazev_planu=? AND nazev_oboru = ?";
	    PreparedStatement prstIDplan;

	    prstIDplan = conn.prepareStatement(getIDplanQuery);

	    prstIDplan.setString(1, nameOfPlan);
	    prstIDplan.setString(2, editingUserField);

	    ResultSet rsIDplan = prstIDplan.executeQuery();
	    int idPlan = 0;
	    while (rsIDplan.next()) {
		idPlan = rsIDplan.getInt("id_studijniho_planu");
		rsIDplan.close();
		prstIDplan.close();
		return idPlan;
	    }
	    rsIDplan.close();
	    prstIDplan.close();
	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
	}
	return 0;
    }

    /**
     * Opens up a dialog for setting a new study plans for specific user
     *
     * @param event
     */
    @FXML
    private void handleButtonChangeAction(ActionEvent event) {
	if (comboBoxField.getValue() == null) {
	    return;
	}
	adminEditing = true;
	chosenField = comboBoxField.getValue();
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/EditProfileChangePlanDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    stage.setOnHidden(e -> {
		refreshPlans();
		adminEditing = false;
	    });
	    changeStudyPlans = stage;
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Allows an administrator to insert a foto for edited user
     *
     * @param event
     * @throws FileNotFoundException
     */
    @FXML
    private void handleButtonInsertPhotoAction(ActionEvent event) throws FileNotFoundException {
	FileChooser fileChooser = new FileChooser();
	fileChooser.setTitle("Select Profile Picture");
	fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
	file = fileChooser.showOpenDialog(null);
	if (file != null) {
	    Image image = new Image(file.toURI().toString());
	    imageViewProfilePicture.setImage(image);
	}
    }

    /**
     * Reloads a photo from database for current user
     *
     * @throws FileNotFoundException
     */
    private void refreshPhoto() throws FileNotFoundException {
	if (file != null) {
	    try {
		String queryGetIdFotky = "SELECT fotky_id_fotky FROM fotky_uzivatelu WHERE uzivatele_id_uzivatel = ?";

		PreparedStatement prstGetIdFotky = conn.prepareStatement(queryGetIdFotky);

		prstGetIdFotky.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());

		ResultSet rsIdFotky = prstGetIdFotky.executeQuery();

		while (rsIdFotky.next()) {
		    int fotkaID = rsIdFotky.getInt("fotky_id_fotky");

		    String queryDeleteFotkyUzivatelu = "DELETE FROM fotky_uzivatelu WHERE uzivatele_id_uzivatel= ?";
		    PreparedStatement prstDeleteFotkyUzivatelu = conn.prepareStatement(queryDeleteFotkyUzivatelu);

		    prstDeleteFotkyUzivatelu.setInt(1, ManageUsersDialogController.getEditingUser().getAccountID());

		    prstDeleteFotkyUzivatelu.executeUpdate();
		    prstDeleteFotkyUzivatelu.close();

		    if (fotkaID != 0) {
			String queryDeleteFotky = "DELETE FROM fotky WHERE id_fotky = ?";
			PreparedStatement prstDeleteFotky = conn.prepareStatement(queryDeleteFotky);

			prstDeleteFotky.setInt(1, fotkaID);

			prstDeleteFotky.executeUpdate();
			prstDeleteFotky.close();
		    }

		}
		rsIdFotky.close();
		prstGetIdFotky.close();
		String query = "INSERT INTO fotky (fotka,nazev_souboru, typ_souboru, pripona_souboru, datum_nahrani) "
			+ "VALUES ( ?, ?, ?, ?, CURRENT_TIMESTAMP)";
		PreparedStatement prstInsertFotky = conn.prepareStatement(query);
		FileInputStream in = new FileInputStream(file);
		prstInsertFotky.setBinaryStream(1, in, (int) file.length());
		prstInsertFotky.setNString(2, file.getName().replace(".", ""));
		prstInsertFotky.setNString(3, getFileType(file));
		prstInsertFotky.setNString(4, getFileExtension(file));
		prstInsertFotky.executeUpdate();
		prstInsertFotky.close();

		String lastPhotoQuery = "SELECT MAX(id_fotky) AS id_fotky FROM fotky";

		PreparedStatement prstLastPhoto = conn.prepareStatement(lastPhotoQuery);
		ResultSet rs = prstLastPhoto.executeQuery();

		photoID = 0;
		while (rs.next()) {
		    photoID = rs.getInt("id_fotky");
		}
		rs.close();
		prstLastPhoto.close();

		String queryFotkyUzivatelu = "INSERT INTO fotky_uzivatelu (fotky_id_fotky, uzivatele_id_uzivatel) VALUES (?, ?)";
		PreparedStatement prstInsertFotkyUzivatelu = conn.prepareStatement(queryFotkyUzivatelu);
		prstInsertFotkyUzivatelu.setInt(1, photoID);
		prstInsertFotkyUzivatelu.setInt(2, ManageUsersDialogController.getEditingUser().getAccountID());
		prstInsertFotkyUzivatelu.executeUpdate();
		prstInsertFotkyUzivatelu.close();

	    } catch (SQLException ex) {
		ALERT.showCouldNotExecuteCommand();
	    }
	}
    }

    /**
     * Returns a file extension (foto extension)
     *
     * @param file
     * @return
     */
    private static String getFileExtension(File file) {
	String fileName = file.getName();
	if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
	    return fileName.substring(fileName.lastIndexOf("."));
	} else {
	    return "";
	}
    }

    /**
     * Returns a file type (foto type)
     *
     * @param file
     * @return
     */
    private static String getFileType(File file) {
	String fileName = file.getName();
	if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
	    return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
	} else {
	    return "";
	}
    }

    /**
     * Sets a custom button visuals for current dialog
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
