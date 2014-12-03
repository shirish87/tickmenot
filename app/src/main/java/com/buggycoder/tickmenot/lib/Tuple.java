package com.buggycoder.tickmenot.lib;

public class Tuple<T, U> {
    public final T _1;
    public final U _2;

    public Tuple(T _1, U _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public boolean isEmpty() {
        return (_1 == null && _2 == null);
    }

    public boolean isNotNull() {
        return (_1 != null && _2 != null);
    }
}
