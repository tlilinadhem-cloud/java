package tn.isg.economics.dashboard.controller;

/**
 * Command interface for undo/redo functionality (Command pattern).
 */
public interface Command {
    void execute();
    void undo();
    String getDescription();
}
