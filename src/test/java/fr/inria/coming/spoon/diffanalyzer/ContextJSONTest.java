package fr.inria.coming.spoon.diffanalyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import fr.inria.astor.core.entities.CNTX_Property;
import fr.inria.coming.core.engine.files.BugFixRunner;
import fr.inria.coming.core.engine.files.DiffICSE15ContextAnalyzer;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.DeleteOperation;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.Operation;

public class ContextJSONTest {
	File s = getFile("patterns_examples/case2/1205753_EmbedPooledConnection_0_s.java");
	File t = getFile("patterns_examples/case2/1205753_EmbedPooledConnection_0_t.java");

	@Before
	public void setUp() throws Exception {

		ConsoleAppender console = new ConsoleAppender();
		String PATTERN = "%m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		Logger.getRootLogger().addAppender(console);

	}

	@Test
	public void testDiff2Invocation() throws Exception {
		Diff diffInsert = null;

		BugFixRunner r = new BugFixRunner();
		diffInsert = r.getdiff(s, t);
		System.out.println("Output: " + diffInsert);
		Assert.assertEquals(1, diffInsert.getRootOperations().size());
		Assert.assertTrue(diffInsert.getRootOperations().get(0) instanceof InsertOperation);

		List<Operation> opsr = diffInsert.getRootOperations();
		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();
		Map<String, Diff> opsByFile = new HashMap<>();
		opsByFile.put("filetest", diffInsert);
		JsonObject js = analyzer.calculateCntxJSON("filetest", opsByFile);
		System.out.println("out:\n" + js);

		for (Operation operation : opsr) {
			System.out.println("src: " + operation.getSrcNode());
			System.out.println("trg: " + operation.getDstNode());
			System.out.println("node: " + operation.getNode());
		}
	}

	@Test
	public void testDiffDelete() throws Exception {

		BugFixRunner r = new BugFixRunner();
		Diff diffRemove = null;
		diffRemove = r.getdiff(t, s);
		Assert.assertEquals(1, diffRemove.getRootOperations().size());
		Assert.assertTrue(diffRemove.getRootOperations().get(0) instanceof DeleteOperation);

		List<Operation> opsr = diffRemove.getRootOperations();
		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();
		Map<String, Diff> opsByFile = new HashMap<>();
		opsByFile.put("filetest", diffRemove);
		JsonObject js = analyzer.calculateCntxJSON("filetest", opsByFile);
		System.out.println("out:\n" + js);

		for (Operation operation : opsr) {
			System.out.println("**src: " + operation.getSrcNode());
			System.out.println("**trg: " + operation.getDstNode());
			System.out.println("**node: " + operation.getNode());
		}
	}

	public File getFile(String name) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(name).getFile());
		return file;
	}

	@Test
	public void testContext_v1() {

		String diffId = "Math_24";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println(resultjson);
		assertMarkedlAST(resultjson, CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN, Boolean.TRUE);
	}

	@Test
	public void testContext_m1_1() {

		String diffId = null;// "Math_58";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN, Boolean.FALSE);
	}

	@Test
	public void testContext_v2_2() {

		String diffId = "Math_26";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN, Boolean.FALSE);
	}

	@Test
	public void testContext_L1_1() {

		String diffId = "Closure_20";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE1_EXISTS_RELATED_BOOLEAN_EXPRESSION, Boolean.TRUE);
	}

	@Test
	public void testContext_L2_1() {

		String diffId = "Closure_51";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR, Boolean.TRUE);
	}

	@Test
	public void testContext_L3_1() {

		String diffId = "Chart_9";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED, Boolean.TRUE);
	}

	@Test
	public void testContext_L4_1() {

		String diffId = "Closure_38";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE4_EXISTS_LOCAL_UNUSED_VARIABLES, Boolean.TRUE);
	}

	@Test
	public void testContext_L5_1() {

		String diffId = "Closure_38";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE5_BOOLEAN_EXPRESSIONS_IN_FAULTY, Boolean.TRUE);
	}

	@Test
	public void testContext_S1_1() {

		String diffId = "Chart_4";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.S1_LOCAL_VAR_NOT_ASSIGNED, Boolean.TRUE);
	}

	public static void assertMarkedlAST(JsonObject resultjson, CNTX_Property name, Boolean b) {

		System.out.println("**************** finding " + name);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJsonString = gson.toJson(resultjson);

		// System.out.println(prettyJsonString);
		boolean found = false;
		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() > 0);

			// System.out.println("--> AST element: \n" + elAST);
			for (JsonElement suspiciousTree : ar) {

				JsonObject jso = suspiciousTree.getAsJsonObject();
				// System.out.println("--> AST element: \n" + jso.get("pattern_name"));
				// System.out.println("suspicious element:\n" + prettyJsonString);
				JsonObject asJsonObject = jso.get("context").getAsJsonObject().get("cntx").getAsJsonObject();
				JsonPrimitive value = asJsonObject.get(name.toString()).getAsJsonPrimitive();

				System.out.println(name + " " + value.getAsString());
				assertEquals(b, Boolean.parseBoolean(value.getAsString()));

			}

		}
		// assertTrue("Node suspicious not found", found);
	}
}
