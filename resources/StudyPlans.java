package resources;

import javafx.beans.property.SimpleStringProperty;


public class StudyPlans {
    private SimpleStringProperty planName;
    private SimpleStringProperty field;
    private int planID;
	    
    public StudyPlans(int planID, String planName, String field) {
	this.planName = new SimpleStringProperty(planName);
	this.field = new SimpleStringProperty(field);
	this.planID = planID;
    }

    public String getPlanName() {
	return planName.get();
    }

    public void setPlanName(String planName) {
	this.planName = new SimpleStringProperty(planName);
    }

    public String getField() {
	return field.get();
    }

    public void setField(String field) {
	this.field = new SimpleStringProperty(field);
    }

    public int getPlanID() {
	return planID;
    }

    public void setPlanID(int planID) {
	this.planID = planID;
    }

    
    
    
    
    
    
}
