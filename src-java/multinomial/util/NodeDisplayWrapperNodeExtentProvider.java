package multinomial.util;

import org.abego.treelayout.NodeExtentProvider;

public class NodeDisplayWrapperNodeExtentProvider implements NodeExtentProvider<NodeDisplayWrapper> {
    @Override
    public double getWidth(NodeDisplayWrapper nodeDisplayWrapper) {
        return nodeDisplayWrapper.width;
    }

    @Override
    public double getHeight(NodeDisplayWrapper nodeDisplayWrapper) {
        return nodeDisplayWrapper.height;
    }
}
