package de.bund.bfr.knime.fakx;

import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import org.foodauthent.model.Tag;
import org.foodauthent.model.Tag.TypeEnum;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.StringCell;

/**
 * Row for FoodAuthent Tag.
 * 
 * Has the columns:
 * <ul>
 * <li>fa-id: UUID
 * <li>name: String
 * <li>description: String
 * <li>type: org.foodauthent.model.Tag.TypeEnum
 * </ul>
 * 
 * @author Miguel de Alba
 */
public class TagRow implements DataRow {

	private DataCell[] cell;
	private final RowKey rowKey;
	
	public TagRow(Tag tag) {
		
		cell = new DataCell[4];
		cell[0] = new StringCell(tag.getFaId().toString());
		cell[1] = new StringCell(tag.getName());
		cell[2] = new StringCell(tag.getDescription());
		cell[3] = new StringCell(tag.getType().toString());
		
		rowKey = new RowKey(String.valueOf(new Random().nextInt()));
	}
	
	// Convenience getters
	public UUID getFaId() {
		return UUID.fromString(((StringCell)cell[0]).getStringValue());
	}
	
	public String getName() {
		return ((StringCell)cell[1]).getStringValue();
	}
	
	public String getDescription() {
		return ((StringCell)cell[2]).getStringValue();
	}
	
	public TypeEnum getType() {
		return TypeEnum.valueOf(((StringCell)cell[3]).getStringValue());
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
		
		DataColumnSpec[] specs = new DataColumnSpec[4];
		specs[0] = new DataColumnSpecCreator("fa-id", StringCell.TYPE).createSpec();
		specs[1] = new DataColumnSpecCreator("name", StringCell.TYPE).createSpec();
		specs[2] = new DataColumnSpecCreator("description", StringCell.TYPE).createSpec();
		specs[3] = new DataColumnSpecCreator("type", StringCell.TYPE).createSpec();
		
		return new DataTableSpec(specs);
	}
}
