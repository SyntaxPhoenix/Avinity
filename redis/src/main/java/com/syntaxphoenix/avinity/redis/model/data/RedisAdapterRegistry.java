package com.syntaxphoenix.avinity.redis.model.data;

import java.util.function.Function;

import com.syntaxphoenix.avinity.redis.model.RModel;
import com.syntaxphoenix.syntaxapi.data.DataAdapter;
import com.syntaxphoenix.syntaxapi.data.DataAdapterRegistry;

public final class RedisAdapterRegistry extends DataAdapterRegistry<RModel> {

    public static final RedisAdapterRegistry GLOBAL = new RedisAdapterRegistry();

    private RedisAdapterRegistry() {}

    public Object extract(RModel base) {
        return extract(base.getType().getOwningClass(), base);
    }

    @Override
    protected <I, R extends RModel> RedisAdapter<I, R> createAdapter(Class<I> primitiveType, Class<R> resultType, Function<I, R> builder,
        Function<R, I> extractor) {
        return new RedisAdapter<>(primitiveType, resultType, builder, extractor);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <I, R extends RModel> DataAdapter<I, R, RModel> buildAdapter(Class<?> clazz) {
        return (DataAdapter<I, R, RModel>) RedisAdapter.createAdapter(clazz);
    }

}
