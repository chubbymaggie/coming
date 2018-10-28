package fr.inria.coming.core.engine.files;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import add.entities.RepairActions;
import add.entities.RepairPatterns;
import add.features.detector.EditScriptBasedDetector;
import add.features.detector.repairactions.RepairActionDetector;
import add.features.detector.repairpatterns.RepairPatternDetector;
import add.main.Config;
import fr.inria.astor.core.entities.CNTX_Property;
import fr.inria.astor.core.entities.Cntx;
import fr.inria.astor.core.entities.CntxResolver;
import fr.inria.astor.util.MapList;
import fr.inria.coming.main.ComingProperties;
import gumtree.spoon.builder.Json4SpoonGenerator;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.builder.jsonsupport.NodePainter;
import gumtree.spoon.builder.jsonsupport.OperationNodePainter;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.DiffImpl;
import gumtree.spoon.diff.operations.DeleteOperation;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.MoveOperation;
import gumtree.spoon.diff.operations.Operation;
import gumtree.spoon.diff.operations.UpdateOperation;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

/**
 * 
 * @author Matias Martinez
 *
 */
public class DiffICSE15ContextAnalyzer extends BugFixRunner {
	File out = null;

	public DiffICSE15ContextAnalyzer() {
		super();
		ComingProperties.properties.setProperty("maxdifftoanalyze", "5");

		out = new File("/Users/matias/develop/CodeRep-data/process_Dataset1_unidiff");
		out.mkdirs();
	}

	public DiffICSE15ContextAnalyzer(String outfile) {
		super();

		out = new File(outfile);
		out.mkdirs();
	}

	Map<String, Diff> diffOfcommit = new HashMap();

	@Override
	public void processDiff(File fileModif, Diff diff) {
		List<Operation> ops = diff.getRootOperations();
		String key = fileModif.getParentFile().getName() + "_" + fileModif.getName();
		this.diffOfcommit.put(key, diff);
	}

	@Override
	protected boolean acceptFile(File fileModif) {
		File f = new File(out.getAbsolutePath() + File.separator + fileModif.getName() + ".json");
		// If the json file does not exist, we process it
		return !f.exists();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void atEndCommit(File difffile) {
		try {

			JsonObject statsjsonRoot = getContextFuture(difffile.getName(), diffOfcommit);// calculateCntxJSON(difffile.getName(),
																							// diffOfcommit);

			FileWriter fw = new FileWriter(out.getAbsolutePath() + File.separator + difffile.getName() + ".json");

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String prettyJsonString = gson.toJson(statsjsonRoot);
			fw.write(prettyJsonString);

			fw.flush();
			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		diffOfcommit.clear();
	}

	/// -=-=--=-=-=-=--=

	private Future<JsonObject> getfutureContect(ExecutorService executorService, String id,
			Map<String, Diff> operations) {

		Future<JsonObject> future = executorService.submit(() -> {
			JsonObject statsjsonRoot = calculateCntxJSON(id, diffOfcommit);
			return statsjsonRoot;
		});
		return future;
	}

	public JsonObject getContextFuture(String id, Map<String, Diff> operations) throws Exception {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<JsonObject> future = getfutureContect(executorService, id, operations);

		JsonObject resukltDiff = null;
		try {
			resukltDiff = future.get(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) { // <-- possible error cases
			System.out.println("job was interrupted");
		} catch (ExecutionException e) {
			System.out.println("caught exception: " + e.getCause());
		} catch (TimeoutException e) {
			System.out.println("timeout context analyzed.");
		}

		executorService.shutdown();
		return resukltDiff;

	}

	/////// ---------=-=-=-=--=-=-=-

	////
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JsonObject calculateCntxJSONOLD(String id, Map<String, Diff> operations) {
		JsonObject statsjsonRoot = new JsonObject();
		statsjsonRoot.addProperty("diffid", id);
		JsonArray sublistJSon = new JsonArray();
		statsjsonRoot.add("operations", sublistJSon);
		JsonArray patternlistJSon = new JsonArray();
		statsjsonRoot.add("patterns", patternlistJSon);
		JsonArray repairActionslistJSon = new JsonArray();
		statsjsonRoot.add("repairactions", repairActionslistJSon);

		for (String modifiedFile : operations.keySet()) {

			Diff diff = operations.get(modifiedFile);
			List<Operation> ops = diff.getRootOperations();

			System.out.println("Diff file " + modifiedFile + " " + ops.size());
			for (Operation operation : ops) {
				CntxResolver cresolver = new CntxResolver();

				JsonObject opContext = new JsonObject();

				opContext.addProperty("bug", modifiedFile);

				opContext.addProperty("key", modifiedFile);
				Cntx iContext = cresolver.retrieveCntx(operation.getSrcNode());
				iContext.setIdentifier(modifiedFile);
				opContext.add("cntx", iContext.toJSON());

				setBuggyInformation(operation, cresolver, opContext, diff);

				setPatchInformation(operation, cresolver, opContext, diff);

				calculateJSONAffectedMethod(diff, operation, opContext);
				calculateJSONAffectedElement(diff, operation, opContext);
				sublistJSon.add(opContext);

			}

			// Patterns:

			JsonObject patternFile = new JsonObject();
			Config config = new Config();
			RepairPatternDetector detector = new RepairPatternDetector(config, diff);
			RepairPatterns rp = detector.analyze();

			JsonObject patterns = new JsonObject();
			for (String featureName : rp.getFeatureNames()) {
				int counter = rp.getFeatureCounter(featureName);
				patterns.addProperty(featureName, counter);
			}
			patternFile.add("repairpatterns", patterns);
			patternFile.addProperty("file", modifiedFile);
			patternlistJSon.add(patternFile);

			JsonObject repairActionFile = new JsonObject();
			JsonObject repairactions = new JsonObject();
			RepairActionDetector pa = new RepairActionDetector(config, diff);
			RepairActions as = pa.analyze();
			for (String featureName : as.getFeatureNames()) {
				int counter = as.getFeatureCounter(featureName);
				repairactions.addProperty(featureName, counter);
			}

			repairActionFile.add("repairactions", repairactions);
			repairActionFile.addProperty("file", modifiedFile);
			repairActionslistJSon.add(repairActionFile);
		}
		// System.out.println(statsjsonRoot);
		return statsjsonRoot;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JsonObject calculateCntxJSON(String id, Map<String, Diff> operations) {
		JsonObject statsjsonRoot = new JsonObject();
		statsjsonRoot.addProperty("diffid", id);
		// JsonArray sublistJSon = new JsonArray();
		// statsjsonRoot.add("info", sublistJSon);
		JsonArray patternlistJSon = new JsonArray();
		statsjsonRoot.add("patterns", patternlistJSon);
		JsonArray repairActionslistJSon = new JsonArray();
		statsjsonRoot.add("repairactions", repairActionslistJSon);

		JsonArray affectedlistJSon = new JsonArray();
		statsjsonRoot.add("affected", affectedlistJSon);

		for (String modifiedFile : operations.keySet()) {
			MapList<Operation, String> patternsPerOp = new MapList<>();
			MapList<Operation, String> repairactionPerOp = new MapList<>();

			Diff diff = operations.get(modifiedFile);
			List<Operation> operationsFromFile = diff.getRootOperations();

			System.out.println("Diff file " + modifiedFile + " " + operationsFromFile.size());

			// Patterns:

			JsonObject patternFile = new JsonObject();
			Config config = new Config();
			EditScriptBasedDetector.preprocessEditScript(diff);
			RepairPatternDetector detector = new RepairPatternDetector(config, diff);
			RepairPatterns rp = detector.analyze();

			JsonObject patterns = new JsonObject();
			for (String featureName : rp.getFeatureNames()) {
				int counter = rp.getFeatureCounter(featureName);
				patterns.addProperty(featureName, counter);
				JsonArray opsFeature = new JsonArray();

				List<Operation> opsfeature = rp.getOperationsPerFeature().get(featureName);
				if (opsfeature == null || opsfeature.isEmpty())
					continue;

				for (Operation operation : opsfeature) {

					patternsPerOp.add(operation, featureName);

				}
			}

			patternFile.add("repairpatterns", patterns);
			patternFile.addProperty("file", modifiedFile);
			patternlistJSon.add(patternFile);
			/// Repair actions
			JsonObject repairActionFile = new JsonObject();
			JsonObject repairactions = new JsonObject();
			RepairActionDetector pa = new RepairActionDetector(config, diff);
			RepairActions as = pa.analyze();

			for (String featureName : as.getFeatureNames()) {
				int counter = as.getFeatureCounter(featureName);
				repairactions.addProperty(featureName, counter);

				List<CtElement> el = as.getElementPerFeature().get(featureName);
				if (el != null && el.size() > 0) {
					for (Operation opi : diff.getAllOperations()) {
						if (el.contains(opi.getNode())) {
							repairactionPerOp.add(opi, featureName);
						}
					}
				}
			}

			repairActionFile.add("repairactions", repairactions);
			repairActionFile.addProperty("file", modifiedFile);
			repairActionslistJSon.add(repairActionFile);
			// End repair actions

			List<CtElement> analyzedelements = new ArrayList();
			JsonArray patternarray = new JsonArray();
			// patternFile.add("patterninstances", patternarray);
			affectedlistJSon.add(patternarray);
			for (Operation op : operationsFromFile) {
				if (!analyzedelements.contains(op.getNode())) {
					analyzedelements.add(op.getNode());

					//
					CntxResolver cresolver = new CntxResolver();

					JsonObject opContext = new JsonObject();

					opContext.addProperty("bug", modifiedFile);

					opContext.addProperty("key", modifiedFile);
					Cntx iContext = cresolver.retrieveCntx(op.getSrcNode());
					iContext.setIdentifier(modifiedFile);
					opContext.add("cntx", iContext.toJSON());

					setBuggyInformation(op, cresolver, opContext, diff);

					setPatchInformation(op, cresolver, opContext, diff);

					//
					JsonObject jo = calculateJSONAffectedStatement(diff, op, patternsPerOp, repairactionPerOp);
					opContext.add("ast", jo);

					patternarray.add(opContext);

					// if (jo != null) {
					// patternarray.add(jo);
					// }
				} else {
					// System.out.println("Alr");
				}
			}

		}
		// System.out.println("-->" + statsjsonRoot);

		// for each faulty statement, attach repair actions and pattern affected

		return statsjsonRoot;

	}

	/**
	 * CntxResolver cresolver = new CntxResolver();
	 * 
	 * JsonObject opContext = new JsonObject();
	 * 
	 * opContext.addProperty("bug", modifiedFile);
	 * 
	 * opContext.addProperty("key", modifiedFile); Cntx iContext =
	 * cresolver.retrieveCntx(operation.getSrcNode());
	 * iContext.setIdentifier(modifiedFile); opContext.add("cntx",
	 * iContext.toJSON());
	 * 
	 * setBuggyInformation(operation, cresolver, opContext, diff);
	 * 
	 * setPatchInformation(operation, cresolver, opContext, diff);
	 * calculateJSONAffectedMethod(diff, operation, opContext);
	 * calculateJSONAffectedElement(diff, operation, opContext);
	 * opsFeature.add(opContext);
	 */

	/**
	 * // let's find the destination in the Source Tree Move ma = (Move)
	 * operation.getAction(); ITree newParentDst = ma.getParent(); ITree
	 * mappedParentSrc = null; do { // ITree parentTree =
	 * operation.getAction().getNode().getParent(); mappedParentSrc =
	 * mappings.getSrc(newParentDst); newParentDst = newParentDst.getParent(); }
	 * while (mappedParentSrc == null && newParentDst != null);
	 * 
	 * @param operation
	 * @param cresolver
	 * @param opContext
	 * @param diff
	 */

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private void setBuggyInformation(Operation operation, CntxResolver cresolver, JsonObject opContext, Diff diff) {

		Cntx bugContext = new Cntx<>();
		if (operation instanceof MoveOperation) {

			MoveOperation movop = (MoveOperation) operation;
			// Element to move in source
			CtElement affectedMoved = operation.getSrcNode();
			MappingStore mappings = diff.getMappingsComp();

			bugContext.getInformation().put(CNTX_Property.OPERATION, "MV");

			bugContext.getInformation().put(CNTX_Property.AFFECTED, cresolver.retrieveBuggyInfo(affectedMoved));

			List lp = new ArrayList();
			CtElement oldParentLocationInsertStmt = getStmtParent(affectedMoved);

			lp.add(cresolver.retrieveBuggyInfo(oldParentLocationInsertStmt));
			CtElement newParentLocationInsertStmt = getStmtParent(movop.getParent());
			if (oldParentLocationInsertStmt != newParentLocationInsertStmt) {

				lp.add(cresolver.retrieveBuggyInfo(newParentLocationInsertStmt));

			}

			bugContext.getInformation().put(CNTX_Property.AFFECTED_PARENT, lp);

		} else if (operation instanceof InsertOperation)

		{

			CtElement oldLocation = ((InsertOperation) operation).getParent();
			CtElement oldParentLocationInsertStmt = getStmtParent(oldLocation);

			bugContext.getInformation().put(CNTX_Property.AFFECTED, null);
			bugContext.getInformation().put(CNTX_Property.AFFECTED_PARENT,
					cresolver.retrieveBuggyInfo(oldParentLocationInsertStmt));
			bugContext.getInformation().put(CNTX_Property.OPERATION, "INS");

		} else if (operation instanceof DeleteOperation) {

			DeleteOperation up = (DeleteOperation) operation;
			CtElement oldLocation = operation.getSrcNode();
			CtElement oldParentLocationInsertStmt = getStmtParent(oldLocation);
			bugContext.getInformation().put(CNTX_Property.AFFECTED, cresolver.retrieveBuggyInfo(oldLocation));
			bugContext.getInformation().put(CNTX_Property.AFFECTED_PARENT,
					cresolver.retrieveBuggyInfo(oldParentLocationInsertStmt));
			bugContext.getInformation().put(CNTX_Property.OPERATION, "DEL");

		} else if (operation instanceof UpdateOperation) {

			UpdateOperation up = (UpdateOperation) operation;
			CtElement oldLocation = operation.getSrcNode();
			CtElement oldParentLocationInsertStmt = getStmtParent(oldLocation);
			bugContext.getInformation().put(CNTX_Property.AFFECTED, cresolver.retrieveBuggyInfo(oldLocation));
			bugContext.getInformation().put(CNTX_Property.AFFECTED_PARENT,
					cresolver.retrieveBuggyInfo(oldParentLocationInsertStmt));
			bugContext.getInformation().put(CNTX_Property.OPERATION, "UPD");
		}

		if (bugContext != null)
			opContext.add("bug", bugContext.toJSON());
		else
			System.out.println("Operation not known: " + operation.getClass().getSimpleName());

	}

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private void setPatchInformation(Operation operation, CntxResolver cresolver, JsonObject opContext, Diff diff) {

		Cntx bugContext = new Cntx<>();
		MappingStore mappings = diff.getMappingsComp();
		if (operation instanceof MoveOperation) {

			// Element to move in source
			CtElement affectedMoved = operation.getSrcNode();
			bugContext.getInformation().put(CNTX_Property.OPERATION, "MV");
			// Find the parent

			// let's find the destination in the Source Tree
			Move ma = (Move) operation.getAction();
			// This parent is from the dst
			ITree newParentSRC = ma.getParent();

			bugContext.getInformation().put(CNTX_Property.AFFECTED, cresolver.retrieveBuggyInfo(affectedMoved));

			// Parent in DST were we put

			List lp = new ArrayList();
			bugContext.getInformation().put(CNTX_Property.AFFECTED_PARENT, lp);

			CtElement parentMovedElementInDst = searchMapped(mappings, newParentSRC);
			CtElement newLocationInDst = null;
			if (parentMovedElementInDst != null) {
				newLocationInDst = getStmtParent(parentMovedElementInDst);
				lp.add(cresolver.retrieveBuggyInfo(newLocationInDst));
			}

			// parent in dst were it was.

			CtElement oldParentInDst = searchMapped(mappings, ma.getNode().getParent());
			if (oldParentInDst != null) {
				CtElement oldParentLocationInsertStmt = getStmtParent(oldParentInDst);
				if (oldParentLocationInsertStmt != newLocationInDst)
					lp.add(cresolver.retrieveBuggyInfo(oldParentLocationInsertStmt));
			}

		} else if (operation instanceof InsertOperation)

		{
			InsertOperation op = (InsertOperation) operation;
			CtElement newLocation = op.getSrcNode();
			CtElement newParentLocationInsertStmt = getStmtParent(newLocation);

			bugContext.getInformation().put(CNTX_Property.AFFECTED, cresolver.retrieveBuggyInfo(newLocation));
			bugContext.getInformation().put(CNTX_Property.AFFECTED_PARENT,
					cresolver.retrieveBuggyInfo(newParentLocationInsertStmt));
			bugContext.getInformation().put(CNTX_Property.OPERATION, "INS");
		} else if (operation instanceof DeleteOperation) {

			DeleteOperation up = (DeleteOperation) operation;

			ITree newParentDst = up.getAction().getNode().getParent();
			ITree mappedParentDst = null;
			do {
				mappedParentDst = mappings.getDst(newParentDst);
				newParentDst = newParentDst.getParent();
			} while (mappedParentDst == null && newParentDst != null);

			if (mappedParentDst != null) {

				CtElement parentDstInDst = (CtElement) mappedParentDst.getMetadata("spoon_object");

				CtElement oldParentLocationInsertStmt = getStmtParent(parentDstInDst);
				bugContext.getInformation().put(CNTX_Property.AFFECTED, null);
				bugContext.getInformation().put(CNTX_Property.AFFECTED_PARENT,
						cresolver.retrieveBuggyInfo(oldParentLocationInsertStmt));
				bugContext.getInformation().put(CNTX_Property.OPERATION, "DEL");
			}

		} else if (operation instanceof UpdateOperation) {

			UpdateOperation up = (UpdateOperation) operation;
			CtElement oldLocation = operation.getDstNode();
			CtElement oldParentLocationInsertStmt = getStmtParent(oldLocation);

			bugContext.getInformation().put(CNTX_Property.OPERATION, "UPD");
			bugContext.getInformation().put(CNTX_Property.AFFECTED, cresolver.retrieveBuggyInfo(oldLocation));
			bugContext.getInformation().put(CNTX_Property.AFFECTED_PARENT,
					cresolver.retrieveBuggyInfo(oldParentLocationInsertStmt));
		}

		if (bugContext != null)
			opContext.add("patch", bugContext.toJSON());
		else
			System.out.println("Operation not known: " + operation.getClass().getSimpleName());

	}

	private CtElement searchMapped(MappingStore mappings, ITree newParentSRC) {
		ITree mappedParentDst = null;
		do {
			mappedParentDst = mappings.getDst(newParentSRC);
			newParentSRC = newParentSRC.getParent();
		} while (mappedParentDst == null && newParentSRC != null);

		if (mappedParentDst == null)
			return null;

		CtElement parentMovedElementInDst = (CtElement) mappedParentDst.getMetadata("spoon_object");
		return parentMovedElementInDst;
	}

	private CtElement getStmtParent(CtElement element) {
		if (element instanceof CtField)
			return element;

		CtElement parent = element.getParent(CtStatement.class);
		if (parent == null)
			parent = element.getParent(CtMethod.class);
		else {
			// Workarround case of X = new X();
			if (parent.getParent() instanceof CtStatement && !(parent.getParent() instanceof CtBlock))
				return getStmtParent(element.getParent());// parent.getParent();
			else {
				return parent;
			}
		}

		return element.getParent();
	}

	private void calculateJSONAffectedMethod(Diff diff, Operation operation, JsonObject opContext) {

		CtMethod methodOfOperation = operation.getNode().getParent(CtMethod.class);
		Json4SpoonGenerator jsongen = new Json4SpoonGenerator();

		Action affectedAction = operation.getAction();
		ITree affected = affectedAction.getNode();
		// jsongen.getJSONasJsonObject(

		ITree methodTreeNode = null;
		do {
			CtElement relatedCtElement = (CtElement) affected.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
			if (relatedCtElement instanceof CtExecutable) { // check if its th buggy method
				// if (methodOfOperation == relatedCtElement) {// same object
				methodTreeNode = affected;
			}
			affected = affected.getParent();
		} while (methodTreeNode == null && affected.getParent() != null);
		//
		if (methodTreeNode != null) {
			JsonObject jsonT = jsongen.getJSONwithOperations(((DiffImpl) diff).getContext(), methodTreeNode,
					diff.getAllOperations());

			opContext.add(CNTX_Property.AST_PARENT.toString(), jsonT);

		}

	}

	public JsonObject calculateJSONAffectedStatement(Diff diff, Operation operation,
			MapList<Operation, String> patternsPerOp, MapList<Operation, String> repairactionPerOp) {

		CtMethod methodOfOperation = operation.getNode().getParent(CtMethod.class);
		Json4SpoonGenerator jsongen = new Json4SpoonGenerator();

		Action affectedAction = operation.getAction();
		ITree affected = affectedAction.getNode();

		ITree targetTreeNode = null;
		do {
			CtElement relatedCtElement = (CtElement) affected.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

			if (relatedCtElement instanceof CtExecutable || relatedCtElement instanceof CtStatement
					|| relatedCtElement instanceof CtField) {
				targetTreeNode = affected;
			}
			affected = affected.getParent();
		} while (targetTreeNode == null && affected.getParent() != null);

		List<NodePainter> painters = new ArrayList();
		painters.add(new PatternPainter(patternsPerOp, "patterns"));
		painters.add(new PatternPainter(repairactionPerOp, "repairactions"));
		painters.add(new OperationNodePainter(diff.getAllOperations()));

		if (targetTreeNode != null) {
			JsonObject jsonT = jsongen.getJSONwithCustorLabels(((DiffImpl) diff).getContext(), targetTreeNode,
					painters);
			return jsonT;
		}
		return null;
	}

	static List emptyList = new ArrayList();

	private void calculateJSONAffectedElement(Diff diff, Operation operation, JsonObject opContext) {

		operation.getNode();
		Json4SpoonGenerator jsongen = new Json4SpoonGenerator();

		JsonObject jsonT = jsongen.getJSONwithOperations(((DiffImpl) diff).getContext(),
				operation.getAction().getNode(), emptyList);
		opContext.add(CNTX_Property.AST.toString(), jsonT);

	}

	public Map<String, Diff> getDiffOfcommit() {
		return diffOfcommit;
	}

	public void setDiffOfcommit(Map<String, Diff> diffOfcommit) {
		this.diffOfcommit = diffOfcommit;
	}

}
