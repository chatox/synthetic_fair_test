package multinomial.util;

public class NodeDisplayWrapper {

    public final String text;
    public final int width;
    public final int height;
    public final TreeNode<int[]> node;

    public NodeDisplayWrapper(String text, int width, int height, TreeNode<int[]> node){
        this.text = text;
        this.width = width;
        this.height = height;
        this.node = node;
    }
}
