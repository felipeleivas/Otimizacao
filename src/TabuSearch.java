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
    private Integer numberOfIteractions;
    private Integer numberOfNodesModifiedPerNeighbor;
    private Integer interactionNumber;

    public TabuSearch(String filePath){
        this.fp = new FileParser(filePath);
        this.maxCost = this.fp.getCostLimit();
        this.bestResultPath = new ArrayList<>();
        this.bestResultPath.add(this.fp.getNode(this.fp.getStartNode()));
        this.bestResult = 0;
        this.currentResultPath = new ArrayList<>(this.bestResultPath);
        this.currentResult = 0;
        this.numberOfNeighborsPerInteraction = fp.getDimension() * 10;
        this.numberOfIteractions = 1000000;
        this.interactionNumber = 0;
        this.numberOfNodesModifiedPerNeighbor = (int) Math.log(fp.getDimension());
        System.out.println(this.numberOfNodesModifiedPerNeighbor);

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
            for (int j = 0; j < this.numberOfNodesModifiedPerNeighbor; j++) {
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
                            currentAvailableNodes.add(deletedNode);
                        } else { // replace a node for one another
                            if(currentAvailableNodes.size()>0 && randomChoise == 0 && newNeighbor.size()>2){
                                Integer deletedNodePosition = this.worstNodeOnPath(newNeighbor);
                                Node newNode = this.bestNodeForPosition(currentAvailableNodes, newNeighbor.get(deletedNodePosition -1));
                                Node deletedNode = newNeighbor.get(deletedNodePosition);
                                newNeighbor.remove(deletedNode);
                                currentAvailableNodes.add(deletedNode);
                                if(newNode != null)
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

    private Integer worstNodeOnPath(List<Node> neighbors){
        int index=-1;
        int indexMin = 2;
        double min = Double.MAX_VALUE;
        Node before = neighbors.get(0);
        Integer dist;
        double value = Double.MAX_VALUE;;

        for(Node n: neighbors){

            index ++;
            if(index != 0){
                dist = before.getDistance(n);
                before = n;
                if( dist != 0){
                    value = n.getValue().doubleValue()/dist.doubleValue();
                }

                if(value < min){
                    min = value;
                    indexMin = index;
                }
            }
        }

        return  indexMin;
    }

    private Node bestNodeForPosition(List<Node> possibilities, Node node){
        int index=-1;
        int indexMax = 0;
        double max = Double.MIN_VALUE;
        Node aux = null;

        Integer dist;
        double value = 0;
        for(Node n: possibilities){
            index ++;
            dist = node.getDistance(n);
            if( dist != 0){
                value = n.getValue()/dist;
            }
            if(value > max){
                max = value;
                aux = n;
            }
        }
        //System.out.println("asdsadsadsda");
        return  aux;
    }
    public void printNeighbors(){
        List<List<Node>> neighbors = this.getNeighbors(this.currentResultPath);

        for(List<Node> neighbor: neighbors){
            System.out.println("\n\nNEW NEIGHBOR: " );
            for(Node node: neighbor){
                System.out.println(node.toString());
            }
            System.out.println(this.evalPath(neighbor ));
        }
    }

    public int evalPath(List<Node> neighbor){
        int result = 0;
        int cost = 0;
        int pontuation = 0;
        Node first = neighbor.get(0);
        Node before = neighbor.get(0);
        for(Node n: neighbor){
            cost += n.getDistance(before);
            before = n;
            pontuation += n.getValue();
        }
        cost += before.getDistance(first);

        if(cost > this.maxCost){
            result = Integer.MIN_VALUE + (2 * (cost - this.maxCost) + pontuation);
        }
        else{
            result = pontuation;
        }

        return result;
    }

    public int getDistance(List<Node> neighbor){
        int cost = 0;
        Node first = neighbor.get(0);
        Node before = neighbor.get(0);
        for(Node n: neighbor){
            cost += n.getDistance(before);
            before = n;
        }
        cost += before.getDistance(first);
        return cost;
    }

    private List<Node> getBestNeighor(List<List<Node>> neighbors){
        int max = Integer.MIN_VALUE;
        int current;
        List<Node> bestNeighborFound = null;
        for(List<Node> neighbor:neighbors){
            current = this.evalPath(neighbor);
            if(current > max){
                bestNeighborFound = neighbor;
                max = current;
            }
        }
        return bestNeighborFound;
    }
    public List<Node> search(){
        List<Node> currentPath = this.bestResultPath;
        Integer currentPathEval = this.bestResult;
        List<List<Node>> neighbors = null;
        List<List<Node>> tabuList = new ArrayList<>();
        int numberOfInteractionswithoutImproviment = 0;
        for(Integer i=0; (i<this.numberOfIteractions)  ; i++) {
            neighbors = this.getNeighbors(currentPath);
            neighbors.removeAll(tabuList);
            currentPath = this.getBestNeighor(neighbors);
            currentPathEval = this.evalPath(currentPath);
            if (this.bestResult < currentPathEval) {
                this.bestResult = currentPathEval;
                this.bestResultPath = currentPath;
                numberOfInteractionswithoutImproviment = 0;

            } else {
                tabuList.add(currentPath);
                numberOfInteractionswithoutImproviment++;
            }
            this.manageTabu(tabuList);
        }
        return this.bestResultPath;
    }

    private  void manageTabu( List<List<Node>> tabu){
        if(tabu.size() > 100){
            tabu.remove(0);
        }
    }
}
