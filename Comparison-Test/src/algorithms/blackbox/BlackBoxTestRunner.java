package algorithms.blackbox;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rocknroll on 25/08/14.
 */
public class BlackBoxTestRunner extends Thread{

	private BlackBox blackBox;

	private Set<OWLAxiom> result;

	private Set<OWLAxiom> kb;

	private OWLAxiom entailment;

	private Thread mainThread;
	private boolean finished;


	public BlackBoxTestRunner(Set<OWLAxiom> kb, OWLAxiom entailment) {
		this.kb = kb;
		this.entailment = entailment;
		this.result = new HashSet<OWLAxiom>();
		this.finished = false;
	}

	public boolean isFinished(){
		return finished;
	}

	public Thread getMainThread() {
		return mainThread;
	}

	public void setMainThread(Thread mainThread) {
		this.mainThread = mainThread;
	}

	public void setKb(Set<OWLAxiom> kb) {
		this.kb = kb;
	}

	public void setEntailment(OWLAxiom entailment) {
		this.entailment = entailment;
	}

	public void setBlackBox(BlackBox blackBox) {
		this.blackBox = blackBox;
	}

	public Set<OWLAxiom> getResult() {
		return result;
	}

	@Override
	public void run() {
		try {
			this.result = blackBox.blackBox(this.kb,this.entailment);
			this.finished = true;
			mainThread.interrupt();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}
}
