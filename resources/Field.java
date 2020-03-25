
package resources;


public class Field {
    
    private int fieldID;
    private String fieldName;

    public Field(int fieldID, String fieldName) {
	this.fieldID = fieldID;
	this.fieldName = fieldName;
    }

    public int getFieldID() {
	return fieldID;
    }

    public void setFieldID(int fieldID) {
	this.fieldID = fieldID;
    }

    public String getFieldName() {
	return fieldName;
    }

    public void setFieldName(String fieldName) {
	this.fieldName = fieldName;
    }

    @Override
    public String toString() {
	return fieldName;
    }
    
    
    
}
