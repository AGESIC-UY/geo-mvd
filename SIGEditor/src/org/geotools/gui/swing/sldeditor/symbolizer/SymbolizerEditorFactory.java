/*
 * Created on 15-feb-2004
 */
package org.geotools.gui.swing.sldeditor.symbolizer;

import java.awt.Component;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.geotools.factory.Factory;
import org.geotools.factory.FactoryRegistry;
import org.geotools.feature.FeatureType;


/**
 * DOCUMENT ME!
 *
 * @author wolf
 */
public abstract class SymbolizerEditorFactory implements Factory {
    private static SymbolizerEditorFactory factory = null;
    protected boolean inExpertMode;

    /**
     * Create an instance of the factory.
     *
     * @return An instance of the Factory, or null if the Factory could not be
     *         created.
     *
     * @throws FactoryConfigurationError
     */
    public static SymbolizerEditorFactory createPropertyEditorFactory(){
        if (factory == null) {
        	Set<?> categories = Collections.singleton(new Class[] {SymbolizerEditorFactory.class});
        	FactoryRegistry fr = new FactoryRegistry(categories);
            factory = (SymbolizerEditorFactory) fr.getServiceProviders(SymbolizerEditorFactory.class, null, null);
        }

        return factory;
    }

    public abstract SymbolizerEditor createLineSymbolizerEditor(FeatureType featureType);
    
    public abstract SymbolizerEditor createPointSymbolizerEditor(FeatureType featureType);
    
    public abstract SymbolizerEditor createPolygonSymbolizerEditor(FeatureType featureType);
    
    public abstract SymbolizerEditor createTextSymbolizerEditor(FeatureType featureType);
    
    public abstract SymbolizerEditor createRasterSymbolizerEditor(FeatureType featureType);
    
    public abstract SymbolizerChooserDialog createSymbolizerChooserDialog(Component parent, FeatureType featureType); 

    /**
     * Returns the implementation hints. The default implementation returns en empty map.
     */
    public Map getImplementationHints() {
        return Collections.EMPTY_MAP;
    }
}
