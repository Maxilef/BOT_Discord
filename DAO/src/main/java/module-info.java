module DAO {
    exports DAO;

    requires Model; // Pour utiliser les classes comme APPUser
    requires jakarta.persistence; // Pour EntityManager, etc.
    requires jakarta.transaction; // Pour @Transactional
    requires lombok; // Pour annotations comme @Slf4j
    requires org.slf4j; // Pour la gestion des logs
}
