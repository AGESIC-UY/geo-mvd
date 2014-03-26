package imm.gis.core.model.undo;

import imm.gis.core.controller.ContUndo;

public class UndoModel implements UndoListener {
	private UndoableOperation operations[];
	private int pos;
	private int cant;
	private ContUndo contUndo;
	
	public UndoModel(ContUndo c, int l){
		contUndo = c;
		operations = new UndoableOperation[l];
		pos = -1;
		cant = 0;
	}

	public void removeOperations(){
		pos = -1;
		cant = 0;
		contUndo.modelChanged();
	}
	
	public boolean isEmpty(){
		return cant == 0;
	}
	
	public UndoableOperation getLastOperation(){
		UndoableOperation res = null;
		
		if (!isEmpty()){
			res = operations[pos];
			operations[pos--] = null;
			
			if (pos<0)
				pos += operations.length;

			cant--;
		}
		
		return res;
	}

	public void changedPerformed(UndoableOperation fo) {
		
		if (fo == null)
			return;
		
		pos = ++pos % operations.length;
		
		if (cant==operations.length)
			operations[pos] = null;
		else
			cant++;
		
		operations[pos] = fo;

		contUndo.modelChanged();
	}

	
}
