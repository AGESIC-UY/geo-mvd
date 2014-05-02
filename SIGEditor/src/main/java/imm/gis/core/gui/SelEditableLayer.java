package imm.gis.core.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

public class SelEditableLayer extends JDialog {

	private static final long serialVersionUID = 1L;

	private String value = null;
	private JList list;
	private ActionListener closeListener;
	
	public SelEditableLayer(JFrame ppal, String l[]) {
		super(ppal, "Elija la capa a editar", true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		list = new JList(l);
		list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		init();
		pack();
		GuiUtils.centerWindow(ppal, this);
	}

	public void setCloseListener(ActionListener cl){
		closeListener = cl;
	}
	
	public void choose(){
		value = null;
		
//		list.setSelectedValue(actual, true);
		setVisible(true);
	}
	
	private void init(){
		JPanel res = new JPanel(new java.awt.BorderLayout());
		JButton but ;
		
		list.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
			
				if (e.getClickCount() == 2){
					value = (String)list.getSelectedValue();
					closeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
				}
			}
		});
		
		res.add(new JLabel("Elija la capa a editar"), java.awt.BorderLayout.NORTH);
		res.add(new JScrollPane(list), java.awt.BorderLayout.CENTER);
		
		JPanel tmp = new JPanel(new java.awt.FlowLayout());
		but = new JButton("Ok");
		but.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				value = (String)list.getSelectedValue();
					if(value==null){
						javax.swing.JOptionPane.showMessageDialog(SelEditableLayer.this, "Debe seleccionar una capa", "Error al editar",
								javax.swing.JOptionPane.ERROR_MESSAGE);
						return;
					}
					closeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
				
				
			}
		});
		tmp.add(but);
		but = new JButton("Cancelar");
		but.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CANCEL"));
			}
		});
		tmp.add(but);
		res.add(tmp, java.awt.BorderLayout.SOUTH);
		
		getContentPane().add(res);
	}
	
	protected JRootPane createRootPane() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();

		rootPane.registerKeyboardAction(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						closeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
					}
				}, 
				stroke,
				JComponent.WHEN_IN_FOCUSED_WINDOW);

		return rootPane;
	}
	
	public String getValue(){
		return value;
	}

	public JList getList(){
		return list;
	}
}
