package com.hospital.sanrafael.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class HospitalRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recordId;
    private String personId;
    private String personName;
    private String type;
    private LocalDate date;
    private LocalTime entryTime;
    private LocalTime exitTime;
    private String area;
    private String activity;
    private String observations;

    public HospitalRecord() {
    }

    public HospitalRecord(String recordId, String personId, String personName,
                          String type, LocalDate date, LocalTime entryTime,
                          LocalTime exitTime, String area, String activity,
                          String observations) {
        this.recordId = recordId;
        this.personId = personId;
        this.personName = personName;
        this.type = type;
        this.date = date;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.area = area;
        this.activity = activity;
        this.observations = observations;
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }
    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalTime entryTime) { this.entryTime = entryTime; }
    public LocalTime getExitTime() { return exitTime; }
    public void setExitTime(LocalTime exitTime) { this.exitTime = exitTime; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s) - %s %s",
                date, personName, type, activity, area != null ? "in " + area : "");
    }
}
