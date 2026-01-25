package com.voluntariat.platforma.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Ex: "Ecologie"
    //private String icon; // Ex: "🌱"

    // O categorie poate avea multe evenimente
    @OneToMany(mappedBy = "category")
    private List<Event> events;

    public Category() {}

    public Category(String name) {
        this.name = name;
        //this.icon = icon;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }



    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }
}