package org.noear.solon.ai.rag.repository;

/**
 * @author noear 2025/2/18 created
 */
public class SearchRequest {
    private String query = "";
    private int topK = 4;
    private double similarityThreshold = 0.0D;

    public String getQuery() {
        return query;
    }

    public int getTopK() {
        return topK;
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }


    public SearchRequest(String query) {
        this.query = query;
    }

    public SearchRequest topK(int topK) {
        this.topK = topK;
        return this;
    }

    public SearchRequest similarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
        return this;
    }
}
