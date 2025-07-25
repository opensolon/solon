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
package webapp.asm;

import org.noear.solon.annotation.Managed;

/**
 * @author noear 2022/9/30 created
 */
@Managed
public class TagServiceImpl extends CrudServiceImpl<TagDao, Tag, TagDTO, TagConvert> implements TagService {

    @Override
    public TagDTO update(Tag tag) {
        //return super.update(tag);
        System.out.println("hello");
        return null;
    }

    @Override
    public TagDTO update(TagDTO tagDTO) {
        //return super.update(tag);
        System.out.println("hello1");
        return null;
    }
}