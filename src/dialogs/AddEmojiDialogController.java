package dialogs;

import java.net.URL;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * Opens up a dialog with emoji selection
 *
 * @author Antrophy
 */
public class AddEmojiDialogController implements Initializable {

    @FXML
    private GridPane emojiGrid;
    @FXML
    private Button btnClose;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

	setButtonVisuals(btnClose);

	byte[] data = "😀".getBytes(UTF_8);

	for (int i = 0; i < 6; i++) {
	    for (int j = 0; j < 6; j++) {

		String text = new String(data, UTF_8);
		Button newBtn = new Button();
		newBtn.setText(text);

		setButtonVisuals(newBtn);

		newBtn.setOnAction(e -> {
		    if (ReplyToPostDialogController.replyingToPost) {
			ReplyToPostDialogController.geSendMessageArea().appendText(" " + newBtn.getText());
		    } else {
			MainWindowController.geSendMessageArea().appendText(" " + newBtn.getText());
		    }
		});
		newBtn.setStyle("-fx-font-size: 2em");
		newBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setFillHeight(newBtn, true);
		GridPane.setFillWidth(newBtn, true);
		emojiGrid.add(newBtn, i, j);

		data = incrementBytes(data);
	    }
	}
    }

    /**
     * Increments bytes for the parameter so that I can get the next emoji.
     *
     * @param data
     * @return
     */
    public static byte[] incrementBytes(byte[] data) {
	for (int i = data.length - 1; i >= 0; i--) {
	    data[i] = (byte) (data[i] + 1);

	    // No overflow
	    if (data[i] != Byte.MIN_VALUE) {
		return data;
	    }
	}
	// Full overflow
	byte[] biggerData = new byte[data.length + 1];
	biggerData[0] = Byte.MIN_VALUE + 1;
	System.arraycopy(data, 0, biggerData, 1, data.length);
	return biggerData;
    }

    /**
     * Closes current dialog
     *
     * @param event
     */
    @FXML
    private void handleButtonCloseAction(ActionEvent event) {
	MainWindowController.addEmoji.close();
    }

    /**
     * Sets a custom visuals for button in this dialog
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
