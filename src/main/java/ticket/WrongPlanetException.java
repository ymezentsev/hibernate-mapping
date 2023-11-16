package ticket;

public class WrongPlanetException extends Exception{
    @Override
    public String getMessage() {
        return "You entered an incorrect planet";
    }
}
