/**
 * @author Group 9
 */
package org.example.model;

import java.time.LocalDateTime;

/**
 * Represents a lending record in the university equipment lending system.
 * Each record tracks the borrowing and returning of equipment by users.
 * For students, each lending record must be associated with a course.
 */
public class LendingRecord {
    private int id;
    private User borrower;
    private Equipment equipment;
    private Course course;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status; // "Pending", "Approved", "Borrowed", "Returned", "Overdue", "Rejected"
    private String purpose;
    private String condition;
    private String notes;
    private User approver;

    /**
     * Default constructor
     */
    public LendingRecord() {
    }

    /**
     * Parameterized constructor for student borrowers
     *
     * @param id         Unique identifier for the lending record
     * @param borrower   The user borrowing the equipment
     * @param equipment  The equipment being borrowed
     * @param course     The course for which the equipment is being borrowed (required for students)
     * @param borrowDate The date and time when the equipment was borrowed
     * @param dueDate    The date and time when the equipment is due to be returned
     * @param status     The status of the lending record
     * @param purpose    The purpose for borrowing the equipment
     * @param condition  The condition of the equipment at the time of borrowing
     * @param notes      Additional notes about the lending
     * @param approver   The academic staff who approved the lending (for student borrowers)
     */
    public LendingRecord(int id, User borrower, Equipment equipment, Course course,
                        LocalDateTime borrowDate, LocalDateTime dueDate, String status,
                        String purpose, String condition, String notes, User approver) {
        this.id = id;
        this.borrower = borrower;
        this.equipment = equipment;
        this.course = course;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
        this.purpose = purpose;
        this.condition = condition;
        this.notes = notes;
        this.approver = approver;
    }

    /**
     * Parameterized constructor for staff borrowers (no course required)
     *
     * @param id         Unique identifier for the lending record
     * @param borrower   The user borrowing the equipment
     * @param equipment  The equipment being borrowed
     * @param borrowDate The date and time when the equipment was borrowed
     * @param dueDate    The date and time when the equipment is due to be returned
     * @param status     The status of the lending record
     * @param purpose    The purpose for borrowing the equipment
     * @param condition  The condition of the equipment at the time of borrowing
     * @param notes      Additional notes about the lending
     */
    public LendingRecord(int id, User borrower, Equipment equipment,
                        LocalDateTime borrowDate, LocalDateTime dueDate, String status,
                        String purpose, String condition, String notes) {
        this.id = id;
        this.borrower = borrower;
        this.equipment = equipment;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
        this.purpose = purpose;
        this.condition = condition;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }

    /**
     * Check if the lending is overdue
     *
     * @return true if the lending is overdue, false otherwise
     */
    public boolean isOverdue() {
        if (returnDate != null) {
            return false; // Already returned
        }
        return LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Return the equipment
     *
     * @param returnDate The date and time when the equipment was returned
     * @param condition  The condition of the equipment at the time of return
     * @param notes      Additional notes about the return
     */
    public void returnEquipment(LocalDateTime returnDate, String condition, String notes) {
        this.returnDate = returnDate;
        this.condition = condition;
        this.notes = notes;
        this.status = "Returned";
    }

    @Override
    public String toString() {
        return "LendingRecord{" +
                "id=" + id +
                ", borrower=" + (borrower != null ? borrower.getUsername() : "None") +
                ", equipment=" + (equipment != null ? equipment.getName() : "None") +
                ", course=" + (course != null ? course.getCourseCode() : "None") +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                ", purpose='" + purpose + '\'' +
                ", condition='" + condition + '\'' +
                ", approver=" + (approver != null ? approver.getUsername() : "None") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LendingRecord that = (LendingRecord) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}