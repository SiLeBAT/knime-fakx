package de.bund.bfr.knime.fakx.nodes.reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.foodauthent.fakx.Archive;
import org.foodauthent.fakx.FAKX;
import org.foodauthent.model.FileMetadata;
import org.foodauthent.model.Product;
import org.foodauthent.model.SOP;
import org.foodauthent.model.Tag;
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

import de.bund.bfr.knime.fakx.MetadataRow;
import de.bund.bfr.knime.fakx.ProductRow;
import de.bund.bfr.knime.fakx.SopRow;
import de.bund.bfr.knime.fakx.TagRow;

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

		URL url = FileUtil.toURL(m_path.getStringValue());
		Path localPath = FileUtil.resolveToPath(url);

		Archive archive;

		if (localPath != null) {
			// Read archive from file
			try (InputStream stream = Files.newInputStream(localPath)) {
				archive = FAKX.read(stream);
			}
		}
		// if path is an external URL the archive is downloaded to a temporary file
		else {
			try (InputStream inStream = FileUtil.openStreamWithTimeout(url)) {
				archive = FAKX.read(inStream);
			}
		}

		BufferedDataTable productTable = createProductTable(exec, archive.getProduct());
		BufferedDataTable sopTable = createSopTable(exec, archive.getSOP());
		BufferedDataTable metadataTable = createMetadataTable(exec, archive.getMetadata());
		BufferedDataTable tagTable = createTagTable(exec, archive.getTag());

		return new BufferedDataTable[] { productTable, sopTable, metadataTable, tagTable };
	}
	
	private static BufferedDataTable createProductTable(ExecutionContext exec, List<Product> products) {
		BufferedDataContainer container = exec.createDataContainer(ProductRow.createSpec());
		products.stream().map(ProductRow::new).forEach(container::addRowToTable);
		container.close();
		
		return container.getTable();
	}
	
	private static BufferedDataTable createSopTable(ExecutionContext exec, List<SOP> sops) {
		BufferedDataContainer container = exec.createDataContainer(SopRow.createSpec());
		sops.stream().map(SopRow::new).forEach(container::addRowToTable);
		container.close();
		
		return container.getTable();
	}
	
	private static BufferedDataTable createMetadataTable(ExecutionContext exec, List<FileMetadata> metadata) {
		BufferedDataContainer container = exec.createDataContainer(MetadataRow.createSpec());
		metadata.stream().map(MetadataRow::new).forEach(container::addRowToTable);
		container.close();
		
		return container.getTable();
	}
	
	private static BufferedDataTable createTagTable(ExecutionContext exec, List<Tag> tag) {
		BufferedDataContainer container = exec.createDataContainer(TagRow.createSpec());
		tag.stream().map(TagRow::new).forEach(container::addRowToTable);
		container.close();
		
		return container.getTable();
	}
}
