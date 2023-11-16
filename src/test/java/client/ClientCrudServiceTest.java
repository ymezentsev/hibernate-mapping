package client;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import planet.Planet;
import storage.DatabaseInitService;
import ticket.Ticket;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

class ClientCrudServiceTest {
    private static ClientCrudService clientCrudService;
    private static SessionFactory sessionFactory;

    @BeforeAll
    static void initDb() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Planet.class)
                .addAnnotatedClass(Ticket.class)
                .buildSessionFactory();

        new DatabaseInitService().initDb();

        clientCrudService = new ClientCrudService(sessionFactory);
    }

    @AfterAll
    static void closeSessionFactory() {
        sessionFactory.close();
    }

    @BeforeEach
    void cleanClientTable(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createMutationQuery("delete from Client").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Test
    void testGetById() {
        String expectedName = "getClientByIdTest";
        Client newClient = new Client();
        newClient.setName(expectedName);

        clientCrudService.save(newClient);
        Assertions.assertEquals(expectedName, clientCrudService.getByID(newClient.getId()).getName());
    }

    @Test
    void testGetAll() {
        List<Client> expectedClients = new ArrayList<>();

        Client newClient1 = new Client();
        newClient1.setName("newClient1");
        clientCrudService.save(newClient1);

        Client newClient2 = new Client();
        newClient2.setName("newClient2");
        clientCrudService.save(newClient2);

        expectedClients.add(newClient1);
        expectedClients.add(newClient2);

        List<Client> actualClients = clientCrudService.getAll();
        Assertions.assertEquals(expectedClients, actualClients);
    }

    @Test
    void testSave() {
        Client newClient = new Client();
        newClient.setName("addClientTest");

        Assertions.assertEquals(0, newClient.getId());
        clientCrudService.save(newClient);
        Assertions.assertNotEquals(0, newClient.getId());
    }

    @Test
    void testThatSaveHandleTooShotNames() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Client newClient = new Client();
        newClient.setName("12");
        clientCrudService.save(newClient);

        Assertions.assertEquals("You entered an incorrect client name", output.toString().trim());
        System.setOut(standardOut);
    }

    @Test
    void testUpdate() {
        Client newClient = new Client();
        newClient.setName("updateClientTest");

        clientCrudService.save(newClient);

        String newName = "newUpdateName";
        clientCrudService.update(newClient.getId(), newName);
        Assertions.assertEquals(newName, clientCrudService.getByID(newClient.getId()).getName());
    }

    @Test
    void testThatUpdateHandleTooShotNames() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        clientCrudService.update(1, "12");

        Assertions.assertEquals("You entered an incorrect client name", output.toString().trim());
        System.setOut(standardOut);
    }

    @Test
    void testDeleteById() {
        Client newClient = new Client();
        newClient.setName("deleteClientTest");

        clientCrudService.save(newClient);

        clientCrudService.deleteById(newClient.getId());
        Assertions.assertNull(clientCrudService.getByID(newClient.getId()));
    }
}