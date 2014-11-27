package algorithms.blackbox.remainder;

import algorithms.blackbox.BlackBoxExpansionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Set;

/**
 * Created by rocknroll on 07/09/14.
 */
public abstract class BlackBoxRemainderExpansionStrategy extends BlackBoxExpansionStrategy {
	private Set<OWLAxiom> remains;

	public BlackBoxRemainderExpansionStrategy(OWLOntologyManager manager) {
		super(manager);
	}

	public Set<OWLAxiom> getRemains() {
		return remains;
	}

	public void setRemains(Set<OWLAxiom> remains) {
		this.remains = remains;
	}


	public BlackBoxRemainderExpansionStrategy(OWLOntologyManager manager, Set<OWLAxiom> remains) {
		super(manager);
		this.remains = remains;
	}

}
