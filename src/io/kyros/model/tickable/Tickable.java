package io.kyros.model.tickable;

public interface Tickable<T> {

    void tick(TickableContainer<T> container, T t);

}
