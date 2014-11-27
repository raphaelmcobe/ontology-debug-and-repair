package algorithms.blackbox.remainder;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by rocknroll on 07/09/14.
 */
public class SlidingWindowBlackBoxRemainderContractionStrategy extends BlackBoxRemainderContractionStrategy {
	private int windowSize = 10 ;

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}


	public SlidingWindowBlackBoxRemainderContractionStrategy(OWLOntologyManager manager) {
		super(manager);
	}

	@Override
	public Set<OWLAxiom> contract(Set<OWLAxiom> kb, OWLAxiom entailment) throws OWLOntologyCreationException {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
		this.usedMemory += memoryMXBean.getNonHeapMemoryUsage().getUsed();
		this.totalTime = System.currentTimeMillis();
		List<OWLAxiom> kbList = new ArrayList<OWLAxiom>(kb);
		int windowStart = 0;
		OWLOntology ontology =  manager.createOntology(kb);

		while(windowStart < kbList.size()){
			int windowEnd = windowStart+windowSize;
			if(windowEnd > (kbList.size()-1)){
				windowEnd = kbList.size()-1;
			}
			HashSet<OWLAxiom> window = new HashSet<OWLAxiom>(kbList.subList(windowStart,windowEnd));
			manager.removeAxioms(ontology, window);
			Set<OWLAxiom> remains = this.getRemains();
			remains.addAll(window);
			this.setRemains(remains);
			if(!isEntailed(ontology,entailment)){
				break;
			}else{
				windowStart = windowStart+1;
			}
		}
		this.totalTime = System.currentTimeMillis() - this.totalTime;
		this.usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed() - this.usedMemory;

		return ontology.getAxioms();
	}
}
