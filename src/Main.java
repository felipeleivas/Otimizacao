import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
//        FileParser x = new FileParser("instances/a8.oplib");
//        List<Integer> e = new ArrayList<>();
//        Integer i = 4;
//        System.out.println(x.getNode(4l).getDistance(x.getNode(3l)));
//
//        System.out.println(x.toString());    }
        TabuSearch a = new TabuSearch("instances/a8.oplib");
        a.printNeighbors();

    }

}


