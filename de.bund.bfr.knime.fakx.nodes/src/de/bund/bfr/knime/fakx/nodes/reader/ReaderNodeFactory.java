package de.bund.bfr.knime.fakx.nodes.reader;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class ReaderNodeFactory extends NodeFactory<ReaderNodeModel> {

	@Override
	public ReaderNodeModel createNodeModel() {
		return new ReaderNodeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<ReaderNodeModel> createNodeView(int viewIndex, ReaderNodeModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new ReaderNodeDialog();
	}

}
