/*
 * Copyright 2017-2025 noear.org and authors
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
package features.serialization.javabin.model;

import java.io.Serializable;
import java.util.Objects;

public class UserDo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int age;

    public UserDo() {
    }

    public UserDo(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDo)) return false;
        UserDo userDo = (UserDo) o;
        return age == userDo.age && Objects.equals(name, userDo.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name, age); }
}
