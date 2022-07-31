package swag.md_elements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MDSchemaGraphUriBasedOps extends MDSchemaGraphQB4OLAP {

	public MDSchemaGraphUriBasedOps(String name, String uri, String nameSpace, String endpoint,
			String preferredLabelProperty, MDSchemaType type) {
		super(name, uri, nameSpace, endpoint, preferredLabelProperty, type);
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

}
