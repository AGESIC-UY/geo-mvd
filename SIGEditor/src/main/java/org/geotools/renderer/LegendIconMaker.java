/*
 *    Geotools2 - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
/*
 * LegendNoteIconMaker.java
 *
 * Created on 05 July 2003, 22:05
 */
package org.geotools.renderer;

import imm.gis.core.feature.Util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;
import org.geotools.gui.swing.sldeditor.util.StyleCloner;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.DefaultMapLayer;
import org.geotools.map.MapLayer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
//import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryImpl;
import org.geotools.styling.Symbolizer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;


/**
 * DOCUMENT ME!
 *
 * @author jianhuij
 */
public class LegendIconMaker {
    /**
     * for create artificial geometry feature for making legend icon since the
     * icon somehow has somekind of shape such as line, polygon etc. then
     * other code will apply fill and stroke to make an image
     */
    public static GeometryFactory gFac = new GeometryFactory();

    /**
     * if the rule already has defined legendGraphic the stylefactory could
     * create symbolizer to contain it
     */
    public static StyleFactory sFac = new StyleFactoryImpl();
    
    /**
     * offset for icon, otherwise icons will be connected to others in the
     * legend
     */
    public static int offset = 1;

    /** the current renderer object */
    private static GTRenderer renderer = new StreamingRenderer();
    private static StyleBuilder styleBuilder = new StyleBuilder();
    private static StyleCloner styleCloner = new StyleCloner(styleBuilder.getStyleFactory());
//	private static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
    /**
     * An icon cache that contains no more than a specified number of lastly
     * accessed entries
     */
    private static final int ICON_CACHE_SIZE = 30;
    private static Map<IconDescriptor, Icon> iconCache = new LinkedHashMap<IconDescriptor, Icon>(16, 0.75f, true) {
            /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

			protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > ICON_CACHE_SIZE;
            }
        };

    private static FeatureType fFac;

    // Static initialization block
    static {
        // renderer = new Java2DRenderer();
        renderer.setJava2DHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ));
        try {
        	FeatureTypeBuilder build = FeatureTypeBuilder.newInstance("testGeometry");
        	AttributeType at = AttributeTypeFactory.newAttributeType("testGeometry", Geometry.class);
        	build.addType(at);
        	fFac = build.getFeatureType();
        } catch (SchemaException se) {
            throw new RuntimeException(se);
        }
    }

    private LegendIconMaker() {
    }

    public static Icon makeLegendIcon(int iconWidth, Color background,
        Rule rule) {
        return makeLegendIcon(iconWidth, background, rule.getSymbolizers());
    }

    public static Icon makeLegendIcon(int iconWidth, Rule rule) {
        return makeLegendIcon(iconWidth, new Color(0, 0, 0, 0), rule);
    }

    public static Icon makeLegendIcon(int iconWidth, Color background,
        Symbolizer[] syms) {
        return makeLegendIcon(iconWidth, iconWidth, background, syms, true);
    }

    public static Icon makeLegendIcon(int iconWidth, int iconHeight,
        Color background, Symbolizer[] syms, boolean cacheIcon) {
        IconDescriptor descriptor = new IconDescriptor(iconWidth, iconHeight,
                background, syms);
        Icon icon = (Icon) iconCache.get(descriptor);

        if (icon == null) {
            icon = reallyMakeLegendIcon(iconWidth, iconHeight, background, syms);
            if(cacheIcon) iconCache.put(descriptor, icon);
        }

        return icon;
    }

//    private static Icon reallyMakeLegendIcon(int iconWidth, int iconHeight,
//            Color background, Symbolizer[] symbolizers, Feature sample) {

    private static Icon reallyMakeLegendIcon(int iconWidth, int iconHeight,
        Color background, Symbolizer[] symbolizers) {
        FeatureCollection fc = FeatureCollections.newCollection();
        
        Symbolizer[] syms = symbolizers;
        for (int i = 0; i < symbolizers.length; i++) {
            syms[i] = styleCloner.clone(syms[i]);
            if(syms[i] instanceof PolygonSymbolizer) {
                PolygonSymbolizer ps = (PolygonSymbolizer) syms[i];
                ps.setGeometryPropertyName(null);
            } if (syms[i] instanceof PointSymbolizer) {
                PointSymbolizer ps = (PointSymbolizer) syms[i];
                ps.setGeometryPropertyName(null);
            } if (syms[i] instanceof LineSymbolizer) {
                LineSymbolizer ls = (LineSymbolizer) syms[i];
                ls.setGeometryPropertyName(null);
            }
        }

        for (int i = 0; i < syms.length; i++) {
            Feature feature = null;

            if (syms[i] instanceof PolygonSymbolizer) {
//                Number lineWidth = new Integer(0);
//                Stroke stroke = ((PolygonSymbolizer) syms[i]).getStroke();

//                if ((stroke != null) && (stroke.getWidth() != null)) {
//                    lineWidth = (Number) stroke.getWidth().evaluate(sample);
//                }

                Coordinate[] c = new Coordinate[5];
//                double marginForLineWidth = lineWidth.intValue() / 2.0d;
                double marginForLineWidth = 1;
                c[0] = new Coordinate(offset + marginForLineWidth,
                        offset + marginForLineWidth);
                c[1] = new Coordinate(iconWidth - offset - marginForLineWidth,
                        offset + marginForLineWidth);
                c[2] = new Coordinate(iconWidth - offset - marginForLineWidth,
                        iconHeight - offset - marginForLineWidth);
                c[3] = new Coordinate(offset + marginForLineWidth,
                        iconHeight - offset - marginForLineWidth);
                c[4] = new Coordinate(offset + marginForLineWidth,
                        offset + marginForLineWidth);

                com.vividsolutions.jts.geom.LinearRing r = null;

                try {
                    r = gFac.createLinearRing(c);
                } catch (com.vividsolutions.jts.geom.TopologyException e) {
                    e.printStackTrace();
                }

                Polygon poly = gFac.createPolygon(r, null);
                Object[] attrib = { poly };

                try {
                    feature = fFac.create(attrib);
                } catch (IllegalAttributeException ife) {
                    throw new RuntimeException(ife);
                }

            } else if (syms[i] instanceof LineSymbolizer) {

                Coordinate[] c = new Coordinate[2];
                c[0] = new Coordinate(offset, offset);

                //                c[1] = new Coordinate(offset + (iconWidth * 0.3), offset + (iconWidth * 0.3));
                //                c[2] = new Coordinate(offset + (iconWidth * 0.3), offset + (iconWidth * 0.7));
                //                c[3] = new Coordinate(offset + (iconWidth * 0.7), offset + (iconWidth * 0.7));
                c[1] = new Coordinate(offset + iconWidth, offset + iconHeight);

                LineString line = gFac.createLineString(c);
                Object[] attrib = { line };

                try {
                    feature = fFac.create(attrib);
                } catch (IllegalAttributeException ife) {
                    throw new RuntimeException(ife);
                }

            } else if (syms[i] instanceof PointSymbolizer) {

                Point p = gFac.createPoint(new Coordinate(offset
                            + (iconWidth / 2.0d), offset + (iconHeight / 2.0d)));
                Object[] attrib = { p };

                try {
                    feature = fFac.create(attrib);
                } catch (IllegalAttributeException ife) {
                    throw new RuntimeException(ife);
                }

            }

            if (feature != null) {
                fc.add(feature);
            }
        }

        FeatureTypeStyle fts = styleBuilder.createFeatureTypeStyle("",
                styleBuilder.createRule(syms));
        fts.setFeatureTypeName(fc.features().next().getFeatureType()
                                 .getTypeName());

        Style s = styleBuilder.createStyle();
        s.addFeatureTypeStyle(fts);

        ImageIcon icon = null;

        BufferedImage image = new BufferedImage(iconWidth, iconHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(background);
        graphics.setColor(background);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        renderer.setContext(new DefaultMapContext(
        		new MapLayer[]{new DefaultMapLayer(fc, s)}, 
        		Util.crs));
        renderer.paint(
        		graphics, 
        		new java.awt.Rectangle(0, 0, image.getWidth(), image.getHeight()), 
        		new Envelope(0, iconWidth, 0, iconWidth)
        );
        icon = new ImageIcon(image);

        return icon;
    }

    /**
     * DOCUMENT ME!
     *
     * @param args the command line arguments
     */
    private static class IconDescriptor {
        private int iconHeight;
        private int iconWidth;
        private Color background;
        private Symbolizer[] symbolizers;

        public IconDescriptor(int iconWidth, int iconHeight, Color background,
            Symbolizer[] symbolizers) {
            this.iconWidth = iconWidth;
            this.iconHeight = iconHeight;
            this.background = background;
            this.symbolizers = symbolizers;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            if (!(obj instanceof IconDescriptor)) {
                return false;
            }

            IconDescriptor other = (IconDescriptor) obj;

            if ((other.iconWidth != iconWidth)
                    || (other.iconHeight != iconHeight)) {
                return false;
            }

            if (!((background == null && other.background == null) || other.background.equals(background))) {
                return false;
            }

            if (((symbolizers == null) && (other.symbolizers != null))
                    || ((symbolizers != null) && (other.symbolizers == null))
                    || (symbolizers.length != other.symbolizers.length)) {
                return false;
            }

            for (int i = 0; i < symbolizers.length; i++) {
                if (symbolizers[i] != null && !symbolizers[i].equals(other.symbolizers[i])) {
                    return false;
                }
            }

            return true;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return ((((((((17 + symbolizersHashCode()) * 37) + iconWidth) * 37)
            + iconHeight) * 37) + (background != null ? background.hashCode() : 0)) * 37);
        }
        
        private int symbolizersHashCode() {
        	int hash = 17;
        	for(int i = 0; i < symbolizers.length; i++) {
        		if (symbolizers[i] != null) hash = (hash + symbolizers[i].hashCode()) * 37;  
        	}
        	return hash;
        }
    }
}
