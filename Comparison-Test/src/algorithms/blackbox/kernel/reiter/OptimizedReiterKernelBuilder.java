package algorithms.blackbox.kernel.reiter;

import algorithms.blackbox.BlackBox;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created by rocknroll on 20/09/14.
 */
public class OptimizedReiterKernelBuilder extends ReiterKernelBuilder {

	private int earlyPathTermination;
	private int reusedNodes;

	public OptimizedReiterKernelBuilder(BlackBox blackBox, OWLOntologyManager manager) {
		super(blackBox, manager);
		this.earlyPathTermination=0;
		this.reusedNodes++;
	}

	public Set<Set<OWLAxiom>> kernelSet(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		Set<Set<OWLAxiom>> kernel = new HashSet<Set<OWLAxiom>>();

		Set<Set<OWLAxiom>> cut = new HashSet<Set<OWLAxiom>>();

		Stack<Set<OWLAxiom>> stack = new Stack<>();
		Set<OWLAxiom> element;
		Set<OWLAxiom> candidate;
		Set<OWLAxiom> hn;

		Set<Set<OWLAxiom>> cache = new HashSet<Set<OWLAxiom>>();

		OWLOntology ontology = manager.createOntology(kb);

		if (!isEntailed(ontology, entailment)) {
			return kernel;
		}
		element = this.blackBox.blackBox(ontology.getAxioms(), entailment);
		cache.add(element);
		this.addToReasonerCalls(this.blackBox.getReasonerCalls());
		kernel.add(element);
		for (OWLAxiom axiom : element) {
			Set<OWLAxiom> set = new HashSet<OWLAxiom>();
			set.add(axiom);
			stack.add(set);
		}
		// Reiter's algorithm
		while (!stack.isEmpty()) {
			hn = stack.pop();
			for (OWLAxiom axiom : hn) {
				manager.removeAxiom(ontology, axiom);
			}
			if (isEntailed(ontology, entailment)) {
				Set<OWLAxiom> kernelCache = findCache(cache, hn);
				if(kernelCache!= null){
					candidate = kernelCache;
					this.reusedNodes++;
				}
				else {
					candidate = blackBox.blackBox(ontology.getAxioms(), entailment);
				}
				kernel.add(candidate);
				for (OWLAxiom axiom : candidate) {
					Set<OWLAxiom> set2 = new HashSet<OWLAxiom>();
					set2.addAll(hn);
					set2.add(axiom);
					if (!stack.contains(set2)) {
						stack.add(set2);
					}
					else{
						this.earlyPathTermination++;
					}
				}
			} else
				cut.add(hn);

			// Restore to the ontology the axioms removed so it can be used
			// again
			for (OWLAxiom axiom : hn) {
				manager.addAxiom(ontology, axiom);
			}
		}
		this.totalTime = System.currentTimeMillis()-this.totalTime;
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed() - this.usedMemory;
		return kernel;
	}

	private Set<OWLAxiom> findCache(Set<Set<OWLAxiom>> cache, Set<OWLAxiom> hn) {
		for (Set<OWLAxiom> eachKernel : cache){
			Set<OWLAxiom> copy = new HashSet<OWLAxiom>(eachKernel);
			copy.retainAll(hn);
			if(copy.isEmpty()){
				return eachKernel;
			}
		}
		return null;
	}

	public int getEarlyPathTermination() {
		return earlyPathTermination;
	}

	public int getReusedNodes() {
		return reusedNodes;
	}

	public void reset(){
		super.reset();
		this.reusedNodes=0;
		this.earlyPathTermination=0;
	}
}
