import client.Client;
import client.ClientCrudService;
import org.hibernate.SessionFactory;
import planet.Planet;
import planet.PlanetCrudService;
import storage.DatabaseInitService;
import storage.HibernateUtil;
import ticket.Ticket;
import ticket.TicketCrudService;

public class App {
    public static void main(String[] args) {
        new DatabaseInitService().initDb();
        SessionFactory sessionFactory = HibernateUtil.getInstance().getSessionFactory();

        ClientCrudService clientCrudService = new ClientCrudService(sessionFactory);
        Client wrongClient = new Client();
        wrongClient.setName("Ig");
        clientCrudService.save(wrongClient);

        Client newClient = new Client();
        newClient.setName("Igor Sidorov");
        clientCrudService.save(newClient);

        clientCrudService.update(2L, "Fedor");
        clientCrudService.deleteById(6);
        System.out.println(clientCrudService.getByID(7L));
        System.out.println(clientCrudService.getAll());


        PlanetCrudService planetCrudService = new PlanetCrudService(sessionFactory);
        Planet wrongPlanet = new Planet();
        wrongPlanet.setId("mar1");
        wrongPlanet.setName("Mars 1");
        planetCrudService.save(wrongPlanet);

        Planet newPlanet = new Planet();
        newPlanet.setId("MAR1");
        newPlanet.setName("Mars 1");
        planetCrudService.save(newPlanet);

        planetCrudService.update("MAR", "Update Mars");
        planetCrudService.deleteById("VEN");
        System.out.println(planetCrudService.getById("EAR"));
        System.out.println(planetCrudService.getAll());


        TicketCrudService ticketCrudService = new TicketCrudService(sessionFactory);
        Ticket newTicket = new Ticket();
        newTicket.setClient(clientCrudService.getByID(1));
        newTicket.setFromPlanet(planetCrudService.getById("MAR"));
        newTicket.setToPlanet(planetCrudService.getById("EAR"));
        ticketCrudService.save(newTicket);

        Ticket updateTicket = new Ticket();
        updateTicket.setClient(clientCrudService.getByID(10));
        updateTicket.setFromPlanet(planetCrudService.getById("JUP"));
        updateTicket.setToPlanet(planetCrudService.getById("SAT"));
        ticketCrudService.update(2, updateTicket);

        ticketCrudService.deleteById(8L);
        System.out.println(ticketCrudService.getById(7L));
        System.out.println(ticketCrudService.getAll());

        sessionFactory.close();
    }
}


