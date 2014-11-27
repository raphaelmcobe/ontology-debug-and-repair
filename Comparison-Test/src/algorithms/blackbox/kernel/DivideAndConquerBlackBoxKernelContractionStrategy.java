package algorithms.blackbox.kernel;

import algorithms.blackbox.BlackBox;
import algorithms.blackbox.BlackBoxContractionStrategy;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import util.SanityChecker;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rocknroll on 25/08/14.
 */
public class DivideAndConquerBlackBoxKernelContractionStrategy extends BlackBoxContractionStrategy {
	public DivideAndConquerBlackBoxKernelContractionStrategy(OWLOntologyManager manager) {
		super(manager);
	}

	@Override
	public Set<OWLAxiom> contract(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		Set<OWLAxiom> toReturn = this.divideAndConquerBlackBox(new ArrayList<OWLAxiom>(kb),entailment);
		this.totalTime = System.currentTimeMillis()-this.totalTime;
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed() - this.usedMemory;
		return toReturn;
	}

	private  Set<OWLAxiom> divideAndConquerBlackBox(List<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException{
		List<OWLAxiom> l1 = kb.subList(0, kb.size()/2);
		List<OWLAxiom> l2 = kb.subList(kb.size()/2, kb.size());
		Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();

		if (kb.size() <= 3) {
			BlackBoxContractionStrategy classicalContraction = new ClassicalBlackBoxKernelContractionStrategy(manager);
			toReturn = classicalContraction.contract(new HashSet<OWLAxiom>(kb), entailment);
			this.totalTime += classicalContraction.getTotalTime();
			this.reasonerCalls.addAll(classicalContraction.reasonerCallsTimes());
			return toReturn;
		}

		OWLOntology ontology = this.manager.createOntology(new HashSet<OWLAxiom>(l1));

		if(isEntailed(ontology,entailment)){
			return divideAndConquerBlackBox(l1,entailment);
		}
		else{
			ontology = manager.createOntology(new HashSet<OWLAxiom>(l2));
			if(isEntailed(ontology,entailment)){
				return divideAndConquerBlackBox(l2,entailment);
			}
			else{
				List<OWLAxiom> l11 = new ArrayList<OWLAxiom>(l1.subList(0, l1.size()/2));
				l11.addAll(l2);
				ontology = manager.createOntology(new HashSet<>(l11));
				if(isEntailed(ontology,entailment)){
//					System.out.println("L11");
					return divideAndConquerBlackBox(l11,entailment);
				}
				else{
					List<OWLAxiom> l12 = new ArrayList<OWLAxiom>(l1.subList(l1.size()/2,l1.size()));
					l12.addAll(l2);
					ontology = manager.createOntology(new HashSet<OWLAxiom>(l12));
					if(isEntailed(ontology,entailment)){
						return divideAndConquerBlackBox(l12,entailment);
					}
					else{
						List<OWLAxiom> l21 = new ArrayList<OWLAxiom>(l2.subList(0, l2.size()/2));
						l21.addAll(l1);
						ontology = manager.createOntology(new HashSet<OWLAxiom>(l21));
						if(isEntailed(ontology,entailment)){
							return divideAndConquerBlackBox(l21,entailment);
						}
						else{
							List<OWLAxiom> l22 = new ArrayList<OWLAxiom>(l2.subList(l2.size()/2, l2.size()));
							l22.addAll(l1);
							ontology = manager.createOntology(new HashSet<OWLAxiom>(l22));
							if(isEntailed(ontology,entailment)){
								return divideAndConquerBlackBox(l22,entailment);
							}
							else {
								BlackBoxContractionStrategy classicalContraction = new ClassicalBlackBoxKernelContractionStrategy(manager);
								toReturn = classicalContraction.contract(new HashSet<OWLAxiom>(kb), entailment);
								this.totalTime+= classicalContraction.getTotalTime();
								this.reasonerCalls.addAll(classicalContraction.reasonerCallsTimes());
								return toReturn;
							}
						}
					}
				}
			}
		}
	}


	public static void main(String[] args) throws OWLOntologyCreationException {
		String type = "Large";
		for (int i = 1; i <= 16; i++) {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("benchmark/input/" + type + "KernelEntailment/" + type + "KernelWith" + i + "classes.owl"));
			IRI ontologyIRI = IRI.create("http://www.ime.usp.br/liamf/ontologies/" + type + "KernelWith" + i + "Classes.owl");
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLAxiom entailment = null;
			if(type.equals("Small")) {
				OWLClass clsA = factory.getOWLClass(IRI.create(ontologyIRI + "#A"));
				OWLClass clsC = factory.getOWLClass(IRI.create(ontologyIRI + "#C"));
				entailment = factory.getOWLSubClassOfAxiom(clsA, clsC);
			}else if(type.equals("Large")){
				OWLClass clsBN= factory.getOWLClass(IRI.create(ontologyIRI + "#B"+i));
				OWLClass clsBNPrime= factory.getOWLClass(IRI.create(ontologyIRI + "#B"+i+"Prime"));
				OWLIndividual aIndividual = factory.getOWLNamedIndividual(IRI.create(ontologyIRI+"#a"));
				OWLObjectUnionOf unionOf = factory.getOWLObjectUnionOf(clsBN,clsBNPrime);
				OWLClassAssertionAxiom aSubClassOfBNUnionBNPrime = factory.getOWLClassAssertionAxiom(unionOf,aIndividual);
				entailment = aSubClassOfBNUnionBNPrime;
			}
			Set<OWLAxiom> kb = ontology.getABoxAxioms(false);
			kb.addAll(ontology.getTBoxAxioms(false));
//			Util.print(entailment);
//			System.out.println();
			BlackBox blackBox = new BlackBoxKernel(new ClassicalBlackBoxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager));

//			System.out.println("=================KB=================");
//			Util.print(kb);
//			System.out.println("=================KB=================");
			Set<OWLAxiom> kernel = blackBox.blackBox(kb, entailment);
			System.out.println(new DivideAndConquerBlackBoxKernelContractionStrategy(manager).isEntailed(manager.createOntology(kernel),entailment) + " "+SanityChecker.checkSingleKernelSanity(kernel,entailment));
			System.out.format("%-20d \n", kernel.size());
		}
	}
}
