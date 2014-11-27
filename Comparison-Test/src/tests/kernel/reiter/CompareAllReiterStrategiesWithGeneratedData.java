package tests.kernel.reiter;

import algorithms.blackbox.kernel.*;
import algorithms.blackbox.kernel.reiter.*;
import algorithms.blackbox.kernel.reiter.OptimizedReiterKernelBuilder;
import algorithms.blackbox.kernel.reiter.ReiterKernelBuilder;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import util.SanityChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rocknroll on 25/08/14.
 */
public class CompareAllReiterStrategiesWithGeneratedData {


	static String SMALL_KERNEL_TEST_TYPE = "Small";
	static String LARGE_KERNEL_TEST_TYPE = "Large";


	private static Map<String, ReiterKernelBuilder> operators;

	private static OWLOntologyManager manager = null;


	public static void main(String[] args) throws OWLOntologyCreationException, InterruptedException, FileNotFoundException {

		operators = new HashMap<String, ReiterKernelBuilder>();
		manager = OWLManager.createOWLOntologyManager();
		operators.put("cr_sre_dacc", new ClassicalReiterKernelBuilder(new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager)),manager));
		operators.put("or_sre_dacc", new OptimizedReiterKernelBuilder(new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager)),manager));
		operators.put("hr_sre_dacc", new HorridgeReiterKernelBuilder(null,manager));


		String type = "";
		if (args[0].equals("-large")) {
			type = LARGE_KERNEL_TEST_TYPE;
		} else if (args[0].equals("-small")) {
			type = SMALL_KERNEL_TEST_TYPE;
		}

		int size = Integer.parseInt(args[1]);

		String algorithmName = args[2];

		ReiterKernelBuilder selectedReiterKernelBuilder = operators.get(algorithmName);


		System.setErr(new PrintStream("/tmp/reintertest.log"));


		System.out.format("%-10s", "#i");

		System.out.format("%-10s", "Axioms");

		System.out.format("%-15s", "size");

		System.out.format("%-15s", "time");

		System.out.format("%-15s", "RC");

//		System.out.format("%-20s", "ont_mean_size");

//		System.out.format("%-20s", "RC_mean_time");

		System.out.format("%-20s", "RC_total_time");

		System.out.format("%-15s", "mem");

		System.out.format("%-10s", "sanity");

		if(selectedReiterKernelBuilder instanceof OptimizedReiterKernelBuilder){
			System.out.format("%-15s", "ReusedN");

			System.out.format("%-15s", "EarlyPT");
		}

		System.out.println();


		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("../input/" + type + "KernelEntailment/" + type + "KernelWith" + size + "classes.owl"));
		System.out.format("%-10d", size);
		System.out.format("%-10d", ontology.getAxiomCount());
		IRI ontologyIRI = IRI.create("http://www.ime.usp.br/liamf/ontologies/" + type + "KernelWith" + size + "Classes.owl");
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLAxiom entailment = null;
		if (type.equals(SMALL_KERNEL_TEST_TYPE)) {
			OWLClass clsA = factory.getOWLClass(IRI.create(ontologyIRI + "#A"));
			OWLClass clsC = factory.getOWLClass(IRI.create(ontologyIRI + "#C"));
			entailment = factory.getOWLSubClassOfAxiom(clsA, clsC);
		} else if (type.equals(LARGE_KERNEL_TEST_TYPE)) {
			OWLClass clsBN = factory.getOWLClass(IRI.create(ontologyIRI + "#B" + size));
			OWLClass clsBNPrime = factory.getOWLClass(IRI.create(ontologyIRI + "#B" + size + "Prime"));
			OWLIndividual aIndividual = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#a"));
			OWLObjectUnionOf unionOf = factory.getOWLObjectUnionOf(clsBN, clsBNPrime);
			OWLClassAssertionAxiom aSubClassOfBNUnionBNPrime = factory.getOWLClassAssertionAxiom(unionOf, aIndividual);
			entailment = aSubClassOfBNUnionBNPrime;
		}
		Set<OWLAxiom> kb = ontology.getABoxAxioms(false);
		kb.addAll(ontology.getTBoxAxioms(false));

		Set<Set<OWLAxiom>> kernelSet = selectedReiterKernelBuilder.kernelSet(kb, entailment);

		System.out.format("%-15d", ((kernelSet != null) ? kernelSet.size() : -1));

		System.out.format("%-15.2f", selectedReiterKernelBuilder.getTotalTime());

		System.out.format("%-15d", selectedReiterKernelBuilder.getNumberOfReasonerCalls());

		System.out.format("%-20.2f", selectedReiterKernelBuilder.getTotalReasonerCallTime());

		System.out.format("%-15d", selectedReiterKernelBuilder.getUsedMemory());

		System.out.format("%-10s", SanityChecker.checkKernelSanity(kernelSet, entailment));

		if(selectedReiterKernelBuilder instanceof OptimizedReiterKernelBuilder){
			OptimizedReiterKernelBuilder optimizedReiter = (OptimizedReiterKernelBuilder) selectedReiterKernelBuilder;

			System.out.format("%-15d", optimizedReiter.getReusedNodes());

			System.out.format("%-15d", optimizedReiter.getEarlyPathTermination());

		}

		System.out.println();

		System.exit(0);
	}
}
