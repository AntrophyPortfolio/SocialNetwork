package dialogs;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import resources.Task;

/**
 * Opens up a dialog for editing already existing tasks for specific group
 *
 * @author Antrophy
 */
public class EditTasksDialogController implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane gridPaneTasks;
    @FXML
    private Label labelNameOfUser;
    @FXML
    private GridPane gridPanMain;
    private final Connection conn = LoginDialogController.CONN;
    private int rowCount;
    private ArrayList<CheckBox> checkBoxList = new ArrayList<>();
    @FXML
    private Label labelBeforeName;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

	setButtonVisuals(btnSave);
	setButtonVisuals(btnBack);

	if (MainWindowController.isCheckingTasks) {
	    btnSave.setVisible(false);
	    String fullName = LoginDialogController.loggedContact.getName() + " " + LoginDialogController.loggedContact.getLastName();
	    labelNameOfUser.setText(fullName);
	    labelBeforeName.setText("Your task standings as:");
	} else {
	    String fullName = ManageUserTasksDialogController.getEditingUser().getName() + " "
		    + ManageUserTasksDialogController.getEditingUser().getLastName();
	    labelNameOfUser.setText(fullName);
	}
	scrollPane.prefWidthProperty().bind(gridPanMain.prefWidthProperty());
	gridPaneTasks.prefWidthProperty().bind(scrollPane.prefWidthProperty());
	try {
	    // vytahne vsechny ukoly pro moji skupinu
	    String getTasksQuery = "SELECT u.id_ukolu, u.text\n"
		    + "FROM ukoly u\n"
		    + "INNER JOIN ukoly_skupin us\n"
		    + "ON u.id_ukolu = us.id_ukolu\n"
		    + "WHERE us.id_studijniho_planu=dej_id_studijniho_planu(?)";
	    PreparedStatement prstGetTasks = conn.prepareStatement(getTasksQuery);
	    prstGetTasks.setString(1, MainWindowController.getCurrentGroupName());

	    ResultSet rs = prstGetTasks.executeQuery();
	    while (rs.next()) {
		CheckBox task = new CheckBox();
		task.setStyle("-fx-text-fill: #aaaabb");
		Task newTask = new Task(rs.getInt("id_ukolu"), rs.getNString("text"));
		task.setUserData(newTask);
		task.setText(task.getUserData().toString());
		gridPaneTasks.addRow(rowCount++, task);
		GridPane.setMargin(task, new Insets(20, 0, 0, 0));
		checkBoxList.add(task);
		if (MainWindowController.isCheckingTasks) {
		    task.setDisable(true);
		} else {
		    task.setDisable(false);
		}
	    }
	    rs.close();
	    prstGetTasks.close();

	    // pokud existuje zaznam o splneni, tak se zasrktne checkbox
	    String getCheckBoxTicks = "SELECT count(*) as existuje FROM ukoly_uzivatelu WHERE ? = id_ukolu AND ? = id_uzivatel";
	    PreparedStatement prst = conn.prepareStatement(getCheckBoxTicks);

	    checkBoxList.forEach(e -> {
		Task task = (Task) e.getUserData();
		try {
		    prst.setInt(1, task.getID());
		    if (MainWindowController.isCheckingTasks) {
			prst.setInt(2, LoginDialogController.loggedContact.getAccountID());
		    } else {
			prst.setInt(2, ManageUserTasksDialogController.getEditingUser().getAccountID());
		    }
		    ResultSet result = prst.executeQuery();
		    int exists = 0;
		    while (result.next()) {
			exists = result.getInt("existuje");
		    }
		    result.close();
		    if (exists == 1) {
			e.setSelected(true);
		    }
		} catch (SQLException ex) {
		    Logger.getLogger(EditTasksDialogController.class.getName()).log(Level.SEVERE, null, ex);
		}
	    });
	    prst.close();
	} catch (SQLException ex) {
	    Logger.getLogger(EditTasksDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Calls a procedure in the database that will insert all the new tasks
     *
     * @param event
     */
    @FXML
    private void handleButtonSaveAction(ActionEvent event) {
	try {
	    CallableStatement clstmntInsert = conn.prepareCall("{call proc_vloz_ukol_uzivateli(?,?)}");
	    CallableStatement clstmntDelete = conn.prepareCall("{call proc_smaz_ukol_uzivateli(?,?)}");

	    checkBoxList.forEach(e -> {

		Task task = (Task) e.getUserData();
		try {
		    if (e.isSelected()) {

			clstmntInsert.setInt(1, task.getID());
			clstmntInsert.setInt(2, ManageUserTasksDialogController.getEditingUser().getAccountID());
			clstmntInsert.execute();

		    } else {
			clstmntDelete.setInt(1, task.getID());
			clstmntDelete.setInt(2, ManageUserTasksDialogController.getEditingUser().getAccountID());
			clstmntDelete.execute();
		    }
		} catch (SQLException ex) {
		    Logger.getLogger(EditTasksDialogController.class.getName()).log(Level.SEVERE, null, ex);
		}
	    });
	    clstmntInsert.close();
	    clstmntDelete.close();
	} catch (SQLException ex) {
	    Logger.getLogger(EditTasksDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}

	ManageUserTasksDialogController.userTasks.close();
	MainWindowController.manageUserTasks.show();
    }

    /**
     * Closes current dialog
     *
     * @param event
     */
    @FXML
    private void handleButtonBackAction(ActionEvent event) {
	if (MainWindowController.isCheckingTasks) {
	    MainWindowController.showTaskOverview.close();
	} else {
	    ManageUserTasksDialogController.userTasks.close();
	    MainWindowController.manageUserTasks.show();
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
