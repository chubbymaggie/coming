package fr.inria.coming.spoon.diffanalyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import add.entities.PatternInstance;
import add.entities.RepairPatterns;
import add.features.detector.EditScriptBasedDetector;
import add.features.detector.repairpatterns.RepairPatternDetector;
import add.main.Config;
import fr.inria.coming.core.engine.files.DiffICSE15ContextAnalyzer;
import fr.inria.coming.core.engine.files.MapCounter;
import gumtree.spoon.diff.Diff;

public class VerificationTest {

	public List<RepairPatterns> analyze(String input) {
		File fileInput = new File(input);
		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		@SuppressWarnings("unused")
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileInput);

		Map<String, Diff> diffOfcommit = analyzer.getDiffOfcommit();

		List<RepairPatterns> patterns = new ArrayList<>();
		for (Diff diff : diffOfcommit.values()) {
			System.out.println("Diff " + diff);

			RepairPatterns r = getPatterns(diff);
			patterns.add(r);

		}
		return patterns;
	}

	public RepairPatterns getPatterns(Diff diff) {
		Config config = new Config();
		EditScriptBasedDetector.preprocessEditScript(diff);
		RepairPatternDetector detector = new RepairPatternDetector(config, diff);
		RepairPatterns rp = detector.analyze();
		return rp;
	}

	@Test
	public void testVerificationD4Jmath105() throws Exception {
		String diffId = "Math_105";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString()
				.contains("Math.max(0.0, ((sumYY) - (((sumXY) * (sumXY)) / (sumXX))))"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("((sumYY) - (((sumXY) * (sumXY)) / (sumXX)))")).findFirst()
				.isPresent());

		assertTrue(pi1.getFaultyTree() != null);
		assertEquals("return (sumYY) - (((sumXY) * (sumXY)) / (sumXX))", pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		JSonTest.showAST(resultjson);
	}

	@Test
	public void testVerificationD4JChart13() throws Exception {
		String diffId = "Chart_13";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		// assertTrue(pi1.getNodeAffectedOp().toString()
		// .contains("Math.max(0.0, ((sumYY) - (((sumXY) * (sumXY)) / (sumXX))))"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("((constraint.getWidth()) - (w[2]))"))
				.findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals(
				"org.jfree.chart.block.RectangleConstraint c4 = new org.jfree.chart.block.RectangleConstraint(0.0, new org.jfree.data.Range(0.0, ((constraint.getWidth()) - (w[2]))), LengthConstraintType.RANGE, h[2], null, LengthConstraintType.FIXED)",
				pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		JSonTest.showAST(resultjson);
	}

	@Test
	public void testVerificationD4Jchart10() throws Exception {
		String diffId = "Chart_10";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("ImageMapUtilities.htmlEscape(toolTipText)"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("toolTipText")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals("return (\" title=\\\"\" + toolTipText) + \"\\\" alt=\\\"\\\"\"", pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		JSonTest.showAST(resultjson);
	}

	@Test
	public void testVerificationD4Jchart12() throws Exception {
		String diffId = "Chart_12";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("setDataset(dataset)"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("this.dataset")).findFirst()

				// assertTrue(pi1.getFaulty().stream().filter(e ->
				// e.toString().contains("this.dataset = dataset")).findFirst()
				.isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("this.dataset = dataset", pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		JSonTest.showAST(resultjson);
	}

	@Test
	public void testVerificationD4Jtime8() throws Exception {
		String diffId = "Time_8";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("Math.abs(minutesOffset)"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("inutesOffset")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals("minutesOffset = hoursInMinutes - minutesOffset", pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		JSonTest.showAST(resultjson);
	}

	@Test
	public void testVerificationD4JMath_26() throws Exception {
		String diffId = "Math_26";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.stream().filter(
				e -> e.getNodeAffectedOp().toString().equals("(org.apache.commons.math3.util.FastMath.abs(p2))"))
				.findFirst().get();
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("p2")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals("(p2 > overflow) || (q2 > overflow)", pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		JSonTest.showAST(resultjson);

		// To check duplicates
		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 2);

		}
	}

	@Test
	public void testVerificationD4JMath35() throws Exception {
		String diffId = "Math_35";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("setElitismRate(elitismRate)"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(
				pi1.getFaulty().stream().filter(e -> e.toString().equals("this.elitismRate")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals("this.elitismRate = elitismRate", pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		JSonTest.showAST(resultjson);
		JSonTest.assertMarkedlAST(resultjson, "wrapsMethod", "elitismRate", "FieldWrite");
	}

	@Test
	public void testVerificationD4JClosure25() throws Exception {
		String diffId = "Closure_25";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrongMethodRef") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongMethodRef");
		System.out.println(insts);
		assertEquals(1, insts.size());

		PatternInstance pi1 = insts.stream().filter(e -> e.getPatternName().equals("wrongMethodRef")).findFirst().get();
		// assertTrue(pi1.getNodeAffectedOp().toString().contains("setElitismRate(elitismRate)"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().equals("traverse(constructor, scope)")).findFirst()
				.isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals("scope = traverse(constructor, scope)", pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		JSonTest.showAST(resultjson);
		JSonTest.assertMarkedlAST(resultjson, "wrongMethodRef",
				"com.google.javascript.jscomp.TypeInference#traverse(com.google.javascript.rhino.Node,com.google.javascript.jscomp.type.FlowScope)",
				"Invocation");

		// To check Patterns instances
		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			// assertTrue(ar.size() == 2);

		}
		System.out.println("End 1\n " + resultjson);
		// SECOND pattern move

		// AVOID moves
		if (false) {
			List<PatternInstance> instsMove = repairPatterns.getPatternInstances().get("codeMove");
			System.out.println("Pattern move: ");
			System.out.println(instsMove);
			assertEquals(1, instsMove.size());

			pi1 = instsMove.stream().filter(e -> e.getPatternName().equals("codeMove")).findFirst().get();
			assertTrue(pi1.getNodeAffectedOp().toString().contains("Node constructor = n.getFirstChild()"));
			// assertEquals(1, pi1.getFaulty().size());
			// assertTrue(pi1.getFaulty().stream().filter(e ->
			// e.toString().equals("constructor.getJSType()")).findFirst()
			// .isPresent());

			assertNotNull(pi1.getFaultyTree());
			assertEquals("com.google.javascript.rhino.jstype.JSType constructorType = constructor.getJSType()",
					pi1.getFaultyLine().toString());

			// resultjson = JSonTest.getContext(diffId, input);
			// System.out.println("End 2\n " + resultjson);
			// JSonTest.showAST(resultjson);
			JSonTest.assertMarkedlAST(resultjson, "codeMove", "constructorType", "LocalVariable");

		}
	}

	@Test
	public void testVerificationD4JClosure103() throws Exception {
		String diffId = "Closure_103";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);
		Assert.assertTrue(repairPatterns.getFeatureCounter("expLogicExpand") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("expLogicExpand");

		System.out.println("Values " + repairPatterns.getPatternInstances().values());

		assertNull(insts);
		System.out.println(insts);

	}

	@Test
	public void testVerificationD4Jchart15() throws Exception {
		String diffId = "Chart_15";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);
		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIf") > 0);
		Assert.assertTrue(repairPatterns.getFeatureCounter("missNullCheckP") > 0);
		Assert.assertTrue(repairPatterns.getFeatureCounter("missNullCheckN") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("(this.dataset) != null"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains(
				"state.setTotal(org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(plot.getDataset())"))
				.findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals(
				"state.setTotal(org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(plot.getDataset()))",
				pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrapsIf", "org.jfree.chart.plot.PiePlotState#setTotal()", "Invocation");

		// The second pattern:
		insts = repairPatterns.getPatternInstances().get("missNullCheckP");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("(this.dataset) == null"));
		// assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("double result = 0.0")).findFirst()
				.isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("double result = 0.0", pi1.getFaultyLine().toString());
		// assertEquals(1, pi1.getFaulty().size());

		// The third pattern:
		insts = repairPatterns.getPatternInstances().get("missNullCheckN");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("(this.dataset) != null"));
		// assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains(
				"state.setTotal(org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(plot.getDataset()))"))
				.findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals(
				"state.setTotal(org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(plot.getDataset()))",
				pi1.getFaultyLine().toString());
		assertEquals(1, pi1.getFaulty().size());

		// Check that there are two suspicious, and one with two patterns

		// To check Patterns instances
		JsonArray affected = (JsonArray) resultjson.get("affected_files");

		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 2);
			boolean hasBoth = false;
			for (JsonElement e : ar) {
				JsonElement el = JSonTest.getSusp(e);
				if (JSonTest.hasPattern((JsonObject) el, "susp_missNullCheckN")
						&& JSonTest.hasPattern((JsonObject) el, "susp_wrapsIf"))
					hasBoth = true;
			}
			assertTrue(hasBoth);
		}

	}

	@Test
	public void testVerificationD4Jchart26() throws Exception {
		String diffId = "Chart_26";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);
		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIf") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("owner != null"));
		assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("EntityCollection entities = owner.getEntityCollection()"))
				.findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("org.jfree.chart.entity.EntityCollection entities = owner.getEntityCollection()",
				pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrapsIf", "entities", "LocalVariable");
		JSonTest.assertMarkedlAST(resultjson, "missNullCheckN", "entities", "LocalVariable");

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 1);
			boolean hasBoth = false;
			for (JsonElement e : ar) {
				JsonElement el = JSonTest.getSusp(e);
				if (JSonTest.hasPattern((JsonObject) el, "susp_missNullCheckN")
						&& JSonTest.hasPattern((JsonObject) el, "susp_wrapsIf"))
					hasBoth = true;
			}
			assertTrue(hasBoth);
		}

	}

	@Test
	public void testVerificationD4Jchart4() throws Exception {
		String diffId = "Chart_4";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);
		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIf") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		assertEquals(3, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("java.util.Collection c = r.getAnnotations()")).findFirst()
				.isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("java.util.Collection c = r.getAnnotations()", pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrapsIf", "c", "LocalVariable");
		JSonTest.assertMarkedlAST(resultjson, "missNullCheckN", "c", "LocalVariable");

	}

	@Test
	public void testVerificationD4Jchart21() throws Exception {
		String diffId = "Chart_21";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);
		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsElse") > 0);

		// Pattern 1
		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		// assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("double minval = java.lang.Double.NaN"))
				.findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("double minval = java.lang.Double.NaN", pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrapsElse", "minval", "LocalVariable");

		// Pattern addassignment

		insts = repairPatterns.getPatternInstances().get("addassignment");
		System.out.println(insts);
		assertTrue(insts.size() == 4);

		pi1 = insts.get(2);
		// assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("this.maximumRangeValue = java.lang.Double.NaN")).findFirst()
				.isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("this.maximumRangeValue = java.lang.Double.NaN", pi1.getFaultyLine().toString());

		System.out.println("END 1\n" + resultjson.toString());
		// TODO:
		JSonTest.assertMarkedlAST(resultjson, "addassignment", "\u003d"/* "maximumRangeValue" */, "Assignment");

	}

	@Test
	public void testVerificationD4Jchart22() throws Exception {
		String diffId = "Chart_22";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		// assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("row")).findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("row >= 0", pi1.getFaultyLine().toString());

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrongVarRef", "row", "VariableRead");

		// Pattern 2
		insts = repairPatterns.getPatternInstances().get("unwrapMethod");
		pi1 = insts.get(0);
		System.out.println(pi1);
	}

	@Test
	public void testVerificationD4JClosure96() throws Exception {
		String diffId = "Closure_96";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("expLogicExpand");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);

		// assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("(arguments.hasNext())")).findFirst()
				.isPresent());
		assertNotNull(pi1.getFaultyTree());
		// assertNotNull(pi1.getFaultyTree());
		assertEquals("(arguments.hasNext()) && (parameters.hasNext())", pi1.getFaultyLine().toString());
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// TODO:
		// JSonTest.showAST(resultjson, "expLogicExpand", "AND"/* "maximumRangeValue"
		// */, "BinaryOperator");

	}

	@Test
	public void testVerificationD4JChart5() throws Exception {
		String diffId = "Chart_5";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("expLogicReduce");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);

		// assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		// assertEquals(2, pi1.getFaulty().size());

		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().equals("(!(this.allowDuplicateXValues))"))
				.findFirst().isPresent());
		// assertTrue(pi1.getFaulty().stream().filter(e ->
		// e.toString().equals("this.allowDuplicateXValues")).findFirst()
		// .isPresent());
		assertNotNull(pi1.getFaultyTree());
		// assertNotNull(pi1.getFaultyTree());
		assertEquals("(index >= 0) && (!(this.allowDuplicateXValues))", pi1.getFaultyLine().toString());
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// TODO:
		// JSonTest.showAST(resultjson, "expLogicExpand", "AND"/* "maximumRangeValue"
		// */, "BinaryOperator");

	}

	@Test
	public void testVerificationD4JClosure20() throws Exception {
		String diffId = "Closure_20";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("expLogicExpand");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);
		assertTrue(pi1.getNodeAffectedOp().toString().contains(
				"((value != null) && ((value.getNext()) == null)) && (com.google.javascript.jscomp.NodeUtil.isImmutableValue(value))"));
		assertNotNull(pi1.getFaultyTree());
		assertTrue(
				pi1.getFaulty().stream().filter(e -> e.toString().contains("value != null")).findFirst().isPresent());

		// assertTrue(pi1.getFaultyLine().toString().equals("value != null"));
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
	}

	@Test
	public void testVerificationD4JMath_28() throws Exception {
		String diffId = "Math_28";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);
		// assertTrue(pi1.getNodeAffectedOp().toString().contains(
		// "((value != null) && ((value.getNext()) == null)) &&
		// (com.google.javascript.jscomp.NodeUtil.isImmutableValue(value))"));

		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().startsWith("for (java.lang.Integer row : minRatioPositions) {")).findFirst()
				.isPresent());

		// assertTrue(pi1.getFaultyLine().toString().equals("value != null"));
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrapsIf", "", "ForEach");
	}

	@Test
	public void testVerificationD4JMath54() throws Exception {
		String diffId = "Math_54";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);
		// assertTrue(pi1.getNodeAffectedOp().toString().contains(
		// "((value != null) && ((value.getNext()) == null)) &&
		// (com.google.javascript.jscomp.NodeUtil.isImmutableValue(value))"));

		assertTrue(
				pi1.getFaulty().stream().filter(e -> e.toString().startsWith("y = negate()")).findFirst().isPresent());

		// assertTrue(pi1.getFaultyLine().toString().equals("value != null"));
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrapsIf", "\u003d", "Assignment");
	}

	@Test
	public void testVerificationD4JMath86() throws Exception {
		String diffId = "Math_86";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);
	}

	@Test
	public void testVerificationD4JClosure88() throws Exception {
		String diffId = "Closure_88";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);
		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);
	}

	@Test
	public void testVerificationD4JClosure17() throws Exception {
		String diffId = "Closure_17";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);
		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

	}

	@Test
	public void testVerificationD4JClosure58() throws Exception {
		String diffId = "Closure_58";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		assertTrue(patterns.size() > 0);
		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);
		assertTrue(pi1.getNodeAffectedOp().toString()
				.contains("if (com.google.javascript.jscomp.NodeUtil.isName(lhs)) {"));

		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().startsWith("addToSetIfLocal(lhs, kill)"))
				.findFirst().isPresent());

		assertTrue(pi1.getFaultyLine().toString().equals("addToSetIfLocal(lhs, kill)"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrapsIfElse",
				"com.google.javascript.jscomp.LiveVariablesAnalysis#addToSetIfLocal(com.google.javascript.rhino.Node,java.util.BitSet)",
				"Invocation");

	}

	////

	@Test
	public void testVerificationD4JChart18() {
		String diffId = "Chart_18";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		assertTrue(patterns.size() == 2);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIf") > 0);

		List<PatternInstance> listWrap = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(listWrap);
		assertTrue(listWrap.size() > 0);

		PatternInstance pi1 = listWrap.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("index >= 0"));
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("rowData.removeValue(columnKey)"))
				.findFirst().isPresent());

		List<PatternInstance> insts = patterns.get(1).getPatternInstances().get("unwrapIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("rebuildIndex()"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("if (index < (this.keys.size()))"))
				.findFirst().isPresent());
		assertTrue(pi1.getFaultyLine().toString().contains("if (index < (this.keys.size())) {"));
		assertNotNull(pi1.getFaultyTree());
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "unwrapIfElse", "if", "If");

	}

	@Test
	public void testVerificationD4JClosure2() {

		String diffId = "Closure_2";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		assertTrue(patterns.size() == 1);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIfElse") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("implicitProto == null"));
		assertTrue(pi1.getFaulty().size() == 1);
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("currentPropertyNames = implicitProto.getOwnPropertyNames()"))
				.findFirst().isPresent());
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		JSonTest.assertMarkedlAST(resultjson, "wrapsIfElse", "\u003d", "Assignment");

		Assert.assertTrue(repairPatterns.getFeatureCounter("missNullCheckP") > 0);

		///
		insts = repairPatterns.getPatternInstances().get("missNullCheckP");

		System.out.println(insts);
		assertTrue(insts.size() > 0);

		pi1 = insts.get(0);

		assertTrue(pi1.getFaultyLine().toString().equals("currentPropertyNames = implicitProto.getOwnPropertyNames()"));

	}

	@Test
	public void testVerificationD4JLang33() {

		String diffId = "Lang_33";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIfElse") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("(array[i]) == null"));
		assertTrue(pi1.getFaulty().size() == 1);
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("array[i].getClass()")).findFirst()
				.isPresent());

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		JSonTest.assertMarkedlAST(resultjson, "wrapsIfElse", "java.lang.Object#getClass()", "Invocation");
	}

	@Test
	public void testVerificationD4JClosure111() {
		String diffId = "Closure_111";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIfElse") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("topType.isAllType()"));
		assertTrue(pi1.getFaulty().size() == 1);
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("topType")).findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		JSonTest.assertMarkedlAST(resultjson, "wrapsIfElse", "topType", "VariableRead");
	}

	@Test
	public void testVerificationD4JLang17() {
		String diffId = "Lang_17";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("unwrapIfElse") > 0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("unwrapMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString()
				.contains("pos += java.lang.Character.charCount(java.lang.Character.codePointAt(input, pos))"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("if (pos < (len - 2))")).findFirst()
				.isPresent());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("pos++")).findFirst().isPresent());

		assertTrue(pi1.getFaultyLine().toString().contains("if (pos < (len - 2))"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "unwrapIfElse", "if", "If");

		// Second pattern
		insts = repairPatterns.getPatternInstances().get("unwrapMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("input.length()"));
		// assertEquals(2, pi1.getFaulty().size());
		// assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("if
		// (pos < (len - 2))")).findFirst()
		// .isPresent());
		// assertTrue(pi1.getFaulty().stream().filter(e ->
		// e.toString().contains("pos++")).findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString()
				.equals("int len = java.lang.Character.codePointCount(input, 0, input.length())"));

		resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "unwrapMethod",
				"java.lang.Character#codePointCount(java.lang.CharSequence,int,int)", "Invocation");

	}

	@Test
	public void testVerificationD4JMath46() {
		String diffId = "Math_46";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		Assert.assertTrue(repairPatterns.getFeatureCounter("unwrapIfElse") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("NaN"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("isZero")).findFirst().isPresent());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("INF")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"return isZero ? org.apache.commons.math.complex.Complex.NaN : org.apache.commons.math.complex.Complex.INF"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "unwrapIfElse", "INF", "FieldRead");

	}

	@Test
	public void testVerificationD4JClosure11() {
		String diffId = "Closure_11";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);
	}

	@Test
	public void testVerificationD4JClosure30() {
		String diffId = "Closure_30";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);
	}

	@Test
	public void testVerificationD4JTime20() {
		String diffId = "Time_20";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);
	}

	@Test
	public void testVerificationD4JClosure9() {
		String diffId = "Closure_9";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "unwrapMethod",
				"com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
				"Invocation");
	}

	@Test
	public void testVerificationD4JClosure90() {
		String diffId = "Closure_90";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");
	}

	@Test
	public void testVerificationD4JLang32() {
		String diffId = "Lang_32";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");
	}

	@Test
	public void testVerificationD4JChart11() {
		String diffId = "Chart_11";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("p1")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString()
				.equals("java.awt.geom.PathIterator iterator2 = p1.getPathIterator(null)"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrongVarRef", "p1", "VariableRead");
	}

	@Test
	public void testVerificationD4JClosure213() {
		String diffId = "Closure_123";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("com.google.javascript.jscomp.CodeGenerator.Context.OTHER"))
				.findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"com.google.javascript.jscomp.CodeGenerator.Context rhsContext = com.google.javascript.jscomp.CodeGenerator.Context.OTHER"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrongVarRef", "OTHER", "FieldRead");
	}

	@Test
	public void testVerificationD4JMath98() {
		String diffId = "Math_98";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("v.length")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("final double[] out = new double[v.length]"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrongVarRef", "length", "FieldRead");
	}

	@Test
	public void testVerificationD4JChar20() {
		String diffId = "Chart_20";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);

		assertTrue(checkVar(pi1, "stroke", "paint"));

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("super(paint, stroke, paint, stroke, alpha)"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		PatternInstance pi2 = insts.get(0);

		assertTrue(checkVar(pi2, "stroke", "paint"));

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrongVarRef", "stroke", "VariableRead");
	}

	public boolean checkVar(PatternInstance pi1, String v1, String v2) {
		return pi1.getFaulty().stream().filter(e -> e.toString().contains(v1)).findFirst().isPresent()
				|| pi1.getFaulty().stream().filter(e -> e.toString().contains(v2)).findFirst().isPresent();
	}

	@Test
	public void testVerificationD4JClosure75() {
		String diffId = "Closure_75";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("com.google.javascript.rhino.jstype.TernaryValue.TRUE")).findFirst()
				.isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(
				pi1.getFaultyLine().toString().equals("return com.google.javascript.rhino.jstype.TernaryValue.TRUE"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrongVarRef", "TRUE", "FieldRead");

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 1);

		}

	}

	@Test
	public void testVerificationD4JLang21() {
		String diffId = "Lang_21";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("java.util.Calendar.HOUR")).findFirst()
				.isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"return ((((((((cal1.get(java.util.Calendar.MILLISECOND)) == (cal2.get(java.util.Calendar.MILLISECOND))) && ((cal1.get(java.util.Calendar.SECOND)) == (cal2.get(java.util.Calendar.SECOND)))) && ((cal1.get(java.util.Calendar.MINUTE)) == (cal2.get(java.util.Calendar.MINUTE)))) && ((cal1.get(java.util.Calendar.HOUR)) == (cal2.get(java.util.Calendar.HOUR)))) && ((cal1.get(java.util.Calendar.DAY_OF_YEAR)) == (cal2.get(java.util.Calendar.DAY_OF_YEAR)))) && ((cal1.get(java.util.Calendar.YEAR)) == (cal2.get(java.util.Calendar.YEAR)))) && ((cal1.get(java.util.Calendar.ERA)) == (cal2.get(java.util.Calendar.ERA)))) && ((cal1.getClass()) == (cal2.getClass()))"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// JSonTest.showAST(resultjson, "wrongVarRef", "TRUE", "FieldRead");

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 1);

		}

	}

	@Test
	public void testVerificationD4JMath5() {
		String diffId = "Math_5";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("org.apache.commons.math3.complex.Complex.NaN")).findFirst()
				.isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("return org.apache.commons.math3.complex.Complex.NaN"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "wrongVarRef", "NaN", "FieldRead");

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 1);

		}

	}

	@Test
	public void testVerificationD4JMath76() {
		String diffId = "Math_76";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("p")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"final org.apache.commons.math.linear.RealMatrix e = eigenDecomposition.getV().getSubMatrix(0, (p - 1), 0, (p - 1))"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// JSonTest.showAST(resultjson, "wrongVarRef", "NaN", "FieldRead");

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 2);

		}

	}

	@Test
	public void testVerificationD4JClosure40() {
		String diffId = "Closure_40";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("constChange");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("false")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString()
				.equals("com.google.javascript.jscomp.NameAnalyzer.JsName name = getName(ns.name, false)"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "constChange", "false", "Literal");
	}

	@Test
	@Ignore
	public void testVerificationD4JClosure102() {
		String diffId = "Closure_102";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("codeMove");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString()
				.startsWith("if (com.google.javascript.jscomp.Normalize.MAKE_LOCAL_NAMES_UNIQUE) "));
		// assertEquals(2, pi1.getFaulty().size());
		// assertTrue(pi1.getFaulty().stream().filter(e ->
		// e.toString().contains("false")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"new com.google.javascript.jscomp.Normalize.PropogateConstantAnnotations(compiler, assertOnChange).process(externs, root)"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// JSonTest.assertMarkedlAST(resultjson, "constChange", "false", "Literal");
	}

	@Test
	public void testVerificationD4JChart1() {
		String diffId = "Chart_1";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		Assert.assertTrue(repairPatterns.getFeatureCounter("binOperatorModif") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("binOperatorModif");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("dataset == null"));

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("dataset != null"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "binOperatorModif", "NE", "BinaryOperator");

	}

	@Test
	public void testVerificationD4JChart3() {
		String diffId = "Chart_3";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		Assert.assertTrue(repairPatterns.getFeatureCounter("addassignment") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("addassignment");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("java.lang.Double.NaN"));

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("copy.data = new java.util.ArrayList()"));

		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		JSonTest.assertMarkedlAST(resultjson, "addassignment", "\u003d", "Assignment");

	}

	public static void showJSONFaultyAST(JsonObject resultjson) {

		System.out.println("****************");

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
			int size = ar.size();
			int i = 0;
			for (JsonElement suspiciousTree : ar) {

				System.out.println("***");

				JsonObject jso = suspiciousTree.getAsJsonObject();
				System.out.println("--> AST element: \n" + jso.get("pattern_name"));

				prettyJsonString = gson.toJson(jso.get("faulty_ast"));
				System.out.println("--faulty_ast:\n" + (++i) + "/" + size + ": " + prettyJsonString);

			}

		}

	}

	@Test
	public void testVerificationTest1205753() {
		String diffId = "1205753";

		String input = "/Users/matias/develop/code/git-gt-spoon-diff/coming/src/main/resources/testInsert2/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);
		JSonTest.assertMarkedlAST(resultjson, "addassignment", "if", "If");

	}

	@Test
	public void testVerificationTest1205753_insert_end() {
		String diffId = "1205753";

		String input = "/Users/matias/develop/code/git-gt-spoon-diff/coming/src/main/resources/testInsert3/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);
		JSonTest.assertMarkedlAST(resultjson, "addassignment", "if", "If");

	}

	@Test
	public void testVerificationICSE591061() {
		String diffId = "591061";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/icse2015/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);

	}

	@Test
	public void testVerificationICSE888066_METHOD() {
		String diffId = "888066";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/icse2015/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);

	}

	@Test
	public void testVerificationICSE1002329_METHOD() {
		String diffId = "1002329";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/icse2015/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);

	}

	@Test
	public void testVerificationICSE_1064371_METHOD() {
		String diffId = "1064371";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/icse2015/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);

	}

	@Test
	public void testVerificationICSE_1086957_METHOD() {
		// False positive
		String diffId = "1086957";

		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/icse2015/" + diffId;

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = JSonTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);

	}

}
