package com.kayyagari.ctrefs.server;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;

/**
 * 
 * @author Kiran Ayyagari (kayyagari@apache.org)
 */
public class FunctionNodeVisitor implements NodeVisitor {
	private List<String> functionNames;

	public FunctionNodeVisitor() {
		functionNames = new ArrayList<>();
	}

	@Override
	public boolean visit(AstNode node) {
		//System.out.println("visiting " + Token.typeToName(node.getType()));
		if(node instanceof FunctionNode) {
			String name = ((FunctionNode) node).getName();
			functionNames.add(name);
		}
		return true;
	}

	public List<String> getFunctionNames() {
		return functionNames;
	}
}
