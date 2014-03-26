package imm.gis.core.gui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class UserChooser extends JDialog {
	private static final long serialVersionUID = 1L;
	private JList lstOptions;
	private JButton butOk;
	private JButton butCancel;
	private Object[] option = null;
	
	public UserChooser(String title){
		this(title, null);
	}
	
	public UserChooser(String title, Frame owner){
		super(owner, true);
		setTitle(title);
		setLocationRelativeTo(owner);
		lstOptions = new JList();
		butOk = new JButton("Ok");
		butCancel = new JButton("Cancel");
		
		butOk.setEnabled(false);
		createGUI();
		setListeners();
	}
	
	private void createGUI(){
		add(new JScrollPane(lstOptions), java.awt.BorderLayout.CENTER);
		JPanel south = new JPanel();
		south.add(butOk);
		south.add(butCancel);
		add(south, java.awt.BorderLayout.SOUTH);
		pack();
	}
	
	private void setListeners(){
		butOk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				option = lstOptions.getSelectedValues();
				setVisible(false);
			}
		});

		butCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				option = null;
				setVisible(false);
			}
		});
		
		lstOptions.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 2){
					option = lstOptions.getSelectedValues();
					if (option != null) setVisible(false);					
				} else {
					butOk.setEnabled(lstOptions.getSelectedIndex() >= 0);
				}
			}
		});
	}
	
	public void choose(Object values[]){
		choose(values, -1, -1);
	}

	public void choose(Object values[], int sizeX, int sizeY){
		lstOptions.setListData(values);
		if (sizeX >= 0 && sizeY >= 0){
			setSize(sizeX, sizeY);
		} else {
			pack();			
		}
		
		setVisible(true);
	}

	public Object[] getSelectedOptions(){
		return option;
	}
	
	@Override
	public Dimension getMinimumSize(){
		return new Dimension(100, 100);
	}
}
