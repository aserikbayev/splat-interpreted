package splat.executor;

import splat.parser.elements.Type;

public abstract class Value {

    protected Object value;
    protected Type type;

    public Value(Object value, Type type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }
}
