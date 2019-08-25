package pl.codementors.finalproject.model;

import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class LocalUser {

    @Id
    private String id;

    private String name;

    private String surname;

    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private boolean active;

    @OneToMany(mappedBy = "user")
    private List<Product> products;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalUser)) return false;
        LocalUser localUser = (LocalUser) o;
        return isActive() == localUser.isActive() &&
                Objects.equals(getId(), localUser.getId()) &&
                Objects.equals(getName(), localUser.getName()) &&
                Objects.equals(getSurname(), localUser.getSurname()) &&
                Objects.equals(getUsername(), localUser.getUsername()) &&
                Objects.equals(getPassword(), localUser.getPassword()) &&
                Objects.equals(getEmail(), localUser.getEmail()) &&
                getRole() == localUser.getRole() &&
                Objects.equals(products, localUser.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getSurname(), getUsername(), getPassword(), getEmail(), getRole(), isActive(), products);
    }
}
