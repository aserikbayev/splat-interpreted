package splat.executor;

import splat.parser.elements.Type;

public class IntValue extends Value {
    public IntValue(Object value) {
        super(value, Type.Integer());
    }
}
