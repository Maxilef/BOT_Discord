package modele;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Role implements Identifiable<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @ManyToMany(mappedBy = "roles")
    private Set<APPUser> users = new HashSet<>();

    // Constructeurs, getters, setters, equals, hashCode, toString
}

