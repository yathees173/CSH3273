import java.util.*;

public class RomaniaInformed {

    // HEURISTIC (straight-line distance to Bucharest)
    static Map<String, Integer> h = new HashMap<>();
    static {
        h.put("A",  366); h.put("B",   0); h.put("C",  160);
        h.put("D",  242); h.put("E",  161); h.put("F",  176);
        h.put("G",   77); h.put("H",  151); h.put("I",  226);
        h.put("L",  244); h.put("M",  241); h.put("N",  234);
        h.put("O",  380); h.put("P",  100); h.put("RV", 193);
        h.put("S",  253); h.put("T",  329); h.put("U",   80);
        h.put("V",  199); h.put("Z",  374);
    }

    static Map<String, String> fullName = new LinkedHashMap<>();
    static Map<String, List<int[]>> graph = new HashMap<>();
    // int[] = {neighborIndex, cost}
    static String[] nodes = {
        "A","O","Z","T","L","M","D","C","S","F",
        "RV","P","B","G","U","H","E","V","I","N"
    };

    static void buildGraph() {
        fullName.put("A","Arad");        fullName.put("O","Oradea");
        fullName.put("Z","Zerind");      fullName.put("T","Timisoara");
        fullName.put("L","Lugoj");       fullName.put("M","Mehadia");
        fullName.put("D","Drobeta");     fullName.put("C","Craiova");
        fullName.put("S","Sibiu");       fullName.put("F","Fagaras");
        fullName.put("RV","Rimnicu Vilcea"); fullName.put("P","Pitesti");
        fullName.put("B","Bucharest");   fullName.put("G","Giurgiu");
        fullName.put("U","Urziceni");    fullName.put("H","Hirsova");
        fullName.put("E","Eforie");      fullName.put("V","Vaslui");
        fullName.put("I","Iasi");        fullName.put("N","Neamt");

        for (String n : nodes) graph.put(n, new ArrayList<>());

        addEdge("A","Z",75);  addEdge("A","T",118); addEdge("A","S",140);
        addEdge("Z","O",71);  addEdge("O","S",151);
        addEdge("T","L",111); addEdge("L","M",70);  addEdge("M","D",75);
        addEdge("D","C",120); addEdge("C","RV",146);addEdge("C","P",138);
        addEdge("S","F",99);  addEdge("S","RV",80);
        addEdge("F","B",211); addEdge("RV","P",97);
        addEdge("P","B",101); addEdge("B","G",90);  addEdge("B","U",85);
        addEdge("U","H",98);  addEdge("U","V",142);
        addEdge("H","E",86);  addEdge("V","I",92);  addEdge("I","N",87);
    }

    static void addEdge(String a, String b, int cost) {
        graph.get(a).add(new int[]{indexOf(b), cost});
        graph.get(b).add(new int[]{indexOf(a), cost});
    }

    static int indexOf(String node) {
        for (int i = 0; i < nodes.length; i++)
            if (nodes[i].equals(node)) return i;
        return -1;
    }
    static String nodeOf(int i) { return nodes[i]; }
    static int hVal(String n) { return h.getOrDefault(n, 0); }

    // Node state class for path tracking
    static class State {
        String node;
        State parent;
        int g; // cost so far
        int f; // f = g + h

        State(String node, State parent, int g, String goal) {
            this.node = node;
            this.parent = parent;
            this.g = g;
            this.f = g + hVal(node);
        }
        State(String node, State parent, int g, int f) {
            this.node = node; this.parent = parent;
            this.g = g; this.f = f;
        }
    }

    // PRINT HELPERS
    static void printNodeRef() {
        System.out.println("\n=== Node Reference ===");
        int i = 0;
        for (String n : nodes) {
            System.out.printf("  %-4s = %-20s  h=%d%n", n, fullName.get(n), hVal(n));
        }
    }

    static void printResult(String algo, List<String> visitOrder, List<String> path, int cost) {
        System.out.println("\n=== " + algo + " ===");
        System.out.print("Visit order: ");
        System.out.println(String.join(" -> ", visitOrder));
        if (path != null && !path.isEmpty()) {
            System.out.println("Path      : " + String.join(" -> ", path));
            System.out.println("Path Cost : " + cost);
        } else {
            System.out.println("Goal not reachable.");
        }
    }

    static List<String> extractPath(State s) {
        List<String> path = new ArrayList<>();
        while (s != null) { path.add(0, s.node); s = s.parent; }
        return path;
    }

    static int pathCost(List<String> path) {
        int total = 0;
        for (int i = 0; i < path.size()-1; i++) {
            String a = path.get(i), b = path.get(i+1);
            for (int[] nb : graph.get(a))
                if (nodeOf(nb[0]).equals(b)) { total += nb[1]; break; }
        }
        return total;
    }

    // 1. A* SEARCH
    static void aStar(String start, String goal) {
        List<String> visited = new ArrayList<>();
        PriorityQueue<State> open = new PriorityQueue<>(Comparator.comparingInt(s -> s.f));
        Map<String, Integer> bestG = new HashMap<>();

        open.add(new State(start, null, 0, goal));
        bestG.put(start, 0);

        while (!open.isEmpty()) {
            State cur = open.poll();
            if (visited.contains(cur.node)) continue;
            visited.add(cur.node);

            if (cur.node.equals(goal)) {
                printResult("A*", visited, extractPath(cur), cur.g);
                return;
            }
            for (int[] nb : graph.get(cur.node)) {
                String next = nodeOf(nb[0]);
                int newG = cur.g + nb[1];
                if (!bestG.containsKey(next) || newG < bestG.get(next)) {
                    bestG.put(next, newG);
                    open.add(new State(next, cur, newG, goal));
                }
            }
        }
        printResult("A*", visited, null, 0);
    }

    // 2. IDA* (Iterative Deepening A*)
    static List<String> idaVisited;
    static List<String> idaPath;
    static int idaCost;

    static void idaStar(String start, String goal) {
        idaVisited = new ArrayList<>();
        int bound = hVal(start);
        List<String> path = new ArrayList<>();
        path.add(start);

        while (true) {
            idaPath = null;
            int t = idaSearch(path, 0, bound, goal);
            if (idaPath != null) {
                printResult("IDA*", idaVisited, idaPath, idaCost);
                return;
            }
            if (t == Integer.MAX_VALUE) break;
            bound = t;
        }
        printResult("IDA*", idaVisited, null, 0);
    }

    static int idaSearch(List<String> path, int g, int bound, String goal) {
        String cur = path.get(path.size()-1);
        int f = g + hVal(cur);
        if (!idaVisited.contains(cur)) idaVisited.add(cur);
        if (f > bound) return f;
        if (cur.equals(goal)) {
            idaPath = new ArrayList<>(path);
            idaCost = g;
            return -1;
        }
        int min = Integer.MAX_VALUE;
        for (int[] nb : graph.get(cur)) {
            String next = nodeOf(nb[0]);
            if (!path.contains(next)) {
                path.add(next);
                int t = idaSearch(path, g + nb[1], bound, goal);
                if (t == -1) return -1;
                if (t < min) min = t;
                path.remove(path.size()-1);
            }
        }
        return min;
    }

    // 3. GREEDY BEST-FIRST SEARCH
    static void greedyBestFirst(String start, String goal) {
        List<String> visited = new ArrayList<>();
        PriorityQueue<State> open = new PriorityQueue<>(Comparator.comparingInt(s -> hVal(s.node)));
        Set<String> inOpen = new HashSet<>();
        Map<String, State> stateMap = new HashMap<>();

        State s0 = new State(start, null, 0, goal);
        open.add(s0); inOpen.add(start);

        while (!open.isEmpty()) {
            State cur = open.poll(); inOpen.remove(cur.node);
            visited.add(cur.node);

            if (cur.node.equals(goal)) {
                printResult("Greedy Best-First", visited, extractPath(cur), cur.g);
                return;
            }
            for (int[] nb : graph.get(cur.node)) {
                String next = nodeOf(nb[0]);
                if (!visited.contains(next) && !inOpen.contains(next)) {
                    State ns = new State(next, cur, cur.g + nb[1], goal);
                    open.add(ns); inOpen.add(next);
                }
            }
        }
        printResult("Greedy Best-First", visited, null, 0);
    }

    // 4. WEIGHTED A* (w * h(n) + g(n))
    static void weightedAStar(String start, String goal, double w) {
        List<String> visited = new ArrayList<>();
        // f = g + w*h
        PriorityQueue<State> open = new PriorityQueue<>(
            Comparator.comparingInt(s -> s.f));
        Map<String, Integer> bestG = new HashMap<>();

        int fStart = (int)(0 + w * hVal(start));
        open.add(new State(start, null, 0, fStart));
        bestG.put(start, 0);

        while (!open.isEmpty()) {
            State cur = open.poll();
            if (visited.contains(cur.node)) continue;
            visited.add(cur.node);

            if (cur.node.equals(goal)) {
                printResult("Weighted A* (w=" + w + ")", visited, extractPath(cur), cur.g);
                return;
            }
            for (int[] nb : graph.get(cur.node)) {
                String next = nodeOf(nb[0]);
                int newG = cur.g + nb[1];
                if (!bestG.containsKey(next) || newG < bestG.get(next)) {
                    bestG.put(next, newG);
                    int fNew = (int)(newG + w * hVal(next));
                    open.add(new State(next, cur, newG, fNew));
                }
            }
        }
        printResult("Weighted A* (w=" + w + ")", visited, null, 0);
    }

    // 5. RBFS (Recursive Best-First Search)
    static List<String> rbfsVisited;
    static List<String> rbfsPath;
    static int rbfsCost;

    static void rbfs(String start, String goal) {
        rbfsVisited = new ArrayList<>();
        State s0 = new State(start, null, 0, goal);
        s0.f = hVal(start);
        int[] result = rbfsSearch(s0, goal, Integer.MAX_VALUE);
        if (rbfsPath != null)
            printResult("RBFS", rbfsVisited, rbfsPath, rbfsCost);
        else
            printResult("RBFS", rbfsVisited, null, 0);
    }

    // returns [result_f, found_flag]
    static int[] rbfsSearch(State cur, String goal, int fLimit) {
        if (!rbfsVisited.contains(cur.node)) rbfsVisited.add(cur.node);

        if (cur.node.equals(goal)) {
            rbfsPath = extractPath(cur);
            rbfsCost = cur.g;
            return new int[]{cur.f, 1};
        }

        List<State> successors = new ArrayList<>();
        for (int[] nb : graph.get(cur.node)) {
            String next = nodeOf(nb[0]);
            if (cur.parent != null && next.equals(cur.parent.node)) continue;
            int newG = cur.g + nb[1];
            int newF = Math.max(newG + hVal(next), cur.f);
            successors.add(new State(next, cur, newG, newF));
        }

        if (successors.isEmpty()) return new int[]{Integer.MAX_VALUE, 0};

        successors.sort(Comparator.comparingInt(s -> s.f));

        while (true) {
            State best = successors.get(0);
            if (best.f > fLimit) return new int[]{best.f, 0};
            int alternative = successors.size() > 1 ? successors.get(1).f : Integer.MAX_VALUE;
            int[] res = rbfsSearch(best, goal, Math.min(fLimit, alternative));
            best.f = res[0];
            if (res[1] == 1) return res;
            successors.sort(Comparator.comparingInt(s -> s.f));
        }
    }

    // 6. MA* (Memory-Bounded A*) - simplified version
    //    Uses a fixed memory limit (max open nodes)
    static void maStar(String start, String goal, int memLimit) {
        List<String> visited = new ArrayList<>();
        PriorityQueue<State> open = new PriorityQueue<>(Comparator.comparingInt(s -> s.f));
        Map<String, Integer> bestG = new HashMap<>();

        open.add(new State(start, null, 0, goal));
        bestG.put(start, 0);

        while (!open.isEmpty()) {
            // Enforce memory limit: drop worst node if over limit
            while (open.size() > memLimit) {
                State worst = null;
                for (State st : open)
                    if (worst == null || st.f > worst.f) worst = st;
                open.remove(worst);
            }

            State cur = open.poll();
            if (visited.contains(cur.node)) continue;
            visited.add(cur.node);

            if (cur.node.equals(goal)) {
                printResult("MA* (memLimit=" + memLimit + ")", visited, extractPath(cur), cur.g);
                return;
            }
            for (int[] nb : graph.get(cur.node)) {
                String next = nodeOf(nb[0]);
                int newG = cur.g + nb[1];
                if (!bestG.containsKey(next) || newG < bestG.get(next)) {
                    bestG.put(next, newG);
                    open.add(new State(next, cur, newG, goal));
                }
            }
        }
        printResult("MA* (memLimit=" + memLimit + ")", visited, null, 0);
    }

    // 7. SMA* (Simplified Memory-Bounded A*)
    //    Keeps at most maxNodes in open; prunes worst-f leaf
    static void smaStar(String start, String goal, int maxNodes) {
        List<String> visited = new ArrayList<>();
        // Use TreeSet ordered by f then node name for determinism
        TreeMap<Integer, LinkedList<State>> open = new TreeMap<>();
        Map<String, Integer> bestG = new HashMap<>();

        State s0 = new State(start, null, 0, goal);
        addToOpen(open, s0);
        bestG.put(start, 0);
        int openCount = 1;

        while (!open.isEmpty()) {
            // Get best (lowest f)
            State cur = open.firstEntry().getValue().pollFirst();
            if (open.firstEntry().getValue().isEmpty())
                open.pollFirstEntry();
            openCount--;

            if (visited.contains(cur.node)) continue;
            visited.add(cur.node);

            if (cur.node.equals(goal)) {
                printResult("SMA* (maxNodes=" + maxNodes + ")", visited, extractPath(cur), cur.g);
                return;
            }

            for (int[] nb : graph.get(cur.node)) {
                String next = nodeOf(nb[0]);
                int newG = cur.g + nb[1];
                if (!bestG.containsKey(next) || newG < bestG.get(next)) {
                    bestG.put(next, newG);
                    State ns = new State(next, cur, newG, goal);
                    addToOpen(open, ns);
                    openCount++;

                    // Prune if over limit
                    if (openCount > maxNodes) {
                        // Remove worst-f entry
                        Map.Entry<Integer, LinkedList<State>> worst = open.lastEntry();
                        worst.getValue().pollLast();
                        if (worst.getValue().isEmpty()) open.pollLastEntry();
                        openCount--;
                    }
                }
            }
        }
        printResult("SMA* (maxNodes=" + maxNodes + ")", visited, null, 0);
    }

    static void addToOpen(TreeMap<Integer, LinkedList<State>> open, State s) {
        open.computeIfAbsent(s.f, k -> new LinkedList<>()).addLast(s);
    }

    // 8. SIMPLIFIED MA* (like SMA* but simpler structure)
    //    Expand best, prune worst when memory full
    static void simplifiedMAStar(String start, String goal, int maxNodes) {
        List<String> visited = new ArrayList<>();
        List<State> open = new ArrayList<>();
        Map<String, Integer> bestG = new HashMap<>();

        State s0 = new State(start, null, 0, goal);
        open.add(s0);
        bestG.put(start, 0);

        while (!open.isEmpty()) {
            // Sort: pick lowest f
            open.sort(Comparator.comparingInt(s -> s.f));
            State cur = open.remove(0);

            if (visited.contains(cur.node)) continue;
            visited.add(cur.node);

            if (cur.node.equals(goal)) {
                printResult("Simplified MA*", visited, extractPath(cur), cur.g);
                return;
            }

            for (int[] nb : graph.get(cur.node)) {
                String next = nodeOf(nb[0]);
                int newG = cur.g + nb[1];
                if (!bestG.containsKey(next) || newG < bestG.get(next)) {
                    bestG.put(next, newG);
                    open.add(new State(next, cur, newG, goal));
                }
            }

            // Prune worst if over memory limit
            while (open.size() > maxNodes) {
                open.sort(Comparator.comparingInt(s -> s.f));
                open.remove(open.size()-1); // remove highest f
            }
        }
        printResult("Simplified MA*", visited, null, 0);
    }

    // 9. BIDIRECTIONAL HEURISTIC SEARCH
    //    Forward: A* from start; Backward: A* from goal
    //    Meet in middle when frontiers intersect
    static void bidirectionalHeuristic(String start, String goal) {
        List<String> visited = new ArrayList<>();

        // Forward open (f = g + h_to_goal)
        PriorityQueue<State> fwdOpen = new PriorityQueue<>(Comparator.comparingInt(s -> s.f));
        Map<String, State> fwdClosed = new HashMap<>();
        Map<String, Integer> fwdG = new HashMap<>();

        // Backward open (f = g + h_from_goal_side, use h of node toward start)
        PriorityQueue<State> bwdOpen = new PriorityQueue<>(Comparator.comparingInt(s -> s.f));
        Map<String, State> bwdClosed = new HashMap<>();
        Map<String, Integer> bwdG = new HashMap<>();

        fwdOpen.add(new State(start, null, 0, goal));
        fwdG.put(start, 0);

        // For backward search heuristic: use same h (SLD to Bucharest)
        // but if goal != B, we just use h as is (still admissible for this map)
        bwdOpen.add(new State(goal, null, 0, start));
        bwdG.put(goal, 0);

        int bestCost = Integer.MAX_VALUE;
        String meetNode = null;

        while (!fwdOpen.isEmpty() || !bwdOpen.isEmpty()) {
            // Expand forward
            if (!fwdOpen.isEmpty()) {
                State cur = fwdOpen.poll();
                if (fwdClosed.containsKey(cur.node)) continue;
                fwdClosed.put(cur.node, cur);
                visited.add(cur.node + "(F)");

                if (bwdClosed.containsKey(cur.node)) {
                    int total = cur.g + bwdClosed.get(cur.node).g;
                    if (total < bestCost) { bestCost = total; meetNode = cur.node; }
                }

                for (int[] nb : graph.get(cur.node)) {
                    String next = nodeOf(nb[0]);
                    int newG = cur.g + nb[1];
                    if (!fwdClosed.containsKey(next) &&
                        (!fwdG.containsKey(next) || newG < fwdG.get(next))) {
                        fwdG.put(next, newG);
                        fwdOpen.add(new State(next, cur, newG, goal));
                    }
                }
            }

            // Expand backward
            if (!bwdOpen.isEmpty()) {
                State cur = bwdOpen.poll();
                if (bwdClosed.containsKey(cur.node)) continue;
                bwdClosed.put(cur.node, cur);
                visited.add(cur.node + "(B)");

                if (fwdClosed.containsKey(cur.node)) {
                    int total = cur.g + fwdClosed.get(cur.node).g;
                    if (total < bestCost) { bestCost = total; meetNode = cur.node; }
                }

                for (int[] nb : graph.get(cur.node)) {
                    String next = nodeOf(nb[0]);
                    int newG = cur.g + nb[1];
                    if (!bwdClosed.containsKey(next) &&
                        (!bwdG.containsKey(next) || newG < bwdG.get(next))) {
                        bwdG.put(next, newG);
                        bwdOpen.add(new State(next, cur, newG, start));
                    }
                }
            }

            // Termination: both frontiers have expanded enough
            int fwdMin = fwdOpen.isEmpty() ? Integer.MAX_VALUE : fwdOpen.peek().f;
            int bwdMin = bwdOpen.isEmpty() ? Integer.MAX_VALUE : bwdOpen.peek().f;
            if (meetNode != null && (fwdMin + bwdMin) >= bestCost) break;
        }

        System.out.println("\n=== Bidirectional Heuristic Search ===");
        System.out.println("Visit order: " + String.join(" -> ", visited));

        if (meetNode != null) {
            // Reconstruct path via meet node
            List<String> fwdPath = new ArrayList<>();
            State s = fwdClosed.get(meetNode);
            while (s != null) { fwdPath.add(0, s.node); s = s.parent; }

            List<String> bwdPath = new ArrayList<>();
            s = bwdClosed.get(meetNode);
            while (s != null) { bwdPath.add(s.node); s = s.parent; }

            // Combine: fwdPath + bwdPath[1:] (skip duplicate meetNode)
            for (int i = 1; i < bwdPath.size(); i++) fwdPath.add(bwdPath.get(i));

            System.out.println("Meeting node: " + meetNode);
            System.out.println("Path        : " + String.join(" -> ", fwdPath));
            System.out.println("Path Cost   : " + bestCost);
        } else {
            System.out.println("Goal not reachable.");
        }
    }

    // MAIN
   public static void main(String[] args) {
    buildGraph();
    Scanner sc = new Scanner(System.in);
    printNodeRef();

    System.out.println("\n===========================================");
    System.out.println("  Romania Map - Heuristic Search");
    System.out.println("  Algorithms:");
    System.out.println("  1. A* Search");
    System.out.println("  2. Iterative Deepening A*");
    System.out.println("  3. Greedy Best-First Search");
    System.out.println("  4. Weighted A*");
    System.out.println("  5. Recursive Best-First Search");
    System.out.println("  6. Memory-Bounded A*");
    System.out.println("  7. Simplified Memory-Bounded A*");
    System.out.println("  8. Simplified MA*");
    System.out.println("  9. Bidirectional Heuristic Search");
    System.out.println("===========================================");

    System.out.print("\nEnter START node (e.g. A, RV, B): ");
    String start = sc.next().trim().toUpperCase();

    System.out.print("Enter GOAL  node (e.g. B, E, N): ");
    String goal = sc.next().trim().toUpperCase();

    if (!graph.containsKey(start)) { System.out.println("Unknown start: " + start); return; }
    if (!graph.containsKey(goal))  { System.out.println("Unknown goal: "  + goal);  return; }

    System.out.print("Enter algorithm number (1-9): ");
    int algo = sc.nextInt();

    switch (algo) {
        case 1:
            aStar(start, goal);
            break;
        case 2:
            idaStar(start, goal);
            break;
        case 3:
            greedyBestFirst(start, goal);
            break;
        case 4:
            System.out.print("Enter weight w (e.g. 1.5): ");
            double w = sc.nextDouble();
            weightedAStar(start, goal, w);
            break;
        case 5:
            rbfs(start, goal);
            break;
        case 6:
            System.out.print("Enter memory limit (max open nodes, e.g. 10): ");
            int ml = sc.nextInt();
            maStar(start, goal, ml);
            break;
        case 7:
            System.out.print("Enter max nodes in memory (e.g. 8): ");
            int mn = sc.nextInt();
            smaStar(start, goal, mn);
            break;
        case 8:
            System.out.print("Enter max open list size (e.g. 10): ");
            int ms = sc.nextInt();
            simplifiedMAStar(start, goal, ms);
            break;
        case 9:
            bidirectionalHeuristic(start, goal);
            break;
        default:
            System.out.println("Invalid choice. Please enter a number between 1 and 9.");
    }

    sc.close();
	}
}