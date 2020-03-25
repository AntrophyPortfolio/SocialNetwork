package dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import resources.Alerts;
import resources.Contacts;
import resources.IAlerts;
import resources.Subject;
import run.Main;

/**
 * The main dialog window where most of the activities is done
 *
 * @author Antrophy
 */
public class MainWindowController implements Initializable {

    public static Stage editProfileDialog;
    public static Stage manageUsersDialog;
    public static Stage manageSubjects;
    public static Stage addNotification;
    public static Stage findUserDialog;
    public static Stage managePlans;
    public static Stage changeStudyPlans;
    public static Stage replyToPost;
    public static Stage addEmoji;
    public static Stage groupAddTasks;
    public static Stage manageUserTasks;
    public static Stage showTaskOverview;

    public static boolean notifyIsSet = false;
    private static int indexUsers = 3;
    private static int indexGroups = 1;
    public static boolean isEditingTasks = false;
    @FXML
    private TextArea textAreaSendMessage;
    @FXML
    private GridPane gridPaneUsers;
    @FXML
    private GridPane gridPaneGroups;
    @FXML
    private Label labelMessagedUserName;
    @FXML
    private GridPane gridAddContact;
    @FXML
    private GridPane gridChat;

    private ObservableList<Contacts> contactsList = FXCollections.observableArrayList();
    private ObservableList<Contacts> friends = FXCollections.observableArrayList();
    private ObservableList<Subject> teacherSubjectTable = FXCollections.observableArrayList();

    private static Contacts contactInfo;
    public static Contacts currentContact;
    public static boolean isCheckingTasks;
    private int currentGroupID;
    private static String currentGroupName;

    private IAlerts ALERT = new Alerts();

    @FXML
    private TableColumn<Contacts, String> tableColumnLogin;
    @FXML
    private TableColumn<Contacts, String> tableColumnName;
    @FXML
    private TableColumn<Contacts, String> tableColumnLastName;
    @FXML
    private TableView<Contacts> tableViewContacts;
    @FXML
    private Menu menuItemLogged;
    @FXML
    private TextField textFieldLogin;
    @FXML
    private PasswordField passFieldPassword;
    @FXML
    private PasswordField passFieldPassConfirm;
    @FXML
    private TextField textFieldSurname;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextArea textAreaDescription;
    @FXML
    private GridPane gridEditProfile;
    @FXML
    private Label labelLoggedInUser;
    @FXML
    private VBox vBoxChat;
    @FXML
    private Spinner<Integer> spinnerStartedSchool;
    @FXML
    private Label labelNotifyName;
    @FXML
    private Label labelNotifyText;
    @FXML
    private Label labelNotifyDate;
    @FXML
    private Button buttonAddNotification;
    @FXML
    private Button buttonEditNotification;
    @FXML
    private Menu menuItemManage;
    private boolean moreMessages;
    private static String loggedUserField;
    @FXML
    private TextField textFieldNameFind;
    @FXML
    private TextField textFieldLastNameFind;
    @FXML
    private Button buttonSaveNoTeacher;
    @FXML
    private Button buttonSaveYesTeacher;
    @FXML
    private TableView<Subject> tableViewSubjects;
    @FXML
    private TableColumn<Subject, String> columnShortEdit;
    @FXML
    private TableColumn<Subject, String> columnNameEdit;
    @FXML
    private Label labelTaughtSubjects;
    @FXML
    private TableView<Contacts> tableViewSendMessageMore;
    @FXML
    private TableColumn<Contacts, String> columnLoginMess;
    @FXML
    private TableColumn<Contacts, String> columnNameMess;
    @FXML
    private TableColumn<Contacts, String> columnLastNameMess;
    @FXML
    private ScrollPane scrollPaneFBmessages;
    private int messageID;
    private int photoID;
    private File file;
    @FXML
    private Label labelChosenStudyPlans;
    @FXML
    private Button buttonChangePlan;
    private static boolean userEditing;
    private boolean editingPost;
    private int editComentID;
    private static String replyingToText;
    private static String replyingToName;
    private static int replyingToCommentID;
    private static TextArea appendEmojiToArea;

    public static String getCurrentGroupName() {
	return currentGroupName;
    }

    public static TextArea geSendMessageArea() {
	return appendEmojiToArea;
    }

    public static int getReplyingToCommentID() {
	return replyingToCommentID;
    }

    public static String getReplyingToName() {
	return replyingToName;
    }

    public static String getReplyingToText() {
	return replyingToText;
    }

    public static boolean isUserEditing() {
	return userEditing;
    }

    public static String getLoggedUserField() {
	return loggedUserField;
    }
    @FXML
    private Label labelNameOfGroup;
    @FXML
    private VBox vBoxMainComments;
    @FXML
    private GridPane gridComments;
    @FXML
    private TextArea textAreaPost;
    @FXML
    private Button buttonPost;
    @FXML
    private Button buttonCancelEdit;
    @FXML
    private ImageView imageViewProfilePicture;
    @FXML
    private MenuItem manageUsersItem;
    @FXML
    private ScrollPane scrollPaneUsers;
    @FXML
    private MenuItem manageSubjectsMenuItem;
    @FXML
    private MenuItem ManagePlansMenuItem;
    @FXML
    private Button buttonInsertPhoto;
    @FXML
    private ImageView imageViewProfilePicture2;
    @FXML
    private Button btnAddTasks;
    @FXML
    private Button btnEditTasks;
    @FXML
    private Button btnTasksOverview;
    @FXML
    private Button btnManageUsersTask;

    private Connection conn = LoginDialogController.CONN;
    @FXML
    private ScrollPane scrollPaneGroups;
    @FXML
    private ColumnConstraints columnUsers;
    @FXML
    private ColumnConstraints columnGroups;
    @FXML
    private GridPane gridPaneMain;
    @FXML
    private AnchorPane anchorMesagesToAll;
    @FXML
    private ScrollPane scrollPanePosts;
    @FXML
    private ColumnConstraints mainColumnGrid;
    @FXML
    private Button btnSendEmojiChat;
    @FXML
    private Button btnSendMessageChat;
    @FXML
    private Button btnSearchUsers;
    @FXML
    private Button btnAddEmojiPosts;
    @FXML
    private AnchorPane anchorPaneSubjects;

    private static boolean isCurrentUserAdmin = false;
    private static boolean isCurrentUserTeacher = false;
    @FXML
    private Menu menuEmulation;
    @FXML
    private Label labelSavedYesTeacher;
    @FXML
    private Button btnRegister;
    @FXML
    private TableColumn<Subject, String> columnPlanEdit;
    @FXML
    private Button buttonLoadFriends;
    @FXML
    private Button buttonLoadTaughtSubjects;
    @FXML
    private TableView<Contacts> tableViewFriends;
    @FXML
    private TableColumn<Contacts, String> tableColumnFriendsLogin;
    @FXML
    private TableColumn<Contacts, String> tableColumnFriendsName;
    @FXML
    private TableColumn<Contacts, String> tableColumnFriendsLastName;
    @FXML
    private MenuItem menuItemEditProfile;
    @FXML
    private MenuItem menuItemSignOut;
    @FXML
    private RowConstraints columnMessagePicture;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

	setLoggedInUsersLabel();
	menuItemLogged.setVisible(true);
	labelTaughtSubjects.setVisible(false);
	btnRegister.setVisible(false);
	if (LoginDialogController.guestLogin == true) {
	    btnRegister.setVisible(true);
	    menuItemEditProfile.setVisible(false);
	    menuItemSignOut.setText("Back");
	}
	menuEmulation.setVisible(false);
	setButtons();

	if (LoginDialogController.previousContact != null) {

	    menuEmulation.setVisible(true);
	}
	btnAddTasks.setVisible(false);
	btnEditTasks.setVisible(false);
	btnManageUsersTask.setVisible(false);
	btnTasksOverview.setVisible(false);

	scrollPaneUsers.setFitToWidth(true);
	scrollPaneGroups.setFitToWidth(true);
	scrollPaneFBmessages.setFitToWidth(true);
	scrollPaneUsers.prefWidthProperty().bind(columnUsers.prefWidthProperty());
	scrollPaneGroups.prefWidthProperty().bind(columnGroups.prefWidthProperty());
	gridPaneUsers.prefWidthProperty().bind(scrollPaneUsers.prefWidthProperty());
	gridPaneGroups.prefWidthProperty().bind(scrollPaneGroups.prefWidthProperty());
	vBoxChat.prefWidthProperty().bind(scrollPaneFBmessages.prefWidthProperty());
	vBoxChat.prefHeightProperty().bind(scrollPaneFBmessages.prefHeightProperty());
	vBoxChat.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	scrollPanePosts.prefWidthProperty().bind(mainColumnGrid.prefWidthProperty());
	vBoxMainComments.prefWidthProperty().bind(scrollPanePosts.prefWidthProperty());
	Label noSubjects = new Label();
	noSubjects.setText("No taught subjects.");
	noSubjects.setStyle("-fx-text-fill:  #aaaabb; \n -fx-font-size: 2em; \n -fx-background-color:#2c2c36");
	tableViewSubjects.setPlaceholder(noSubjects);

	Label noFriends = new Label();
	noFriends.setText("No friends.");
	noFriends.setStyle("-fx-text-fill:  #aaaabb; \n -fx-font-size: 2em; \n -fx-background-color:#2c2c36");
	tableViewFriends.setPlaceholder(noFriends);

	scrollPanePosts.setFitToWidth(true);
	textAreaSendMessage.setText("");
	if (LoginDialogController.guestLogin) {
	    menuItemManage.setVisible(true);
	    menuItemManage.setText("Show other");
	    manageSubjectsMenuItem.setText("Show subjects");
	    ManagePlansMenuItem.setText("Show study plans");
	    gridPaneGroups.setVisible(false);
	    manageUsersItem.setVisible(false);
	    loadUsers();
	    handleButtonAddMoreContactsAction();
	} else {
	    menuItemManage.setText("Manage");
	    manageSubjectsMenuItem.setText("Manage subjects");
	    ManagePlansMenuItem.setText("Manage study plans");
	    vBoxMainComments.setSpacing(20);
	    vBoxMainComments.setPadding(new Insets(15, 0, 0, 0));
	    textFieldNameFind.setOnAction(e -> {
		handleButtonSearchAction(null);
	    });
	    textFieldLastNameFind.setOnAction(e -> {
		handleButtonSearchAction(null);
	    });

	    spinnerStartedSchool.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1970, 2020));
	    menuItemManage.setVisible(false);
	    buttonEditNotification.setVisible(false);
	    indexUsers = 3;
	    indexGroups = 1;
	    textAreaSendMessage.setWrapText(true);
	    vBoxChat.setSpacing(20);
	    try {
		String queryRoles = "SELECT role_id_role\n"
			+ "FROM uzivatelske_role\n"
			+ "WHERE uzivatel_id_uzivatel=?";
		PreparedStatement prst = conn.prepareStatement(queryRoles);

		prst.setInt(1, LoginDialogController.loggedContact.getAccountID());

		ResultSet userRoles = prst.executeQuery();
		int role = 0;
		isCurrentUserAdmin = false;
		while (userRoles.next()) {
		    role = userRoles.getInt("role_id_role");
		    if (role == 1) {
			menuItemManage.setVisible(true);
			isCurrentUserAdmin = true;
		    }
		    if (role == 2) {
			isCurrentUserTeacher = true;
		    }
		}
		userRoles.close();
		prst.close();
		handleButtonAddMoreContactsAction();
		loadUsers();
		loadGroups();

	    } catch (SQLException ex) {
		ALERT.showCouldNotExecuteCommand();
	    }
	}
	tableColumnLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
	tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
	tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

	tableColumnFriendsLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
	tableColumnFriendsName.setCellValueFactory(new PropertyValueFactory<>("name"));
	tableColumnFriendsLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

	columnNameEdit.setCellValueFactory(new PropertyValueFactory<>("name"));
	columnShortEdit.setCellValueFactory(new PropertyValueFactory<>("shortName"));
	columnPlanEdit.setCellValueFactory(new PropertyValueFactory<>("studyPlan"));
	Main.loginStage.close();
    }

    /**
     * Clicking sign out menu item will close main window and opens up back the
     * login window
     *
     * @param event
     */
    @FXML
    private void handleMenuItemSignOutAction(ActionEvent event) {
	LoginDialogController.previousContact = null;
	isCurrentUserAdmin = false;
	isCurrentUserTeacher = false;
	dialogs.LoginDialogController.mainWindow.close();
	Main.loginStage.show();
    }

    /**
     * Load all users into friends bar in the left side (only those who are
     * user's contacts)
     */
    private void loadUsers() {
	indexUsers = 3;
	try {
	    gridPaneUsers.getChildren().clear();
	    Label title = new Label("USERS");
	    title.setStyle("-fx-text-fill:  #aaaabb; \n -fx-font-size: 2em; \n -fx-background-color:#2c2c36");
	    title.setAlignment(Pos.CENTER);
	    title.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

	    Button addMoreContacts = new Button("All contacts");
	    addMoreContacts.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	    addMoreContacts.setAlignment(Pos.CENTER);
	    addMoreContacts.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
	    addMoreContacts.setOnAction(e -> {
		handleButtonAddMoreContactsAction();
	    });
	    if (LoginDialogController.guestLogin) {
		gridPaneUsers.getChildren().addAll(title, addMoreContacts);
	    } else {
		Button sendMessage = new Button("Send message");
		sendMessage.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		sendMessage.setAlignment(Pos.CENTER);
		sendMessage.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
		sendMessage.setOnAction(e -> {

		    handleButtonSendMessageAction();

		});

		gridPaneUsers.add(title, 0, 0);
		gridPaneUsers.add(addMoreContacts, 0, 1);
		gridPaneUsers.add(sendMessage, 0, 2);

		// selecting only users that are logged in's contacts
		String listNames = "SELECT u2.id_uzivatel, u2.jmeno, u2.login, u2.prijmeni, ku.kontakt_id_kontakt\n"
			+ "FROM uzivatele u\n"
			+ "JOIN kontakty_uzivatele ku ON u.id_uzivatel = ku.uzivatel_id_uzivatel\n"
			+ "JOIN kontakty k ON ku.kontakt_id_kontakt=k.id_kontakt\n"
			+ "JOIN uzivatele u2 ON k.uzivatele_id_uzivatel = u2.id_uzivatel\n"
			+ "WHERE ku.uzivatel_id_uzivatel=?";

		PreparedStatement prstListNames = conn.prepareStatement(listNames);
		prstListNames.setInt(1, LoginDialogController.loggedContact.getAccountID());

		ResultSet rs = prstListNames.executeQuery();
		while (rs.next()) {
		    String login = rs.getString("login");
		    String name = rs.getString("jmeno");
		    String lastName = rs.getString("prijmeni");

		    addUser(indexUsers, name, lastName, login);
		    indexUsers++;
		}
		rs.close();
		prstListNames.close();
	    }
	} catch (SQLException ex) {
	    ALERT.showCouldNotExecuteCommand();
	}

    }

    /**
     * Loads all groups that the user takes part of
     */
    private void loadGroups() {

	indexGroups = 1;
	gridPaneGroups.getChildren().clear();

	Label title = new Label("GROUPS");
	title.setStyle("-fx-text-fill: #aaaabb; \n -fx-font-size: 2em; \n -fx-background-color:#2c2c36; -fx-border-color: #aaaabb");

	title.setAlignment(Pos.CENTER);
	title.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

	gridPaneGroups.add(title, 0, 0);

	// SELECT ALL SUBJECTS FOR MY PLAN
	String getPlanQuery = "SELECT sp.nazev,sp.id_studijniho_planu \n"
		+ "FROM studijni_plany sp\n"
		+ "INNER JOIN sp_uzivatelu spu\n"
		+ "ON sp.id_studijniho_planu=spu.sp_id_studijniho_planu\n"
		+ "WHERE spu.uzivatel_id_uzivatel=?";

	try {
	    PreparedStatement prstPlan = conn.prepareStatement(getPlanQuery);
	    prstPlan.setInt(1, LoginDialogController.loggedContact.getAccountID());

	    ResultSet rsPlan = prstPlan.executeQuery();
	    while (rsPlan.next()) {
		String name = rsPlan.getString("nazev");
		int planID = rsPlan.getInt("id_studijniho_planu");

		addGroup(indexGroups, name, planID);
		indexGroups++;
	    }
	    rsPlan.close();
	    prstPlan.close();

	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * This method is called from loadUser and it adds users into the gridpane
     * where contacts are shown
     *
     * @param indexRow
     * @param name
     * @param lastName
     * @param login
     */
    private void addUser(int indexRow, String name, String lastName, String login) {
	String fullName = name + " " + lastName;
	Button btn = new Button(fullName + " (" + login + ")");
	btn.setBackground(new Background(new BackgroundFill(Color.web("2c2c36"), CornerRadii.EMPTY, Insets.EMPTY)));
	btn.setStyle("-fx-text-fill: #aaaabb; -fx-font-size: 1.2em");
	btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	btn.setMinSize(30, 50);
	btn.setAlignment(Pos.BASELINE_LEFT);
	btn.graphicTextGapProperty().setValue(10);
	btn.addEventHandler(MouseEvent.MOUSE_ENTERED,
		new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		btn.setBackground(new Background(new BackgroundFill(Color.web("3b3b4b"), CornerRadii.EMPTY, Insets.EMPTY)));
	    }
	});
	btn.addEventHandler(MouseEvent.MOUSE_EXITED,
		new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		btn.setBackground(new Background(new BackgroundFill(Color.web("2c2c36"), CornerRadii.EMPTY, Insets.EMPTY)));
	    }
	});

	String prstGetFotkyQuery = "SELECT fotka FROM fotky f\n"
		+ "INNER JOIN fotky_uzivatelu fu\n"
		+ "ON f.id_fotky=fu.fotky_id_fotky\n"
		+ "WHERE fu.uzivatele_id_uzivatel=(dej_id_uzivatele(?))";

	ImageView imageFriendButton = new ImageView();
	PreparedStatement prstGetFotky;
	try {
	    prstGetFotky = conn.prepareStatement(prstGetFotkyQuery);
	    prstGetFotky.setString(1, login);

	    ResultSet rsFotky = prstGetFotky.executeQuery();
	    while (rsFotky.next()) {
		Blob picture = rsFotky.getBlob("fotka");
		imageFriendButton.setImage(new Image(picture.getBinaryStream()));
		imageFriendButton.setFitHeight(40);
		imageFriendButton.setFitWidth(40);
		btn.setGraphic(imageFriendButton);
	    }
	    prstGetFotky.close();
	    rsFotky.close();

	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}

	btn.setOnAction(e -> {
	    imageViewProfilePicture2.fitHeightProperty().bind(LoginDialogController.mainWindow.heightProperty().divide(7.5));
	    LoginDialogController.mainWindow.heightProperty().addListener((observable, oldValue, newValue) -> {
		imageViewProfilePicture2.fitHeightProperty().bind(LoginDialogController.mainWindow.heightProperty().divide(7.5));
	    });
	    moreMessages = false;
	    labelSavedYesTeacher.setVisible(false);
	    labelNotifyDate.setText("");
	    labelNotifyName.setText("");
	    labelNotifyText.setText("");
	    try {

		gridAddContact.setVisible(false);
		gridComments.setVisible(false);
		gridChat.setVisible(true);
		gridEditProfile.setVisible(false);
		scrollPaneFBmessages.setVisible(true);
		anchorMesagesToAll.setVisible(false);
		vBoxChat.getChildren().clear();
		labelMessagedUserName.setText(fullName + "(" + login + ")");

		// GETTING THE USER OF THIS BUTTON TO SAVE IT IN MEMORY FOR LATER USE
		Statement st = conn.createStatement();

		String query = "SELECT u.blokace, u.id_uzivatel, u.login, u.jmeno, u.prijmeni, k.id_kontakt\n"
			+ "FROM uzivatele u\n"
			+ "INNER JOIN kontakty k\n"
			+ "ON u.id_uzivatel=k.uzivatele_id_uzivatel\n"
			+ "WHERE login='" + login + "'";

		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
		    int accountID = rs.getInt("id_uzivatel");
		    int contactID = rs.getInt("id_kontakt");
		    int ban = rs.getInt("blokace");
		    String loginUser = rs.getString("login");
		    String nameUser = rs.getString("jmeno");
		    String surname = rs.getString("prijmeni");

		    currentContact = new Contacts(accountID, contactID, loginUser, nameUser, surname, ban);

		}
		rs.close();
		st.close();

	    } catch (SQLException ex) {
		ALERT.showCouldNotExecuteCommand();
	    }
	    refreshChat();
	});
	gridPaneUsers.addRow(indexRow, btn);
    }

    /**
     * This method is called in loadGroups and it adds a new group buttons to
     * the gridPaneGroups on the right
     *
     * @param indexRow
     * @param name
     * @param idPlan
     */
    private void addGroup(int indexRow, String name, int idPlan) {

	Button group = new Button(name);
	group.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	group.setMinSize(30, 50);
	group.setAlignment(Pos.CENTER);
	group.setBackground(new Background(new BackgroundFill(Color.web("2c2c36"), CornerRadii.EMPTY, Insets.EMPTY)));
	group.setStyle("-fx-text-fill: #aaaabb; -fx-font-size: 1.2em");
	group.addEventHandler(MouseEvent.MOUSE_ENTERED,
		new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		group.setBackground(new Background(new BackgroundFill(Color.web("3b3b4b"), CornerRadii.EMPTY, Insets.EMPTY)));
	    }
	});
	group.addEventHandler(MouseEvent.MOUSE_EXITED,
		new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		group.setBackground(new Background(new BackgroundFill(Color.web("2c2c36"), CornerRadii.EMPTY, Insets.EMPTY)));
	    }
	});
	// Pocet neprectenych komentaru ve skupine

	String getUnreadCountQuery = "SELECT Count(*) AS Pocet FROM NEPRECTENE_KOMENTARE WHERE id_uzivatel = ? AND id_komentare IN (SELECT komentar_id_komentare FROM SKUPINY WHERE SP_ID_STUDIJNIHO_PLANU = ?)";
	try {
	    PreparedStatement prstGetUnreadCount = conn.prepareStatement(getUnreadCountQuery);

	    prstGetUnreadCount.setInt(1, LoginDialogController.loggedContact.getAccountID());
	    prstGetUnreadCount.setInt(2, idPlan);
	    ResultSet rsCount = prstGetUnreadCount.executeQuery();
	    int unreadCount = 0;
	    while (rsCount.next()) {
		unreadCount = rsCount.getInt("Pocet");
	    }
	    rsCount.close();
	    prstGetUnreadCount.close();

	    if (unreadCount > 0) {
		group.setMinHeight(70);
		if (unreadCount == 1) {
		    group.setText(name + "\n(" + unreadCount + " unread post)");
		} else {
		    group.setText(name + "\n(" + unreadCount + " unread posts)");
		}
		//animace novych zprav
		Animation animation = new Transition() {
		    {
			setCycleDuration(Duration.millis(1200));
			setInterpolator(Interpolator.EASE_OUT);
		    }

		    @Override
		    protected void interpolate(double frac) {
			Color vColor = new Color(0.9, 0.9, 0.9, 1 - frac);
			group.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
		    }
		};
		animation.setCycleCount(Animation.INDEFINITE);
		animation.setAutoReverse(true);
		animation.play();

	    }
	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
	group.setOnAction(e -> {
	    labelSavedYesTeacher.setVisible(false);
	    try {
		// smazat uzivatele z neprectenych komentaru ve skupine
		String deleteUnreadMsgQuery = "DELETE FROM NEPRECTENE_KOMENTARE\n"
			+ "WHERE id_uzivatel = ? AND id_komentare IN (SELECT komentar_id_komentare \n"
			+ "FROM SKUPINY sk\n"
			+ "WHERE sp_id_studijniho_planu = ?)";
		PreparedStatement prstDeleteUnreadMsg = conn.prepareStatement(deleteUnreadMsgQuery);
		prstDeleteUnreadMsg.setInt(1, LoginDialogController.loggedContact.getAccountID());
		prstDeleteUnreadMsg.setInt(2, idPlan);
		prstDeleteUnreadMsg.execute();
		prstDeleteUnreadMsg.close();

		loadGroups();
		currentGroupID = idPlan;
		vBoxMainComments.getChildren().clear();
		gridAddContact.setVisible(false);
		gridChat.setVisible(false);
		gridEditProfile.setVisible(false);
		gridComments.setVisible(true);
		labelNameOfGroup.setText(name);
		currentGroupName = name;
		String queryRoles = "SELECT role_id_role\n"
			+ "FROM uzivatelske_role\n"
			+ "WHERE uzivatel_id_uzivatel=?";
		PreparedStatement prst = conn.prepareStatement(queryRoles);

		prst.setInt(1, LoginDialogController.loggedContact.getAccountID());

		ResultSet userRoles = prst.executeQuery();
		while (userRoles.next()) {
		    int role = userRoles.getInt("role_id_role");

		    if (isCurrentUserAdmin == true) {
			menuItemManage.setVisible(true);
		    }
		}
		userRoles.close();
		prst.close();
		refreshPosts();

	    } catch (SQLException ex) {
		Logger.getLogger(MainWindowController.class
			.getName()).log(Level.SEVERE, null, ex);
	    }
	});
	gridPaneGroups.addRow(indexRow, group);
    }

    /**
     * Clicking edit profile menu item will show all details about user
     *
     * @param event
     */
    @FXML
    private void handleMenuItemEditProfileAction(ActionEvent event) {

	setLoggedInUsersLabel();
	buttonChangePlan.setVisible(true);
	labelChosenStudyPlans.setDisable(false);
	textFieldLastName.setDisable(false);
	textFieldLogin.setDisable(false);
	textFieldSurname.setDisable(false);
	imageViewProfilePicture.setDisable(false);
	buttonSaveNoTeacher.setVisible(true);
	buttonSaveYesTeacher.setVisible(false);
	anchorPaneSubjects.setVisible(false);
	buttonLoadFriends.setVisible(false);
	buttonLoadTaughtSubjects.setVisible(false);
	tableViewSubjects.setVisible(false);
	labelTaughtSubjects.setVisible(false);
	if (isCurrentUserTeacher) {
	    anchorPaneSubjects.setVisible(true);
	    tableViewSubjects.setVisible(true);
	    labelTaughtSubjects.setVisible(true);
	    teacherSubjectTable.clear();
	    buttonSaveYesTeacher.setVisible(true);

	    String getSubjectsQuery = "SELECT * FROM predmety_veskere_info WHERE id_ucitele = ?";
	    try {
		PreparedStatement prstGetSubjects = conn.prepareStatement(getSubjectsQuery);

		prstGetSubjects.setInt(1, LoginDialogController.loggedContact.getAccountID());

		ResultSet rsSubjects = prstGetSubjects.executeQuery();
		while (rsSubjects.next()) {
		    String shortName = rsSubjects.getString("zkratka");
		    String name = rsSubjects.getString("nazev");
		    String plan = rsSubjects.getString("nazev_planu");

		    teacherSubjectTable.add(new Subject(0, shortName, name, null, plan, null));
		}
		rsSubjects.close();
		prstGetSubjects.close();
		tableViewSubjects.setItems(teacherSubjectTable);

	    } catch (SQLException ex) {
		Logger.getLogger(MainWindowController.class
			.getName()).log(Level.SEVERE, null, ex);
	    }
	} else {
	    buttonSaveNoTeacher.setVisible(true);
	}
	textFieldLogin.setDisable(false);
	textFieldSurname.setDisable(false);
	textFieldLastName.setDisable(false);
	spinnerStartedSchool.setDisable(false);
	textAreaDescription.setDisable(false);
	passFieldPassConfirm.setDisable(false);
	passFieldPassword.setDisable(false);
	imageViewProfilePicture.setDisable(false);
	buttonInsertPhoto.setVisible(true);

	try {

	    gridEditProfile.setVisible(true);
	    gridChat.setVisible(false);
	    gridAddContact.setVisible(false);
	    gridComments.setVisible(false);

	    // GET DATA ABOUT LOGGED USER
	    String getUserDataQuery = "SELECT login, heslo, jmeno, prijmeni, poznamka, rok_studia, soap.nazev_oboru\n"
		    + "FROM uzivatele u\n"
		    + "INNER JOIN sp_uzivatelu spu \n"
		    + "ON u.id_uzivatel=spu.uzivatel_id_uzivatel\n"
		    + "INNER JOIN studijni_obory_a_plany soap\n"
		    + "ON spu.sp_id_studijniho_planu=soap.id_studijniho_planu\n"
		    + "WHERE id_uzivatel=?";

	    PreparedStatement prstData = conn.prepareStatement(getUserDataQuery);
	    prstData.setInt(1, LoginDialogController.loggedContact.getAccountID());

	    ResultSet rsData = prstData.executeQuery();
	    while (rsData.next()) {

		textFieldLogin.setText(rsData.getString("login"));
		passFieldPassword.setText(rsData.getString("heslo"));
		passFieldPassConfirm.setText(rsData.getString("heslo"));
		textFieldSurname.setText(rsData.getString("jmeno"));
		textFieldLastName.setText(rsData.getString("prijmeni"));
		spinnerStartedSchool.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1970, 2020, rsData.getInt("rok_studia")));
		textAreaDescription.setText(rsData.getNString("poznamka"));
		loggedUserField = rsData.getString("nazev_oboru");

	    }
	    prstData.close();
	    rsData.close();

	    String getFotky = "SELECT F.ID_FOTKY, F.FOTKA AS FO FROM FOTKY F "
		    + "INNER JOIN FOTKY_UZIVATELU FU ON FU.FOTKY_ID_FOTKY = "
		    + "F.ID_FOTKY WHERE FU.UZIVATELE_ID_UZIVATEL = ? ORDER BY ID_FOTKY";

	    PreparedStatement prstmFotky = conn.prepareStatement(getFotky);
	    prstmFotky.setInt(1, LoginDialogController.loggedContact.getAccountID());
	    ResultSet rsFotky = prstmFotky.executeQuery();
	    while (rsFotky.next()) {
		Blob b = rsFotky.getBlob("fo");
		imageViewProfilePicture.setImage(new Image(b.getBinaryStream()));
	    }
	    rsFotky.close();
	    prstmFotky.close();
	    refreshPlans();

	} catch (SQLException ex) {
	    ALERT.showCouldNotExecuteCommand();
	}
    }

    /**
     * Clicking menuItem Manage Users will take the admin to see all users of
     * network and edit them
     *
     * @param event
     */
    @FXML
    private void handleMenuItemManageUsersAction(ActionEvent event) {

	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/ManageUsersDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    manageUsersDialog = stage;
	    manageUsersDialog.setOnHidden(e -> {
		if (!LoginDialogController.guestLogin) {
		    loadGroups();
		    loadUsers();
		}

	    });
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    ALERT.showCouldNotLoadWindowAlert();
	}
    }

    /**
     * Clicking manage network menu item will take administrator to edit details
     * about subjects
     *
     * @param event
     */
    @FXML
    private void handleMenuItemManageNetwork(ActionEvent event) {

	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/ManageSubjectsDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    manageSubjects = stage;
	    manageSubjects.setOnHidden(e -> {
		if (!LoginDialogController.guestLogin) {
		    loadGroups();
		    loadUsers();
		}
	    });
	    stage.setScene(scene);
	    stage.show();

	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Clickig More contacts button will show tableview of all contacts in the
     * database in the center of the application
     */
    private void handleButtonAddMoreContactsAction() {
	labelSavedYesTeacher.setVisible(false);
	textFieldLastNameFind.setText("");
	textFieldNameFind.setText("");
	try {
	    imageViewProfilePicture.setImage(null);
	    gridAddContact.setVisible(true);
	    gridChat.setVisible(false);
	    gridComments.setVisible(false);
	    gridEditProfile.setVisible(false);
	    contactsList.clear();
	    ContextMenu contextMenu = new ContextMenu();
	    MenuItem addContact = new MenuItem("Add contact");
	    MenuItem removeContact = new MenuItem("Remove contact");
	    MenuItem infoContact = new MenuItem("Contact info");
	    MenuItem emulateContact = new MenuItem("Emulate user");

	    if (LoginDialogController.guestLogin) {
		contextMenu.getItems().add(infoContact);
	    } else {
		if (isCurrentUserAdmin) {
		    contextMenu.getItems().addAll(addContact, infoContact, emulateContact);
		} else {
		    contextMenu.getItems().addAll(addContact, infoContact);
		}
	    }

	    tableViewContacts.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
		    if (event.getButton() == MouseButton.PRIMARY) {
			contextMenu.hide();
			return;
		    }
		    if (event.getButton() == MouseButton.SECONDARY) {
			if (!LoginDialogController.guestLogin) {

			    try {
				Contacts selected = tableViewContacts.getSelectionModel().getSelectedItem();
				Statement st = conn.createStatement();

				// IF ALREADY IN CONTACTS, SHOW REMOVE
				String query = "SELECT u2.id_uzivatel\n"
					+ "FROM uzivatele u\n"
					+ "JOIN kontakty_uzivatele ku ON u.id_uzivatel = ku.uzivatel_id_uzivatel\n"
					+ "JOIN kontakty k ON ku.kontakt_id_kontakt=k.id_kontakt\n"
					+ "JOIN uzivatele u2 ON k.uzivatele_id_uzivatel = u2.id_uzivatel\n"
					+ "WHERE ku.uzivatel_id_uzivatel=" + LoginDialogController.loggedContact.getAccountID();

				ResultSet rs = st.executeQuery(query);
				contextMenu.getItems().setAll(addContact, infoContact);
				if (isCurrentUserAdmin) {
				    contextMenu.getItems().setAll(addContact, infoContact, emulateContact);
				}
				while (rs.next()) {
				    int idSelected = rs.getInt("id_uzivatel");

				    if (idSelected == selected.getAccountID()) {
					contextMenu.getItems().setAll(removeContact, infoContact);
					if (isCurrentUserAdmin) {
					    contextMenu.getItems().setAll(removeContact, infoContact, emulateContact);
					}
				    }
				}
				rs.close();
				st.close();

				contextMenu.show(tableViewContacts, event.getScreenX(), event.getScreenY());

				removeContact.setOnAction(e -> {
				    contextMenu.hide();
				    handleContextItemRemoveContact(selected.getContactID());
				});
				addContact.setOnAction(e -> {
				    contextMenu.hide();
				    handleContextItemAddContactAction(selected.getAccountID());
				});
				infoContact.setOnAction(e -> {
				    contextMenu.hide();
				    handleContextItemInfoContactAction(selected);
				});
				emulateContact.setOnAction(e -> {
				    isCurrentUserAdmin = false;
				    isCurrentUserTeacher = false;
				    contextMenu.hide();
				    handleContextItemEmulateContactAction(selected);
				});
			    } catch (SQLException ex) {
				ALERT.showCouldNotExecuteCommand();
			    }
			} else {
			    Contacts selected = tableViewContacts.getSelectionModel().getSelectedItem();
			    contextMenu.show(tableViewContacts, event.getScreenX(), event.getScreenY());
			    infoContact.setOnAction(e -> {
				contextMenu.hide();
				handleContextItemInfoContactAction(selected);
			    });
			}
		    }
		}

		private void handleContextItemAddContactAction(int userID) {
		    try {

			Statement st = conn.createStatement();

			String getContactID = "SELECT id_kontakt FROM kontakty WHERE uzivatele_id_uzivatel=" + userID;
			ResultSet rs = st.executeQuery(getContactID);

			int contactID = 0;
			while (rs.next()) {
			    contactID = rs.getInt("id_kontakt");
			}
			String query = "INSERT INTO kontakty_uzivatele(uzivatel_id_uzivatel, kontakt_id_kontakt)"
				+ " VALUES (" + LoginDialogController.loggedContact.getAccountID() + ", " + contactID + ")";

			st.executeQuery(query);
			st.close();
			rs.close();

			indexUsers = 3;
			loadUsers();
		    } catch (SQLException ex) {
			ALERT.showCouldNotExecuteCommand();
		    }
		}

		private void handleContextItemRemoveContact(int contactID) {
		    try {

			Statement st = conn.createStatement();

			String query = "DELETE FROM kontakty_uzivatele WHERE kontakt_id_kontakt=" + contactID
				+ " AND uzivatel_id_uzivatel=" + LoginDialogController.loggedContact.getAccountID();

			st.executeQuery(query);
			st.close();

			indexUsers = 3;
			loadUsers();
		    } catch (SQLException ex) {
			ALERT.showCouldNotExecuteCommand();
		    }
		}

		private void handleContextItemInfoContactAction(Contacts account) {
		    buttonChangePlan.setVisible(false);
		    labelChosenStudyPlans.setDisable(true);
		    buttonInsertPhoto.setVisible(false);
		    imageViewProfilePicture.setDisable(true);
		    labelTaughtSubjects.setVisible(true);

		    try {
			// IF HE IS A TEACHER, SHOW SUBJECTS
			String getUserRoles = "SELECT uzivatel_id_uzivatel FROM uzivatelske_role WHERE uzivatel_id_uzivatel = ? AND role_id_role = ?";

			PreparedStatement prstUserRoles = conn.prepareStatement(getUserRoles);
			prstUserRoles.setInt(1, account.getAccountID());
			prstUserRoles.setInt(2, 2);

			ResultSet rsUserRoles = prstUserRoles.executeQuery();
			boolean isTeacher = false;
			while (rsUserRoles.next()) {
			    isTeacher = true;
			}
			labelTaughtSubjects.setText("Friends:");
			anchorPaneSubjects.setVisible(true);
			tableViewFriends.setVisible(true);
			tableViewSubjects.setVisible(false);
			loadFriends(account);
			rsUserRoles.close();
			prstUserRoles.close();
			if (isTeacher) {
			    buttonSaveNoTeacher.setVisible(false);
			    buttonSaveYesTeacher.setVisible(false);
			    buttonLoadTaughtSubjects.setVisible(true);
			} else {
			    buttonSaveNoTeacher.setVisible(false);
			    buttonSaveYesTeacher.setVisible(false);
			    buttonLoadTaughtSubjects.setVisible(false);
			    buttonLoadFriends.setVisible(false);
			}

			gridEditProfile.setVisible(true);
			gridChat.setVisible(false);
			gridAddContact.setVisible(false);
			gridComments.setVisible(false);

			// GET DATA ABOUT LOGGED USER
			String getUserDataQuery = "SELECT login, heslo, jmeno, prijmeni, poznamka, rok_studia, soap.nazev_oboru\n"
				+ "FROM uzivatele u\n"
				+ "INNER JOIN sp_uzivatelu spu \n"
				+ "ON u.id_uzivatel=spu.uzivatel_id_uzivatel\n"
				+ "INNER JOIN studijni_obory_a_plany soap\n"
				+ "ON spu.sp_id_studijniho_planu=soap.id_studijniho_planu\n"
				+ "WHERE id_uzivatel=?";

			PreparedStatement prstData = conn.prepareStatement(getUserDataQuery);
			prstData.setInt(1, account.getAccountID());

			ResultSet rsData = prstData.executeQuery();
			while (rsData.next()) {
			    textFieldLogin.setText(rsData.getString("login"));
			    textFieldSurname.setText(rsData.getString("jmeno"));
			    textFieldLastName.setText(rsData.getString("prijmeni"));
			    spinnerStartedSchool.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1970, 2020, rsData.getInt("rok_studia")));
			    textAreaDescription.setText(rsData.getNString("poznamka"));
			    loggedUserField = rsData.getString("nazev_oboru");
			}
			prstData.close();
			rsData.close();
			String getFotky = "SELECT F.ID_FOTKY, F.FOTKA AS FO FROM ST55447.FOTKY F\n"
				+ "INNER JOIN FOTKY_UZIVATELU FU ON FU.FOTKY_ID_FOTKY = F.ID_FOTKY\n"
				+ "WHERE FU.UZIVATELE_ID_UZIVATEL = ? ORDER BY ID_FOTKY";

			PreparedStatement prstmFotky = conn.prepareStatement(getFotky);
			prstmFotky.setInt(1, account.getAccountID());
			ResultSet rsFotky = prstmFotky.executeQuery();
			while (rsFotky.next()) {
			    Blob b = rsFotky.getBlob("fo");
			    imageViewProfilePicture.setImage(new Image(b.getBinaryStream()));
			}
			rsFotky.close();
			prstmFotky.close();
			textFieldLogin.setDisable(true);
			textFieldSurname.setDisable(true);
			textFieldLastName.setDisable(true);
			spinnerStartedSchool.setDisable(true);
			textAreaDescription.setDisable(true);
			passFieldPassConfirm.setDisable(true);
			passFieldPassword.setDisable(true);
			labelLoggedInUser.setText(account.getLogin());

			String queryGetPlans = "SELECT nazev_planu FROM studijni_plany_uzivatelu WHERE id_uzivatel = ?";
			PreparedStatement prstGetPlans = conn.prepareStatement(queryGetPlans);

			prstGetPlans.setInt(1, account.getAccountID());

			ResultSet rsGetPlans = prstGetPlans.executeQuery();
			boolean prvni = true;
			String allPlans = "";
			while (rsGetPlans.next()) {
			    if (prvni) {
				allPlans = rsGetPlans.getString("nazev_planu");
			    } else {
				allPlans += ", " + rsGetPlans.getString("nazev_planu");
			    }
			    prvni = false;
			}
			rsGetPlans.close();
			prstGetPlans.close();
			labelChosenStudyPlans.setText(allPlans);

		    } catch (SQLException ex) {
			Logger.getLogger(MainWindowController.class
				.getName()).log(Level.SEVERE, null, ex);
		    }
		}

		private void handleContextItemEmulateContactAction(Contacts selected) {
		    LoginDialogController.previousContact = LoginDialogController.loggedContact;
		    LoginDialogController.loggedContact = selected;

		    LoginDialogController.mainWindow.close();
		    Stage stage = new Stage();
		    Parent root;
		    try {
			root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));

			Scene scene = new Scene(root);
			stage.setResizable(true);
			stage.setMaximized(true);
			LoginDialogController.mainWindow = stage;
			stage.setScene(scene);
			stage.show();

		    } catch (IOException ex) {
			Logger.getLogger(MainWindowController.class
				.getName()).log(Level.SEVERE, null, ex);
		    }
		}
	    });

	    // LIST ALL SIGNED UP USERS EXCEPT ME
	    Statement st = conn.createStatement();

	    String listNames;
	    ResultSet rsListNames;
	    if (!LoginDialogController.guestLogin) {
		listNames = "SELECT u.blokace, u.id_uzivatel, u.login, u.jmeno, u.prijmeni, k.id_kontakt\n"
			+ "FROM uzivatele u\n"
			+ "INNER JOIN kontakty k\n"
			+ "ON u.id_uzivatel=k.uzivatele_id_uzivatel\n"
			+ "WHERE id_uzivatel!=" + LoginDialogController.loggedContact.getAccountID();;
		rsListNames = st.executeQuery(listNames);
	    } else {
		listNames = "SELECT u.blokace, u.id_uzivatel, u.login, u.jmeno, u.prijmeni, k.id_kontakt\n"
			+ "FROM uzivatele u\n"
			+ "INNER JOIN kontakty k\n"
			+ "ON u.id_uzivatel=k.uzivatele_id_uzivatel\n";
		rsListNames = st.executeQuery(listNames);
	    }
	    while (rsListNames.next()) {
		int userID = rsListNames.getInt("id_uzivatel");
		int contactID = rsListNames.getInt("id_kontakt");
		int ban = rsListNames.getInt("blokace");
		String login = rsListNames.getString("login");
		String name = rsListNames.getString("jmeno");
		String lastName = rsListNames.getString("prijmeni");

		contactsList.add(new Contacts(userID, contactID, login, name, lastName, ban));
	    }
	    rsListNames.close();
	    st.close();

	    tableViewContacts.setItems(contactsList);
	} catch (SQLException ex) {
	    ALERT.showCouldNotExecuteCommand();
	}
    }

    /**
     * Clicking save button in edit profile will send all fields to the database
     *
     * @param event
     * @throws FileNotFoundException
     */
    @FXML
    private void handleButtonSaveAction(ActionEvent event) throws FileNotFoundException {
	labelSavedYesTeacher.setVisible(true);
	labelSavedYesTeacher.setText("SAVED!");
	try {
	    if (textFieldLogin.getText().isEmpty()
		    || textFieldLastName.getText().isEmpty()
		    || textFieldSurname.getText().isEmpty()
		    || passFieldPassConfirm.getText().isEmpty()
		    || passFieldPassword.getText().isEmpty()) {

		ALERT.showValuesNotFilledAlert();
		return;
	    }
	    if (!passFieldPassConfirm.getText().equals(passFieldPassword.getText())) {
		ALERT.showPasswordsDontMatchAlert();
		return;
	    }

	    for (Contacts contacts : contactsList) {
		if (contacts.getLogin().equals(textFieldLogin.getText())) {
		    ALERT.showLoginAlreadyInUseAlert();
		    handleMenuItemEditProfileAction(null);
		    return;
		}
	    }
	    String login = textFieldLogin.getText();
	    String surName = textFieldSurname.getText();
	    String lastName = textFieldLastName.getText();
	    String password = passFieldPassword.getText();
	    int startedSchool = spinnerStartedSchool.getValue(); // convert to date
	    String description = textAreaDescription.getText();

	    // update uzivatele
	    String query = "UPDATE uzivatele"
		    + " SET login = ?, heslo = ?,jmeno = ?, prijmeni = ?, rok_studia = ?, poznamka = ?"
		    + " WHERE id_uzivatel=?";

	    PreparedStatement prstUpdateUser = conn.prepareStatement(query);

	    prstUpdateUser.setString(1, login);
	    prstUpdateUser.setString(2, password);
	    prstUpdateUser.setString(3, surName);
	    prstUpdateUser.setString(4, lastName);
	    prstUpdateUser.setInt(5, startedSchool);
	    prstUpdateUser.setString(6, description);
	    prstUpdateUser.setInt(7, LoginDialogController.loggedContact.getAccountID());

	    prstUpdateUser.execute();
	    prstUpdateUser.close();

	    // update sp_uzivatelu
	    refreshPlans();
	    refreshPhoto();
	    loadGroups();
	    // close
	    handleMenuItemEditProfileAction(null);

	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Clicking send button will send message content, reciever and sender to
     * the database as a private message or multi-user message
     *
     * @param event
     */
    @FXML
    private void handleButtonSendAction(ActionEvent event) {

	if (textAreaSendMessage.getText().isEmpty()) {
	    ALERT.showValuesNotFilledAlert();
	    return;
	}
	// IF SENDING ONLY ONE MESSAGE TO 1 PERSON
	try {

	    String query = "INSERT INTO zpravy (obsah, casove_razitko) VALUES (? , CURRENT_TIMESTAMP)";
	    PreparedStatement prstInsertMessage = conn.prepareStatement(query);

	    prstInsertMessage.setNString(1, textAreaSendMessage.getText());
	    prstInsertMessage.executeUpdate();

	    prstInsertMessage.close();

	    String lastRowQuery = "SELECT MAX(id_zprava) AS id_zprava FROM zpravy";

	    PreparedStatement prstLastRow = conn.prepareStatement(lastRowQuery);
	    ResultSet rs = prstLastRow.executeQuery();

	    messageID = 0;
	    while (rs.next()) {
		messageID = rs.getInt("id_zprava");
	    }
	    rs.close();
	    prstLastRow.close();
	    String queryToMessContacts = "INSERT INTO zpravy_kontaktu (zprava_id_zprava, kontakt_id_kontakt, adresat_ID)"
		    + " VALUES (?, ?,?)";
	    PreparedStatement prstInsertToMessContacts = conn.prepareStatement(queryToMessContacts);

	    if (!moreMessages) {

		prstInsertToMessContacts.setInt(1, messageID);
		prstInsertToMessContacts.setInt(2, LoginDialogController.loggedContact.getContactID());
		prstInsertToMessContacts.setInt(3, currentContact.getContactID());

		prstInsertToMessContacts.executeUpdate();

		textAreaSendMessage.setText("");
	    } else {
		tableViewSendMessageMore.getSelectionModel().getSelectedItems().forEach(new Consumer<Contacts>() {
		    @Override
		    public void accept(Contacts user) {
			try {
			    prstInsertToMessContacts.setInt(1, messageID);
			    prstInsertToMessContacts.setInt(2, LoginDialogController.loggedContact.getContactID());
			    prstInsertToMessContacts.setInt(3, user.getContactID());

			    prstInsertToMessContacts.executeUpdate();

			} catch (SQLException ex) {
			    Logger.getLogger(MainWindowController.class
				    .getName()).log(Level.SEVERE, null, ex);
			}
		    }

		});
		textAreaSendMessage.setText("");
	    }
	    prstInsertToMessContacts.close();

	} catch (SQLException ex) {
	    ALERT.showCouldNotExecuteCommand();
	}
	refreshChat();
    }

    /**
     * Clicking add notification button will open a dialog for entering details
     * about notification
     *
     * @param event
     */
    @FXML
    private void handleButtonAddNotificationAction(ActionEvent event) {

	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AddNotificationDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    stage.setOnHidden(e -> {
		if (!LoginDialogController.guestLogin) {
		    refreshChat();
		}
	    });
	    addNotification = stage;
	    stage.setScene(scene);
	    stage.show();

	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
	refreshChat();

    }

    /**
     * Shows all study plans the current user takes part in
     */
    private void refreshPlans() {
	String querySelectPlans = "SELECT nazev_planu FROM studijni_plany_uzivatelu WHERE id_uzivatel = ?";
	try {
	    PreparedStatement prstSelectPlans = conn.prepareStatement(querySelectPlans);

	    prstSelectPlans.setInt(1, LoginDialogController.loggedContact.getAccountID());

	    ResultSet rsPlans = prstSelectPlans.executeQuery();

	    boolean prvni = true;
	    String allPlans = "";
	    while (rsPlans.next()) {
		if (prvni) {
		    allPlans = rsPlans.getString("nazev_planu");
		} else {
		    allPlans += ", " + rsPlans.getString("nazev_planu");
		}
		prvni = false;
	    }
	    rsPlans.close();
	    prstSelectPlans.close();
	    labelChosenStudyPlans.setText(allPlans);

	} catch (SQLException ex) {
	    Logger.getLogger(AdminEditUserDialogController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Sets a name of the logged in user into the menu bar at the top left
     */
    private void setLoggedInUsersLabel() {
	if (LoginDialogController.guestLogin) {
	    menuItemLogged.setText("Guest");
	    menuItemLogged.setVisible(true);
	} else {

	    Statement st;
	    try {
		st = conn.createStatement();

		String query = "SELECT login FROM uzivatele WHERE ID_UZIVATEL=" + LoginDialogController.loggedContact.getAccountID();
		ResultSet rs = st.executeQuery(query);

		while (rs.next()) {
		    menuItemLogged.setText(String.valueOf(rs.getString("login")));
		    labelLoggedInUser.setText(String.valueOf(rs.getString("login")));

		}
		rs.close();
		st.close();

	    } catch (SQLException ex) {
		Logger.getLogger(MainWindowController.class
			.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    /**
     * Clicking search button will search through all users of network to find
     * corresponding
     *
     * @param event
     */
    @FXML
    private void handleButtonSearchAction(ActionEvent event) {
	ObservableList<Contacts> foundUsers = FXCollections.observableArrayList();
	if (textFieldNameFind.getText().isEmpty() && textFieldLastNameFind.getText().isEmpty()) {
	    handleButtonAddMoreContactsAction();
	    return;
	}
	if (textFieldNameFind.getText().isEmpty() && !textFieldLastNameFind.getText().isEmpty()) {

	    contactsList.forEach(e -> {
		if (e.getLastName().equals(textFieldLastNameFind.getText())) {
		    foundUsers.add(e);
		}
	    });
	    tableViewContacts.setItems(foundUsers);

	    return;
	}
	if (!textFieldNameFind.getText().isEmpty() && textFieldLastNameFind.getText().isEmpty()) {

	    contactsList.forEach(e -> {
		if (e.getName().equals(textFieldNameFind.getText())) {
		    foundUsers.add(e);
		}
	    });
	    tableViewContacts.setItems(foundUsers);

	    return;
	}

	if (!textFieldNameFind.getText().isEmpty() && !textFieldLastNameFind.getText().isEmpty()) {
	    contactsList.forEach(e -> {
		if (e.getName().equals(textFieldNameFind.getText()) && e.getLastName().equals(textFieldLastNameFind.getText())) {
		    foundUsers.add(e);
		}
	    });
	    tableViewContacts.setItems(foundUsers);
	}

    }

    /**
     * Clicking send message contacts below more users will show table view of
     * all users in contacts you can send message to (it's multi-user message)
     */
    private void handleButtonSendMessageAction() {
	labelSavedYesTeacher.setVisible(false);
	moreMessages = true;
	vBoxChat.getChildren().clear();
	scrollPaneFBmessages.setVisible(false);
	anchorMesagesToAll.setVisible(true);
	labelNotifyDate.setText("");
	labelNotifyName.setText("");
	labelNotifyText.setText("");
	gridAddContact.setVisible(false);
	gridComments.setVisible(false);
	gridChat.setVisible(true);
	gridEditProfile.setVisible(false);
	tableViewSendMessageMore.setVisible(true);
	labelMessagedUserName.setText("SEND MESSAGE");
	buttonAddNotification.setVisible(false);
	buttonEditNotification.setVisible(false);
	imageViewProfilePicture2.setVisible(false);
	columnLoginMess.setCellValueFactory(new PropertyValueFactory<>("login"));
	columnNameMess.setCellValueFactory(new PropertyValueFactory<>("name"));
	columnLastNameMess.setCellValueFactory(new PropertyValueFactory<>("lastName"));

	// selecting only users that are logged in's contacts
	String listNames = "SELECT u2.id_uzivatel, u2.jmeno, u2.login, u2.prijmeni, ku.kontakt_id_kontakt\n"
		+ "FROM uzivatele u\n"
		+ "JOIN kontakty_uzivatele ku ON u.id_uzivatel = ku.uzivatel_id_uzivatel\n"
		+ "JOIN kontakty k ON ku.kontakt_id_kontakt=k.id_kontakt\n"
		+ "JOIN uzivatele u2 ON k.uzivatele_id_uzivatel = u2.id_uzivatel\n"
		+ "WHERE ku.uzivatel_id_uzivatel=?";
	try {

	    PreparedStatement prstListNames;
	    prstListNames = conn.prepareStatement(listNames);

	    prstListNames.setInt(1, LoginDialogController.loggedContact.getAccountID());

	    ResultSet rs = prstListNames.executeQuery();
	    ObservableList<Contacts> contacts = FXCollections.observableArrayList();
	    while (rs.next()) {
		int userID = rs.getInt("id_uzivatel");
		int contactID = rs.getInt("kontakt_id_kontakt");
		String login = rs.getString("login");
		String name = rs.getString("jmeno");
		String lastName = rs.getString("prijmeni");

		contacts.add(new Contacts(userID, contactID, login, name, lastName, 0));
	    }
	    rs.close();
	    prstListNames.close();
	    tableViewSendMessageMore.setItems(contacts);
	    tableViewSendMessageMore.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Clicking menu item for managing plans opens up a new dialog
     * (ManagePlansDialog)
     *
     * @param event
     */
    @FXML
    private void handleMenuItemManagePlans(ActionEvent event) {

	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/ManagePlansDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    managePlans = stage;
	    managePlans.setOnHidden(e -> {
		if (!LoginDialogController.guestLogin) {
		    loadGroups();
		    loadUsers();
		}
	    });
	    stage.setScene(scene);
	    stage.show();

	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Opens up a dialog for changing your current plan after clicking on change
     * in user editing
     *
     * @param event
     */
    @FXML
    private void handleButtonChangeAction(ActionEvent event) {

	userEditing = true;
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/EditProfileChangePlanDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    stage.setOnHidden(e -> {
		if (!LoginDialogController.guestLogin) {
		    refreshPlans();
		    userEditing = false;
		    loadGroups();
		}
	    });
	    changeStudyPlans = stage;
	    stage.setScene(scene);
	    stage.show();

	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}

    }

    /**
     * Clicking post button in groups will send all details about the message to
     * the database
     *
     * @param event
     */
    @FXML
    private void handleButtonPostAction(ActionEvent event) {
	if (textAreaPost.getText().isEmpty()) {
	    ALERT.showValuesNotFilledAlert();
	    return;
	}
	try {
	    if (editingPost) {

		String queryUpdateText = "UPDATE komentare SET obsah = ? WHERE id_komentare = ?";
		PreparedStatement prstUpdateText = conn.prepareStatement(queryUpdateText);
		prstUpdateText.setString(1, textAreaPost.getText());
		prstUpdateText.setInt(2, editComentID);

		prstUpdateText.executeUpdate();
		prstUpdateText.close();
	    } else {
		String queryInsertPost = "INSERT INTO komentare (obsah, casove_razitko,"
			+ " blokace, autor_id) VALUES (?,CURRENT_TIMESTAMP, ?, ?)";

		PreparedStatement prstInsertPost = conn.prepareStatement(queryInsertPost);

		prstInsertPost.setNString(1, textAreaPost.getText());
		prstInsertPost.setInt(2, 0);
		prstInsertPost.setInt(3, LoginDialogController.loggedContact.getAccountID());

		prstInsertPost.executeUpdate();
		prstInsertPost.close();

		String queryInsertGroup = "INSERT INTO skupiny (komentar_id_komentare, sp_id_studijniho_planu) VALUES ((SELECT max(id_komentare) FROM komentare),?)";
		PreparedStatement prstInsertGroup = conn.prepareStatement(queryInsertGroup);

		prstInsertGroup.setInt(1, currentGroupID);

		prstInsertGroup.executeUpdate();
		prstInsertGroup.close();

	    }
	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
	handleButtonCancelEdit(null);
	textAreaPost.setText("");
	refreshPosts();

    }

    /**
     * This method is recursively called whenever it has any child comments, it
     * "prints" new comment to the application
     *
     * @param commentID
     * @param name
     * @param lastname
     * @param login
     * @param ban
     * @param text
     * @param time
     * @return
     */
    private VBox addComment(int commentID, String name, String lastname, String login, int ban, String text, String time) {
	String messageText = text;
	VBox commentPanel = new VBox();

	try {
	    // IF IT IS ROOT POST
	    String queryIsRoot = "SELECT komentar_id_komentare FROM skupiny";
	    PreparedStatement prstIsRoot = conn.prepareStatement(queryIsRoot);

	    ResultSet rsIsRoot = prstIsRoot.executeQuery();
	    while (rsIsRoot.next()) {
		int commentIDRoot = rsIsRoot.getInt("komentar_id_komentare");

		if (commentID == commentIDRoot) {
		    commentPanel.setBorder(new Border(new BorderStroke(Color.ORANGE, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(3))));
		}
	    }
	    rsIsRoot.close();
	    prstIsRoot.close();
	    String author = name + " " + lastname + "(" + login + ")";
	    commentPanel.setSpacing(5);

	    // HBOX TOP NAME + BUTTONS + TIMESTAMP
	    HBox topSide = new HBox();
	    topSide.prefWidthProperty().bind(scrollPanePosts.prefWidthProperty());

	    // AUTHOR NAME
	    Label labelName = new Label(author);

	    labelName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	    labelName.setPadding(new Insets(0, 0, 0, 0));
	    labelName.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, new CornerRadii(7), new Insets(-5, -5, -2, -5))));
	    labelName.setAlignment(Pos.BASELINE_LEFT);
	    labelName.setMinSize(100, 20);

	    // BUTTONS
	    HBox buttons = new HBox();
	    buttons.setAlignment(Pos.BASELINE_CENTER);

	    Button buttonReply = new Button();
	    buttonReply.setText("Reply");
	    setButtonVisuals(buttonReply);

	    Button buttonEdit = new Button();
	    buttonEdit.setText("Edit");
	    buttonEdit.setVisible(false);
	    setButtonVisuals(buttonEdit);

	    Button buttonDelete = new Button();
	    buttonDelete.setText("Delete");
	    buttonDelete.setVisible(false);
	    buttonDelete.setBackground(new Background(new BackgroundFill(Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY)));

	    buttonDelete.addEventHandler(MouseEvent.MOUSE_ENTERED,
		    new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    buttonDelete.setBackground(new Background(new BackgroundFill(Color.web("ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
		}
	    });
	    buttonDelete.addEventHandler(MouseEvent.MOUSE_EXITED,
		    new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    buttonDelete.setBackground(new Background(new BackgroundFill(Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY)));
		}
	    });

	    Button buttonBlock = new Button();
	    buttonBlock.setText("Block");
	    buttonBlock.setVisible(false);
	    buttonBlock.setBackground(new Background(new BackgroundFill(Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY)));

	    buttonBlock.addEventHandler(MouseEvent.MOUSE_ENTERED,
		    new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    buttonBlock.setBackground(new Background(new BackgroundFill(Color.web("ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
		}
	    });
	    buttonBlock.addEventHandler(MouseEvent.MOUSE_EXITED,
		    new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    buttonBlock.setBackground(new Background(new BackgroundFill(Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY)));
		}
	    });

	    buttons.setSpacing(15);
	    buttons.setAlignment(Pos.CENTER);
	    buttons.setPadding(new Insets(0, 0, 0, 60));
	    buttons.setMinSize(300, 20);
	    buttons.getChildren().addAll(buttonReply, buttonEdit, buttonDelete, buttonBlock);

	    // TIMESTAMP
	    Label timestamp = new Label(time);
	    timestamp.setAlignment(Pos.CENTER_RIGHT);
	    topSide.getChildren().addAll(labelName, buttons, timestamp);

	    // COMMENTS BELOW
	    TitledPane commentsPane = new TitledPane();
	    commentsPane.prefWidthProperty().bind(scrollPanePosts.prefWidthProperty());
	    commentsPane.setExpanded(false);
	    commentsPane.setText("Comments");
	    commentsPane.setStyle("-fx-background-color: #2c2c36");

	    VBox vboxComments = new VBox();
	    vboxComments.setStyle("-fx-background-color: #2c2c36");

	    String query = "SELECT * FROM komentare_uzivatelu_skupin WHERE komentar_id_komentare = ?";
	    PreparedStatement prstGetReplies = conn.prepareStatement(query);

	    prstGetReplies.setInt(1, commentID);

	    ResultSet rsReplies = prstGetReplies.executeQuery();
	    while (rsReplies.next()) {
		String date = rsReplies.getString("cas_odeslani");
		int newCommentID = rsReplies.getInt("id_komentare");
		String content = rsReplies.getNString("obsah");
		int newBan = rsReplies.getInt("blokace");
		String newName = rsReplies.getString("jmeno");
		String newLastname = rsReplies.getString("prijmeni");
		String newLogin = rsReplies.getString("login");
		vboxComments.getChildren().add(addComment(newCommentID, newName, newLastname, newLogin, newBan, content, date));
	    }
	    rsReplies.close();
	    prstGetReplies.close();

	    commentsPane.setContent(vboxComments);

	    // MESSAGE
	    Label labelMessage = new Label();
	    labelMessage.setStyle("-fx-font-weight: bold");
	    labelMessage.setWrapText(true);
	    labelMessage.setPadding(new Insets(0, 0, 0, 0));
	    labelMessage.setMinSize(500, 50);
	    labelMessage.setAlignment(Pos.TOP_LEFT);

	    if (ban == 1) {
		messageText = "COMMENT BLOCKED!";
		labelMessage.setBackground(new Background(new BackgroundFill(Color.DARKRED, new CornerRadii(7), new Insets(-2, -5, -2, -5))));
		buttonBlock.setText("Unblock");
		buttonEdit.setDisable(true);
		buttonReply.setDisable(true);

	    } else {
		labelMessage.setBackground(new Background(new BackgroundFill(Color.web("aaaabb"), new CornerRadii(7), new Insets(-2, -5, -2, -5))));
		buttonBlock.setText("Block");
		buttonEdit.setDisable(false);
	    }

	    labelMessage.setText(messageText);

	    // GET ROLES
	    String queryGetRoles = "SELECT id_role FROM role_uzivatelu WHERE id_uzivatel = ?";

	    PreparedStatement prstGetRole = conn.prepareStatement(queryGetRoles);

	    prstGetRole.setInt(1, LoginDialogController.loggedContact.getAccountID());

	    ResultSet rsRoles = prstGetRole.executeQuery();

	    int groupTasksCount = doesGroupHaveTasks();;

	    while (rsRoles.next()) {
		int roleID = rsRoles.getInt("id_role");
		switch (roleID) {
		    case 1:
			buttonBlock.setVisible(true);
			buttonDelete.setVisible(true);
			buttonEdit.setVisible(true);
			if (groupTasksCount > 0) {
			    btnEditTasks.setVisible(true);
			    btnTasksOverview.setVisible(false);
			    btnManageUsersTask.setVisible(true);
			    btnAddTasks.setVisible(false);
			} else {
			    btnAddTasks.setVisible(true);
			    btnEditTasks.setVisible(false);
			    btnManageUsersTask.setVisible(false);
			    btnTasksOverview.setVisible(false);
			}
			break;
		    case 2:
			buttonDelete.setVisible(true);
			buttonEdit.setVisible(true);
			buttonBlock.setVisible(true);
			if (groupTasksCount > 0) {
			    btnEditTasks.setVisible(true);
			    btnTasksOverview.setVisible(false);
			    btnManageUsersTask.setVisible(true);
			    btnAddTasks.setVisible(false);
			} else {
			    btnAddTasks.setVisible(true);
			    btnEditTasks.setVisible(false);
			    btnManageUsersTask.setVisible(false);
			    btnTasksOverview.setVisible(false);
			}
			break;
		    default:
			if (groupTasksCount > 0) {
			    btnTasksOverview.setVisible(true);
			} else {
			    btnTasksOverview.setVisible(false);
			}
			break;
		}
	    }
	    rsRoles.close();
	    prstGetRole.close();
	    // WHICH BUTTONS TO SHOW
	    if (login.equals(LoginDialogController.loggedContact.getLogin())) {
		buttonDelete.setVisible(true);
		buttonEdit.setVisible(true);

	    }

	    // SETTING WHAT BUTTONS DO
	    buttonReply.setOnAction(e -> {
		replyingToText = text;
		replyingToName = name + " " + lastname + " " + " (" + login + ")";
		replyingToCommentID = commentID;
		try {
		    Stage stage = new Stage();
		    Parent root = FXMLLoader.load(getClass().getResource("/fxml/ReplyToPostDialog.fxml"));
		    Scene scene = new Scene(root);
		    stage.setResizable(false);
		    stage.setOnHidden(ev -> {
			if (!LoginDialogController.guestLogin) {
			    refreshPosts();
			}
		    });
		    replyToPost = stage;
		    stage.setScene(scene);
		    stage.show();

		} catch (IOException ex) {
		    Logger.getLogger(MainWindowController.class
			    .getName()).log(Level.SEVERE, null, ex);
		}
	    });

	    buttonEdit.setOnAction(e -> {
		buttonPost.setText("Save");
		textAreaPost.setText(text);
		editingPost = true;
		editComentID = commentID;
		buttonCancelEdit.setVisible(true);
		refreshPosts();
	    });
	    buttonDelete.setOnAction(e -> {
		if (editComentID == commentID) {
		    handleButtonCancelEdit(null);
		}
		try {
		    String queryDeleteGroup = "DELETE FROM skupiny WHERE komentar_id_komentare = ?";
		    PreparedStatement prstDeleteGroup = conn.prepareStatement(queryDeleteGroup);

		    prstDeleteGroup.setInt(1, commentID);

		    prstDeleteGroup.executeUpdate();
		    prstDeleteGroup.close();

		    String queryDelete = "DELETE FROM komentare WHERE id_komentare = ?";
		    PreparedStatement prstDelete = conn.prepareStatement(queryDelete);

		    prstDelete.setInt(1, commentID);

		    prstDelete.executeUpdate();
		    prstDelete.close();

		} catch (SQLException ex) {
		    Logger.getLogger(MainWindowController.class
			    .getName()).log(Level.SEVERE, null, ex);
		}
		refreshPosts();
	    });
	    buttonBlock.setOnAction(e -> {
		String queryBlockPost = "UPDATE komentare SET blokace = ? WHERE id_komentare=?";
		try {

		    PreparedStatement prstUpdateBlock = conn.prepareStatement(queryBlockPost);

		    prstUpdateBlock.setInt(2, commentID);

		    if (ban == 1) {

			prstUpdateBlock.setInt(1, 0);

		    } else {
			prstUpdateBlock.setInt(1, 1);
		    }
		    prstUpdateBlock.executeUpdate();
		    prstUpdateBlock.close();

		} catch (SQLException ex) {
		    Logger.getLogger(MainWindowController.class
			    .getName()).log(Level.SEVERE, null, ex);
		}

		refreshPosts();
	    });

	    commentPanel.getChildren().add(0, topSide);
	    commentPanel.getChildren().add(1, labelMessage);
	    commentPanel.getChildren().add(2, commentsPane);

	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}

	commentPanel.setPadding(new Insets(10, 10, 10, 10));
	commentPanel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	return commentPanel;
    }

    /**
     * This method will refresh all the posts (re-reads all the comments in the
     * database and see if it has any change)
     */
    private void refreshPosts() {
	vBoxMainComments.getChildren().clear();

	String queryGetPosts = "SELECT cas_odeslani, id_komentare, obsah, jmeno, prijmeni, login, blokace FROM prispevky_skupin WHERE sp_id_studijniho_planu = ?";

	try {
	    PreparedStatement prstGetPosts = conn.prepareStatement(queryGetPosts);
	    prstGetPosts.setInt(1, currentGroupID);

	    ResultSet rsGetPosts = prstGetPosts.executeQuery();
	    while (rsGetPosts.next()) {
		String date = rsGetPosts.getString("cas_odeslani");
		int commentID = rsGetPosts.getInt("id_komentare");
		String content = rsGetPosts.getNString("obsah");
		String nameComment = rsGetPosts.getString("jmeno");
		String lastname = rsGetPosts.getString("prijmeni");
		String login = rsGetPosts.getString("login");
		int ban = rsGetPosts.getInt("blokace");

		vBoxMainComments.getChildren().add(addComment(commentID, nameComment, lastname, login, ban, content, date));
	    }
	    rsGetPosts.close();
	    prstGetPosts.close();

	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}

    }

    /**
     * This method re-reads all the details about messages in database and see
     * if it has any changes / additions
     */
    private void refreshChat() {

	if (!moreMessages) {
	    try {
		vBoxChat.getChildren().clear();
		// all messages between me and him
		Statement messages = conn.createStatement();

		String queryMessages = "SELECT z.obsah, to_char(z.casove_razitko,'HH:MI') AS cas_odeslani, zk.adresat_id, zk.kontakt_id_kontakt,z.casove_razitko\n"
			+ "FROM zpravy z\n"
			+ "JOIN zpravy_kontaktu zk ON z.id_zprava=zk.zprava_id_zprava\n"
			+ "WHERE zk.adresat_id=" + LoginDialogController.loggedContact.getContactID() + " AND zk.kontakt_id_kontakt=" + currentContact.getContactID() + "\n"
			+ "OR zk.adresat_id=" + currentContact.getContactID() + " AND zk.kontakt_id_kontakt=" + LoginDialogController.loggedContact.getContactID() + "\n"
			+ "ORDER BY z.casove_razitko";

		ResultSet messagesResults = messages.executeQuery(queryMessages);

		while (messagesResults.next()) {
		    int reciever = messagesResults.getInt("adresat_id");
		    String message = messagesResults.getNString("obsah");
		    String timestamp = messagesResults.getString("cas_odeslani");

		    VBox vBoxSingleMessage = new VBox();

		    Label labelTimeStamp = new Label();
		    labelTimeStamp.setText(timestamp);

		    if (reciever == currentContact.getContactID()) {

			Label myMessage = new Label();
			myMessage.setText(message);
			myMessage.setTextFill(Color.web("#aaaabb"));
			myMessage.setPadding(new Insets(0, 5, 0, 5));
			myMessage.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(7), new Insets(-2, -5, -2, -5))));
			myMessage.setFont(new Font(20));
			myMessage.setWrapText(true);

			vBoxSingleMessage.getChildren().addAll(labelTimeStamp, myMessage);
			HBox hBoxRight = new HBox();
			hBoxRight.getChildren().add(vBoxSingleMessage);
			hBoxRight.setAlignment(Pos.BASELINE_RIGHT);

			vBoxChat.getChildren().add(hBoxRight);
			vBoxChat.setMargin(hBoxRight, new Insets(5, 20, 5, 5));
		    } else {
			Label hisMessage = new Label();
			hisMessage.setText(message);
			hisMessage.setTextFill(Color.web("#2c2c36"));
			hisMessage.setPadding(new Insets(0, 5, 0, 5));
			hisMessage.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(7), new Insets(-2, -5, -2, -5))));
			hisMessage.setFont(new Font(20));
			hisMessage.setWrapText(true);

			vBoxSingleMessage.getChildren().addAll(labelTimeStamp, hisMessage);

			HBox hBoxLeft = new HBox();
			hBoxLeft.getChildren().add(vBoxSingleMessage);
			hBoxLeft.setAlignment(Pos.BASELINE_LEFT);

			vBoxChat.getChildren().add(hBoxLeft);
			vBoxChat.setMargin(hBoxLeft, new Insets(5, 5, 5, 20));
		    }
		}
		messagesResults.close();
		messages.close();

		// NOTIFICATIONS
		Statement getNotifyCationQuery = conn.createStatement();

		String getNotification = "SELECT p.nazev, TO_CHAR(p.datum, 'yyyy-dd-mm') AS datum, p.poznamka\n"
			+ "FROM pripominky p\n"
			+ "INNER JOIN pripominky_kontaktu pk\n"
			+ "ON p.id_pripominky=pk.pripominky_id_pripominky\n"
			+ "WHERE pk.id_adresat=" + currentContact.getContactID() + ""
			+ " AND pk.kontakt_id_kontakt=" + LoginDialogController.loggedContact.getContactID() + "\n"
			+ "OR pk.id_adresat=" + LoginDialogController.loggedContact.getContactID() + " AND pk.kontakt_id_kontakt=" + currentContact.getContactID();

		ResultSet rsNotify = getNotifyCationQuery.executeQuery(getNotification);
		notifyIsSet = false;
		while (rsNotify.next()) {
		    String notifyName = rsNotify.getString("nazev");
		    String date = rsNotify.getString("datum");
		    String description = rsNotify.getNString("poznamka");
		    notifyIsSet = true;
		    labelNotifyDate.setText(String.valueOf(date));
		    labelNotifyName.setText(notifyName);
		    labelNotifyText.setText(description);

		}
		rsNotify.close();
		getNotifyCationQuery.close();
		if (notifyIsSet) {
		    buttonAddNotification.setVisible(false);
		    buttonEditNotification.setVisible(true);
		} else {
		    buttonAddNotification.setVisible(true);
		    buttonEditNotification.setVisible(false);
		    labelNotifyDate.setText("");
		    labelNotifyName.setText("");
		    labelNotifyText.setText("");
		}
		imageViewProfilePicture2.setVisible(true);

		String getFotky = "SELECT FOTKA FROM FOTKY F\n"
			+ "INNER JOIN FOTKY_UZIVATELU FU\n"
			+ "ON FU.FOTKY_ID_FOTKY = F.ID_FOTKY WHERE FU.UZIVATELE_ID_UZIVATEL = ?";

		PreparedStatement prstmFotky = conn.prepareStatement(getFotky);
		prstmFotky.setInt(1, currentContact.getAccountID());
		ResultSet rsFotky = prstmFotky.executeQuery();
		while (rsFotky.next()) {
		    Blob b = rsFotky.getBlob("fotka");
		    imageViewProfilePicture2.setImage(new Image(b.getBinaryStream()));
		}
		rsFotky.close();
		prstmFotky.close();

	    } catch (SQLException ex) {
		Logger.getLogger(MainWindowController.class
			.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    /**
     * Clicking cancel edit in groups when editing will stop editing that post
     * and it will be returned to before state
     *
     * @param event
     */
    @FXML
    private void handleButtonCancelEdit(ActionEvent event) {
	buttonPost.setText("Post");
	buttonCancelEdit.setVisible(false);
	editingPost = false;
	textAreaPost.setText("");
    }

    /**
     * Opens up a dialog for emojis
     *
     * @param event
     */
    @FXML
    private void handleButtonEmojiAction(ActionEvent event) {
	appendEmojiToArea = textAreaSendMessage;
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AddEmojiDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    addEmoji = stage;
	    stage.setTitle("Emoji selection");
	    stage.setOnHidden(e -> {
		textAreaSendMessage = appendEmojiToArea;
	    });
	    stage.setScene(scene);
	    stage.show();

	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Allows a user to add a user specific photo that will be uploaded to
     * database
     *
     * @param event
     * @throws IOException
     * @throws SQLException
     */
    @FXML
    private void handleButtonInsertPhotoAction(ActionEvent event) throws IOException, SQLException {
	FileChooser fileChooser = new FileChooser();
	fileChooser.setTitle("Select Profile Picture");
	fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
	file = fileChooser.showOpenDialog(null);
	if (file != null) {
	    Image image = new Image(file.toURI().toString());
	    imageViewProfilePicture.setImage(image);
	}
    }

    /**
     * This method is called when we are getting the info about current user's
     * photo
     *
     * @throws FileNotFoundException
     */
    private void refreshPhoto() throws FileNotFoundException {
	if (file != null) {
	    try {
		String queryGetIdFotky = "SELECT fotky_id_fotky FROM fotky_uzivatelu WHERE uzivatele_id_uzivatel = ?";

		PreparedStatement prstGetIdFotky = conn.prepareStatement(queryGetIdFotky);

		prstGetIdFotky.setInt(1, LoginDialogController.loggedContact.getAccountID());

		ResultSet rsIdFotky = prstGetIdFotky.executeQuery();

		while (rsIdFotky.next()) {
		    int fotkaID = rsIdFotky.getInt("fotky_id_fotky");

		    String queryDeleteFotkyUzivatelu = "DELETE FROM fotky_uzivatelu WHERE uzivatele_id_uzivatel = ?";
		    PreparedStatement prstDeleteFotkyUzivatelu = conn.prepareStatement(queryDeleteFotkyUzivatelu);

		    prstDeleteFotkyUzivatelu.setInt(1, LoginDialogController.loggedContact.getAccountID());

		    prstDeleteFotkyUzivatelu.executeUpdate();
		    prstDeleteFotkyUzivatelu.close();

		    if (fotkaID != 0) {
			String queryDeleteFotky = "DELETE FROM fotky WHERE id_fotky = ?";
			PreparedStatement prstDeleteFotky = conn.prepareStatement(queryDeleteFotky);

			prstDeleteFotky.setInt(1, fotkaID);

			prstDeleteFotky.executeUpdate();
			prstDeleteFotky.close();
		    }
		}
		rsIdFotky.close();
		prstGetIdFotky.close();
		String query = "INSERT INTO fotky (fotka,nazev_souboru, typ_souboru, pripona_souboru, datum_nahrani) "
			+ "VALUES ( ?, ?, ?, ?, CURRENT_TIMESTAMP)";
		PreparedStatement prstInsertFotky = conn.prepareStatement(query);
		FileInputStream in = new FileInputStream(file);
		prstInsertFotky.setBinaryStream(1, in, (int) file.length());
		prstInsertFotky.setNString(2, file.getName().replace(".", ""));
		prstInsertFotky.setNString(3, getFileType(file));
		prstInsertFotky.setNString(4, getFileExtension(file));
		prstInsertFotky.executeUpdate();
		prstInsertFotky.close();

		String lastPhotoQuery = "SELECT MAX(id_fotky) AS id_fotky FROM fotky";

		PreparedStatement prstLastPhoto = conn.prepareStatement(lastPhotoQuery);
		ResultSet rs = prstLastPhoto.executeQuery();

		photoID = 0;
		while (rs.next()) {
		    photoID = rs.getInt("id_fotky");
		}
		rs.close();
		prstLastPhoto.close();

		String queryFotkyUzivatelu = "INSERT INTO fotky_uzivatelu (fotky_id_fotky, uzivatele_id_uzivatel) VALUES (?, ?)";
		PreparedStatement prstInsertFotkyUzivatelu = conn.prepareStatement(queryFotkyUzivatelu);
		prstInsertFotkyUzivatelu.setInt(1, photoID);
		prstInsertFotkyUzivatelu.setInt(2, LoginDialogController.loggedContact.getAccountID());
		prstInsertFotkyUzivatelu.executeUpdate();
		prstInsertFotkyUzivatelu.close();

	    } catch (SQLException ex) {
		ALERT.showCouldNotExecuteCommand();
	    }
	}
    }

    /**
     * Returns file extensions from the filename
     *
     * @param file
     * @return
     */
    private static String getFileExtension(File file) {
	String fileName = file.getName();
	if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
	    return fileName.substring(fileName.lastIndexOf("."));
	} else {
	    return "";
	}
    }

    /**
     * Returns file type of the file (picture)
     *
     * @param file
     * @return
     */
    private static String getFileType(File file) {
	String fileName = file.getName();
	if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
	    return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
	} else {
	    return "";
	}
    }

    /**
     * Clicking this button opens up a GroupAddTasksDialog dialog.
     *
     * @param event
     */
    @FXML
    private void handleButtonAddTasksAction(ActionEvent event) {
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/GroupAddTasksDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    groupAddTasks = stage;
	    if (isEditingTasks) {
		stage.setTitle("Edit tasks");
	    } else {
		stage.setTitle("Add tasks");
	    }
	    stage.setOnHidden(e -> {
		int taskCount = doesGroupHaveTasks();

		if (taskCount > 0) {
		    btnEditTasks.setVisible(true);
		    btnAddTasks.setVisible(false);
		    btnManageUsersTask.setVisible(true);
		} else {
		    btnEditTasks.setVisible(false);
		    btnAddTasks.setVisible(true);
		    btnManageUsersTask.setVisible(false);
		}
	    });
	    stage.setScene(scene);
	    stage.show();

	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Clicking this buttons opens up an EditTasksDialog where user can see
     * their standings in tasks
     *
     * @param event
     */
    @FXML
    private void handleButtonTaskOverviewAction(ActionEvent event) {
	try {
	    isCheckingTasks = true;
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/EditTasksDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    showTaskOverview = stage;
	    stage.setTitle("User tasks");
	    stage.setScene(scene);
	    stage.show();

	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Clicking this button will allow teachers or admins to edit tasks for
     * group
     *
     * @param event
     */
    @FXML
    private void handleButtonEditTasksAction(ActionEvent event) {
	isEditingTasks = true;
	handleButtonAddTasksAction(null);
    }

    /**
     * Clicking this button will allow admin or teacher to edit users tasks
     * standings
     *
     * @param event
     */
    @FXML
    private void handleButtonManageUsersTask(ActionEvent event) {
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/ManageUserTasksDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    manageUserTasks = stage;
	    stage.setTitle("User tasks");
	    stage.setScene(scene);
	    stage.show();

	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * This method calls a function in database and asks whether it has any
     * tasks so that it can decide what buttons to show in group
     *
     * @return 0 or 1 (true or false)
     */
    private int doesGroupHaveTasks() {
	try {
	    CallableStatement cstmt = conn.prepareCall("{? = call ma_skupina_ukoly(?)}");
	    cstmt.registerOutParameter(1, Types.INTEGER);
	    cstmt.setInt(2, currentGroupID);
	    cstmt.execute();
	    int result = cstmt.getInt(1);
	    cstmt.close();
	    return result;

	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
	return 0;
    }

    /**
     * This opens up an emoji dialog in groups
     *
     * @param event
     */
    @FXML
    private void handleButtonGroupEmojiAction(ActionEvent event) {
	appendEmojiToArea = textAreaPost;
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AddEmojiDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    addEmoji = stage;
	    stage.setTitle("Emoji selection");
	    stage.setOnHidden(e -> {
		textAreaPost = appendEmojiToArea;
	    });
	    stage.setScene(scene);
	    stage.show();

	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Sets a custom visuals to buttons in this dialog
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

    /**
     * Sets all buttons in this dialog to the specific visuals
     */
    private void setButtons() {
	setButtonVisuals(buttonLoadFriends);
	setButtonVisuals(buttonLoadTaughtSubjects);
	setButtonVisuals(btnRegister);
	setButtonVisuals(btnSendMessageChat);
	setButtonVisuals(btnAddTasks);
	setButtonVisuals(btnEditTasks);
	setButtonVisuals(btnManageUsersTask);
	setButtonVisuals(btnSendEmojiChat);
	setButtonVisuals(btnTasksOverview);
	setButtonVisuals(buttonAddNotification);
	setButtonVisuals(buttonCancelEdit);
	setButtonVisuals(buttonChangePlan);
	setButtonVisuals(buttonEditNotification);
	setButtonVisuals(buttonInsertPhoto);
	setButtonVisuals(buttonPost);
	setButtonVisuals(buttonSaveNoTeacher);
	setButtonVisuals(buttonSaveYesTeacher);
	setButtonVisuals(btnSearchUsers);
	setButtonVisuals(btnAddEmojiPosts);
    }

    /**
     * Allows administrator who emulated a user to go back to his profile
     *
     * @param event
     */
    @FXML
    private void handleMenuItemLogBackAdminAction(ActionEvent event) {
	isCurrentUserAdmin = true;
	LoginDialogController.loggedContact = LoginDialogController.previousContact;
	LoginDialogController.previousContact = null;

	LoginDialogController.mainWindow.close();
	Stage stage = new Stage();
	Parent root;
	try {
	    root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));

	    Scene scene = new Scene(root);
	    stage.setResizable(true);
	    stage.setMaximized(true);
	    LoginDialogController.mainWindow = stage;
	    stage.setScene(scene);
	    stage.show();

	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * If the user is not logged in this button appears that will allow him to
     * register
     *
     * @param event
     */
    @FXML
    private void handleButtonRegisterAction(ActionEvent event) {
	LoginDialogController.mainWindow.close();
	LoginDialogController.mainWindow = null;
	LoginDialogController.guestLogin = false;
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignUpDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    LoginDialogController.registerWindow = stage;
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    ALERT.showCouldNotLoadWindowAlert();
	}
    }

    /**
     * This method fills teacher's subjects into his profile (if he's a teacher)
     *
     * @param account
     * @throws SQLException
     */
    private void fillSubjectsTableTeacher(Contacts account) throws SQLException {
	teacherSubjectTable.clear();

	String getSubjectsQuery = "SELECT * FROM predmety_veskere_info WHERE id_ucitele = ?";
	try {
	    PreparedStatement prstGetSubjects = conn.prepareStatement(getSubjectsQuery);

	    prstGetSubjects.setInt(1, account.getAccountID());

	    ResultSet rsSubjects = prstGetSubjects.executeQuery();
	    while (rsSubjects.next()) {
		String shortName = rsSubjects.getString("zkratka");
		String name = rsSubjects.getString("nazev");
		String plan = rsSubjects.getString("nazev_planu");

		teacherSubjectTable.add(new Subject(0, shortName, name, null, plan, null));
	    }
	    rsSubjects.close();
	    prstGetSubjects.close();
	    tableViewSubjects.setItems(teacherSubjectTable);

	} catch (SQLException ex) {
	    Logger.getLogger(MainWindowController.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
	contactInfo = account;
    }

    /**
     * This button loads his friends into the table view in user's profile
     *
     * @param account
     */
    private void loadFriends(Contacts account) {
	friends.clear();
	String getFriendsQuery = "SELECT u2.id_uzivatel, u2.jmeno, u2.login, u2.prijmeni, u2.blokace, ku.kontakt_id_kontakt\n"
		+ "FROM uzivatele u\n"
		+ "JOIN kontakty_uzivatele ku ON u.id_uzivatel = ku.uzivatel_id_uzivatel\n"
		+ "JOIN kontakty k ON ku.kontakt_id_kontakt=k.id_kontakt\n"
		+ "JOIN uzivatele u2 ON k.uzivatele_id_uzivatel = u2.id_uzivatel\n"
		+ "WHERE ku.uzivatel_id_uzivatel=?";
	try {
	    PreparedStatement prstGetFriends = conn.prepareStatement(getFriendsQuery);
	    prstGetFriends.setInt(1, account.getAccountID());
	    ResultSet rsFriends = prstGetFriends.executeQuery();

	    while (rsFriends.next()) {
		int friendID = rsFriends.getInt("id_uzivatel");
		String login = rsFriends.getString("login");
		String name = rsFriends.getString("jmeno");
		String lastName = rsFriends.getString("prijmeni");
		int contactID = rsFriends.getInt("kontakt_id_kontakt");
		int ban = rsFriends.getInt("blokace");
		if (friendID != LoginDialogController.loggedContact.getAccountID()) {
		    friends.add(new Contacts(friendID, contactID, login, name, lastName, ban));
		}

	    }
	    rsFriends.close();
	    prstGetFriends.close();

	} catch (SQLException ex) {
	    Logger.getLogger(ManagePlansDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
	contactInfo = account;
	tableViewFriends.setItems(friends);
    }

    /**
     * This button switches between table views and buttons shown
     *
     * @throws SQLException
     */
    @FXML
    private void handleButtonLoadFriends() throws SQLException {
	labelTaughtSubjects.setText("Friends:");

	buttonLoadTaughtSubjects.setVisible(true);
	buttonLoadFriends.setVisible(false);

	tableViewFriends.setVisible(true);
	tableViewSubjects.setVisible(false);

	loadFriends(contactInfo);
    }

    /**
     * This button switches between table views and buttons which are shown
     *
     * @throws SQLException
     */
    @FXML
    private void handleButtonLoadTaughtSubjects() throws SQLException {

	labelTaughtSubjects.setText("Taught subjects:");

	buttonLoadTaughtSubjects.setVisible(false);
	buttonLoadFriends.setVisible(true);

	tableViewSubjects.setVisible(true);
	tableViewFriends.setVisible(false);
	fillSubjectsTableTeacher(contactInfo);

    }
}
