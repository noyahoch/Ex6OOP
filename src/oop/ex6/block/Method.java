package oop.ex6.block;

import oop.ex6.variable.*;
import java.util.ArrayList;
import java.util.regex.*;

/**
 * A class representing a method in a Sjava file.
 */
public class Method extends Block {

    public final static String VALID_METHOD_NAME = "([A-Za-z]+[\\w]*)";
    private final ArrayList<Variable> params;
    String name;

    public Method(String name, Block parent, ArrayList<Variable> params ){
        this.name = name;
        this.isMethod = true;
        this.parent = parent;
        this.variables = new ArrayList<>();
        this.params = params;
    }

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
     * @return
     */
    public boolean checkParamValidity(ArrayList<String> givenArgs) { //todo rethinking
        if (givenArgs.size() != params.size())
            return false; // Check if given args are of right length
        for (int i = 0; i < params.size(); i++){
            String paramValue;
            //If its a name of a relevant variable, assign its value.
            paramValue = this.valueOfVar(givenArgs.get(i));
            if (paramValue == null)//If its not a var name assign it.
                paramValue = givenArgs.get(i);
            params.get(i).setValue(paramValue);
        }
        boolean isValid = true;
        // Check if values given by the call are valid.
        for (Variable param : params){
            if(!param.checkValidity())
                isValid = false;
            param.setValue(null); //So value will not be changed.
        }
        return isValid;
    }

}
