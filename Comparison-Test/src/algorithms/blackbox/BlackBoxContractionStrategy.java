package algorithms.blackbox;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Set;

/**
 * Created by rocknroll on 21/08/14.
 */
public abstract class BlackBoxContractionStrategy extends OperatorStrategy {

	public BlackBoxContractionStrategy(OWLOntologyManager manager) {
		super(manager);
	}

	public abstract Set<OWLAxiom> contract(Set<OWLAxiom> ontology, OWLAxiom entailment) throws OWLOntologyCreationException;

}
