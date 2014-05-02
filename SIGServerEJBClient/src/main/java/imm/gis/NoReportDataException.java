package imm.gis;

public class NoReportDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoReportDataException(String idApp, String idReport) {
		super("No hay datos para el reporte " + idReport + " de la aplicacion " + idApp);
	}
}
