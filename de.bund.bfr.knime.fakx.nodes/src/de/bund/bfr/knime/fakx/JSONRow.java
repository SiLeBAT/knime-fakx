package de.bund.bfr.knime.fakx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.json.JsonValue;

import org.foodauthent.fakx.Archive;
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
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.json.JSONCell;
import org.knime.core.data.json.JSONCellFactory;
import org.knime.json.util.JSONUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Row for FoodAuthent data.
 * 
 * Columns:
 * <ul>
 * <li>Product
 * <li>SOP
 * <li>Metadata
 * <li>Tag
 * <li>Model
 * <li>Prediction
 * <li>Workflow
 * <li>Fingerprint
 * <li>FingerprintSet
 * </ul>
 * 
 * @author Miguel de Alba
 */
public class JSONRow implements DataRow {

	private DataCell[] cell;
	private final RowKey rowKey;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public JSONRow(Archive archive) {
		
		cell = new DataCell[9];
		cell[0] = createJSONCell(archive.getProduct());
		cell[1] = createJSONCell(archive.getSOP());
		cell[2] = createJSONCell(archive.getMetadata());
		cell[3] = createJSONCell(archive.getTag());
		cell[4] = createJSONCell(archive.getModel());
		cell[5] = createJSONCell(archive.getPrediction());
		cell[6] = createJSONCell(archive.getWorkflow());
		cell[7] = createJSONCell(archive.getFingerprint());
		cell[8] = createJSONCell(archive.getFingerprintSet());
		
		rowKey = new RowKey(String.valueOf(new Random().nextInt()));
	}
	
	public JSONRow(DataRow row) {
		cell = new DataCell[9];
		for (int i = 0; i < 9; i++) {
			cell[i] = row.getCell(i);
		}
		rowKey = row.getKey();
	}
	
	// Convenience getters
	public List<Product> getProducts() {
		return getList(0, new TypeReference<List<Product>>() {});
	}
	
	public List<SOP> getSOP() {
		return getList(1, new TypeReference<List<SOP>>() {});
	}
	
	public List<FileMetadata> getMetadata() {
		return getList(2, new TypeReference<List<FileMetadata>>() {});
	}
	
	public List<Tag> getTag() {
		return getList(3, new TypeReference<List<Tag>>() {});
	}

	public List<Model> getModel() {
		return getList(4, new TypeReference<List<Model>>() {});
	}

	public List<Prediction> getPrediction() {
		return getList(5, new TypeReference<List<Prediction>>() {});
	}

	public List<Workflow> getWorkflow() {
		return getList(6, new TypeReference<List<Workflow>>() {});
	}

	public List<Fingerprint> getFingerprint() {
		return getList(7, new TypeReference<List<Fingerprint>>() {});
	}

	public List<FingerprintSet> getFingerprintSet() {
		return getList(8, new TypeReference<List<FingerprintSet>>() {});
	}
	
	private <T extends FaModel> List<T> getList(int index, TypeReference<?> typeReference) {
		String string = ((JSONCell)cell[index]).getStringValue();
		
		try {
			return MAPPER.readValue(string, typeReference);
		} catch (Exception exception) {
			return new ArrayList<>();
		}
	}
	
	// DataRow methods
	@Override
	public Iterator<DataCell> iterator() {
		return new RowIterator(this);
	}
	
	@Override
	public int getNumCells() {
		return cell.length;
	}
	
	@Override
	public RowKey getKey() {
		return rowKey;
	}
	
	@Override
	public DataCell getCell(int index) {
		return cell[index];
	}
	
	public static DataTableSpec createSpec() {
		
		DataColumnSpec[] specs = new DataColumnSpec[9];
		specs[0] = new DataColumnSpecCreator("product", JSONCell.TYPE).createSpec();
		specs[1] = new DataColumnSpecCreator("sop", JSONCell.TYPE).createSpec();
		specs[2] = new DataColumnSpecCreator("metadata", JSONCell.TYPE).createSpec();
		specs[3] = new DataColumnSpecCreator("tag", JSONCell.TYPE).createSpec();
		specs[4] = new DataColumnSpecCreator("model", JSONCell.TYPE).createSpec();
		specs[5] = new DataColumnSpecCreator("prediction", JSONCell.TYPE).createSpec();
		specs[6] = new DataColumnSpecCreator("workflow", JSONCell.TYPE).createSpec();
		specs[7] = new DataColumnSpecCreator("fingerprint", JSONCell.TYPE).createSpec();
		specs[8] = new DataColumnSpecCreator("fingerprintSet", JSONCell.TYPE).createSpec();
		
		return new DataTableSpec(specs);
	}
	
	private static DataCell createJSONCell(final Object object) {
		JsonValue jsonValue = JsonValue.NULL;

		if (object != null) {
			try {
				final String jsonStr = MAPPER.writeValueAsString(object);
				jsonValue = JSONUtil.parseJSONValue(jsonStr);
			} catch (IOException e) {
			}
		}

		return JSONCellFactory.create(jsonValue);
	}
}
