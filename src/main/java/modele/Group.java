package modele;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Entity
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter @Setter
    private String nom;

    @ManyToMany
    @JoinTable(name = "group_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Getter @Setter
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL)
    @Getter @Setter
    private Set<Channel> canaux = new HashSet<>();

    // Constructeurs, getters, setters, equals, hashCode, toString

}
