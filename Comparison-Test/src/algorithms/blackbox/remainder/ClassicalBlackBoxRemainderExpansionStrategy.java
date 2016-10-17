package algorithms.blackbox.remainder;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Set;

/**
 * Created by rocknroll on 07/09/14.
 */
public class ClassicalBlackBoxRemainderExpansionStrategy extends BlackBoxRemainderExpansionStrategy {
	public ClassicalBlackBoxRemainderExpansionStrategy(OWLOntologyManager manager) {
		super(manager);
	}

	@Override
	public Set<OWLAxiom> expand(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		OWLOntology ontology = manager.createOntology(kb);
		for(OWLAxiom axiom : this.getRemains()){
			manager.addAxiom(ontology,axiom);
			if(isEntailed(ontology,entailment)){
				manager.removeAxiom(ontology,axiom);
			}
		}
		this.totalTime = System.currentTimeMillis() - this.totalTime;
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed() - this.usedMemory;

		return ontology.getAxioms();

	}
}
