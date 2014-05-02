package imm.gis.form;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

public class FeatureTableModelListener implements TableModelListener {
	private static Logger log = Logger.getLogger(FeatureTableModelListener.class);
//	private EditableFeatureTableModel model;

	public void tableChanged(TableModelEvent e) {
//		model = (EditableFeatureTableModel)e.getSource();
//		int row = e.getFirstRow();
//		int col = e.getColumn();
		
		switch (e.getType()){
		case TableModelEvent.DELETE:
			break;
		case TableModelEvent.INSERT:
			break;
		case TableModelEvent.UPDATE:
//			log.info("Se actualizo celda " + row + "," + col);
//			model.setValueAt(model.getValueAt(row, col), row, col);
			break;
		default:
			log.info("Evento no manejado en el TableModel: " + e.getType());
			break;
		}
	}
}
