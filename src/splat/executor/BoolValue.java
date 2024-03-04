package splat.executor;

import splat.parser.elements.Type;

public class BoolValue extends Value
{
    public BoolValue(Object value) {
        super(value, Type.Boolean());
    }
}
