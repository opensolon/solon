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
