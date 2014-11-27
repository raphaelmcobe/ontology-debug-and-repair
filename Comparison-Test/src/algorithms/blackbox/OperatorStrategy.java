package algorithms.blackbox;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rocknroll on 25/08/14.
 */
public class OperatorStrategy {
	protected List<Double> reasonerCalls;

	protected OWLOntologyManager manager;

	protected double totalTime;

	protected long usedMemory;

	private List<Integer> reasoningOntologiesSize;

	public OperatorStrategy() {
		super();
	}

	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}

	public List<Double> reasonerCallsTimes(){
		return this.reasonerCalls;
	}

	public OperatorStrategy(OWLOntologyManager manager) {
		this.reasonerCalls = new ArrayList<Double>();
		this.reasoningOntologiesSize = new ArrayList<Integer>();
		this.usedMemory = 0;
		this.manager = manager;
	}

	public boolean isEntailed(OWLOntology ontology, OWLAxiom entailment){
		OWLReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		this.reasoningOntologiesSize.add(ontology.getAxiomCount());
		double time = System.currentTimeMillis();
		boolean isEntailed = reasoner.isEntailed(entailment);
		this.reasonerCalls.add(System.currentTimeMillis()-time);
		return isEntailed;
	}


	public int getNumberOfReasonerCalls(){
		return this.reasonerCallsTimes().size();
	}

	public List<Integer> getReasoningOntologiesSize() {
		return reasoningOntologiesSize;
	}

	public double getTotalReasonerCallTime(){
		double total = 0.0;
		for(Double value : this.reasonerCallsTimes()){
			total+=value;
		}
		return total;
	}

	public double getTotalTime(){
		return this.totalTime;
	}

	public void reset() {
		this.reasonerCalls = new ArrayList<Double>();
		this.reasoningOntologiesSize = new ArrayList<Integer>();
		totalTime = 0;
		for(OWLOntology ontology : this.manager.getOntologies()){
			this.manager.removeOntology(ontology);
		}
	}

	public void addToReasonerCalls(List<Double> newCalls){
		this.reasonerCalls.addAll(newCalls);
	}

	public long getUsedMemory() {
		return usedMemory/(1024*1024);
	}
}
