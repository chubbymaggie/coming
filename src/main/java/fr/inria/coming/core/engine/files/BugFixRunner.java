package fr.inria.coming.core.engine.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Ignore;

import fr.inria.coming.changeminer.analyzer.DiffEngineFacade;
import fr.inria.coming.changeminer.analyzer.commitAnalyzer.FineGrainDifftAnalyzer;
import fr.inria.coming.main.ComingProperties;
import fr.inria.coming.utils.MapCounter;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;

/**
 * 
 * @author Matias Martinez
 *
 */
public class BugFixRunner {
	private Logger log = Logger.getLogger(DiffEngineFacade.class.getName());
	int error = 0;
	int zero = 0;
	int withactions = 0;

	@SuppressWarnings("unchecked")
	public void run(String path) throws Exception {

		error = 0;
		zero = 0;
		withactions = 0;
		MapCounter<String> counter = new MapCounter<>();
		MapCounter<String> counterParent = new MapCounter<>();
		JSONObject root = new JSONObject();
		JSONArray firstArray = new JSONArray();
		root.put("diffs", firstArray);

		File dir = new File(path);

		beforeStart();

		int diffanalyzed = 0;
		for (File difffile : dir.listFiles()) {

			if (difffile.isFile() || difffile.listFiles() == null)
				continue;

			diffanalyzed++;

			if (diffanalyzed % 100 == 0) {
				// System.out.println(diffanalyzed + "/" + dir.listFiles().length);
			}

			log.debug("-commit->" + difffile);
			System.out.println(diffanalyzed + "/" + dir.listFiles().length + ": " + difffile.getName());

			if (!acceptFile(difffile)) {
				System.out.println("existing json for: " + difffile.getName());
				continue;
			}

			JSONObject jsondiff = new JSONObject();
			firstArray.add(jsondiff);
			jsondiff.put("diffid", difffile.getName());

			JSONArray filesArray = processDiff(counter, counterParent, difffile);

			jsondiff.put("files", filesArray);
			atEndCommit(difffile);

			if (diffanalyzed == ComingProperties.getPropertyInteger("maxdifftoanalyze")) {
				System.out.println("max-break");
				break;
			}
		}

		Map sorted = counter.sorted();
		System.out.println("\n***\nSorted:" + sorted);
		///

		addStats(root, "frequency", sorted);
		addStats(root, "frequencyParent", counterParent.sorted());
		Map prob = counter.getProbabilies();
		addStats(root, "probability", prob);
		Map probParent = counterParent.getProbabilies();
		addStats(root, "probabilityParent", probParent);

		root.put("diffwithactions", withactions);
		root.put("diffzeroactions", zero);
		root.put("differrors", error);

		System.out.println("\n***\nProb: " + counter.getProbabilies());
		System.out.println("Withactions " + withactions);
		System.out.println("Zero " + zero);
		System.out.println("Error " + error);

		// System.out.println("JSON: \n" + root);
		// FileWriter fw = new FileWriter("./outputanalysis" + (new Date()).toString() +
		// ".json");
		// fw.write(root.toJSONString());
		// fw.flush();
		// fw.close();

		beforeEnd();
	}

	@SuppressWarnings("unchecked")
	public JSONArray processDiff(MapCounter<String> counter, MapCounter<String> counterParent, File difffile) {
		JSONArray filesArray = new JSONArray();
		FineGrainDifftAnalyzer analyzer = new FineGrainDifftAnalyzer();
		for (File fileModif : difffile.listFiles()) {
			int i_hunk = 0;

			if (".DS_Store".equals(fileModif.getName()))
				continue;

			String pathname = fileModif.getAbsolutePath() + File.separator + difffile.getName() + "_"
					+ fileModif.getName() + "_" + i_hunk;
			File previousVersion = new File(pathname + "_s.java");
			if (!previousVersion.exists()) {
				break;
			}

			JSONObject file = new JSONObject();
			filesArray.add(file);
			file.put("name", fileModif.getName());
			JSONArray changesArray = new JSONArray();
			file.put("changes", changesArray);

			File postVersion = new File(pathname + "_t.java");
			i_hunk++;
			try {

				Diff diff = analyzer.getdiffFuture(previousVersion, postVersion);
				if (diff == null) {
					file.put("status", "differror");
					continue;
				}

				JSONObject singlediff = new JSONObject();
				changesArray.add(singlediff);
				// singlediff.put("filename", fileModif.getName());
				singlediff.put("rootop", diff.getRootOperations().size());
				JSONArray operationsArray = new JSONArray();

				singlediff.put("operations", operationsArray);
				singlediff.put("allop", diff.getAllOperations().size());

				processDiff(fileModif, diff);

				if (diff.getAllOperations().size() > 0) {

					withactions++;
					log.debug("-file->" + fileModif + " actions " + diff.getRootOperations().size());
					for (Operation operation : diff.getRootOperations()) {

						log.debug("-op->" + operation);
						counter.add(operation.getNode().getClass().getSimpleName());
						counterParent.add(
								operation.getAction().getName() + "_" + operation.getNode().getClass().getSimpleName()
										+ "_" + operation.getNode().getParent().getClass().getSimpleName());

						JSONObject op = getJSONFromOperator(operation);

						operationsArray.add(op);
					}

				} else {
					zero++;
					log.debug("-file->" + fileModif + " zero actions ");
				}
				file.put("status", "ok");
			} catch (Throwable e) {
				System.out.println("error with " + previousVersion);
				e.printStackTrace();
				error++;
				file.put("status", "exception");
			}

		}
		return filesArray;
	}

	protected boolean acceptFile(File fileModif) {
		// By default, it processes the file
		return true;
	}

	@SuppressWarnings("unchecked")
	protected JSONObject getJSONFromOperator(Operation operation) {
		JSONObject op = new JSONObject();
		op.put("operator", operation.getAction().getName());
		op.put("src", (operation.getSrcNode() != null) ? operation.getSrcNode().getClass().getSimpleName() : "null");
		op.put("dst", (operation.getDstNode() != null) ? operation.getDstNode().getParent().getClass().getSimpleName()
				: "null");

		op.put("srcparent",
				(operation.getSrcNode() != null) ? operation.getSrcNode().getClass().getSimpleName() : "null");
		op.put("dstparent",
				(operation.getDstNode() != null) ? operation.getDstNode().getParent().getClass().getSimpleName()
						: "null");
		return op;
	}

	public void atEndCommit(File difffile) {
		// TODO Auto-generated method stub

	}

	public void beforeEnd() {
		// Do nothing
	}

	public void beforeStart() {
		// Do nothing
	}

	public void processDiff(File fileModif, Diff diff) {
		// Do nothing

	}

	private void addStats(JSONObject root, String key1, Map sorted) {
		JSONArray frequencyArray = new JSONArray();
		root.put(key1, frequencyArray);
		for (Object key : sorted.keySet()) {
			Object v = sorted.get(key);
			JSONObject singlediff = new JSONObject();
			singlediff.put("c", key);
			singlediff.put("f", v);
			frequencyArray.add(singlediff);
		}
	}

	// Buggy Array exception
	@Ignore
	public String read(File file) {
		String s = "";
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				s += (line);

			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("One arg: folder path");
		}
		String path = args[0];
		BugFixRunner runner = new BugFixRunner();
		runner.run(path);
	}

}
