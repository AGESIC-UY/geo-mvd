/*
 * DecoratedIcon.java
 *
 * Created on 25 February 2003, 20:47
 */

package imm.gis.gui.toolbar.toolbutton;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * The base class for any decorated icon. The icon to decorate is supplied
 * as the constructor and the decoration defined as a pattern buffer in
 * the concrete class.
 *
 * @author Bob Tarling
 */
abstract public class DecoratedIcon extends ImageIcon {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int ROLLOVER = 0;
    public static final int STANDARD = 1;

    // Sprite buffer for the arrow image of the left button
    protected int[][] _buffer;

    protected int _popupIconWidth = 11;
    protected int _popupIconHeight = 16;
    private int _popupIconOffset = 5;

    private ImageIcon _imageIcon;
    
    /** Construct an dropdown icon pointing in the given direction
     * @param direction the direction the arrow will point, this being one of the constants NORTH, SOUTH, EAST, WEST
     */        
    DecoratedIcon(ImageIcon imageIcon) {
        _imageIcon = imageIcon;
    }
    
    protected void init(int[][] buffer) {
        _buffer = buffer;
        _popupIconWidth = _buffer[0].length;
        _popupIconHeight = _buffer.length;
        int newWidth = _imageIcon.getIconWidth() + _popupIconOffset + _popupIconWidth;
        int newHeight = _imageIcon.getIconHeight();
        BufferedImage mergedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = mergedImage.createGraphics();
        g2.drawImage(_imageIcon.getImage(), null, null);
        setImage(mergedImage);
    }

    /** Paints the icon. The top-left corner of the icon is drawn at the point
     * (x, y) in the coordinate space of the graphics context g. If this icon has
     * no image observer, this method uses the c component as the observer.
     *
     * @param c the component to be used as the observer if this icon has no image observer
     * @param g the graphics context
     * @param x the X coordinate of the icon's top-left corner
     * @param y the Y coordinate of the icon's top-left corner
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        super.paintIcon(c, g, x, y);

        int xOffset = x + _imageIcon.getIconWidth() + _popupIconOffset;
        Color[] colors = {
	    c.getBackground(),
	    UIManager.getColor("controlDkShadow"),
	    UIManager.getColor("controlText"),
	    UIManager.getColor("controlHighlight")};

        for (int i=0; i < _popupIconWidth; i++) {
            for (int j=0; j < _popupIconHeight; j++) {
                if (_buffer[j][i] != 0) {
                    g.setColor(colors[_buffer[j][i]]);
                    g.drawLine(xOffset + i, y + j, xOffset + i, y + j);
                }
            }
        }
    }
}
