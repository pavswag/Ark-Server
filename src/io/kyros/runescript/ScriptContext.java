package io.kyros.runescript;

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.ConditionalAction;

import java.util.*;

public class ScriptContext {
    private final Player player;
    private final List<Action> actions = new ArrayList<>();
    private final Map<String, Integer> intVariables = new HashMap<>();
    private boolean paused = false;
    private boolean lastConditionResult = false;
    private boolean inConditionBlock = false;

    private int currentActionIndex = 0;
    private final Deque<Integer> loopStartIndices = new ArrayDeque<>();
    private final Deque<Integer> loopEndIndices = new ArrayDeque<>();



    public ScriptContext(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public Action getNextAction() {
        if (currentActionIndex < actions.size()) {
            return actions.get(currentActionIndex++);
        }
        return null;
    }

    public boolean hasNextAction() {
        return currentActionIndex < actions.size();
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public void resume() {
        setPaused(false);
        ScriptInterpreter.resumeScript(this);
    }

    public void pause() {
        setPaused(true);
    }

    public void setIntVariable(String name, int value) {
        intVariables.put(name, value);
    }

    public int getIntVariable(String name) {
        return intVariables.getOrDefault(name, 0);
    }

    public void interpret(List<String> scriptLines) {
        ScriptInterpreter interpreter = new ScriptInterpreter();
        interpreter.interpret(scriptLines, this);
    }

    public void skipToNextCondition() {
        while (hasNextAction()) {
            Action action = getNextAction();
            if (action instanceof ConditionalAction) {
                ConditionalAction condAction = (ConditionalAction) action;
                if ("if".equals(condAction.getType()) || "else if".equals(condAction.getType()) || "else".equals(condAction.getType())) {
                    currentActionIndex--; // Move back to reprocess this action
                    break;
                }
            }
        }
    }

    public void skipToEndOfLoop() {
        if (!loopEndIndices.isEmpty()) {
            int endIndex = loopEndIndices.pop();
            while (currentActionIndex < endIndex && hasNextAction()) {
                getNextAction();
            }
        }
    }

    public void loopBackToStart() {
        if (!loopStartIndices.isEmpty()) {
            int startIndex = loopStartIndices.peek();
            currentActionIndex = startIndex;
        }
    }

    public void addLoopStartIndex(int index) {
        loopStartIndices.push(index);
    }

    public void addLoopEndIndex(int index) {
        loopEndIndices.push(index);
    }

    public boolean getLastConditionResult() {
        return lastConditionResult;
    }

    public void setLastConditionResult(boolean result) {
        this.lastConditionResult = result;
    }

    public boolean isInConditionBlock() {
        return inConditionBlock;
    }

    public void setInConditionBlock(boolean inConditionBlock) {
        this.inConditionBlock = inConditionBlock;
    }

    public boolean evaluateCondition(String condition) {
        condition = condition.replace("(", "").replace(")", "").trim();

        if ("true".equals(condition)) {
            return true;
        } else if ("false".equals(condition)) {
            return false;
        }

        String[] andConditions = condition.split("&");
        boolean andResult = true;

        for (String andCondition : andConditions) {
            String[] orConditions = andCondition.split("\\|");
            boolean orResult = false;

            for (String orCondition : orConditions) {
                orCondition = orCondition.trim();
                String[] parts = orCondition.split(" ");

                if (parts.length < 3) {
                    throw new IllegalArgumentException("Invalid condition: " + condition);
                }

                int left = getIntVariable(parts[0]);
                String operator = parts[1];
                int right = Integer.parseInt(parts[2]);

                boolean result;
                switch (operator) {
                    case "<":
                        result = left < right;
                        break;
                    case ">":
                        result = left > right;
                        break;
                    case "<=":
                        result = left <= right;
                        break;
                    case ">=":
                        result = left >= right;
                        break;
                    case "==":
                        result = left == right;
                        break;
                    case "!=":
                        result = left != right;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid operator: " + operator);
                }

                orResult = orResult || result;
            }

            andResult = andResult && orResult;
        }

        return andResult;
    }


    public int getCurrentActionIndex() {
        return currentActionIndex;
    }
}
