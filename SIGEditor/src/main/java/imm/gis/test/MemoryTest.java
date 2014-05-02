package imm.gis.test;

import java.io.IOException;

import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;

public class MemoryTest{
	MemoryDataStore ds = new MemoryDataStore();
	
	public MemoryTest(){
		try{
			createSchema();	
			loadData();
			testData();
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			System.out.println("Listo");
			System.exit(0);
		}
	}
	
	void createSchema() throws IOException, SchemaException {
		FeatureTypeBuilder ftf = FeatureTypeBuilder.newInstance("capa");
		ftf.addType(AttributeTypeFactory.newAttributeType("campo1", Integer.class, true));
		ftf.addType(AttributeTypeFactory.newAttributeType("campo2", Integer.class, true));
		ds.createSchema(ftf.getFeatureType());		
	}
	
	void loadData() throws IOException, IllegalAttributeException  {
		FeatureType ft = ds.getSchema("capa");
		
		Feature data[] = new Feature[]{
			ft.create(new Object[]{new Integer(1), new Integer(2)}),
			ft.create(new Object[]{new Integer(3), new Integer(4)}),
			ft.create(new Object[]{new Integer(5), new Integer(6)})
		};
		
		ds.addFeatures(data);
	}
	
	void testData() throws IOException, IllegalAttributeException {
		FeatureWriter fw = ds.createFeatureWriter("capa", Transaction.AUTO_COMMIT);
		Feature f;
		
		while (fw.hasNext()){
			f = fw.next();
			f.setAttribute(0, new Integer(50));
			fw.write();
		}
		fw.close();
		
		FeatureReader fr = ds.getFeatureReader("capa");
		
		while (fr.hasNext()){
			System.out.println(fr.next());
		}
		
		fr.close();
	}
	
	public static void main(String[] args) {
		new MemoryTest();
	}

}
