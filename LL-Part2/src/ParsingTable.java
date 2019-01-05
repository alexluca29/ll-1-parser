import java.util.HashMap;
import java.util.Map;

public class ParsingTable {
    private Map<Pair, Production> table;

    private First first;
    private Follow follow;
    private Grammar grammar;

    /*
    The key for the table is a Pair and the value is a Production

    Pair has two fields: first and second
    first is a non-terminal
    second is a terminal

    An entry from the table (Pair: Production) can be read as follows:
    If you have to derive Pair.second (terminal) from Pair.first (non-terminal), then
    use this production (Production).
     */

    public ParsingTable(Grammar grammar, First first, Follow follow) {
        table = new HashMap<>();

        this.grammar = grammar;
        this.first = first;
        this.follow = follow;

        buildTable();
    }

    private void buildTable() {
        for (Production production: grammar.getProductions()) {
            if (production.getRight().equals(Grammar.EPS)) {
                for (String s: follow.getFollow(production.getLeft())) {
                    add(production.getLeft(), s, production);
                }
            }
            else {
                for(String s: first.getFirst(production)) {
                    if (!s.equals(Grammar.EPS)) {
                        add(production.getLeft(), s, production);
                    }
                }
            }
        }
    }

    public void add(String nonTerminal, String terminal, Production production) {
        Pair pair = new Pair(nonTerminal, terminal);

        if (!table.containsKey(pair)) {
            table.put(pair, production);
        }
    }

    public Production getProduction(String nonTerminal, String terminal) {
        // Returns the production used to derive the given terminal
        // from the given non-terminal

        Pair pair = new Pair(nonTerminal, terminal);

        return table.get(pair);
    }
}
