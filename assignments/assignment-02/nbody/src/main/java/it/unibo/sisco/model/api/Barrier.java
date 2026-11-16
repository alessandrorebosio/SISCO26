package it.unibo.sisco.model.api;

public interface Barrier {
    void hitAndWaitAll() throws InterruptedException;
}
