package imm.gis.form.item;

import imm.gis.core.layer.metadata.LayerAttributePresentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.geotools.feature.AttributeType;

public class URLFormItem extends BasicFormItem {
	private static final long serialVersionUID = 1L;
	
	public URLFormItem(AttributeType _at,Object value, LayerAttributePresentation itemLap) {
		super(_at, value, itemLap);
		
		getValueComponent().setFont(getValueComponent().getFont().deriveFont(Font.ITALIC));
		getValueComponent().setForeground(Color.blue);
		getValueComponent().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent evt){
				
				try {
					int mask = MouseEvent.CTRL_DOWN_MASK; 
					boolean controlPressed = ((evt.getModifiersEx() & mask) == mask);
					
					if (controlPressed){
						URL url = new URL(getValue().toString());
// JAVA 6						Desktop.getDesktop().browse(url.toURI());
						if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0){
							Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url.toString());							
						} else {
							Runtime.getRuntime().exec("firefox " + url.toString());														
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e2){
					e2.printStackTrace();
				}
			}
		});
		
	}	
}
