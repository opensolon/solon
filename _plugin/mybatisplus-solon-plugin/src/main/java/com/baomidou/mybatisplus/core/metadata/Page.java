package com.baomidou.mybatisplus.core.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * 分页配置器
 *
 * @author noear
 * @since 1.5
 */
public class Page<T> implements IPage<T> {
    private static final long serialVersionUID = 8545996863226528798L;
    protected List<T> records;
    protected long total;
    protected long size;
    protected long current;
    protected List<OrderItem> orders;
    protected boolean optimizeCountSql;
    protected boolean searchCount;
    protected String countId;
    protected Long maxLimit;

    public Page() {
        this.records = Collections.emptyList();
        this.total = 0L;
        this.size = 10L;
        this.current = 1L;
        this.orders = new ArrayList();
        this.optimizeCountSql = true;
        this.searchCount = true;
    }

    public Page(long current, long size) {
        this(current, size, 0L);
    }

    public Page(long current, long size, long total) {
        this(current, size, total, true);
    }

    public Page(long current, long size, boolean searchCount) {
        this(current, size, 0L, searchCount);
    }

    public Page(long current, long size, long total, boolean searchCount) {
        this.records = Collections.emptyList();
        this.total = 0L;
        this.size = 10L;
        this.current = 1L;
        this.orders = new ArrayList();
        this.optimizeCountSql = true;
        this.searchCount = true;
        if (current > 1L) {
            this.current = current;
        }

        this.size = size;
        this.total = total;
        this.searchCount = searchCount;
    }

    public boolean hasPrevious() {
        return this.current > 1L;
    }

    public boolean hasNext() {
        return this.current < this.getPages();
    }

    public List<T> getRecords() {
        return this.records;
    }

    public Page<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public long getTotal() {
        return this.total;
    }

    public Page<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getSize() {
        return this.size;
    }

    public Page<T> setSize(long size) {
        this.size = size;
        return this;
    }

    public long getCurrent() {
        return this.current;
    }

    public Page<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    public String countId() {
        return this.countId;
    }

    public Long maxLimit() {
        return this.maxLimit;
    }

    private String[] mapOrderToArray(Predicate<OrderItem> filter) {
        List<String> columns = new ArrayList(this.orders.size());
        this.orders.forEach((i) -> {
            if (filter.test(i)) {
                columns.add(i.getColumn());
            }

        });
        return (String[])columns.toArray(new String[0]);
    }

    private void removeOrder(Predicate<OrderItem> filter) {
        for(int i = this.orders.size() - 1; i >= 0; --i) {
            if (filter.test(this.orders.get(i))) {
                this.orders.remove(i);
            }
        }

    }

    public Page<T> addOrder(OrderItem... items) {
        this.orders.addAll(Arrays.asList(items));
        return this;
    }

    public Page<T> addOrder(List<OrderItem> items) {
        this.orders.addAll(items);
        return this;
    }

    public List<OrderItem> orders() {
        return this.orders;
    }

    public boolean optimizeCountSql() {
        return this.optimizeCountSql;
    }

    public boolean searchCount() {
        return this.total < 0L ? false : this.searchCount;
    }

    public Page<T> setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
        return this;
    }

    public Page<T> setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
        return this;
    }


    public static <T> Page<T> of(long current, long size) {
        return of(current, size, 0L);
    }

    public static <T> Page<T> of(long current, long size, long total) {
        return of(current, size, total, true);
    }

    public static <T> Page<T> of(long current, long size, boolean searchCount) {
        return of(current, size, 0L, searchCount);
    }

    public static <T> Page<T> of(long current, long size, long total, boolean searchCount) {
        return new Page(current, size, total, searchCount);
    }


    public void setOrders(final List<OrderItem> orders) {
        this.orders = orders;
    }

    public void setCountId(final String countId) {
        this.countId = countId;
    }

    public void setMaxLimit(final Long maxLimit) {
        this.maxLimit = maxLimit;
    }
}