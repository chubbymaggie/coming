package fr.inria.coming.spoon.diffanalyzer;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.google.gson.JsonArray;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.coming.core.engine.files.DiffICSE15ContextAnalyzer;
import fr.inria.coming.core.engine.files.MapCounter;
import fr.inria.coming.main.ComingProperties;

//@Ignore
public class DiffICSE2015Test {

	@Test
	public void testICSE2015() throws Exception {
		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();
		analyzer.run(ComingProperties.getProperty("icse15difffolder"));
	}

	@Test
	public void testICSE15() throws Exception {
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ComingProperties.properties.setProperty("max_synthesis_step", "100000");
		ComingProperties.properties.setProperty("MAX_AST_CHANGES_PER_FILE", "20");
		String out = "/Users/matias/develop/CodeRep-data/processed_ICSE2015_unidiff";
		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer(out);
		String input = "/Users/matias/develop/sketch-repair/outputdiff4/";
		ComingProperties.properties.setProperty("icse15difffolder", input);
		analyzer.run(ComingProperties.getProperty("icse15difffolder"));

	}

	@Test
	public void testD4J() throws Exception {
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ComingProperties.properties.setProperty("max_synthesis_step", "100000");
		ComingProperties.properties.setProperty("MAX_AST_CHANGES_PER_FILE", "200");
		String out = "/Users/matias/develop/sketch-repair/git-sketch4repair/diff_analysis/Defects4J";
		// +"//"/Users/matias/develop/CodeRep-data/processed_d4J/";
		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer(out);
		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/";
		ComingProperties.properties.setProperty("icse15difffolder", input);
		analyzer.run(ComingProperties.getProperty("icse15difffolder"));
	}

	@Test
	public void testCODEREP() throws Exception {
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ComingProperties.properties.setProperty("max_synthesis_step", "100000");
		for (int i = 1; i <= 1; i++) {
			String out = "/Users/matias/develop/CodeRep-data/process_Dataset" + i + "_unidiff";
			DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer(out);
			String input = "/Users/matias/develop/CodeRep-data/result_Dataset" + i + "_unidiff/";
			ComingProperties.properties.setProperty("icse15difffolder", input);
			analyzer.run(ComingProperties.getProperty("icse15difffolder"));
		}
	}

	@Test
	public void testFailingTChart_21() throws Exception {
		String diffId = "Chart_21";

		String out = "/Users/matias/develop/CodeRep-data/processed_d4J/";
		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer(out);
		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/";
		ComingProperties.properties.setProperty("icse15difffolder", input);

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_1555_Move() throws Exception {
		String diffId = "1555";
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ComingProperties.properties.setProperty("max_synthesis_step", "100000");

		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailing_MaxNodes_966027() throws Exception {
		String diffId = "966027";
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ComingProperties.properties.setProperty("max_synthesis_step", "100000");

		String out = "/Users/matias/develop/CodeRep-data/processed_ICSE2015" + "_unidiff";
		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer(out);

		File fileDiff = new File("/Users/matias/develop/sketch-repair/outputdiff4/" + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_3168() throws Exception {
		String diffId = "3168";

		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_1792() throws Exception {
		String diffId = "1792";

		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_95() throws Exception {
		String diffId = "95";

		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_909() throws Exception {
		String diffId = "909";

		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_2150() throws Exception {
		String diffId = "2150";

		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_2954() throws Exception {
		String diffId = "2954";

		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_1806() throws Exception {
		String diffId = "1806";

		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_4185() throws Exception {
		String diffId = "4185";

		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_584756() throws Exception {
		String diffId = "584756";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testFailingTimeoutCase_1421510() throws Exception {

		String diffId = "1421510";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testFailingTimeoutCase_613948() throws Exception {

		String diffId = "613948";

		runAndAssertSingleDiff(diffId);
	}

//Diff file 4185_TestTypePromotion 3
	@Test
	public void testFailingTimeoutCase_1305909() throws Exception {

		String diffId = "1305909";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testFailingTimeoutCase_985877() throws Exception {

		String diffId = "985877";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testFailingTimeoutCase_932564() throws Exception {

		String diffId = "932564";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testFailingCase_1103681() throws Exception {
		// To see

		String diffId = "1103681";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testNoChangesCaseCase_1329010() throws Exception {

		String diffId = "1329010";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testChangesCaseCase_1185675() throws Exception {

		String diffId = "1185675";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testNoChangesCase_1381711() throws Exception {

		String diffId = "1381711";

		runAndAssertSingleDiff(diffId);
	}

	public void runAndAssertSingleDiff(String case1421510) {
		DiffICSE15ContextAnalyzer analyzer = new DiffICSE15ContextAnalyzer();

		File fileDiff = new File(ComingProperties.getProperty("icse15difffolder") + "/" + case1421510);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);
		System.out.println(arrayout);
	}

}
