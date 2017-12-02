public class Main {

    public static void main(String[] args) {
        FileParser x = new FileParser("instances/a8.oplib");

        System.out.println(x.getNode(4l).getDistance(x.getNode(3l)));

        System.out.println(x.toString());
    }
}

