package ab1.impl.SchachnerIsmailov;

import ab1.DFA;
import ab1.NFA;
import ab1.exceptions.IllegalCharacterException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DFAImpl extends NFAImpl implements DFA {

    public static int currentState;


    public DFAImpl(int numStates, Set<Character> characters, Set<Integer> acceptingStates, int initialState) {
        super(numStates, characters, acceptingStates, initialState);

    }



    @Override
    public void reset() {
    currentState = initialState;
    }

   //public void setCurrentState(int currentState){

    //}

    @Override
    public int getCurrentState() {
        //for (int i = 0; i<= this.getNumStates(); i++){
        //}

        return currentState;
    }

    @Override
    public int doStep(char c) throws IllegalCharacterException, IllegalStateException {
        int curr = getCurrentState();
        if (this.getAlphabet().contains(c)){
            return this.getNextState(curr,c);
        }
        else throw new IllegalCharacterException();

    }

    @Override
    public Integer getNextState(int s, char c) throws IllegalCharacterException, IllegalStateException {
        int nextState=0;
        if (this.getAlphabet().contains(c)){
            if (s <= this.getNumStates() && this.getAcceptingStates().contains(s)){
                while (!this.getTransitions()[s][nextState].contains(c)){ //??
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
        if (this.getCurrentState() <= numStates) return true;
        else return false;
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
