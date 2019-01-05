import org.json.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Grammar {
    public static final String EPS = "";

    private List<String> terminals;
    private List<String> nonTerminals;
    private List<Production> productions;
    private String startingSymbol;

    public Grammar(String filename) {
        terminals = new ArrayList<>();
        nonTerminals = new ArrayList<>();
        productions = new ArrayList<>();

        String json = "";

        try {
            json = getJSON(filename);
        }
        catch (IOException e) {
            System.out.println("Unable to read from file " + filename);
            e.printStackTrace();
        }

        try {
            buildObjectFromJSON(json);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isTerminal(String symbol) {
        return terminals.contains(symbol);
    }

    public boolean isNonTerminal(String symbol) {
        return nonTerminals.contains(symbol);
    }

    private String getJSON(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public List<Production> getProductionsWithSymbol(String symbol) {
        List<Production> l = new ArrayList<>();

        for (Production production: productions) {
            if (production.getRight().contains(symbol)) {
                l.add(production);
            }
        }

        return l;
    }

    public List<String> getNonTerminals() {
        return nonTerminals;
    }

    public List<Production> getProductionsForNonTerminal(String nonTermial) {
        List<Production> productions = new ArrayList<>();

        for (Production production: this.productions) {
            if (production.getLeft().equals(nonTermial)) {
                productions.add(production);
            }
        }

        return productions;
    }

    private void buildObjectFromJSON(String json) throws JSONException {
        JSONObject object = new JSONObject(json);

        // Get Starting Symbol from JSON
        this.startingSymbol = object.getJSONObject("grammar").getString("startingSymbol");

        // Get Terminals from JSON
        JSONArray terminalsArray = object.getJSONObject("grammar").getJSONArray("terminals");
        for (int i = 0; i < terminalsArray.length(); i++) {
            terminals.add(terminalsArray.getString(i));
        }

        // Get Non Terminals from JSON
        JSONArray nonTerminalsArray = object.getJSONObject("grammar").getJSONArray("nonTerminals");
        for (int i = 0; i < nonTerminalsArray.length(); i++) {
            nonTerminals.add(nonTerminalsArray.getString(i));
        }

        // Get productions from JSON
        JSONArray productionsArray = object.getJSONObject("grammar").getJSONArray("productions");
        for (int i = 0; i < productionsArray.length(); i++) {
            JSONObject productionObject = productionsArray.getJSONObject(i);

            JSONArray right = productionObject.getJSONArray("right");
            for (int j = 0; j < right.length(); j++) {
                Production production = new Production(
                        productionObject.getString("left"),
                        right.getString(j));
                productions.add(production);
            }

        }
    }

    public Production getProduction(String nonTerminal) {
        for (Production production: productions) {
            if (production.getLeft().equals(nonTerminal)) {
                return production;
            }
        }

        return null;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public String getStartingSymbol() {
        return startingSymbol;
    }
}
