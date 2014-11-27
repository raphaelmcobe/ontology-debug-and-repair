package util;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

/**
 * Created by rmcobe on 10/12/14.
 */
public class ExtractEntailments {

    public static void main(String[] args) throws OWLOntologyCreationException, FileNotFoundException {

        String ontologyFileName = args[0];


        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology entailments = manager.loadOntologyFromOntologyDocument(new File("../BioPortal/entailed/"+ontologyFileName + "/" + "selectedentailments.xml"));

        Set<OWLSubClassOfAxiom> allAxioms = entailments.getAxioms(AxiomType.SUBCLASS_OF);


        for (OWLAxiom entailment : entailments.getAxioms()){
            System.out.println(Util.getManchesterSyntaxAxiom(entailment));

        }
    }
}
