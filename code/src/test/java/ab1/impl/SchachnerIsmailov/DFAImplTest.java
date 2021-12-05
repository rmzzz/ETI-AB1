package ab1.impl.SchachnerIsmailov;

import ab1.DFA;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class DFAImplTest {

    Ab1Impl factory = new Ab1Impl();

    public static final Set<Character> chars = Set.of('a', 'b', 'c');

    @Test
    void reset() {
        var d = createDFA6();
        d.doStep('c');
        assertEquals(1, d.getCurrentState());
        d.reset();
        assertEquals(0, d.getCurrentState());
    }

    @Test
    void getCurrentState() {
        var d = createDFA5();
        assertEquals(0, d.getInitialState());
        assertEquals(0, d.getCurrentState());
        assertEquals(1, d.doStep('c'));
        assertEquals(1, d.getCurrentState());
    }

    @Test
    void doStep() {
        var d = createDFA6();
        System.out.println(d);
        assertEquals(0, d.doStep('a'));
        assertEquals(0, d.doStep('b'));
        assertEquals(1, d.doStep('c'));
        assertEquals(1, d.doStep('c'));
        assertEquals(0, d.doStep('a'));
    }

    @Test
    void getNextState() {
        var d = createDFA6();
        assertEquals(0, d.getNextState(0, 'a'));
        assertEquals(0, d.getNextState(0, 'b'));
        assertEquals(1, d.getNextState(0, 'c'));
        assertEquals(1, d.getNextState(1, 'c'));
        assertEquals(0, d.getNextState(1, 'a'));
        assertEquals(0, d.getNextState(1, 'b'));
    }

    @Test
    void isInAcceptingState() {
        var d = createDFA5();
        assertTrue(d.isInAcceptingState());
        assertEquals(0, d.doStep('a'));
        assertTrue(d.isInAcceptingState());
        assertEquals(0, d.doStep('b'));
        assertTrue(d.isInAcceptingState());
        assertEquals(1, d.doStep('c'));
        assertTrue(d.isInAcceptingState());
        assertEquals(1, d.doStep('c'));
        assertTrue(d.isInAcceptingState());
        assertThrows(IllegalStateException.class, () -> d.doStep('a'));
        assertTrue(d.isInAcceptingState());
    }

    @Test
    void complementD5() {
        var d5 = createDFA5();
        System.out.println(d5);
        var notD5 = d5.complement();
        System.out.println(notD5);
        assertFalse(notD5.acceptsNothing());
        assertEquals(d5, notD5.complement());
    }
    @Test
    void complementD6() {
        var d6 = createDFA6();
        System.out.println(d6);
        var notD6 = d6.complement();
        System.out.println(notD6);
        assertTrue(notD6.acceptsNothing());
        assertEquals(d6, notD6.complement());
    }

    @Test
    void toDFA() {
        var d = createDFA5();
        assertSame(d, d.toDFA());

        d = createDFA6();
        assertSame(d, d.toDFA());
    }

    public DFA createDFA5() {
        Set<Integer> accept = new TreeSet<Integer>();
        accept.add(0);
        accept.add(1);

        var d5 = factory.createDFA(2, chars, accept, 0);

        d5.setTransition(0, 'a', 0);
        d5.setTransition(0, 'b', 0);
        d5.setTransition(0, 'c', 1);
        d5.setTransition(1, 'c', 1);

        return d5;
    }

    public DFA createDFA6() {
        Set<Integer> accept = new TreeSet<Integer>();
        accept.add(0);
        accept.add(1);

        DFA d6 = factory.createDFA(2, chars, accept, 0);

        d6.setTransition(0, 'a', 0);
        d6.setTransition(0, 'b', 0);
        d6.setTransition(0, 'c', 1);
        d6.setTransition(1, 'c', 1);
        d6.setTransition(1, 'a', 0);
        d6.setTransition(1, 'b', 0);

        return d6;
    }
}