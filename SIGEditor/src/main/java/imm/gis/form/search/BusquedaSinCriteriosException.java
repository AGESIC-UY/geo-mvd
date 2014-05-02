package imm.gis.form.search;

public class BusquedaSinCriteriosException extends Exception {

	private static final long serialVersionUID = 1L;

	public BusquedaSinCriteriosException(){
		super("Se requieren los criterios para realizar la busqueda");
	}
}
