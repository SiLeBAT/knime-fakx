package de.bund.bfr.knime.fakx;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.foodauthent.model.FileMetadata;
import org.foodauthent.model.WorkflowParameter.TypeEnum;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.json.JSONCell;
import org.knime.core.data.json.JSONCellFactory;

/**
 * Row for FoodAuthent FileMetadata.
 * 
 * Has the rows:
 * <ul>
 * <li>fa-id: UUID
 * <li>type: org.foodauthent.model.FileMetadata.TypeEnum
 * <li>name: String
 * <li>contentType: String
 * <li>uploadName: String
 * <li>description: String
 * <li>author: String
 * <li>date: LocalDate
 * <li>uploadDate: LocalDate
 * <li>version: Integer
 * <li>additionalProperties: Map<String, String>
 * </ul>
 * 
 * @author Miguel de Alba
 */
public class MetadataRow implements DataRow {

	private DataCell[] cell;
	private final RowKey rowKey;

	public MetadataRow(FileMetadata metadata) {

		LocalDate date = metadata.getDate();
		LocalDate uploadDate = metadata.getDate();

		cell = new DataCell[11];
		cell[0] = new StringCell(metadata.getFaId().toString());
		cell[1] = new StringCell(metadata.getType().toString());
		cell[2] = new StringCell(metadata.getName());
		cell[3] = new StringCell(metadata.getContentType());
		cell[4] = new StringCell(metadata.getUploadName());
		cell[5] = new StringCell(metadata.getDescription());
		cell[6] = new StringCell(metadata.getAuthor());
		cell[7] = new DateAndTimeCell(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
		cell[8] = new DateAndTimeCell(uploadDate.getYear(), uploadDate.getMonthValue(), uploadDate.getDayOfMonth());
		cell[9] = new IntCell(metadata.getVersion());
		// TODO: Convert additionalProperties to JSON and store in JSONCell
		cell[10] = JSONCellFactory.create((javax.json.JsonValue) null);

		rowKey = new RowKey(String.valueOf(new Random().nextInt()));
	}

	// Convenience getters
	public UUID getFaId() {
		return UUID.fromString(((StringCell) cell[0]).getStringValue());
	}

	public TypeEnum getType() {
		return TypeEnum.valueOf(((StringCell) cell[1]).getStringValue());
	}

	public String getName() {
		return ((StringCell) cell[2]).getStringValue();
	}

	public String getContentType() {
		return ((StringCell) cell[3]).getStringValue();
	}

	public String getUploadName() {
		return ((StringCell) cell[4]).getStringValue();
	}

	public String getDescription() {
		return ((StringCell) cell[5]).getStringValue();
	}

	public String getAuthor() {
		return ((StringCell) cell[6]).getStringValue();
	}

	public LocalDate getDate() {
		DateAndTimeCell dateCell = ((DateAndTimeCell) cell[7]);
		return LocalDate.of(dateCell.getYear(), dateCell.getMonth(), dateCell.getDayOfMonth());
	}

	public LocalDate getUploadDate() {
		DateAndTimeCell dateCell = ((DateAndTimeCell) cell[8]);
		return LocalDate.of(dateCell.getYear(), dateCell.getMonth(), dateCell.getDayOfMonth());
	}

	// TODO: additionalProperties
	public Map<String, String> getAdditionalProperties() {
		return new HashMap<>();
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
		
		DataColumnSpec[] specs = new DataColumnSpec[11];
		specs[0] = new DataColumnSpecCreator("fa-id", StringCell.TYPE).createSpec();
		specs[1] = new DataColumnSpecCreator("type", StringCell.TYPE).createSpec();
		specs[2] = new DataColumnSpecCreator("name", StringCell.TYPE).createSpec();
		specs[3] = new DataColumnSpecCreator("contentType", StringCell.TYPE).createSpec();
		specs[4] = new DataColumnSpecCreator("uploadName", StringCell.TYPE).createSpec();
		specs[5] = new DataColumnSpecCreator("description", StringCell.TYPE).createSpec();
		specs[6] = new DataColumnSpecCreator("author", StringCell.TYPE).createSpec();
		specs[7] = new DataColumnSpecCreator("date", DateAndTimeCell.TYPE).createSpec();
		specs[8] = new DataColumnSpecCreator("uploadDate", DateAndTimeCell.TYPE).createSpec();
		specs[9] = new DataColumnSpecCreator("version", IntCell.TYPE).createSpec();
		specs[10] = new DataColumnSpecCreator("additionalProperties", JSONCell.TYPE).createSpec();
		
		return new DataTableSpec(specs);
	}
}
