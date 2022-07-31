package swag.md_elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.analysis_situations.IMeasure;
import swag.graph.Path;
import swag.helpers.AutoCompleteData;

public class MDSchemaGraphSMD implements MDSchema {

	private String name;
	private String endpoint;
	private String uri;
	private String nameSpace;
	private String preferredLabelProperty;
	private MDSchemaType type;

	public Set<Dimension> getDimensionsOfLevel(String leveName) {

		Set<Dimension> dims = new HashSet<>();

		MDElement level = this.getNode(leveName);
		if (level != null && level instanceof Level) {
			Set<MDRelation> rels = getEdgesOfNode(level);
			if (!rels.isEmpty()) {
				for (MDRelation rel : rels) {
					if (rel instanceof QB4OInHierarchy) {
						if (rel.getTarget() != null) {
							Set<MDRelation> rels1 = getEdgesOfNode(rel.getTarget());

							for (MDRelation relation : rels1) {
								if (relation.getTo() != null && relation instanceof QB4OHierarchyInDimension
										&& relation.getTo() instanceof Dimension) {
									dims.add((Dimension) relation.getTo());
								}
							}
						}
					}
				}
			}
		}
		return dims;
	}

	public List<Level> getPath(String startLevel, String endLevel, String dimension) {

		List<Level> path = new LinkedList<>();

		for (MDElement elem : this.getMdGraphMap().keySet()) {
			if (elem.getURI().equals(endLevel) && elem instanceof Level && getDimensionsOfLevel(endLevel).stream()
					.map(d -> d.getURI()).collect(Collectors.toSet()).contains(dimension)) {
				path.add((Level) elem);
				for (MDRelation rel : getInEdgesOfNode(elem)) {
					if (rel instanceof QB4OHierarchyStep) {
						path.add((Level) rel.getSource());
						path.addAll(getPath(rel.getSource().getURI(), endLevel, dimension));
					}
				}
			}
		}
		return path;
	}

	@Override
	public List<MDElement> getPath(List<MDElement> path, String startLevel, String endLevel, String dimension) {
		return null;
	}

	public MDRelation getRollUpOrHasAttributeProperty(String l1, String d1, String l2, String d2) {

		for (MDElement elem1 : getAllElemsWithUri(l1)) {
			if (elem1 instanceof Level) {
				if (deosOutEdgesTargetsContainElem(getOutEdgesOfNode(elem1), d1)) {
					for (MDElement elem2 : getAllElemsWithUri(l2)) {
						if (elem1 instanceof Level) {
							if (deosOutEdgesTargetsContainElem(getOutEdgesOfNode(elem2), d2)) {
								for (MDRelation rel : getMdGraphMap().values().stream().flatMap(val -> val.stream())
										.collect(Collectors.toSet())) {
									if (rel instanceof QB4OHierarchyStep && rel.getSource().getURI().equals(l1)
											&& rel.getTarget().getURI().equals(l2)) {
										return rel;
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public Level getLevelOfDescriptor1(String descriptorURI) {
		return null;
	}

	public boolean deosOutEdgesTargetsContainElem(Set<MDRelation> rels, String elm) {

		for (MDRelation rel : rels) {
			if (rel.getTarget().getURI().equals(elm)) {
				return true;
			}
		}
		return false;
	}

	public Set<MDRelation> getOutEdgesOfNode(MDElement e) {
		Set<MDRelation> rels = new HashSet<>();

		for (MDRelation rel : this.getAllEdges()) {
			if (rel.getSource().getURI().equals(e.getURI())) {
				rels.add(rel);
			}
		}
		return rels;
	}

	public Set<MDRelation> getInEdgesOfNode(MDElement e) {
		Set<MDRelation> rels = new HashSet<>();

		for (MDRelation rel : this.getAllEdges()) {
			if (rel.getTarget().getURI().equals(e.getURI())) {
				rels.add(rel);
			}
		}
		return rels;
	}

	public Set<MDElement> getAllElemsWithUri(String uri) {
		Set<MDElement> elems = new HashSet<>();

		for (MDElement elem : getMdGraphMap().keySet()) {
			if (elem.getURI().equals(uri)) {
				elems.add(elem);
			}
		}
		return elems;
	}

	public Set<Dimension> getDimensionsOfLevel(Level l) {

		Set<Dimension> dimensions = new HashSet<>();

		Set<Level> levels = getLevelByNonUNiqueUri(l.getURI());

		for (Level level : levels) {
			if (level != null && level instanceof Level) {
				Set<MDRelation> rels = getOutEdgesOfNode(level);

				for (MDRelation rel : rels) {

					if (rel instanceof InDimension) {

						if (rel.getTo() != null && rel.getTo() instanceof Dimension) {
							dimensions.add((Dimension) rel.getTo());
						}
					}
				}
			}
		}

		return dimensions;
	}

	private static final Logger logger = Logger.getLogger(MDSchemaGraphSMD.class);

	private Map<MDElement, Set<MDRelation>> mdGraphMap;

	public Map<MDElement, Set<MDRelation>> getMdGraphMap() {
		return mdGraphMap;
	}

	public void setMdGraphMap(Map<MDElement, Set<MDRelation>> mdGraphMap) {
		this.mdGraphMap = mdGraphMap;
	}

	public boolean addEntry(MDElement elem, Set<MDRelation> rels) {

		if (elem != null && rels != null) {
			if (!mdGraphMap.containsKey(elem)) {
				mdGraphMap.put(elem, rels);
				return true;
			}
		}
		return false;
	}

	public boolean removeEntry(MDElement elem) {
		if (elem != null && mdGraphMap.containsKey(elem)) {
			mdGraphMap.remove(elem);
			return true;
		}
		return false;
	}

	public Map<MDElement, Set<MDRelation>> copyEntry(MDElement elem) {

		Map<MDElement, Set<MDRelation>> copy = new HashMap<>();
		Set<MDRelation> relsOfElem = getEdgesOfNode(elem) != null ? new HashSet<>(getEdgesOfNode(elem)) : null;

		Set<MDRelation> copyOfRels = new HashSet<>();
		for (MDRelation relation : relsOfElem) {
			copyOfRels.add(relation.deepCopy());
		}

		copy.put(elem, copyOfRels);
		return copy;
	}

	public MDSchemaGraphSMD() {
		this.mdGraphMap = new HashMap<>();
	}

	public MDSchemaGraphSMD(String name, String uri, String nameSpace, String endpoint, String preferredLabelProperty,
			MDSchemaType type) {
		this();
		this.name = name;
		this.uri = uri;
		this.nameSpace = nameSpace;
		this.endpoint = endpoint;
		this.preferredLabelProperty = preferredLabelProperty;
		this.type = type;
	}

	@Override
	public final MDElement getNode(String name) {

		if (MDElement.isMultipleElement(name)) {
			return MultipleMDElement.getInstance();
		}

		for (MDElement node : mdGraphMap.keySet()) {
			if (node.getIdentifyingName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	public Set<Level> getLevelByNonUNiqueUri(String uri) {
		Set<Level> levels = new HashSet<>();

		for (MDElement node : mdGraphMap.keySet()) {
			if (node.getURI().equals(uri) && node instanceof Level) {
				levels.add((Level) node);
			}
		}
		return levels;
	}

	@Override
	public MDRelation getEdge(String name) {
		for (MDElement elem : mdGraphMap.keySet()) {
			for (MDRelation rel : mdGraphMap.get(elem)) {
				if (rel.getIdentifyingName().equals(name))
					return rel;
			}
		}
		return null;
	}

	@Override
	public Set<MDRelation> getEdgesOfNode(MDElement node) {
		/*
		 * if (mdGraphMap.keySet().contains(node)) {
		 * System.out.println("ccccccccccccccccccccc");
		 * System.out.println("ccccccccccccccccccccc");
		 * System.out.println("ccccccccccccccccccccc");
		 * System.out.println("ccccccccccccccccccccc"); } for (MDElement e :
		 * mdGraphMap.keySet()) { System.out.println("ssssss"); System.out.println(e);
		 * System.out.println("------------------"); System.out.println(node);
		 * System.out.println("============="); }
		 */
		return mdGraphMap.get(node) != null ? mdGraphMap.get(node) : new HashSet<MDRelation>();
	}

	@Override
	public boolean addNode(MDElement node) {
		if (mdGraphMap.get(node) == null) {
			mdGraphMap.put(node, new HashSet<>());
			return true;
		}
		return false;
	}

	@Override
	public boolean addEdge(MDRelation edge) {

		if (edge != null && getEdge(edge.getIdentifyingName()) == null) {
			MDElement node = edge.getSource();

			if (node != null) {
				if (mdGraphMap.get(node) == null) {
					Set<MDRelation> list = new HashSet<>();
					list.add(edge);
					mdGraphMap.put(node, list);
				} else {
					mdGraphMap.get(node).add(edge);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public Fact getFactOfSchema() {

		for (MDElement elem : this.mdGraphMap.keySet()) {
			if (elem instanceof Fact) {
				return (Fact) elem;
			}
		}
		return null;
	}

	@Override
	public MDElement getLeastCommonAncestor(String level, String item) {

		int chooser = compareItemsInDimension(level, item);

		if (chooser == 1) {
			return getNode(level);
		}

		if (chooser == 2) {
			return getNode(level);
		}

		if (chooser == 0) {
			return getNode(item);
		}

		if (chooser == -1) {

			// Both are levels
			if (getNode(level) instanceof Level && getNode(item) instanceof Level) {

				Level level1 = (Level) getNode(level);
				Level level2 = (Level) getNode(item);

				// Both levels in the same dimension
				if (getPossibleLevelsOnDimension(getDimensionOfLevel(level).getIdentifyingName()).contains(item)) {

					// finest level on dimension
					Level commonAncestor = getFinestLevelOnDimension(getDimensionOfLevel(level).getIdentifyingName());

					// if finest level does not roll up to both levels in check --> problem!
					if (!rollsUpDirectlyOrIndirectlyTo(commonAncestor.getIdentifyingName(), level1.getIdentifyingName())
							|| !rollsUpDirectlyOrIndirectlyTo(commonAncestor.getIdentifyingName(),
									level2.getIdentifyingName())) {
						return null;
					} else {
						while (getNextLevel(commonAncestor.getIdentifyingName()) != null
								&& !getNextLevel(commonAncestor.getIdentifyingName()).equals(level1)
								&& !getNextLevel(commonAncestor.getIdentifyingName()).equals(level2)
								&& rollsUpDirectlyOrIndirectlyTo(
										getNextLevel(commonAncestor.getIdentifyingName()).getIdentifyingName(),
										level1.getIdentifyingName())
								&& rollsUpDirectlyOrIndirectlyTo(
										getNextLevel(commonAncestor.getIdentifyingName()).getIdentifyingName(),
										level2.getIdentifyingName())) {
							commonAncestor = getNextLevel(commonAncestor.getIdentifyingName());
						}
						return commonAncestor;
					}
				}
			} else {
				// Both are levels
				if (getNode(level) instanceof Level && getNode(item) instanceof Descriptor) {

					Level level1 = (Level) getNode(level);
					Level level2 = getLevelOfDescriptor(item);

					// Both levels in the same dimension
					if (getPossibleLevelsOnDimension(getDimensionOfLevel(level).getIdentifyingName()).contains(item)) {

						// finest level on dimension
						Level commonAncestor = getFinestLevelOnDimension(
								getDimensionOfLevel(level).getIdentifyingName());

						// if finest level does not roll up to both levels in check --> problem!
						if (!rollsUpDirectlyOrIndirectlyTo(commonAncestor.getIdentifyingName(),
								level1.getIdentifyingName())
								|| !rollsUpDirectlyOrIndirectlyTo(commonAncestor.getIdentifyingName(),
										level2.getIdentifyingName())) {
							return null;
						} else {
							while (getNextLevel(commonAncestor.getIdentifyingName()) != null
									&& !getNextLevel(commonAncestor.getIdentifyingName()).equals(level1)
									&& !getNextLevel(commonAncestor.getIdentifyingName()).equals(level2)
									&& rollsUpDirectlyOrIndirectlyTo(
											getNextLevel(commonAncestor.getIdentifyingName()).getIdentifyingName(),
											level1.getIdentifyingName())
									&& rollsUpDirectlyOrIndirectlyTo(
											getNextLevel(commonAncestor.getIdentifyingName()).getIdentifyingName(),
											level2.getIdentifyingName())) {
								commonAncestor = getNextLevel(commonAncestor.getIdentifyingName());
							}
							return commonAncestor;
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public Level getLevelOfDescriptor(String descriptorURI) {

		Level level = null;

		if (getNode(descriptorURI) instanceof Descriptor) {

			for (MDElement elem : getAllNodes()) {
				if (elem instanceof Level) {
					for (MDRelation rel : getEdgesOfNode(elem)) {
						if (rel instanceof HasDescriptor && rel.getTo() != null
								&& rel.getTo().getIdentifyingName().equals(descriptorURI)) {
							return (Level) rel.getFrom();
						}
					}
				}
			}
		}
		return level;

	}

	public Level getFinestLevelOnDimension1(String dimensionURI) {

		boolean found = false;

		List<String> levelsOnDimensionURIs = getPossibleLevelsOnDimension(dimensionURI);
		List<Level> allDimensionLevels = new ArrayList<>();

		for (String levelURI : levelsOnDimensionURIs) {
			allDimensionLevels.add((Level) getNode(levelURI));
		}

		for (Level lChecked : allDimensionLevels) {

			found = false;
			for (Level l : allDimensionLevels) {
				if (!lChecked.equals(l)) {
					if (rollsUpDirectlyOrIndirectlyTo(l.getIdentifyingName(), lChecked.getIdentifyingName())) {
						found = true;
						break;
					}
				}
			}

			if (!found) {
				return lChecked;
			}
		}
		return null;
	}

	@Override
	public Level getFinestLevelOnDimension(String dimensionURI) {

		boolean found;

		List<String> levelsOnDimensionURIs = getPossibleLevelsOnDimension(dimensionURI);
		List<Level> allDimensionLevels = new ArrayList<>();

		for (String levelURI : levelsOnDimensionURIs) {
			allDimensionLevels.add((Level) getNode(levelURI));
		}

		for (Level lChecked : allDimensionLevels) {

			found = true;

			for (Level l : allDimensionLevels) {
				if (!lChecked.equals(l)) {
					if (!rollsUpDirectlyOrIndirectlyTo(lChecked.getIdentifyingName(), l.getIdentifyingName())) {
						found = false;
					}
				}
			}

			if (found) {
				return lChecked;
			}
		}
		return null;
	}

	@Override
	public Set<MDElement> getAllElementsOnDimension(String dimensionURI) {

		Set<MDElement> elems = new HashSet<>();
		List<String> levelsNames = getPossibleLevelsOnDimension(dimensionURI);

		for (String lvl : levelsNames) {
			elems.add(getNode(lvl));
			if (lvl != null) {
				elems.addAll(getDescriptors(lvl));
			}
		}

		return elems;
	}

	@Override
	public int compareItemsInDimension(String level, String item) {

		// level either rolls up to item or item is a descriptor of level.
		if (isItemDescendantForLevel(level, item)) {
			return 2;
		}

		// level and item are equal
		if (getNode(level).equals(getNode(item))) {
			return 1;
		}

		// item rolls up to level.
		if ((getNode(item) instanceof Level) && isItemDescendantForLevel(item, level)) {
			return 0;
		}

		// no common path; item and level are incomparable
		return -1;
	}

	@Override
	public boolean isItemDescendantForLevel(String level, String item) {

		MDElement currentLevel = getNode(level);

		if (currentLevel instanceof Level) {

			MDElement elem = getNode(item);
			if (elem instanceof Descriptor) {
				if (getDescriptors(level).contains((Descriptor) elem)) {
					return true;
				}
			} else {
				if (elem instanceof Level) {
					if (rollsUpDirectlyOrIndirectlyTo(level, item)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean rollsUpDirectlyOrIndirectlyTo(String level1, String level2) {

		Level nextLevel = getNextLevel(level1);

		if (nextLevel != null) {
			if (nextLevel.equals((Level) getNode(level2))) {
				return true;
			} else {
				return rollsUpDirectlyOrIndirectlyTo(nextLevel.getIdentifyingName(), level2);
			}
		}

		return false;
	}

	@Override
	public final List<Descriptor> getDescriptors(String currentLevelName) {

		List<Descriptor> descriptors = new ArrayList<>();

		try {
			MDElement elem = getNode(currentLevelName);
			if (elem instanceof Level) {
				for (MDRelation rel : getEdgesOfNode(elem)) {
					if (rel instanceof HasDescriptor) {
						descriptors.add((Descriptor) rel.getTo());
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Cannot get descriptors", ex);
		}

		return descriptors;
	}

	@Override
	public Level getNextLevel(String currentLevelName) {
		try {
			MDElement elem = getNode(currentLevelName);
			if (elem instanceof Level) {
				for (MDRelation rel : getEdgesOfNode(elem)) {
					if (rel instanceof QB4OHierarchyStep) {
						return (Level) rel.getTo();
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Cannot find next level", ex);
		}
		return null;
	}

	@Override
	public Level getPreviousLevel(String currentLevelName) {

		try {
			for (MDElement elem : mdGraphMap.keySet()) {
				if (elem instanceof Level) {
					for (MDRelation rel : getEdgesOfNode(elem)) {
						if (rel.getTo().getIdentifyingName().equals(currentLevelName)) {
							return (Level) rel.getTo();
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Cannot previous next level", ex);
		}
		return null;
	}

	public List<String> getPossibleLevelsOnDimension(String dimensionURI) {

		List<String> levels = new ArrayList<>();

		for (MDElement elem : mdGraphMap.keySet()) {
			if (elem instanceof Level) {
				for (MDRelation rel : getEdgesOfNode(elem)) {
					if (rel instanceof InDimension && rel.getTo() != null
							&& rel.getTo().getIdentifyingName().equals(dimensionURI)) {
						levels.add(rel.getFrom().getIdentifyingName());
					}
				}
			}
		}
		return levels;
	}

	public Set<String> getUniquePossibleLevelsOnDimension(String dimensionURI) {

		return getPossibleLevelsOnDimension(dimensionURI).stream().collect(Collectors.toSet());
	}

	public Set<AutoCompleteData> getUniquePossibleLevelsOnDimensionWithLabels(String dimensionURI) {

		Set<AutoCompleteData> levels = new HashSet<>();

		for (MDElement elem : mdGraphMap.keySet()) {
			if (elem instanceof Level) {
				for (MDRelation rel : getEdgesOfNode(elem)) {
					if (rel instanceof InDimension && rel.getTo() != null
							&& rel.getTo().getIdentifyingName().equals(dimensionURI)) {
						levels.add(new AutoCompleteData(rel.getFrom().getLabel(), rel.getFrom().getIdentifyingName()));
					}
				}
			}
		}
		return levels;
	}

	@Override
	public List<Path<MDElement, MDRelation>> getAllPathsBetweenTwoVertices(MDElement startNode, MDElement endNode) {

		List<Path<MDElement, MDRelation>> paths = new LinkedList<>();
		List<Path<MDElement, MDRelation>> maxLengthPaths = Collections.synchronizedList(new LinkedList<>());

		if (this.mdGraphMap.get(startNode) != null) {
			for (MDRelation rel : this.mdGraphMap.get(startNode)) {
				MDPath p = new MDPath(rel);
				maxLengthPaths.add(p);
				// System.out.println("1 Adding path" + p);
			}
		}

		while (maxLengthPaths.size() > 0) {
			for (ListIterator<Path<MDElement, MDRelation>> iterator = maxLengthPaths.listIterator(); iterator
					.hasNext();) {
				Path<MDElement, MDRelation> p = iterator.next();
				MDElement firstVertex = p.getFirstNode();
				MDElement lastVertex = p.getLastNode();
				MDRelation lastEdge = p.getLastEdge();

				// ensuring not reaching a tagged node
				if (lastVertex.equals(endNode)) {
					if (!paths.contains(p))
						paths.add(p);
					// System.out.println("2 Adding path" + p);
					iterator.remove();
					break;
				} else {
					// re-launching from last reached vertex
					if (this.getEdgesOfNode(p.getLastNode())
							.size() > 0 /*
										 * && // there are out edges !launchedVertices.contains(p.getLastVertex())
										 */) { // avoiding re-launching twice from the same vertex
						try {
							Path<MDElement, MDRelation> p_ext;

							iterator.remove();
							for (MDRelation e : this.getEdgesOfNode(p.getLastNode())) {
								if (!p.checkIfPathContainsEdge(e)) {
									p_ext = p.copy();
									p_ext.insertEdgeWithCheck(e);
									iterator.add(p_ext);
									// System.out.println("3 Adding path" + p_ext);
								}
							}
						} catch (Exception ex) {
							logger.error("Exception cloning path: " + p, ex);
						}
					} else {
						iterator.remove();
						break;
					}
				}
			}
		}
		// Path.PrintPathsList(paths);
		return paths;
	}

	@Override
	public List<Path<MDElement, MDRelation>> getAllMappedPathsBetweenTwoVertices(MDElement startNode,
			MDElement endNode) {

		List<Path<MDElement, MDRelation>> paths = new ArrayList<>();
		List<Path<MDElement, MDRelation>> maxLengthPaths = Collections.synchronizedList(new LinkedList<>());

		if (this.mdGraphMap.get(startNode) != null && this.mdGraphMap.get(endNode) != null) {
			Set<MDRelation> outEdges = getMappedOutEdgesOfNodeSmartly(startNode.getIdentifyingName(),
					endNode.getIdentifyingName());
			maxLengthPaths = outEdges.stream().map(x -> new MDPath(x)).collect(Collectors.toList());
			// maxLengthPaths.forEach(x -> System.out.println("1 Adding path" + x));
		}

		while (maxLengthPaths.size() > 0) {
			for (ListIterator<Path<MDElement, MDRelation>> iterator = maxLengthPaths.listIterator(); iterator
					.hasNext();) {
				Path<MDElement, MDRelation> p = iterator.next();
				MDElement firstVertex = p.getFirstNode();
				MDElement lastVertex = p.getLastNode();
				MDRelation lastEdge = p.getLastEdge();

				// ensuring not reaching a tagged node
				if (lastVertex.equals(endNode)) {
					if (!paths.contains(p))
						paths.add(p);
					// System.out.println("2 Adding path" + p);
					iterator.remove();
					break;
				} else {
					Set<MDRelation> innerOutEdges = this.getMappedOutEdgesOfNode(p.getLastNode().getIdentifyingName());
					// re-launching from last reached vertex
					if (innerOutEdges
							.size() > 0 /*
										 * && // there are out edges !launchedVertices.contains(p.getLastVertex())
										 */) { // avoiding re-launching twice from the same vertex
						try {
							Path<MDElement, MDRelation> p_ext;

							iterator.remove();
							for (MDRelation e : innerOutEdges) {
								if (!p.checkIfPathContainsEdge(e)) {
									p_ext = p.copy();
									if (p_ext.insertEdgeWithCheck(e)) {
										iterator.add(p_ext);
										// System.out.println("3 Adding path" + p_ext);
									}
								}
							}
						} catch (Exception ex) {
							logger.error("Exception cloning path: " + p, ex);
						}
					} else {
						iterator.remove();
						break;
					}
				}
			}
		}
		// Path.PrintPathsList(paths);
		return paths;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getEndpoint() {
		return this.endpoint;
	}

	@Override
	public String getURI() {
		return this.uri;
	}

	@Override
	public Set<MDRelation> getAllEdges() {
		Set<MDRelation> relations = new HashSet<>();
		for (MDElement elem : this.mdGraphMap.keySet()) {
			relations.addAll(getEdgesOfNode(elem));
		}
		return relations;
	}

	@Override
	public Set<MDElement> getAllNodes() {
		return this.mdGraphMap.keySet();
	}

	@Override
	public MDElement getDescendantMDElement(String currentElemURI) {

		MDElement elem1 = this.getNode(currentElemURI);
		if (elem1.equals(getFactOfSchema())) {
			return null;
		}

		try {
			for (MDElement elem : mdGraphMap.keySet()) {
				if (elem instanceof Level || elem instanceof Fact) {
					for (MDRelation rel : getEdgesOfNode(elem)) {
						if (rel.getTo().getIdentifyingName().equals(currentElemURI)) {
							return rel.getFrom();
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Cannot get Descendant MDElement", ex);
		}
		return null;
	}

	@Override
	public Set<MDRelation> getMappedOutEdgesOfNode(String nodeUri) {
		Set<MDRelation> rels = new HashSet<>();

		for (MDRelation rel : getEdgesOfNode(getNode(nodeUri))) {
			if (rel instanceof HasLevel || rel instanceof HasMeasure || rel instanceof HasDescriptor
					|| rel instanceof QB4OHierarchyStep) {
				rels.add(rel);
			}
		}
		return rels;
	}

	@Override
	public Set<MDRelation> getMappedOutEdgesOfNodeSmartly(String fromUri, String toUri) {

		Set<MDRelation> rels = new HashSet<>();

		if (getNode(toUri) instanceof Level) {

			Dimension dimOflevel = getDimensionOfLevel(toUri);

			if (dimOflevel != null) {

				for (MDRelation rel : getEdgesOfNode(getNode(fromUri))) {

					if ((rel instanceof HasLevel || rel instanceof HasDescriptor || rel instanceof QB4OHierarchyStep)
							&& dimOflevel
									.equals(getDimensoinOfLevelOrDescriptor(rel.getTarget().getIdentifyingName()))) {
						rels.add(rel);
					}
				}
			}
		}

		if (getNode(toUri) instanceof Descriptor) {

			Dimension dimOflevel = getDimensoinOfDescriptor(toUri);

			if (dimOflevel != null) {

				for (MDRelation rel : getEdgesOfNode(getNode(fromUri))) {

					if ((rel instanceof HasLevel || rel instanceof HasDescriptor || rel instanceof QB4OHierarchyStep)
							&& dimOflevel
									.equals(getDimensoinOfLevelOrDescriptor(rel.getTarget().getIdentifyingName()))) {
						rels.add(rel);
					}
				}
			}
		}

		if (getNode(toUri) instanceof IMeasure) {

			for (MDRelation rel : getEdgesOfNode(getNode(fromUri))) {

				if ((rel instanceof HasMeasure)) {
					rels.add(rel);
				}
			}
		}

		return rels;
	}

	@Override
	public Set<MDElement> getNodesByURI(String uri) {
		return getAllNodes().stream().filter(x -> x.getURI().equals(uri)).collect(Collectors.toSet());
	}

	@Override
	public String getIdentifyingNameFromUriAndDimensionAndHier(String uri, String dimUri, String hierURI) {
		return uri;
	}

	@Override
	public String getLevelAttributeIdentifyingNameFromUriAndDimensionAndHier(String elemUri,
			String levelIdentifyingName) {
		return elemUri;
	}

	public MDSchemaType getSchemaType() {
		return type;
	}

	public void setType(MDSchemaType type) {
		this.type = type;
	}

	public String getPreferredLabelProperty() {
		return preferredLabelProperty;
	}

	public void setPreferredLabelProperty(String preferredLabelProperty) {
		this.preferredLabelProperty = preferredLabelProperty;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
}

class MDPath implements Path<MDElement, MDRelation> {

	private List<MDRelation> path = new LinkedList<>();

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder(100);
		for (MDRelation rel : path) {
			builder.append("Edge in path: ");
			builder.append(rel);
			builder.append("\r\n");
		}
		return builder.toString();
	}

	@Override
	public MDPath copy() { //
		MDPath clonedPath = new MDPath(); // big P in Path
		LinkedList<MDRelation> clonedpath = new LinkedList<MDRelation>(); // small p in path
		for (MDRelation e : this.path) {
			MDRelation colnedEdge = (MDRelation) MappableRelationFactory.copyMappableRelation(e);
			clonedpath.add(colnedEdge);
		}
		clonedPath.path = clonedpath;
		return clonedPath;
	}

	public boolean insertEdgeWithCheck(MDRelation e) {
		if (this.path.size() == 0)
			return this.path.add(e);
		else // the path contains one edge at least
		if (this.getLastNode().equals(e.getFrom()) && // check the last path vertex is identical to
														// the
		// edge source
				!this.path.contains(e)) // check the new edge doesn't exist in the path
			return this.path.add(e);
		else
			return false;
	}

	public MDPath() {
		super();
	}

	/**
	 * Constructor: builds a path from a single MDRelation
	 * 
	 * @param rel
	 */
	public MDPath(MDRelation rel) {
		this.path.add(rel);
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj != null && obj instanceof MDPath) {
			MDPath objPath = (MDPath) obj;
			if (this.path != null) {
				return this.path.equals(objPath.path);
			}
		}
		return false;
	}

	public boolean contains(MDPath otherPath) {
		if (this.getPathEdges().containsAll(otherPath.getPathEdges())) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param otherPath
	 * 
	 * @return if this path contains edges of {@code otherPath}, then a list of
	 *         these edges is returned. An empty list is returned otherwise.
	 * 
	 */
	public List<MDRelation> intersectsWith(MDPath otherPath) {

		List<MDRelation> intersection = new ArrayList<>();

		for (MDRelation rel : otherPath.getPathEdges()) {
			if (this.getPathEdges().contains(rel)) {
				intersection.add(rel);
			}
		}

		return intersection;
	}

	@Override
	public int compareWith(Path<MDElement, MDRelation> otherPath, List<MDRelation> intersection) {

		intersection.clear();
		if (otherPath instanceof MDPath) {

			MDPath castedPath = (MDPath) otherPath;

			if (this.equals(castedPath)) {
				return 0;
			}
			if (this.contains(castedPath)) {
				return 1;
			}
			if (castedPath.contains(this)) {
				return -1;
			}
			intersection.addAll(this.intersectsWith(castedPath));
			if (intersection.size() > 0) {
				return -2;
			}
			return -3;
		}
		return -4;
	}

	public MDRelation getLastEdge() {
		if (this.path.size() > 0) {
			return this.path.get(this.path.size() - 1);
		}
		return null;
	}

	public boolean checkIfPathContainsEdge(MDRelation edge) {
		return this.path.contains(edge);
	}

	@Override
	public MDElement getFirstNode() {
		return this.path.get(0).getFrom();
	}

	@Override
	public MDElement getLastNode() {
		return this.path.get(this.path.size() - 1).getTo();
	}

	@Override
	public List<MDRelation> getPathEdges() {
		return this.path;
	}

}
