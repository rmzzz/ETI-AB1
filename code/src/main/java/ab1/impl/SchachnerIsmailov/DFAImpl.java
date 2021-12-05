package ab1.impl.SchachnerIsmailov;

import ab1.DFA;
import ab1.NFA;
import ab1.exceptions.IllegalCharacterException;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DFAImpl extends NFAImpl implements DFA {

    int currentState;

    public DFAImpl(int numStates, Set<Character> characters, Set<Integer> acceptingStates, int initialState) {
        super(numStates, characters, acceptingStates, initialState);
        currentState = initialState;
    }

    @Override
    public void reset() {
    currentState = initialState;
    }

    @Override
    public int getCurrentState() {
        return currentState;
    }

    @Override
    public int doStep(char c) throws IllegalCharacterException, IllegalStateException {
        int curr = getCurrentState();
        Integer next = this.getNextState(curr,c);
        if(next != null)
            return currentState = next;
        throw new IllegalStateException();
    }

    @Override
    public Integer getNextState(int s, char c) throws IllegalCharacterException, IllegalStateException {
        int nextState=0;
        if (this.getAlphabet().contains(c)){
            if (s < this.getNumStates()){
                while (this.getTransitions()[s][nextState] == null
                        || !this.getTransitions()[s][nextState].contains(c)){ //??
                    nextState++;
                    if (nextState >= numStates){
                        return null;
                    }
                }
                return nextState;
            }
            else throw new IllegalStateException();
        }
        else throw new IllegalCharacterException();
    }

    @Override
    public boolean isInAcceptingState() {
        return getAcceptingStates().contains(currentState);
    }

    /**
     * see VO 2021WS-NFA p22/24
     */
    @Override
    public NFA complement() {
        DFA completeDfa = toCompleteDFA(this);
        Set<Integer> inverseAcceptingStates = IntStream.range(0, completeDfa.getNumStates()).boxed()
                .filter(s -> !completeDfa.getAcceptingStates().contains(s))
                .collect(Collectors.toSet());
        var res = new DFAImpl(completeDfa.getNumStates(), completeDfa.getAlphabet(), inverseAcceptingStates, completeDfa.getInitialState());
        res.copyTransitions(completeDfa.getTransitions());
        debug("NOT %s\n => %s", this, res);
        return res;
    }

    @Override
    public DFA toDFA() {
        return this;
    }

    @Override
    public String toString() {
        return toString("M=(Q={0..%d}, Σ=%s, δ=%s, q₀=%s, F=%s)");
    }
}
