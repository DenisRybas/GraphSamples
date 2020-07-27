package ru.vsu.cs.course1.graph;

import java.util.*;

/**
 * Реализация графа на основе матрицы смежности
 */
public class AdjMatrixGraph implements Graph {

    private boolean[][] adjMatrix = null;
    private int vCount = 0;
    private int eCount = 0;

    /**
     * Конструктор
     *
     * @param vertexCount Кол-во вершин графа (может увеличиваться при добавлении ребер)
     */
    public AdjMatrixGraph(int vertexCount) {
        adjMatrix = new boolean[vertexCount][vertexCount];
        vCount = vertexCount;
    }

    /**
     * Конструктор без парметров
     * (лучше не использовать, т.к. при добавлении вершин каждый раз пересоздается матрица)
     */
    public AdjMatrixGraph() {
        this(0);
    }

    @Override
    public int vertexCount() {
        return vCount;
    }

    @Override
    public int edgeCount() {
        return eCount;
    }

    @Override
    public void addAdge(int v1, int v2) {
        int maxV = Math.max(v1, v2);
        if (maxV >= vertexCount()) {
            adjMatrix = Arrays.copyOf(adjMatrix, maxV + 1);
            for (int i = 0; i <= maxV; i++) {
                adjMatrix[i] = i < vCount ? Arrays.copyOf(adjMatrix[i], maxV + 1) : new boolean[maxV + 1];
            }
            vCount = maxV + 1;
        }
        if (!adjMatrix[v1][v2]) {
            adjMatrix[v1][v2] = true;
            eCount++;
            // для наследников
            if (!(this instanceof Digraph)) {
                adjMatrix[v2][v1] = true;
            }
        }
    }

    @Override
    public void removeAdge(int v1, int v2) {
        if (adjMatrix[v1][v2]) {
            adjMatrix[v1][v2] = false;
            eCount--;
            // для наследников
            if (!(this instanceof Digraph)) {
                adjMatrix[v2][v1] = false;
            }
        }
    }

    @Override
    public Iterable<Integer> adjacencies(int v) {
        return new Iterable<Integer>() {
            Integer nextAdj = null;

            @Override
            public Iterator<Integer> iterator() {
                for (int i = 0; i < vCount; i++) {
                    if (adjMatrix[v][i]) {
                        nextAdj = i;
                        break;
                    }
                }

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return nextAdj != null;
                    }

                    @Override
                    public Integer next() {
                        Integer result = nextAdj;
                        nextAdj = null;
                        for (int i = result + 1; i < vCount; i++) {
                            if (adjMatrix[v][i]) {
                                nextAdj = i;
                                break;
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }

    //smth like dat
    private boolean isSafe(int v, int graph[][], List<Integer> path, int pos) {
        if (graph[path.get(pos - 1)][v] == 0)
            return false;

        return !path.contains(v);
        //for (int i = 0; i < pos; i++)
          //  if (path[i] == v)
            //    return false;
    }

    interface PathsExplorer {
        class LazyList {
            private List<Integer> list;

            public LazyList(List<Integer> list) {
                this.list = list;
            }

            public List<Integer> getList() {
                return new LinkedList<>(list);
            }
            public int getSize() {
                return list.size();
            }
        }
        void newPathFound(LazyList ll);
    }

    private void hamCycleUtil(int graph[][], List<Integer> path, int pos, /*List<List<Integer>> paths*/PathsExplorer pe) {
        if (pos >= adjMatrix.length) {
            return;
        }

        for (int v = 0; v < adjMatrix.length; v++) {
            if (isSafe(v, graph, path, pos)) {
                //path[pos] = v;
                path.add(v);

                //System.arraycopy(path, 0, copyOfPath, 0, adjMatrix.length);
                //List<Integer> copyOfPath = new LinkedList<>(path);
                //paths.add(copyOfPath);
                pe.newPathFound(new PathsExplorer.LazyList(path));

                hamCycleUtil(graph, path, pos + 1, pe);

                path.remove(pos);
                // path[pos] = -1;
            }
        }
    }


    public int createChain() {
        List<List<Integer>> paths = new ArrayList<>();
        int[][] matrix = new int[adjMatrix.length][adjMatrix.length];
        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix.length; j++) {
                if (adjMatrix[i][j]) matrix[i][j] = 1;
                else matrix[i][j] = 0;
            }
        }
        //Arrays.fill(path, -1);
        //List<Integer> maxPath = new LinkedList<>();

        class MaxPathsExplorer implements PathsExplorer {
            private List<Integer> maxPath = new LinkedList<>();
            public List<Integer> getMaxPath() {
                return maxPath;
            }

            @Override
            public void newPathFound(LazyList list) {
                if (list.getSize() > maxPath.size()) maxPath = list.getList();
            }
        }
        MaxPathsExplorer pe = new MaxPathsExplorer();

        for (int i = 0; i < adjMatrix.length; i++) {
            System.out.printf("Progress: %d/%d\n", i, adjMatrix.length);
            List<Integer> path = new LinkedList<>();
            path.add(i);
            hamCycleUtil(matrix, path, 1, pe);
        }

        //Arrays.fill(maxPath, -1);
        //int quantityOfElems = 0;
        //int maxPathLength = 0;
        /*for (int[] chain : paths) {
            for (int value : chain) {
                if (value == -1) {
                    break;
                }
                quantityOfElems++;
            }
            for (int value : maxPath) {
                if (value == -1) {
                    break;
                }
                maxPathLength++;
            }
            if (quantityOfElems >= maxPathLength) {
                maxPath = Arrays.copyOf(chain, chain.length);
            }
            maxPathLength = 0;
            quantityOfElems = 0;
        }

         */

        /*
        for (int value : maxPath) {
            System.out.print(value + " ");
        }
        System.out.println();*/

        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix.length; j++) {
                adjMatrix[i][j] = false;
            }
        }

        int quantityOfElems = 0;
        Integer[] maxPath = pe.getMaxPath().toArray(new Integer[0]);
        for (int i = 0; i < maxPath.length - 1; i++) {
            adjMatrix[maxPath[i]][maxPath[i + 1]] = true;
            adjMatrix[maxPath[i + 1]][maxPath[i]] = true;
        }

        /*List<Integer> mp = pe.getMaxPath();
        Iterator<Integer> first = mp.iterator();
        Iterator<Integer> second = mp.iterator();
        if (second.hasNext()) second.next();
        while (second.hasNext()) {
            int f = first.next();
            int s = second.next();
            adjMatrix[f][s] = true;
            adjMatrix[s][f] = true;
        }*/

        return adjMatrix.length - maxPath.length;
    }
}
