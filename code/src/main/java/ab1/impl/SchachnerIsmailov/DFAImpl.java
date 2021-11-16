package ab1.impl.SchachnerIsmailov;

import ab1.DFA;
import ab1.NFA;
import ab1.exceptions.IllegalCharacterException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DFAImpl extends NFAImpl implements DFA {
    public DFAImpl(int numStates, Set<Character> characters, Set<Integer> acceptingStates, int initialState) {
        super(numStates, characters, acceptingStates, initialState);
    }

    @Override
    public void reset() {

    }

    @Override
    public int getCurrentState() {
        return 0;
    }

    @Override
    public int doStep(char c) throws IllegalCharacterException, IllegalStateException {
        return 0;
    }

    @Override
    public Integer getNextState(int s, char c) throws IllegalCharacterException, IllegalStateException {
        return null;
    }

    @Override
    public boolean isInAcceptingState() {
        return false;
    }

    /**
     * see VO 2021WS-NFA p22/24
     */
    @Override
    public NFA complement() {
        Set<Integer> inverseAcceptingStates = IntStream.range(0, numStates).boxed()
                .filter(s -> !acceptingStates.contains(s))
                .collect(Collectors.toSet());
        var res = new DFAImpl(numStates, alphabet, inverseAcceptingStates, initialState);
        res.copyTransitions(transitions, 0);
        return res;
    }

    @Override
    public DFA toDFA() {
        return this;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o)
//            return true;
//        if (!(o instanceof NFA)) ///< this is correct, DFA can be compared against NFA
//            return false;
//        DFA dfa = ((NFA) o).toDFA();
//
//        boolean isEqual = numStates == dfa.getNumStates()
//                && initialState == dfa.getInitialState()
//                && getAlphabet().equals(dfa.getAlphabet())
//                && getAcceptingStates().equals(dfa.getAcceptingStates())
//                && Arrays.deepEquals(transitions, dfa.getTransitions());
//        debug("%s\n==?\n%s\n=>%b", this, dfa, isEqual);
//        return isEqual;
//    }

    @Override
    public String toString() {
        return toString("M=(Q={0..%d}, Σ=%s, δ=%s, q₀=%s, F=%s)");
    }


}
