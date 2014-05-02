package imm.gis.core.gui;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;

public class FeatureUserChooser extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTable lstOptions;
	private JButton butOk;
	private JButton butCancel;
	private Feature option = null;
	private FeatureCollection fc;
	
	public FeatureUserChooser(){
		this(null);
	}
	
	public FeatureUserChooser(Dialog owner){
		super(owner, true);
		setLocationRelativeTo(owner);
		lstOptions = new JTable(new FeatureTableModel());
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
				option = getSelectedFeature();
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
					option = getSelectedFeature();
					if (option != null) setVisible(false);					
				} else {
					butOk.setEnabled(lstOptions.getSelectedRow() >= 0);
				}
			}
		});
	}
	
	public void choose(FeatureCollection fc){
		choose(fc, null);
	}
	
	public void choose(FeatureCollection fc, String[] columns){
		this.fc = fc;
		((FeatureTableModel)lstOptions.getModel()).setFeatureCollection(fc, columns);
		pack();
		setVisible(true);
	}

	public Feature getSelectedOption(){
		return option;
	}
	
	private Feature getSelectedFeature(){
		int row = lstOptions.getSelectedRow();
		Feature f = null;
		int i = 0;
		
		for (FeatureIterator it = fc.features(); it.hasNext() && i <= row; i++){
			f = it.next();
		}
		
		return f;
	}
}
