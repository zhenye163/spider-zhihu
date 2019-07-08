package com.netopstec.spiderzhihu.bloomfilter;

import java.util.BitSet;

/**
 * 布隆过滤器---每个知乎用户有唯一的url_token
 * @author zhenye 2019/7/8
 */
public class BloomFilter {

    private static final int DEFAULT_SIZE = 2 << 24;
    private static final int[] SEEDS = new int[]{ 2, 7, 11, 17, 23, 29, 31, 37 };

    private BitSet bitSet = new BitSet(DEFAULT_SIZE);
    private SimpleHash[] hashFunList = new SimpleHash[SEEDS.length];

    public BloomFilter() {
        for (int i = 0; i < SEEDS.length; i++) {
            hashFunList[i] = new SimpleHash(DEFAULT_SIZE, SEEDS[i]);
        }
    }

    public boolean contains(String value) {
        if (value == null) {
            return false;
        }
        Boolean result = true;
        for (SimpleHash hashFun : hashFunList) {
            result = result && bitSet.get(hashFun.hash(value));
        }
        return result;
    }

    public void add (String value) {
        for (SimpleHash hashFun : hashFunList) {
            bitSet.set(hashFun.hash(value), true);
        }
    }

    private class SimpleHash {
        private int cap;
        private int seed;
        SimpleHash(int cap, int seed) {
            this.cap = cap;
            this.seed = seed;
        }

        int hash(String value) {
            int result = 0;
            int len = value.length();
            for (int i = 0; i < len; i++) {
                result = seed * result + value.charAt(i);
            }
            return (cap - 1) & result;
        }
    }
}
