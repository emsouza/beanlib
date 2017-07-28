/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.beanlib.spi;

import java.util.Map;

import net.sf.beanlib.spi.replicator.ArrayReplicatorSpi;
import net.sf.beanlib.spi.replicator.BeanReplicatorSpi;
import net.sf.beanlib.spi.replicator.BlobReplicatorSpi;
import net.sf.beanlib.spi.replicator.CalendarReplicatorSpi;
import net.sf.beanlib.spi.replicator.CollectionReplicatorSpi;
import net.sf.beanlib.spi.replicator.DateReplicatorSpi;
import net.sf.beanlib.spi.replicator.ImmutableReplicatorSpi;
import net.sf.beanlib.spi.replicator.MapReplicatorSpi;

/**
 * Bean Transformer SPI.
 *
 * @author Joe D. Velopar
 */
public interface BeanTransformerSpi extends Transformable, BeanPopulatorBaseSpi {

    /**
     * Bean Transformer Factory SPI.
     *
     * @author Joe D. Velopar
     */
    public static interface Factory {

        /**
         * Returns a bean transformer, given a bean populator factory.
         */
        BeanTransformerSpi newBeanTransformer(BeanPopulatorSpi.Factory beanPopulatorFactory);
    }

    /**
     * Initializes with a custom transformer factory.
     */
    BeanTransformerSpi initCustomTransformerFactory(CustomBeanTransformerSpi.Factory customTransformerFactory);

    /**
     * Returns the customer bean transformer, if any. See
     * {@link BeanTransformerSpi#initCustomTransformerFactory(net.sf.beanlib.spi.CustomBeanTransformerSpi.Factory)}
     */
    CustomBeanTransformerSpi getCustomBeanTransformer();

    /**
     * Reset the internal identity maps used to contain objects that have been replicated. (Warning: Don't invoke this
     * method unless you really know what you are doing.)
     */
    void reset();

    /**
     * Returns a map of those (from-to) objects that have been replicated. This map is internally used to resolve object
     * identities and circular references in the object graph.
     */
    <K, V> Map<K, V> getClonedMap();

    // Configure the replicator factories for some major/common types
    /** Used to initialize the replicator factory for immutables. */
    BeanTransformerSpi initImmutableReplicatableFactory(ImmutableReplicatorSpi.Factory immutableReplicatableFactory);

    /** Used to initialize the replicator factory for collections. */
    BeanTransformerSpi initCollectionReplicatableFactory(CollectionReplicatorSpi.Factory collectionReplicatableFactory);

    /** Used to initialize the replicator factory for maps. */
    BeanTransformerSpi initMapReplicatableFactory(MapReplicatorSpi.Factory mapReplicatableFactory);

    /** Used to initialize the replicator factory for arrays. */
    BeanTransformerSpi initArrayReplicatableFactory(ArrayReplicatorSpi.Factory arrayReplicatableFactory);

    /** Used to initialize the replicator factory for blob's. */
    BeanTransformerSpi initBlobReplicatableFactory(BlobReplicatorSpi.Factory blobReplicatableFactory);

    /** Used to initialize the replicator factory for dates. */
    BeanTransformerSpi initDateReplicatableFactory(DateReplicatorSpi.Factory dateReplicatableFactory);

    /** Used to initialize the replicator factory for calendars. */
    BeanTransformerSpi initCalendarReplicatableFactory(CalendarReplicatorSpi.Factory calendarReplicatableFactory);

    /** Used to initialize the replicator factory for JavaBean's. */
    BeanTransformerSpi initBeanReplicatableFactory(BeanReplicatorSpi.Factory beanReplicatableFactory);

    /** Returns the current replicator for immutables. */
    ImmutableReplicatorSpi getImmutableReplicatable();

    /** Returns the current replicator for collections. */
    CollectionReplicatorSpi getCollectionReplicatable();

    /** Returns the current replicator for maps. */
    MapReplicatorSpi getMapReplicatable();

    /** Returns the current replicator for array. */
    ArrayReplicatorSpi getArrayReplicatable();

    /** Returns the current replicator for blobs. */
    BlobReplicatorSpi getBlobReplicatable();

    /** Returns the current replicator for dates. */
    DateReplicatorSpi getDateReplicatable();

    /** Returns the current replicator for calendars. */
    CalendarReplicatorSpi getCalendarReplicatable();

    /** Returns the current replicator for JavaBeans. */
    BeanReplicatorSpi getBeanReplicatable();

    /**
     * Returns the current bean populator factory, which is used to create a bean populator which can then be used to
     * determine whether a specific JavaBean property should be propagated from a source bean to a target bean.
     */
    BeanPopulatorSpi.Factory getBeanPopulatorSpiFactory();

    /**
     * Returns all the configuration options as a single configuration object.
     */
    @Override
    BeanPopulatorBaseConfig getBeanPopulatorBaseConfig();

    // -------------------------- BeanPopulatorBaseSpi --------------------------

    // Overrides here for co-variant return type.
    // Don't invoke this method, except from within the BeanPopulatorSpi implementation class.
    @Override
    BeanTransformerSpi initPropertyFilter(PropertyFilter propertyFilter);

    // Overrides here for co-variant return type.
    // Don't invoke this method, except from within the BeanPopulatorSpi implementation class.
    @Override
    BeanTransformerSpi initDetailedPropertyFilter(DetailedPropertyFilter detailedPropertyFilter);

    // Overrides here for co-variant return type.
    // Don't invoke this method, except from within the BeanPopulatorSpi implementation class.
    @Override
    BeanTransformerSpi initBeanSourceHandler(BeanSourceHandler beanSourceHandler);

    // Overrides here for co-variant return type.
    // Don't invoke this method, except from within the BeanPopulatorSpi implementation class.
    @Override
    BeanTransformerSpi initReaderMethodFinder(BeanMethodFinder readerMethodFinder);

    // Overrides here for co-variant return type.
    // Don't invoke this method, except from within the BeanPopulatorSpi implementation class.
    @Override
    BeanTransformerSpi initSetterMethodCollector(BeanMethodCollector setterMethodCollector);

    // Overrides here for co-variant return type.
    // Don't invoke this method, except from within the BeanPopulatorSpi implementation class.
    @Override
    BeanTransformerSpi initBeanPopulationExceptionHandler(BeanPopulationExceptionHandler beanPopulationExceptionHandler);

    // Overrides here for co-variant return type.
    // Don't invoke this method, except from within the BeanPopulatorSpi implementation class.
    @Override
    BeanTransformerSpi initDebug(boolean debug);

    // Overrides here for co-variant return type.
    // Don't invoke this method, except from within the BeanPopulatorSpi implementation class.
    @Override
    BeanTransformerSpi initBeanPopulatorBaseConfig(BeanPopulatorBaseConfig baseConfig);
}
