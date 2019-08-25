package pl.codementors.finalproject.model;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name= "products")
public class Product {

    @Id
    @JsonView(ProductView.class)
    private String id;

    @NotNull
    @JsonView(ProductView.class)
    private String name;

    @JsonView(ProductView.class)
    private String description;

    @JsonView(ProductView.class)
    private boolean available;

    @NotNull
    @JsonView(ProductView.class)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private LocalUser user;

    @UpdateTimestamp
    @JsonView(ProductView.class)
    private Timestamp timestamp;

    public Timestamp getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timestamp = timeStamp;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public BigDecimal getPrice() {return price; }
    public void setPrice(BigDecimal price) {this.price = price; }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public LocalUser getUser() {
        return user;
    }

    public void setUser(LocalUser user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return isAvailable() == product.isAvailable() &&
                Objects.equals(getId(), product.getId()) &&
                Objects.equals(getName(), product.getName()) &&
                Objects.equals(getDescription(), product.getDescription()) &&
                Objects.equals(getPrice(), product.getPrice()) &&
                Objects.equals(getUser(), product.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), isAvailable(), getPrice(), getUser());
    }

    @Override
    public String toString() {
        if (user!=null) {
            return "Product{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", description='" + description + '\'' + ", available=" + available + ", price=" + price + ", user=" + user.getName() + ", timestamp="
                   + timestamp + '}';
        }else{
            return "Product{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", description='" + description + '\'' + ", available=" + available + ", price=" + price + ","  + ", timestamp="
                   + timestamp + '}';
        }
    }
}
