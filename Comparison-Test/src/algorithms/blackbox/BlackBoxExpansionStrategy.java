package algorithms.blackbox;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Set;

/**
 * Created by rocknroll on 21/08/14.
 */
public abstract class BlackBoxExpansionStrategy extends OperatorStrategy {


	public BlackBoxExpansionStrategy(OWLOntologyManager manager) {
		super(manager);
	}


	public abstract Set<OWLAxiom> expand(Set<OWLAxiom> ontology, OWLAxiom entailment) throws OWLOntologyCreationException;





}
