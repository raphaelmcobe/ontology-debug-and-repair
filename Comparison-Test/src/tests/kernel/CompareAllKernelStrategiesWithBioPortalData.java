package tests.kernel;

import algorithms.blackbox.BlackBox;
import algorithms.blackbox.kernel.*;
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
public class CompareAllKernelStrategiesWithBioPortalData {



    public static void main(String[] args) throws OWLOntologyCreationException, InterruptedException, FileNotFoundException {



        HashMap<String, BlackBox>  operators = new HashMap<String, BlackBox>();
        OWLOntologyManager  manager = OWLManager.createOWLOntologyManager();
        operators.put("ce_cc", new BlackBoxKernel(new ClassicalBlackBoxKernelExpansionStrategy(manager), new ClassicalBlackBoxKernelContractionStrategy(manager)));
        operators.put("ce_swc", new BlackBoxKernel(new ClassicalBlackBoxKernelExpansionStrategy(manager), new SlidingWindowBlackBoxKernelContractionStrategy(manager, 10)));
        operators.put("ce_dacc", new BlackBoxKernel(new ClassicalBlackBoxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager)));
        operators.put("sre_cc", new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new ClassicalBlackBoxKernelContractionStrategy(manager)));
        operators.put("sre_swc", new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new SlidingWindowBlackBoxKernelContractionStrategy(manager, 10)));
        operators.put("sre_dacc", new BlackBoxKernel(new SyntaticRelevanceBlackboxKernelExpansionStrategy(manager), new DivideAndConquerBlackBoxKernelContractionStrategy(manager)));
        operators.put("hr_sre_dacc", new HorridgeBlackBoxKernel(manager));


        CompareAllKernelStrategiesWithBioPortalData test = new CompareAllKernelStrategiesWithBioPortalData();


        String ontologyFileName = args[0];

        String blackBoxName = args[1];

        int entailmentIndex = Integer.parseInt(args[2])-1;

        BlackBox selectedBlackBox = operators.get(blackBoxName);

        System.setErr(new PrintStream("/tmp/kerneltest.log"));

        System.out.format("%-120s", "#Axiom String");

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

        Set<OWLAxiom> kernel = selectedBlackBox.blackBox(kb, entailment);

        System.out.format("%-120s", axiomString);

        System.out.format("%-10d", entailmentIndex+1);

        System.out.format("%-20d", kb.size());

        System.out.format("%-15d", ((kernel != null) ? kernel.size() : -1));

        System.out.format("%-15.2f", selectedBlackBox.getTotalTime());

        System.out.format("%-15.2f", selectedBlackBox.getExpansionTotalTime());

        System.out.format("%-15.2f", selectedBlackBox.getContractionTotalTime());

        System.out.format("%-15d", selectedBlackBox.getTotalReasonerCalls());

        System.out.format("%-20.2f", selectedBlackBox.getReasoningOntologiesMeanSize());

        System.out.format("%-20.2f", selectedBlackBox.getMeanReasoningTime());

        System.out.format("%-20.2f", selectedBlackBox.getTotalReasonerCallTime());

        System.out.format("%-15d", selectedBlackBox.getUsedMemory());

		//manager = OWLManager.createOWLOntologyManager();

        //System.out.format("%-10s", SanityChecker.checkSingleKernelSanity(kernel, entailment));

        System.out.println();

        System.exit(0);
    }
}
