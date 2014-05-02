/*
 *  DockLayout.java
 *  2004-01-02
 */


package imm.gis.gui.toolbar.layouts;

import java.awt.*;
import javax.swing.*;

/**
 * Class providing layout management to control positions of docked
 * toolbars.
 * @author Christopher Bach
 */
public class DockLayout implements LayoutManager, LayoutManager2
{

    private DockBoundary                ourNorthBoundary = null;
    private DockBoundary                ourSouthBoundary = null;
    private DockBoundary                ourEastBoundary = null;
    private DockBoundary                ourWestBoundary = null;
    private Component                   ourContent = null;

    private Container                   ourTargetContainer = null;

    private int                         ourVerticalSpacing = 0;
    private int                         ourHorizontalSpacing = 0;

    private boolean                     ourVersionIsCompatible = false;

    public static final int             CHAINING_STYLE = -1;
    public static final int             WRAPPING_STYLE = 0;
    public static final int             STACKING_STYLE = 1;

    public static final int             MAX         = Integer.MAX_VALUE;
    public static final int             NORTH       = SwingConstants.NORTH;
    public static final int             SOUTH       = SwingConstants.SOUTH;
    public static final int             EAST        = SwingConstants.EAST;
    public static final int             WEST        = SwingConstants.WEST;
    public static final int             CENTER      = SwingConstants.CENTER;
    public static final int             HORIZONTAL  = SwingConstants.HORIZONTAL;
    public static final int             VERTICAL    = SwingConstants.VERTICAL;

    public static final String          north       = BorderLayout.NORTH;
    public static final String          south       = BorderLayout.SOUTH;
    public static final String          east        = BorderLayout.EAST;
    public static final String          west        = BorderLayout.WEST;
    public static final String          center      = BorderLayout.CENTER;







    /**
     * Creates a DockLayout for the specified container
     * with a default wrapping style layout.
     */
    public DockLayout(Container target)
    {
        this(target, WRAPPING_STYLE);
    }


    /**
     * Creates a DockLayout for the specified container with
     * the specified layout style (wrapping, stacking, or chaining).
     */
    public DockLayout(Container target, int style)
    {
        ourTargetContainer = target;

        String specVersion = System.getProperty("java.specification.version");
        try
        {
            float ver = Float.parseFloat(specVersion);
            if (ver > 1.2f) ourVersionIsCompatible = true;
        }

        catch(Exception ex)
        {
            // Assume the version is not high enough,
            // leave ourVersionIsCompatible = false.
        }


        installDockBoundary(createBoundary(style, NORTH, 2));
        installDockBoundary(createBoundary(style, SOUTH, 2));
        installDockBoundary(createBoundary(style, EAST, 2));
        installDockBoundary(createBoundary(style, WEST, 2));
    }




    /**
     * Returns a new DockBoundary based on the specified style for
     * the specified edge with the provided toolbar spacing.
     */
    private DockBoundary createBoundary(int style, int edge, int spacing)
    {
        if (style == STACKING_STYLE)
            return new StackingDockBoundary(edge, spacing);

        else if (style == CHAINING_STYLE)
            return new ChainingDockBoundary(edge, spacing);

        else return new WrappingDockBoundary(edge, spacing);
    }


















    /**
     * Sets the horizontal spacing between the content
     * and the east and west docks.
     */
    public void setHorizontalSpacing(int spacing)
    {
        ourHorizontalSpacing = spacing;
    }


    /**
     * Sets the vertical spacing between the content
     * and the north and south docks.
     */
    public void setVerticalSpacing(int spacing)
    {
        ourVerticalSpacing = spacing;
    }


    /**
     * Sets the spacing between the toolbars at each dock.
     */
    public void setToolBarSpacing(int spacing)
    {
        ourNorthBoundary.setSpacing(spacing);
        ourSouthBoundary.setSpacing(spacing);
        ourEastBoundary.setSpacing(spacing);
        ourWestBoundary.setSpacing(spacing);
    }










    /**
     * Returns the horizontal spacing between the content
     * and the west and east docks.
     */
    public int getHorizontalSpacing()
    {
        return ourHorizontalSpacing;
    }


    /**
     * Returns the vertical spacing between the content
     * and the north and south docks.
     */
    public int getVerticalSpacing()
    {
        return ourVerticalSpacing;
    }


    /**
     * Returns the spacing between the toolbars at each dock.
     */
    public int getToolBarSpacing()
    {
        return ourNorthBoundary.getSpacing();
    }















    /**
     * Provided for those who wish to create their own DockBoundary
     * implementation for functionality not available in the provided
     * docking styles.
     */
    public void installDockBoundary(DockBoundary boundary)
    {
        if (boundary == null) return;

        // Make sure that we're running a compatible java spec version.  For
        // version 1.2, the UI will maintain control of drag and drop, so we
        // should only use the simplest dock boundary style.
        if (!ourVersionIsCompatible
                && !(boundary instanceof ChainingDockBoundary))
                        boundary = new ChainingDockBoundary(boundary.getEdge());

        int edge = boundary.getEdge();
        DockBoundary db = getBoundary(edge);

        // Copy toolbars from old boundary...
        JToolBar[] toolbars = null;
        if (db != null) toolbars = db.getToolBars();
        // ...into new boundary.
        if (toolbars != null)
        {
            for (int i=0; i < toolbars.length; i++)
            {
                boundary.addToolBar(toolbars[i], 0, MAX);
            }
        }

        if (edge == NORTH) ourNorthBoundary = boundary;
        else if (edge == SOUTH) ourSouthBoundary = boundary;
        else if (edge == EAST) ourEastBoundary = boundary;
        else if (edge == WEST) ourWestBoundary = boundary;
    }



    /**
     * Returns the DockBoundary at the specified edge.
     */
    private DockBoundary getBoundary(int edge)
    {
        if (edge == NORTH) return ourNorthBoundary;
        else if (edge == SOUTH) return ourSouthBoundary;
        else if (edge == EAST) return ourEastBoundary;
        else if (edge == WEST) return ourWestBoundary;
        else return null;
    }


    /**
     * Returns the DockBoundary at the specified edge.
     */
    private DockBoundary getBoundary(String edge)
    {
        if (edge.equals(north)) return ourNorthBoundary;
        else if (edge.equals(south)) return ourSouthBoundary;
        else if (edge.equals(east)) return ourEastBoundary;
        else if (edge.equals(west)) return ourWestBoundary;
        else return null;
    }



    // Package access only
    /**
     * Returns the DockBoundary containing the specified point,
     * or null if no boundary contains this point.
     */
    DockBoundary getBoundary(Point point)
    {
        if (ourNorthBoundary.contains(point)) return ourNorthBoundary;
        else if (ourSouthBoundary.contains(point)) return ourSouthBoundary;
        else if (ourWestBoundary.contains(point)) return ourWestBoundary;
        else if (ourEastBoundary.contains(point)) return ourEastBoundary;
        else return null;
    }



    // Package access only
    /**
     * Returns the DockBoundary whose dockable range contains the
     * specified point, or null if no boundary contains this point.
     */
    DockBoundary getDockableBoundary(Point point)
    {
        if (ourNorthBoundary.isDockablePoint(point)) return ourNorthBoundary;
        else if (ourSouthBoundary.isDockablePoint(point)) return ourSouthBoundary;
        else if (ourWestBoundary.isDockablePoint(point)) return ourWestBoundary;
        else if (ourEastBoundary.isDockablePoint(point)) return ourEastBoundary;
        else return null;
    }



    // Package access only
    /**
     * Returns the target container managed by this DockManager.
     */
    Container getTargetContainer()
    {
        return ourTargetContainer;
    }





    /**
     * Returns the ToolBarHandler attached to the specified toolbar
     * as a client property.  If no handler is present, creates a
     * new one and attaches it to the toolbar.
     */
    private ToolBarHandler getHandler(JToolBar toolbar)
    {
        if (toolbar == null) return null;

        ToolBarHandler handler = extractHandler(toolbar);

        if (handler == null) // Ensure that the toolbar has a handler
        {
            String key = ToolBarHandler.TOOL_BAR_HANDLER_KEY;
            handler = new ToolBarHandler(toolbar, this);
            toolbar.putClientProperty(key, handler);
        }

        return handler;
    }


    /**
     * Returns the ToolBarHandler attached to the specified toolbar
     * as a client property, or null if no handler is attached.
     */
    private ToolBarHandler extractHandler(JToolBar toolbar)
    {
        if (toolbar == null) return null;

        String key = ToolBarHandler.TOOL_BAR_HANDLER_KEY;

        Object prop = toolbar.getClientProperty(key);
        return (ToolBarHandler)prop;    // May be null.
    }
















    //// LayoutManager and LayoutManager2 implementation ////


    /**
     * Returns the alignment of this layout along the X-axis.
     */
    public float getLayoutAlignmentX(Container target)
    {
        return 0.5f;
    }


    /**
     * Returns the alignment of this layout along the Y-axis.
     */
    public float getLayoutAlignmentY(Container target)
    {
        return 0.5f;
    }


    /**
     * Adds the specified component to the layout.  If the constraints object
     * is a DockingConstraints instance, the parameters of this object will
     * be used to determine the location of the component within the layout
     * relative to components already added to the layout.
     */
    public void addLayoutComponent(Component component, Object constraints)
    {
        String constr = "";

        if (component instanceof JToolBar
            && constraints instanceof DockingConstraints)
        {
            JToolBar toolbar = (JToolBar)component;
            ToolBarHandler handler = getHandler(toolbar);
            DockingConstraints dc = (DockingConstraints)constraints;
            handler.setConstraints(dc);

            int i = handler.getDockEdge();
            if (i == NORTH) constr = north;
            else if (i == SOUTH) constr = south;
            else if (i == WEST) constr = west;
            else if (i == EAST) constr = east;
        }

        else if (constraints != null && constraints instanceof String)
        {
            constr = constraints.toString();
        }

        addLayoutComponent(constr, component);
    }

    /**
     * Adds the specified component to the layout.  A JToolBar passed into
     * this method will be docked at the edge specified by its associated
     * ToolBarHandler unless the caller passes in an alternate edge as
     * the constraint.
     */
    public void addLayoutComponent(String constraints, Component component)
    {
        synchronized (component.getTreeLock())
        {
            if (component instanceof JToolBar)
            {
                JToolBar toolbar = (JToolBar)component;
                ToolBarHandler handler = getHandler(toolbar);


                DockBoundary boundary = null;
                if (constraints != null) boundary = getBoundary(constraints);
                if (boundary == null) boundary = getBoundary(handler.getDockEdge());



                boundary.addToolBar( toolbar,
                                        handler.getRowIndex(),
                                            handler.getDockIndex() );
                boundary.refreshHandlers();
            }


            else ourContent = component;
        }

    }



    /**
     * Removes the specified component from the layout.
     */
    public void removeLayoutComponent(Component component)
    {
        ourNorthBoundary.removeComponent(component);
        ourSouthBoundary.removeComponent(component);
        ourEastBoundary.removeComponent(component);
        ourWestBoundary.removeComponent(component);
        if (component == ourContent) ourContent = null;
    }



    /**
     * Invalidates the layout.
     */
    public void invalidateLayout(Container target)
    {

    }



    /**
     * Sets the sizes and locations of the specified container's
     * subcomponents (docked toolbars and content).
     */
    public void layoutContainer(Container target)
    {
        ourTargetContainer = target;

        synchronized (target.getTreeLock())
        {
            int width = target.getWidth();
            int height = target.getHeight();

            Insets insets = target.getInsets();
            int top     = insets.top;
            int bottom  = height - insets.bottom;
            int left    = insets.left;
            int right   = width - insets.right;


            ourNorthBoundary.setPosition(left, top, width);
            ourSouthBoundary.setPosition(left, bottom, width);

            int northHeight = ourNorthBoundary.getDepth();
            int southHeight = ourSouthBoundary.getDepth();
            if (northHeight > 0) northHeight += ourVerticalSpacing;
            if (southHeight > 0) southHeight += ourVerticalSpacing;
            height = (bottom - top) - northHeight - southHeight;
            top += northHeight;

            ourWestBoundary.setPosition(left, top, height);
            ourEastBoundary.setPosition(right, top, height);

            int eastWidth = ourEastBoundary.getDepth();
            int westWidth = ourWestBoundary.getDepth();
            if (eastWidth > 0) eastWidth += ourHorizontalSpacing;
            if (westWidth > 0) westWidth += ourHorizontalSpacing;
            width = (right - left) - eastWidth - westWidth;
            left += westWidth;


            if (ourContent != null)
            {
                ourContent.setBounds(left, top, width, height);
            }
        }
    }



    /**
     * Determines the preferred dimensions of the specified container.
     */
    public Dimension preferredLayoutSize(Container target)
    {
        Dimension prefDim = null;
        if (ourContent != null) prefDim = ourContent.getPreferredSize();
        else prefDim = new Dimension(0, 0);

        int dim = ourNorthBoundary.getDepth();
        if (dim > 0 && prefDim.height > 0) prefDim.height += ourVerticalSpacing;
        prefDim.height += dim;

        dim = ourSouthBoundary.getDepth();
        if (dim > 0 && prefDim.height > 0) prefDim.height += ourVerticalSpacing;
        prefDim.height += dim;

        dim = ourWestBoundary.getDepth();
        if (dim > 0 && prefDim.width > 0) prefDim.width += ourHorizontalSpacing;
        prefDim.width += dim;

        dim = ourEastBoundary.getDepth();
        if (dim > 0 && prefDim.width > 0) prefDim.width += ourHorizontalSpacing;
        prefDim.width += dim;

        return prefDim;
    }


    /**
     * Determines the minimum dimensions of the specified container.
     */
    public Dimension minimumLayoutSize(Container target)
    {
        return new Dimension(0, 0);
    }


    /**
     * Determines the maximum dimensions of the specified container.
     */
    public Dimension maximumLayoutSize(Container target)
    {
        return new Dimension(MAX, MAX);
    }


}