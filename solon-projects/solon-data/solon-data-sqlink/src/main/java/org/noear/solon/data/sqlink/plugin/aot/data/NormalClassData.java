/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
