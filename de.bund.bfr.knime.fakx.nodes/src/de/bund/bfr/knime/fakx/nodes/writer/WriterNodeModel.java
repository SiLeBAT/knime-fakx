package de.bund.bfr.knime.fakx.nodes.writer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.foodauthent.fakx.Archive;
import org.foodauthent.fakx.FAKX;
import org.knime.core.data.DataTableSpec;
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

public class WriterNodeModel extends NodeModel {

	static final String CFGKEY_FILE = "filename";
	static final String DEFAULT_FILE = "";

	private final SettingsModelString m_path = new SettingsModelString(WriterNodeModel.CFGKEY_FILE,
			WriterNodeModel.DEFAULT_FILE);

	WriterNodeModel() {
		super(1, 0);
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
		return new DataTableSpec[] {};
	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {

		BufferedDataTable in = (BufferedDataTable) inData[0];
		JSONRow row = (JSONRow) in.iterator().next();

		// Build Archive using convenience getters
		Archive.Builder builder = new Archive.Builder();
		builder.product(row.getProducts());
		builder.sop(row.getSOP());
		builder.metadata(row.getMetadata());
		builder.tag(row.getTag());
		builder.model(row.getModel());
		builder.prediction(row.getPrediction());
		builder.workflow(row.getWorkflow());
		builder.fingerprint(row.getFingerprint());
		builder.fingerprintset(row.getFingerprintSet());
		Archive archive = builder.build();

		URL url = FileUtil.toURL(m_path.getStringValue());
		Path localPath = FileUtil.resolveToPath(url);

		// If path is not local write to URL
		if (localPath != null) {
			Files.deleteIfExists(localPath);
			FAKX.write(archive, localPath);
		} else {

			// Creates archive in temporary archive file
			Path temporaryFile = Files.createTempFile("model", "fakx");

			// Write archive
			FAKX.write(archive, temporaryFile);

			// Copy temporary file to output stream
			try (OutputStream os = FileUtil.openOutputConnection(url, "PUT").getOutputStream()) {
				Files.copy(temporaryFile, os);
			}

			// Deletes temporary file
			Files.delete(localPath);
		}

		return new BufferedDataTable[] {};
	}
}
