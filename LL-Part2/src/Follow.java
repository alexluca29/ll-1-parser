import exceptions.InvalidSymbolException;

import java.util.*;

public class Follow {
    private Map<String, Set<String>> follow;
    private Grammar grammar;
    private First first;

    public Follow(Grammar grammar, First first) throws InvalidSymbolException {
        this.grammar = grammar;
        this.first = first;
        follow = new HashMap<>();

        // There is always a '$' in FOLLOW(starting-symbol)
        follow.put(grammar.getStartingSymbol(), new HashSet<>());
        follow.get(grammar.getStartingSymbol()).add("$");

        for (String nonTerminal: grammar.getNonTerminals()) {
            createFollow(nonTerminal);
        }
    }

    public Set<String> getFollow(String nonTerminal) {
        return follow.get(nonTerminal);
    }

    private void createFollow(String nonTerminal) throws InvalidSymbolException {
        if (!follow.containsKey(nonTerminal)) {
            follow.put(nonTerminal, new HashSet<>());
        }

        // Get all the productions that have 'nonTerminal' in their right hand side.
        List<Production> containingProductions = grammar.getProductionsWithSymbol(nonTerminal);

        for (Production prod: containingProductions) {
            String left = prod.getLeft();
            String right = prod.getRight();

            // Used to parse the right hand side of the production
            // from the first occurrence of 'nonTerminal' forward
            int index = Arrays.asList(right.split(" ")).indexOf(nonTerminal);

            // The symbol after the index
            String nextSymbol;

            // Tells us when to stop parsing
            boolean stop;

            do {
                stop = true;

                try {
                    // Try to get the symbol following the index
                    nextSymbol = right.split(" ")[index + 1];

                    if (grammar.isTerminal(nextSymbol)) {
                        // If the symbol following the index is a terminal,
                        // Add it in FOLLOW(nonTerminal) and stop parsing
                        follow.get(nonTerminal).add(nextSymbol);
                    }
                    else if (grammar.isNonTerminal(nextSymbol)) {
                        // If the symbol following the index is a non-terminal,
                        // then FOLLOW(nonTerminal) = FIRST(nextSymbol)
                        for (String s: first.getFirstForNonTerminal(nextSymbol)) {
                            if (s.equals(Grammar.EPS)) {
                                // If there is an epsilon in FIRST(nextSymbol)
                                // we will continue parsing
                                stop = false;
                                index += 1;
                            }
                            else {
                                // Add FIRST(nextSymbol) in FOLLOW(nonTerminal)
                                follow.get(nonTerminal).add(s);
                            }
                        }
                    }
                    else {
                        throw new InvalidSymbolException("FOLLOW: Unknown symbol " + nextSymbol);
                    }
                } catch (IndexOutOfBoundsException e) {
                    // If there is no other symbol after the non-terminal
                    // then FOLLOW(non-terminal) = FOLLOW(left)

                    if (!left.equals(nonTerminal)) {
                        createFollow(left);

                        for (String s: follow.get(left)) {
                            follow.get(nonTerminal).add(s);
                        }
                    }
                }
            } while (!stop);
        }
    }
}
