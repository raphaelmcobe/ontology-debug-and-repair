package algorithms.blackbox;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.List;
import java.util.Set;

/**
 * Created by rocknroll on 21/08/14.
 */
public abstract class BlackBox {

	public BlackBoxExpansionStrategy expansionStrategy;

	public BlackBoxContractionStrategy contractionStrategy;

	public BlackBox(BlackBoxExpansionStrategy expansionStrategy, BlackBoxContractionStrategy contractionStrategy) {
		this.expansionStrategy = expansionStrategy;
		this.contractionStrategy = contractionStrategy;
	}


	public BlackBoxExpansionStrategy getExpansionStrategy() {
		return expansionStrategy;
	}


	public BlackBoxContractionStrategy getContractionStrategy() {
		return contractionStrategy;
	}


	public abstract Set<OWLAxiom> blackBox(Set<OWLAxiom> ontology, OWLAxiom entailment) throws OWLOntologyCreationException;


	public double getExpansionTotalTime(){
		return this.expansionStrategy.getTotalTime();
	}

	public double getContractionTotalTime(){
		return this.contractionStrategy.getTotalTime();
	}

	public double getTotalReasonerCallTime(){
		return this.contractionStrategy.getTotalReasonerCallTime()+this.expansionStrategy.getTotalReasonerCallTime();
	}

	public double getTotalTime(){
		return this.getExpansionTotalTime()+this.getContractionTotalTime();
	}

	public int getTotalReasonerCalls(){
		return this.expansionStrategy.getNumberOfReasonerCalls()+this.contractionStrategy.getNumberOfReasonerCalls();
	}

	public double getMeanReasoningTime(){
		return this.getTotalReasonerCallTime()/this.getTotalReasonerCalls();
	}

	public void reset() {
		this.expansionStrategy.reset();
		this.contractionStrategy.reset();

	}

	public long getUsedMemory(){
		return this.expansionStrategy.getUsedMemory()+this.contractionStrategy.getUsedMemory();
	}

	public double getReasoningOntologiesMeanSize(){
		double total=0.0;

		for(Integer value : this.expansionStrategy.getReasoningOntologiesSize()){
			total+=value;
		}
		for(Integer value : this.contractionStrategy.getReasoningOntologiesSize()){
			total+=value;
		}
		return total/getTotalReasonerCalls();
	}

	public List<Double> getReasonerCalls(){
		List<Double> toReturn = this.expansionStrategy.reasonerCalls;
		toReturn.addAll(this.contractionStrategy.reasonerCalls);
		return toReturn;
	}
}
