package resources;


public interface IAlerts {
    /**
     * Shows error message for wrong credentials entered.
     */
    void showIncorrectCredentialsAlert();
    
    /**
     * Shows alert that passwords don't match
     */
    void showPasswordsDontMatchAlert();
    
    /**
     * Shows alert that not all fields are filled with info
     */
    void showValuesNotFilledAlert();
    
    /**
     * Shows alert when inserting a login that is not 6 chars long.
     */
    void showIncorrectLoginLengthAlert();
    
    /**
     * Shows login already in use alert.
     */
    void showLoginAlreadyInUseAlert();
    
    /**
     * Shows could not establish connection alert
     */
    void showCouldNotEstablishConnectionAlert();
    
    /**
     * 
     * Shows could not load a window alert
     */
    void showCouldNotLoadWindowAlert();
    
    /**
     * Shows could not execute said command alert
     */
    void showCouldNotExecuteCommand();
    
    /**
     * Shows alert that the account you are trying to sign in with is banned
     */
    void showBannedAccountAlert();
    
    /**
     * Shows alert that you didn't fill all needed fields for search
     */
    void showNotAllFilledForSearch();
}
