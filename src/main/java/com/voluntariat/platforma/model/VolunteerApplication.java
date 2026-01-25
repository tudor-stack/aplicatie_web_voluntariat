package com.voluntariat.platforma.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="VolunteerApplication", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user id","event_id"})
})

public class VolunteerApplication {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User volunteer;


    @ManyToOne
    @JoinColumn(name="event_id", nullable=false)
    private Event event;

    private LocalDateTime applicationDate;
    private String status;  //Pending | Accepted | rejected

    public VolunteerApplication() {
    }

    public VolunteerApplication(User volunteer, Event event) {
        this.volunteer=volunteer;
        this.event=event;
        this.applicationDate=LocalDateTime.now();
        this.status="PENDING";
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public User getVolunteer() {return volunteer;}

    public void setVolunteer(User volunteer) {this.volunteer = volunteer;}

    public Event getEvent() {return event;}

    public void setEvent(Event event) {this.event = event;}

    public LocalDateTime getApplicationDate() {return applicationDate;}

    public void setApplicationDate(LocalDateTime applicationDate) {this.applicationDate = applicationDate;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}
    // Adaugă asta în VolunteerApplication.java, jos de tot
    @Override
    public String toString() {
        return "VolunteerApplication{" +
                "id=" + id +
                ", status='" + status + '\'' +
                // IMPORTANT: Putem pune ID-urile, dar nu obiectele întregi
                ", eventId=" + (event != null ? event.getId() : "null") +
                ", volunteerId=" + (volunteer != null ? volunteer.getId() : "null") +
                '}';
    }
}
