package dialogs;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import resources.Alerts;
import resources.IAlerts;
import resources.Task;

/**
 * Opens up a dialog for adding new tasks for group
 *
 * @author Antrophy
 */
public class GroupAddTasksDialogController implements Initializable {

    @FXML
    private Label group_name;
    @FXML
    private TextField textFieldTask;
    @FXML
    private ListView<Task> listViewTasks;
    private ObservableList<Task> obsList = FXCollections.observableArrayList();
    private ObservableList<Task> removedTasks = FXCollections.observableArrayList();
    @FXML
    private Button btnDelete;
    private final Connection conn = LoginDialogController.CONN;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDone;

    private static final IAlerts ALERTS = new Alerts();
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSaveAndAdd;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

	setButtonVisuals(btnDelete);
	setButtonVisuals(btnAdd);
	setButtonVisuals(btnEdit);
	setButtonVisuals(btnDone);
	setButtonVisuals(btnCancel);
	setButtonVisuals(btnSaveAndAdd);
	btnDone.setVisible(false);
	btnCancel.setVisible(false);
	group_name.setText(MainWindowController.getCurrentGroupName());
	btnDelete.setVisible(false);
	btnEdit.setVisible(false);
	listViewTasks.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
	    @Override
	    public void changed(ObservableValue<? extends Task> observable, Task oldValue, Task newValue) {
		btnDelete.setVisible(true);
		btnEdit.setVisible(true);
	    }
	});

	if (MainWindowController.isEditingTasks) {
	    PreparedStatement prstGetTasks;
	    ResultSet rs;
	    try {
		String getTasksQuery = "SELECT ukoly.id_ukolu, ukoly.text FROM UKOLY\n"
			+ "INNER JOIN UKOLY_SKUPIN\n"
			+ "ON ukoly_skupin.id_ukolu = ukoly.id_ukolu\n"
			+ "WHERE id_studijniho_planu=(dej_id_studijniho_planu(?))";
		prstGetTasks = conn.prepareStatement(getTasksQuery);
		prstGetTasks.setString(1, MainWindowController.getCurrentGroupName());
		prstGetTasks.execute();

		rs = prstGetTasks.getResultSet();
		while (rs.next()) {
		    Task newTask = new Task(rs.getInt("id_ukolu"), rs.getNString("Text"));
		    obsList.add(newTask);
		}
		rs.close();
		prstGetTasks.close();

		listViewTasks.setItems(obsList);
		listViewTasks.refresh();
	    } catch (SQLException ex) {
		Logger.getLogger(GroupAddTasksDialogController.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    /**
     * Adds a entered text as a new task into the list
     *
     * @param event
     */
    @FXML
    private void handleButtonAddAction(ActionEvent event) {

	if (!textFieldTask.getText().isEmpty()) {

	    Task newTask = new Task(0, textFieldTask.getText());
	    obsList.add(newTask);
	    listViewTasks.setItems(obsList);
	    textFieldTask.setText("");
	}
    }

    /**
     * Sends all tasks in the list to the database and saves them
     *
     * @param event
     */
    @FXML
    private void handleButtonSaveAndAddAction(ActionEvent event) {

	obsList.forEach(e -> {
	    try {

		CallableStatement clStInsert = conn.prepareCall("{call proc_vloz_ukol(?,?,?,?)}");
		clStInsert.setInt(1, e.getID());
		clStInsert.setNString(2, e.toString());
		clStInsert.setInt(3, LoginDialogController.loggedContact.getAccountID());
		clStInsert.setString(4, MainWindowController.getCurrentGroupName());
		clStInsert.execute();
		clStInsert.close();
	    } catch (SQLException ex) {
		Logger.getLogger(GroupAddTasksDialogController.class.getName()).log(Level.SEVERE, null, ex);
	    }
	});
	if (!removedTasks.isEmpty()) {
	    removedTasks.forEach(e -> {
		try {
		    CallableStatement clStInsert = conn.prepareCall("{call proc_vloz_ukol(?,?,?,?)}");
		    clStInsert.setInt(1, e.getID());
		    clStInsert.setNString(2, "_removed");
		    clStInsert.setInt(3, LoginDialogController.loggedContact.getAccountID());
		    clStInsert.setString(4, MainWindowController.getCurrentGroupName());
		    clStInsert.execute();
		    clStInsert.close();
		} catch (SQLException ex) {
		    Logger.getLogger(GroupAddTasksDialogController.class.getName()).log(Level.SEVERE, null, ex);
		}
	    });
	}
	MainWindowController.groupAddTasks.close();
    }

    /**
     * Deletes a selected task in the list
     *
     * @param event
     */
    @FXML
    private void handleButtonDeleteAction(ActionEvent event) {
	removedTasks.add(listViewTasks.getSelectionModel().getSelectedItem());
	obsList.remove(listViewTasks.getSelectionModel().getSelectedItem());
	if (obsList.isEmpty()) {
	    btnDelete.setVisible(false);
	    btnEdit.setVisible(false);
	}
    }

    /**
     * Edits a selected task in the list
     *
     * @param event
     */
    @FXML
    private void handleButtonEditAction(ActionEvent event) {
	textFieldTask.setText(obsList.get(listViewTasks.getSelectionModel().getSelectedIndex()).toString());
	btnDone.setVisible(true);
	btnAdd.setVisible(false);
	btnCancel.setVisible(true);
    }

    /**
     * Saves current edited task into the list
     *
     * @param event
     */
    @FXML
    private void handleButtonDoneAction(ActionEvent event) {
	if (textFieldTask.getText().isEmpty()) {
	    ALERTS.showValuesNotFilledAlert();
	} else {
	    obsList.get(listViewTasks.getSelectionModel().getSelectedIndex()).setText(textFieldTask.getText());
	    btnDone.setVisible(false);
	    btnAdd.setVisible(true);
	    btnCancel.setVisible(false);
	    textFieldTask.setText("");
	    listViewTasks.refresh();
	}
    }

    /**
     * Hides all editing tools for editing tasks
     *
     * @param event
     */
    @FXML
    private void handleButtonCancelAction(ActionEvent event) {
	btnCancel.setVisible(false);
	btnAdd.setVisible(true);
	btnDone.setVisible(false);
    }

    /**
     * Sets a button visuals for this dialog
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
