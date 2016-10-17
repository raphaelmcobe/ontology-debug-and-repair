package tests.kernel;

import algorithms.blackbox.BlackBox;
import algorithms.blackbox.kernel.*;
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
public class CompareAllKernelStrategiesWithGeneratedData {


	static String SMALL_KERNEL_TEST_TYPE = "Small";
	static String LARGE_KERNEL_TEST_TYPE = "Large";


	private static Map<String, BlackBox> operators;

	private static OWLOntologyManager manager = null;


	static {
		operators = new HashMap<String, BlackBox>();
		manager = OWLManager.createOWLOntologyManager();
		operators.put("ce_cc", new BlackBoxKernel(new ClassicalBlackBoxKernelExpansionStrategy(manager), new ClassicalBlackBoxKernelContractionStrategy(manager)));
		operators.put("ce_swc", new BlackBoxKernel(new ClassicalBlackBoxKernelExpansionStrategy(manager), new SlidingWindowBlackBoxKernelContractionStrategy(manager, 10)));
		operators.put("ce_dacc", new BlackBoxKernel(new ClassicalBlackBoxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager)));
		operators.put("sre_cc", new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new ClassicalBlackBoxKernelContractionStrategy(manager)));
		operators.put("sre_swc", new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new SlidingWindowBlackBoxKernelContractionStrategy(manager, 10)));
		operators.put("sre_dacc", new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager)));
	}


	public static void main(String[] args) throws OWLOntologyCreationException, InterruptedException, FileNotFoundException {

		String type = "";
		if (args[0].equals("-large")) {
			type = LARGE_KERNEL_TEST_TYPE;
		} else if (args[0].equals("-small")) {
			type = SMALL_KERNEL_TEST_TYPE;
		}

		int size = Integer.parseInt(args[1]);

		String blackBoxName = args[2];

		BlackBox selectedBlackBox = CompareAllKernelStrategiesWithGeneratedData.operators.get(blackBoxName);


		System.setErr(new PrintStream("/tmp/kerneltest.log"));


		System.out.format("%-10s", "#i");

		System.out.format("%-10s", "Axioms");

		System.out.format("%-15s", "size");

		System.out.format("%-15s", "time");

		System.out.format("%-15s", "exp_time");

		System.out.format("%-15s", "cont_time");

		System.out.format("%-15s", "RC");

		System.out.format("%-20s", "ont_mean_size");

		System.out.format("%-20s", "RC_mean_time");

		System.out.format("%-20s", "RC_total_time");

		System.out.format("%-15s", "mem");

		System.out.format("%-10s", "sanity");

		System.out.println();


		selectedBlackBox.reset();
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

		Set<OWLAxiom> kernel = selectedBlackBox.blackBox(kb,entailment);

		System.out.format("%-15d", ((kernel != null) ? kernel.size() : -1));

		System.out.format("%-15.2f", selectedBlackBox.getTotalTime());

		System.out.format("%-15.2f", selectedBlackBox.getExpansionTotalTime());

		System.out.format("%-15.2f", selectedBlackBox.getContractionTotalTime());

		System.out.format("%-15d", selectedBlackBox.getTotalReasonerCalls());

		System.out.format("%-20.2f", selectedBlackBox.getReasoningOntologiesMeanSize());

		System.out.format("%-20.2f", selectedBlackBox.getMeanReasoningTime());

		System.out.format("%-20.2f", selectedBlackBox.getTotalReasonerCallTime());

		System.out.format("%-15d", selectedBlackBox.getUsedMemory());

		System.out.format("%-10s", SanityChecker.checkSingleKernelSanity(kernel, entailment));

		System.out.println();


		System.exit(0);
	}
}
