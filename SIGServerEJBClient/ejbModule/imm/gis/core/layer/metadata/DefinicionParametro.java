package imm.gis.core.layer.metadata;

import java.io.Serializable;

public class DefinicionParametro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -727434446693029184L;
	private String tipo;
	private String valor;
	
	public DefinicionParametro() {
		tipo = null;
		valor = null;
	}
	
	public DefinicionParametro(String _tipo, String _valor) {
		tipo = _tipo;
		valor = _valor;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	
}
