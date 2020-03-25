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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import resources.Alerts;
import resources.IAlerts;

/**
 * Dialog for adding a new subjects in social network
 *
 * @author Antrophy
 */
public class AddNewSubjectDialogController implements Initializable {

    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldShortName;

    private final Connection conn = LoginDialogController.CONN;
    private static final IAlerts ALERT = new Alerts();
    @FXML
    private Button btnAdd;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	setButtonVisuals(btnAdd);
    }

    /**
     * Adds a new subject into the database (inserts) or updates
     *
     * @param event
     */
    @FXML
    private void handleButtonAddAction(ActionEvent event) {
	if (textFieldName.getText().isEmpty() || textFieldShortName.getText().isEmpty()) {
	    ALERT.showValuesNotFilledAlert();
	    return;
	}
	String queryInsert = "INSERT INTO predmety (nazev, zkratka) VALUES (?, ?)";
	PreparedStatement prstInsert;
	try {
	    prstInsert = conn.prepareStatement(queryInsert);

	    prstInsert.setString(1, textFieldName.getText());
	    prstInsert.setString(2, textFieldShortName.getText());
	    prstInsert.executeUpdate();
	    prstInsert.close();
	} catch (SQLException ex) {
	    Logger.getLogger(AddNewSubjectDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
	ManageSubjectsDialogController.addSubjectDialog.close();
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
