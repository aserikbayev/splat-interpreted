package splat.executor;

import splat.parser.elements.Type;

public class StringValue extends Value {
    public StringValue(Object value) {
        super(value, Type.String());
    }
}
