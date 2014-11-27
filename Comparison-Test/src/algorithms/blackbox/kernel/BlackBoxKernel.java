package algorithms.blackbox.kernel;

import algorithms.blackbox.BlackBox;
import algorithms.blackbox.BlackBoxContractionStrategy;
import algorithms.blackbox.BlackBoxExpansionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.Set;

/**
 * Created by rocknroll on 06/09/14.
 */
public class BlackBoxKernel extends BlackBox {

	public BlackBoxKernel(BlackBoxExpansionStrategy expansionStrategy, BlackBoxContractionStrategy contractionStrategy) {
		super(expansionStrategy, contractionStrategy);
	}

	@Override
	public Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment) throws OWLOntologyCreationException {
		Set<OWLAxiom> toReturn = contractionStrategy.contract(expansionStrategy.expand(ontology,entailment),entailment);
		return toReturn;
	}
}
