package block;

/**
 * a class representing a while loop or an if statement in a Sjavac file
 */
public class Conditional extends Block{

    private String condition;
    private final String BOOLEAN_OP = " *&& *| *\\|\\| *";

    Conditional(String condition){
        this.condition = condition;
        this.isMethod = false;
    }

    /**
     * Checks validity of the condition of the Conditional.
     * @return true iff it is a valid boolean condition.
     */
    boolean checkValidity() {
        char lastChar = condition.charAt(condition.length()-1);
        if (lastChar=='&' ||lastChar=='|')
            return false;
        String parts[] = condition.split(BOOLEAN_OP);
        for (String part : parts){
            String varValue = Block.valueOfVar(part, this);
            if (varValue != null)
                part = varValue;
            try {
                Boolean.parseBoolean(part);
            } catch (Exception e) {
                return false;
            }
        }
    return true;
    }



}
