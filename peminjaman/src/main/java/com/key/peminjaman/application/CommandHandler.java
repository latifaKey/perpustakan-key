package com.key.peminjaman.application;

/**
 * Handler interface for executing Commands
 * 
 * @param <C> Command type
 * @param <R> Return type
 */
public interface CommandHandler<C extends Command<R>, R> {
    R handle(C command);
}
