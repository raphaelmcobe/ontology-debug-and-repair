package algorithms.blackbox.kernel;

import algorithms.blackbox.BlackBoxExpansionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Collections;
import java.util.Set;

/**
 * Created by rocknroll on 24/08/14.
 */
public class ClassicalBlackBoxKernelExpansionStrategy extends BlackBoxExpansionStrategy {


	public ClassicalBlackBoxKernelExpansionStrategy(OWLOntologyManager manager) {
		super(manager);
	}


	@Override
	public Set<OWLAxiom> expand(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		Set<OWLAxiom> toReturn;
		OWLOntology ontology = manager.createOntology(kb);
		if(isEntailed(ontology,entailment)) {
			toReturn = kb;
		}
		else {
			toReturn = Collections.<OWLAxiom>emptySet();
		}
		this.totalTime = System.currentTimeMillis()-this.totalTime;
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed() - this.usedMemory;
		return toReturn;
	}

}
