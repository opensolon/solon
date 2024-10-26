package org.noear.solon.data.sqlink.plugin.aot.data;

public class AnonymousClassData extends ClassData
{
    private String name;
    private boolean unsafeAllocated = true;
    private boolean allDeclaredFields = true;
    private boolean allPublicMethods = true;

    public AnonymousClassData(String name)
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

    public boolean isUnsafeAllocated()
    {
        return unsafeAllocated;
    }

    public void setUnsafeAllocated(boolean unsafeAllocated)
    {
        this.unsafeAllocated = unsafeAllocated;
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
