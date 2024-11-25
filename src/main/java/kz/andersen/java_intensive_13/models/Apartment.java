package kz.andersen.java_intensive_13.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "apartment", schema = "public")
public class Apartment extends AuditableEntity<Integer>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "price")
    private double price;

    @JsonProperty("isReserved")
    @Column(name = "\"isReserved\"")
    private boolean isReserved;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(referencedColumnName = "id")
    private User user;

    public Apartment(double price){
        this.price = price;
        this.isReserved = false;
    }
}
