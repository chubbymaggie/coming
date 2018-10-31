package fr.inria.coming.core.engine.files;

import java.util.List;

import com.github.gumtreediff.tree.ITree;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import add.entities.PatternInstance;
import fr.inria.astor.util.MapList;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.builder.jsonsupport.NodePainter;
import spoon.reflect.declaration.CtElement;

public class FaultyElementPatternPainter implements NodePainter {

	MapList<CtElement, String> nodesAffectedByPattern = new MapList<>();
	String label = "susp";

	public FaultyElementPatternPainter(List<PatternInstance> instances) {
		// Collect all nodes and get the operator

		for (PatternInstance patternInstance : instances) {
			for (CtElement susp : patternInstance.getFaulty()) {
				nodesAffectedByPattern.add(susp, "susp_" + patternInstance.getPatternName());
			}
		}

	}

	@Override
	public void paint(ITree tree, JsonObject jsontree) {

		CtElement ctelement = (CtElement) tree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

		boolean found = paint(jsontree, ctelement);

		if (!found) {
			CtElement ctelementdsr = (CtElement) tree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT_DEST);
			if (ctelementdsr != null)
				paint(jsontree, ctelementdsr);
		}
	}

	private boolean paint(JsonObject jsontree, CtElement ctelement) {
		boolean found = false;
		if (nodesAffectedByPattern.containsKey(ctelement)) {

			JsonArray arr = new JsonArray();
			List<String> ps = nodesAffectedByPattern.get(ctelement);
			for (String p : ps) {
				JsonPrimitive prim = new JsonPrimitive(p);
				arr.add(prim);
			}
			jsontree.add(this.label, arr);
			found = true;
		}
		return found;
	}

}
