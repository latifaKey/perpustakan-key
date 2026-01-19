package com.key.peminjaman.application;

/**
 * Handler interface for executing Queries
 * 
 * @param <Q> Query type
 * @param <R> Return type
 */
public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
