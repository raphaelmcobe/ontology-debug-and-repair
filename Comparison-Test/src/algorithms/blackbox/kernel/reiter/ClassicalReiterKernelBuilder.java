package algorithms.blackbox.kernel.reiter;

import algorithms.blackbox.BlackBox;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Created by rocknroll on 20/09/14.
 */
public class ClassicalReiterKernelBuilder extends ReiterKernelBuilder {

	public ClassicalReiterKernelBuilder(BlackBox blackBox, OWLOntologyManager manager) {
		super(blackBox, manager);
	}

	public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		Set<Set<OWLAxiom>> kernel = new HashSet<Set<OWLAxiom>>();

		Set<Set<OWLAxiom>> cut = new HashSet<Set<OWLAxiom>>();

		Queue<Set<OWLAxiom>> queue = new LinkedList<Set<OWLAxiom>>();
		Set<OWLAxiom> element;
		Set<OWLAxiom> candidate;
		Set<OWLAxiom> hn;

		OWLOntology ontology = manager.createOntology(kb);

		if (!isEntailed(ontology, entailment)) {
			return kernel;
		}
		element = this.blackBox.blackBox(ontology.getAxioms(), entailment);
		this.addToReasonerCalls(this.blackBox.getReasonerCalls());
		kernel.add(element);
		for (OWLAxiom axiom : element) {
			Set<OWLAxiom> set = new HashSet<OWLAxiom>();
			set.add(axiom);
			queue.add(set);
		}
		// Reiter's algorithm
		while (!queue.isEmpty()) {
			hn = queue.remove();
			for (OWLAxiom axiom : hn) {
				manager.removeAxiom(ontology, axiom);
			}
			if (isEntailed(ontology, entailment)) {
				candidate = blackBox.blackBox(ontology.getAxioms(), entailment);
				kernel.add(candidate);
				for (OWLAxiom axiom : candidate) {
					Set<OWLAxiom> set2 = new HashSet<OWLAxiom>();
					set2.addAll(hn);
					set2.add(axiom);
					queue.add(set2);
				}
			} else
				cut.add(hn);

			// Restore to the ontology the axioms removed so it can be used
			// again
			for (OWLAxiom axiom : hn) {
				manager.addAxiom(ontology, axiom);
			}
		}
		this.totalTime = System.currentTimeMillis()-this.totalTime;
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed() - this.usedMemory;
		return kernel;
	}

}
