package ticket;

import client.Client;
import client.ClientCrudService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import planet.Planet;
import planet.PlanetCrudService;
import storage.DatabaseInitService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;


class TicketCrudServiceTest {
    private static SessionFactory sessionFactory;
    private static TicketCrudService ticketCrudService;
    private static PlanetCrudService planetCrudService;
    private static ClientCrudService clientCrudService;

    @BeforeAll
    static void initDb() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Ticket.class)
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Planet.class)
                .buildSessionFactory();

        new DatabaseInitService().initDb();

        planetCrudService = new PlanetCrudService(sessionFactory);
        clientCrudService = new ClientCrudService(sessionFactory);
        ticketCrudService = new TicketCrudService(sessionFactory);
        updateClientAndPlanetTable();
    }

    private static void updateClientAndPlanetTable() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createMutationQuery("delete from Client").executeUpdate();
        session.createMutationQuery("delete from Planet").executeUpdate();
        session.getTransaction().commit();
        session.close();

        Client newClient = new Client();
        newClient.setName("Igor Sidorov");
        clientCrudService.save(newClient);

        Planet from = new Planet();
        from.setId("FROM");
        from.setName("FromPlanet");
        planetCrudService.save(from);

        Planet to = new Planet();
        to.setId("TO");
        to.setName("ToPlanet");
        planetCrudService.save(to);
    }

    @AfterAll
    static void closeSessionFactory() {
        sessionFactory.close();
    }

    @BeforeEach
    void cleanTicketTable() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createMutationQuery("delete from Ticket").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Test
    void testGetByID() {
        Ticket newTicket = getTicket();
        ticketCrudService.save(newTicket);

        Assertions.assertEquals(clientCrudService.getAll().get(0),
                ticketCrudService.getById(newTicket.getId()).getClient());
    }

    @Test
    void testGetAll() {
        ticketCrudService.save(getTicket());
        ticketCrudService.save(getTicket());

        List<Ticket> actualTickets = ticketCrudService.getAll();
        Assertions.assertEquals(2, actualTickets.size());
    }

    @Test
    void testSave() {
        Ticket newTicket = getTicket();

        Assertions.assertEquals(0, newTicket.getId());
        ticketCrudService.save(newTicket);
        Assertions.assertNotEquals(0, newTicket.getId());
    }

    @Test
    void testThatSaveHandleWrongClient() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Client wrongClient = new Client();
        wrongClient.setName("Not existing in DB client");

        Ticket newTicket = getTicket();
        newTicket.setClient(wrongClient);
        ticketCrudService.save(newTicket);

        Assertions.assertEquals("You entered an incorrect client", output.toString().trim());
        System.setOut(standardOut);
    }

    @Test
    void testThatSaveHandleWrongPlanet() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Planet wrongPlanet = new Planet();
        wrongPlanet.setId("WRONG");
        wrongPlanet.setName("Not existing in DB planet");

        Ticket newTicket = getTicket();
        newTicket.setFromPlanet(wrongPlanet);
        ticketCrudService.save(newTicket);

        Assertions.assertEquals("You entered an incorrect planet", output.toString().trim());
        System.setOut(standardOut);
    }

    @Test
    void testThatSaveHandleWrongDestinationPlanet() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Planet wrongToPlanet = planetCrudService.getById("FROM");

        Ticket newTicket = getTicket();
        newTicket.setToPlanet(wrongToPlanet);
        ticketCrudService.save(newTicket);

        Assertions.assertEquals("Planet of departure and destination are the same", output.toString().trim());
        System.setOut(standardOut);
    }

    @Test
    void testUpdate() {
        Ticket newTicket = getTicket();
        ticketCrudService.save(newTicket);

        Client expectedClient = new Client();
        expectedClient.setName("newClient");
        clientCrudService.save(expectedClient);

        Ticket updateTicket = getTicket();
        updateTicket.setClient(expectedClient);

        ticketCrudService.update(newTicket.getId(), updateTicket);
        Assertions.assertEquals(expectedClient, ticketCrudService.getById(newTicket.getId()).getClient());
    }

    @Test
    void testThatUpdateHandleWrongClient() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Ticket newTicket = getTicket();
        ticketCrudService.save(newTicket);

        Client wrongClient = new Client();
        wrongClient.setName("Not existing in DB client");

        Ticket updateTicket = getTicket();
        updateTicket.setClient(wrongClient);
        ticketCrudService.update(newTicket.getId(), updateTicket);

        Assertions.assertEquals("You entered an incorrect client", output.toString().trim());
        System.setOut(standardOut);
    }

    @Test
    void testThatUpdateHandleWrongPlanet() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Ticket newTicket = getTicket();
        ticketCrudService.save(newTicket);

        Planet wrongPlanet = new Planet();
        wrongPlanet.setId("WRONG");
        wrongPlanet.setName("Not existing in DB planet");

        Ticket updateTicket = getTicket();
        updateTicket.setFromPlanet(wrongPlanet);
        ticketCrudService.update(newTicket.getId(), updateTicket);

        Assertions.assertEquals("You entered an incorrect planet", output.toString().trim());
        System.setOut(standardOut);
    }

    @Test
    void testThatUpdateHandleWrongDestinationPlanet() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Ticket newTicket = getTicket();
        ticketCrudService.save(newTicket);

        Planet wrongToPlanet = planetCrudService.getById("FROM");

        Ticket updateTicket = getTicket();
        updateTicket.setToPlanet(wrongToPlanet);
        ticketCrudService.update(newTicket.getId(), updateTicket);

        Assertions.assertEquals("Planet of departure and destination are the same", output.toString().trim());
        System.setOut(standardOut);
    }

    @Test
    void testDeleteById() {
        Ticket newTicket = getTicket();
        ticketCrudService.save(newTicket);

        ticketCrudService.deleteById(newTicket.getId());
        Assertions.assertNull(ticketCrudService.getById(newTicket.getId()));
    }

    private Ticket getTicket() {
        Ticket newTicket = new Ticket();
        newTicket.setClient(clientCrudService.getAll().get(0));
        newTicket.setFromPlanet(planetCrudService.getById("FROM"));
        newTicket.setToPlanet(planetCrudService.getById("TO"));
        return newTicket;
    }
}