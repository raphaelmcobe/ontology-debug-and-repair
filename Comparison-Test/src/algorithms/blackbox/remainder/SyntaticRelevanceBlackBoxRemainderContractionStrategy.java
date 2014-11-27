package algorithms.blackbox.remainder;

import org.semanticweb.owlapi.model.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Collections;
import java.util.Set;

/**
 * Created by rocknroll on 11/09/14.
 */
public class SyntaticRelevanceBlackBoxRemainderContractionStrategy extends BlackBoxRemainderContractionStrategy {
	public SyntaticRelevanceBlackBoxRemainderContractionStrategy(OWLOntologyManager manager) {
		super(manager);
	}

	@Override
	public Set<OWLAxiom> contract(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		Set<OWLAxiom> toReturn = Collections.<OWLAxiom>emptySet();;
		Set<OWLClass> classesInSignature = entailment.getClassesInSignature();
		OWLOntology ontology =  manager.createOntology(kb);
		for (OWLAxiom axiom : kb){
			Set<OWLClass> classesInSignatureToCompare = axiom.getClassesInSignature();
			classesInSignatureToCompare.retainAll(classesInSignature);
			if(classesInSignatureToCompare.size() != 0){
				manager.removeAxiom(ontology,axiom);
				Set<OWLAxiom> remains = this.getRemains();
				remains.add(axiom);
				this.setRemains(remains);
 				if(!isEntailed(ontology,entailment)) {
					toReturn = ontology.getAxioms();
					break;
				}
			}
		}

		if(isEntailed(ontology,entailment)){
			ClassicalBlackBoxRemainderContractionStrategy blackbox = new ClassicalBlackBoxRemainderContractionStrategy(manager);
			toReturn = blackbox.contract(ontology.getAxioms(),entailment);
			Set<OWLAxiom> remains = this.getRemains();
			remains.addAll(blackbox.getRemains());
			this.reasonerCalls.addAll(blackbox.reasonerCallsTimes());
			this.setRemains(remains);

		}
		this.totalTime = System.currentTimeMillis()-this.totalTime;
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed() - this.usedMemory;
		return toReturn;
	}
}
