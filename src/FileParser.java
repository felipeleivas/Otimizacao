
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FileParser {
    private String filePath;
    private String fileName;
    private Integer costLimit;
    private Long startNode;
    private HashMap<Long, Node> nodeHash = new HashMap<>();
    private List<Node> nodeList;

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
        this.nodeList = this.allNodes();
    }

    private void parseFile() {
        try {
            File file = new File(this.filePath);
            FileInputStream inputStream = new FileInputStream(file);

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                if(br == null) {
                    System.out.println("ERRO");
                }
                line = br.readLine();
                while ( line != null && !line.startsWith("NAME")) {
                    System.out.println(line);
                    line = br.readLine();
                }

                if(line != null){
                    this.fileName = line.split(":")[1].trim();
                }
                else{
                    System.out.println("Erro na leitura");
                    System.out.println(line);
                }

                while ( line != null && !line.startsWith("COST_LIMIT")) {
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
                line = br.readLine();
                try{
                    this.startNode = Long.parseLong( line.trim());
                }catch (Exception e){
                    System.out.println("ERRO NO PARSING DO ARQUIVO, NÂO PODE CONVERTER UMA STRING EM NUMERO (Start Node");
                    System.exit(-1);
                }
            } catch (IOException e) {
                System.out.println("ERRO LENDO O ARQUIVO 2");
                System.exit(-1);
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERRO LENDO O ARQUIVO 1");
        }

    }

    public Long getStartNode() {
        return startNode;
    }

    public void setStartNode(Long startNode) {
        this.startNode = startNode;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    /* EXEMPLO do que preciso :D
    set V := 1 2 3 4 5;
    param C := 25;
    param I := 1;
    param D :1   2    3    4    5:=
        1   26   1    4    18   7
        2   1    26   5    15   2
        3   4    5    26   11   8
        4   18   15   11   26   1
        5   7    2    8    1    26;
    param P :=
        1 10
        2 1
        3 5
        4 20
        5 3;
    end;
         */
    public void writeDataForGLPK(String fileName) {
        //o que tá comentado com um ~ é o q deve ser escrito no arquivo
        List<Node> allNodes = this.allNodes();
        //abre file pra escrever do tipo .dat
        //FALTA ABRIR O ARQUIVO
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
        } catch (Exception e) {
            System.out.println("Erro na criação do arquivo GLPK");
            System.exit(-1);
        }
        String content = new String();
        //~data;\n
        writer.print("data;\n");

        //~set V := <índices dos vértices espaçados>;\n
        writer.print("set V := ");
        for(Node n: allNodes){
            writer.print(n.getId().toString() + " ");
        }
        writer.print(";\n");

        //~param C := <COST_LIMIT>;\n
        System.out.println(this.costLimit.toString() );
        writer.print("param C := " + this.costLimit.toString() + ";\n");
        //~param I := <nó de origem>;\n
        writer.print("param I := " + this.startNode.toString() + ";\n");
        //os proximos não são tão simples, vou por um "código" e tu passa p java
        //~param P := \n
        writer.print("param P := \n");
        //for(int i=0; i < V.length; i++){
        //~<indice do nodo i> <pontuação do nodo i>\n
        //}
        Node lastNode = allNodes.get(allNodes.size() - 1);
        for(Node n: allNodes){
            if(n == lastNode ) {
                writer.print(n.getId().toString() + " " + n.getValue().toString()+ ";\n");
            }
            else{
                writer.print(n.getId().toString() + " " + n.getValue().toString()+ "\n");
            }
        }
        //~;\n

        //~param D : <índices dos vértices espaçados> :=\n
        writer.print("param D :");
        for(Node n: allNodes){
            writer.print(n.getId().toString() + " ");
        }
        writer.print(" :=\n");

        //for(int i=0; i < V.length; i++){
        //    //~<indice do nodo i>
        //        for(int j=0; i < V.length; j++){
        //            if (i == j) {
        //                //isso impede que o glpk use na solução as supostas existentes arestas em loop
        //                //~ <COST_LIMIT + 1>
        //            } else {
        //               //~ <distancia do nodo i para o nodo j>
        //           }
        //       }
        //    //~\n
        //}
        for(Node n: allNodes){
            writer.print(n.getId().toString() + " ");
            for(Node n2: allNodes){
                if(n2.getId() == n.getId()){

                    Integer max = costLimit + 1;
                    writer.print(max.toString() +" ");
                }
                else{
                    Integer dist = n.getDistance(n2);
                    writer.print(dist.toString() + " ");
                }
            }
            if(n == lastNode){
                writer.print(";\n");
            }
            else{
                writer.print("\n");
            }
        }
        //~;\n

        //~end;
        writer.print("end;");
        //System.out.println(content);
        //writer.println(content);
        writer.close();
        //tem ali em cima o exemplo de como ficaria com um grafo de 5 nodos
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

    private List<Node> allNodes(){
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
