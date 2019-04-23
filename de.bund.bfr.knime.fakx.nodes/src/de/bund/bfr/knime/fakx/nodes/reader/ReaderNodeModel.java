package de.bund.bfr.knime.fakx.nodes.reader;

import java.io.File;
import java.io.IOException;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class ReaderNodeModel extends NodeModel {
	
	static final String CFGKEY_FILE = "filename";
	static final String DEFAULT_FILE = "";
	
	private final SettingsModelString m_path = new SettingsModelString(ReaderNodeModel.CFGKEY_FILE,
			ReaderNodeModel.DEFAULT_FILE);

	ReaderNodeModel() {
		super(0, 1);
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// No internal data
	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// No internal data
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		m_path.saveSettingsTo(settings);
	}

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		m_path.validateSettings(settings);
		
	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		m_path.loadSettingsFrom(settings);
	}

	@Override
	protected void reset() {
		// No internal data to reset
		// No models build during execute to reset
	}
	
	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {
		// TODO Auto-generated method stub
		return super.execute(inData, exec);
	}
}
