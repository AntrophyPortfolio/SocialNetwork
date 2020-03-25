package dialogs;

import static dialogs.MainWindowController.addEmoji;
import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
/**
 * Opens up a new dialog for replying to post where the text you are replying to is shown.
 * @author Antrophy
 */
public class ReplyToPostDialogController implements Initializable {

    @FXML
    private Label labelReplyingToText;
    @FXML
    private TextArea textAreaReply;
    @FXML
    private Label labelNameOfUser;

    private final Connection conn = LoginDialogController.CONN;

    private static TextArea appendEmojiToArea;
    
    public static TextArea geSendMessageArea() {
	return appendEmojiToArea;
    }
    
    public static boolean replyingToPost = false;
    @FXML
    private Button btnSend;
    @FXML
    private Button btnEmoji;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	labelReplyingToText.setText(MainWindowController.getReplyingToText());
	labelNameOfUser.setText(MainWindowController.getReplyingToName());
	
	setButtonVisuals(btnSend);
	setButtonVisuals(btnEmoji);
    }

    /**
     * When send button is pressed the post is saved in the database and shown to all users in group
     * @param event 
     */
    @FXML
    private void handleButtonSendAction(ActionEvent event) {
	try {
	    String queryInsert = "INSERT INTO komentare (obsah, casove_razitko, blokace, komentar_id_komentare, autor_id)"
		    + " VALUES (?, CURRENT_TIMESTAMP, 0, ?, ?)";
	    
	    PreparedStatement prstInsertToComments = conn.prepareStatement(queryInsert);

	    prstInsertToComments.setNString(1, textAreaReply.getText());
	    prstInsertToComments.setInt(2, MainWindowController.getReplyingToCommentID());
	    prstInsertToComments.setInt(3, LoginDialogController.loggedContact.getAccountID());

	    prstInsertToComments.executeUpdate();
	    prstInsertToComments.close();

	} catch (SQLException ex) {
	    Logger.getLogger(ReplyToPostDialogController.class.getName()).log(Level.SEVERE, null, ex);
	}
	MainWindowController.replyToPost.close();
    }
/**
 * When emoji button is clicked show all emojis to enter
 * @param event 
 */
    @FXML
    private void handleButtonEmojiAction(ActionEvent event) {
	replyingToPost = true;
	appendEmojiToArea = textAreaReply;
	try {
	    Stage stage = new Stage();
	    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AddEmojiDialog.fxml"));
	    Scene scene = new Scene(root);
	    stage.setResizable(false);
	    addEmoji = stage;
	    stage.setTitle("Emoji selection");
	    stage.setOnHidden(e -> {
		textAreaReply = appendEmojiToArea;
		replyingToPost = false;
	    });
	    stage.setScene(scene);
	    stage.show();
	} catch (IOException ex) {
	    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    /**
     * Setting a custom button visual
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
