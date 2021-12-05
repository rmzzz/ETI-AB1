package ab1.impl.SchachnerIsmailov;

import ab1.DFA;
import ab1.NFA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class NFAImplTest {

    Ab1Impl factory = new Ab1Impl();

    public static final Set<Character> chars = Set.of('a', 'b', 'c');

    @Test
    void plus() {
        Locale.setDefault(Locale.ROOT);
        assertEquals(StandardCharsets.UTF_8, Charset.defaultCharset());
        var n7 = createNFA7();
        System.out.println(n7);
        assertFalse(n7.acceptsNothing());
        assertFalse(n7.acceptsEpsilon());
        assertFalse(n7.acceptsEpsilonOnly());
        assertFalse(n7.accepts("a"));
        assertFalse(n7.accepts("aa"));
        assertTrue(n7.accepts("ab"));
        assertFalse(n7.accepts("abc"));

        var n = n7.plus();
        System.out.println(n);
        assertFalse(n.acceptsNothing());
        assertFalse(n.acceptsEpsilon());
        assertFalse(n.acceptsEpsilonOnly());
        assertFalse(n.accepts("a"));
        assertFalse(n.accepts("aa"));
        assertTrue(n.accepts("ab"));
        assertFalse(n.accepts("abc"));
        assertTrue(n.accepts("abab"));
        assertTrue(n.accepts("ababab"));
        assertTrue(n.accepts("abababab"));
    }

    @Test
    void concat() {
        var n7 = createNFA7();
        assertFalse(n7.acceptsNothing());
        assertFalse(n7.acceptsEpsilon());
        assertFalse(n7.acceptsEpsilonOnly());
        assertFalse(n7.accepts("a"));
        assertFalse(n7.accepts("aa"));
        assertTrue(n7.accepts("ab"));
        assertFalse(n7.accepts("abc"));

        var n = n7.concat(n7);
        assertFalse(n.acceptsNothing());
        assertFalse(n.acceptsEpsilon());
        assertFalse(n.acceptsEpsilonOnly());
        assertFalse(n.accepts("a"));
        assertFalse(n.accepts("aa"));
        assertFalse(n.accepts("ab"));
        assertFalse(n.accepts("abc"));

        assertTrue(n7.accepts("abab"));

        assertFalse(n7.accepts("ababab"));
        assertTrue(n.concat(n7).accepts("ababab"));
    }

    @Test
    void star() {
        var n7 = createNFA7();
        System.out.println(n7);
        assertFalse(n7.acceptsNothing());
        assertFalse(n7.acceptsEpsilon());
        assertFalse(n7.acceptsEpsilonOnly());
        assertFalse(n7.accepts("a"));
        assertFalse(n7.accepts("aa"));
        assertTrue(n7.accepts("ab"));
        assertFalse(n7.accepts("abc"));

        var n = n7.kleeneStar();
        System.out.println(n);
        assertFalse(n.acceptsNothing());
        assertTrue(n.acceptsEpsilon());
        assertFalse(n.acceptsEpsilonOnly());
        assertFalse(n.accepts("a"));
        assertFalse(n.accepts("aa"));
        assertTrue(n.accepts("ab"));
        assertFalse(n.accepts("abc"));

        assertTrue(n.accepts("abab"));
        assertTrue(n.accepts("ababab"));
        assertTrue(n.accepts("abababab"));
        assertTrue(n.accepts("ababababab"));
    }

    @Test
    void subSetN5() {
        var n5 = createNFA5();
        assertTrue(n5.subSetOf(n5));
    }

    @Test
    void subSetD5() {
        var d5 = createDFA5();
        assertTrue(d5.subSetOf(d5));
    }

    @Test
    void testN5EqualsD5() {
        var n5 = createNFA5();
        var d5 = createDFA5();
        System.out.println(n5);
        System.out.println(d5);
        assertTrue(
                n5.equals(d5));
    }

    @Test
    void toDfa7() {
        var n7 = createNFA7();
        System.out.println(n7);
        var d7 = n7.toDFA();
        System.out.println(d7);
        assertEquals(n7, d7);
    }

    @Test
    void testBigNFA() {
        Set<Character> alphabet = IntStream.range('a', 'z'+1).mapToObj(i -> (char) i).collect(Collectors.toSet());
        int numStates = 100;
        Set<Integer> acceptingStates = Set.of(0, 99);
        NFA n = factory.createNFA(numStates, alphabet, acceptingStates, 0);
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numStates; j++) {
                for(Character c : alphabet) {
                    if((i + j + c) % 47 == 0) {
                        n.setTransition(i, c, j);
                    }
                }
            }
        }
        //System.out.println(n);
        assertFalse(n.acceptsNothing());
        assertTrue(n.acceptsEpsilon());
        assertFalse(n.acceptsEpsilonOnly());
        assertEquals(n, n.toDFA());
        assertTrue(n.accepts("zu"));
    }

    public NFA createNFA7() {
        Set<Integer> accept = new TreeSet<Integer>();
        accept.add(4);

        var n7 = factory.createNFA(5, chars, accept, 0);

        n7.setTransition(0, 'b', 1);
        n7.setTransition(0, 'a', 2);
        n7.setTransition(0, 'a', 3);
        n7.setTransition(1, 'b', 2);
        n7.setTransition(1, 'a', 4);
        n7.setTransition(2, 'a', 2);
        n7.setTransition(2, 'b', 4);
        n7.setTransition(3, 'b', 2);
        return n7;
    }

    public NFA createNFA5() {
        Set<Integer> accept = new TreeSet<Integer>();
        accept.add(0);
        accept.add(1);

        var n5 = factory.createNFA(2, chars, accept, 0);

        n5.setTransition(0, 'a', 0);
        n5.setTransition(0, 'b', 0);
        n5.setTransition(1, 'c', 1);
        n5.setTransition(0, null, 1);

        return n5;
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