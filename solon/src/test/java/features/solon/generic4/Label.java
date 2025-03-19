package features.solon.generic4;

public class Label<ID> {

    private final ID id;
    private final String label;

    public Label(ID id, String label) {
        this.id = id;
        this.label = label;
    }

    public ID getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

}
