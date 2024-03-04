package splat.parser.elements;

import java.util.Objects;

public class Type {
    private final String name;

    protected Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Type Boolean() {
        return new BooleanType();
    }

    public static Type String()
    {
        return new StringType();
    }

    public static Type Integer()
    {
        return new IntegerType();
    }

    public static Type Void()
    {
        return new VoidType();
    }

    public static final String String = "String";
    public static final String Boolean = "Boolean";
    public static final String Integer = "Integer";
    public static final String Void = "void";

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return Objects.equals(name, type.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
