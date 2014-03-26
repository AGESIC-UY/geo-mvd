package imm.gis.test;

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class TestClick extends JFrame{
	private static final long serialVersionUID = 5210472683063479763L;
	private JTextField text = new JTextField();
	
	public TestClick(){
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		text.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				System.out.println(e.getClickCount());
			}
		});
		add(text);
		System.out.println(text.getSize());
		pack();
		System.out.println(text.getSize());
		fullScreen(this);
		System.out.println(text.getSize());
		if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH)){
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		System.out.println(text.getSize());
		setVisible(true);
		System.out.println(text.getSize());
	}
	
	public static void fullScreen(Window win){
		java.awt.Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		win.setLocation(0, 0);
		win.setSize((int)Math.round(dim.getWidth()), (int)Math.round(dim.getHeight()));
		win.validate();
	}

	public static void main(String[] args) {
		new TestClick();
	}

}
