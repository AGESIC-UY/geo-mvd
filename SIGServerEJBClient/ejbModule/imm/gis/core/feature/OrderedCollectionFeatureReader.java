package imm.gis.core.feature;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.geotools.data.FeatureReader;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

public class OrderedCollectionFeatureReader implements FeatureReader {
    private Iterator<Feature> features;
    private FeatureType type;
    private boolean closed = false;

    public OrderedCollectionFeatureReader(List<Feature> featuresArg,
            FeatureType typeArg) {
            assert !featuresArg.isEmpty();
            this.features = featuresArg.iterator();
            this.type = typeArg;
        }

    public void close() throws IOException {
        closed = true;
        
        features = null;
	}

	public FeatureType getFeatureType() {
        return type;
	}

	public boolean hasNext() throws IOException {
        return features.hasNext() && !closed;
	}

	public Feature next() throws IOException, IllegalAttributeException,
			NoSuchElementException {
        if (closed) {
            throw new NoSuchElementException("Reader has been closed");
        }

        Feature f = features.next();

        return f;
	}

}
