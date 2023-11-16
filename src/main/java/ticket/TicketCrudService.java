package ticket;

import client.Client;
import client.ClientCrudService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import planet.Planet;
import planet.PlanetCrudService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class TicketCrudService {
    private final SessionFactory sessionFactory;

    public TicketCrudService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Ticket getById(long id) {
        Session session = sessionFactory.openSession();
        Ticket ticket = session.get(Ticket.class, id);
        session.close();
        return ticket;
    }

    public List<Ticket> getAll() {
        Session session = sessionFactory.openSession();
        List<Ticket> tickets = session.createQuery("from Ticket", Ticket.class).list();
        session.close();
        return tickets;
    }

    public void save(Ticket ticket) {
        try {
            Client client = ticket.getClient();
            Planet fromPlanet = ticket.getFromPlanet();
            Planet toPlanet = ticket.getToPlanet();
            validateClient(client);
            validatePlanet(fromPlanet);
            validatePlanet(toPlanet);
            validateDestinationPlanet(fromPlanet, toPlanet);
        } catch (WrongClientException | WrongPlanetException | WrongDestinationPlanetException e) {
            System.out.println(e.getMessage());
            return;
        }

        ticket.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(ticket);
        transaction.commit();
        session.close();
    }

    public void update(long id, Ticket ticket) {
        Client client;
        Planet fromPlanet;
        Planet toPlanet;
        try {
            client = ticket.getClient();
            fromPlanet = ticket.getFromPlanet();
            toPlanet = ticket.getToPlanet();
            validateClient(client);
            validatePlanet(fromPlanet);
            validatePlanet(toPlanet);
            validateDestinationPlanet(fromPlanet, toPlanet);
        } catch (WrongClientException | WrongPlanetException | WrongDestinationPlanetException e) {
            System.out.println(e.getMessage());
            return;
        }

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Ticket ticketByID = session.get(Ticket.class, id);
        if (ticketByID != null) {
            ticketByID.setClient(client);
            ticketByID.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
            ticketByID.setFromPlanet(fromPlanet);
            ticketByID.setToPlanet(toPlanet);
            session.persist(ticketByID);
        }
        transaction.commit();
        session.close();
    }

    public void deleteById(long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Ticket ticketByID = session.get(Ticket.class, id);
        if (ticketByID != null) {
            session.remove(ticketByID);
        }

        transaction.commit();
        session.close();
    }

    private void validateClient(Client client) throws WrongClientException {
        if (client == null || new ClientCrudService(sessionFactory).getByID(client.getId()) == null) {
            throw new WrongClientException();
        }
    }

    private void validatePlanet(Planet planet) throws WrongPlanetException {
        if (planet == null || new PlanetCrudService(sessionFactory).getById(planet.getId()) == null) {
            throw new WrongPlanetException();
        }
    }

    private void validateDestinationPlanet(Planet from, Planet to) throws WrongDestinationPlanetException {
        if (from.equals(to)) {
            throw new WrongDestinationPlanetException();
        }
    }
}
