package imm.gis.core.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import imm.gis.core.gui.worker.BlockingSwingWorker;
import imm.gis.core.interfaces.IMap;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ScalePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private IMap mapa;
	private JFrame frame;
	JComboBox scaleComboBox;
	
	public ScalePanel(IMap mapa, JFrame frame) {
		super(new FlowLayout());
		this.mapa = mapa;
		this.frame = frame;
		
//		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel scaleLabel = new JLabel("Escala 1 : ");
		scaleComboBox = new JComboBox(new String[] {"1000","2500","5000","10000","15000","20000","25000","50000"
				,"100000","150000"});
		scaleComboBox.setEditable(true);		
		add(scaleLabel);
		add(scaleComboBox);
		
		scaleComboBox.addItemListener(new ConfirmationListener());
		scaleComboBox.setMaximumSize(new Dimension(100,20));
	}
	
	private class ConfirmationListener implements ItemListener {
		
		public void itemStateChanged(ItemEvent e) {
			
			if (e.getStateChange() == ItemEvent.SELECTED) {
				
				
				try {
					final long newScale = Long.parseLong(scaleComboBox.getEditor().getItem().toString());
					
					if (newScale > 0) {
						new BlockingSwingWorker(frame) {
				            protected void doNonUILogic() throws RuntimeException {
								mapa.setScale(newScale);
				            }
			        	}.start();
					}
					else
						JOptionPane.showMessageDialog(null,"La escala introducida no es valida","",JOptionPane.WARNING_MESSAGE);					
				}
				catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null,"La escala introducida no es valida","",JOptionPane.WARNING_MESSAGE);					
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		scaleComboBox.getEditor().setItem(new Long(mapa.getScale()).toString());
	}
}
