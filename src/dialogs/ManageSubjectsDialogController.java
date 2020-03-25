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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import resources.Alerts;
import resources.IAlerts;
import resources.Subject;
/**
 * Opens up a dialog where administrator can manage subjects (edit, delete, add)
 * @author Antrophy
 */
public class ManageSubjectsDialogController implements Initializable {

    @FXML
    private TableView<Subject> tableView;
    @FXML
    private TableColumn<Subject, String> tableColumnShortName;
    @FXML
    private TableColumn<Subject, String> tableColumnName;
    @FXML
    private TableColumn<Subject, String> tableColumnStudyField;
    @FXML
    private TableColumn<Subject, String> tableColumnStudyPlan;
    @FXML
    private TableColumn<Subject, String> tableColumnTeacher;

    private final Connection conn = LoginDialogController.CONN;
    private ObservableList<Subject> subjectsList = FXCollections.observableArrayList();
    private final IAlerts ALERT = new Alerts();
    private static Subject editingSubject;
    public static Stage editSubjectDialog;
    public static Stage addSubjectDialog;
    public static boolean addingDetails;
    private static boolean addingNew;
    @FXML
    private Button buttonRefresh;
    @FXML
    private Button buttonAddSubject;
    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

	setButtonVisuals(btnBack);
	setButtonVisuals(buttonAddSubject);
	setButtonVisuals(buttonRefresh);
	if (LoginDialogController.guestLogin) {
	    buttonRefresh.setVisible(false);
	    buttonAddSubject.setVisible(false);
	} else {
	    buttonRefresh.setVisible(true);
	    buttonAddSubject.setVisible(true);
	}
	tableColumnShortName.setCellValueFactory(new PropertyValueFactory<>("shortName"));
	tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
	tableColumnStudyPlan.setCellValueFactory(new PropertyValueFactory<>("studyPlan"));
	tableColumnStudyField.setCellValueFactory(new PropertyValueFactory<>("studyField"));
	tableColumnTeacher.setCellValueFactory(new PropertyValueFactory<>("teacher"));

	refreshList();

	if (!LoginDialogController.guestLogin) {
	    // context menu for editing subjects
	    ContextMenu cm = new ContextMenu();
	    MenuItem editDetails = new MenuItem("Edit details");
	    MenuItem removeInfo = new MenuItem("Remove info");
	    MenuItem removeSubj = new MenuItem("Remove subject");

	    tableView.addEventHandler(MouseEvent.MOUSE_CLICKED,
		    new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event
		) {
		    if (event.getButton() == MouseButton.PRIMARY) {
			cm.hide();
			return;
		    }
		    if (event.getButton() == MouseButton.SECONDARY) {

			Subject selectedSubject = tableView.getSelectionModel().getSelectedItem();

			if (!(selectedSubject.getTeacher().isEmpty())
				|| !(selectedSubject.getStudyField().isEmpty())
				|| !(selectedSubject.getStudyPlan().isEmpty())) {
			    addingDetails = false;
			    cm.getItems().setAll(editDetails, removeInfo, removeSubj);
			} else {
			    addingDetails = true;
			    cm.getItems().setAll(editDetails, removeSubj);
			}

			cm.show(tableView, event.getScreenX(), event.getScreenY());

			removeInfo.setOnAction(e -> {
			    handleMenuItemRemoveInfo(selectedSubject);
			    cm.hide();
			});
			editDetails.setOnAction(e -> {
			    handleMenuItemEditSubject(selectedSubject);
			    refreshList();
			    cm.hide();
			});

			removeSubj.setOnAction(e -> {
			    handleMenuItemRemoveSubject(selectedSubject);
			    refreshList();
			    cm.hide();
			});
		    }
		}

		private void handleMenuItemRemoveInfo(Subject selectedSubject) {
		    String queryRemoveInfo = "DELETE FROM predmety_sp WHERE predmet_id_predmetu = ?";
		    try {
			PreparedStatement prstRemoveInfo = conn.prepareStatement(queryRemoveInfo);
			prstRemoveInfo.setInt(1, selectedSubject.getIdSubject());

			prstRemoveInfo.executeUpdate();
			prstRemoveInfo.close();

			String removeTeacherQuery = "UPDATE predmety SET id_ucitele = null WHERE id_predmetu = ?";
			PreparedStatement prstRemoveTeacher = conn.prepareStatement(removeTeacherQuery);
			prstRemoveTeacher.setInt(1, selectedSubject.getIdSubject());
			prstRemoveTeacher.executeUpdate();
			prstRemoveTeacher.close();

			selectedSubject.setStudyField(null);
			selectedSubject.setStudyField(null);
			selectedSubject.setTeacher(null);

			refreshList();
			cm.hide();

		    } catch (SQLException ex) {
			Logger.getLogger(ManageSubjectsDialogController.class.getName()).log(Level.SEVERE, null, ex);
		    }
		}

		private void handleMenuItemEditSubject(Subject selectedSubject) {
		    addingNew = false;
		    try {
			editingSubject = selectedSubject;
			Stage stage = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminEditSubjectDialog.fxml"));
			Scene scene = new Scene(root);
			editSubjectDialog = stage;
			editSubjectDialog.setOnHidden(e -> {
			    refreshList();
			});
			stage.setResizable(false);
			stage.setScene(scene);
			stage.show();
		    } catch (IOException ex) {
			ALERT.showCouldNotLoadWindowAlert();
		    }
		}

		private void handleMenuItemRemoveSubject(Subject selectedSubject) {
		    String queryRemoveSubj = "DELETE FROM predmety WHERE id_predmetu = ?";
		    String queryRemoveSubjPlan = "DELETE FROM predmety_sp WHERE predmet_id_predmetu = ?";

		    try {
			PreparedStatement prstRemoveSubjPlan = conn.prepareStatement(queryRemoveSubjPlan);

			prstRemoveSubjPlan.setInt(1, selectedSubject.getIdSubject());
			prstRemoveSubjPlan.executeUpdate();
			prstRemoveSubjPlan.close();

			PreparedStatement prstRemoveSubj = conn.prepareStatement(queryRemoveSubj);
			prstRemoveSubj.setInt(1, selectedSubject.getIdSubject());

			prstRemoveSubj.executeUpdate();
			prstRemoveSubj.close();
		    } catch (SQLException ex) {
			Logger.getLogger(ManageSubjectsDialogController.class.getName()).log(Level.SEVERE, null, ex);
		    }
		}
	    });
	}
    }

    /**
     * Closes this window
     * @param event 
     */
    @FXML
    private void handlleButtonBackAction(ActionEvent event) {
	MainWindowController.manageSubjects.close();
    }

    /**
     * (Re)Loads all subjects into the tableview
     */
    public void refreshList() {
	try {
	    // LOAD ALL SUBJECTS INTO TABLEVIEW
	    subjectsList.clear();
	    String query = "SELECT id_predmetu, nazev, zkratka, jmeno, prijmeni FROM predmety_veskere_info";
	    PreparedStatement prstSubjectInfo = conn.prepareStatement(query);

	    ResultSet rsSubjects = prstSubjectInfo.executeQuery();
	    while (rsSubjects.next()) {
		boolean isAlreadyThere = false;

		int idSubject = rsSubjects.getInt("id_predmetu");
		for (Subject subject : subjectsList) {
		    if (subject.getIdSubject() == idSubject) {
			isAlreadyThere = true;
		    }
		}
		if (isAlreadyThere) {
		    continue;
		}
		String name = rsSubjects.getString("nazev");
		String shortName = rsSubjects.getString("zkratka");
		String teacherName = rsSubjects.getString("jmeno");
		String teacherLastName = rsSubjects.getString("prijmeni");

		String queryGetPlansFields = "SELECT nazev_planu, nazev_oboru FROM predmety_veskere_info WHERE id_predmetu = ?";
		PreparedStatement prstPlansFields = conn.prepareStatement(queryGetPlansFields);
		prstPlansFields.setInt(1, idSubject);
		ResultSet rsPlansFields = prstPlansFields.executeQuery();

		String studyField = null;
		String studyPlan = null;
		boolean prvni = true;
		while (rsPlansFields.next()) {
		    if (prvni) {
			studyField = rsPlansFields.getString("nazev_oboru");
			studyPlan = rsPlansFields.getString("nazev_planu");
		    } else {
			studyField += ", " + rsPlansFields.getString("nazev_oboru");
			studyPlan += ", " + rsPlansFields.getString("nazev_planu");
		    }
		    prvni = false;
		}
		rsPlansFields.close();
		prstPlansFields.close();
		if (studyField == null) {
		    studyField = "";
		}
		if (studyPlan == null) {
		    studyPlan = "";
		}
		String teacher;
		if (teacherName != null) {
		    teacher = teacherName + " " + teacherLastName;
		} else {
		    teacher = "";
		}
		subjectsList.add(new Subject(idSubject, shortName, name, studyField, studyPlan, teacher));
	    }
	    rsSubjects.close();
	    prstSubjectInfo.close();
	    tableView.setItems(subjectsList);
	    tableView.refresh();

	} catch (SQLException ex) {
	    Logger.getLogger(ManageSubjectsDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Returns clicked subject
     * @return 
     */
    public static Subject getEditingSubject() {
	return editingSubject;
    }

    /**
     * Clicking refresh button re-reloads all subjects into tableview
     * @param event 
     */
    @FXML
    private void handleButtonRefreshAction(ActionEvent event) {
	refreshList();
    }

    /**
     * Clicking add button opens up a AddNewSubjectDialog window
     * @param event 
     */
    @FXML
    private void handleButtonAddSubject(ActionEvent event) {
	try {
	    addingNew = true;

	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AddNewSubjectDialog.fxml"));
	    Scene scene = new Scene(root);
	    addSubjectDialog = stage;
	    addSubjectDialog.setOnHidden(e -> {
		refreshList();
		addingNew = false;
	    });
	    stage.setResizable(false);
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    Logger.getLogger(ManageSubjectsDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * When clicked add subject, set to true
     * @return 
     */
    public static boolean isAddingNew() {
	return addingNew;
    }

    /**
     * Sets custom visuals for buttons
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
