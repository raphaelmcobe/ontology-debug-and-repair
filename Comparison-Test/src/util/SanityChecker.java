package util;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;

public abstract class SanityChecker {
	

	public static boolean checkSingleRemainderSanity(OWLOntology kb, Set<OWLAxiom> remainder, OWLAxiom entailment){
		boolean useConsistencyCheck = false;

		if (entailment != null) {
			if (entailment.isOfType(AxiomType.SUBCLASS_OF)) {
				OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) entailment;
				OWLClassExpression left = subClassAxiom.getSubClass();
				OWLClassExpression right = subClassAxiom.getSuperClass();
				if (left.isOWLThing() && right.isOWLNothing()) {
					useConsistencyCheck = true;
				}
			}
		}
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = null;
		try {
			ont = manager.createOntology();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
		manager.addOntologyChangeListener(reasoner);
		Set<OWLAxiom> rest = new HashSet<>();
		rest.addAll(kb.getAxioms());
		rest.removeAll(remainder);
		manager.addAxioms(ont, remainder);
		
		if(useConsistencyCheck){
			if(!reasoner.isConsistent()){
				return false;
			}
		}
		else{
			if(reasoner.isEntailed(entailment)){
				return false;
			}
		}
		
		for (OWLAxiom owlAxiom : rest) {
			manager.addAxiom(ont, owlAxiom);
			if(useConsistencyCheck){
				if(reasoner.isConsistent()){
					return false;
				}
			}else{
				if(!reasoner.isEntailed(entailment)){
					return false;
				}
			}
			manager.removeAxiom(ont, owlAxiom);
		}
		return true;
	}




	public static boolean checkSingleKernelSanity(Set<OWLAxiom> kernel, OWLAxiom entailment){
		boolean useConsistencyCheck = false;

		if (entailment != null) {
			if (entailment.isOfType(AxiomType.SUBCLASS_OF)) {
				OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) entailment;
				OWLClassExpression left = subClassAxiom.getSubClass();
				OWLClassExpression right = subClassAxiom.getSuperClass();
				if (left.isOWLThing() && right.isOWLNothing()) {
					useConsistencyCheck = true;
				}
			}
		}
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = null;
		try {
			ont = manager.createOntology();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
		manager.addOntologyChangeListener(reasoner);

		manager.addAxioms(ont, kernel);

		for (OWLAxiom owlAxiom : kernel) {
			manager.removeAxiom(ont, owlAxiom);
			if(useConsistencyCheck){
				if(!reasoner.isConsistent()){
					return false;
				}
			}else{
				if(reasoner.isEntailed(entailment)){
					return false;
				}
			}
			manager.addAxiom(ont, owlAxiom);
		}
		return true;
	}


	public static boolean checkKernelSanity(Set<Set<OWLAxiom>> kernelSet, OWLAxiom entailment){
		boolean sane = true;
		for (Set<OWLAxiom> eachKernel : kernelSet){
			sane&=checkSingleKernelSanity(eachKernel,entailment);
		}
		return sane;
	}


}
