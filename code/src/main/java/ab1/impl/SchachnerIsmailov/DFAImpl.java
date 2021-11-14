package ab1.impl.SchachnerIsmailov;

import ab1.DFA;
import ab1.NFA;
import ab1.exceptions.IllegalCharacterException;

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
}
