public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Grammar has to be DETERMINISTIC
        // TODO: Grammar must NOT contain LEFT RECURSION

       /* Grammar g = new Grammar("src/grammars/grammar2.json");
        LLParser parser = new LLParser(g);

        System.out.println(parser.parse("gg"));*/

        Grammar g = new Grammar("src/grammars/grammar4.json");
        LLParser parser = new LLParser(g);

        System.out.println(parser.parse("a*(a+a)"));
    }
}
