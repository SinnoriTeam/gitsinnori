package kr.pe.codda.gui.helper.iobuilder.table;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;



@SuppressWarnings("serial")
public class BuildFunctionCellRenderer extends JPanel implements TableCellRenderer {	
	public BuildFunctionCellRenderer() {
		setOpaque(true);
		init();
	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.CENTER));
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {		
		BuildFunctionCellValue sourceFileCellValue = (BuildFunctionCellValue)value;
		
		if (isSelected) {
			sourceFileCellValue.setForeground(table.getSelectionForeground());
			sourceFileCellValue.setBackground(table.getSelectionBackground());
		} else {
			sourceFileCellValue.setForeground(table.getForeground());
			sourceFileCellValue.setBackground(table.getBackground());
		}
		
		return sourceFileCellValue;
	}
	

}

