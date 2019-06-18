package de.bund.bfr.knime.fakx.nodes.reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.foodauthent.fakx.Archive;
import org.foodauthent.fakx.FAKX;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.util.FileUtil;

import de.bund.bfr.knime.fakx.JSONRow;

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
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[] { null };
	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {

		URL url = FileUtil.toURL(m_path.getStringValue());
		Path localPath = FileUtil.resolveToPath(url);

		Archive archive;

		if (localPath != null) {
			// Read archive from file
			archive = FAKX.read(localPath);
		}
		// if path is an external URL the archive is downloaded to a temporary file
		else {
			try (InputStream inStream = FileUtil.openStreamWithTimeout(url)) {
				// Download to temporary file
				Path tempFile = Files.createTempFile("archive", null);
				Files.copy(inStream, tempFile);
				
				// Read archive
				archive = FAKX.read(tempFile);
				
				// Delete temporary file
				Files.delete(tempFile);
			}
		}

		BufferedDataContainer container = exec.createDataContainer(JSONRow.createSpec());
		container.addRowToTable(new JSONRow(archive));
		container.close();
		
		return new BufferedDataTable[] { container.getTable() };
	}
}
