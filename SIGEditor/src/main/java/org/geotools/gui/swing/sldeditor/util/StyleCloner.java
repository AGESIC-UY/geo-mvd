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
package org.geotools.gui.swing.sldeditor.util;

import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Font;
import org.geotools.styling.Graphic;
import org.geotools.styling.Halo;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;


/*
 * Created on 25/06/2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * A utility class to clone Style objects, ideally it'd better to be inside those Style class
 * itself. follow Sean's work, made another half of this class, have a look on the clone method
 * parameters with checking the classes in the styling packge you will get a clue, what have done,
 * how deep the clone goes, and what left  untouched. For your interest, adding your clone method
 * to clone those haven't been done and also please modify the places where other clone methods
 * use them
 *
 * @author Sean Geoghegan
 * @author Jianhui Jin
 */
public class StyleCloner {
    private StyleFactory styleFactory;

    /**
     * DOCUMENT ME!
     *
     * @param styleFactory
     */
    public StyleCloner(StyleFactory styleFactory) {
        this.styleFactory = styleFactory;
    }

    /**
     * DOCUMENT ME!
     *
     * @param toClone
     *
     * @return
     */
    public Style clone(Style toClone) {
        Style newStyle = styleFactory.createStyle();
        newStyle.setAbstract((String) toClone.getAbstract());
        newStyle.setDefault(toClone.isDefault());
        newStyle.setName(toClone.getName());
        newStyle.setTitle(toClone.getTitle());
        newStyle.setFeatureTypeStyles(clone(toClone.getFeatureTypeStyles()));

        return newStyle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param toClone
     *
     * @return
     */
    public FeatureTypeStyle[] clone(FeatureTypeStyle[] toClone) {
        FeatureTypeStyle[] clones = new FeatureTypeStyle[toClone.length];

        for (int i = 0; i < toClone.length; i++) {
            clones[i] = clone(toClone[i]);
        }

        return clones;
    }

    /**
     * DOCUMENT ME!
     *
     * @param toClone
     *
     * @return
     */
    public FeatureTypeStyle clone(FeatureTypeStyle toClone) {
        FeatureTypeStyle clone = styleFactory.createFeatureTypeStyle(clone(toClone.getRules()));
        clone.setAbstract(toClone.getAbstract());
        clone.setName(toClone.getName());
        clone.setFeatureTypeName(toClone.getFeatureTypeName());
        clone.setSemanticTypeIdentifiers(toClone.getSemanticTypeIdentifiers());
        clone.setTitle(toClone.getTitle());

        return clone;
    }

    /**
     * DOCUMENT ME!
     *
     * @param toClone
     *
     * @return
     */
    public Rule[] clone(Rule[] toClone) {
        Rule[] clones = new Rule[toClone.length];

        for (int i = 0; i < toClone.length; i++) {
            clones[i] = clone(toClone[i]);
        }

        return clones;
    }

    /**
     * DOCUMENT ME!
     *
     * @param toClone
     *
     * @return
     */
    public Rule clone(Rule toClone) {
        Rule clone = styleFactory.createRule();
        clone.setFilter(toClone.getFilter());
        clone.setAbstract(toClone.getAbstract());
        clone.setName(toClone.getName());
        clone.setTitle(toClone.getTitle());
        clone.setIsElseFilter(toClone.hasElseFilter());
        clone.setLegendGraphic(toClone.getLegendGraphic());
        clone.setMaxScaleDenominator(toClone.getMaxScaleDenominator());
        clone.setMinScaleDenominator(toClone.getMinScaleDenominator());
        clone.setSymbolizers(clone(toClone.getSymbolizers()));

        return clone;
    }

    /**
     * DOCUMENT ME!
     *
     * @param toClone
     *
     * @return
     */
    public Symbolizer[] clone(Symbolizer[] toClone) {
        Symbolizer[] clones = new Symbolizer[toClone.length];

        for (int i = 0; i < clones.length; i++) {
            clones[i] = clone(toClone[i]);
        }

        return clones;
    }

    public Symbolizer clone(Symbolizer s) {
        Symbolizer newS = null;

        if (s != null) {
            if (s instanceof PointSymbolizer) {
                newS = clone((PointSymbolizer) s);
            } else if (s instanceof LineSymbolizer) {
                newS = clone((LineSymbolizer) s);
            } else if (s instanceof PolygonSymbolizer) {
                newS = clone((PolygonSymbolizer) s);
            } else if (s instanceof TextSymbolizer){
                newS = clone((TextSymbolizer) s);            	
            }
        }

        return newS;
    }

    public PointSymbolizer clone(PointSymbolizer p) {
        PointSymbolizer newP = null;

        if (p != null) {
            newP = styleFactory.getDefaultPointSymbolizer();
            newP.setGeometryPropertyName(p.getGeometryPropertyName());
            newP.setGraphic(clone(p.getGraphic()));
        }

        return newP;
    }

    public LineSymbolizer clone(LineSymbolizer l) {
        LineSymbolizer newL = null;

        if (l != null) {
            newL = styleFactory.getDefaultLineSymbolizer();
            newL.setGeometryPropertyName(l.getGeometryPropertyName());
            newL.setStroke(clone(l.getStroke()));
        }

        return newL;
    }

    public PolygonSymbolizer clone(PolygonSymbolizer p) {
        PolygonSymbolizer newP = null;

        if (p != null) {
            newP = styleFactory.getDefaultPolygonSymbolizer();
            newP.setGeometryPropertyName(p.getGeometryPropertyName());
            newP.setStroke(clone(p.getStroke()));
            newP.setFill(clone(p.getFill()));
        }

        return newP;
    }

    public TextSymbolizer clone(TextSymbolizer p) {
        TextSymbolizer newP = null;

        if (p != null) {
            newP = styleFactory.getDefaultTextSymbolizer();
            newP.setFonts(clone(p.getFonts()));
            newP.setLabel(p.getLabel());
            newP.setPlacement(p.getPlacement());
            newP.setGeometryPropertyName(p.getGeometryPropertyName());
            newP.setHalo(clone(p.getHalo()));
            newP.setFill(clone(p.getFill()));
            
            if (p.getOptions() != null ){
                String optionName;
                for (java.util.Iterator it = p.getOptions().keySet().iterator(); it.hasNext();){
                	optionName = (String)it.next();
                	newP.addToOptions(optionName, p.getOption(optionName));
                }            	
            }
        }

        return newP;
    }

    public Halo clone(Halo halo){
    	Halo res = null;
    	
    	if (res != null){
    		res = styleFactory.createHalo(halo.getFill(), halo.getRadius());
    	}
    	
    	return res;
    }
    
    public Font[] clone(Font[] fonts){
    	Font[] res = null;
    	
    	if (fonts != null){
        	res = new Font[fonts.length];
        	Font font;
        	for (int i = 0; i < fonts.length; i++){
        		font = fonts[i];
        		res[i] = styleFactory.createFont(font.getFontFamily(), font.getFontStyle(), font.getFontWeight(), font.getFontSize());
        	}
    	}
    	
    	return res;
    }
    
    public Graphic clone(Graphic g) {
        Graphic newG = null;

        if (g != null) {
            newG = styleFactory.getDefaultGraphic();
            newG.setGeometryPropertyName(g.getGeometryPropertyName());

            if (g.getExternalGraphics() != null) {
                newG.setExternalGraphics(g.getExternalGraphics());
            }

            if (g.getMarks() != null) {
                newG.setMarks(clone(g.getMarks()));
            }

            newG.setOpacity(g.getOpacity());
            newG.setRotation(g.getRotation());
            newG.setSize(g.getSize());

            // newG.setSymbols(g.getSymbols());
        }

        return newG;
    }

    public Mark[] clone(Mark[] oldM) {
        Mark[] newM;

        if (oldM == null) {
            newM = null;
        } else {
            newM = new Mark[oldM.length];

            for (int i = 0; i < newM.length; i++) {
                newM[i] = clone(oldM[i]);
            }
        }

        return newM;
    }

    public Mark clone(Mark oldM) {
        Mark newM = null;

        if (oldM != null) {
            newM = styleFactory.getDefaultMark();
            newM.setFill(oldM.getFill());
            newM.setRotation(oldM.getRotation());
            newM.setSize(oldM.getSize());
            newM.setStroke(oldM.getStroke());
            newM.setWellKnownName(oldM.getWellKnownName());
        }

        return newM;
    }

    public Fill clone(Fill oldF) {
        Fill newF = null;

        if (oldF != null) {
            newF = styleFactory.getDefaultFill();
            newF.setBackgroundColor(oldF.getBackgroundColor());
            newF.setColor(oldF.getColor());
            newF.setGraphicFill(clone(oldF.getGraphicFill()));
            newF.setOpacity(oldF.getOpacity());
        }

        return newF;
    }

    public Stroke clone(Stroke oldS) {
        Stroke newS = null;

        if (oldS != null) {
            newS = styleFactory.getDefaultStroke();
            newS.setColor(oldS.getColor());
            newS.setDashArray(clone(oldS.getDashArray()));
            newS.setDashOffset(oldS.getDashOffset());
            newS.setGraphicFill(clone(oldS.getGraphicFill()));
            newS.setGraphicStroke(clone(oldS.getGraphicStroke()));
            newS.setLineCap(oldS.getLineCap());
            newS.setLineJoin(oldS.getLineJoin());
            newS.setOpacity(oldS.getOpacity());
            newS.setWidth(oldS.getWidth());
        }

        return newS;
    }

    public float[] clone(float[] d) {
        float[] newD;

        if (d == null) {
            newD = null;
        } else {
            newD = new float[d.length];

            for (int i = 0; i < newD.length; i++) {
                newD[i] = d[i];
            }
        }

        return newD;
    }
}
