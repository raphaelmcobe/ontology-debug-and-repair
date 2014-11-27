package algorithms.blackbox.kernel;

import algorithms.blackbox.BlackBox;
import algorithms.blackbox.BlackBoxContractionStrategy;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rocknroll on 24/08/14.
 */
public class SlidingWindowBlackBoxKernelContractionStrategy extends BlackBoxContractionStrategy {


	private int windowSize = 1;

	public SlidingWindowBlackBoxKernelContractionStrategy(OWLOntologyManager manager, int windowSize) {
		super(manager);
		this.windowSize = windowSize;
	}



	public SlidingWindowBlackBoxKernelContractionStrategy(OWLOntologyManager manager){
		this(manager,10);
	}

	@Override
	public Set<OWLAxiom> contract(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		if(Thread.currentThread().isInterrupted()) return null;
		OWLOntology ontology= manager.createOntology(kb);
		List<OWLAxiom> kbList = new ArrayList<OWLAxiom>(kb);
		int windowStart=0;
		while(windowStart<=kbList.size()){
			int windowEnd = (((windowStart+windowSize)>=kbList.size())? kbList.size(): windowStart+windowSize);
			manager.removeAxioms(ontology, new HashSet<OWLAxiom>(kbList.subList(windowStart,windowEnd)));
			if(!isEntailed(ontology,entailment)){
				manager.addAxioms(ontology,new HashSet<OWLAxiom>(kbList.subList(windowStart,windowEnd)));
				windowStart++;
			}
			else{
				windowStart+=windowEnd;
			}
		}
		BlackBoxContractionStrategy classicalContraction = new ClassicalBlackBoxKernelContractionStrategy(manager);
		Set<OWLAxiom> toReturn = classicalContraction.contract(ontology.getAxioms(),entailment);
		this.totalTime = System.currentTimeMillis()-this.totalTime;
		this.totalTime+= classicalContraction.getTotalTime();
		this.reasonerCalls.addAll(classicalContraction.reasonerCallsTimes());
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


			BlackBox classicalExpansion_classicalContraction = new BlackBoxKernel(new ClassicalBlackBoxKernelExpansionStrategy(manager), new SlidingWindowBlackBoxKernelContractionStrategy(manager, 10));

			Set<OWLAxiom> kernel = classicalExpansion_classicalContraction.blackBox(kb, entailment);
			System.out.format("%-20d \n", kernel.size());
		}
	}
}
