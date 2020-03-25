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
import resources.Contacts;
import resources.IAlerts;

/**
 * Opens up a dialog where either administrator or teacher can manage user's
 * done tasks
 *
 * @author Antrophy
 */
public class ManageUserTasksDialogController implements Initializable {

    @FXML
    private TableColumn<Contacts, String> tableColumnName;
    @FXML
    private TableColumn<Contacts, String> tableColumnLastName;
    @FXML
    private TableView<Contacts> tableView;

    private final Connection conn = LoginDialogController.CONN;
    private ObservableList<Contacts> userList = FXCollections.observableArrayList();

    private static final IAlerts ALERT = new Alerts();

    public static Stage userTasks;
    private static Contacts editingUser = null;
    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	setButtonVisuals(btnBack);
	tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
	tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
	loadUsers();

	ContextMenu contextMenu = new ContextMenu();
	MenuItem editUserTasks = new MenuItem("Edit user tasks");

	contextMenu.getItems().add(editUserTasks);

	tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {

		    Contacts selected = tableView.getSelectionModel().getSelectedItem();

		    editUserTasks.setOnAction(e -> {
			handleContextItemEditUserAction(selected);
			contextMenu.hide();
		    });
		    contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
		}
	    }
	});
    }

    /**
     * Method loads all users in the specific study plan (group)
     */
    private void loadUsers() {
	try {
	    // vyber jen lidi, co maji dany studijni plan
	    String listNames = "SELECT u.blokace, u.id_uzivatel, u.login, u.jmeno, u.prijmeni, k.id_kontakt\n"
		    + "FROM uzivatele u\n"
		    + "INNER JOIN kontakty k\n"
		    + "ON u.id_uzivatel=k.uzivatele_id_uzivatel\n"
		    + "INNER JOIN sp_uzivatelu sp\n"
		    + "ON u.id_uzivatel = sp.uzivatel_id_uzivatel\n"
		    + "WHERE sp.sp_id_studijniho_planu = dej_id_studijniho_planu(?)";
	    PreparedStatement prstNames = conn.prepareStatement(listNames);
	    prstNames.setString(1, MainWindowController.getCurrentGroupName());
	    ResultSet rs = prstNames.executeQuery();
	    while (rs.next()) {
		int userID = rs.getInt("id_uzivatel");
		int contactID = rs.getInt("id_kontakt");
		int ban = rs.getInt("blokace");
		String login = rs.getString("login");
		String name = rs.getString("jmeno");
		String lastName = rs.getString("prijmeni");

		userList.add(new Contacts(userID, contactID, login, name, lastName, ban));
	    }
	    prstNames.close();
	    rs.close();
	    tableView.setItems(userList);
	} catch (SQLException ex) {
	    ALERT.showCouldNotExecuteCommand();
	}
    }

    /**
     * Clicking edit menu item will switch scenes to EditTasksDialog
     *
     * @param contact
     */
    private void handleContextItemEditUserAction(Contacts contact) {
	setEditingUser(contact);
	MainWindowController.isCheckingTasks = false;
	try {
	    MainWindowController.manageUserTasks.close();
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/EditTasksDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    userTasks = stage;
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    Logger.getLogger(ManageUsersDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    /**
     * Closes this stage and opens up a main window dialog
     *
     * @param event
     */
    @FXML
    private void handlleButtonBackAction(ActionEvent event) {
	MainWindowController.manageUserTasks.close();
	LoginDialogController.mainWindow.show();
    }

    /**
     * Returns clicked user's details
     *
     * @return
     */
    public static Contacts getEditingUser() {
	return editingUser;
    }

    /**
     * Sets details into variable for later usage
     *
     * @param editingUser
     */
    public static void setEditingUser(Contacts editingUser) {
	ManageUserTasksDialogController.editingUser = editingUser;
    }

    /**
     * Sets custom button visuals
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
