/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.priorityreservation.service;

/**
 *
 * @author rodol
 */


import org.springframework.stereotype.Service;
import java.util.Stack;

@Service
public class UndoManager {
    private final Stack<Runnable> actionStack = new Stack<>();

    public void registerAction(Runnable action) {
        if (action != null) {
            actionStack.push(action);
        }
    }

    public void undoLastAction() {
        if (!actionStack.isEmpty()) {
            actionStack.pop().run();
        }
    }

    public void clearHistory() {
        actionStack.clear();
    }
}