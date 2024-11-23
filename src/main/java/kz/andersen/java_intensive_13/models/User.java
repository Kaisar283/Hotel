package kz.andersen.java_intensive_13.models;

import jakarta.persistence.*;
import kz.andersen.java_intensive_13.enums.UserRole;
import kz.andersen.java_intensive_13.hibernate.convertor.ZonedDateTimeConvertor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user", schema = "public")
public class User extends AuditableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    public User(String fistName) {
        this.firstName = fistName;
    }

    public User(long id, String fistName) {
        this.id = id;
        this.firstName = fistName;
        this.userRole = UserRole.USER;
    }
}
