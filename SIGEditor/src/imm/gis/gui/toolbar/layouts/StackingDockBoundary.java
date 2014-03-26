/*
 *  StackingDockBoundary.java
 *  2004-01-02
 */


package imm.gis.gui.toolbar.layouts;

import java.awt.*;
import javax.swing.*;
import java.util.*;


/**
 * A DockBoundary that allows stacking toolbars into rows (or columns).
 * Rows are NOT automatically wrapped when the toolbars don't fit.
 * @author Christopher Bach
 */
// Package access only...
class StackingDockBoundary extends DockBoundary
{

	private static final long serialVersionUID = 1L;

	private ArrayList<DockSliver>   ourDockSlivers = new ArrayList<DockSliver>();




    /**
     * Creates a StackingDockBoundary for the specified edge.
     */
    public StackingDockBoundary(int edge)
    {
        super(edge);
    }

    /**
     * Creates a StackingDockBoundary for the specified edge
     * with the provided spacing.
     */
    public StackingDockBoundary(int edge, int spacing)
    {
        super(edge, spacing);
    }












    /**
     * Implementation of the abstract superclass method, returns
     * the index at which the toolbar should be added when dropped
     * at the specified point.
     */
    public int getDockIndex(Point p)
    {
        for (int i=0; i < ourDockSlivers.size(); i++)
        {
            DockSliver sliver = (DockSliver)ourDockSlivers.get(i);
            if (sliver.contains(p)) return sliver.getDockIndex(p);
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
        DockSliver sliver = getDockSliver(toolbar);
        if (sliver == null) return -1;
        else return sliver.getDockIndex(toolbar);
    }




    /**
     * Implementation of the abstract superclass method, returns
     * the row index at which the toolbar should be added when
     * dropped at the specified point.
     */
    public int getRowIndex(Point p)
    {
        for (int i=0; i < ourDockSlivers.size(); i++)
        {
            DockSliver sliver = (DockSliver)ourDockSlivers.get(i);
            if (sliver.contains(p)) return i;
        }

        return DockLayout.MAX;
    }



    /**
     * Implementation of the abstract superclass method, returns
     * the row index of the specified toolbar in this boundary,
     * or -1 if the toolbar is not present.
     */
    public int getRowIndex(JToolBar toolbar)
    {
        for (int i=0; i < ourDockSlivers.size(); i++)
        {
            DockSliver sliver = (DockSliver)ourDockSlivers.get(i);
            if (sliver.containsToolBar(toolbar)) return i;
        }

        return -1;
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
        int edge = getEdge();

        int length = 0;
        if (orientation == DockLayout.HORIZONTAL) length = width;
        else length = height;

        int totalDepth = 0;


        for (int i=0; i < ourDockSlivers.size(); i++)
        {
            DockSliver sliver = (DockSliver)ourDockSlivers.get(i);

            if (totalDepth != 0) totalDepth += spacing;

            if (orientation == DockLayout.HORIZONTAL)
                sliver.setPosition(x, y + totalDepth, length);
            else sliver.setPosition(x + totalDepth, y, length);

            if (edge == DockLayout.EAST || edge == DockLayout.SOUTH)
            {
                Rectangle r = sliver.getBounds();

                if (orientation == DockLayout.HORIZONTAL)
                                mirrorBounds(r, y);
                else mirrorBounds(r, x);

                sliver.setBounds(r);
                sliver.validate();
            }

            totalDepth += sliver.getDepth();
        }


        setDepth(totalDepth);

    }


















    // Override superclass methods to provide additional behavior


    /**
     * Inserts the specified toolbar into this boundary at the
     * provided indices.
     */
    protected void toolBarAdded(JToolBar toolbar, int rowIndex, int index)
    {
        getDockSliver(rowIndex).addToolBar(toolbar, index);
    }



    /**
     * Removes the specified toolbar from this boundary.
     */
    protected void toolBarRemoved(JToolBar toolbar)
    {
        DockSliver sliver = getDockSliver(toolbar);
        if (sliver != null)
        {
            sliver.removeToolBar(toolbar);
            if (sliver.getToolBarCount() == 0)
                    ourDockSlivers.remove(sliver);
        }
    }
















    /**
     * Returns a DockSliver for the specified row.  If none exists
     * at this index, a new one is created and inserted.
     */
    private DockSliver getDockSliver(int row)
    {
        if (row < 0)
        {
            DockSliver sliver = new DockSliver();
            ourDockSlivers.add(0, sliver);
            return sliver;
        }

        else if (row >= ourDockSlivers.size())
        {
            DockSliver sliver = new DockSliver();
            ourDockSlivers.add(sliver);
            return sliver;
        }

        else return (DockSliver)ourDockSlivers.get(row);
    }



    /**
     * Returns the DockSliver containing the specified toolbar or
     * null if no DockSliver contains this toolbar.
     */
    private DockSliver getDockSliver(JToolBar toolbar)
    {
        for (int i=0; i < ourDockSlivers.size(); i++)
        {
            DockSliver sliver = (DockSliver)ourDockSlivers.get(i);
            if (sliver.containsToolBar(toolbar)) return sliver;
        }

        return null;
    }















    /**
     * Inner class defining a row or "sliver" of toolbars stacked
     * within this boundary.
     */
    private class DockSliver extends Rectangle
    {

		private static final long serialVersionUID = 1L;

		private ArrayList<JToolBar>   myToolBars = new ArrayList<JToolBar>();


        public DockSliver()
        {

        }



        public void addToolBar(JToolBar toolbar)
        {
            myToolBars.add(toolbar);
        }


        public void addToolBar(JToolBar toolbar, int index)
        {
            if (index < 0) myToolBars.add(0, toolbar);
            else if (index >= myToolBars.size()) myToolBars.add(toolbar);
            else myToolBars.add(index, toolbar);
        }


        public void removeToolBar(JToolBar toolbar)
        {
            myToolBars.remove(toolbar);
        }


        public int getToolBarCount()
        {
            return myToolBars.size();
        }


        public boolean containsToolBar(JToolBar toolbar)
        {
            return myToolBars.contains(toolbar);
        }






        public void setPosition(int x, int y, int length)
        {
            setLocation(x, y);
            if (getOrientation() == DockLayout.HORIZONTAL) width = length;
            else height = length;
            validate();
        }



        public void validate()
        {
            int pos = 0;
            int base = 0;
            int orient = getOrientation();
            int space = getSpacing();
            int length = 0;

            if (orient == DockLayout.HORIZONTAL)
            {
                pos = x;
                base = x;
                length = width;
                height = getPreferredDepth();
            }

            else
            {
                pos = y;
                base = y;
                length = height;
                width = getPreferredDepth();
            }

            for (int i=0; i < myToolBars.size(); i++)
            {
                JToolBar toolbar = (JToolBar)myToolBars.get(i);
                int barLength = getPreferredToolBarLength(toolbar);
                if (pos + barLength > length + base)
                        barLength = base + length - pos;

                if (orient == DockLayout.HORIZONTAL)
                {
                    toolbar.setBounds(pos, y, barLength, height);
                }

                else toolbar.setBounds(x, pos, width, barLength);

                pos += barLength + space;
            }
        }


        public int getPreferredDepth()
        {
            int depth = 0;

            for (int i=0; i < myToolBars.size(); i++)
            {
                JToolBar toolbar = (JToolBar)myToolBars.get(i);
                int barDepth = getPreferredToolBarDepth(toolbar);
                depth = Math.max(depth, barDepth);

            }

            return depth;
        }


        public int getDepth()
        {
            if (getOrientation() == DockLayout.HORIZONTAL) return height;
            else return width;
        }



        public int getDockIndex(Point p)
        {
            for (int i=0; i < myToolBars.size(); i++)
            {
                JToolBar toolbar = (JToolBar)myToolBars.get(i);
                if (toolbar.getBounds().contains(p)) return i;
            }

            return DockLayout.MAX;
        }


        public int getDockIndex(JToolBar toolbar)
        {
            return myToolBars.indexOf(toolbar);
        }
    }

}