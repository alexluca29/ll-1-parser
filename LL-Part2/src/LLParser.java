import exceptions.InvalidInputException;
import exceptions.InvalidSymbolException;
import javafx.util.Pair;

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
    private Map<String,Integer> codificationTable;


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

        codificationTable = new HashMap<>();
        initCodificationTable();
    }

    public boolean parsePIF(String pifFilename, String stFilename) {
        List<Pair<String,String>> pifPairs = readPairsFromFile(pifFilename);
        List<Pair<String,String>> symbTablePairs = readPairsFromFile(stFilename);

        List<String> pifElements = new ArrayList<>();
        for(Pair<String,String> pifPair : pifPairs) {
            String pifElem = "";

            // If the key is an identifier or constant look up in the symbol table and get the name
            if(pifPair.getKey().equals("0") || pifPair.getValue().equals("1")) {
                try {
                    pifElem = symbTablePairs.stream()
                            .filter(pair -> pair.getValue().equals(pifPair.getValue()))
                            .map(Pair::getKey)
                            .findFirst().orElseThrow(() -> new InvalidInputException("PIF or SymbolTable not valid"));
                }catch (InvalidInputException e){
                    e.printStackTrace();
                }
            }else {
                pifElem = pifPair.getKey();
            }
            pifElements.add(pifElem);
        }

        pifElements.add("$");

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

            // String which contains the integer value from the codification table
            String nextPIFElem = pifElements.get(index);

            String current = "";
            // If the element can be parsed as int look up in the codification table for the key
            if(tryParseInt(nextPIFElem)) {
                try {
                    current = codificationTable.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(Integer.parseInt(nextPIFElem)))
                            .map(Map.Entry::getKey)
                            .findFirst().orElseThrow(() -> new InvalidInputException("PIF or SymbolTable not valid"));
                } catch (InvalidInputException e) {
                    e.printStackTrace();
                }
            }else {
                // It is a variable - take it as it is
                current = nextPIFElem;
            }

            // If it has children, make the first one as current
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

                for (int i = production.getRight().split(" ").length - 1; i >= 0; i--) {
                    // We add every symbol from the right hand side of the production
                    // in the stack in reverse order
                    stack.addFirst(production.getRight().split(" ")[i]);

                    // Add the symbols as children to the current node
                    ParseTree child = new ParseTree();
                    child.setData(production.getRight().split(" ")[i]);
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

    private List<Pair<String,String>> readPairsFromFile(String filename) {
        List<Pair<String,String>> linesList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    filename));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Pair<String,String> pair = new Pair<>(line.split(" ")[0],line.split(" ")[1]);
                linesList.add(pair);
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
            e.printStackTrace();
        }
        return linesList;
    }

    private void initCodificationTable() {
        codificationTable.put("identifier",0);
        codificationTable.put("constant",1);
        codificationTable.put("psvm",2);
        codificationTable.put("array",3);
        codificationTable.put("of",4);
        codificationTable.put("int",5);
        codificationTable.put("double",6);
        codificationTable.put("bool",7);
        codificationTable.put("{",8);
        codificationTable.put("}",9);
        codificationTable.put("scan",10);
        codificationTable.put("sout",11);
        codificationTable.put("while",12);
        codificationTable.put("do",13);
        codificationTable.put("if",14);
        codificationTable.put("else",15);
        codificationTable.put("&&",16);
        codificationTable.put("||",17);
        codificationTable.put("!",18);
        codificationTable.put(":",19);
        codificationTable.put(";",20);
        codificationTable.put("+",21);
        codificationTable.put("-",22);
        codificationTable.put("*",23);
        codificationTable.put("/",24);
        codificationTable.put("(",25);
        codificationTable.put(")",26);
        codificationTable.put("[",27);
        codificationTable.put("]",28);
        codificationTable.put("<",29);
        codificationTable.put(">",30);
        codificationTable.put("<=",31);
        codificationTable.put(">=",32);
        codificationTable.put("==",33);
        codificationTable.put("=",34);
        codificationTable.put("char",35);
        codificationTable.put("float",36);
        codificationTable.put("!=",37);

    }

    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
