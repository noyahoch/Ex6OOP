package block;

import main.LogicalException;
import main.ReadingCodeException;
import main.SyntaxException;
import variable.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MethodCall extends Block {
	String name;
	Block parent;
	ArrayList<String> callArgs;

	public MethodCall(String name, Block parent, ArrayList<String> callArgs) {
		this.name = name;
		this.parent = parent;
		this.callArgs = callArgs;

	}

	@Override
	public boolean checkValidity() {
		return true;
	}


	public String getName() {
		return name;
	}

	public ArrayList<String> getCallArgs() {
		return callArgs;
	}

	@Override
	public Block getParent() {
		return parent;
	}
}
