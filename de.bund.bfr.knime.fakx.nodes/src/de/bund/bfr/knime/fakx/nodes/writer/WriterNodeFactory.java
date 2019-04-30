package de.bund.bfr.knime.fakx.nodes.writer;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class WriterNodeFactory extends NodeFactory<WriterNodeModel> {

    @Override
    public WriterNodeModel createNodeModel() {
        return new WriterNodeModel();
    }

    @Override
    public int getNrNodeViews() {
        return 0;
    }

    @Override
    public NodeView<WriterNodeModel> createNodeView(int viewIndex, WriterNodeModel nodeModel) {
        return null;
    }

    @Override
    protected boolean hasDialog() {
        return true;
    }

    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new WriterNodeDialog();
    }
}
