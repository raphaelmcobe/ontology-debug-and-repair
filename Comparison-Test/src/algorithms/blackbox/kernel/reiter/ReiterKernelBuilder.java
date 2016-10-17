package algorithms.blackbox.kernel.reiter;

import algorithms.blackbox.BlackBox;
import algorithms.blackbox.OperatorStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Set;

/**
 * Created by rocknroll on 20/09/14.
 */
public abstract class ReiterKernelBuilder extends OperatorStrategy{

	protected BlackBox blackBox;

	public abstract Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException;

	protected ReiterKernelBuilder(BlackBox blackBox, OWLOntologyManager manager) {
		super(manager);
		this.blackBox = blackBox;
	}
}
