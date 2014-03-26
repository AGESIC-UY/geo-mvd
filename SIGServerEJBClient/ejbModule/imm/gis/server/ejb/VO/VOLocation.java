package imm.gis.server.ejb.VO;

import java.io.Serializable;

public class VOLocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int codCalle;
	private String nombreCalle;
	private int nroPuerta;
	private int ccz;
	
	public VOLocation(){
		
	}
	
	public VOLocation(int cc, String nc, int np, int ccz){
		setCodCalle(cc);
		setNombreCalle(nc);
		setNroPuerta(np);
		setCcz(ccz);
	}

	public int getCodCalle() {
		return codCalle;
	}

	public void setCodCalle(int codCalle) {
		this.codCalle = codCalle;
	}

	public String getNombreCalle() {
		return nombreCalle;
	}

	public void setNombreCalle(String nombreCalle) {
		this.nombreCalle = nombreCalle;
	}

	public int getNroPuerta() {
		return nroPuerta;
	}

	public void setNroPuerta(int nroPuerta) {
		this.nroPuerta = nroPuerta;
	}

	public int getCcz() {
		return ccz;
	}

	public void setCcz(int ccz) {
		this.ccz = ccz;
	}
	
	public String toString(){
		StringBuffer res = new StringBuffer();
		res.append("Calle ");
		res.append(nombreCalle);
		res.append(" (cod. ");
		res.append(codCalle);
		res.append(") al ");
		res.append(nroPuerta);
		res.append(", ccz ");
		res.append(ccz);
		
		return res.toString();
	}
}
