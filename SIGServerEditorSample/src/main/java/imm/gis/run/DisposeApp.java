package imm.gis.run;

import imm.gis.GisException;
import imm.gis.comm.ServerServicesFactory;

public class DisposeApp {

	public static void main(String[] args) {
		if (args.length != 1){
			System.out.println("Uso: DisposeApp idApp");
			return;
		}
		
		try {
			System.out.println("Bajando aplicacion " + args[0] + " ...");
			ServerServicesFactory.getServerServices().closeApp("limpieza");
			System.out.println("Listo");
		} catch (GisException e) {
			e.printStackTrace();
		}
	}

}
