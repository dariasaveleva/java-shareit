package ru.practicum.shareit.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String email;
}
