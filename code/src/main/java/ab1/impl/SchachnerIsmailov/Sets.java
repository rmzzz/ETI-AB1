package ab1.impl.SchachnerIsmailov;

import java.util.HashSet;
import java.util.Set;

public class Sets {
    public static <T> Set<T> union(Set<T> a, Set<T> b) {
        Set<T> u = new HashSet<>(a);
        u.addAll(b);
        return u;
    }

    public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
        Set<T> u = new HashSet<>(a);
        u.removeAll(b);
        return u;
    }
}
