package org.noear.solon.data.sqlink.base.expression;

import java.util.Objects;

public class AsName {
    private String name;

    public AsName() {
    }

    public AsName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsName asName = (AsName) o;
        return Objects.equals(name, asName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
