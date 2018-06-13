package block;

import variable.Variable;

import java.util.ArrayList;

/**
 * a class representing a while loop or an if statement in a Sjavac file
 */
public class Conditional extends Block{

    private String condition;
    private final String BOOLEAN_OP = " *&& *| *\\|\\| *";

    Conditional(String condition, Block parent){
        this.condition = condition.trim();
        this.isMethod = false;
        this.parent = parent;
        this.variables = new ArrayList<Variable>();
    }

    /**
     * Checks validity of the condition of the Conditional.
     * @return true iff it is a valid boolean condition.
     */
    boolean checkValidity() {
        char lastChar = condition.charAt(condition.length()-1);
        if (lastChar=='&' ||lastChar=='|' || condition == "")
            return false;
        String parts[] = condition.split(BOOLEAN_OP);
        for (String part : parts){
            String varValue = this.valueOfVar(part);
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
