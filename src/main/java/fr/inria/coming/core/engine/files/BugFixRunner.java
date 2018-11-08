package fr.inria.coming.core.engine.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.junit.Ignore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fr.inria.coming.changeminer.analyzer.DiffEngineFacade;
import fr.inria.coming.changeminer.entity.GranuralityType;
import fr.inria.coming.main.ComingProperties;
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
		JsonObject root = new JsonObject();
		JsonArray firstArray = new JsonArray();
		root.add("diffs", firstArray);

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

			if (difffile.getName().equals("948316") || "995086".equals(difffile.getName())
					|| "2194".equals(difffile.getName()) || "4094".equals(difffile.getName())
					|| "2954".equals(difffile.getName()) || "2150".equals(difffile.getName()))
				continue;
			log.debug("-commit->" + difffile);
			System.out.println(diffanalyzed + "/" + dir.listFiles().length + ": " + difffile.getName());

			if (!acceptFile(difffile)) {
				System.out.println("existing json for: " + difffile.getName());
				continue;
			}

			JsonObject jsondiff = new JsonObject();
			firstArray.add(jsondiff);
			jsondiff.addProperty("diffid", difffile.getName());

			JsonArray filesArray = processDiff(counter, counterParent, difffile);

			jsondiff.add("files", filesArray);
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

		root.addProperty("diffwithactions", withactions);
		root.addProperty("diffzeroactions", zero);
		root.addProperty("differrors", error);

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
	public JsonArray processDiff(MapCounter<String> counter, MapCounter<String> counterParent, File difffile) {
		JsonArray filesArray = new JsonArray();
		for (File fileModif : difffile.listFiles()) {
			int i_hunk = 0;

			if (".DS_Store".equals(fileModif.getName()))
				continue;

			String pathname = fileModif.getAbsolutePath() + File.separator + difffile.getName() + "_"
					+ fileModif.getName(); // + "_" + i_hunk;
			File previousVersion = new File(pathname + "_s.java");
			if (!previousVersion.exists()) {
				pathname = pathname + "_" + i_hunk;
				previousVersion = new File(pathname + "_s.java");
				if (!previousVersion.exists())
					break;
			}

			JsonObject file = new JsonObject();
			filesArray.add(file);
			file.addProperty("name", fileModif.getName());
			JsonArray changesArray = new JsonArray();
			file.add("changes", changesArray);

			File postVersion = new File(pathname + "_t.java");
			i_hunk++;
			try {

				Diff diff = getdiffFuture(previousVersion, postVersion);
				if (diff == null) {
					file.addProperty("status", "differror");
					continue;
				}
				Integer maxASTChanges = ComingProperties.getPropertyInteger("MAX_AST_CHANGES_PER_FILE");
				if (diff.getRootOperations().size() > maxASTChanges) {
					file.addProperty("status", "max_changes_" + maxASTChanges);
					continue;
				}

				if (diff.getRootOperations().size() == 0) {
					file.addProperty("status", "no_change");
					continue;
				}

				JsonObject singlediff = new JsonObject();
				changesArray.add(singlediff);
				// singlediff.put("filename", fileModif.getName());
				singlediff.addProperty("rootop", diff.getRootOperations().size());
				JsonArray operationsArray = new JsonArray();

				singlediff.add("operations", operationsArray);
				singlediff.addProperty("allop", diff.getAllOperations().size());

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

						JsonObject op = getJSONFromOperator(operation);

						operationsArray.add(op);
					}

				} else {
					zero++;
					log.debug("-file->" + fileModif + " zero actions ");
				}
				file.addProperty("status", "ok");
			} catch (Throwable e) {
				System.out.println("error with " + previousVersion);
				e.printStackTrace();
				error++;
				file.addProperty("status", "exception");
			}

		}
		return filesArray;
	}

	protected boolean acceptFile(File fileModif) {
		// By default, it processes the file
		return true;
	}

	@SuppressWarnings("unchecked")
	protected JsonObject getJSONFromOperator(Operation operation) {
		JsonObject op = new JsonObject();
		op.addProperty("operator", operation.getAction().getName());
		op.addProperty("src",
				(operation.getSrcNode() != null) ? operation.getSrcNode().getClass().getSimpleName() : "null");
		op.addProperty("dst",
				(operation.getDstNode() != null) ? operation.getDstNode().getParent().getClass().getSimpleName()
						: "null");

		op.addProperty("srcparent",
				(operation.getSrcNode() != null) ? operation.getSrcNode().getClass().getSimpleName() : "null");
		op.addProperty("dstparent",
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

	public Diff getdiff(File left, File right) throws Exception {

		DiffEngineFacade cdiff = new DiffEngineFacade();
		// Diff d = cdiff.compareContent(left, right, GranuralityType.SPOON);
		Diff d = cdiff.compareFiles(left, right, GranuralityType.SPOON);
		// System.out.println("-->" + d.getAllOperations().size());
		return d;
	}

	private void addStats(JsonObject root, String key1, Map sorted) {
		JsonArray frequencyArray = new JsonArray();
		root.add(key1, frequencyArray);
		for (Object key : sorted.keySet()) {
			Object v = sorted.get(key);
			JsonObject singlediff = new JsonObject();
			singlediff.addProperty("c", key.toString());
			singlediff.addProperty("f", v.toString());
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

	private Future<Diff> getfutureResult(ExecutorService executorService, File left, File right) {

		Future<Diff> future = executorService.submit(() -> {
			DiffEngineFacade cdiff = new DiffEngineFacade();
			// Diff d = cdiff.compareContent(left, right, GranuralityType.SPOON);
			Diff d = cdiff.compareFiles(left, right, GranuralityType.SPOON);
			// System.out.println("-->" + d.getAllOperations().size());
			return d;
		});
		return future;
	}

	public Diff getdiffFuture(File left, File right) throws Exception {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Diff> future = getfutureResult(executorService, left, right);

		Diff resukltDiff = null;
		try {
			resukltDiff = future.get(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) { // <-- possible error cases
			System.out.println("job was interrupted");
		} catch (ExecutionException e) {
			System.out.println("caught exception: " + e.getCause());
		} catch (TimeoutException e) {
			System.out.println("timeout");
		}

		executorService.shutdown();
		return resukltDiff;

	}
}
