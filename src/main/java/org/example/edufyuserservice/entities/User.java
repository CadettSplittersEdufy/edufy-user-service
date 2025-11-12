package org.example.edufyuserservice.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length=25, nullable=false)
    private long id;

    @Column(length=175, nullable=false, unique=true)
    private String keycloakId;

    @Column(length=50, nullable=false)
    private String username;

    @Column(length=50, nullable = false)
    private String firstName;

    @Column(length=50,nullable = false)
    private String lastName;

    @Column(length=75, nullable = false)
    private String email;

    @Column(length=25, nullable = false)
    private boolean active;



    public User() {}

    public User(String username, String firstName, String lastName, String email, boolean active, String keycloakId) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.keycloakId = keycloakId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    @Override
    public String toString() {
        return "User [id=" + id +
                ", username=" + username +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", isActive=" + active +
                ", email=" + email +
                ", keycloakId=" + keycloakId +"]";
    }
}
