package tests.remainder;

import algorithms.blackbox.BlackBox;
import algorithms.blackbox.remainder.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import util.SanityChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by rocknroll on 25/08/14.
 */
public class CompareAllRemainderStrategiesWithGeneratedData {


	static String SMALL_KERNEL_TEST_TYPE = "Small";
	static String LARGE_KERNEL_TEST_TYPE = "Large";


	private static HashMap<String, BlackBox> operators;

	private static OWLOntologyManager manager = null;


	static {
		operators = new HashMap<String, BlackBox>();
		manager = OWLManager.createOWLOntologyManager();
		operators.put("dace_trc", new BlackBoxRemainder(new DivideAndConquerBlackBoxRemainderExpansionStrategy(manager), new TrivialBlackBoxRemainderContractionStrategy(manager)));
		operators.put("ce_trc", new BlackBoxRemainder(new ClassicalBlackBoxRemainderExpansionStrategy(manager), new TrivialBlackBoxRemainderContractionStrategy(manager)));
		operators.put("ce_swc", new BlackBoxRemainder(new ClassicalBlackBoxRemainderExpansionStrategy(manager), new SlidingWindowBlackBoxRemainderContractionStrategy(manager)));
		operators.put("ce_cc", new BlackBoxRemainder(new ClassicalBlackBoxRemainderExpansionStrategy(manager), new ClassicalBlackBoxRemainderContractionStrategy(manager)));
		operators.put("dace_src", new BlackBoxRemainder(new DivideAndConquerBlackBoxRemainderExpansionStrategy(manager), new SyntaticRelevanceBlackBoxRemainderContractionStrategy(manager)));
		operators.put("ce_src", new BlackBoxRemainder(new ClassicalBlackBoxRemainderExpansionStrategy(manager), new SyntaticRelevanceBlackBoxRemainderContractionStrategy(manager)));
	}


	public static void main(String[] args) throws OWLOntologyCreationException, InterruptedException, FileNotFoundException {

		CompareAllRemainderStrategiesWithGeneratedData test = new CompareAllRemainderStrategiesWithGeneratedData();

		String type = "";
		if (args[0].equals("-large")) {
			type = LARGE_KERNEL_TEST_TYPE;
		} else if (args[0].equals("-small")) {
			type = SMALL_KERNEL_TEST_TYPE;
		}

		int size = Integer.parseInt(args[1]);

		String blackBoxName = args[2];

		BlackBox selectedBlackBox = test.operators.get(blackBoxName);


		System.setErr(new PrintStream("/tmp/remaindertest.log"));


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

		Set<OWLAxiom> remainder = selectedBlackBox.blackBox(kb, entailment);

		System.out.format("%-15d", ((remainder != null) ? remainder.size() : -1));

		System.out.format("%-15.2f", selectedBlackBox.getTotalTime());

		System.out.format("%-15.2f", selectedBlackBox.getExpansionTotalTime());

		System.out.format("%-15.2f", selectedBlackBox.getContractionTotalTime());

		System.out.format("%-15d", selectedBlackBox.getTotalReasonerCalls());

		System.out.format("%-20.2f", selectedBlackBox.getReasoningOntologiesMeanSize());

		System.out.format("%-20.2f", selectedBlackBox.getMeanReasoningTime());

		System.out.format("%-20.2f", selectedBlackBox.getTotalReasonerCallTime());

		System.out.format("%-15d", selectedBlackBox.getUsedMemory());

		System.out.format("%-10s", SanityChecker.checkSingleRemainderSanity(manager.createOntology(kb), remainder, entailment));

		System.out.println();


		System.exit(0);
	}
}
