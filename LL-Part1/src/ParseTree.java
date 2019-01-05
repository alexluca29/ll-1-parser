import java.util.ArrayDeque;
import java.util.List;

public class ParseTree {

    String data;
    ParseTree parent;
    List<ParseTree> children;

    public ParseTree(ParseTree root) {
        this.data = root.data;
        this.children = root.children;
    }

    public ParseTree() { }

    public void iterate() {
        ArrayDeque<ParseTree> queue = new ArrayDeque<>();
        queue.add(this);
        while(!queue.isEmpty()){
            ParseTree parseTree = queue.pop();
            System.out.print(parseTree.data + " ");
            if(parseTree.children != null){
                queue.addAll(parseTree.children);
            }
        }
    }

    public void addChild(ParseTree child){
        ParseTree childNode = new ParseTree(child);
        childNode.parent = this;
        this.children.add(childNode);
    }

    public void setData(String data) {
        this.data = data;
    }
}
