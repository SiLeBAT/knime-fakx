package de.bund.bfr.knime.fakx;

import java.util.Iterator;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;

public class RowIterator implements Iterator<DataCell> {
	private int i = 0;
	private DataRow row;
	
	public RowIterator(DataRow row) {
		this.row = row;
	}

	@Override
	public boolean hasNext() {
		return i < row.getNumCells();
	}

	@Override
	public DataCell next() {
		return row.getCell(i++);
	}
}