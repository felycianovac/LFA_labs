import org.junit.Before;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.assertEquals;

public class ChomskyNormalFormTest {
    private Grammar grammar;
    private ChomskyNormalForm chomskyNormalForm;

    @Before
    public void setUp(){
        /*
        Variant 17
        Q = {q0,q1,q2,q3},
        ∑ = {a,b,c},
        F = {q3},
        δ(q0,a) = q0,
        δ(q0,a) = q1,
        δ(q1,b) = q1,
        δ(q2,b) = q3,
        δ(q1,a) = q2,
        δ(q2,a) = q0.
        Let S=q0, A=q1, B=q2, C=q3.
         */
        List<Character> Vn = Arrays.asList('S', 'A', 'B', 'C');
        List<Character> Vt = Arrays.asList('a', 'b');
        Map<String, List<String>> P = new HashMap<>() {{
            put("S", List.of("aS", "aA"));
            put("A", List.of("bA", "aB"));
            put("B", List.of("bC","aS"));
            put("C", List.of());
        }};
        Character S = 'S';
        grammar = new Grammar(Vn, Vt, P, S);
        chomskyNormalForm = new ChomskyNormalForm(grammar);
    }

    @Test
    public void testEpsilonRemoval() {
        Map<String, List<String>> result = chomskyNormalForm.removeEpsilonProductions();
        //TODO: Replace the placeholders with the expected values
//        Map<String, List<String>> expected = new HashMap<>() {{
//            put("S", List.of("aS", "aA", "a"));
//            put("A", List.of("bA", "aB", "b", "a"));
//            put("B", List.of("bC", "aS", "b"));
//            put("C", List.of());
//        }};
//        assertEquals(expected, result);
    }

    @Test
    public void testUnitProductionsRemoval() {
        Map<String, List<String>> result = chomskyNormalForm.removeUnitProductions();
        //TODO: Replace the placeholders with the expected values
//        Map<String, List<String>> expected = new HashMap<>() {{
//            put("S", List.of("aS", "aA", "a"));
//            put("A", List.of("bA", "aB", "b", "a"));
//            put("B", List.of("bC", "aS", "b"));
//            put("C", List.of());
//        }};
//        assertEquals(expected, result);
    }

    @Test
    public void testInaccessibleProductionsRemoval() {
        Map<String, List<String>> result = chomskyNormalForm.removeInaccessibleSymbols();
        //TODO: Replace the placeholders with the expected values
//        Map<String, List<String>> expected = new HashMap<>() {{
//            put("S", List.of("aS", "aA", "a"));
//            put("A", List.of("bA", "aB", "b", "a"));
//            put("B", List.of("bC", "aS", "b"));
//        }};
//        assertEquals(expected, result);
    }

    @Test
    public void testNonProductiveSymbolsRemoval() {
        Map<String, List<String>> result = chomskyNormalForm.removeNonProductiveSymbols();
        //TODO: Replace the placeholders with the expected values
//        Map<String, List<String>> expected = new HashMap<>() {{
//            put("S", List.of("aS", "aA", "a"));
//            put("A", List.of("bA", "aB", "b", "a"));
//            put("B", List.of("bC", "aS", "b"));
//        }};
//        assertEquals(expected, result);
    }

    @Test
    public void testChomskyNormalFormConversion() {
        Map<String, List<String>> result = chomskyNormalForm.toCNF();
        //TODO: Replace the placeholders with the expected values
//        Map<String, List<String>> expected = new HashMap<>() {{
//            put("S", List.of("aS", "aA", "a"));
//            put("A", List.of("bA", "aB", "b", "a"));
//            put("B", List.of("bC", "aS", "b"));
//        }};
//        assertEquals(expected, result);
    }


}
