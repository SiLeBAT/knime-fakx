package de.bund.bfr.knime.fakx.nodes.writer;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class WriterNodeDialog extends DefaultNodeSettingsPane {

    WriterNodeDialog() {

        // Create settings model and dialog component
        SettingsModelString stringModel = new SettingsModelString(WriterNodeModel.CFGKEY_FILE, WriterNodeModel.DEFAULT_FILE);
        DialogComponentFileChooser fileChooser = new DialogComponentFileChooser(stringModel, "writer-history", ".fakx");

        addDialogComponent(fileChooser);
    }
}
