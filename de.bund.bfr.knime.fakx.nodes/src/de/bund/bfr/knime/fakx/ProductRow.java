package de.bund.bfr.knime.fakx;

import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import org.foodauthent.model.Product;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.StringCell;

/**
 * Row for FoodAuthent Product.
 * 
 * Has the columns:
 * <ul>
 * <li>fa-id: UUID
 * <li>gtin: String
 * <li>brand: String
 * <li>gpcAttributes
 * </ul>
 * 
 * @author Miguel de Alba
 */
public class ProductRow implements DataRow {

	private DataCell[] cell;
	private final RowKey rowKey;
	
	public ProductRow(Product product) {

		cell = new DataCell[4];
		cell[0] = new StringCell(product.getFaId().toString());
		cell[1] = new StringCell(product.getGtin());
		cell[2] = new StringCell(product.getBrand());
		// TODO: GPCAttributes should rather be encoded in JSON array
		cell[3] = new StringCell(product.getGpcBricks().toString());
		
		rowKey = new RowKey(String.valueOf(new Random().nextInt()));
	}
	
	// Convenience getters
	public UUID getFaId() {
		return UUID.fromString(((StringCell)cell[0]).getStringValue());
	}
	
	public String getGtin() {
		return ((StringCell)cell[1]).getStringValue();
	}
	
	public String getBrand() {
		return ((StringCell)cell[2]).getStringValue();
	}
	
	// TODO: get GPCAttribute

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
		specs[1] = new DataColumnSpecCreator("gtin", StringCell.TYPE).createSpec();
		specs[2] = new DataColumnSpecCreator("brand", StringCell.TYPE).createSpec();
		specs[3] = new DataColumnSpecCreator("gpc", StringCell.TYPE).createSpec();
		
		return new DataTableSpec(specs);
	}
}
