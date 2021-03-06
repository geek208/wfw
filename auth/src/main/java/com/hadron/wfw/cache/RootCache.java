package com.hadron.wfw.cache;

/**
 * 
 * @author xuychao
 * @date 2022年3月15日
 * @classname RootCache.java
 * @email xuychao@163.com  git@github.com:geek208/wfw.git
 * @param <K>
 * @param <V>
 */
public abstract class RootCache<K,V> implements CacheI<K,V> {
    @Override
    public V getCache(K key) {
        throw  new UnsupportedOperationException();
    }

    @Override
    public void setCache(K key, V value) {
        throw  new UnsupportedOperationException();
    }

    @Override
    public void rmCache(K key) {
        throw  new UnsupportedOperationException();
    }

    @Override
    public boolean containKey(K key) {
        throw  new UnsupportedOperationException();
    }
}
