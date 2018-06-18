package block;

import java.util.ArrayList;

public class MethodCall extends Block {
	private String name;

	private Block parent;

	private ArrayList<String> callArgs;

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
