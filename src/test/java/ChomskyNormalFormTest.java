import org.junit.Before;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChomskyNormalFormTest {
    private Grammar grammar;
    private ChomskyNormalForm chomskyNormalForm;

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
        ChomskyNormalForm cnf = new ChomskyNormalForm(grammar);
        cnf.setP(cnf.removeEpsilonProductions());
        boolean hasEpsilon = cnf.getP().values().stream()
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
        ChomskyNormalForm cnf = new ChomskyNormalForm(grammar);
        cnf.setP(cnf.removeEpsilonProductions());
        cnf.setP(cnf.removeUnitProductions());
        boolean hasUnitProduction = cnf.getP().entrySet().stream()
                .anyMatch(entry -> entry.getValue().stream()
                        .anyMatch(production -> production.length() == 1 && cnf.getVn().contains(production.charAt(0))));
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
        ChomskyNormalForm cnfTransformed = new ChomskyNormalForm(grammar);
        cnfTransformed.setP(cnfTransformed.removeEpsilonProductions());
        cnfTransformed.setP(cnfTransformed.removeUnitProductions());
        cnfTransformed.setP(cnfTransformed.removeInaccessibleSymbols());
        // Collect all accessible symbols starting from the start symbol
        Set<String> accessibleSymbols = new HashSet<>();
        Set<String> toCheck = new HashSet<>();
        accessibleSymbols.add(String.valueOf(cnfTransformed.getS()));
        toCheck.add(String.valueOf(cnfTransformed.getS()));

        while (!toCheck.isEmpty()) {
            Set<String> newToCheck = new HashSet<>();
            for (String symbol : toCheck) {
                List<String> productions = cnfTransformed.getP().get(symbol);
                if (productions != null) {
                    for (String production : productions) {
                        for (char c : production.toCharArray()) {
                            String cStr = String.valueOf(c);
                            if (cnfTransformed.getVn().contains(c) && accessibleSymbols.add(cStr)) {
                                newToCheck.add(cStr);
                            }
                        }
                    }
                }
            }
            toCheck = newToCheck;
        }

        // Verify that all non-terminals are accessible
        for (Character nt : cnfTransformed.getVn()) {
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
        ChomskyNormalForm cnf = new ChomskyNormalForm(grammar);
        cnf.setP(cnf.removeEpsilonProductions());
        cnf.setP(cnf.removeUnitProductions());
        cnf.setP(cnf.removeInaccessibleSymbols());
        cnf.setP(cnf.removeNonProductiveSymbols());

        // Check that all non-terminals in the productions are productive
        Map<String, List<String>> productions = cnf.getP();
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
        ChomskyNormalForm cnf = new ChomskyNormalForm(grammar);
        cnf.setP(cnf.removeEpsilonProductions());
        cnf.setP(cnf.removeUnitProductions());
        cnf.setP(cnf.removeInaccessibleSymbols());
        cnf.setP(cnf.removeNonProductiveSymbols());
        cnf.setP(cnf.toCNF());


        Map<String, List<String>> productions = cnf.getP();
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
