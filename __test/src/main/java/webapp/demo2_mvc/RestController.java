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
package webapp.demo2_mvc;

import org.noear.solon.annotation.*;
import webapp.models.Book;

/**
 * @author noear 2021/11/25 created
 */

@Mapping("/demo2a/rest")
@Controller
public class RestController {

    @Get
    @Mapping("null")
    public String test_null() {
        return null;
    }

    @Get
    @Mapping("test")
    public String test_get(String name) {
        return "Get-" + name;
    }

    @Post
    @Mapping("test")
    public String test_post(String name) {
        return "Post-" + name;
    }

    @Put
    @Mapping("test")
    public String test_put(String name) {
        return "Put-" + name;
    }

    @Delete
    @Mapping("test")
    public String test_delete(String name) {
        return "Delete-" + name;
    }

    @Patch
    @Mapping("test")
    public String test_patch(String name) {
        return "Patch-" + name;
    }

    @Mapping("book")
    public Book book(Book book) {
        return book;
    }
}
