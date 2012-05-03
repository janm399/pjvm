package net.cakesolutions.pjvm.session2.domain;

import net.cakesolutions.pjvm.session2.domain.validation.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Size;

/**
 * @author janm
 */
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private int version;

    @NotBlank
    @Size(max = 16)
    private String username;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{\"id\":\"").append(id).append('"');
        sb.append(", \"version\":\"").append(version).append('"');
        sb.append(", \"username\":\"").append(username).append('"');
        sb.append(", \"firstName\":\"").append(firstName).append('"');
        sb.append(", \"lastName\":\"").append(lastName).append('"');
        sb.append('}');
        return sb.toString();
    }

    // -- Getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
