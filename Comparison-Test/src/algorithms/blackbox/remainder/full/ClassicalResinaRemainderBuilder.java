package algorithms.blackbox.remainder.full;

import algorithms.blackbox.BlackBox;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class ClassicalResinaRemainderBuilder extends ResinaRemainderBuilder {


	protected ClassicalResinaRemainderBuilder(BlackBox blackBox, OWLOntologyManager manager) {
		super(blackBox, manager);
	}

	public Set<Set<OWLAxiom>> remainderSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException, OWLOntologyChangeException {
		totalTime = System.currentTimeMillis();
		Set<Set<OWLAxiom>> remainderSet = new HashSet<>();
		boolean isConsistencyCheck = false;

		if (entailment != null) {
			if (entailment.isOfType(AxiomType.SUBCLASS_OF)) {
				OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) entailment;
				OWLClassExpression left = subClassAxiom.getSubClass();
				OWLClassExpression right = subClassAxiom.getSuperClass();
				if (left.isOWLThing() && right.isOWLNothing()) {
					isConsistencyCheck = true;
				}
			}
		}
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(manager.createOntology(kb));
		// Reasoner reasoner = new Reasoner(B);
		manager.addOntologyChangeListener(reasoner);

		Queue<Set<OWLAxiom>> queue = new LinkedList<Set<OWLAxiom>>();
		Set<OWLAxiom> element = null;
		Set<OWLAxiom> diff = null;
		Set<OWLAxiom> hn = null;
		boolean condition = false;
		if (isConsistencyCheck) {
			double time = System.currentTimeMillis();
			condition = reasoner.isConsistent();
		} else {
			double time = System.currentTimeMillis();
			condition = !reasoner.isEntailed(entailment);
		}
		if (condition) {
			return remainderSet;
		}

		element = this.blackBox.blackBox(kb, entailment);
		remainderSet.add(element);
		diff = new HashSet<OWLAxiom>();
		diff.addAll(kb);
		diff.removeAll(element);
		for (OWLAxiom axiom : diff) {
			Set<OWLAxiom> set = new HashSet<OWLAxiom>();
			set.add(axiom);
			queue.add(set);
		}

		OWLOntology ont = manager.createOntology();
		reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);

		while (!queue.isEmpty()) {
			hn = queue.remove();
			manager.addAxioms(ont, hn);
			if (isConsistencyCheck) {
				condition = reasoner.isConsistent();
			} else {
				condition = !reasoner.isEntailed(entailment);
			}
			manager.removeAxioms(ont, hn);
			if (!condition) {
				continue;
			}
			element = this.blackBox.blackBox(kb, entailment);
			remainderSet.add(element);
			diff.addAll(kb);
			diff.removeAll(element);
			for (OWLAxiom axiom : diff) {
				Set<OWLAxiom> set = new HashSet<OWLAxiom>();
				set.addAll(hn);
				set.add(axiom);
				queue.add(set);
			}
		}
		totalTime = System.currentTimeMillis() - totalTime;
		return remainderSet;
	}
}
