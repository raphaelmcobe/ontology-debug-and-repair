package tests.kernel.reiter;

import algorithms.blackbox.kernel.BlackBoxKernel;
import algorithms.blackbox.kernel.DivideAndConquerBlackBoxKernelContractionStrategy;
import algorithms.blackbox.kernel.SyntaticRelevanceBlackboxKernelExpansionStrategy;
import algorithms.blackbox.kernel.reiter.*;
import algorithms.blackbox.kernel.reiter.OptimizedReiterKernelBuilder;
import algorithms.blackbox.kernel.reiter.ReiterKernelBuilder;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by rocknroll on 25/08/14.
 */
public class CompareAllReiterStrategiesWithBioPortalData {

    private static Map<String, ReiterKernelBuilder> operators;

    private static OWLOntologyManager manager = null;


    public static void main(String[] args) throws OWLOntologyCreationException, InterruptedException, FileNotFoundException {
        operators = new HashMap<String, ReiterKernelBuilder>();
        manager = OWLManager.createOWLOntologyManager();
        operators.put("cr_sre_dacc", new ClassicalReiterKernelBuilder(new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager)), manager));
        operators.put("or_sre_dacc", new OptimizedReiterKernelBuilder(new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager)), manager));
        operators.put("hr_sre_dacc", new HorridgeReiterKernelBuilder(null, manager));


        String ontologyFileName = args[0];

        String blackBoxName = args[1];

        int entailmentIndex = Integer.parseInt(args[2])-1;

        ReiterKernelBuilder selectedReiterKernelBuilder = operators.get(blackBoxName);

        System.setErr(new PrintStream("/tmp/reitertest.log"));


        System.out.format("%-70s", "#Axiom String");

        System.out.format("%-10s", "i");

        System.out.format("%-20s", "No. Axioms");

        System.out.format("%-15s", "size");

        System.out.format("%-15s", "time");

        System.out.format("%-15s", "RC");

        System.out.format("%-20s", "RC_total_time");

        System.out.format("%-15s", "mem");

        System.out.format("%-10s", "sanity");

        if (selectedReiterKernelBuilder instanceof OptimizedReiterKernelBuilder) {
            System.out.format("%-15s", "ReusedN");

            System.out.format("%-15s", "EarlyPT");
        }

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

        selectedReiterKernelBuilder.reset();

        Set<Set<OWLAxiom>> kernelSet = selectedReiterKernelBuilder.kernelSet(kb, entailment);


        System.out.format("%-70s", axiomString);

        System.out.format("%-10s", entailmentIndex+1);

        System.out.format("%-20s", kb.size());

        System.out.format("%-15d", ((kernelSet != null) ? kernelSet.size() : -1));

        System.out.format("%-15.2f", selectedReiterKernelBuilder.getTotalTime());

        System.out.format("%-15d", selectedReiterKernelBuilder.getNumberOfReasonerCalls());

        System.out.format("%-20.2f", selectedReiterKernelBuilder.getTotalReasonerCallTime());

        System.out.format("%-15d", selectedReiterKernelBuilder.getUsedMemory());

		//manager = OWLManager.createOWLOntologyManager();

        //System.out.format("%-10s", SanityChecker.checkKernelSanity(kernelSet, entailment));

        //if (selectedReiter instanceof OptimizedReiter) {
        //    OptimizedReiter optimizedReiter = (OptimizedReiter) selectedReiter;

        //    System.out.format("%-15d", optimizedReiter.getReusedNodes());

        //    System.out.format("%-15d", optimizedReiter.getEarlyPathTermination());

        //}
        System.out.println();
        System.exit(0);
    }
}
