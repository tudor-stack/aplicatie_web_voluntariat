package com.voluntariat.platforma.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="Eveniments")
public class Event {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name="company_id",nullable=false)
    private Company company;

    public Event(){}

    public Event(String title, String description, LocalDate startDate, LocalDate endDate, Company company) {
        //this.id = id;     id-ul e generat automat
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.company = company;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public LocalDate getStartDate() {return startDate;}

    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalDate getEndDate() {return endDate;}

    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public Company getCompany() {return company;}

    public void setCompany(Company company) {this.company = company;}
}
