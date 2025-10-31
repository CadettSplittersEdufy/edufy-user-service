package org.example.edufyuserservice.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length=25, nullable=false)
    private long id;

    @Column(length=50, nullable=false)
    private String username;

    @Column(length=50, nullable = false)
    private String firstName;

    @Column(length=50,nullable = false)
    private String lastName;

    @Column(length=25, nullable = false)
    private boolean active;

    public User() {}

    public User(String username, String firstName, String lastName, boolean active) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", firstName=" + firstName + ", lastName=" + lastName + ", isActive=" + active + "]";
    }
}
