
package imm.gis.gui.toolbar;

/**
 * A utility to return the version number of this release
 *
 * @author Bob Tarling
 * @stereotype utility
 */
public class Version {
	public static int RELEASE_NUMBER = 1;
	public static int MAJOR_REVISION_NUMBER = 1;
	public static int MINOR_REVISION_NUMBER = 0;
	private Version() {}
	
	public static String getVersion() {
		return RELEASE_NUMBER+"."+MAJOR_REVISION_NUMBER+"."+MINOR_REVISION_NUMBER;
	}
}
