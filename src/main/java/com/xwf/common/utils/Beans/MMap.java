package com.xwf.common.utils.Beans;

import com.jfinal.render.JsonRender;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by weifengxu on 2018/8/8.
 */
public class MMap extends JsonRender implements Map {

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean containsKey(Object key) {
        return false;
    }

    public boolean containsValue(Object value) {
        return false;
    }

    public Object get(Object key) {
        return null;
    }

    public Object put(Object key, Object value) {
        return null;
    }

    public Object remove(Object key) {
        return null;
    }

    public void putAll(Map m) {

    }

    public void clear() {

    }

    public Set keySet() {
        return null;
    }

    public Collection values() {
        return null;
    }

    public Set<Entry> entrySet() {
        return null;
    }
}
