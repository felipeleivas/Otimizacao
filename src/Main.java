import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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
        //args[0] = "instances/a8.oplib";
        PrintWriter writer = null;
        try {
            writer = new PrintWriter( new FileOutputStream(
                    new File(args[0]+ ".txt"), true));
        } catch (Exception e) {
            System.out.println("Erro na criação do arquivo saida");
            System.exit(-1);
        }
        TabuSearch a = new TabuSearch(args[0]);
        //a.printNeighbors();
        long startTime = System.currentTimeMillis();

        List<Node> x = a.search();

        long endTime   = System.currentTimeMillis();
        Long totalTime = endTime - startTime;
        writer.println(args[0]);
        writer.print("Tempo Total: ");
        writer.println(totalTime);
        for(Node u: x){
            writer.println(u.toString());
        }
        writer.println("assadsad");
        writer.println("Valor da solção:" + a.evalPath(x));
        writer.println("Distancia da solção:" + a.getDistance(x));
        System.out.println("Valor da eval:" + a.evalPath(x));
        writer.close();
        String str = args[0] + a.evalPath(x) + ";" + a.getDistance(x) + ";" +  totalTime;try {
            writer = new PrintWriter( new FileOutputStream(
                    new File("resultsOnly"+ ".txt"), true));
        } catch (Exception e) {
            System.out.println("Erro na criação do arquivo saida");
            System.exit(-1);
        }
        writer.println(str);
        writer.close();

    }


}


