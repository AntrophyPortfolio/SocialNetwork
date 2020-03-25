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
import resources.Field;
import resources.StudyPlans;

/**
 * Opens up a dialog for managing study plans (as administrator)
 *
 * @author Antrophy
 */
public class ManagePlansDialogController implements Initializable {

    @FXML
    private TableColumn<StudyPlans, String> columnPlanName;
    @FXML
    private TableColumn<StudyPlans, String> columnField;

    private ObservableList<StudyPlans> plans = FXCollections.observableArrayList();
    private ObservableList<Field> fields = FXCollections.observableArrayList();

    private final Connection conn = LoginDialogController.CONN;
    public static Stage editPlan;
    public static Stage editField;
    @FXML
    private TableView<Field> tableViewFields;
    @FXML
    private TableColumn<Field, String> columnFieldname;
    @FXML
    private TableView<StudyPlans> tableViewPlans;
    @FXML
    private Button buttonLoadFields;
    @FXML
    private Button buttonLoadPlans;

    private static StudyPlans editingPlan;
    private static Field editingField;
    private static boolean addingPlan;
    private static boolean addingField;

    public static Field getEditingField() {
	return editingField;
    }

    public static boolean isAddingPlan() {
	return addingPlan;
    }

    public static boolean isAddingField() {
	return addingField;
    }

    public static StudyPlans getEditingPlan() {
	return editingPlan;
    }
    @FXML
    private Button buttonAddPlan;
    @FXML
    private Button buttonAddField;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnRefresh;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

	setButtonVisuals(btnBack);
	setButtonVisuals(btnRefresh);
	setButtonVisuals(buttonAddField);
	setButtonVisuals(buttonAddPlan);
	setButtonVisuals(buttonLoadPlans);
	setButtonVisuals(buttonLoadFields);

	if (LoginDialogController.guestLogin) {
	    buttonAddPlan.setVisible(false);
	    buttonAddField.setVisible(false);
	    buttonLoadFields.setVisible(false);
	    buttonLoadPlans.setVisible(false);
	}
	buttonLoadFields.setVisible(true);
	buttonLoadPlans.setVisible(false);

	columnPlanName.setCellValueFactory(new PropertyValueFactory<>("planName"));
	columnField.setCellValueFactory(new PropertyValueFactory<>("field"));

	columnPlanName.setStyle("-fx-alignment: CENTER");
	columnField.setStyle("-fx-alignment: CENTER");

	columnFieldname.setStyle("-fx-alignment: CENTER");
	columnFieldname.setCellValueFactory(new PropertyValueFactory<>("fieldName"));

	loadPlans();
    }

    /**
     * Closes current dialog
     *
     * @param event
     */
    @FXML
    private void handleButtonBackAction(ActionEvent event) {
	MainWindowController.managePlans.close();
    }

    /**
     * Opens up a AdminEditPlanDialog dialog
     *
     * @param event
     */
    @FXML
    private void handleButtonAddNewPlan(ActionEvent event) {
	addingPlan = true;
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminEditPlanDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    stage.setOnHidden(e -> {
		addingPlan = false;
		handleButtonRefreshAction(null);
	    });
	    editPlan = stage;
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Opens up AdminEditFieldDialog
     *
     * @param event
     */
    @FXML
    private void handleButtonAddFieldAction(ActionEvent event) {
	addingField = true;
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminEditFieldDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    stage.setOnHidden(e -> {
		addingField = false;
		handleButtonRefreshAction(null);
	    });
	    editField = stage;
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Clicking Load fields button will switch details in the table view to show
     * fields.
     *
     * @param event
     */
    @FXML
    private void handlleButtonLoadFieldsAction(ActionEvent event) {
	buttonLoadFields.setVisible(false);
	buttonLoadPlans.setVisible(true);

	tableViewFields.setVisible(true);
	tableViewPlans.setVisible(false);
	loadFields();
    }

    /**
     * Clicking load plans will switch details in the table view to show study
     * plans
     *
     * @param event
     */
    @FXML
    private void handleButtonLoadPlansAction(ActionEvent event) {
	buttonLoadFields.setVisible(true);
	buttonLoadPlans.setVisible(false);

	tableViewFields.setVisible(false);
	tableViewPlans.setVisible(true);
	loadPlans();
    }

    /**
     * Loads all study plans in the network and shows them
     */
    private void loadPlans() {
	plans.clear();
	String getPlansQuery = "SELECT sp.id_studijniho_planu, sp.nazev AS nazev_planu, so.nazev AS nazev_oboru "
		+ "FROM studijni_plany sp "
		+ "INNER JOIN studijni_obory so "
		+ "ON sp.studijni_obor_id_oboru=so.id_oboru";
	try {
	    PreparedStatement prstGetPlans = conn.prepareStatement(getPlansQuery);

	    ResultSet rsPlansData = prstGetPlans.executeQuery();
	    while (rsPlansData.next()) {
		int planID = rsPlansData.getInt("id_studijniho_planu");
		String planName = rsPlansData.getString("nazev_planu");
		String fieldName = rsPlansData.getString("nazev_oboru");

		plans.add(new StudyPlans(planID, planName, fieldName));

	    }
	    rsPlansData.close();
	    prstGetPlans.close();

	} catch (SQLException ex) {
	    Logger.getLogger(ManagePlansDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
	tableViewPlans.setItems(plans);

	ContextMenu cm = new ContextMenu();
	MenuItem removePlanItem = new MenuItem("Remove plan");
	MenuItem editPlanItem = new MenuItem("Edit plan");

	tableViewPlans.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
		    cm.hide();
		    return;
		}
		if (event.getButton() == MouseButton.SECONDARY) {
		    StudyPlans selectedPlan = tableViewPlans.getSelectionModel().getSelectedItem();

		    removePlanItem.setOnAction(e -> {
			handleMenuItemRemovePlanAction(selectedPlan);
			cm.hide();
		    });
		    editPlanItem.setOnAction(e -> {
			handleMenuItemEditPlanAction(selectedPlan);
			cm.hide();
		    });
		    cm.getItems().setAll(removePlanItem, editPlanItem);
		    cm.show(tableViewPlans, event.getScreenX(), event.getScreenY());
		}

	    }

	    private void handleMenuItemRemovePlanAction(StudyPlans selectedPlan) {
		try {
		    // REMOVE FROM USER PLANS
		    String removeUserPlan = "DELETE FROM sp_uzivatelu WHERE sp_id_studijniho_planu = ?";
		    PreparedStatement prstRemoveUserPlan = conn.prepareStatement(removeUserPlan);

		    prstRemoveUserPlan.setInt(1, selectedPlan.getPlanID());
		    prstRemoveUserPlan.executeUpdate();

		    prstRemoveUserPlan.close();

		    // REMOVE FROM GROUPS
		    String removeGroupPlan = "DELETE FROM skupiny WHERE sp_id_studijniho_planu = ?";
		    PreparedStatement prstRemoveGroupPlan = conn.prepareStatement(removeGroupPlan);

		    prstRemoveGroupPlan.setInt(1, selectedPlan.getPlanID());
		    prstRemoveGroupPlan.executeUpdate();

		    prstRemoveGroupPlan.close();

		    // REMOVE FROM SUBJECTS
		    String removePlanSubject = "DELETE FROM predmety_sp WHERE sp_id_studijniho_planu = ?";
		    PreparedStatement prstRemovePlanSubject = conn.prepareStatement(removePlanSubject);

		    prstRemovePlanSubject.setInt(1, selectedPlan.getPlanID());
		    prstRemovePlanSubject.executeUpdate();

		    prstRemovePlanSubject.close();

		    // REMOVE FROM STUDY PLANS
		    String removePlan = "DELETE FROM studijni_plany WHERE id_studijniho_planu = ?";
		    PreparedStatement prstRemovePlan = conn.prepareStatement(removePlan);

		    prstRemovePlan.setInt(1, selectedPlan.getPlanID());
		    prstRemovePlan.executeUpdate();

		    prstRemovePlan.close();

		    loadPlans();
		    cm.hide();
		} catch (SQLException ex) {
		    Logger.getLogger(ManagePlansDialogController.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }

	    private void handleMenuItemEditPlanAction(StudyPlans selectedPlan) {
		addingPlan = false;
		editingPlan = selectedPlan;

		try {
		    Stage stage = new Stage();
		    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminEditPlanDialog.fxml"));
		    Scene scene = new Scene(root);
		    stage.setResizable(false);
		    stage.setOnHidden(e -> {
			addingPlan = false;
			handleButtonRefreshAction(null);
		    });
		    editPlan = stage;
		    stage.setScene(scene);
		    stage.show();
		} catch (IOException ex) {
		    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	});
    }

    /**
     * Loads all fields in the network and shows them in a table view
     */
    private void loadFields() {
	fields.clear();
	String query = "SELECT * FROM studijni_obory";
	PreparedStatement prst;
	try {
	    prst = conn.prepareStatement(query);

	    ResultSet rs = prst.executeQuery();
	    while (rs.next()) {
		int fieldID = rs.getInt("id_oboru");
		String name = rs.getString("nazev");

		fields.add(new Field(fieldID, name));
	    }
	    rs.close();
	    prst.close();

	} catch (SQLException ex) {
	    Logger.getLogger(ManagePlansDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
	tableViewFields.setItems(fields);

	ContextMenu cm = new ContextMenu();
	MenuItem removeFieldItem = new MenuItem("Remove field");
	MenuItem editFieldItem = new MenuItem("Edit field");

	tableViewFields.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
		    cm.hide();
		    return;
		}
		if (event.getButton() == MouseButton.SECONDARY) {
		    Field selectedField = tableViewFields.getSelectionModel().getSelectedItem();

		    removeFieldItem.setOnAction(e -> {
			handleMenuItemRemoveFieldAction(selectedField);
			cm.hide();
		    });
		    editFieldItem.setOnAction(e -> {
			handleMenuItemEditFieldAction(selectedField);
			cm.hide();
		    });
		    cm.getItems().setAll(removeFieldItem, editFieldItem);
		    cm.show(tableViewFields, event.getScreenX(), event.getScreenY());
		}

	    }

	    private void handleMenuItemRemoveFieldAction(Field selectedField) {
		try {
		    // GET IDs of study plans for field
		    String selectID = "SELECT id_studijniho_planu FROM studijni_plany WHERE studijni_obor_id_oboru = ?";
		    PreparedStatement prstSelectID = conn.prepareStatement(selectID);
		    prstSelectID.setInt(1, selectedField.getFieldID());

		    ResultSet rsDelete = prstSelectID.executeQuery();
		    while (rsDelete.next()) {
			int planID = rsDelete.getInt("id_studijniho_planu");

			// REMOVE FROM USER PLANS
			String removeUserPlan = "DELETE FROM sp_uzivatelu WHERE sp_id_studijniho_planu = ?";
			PreparedStatement prstRemoveUserPlan = conn.prepareStatement(removeUserPlan);

			prstRemoveUserPlan.setInt(1, planID);
			prstRemoveUserPlan.executeUpdate();

			prstRemoveUserPlan.close();

			// REMOVE FROM GROUPS
			String removeGroupPlan = "DELETE FROM skupiny WHERE sp_id_studijniho_planu = ?";
			PreparedStatement prstRemoveGroupPlan = conn.prepareStatement(removeGroupPlan);

			prstRemoveGroupPlan.setInt(1, planID);
			prstRemoveGroupPlan.executeUpdate();

			prstRemoveGroupPlan.close();

			// REMOVE FROM SUBJECTS
			String removePlanSubject = "DELETE FROM predmety_sp WHERE sp_id_studijniho_planu = ?";
			PreparedStatement prstRemovePlanSubject = conn.prepareStatement(removePlanSubject);

			prstRemovePlanSubject.setInt(1, planID);
			prstRemovePlanSubject.executeUpdate();

			prstRemovePlanSubject.close();

			// REMOVE FROM STUDY PLANS
			String removePlan = "DELETE FROM studijni_plany WHERE id_studijniho_planu = ?";
			PreparedStatement prstRemovePlan = conn.prepareStatement(removePlan);

			prstRemovePlan.setInt(1, planID);
			prstRemovePlan.executeUpdate();

			prstRemovePlan.close();
		    }
		    rsDelete.close();
		    prstSelectID.close();

		    // REMOVE FROM FIELDS
		    String queryDelete = "DELETE FROM studijni_obory WHERE id_oboru = ?";
		    PreparedStatement prstDeleteField = conn.prepareStatement(queryDelete);
		    prstDeleteField.setInt(1, selectedField.getFieldID());

		    prstDeleteField.executeUpdate();
		    prstDeleteField.close();

		} catch (SQLException ex) {
		    Logger.getLogger(ManagePlansDialogController.class.getName()).log(Level.SEVERE, null, ex);
		}
		handleButtonRefreshAction(null);
	    }

	    private void handleMenuItemEditFieldAction(Field selectedField) {
		addingField = false;
		editingField = selectedField;

		try {
		    Stage stage = new Stage();
		    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminEditFieldDialog.fxml"));
		    Scene scene = new Scene(root);
		    stage.setResizable(false);
		    stage.setOnHidden(e -> {
			addingField = false;
			handleButtonRefreshAction(null);
		    });
		    editField = stage;
		    stage.setScene(scene);
		    stage.show();
		} catch (IOException ex) {
		    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	});
    }

    /**
     * Clicking refresh button will re-load all fields and plans into tableviews
     *
     * @param event
     */
    @FXML
    private void handleButtonRefreshAction(ActionEvent event) {
	loadFields();
	loadPlans();
    }

    /**
     * Sets a custom button visuals
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
