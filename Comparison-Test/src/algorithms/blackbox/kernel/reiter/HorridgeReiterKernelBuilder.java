package algorithms.blackbox.kernel.reiter;

import algorithms.blackbox.BlackBox;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.semanticweb.owl.explanation.api.*;
import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rocknroll on 21/09/14.
 */
public class HorridgeReiterKernelBuilder extends ReiterKernelBuilder {
	public HorridgeReiterKernelBuilder(BlackBox blackBox, OWLOntologyManager manager) {
		super(blackBox, manager);
	}

	@Override
	public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		OWLOntology ontology = manager.createOntology(kb);
		ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(new PelletReasonerFactory());
		ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(ontology);
		Set<Explanation<OWLAxiom>> expl = gen.getExplanations(entailment,Integer.MAX_VALUE);

		Set<Set<OWLAxiom>> kernelSet = new HashSet<Set<OWLAxiom>>();

		for(Explanation<OWLAxiom> explanation: expl){
			kernelSet.add(explanation.getAxioms());
		}

		return kernelSet;
	}


	public void reset(){
		for(OWLOntology ontology : this.manager.getOntologies()){
			this.manager.removeOntology(ontology);
		}
	}
}
