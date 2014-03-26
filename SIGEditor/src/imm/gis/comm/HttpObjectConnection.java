package imm.gis.comm;

import imm.gis.GisException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class HttpObjectConnection {
	private String context;
	private String codebase;
	private static final Logger log = Logger.getLogger(HttpObjectConnection.class);

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
		log.info("Contexto: " + context);
	}

	public String getCodebase() {
		return codebase;
	}

	public void setCodebase(String codebase) {
		this.codebase = codebase;
		log.info("Codebase: " + codebase);
	}

	public Object callService(Object parameter)
			throws GisException {
		Object res = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		HttpURLConnection conn = null;

		try {
			conn = (HttpURLConnection) new URL(getCodebase() + getContext())
					.openConnection();
//			conn.setUseCaches(false);
//			conn.setDefaultUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setAllowUserInteraction(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type ",
					"application/x-java-serialized-object");

			oos = new ObjectOutputStream(conn.getOutputStream());
			oos.writeObject(parameter);
			oos.flush();

			ois = new ObjectInputStream(conn.getInputStream());
			res = ois.readObject();
		} catch (Exception e) {
			throw new GisException(e);
		} finally {
			try {
				oos.close();
				ois.close();
				conn.disconnect();
			} catch (Exception e) {
			}
		}

		return res;
	}

}
