package org.noear.solon.data.sqlink.plugin.aot.data;

public class NormalClassData extends ClassData
{
    private String name;

    protected boolean allPublicConstructors = true;
    protected boolean allDeclaredFields = true;
    protected boolean allPublicMethods = true;

    public NormalClassData(String name)
    {
        super(name);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isAllPublicConstructors()
    {
        return allPublicConstructors;
    }

    public void setAllPublicConstructors(boolean allPublicConstructors)
    {
        this.allPublicConstructors = allPublicConstructors;
    }

    public boolean isAllDeclaredFields()
    {
        return allDeclaredFields;
    }

    public void setAllDeclaredFields(boolean allDeclaredFields)
    {
        this.allDeclaredFields = allDeclaredFields;
    }

    public boolean isAllPublicMethods()
    {
        return allPublicMethods;
    }

    public void setAllPublicMethods(boolean allPublicMethods)
    {
        this.allPublicMethods = allPublicMethods;
    }
}
