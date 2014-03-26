/*
 *  DockBoundary.java
 *  2004-01-02
 */


package imm.gis.gui.toolbar.layouts;

import java.awt.*;
import javax.swing.*;
import java.util.*;


/**
 * Abstract Rectangle class for representing the bounds of the docked
 * toolbars at a given edge.
 * @author Christopher Bach
 */
public abstract class DockBoundary extends Rectangle
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<JToolBar>       ourToolBars = new ArrayList<JToolBar>();
    private Rectangle       ourDockableBoundary = null;
    private int             ourDockableMargin = 10;

    private int             ourDockEdge = DockLayout.NORTH;
    private int             ourOrientation = DockLayout.HORIZONTAL;
    private int             ourSpacing = 0;




    /**
     * Creates a DockBoundary for the specified edge.
     */
    public DockBoundary(int edge)
    {
        super(0, 0, 0, 0);
        ourDockEdge = edge;
        ourDockableBoundary = new Rectangle(0, 0, ourDockableMargin, ourDockableMargin);

        if (edge == DockLayout.NORTH || edge == DockLayout.SOUTH)
        {
                ourOrientation = DockLayout.HORIZONTAL;
        }

        else ourOrientation = DockLayout.VERTICAL;
    }



    /**
     * Creates a DockBoundary for the specified edge
     * with the specified spacing.
     */
    public DockBoundary(int edge, int spacing)
    {
        this(edge);
        setSpacing(spacing);
    }


















    /**
     * Sets the spacing between toolbars added to this edge.
     */
    public void setSpacing(int spacing)
    {
        if (spacing >= 0) ourSpacing = spacing;
        else ourSpacing = 0;
    }

    /**
     * Gets the spacing between toolbars added to this edge.
     */
    public int getSpacing()
    {
        return ourSpacing;
    }

    /**
     * Gets the orientation of this edge.
     */
    public int getOrientation()
    {
        return ourOrientation;
    }

    /**
     * Gets the edge that this boundary represents.
     */
    public int getEdge()
    {
        return ourDockEdge;
    }







    /**
     * Determines if the provided point is within the
     * dockable range for this edge.
     */
    public boolean isDockablePoint(Point p)
    {
        return ourDockableBoundary.contains(p);
    }


    /**
     * Determines if a drag operation on the provided toolbar
     * at the provided point may begin or continue.  The toolbar handler
     * calls this method when a drag event occurs on the toolbar
     * prior to displaying or positioning the dynamic dragging window.
     * Subclasses may override this method to return false in order to
     * internally process the drag gesture without the ToolBarHandler
     * having to undock and redock the toolbar.
     */
    public boolean isDraggablePoint(Point p, JToolBar toolbar)
    {
        return true;
    }



    /**
     * Subclass implementations should return the index at which
     * the toolbar should be added when dropped at this point.
     */
    public abstract int getDockIndex(Point p);

    /**
     * Subclass implementation should return the index at which
     * the specified toolbar is located within this boundary, or
     * -1 if the toolbar is not present.
     */
    public abstract int getDockIndex(JToolBar toolbar);

    /**
     * Subclass implementations should return the row index at which
     * the toolbar should be added when dropped at this point.
     */
    public abstract int getRowIndex(Point p);

    /**
     * Subclass implementation should return the row index at which
     * the specified toolbar is located within this boundary, or
     * -1 if the toolbar is not present.
     */
    public abstract int getRowIndex(JToolBar toolbar);








    /**
     * Sets the location and length of this boundary, and calculates
     * the bounds based on validating the toolbar layout.
     */
    public void setPosition(int newX, int newY, int length)
    {
        super.setLocation(newX, newY);
        if (ourOrientation == DockLayout.HORIZONTAL) width = length;
        else height = length;
        setDepth(0);
        validate();

        // Since the positions of the South and East dock boundaries are
        // given as the bottom and right edges of the container, we need
        // to shift their bounds back into the container...
        if (ourDockEdge == DockLayout.SOUTH)
        {
            // Make sure the subclass provides a positive depth...
            int depth = Math.abs(height);
            setBounds(newX, newY - depth, width, depth);
        }

        else if (ourDockEdge == DockLayout.EAST)
        {
            // Make sure the subclass provides a positive depth...
            int depth = Math.abs(width);
            setBounds(newX - depth, newY, depth, height);
        }

        validateDockableBoundary();
    }


    /**
     * Lays out the toolbars associated with this boundary and calculates
     * the depth of the boundary based on the toolbar layout.
     * Subclass implementations should lay out the associated toolbars
     * and determine the depth of this boundary.  At the start of this
     * mehtod, the depth of this boundary should be assumed to be 0 and
     * should be determined by the time the validation completes, finishing
     * with a call to setDepth().  This method usually will not be called
     * directly.  Instead, a call to setPosition() will call this method
     * and will also subsequently verify the bounds of this boundary based
     * on the new depth.
     */
    public abstract void validate();


    /**
     * Validates this boundary and recalculates its dockable range.
     */
    public void revalidate()
    {
        validate();
        validateDockableBoundary();
    }


    /**
     * Returns the height or width of this boundary depending on
     * orientation as determined by a previous call to validate.
     */
    public int getDepth()
    {
        if (ourOrientation == DockLayout.HORIZONTAL) return height;
        else return width;
    }



    /**
     * Sets the height or width of this boundary depending on orientation.
     * This method should be called by the subclass implementation's validate
     * method prior to returning.
     */
    protected void setDepth(int depth)
    {
        if (ourOrientation == DockLayout.HORIZONTAL) height = Math.abs(depth);
        else width = Math.abs(depth);
    }















    /**
     * Inserts the specified toolbar into this boundary at the
     * provided indices.
     * Subclasses generally should not override this method,
     * but may provide more detailed behavior by overriding
     * the toolBarAdded() callback method instead.
     */
    public void addToolBar(JToolBar toolbar, int rowIndex, int index)
    {
        toolbar.setOrientation(ourOrientation);
        ourToolBars.add(toolbar);
        toolBarAdded(toolbar, rowIndex, index);
    }


    /**
     * Adds the specified toolbar to this boundary calculating the
     * insertion index based on the provided drop point.
     * Subclasses generally should not override this method,
     * but may provide more detailed behavior by overriding
     * the toolBarAdded() callback method instead.
     */
    public void addToolBar(JToolBar toolbar, Point dropPoint)
    {
        addToolBar(toolbar, getRowIndex(dropPoint), getDockIndex(dropPoint));
    }


    /**
     * Callback method called when a toolbar is added to the boundary
     * in order to allow subclasses to provide additional handling.
     */
    protected void toolBarAdded(JToolBar toolbar, int rowIndex, int index)
    {

    }







    /**
     * Removes the specified toolbar from this boundary.
     * Subclasses generally should not override this method,
     * but may provide more detailed behavior by overriding
     * the toolBarRemoved() callback method instead.
     */
    public void removeToolBar(JToolBar toolbar)
    {
        ourToolBars.remove(toolbar);
        toolBarRemoved(toolbar);
    }


    /**
     * Removes the specified component from this boundary as long as
     * the runtime type of the component is a JToolBar.
     * Subclasses generally should not override this method,
     * but may provide more detailed behavior by overriding
     * the toolBarRemoved() callback method instead.
     */
    public void removeComponent(Component component)
    {
        if (component instanceof JToolBar)
                removeToolBar((JToolBar)component);
    }


    /**
     * Callback method called when a toolbar is removed from the boundary
     * in order to allow the subclass to provide additional handling.
     */
    protected void toolBarRemoved(JToolBar toolbar)
    {

    }








    /**
     * Gets an array of the toolbars registered with this boundary.
     */
    public JToolBar[] getToolBars()
    {
        JToolBar[] bars = new JToolBar[ourToolBars.size()];
        bars = (JToolBar[])ourToolBars.toArray(bars);
        return bars;
    }


    /**
     * Returns a boolean indicating whether or not this boundary
     * contains the provided toolbar.
     */
    public boolean containsToolBar(JToolBar toolbar)
    {
        return ourToolBars.contains(toolbar);
    }









    // Package access only...
    /**
     * Coordinates the actaul location of each toolbar (edge and indicies)
     * with the toolbar's handler.
     */
    void refreshHandlers()
    {
        String key = ToolBarHandler.TOOL_BAR_HANDLER_KEY;

        for (int i=0; i < ourToolBars.size(); i++)
        {
            JToolBar toolbar = (JToolBar)ourToolBars.get(i);
            Object prop = toolbar.getClientProperty(key);
            ToolBarHandler handler = (ToolBarHandler)prop;

            if (handler != null)
            {
                handler.setDockEdge(getEdge());
                handler.setDockIndex(getDockIndex(toolbar));
                handler.setRowIndex(getRowIndex(toolbar));
            }
        }

    }















    /**
     * Returns the "length" (width or height) of the provided toolbar
     * depending on this boundary's orientation.
     */
    protected int getPreferredToolBarLength(JToolBar toolbar)
    {
        Dimension d = toolbar.getPreferredSize();
        if (ourOrientation == DockLayout.HORIZONTAL) return d.width;
        else return d.height;
    }



    /**
     * Returns the "depth" (height or width) of the provided toolbar depending
     * on this boundary's orientation.
     */
    protected int getPreferredToolBarDepth(JToolBar toolbar)
    {
        Dimension d = toolbar.getPreferredSize();
        if (ourOrientation == DockLayout.HORIZONTAL) return d.height;
        else return d.width;
    }




    /**
     * Reflects the provided rectangle about a line passing through
     * the point at the provided offset distance depending on the
     * orientation of this boundary.  This method may be used by
     * subclasses (especially at the south and east edges) to lay
     * out the toolbars relative to the boundary origin and then
     * reflect them about the offset line to produce a reverse wrap
     * of the toolbars.
     */
    protected void mirrorBounds(Rectangle r, int mirrorOffset)
    {
        int distanceFromMirror = 0;

        if (getOrientation() == DockLayout.HORIZONTAL)
        {
            // mirrorOffset represents the x-axis
            distanceFromMirror = r.y - mirrorOffset;
            int newY = r.y - (2 * distanceFromMirror) - r.height;
            r.y = newY;
        }

        else
        {
            // mirrorOffset represents the y-axis
            distanceFromMirror = r.x - mirrorOffset;
            int newX = r.x - (2 * distanceFromMirror) - r.width;
            r.x = newX;
        }
    }








    /**
     * Calculates the bounds of the dockable range based on the
     * current bounds of this boundary.
     */
    private void validateDockableBoundary()
    {
        ourDockableBoundary.setBounds(this);
        if (ourOrientation == DockLayout.HORIZONTAL)
                    ourDockableBoundary.height += ourDockableMargin;

        else ourDockableBoundary.width += ourDockableMargin;


        if (ourDockEdge == DockLayout.SOUTH)
                    ourDockableBoundary.y -= ourDockableMargin;

        else if (ourDockEdge == DockLayout.EAST)
                    ourDockableBoundary.x -= ourDockableMargin;
    }



}