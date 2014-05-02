package imm.gis.core.controller;

import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;


public class ContKeyboardEvents {
	
	public ContKeyboardEvents(ContPpal contPpal){
		JComponent root = contPpal.getVistaPrincipal().getRootPane(); 
		InputMap in = root.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actions = root.getActionMap();
		setKeyboardListeners(
				contPpal.getVistaPrincipal().getEditionToolBar().getActions(), 
				contPpal.getVistaPrincipal().getGeneralToolBar().getActions(), 
				in, 
				actions);
		
		contPpal.getVistaPrincipal().getGeneralToolBar().getInputMap().setParent(in);
		contPpal.getVistaPrincipal().getGeneralToolBar().getActionMap().setParent(actions);
		
		contPpal.getVistaPrincipal().getEditionToolBar().getInputMap().setParent(in);
		contPpal.getVistaPrincipal().getEditionToolBar().getActionMap().setParent(actions);		
		
	}

	private void setKeyboardListeners(Map editActions, Map generalActions, InputMap in, ActionMap actions){
		KeyStroke keyStroke;
		String obj;

		obj = "DELETE";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) editActions.get("DELETE_FEATURE"));

		obj = "INSERT";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) editActions.get("ADD_FEATURE"));

		obj = "ESCAPE";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) editActions.get("CANCEL"));	

		obj = "SAVE";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) editActions.get("SAVE"));	

		obj = "UNDO";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) editActions.get("UNDO"));	

		
		obj = "LEFT";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) generalActions.get("MOVE_WEST"));
		
		obj = "RIGHT";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) generalActions.get("MOVE_EAST"));	

		obj = "UP";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) generalActions.get("MOVE_NORTH"));	

		obj = "DOWN";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) generalActions.get("MOVE_SOUTH"));	

		obj = "ZOOM_PREVIOUS";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) generalActions.get("PREVIOUS_ZOOM"));	

		obj = "INFO";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) generalActions.get("QUERY"));	

		obj = "BUSCAR";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) generalActions.get("SEARCH"));	

		obj = "MEDIR";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) generalActions.get("MEASURE"));	

		obj = "ACTUALIZAR";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) generalActions.get("REFRESH_MAP"));	

		obj = "CLONE";
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK);
		in.put(keyStroke, obj);
		actions.put(obj, (Action) editActions.get("CLONE_FEATURE"));	
	}
	
}
