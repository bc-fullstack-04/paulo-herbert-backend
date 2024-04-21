package br.com.sysmap.bootcamp.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "albums")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "id_spotify", nullable = false, length = 100,unique = true)
    private String idSpotify;

    @Column(name = "artist_name", nullable = false, length = 150)
    private String artistName;

    @Column(name = "image_url", nullable = false, length = 150)
    private String imageUrl;

    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "id_user")
    @JsonIgnore
    private Users users;

}