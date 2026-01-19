package com.key.peminjaman.dto;

/**
 * Result object for Command operations
 * Commands should return simple result, not full entity
 */
public class CommandResult {
    private final Long id;
    private final boolean success;
    private final String message;

    private CommandResult(Long id, boolean success, String message) {
        this.id = id;
        this.success = success;
        this.message = message;
    }

    public static CommandResult success(Long id) {
        return new CommandResult(id, true, "Operation completed successfully");
    }

    public static CommandResult success(Long id, String message) {
        return new CommandResult(id, true, message);
    }

    public static CommandResult failure(String message) {
        return new CommandResult(null, false, message);
    }

    public Long getId() {
        return id;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
