package ticket;

public class WrongDestinationPlanetException extends Exception{
    @Override
    public String getMessage() {
        return "Planet of departure and destination are the same";
    }
}
