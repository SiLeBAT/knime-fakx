package de.bund.bfr.knime.fakx.nodes.reader;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class ReaderNodeDialog extends DefaultNodeSettingsPane {

	ReaderNodeDialog() {

		// Create settings model and dialog component
		SettingsModelString stringModel = new SettingsModelString(ReaderNodeModel.CFGKEY_FILE,
				ReaderNodeModel.DEFAULT_FILE);
		DialogComponentFileChooser fileChooser = new DialogComponentFileChooser(stringModel, "reader-history", ".fakx");

		addDialogComponent(fileChooser);
	}
}
