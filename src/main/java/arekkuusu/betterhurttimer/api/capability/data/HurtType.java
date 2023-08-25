package arekkuusu.betterhurttimer.api.capability.data;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class HurtType implements CharSequence {

    public final CharSequence type;
    public final Pattern pattern;

    public HurtType(CharSequence type) {
        this.type = type;
        this.pattern = Pattern.compile(type.toString());
    }

    @Override
    public int length() {
        return type.length();
    }

    @Override
    public char charAt(int index) {
        return type.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return type.subSequence(start, end);
    }

    @Override
    @Nonnull
    public String toString() {
        return type.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharSequence)) return false;
        CharSequence charSequence = (CharSequence) o;
        return type.equals(o) || pattern.matcher(charSequence).matches();
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
