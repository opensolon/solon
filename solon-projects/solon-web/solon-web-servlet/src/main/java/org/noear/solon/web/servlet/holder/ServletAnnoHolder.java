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
package org.noear.solon.web.servlet.holder;

import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;
import java.util.Objects;

public class ServletAnnoHolder {
    public final WebServlet anno;
    public final Servlet servlet;

    public ServletAnnoHolder(WebServlet anno, Servlet servlet) {
        this.anno = anno;
        this.servlet = servlet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServletAnnoHolder that = (ServletAnnoHolder) o;
        return Objects.equals(anno, that.anno) &&
                Objects.equals(servlet, that.servlet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anno, servlet);
    }
}
