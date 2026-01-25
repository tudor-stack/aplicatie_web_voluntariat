package com.voluntariat.platforma.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.ArrayList;
import com.voluntariat.platforma.model.VolunteerApplication;

@Entity
@Table(name="Eveniments")
public class Event {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Column(length = 1000)
    private String description;
    private String duration;
    private LocalDate startDate;
    private LocalDate endDate;

    // Importă: import jakarta.persistence.FetchType;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<VolunteerApplication> applications = new ArrayList<>();


    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // 👇 Getter și Setter pentru Category 👇
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }


    public List<VolunteerApplication> getApplications() {return applications;}

    public void setApplications(List<VolunteerApplication> applications) {this.applications = applications;}

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

    public List<Review> getReviews() { return reviews; }

    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public String getDuration() { return this.duration; }

    public void setDuration(String duration) { this.duration = duration; }

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
    // Adaugă asta în Event.java, jos de tot
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                // IMPORTANT: NU includem 'applications' aici!
                '}';
    }
}
