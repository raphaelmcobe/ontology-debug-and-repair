package algorithms.blackbox.remainder;

import algorithms.blackbox.BlackBox;
import algorithms.blackbox.BlackBoxContractionStrategy;
import algorithms.blackbox.BlackBoxExpansionStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rocknroll on 06/09/14.
 */
public class BlackBoxRemainder extends BlackBox {
	public BlackBoxRemainder(BlackBoxExpansionStrategy expansionStrategy, BlackBoxContractionStrategy contractionStrategy) {
		super(expansionStrategy, contractionStrategy);
	}

	@Override
	public Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment) throws OWLOntologyCreationException {
		Set<OWLAxiom> contractionResult = this.contractionStrategy.contract(ontology, entailment);
		Set<OWLAxiom> remains = ((BlackBoxRemainderContractionStrategy) this.contractionStrategy).getRemains();
		((BlackBoxRemainderExpansionStrategy) this.expansionStrategy).setRemains(remains);

		return this.expansionStrategy.expand(contractionResult, entailment);
	}

	public void reset(){
		super.reset();
		((BlackBoxRemainderExpansionStrategy) this.getExpansionStrategy()).setRemains(new HashSet<OWLAxiom>());
		((BlackBoxRemainderContractionStrategy) this.getContractionStrategy()).setRemains(new HashSet<OWLAxiom>());
	}
}
