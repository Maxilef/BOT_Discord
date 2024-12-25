package modele;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Channel implements Identifiable<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String type; // Par exemple : "textuel" ou "vocal"*

    @Override
    public Long getId() {
        return id;
    }



    // Constructeurs, getters, setters, equals, hashCode, toString
}
