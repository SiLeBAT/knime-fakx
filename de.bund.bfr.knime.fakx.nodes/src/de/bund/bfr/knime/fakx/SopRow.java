package de.bund.bfr.knime.fakx;

import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import org.foodauthent.model.SOP;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.StringCell;

/**
 * Row for FoodAuthent SOP.
 * 
 * Has the columns:
 * <ul>
 * <li>fa-id: UUID
 * <li>file-id: UUID
 * <li>name: String
 * <li>description: String
 * <li>product: String
 * </ul>
 * 
 * @author Miguel de Alba
 */
public class SopRow implements DataRow {
	
	private DataCell[] cell;
	private final RowKey rowKey;

	public SopRow(SOP sop) {
		
		cell = new DataCell[5];
		cell[0] = new StringCell(sop.getFaId().toString());
		cell[1] = new StringCell(sop.getFileId().toString());
		cell[2] = new StringCell(sop.getName());
		cell[3] = new StringCell(sop.getDescription());
		cell[4] = new StringCell(sop.getProductId().toString());

		rowKey = new RowKey(String.valueOf(new Random().nextInt()));
	}
	
	// Convenience getters
	public UUID getFaId() {
		return UUID.fromString(((StringCell)cell[0]).getStringValue());
	}
	
	public UUID getFileId() {
		return UUID.fromString(((StringCell)cell[1]).getStringValue());
	}
	
	public String getName() {
		return ((StringCell)cell[2]).getStringValue();
	}
	
	public String getDescription() {
		return ((StringCell)cell[3]).getStringValue();
	}
	
	public UUID getProductId() {
		return UUID.fromString(((StringCell)cell[4]).getStringValue());
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
}
