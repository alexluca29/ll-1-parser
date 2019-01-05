import exceptions.InvalidSymbolException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LLParser {
    private Grammar grammar;
    private First first;
    private Follow follow;
    private ParsingTable table;
    private ParseTree tree;


    public LLParser(Grammar grammar) {
        this.grammar = grammar;
        first = new First(grammar);

        try {
            follow = new Follow(grammar, first);
        }
        catch (InvalidSymbolException e) {
            System.out.println(e.getMessage());
        }

        table = new ParsingTable(grammar, first, follow);
    }

    public boolean parse(String input) {
        input = input + "$";

        Deque<String> stack = new ArrayDeque<>();

        stack.addFirst("$");
        stack.addFirst(grammar.getStartingSymbol());

        // Create the parsing tree and add the first element
        tree = new ParseTree();
        ParseTree originalTree = tree;
        tree.setData(grammar.getStartingSymbol());

        // Used to parse the input string
        int index = 0;

        while (stack.size() > 0) {
            String stackTop = stack.removeFirst();
            String current = new String (new char[] {input.charAt(index)});
            if(tree.children != null) {
                tree = tree.children.get(0);
            }

            if (stackTop.equals(current) && stackTop.equals("$")) {
                // If both the stack top and the current symbol from input are '$',
                // parsing is successful

                // If the sequence is accepted show the parsing tree
                originalTree.iterate();

                return true;
            }

            if (!grammar.isTerminal(current) && !current.equals("$")) {
                // If we found an unknown terminal in the input string,
                // Then the parsing failed.
                return false;
            }

            if (stackTop.equals(current)) {
                // If the stack top and the current symbol are the same,
                // We go to the next symbol from input.
                index += 1;
            }
            else if (grammar.isNonTerminal(stackTop)) {
                // If the stack top is a non-terminal,
                // We get the production used to derive the symbol from input
                // from the non-terminal
                Production production = table.getProduction(stackTop, current);

                if (production == null) {
                    // If such a production doesn't exist, then the parsing fails.
                    return false;
                }

                for (int i = production.getRight().length() - 1; i >= 0; i--) {
                    // We add every symbol from the right hand side of the production
                    // in the stack in reverse order
                    stack.addFirst(new String(new char[] {production.getRight().charAt(i)}));

                    // Add the symbols as children to the current node
                    ParseTree child = new ParseTree();
                    child.setData(new String(new char[] {production.getRight().charAt(i)}));
                    tree.children = new ArrayList<>();
                    tree.addChild(child);
                }
            }
            else if (!stackTop.equals(current)) {
                // If the stack top is a terminal, but it's not the same as
                // the current symbol from input, the parsing fails.
                return false;
            }
        }

        return true;
    }
}
