/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.multimap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.TransformerUtils;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.collection.TransformedCollectionTest;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link TransformedMultiValuedMap}.
 */
public class TransformedMultiValuedMapTest<K, V> extends AbstractMultiValuedMapTest<K, V> {

    @Override
    protected int getIterationBehaviour() {
        return AbstractCollectionTest.UNORDERED;
    }

    @Override
    public MultiValuedMap<K, V> makeObject() {
        return TransformedMultiValuedMap.transformingMap(new ArrayListValuedHashMap<>(),
                TransformerUtils.<K>nopTransformer(), TransformerUtils.<V>nopTransformer());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFactory_Decorate() {
        final MultiValuedMap<K, V> base = new ArrayListValuedHashMap<>();
        base.put((K) "A", (V) "1");
        base.put((K) "B", (V) "2");
        base.put((K) "C", (V) "3");

        final MultiValuedMap<K, V> trans = TransformedMultiValuedMap
                .transformingMap(
                        base,
                        null,
                        (Transformer<? super V, ? extends V>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(3, trans.size());
        assertTrue(trans.get((K) "A").contains("1"));
        assertTrue(trans.get((K) "B").contains("2"));
        assertTrue(trans.get((K) "C").contains("3"));
        trans.put((K) "D", (V) "4");
        assertTrue(trans.get((K) "D").contains(Integer.valueOf(4)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFactory_decorateTransform() {
        final MultiValuedMap<K, V> base = new ArrayListValuedHashMap<>();
        base.put((K) "A", (V) "1");
        base.put((K) "B", (V) "2");
        base.put((K) "C", (V) "3");

        final MultiValuedMap<K, V> trans = TransformedMultiValuedMap
                .transformedMap(
                        base,
                        null,
                        (Transformer<? super V, ? extends V>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(3, trans.size());
        assertTrue(trans.get((K) "A").contains(Integer.valueOf(1)));
        assertTrue(trans.get((K) "B").contains(Integer.valueOf(2)));
        assertTrue(trans.get((K) "C").contains(Integer.valueOf(3)));
        trans.put((K) "D", (V) "4");
        assertTrue(trans.get((K) "D").contains(Integer.valueOf(4)));

        final MultiValuedMap<K, V> baseMap = new ArrayListValuedHashMap<>();
        final MultiValuedMap<K, V> transMap = TransformedMultiValuedMap
                .transformedMap(
                        baseMap,
                        null,
                        (Transformer<? super V, ? extends V>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, transMap.size());
        transMap.put((K) "D", (V) "4");
        assertEquals(1, transMap.size());
        assertTrue(transMap.get((K) "D").contains(Integer.valueOf(4)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testKeyTransformedMap() {
        final Object[] els = { "1", "3", "5", "7", "2", "4", "6" };

        final MultiValuedMap<K, V> map = TransformedMultiValuedMap.transformingMap(
                new ArrayListValuedHashMap<>(),
                (Transformer<? super K, ? extends K>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER,
                null);
        assertEquals(0, map.size());
        for (int i = 0; i < els.length; i++) {
            map.put((K) els[i], (V) els[i]);
            assertEquals(i + 1, map.size());
            assertTrue(map.containsKey(Integer.valueOf((String) els[i])));
            assertFalse(map.containsKey(els[i]));
            assertTrue(map.containsValue(els[i]));
            assertTrue(map.get((K) Integer.valueOf((String) els[i])).contains(els[i]));
        }

        final Collection<V> coll = map.remove(els[0]);
        assertNotNull(coll);
        assertEquals(0, coll.size());
        assertTrue(map.remove(Integer.valueOf((String) els[0])).contains(els[0]));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testValueTransformedMap() {
        final Object[] els = { "1", "3", "5", "7", "2", "4", "6" };

        final MultiValuedMap<K, V> map = TransformedMultiValuedMap.transformingMap(
                new ArrayListValuedHashMap<>(), null,
                (Transformer<? super V, ? extends V>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, map.size());
        for (int i = 0; i < els.length; i++) {
            map.put((K) els[i], (V) els[i]);
            assertEquals(i + 1, map.size());
            assertTrue(map.containsValue(Integer.valueOf((String) els[i])));
            assertFalse(map.containsValue(els[i]));
            assertTrue(map.containsKey(els[i]));
            assertTrue(map.get((K) els[i]).contains(Integer.valueOf((String) els[i])));
        }
        assertTrue(map.remove(els[0]).contains(Integer.valueOf((String) els[0])));
    }

//    void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/TransformedMultiValuedMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/TransformedMultiValuedMap.fullCollection.version4.1.obj");
//    }

}
