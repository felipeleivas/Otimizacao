import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

public class FilePaser {
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

    public FilePaser(String filePath) {
        this.filePath = filePath;
        this.nodeHash = new HashMap<>();
        this.parseFile2();
    }

    private void parseFile(){
        try (Stream<String> stream = Files.lines(Paths.get(this.filePath))) {

            stream.forEach((String s) -> {
                if(s.startsWith("NAME :")){
                    this.fileName = s.substring(6,s.length()).trim();

                }
                if(s.startsWith("COST_LIMIT : ")){
                    this.costLimit = Integer.parseInt(s.substring("COST_LIMIT : ".length()).trim());
                }
                if(this.coordMng){
                    String[] lineValues = s.split(" ");
                    Long id = Long.parseLong(lineValues[0]);
                    Integer x = Integer.parseInt(lineValues[1]);
                    Integer y = Integer.parseInt(lineValues[2]);
                    Node newNode = new Node(id, x, y);
                    this.nodeHash.put(id, newNode);
                }
                if(this.nodeValueMng){
                    System.out.println("Veio");
                    String[] lineValues = s.split(" ");
                    Long id = Long.parseLong(lineValues[0]);
                    Integer value = Integer.parseInt(lineValues[1]);
                    Node newNode = this.nodeHash.get(id);
                    this.nodeHash.remove(id);
                    newNode.setValue(value);
                    this.nodeHash.put(id, newNode);
                }
                if(s.equals("NODE_COORD_SECTION")){
                    this.coordMng = true;
                    this.nodeValueMng = false;
                }
                if(s.equals("NODE_SCORE_SECTION")){
                    this.coordMng = false;
                    this.nodeValueMng = true;
                }
                if(s.equals("NODE_SCORE_SECTION")) {
                    this.nodeValueMng = false;
                    this.coordMng = false;
                }

            });


        } catch (IOException e) {
            System.out.println("DEU RUIM LENDO O ARQUIVO");
        }
    }
    private void parseFile2() {
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
}
