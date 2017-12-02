import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FileParser {
    private String filePath;
    private String fileName;
    private Integer costLimit;
    private HashMap<Long, Node> nodeHash = new HashMap<>();
    private Boolean coordMng = false;
    private Boolean nodeValueMng = false;
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getCostLimit() {
        return costLimit;
    }

    public void setCostLimit(Integer costLimit) {
        this.costLimit = costLimit;
    }

    public HashMap<Long, Node> getNodeHash() {
        return nodeHash;
    }

    public void setNodeHash(HashMap<Long, Node> nodeHash) {
        this.nodeHash = nodeHash;
    }

    public FileParser(String filePath) {
        this.filePath = filePath;
        this.nodeHash = new HashMap<>();
        this.parseFile();
    }

    private void parseFile() {
        InputStream inputStream = null;
        StringBuilder resultStringBuilder = new StringBuilder();
        try {
            inputStream = new FileInputStream(this.filePath);


            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                if(br == null){
                    System.out.println("ERRO");
                }
                line = br.readLine();
                while ( line != null && !line.startsWith("NAME :")) {
                    System.out.println(line);
                    line = br.readLine();
                }
                this.fileName = line.split(":")[1].trim();

                while ( line != null && !line.startsWith("COST_LIMIT :")) {
                    line = br.readLine();
                }
                try{
                    this.costLimit = Integer.parseInt(line.split(":")[1].trim());
                }catch (Exception e){
                    System.out.println("ERRO NO PARSING DO ARQUIVO, NÂO PODE CONVERTER UMA STRING EM NUMERO(COST LIMIT)");
                    System.exit(-1);
                }
                while ( line != null && !line.startsWith("NODE_COORD_SECTION")) {
                    line = br.readLine();
                }
                line = br.readLine();
                while ( line != null && !line.startsWith("NODE_SCORE_SECTION")) {
                    String[] values;
                    values = line.split(" ");
                    Integer x = -1,y = -1;
                    Long id= -1l;
                    try{
                        id = Long.parseLong(values[0].trim());
                        x = Integer.parseInt(values[1].trim());
                        y = Integer.parseInt(values[2].trim());
                    }catch (Exception e){
                        System.out.println("ERRO NO PARSING DO ARQUIVO, NÂO PODE CONVERTER UMA STRING EM NUMERO (NODE CORD");
                        System.exit(-1);
                    }
                    Node newNode = new Node(id,x , y);
                    this.nodeHash.put(id, newNode);
                    line = br.readLine();
                }
                line = br.readLine();
                while ( line != null && !line.startsWith("DEPOT_SECTION")) {
                    String[] values;
                    values = line.split(" ");
                    Integer value = -1;
                    Long id= -1l;
                    try{
                        id = Long.parseLong(values[0].trim());
                        value = Integer.parseInt(values[1].trim());
                    }catch (Exception e){
                        System.out.println("ERRO NO PARSING DO ARQUIVO, NÂO PODE CONVERTER UMA STRING EM NUMERO (NODE VALUE");
                        System.exit(-1);
                    }

                    Node node = this.nodeHash.get(id);
                    if(node == null){
                        System.out.println("Erro No parsingo do arquivo, nodo não instanciado");
                        System.exit(-1);
                    }
                    node.setValue(value);
                    this.nodeHash.put(id, node);
                    line = br.readLine();
                }





            } catch (IOException e) {
                System.out.println("ERRO LENDO O ARQUIVO");
                System.exit(-1);
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERRO LENDO O ARQUIVO");
        }

    }


        @Override
    public String toString() {
        String result = "Name: " + this.fileName+ " Limite: " + this.costLimit + "Nodos: " ;
        Set<Long> idSet = this.nodeHash.keySet();
        Node aux = null;
        for(Long id: idSet){
            aux  = this.nodeHash.get(id);
            result += "\n" + aux.toString();
        }
        return result;
    }

    public List<Node> allNodes(){
        List nodesList = new ArrayList<Node>();
        Set<Long> keySet = this.nodeHash.keySet();

        for(Long id: keySet){
            nodesList.add(this.nodeHash.get(id));
        }
        return nodesList;
    }

    public Node getNode(Long id){
        return this.nodeHash.get(id);
    }
}
