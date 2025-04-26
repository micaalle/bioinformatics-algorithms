import java.io.*;
import java.util.*;
import java.util.stream.*;


    // twoBreakDistance function
    public static int TwoBreakDistance(List<List<Integer>> P, List<List<Integer>> Q) {
        int blocks = 0;
        for (List<Integer> chromosome : P) {
            blocks += chromosome.size();
        }

        // find the edges colored red and blue
        Set<int[]> edges = new HashSet<>();
        edges.addAll(coloredEdges(P));
        edges.addAll(coloredEdges(Q));

        // making of disjoint set
        Map<Integer, Integer> parent = new HashMap<>();
        Map<Integer, Integer> rank = new HashMap<>();
        for (int[] edge : edges) {
            parent.put(edge[0], edge[0]);
            parent.put(edge[1], edge[1]);
            rank.put(edge[0], 0);
            rank.put(edge[1], 0);
        }

        // union operation on the edges
        for (int[] edge : edges) {
            union(edge[0], edge[1], parent, rank);
        }

        // counts the number of cycles and returns num of blocks - num of cycles
        Set<Integer> nodesSets = new HashSet<>();
        for (int[] edge : edges) {
            int id = findParent(edge[0], parent);
            nodesSets.add(id);
        }

        int cycles = nodesSets.size();
        return blocks - cycles;
    }


    // converts chromosomes to a cycle representation
    private static List<Integer> chromosomeToCycle(List<Integer> chromosome) {
        int l = chromosome.size();
        List<Integer> nodes = new ArrayList<>(Collections.nCopies(2 * l, 0));
        for (int j = 0; j < l; j++) {
            int i = chromosome.get(j);
            if (i > 0) {
                nodes.set(2 * j, 2 * i - 1);
                nodes.set(2 * j + 1, 2 * i);
            } else {
                nodes.set(2 * j, -2 * i);
                nodes.set(2 * j + 1, -2 * i - 1);
            }
        }
        return nodes;
    }

    private static List<Integer> cycleToChromosome(List<Integer> nodes) {
        int l = nodes.size() / 2;
        List<Integer> chromosome = new ArrayList<>(Collections.nCopies(l, 0));
        for (int j = 0; j < l; j++) {
            if (nodes.get(2 * j) < nodes.get(2 * j + 1)) {
                chromosome.set(j, nodes.get(2 * j + 1) / 2);
            } else {
                chromosome.set(j, -nodes.get(2 * j) / 2);
            }
        }
        return chromosome;
    }

    //simple function made to output in the corect format
    private static void printChromosome(List<Integer> chromosome) {
        System.out.print("(");
        for (int i = 0; i < chromosome.size(); i++) {
            System.out.print((chromosome.get(i) > 0 ? "+" : "") + chromosome.get(i));
            if (i != chromosome.size() - 1) {
                System.out.print(" ");
            }
        }
        System.out.println(")");
    }

    // Generate the edges from a genome
    private static Set<int[]> coloredEdges(List<List<Integer>> genome) {
        Set<int[]> edges = new HashSet<>();
        for (List<Integer> chromosome : genome) {
            List<Integer> nodes = chromosomeToCycle(chromosome);
            nodes.add(nodes.get(0)); // Closing the cycle by appending the first node
            for (int j = 0; j < chromosome.size(); j++) {
                edges.add(new int[]{nodes.get(2 * j + 1), nodes.get(2 * j + 2)});
            }
        }
        return edges;
    }

    // reads through the genome and will remove the parenthses 
    // from the input and make it easier to add to the list of
    // genomes 
    private static List<List<Integer>> inputGenomes() {
        Scanner scanner = new Scanner(System.in);
        List<List<Integer>> genomes = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().strip();
            if (line.isEmpty()) continue;
            List<Integer> genome = new ArrayList<>();
            String[] chromosomes = line.split("\\)\\(");
            for (String chromosome : chromosomes) {
                chromosome = chromosome.replaceAll("[()]", "");
                String[] genes = chromosome.split("\\s+");
                for (String gene : genes) {
                    genome.add(Integer.parseInt(gene));
                }
            }
            genomes.add(genome);
        }
        return genomes;
    }

    //this will find the parent of a set which is needed for path compression
    private static int findParent(int i, Map<Integer, Integer> parent) {
        if (i != parent.get(i)) {
            parent.put(i, findParent(parent.get(i), parent)); 
        }
        return parent.get(i);
    }

    //function to connect trees based on parents and size
    private static void union(int i, int j, Map<Integer, Integer> parent, Map<Integer, Integer> rank) {
        int iParent = findParent(i, parent);
        int jParent = findParent(j, parent);
        if (iParent == jParent) return;
        if (rank.get(iParent) > rank.get(jParent)) {
            parent.put(jParent, iParent);
        } else {
            parent.put(iParent, jParent);
            if (rank.get(iParent).equals(rank.get(jParent))) {
                rank.put(jParent, rank.get(jParent) + 1);
            }
        }
    }
