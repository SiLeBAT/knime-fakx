package org.foodauthent.fakx;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.foodauthent.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

    public static Archive read(InputStream stream) throws IOException {

        ZipInputStream zipStream = new ZipInputStream(stream);
        Archive.Builder builder = new Archive.Builder();

        List<Path> files = new ArrayList<>();

        ZipEntry entry;
        while ((entry = zipStream.getNextEntry()) != null) {
            // process ZipEntry
            String entryName = entry.getName();

            if (entryName.equals("sop.json")) {
                builder.sop(MAPPER.readValue(zipStream, sopListType));
            } else if (entryName.equals("product.json")) {
                builder.product(MAPPER.readValue(zipStream, productListType));
            } else if (entryName.equals("metadata.json")) {
                builder.metadata(MAPPER.readValue(zipStream, metadataListType));
            } else if (entryName.equals("tag.json")) {
                builder.tag(MAPPER.readValue(zipStream, tagListType));
            } else if (entryName.equals("model.json")) {
                builder.model(MAPPER.readValue(zipStream, modelListType));
            } else if (entryName.equals("prediction.json")) {
                builder.prediction(MAPPER.readValue(zipStream, predictionListType));
            } else if (entryName.equals("workflow.json")) {
                builder.workflow(MAPPER.readValue(zipStream, workflowListType));
            } else if (entryName.equals("fingerprint.json")) {
                builder.fingerprint(MAPPER.readValue(zipStream, fingerprintListType));
            } else if (entryName.equals("fingerprintset.json")) {
                builder.fingerprintset(MAPPER.readValue(zipStream, fingerprintsetListType));
            } else if (entryName.startsWith("files/")) {
                Path tempFile = Files.createTempFile(null, null);
                Files.copy(zipStream, tempFile);
                files.add(tempFile);
            }
        }
        builder.files(files);

        return builder.build();
    }

    public static void write(Archive archive, Path file) throws IOException {

        try (OutputStream stream = Files.newOutputStream(file);
             ZipOutputStream zipStream = new ZipOutputStream(stream)) {

            writeEntry(archive.getSOP(), zipStream, "sop.json");
            writeEntry(archive.getProduct(), zipStream, "product.json");
            writeEntry(archive.getMetadata(), zipStream, "metadata.json");
            writeEntry(archive.getTag(), zipStream, "tag.json");
            writeEntry(archive.getModel(), zipStream, "model.json");
            writeEntry(archive.getPrediction(), zipStream, "prediction.json");
            writeEntry(archive.getWorkflow(), zipStream, "workflow.json");
            writeEntry(archive.getFingerprint(), zipStream, "fingerprint.json");
            writeEntry(archive.getFingerprintSet(), zipStream, "fingerprintset.json");

            for (Path it : archive.getFile()) {
                String entryName = "files/" + it.getFileName();
                zipStream.putNextEntry(new ZipEntry(entryName));
                Files.copy(it, zipStream);
                zipStream.closeEntry();
            }
        }
    }

    private static void writeEntry(List<? extends FaModel> models, ZipOutputStream stream, String entryName) throws IOException {
        stream.putNextEntry(new ZipEntry(entryName));
        MAPPER.writeValue(stream, models);
        stream.closeEntry();
    }
}
