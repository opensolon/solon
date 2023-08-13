package graphql.solon.fetcher;

import graphql.schema.DataFetcher;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class DataFetcherWrap {

    private String typeName;
    private String fieldName;
    private DataFetcher dataFetcher;

    public DataFetcherWrap(String typeName, String fieldName, DataFetcher dataFetcher) {
        this.typeName = typeName;
        this.fieldName = fieldName;
        this.dataFetcher = dataFetcher;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public DataFetcher getDataFetcher() {
        return dataFetcher;
    }
}
