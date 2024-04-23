package br.com.sysmap.bootcamp.domain.entities;


import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column( name = "name" ,nullable = false)
    private String name;

    @Column( name = "email",nullable = false)
    private String email;

    @Column( name = "password", nullable = false)
    private String password;

}
