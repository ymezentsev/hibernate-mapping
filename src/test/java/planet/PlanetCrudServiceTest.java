package planet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import storage.DatabaseInitService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

class PlanetCrudServiceTest {

    private static PlanetCrudService planetCrudService;
    private static SessionFactory sessionFactory;

    @BeforeAll
    static void initDb() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Planet.class)
                .buildSessionFactory();

        new DatabaseInitService().initDb();

        planetCrudService = new PlanetCrudService(sessionFactory);
    }

    @AfterAll
    static void closeSessionFactory() {
        sessionFactory.close();
    }

    @BeforeEach
    void cleanPlanetTable(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createMutationQuery("delete from Planet").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Test
    void testGetById() {
        String expectedName = "getPlanetByIdTest";
        Planet newPlanet = new Planet();
        newPlanet.setId("GET");
        newPlanet.setName(expectedName);

        planetCrudService.save(newPlanet);
        Assertions.assertEquals(expectedName, planetCrudService.getById("GET").getName());
    }

    @Test
    void testGetAll() {
        List<Planet> expectedPlanets = new ArrayList<>();

        Planet newPlanet1 = new Planet();
        newPlanet1.setId("PL1");
        newPlanet1.setName("newPlanet1");
        planetCrudService.save(newPlanet1);

        Planet newPlanet2 = new Planet();
        newPlanet2.setId("PL2");
        newPlanet2.setName("newPlanet2");
        planetCrudService.save(newPlanet2);

        expectedPlanets.add(newPlanet1);
        expectedPlanets.add(newPlanet2);

        List<Planet> actualPlanets = planetCrudService.getAll();
        Assertions.assertEquals(expectedPlanets, actualPlanets);
    }

    @Test
    void testSave() {
        String expectedName = "addPlanet";
        Planet newPlanet = new Planet();
        newPlanet.setId("NEW");
        newPlanet.setName(expectedName);
        planetCrudService.save(newPlanet);

        Assertions.assertEquals(expectedName, planetCrudService.getById("NEW").getName());
    }

    @Test
    void testThatSaveHandleWrongId() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Planet newPlanet = new Planet();
        newPlanet.setId("wrong");
        newPlanet.setName("planet");
        planetCrudService.save(newPlanet);

        Assertions.assertEquals("You entered an incorrect planet id", output.toString().trim());
        System.setOut(standardOut);
    }

    @Test
    void testUpdate() {
        Planet newPlanet = new Planet();
        newPlanet.setId("NEW");
        newPlanet.setName("newPlanet");
        planetCrudService.save(newPlanet);

        String newName = "updatePlanet";
        planetCrudService.update("NEW", newName);
        Assertions.assertEquals(newName, planetCrudService.getById("NEW").getName());
    }

    @Test
    void testDeleteById() {
        Planet newPlanet = new Planet();
        newPlanet.setId("DEL");
        newPlanet.setName("deletedPlanet");
        planetCrudService.save(newPlanet);

        planetCrudService.deleteById("DEL");
        Assertions.assertNull(planetCrudService.getById("DEL"));
    }
}