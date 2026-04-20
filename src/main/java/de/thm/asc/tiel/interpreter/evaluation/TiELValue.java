package de.thm.asc.tiel.interpreter.evaluation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents all runtime values in the interpreter.
 */
public sealed interface TiELValue permits TiELCallable, TiELValue.TArray, TiELValue.TBoolean, TiELValue.TNil,
        TiELValue.TNumber, TiELValue.TString {

    /**
     * Represents an array value containing a list of elements.
     */
    record TArray(List<TiELValue> value) implements TiELValue {

        @Override
        public String toString() {
            var inner = value.stream().map(Object::toString).collect(Collectors.joining(", "));

            return "[%s]".formatted(inner);
        }
    }

    /**
     * Represents a numeric (double-precision) value.
     */
    record TNumber(double value) implements TiELValue {

        @Override
        public String toString() {
            var text = String.valueOf(value);
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
    }

    /**
     * Represents a string value.
     */
    record TString(String value) implements TiELValue {

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Represents a boolean value.
     */
    record TBoolean(boolean value) implements TiELValue {

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    /**
     * Represents the absence of a value.
     */
    record TNil() implements TiELValue {

        @Override
        public String toString() {
            return "nil";
        }
    }

    TiELValue TRUE = new TBoolean(true);
    TiELValue FALSE = new TBoolean(false);
    TiELValue NIL = new TNil();
}
