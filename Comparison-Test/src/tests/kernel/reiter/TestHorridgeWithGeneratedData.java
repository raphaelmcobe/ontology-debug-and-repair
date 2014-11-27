package tests.kernel.reiter;

import algorithms.blackbox.kernel.BlackBoxKernel;
import algorithms.blackbox.kernel.DivideAndConquerBlackBoxKernelContractionStrategy;
import algorithms.blackbox.kernel.SyntaticRelevanceBlackboxKernelExpansionStrategy;
import algorithms.blackbox.kernel.reiter.ClassicalReiterKernelBuilder;
import algorithms.blackbox.kernel.reiter.HorridgeReiterKernelBuilder;
import algorithms.blackbox.kernel.reiter.OptimizedReiterKernelBuilder;
import algorithms.blackbox.kernel.reiter.ReiterKernelBuilder;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rocknroll on 25/08/14.
 */
public class TestHorridgeWithGeneratedData {


	static String SMALL_KERNEL_TEST_TYPE = "Small";
	static String LARGE_KERNEL_TEST_TYPE = "Large";


	private static Map<String, ReiterKernelBuilder> operators;

	private static OWLOntologyManager manager = null;


	public static void main(String[] args) throws OWLOntologyCreationException, InterruptedException, FileNotFoundException {

		operators = new HashMap<String, ReiterKernelBuilder>();
		manager = OWLManager.createOWLOntologyManager();
		operators.put("cr_sre_dacc", new ClassicalReiterKernelBuilder(new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager)), manager));
		operators.put("or_sre_dacc", new OptimizedReiterKernelBuilder(new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager)), manager));
		operators.put("hr_sre_dacc", new HorridgeReiterKernelBuilder(null, manager));


//		String type = "";
//		if (args[0].equals("-large")) {
//			type = LARGE_KERNEL_TEST_TYPE;
//		} else if (args[0].equals("-small")) {
//			type = SMALL_KERNEL_TEST_TYPE;
//		}

//		int size = Integer.parseInt(args[1]);

//		String algorithmName = args[2];
//
//		Reiter selectedReiter = operators.get(algorithmName);
//
//
		System.setErr(new PrintStream("/tmp/reintertest.log"));
//
//
//		System.out.format("%-10s", "#i");
//
//		System.out.format("%-10s", "Axioms");
//
//		System.out.format("%-15s", "size");
//
//		System.out.format("%-15s", "time");
//
//		System.out.format("%-15s", "RC");
//
////		System.out.format("%-20s", "ont_mean_size");
//
////		System.out.format("%-20s", "RC_mean_time");
//
//		System.out.format("%-20s", "RC_total_time");
//
//		System.out.format("%-15s", "mem");
//
//		System.out.format("%-10s", "sanity");
//
//		if(selectedReiter instanceof OptimizedReiter){
//			System.out.format("%-15s", "ReusedN");
//
//			System.out.format("%-15s", "EarlyPT");
//		}
//
//		System.out.println();

		String type = SMALL_KERNEL_TEST_TYPE;

		for (int size = 1; size <= 128; size++) {


			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("benchmark/input/" +type + "KernelWith" + size + "classes.owl"));
			System.out.format("%-10d", size);
			System.out.format("%-10d", ontology.getAxiomCount());
//			IRI ontologyIRI = IRI.create("http://www.ime.usp.br/liamf/ontologies/" + type + "KernelWith" + size + "Classes.owl");
//			OWLDataFactory factory = manager.getOWLDataFactory();
//			OWLAxiom entailment = null;
//			if (type.equals(SMALL_KERNEL_TEST_TYPE)) {
//				OWLClass clsA = factory.getOWLClass(IRI.create(ontologyIRI + "#A"));
//				OWLClass clsC = factory.getOWLClass(IRI.create(ontologyIRI + "#C"));
//				entailment = factory.getOWLSubClassOfAxiom(clsA, clsC);
//			} else if (type.equals(LARGE_KERNEL_TEST_TYPE)) {
//				OWLClass clsBN = factory.getOWLClass(IRI.create(ontologyIRI + "#B" + size));
//				OWLClass clsBNPrime = factory.getOWLClass(IRI.create(ontologyIRI + "#B" + size + "Prime"));
//				OWLIndividual aIndividual = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#a"));
//				OWLObjectUnionOf unionOf = factory.getOWLObjectUnionOf(clsBN, clsBNPrime);
//				OWLClassAssertionAxiom aSubClassOfBNUnionBNPrime = factory.getOWLClassAssertionAxiom(unionOf, aIndividual);
//				entailment = aSubClassOfBNUnionBNPrime;
//			}
			Set<OWLAxiom> kb = ontology.getABoxAxioms(false);
			kb.addAll(ontology.getTBoxAxioms(false));

//			ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(new PelletReasonerFactory());
//
//			ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(ontology);
//
//			double totalTime = System.currentTimeMillis();
//			Set<Explanation<OWLAxiom>> expl = gen.getExplanations(entailment);

//			System.out.println(totalTime);


//			Set<Set<OWLAxiom>> kernelSet = selectedReiter.kernelSet(kb, entailment);

//		System.out.format("%-15d", ((kernelSet != null) ? kernelSet.size() : -1));
//
//		System.out.format("%-15.2f", selectedReiter.getTotalTime());
//
//		System.out.format("%-15d", selectedReiter.getNumberOfReasonerCalls());
//
//		System.out.format("%-20.2f", selectedReiter.getTotalReasonerCallTime());
//
//		System.out.format("%-15d", selectedReiter.getUsedMemory());
//
//		System.out.format("%-10s", SanityChecker.checkKernelSanity(kernelSet, entailment));
//
//		if(selectedReiter instanceof OptimizedReiter){
//			OptimizedReiter optimizedReiter = (OptimizedReiter)selectedReiter;
//
//			System.out.format("%-15d", optimizedReiter.getReusedNodes());
//
//			System.out.format("%-15d", optimizedReiter.getEarlyPathTermination());
//
//		}
//


			InconsistentOntologyExplanationGeneratorFactory fac = new InconsistentOntologyExplanationGeneratorFactory(new PelletReasonerFactory(), Long.MAX_VALUE);
			ExplanationGenerator<OWLAxiom> delegate = fac.createExplanationGenerator(ontology);
			OWLDataFactory df = manager.getOWLDataFactory();
			OWLClass top = df.getOWLThing();
			OWLClass bottom = df.getOWLNothing();
			OWLAxiom conflict = df.getOWLSubClassOfAxiom(top, bottom);
			double time = System.currentTimeMillis();
			delegate.getExplanations(conflict);
			time = System.currentTimeMillis() - time;
			System.out.println(time);
		}
		System.exit(0);
	}

}
