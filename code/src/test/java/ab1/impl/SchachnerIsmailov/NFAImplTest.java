package ab1.impl.SchachnerIsmailov;

import ab1.NFA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

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
    void testEquals() {
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

}