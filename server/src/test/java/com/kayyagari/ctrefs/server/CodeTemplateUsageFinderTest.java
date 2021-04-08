package com.kayyagari.ctrefs.server;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

public class CodeTemplateUsageFinderTest {
	
	String script = "/**\n"
			+ "	Modify the description here. Modify the function name and parameters as needed. One function per\n"
			+ "	template is recommended; create a new code template for each new function.\n"
			+ "\n"
			+ "	@param {String} arg1 - arg1 description\n"
			+ "	@return {String} return description\n"
			+ "*/\n"
			+ "function new_function1(arg1) {\n"
			+ "	// TODO: Enter code here\n"
			+ "}";
	@Test
	public void testParseJavaScript() {
		Parser parser = new Parser();
		AstRoot root = parser.parse(script, null, 1);
		FunctionNodeVisitor fnv = new FunctionNodeVisitor();
		root.visitAll(fnv);
		assertEquals(1, fnv.getFunctionNames().size());
	}
}
