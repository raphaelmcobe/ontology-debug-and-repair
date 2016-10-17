package algorithms.blackbox.kernel;

import algorithms.blackbox.BlackBoxContractionStrategy;
import algorithms.blackbox.BlackBoxExpansionStrategy;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.semanticweb.owl.explanation.api.*;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Set;

/**
 * Created by rocknroll on 25/09/14.
 */


public class HorridgeBlackBoxKernel extends BlackBoxKernel {


	private OWLOntologyManager manager;


	public HorridgeBlackBoxKernel(BlackBoxExpansionStrategy expansionStrategy, BlackBoxContractionStrategy contractionStrategy) {
		super(expansionStrategy, contractionStrategy);
	}


	public HorridgeBlackBoxKernel(OWLOntologyManager manager) {
		super(null,null);
		this.manager = manager;
	}


	@Override
	public Set<OWLAxiom> blackBox(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		OWLOntology ontology = manager.createOntology(kb);

		ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(new PelletReasonerFactory());

		ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(ontology);
		Set<Explanation<OWLAxiom>> expl = gen.getExplanations(entailment,1);
		return expl.iterator().next().getAxioms();
	}

	@Override
	public void reset() {
		for(OWLOntology ontology : this.manager.getOntologies()){
			this.manager.removeOntology(ontology);
		}
	}

}
