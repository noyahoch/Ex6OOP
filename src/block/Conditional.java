package block;

/**
 * a class representing a while loop or an if statement in a Sjavac file
 */
public class Conditional extends Block{

    private String condition;

    Conditional(String condition){
        this.condition = condition;
        this.isMethod = false;
    }

    boolean checkValidity() {
        try { //TODO not correct
            boolean con = Boolean.parseBoolean(condition);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



}
