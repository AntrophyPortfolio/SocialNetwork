package resources;

import javafx.beans.property.SimpleStringProperty;

public class Subject {
    private int idSubject;
    private SimpleStringProperty shortName;
    private SimpleStringProperty name;
    private SimpleStringProperty studyField;
    private SimpleStringProperty studyPlan;
    private SimpleStringProperty teacher;

    public Subject(int idSubject, String shortName, String name, String studyField, String studyPlan, String teacher) {
        this.idSubject = idSubject;
        this.shortName = new SimpleStringProperty(shortName);
        this.name = new SimpleStringProperty(name);
        this.studyField = new SimpleStringProperty(studyField);
        this.studyPlan = new SimpleStringProperty(studyPlan);
        this.teacher = new SimpleStringProperty(teacher);
    }

    public String getShortName() {
        return shortName.get();
    }

    public void setShortName(String shortName) {
        this.shortName = new SimpleStringProperty(shortName);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getStudyField() {
        return studyField.get();
    }

    public void setStudyField(String studyField) {
        this.studyField = new SimpleStringProperty(studyField);
    }

    public String getStudyPlan() {
        return studyPlan.get();
    }

    public void setStudyPlan(String studyPlan) {
        this.studyPlan = new SimpleStringProperty(studyPlan);
    }

    public String getTeacher() {
        return teacher.get();
    }

    public void setTeacher(String teacher) {
        this.teacher = new SimpleStringProperty(teacher);
    }

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }
    

}
