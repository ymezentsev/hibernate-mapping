package storage;

import client.Client;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import planet.Planet;
import ticket.Ticket;
import ticket.TicketCrudService;

public class HibernateUtil {
    private static final HibernateUtil INSTANCE;
    @Getter
    private SessionFactory sessionFactory;

    static {
        INSTANCE = new HibernateUtil();
    }
    private HibernateUtil() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Planet.class)
                .addAnnotatedClass(Ticket.class)
                .buildSessionFactory();
    }

    public static HibernateUtil getInstance(){
        return INSTANCE;
    }
}