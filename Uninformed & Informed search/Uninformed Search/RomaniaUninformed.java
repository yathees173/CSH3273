import java.util.*;

public class RomaniaUninformed {

    // GRAPH SETUP
    static Map<String, List<int[]>> graph = new HashMap<>();
    // int[] = {neighborIndex, cost}
    static String[] nodes = {
        "A",  // 0  Arad
        "O",  // 1  Oradea
        "Z",  // 2  Zerind
        "T",  // 3  Timisoara
        "L",  // 4  Lugoj
        "M",  // 5  Mehadia
        "D",  // 6  Drobeta
        "C",  // 7  Craiova
        "S",  // 8  Sibiu
        "F",  // 9  Fagaras
        "RV", // 10 Rimnicu Vilcea
        "P",  // 11 Pitesti
        "B",  // 12 Bucharest
        "G",  // 13 Giurgiu
        "U",  // 14 Urziceni
        "H",  // 15 Hirsova
        "E",  // 16 Eforie
        "V",  // 17 Vaslui
        "I",  // 18 Iasi
        "N"   // 19 Neamt
    };

    static Map<String, String> fullName = new LinkedHashMap<>();

    static void buildGraph() {
        // Full names for display
        fullName.put("A",  "Arad");
        fullName.put("O",  "Oradea");
        fullName.put("Z",  "Zerind");
        fullName.put("T",  "Timisoara");
        fullName.put("L",  "Lugoj");
        fullName.put("M",  "Mehadia");
        fullName.put("D",  "Drobeta");
        fullName.put("C",  "Craiova");
        fullName.put("S",  "Sibiu");
        fullName.put("F",  "Fagaras");
        fullName.put("RV", "Rimnicu Vilcea");
        fullName.put("P",  "Pitesti");
        fullName.put("B",  "Bucharest");
        fullName.put("G",  "Giurgiu");
        fullName.put("U",  "Urziceni");
        fullName.put("H",  "Hirsova");
        fullName.put("E",  "Eforie");
        fullName.put("V",  "Vaslui");
        fullName.put("I",  "Iasi");
        fullName.put("N",  "Neamt");

        // Initialize adjacency lists
        for (String n : nodes) graph.put(n, new ArrayList<>());

        // Add edges (bidirectional) using node letters
        addEdge("A",  "Z",  75);
        addEdge("A",  "O",  146); // Arad-Oradea via Zerind: 75+71=146, but map shows A-O=146? 
        // Actually from map: A-Z=75, Z-O=71, A-S=140, A-T=118
        // Let me re-read the map carefully:
        // A--Z: 75, Z--O: 71, A--O: not direct (goes through Z)
        // A--T: 118, A--S: 140
        // Re-do correctly:
        graph.get("A").clear(); graph.get("Z").clear();
        graph.get("O").clear(); graph.get("T").clear();
        graph.get("S").clear();

        addEdge("A",  "Z",  75);
        addEdge("A",  "T",  118);
        addEdge("A",  "S",  140);
        addEdge("Z",  "O",  71);
        addEdge("O",  "S",  151);
        addEdge("T",  "L",  111);
        addEdge("L",  "M",  70);
        addEdge("M",  "D",  75);
        addEdge("D",  "C",  120);
        addEdge("S",  "F",  99);
        addEdge("S",  "RV", 80);
        addEdge("F",  "B",  211);
        addEdge("RV", "P",  97);
        addEdge("RV", "C",  146);
        addEdge("P",  "B",  101);
        addEdge("P",  "C",  138);
        addEdge("B",  "G",  90);
        addEdge("B",  "U",  85);
        addEdge("U",  "H",  98);
        addEdge("U",  "V",  142);
        addEdge("H",  "E",  86);
        addEdge("V",  "I",  92);
        addEdge("I",  "N",  87);
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

    // PRINT HELPERS
    static void printNodeList(String label) {
        System.out.println("\n=== Node List ===");
        for (String n : nodes)
            System.out.println("  " + n + " = " + fullName.get(n));
    }

    static void printPath(List<String> path, int cost) {
        System.out.print("Path: ");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));
            if (i < path.size() - 1) System.out.print(" -> ");
        }
        System.out.println();
        System.out.println("Path Cost: " + cost);
    }

    // BFS
    static void bfs(String start, String goal) {
        System.out.println("\n=== BFS ===");
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new LinkedHashSet<>();

        queue.add(start);
        visited.add(start);
        parent.put(start, null);

        System.out.print("Visit order: ");
        boolean found = false;

        while (!queue.isEmpty()) {
            String cur = queue.poll();
            System.out.print(cur + " ");

            if (cur.equals(goal)) { found = true; break; }

            for (int[] nb : graph.get(cur)) {
                String next = nodeOf(nb[0]);
                if (!visited.contains(next)) {
                    visited.add(next);
                    parent.put(next, cur);
                    queue.add(next);
                }
            }
        }
        System.out.println();

        if (found) {
            List<String> path = buildPath(parent, start, goal);
            int cost = pathCost(path);
            printPath(path, cost);
        } else {
            System.out.println("Goal not reachable.");
        }
    }

    // DFS
    static void dfs(String start, String goal) {
        System.out.println("\n=== DFS ===");
        Stack<String> stack = new Stack<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new LinkedHashSet<>();

        stack.push(start);
        parent.put(start, null);
        System.out.print("Visit order: ");
        boolean found = false;

        while (!stack.isEmpty()) {
            String cur = stack.pop();
            if (visited.contains(cur)) continue;
            visited.add(cur);
            System.out.print(cur + " ");

            if (cur.equals(goal)) { found = true; break; }

            for (int[] nb : graph.get(cur)) {
                String next = nodeOf(nb[0]);
                if (!visited.contains(next)) {
                    stack.push(next);
                    if (!parent.containsKey(next))
                        parent.put(next, cur);
                }
            }
        }
        System.out.println();

        if (found) {
            List<String> path = buildPath(parent, start, goal);
            int cost = pathCost(path);
            printPath(path, cost);
        } else {
            System.out.println("Goal not reachable.");
        }
    }

    // UCS (Uniform Cost Search)
    static void ucs(String start, String goal) {
        System.out.println("\n=== UCS ===");
        // PriorityQueue: [costSoFar, nodeIndex, parentNode]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(x -> x[0]));
        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> costMap = new HashMap<>();
        Set<String> visited = new LinkedHashSet<>();

        pq.add(new int[]{0, indexOf(start), -1});
        costMap.put(start, 0);
        parent.put(start, null);

        System.out.print("Visit order: ");
        boolean found = false;

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int curCost = cur[0];
            String curNode = nodeOf(cur[1]);

            if (visited.contains(curNode)) continue;
            visited.add(curNode);
            System.out.print(curNode + " ");

            if (curNode.equals(goal)) { found = true; break; }

            for (int[] nb : graph.get(curNode)) {
                String next = nodeOf(nb[0]);
                int newCost = curCost + nb[1];
                if (!visited.contains(next) &&
                    (!costMap.containsKey(next) || newCost < costMap.get(next))) {
                    costMap.put(next, newCost);
                    parent.put(next, curNode);
                    pq.add(new int[]{newCost, nb[0], indexOf(curNode)});
                }
            }
        }
        System.out.println();

        if (found) {
            List<String> path = buildPath(parent, start, goal);
            int cost = pathCost(path);
            printPath(path, cost);
        } else {
            System.out.println("Goal not reachable.");
        }
    }

    // DLS (Depth Limited Search)
    static Map<String, String> dlsParent;
    static boolean dlsFound;

    static void dls(String start, String goal, int limit) {
        System.out.println("\n=== DLS (limit=" + limit + ") ===");
        dlsParent = new HashMap<>();
        dlsFound = false;
        dlsParent.put(start, null);
        System.out.print("Visit order: ");
        dlsRecursive(start, goal, limit, new LinkedHashSet<>());
        System.out.println();

        if (dlsFound) {
			System.out.println("Solution found at depth limit: " + limit);
            List<String> path = buildPath(dlsParent, start, goal);
            int cost = pathCost(path);
            printPath(path, cost);
        } else {
            System.out.println("Goal not found within depth limit " + limit);
        }
    }

    static void dlsRecursive(String cur, String goal, int limit, Set<String> visited) {
		visited.add(cur);
		System.out.print(cur + " ");

		if (cur.equals(goal)) { dlsFound = true; return; }
		if (limit == 0) {
			visited.remove(cur); 
			return;
		}

		for (int[] nb : graph.get(cur)) {
			String next = nodeOf(nb[0]);
			if (!visited.contains(next)) {
				dlsParent.put(next, cur);
				dlsRecursive(next, goal, limit - 1, visited);
				if (dlsFound) return;
			}
		}
		visited.remove(cur);
	}

    // IDS (Iterative Deepening Search)
    static void ids(String start, String goal) {
        System.out.println("\n=== IDS ===");
        for (int depth = 0; depth <= 20; depth++) {
            dlsParent = new HashMap<>();
            dlsFound = false;
            dlsParent.put(start, null);
            System.out.print("  Depth " + depth + " visit order: ");
            dlsRecursive(start, goal, depth, new LinkedHashSet<>());
            System.out.println();

            if (dlsFound) {
                System.out.println("Found at depth: " + depth);
                List<String> path = buildPath(dlsParent, start, goal);
                int cost = pathCost(path);
                printPath(path, cost);
                return;
            }
        }
        System.out.println("Goal not reachable.");
    }

    // BDS (Bidirectional Search)
    static void bds(String start, String goal) {
        System.out.println("\n=== BDS (Bidirectional BFS) ===");

        // Forward frontier
        Queue<String> fwdQ = new LinkedList<>();
        Map<String, String> fwdParent = new HashMap<>();
        Set<String> fwdVisited = new HashSet<>();

        // Backward frontier
        Queue<String> bwdQ = new LinkedList<>();
        Map<String, String> bwdParent = new HashMap<>();
        Set<String> bwdVisited = new HashSet<>();

        fwdQ.add(start); fwdVisited.add(start); fwdParent.put(start, null);
        bwdQ.add(goal);  bwdVisited.add(goal);  bwdParent.put(goal, null);

        String meetNode = null;

        System.out.print("Forward visits: ");
        outer:
        while (!fwdQ.isEmpty() || !bwdQ.isEmpty()) {
            // Expand forward
            if (!fwdQ.isEmpty()) {
                String cur = fwdQ.poll();
                System.out.print(cur + "(F) ");
                if (bwdVisited.contains(cur)) { meetNode = cur; break outer; }
                for (int[] nb : graph.get(cur)) {
                    String next = nodeOf(nb[0]);
                    if (!fwdVisited.contains(next)) {
                        fwdVisited.add(next);
                        fwdParent.put(next, cur);
                        fwdQ.add(next);
                    }
                }
            }
            // Expand backward
            if (!bwdQ.isEmpty()) {
                String cur = bwdQ.poll();
                System.out.print(cur + "(B) ");
                if (fwdVisited.contains(cur)) { meetNode = cur; break outer; }
                for (int[] nb : graph.get(cur)) {
                    String next = nodeOf(nb[0]);
                    if (!bwdVisited.contains(next)) {
                        bwdVisited.add(next);
                        bwdParent.put(next, cur);
                        bwdQ.add(next);
                    }
                }
            }
        }
        System.out.println();

        if (meetNode == null) {
            System.out.println("Goal not reachable.");
            return;
        }

        System.out.println("Meeting point: " + meetNode);

        // Build forward path: start -> meetNode
        List<String> fwdPath = new ArrayList<>();
        String cur = meetNode;
        while (cur != null) { fwdPath.add(0, cur); cur = fwdParent.get(cur); }

        // Build backward path: meetNode -> goal
        List<String> bwdPath = new ArrayList<>();
        cur = meetNode;
        while (cur != null) { bwdPath.add(cur); cur = bwdParent.get(cur); }

        // Combine (remove duplicate meetNode)
        List<String> fullPath = new ArrayList<>(fwdPath);
        for (int i = 1; i < bwdPath.size(); i++) fullPath.add(bwdPath.get(i));

        int cost = pathCost(fullPath);
        printPath(fullPath, cost);
    }

    // HELPERS
    static List<String> buildPath(Map<String, String> parent, String start, String goal) {
        List<String> path = new ArrayList<>();
        String cur = goal;
        while (cur != null) {
            path.add(0, cur);
            cur = parent.get(cur);
        }
        return path;
    }

    static int pathCost(List<String> path) {
        int total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String a = path.get(i), b = path.get(i + 1);
            for (int[] nb : graph.get(a)) {
                if (nodeOf(nb[0]).equals(b)) { total += nb[1]; break; }
            }
        }
        return total;
    }

    // MAIN
    public static void main(String[] args) {
        buildGraph();
        Scanner sc = new Scanner(System.in);

        // Print node reference
        printNodeList("");

        System.out.println("\n--- Romania Map Search ---");
        System.out.println("Algorithms: BFS | DFS | UCS | IDS | DLS | BDS");

        System.out.print("\nEnter START node: ");
        String start = sc.next().trim().toUpperCase();

        System.out.print("Enter GOAL  node: ");
        String goal  = sc.next().trim().toUpperCase();

        // Validate
        if (!graph.containsKey(start)) { System.out.println("Unknown start node: " + start); return; }
        if (!graph.containsKey(goal))  { System.out.println("Unknown goal node: "  + goal);  return; }

        System.out.print("Enter algorithm (BFS/DFS/UCS/IDS/DLS/BDS): ");
        String algo = sc.next().trim().toUpperCase();

        switch (algo) {
            case "BFS": bfs(start, goal); break;
            case "DFS": dfs(start, goal); break;
            case "UCS": ucs(start, goal); break;
            case "IDS": ids(start, goal); break;
            case "DLS":
                System.out.print("Enter depth limit: ");
                int limit = sc.nextInt();
                dls(start, goal, limit);
                break;
            case "BDS": bds(start, goal); break;
            default: System.out.println("Unknown algorithm: " + algo);
        }

        sc.close();
    }
}