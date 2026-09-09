/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - EventHashMap.java
 */

package net.buildtheearth.terrabungee.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class EventHashMap<K, V> extends HashMap<K, V> {

    private Consumer<HashMap<K, V>> consumer;

    public EventHashMap() {
    }

    public EventHashMap(Map<K, V> map) {
        super(map);
    }

    @Override
    public V put(K key, V value) {
        V obj = super.put(key, value);
        event();
        return obj;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        event();
    }

    @Override
    public V remove(Object key) {
        V obj = super.remove(key);
        event();
        return obj;
    }

    @Override
    public boolean remove(Object key, Object value) {
        boolean obj = super.remove(key, value);
        event();
        return obj;
    }

    @Override
    public void clear() {
        super.clear();
        event();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V obj = super.putIfAbsent(key, value);
        event();
        return obj;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        boolean obj = super.replace(key, oldValue, newValue);
        event();
        return obj;
    }

    @Override
    public V replace(K key, V value) {
        V obj = super.replace(key, value);
        event();
        return obj;
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        super.replaceAll(function);
        event();
    }

    public void onEditEvent(Consumer<HashMap<K, V>> consumer) {
        this.consumer = consumer;
    }

    private void event() {
        if (consumer != null) {
            consumer.accept(this);
        }
    }
}
