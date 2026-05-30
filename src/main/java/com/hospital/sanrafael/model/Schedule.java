package com.hospital.sanrafael.model;

import java.io.Serializable;

public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private String day;
    private String startTime;
    private String endTime;
    private String activity;
    private String responsible;
    private String classroom;

    public Schedule() {
    }

    public Schedule(String day, String startTime, String endTime, String activity,
                    String responsible, String classroom) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activity = activity;
        this.responsible = responsible;
        this.classroom = classroom;
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }
    public String getResponsible() { return responsible; }
    public void setResponsible(String responsible) { this.responsible = responsible; }
    public String getClassroom() { return classroom; }
    public void setClassroom(String classroom) { this.classroom = classroom; }

    @Override
    public String toString() {
        return String.format("%s %s-%s: %s (%s) - %s",
                day, startTime, endTime, activity, responsible, classroom);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Schedule schedule = (Schedule) obj;
        return day != null ? day.equals(schedule.day) : schedule.day == null &&
                startTime != null ? startTime.equals(schedule.startTime) : schedule.startTime == null;
    }

    @Override
    public int hashCode() {
        int result = day != null ? day.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        return result;
    }
}
