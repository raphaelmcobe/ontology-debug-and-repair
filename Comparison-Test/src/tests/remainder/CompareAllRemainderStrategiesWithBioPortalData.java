package tests.remainder;

import algorithms.blackbox.BlackBox;
import algorithms.blackbox.BlackBoxTestRunner;
import algorithms.blackbox.remainder.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import util.SanityChecker;
import util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by rocknroll on 25/08/14.
 */
public class CompareAllRemainderStrategiesWithBioPortalData {




    static {

    }


    public static void main(String[] args) throws OWLOntologyCreationException, InterruptedException, FileNotFoundException {



        HashMap<String, BlackBox> operators = new HashMap<String, BlackBox>();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        operators.put("dace_trc", new BlackBoxRemainder(new DivideAndConquerBlackBoxRemainderExpansionStrategy(manager), new TrivialBlackBoxRemainderContractionStrategy(manager)));
        operators.put("ce_trc", new BlackBoxRemainder(new ClassicalBlackBoxRemainderExpansionStrategy(manager), new TrivialBlackBoxRemainderContractionStrategy(manager)));
        operators.put("ce_swc", new BlackBoxRemainder(new ClassicalBlackBoxRemainderExpansionStrategy(manager), new SlidingWindowBlackBoxRemainderContractionStrategy(manager)));
        operators.put("ce_cc", new BlackBoxRemainder(new ClassicalBlackBoxRemainderExpansionStrategy(manager), new ClassicalBlackBoxRemainderContractionStrategy(manager)));
        operators.put("dace_src", new BlackBoxRemainder(new DivideAndConquerBlackBoxRemainderExpansionStrategy(manager), new SyntaticRelevanceBlackBoxRemainderContractionStrategy(manager)));
        operators.put("ce_src", new BlackBoxRemainder(new ClassicalBlackBoxRemainderExpansionStrategy(manager), new SyntaticRelevanceBlackBoxRemainderContractionStrategy(manager)));


        String ontologyFileName = args[0];

        String blackBoxName = args[1];

        int entailmentIndex = Integer.parseInt(args[2])-1;

        BlackBox selectedBlackBox = operators.get(blackBoxName);

        System.setErr(new PrintStream("/tmp/remaindertest.log"));


        System.out.format("%-70s", "#Axiom String");

        System.out.format("%-10s", "i");

        System.out.format("%-20s", "No. Axioms");

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


        OWLOntology entailments = manager.loadOntologyFromOntologyDocument(new File("../BioPortal/entailed/" + ontologyFileName + "/" + "selectedentailments.xml"));
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("../BioPortal/entailed/" + ontologyFileName + "/" + ontologyFileName + ".owl.xml"));
        Set<OWLAxiom> kb = ontology.getABoxAxioms(true);
        kb.addAll(ontology.getTBoxAxioms(true));


        Set<OWLSubClassOfAxiom> entailmentAxioms = entailments.getAxioms(AxiomType.SUBCLASS_OF);
        Map<String, OWLAxiom> entailmentMap = new HashMap<>();

        for (OWLAxiom entailment : entailmentAxioms) {
            entailmentMap.put(Util.getManchesterSyntaxAxiom(entailment), entailment);
        }


        List<String> entailmentKeys = new ArrayList<String>(entailmentMap.keySet());


        Collections.sort(entailmentKeys);


        OWLAxiom entailment = entailmentMap.get(entailmentKeys.get(entailmentIndex));

		String axiomString = Util.getManchesterSyntaxAxiom(entailment);

		axiomString = axiomString.replaceAll(" ","_");

		selectedBlackBox.reset();

        Set<OWLAxiom> remainder = selectedBlackBox.blackBox(kb, entailment);

        System.out.format("%-70s", axiomString);

        System.out.format("%-10s", entailmentIndex+1);

        System.out.format("%-20d", kb.size());

        System.out.format("%-15d", ((remainder != null) ? remainder.size() : -1));

        System.out.format("%-15.2f", selectedBlackBox.getTotalTime());

        System.out.format("%-15.2f", selectedBlackBox.getExpansionTotalTime());

        System.out.format("%-15.2f", selectedBlackBox.getContractionTotalTime());

        System.out.format("%-15d", selectedBlackBox.getTotalReasonerCalls());

        System.out.format("%-20.2f", selectedBlackBox.getReasoningOntologiesMeanSize());

        System.out.format("%-20.2f", selectedBlackBox.getMeanReasoningTime());

        System.out.format("%-20.2f", selectedBlackBox.getTotalReasonerCallTime());

        System.out.format("%-15d", selectedBlackBox.getUsedMemory());

		//manager = OWLManager.createOWLOntologyManager();

        //System.out.format("%-10s", SanityChecker.checkSingleRemainderSanity(manager.createOntology(kb), remainder, entailment));

        System.out.println();

        System.exit(0);
    }
}
