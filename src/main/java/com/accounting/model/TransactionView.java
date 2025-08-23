package com.accounting.model;

import java.time.LocalDate;

public class TransactionView {
    private int id;
    private String employeeName;
    private double amount;
    private LocalDate date;
    private String time;
    private String notes;
    
    public TransactionView() {}
    
    public TransactionView(int id, String employeeName, double amount, 
                          LocalDate date, String time, String notes) {
        this.id = id;
        this.employeeName = employeeName;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.notes = notes;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}