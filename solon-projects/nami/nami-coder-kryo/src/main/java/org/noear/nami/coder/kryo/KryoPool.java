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
package org.noear.nami.coder.kryo;

import com.esotericsoftware.kryo.Kryo;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Kryo 使用池
 *
 * @author noear
 * @since 3.0
 * */
public class KryoPool {
    protected static final String label = "application/kryo";
    private final Queue<Kryo> objects = new ConcurrentLinkedQueue<>();

    protected Kryo obtain() {
        Kryo tmp = objects.poll();
        if (tmp == null) {
            tmp = new Kryo();
            tmp.setRegistrationRequired(false);
        }
        return tmp;
    }

    protected void free(Kryo kryo) {
        this.objects.offer(kryo);
    }
}
