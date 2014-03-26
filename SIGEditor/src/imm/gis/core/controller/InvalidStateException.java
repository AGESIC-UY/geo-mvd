package imm.gis.core.controller;

import imm.gis.GisException;

public class InvalidStateException extends GisException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidStateException(){
		super("No tiene permitida esta operacion...");
	}
}
