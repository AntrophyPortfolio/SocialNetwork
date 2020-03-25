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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import resources.Alerts;
import resources.Field;
import resources.IAlerts;

/**
 * Opens up a dialog where administrator can edit study plans
 *
 * @author Antrophy
 */
public class AdminEditPlanDialogController implements Initializable {

    @FXML
    private Label labelEditedPlanName;
    @FXML
    private TextField textFieldName;
    @FXML
    private ComboBox<Field> comboBoxFields = new ComboBox();

    private final Connection conn = LoginDialogController.CONN;
    private ObservableList<Field> fields = FXCollections.observableArrayList();

    private static final IAlerts ALERT = new Alerts();
    @FXML
    private Button btnSave;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	setButtonVisuals(btnSave);
	textFieldName.setOnAction(e -> {
	    handleButtonSaveAction(null);
	});
	// LOAD DATA TO COMBOBOX
	String queryGetFields = "SELECT * FROM studijni_obory";
	comboBoxFields.setItems(fields);
	try {
	    PreparedStatement prstGetFields = conn.prepareStatement(queryGetFields);

	    ResultSet rsGetFields = prstGetFields.executeQuery();
	    while (rsGetFields.next()) {
		int fieldID = rsGetFields.getInt("id_oboru");
		String fieldname = rsGetFields.getString("nazev");

		fields.add(new Field(fieldID, fieldname));
	    }
	    rsGetFields.close();
	    prstGetFields.close();
	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditPlanDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}

	if (!ManagePlansDialogController.isAddingPlan()) {
	    labelEditedPlanName.setText(ManagePlansDialogController.getEditingPlan().getPlanName());

	    textFieldName.setText(ManagePlansDialogController.getEditingPlan().getPlanName());

	    fields.forEach(e -> {
		if (e.getFieldName().equals(ManagePlansDialogController.getEditingPlan().getField())) {
		    comboBoxFields.setValue(e);
		}
	    });
	} else {
	    labelEditedPlanName.setText("Adding new");
	}
    }

    /**
     * Saves all the details into the database and updates values or inserts
     *
     * @param event
     */
    @FXML
    private void handleButtonSaveAction(ActionEvent event) {

	if (textFieldName.getText().isEmpty() || comboBoxFields.getValue() == null) {
	    ALERT.showValuesNotFilledAlert();
	    return;
	}

	try {
	    // IF I'M EDITING AND NOT ADDING
	    if (!ManagePlansDialogController.isAddingPlan()) {
		String updateStudyPlan = "UPDATE studijni_plany SET nazev = ?, studijni_obor_id_oboru = ? WHERE id_studijniho_planu = ?";

		PreparedStatement prstUpdateStudyPlan = conn.prepareStatement(updateStudyPlan);

		prstUpdateStudyPlan.setString(1, textFieldName.getText());
		prstUpdateStudyPlan.setInt(2, comboBoxFields.getValue().getFieldID());
		prstUpdateStudyPlan.setInt(3, ManagePlansDialogController.getEditingPlan().getPlanID());

		prstUpdateStudyPlan.executeUpdate();

		prstUpdateStudyPlan.close();

	    } else {
		String insertStudyPlan = "INSERT INTO studijni_plany(nazev, studijni_obor_id_oboru) VALUES (?, ?)";
		PreparedStatement prstInsert = conn.prepareStatement(insertStudyPlan);

		prstInsert.setString(1, textFieldName.getText());
		prstInsert.setInt(2, comboBoxFields.getValue().getFieldID());

		prstInsert.executeUpdate();
		prstInsert.close();
	    }

	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditPlanDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
	ManagePlansDialogController.editPlan.close();

    }

    /**
     * Sets a custom button visuals in this dualog
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
