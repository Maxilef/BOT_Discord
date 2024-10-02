package modele;

import jakarta.persistence.*;

@Entity
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String type; // Par exemple : "textuel" ou "vocal"

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group groupe;

    // Constructeurs, getters, setters, equals, hashCode, toString
}
