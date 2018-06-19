package block;

import variable.*;
import java.util.ArrayList;
import java.util.regex.*;

/**
 * A class representing a method in a Sjava file
 */
public class Method extends Block {

    public final static String VALID_METHOD_NAME = "([A-Za-z]+[\\w]*)";
    private final String DEFALUT_VALUE = "0"; // A default value, valid for each primitive type;
    private final ArrayList<Variable> params;
    private String name;

    /**
     * Constructs a new method in Sjava file
     * @param name the name of the method
     * @param parent the block in which the method was called
     * @param params methods parameters
     */
    public Method(String name, Block parent, ArrayList<Variable> params ){
        this.name = name;
        this.isMethod = true;
        this.parent = parent;
        this.variables = new ArrayList<>();
        this.params = params;
        for (Variable param : params) { // Every param is considered a local variable;
            param.setValue(DEFALUT_VALUE);
            variables.add(param);
        }
    }

    /**
     * @return The methods name.
     */
    public String getName(){
        return this.name;
    }

    /**
     * Checks if a method declaration is valid.
     * @return true iff the declaration is valid.
     */
    public boolean checkValidity() {
        Pattern p = Pattern.compile(VALID_METHOD_NAME);
        Matcher m = p.matcher(name);
        if (!m.matches())
            return false;
        for (Variable param : params)
            if (!param.checkValidity())
                return false;
        return true;
    }

    /**
     * Checks if given parameters in method call are in accordance with the method parameters.
     * @param givenArgs
     * @return true iff the parameters in the call are valid
     */
    public boolean checkParamValidity(ArrayList<String> givenArgs, Block scope) {
	    if (givenArgs.size() == 1 && givenArgs.get(0).equals("") && params.size()==0)
	    	return true; // Empty call
	    if (givenArgs.size() != params.size())
            return false; // Check if given args are of right length
        for (int i = 0; i < params.size(); i++){
        	String paramValue;
            //If its a name of a relevant variable, assign its value.
	        paramValue = scope.valueOfVar(givenArgs.get(i));
            if (paramValue == null)//If its not a var name assign it.
                paramValue = givenArgs.get(i);
            if(!params.get(i).setValue(paramValue)) //Try assigning value to params, check validity
                return false;
        }
        return true;
    }
}
