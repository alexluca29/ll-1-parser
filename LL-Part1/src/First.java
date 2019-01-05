import java.util.*;

public class First {
    private Map<Production, Set<String>> first;
    private Grammar grammar;

    public First(Grammar grammar) {
        first = new HashMap<>();
        this.grammar = grammar;

        for (Production production: grammar.getProductions()) {
            createFirst(production);
        }
    }

    public Set<String> getFirst(Production production) {
        return first.get(production);
    }

    public Set<String> getFirstForNonTerminal(String nonTerminal) {
        Set<String> first = new HashSet<>();

        for (Production prod: this.first.keySet()) {
            if (prod.getLeft().equals(nonTerminal)) {
                first.addAll(this.first.get(prod));
            }
        }

        return first;
    }

    private void createFirst(Production production) {
        // If the right hand side of the production is Epsilon
        // We add epsilon in FIRST(production)
        if (production.getRight().equals(Grammar.EPS)) {
            addFirst(production, Grammar.EPS);
            return;
        }

        // used to parse the right hand side of the production
        int index = 0;

        // used to know if the right hand side parsing is done
        boolean ok = false;

        while (!ok) {
            // stores the current character from the right hand side of the production
            String current;

            if (index == production.getRight().length())
                break;
            else
                current = new String(new char[] {production.getRight().charAt(index)});

            if (grammar.isTerminal(current)) {
                // If the current character from the right hand side of the production
                // is a terminal, we add it in FIRST(production) and the parsing ends
                addFirst(production, current);
                ok = true;
            }
            else if (current.equals(Grammar.EPS)) {
                // If the current character is a epsilon,
                // we add it in FIRST(production) and the parsing ends
                addFirst(production, Grammar.EPS);
                ok = true;
            }
            else if (grammar.isNonTerminal(current)) {
                // If the current character is a non-terminal
                // then we have to replace it with each symbol from
                // its FIRST list.

                // Get all the productions for the non-terminal
                List<Production> productions = grammar.getProductionsForNonTerminal(current);

                // Tells us of there is epsilon in FIRST(non-terminal)
                boolean containsEpsilon = false;

                // Iterate over the productions and compute first for each of them
                for (Production prod: productions) {
                    if (!first.containsKey(prod)) {
                        createFirst(prod);
                    }

                    // Add all the symbols from FIRST(non-terminal) in FIRST(production)
                    for (String symbol: first.get(prod)) {
                        if (symbol.equals(Grammar.EPS)) {
                            containsEpsilon = true;
                        }
                        else {
                            addFirst(production, symbol);
                        }
                    }
                }

                // If we found epsilon if FIRST(non-terminal) and there is nothing left to parse
                // we add epsilon in FIRST(production)
                if (containsEpsilon && index == production.getRight().length() - 1) {
                    addFirst(production, Grammar.EPS);
                    ok = true;
                }
                else {
                    // Else, we continue parsing
                    index += 1;
                }
            }
            else {
                // Parsing should stop here because of an unknown symbol.
                System.out.println("Error");
            }
        }
    }

    private void addFirst(Production production, String symbol) {
        if (!first.containsKey(production)) {
            first.put(production, new HashSet<>());
        }

        first.get(production).add(symbol);
    }
}
