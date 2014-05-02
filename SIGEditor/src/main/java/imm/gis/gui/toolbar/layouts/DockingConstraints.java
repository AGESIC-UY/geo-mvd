/*
 *  DockingConstraints.java
 *  2004-01-02
 */


package imm.gis.gui.toolbar.layouts;

/**
 * Class representing the edge and indicies at which a toolbar should be docked.
 * @author Christopher Bach
 */
public class DockingConstraints
{
    public static final int             MAX         = DockLayout.MAX;
    public static final int             NORTH       = DockLayout.NORTH;
    public static final int             SOUTH       = DockLayout.SOUTH;
    public static final int             EAST        = DockLayout.EAST;
    public static final int             WEST        = DockLayout.WEST;

    private int                         ourDockIndex = MAX;
    private int                         ourRowIndex = 0;
    private int                         ourDockEdge = NORTH;



    /**
     * Creates a DockingConstraints object with a default
     * edge of NORTH, row of 0, and index of MAX.
     */
    public DockingConstraints()
    {

    }


    /**
     * Creates a DockingConstraints object at the specified
     * edge with a default row of 0 and index of MAX.
     */
    public DockingConstraints(int edge)
    {
        setEdge(edge);
    }


    /**
     * Creates a DockingConstraints object at the specified
     * edge and index with a default row of 0.
     */
    public DockingConstraints(int edge, int index)
    {
        setEdge(edge);
        setIndex(index);
    }


    /**
     * Creates a DockingConstraints object at the specified
     * edge, row, and index.
     */
    public DockingConstraints(int edge, int row, int index)
    {
        setEdge(edge);
        setRow(row);
        setIndex(index);
    }







    /**
     * Sets the edge at which the toolbar is docked.
     */
    void setEdge(int edge)
    {
        if (edge == NORTH
                || edge == SOUTH
                    || edge == EAST
                        || edge == WEST) ourDockEdge = edge;
    }



    /**
     * Sets the index of the row within the DockBoundary in
     * which the toolbar is docked.
     */
    void setRow(int row)
    {
        if (row < 0) ourRowIndex = 0;
        else ourRowIndex = row;
    }


    /**
     * Sets the index within the row in which the toolbar is docked.
     */
    void setIndex(int index)
    {
        if (index < 0) ourDockIndex = 0;
        else ourDockIndex = index;
    }







    /**
     * Returns an int representing the edge in which the toolbar is docked.
     */
    public int getEdge()
    {
        return ourDockEdge;
    }


    /**
     * Returns an int representing the row index within the DockBoundary
     * in which the toolbar is docked.
     */
    public int getRow()
    {
        return ourRowIndex;
    }


    /**
     * Returns an int representing the index within the row in which
     * the toolbar is docked.
     */
    public int getIndex()
    {
        return ourDockIndex;
    }


}
