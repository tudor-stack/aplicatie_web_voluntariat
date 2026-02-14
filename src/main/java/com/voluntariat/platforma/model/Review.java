package com.voluntariat.platforma.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // 1 - 5 stele

    @Column(length = 1000)
    private String comment;

    private LocalDateTime date;

    // Cine scrie recenzia?
    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    // Evenimentul asociat (contextul)
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    // Tipul recenziei: "FROM_VOLUNTEER" sau "FROM_COMPANY"
    private String type;

    public Review() {}

    public Review(int rating, String comment, User reviewer, Event event, String type) {
        this.rating = rating;
        this.comment = comment;
        this.reviewer = reviewer;
        this.event = event;
        this.type = type;
        this.date = LocalDateTime.now();
    }

    // Getters & Setters standard
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }


}