package mymdp.core;

public interface Action {
    boolean isApplyableTo(State state);
}
