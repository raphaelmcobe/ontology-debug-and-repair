package util;

import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxExplanationRenderer;
import org.semanticweb.owlapi.model.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;


public abstract class Util {
	static ManchesterSyntaxExplanationRenderer renderer = new ManchesterSyntaxExplanationRenderer();
	static{
		renderer.setSmartIndent(false);
		renderer.setWrapLines(false);
	}
	
	public static final String EARLY_TERMINATED_PATHS = "7ePT";
	public static final String EARLY_TERMINATED_PATHS_FORMAT = "%-15s";
	public static final String EARLY_TERMINATED_PATHS_NUMBER_FORMAT = "%-15.1f";

	public static final String REUSED_NODES = "6ReN";
	public static final String REUSED_NODES_FORMAT = "%-15s";
	public static final String REUSED_NODES_NUMBER_FORMAT = "%-15.1f";

	public static final String REASONER_CALLS_TOTAL_TIME = "3RTT";
	public static final String REASONER_CALLS_TOTAL_TIME_FORMAT = "%-20s";
	public static final String REASONER_CALLS_TOTAL_TIME_NUMBER_FORMAT = "%-20.2f";

	public static final String REASONER_CALLS_MEAN_TIME = "4RMT";
	public static final String REASONER_CALLS_MEAN_TIME_FORMAT = "%-20s";
	public static final String REASONER_CALLS_MEAN_TIME_NUMBER_FORMAT = "%-20.2f";

	public static final String REASONER_CALLS = "2#RC";
	public static final String REASONER_CALLS_FORMAT = "%-20s";
	public static final String REASONER_CALLS_NUMBER_FORMAT = "%-20.2f";

	public static final String REMAINDER_SIZE = "5#Rem";
	public static final String REMAINDER_SIZE_FORMAT = "%-15s";
	public static final String REMAINDER_SIZE_NUMBER_FORMAT = "%-15.1f";

	public static final String DIVIDE_AND_CONQUER_NODE_REUSE_EARLY_PATH_TERMINATION_PARTIAL_MEET = "1D&C-NR-ePT - PM";
	public static final String DIVIDE_AND_CONQUER_NODE_REUSE_EARLY_PATH_TERMINATION_PARTIAL_MEET_FORMAT = "%-20s";
	public static final String DIVIDE_AND_CONQUER_NODE_REUSE_EARLY_PATH_TERMINATION_PARTIAL_MEET_NUMBER_FORMAT = "%-20.2f";

	public static final String CLASSICAL_PARTIAL_MEET = "1C - PM";
	public static final String CLASSICAL_PARTIAL_MEET_FORMAT = "%-15s";
	public static final String CLASSICAL_PARTIAL_MEET_NUMBER_FORMAT = "%-15s";

	public static String CLASSICAL_REMAINDER_ELEMENT = "1C";
	public static String CLASSICAL_REMAINDER_ELEMENT_FORMAT="%-20s";
	public static String CLASSICAL_REMAINDER_ELEMENT_NUMBER_FORMAT="%-20.2f";

	public static String DIVIDE_AND_CONQUER_REMAINDER_ELEMENT = "1D&C";
	public static String DIVIDE_AND_CONQUER_REMAINDER_ELEMENT_FORMAT = "%-20s";
	public static String DIVIDE_AND_CONQUER_REMAINDER_ELEMENT_NUMBER_FORMAT= "%-20.2f";


	public List<String> getPartialMeetStatisticMetrics(){
		List<String> toReturn = new ArrayList<>();

		toReturn.add(REASONER_CALLS);
		toReturn.add(REASONER_CALLS_MEAN_TIME);
		toReturn.add(REASONER_CALLS_TOTAL_TIME);
		toReturn.add(REMAINDER_SIZE);

		return toReturn;
	}

	public static void print(Set<OWLAxiom> kb){
		if(kb==null) return;
		HashSet<Set<OWLAxiom>> tmp = new HashSet<Set<OWLAxiom>>();
		tmp.add(kb);
		StringWriter out = new StringWriter();
		renderer.startRendering(out);
		try {
			renderer.render(tmp);
			String output = out.toString();
			output = output.replaceAll("Explanation\\(s\\):", "");
			output = output.replaceAll("[0-9]\\)", "  ");
			String[] splittedOutput = output.split("\n");
			Arrays.sort(splittedOutput);
			output="";
			for (String string : splittedOutput) {
				output+=string+"\n";
			}
			System.out.println(output);
		} catch (UnsupportedOperationException | OWLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renderer.endRendering();
	}
	
	public static void print (OWLOntology kb){
		print(kb.getAxioms());
	}
	
	public static void print(OWLAxiom axiom){
		HashSet<OWLAxiom> tmp = new HashSet<OWLAxiom>();
		tmp.add(axiom);
		print(tmp);
	}

	public static void printSet(Set<Set<OWLAxiom>> kb) {
		System.out.println("Sets:");
		int i = 0;
		for (Set<OWLAxiom> set : kb) {
			System.out.print((i++)+(": "));
			print(set);
		}
	}


	public static void print(List<OWLAxiom>kbList){
		for(OWLAxiom axiom : kbList){
			print(axiom);
		}
	}

	public static String getManchesterSyntaxAxiom(OWLAxiom axiom){
		HashSet<Set<OWLAxiom>> tmp = new HashSet<Set<OWLAxiom>>();
		StringWriter out = new StringWriter();
		Set<OWLAxiom> singleAxiom = new HashSet<>();
		singleAxiom.add(axiom);
		tmp.add(singleAxiom);
		renderer.startRendering(out);
		try {
			renderer.render(tmp);
			String output = out.toString();
			output = output.replaceAll("Explanation\\(s\\):", "");
			output = output.replaceAll("[0-9]\\)", "");
			output = output.replaceAll("\\n", "");
			String[] splittedOutput = output.split("\n");
			return splittedOutput[0].trim();
//			return output.toString();
		} catch (UnsupportedOperationException | OWLException | IOException e) {
			e.printStackTrace();
		}
		renderer.endRendering();
		return null;
	}
}
