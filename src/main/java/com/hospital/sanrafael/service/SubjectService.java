package com.hospital.sanrafael.service;

import com.hospital.sanrafael.dao.PostgreSubjectDAO;
import com.hospital.sanrafael.dao.SubjectDAO;
import com.hospital.sanrafael.database.DatabaseConnection;
import com.hospital.sanrafael.model.Subject;

import java.util.List;

public class SubjectService {
    private final SubjectDAO fileDAO;
    private final PostgreSubjectDAO dbDAO;
    private final boolean useDatabase;

    public SubjectService() {
        this.fileDAO = new SubjectDAO();
        this.dbDAO = new PostgreSubjectDAO();
        this.useDatabase = DatabaseConnection.testConnection();
    }

    public List<Subject> getAllSubjects() {
        return useDatabase ? dbDAO.getAll() : fileDAO.getAll();
    }

    public Subject getSubjectByCode(String code) {
        return useDatabase ? dbDAO.getByCode(code) : fileDAO.getByCode(code);
    }

    public Subject registerSubject(Subject subject) {
        subject.setCode(generateNextSubjectCode());
        if (useDatabase) {
            dbDAO.save(subject);
        } else {
            fileDAO.save(subject);
        }
        return subject;
    }

    private String generateNextSubjectCode() {
        List<Subject> all = getAllSubjects();
        int max = 0;
        for (Subject s : all) {
            String code = s.getCode();
            if (code != null && code.startsWith("SUBJ")) {
                try {
                    int n = Integer.parseInt(code.substring(4));
                    if (n > max) max = n;
                } catch (NumberFormatException e) {}
            }
        }
        return "SUBJ" + String.format("%03d", max + 1);
    }

    public Subject updateSubject(Subject subject) {
        if (useDatabase) {
            dbDAO.update(subject);
        } else {
            fileDAO.update(subject);
        }
        return subject;
    }

    public void deleteSubject(String code) {
        if (useDatabase) {
            dbDAO.delete(code);
        } else {
            fileDAO.delete(code);
        }
    }

    public List<Subject> searchBySemester(int semester) {
        return useDatabase ? dbDAO.getBySemester(semester) : fileDAO.getBySemester(semester);
    }

    public List<Subject> searchByProfessor(String professor) {
        return useDatabase ? dbDAO.getByProfessor(professor) : fileDAO.getByProfessor(professor);
    }
}
