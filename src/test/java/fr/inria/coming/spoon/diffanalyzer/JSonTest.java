package fr.inria.coming.spoon.diffanalyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import fr.inria.astor.core.entities.CNTX_Property;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.coming.core.engine.files.DiffICSE15ContextAnalyzer;
import fr.inria.coming.core.engine.files.MapCounter;
import fr.inria.coming.main.ComingProperties;
import gumtree.spoon.diff.Diff;

/**
 * 
 * @author Matias Martinez
 *
 */
public class JSonTest {
	@Test
	@Ignore
	public void testFailingTimeoutCase_986499() throws Exception {
		String diffId = "986499";

		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_2875_NPE() throws Exception {
		String diffId = "2875";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 2);
		assertTrue(hasColored(((JsonObject) allch.get(0)).get(CNTX_Property.AST_PARENT.toString()), "UPD"));
		assertTrue(hasColored(((JsonObject) allch.get(1)).get(CNTX_Property.AST_PARENT.toString()), "UPD"));
		assertFalse(hasColored(((JsonObject) allch.get(0)).get(CNTX_Property.AST_PARENT.toString()), "DEL"));
		assertFalse(hasColored(((JsonObject) allch.get(1)).get(CNTX_Property.AST_PARENT.toString()), "DEL"));
		assertFalse(hasColored(((JsonObject) allch.get(0)).get(CNTX_Property.AST_PARENT.toString()), "INS"));
		assertFalse(hasColored(((JsonObject) allch.get(1)).get(CNTX_Property.AST_PARENT.toString()), "INS"));
	}

	@Test
	public void testFailingTimeoutCase_65_replace() throws Exception {
		String diffId = "65";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
	}

	@Test
	public void testFailingTimeoutCase_4117_MOVE() throws Exception {
		String diffId = "4117";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 2);

		JsonObject jsonMove = (JsonObject) allch.get(1);
		System.out.println(jsonMove);

		JsonPrimitive buggyCode = (JsonPrimitive) jsonMove.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("(bytes.position())", buggyCode.getAsString());

		JsonArray buggyCodeParents = (JsonArray) jsonMove.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonArray();

		assertEquals(1, buggyCodeParents.size());

		JsonPrimitive bl1 = buggyCodeParents.get(0).getAsJsonObject().get(CNTX_Property.CODE.toString())
				.getAsJsonPrimitive();
		System.out.println(bl1);
		assertEquals("return java.lang.String.valueOf(bytes.getLong(((bytes.position()) + (bytes.arrayOffset()))))",
				bl1.getAsString());

		// PATCH

		JsonPrimitive patchCode = (JsonPrimitive) jsonMove.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("(bytes.position())", patchCode.getAsString());

		JsonArray asJsonArraypatch = jsonMove.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonArray();

		System.out.println("\n" + asJsonArraypatch);
		assertTrue(asJsonArraypatch.size() == 1);
		JsonPrimitive patchCodeParent = (JsonPrimitive) asJsonArraypatch.get(0).getAsJsonObject()
				.get(CNTX_Property.CODE.toString());

		System.out.println(patchCodeParent);
		assertEquals("return java.lang.String.valueOf(bytes.getLong(bytes.position()))", patchCodeParent.getAsString());

		// assertTrue(hasColored((JsonObject)
		// json0.get(CNTX_Property.AST_PARENT.toString()), "INS"));

	}

	@Test
	public void testFailingTimeoutCase_1264_NPE() throws Exception {
		String diffId = "1264";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 1);

		JsonObject json0 = (JsonObject) allch.get(0);

		JsonPrimitive ops = (JsonPrimitive) json0.get("bug")// .getAsJsonObject().get(CNTX_Property.AFFECTED.toString())
				.getAsJsonObject().get(CNTX_Property.OPERATION.toString());

		assertEquals("UPD", ops.getAsString());

		JsonPrimitive buggyCode = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("java.lang.String REPLICATION_UNABLE_TO_STOP_MASTER = \\\"XRE07\\\";", buggyCode.getAsString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("java.lang.String REPLICATION_NOT_IN_MASTER_MODE = \\\"XRE07\\\";", buggyCode1.getAsString());

	}

	@Test
	public void testFailingTimeoutCase_2_update() throws Exception {
		String diffId = "2";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 1);

		JsonObject json0 = (JsonObject) allch.get(0);
		System.out.println(json0);

		JsonPrimitive buggyCode = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("pageSize", buggyCode.getAsString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("averages = new float[pageSize]", buggyCode1.getAsString());

		/////
		System.out.println(json0);
		JsonPrimitive patchCode = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("initialPageCount", patchCode.getAsString());

		JsonPrimitive patchCodeP = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("averages = new float[initialPageCount]", patchCodeP.getAsString());

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "UPD"));

	}

	private boolean hasColored(JsonElement pob, String value) {

		if (pob instanceof JsonObject) {
			JsonObject ob = (JsonObject) pob;
			if (ob.keySet().contains("op")) {

				JsonPrimitive e = (JsonPrimitive) ob.get("op");
				if (e.getAsString().equals(value))
					return true;
			}

			for (String v : ob.keySet()) {
				JsonElement je = ob.get(v);

				if (hasColored(je, value)) {
					return true;
				}

			}
		} else if (pob instanceof JsonArray) {
			JsonArray ja = (JsonArray) pob;
			for (JsonElement jsonElement : ja) {
				if (hasColored(jsonElement, value)) {
					return true;
				}
			}

		}

		return false;

	}

	@Test
	public void testFailingTimeoutCase_1290_update() throws Exception {
		String diffId = "1290";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 1);

		JsonObject json0 = (JsonObject) allch.get(0);
		System.out.println("");

	}

	@Test
	public void testFailingTimeoutCase_61_update() throws Exception {
		String diffId = "61";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 1);

		JsonObject json0 = (JsonObject) allch.get(0);

		JsonPrimitive buggyCode = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("org.apache.lucene.index.IndexFileNames.segmentFileName(id, \\\"\\\", Writer.DATA_EXTENSION)",
				buggyCode.getAsString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		System.out.println(buggyCode1);
		assertEquals(
				"datIn = dir.openInput(org.apache.lucene.index.IndexFileNames.segmentFileName(id, \\\"\\\", Writer.DATA_EXTENSION), context)",
				buggyCode1.getAsString());

		/////
		System.out.println(json0);
		JsonPrimitive patchCode = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals(
				"org.apache.lucene.index.IndexFileNames.segmentFileName(id, Bytes.DV_SEGMENT_SUFFIX, Writer.DATA_EXTENSION)",
				patchCode.getAsString());

		JsonPrimitive patchCodeP = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals(
				"datIn = dir.openInput(org.apache.lucene.index.IndexFileNames.segmentFileName(id, Bytes.DV_SEGMENT_SUFFIX, Writer.DATA_EXTENSION), context)",
				patchCodeP.getAsString());

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "UPD"));

	}

	@Test
	public void testFailingTimeoutCase_694_Delete() throws Exception {
		String diffId = "694";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		System.out.println(allch);
		assertTrue(allch.size() == 1);

		JsonObject json0 = (JsonObject) allch.get(0);

		JsonPrimitive buggyCode = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("IndexWriter.MaxFieldLength.LIMITED", buggyCode.getAsString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());
		System.out.println(buggyCode1);
		assertEquals(
				"org.apache.lucene.index.IndexWriter writer = new org.apache.lucene.index.IndexWriter(runData.getDirectory(), config.get(\\\"autocommit\\\", org.apache.lucene.benchmark.byTask.tasks.OpenIndexTask.DEFAULT_AUTO_COMMIT), runData.getAnalyzer(), false, IndexWriter.MaxFieldLength.LIMITED)",
				buggyCode1.getAsString());

		///
		JsonNull patchCode = (JsonNull) json0.get("patch").getAsJsonObject().get(CNTX_Property.AFFECTED.toString());
		System.out.println(patchCode);
		assertEquals("null", patchCode.toString());

		JsonPrimitive patchCode1 = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());
		System.out.println(patchCode1);
		assertEquals(
				"org.apache.lucene.index.IndexWriter writer = new org.apache.lucene.index.IndexWriter(runData.getDirectory(), config.get(\\\"autocommit\\\", org.apache.lucene.benchmark.byTask.tasks.OpenIndexTask.DEFAULT_AUTO_COMMIT), runData.getAnalyzer(), false)",
				patchCode1.getAsString());

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "DEL"));

	}

	@Test
	public void testFailingTimeoutCase_5_insert() throws Exception {
		String diffId = "5";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		System.out.println(allch);
		assertTrue(allch.size() == 1);

		JsonObject json0 = (JsonObject) allch.get(0);

		// JsonPrimitive buggyCode = (JsonPrimitive)
		// json0.get("bug").getAsJsonObject().get(CNTX_Property.AFFECTED.toString()).getAsJsonObject()
		// .get(CNTX_Property.CODE.toString());

		JsonNull buggyCode = (JsonNull) json0.get("bug").getAsJsonObject().get(CNTX_Property.AFFECTED.toString());

		// assertEquals("new org.apache.lucene.index.RandomIndexWriter(random(), dir)",
		// buggyCode.getAsString());
		assertEquals("null", buggyCode.toString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals(
				"org.apache.lucene.index.RandomIndexWriter iw = new org.apache.lucene.index.RandomIndexWriter(random(), dir)",
				buggyCode1.getAsString());

		// Patch

		JsonPrimitive patchCode = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("cfg", patchCode.getAsString());

		JsonPrimitive patchCodeParent = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals(
				"org.apache.lucene.index.RandomIndexWriter iw = new org.apache.lucene.index.RandomIndexWriter(random(), dir, cfg)",
				patchCodeParent.getAsString());

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "INS"));
	}

	@Test
	public void testFailingTimeoutCase_70_delete() throws Exception {
		String diffId = "70";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		System.out.println(allch);
		assertTrue(allch.size() == 3);

		JsonObject json0 = (JsonObject) allch.get(0);

		JsonPrimitive buggyCode = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("!(isSunJVM())", buggyCode.getAsString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		System.out.println(buggyCode1);
		assertTrue(buggyCode1.getAsString().startsWith("if (!(isSunJVM())) "));

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "DEL"));

		JsonObject json1 = (JsonObject) allch.get(1);

		assertTrue(hasColored((JsonObject) json1.get(CNTX_Property.AST_PARENT.toString()), "INS"));

		JsonObject json2 = (JsonObject) allch.get(2);

		assertTrue(hasColored((JsonObject) json2.get(CNTX_Property.AST_PARENT.toString()), "MOV"));
	}

	@Test
	public void test_EMPTY_ICSE15_966027() throws Exception {
		String diffId = "966027";

		String input = "/Users/matias/develop/sketch-repair/outputdiff4/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		System.out.println("Print " + allch);
		// assertTrue(allch.size() == 2);

		// JsonObject json0 = (JsonObject) allch.get(0);

	}

	@Test
	public void testD4JMath4() throws Exception {
		String diffId = "Math_4";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
	}

	@Test
	public void testD4JChar14() throws Exception {
		String diffId = "Chart_14";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

	}

	@Test
	public void testD4JLang7_CBR() throws Exception {
		String diffId = "Lang_7";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

	}

	@Test
	public void testD4JLang3_WrapId() throws Exception {
		String diffId = "Lang_3";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

	}

	@Test
	public void testD4JMath75() throws Exception {
		String diffId = "Math_75";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected");
		for (JsonElement jsonElement : affected) {
			JsonArray affAr = (JsonArray) jsonElement;
			for (JsonElement aff : affAr) {

				JsonObject jo = (JsonObject) aff;
				JsonElement elAST = jo.get("ast");
				// System.out.println("--> AST element: \n" + elAST);
			}
		}

	}

	@Test
	public void testD4JChart3() throws Exception {
		String diffId = "Chart_3";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("faulty_stmts_ast");
			System.out.println("--> AST element: \n" + elAST);

		}

	}

	@Test
	public void testD4JLang1() throws Exception {
		String diffId = "Lang_1";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("faulty_stmts_ast");
			System.out.println("--> AST element: \n" + elAST);

		}

	}

	@Test
	public void testD4JMath_55() throws Exception {
		String diffId = "Math_55";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("faulty_stmts_ast");
			System.out.println("--> AST element: \n" + elAST);

		}

	}

	@Test
	public void testD4JTime_11() throws Exception {
		String diffId = "Time_11";
		ComingProperties.properties.setProperty("MAX_AST_CHANGES_PER_FILE", "200");
		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("faulty_stmts_ast");
			System.out.println("--> AST element: \n" + elAST);

		}

	}

	@Test
	public void testD4JMath_88() throws Exception {
		String diffId = "Math_88";
		ComingProperties.properties.setProperty("MAX_AST_CHANGES_PER_FILE", "200");
		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected");
		for (JsonElement jsonElement : affected) {
			JsonArray affAr = (JsonArray) jsonElement;
			for (JsonElement aff : affAr) {

				JsonObject jo = (JsonObject) aff;
				JsonElement elAST = jo.get("ast");
				System.out.println("--> AST element: \n" + elAST);
			}
		}

	}

	@Test
	public void testD4JChart4() throws Exception {
		String diffId = "Chart_4";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);

	}

	@Test
	public void testD4Jchart18() throws Exception {
		String diffId = "Chart_18";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);

	}

	@Test
	public void testD4Jlang31() throws Exception {
		String diffId = "Lang_31";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);

	}

	@Test
	public void testD4Jclosure2() throws Exception {
		String diffId = "Closure_2";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);

	}

	@Test
	public void testD4Jlang33() throws Exception {
		String diffId = "Lang_33";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);

	}

	@Test
	public void testD4Jclosure111() throws Exception {
		String diffId = "Closure_111";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);

	}

	@Test
	public void testD4Jchart21() throws Exception {
		String diffId = "Chart_21";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);

	}

	@Test
	public void testD4Jlang17() throws Exception {
		String diffId = "Lang_17";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
		// See multiple susp
	}

	@Test
	public void testD4Jmath46() throws Exception {
		String diffId = "Math_46";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	public void testD4Jtime18() throws Exception {
		String diffId = "time_18";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	public void testD4Jclosure83() throws Exception {
		String diffId = "Closure_83";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	public void testD4Jmath60() throws Exception {
		String diffId = "Math_60";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	public void testD4Jlang13() throws Exception {
		String diffId = "Lang_13";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	public void testD4Jchart10() throws Exception {
		String diffId = "Chart_10";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	public void testD4Jchart12() throws Exception {
		String diffId = "Chart_12";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	public void testD4Jmath105() throws Exception {
		String diffId = "Math_105";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	public void testD4Jtime8() throws Exception {
		String diffId = "time_8";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	@Ignore
	public void testD4Jmockito14() throws Exception {
		String diffId = "time_8";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	@Ignore
	public void testD4Jmath27() throws Exception {
		String diffId = "Math_27";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	public void testD4closure124() throws Exception {
		String diffId = "Closure_124";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

	@Test
	public void testD4Math_7() throws Exception {
		String diffId = "Math_7";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		showAST(resultjson);
	}

//	time17()
//	closure124()
//	math7()

	private void showAST(JsonObject resultjson) {
		System.out.println(resultjson);

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("faulty_stmts_ast");
			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() > 0);
			// System.out.println("--> AST element: \n" + elAST);
			for (JsonElement suspiciousTree : ar) {
				System.out.println("--> AST element: \n" + suspiciousTree);
				assertTrue(printSusp(suspiciousTree));
			}

		}
	}

	private boolean printSusp(JsonElement ob) {
		boolean t = false;
		if (ob instanceof JsonObject) {
			JsonObject jon = ob.getAsJsonObject();
			for (String s : jon.keySet()) {
				if (s.equals("susp")) {
					System.out.println("susp--> " + ob);
					t = true;
				} else {

					JsonElement e = jon.get(s);
					if (e instanceof JsonArray) {
						JsonArray arr = e.getAsJsonArray();
						for (JsonElement jsonElement : arr) {
							if (printSusp(jsonElement)) {
								t = true;
							}
						}
					}

				}

			}
		}
		return t;
	}

	@Test
	public void testFailingTimeoutCase_1555_Move_new() throws Exception {
		String diffId = "1555";

		String input = "/Users/matias/develop/CodeRep-data/result_Dataset1_unidiff/" + diffId;
		JsonObject resultjson = getContext(diffId, input);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 2);

		JsonObject json0 = (JsonObject) allch.get(0);

		// INSERT

		Object buggyCode = json0.get("bug").getAsJsonObject().get(CNTX_Property.AFFECTED.toString());
		assertEquals("null", buggyCode.toString());

		JsonPrimitive typeBuggyParent = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get("TYPE");
		assertEquals("CtLocalVariableImpl", typeBuggyParent.getAsString());

		/// Move
		JsonObject json1 = (JsonObject) allch.get(1);
		System.out.println(json1);

		JsonPrimitive buggyCode1 = (JsonPrimitive) json1.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals(
				// "(userStartTimeout != null) ? java.lang.Long.parseLong(userStartTimeout) :
				// org.apache.derbyTesting.functionTests.tests.replicationTests.ReplicationRun.DEFAULT_SERVER_START_TIMEOUT",
				"java.lang.Long.parseLong(userStartTimeout)", buggyCode1.getAsString());

		JsonArray asJsonArray = json1.get("bug").getAsJsonObject().get(CNTX_Property.AFFECTED_PARENT.toString())
				.getAsJsonArray();

		JsonPrimitive buggyCodeParent = (JsonPrimitive) asJsonArray.get(0).getAsJsonObject()
				.get(CNTX_Property.CODE.toString());
		// System.out.println(buggyCodeParent);
		assertEquals(
				"long startTimeout = (userStartTimeout != null) ? java.lang.Long.parseLong(userStartTimeout) : org.apache.derbyTesting.functionTests.tests.replicationTests.ReplicationRun.DEFAULT_SERVER_START_TIMEOUT"

				, buggyCodeParent.getAsString());

		///

		JsonPrimitive patchCode = (JsonPrimitive) json1.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("java.lang.Long.parseLong(userStartTimeout)", patchCode.getAsString());

		JsonArray asJsonArraypatch = json1.get("patch").getAsJsonObject().get(CNTX_Property.AFFECTED_PARENT.toString())
				.getAsJsonArray();

		assertTrue(asJsonArraypatch.size() == 1);
		JsonPrimitive patchCodeParent = (JsonPrimitive) asJsonArraypatch.get(0).getAsJsonObject()
				.get(CNTX_Property.CODE.toString());

		System.out.println(patchCodeParent.getAsString());
		assertEquals(
				"long startTimeout = (userStartTimeout != null) ? (java.lang.Long.parseLong(userStartTimeout)) * 1000 : org.apache.derbyTesting.functionTests.tests.replicationTests.ReplicationRun.DEFAULT_SERVER_START_TIMEOUT",
				patchCodeParent.getAsString());

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "INS"));

	}

	private JsonObject getContext(String diffId, String input) {
		File fileInput = new File(input);
		System.out.println(input);

		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ComingProperties.properties.setProperty("max_synthesis_step", "100000");
		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		@SuppressWarnings("unused")
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileInput);

		Map<String, Diff> diffOfcommit = analyzer.getDiffOfcommit();
		for (Diff diff : diffOfcommit.values()) {
			System.out.println("Diff " + diff);
		}

		MapCounter<String> counter = new MapCounter<>();
		MapCounter<String> counterParent = new MapCounter<>();

		analyzer.processDiff(counter, counterParent, fileInput);

		JsonObject resultjson = analyzer.calculateCntxJSON(diffId, diffOfcommit);
		return resultjson;
	}
}
