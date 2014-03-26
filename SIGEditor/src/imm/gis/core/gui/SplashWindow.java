package imm.gis.core.gui;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;

public class SplashWindow extends JWindow implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Timer timer = new Timer(200, this);

	private ImageIcon icon = GuiUtils.loadIcon("splash.jpg");

	private ImageIcon image1 = GuiUtils.loadIcon("globe1.gif");

	private ImageIcon image2 = GuiUtils.loadIcon("globe2.gif");

	private ImageIcon image3 = GuiUtils.loadIcon("globe3.gif");

	private JLabel lblText = new JLabel();

	private JLabel lblImage = new JLabel();

	private int nro = 0;

	static private SplashWindow instance;

	static public SplashWindow getInstance() {
		if (instance == null) {
			instance = new SplashWindow();
		}

		return instance;
	}

	private SplashWindow() {
		super();
		//setAlwaysOnTop(true);

		getContentPane().setLayout(new GridBagLayout());
		timer.setRepeats(true);
		Font font = new Font("Arial", Font.PLAIN, 10);

		if (font != null)
			lblText.setFont(font);
		createGui(getContentPane());
//		setSize(240, 265);
		pack();
		GuiUtils.centerWindowOnScreen(this);
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				close();
			}
		});
		setVisible(true);
		timer.start();

	}

	private void createGui(Container cnt) {
		GUIConstraints cons = new GUIConstraints();
		GuiUtils.ConstraintGroup cg = new GuiUtils.ConstraintGroup(cons.gMain,
				cons.wMain);

		cnt.add(new JLabel(icon), cg.getConstraints(0));
		cnt.add(lblText, cg.getConstraints(1));
		cnt.add(lblImage, cg.getConstraints(2));
	}

	public void actionPerformed(ActionEvent e) {
		nro = (nro + 1) % 3;

		switch (nro) {
		case 0:
			lblImage.setIcon(image1);
			break;
		case 1:
			lblImage.setIcon(image2);
			break;
		case 2:
			lblImage.setIcon(image3);
			break;
		default:
			break;
		}

		// repaint();
	}

	public void close() {
		if (timer.isRunning())
			timer.stop();
		setVisible(false);
		dispose();
	}

	public void stopRunIndicator() {
		timer.stop();
		timer.setDelay(3000);
		timer.setInitialDelay(3000);
		timer.setRepeats(false);
		timer.removeActionListener(this);
		lblImage.setIcon(null);
		lblText.setText("Listo");
		timer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		timer.start();
	}

	public void setText(String t) {
		lblText.setText(t);
	}

	private final class GUIConstraints {
		private final int NONE = GridBagConstraints.NONE;

		private final int BOTH = GridBagConstraints.BOTH;

		// private final int SOUT = GridBagConstraints.SOUTH;
		private final int CENT = GridBagConstraints.CENTER;

		// private final int EAST = GridBagConstraints.EAST;
		// private final int WEST = GridBagConstraints.WEST;
		// private final int SOEA = GridBagConstraints.SOUTHEAST;
		// private final int HORI = GridBagConstraints.HORIZONTAL;
		private final int RELA = GridBagConstraints.RELATIVE;

		private final int REMA = GridBagConstraints.REMAINDER;

		// {gridx, gridy}, {gridwidth, gridheight}, {fill, anchor}, {top, left,
		// bottom, right}(insets)
		final int[][] gMain = new int[][] { { 0, 0 }, { REMA, RELA },
				{ BOTH, CENT }, { 10, 10, 2, 10 }, { 0, 1 }, { 1, REMA },
				{ NONE, CENT }, { 2, 15, 10, 2 }, { 1, 1 }, { REMA, REMA },
				{ NONE, CENT }, { 2, 2, 10, 10 } };

		// {weightx, weighty}
		final double[][] wMain = new double[][] { { 1.0, 1.0 }, { 0.0, 0.0 },
				{ 0.0, 0.0 } };
	}



	public static void main(String[] args) {
		SplashWindow splash = SplashWindow.getInstance();
		splash.setText("Cargando configuracion XML...");
		try {
			Thread.sleep(30000);
			splash.stopRunIndicator();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
