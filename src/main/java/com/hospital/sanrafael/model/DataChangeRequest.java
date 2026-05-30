package com.hospital.sanrafael.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataChangeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING,
        APPROVED,
        DENIED
    }

    private String id;
    private String requesterId;
    private String requesterName;
    private String requesterRole;
    private String entityId;
    private String entityType;
    private Map<String, String> originalData;
    private Map<String, String> proposedData;
    private Status status;
    private String adminMessage;
    private LocalDate requestDate;
    private LocalDate responseDate;

    public DataChangeRequest() {
        this.originalData = new LinkedHashMap<>();
        this.proposedData = new LinkedHashMap<>();
        this.status = Status.PENDING;
        this.requestDate = LocalDate.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequesterId() { return requesterId; }
    public void setRequesterId(String requesterId) { this.requesterId = requesterId; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getRequesterRole() { return requesterRole; }
    public void setRequesterRole(String requesterRole) { this.requesterRole = requesterRole; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Map<String, String> getOriginalData() { return originalData; }
    public void setOriginalData(Map<String, String> originalData) { this.originalData = originalData; }

    public Map<String, String> getProposedData() { return proposedData; }
    public void setProposedData(Map<String, String> proposedData) { this.proposedData = proposedData; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAdminMessage() { return adminMessage; }
    public void setAdminMessage(String adminMessage) { this.adminMessage = adminMessage; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public LocalDate getResponseDate() { return responseDate; }
    public void setResponseDate(LocalDate responseDate) { this.responseDate = responseDate; }

    public boolean isPending() { return status == Status.PENDING; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s): %s", status, requesterName, requesterRole, entityId, requestDate);
    }
}
