package com.hospital.sanrafael.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private String description;
    private int credits;
    private int recommendedSemester;
    private String professor;
    private String classroom;
    private List<Schedule> schedules;
    private List<String> prerequisites;

    public Subject() {
        this.schedules = new ArrayList<>();
        this.prerequisites = new ArrayList<>();
    }

    public Subject(String code, String name, String description, int credits,
                   int recommendedSemester, String professor, String classroom) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.credits = credits;
        this.recommendedSemester = recommendedSemester;
        this.professor = professor;
        this.classroom = classroom;
        this.schedules = new ArrayList<>();
        this.prerequisites = new ArrayList<>();
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public int getRecommendedSemester() { return recommendedSemester; }
    public void setRecommendedSemester(int recommendedSemester) { this.recommendedSemester = recommendedSemester; }
    public String getProfessor() { return professor; }
    public void setProfessor(String professor) { this.professor = professor; }
    public String getClassroom() { return classroom; }
    public void setClassroom(String classroom) { this.classroom = classroom; }

    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = schedules; }
    public void addSchedule(Schedule schedule) { this.schedules.add(schedule); }

    public List<String> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<String> prerequisites) { this.prerequisites = prerequisites; }

    public void addPrerequisite(String prerequisite) {
        if (!prerequisites.contains(prerequisite)) {
            prerequisites.add(prerequisite);
        }
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d credits)", code, name, credits);
    }
}
