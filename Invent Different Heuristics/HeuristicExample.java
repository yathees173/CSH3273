import java.util.*;

class Heuristic {
    String name;
    int quality;

    Heuristic(String name, int quality) {
        this.name = name;
        this.quality = quality;
    }
}

public class HeuristicExample {

    // simulate search performance
    static double evaluate(Heuristic h) {
        Random r = new Random();

        int nodes = 200 - (h.quality * 15) + r.nextInt(20);
        double time = 2.0 - (h.quality * 0.1) + r.nextDouble() * 0.2;

        // lower is better
        return nodes + (time * 50);
    }

    public static void main(String[] args) {

        List<Heuristic> list = new ArrayList<>();

        // Step 1: create heuristics
        list.add(new Heuristic("Manhattan Distance", 8));
        list.add(new Heuristic("Misplaced Tiles", 5));
        list.add(new Heuristic("Linear Conflict", 9));
		list.add(new Heuristic("Euclidean Distance", 7));
        list.add(new Heuristic("Random Heuristic", 2));

        // Step 2: evaluate
        Heuristic best = null;
        double bestScore = Double.MAX_VALUE;

        System.out.println("Evaluating heuristics...\n");

        for (Heuristic h : list) {
            double score = evaluate(h);

            System.out.println(h.name + " -> Score: " + score);

            if (score < bestScore) {
                bestScore = score;
                best = h;
            }
        }

        // Step 3: select best
        System.out.println("\nBest Heuristic: " + best.name);
    }
}