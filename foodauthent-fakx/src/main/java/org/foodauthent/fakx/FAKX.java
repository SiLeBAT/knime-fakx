package org.foodauthent.fakx;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.foodauthent.model.FaModel;
import org.foodauthent.model.FileMetadata;
import org.foodauthent.model.Fingerprint;
import org.foodauthent.model.FingerprintSet;
import org.foodauthent.model.Model;
import org.foodauthent.model.Prediction;
import org.foodauthent.model.Product;
import org.foodauthent.model.SOP;
import org.foodauthent.model.Tag;
import org.foodauthent.model.Workflow;
import org.jdom2.JDOMException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.unirostock.sems.cbarchive.ArchiveEntry;
import de.unirostock.sems.cbarchive.CombineArchive;
import de.unirostock.sems.cbarchive.CombineArchiveException;

/**
 * Reads/writes contents to FAKX file with structure:
 *
 * <ul>
 * <li>sop.json
 * <li>product.json
 * <li>metadata.json
 * <li>tag.json
 * <li>model.json
 * <li>prediction.json
 * <li>workflow.json
 * <li>fingerprint.json
 * <li>fingerprintset.json
 * </ul>
 */
public class FAKX {

	private static ObjectMapper MAPPER;

	static {
		// ObjectMapper defaults to use a JsonFactory that automatically closes
		// the stream. When further entries are added to the archive the stream
		// is closed and fails. The AUTO_CLOSE_TARGET needs to be disabled.
		JsonFactory jsonFactory = new JsonFactory();
		jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		jsonFactory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);

		MAPPER = new ObjectMapper(jsonFactory);
		MAPPER.registerModule(new JavaTimeModule());

		// Configure MAPPER to ignore properties not defined (FaModel#getTypeID)
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private static URI BIN_URI = URI.create("application/octet-stream");
	private static URI JSON_URI = URI.create("http://purl.org/NET/mediatypes/application/json");

	// Jackson type references
	private static TypeReference<List<SOP>> sopListType = new TypeReference<List<SOP>>() {
	};
	private static TypeReference<List<Product>> productListType = new TypeReference<List<Product>>() {
	};
	private static TypeReference<List<FileMetadata>> metadataListType = new TypeReference<List<FileMetadata>>() {
	};
	private static TypeReference<List<Tag>> tagListType = new TypeReference<List<Tag>>() {
	};
	private static TypeReference<List<Model>> modelListType = new TypeReference<List<Model>>() {
	};
	private static TypeReference<List<Prediction>> predictionListType = new TypeReference<List<Prediction>>() {
	};
	private static TypeReference<List<Workflow>> workflowListType = new TypeReference<List<Workflow>>() {
	};
	private static TypeReference<List<Fingerprint>> fingerprintListType = new TypeReference<List<Fingerprint>>() {
	};
	private static TypeReference<List<FingerprintSet>> fingerprintsetListType = new TypeReference<List<FingerprintSet>>() {
	};

	public static Archive read(Path file) throws IOException, JDOMException, ParseException, CombineArchiveException {

		Archive.Builder builder = new Archive.Builder();

		try (CombineArchive ca = new CombineArchive(file.toFile())) {
			for (ArchiveEntry entry : ca.getEntriesWithFormat(JSON_URI)) {
				final String entryName = entry.getFileName();

				// Read JSON
				File tempFile = File.createTempFile("data", ".json");
				entry.extractFile(tempFile);
				String json = FileUtils.readFileToString(tempFile, "UTF-8");
				tempFile.delete();

				if (entryName.equals("sop.json")) {
					builder.sop(MAPPER.readValue(json, sopListType));
				} else if (entryName.equals("product.json")) {
					builder.product(MAPPER.readValue(json, productListType));
				} else if (entryName.equals("metadata.json")) {
					builder.metadata(MAPPER.readValue(json, metadataListType));
				} else if (entryName.equals("tag.json")) {
					builder.tag(MAPPER.readValue(json, tagListType));
				} else if (entryName.equals("model.json")) {
					builder.model(MAPPER.readValue(json, modelListType));
				} else if (entryName.equals("prediction.json")) {
					builder.prediction(MAPPER.readValue(json, predictionListType));
				} else if (entryName.equals("workflow.json")) {
					builder.workflow(MAPPER.readValue(json, workflowListType));
				} else if (entryName.equals("fingerprint.json")) {
					builder.fingerprint(MAPPER.readValue(json, fingerprintListType));
				} else if (entryName.equals("fingerprintset.json")) {
					builder.fingerprintset(MAPPER.readValue(json, fingerprintsetListType));
				}
			}

			List<Path> files = new ArrayList<>(ca.getNumEntriesWithFormat(BIN_URI));
			for (ArchiveEntry entry : ca.getEntriesWithFormat(BIN_URI)) {
				Path tempFile = Files.createTempFile("modeldata", null);
				entry.extractFile(tempFile.toFile());
				files.add(tempFile);
			}
			builder.files(files);
		}

		return builder.build();
	}

	public static void write(Archive archive, Path file)
			throws IOException, JDOMException, ParseException, CombineArchiveException, TransformerException {

		try (CombineArchive ca = new CombineArchive(file.toFile())) {
			writeEntry(archive.getSOP(), ca, "sop.json");
			writeEntry(archive.getProduct(), ca, "product.json");
			writeEntry(archive.getMetadata(), ca, "metadata.json");
			writeEntry(archive.getTag(), ca, "tag.json");
			writeEntry(archive.getModel(), ca, "model.json");
			writeEntry(archive.getPrediction(), ca, "prediction.json");
			writeEntry(archive.getWorkflow(), ca, "workflow.json");
			writeEntry(archive.getFingerprint(), ca, "fingerprint.json");
			writeEntry(archive.getFingerprintSet(), ca, "fingerprintset.json");

			for (Path it : archive.getFile()) {
				ca.addEntry(it.toFile(), "files/" + it.getFileName(), BIN_URI);
			}

			ca.pack();
		}
	}

	private static void writeEntry(List<? extends FaModel> models, CombineArchive archive, String entryName)
			throws IOException {

		// Create temporary JSON file
		File tempFile = File.createTempFile("data", ".json");
		MAPPER.writeValue(tempFile, models);

		// Add JSON file
		archive.addEntry(tempFile, entryName, JSON_URI);

		// Delete temporary file
		tempFile.delete();
	}
}
