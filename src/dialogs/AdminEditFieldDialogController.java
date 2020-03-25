package dialogs;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import resources.Alerts;
import resources.IAlerts;

/**
 * Opens up a dialog where I can either add or edit existing fields
 *
 * @author Antrophy
 */
public class AdminEditFieldDialogController implements Initializable {

    @FXML
    private TextField textFieldName;

    private final Connection conn = LoginDialogController.CONN;

    private static final IAlerts ALERT = new Alerts();
    @FXML
    private Label labelEditedFieldName;
    @FXML
    private Button btnAdd;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	setButtonVisuals(btnAdd);
	if (!ManagePlansDialogController.isAddingField()) {
	    labelEditedFieldName.setText(ManagePlansDialogController.getEditingField().getFieldName());

	    textFieldName.setText(ManagePlansDialogController.getEditingField().getFieldName());
	} else {
	    labelEditedFieldName.setText("Adding new");
	}
    }

    /**
     * Opens a new dialog for adding new fields
     *
     * @param event
     */
    @FXML
    private void handleButtonAddAction(ActionEvent event) {

	if (textFieldName.getText().isEmpty()) {
	    ALERT.showValuesNotFilledAlert();
	    return;
	}
	try {

	    // IF ADDING NEW AND NOT EDITING
	    if (ManagePlansDialogController.isAddingField()) {

		String query = "INSERT INTO studijni_obory (nazev) VALUES (?)";
		PreparedStatement prst = conn.prepareStatement(query);
		prst.setString(1, textFieldName.getText());

		prst.executeUpdate();
		prst.close();
	    } else {

		String queryUpdate = "UPDATE studijni_obory SET nazev = ? WHERE id_oboru = ?";
		PreparedStatement prstUpdate = conn.prepareStatement(queryUpdate);

		prstUpdate.setString(1, textFieldName.getText());
		prstUpdate.setInt(2, ManagePlansDialogController.getEditingField().getFieldID());

		prstUpdate.executeUpdate();
		prstUpdate.close();
	    }
	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditFieldDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
	ManagePlansDialogController.editField.close();

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
