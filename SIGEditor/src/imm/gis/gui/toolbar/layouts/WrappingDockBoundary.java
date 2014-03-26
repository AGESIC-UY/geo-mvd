/*
 *  WrappingDockBoundary.java
 *  2004-01-02
 */


package imm.gis.gui.toolbar.layouts;

import java.awt.*;
import javax.swing.*;
import java.util.*;


/**
 * A DockBoundary that automatically wraps the docked toolbars
 * when they don't all fit on the same row.
 * @author Christopher Bach
 */
// package access only...
class WrappingDockBoundary extends DockBoundary
{

	private static final long serialVersionUID = 1L;

	private boolean         ourLayoutReflects = false;





    /**
     * Creates a WrappingDockBoundary for the specified edge.
     */
    public WrappingDockBoundary(int edge)
    {
        super(edge);
        ourLayoutReflects = (edge == DockLayout.SOUTH
                                || edge == DockLayout.EAST);
    }


    /**
     * Creates a WrappingDockBoundary for the specified edge
     * with the provided spacing.
     */
    public WrappingDockBoundary(int edge, int spacing)
    {
        super(edge, spacing);
        ourLayoutReflects = (edge == DockLayout.SOUTH
                                || edge == DockLayout.EAST);
    }















    /**
     * Implementation of the abstract superclass method, returns
     * the index at which the toolbar should be added when dropped
     * at the specified point.
     */
    public int getDockIndex(Point p)
    {
        JToolBar[] toolbars = getToolBars();

        for (int i=0; i < toolbars.length; i++)
        {
            if (toolbars[i].getBounds().contains(p))
            {
                return i;
            }
        }

        return DockLayout.MAX;
    }



    /**
     * Implementation of the absract superclass method, returns
     * the index of the specified toolbar within this boundary,
     * or -1 if the toolbar is not present.
     */
    public int getDockIndex(JToolBar toolbar)
    {
        return Arrays.asList(getToolBars()).indexOf(toolbar);
    }




    /**
     * Implementation of the abstract superclass method, returns
     * the row index at which the toolbar should be added when
     * dropped at the specified point.
     */
    public int getRowIndex(Point p)
    {
        return 0;
    }



    /**
     * Implementation of the abstract superclass method, returns
     * the row index of the specified toolbar in this boundary,
     * or -1 if the toolbar is not present.
     */
    public int getRowIndex(JToolBar toolbar)
    {
        if (Arrays.asList(getToolBars()).contains(toolbar)) return 0;
        else return -1;
    }





    /**
     * Implementation of the abstract superclass method, lays out
     * the registered toolbars and calculates the depth of
     * this boundary.  When this method is called, the depth of
     * the boundary is assumed to be 0 and is not determined until
     * the validation completes.  For convenience, the
     * subcomponents are arranged in a top-down or left-to-right
     * fashion relative to the origin of this boundary, which
     * presumably lies at the parent container's edge.  For the
     * south and east boundaries, this puts the bounds of the
     * subcomponents outside the bounds of the parent container.
     * To compensate, these subcomponents are reflected about a
     * line passing through the boundary's origin as they are placed.
     */
    public void validate()
    {
        int spacing = getSpacing();
        int orientation = getOrientation();

        int length = 0;
        if (orientation == DockLayout.HORIZONTAL) length = width;
        else length = height;

        JToolBar[] bars = getToolBars();
        int barDepth = getPreferredDepth();
        int barLength = 0;
        int totalBarDepth = barDepth;
        int totalBarLength = 0;

        for (int i=0; i < bars.length; i++)
        {
            JToolBar toolbar = bars[i];

            barLength = getPreferredToolBarLength(toolbar);

            if (totalBarLength != 0) totalBarLength += spacing;

            setToolBarBounds(toolbar, totalBarLength,
                    totalBarDepth - barDepth,
                    Math.min(barLength, length), barDepth);


            totalBarLength += barLength;

            if (totalBarLength > length && i > 0)
            {
                // Need to move the current toolbar to the
                // next row and start totalling the length anew.
                totalBarDepth += barDepth + spacing;
                totalBarLength = 0;
                setToolBarBounds(toolbar, totalBarLength,
                    totalBarDepth - barDepth,
                    Math.min(barLength, length), barDepth);
                totalBarLength = barLength;
            }
        }

        setDepth(totalBarDepth);
    }



















    /**
     * Returns the largest preferred height or width (depending
     * on orientation) of all of the associated toolbars.
     */
    private int getPreferredDepth()
    {
        int depth = 0;

        JToolBar[] toolbars = super.getToolBars();

        for (int i=0; i < toolbars.length; i++)
        {
            JToolBar toolbar = toolbars[i];
            Dimension d = toolbar.getPreferredSize();
            if (getOrientation() == DockLayout.HORIZONTAL)
                    depth = Math.max(depth, d.height);
            else depth = Math.max(depth, d.width);
        }

        return depth;
    }










    /**
     * Sets the bounds of the provided toolbar based on the provided
     * bounds parameters (given relative to this boundary's origin)
     * accounting for the default wrapping direction for this boundary.
     */
    private void setToolBarBounds(JToolBar toolbar, int lengthOffset,
                    int depthOffset, int length, int depth)
    {
        if (getOrientation() == DockLayout.HORIZONTAL)
        {
            toolbar.setBounds(x + lengthOffset,
                        y + depthOffset, length, depth);

            if (ourLayoutReflects)
            {
                Rectangle r = toolbar.getBounds();
                mirrorBounds(r, y);
                toolbar.setBounds(r);
            }
        }

        else
        {
            toolbar.setBounds(x + depthOffset,
                        y + lengthOffset,
                            depth, length);

            if (ourLayoutReflects)
            {
                Rectangle r = toolbar.getBounds();
                mirrorBounds(r, x);
                toolbar.setBounds(r);
            }
        }
    }






}