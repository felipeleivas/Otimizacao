public class Node {
    private Long id;
    private Integer x;
    private Integer y;
    private Integer value;

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Node(Long id, Integer x, Integer y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;

    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Node(Long id, Integer x, Integer y , Integer value){
        this.x = x;
        this.y = y;
        this.id = id;
        this.value = value;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getDistance(Node another){
        return (int) Math.sqrt( Math.pow((this.x - this.y), 2) +
                         Math.pow((another.getX() - another.getY()), 2) + 0.5);
    }

    @Override
    public String toString() {
        return "ID: " + this.id +" Cord X: " + this.x + " Cord Y: " + this.y + " Value: " + this.value;
    }
}