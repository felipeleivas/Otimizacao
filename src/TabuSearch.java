import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TabuSearch {
    private FileParser fp;
    private Integer bestResult;
    private List<Node> bestResultPath;
    private Integer currentResult;
    private List<Node> currentResultPath;
    private Integer maxCost;
    private Integer numberOfNeighborsPerInteraction;
    private Integer numberOfIteraction;
    public TabuSearch(String filePath){
        this.fp = new FileParser(filePath);
        this.maxCost = this.fp.getCostLimit();
        this.bestResultPath = new ArrayList<>();
        this.bestResultPath.add(this.fp.getNode(this.fp.getStartNode()));
        this.bestResult = Integer.MIN_VALUE;
        this.currentResultPath = new ArrayList<>(this.bestResultPath);
        this.currentResult = Integer.MIN_VALUE;
        this.numberOfNeighborsPerInteraction = 50;
        this.numberOfIteraction = 0;
    }

    private List<List<Node>> getNeighbors(List<Node> currentResultPath){
        List<List<Node>> neighbors = new ArrayList<>();
        List<Node> availableNodes = new ArrayList<>(this.fp.getNodeList());
        availableNodes.removeAll(currentResultPath);

        Random randGenerator = new Random(System.currentTimeMillis());
        Integer randomChoise;
        //Can be transform into a parallel for
        for (int i = 0; i < this.numberOfNeighborsPerInteraction; i++){
            List<Node> newNeighbor = new ArrayList<>(currentResultPath);
            List<Node> currentAvailableNodes = new ArrayList<>(availableNodes);
            for (int j = 0; j < this.numberOfNeighborsPerInteraction; j++) {
            randomChoise = randGenerator.nextInt(3);
                //add a edge to the solution
                if (randomChoise == 1 && currentAvailableNodes.size()>0) {
                    Integer newNodePosition = randGenerator.nextInt(newNeighbor.size()) + 1;
                    Node newNode = currentAvailableNodes.get(randGenerator.nextInt(currentAvailableNodes.size()));
                    newNeighbor.add(newNodePosition, newNode);
                    currentAvailableNodes.remove(newNode);
                } else {
                    if (newNeighbor.size() > 1) {
                        //delete a node from the solution
                        if (randomChoise == 2) {
                            Node deletedNode = newNeighbor.get(randGenerator.nextInt(newNeighbor.size() - 1) + 1);
                            newNeighbor.remove(deletedNode);
                            //currentAvailableNodes.add(deletedNode);
                        } else { // replace a node for one another
                            if(currentAvailableNodes.size()>0 && randomChoise == 0){
                                Node newNode = currentAvailableNodes.get(randGenerator.nextInt(currentAvailableNodes.size()));
                                Integer deletedNodePosition = randGenerator.nextInt(newNeighbor.size() - 1) + 1;
                                Node deletedNode = newNeighbor.get(deletedNodePosition);
                                newNeighbor.remove(deletedNodePosition);
                                currentAvailableNodes.add(deletedNode);
                                newNeighbor.add(deletedNodePosition, newNode);
                                currentAvailableNodes.remove(newNode);
                            }

                        }
                    }
                }
            }
            neighbors.add(newNeighbor);
        }

        return neighbors;
    }

    public void printNeighbors(){
        List<List<Node>> neighbors = this.getNeighbors(this.currentResultPath);

        for(List<Node> neighbor: neighbors){
            System.out.println("\n\nNEW NEIGHBOR: " );
            for(Node node: neighbor){
                System.out.println(node.toString());
            }
        }
    }
}
