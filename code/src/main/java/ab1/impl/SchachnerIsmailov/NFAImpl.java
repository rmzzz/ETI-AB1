package ab1.impl.SchachnerIsmailov;

import ab1.DFA;
import ab1.NFA;
import ab1.exceptions.IllegalCharacterException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NFAImpl implements NFA {
    static final Character EPS = null;

    final int numStates;
    final Set<Character> alphabet;
    final Set<Integer> acceptingStates;
    final Integer initialState;

    /**
     * transitions[from][to] = {c}
     */
    final Set<Character>[][] transitions;

    public NFAImpl(int numStates, Set<Character> characters, Set<Integer> acceptingStates, int initialState) {
        this.numStates = numStates;
        this.alphabet = new HashSet<>(characters);
        this.acceptingStates = acceptingStates;
        this.initialState = initialState;
        transitions = new Set[numStates][numStates];
    }

    @Override
    public Set<Character> getAlphabet() {
        return alphabet;
    }

    @Override
    public Set<Integer> getAcceptingStates() {
        return acceptingStates;
    }

    @Override
    public int getInitialState() {
        return initialState;
    }

    @Override
    public boolean isAcceptingState(int s) throws IllegalStateException {
        checkState(s);
        return acceptingStates.contains(s);
    }

    boolean isAcceptingState(Set<Integer> s) {
        return s.stream().anyMatch(acceptingStates::contains);
    }

    @Override
    public Set<Character>[][] getTransitions() {
        return transitions;
    }

    void checkState(int state) throws IllegalStateException {
        if (state >= numStates || state < 0)
            throw new IllegalStateException("Illegal state: " + state);
    }

    void checkCharacter(Character c) {
        if (c != EPS && !alphabet.contains(c))
            throw new IllegalCharacterException();
    }

    @Override
    public void setTransition(int fromState, Character c, int toState) throws IllegalStateException, IllegalCharacterException {
        checkState(fromState);
        checkState(toState);
        checkCharacter(c);
        if (transitions[fromState][toState] == null) {
            transitions[fromState][toState] = new HashSet<>();
        }
        transitions[fromState][toState].add(c);
    }

    void copyTransitions(Set<Character>[][] delta) {
        copyTransitions(delta, 0);
    }

    void copyTransitions(Set<Character>[][] delta, int stateShift) {
        for (int fromState = 0; fromState < delta.length; fromState++) {
            for (int toState = 0; toState < delta[fromState].length; toState++) {
                Set<Character> s = delta[fromState][toState];
                if (s != null) {
                    for (Character c : s) {
                        setTransition(fromState + stateShift, c, toState + stateShift);
                    }
                }
            }
        }
    }

    @Override
    public void clearTransitions(int fromState, Character c) throws IllegalStateException {
        checkState(fromState);
        for (var s : transitions[fromState]) {
            if (s != null) {
                s.remove(c);
            }
        }
    }

    @Override
    public Set<Integer> getNextStates(int state, Character c) throws IllegalCharacterException, IllegalStateException {
        checkState(state);
        checkCharacter(c);
        return deltaSearch(Set.of(state), s -> s.contains(c));
    }

    @Override
    public int getNumStates() {
        return numStates;
    }

    @Override
    public NFA union(NFA a) {
        // new numStates: this + that + 1 for new initial state
        int uNumStates = numStates + a.getNumStates() + 1;
        // united alphabet
        Set<Character> uAlphabet = new HashSet<>(alphabet);
        uAlphabet.addAll(a.getAlphabet());

        // merge states: leave this states "as is", add initialState, then add a's states shifted after new initialState
        int uInitialState = numStates;
        int stateShift = uInitialState + 1;

        // united accepting states
        Set<Integer> uAcceptingStates = new HashSet<>(acceptingStates);
        // add a's accepting states
        uAcceptingStates.addAll(a.getAcceptingStates().stream().map(s -> s + stateShift).collect(Collectors.toSet()));

        // create union instance: u
        var u = new NFAImpl(uNumStates, uAlphabet, uAcceptingStates, uInitialState);
        // copy this transitions "as is"
        u.copyTransitions(getTransitions(), 0);
        // copy a transitions "after" new initialState
        u.copyTransitions(a.getTransitions(), stateShift);
        // add new initial state epsilon transitions
        u.setTransition(uInitialState, EPS, this.initialState);
        u.setTransition(uInitialState, EPS, a.getInitialState() + stateShift);

        debug("%s\n∪\n%s\n=>\n%s", this, a, u);
        return u;
    }

    /**
     * see VO 2021WS-NFA p.22/24
     * <p>
     * A ∩ -B == -(-A ∪ -B)
     */
    @Override
    public NFA intersection(NFA a) {
        var res = this.complement().union(a.complement()).complement();
        debug("%s\n∩\n%s\n=>\n%s", this, a, res);
        return res;
    }

    /**
     * see VO 2021WS-NFA p.22/24
     * <p>
     * A \ B == A ∩ -B == -(-A ∪ --B) == -(-A ∪ B)
     */
    @Override
    public NFA minus(NFA a) {
        var res = this.intersection(a.complement());
        //var res = this.complement().union(a).complement();
        debug("%s\n\\\n%s\n=>\n%s", this, a, res);
        return res;
    }

    @Override
    public NFA concat(NFA a) {
        int stateShift = numStates;

        Set<Integer> concatAcceptingStates = a.getAcceptingStates().stream()
                .map(s -> s + stateShift)
                .collect(Collectors.toSet());

        var res = new NFAImpl(numStates + a.getNumStates(),
                Sets.union(alphabet, a.getAlphabet()), concatAcceptingStates, initialState);
        // copy this transitions "as is"
        res.copyTransitions(transitions, 0);
        // copy a's transitions with stateShift
        res.copyTransitions(a.getTransitions(), stateShift);
        // add epsilon transitions from this acceptingStates to a's initial state
        for (var f : acceptingStates) {
            res.setTransition(f, EPS, a.getInitialState() + stateShift);
        }
        debug("|\n%s\n%s\n=>\n%s", this, a, res);
        return res;
    }

    /**
     * see VO 2021WS-NFA p22/24
     */
    @Override
    public NFA complement() {
        var res = toDFA().complement();
        debug("-\n%s\n=>\n%s", this, res);
        return res;
    }

    /**
     * see VO 2021WS-NFA p21/24
     */
    @Override
    public NFA kleeneStar() {
        int newInitState = numStates;

        var res = new NFAImpl(numStates + 1, alphabet, Sets.union(acceptingStates, Set.of(newInitState)), newInitState);
        res.copyTransitions(transitions);

        // add epsilon transition from new init to old init
        res.setTransition(newInitState, EPS, initialState);
        // add epsilon transitions from accepting states back to init
        for (int f : acceptingStates) {
            res.setTransition(f, EPS, newInitState);
        }
        debug("*\n%s\n=>\n%s", this, res);
        return res;
    }

    @Override
    public NFA plus() {
        var res = concat(kleeneStar());
        debug("+\n%s\n=>\n%s", this, res);
        return res;
    }

    @Override
    public DFA toDFA() {
        return iterativeToDFA();
    }

    /**
     * see 2021WS-NFA p16/24 -  Iterative Transformation NFA zu DFA
     *
     * @return DFA
     */
    DFA iterativeToDFA() {
        AtomicInteger stateCounter = new AtomicInteger(0);
        Function<Set<Integer>, Integer> qGenerator = k -> stateCounter.getAndIncrement();
        Map<Set<Integer>, Integer> s2q = new HashMap<>();

        Map<Integer, Map<Character, Integer>> qaq1 = new HashMap<>();
        Set<Integer> F1 = new HashSet<>();
        Queue<Set<Integer>> toDefine = new LinkedList<>();
        Set<Integer> sSet0 = Sets.union(Set.of(initialState), deltaSearch(initialState, s -> s.contains(EPS)));
        toDefine.add(sSet0);
        Integer q0 = s2q.computeIfAbsent(sSet0, qGenerator);
        if (!Sets.intersection(sSet0, acceptingStates).isEmpty()) {
            F1.add(q0);
        }
        while (!toDefine.isEmpty()) {
            Set<Integer> sSet = toDefine.poll();
            Integer q = s2q.computeIfAbsent(sSet, qGenerator);
            Map<Character, Integer> qTransitions = qaq1.computeIfAbsent(q, k -> new LinkedHashMap<>());
            Set<Character> a = new TreeSet<>();
            deltaSearch(sSet, s -> {
                for (Character c : s) {
                    if (c != EPS) {
                        a.add(c);
                    }
                }
                return false;
            });
            for (Character c : a) {
                Set<Integer> sSet1 = deltaSearch(sSet, s -> s.contains(c));
                if (!sSet1.isEmpty()) {
                    Integer q1 = s2q.computeIfAbsent(sSet1, qGenerator);
                    qTransitions.put(c, q1);
                    if (!qaq1.containsKey(q1)) {
                        toDefine.add(sSet1);
                    }
                    if (!Sets.intersection(sSet1, acceptingStates).isEmpty()) {
                        F1.add(q1);
                    }
                }
            }
        }

        // finally, ensure completeness of the DFA
        int dfaNumStates = stateCounter.get();
        int sink = dfaNumStates;
        boolean wasIncomplete = false;
        for (int q = 0; q < dfaNumStates; q++) {
            var aq1 = qaq1.get(q);
            for (Character c : alphabet) {
                if (!aq1.containsKey(c)) {
                    aq1.put(c, sink);
                    wasIncomplete = true;
                }
            }
        }
        if (wasIncomplete) {
            dfaNumStates++;
            qaq1.put(sink, alphabet.stream().collect(Collectors.toMap(c -> c, c -> sink)));
        }

        var dfa = new DFAImpl(dfaNumStates, alphabet, F1, q0);
        for (Integer q : qaq1.keySet()) {
            var aq1 = qaq1.get(q);
            for (var transition : aq1.entrySet()) {
                Character a = transition.getKey();
                Integer q1 = transition.getValue();
                dfa.setTransition(q, a, q1);
            }
        }

        debug("NFA->DFA:\n%s\n=>\n%s", this, dfa);
        return dfa;
    }

    Set<Integer> deltaSearch(Integer fromState, Predicate<Set<Character>> transitionPredicate) {
        return deltaSearch(Set.of(fromState), transitionPredicate);
    }

    /**
     * search next states in the δ-relation
     *
     * @param fromStates          set of start states
     * @param transitionPredicate predicate to search state transitions
     * @return set of next states reachable from {@code fromStates} according to {@code transitionPredicate}
     */
    Set<Integer> deltaSearch(Set<Integer> fromStates, Predicate<Set<Character>> transitionPredicate) {
        return deltaSearch(fromStates, transitionPredicate, new HashSet<>());
    }

    Set<Integer> deltaSearch(Set<Integer> fromStates, Predicate<Set<Character>> transitionPredicate, Set<Integer> visitedStates) {
        Set<Integer> next = new HashSet<>();
        Set<Integer> epsilonJumps = new HashSet<>();
        for (int from : fromStates) {
            for (int to = 0; to < numStates; to++) {
                Set<Character> s = transitions[from][to];
                if (s != null) {
                    if (transitionPredicate.test(s)) {
                        next.add(to);
                    }
                    if (s.contains(EPS)) {
                        epsilonJumps.add(to);
                    }
                }
            }
        }
        visitedStates.addAll(fromStates);
        epsilonJumps.removeAll(visitedStates);
        if (!epsilonJumps.isEmpty()) {
            next.addAll(deltaSearch(epsilonJumps, transitionPredicate, visitedStates));
        }
        return next;
    }

    @Override
    public Boolean accepts(String w) throws IllegalCharacterException {
        Set<Integer> fringe = Sets.union(Set.of(initialState), deltaSearch(initialState, s -> s.contains(EPS)));

        for (char c : w.toCharArray()) {
            fringe = deltaSearch(fringe, s -> s.contains(c));
            if (fringe.isEmpty())
                return false;
        }

        return isAcceptingState(fringe);
    }

    @Override
    public Boolean acceptsNothing() {
        return acceptingStates.isEmpty()
                || !acceptsEpsilon() && !acceptsAnyNonEpsilon();
    }

    boolean acceptsAnyNonEpsilon() {
        Set<Integer> fringe = Sets.union(Set.of(initialState), deltaSearch(initialState, s -> s.contains(EPS)));

        while (true) {
            Set<Integer> prev = fringe;
            fringe = deltaSearch(fringe, s -> s.stream().anyMatch(c -> c != EPS));
            if (fringe.isEmpty())
                return false;
            if (isAcceptingState(fringe))
                return true;
            if (fringe.equals(prev))
                return false;
        }
    }

    boolean acceptsAnyNonEpsilonX() {
        Set<Character> symbols = new HashSet<>();
        Predicate<Set<Character>> nonEpsilonPredicate = s -> {
            boolean hasNonEpsilon = false;
            for (Character c : s) {
                if (c != EPS) {
                    symbols.add(c);
                    hasNonEpsilon = true;
                }
            }
            return hasNonEpsilon;
        };
        Set<Integer> fringe = Set.of(initialState);
        while (!fringe.isEmpty()) {
            fringe = deltaSearch(fringe, nonEpsilonPredicate);
            if (!symbols.isEmpty() && fringe.stream().anyMatch(acceptingStates::contains)) {
                return true;
            }
        }
        return false;
    }

    /**
     * find unreachable states
     */
    public Set<Integer> getUnreachableStates() {
        SortedSet<Integer> unreachableStates = new TreeSet<>();
        boolean found;
        do {
            found = false;
            for (int toState = 0; toState < numStates; toState++) {
                if (unreachableStates.contains(toState))
                    continue; //< skip already unreachable!
                boolean isReachable = false;
                for (int fromState = 0; fromState < numStates; fromState++) {
                    if (fromState != toState && (fromState == initialState || !unreachableStates.contains(fromState))) {
                        Set<Character> s = transitions[fromState][toState];
                        if (s != null && !s.isEmpty()) {
                            isReachable = true;
                            break;
                        }
                    }
                }
                if (!isReachable) {
                    unreachableStates.add(toState);
                    found = true;
                }
            }
        } while (found);

        debug("unreachable states: %s", unreachableStates);
        return unreachableStates;
    }

    @Override
    public Boolean acceptsEpsilonOnly() {
        return acceptsEpsilon() && !acceptsAnyNonEpsilon();
//        if (!acceptsEpsilon()) {
//            return false;
//        }
//        Set<Integer> reachableAcceptingStates = new HashSet<>(acceptingStates);
//        reachableAcceptingStates.removeAll(getUnreachableStates());
//        if (!reachableAcceptingStates.isEmpty()) {
//            return false;
//        }
//        // additionally, check the case of accepting initialState with loops
//        return !acceptingStates.contains(initialState)
//                || !deltaSearch(initialState, s -> s.stream().anyMatch(c -> c != EPS))
//                    .contains(initialState);
    }

    @Override
    public Boolean acceptsEpsilon() {
        return acceptingStates.contains(initialState) || accepts("");
    }

    @Override
    public boolean subSetOf(NFA b) {
        return minus(b).acceptsNothing();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof NFA nfa))
            return false;

        //shortcut - if all properties are same
        if (numStates == nfa.getNumStates() && alphabet.equals(nfa.getAlphabet())
                && acceptingStates.equals(nfa.getAcceptingStates())
                && Arrays.deepEquals(transitions, nfa.getTransitions())) {
            return true;
        }

        // general case - FA are equal if they are subSets of each other
        return subSetOf(nfa) && nfa.subSetOf(this);
    }

    @Override
    public String toString() {
        return toString("N=(S={0..%d}, Σ=%s, δ=%s, s₀=%s, F=%s)");
    }

    protected String toString(String format) {
        return String.format(format,
                numStates - 1,
                alphabet.stream().map(String::valueOf)
                        .collect(Collectors.joining(",", "{", "}")),
                transitionsToStrings().stream()
                        .collect(Collectors.joining(",", "{", "}")),
                initialState,
                acceptingStates.stream().map(String::valueOf)
                        .collect(Collectors.joining(",", "{", "}")));
    }

    protected List<String> transitionsToStrings() {
        List<String> strs = new LinkedList<>();
        for (int from = 0; from < transitions.length; from++) {
            for (int to = 0; to < transitions[from].length; to++) {
                Set<Character> s = transitions[from][to];
                if (s != null && !s.isEmpty()) {
                    strs.add(s.stream().map(a -> a == EPS ? "ϵ" : a.toString())
                            .collect(Collectors.joining(",", "(" + from + ",{", "}," + to + ")")));
                }
            }
        }
        return strs;
    }

    static void debug(String msg, Object... params) {
        //ystem.out.printf("DEBUG:\n" + msg + "\n", params);
    }
}
