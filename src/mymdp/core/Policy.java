package mymdp.core;

public interface Policy {
    Action getActionFor(State state);

    void updatePolicy(State state, Action policy);
}
