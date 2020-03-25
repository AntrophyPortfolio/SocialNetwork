package resources;

import javafx.scene.control.Alert;

public class Alerts implements IAlerts {

    public Alerts() {
    }

    private void alertError(String title, String headerText, String contentText) {
	Alert alert = new Alert(Alert.AlertType.ERROR);
	alert.setTitle(title);
	alert.setHeaderText(headerText);
	alert.setContentText(contentText);

	alert.showAndWait();
    }

    private void alertInformation(String title, String headerText, String contentText) {
	Alert alert = new Alert(Alert.AlertType.INFORMATION);
	alert.setTitle(title);
	alert.setHeaderText(headerText);
	alert.setContentText(contentText);

	alert.showAndWait();
    }

    @Override
    public void showIncorrectCredentialsAlert() {
	alertError("Wrong credentials", "You have entered invalid credentials",
		"Try to log in again with different password or username for your account.");
    }

    @Override
    public void showPasswordsDontMatchAlert() {
	alertError("Error", "Please recheck entered passwords.",
		"Entered passwords do not match.");
    }

    @Override
    public void showValuesNotFilledAlert() {
	alertError("Error", "Not all fields were entered.",
		"Please make sure to fill all neccessary info.");
    }

    @Override
    public void showIncorrectLoginLengthAlert() {
	alertError("Error", "You cannot be sign up with the login you provided.",
		"Please make sure the login is 6 characters long.");
    }

    @Override
    public void showLoginAlreadyInUseAlert() {
	alertError("Error", "The login is already in use, try another.",
		"Please try to come up with different login name.");
    }

    @Override
    public void showCouldNotEstablishConnectionAlert() {
	alertError("Error", "Could not establish connection to the server.",
		"Please, make sure you are connected to the Internet and VPN is active.");
    }

    @Override
    public void showCouldNotLoadWindowAlert() {
	alertError("Error", "Could not load selected window due to internal issues",
		"Please contact the administrator of  network.");
    }

    @Override
    public void showCouldNotExecuteCommand() {
	alertError("Error", "Could not execute this command, database error.",
		"Please contact the administrator of network");
    }

    @Override
    public void showBannedAccountAlert() {
	alertError("Error", "Your account is banned.",
		"Please, contact administrator for more information.");
    }

    @Override
    public void showNotAllFilledForSearch() {
	alertError("Error", "You didn't fill all needed fields for search",
		"Please, make sure to fill in both name and last name for search to work.");

    }

}
