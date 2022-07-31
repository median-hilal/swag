package swag.md_elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToBaseMeasureCondition;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituationToResultFilters;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureAggregated;
import swag.analysis_graphs.execution_engine.analysis_situations.MeasureDerived;
import swag.graph.Graph;
import swag.graph.Path;
import swag.helpers.AutoCompleteData;
import swag.sparql_builder.Configuration;

/**
 * 
 * Basic methods of an MD schema.
 * 
 * @author swag
 *
 */
public interface MDSchema extends Graph<MDElement, MDRelation> {

	public List<MDElement> getPath(List<MDElement> path, String startLevel, String endLevel, String dimension);

	public MDRelation getRollUpOrHasAttributeProperty(String l1, String d1, String l2, String d2);

	public Level getLevelOfDescriptor1(String descriptorURI);
	/**
	 * 
	 * Gets all MD elements leading to the element specified by the passed
	 * {@code uri}.
	 * 
	 * @param uri
	 * @return
	 * 
	 */
	public default Set<MDRelation> getRelsLeadingToElment(String uri) {

		Set<MDRelation> elms = new HashSet<>();
		Set<MDRelation> allRels = getAllEdges();
		for (MDRelation rel : allRels) {
			if (rel.getTarget().getIdentifyingName().equals(uri)) {
				elms.add(rel);
			}
		}
		return elms;
	}

	/**
	 * 
	 * Gets the MD schema type (SMD/QB4OLAP)
	 * 
	 * @return
	 */
	public MDSchemaType getSchemaType();

	/**
	 * 
	 * Adds an MD element along with its MD relations to the graph (a new entry of
	 * key value pair).
	 * 
	 * @param elem the element to add
	 * @param rels the MD relations of the element to add.
	 * 
	 * @return true if the pair is added, false otherwise.
	 * 
	 */
	public boolean addEntry(MDElement elem, Set<MDRelation> rels);

	/**
	 * 
	 * Removes an MD element along with its MD relations from the graph (remove an
	 * entry of key value pair).
	 * 
	 * @param elem the element to remove
	 * @param rels the MD relations of the element to remove
	 * 
	 * @return true if the pair is removed, false otherwise.
	 * 
	 */
	public boolean removeEntry(MDElement elem);

	/**
	 * 
	 * Copies an MD element along with its MD relations to the graph (a copy entry
	 * of key value pair).
	 * 
	 * @param elem the element to copy
	 * 
	 * @return a map with a single entry of the copied pair.
	 * 
	 */
	public Map<MDElement, Set<MDRelation>> copyEntry(MDElement elem);

	/**
	 * 
	 * Gets the querying endpoint of the schema
	 * 
	 * @return the endpoint link
	 * 
	 */
	public String getEndpoint();

	/**
	 * 
	 * URI may not be unique, only identifying name is unique. This function gets
	 * nodes that share the same URI.
	 * 
	 * @return
	 * 
	 */
	public Set<MDElement> getNodesByURI(String uri);

	/**
	 * 
	 * For cases where same element can be used in multiple dimensions.
	 * 
	 * @param uri
	 * @return
	 */
	public String getIdentifyingNameFromUriAndDimensionAndHier(String elemUri, String dimURI, String hierURI);

	/**
	 * 
	 * For cases where same level attribute can be used in multiple levels.
	 * 
	 * @param uri
	 * @return
	 */
	public String getLevelAttributeIdentifyingNameFromUriAndDimensionAndHier(String elemUri,
			String levelIdentifyingName);

	public String getName();

	public String getURI();

	public String getNameSpace();

	/**
	 * 
	 * Gets the property used for the labels in both the schema and instances.
	 * 
	 * @return
	 */
	public String getPreferredLabelProperty();

	/**
	 * @param currentElemURI
	 * @return
	 */
	public MDElement getDescendantMDElement(String currentElemURI);

	/**
	 * @param hierUri
	 * @param dimensionURI
	 * @return
	 */
	public default Set<MDElement> getAllElementsOnHierarchyInDimension(String hierUri, String dimensionURI) {

		Set<MDElement> elems = new HashSet<>();
		Set<Level> levels = getAllLevelsOnHierarchyInDimension(hierUri, dimensionURI);
		for (Level lvl : levels) {
			elems.add(lvl);
			if (lvl != null) {
				elems.addAll(getDescriptors(lvl.getIdentifyingName()));
			}
		}
		return elems;
	}

	/**
	 * 
	 * Returns all elements that are either on a hierarchy in dimension or all base
	 * measures or all aggregated measures.
	 * 
	 * @param hierUri
	 * @param dimensionURI
	 * @return
	 */
	public default Set<MDElement> getAllElements(String uri) {

		Set<MDElement> elems = new HashSet<>();

		// Hierarchy in dimension
		if (this.getNode(uri) instanceof HierarchyInDimension) {

			HierarchyInDimension hm = (HierarchyInDimension) this.getNode(uri);
			QB4OHierarchy h = hm.getHier();
			Dimension d = hm.getDim();

			if (h.equals(DefaultHierarchy.getDefaultHierarchy())) {
				for (QB4OHierarchy hInD : getHierarchiesOnDimension(d.getURI())) {
					Set<Level> levels = getAllLevelsOnHierarchyInDimension(hInD.getURI(), d.getURI());
					for (Level lvl : levels) {
						elems.add(lvl);
						if (lvl != null) {
							elems.addAll(getDescriptors(lvl.getIdentifyingName()));
						}
					}
				}
				return elems;
			}

			Set<Level> levels = getAllLevelsOnHierarchyInDimension(h.getURI(), d.getURI());
			for (Level lvl : levels) {
				elems.add(lvl);
				if (lvl != null) {
					elems.addAll(getDescriptors(lvl.getIdentifyingName()));
				}
			}
			return elems;
		}

		// BaseMeasureCondition
		if (uri.equals(AnalysisSituationToBaseMeasureCondition.class.getSimpleName())) {
			elems.addAll(getAllNodesOfType(MeasureDerived.class));
			return elems;
		}

		// Result Filters
		if (uri.equals(AnalysisSituationToResultFilters.class.getSimpleName())) {
			elems.addAll(getAllNodesOfType(MeasureAggregated.class));
			return elems;
		}

		return elems;
	}

	/**
	 * 
	 * Gets all the elements of a given type
	 * 
	 * @param clazz the type of the elements to get
	 * 
	 * @return a set of elements of type T
	 * 
	 */
	public default <T extends MDElement> Set<T> getAllNodesOfType(Class<T> clazz) {

		Set<T> elems = new HashSet<>();

		for (MDElement elem : getAllNodes()) {
			if (elem.getClass().equals(clazz)) {
				elems.add(clazz.cast(elem));
			}
		}
		return elems;
	}

	public default Set<Level> getAllLevelsOnHierarchyInDimension(String hierUri, String dimensionURI) {

		Set<Level> elems = new HashSet<>();

		for (MDElement elem : getAllNodes()) {
			if (elem instanceof QB4OHierarchy && elem.equals(getNode(hierUri))) {
				for (MDRelation rel : getEdgesOfNode(elem)) {
					if (rel instanceof QB4OHierarchyInDimension) {
						if (rel.getTarget().equals(getNode(dimensionURI))) {
							for (MDRelation rel1 : getAllEdges()) {
								if (rel1 instanceof QB4OInHierarchy && rel1.getTarget().equals(elem)) {
									elems.add((Level) rel1.getSource());
								}
							}
						}
					}
				}
			}
		}
		return elems;
	}

	/**
	 * 
	 * Gets the fact of the schema, null if not found.
	 * 
	 * @return
	 */
	public Fact getFactOfSchema();

	public default List<Dimension> getAllDimension() {
		return getAllNodes().stream().filter(x -> x instanceof Dimension).map(x -> (Dimension) x)
				.collect(Collectors.toList());
	}

	public default List<QB4OHierarchy> getHierarchiesOnDimension(String dimUri) {

		List<QB4OHierarchy> allHier = getAllNodes().stream().filter(x -> x instanceof QB4OHierarchy)
				.map(x -> (QB4OHierarchy) x).collect(Collectors.toList());

		List<QB4OHierarchy> dimHiers = new ArrayList<>();

		for (QB4OHierarchy h : allHier) {
			for (MDRelation rel : getEdgesOfNode(h)) {
				if (rel instanceof QB4OHierarchyInDimension) {
					if (rel.getTarget().equals(getNode(dimUri))) {
						dimHiers.add(h);
						continue;
					}
				}
			}
		}
		return dimHiers;
	}

	public default void generateHierarchyIndimensionElements() {

		for (Dimension dim : getAllDimension()) {
			for (QB4OHierarchy hier : getHierarchiesOnDimension(dim.getIdentifyingName())) {
				addNode(new HierarchyInDimension(hier, dim));
			}
		}

		if (Configuration.getInstance().is("singleHierarchy")) {
			for (Dimension dim : getAllDimension()) {
				addNode(new HierarchyInDimension(DefaultHierarchy.getDefaultHierarchy(), dim));
			}
		}
	}

	public default HierarchyInDimension getHierarchyInDimensionNode(String dimStr, String hStr) {

		Dimension dim = (Dimension) getNode(dimStr);
		QB4OHierarchy h = (QB4OHierarchy) getNode(hStr);

		// @formatter:off
		return getAllNodes().stream()
				.filter(x -> (x instanceof HierarchyInDimension) && x.getURI().equals(h.getURI() + dim.getURI()))
				.map(x -> (HierarchyInDimension) x).findFirst().orElse(null);
		// @formatter:on
	}

	public default HierarchyInDimension getHierarchyInDimensionNode(Dimension dim, QB4OHierarchy h) {
		// @formatter:off
		return getAllNodes().stream()
				.filter(x -> (x instanceof HierarchyInDimension) && x.getURI().equals(h.getURI() + dim.getURI()))
				.map(x -> (HierarchyInDimension) x).findFirst().orElse(null);
		// @formatter:on
	}

	/**
	 * 
	 * Gets the next (rolled up to) level, null if not found.
	 * 
	 * @return
	 */
	public Level getNextLevel(String currentLevelName);

	/**
	 * 
	 * Gets the next (rolled up to) level, null if not found.
	 * 
	 * @return
	 */
	public default Level getNextLevelInHierarchy(String currentLevelName, String hierUri, String dimensionURI) {
		for (Level l : getAllLevelsOnHierarchyInDimension(hierUri, dimensionURI)) {
			for (MDRelation rel : getEdgesOfNode(l)) {
				if (rel instanceof QB4OHierarchyStep) {
					if ((getAllLevelsOnHierarchyInDimension(hierUri, dimensionURI)).contains(rel.getTarget())) {
						return (Level) rel.getTarget();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * Gets the descriptors (non-dimensional) attributes of a level
	 * 
	 * @param currentLevelName the level to get descriptors of
	 * 
	 * @return a list of Descriptors of the level; empty list if non exists
	 * 
	 */
	public List<Descriptor> getDescriptors(String currentLevelName);

	/**
	 * 
	 * checks if level1 rolls up (directly or indirectly) to level2.
	 * 
	 * @param level1 first (lower) level
	 * @param level2 second (upper) level
	 * 
	 * @return true if level1 rolls up (directly or indirectly) to level2, false
	 *         otherwise.
	 * 
	 */
	public boolean rollsUpDirectlyOrIndirectlyTo(String level1, String level2);

	/**
	 * 
	 * Checks if item is upper in dimension than level, i.e., item is either a non
	 * dimensional attribute of level or level rolls up to item.
	 * 
	 * @param level
	 * @param item
	 * 
	 * @return true if item is either a non-dimensional attribute of level or level
	 *         rolls up to item; false otherwise
	 * 
	 */
	public boolean isItemDescendantForLevel(String level, String item);

	/**
	 * 
	 * compares the order of level and item in the dimension.
	 * 
	 * @param level
	 * @param item
	 * 
	 * @return 2: level either rolls up to item or item is a descriptor of level. 1:
	 *         level and item are equal 0: item rolls up to level. -1: no common
	 *         path; item and level are incomparable
	 * 
	 */
	public int compareItemsInDimension(String level, String item);

	/**
	 * 
	 * Gets the MDElement that level and item share on the path.
	 * 
	 * @param level
	 * @param item
	 * 
	 * @return the closest common descendant of level and item; null if none found
	 * 
	 */
	public MDElement getLeastCommonAncestor(String level, String item);

	/**
	 * 
	 * Gets the level of a descriptor.
	 * 
	 * @param DescriptorURI
	 * 
	 * @return the level, null if not found
	 * 
	 */
	public MDElement getLevelOfDescriptor(String DescriptorURI);

	/**
	 * 
	 * Returns the finest granularity level on a dimension.
	 * 
	 * @param dimensionURI the URI of the dimension
	 * 
	 * @return the finest granularity level on the dimension; null if non found
	 * 
	 */
	public Level getFinestLevelOnDimension1(String dimensionURI);

	/**
	 * 
	 * Returns the finest granularity level on a dimension.
	 * 
	 * @param dimensionURI the URI of the dimension
	 * 
	 * @return the finest granularity level on the dimension; null if non found
	 * 
	 */
	public Level getFinestLevelOnDimension(String dimensionURI);

	/**
	 * 
	 * Returns all the MDElements on a dimension; i.e., levels and descriptors.
	 * 
	 * @param dimensionURI the URI of the dimension
	 * 
	 * @return a set of MD elements belonging to the dimenison.
	 * 
	 */
	public Set<MDElement> getAllElementsOnDimension(String dimensionURI);

	/**
	 * 
	 * Gets the previous (rolled up from) level, null if not found.
	 * 
	 * @return the previous level
	 * 
	 */
	public Level getPreviousLevel(String currentLevelName);

	/**
	 * 
	 * Gets all the levels in the dimension.
	 * 
	 * @param dimensionURI the URI of the dimenison.
	 * 
	 * @return a list of levels URIs
	 * 
	 */
	public Set<String> getUniquePossibleLevelsOnDimension(String dimensionURI);

	public Set<AutoCompleteData> getUniquePossibleLevelsOnDimensionWithLabels(String dimensionURI);

	public List<String> getPossibleLevelsOnDimension(String dimensionURI);

	public Set<MDRelation> getMappedOutEdgesOfNode(String nodeUri);

	/**
	 * 
	 * returns the paths between two nodes, each path is a sequence of edges. ALl
	 * the edges in the paths are edges that can have a mapping. never exploring a
	 * path twice is guaranteed on the node level; each node's out edges are
	 * explored once
	 * 
	 * @param startNode the source node; first node in the path
	 * @param endNode   the destination node; last node in the path
	 * @return A list of paths from source to destination
	 * 
	 */
	public List<Path<MDElement, MDRelation>> getAllMappedPathsBetweenTwoVertices(MDElement startNode,
			MDElement endNode);

	public default Set<Level> getAllLevels() {
		Set<Level> levels = new HashSet<>();

		for (MDElement elem : this.getAllNodes()) {
			if (elem instanceof Level) {
				levels.add((Level) elem);
			}
		}

		return levels;
	}

	@Override
	public default Set<MDElement> getNodesByIdentifyingNameSimilarity(String name) {
		return getAllNodes().stream().filter(x -> x.getIdentifyingName().contains(name)).collect(Collectors.toSet());
	}

	@Override
	public default Set<MDElement> getNodesByName(String name) {
		return getAllNodes().stream().filter(x -> x.getName().contains(name)).collect(Collectors.toSet());
	}

	/**
	 * @param elmUri
	 * @return
	 */
	public default QB4OHierarchy getHierarchyOfLevelOrDescriptor(String elmUri) {
		MDElement elm = getNode(elmUri);
		if (elm instanceof Level) {
			return getHierarchyOfLevel(elmUri);
		} else {
			if (elm instanceof Descriptor) {
				return getHierarchyOfDescriptor(elmUri);
			}
		}
		return null;
	}

	/**
	 * 
	 * Gets the hierarchy on which the level occurs.
	 * 
	 * @return the hierarchy of level if exists. The default hierarchy if no
	 *         hierarchy exists for level. null if the level is not found.
	 * 
	 */
	public default QB4OHierarchy getHierarchyOfLevel(String levelIdentifyingName) {

		MDElement level = this.getNode(levelIdentifyingName);
		if (level != null && level instanceof Level) {
			Set<MDRelation> rels = getEdgesOfNode(level);
			if (!rels.isEmpty()) {
				for (MDRelation rel : rels) {
					if (rel instanceof QB4OInHierarchy) {
						if (rel.getTo() != null && rel.getTo() instanceof QB4OHierarchy) {
							return (QB4OHierarchy) rel.getTo();
						}
					}
				}
			}
		} else {
			return null;
		}
		return QB4OHierarchy.getDefaultHierarchy();
	}

	/**
	 * 
	 * Gets the hierarchy on which the level occurs.
	 * 
	 * @return the hierarchy of level if exists. The default hierarchy if no
	 *         hierarchy exists for level. null if the level is not found.
	 * 
	 */
	public default QB4OHierarchy getHierarchyOfDescriptor(String descIdentifyingName) {

		MDElement elm = getNode(descIdentifyingName);
		if (elm instanceof Descriptor) {
			return getHierarchyOfLevel(getLevelOfDescriptor(descIdentifyingName).getIdentifyingName());
		}
		return null;
	}

	/**
	 * @param elmUri
	 * @return
	 */
	public default Dimension getDimensoinOfLevelOrDescriptor(String elmUri) {
		MDElement elm = getNode(elmUri);
		if (elm instanceof Level) {
			return getDimensionOfLevel(elmUri);
		} else {
			if (elm instanceof Descriptor) {
				return getDimensoinOfDescriptor(elmUri);
			}
		}
		return null;
	}

	public default Dimension getDimensionOfLevel(String leveName) {
		MDElement level = this.getNode(leveName);
		if (level != null && level instanceof Level) {
			Set<MDRelation> rels = getEdgesOfNode(level);
			if (!rels.isEmpty()) {
				for (MDRelation rel : rels) {
					if (rel instanceof InDimension) {
						if (rel.getTo() != null && rel.getTo() instanceof Dimension) {
							return (Dimension) rel.getTo();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param elmUri
	 * @return
	 */
	public default Dimension getDimensoinOfDescriptor(String elmUri) {
		MDElement elm = getNode(elmUri);
		if (elm instanceof Descriptor) {
			return getDimensionOfLevel(getLevelOfDescriptor(elmUri).getIdentifyingName());
		}
		return null;
	}

	/**
	 * @param fromUri
	 * @param toUri
	 * @return
	 */
	public Set<MDRelation> getMappedOutEdgesOfNodeSmartly(String fromUri, String toUri);
}
