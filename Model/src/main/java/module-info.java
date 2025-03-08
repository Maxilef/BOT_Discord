module Model {
    exports modele; // Remplacez 'modele' par le nom du package que vous souhaitez exposer

    requires jakarta.persistence; // Si vous utilisez JPA
    requires jakarta.transaction;  // Si vous utilisez des transactions JTA
    requires lombok;              // Pour l'annotation Lombok
    requires com.fasterxml.jackson.annotation;  // Pour Jackson
    requires org.hibernate.orm.core;  // Pour Hibernate
    opens modele to org.hibernate.orm.core; // Permet à Hibernate d'accéder aux entités
}
