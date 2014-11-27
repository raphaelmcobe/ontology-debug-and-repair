package algorithms.blackbox.remainder;

import algorithms.blackbox.BlackBoxContractionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rocknroll on 07/09/14.
 */
public abstract class BlackBoxRemainderContractionStrategy extends BlackBoxContractionStrategy {

	private Set<OWLAxiom> remains;

	public BlackBoxRemainderContractionStrategy(OWLOntologyManager manager) {
		super(manager);
		this.remains = new HashSet<OWLAxiom>();
	}

	public Set<OWLAxiom> getRemains() {
		return remains;
	}

	public void setRemains(Set<OWLAxiom> remains) {
		this.remains = remains;
	}
}