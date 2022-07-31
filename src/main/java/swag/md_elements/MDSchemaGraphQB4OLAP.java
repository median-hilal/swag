package swag.md_elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import swag.helpers.AutoCompleteData;
import swag.sparql_builder.Configuration;
import swag.sparql_builder.CustomSPARQLQuery;
import swag.sparql_builder.SPARQLUtilities;

public class MDSchemaGraphQB4OLAP extends MDSchemaGraphSMD {

	public List<MDElement> getPath(List<MDElement> path, String startLevel, String endLevel, String dimension) {

		if (path.size() == 0){
			if (this.getNodeByUri(endLevel) instanceof Descriptor){
				for (MDElement elem : this.getMdGraphMap().keySet()) {
					if (elem.getURI().equals(endLevel) && elem instanceof Descriptor && isAttributeInDimension(dimension, elem.getURI())) {
						if (!path.contains(elem)) {
							path.add((Descriptor) elem);
						}
						for (MDRelation rel : getInEdgesOfNode(elem)) {
							if (rel instanceof HasDescriptor) {
								if(isLevelInDimension(dimension, rel.getSource().getURI())){
									if (!path.contains((Level) rel.getSource())) {
										path.add((Level) rel.getSource());
									}
									getPath(path, startLevel, rel.getSource().getURI(), dimension);
								}
							}
						}
					}
				}
			}
			if(startLevel.equals(endLevel)) {
				if (this.getNodeByUri(startLevel) instanceof Level) {
					path.add((Level) this.getNodeByUri(startLevel));
				}
			}
		}

		if (!startLevel.equals(endLevel)) {
			for (MDElement elem : this.getMdGraphMap().keySet()) {
				if (elem.getURI().equals(endLevel) && elem instanceof Level && isLevelInDimension(dimension, elem.getURI())) {
					if (!path.contains(elem)) {
						path.add((Level) elem);
					}
					for (MDRelation rel : getInEdgesOfNode(elem)) {
						if (rel instanceof QB4OHierarchyStep) {
							if(isLevelInDimension(dimension, rel.getSource().getURI())){
								if (!path.contains((Level) rel.getSource())) {
									path.add((Level) rel.getSource());
								}
								getPath(path, startLevel, rel.getSource().getURI(), dimension);
							}
						}
					}
				}
			}
		}
		return path;
	}

	public boolean isLevelInDimension(String dimension, String level){
		return getDimensionsOfLevel1(level).stream()
				.map(d -> d.getURI()).collect(Collectors.toSet()).contains(dimension);
	}

	public boolean isAttributeInDimension(String dimension, String attribute){
		return getDimensionsOfAttribute(attribute).stream()
				.map(d -> d.getURI()).collect(Collectors.toSet()).contains(dimension);
	}

	public MDRelation getRollUpOrHasAttributeProperty(String l1, String d1, String l2, String d2) {

		for (MDElement elem1 : getAllElemsWithUri(l1)) {
			if (elem1 instanceof Level) {
					for (MDElement elem2 : getAllElemsWithUri(l2)) {
						if (elem2 instanceof Level) {

								for (MDRelation rel : getMdGraphMap().values().stream().flatMap(val -> val.stream())
										.collect(Collectors.toSet())) {
									if (rel instanceof QB4OHierarchyStep && rel.getSource().getURI().equals(l1)
											&& rel.getTarget().getURI().equals(l2)) {
										return rel;
									}
								}
						}else{
							if (elem2 instanceof Descriptor) {

								for (MDRelation rel : getMdGraphMap().values().stream().flatMap(val -> val.stream())
										.collect(Collectors.toSet())) {
									if (rel instanceof HasDescriptor && rel.getSource().getURI().equals(l1)
											&& rel.getTarget().getURI().equals(l2)) {
										return rel;
									}
								}
							}
						}
					}
			}
		}
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

	public MDSchemaType getSchemaType() {
		return MDSchemaType.QB4OLAP;
	}

	public MDSchemaGraphQB4OLAP(String name, String uri, String nameSpace, String endpoint,
			String preferredLabelProperty, MDSchemaType type) {
		super(name, uri, nameSpace, endpoint, preferredLabelProperty, type);
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
	public List<String> getPossibleLevelsOnDimension(String dimensionURI) {

		List<String> levels = new ArrayList<>();
		for (MDElement elem : this.getMdGraphMap().keySet()) {
			if (elem instanceof QB4OHierarchy) {
				for (MDRelation rel : getEdgesOfNode(elem)) {
					if (rel instanceof QB4OHierarchyInDimension) {
						if (rel.getTarget().equals(getNode(dimensionURI))) {
							for (MDRelation edge : getAllEdges()) {
								if (edge instanceof QB4OInHierarchy) {
									if (edge.getTo() != null && edge.getTo().equals(elem)) {
										levels.add(edge.getSource().getIdentifyingName());
									}
								}
							}
						}
					}
				}
			}
		}
		return levels;
	}

	@Override
	public Set<AutoCompleteData> getUniquePossibleLevelsOnDimensionWithLabels(String dimensionURI) {

		Set<AutoCompleteData> levels = new HashSet<>();
		for (MDElement elem : this.getMdGraphMap().keySet()) {
			if (elem instanceof QB4OHierarchy) {
				for (MDRelation rel : getEdgesOfNode(elem)) {
					if (rel instanceof QB4OHierarchyInDimension) {
						if (rel.getTarget().equals(getNode(dimensionURI))) {
							for (MDRelation edge : getAllEdges()) {
								if (edge instanceof QB4OInHierarchy) {
									if (edge.getTo() != null && edge.getTo().equals(elem)) {
										levels.add(new AutoCompleteData(edge.getSource().getLabel(),
												edge.getSource().getIdentifyingName()));
									}
								}
							}
						}
					}
				}
			}
		}
		return levels;
	}

	/*
	 * @Override public boolean addNode(MDElement node) { if
	 * (getNode(node.getIdentifyingName()) == null) { return super.addNode(node); }
	 * else { if (node instanceof Level) { ((Level)
	 * node).setIdentifyingName(node.getName() + "_2"); // ((Level)
	 * node).setURI(node.getName() + "_2"); return super.addNode(node); } } return
	 * false; }
	 * 
	 * @Override public boolean addEdge(MDRelation edge) {
	 * 
	 * if (edge != null && getEdge(edge.getIdentifyingName()) == null) { return
	 * super.addEdge(edge); } return false; }
	 */

	@Override
	public Dimension getDimensionOfLevel(String leveName) {

		Dimension dim = super.getDimensionOfLevel(leveName);

		if (dim != null) {
			return dim;
		}

		MDElement level = this.getNode(leveName);
		if (level != null && level instanceof Level) {
			Set<MDRelation> rels = getEdgesOfNode(level);
			if (!rels.isEmpty()) {
				for (MDRelation rel : rels) {
					if (rel instanceof QB4OInHierarchy) {
						if (rel.getTo() != null) {
							QB4OHierarchy hierarchy = (QB4OHierarchy) rel.getTo();

							Set<MDRelation> relsOfHierarchy = getEdgesOfNode(hierarchy);
							for (MDRelation rel1 : relsOfHierarchy) {
								if (rel1.getTarget() != null && rel1 instanceof QB4OHierarchyInDimension) {
									return (Dimension) rel1.getTarget();
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Renaming elements when eligible. If there are multiple paths arriving at an
	 * element, then the element must be considered for renaming.
	 */
	public void 	injectInitiate() {

		Queue<IndexedMDElement> queue = new LinkedList<>();
		Set<MDElement> visitedElems = new HashSet<>();
		Map<IndexedMDElement, MDElement> previousElement = new HashMap<>();
		Fact f = getFactOfSchema();
		long counter = 0;

		queue.add(new IndexedMDElement(f, counter, null));
		visitedElems.add(f);

		while (!queue.isEmpty()) {
			IndexedMDElement currentElement = queue.remove();
			IndexedMDElement slovedElem = null;

			if (isDueToSolve(currentElement, visitedElems)) {
				IndexedMDElement originalHitElement = IndexedMDElement.getMin(currentElement.getElm(),
						previousElement.keySet());

				if (!originalHitElement.equals(currentElement)) {
					IndexedMDElement newElm = solveElement(currentElement, previousElement.get(currentElement),
							originalHitElement);
					modifyVisitedElements();
					addToVisitedElements(newElm.getElm(), visitedElems);
					slovedElem = new IndexedMDElement(newElm.getElm(), newElm.getIndex(), newElm.getCameFrom());
					previousElement.put(slovedElem, previousElement.get(currentElement));
					queue.add(slovedElem);
				}
			} else {
				// TODO
			}
			addToVisitedElements(currentElement.getElm(), visitedElems);
			Set<MDRelation> neighbours = getEdgesOfNode(currentElement.getElm());

			for (MDRelation rel : neighbours) {
				if ((rel instanceof QB4OHierarchyStep && rel.getTarget() instanceof Level)
						|| (currentElement.getElm() instanceof Fact && rel.getTarget() instanceof Level)) {
					IndexedMDElement indexedElemToPush = new IndexedMDElement(rel.getTo(), ++counter, rel);
					queue.add(indexedElemToPush);

					if (slovedElem != null) {
						previousElement.put(indexedElemToPush, slovedElem.getElm());
					} else {
						previousElement.put(indexedElemToPush, currentElement.getElm());
					}
				}
			}
		}
		renameAll();
	}

	/**
	 * Doing the main processing for elements that need processing renaming due to
	 * multiple paths reaching them.
	 * 
	 * @param elem1
	 * @param subElem
	 * @param originalElementThatHitInVisited
	 * @return
	 */
	public IndexedMDElement solveElement(IndexedMDElement elem1, MDElement subElem,
			IndexedMDElement originalElementThatHitInVisited) {

		MDElement elem = elem1.getElm();
		Set<Dimension> dims = getDimensionsOfLevel(subElem.getIdentifyingName());
		Set<QB4OHierarchy> hiers = getHierarchiesOfLevel(subElem.getIdentifyingName());

		// Get the already existing entry
		Map<MDElement, Set<MDRelation>> map = copyEntry(elem);
		MDElement key = null;
		for (MDElement mdElem : map.keySet()) {
			key = mdElem.deepCopy();
			break;
		}

		deleteDimAndHierFromMap(map, dims, hiers);
		deleteDimAndHierFromOriginalElementMap(getEdgesOfNode(originalElementThatHitInVisited.getElm()), dims, hiers);

		Set<MDRelation> mapRels = map.get(key);
		try {
			// Renaming
			((Level) key).setIdentifyingName(elem.getURI() + elem1.getIndex());

			CustomSPARQLQuery query = SPARQLUtilities.renameVariableInQuery(key.getMapping().getQuery(),
					key.getHeadVar().getName(), key.getHeadVar().getName() + elem1.getIndex());
			key.setMapping(new Mapping(query));

		} catch (Exception ex) {
			System.out.println("ss");
		}

		// Inserting
		addEntry((Level) key, mapRels);

		// modify sub levels using relTo data field.
		for (MDRelation rel : getEdgesOfNode(key)) {
			rel.setFrom((Level) key);

			// Modifying the mapping query String to match the newly created names.
			if (rel instanceof QB4OHierarchyStep) {
				CustomSPARQLQuery query0 = SPARQLUtilities.renameVariableInQuery(rel.getMapping().getQuery(),
						rel.getHeadVarFrom().getName(), rel.getHeadVarFrom().getName() + elem1.getIndex());

				CustomSPARQLQuery query = SPARQLUtilities.renameVariableInQuery(query0, rel.getHeadVarTo().getName(),
						rel.getHeadVarTo().getName() + elem1.getIndex());

				rel.setMapping(new Mapping(query));
			}
		}

		// modify sub elms
		// Modifying the mapping query String to match the newly created names.
		CustomSPARQLQuery query11 = SPARQLUtilities.renameVariableInQuery(elem1.getCameFrom().getMapping().getQuery(),
				elem1.getCameFrom().getHeadVarTo().getName(),
				elem1.getCameFrom().getHeadVarTo().getName() + elem1.getIndex());
		elem1.getCameFrom().setMapping(new Mapping(query11));

		for (Descriptor desc : getDescriptors(key.getURI())) {

			// Get the already existing entry
			Map<MDElement, Set<MDRelation>> mapDesc = copyEntry(desc);
			MDElement keyDesc = null;

			for (MDElement mdElem : mapDesc.keySet()) {
				keyDesc = mdElem.deepCopy();
				break;
			}

			// Renaming
			((Descriptor) keyDesc).setIdentifyingName(desc.getURI() + elem1.getIndex());

			// Modifying the mapping query String to match the newly created names.
			CustomSPARQLQuery query = SPARQLUtilities.renameVariableInQuery(desc.getMapping().getQuery(),
					desc.getHeadVar().getName(), desc.getHeadVar().getName() + elem1.getIndex());
			keyDesc.setMapping(new Mapping(query));
			// Inserting
			addEntry((Descriptor) keyDesc, mapDesc.get(keyDesc));

			// modify sub levels using relTo data field.
			for (MDRelation rel : getEdgesOfNode(key)) {
				if (rel.getTo().equals(desc)) {
					rel.setTo((Descriptor) keyDesc);

					// Modifying the mapping query String to match the newly created names.
					if (rel instanceof HasDescriptor) {
						CustomSPARQLQuery query1 = SPARQLUtilities.renameVariableInQuery(rel.getMapping().getQuery(),
								rel.getHeadVarFrom().getName(), rel.getHeadVarFrom().getName() + elem1.getIndex());

						CustomSPARQLQuery query2 = SPARQLUtilities.renameVariableInQuery(query1,
								rel.getHeadVarTo().getName(), rel.getHeadVarTo().getName() + elem1.getIndex());

						rel.setMapping(new Mapping(query2));
					}
				}
			}
		}

		// modify sub levels using relTo data field.
		for (MDRelation rel : getEdgesOfNode(subElem)) {
			if (rel.getTo().equals(elem)) {
				rel.setTo((Level) key);
			}
		}

		// Deleting original level
		// removeEntry(elem);
		return new IndexedMDElement(key, elem1.getIndex() + 1, null);
	}

	public boolean isDueToSolve(IndexedMDElement elm, Set<MDElement> visitedElems) {
		if (isVisited(elm.getElm(), visitedElems) && !(elm.getElm() instanceof Fact)) {
			return true;
		}
		return false;
	}

	public boolean isVisited(MDElement elm, Set<MDElement> visitedElems) {
		if (visitedElems.contains(elm)) {
			return true;
		}
		return false;
	}

	public void addToVisitedElements(MDElement elm, Set<MDElement> visitedElems) {
		visitedElems.add(elm);
	}

	/**
	 * 
	 * Removes
	 * 
	 * @param map   contains one entry with the element to remove its dimension and
	 *              hierarchy
	 * @param dims
	 * @param hiers
	 */
	private void deleteDimAndHierFromMap(Map<MDElement, Set<MDRelation>> map, Set<Dimension> dims,
			Set<QB4OHierarchy> hiers) {

		MDElement nodeElement = null;
		for (MDElement elm : map.keySet()) {
			nodeElement = elm;
			break;
		}

		Set<MDRelation> relsToRemove = new HashSet<>();

		for (MDRelation rel : map.get(nodeElement)) {
			if ((rel instanceof QB4OInHierarchy && !hiers.contains(rel.getTo()))
					|| (rel instanceof InDimension && !dims.contains(rel.getTo()))) {
				relsToRemove.add(rel);
			}
		}

		for (MDRelation rel : relsToRemove) {
			map.get(nodeElement).remove(rel);
		}

	}

	public void deleteDimAndHierFromOriginalElementMap(Set<MDRelation> rels, Set<Dimension> dims,
			Set<QB4OHierarchy> hiers) {

		Set<MDRelation> relsToRemove = new HashSet<>();

		for (MDRelation rel : rels) {
			if ((rel instanceof QB4OInHierarchy && hiers.contains(rel.getTo()))
					|| (rel instanceof InDimension && dims.contains(rel.getTo()))) {
				relsToRemove.add(rel);
			}
		}

		for (MDRelation rel : relsToRemove) {
			rels.remove(rel);
		}

	}

	/**
	 * 
	 * Rename all levels and descriptors according to their hierarchies/dimensions
	 * 
	 */
	public void renameAll() {

		Map<MDElement, Set<MDRelation>> tempElmsToAdd = new HashMap<>();
		Set<MDElement> elmsToRemove = new HashSet<>();

		// Renaming descriptors
		for (MDElement elm : getAllNodes()) {

			if (elm instanceof Descriptor) {
				Level descLevel = getLevelOfDescriptor(elm.getIdentifyingName());
				// Collecting elements that are to be renamed. Renaming without a remove and a
				// further add
				// to the map may cause an unexpected behaviour of the map.
				elmsToRemove.add(elm.deepCopy());
				Set<MDRelation> rels = getEdgesOfNode(elm);
				// The extension used for renaming
				elm.setIdentifyingName(elm.getURI() + getExtension(descLevel));
				tempElmsToAdd.put(elm, rels);
			}
		}

		for (MDElement elmToRemove : elmsToRemove) {
			removeEntry(elmToRemove);
		}

		for (Map.Entry<MDElement, Set<MDRelation>> entry : tempElmsToAdd.entrySet()) {
			addEntry(entry.getKey(), entry.getValue());
		}

		tempElmsToAdd = new HashMap<>();
		elmsToRemove = new HashSet<>();

		// Renaming levels
		for (MDElement elm : getAllNodes()) {

			if (elm instanceof Level) {
				// Collecting elements that are to be renamed. Renaming without a remove and a
				// further add
				// to the map may cause an unexpected behaviour of the map.
				elmsToRemove.add(elm.deepCopy());
				Set<MDRelation> rels = getEdgesOfNode(elm);
				elm.setIdentifyingName(elm.getURI() + getExtension((Level) elm));
				tempElmsToAdd.put(elm, rels);
			}

		}

		for (MDElement elmToRemove : elmsToRemove) {
			removeEntry(elmToRemove);
		}

		for (Map.Entry<MDElement, Set<MDRelation>> entry : tempElmsToAdd.entrySet()) {
			addEntry(entry.getKey(), entry.getValue());
		}

	}

	/**
	 * 
	 * Generating the string to be added to the element name generated from the
	 * hierarchies and dimensions the element belongs to.
	 * 
	 * @param l
	 * @return
	 */
	public String getExtension(Level l) {

		try {
			Set<Dimension> dims = getDimensionsOfLevel(l.getIdentifyingName());
			Set<QB4OHierarchy> hiers = getHierarchiesOfLevel(l.getIdentifyingName());

			String extension = "";
			for (Dimension dim : dims) {
				extension += "_" + dim.getName();
			}
			for (QB4OHierarchy hier : hiers) {
				extension += "_" + hier.getName();
			}

			return extension;
		} catch (Exception ex) {
			System.out.println("sss");
		}

		return null;
	}

	public void modifyVisitedElements() {

	}

	@Override
	public String getIdentifyingNameFromUriAndDimensionAndHier(String uri, String dimUri, String hierURI) {

		Set<MDElement> nodes = getNodesByURI(uri);
		if (nodes.size() > 1) {

			for (MDElement elm : nodes) {
				if ((getHierarchiesOfLevel(elm.getIdentifyingName()).contains((QB4OHierarchy) getNode(hierURI))
						|| Configuration.getInstance().is("singleHierarchy"))
						&& getDimensionsOfLevel(elm.getIdentifyingName()).contains((Dimension) getNode(dimUri))) {
					return elm.getIdentifyingName();
				}
			}
			return uri;
		} else {
			if (nodes.size() == 1) {
				return nodes.stream().findAny().orElse(null).getIdentifyingName();
			} else {
				return uri;
			}
		}
	}

	@Override
	public String getLevelAttributeIdentifyingNameFromUriAndDimensionAndHier(String elemUri,
			String levelIdentifyingName) {
		return levelIdentifyingName + "_" + elemUri;
	}

	public MDElement getNodeByUri(String uri) {
		for (MDElement elem : this.getMdGraphMap().keySet()) {
			if (elem.getURI().equals(uri)) {
				return elem;
			}
		}
		return null;
	}

	public Set<Dimension> getDimensionsOfAttribute(String attr) {

		return getDimensionsOfLevel1(getLevelOfDescriptor1(attr).getURI());
	}

	public Level getLevelOfDescriptor1(String descriptorURI) {

		Level level = null;

		if (getNodeByUri(descriptorURI) instanceof Descriptor) {

			for (MDElement elem : getAllNodes()) {
				if (elem instanceof Level) {
					for (MDRelation rel :getOutEdgesOfNode(getNodeByUri(elem.getURI()))) {
						if (rel instanceof HasDescriptor && rel.getTo() != null
								&& rel.getTo().getURI().equals(descriptorURI)) {
							return (Level) rel.getFrom();
						}
					}
				}
			}
		}
		return level;

	}


	public Set<Dimension> getDimensionsOfLevel1(String leveName) {

		Set<Dimension> dims = new HashSet<>();

		MDElement level = this.getNodeByUri(leveName);
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

	public Set<QB4OHierarchy> getHierarchiesOfLevel(String leveName) {

		Set<QB4OHierarchy> hiers = new HashSet<>();

		MDElement level = this.getNode(leveName);
		if (level != null && level instanceof Level) {
			Set<MDRelation> rels = getEdgesOfNode(level);
			if (!rels.isEmpty()) {
				for (MDRelation rel : rels) {
					if (rel instanceof QB4OInHierarchy) {
						if (rel.getTarget() != null && rel.getTarget() instanceof QB4OHierarchy) {
							hiers.add((QB4OHierarchy) rel.getTarget());
						}
					}
				}
			}
		}
		return hiers;
	}

}
