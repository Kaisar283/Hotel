package kz.andersen.java_intensive_13.models;

import jakarta.persistence.*;
import kz.andersen.java_intensive_13.enums.UserRole;
import kz.andersen.java_intensive_13.hibernate.convertor.ZonedDateTimeConvertor;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "userRole")
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
