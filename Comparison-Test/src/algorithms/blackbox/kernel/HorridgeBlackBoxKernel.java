package algorithms.blackbox.kernel;

import algorithms.blackbox.BlackBoxContractionStrategy;
import algorithms.blackbox.BlackBoxExpansionStrategy;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.semanticweb.owl.explanation.api.*;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by rocknroll on 25/09/14.
 */


public class HorridgeBlackBoxKernel extends BlackBoxKernel {


	private OWLOntologyManager manager;
	private long usedMemory;
	private long totalTime;

	private List<Double> reasonerCalls;


	public HorridgeBlackBoxKernel(BlackBoxExpansionStrategy expansionStrategy, BlackBoxContractionStrategy contractionStrategy) {
		super(expansionStrategy, contractionStrategy);
	}


	public HorridgeBlackBoxKernel(OWLOntologyManager manager) {
		super(null,null);
		this.manager = manager;
		this.usedMemory=0;
		this.totalTime=0;
		this.reasonerCalls = new ArrayList<>();
	}

	@Override
	public long getUsedMemory() {
		return this.usedMemory;
	}

	@Override
	public Set<OWLAxiom> blackBox(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		OWLOntology ontology = manager.createOntology(kb);

		ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(new PelletReasonerFactory());

		ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(ontology);
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		Set<Explanation<OWLAxiom>> expl = gen.getExplanations(entailment,1);
		this.totalTime = System.currentTimeMillis() - this.totalTime;
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed() - this.usedMemory;

		this.reasonerCalls = OutsideStatistics.instance().getReasonerCalls();

		return expl.iterator().next().getAxioms();
	}

	@Override
	public void reset() {
		this.totalTime=0;
		this.usedMemory=0;
		this.reasonerCalls = new ArrayList<>();
		OutsideStatistics.instance().reset();
		for(OWLOntology ontology : this.manager.getOntologies()){
			this.manager.removeOntology(ontology);
		}
	}

	@Override
	public int getTotalReasonerCalls() {
		return this.reasonerCalls.size();
	}

	@Override
	public double getExpansionTotalTime() {
		return -1;
	}

	@Override
	public double getContractionTotalTime() {
		return -1;
	}

	@Override
	public double getReasoningOntologiesMeanSize() {
		return -1;
	}

	@Override
	public double getTotalTime() {
		double total = 0;
		for(Double callTime : this.reasonerCalls){
			total+=callTime;
		}
		return total;
	}

	@Override
	public double getTotalReasonerCallTime() {
		return this.getTotalTime();
	}

	@Override
	public double getMeanReasoningTime() {
		return -1;
	}
}
