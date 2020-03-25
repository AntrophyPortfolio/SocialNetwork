package dialogs;

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
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import resources.Field;
import resources.StudyPlans;

/**
 * Opens up a dialog where administrator can set a study plan for specific user
 *
 * @author Antrophy
 */
public class EditProfileChangePlanDialogController implements Initializable {

    @FXML
    private TableView<StudyPlans> tableView;
    @FXML
    private TableColumn<StudyPlans, String> columnName;
    private ObservableList<StudyPlans> plans = FXCollections.observableArrayList();
    private final Connection conn = LoginDialogController.CONN;
    private Field chosenField;
    private int editingID;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	setButtonVisuals(btnSave);
	setButtonVisuals(btnBack);
	plans.clear();
	columnName.setCellValueFactory(new PropertyValueFactory<>("planName"));

	tableView.setItems(plans);
	tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

	try {
	    // DEPENDING ON WHERE I CAME FROM 
	    if (AdminEditUserDialogController.isAdminEditing()) {
		chosenField = AdminEditUserDialogController.getChosenField();
		editingID = ManageUsersDialogController.getEditingUser().getAccountID();

		String queryGetPlans = "SELECT id_studijniho_planu, nazev_planu FROM studijni_obory_a_plany";

		PreparedStatement prstGetPlans = conn.prepareStatement(queryGetPlans);

		ResultSet rs = prstGetPlans.executeQuery();
		while (rs.next()) {
		    String planName = rs.getString("nazev_planu");
		    int planID = rs.getInt("id_studijniho_planu");

		    plans.add(new StudyPlans(planID, planName, chosenField.getFieldName()));
		}
		rs.close();
		prstGetPlans.close();
		return;

	    } else if (MainWindowController.isUserEditing()) {
		String queryGetFieldInfo = "SELECT * FROM studijni_obory WHERE nazev = ?";
		try {
		    PreparedStatement prstGetFieldInfo = conn.prepareStatement(queryGetFieldInfo);

		    prstGetFieldInfo.setString(1, MainWindowController.getLoggedUserField());

		    ResultSet rsInfo = prstGetFieldInfo.executeQuery();
		    while (rsInfo.next()) {
			chosenField = new Field(rsInfo.getInt("id_oboru"), rsInfo.getString("nazev"));
		    }
		    rsInfo.close();
		    prstGetFieldInfo.close();
		} catch (SQLException ex) {
		    Logger.getLogger(EditProfileChangePlanDialogController.class.getName()).log(Level.SEVERE, null, ex);
		}
		editingID = LoginDialogController.loggedContact.getAccountID();

	    } else if (AdminEditSubjectDialogController.isSubjectEditing()) {

		if (!ManageSubjectsDialogController.isAddingNew()) {
		    editingID = ManageSubjectsDialogController.getEditingSubject().getIdSubject();
		}

		String queryGetPlans = "SELECT id_studijniho_planu, nazev_planu FROM studijni_obory_a_plany";
		PreparedStatement prstGetPlans = conn.prepareStatement(queryGetPlans);

		ResultSet rsPlans = prstGetPlans.executeQuery();
		while (rsPlans.next()) {
		    String planName = rsPlans.getString("nazev_planu");
		    int planID = rsPlans.getInt("id_studijniho_planu");

		    plans.add(new StudyPlans(planID, planName, null));
		}
		rsPlans.close();
		prstGetPlans.close();
		return;
	    }
	    String queryGetPlans = "SELECT id_studijniho_planu, nazev_planu FROM studijni_obory_a_plany WHERE id_oboru = ?";

	    PreparedStatement prstGetPlans = conn.prepareStatement(queryGetPlans);
	    prstGetPlans.setInt(1, chosenField.getFieldID());

	    ResultSet rs = prstGetPlans.executeQuery();
	    while (rs.next()) {
		String planName = rs.getString("nazev_planu");
		int planID = rs.getInt("id_studijniho_planu");

		plans.add(new StudyPlans(planID, planName, chosenField.getFieldName()));
	    }
	    rs.close();
	    prstGetPlans.close();
	} catch (SQLException ex) {
	    Logger.getLogger(EditProfileChangePlanDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Sends a details into the database with all the entered details and
     * updates data in tables
     *
     * @param event
     */
    @FXML
    private void handleButtonSaveAction(ActionEvent event) {
	// REMOVE ALL PREVIOUSLY SELECTED
	try {
	    // if editing user
	    if (AdminEditUserDialogController.isAdminEditing() || MainWindowController.isUserEditing()) {
		String queryDelete = "DELETE FROM sp_uzivatelu WHERE uzivatel_id_uzivatel = ?";

		PreparedStatement prstDelete = conn.prepareStatement(queryDelete);

		prstDelete.setInt(1, editingID);
		prstDelete.executeUpdate();
		prstDelete.close();

		// IF NOTHING SELECTED
		String queryInsert = "INSERT INTO sp_uzivatelu (sp_id_studijniho_planu, uzivatel_id_uzivatel) VALUES (?,?)";
		PreparedStatement prstInsert = conn.prepareStatement(queryInsert);

		prstInsert.setInt(2, editingID);

		tableView.getSelectionModel().getSelectedItems().forEach(e -> {
		    try {
			if (e.getPlanID() == 0) {
			    return;
			}
			prstInsert.setInt(1, e.getPlanID());
			prstInsert.setInt(2, editingID);

			prstInsert.executeUpdate();
		    } catch (SQLException ex) {
			Logger.getLogger(EditProfileChangePlanDialogController.class.getName()).log(Level.SEVERE, null, ex);
		    }
		});
		prstInsert.close();
		// if editing subject
	    } else {
		// REMOVE ALL previously selected
		String queryDeleteSubj = "DELETE FROM predmety_sp WHERE predmet_id_predmetu =?";
		PreparedStatement prstDeleteSubj = conn.prepareStatement(queryDeleteSubj);

		prstDeleteSubj.setInt(1, ManageSubjectsDialogController.getEditingSubject().getIdSubject());

		prstDeleteSubj.executeUpdate();
		prstDeleteSubj.close();

		// IF NOTHING SELECTED
		String queryInsert = "INSERT INTO predmety_sp (predmet_id_predmetu, sp_id_studijniho_planu) VALUES (?,?)";
		PreparedStatement prstInsert = conn.prepareStatement(queryInsert);

		prstInsert.setInt(1, ManageSubjectsDialogController.getEditingSubject().getIdSubject());
		tableView.getSelectionModel().getSelectedItems().forEach(e -> {
		    try {
			prstInsert.setInt(2, e.getPlanID());

			prstInsert.executeUpdate();
		    } catch (SQLException ex) {
			Logger.getLogger(EditProfileChangePlanDialogController.class.getName()).log(Level.SEVERE, null, ex);
		    }
		});
		prstInsert.close();
	    }
	} catch (SQLException ex) {
	    Logger.getLogger(EditProfileChangePlanDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
	handleButtonBackAction(null);
    }

    /**
     * Closes current dialog
     *
     * @param event
     */
    @FXML
    private void handleButtonBackAction(ActionEvent event) {
	if (AdminEditUserDialogController.isAdminEditing()) {
	    AdminEditUserDialogController.changeStudyPlans.close();
	} else if (MainWindowController.isUserEditing()) {
	    MainWindowController.changeStudyPlans.close();
	} else if (AdminEditSubjectDialogController.isSubjectEditing()) {
	    AdminEditSubjectDialogController.changeStudyPlans.close();
	}
    }

    /**
     * Sets a custom visuals for buttons in this dialog
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
