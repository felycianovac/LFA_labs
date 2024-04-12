import org.junit.Before;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChomskyNormalFormTest {
    private Grammar grammar;
    private Grammar grammar2;
    private ChomskyNormalForm chomskyNormalForm;
    private ChomskyNormalForm cnf2;

    @Before
    public void setUp() {
        List<Character> Vn = Arrays.asList('S', 'A', 'B', 'C', 'D');
        List<Character> Vt = Arrays.asList('a', 'b');
        Map<String, List<String>> P = new HashMap<>() {{
            put("S", List.of("abAB"));
            put("A", List.of("aSab", "BS", "aA", "b"));
            put("B", List.of("BA", "ababB", "b", "ε"));
            put("C", List.of("AS"));
        }};
        Character S = 'S';
        grammar = new Grammar(Vn, Vt, P, S);
        chomskyNormalForm = new ChomskyNormalForm(grammar);

        //Test for another variant, let's say 8
        List<Character> Vn2 = Arrays.asList('S', 'A', 'B', 'C');
        List<Character> Vt2 = Arrays.asList('a', 'd');
        Map<String, List<String>> P2 = new HashMap<>() {{
            put("S", List.of("dB", "A"));
            put("A", List.of("d", "dS","aAdAB"));
            put("B", List.of("a", "aS","A","ε"));
            put("C", List.of("Aa"));
        }};
        Character S2 = 'S';
        grammar2 = new Grammar(Vn2, Vt2, P2, S2);
        cnf2 = new ChomskyNormalForm(grammar2);

    }

    @Test
    public void testEpsilonRemovalVariant() {
        //test correctness of conversion
        Map<String, List<String>> result = chomskyNormalForm.removeEpsilonProductions();
        Map<String, List<String>> expected = new HashMap<>() {{
            put("S", List.of("abA", "abAB"));
            put("A", List.of("aA", "BS", "b", "S", "aSab"));
            put("B", List.of("A", "b", "ababB", "BA", "abab"));
            put("C", List.of("AS"));
        }};
        assertEquals(expected, result);
    }

    @Test
    public void testEpsilonRemoval() {
        cnf2.setP(cnf2.removeEpsilonProductions());
        boolean hasEpsilon = cnf2.getP().values().stream()
                .anyMatch(productions -> productions.contains("ε"));
        assertEquals(false, hasEpsilon);
    }

    @Test
    public void testUnitProductionsRemovalVariant() {
        // First remove epsilon productions

        chomskyNormalForm.setP(chomskyNormalForm.removeEpsilonProductions());
        Map<String, List<String>> result = chomskyNormalForm.removeUnitProductions();

        Map<String, List<String>> expected = new HashMap<>() {{
            put("S", List.of("abA", "abAB"));
            put("A", List.of("aA", "BS", "b", "aSab", "abA", "abAB"));
            put("B", List.of("b", "ababB", "BA", "abab", "aA", "BS", "b", "aSab", "abA", "abAB"));
            put("C", List.of("AS"));
        }};
        assertEquals(expected, result);
    }

    @Test
    public void testUnitProductionsRemoval() {
        cnf2.setP(cnf2.removeEpsilonProductions());
        cnf2.setP(cnf2.removeUnitProductions());
        boolean hasUnitProduction = cnf2.getP().entrySet().stream()
                .anyMatch(entry -> entry.getValue().stream()
                        .anyMatch(production -> production.length() == 1 && cnf2.getVn().contains(production.charAt(0))));
        assertEquals(false, hasUnitProduction);
    }

    @Test
    public void testInaccessibleProductionsRemovalVariant() {
        chomskyNormalForm.setP(chomskyNormalForm.removeEpsilonProductions());
        chomskyNormalForm.setP(chomskyNormalForm.removeUnitProductions());
        Map<String, List<String>> result = chomskyNormalForm.removeInaccessibleSymbols();

        Map<String, List<String>> expected = new HashMap<>() {{
            put("S", List.of("abA", "abAB"));
            put("A", List.of("aA", "BS", "b", "aSab", "abA", "abAB"));
            put("B", List.of("b", "ababB", "BA", "abab", "aA", "BS", "b", "aSab", "abA", "abAB"));
        }};
        assertEquals(expected, result);
    }

    @Test
    public void testInaccessibleSymbolsRemoval() {
        cnf2.setP(cnf2.removeEpsilonProductions());
        cnf2.setP(cnf2.removeUnitProductions());
        cnf2.setP(cnf2.removeInaccessibleSymbols());
        // Collect all accessible symbols starting from the start symbol
        Set<String> accessibleSymbols = new HashSet<>();
        Set<String> toCheck = new HashSet<>();
        accessibleSymbols.add(String.valueOf(cnf2.getS()));
        toCheck.add(String.valueOf(cnf2.getS()));

        while (!toCheck.isEmpty()) {
            Set<String> newToCheck = new HashSet<>();
            for (String symbol : toCheck) {
                List<String> productions = cnf2.getP().get(symbol);
                if (productions != null) {
                    for (String production : productions) {
                        for (char c : production.toCharArray()) {
                            String cStr = String.valueOf(c);
                            if (cnf2.getVn().contains(c) && accessibleSymbols.add(cStr)) {
                                newToCheck.add(cStr);
                            }
                        }
                    }
                }
            }
            toCheck = newToCheck;
        }

        // Verify that all non-terminals are accessible
        for (Character nt : cnf2.getVn()) {
            assertTrue("Non-terminal " + nt + " is inaccessible", accessibleSymbols.contains(String.valueOf(nt)));
        }

    }

    @Test
    public void testNonProductiveSymbolsRemovalVariant() {
        chomskyNormalForm.setP(chomskyNormalForm.removeEpsilonProductions());
        chomskyNormalForm.setP(chomskyNormalForm.removeUnitProductions());
        chomskyNormalForm.setP(chomskyNormalForm.removeInaccessibleSymbols());
        Map<String, List<String>> result = chomskyNormalForm.removeNonProductiveSymbols();
        Map<String, List<String>> expected = new HashMap<>() {{
            put("S", List.of("abA", "abAB"));
            put("A", List.of("aA", "BS", "b", "aSab","abA","abAB"));
            put("B", List.of("b", "ababB", "BA","abab","aA","BS","b","aSab","abA","abAB"));
        }};
        assertEquals(expected, result);
    }

    @Test
    public void testNonProductiveSymbolsRemoval(){
        // Apply CNF transformation
        cnf2.setP(cnf2.removeEpsilonProductions());
        cnf2.setP(cnf2.removeUnitProductions());
        cnf2.setP(cnf2.removeInaccessibleSymbols());
        cnf2.setP(cnf2.removeNonProductiveSymbols());

        // Check that all non-terminals in the productions are productive
        Map<String, List<String>> productions = cnf2.getP();
        Set<String> productiveSymbols = new HashSet<>(productions.keySet()); // Assuming removeNonProductiveSymbols is correct

        boolean allProductive = true;
        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            for (String production : entry.getValue()) {
                for (int i = 0; i < production.length(); i++) {
                    char symbol = production.charAt(i);
                    if (Character.isUpperCase(symbol) && !productiveSymbols.contains(String.valueOf(symbol))) {
                        allProductive = false;
                        break;
                    }
                }
                if (!allProductive) break;
            }
            if (!allProductive) break;
        }

        assertEquals(true, allProductive);

    }


    @Test
    public void testChomskyNormalFormConversionVariant() {
        chomskyNormalForm.setP(chomskyNormalForm.removeEpsilonProductions());
        chomskyNormalForm.setP(chomskyNormalForm.removeUnitProductions());
        chomskyNormalForm.setP(chomskyNormalForm.removeInaccessibleSymbols());
        chomskyNormalForm.setP(chomskyNormalForm.removeNonProductiveSymbols());
        Map<String, List<String>> result = chomskyNormalForm.toCNF();
        Map<String, List<String>> expected = new HashMap<>() {{
            put("S", List.of("GA", "HB"));
            put("A", List.of("CA", "BS", "b", "FD","GA","HB"));
            put("B", List.of("b", "JB", "BA", "ID", "CA", "BS", "b", "FD", "GA", "HB"));
            put("C", List.of("a"));
            put("D", List.of("b"));
            put("E", List.of("CS"));
            put("F", List.of("EC"));
            put("G", List.of("CD"));
            put("H", List.of("GA"));
            put("I", List.of("GC"));
            put("J", List.of("ID"));
        }};
        assertEquals(expected, result);
    }


    @Test
    public void testChomskyNormalFormConversion() {
        cnf2.setP(cnf2.removeEpsilonProductions());
        cnf2.setP(cnf2.removeUnitProductions());
        cnf2.setP(cnf2.removeInaccessibleSymbols());
        cnf2.setP(cnf2.removeNonProductiveSymbols());
        cnf2.setP(cnf2.toCNF());


        Map<String, List<String>> productions = cnf2.getP();
        Set<String> productiveSymbols = new HashSet<>(productions.keySet()); // Assuming these are all productive now

        boolean allProductive = true;
        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            for (String production : entry.getValue()) {
                // Check each symbol in production to ensure it's either a terminal or a productive non-terminal
                for (char symbol : production.toCharArray()) {
                    if (Character.isUpperCase(symbol) && !productiveSymbols.contains(String.valueOf(symbol))) {
                        allProductive = false;
                        break;
                    }
                }
                if (!allProductive) break;
            }
            if (!allProductive) break;
        }

        assertEquals(true, allProductive);
    }


}
