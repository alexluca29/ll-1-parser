public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Grammar has to be DETERMINISTIC
        // TODO: Grammar must NOT contain LEFT RECURSION

        Grammar g = new Grammar("src/grammars/grammar_luca.json");
        LLParser parser = new LLParser(g);
        System.out.println(parser.parsePIF("src/PIFs/Max3NoPIF.txt","src/symbolTables/Max3NoST.txt"));
    }
}
