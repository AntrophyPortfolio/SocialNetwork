package resources;

public class Task {
    private int ID;
    private String text;

    public Task(int ID, String text) {
	this.ID = ID;
	this.text = text;
    }

    public int getID() {
	return ID;
    }

    public void setID(int ID) {
	this.ID = ID;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    
    @Override
    public String toString() {
	return text;
    }
    
    
}
