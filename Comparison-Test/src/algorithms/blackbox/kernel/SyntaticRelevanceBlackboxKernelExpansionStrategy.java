package algorithms.blackbox.kernel;

import algorithms.blackbox.BlackBox;
import algorithms.blackbox.BlackBoxExpansionStrategy;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Collections;
import java.util.Set;

/**
 * Created by rocknroll on 25/08/14.
 */
public class SyntaticRelevanceBlackboxKernelExpansionStrategy extends BlackBoxExpansionStrategy {

	public SyntaticRelevanceBlackboxKernelExpansionStrategy(OWLOntologyManager manager) {
		super(manager);
	}

	@Override
	public Set<OWLAxiom> expand(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		Set<OWLAxiom> toReturn = Collections.<OWLAxiom>emptySet();;
		Set<OWLClass> classesInSignature = entailment.getClassesInSignature();
		OWLOntology ontology =  manager.createOntology();
		for (OWLAxiom axiom : kb){
			Set<OWLClass> classesInSignatureToCompare = axiom.getClassesInSignature();
			classesInSignatureToCompare.retainAll(classesInSignature);
			if(classesInSignatureToCompare.size() != 0){
				manager.addAxiom(ontology,axiom);
				if(isEntailed(ontology,entailment)) {
					toReturn = ontology.getAxioms();
					break;
				}
			}
		}


		if(!isEntailed(ontology,entailment)){
			ontology = manager.createOntology(kb);
			if(isEntailed(ontology,entailment)) {
				toReturn = ontology.getAxioms();
			}
		}
		this.totalTime = System.currentTimeMillis()-this.totalTime;
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed() - this.usedMemory;
		return toReturn;
	}


	public static void main(String[] args) throws OWLOntologyCreationException {
		for (int i = 1; i < 16; i++) {

			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("benchmark/input/SmallKernelEntailment/SmallKernelWith" + i + "classes.owl"));
			IRI ontologyIRI = IRI.create("http://www.ime.usp.br/liamf/ontologies/SmallKernelWith" + i + "Classes.owl");
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLClass clsA = factory.getOWLClass(IRI.create(ontologyIRI + "#A"));
			OWLClass clsC = factory.getOWLClass(IRI.create(ontologyIRI + "#C"));
			OWLAxiom entailment = factory.getOWLSubClassOfAxiom(clsA, clsC);
			Set<OWLAxiom> kb = ontology.getABoxAxioms(false);
			kb.addAll(ontology.getTBoxAxioms(false));


			BlackBox classicalExpansion_classicalContraction = new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new ClassicalBlackBoxKernelContractionStrategy(manager));

			Set<OWLAxiom> kernel = classicalExpansion_classicalContraction.blackBox(kb, entailment);
			System.out.format("%-20d \n", kernel.size());
		}
	}

}
