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
package org.noear.solon.data.sqlink.core;

import org.noear.solon.data.sqlink.api.crud.read.LQuery;

import java.util.Collection;

public class SubQuery<T> {
    SubQuery() {
    }

    public static <T> LQuery<T> subQuery(/*this*/ Collection<T> collection) {
        throw new UnsupportedOperationException();
    }

//    public <R> SubQuery<R> where(Func1<T, R> func1) {
////        List<R> objects = new ArrayList<>();
////        for (T t : iterable) {
////            objects.add(func1.invoke(t));
////        }
////        return objects;
//        throw new UnsupportedOperationException();
//    }
//
//    public <R extends Result> SubQuery<? extends R> select(Func1<T, ? extends R> func1) {
////        List<R> objects = new ArrayList<>();
////        for (T t : iterable) {
////            objects.add(func1.invoke(t));
////        }
////        return objects;
//        throw new UnsupportedOperationException();
//    }
//
//    public long count() {
//        throw new UnsupportedOperationException();
//    }
//
//    public <R> long count(Func1<T, R> func1) {
//        throw new UnsupportedOperationException();
//    }
//
//    public <R extends Number> R sum(Func1<T, R> func1) {
//        throw new UnsupportedOperationException();
//    }
//
//    public <R extends Number> BigDecimal avg(Func1<T, R> func1) {
//        throw new UnsupportedOperationException();
//    }
//
//    public <R> R min(Func1<T, R> func1) {
//        throw new UnsupportedOperationException();
//    }
//
//    public <R> R max(Func1<T, R> func1) {
//        throw new UnsupportedOperationException();
//    }
//
//    public boolean any(Func1<T, Boolean> func1) {
//        throw new UnsupportedOperationException();
//    }
//
//    public <R> SubQuery<T> orderBy(Func1<T, R> func1) {
//        throw new UnsupportedOperationException();
//    }
//
//    public <R> SubQuery<T> orderByDesc(Func1<T, R> func1) {
//        throw new UnsupportedOperationException();
//    }
//
//    public <R> SubQuery<T> limit(long rows) {
//        throw new UnsupportedOperationException();
//    }
//
//    public SubQuery<T> limit(long offset, long rows) {
//        throw new UnsupportedOperationException();
//    }
//
//
}
