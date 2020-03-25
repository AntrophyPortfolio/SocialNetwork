package dialogs;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
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
 * Opens up a dialog where administrator can manage all the users in network
 * (edit details)
 *
 * @author Antrophy
 */
public class ManageUsersDialogController implements Initializable {

    @FXML
    private TableColumn<Contacts, String> tableColumnLogin;
    @FXML
    private TableColumn<Contacts, String> tableColumnName;
    @FXML
    private TableColumn<Contacts, String> tableColumnLastName;
    @FXML
    private TableColumn<Contacts, Integer> tableColumnBan;
    @FXML
    private TableView<Contacts> tableView;

    private final Connection conn = LoginDialogController.CONN;
    private ObservableList<Contacts> userList = FXCollections.observableArrayList();

    private static final IAlerts ALERT = new Alerts();

    public static Stage adminEditUserDialog;
    private static Contacts editingUser = null;
    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	setButtonVisuals(btnBack);
	tableColumnLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
	tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
	tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
	tableColumnBan.setCellValueFactory(new PropertyValueFactory<>("ban"));
	loadUsers();

	ContextMenu contextMenu = new ContextMenu();
	MenuItem editUser = new MenuItem("Edit user");
	MenuItem banUser = new MenuItem("Ban user");
	MenuItem unbanUser = new MenuItem("Unban user");

	contextMenu.getItems().add(editUser);

	tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {

		    Contacts selected = tableView.getSelectionModel().getSelectedItem();

		    if (selected.getBan() == 1) {
			contextMenu.getItems().setAll(editUser, unbanUser);

			unbanUser.setOnAction(e -> {
			    handleContextItemUnbanUserAction(selected);
			    contextMenu.hide();
			});
		    } else {
			contextMenu.getItems().setAll(editUser, banUser);

			banUser.setOnAction(e -> {
			    handleContextItemBanUserAction(selected);
			    contextMenu.hide();
			});
		    }
		    editUser.setOnAction(e -> {
			handleContextItemEditUserAction(selected);
			contextMenu.hide();
		    });
		    contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
		}
	    }
	});
    }

    /**
     * Loads all users of the network to the tableview
     */
    private void loadUsers() {
	try {

	    Statement st = conn.createStatement();

	    String listNames = "SELECT u.blokace, u.id_uzivatel, u.login, u.jmeno, u.prijmeni, k.id_kontakt\n"
		    + "FROM uzivatele u\n"
		    + "INNER JOIN kontakty k\n"
		    + "ON u.id_uzivatel=k.uzivatele_id_uzivatel\n";

	    ResultSet rs = st.executeQuery(listNames);
	    while (rs.next()) {
		int userID = rs.getInt("id_uzivatel");
		int contactID = rs.getInt("id_kontakt");
		int ban = rs.getInt("blokace");
		String login = rs.getString("login");
		String name = rs.getString("jmeno");
		String lastName = rs.getString("prijmeni");

		userList.add(new Contacts(userID, contactID, login, name, lastName, ban));
	    }
	    rs.close();
	    st.close();
	    tableView.setItems(userList);
	} catch (SQLException ex) {
	    ALERT.showCouldNotExecuteCommand();
	}
    }

    /**
     * Clicking on Edit in context menu takes us to the editing of user
     *
     * @param contact
     */
    private void handleContextItemEditUserAction(Contacts contact) {
	setEditingUser(contact);

	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminEditUserDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    adminEditUserDialog = stage;
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    Logger.getLogger(ManageUsersDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Clicking ban context menu, update value of ban so that the user can't log
     * in to the network
     *
     * @param contact
     */
    private void handleContextItemBanUserAction(Contacts contact) {
	try {

	    Statement st = conn.createStatement();

	    String query = "UPDATE uzivatele\n"
		    + "SET blokace = 1\n"
		    + "WHERE id_uzivatel=" + contact.getAccountID();

	    st.executeQuery(query);
	    st.close();
	} catch (SQLException ex) {
	    ALERT.showCouldNotExecuteCommand();

	}
	userList.clear();
	loadUsers();
    }

    /**
     * Updates value ban in database so that user can log in.
     *
     * @param contact
     */
    private void handleContextItemUnbanUserAction(Contacts contact) {
	try {

	    Statement st = conn.createStatement();

	    String query = "UPDATE uzivatele\n"
		    + "SET blokace = 0\n"
		    + "WHERE id_uzivatel=" + contact.getAccountID();

	    st.executeQuery(query);
	    st.close();
	} catch (SQLException ex) {
	    ALERT.showCouldNotExecuteCommand();
	}
	userList.clear();
	loadUsers();
    }

    /**
     * Clicking back button closes the manage users dialog
     *
     * @param event
     */
    @FXML
    private void handlleButtonBackAction(ActionEvent event) {
	MainWindowController.manageUsersDialog.close();
    }

    /**
     * Returns editing user that I clicked on so that I can get info about that
     * user
     *
     * @return
     */
    public static Contacts getEditingUser() {
	return editingUser;
    }

    /**
     * After clicking a user, set that user to the variable that is send to
     * other dialog windows
     *
     * @param editingUser
     */
    public static void setEditingUser(Contacts editingUser) {
	ManageUsersDialogController.editingUser = editingUser;
    }

    /**
     * Setting a custom visuals for buttons
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
