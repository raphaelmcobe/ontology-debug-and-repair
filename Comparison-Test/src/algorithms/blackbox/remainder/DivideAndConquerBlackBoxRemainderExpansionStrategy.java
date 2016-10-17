package algorithms.blackbox.remainder;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.*;

/**
 * Created by rocknroll on 07/09/14.
 */
public class DivideAndConquerBlackBoxRemainderExpansionStrategy extends BlackBoxRemainderExpansionStrategy {
	public DivideAndConquerBlackBoxRemainderExpansionStrategy(OWLOntologyManager manager) {
		super(manager);
	}

	@Override
	public Set<OWLAxiom> expand(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		return findRemainderElement(this.getRemains(),kb,entailment);
	}

	public Set<OWLAxiom> findRemainderElement(Set<OWLAxiom> kb, Set<OWLAxiom> hn, OWLAxiom entailment) throws OWLOntologyCreationException {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.createOntology();
		List<OWLAxiom> allAxioms = new ArrayList<>();
		allAxioms.addAll(kb);
		Collections.shuffle(allAxioms);
		if (hn == null)
			hn = new HashSet<>();
		allAxioms.removeAll(hn);

		Set<OWLAxiom> toReturn = remainderElement(ontology, 0, allAxioms.size(), allAxioms, hn, entailment);
		toReturn.addAll(hn);
		this.totalTime = System.currentTimeMillis() - this.totalTime;
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed() - this.usedMemory;

		return toReturn;

	}

	private Set<OWLAxiom> remainderElement(OWLOntology ontology, int start, int end, List<OWLAxiom> allAxioms, Set<OWLAxiom> hn, OWLAxiom entailment) throws OWLOntologyCreationException {
		manager.addAxioms(ontology, hn);
		boolean condition = false;
		condition = !isEntailed(ontology,entailment);
		manager.removeAxioms(ontology, hn);
		if (!condition) {
			return ontology.getAxioms();
		}

		if (end - start == 1) {

			manager.addAxioms(ontology, new HashSet<>(allAxioms.subList(start, end)));
			manager.addAxioms(ontology, hn);
			condition = !isEntailed(ontology,entailment);
			manager.removeAxioms(ontology, hn);
			if (condition) {
				return ontology.getAxioms();
			} else {
				manager.removeAxioms(ontology, new HashSet<>(allAxioms.subList(start, end)));
			}
			return new HashSet<>();
		}

		int middle = (start + end) / 2;

		manager.addAxioms(ontology, new HashSet<>(allAxioms.subList(start, middle)));
		manager.addAxioms(ontology, hn);
		condition = !isEntailed(ontology,entailment);
		manager.removeAxioms(ontology, hn);
		if (condition) {
			manager.addAxioms(ontology, remainderElement(ontology, middle, end, allAxioms, hn, entailment));
		} else {
			manager.removeAxioms(ontology, new HashSet<>(allAxioms.subList(start, middle)));
			manager.addAxioms(ontology, new HashSet<>(allAxioms.subList(middle, end)));

			manager.addAxioms(ontology, hn);
			condition = !isEntailed(ontology,entailment);
			manager.removeAxioms(ontology, hn);

			if (condition) {
				manager.addAxioms(ontology, remainderElement(ontology, start, middle, allAxioms, hn, entailment));
			} else {
				manager.removeAxioms(ontology, new HashSet<>(allAxioms.subList(middle, end)));
				remainderElement(ontology, start, middle, allAxioms, hn, entailment);
				remainderElement(ontology, middle, end, allAxioms, hn, entailment);
			}
		}
		return ontology.getAxioms();
	}


}
