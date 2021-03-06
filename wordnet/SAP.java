/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.HashMap;

public class SAP {

    private static final int MAXHASH = 1000;

    private final Digraph dg;
    private final HashMap<String, DeluxeBFS> bfsmap = new HashMap<>();
    private final Queue<String> hashorder = new Queue<>();

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Digraph is null");
        dg = new Digraph(G);
    }

    // bfs search that checks at every step whether the vertex
    // is on the path of the other BFS
    private int bfsAncestor(int s, DeluxeBFS bfsw) {
        Queue<Integer> q = new Queue<Integer>();
        boolean[] marked = new boolean[dg.V()];  // marked[v] = is there an s->v path?
        int[] distTo = new int[dg.V()]; // distTo[v] = length of shortest s->v path
        marked[s] = true;
        distTo[s] = 0;
        q.enqueue(s);

        int mindist = Integer.MAX_VALUE;
        int anc = -1;
        while (!q.isEmpty()) {
            int v = q.dequeue();
            // StdOut.println("check v=" + v + " w hasPathTo v=" + bfsw.hasPathTo(v));
            if (bfsw.hasPathTo(v)) {
                int dist = distTo[v] + bfsw.distTo(v);
                if (dist < mindist) {
                    mindist = dist;
                    anc = v;
                }
            }
            for (int w : dg.adj(v)) {
                if (!marked[w]) {
                    marked[w] = true;
                    distTo[w] = distTo[v] + 1;
                    q.enqueue(w);
                }
            }
        }
        // StdOut.println("FOUND ancestor=" + anc);
        return anc;
    }

    private int bfsAncestor(Iterable<Integer> sources, DeluxeBFS bfsw) {
        Queue<Integer> q = new Queue<Integer>();
        boolean[] marked = new boolean[dg.V()];  // marked[v] = is there an s->v path?
        int[] distTo = new int[dg.V()]; // distTo[v] = length of shortest s->v path
        for (int s : sources) {
            marked[s] = true;
            q.enqueue(s);
        }

        int mindist = Integer.MAX_VALUE;
        int anc = -1;
        while (!q.isEmpty()) {
            int v = q.dequeue();
            // StdOut.println("check v=" + v + " w hasPathTo v=" + bfsw.hasPathTo(v));
            if (bfsw.hasPathTo(v)) {
                int dist = distTo[v] + bfsw.distTo(v);
                // StdOut.println(
                //         "dist v=" + distTo[v] + " dist w=" + bfsw.distTo(v) + " total=" + dist);
                if (dist < mindist) {
                    mindist = dist;
                    anc = v;
                }
            }
            for (int w : dg.adj(v)) {
                if (!marked[w]) {
                    marked[w] = true;
                    distTo[w] = distTo[v] + 1;
                    q.enqueue(w);
                }
            }
        }
        return anc;
    }

    private DeluxeBFS getBFS(int v) {
        validateVertex(v);
        if (!bfsmap.containsKey(Integer.toString(v))) {
            DeluxeBFS newbfs = new DeluxeBFS(dg, v);
            String key = Integer.toString(v);
            bfsmap.put(key, newbfs);
            hashorder.enqueue(key);
            if (bfsmap.size() > MAXHASH) {
                bfsmap.remove(hashorder.dequeue()); // remove the oldest
            }
            return newbfs;
        }
        return bfsmap.get(Integer.toString(v));
    }

    private DeluxeBFS getBFS(Iterable<Integer> v) {
        validateVertices(v);

        StringBuilder sb = new StringBuilder();
        for (int vert : v) {
            sb.append(Integer.toString(vert) + "-");
        }
        String key = sb.toString();
        if (!bfsmap.containsKey(key)) {
            DeluxeBFS newbfs = new DeluxeBFS(dg, v);
            bfsmap.put(key, newbfs);
            hashorder.enqueue(key);
            if (bfsmap.size() > MAXHASH) {
                bfsmap.remove(hashorder.dequeue()); // remove the oldest
            }
            return newbfs;
        }
        return bfsmap.get(key);
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int length = dg.V();
        if (v < 0 || v >= length)
            throw new IllegalArgumentException(
                    "vertex " + v + " is not between 0 and " + (length - 1));
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        for (Integer v : vertices) {
            if (v == null) {
                throw new IllegalArgumentException("vertex is null");
            }
            validateVertex(v);
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        DeluxeBFS bv = getBFS(v);
        DeluxeBFS bw = getBFS(w);

        int anc = ancestor(v, w);
        if (anc != -1) return bv.distTo(anc) + bw.distTo(anc);

        return -1; // no common anc
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        DeluxeBFS bw = getBFS(w);
        return bfsAncestor(v, bw);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        DeluxeBFS bv = getBFS(v);
        DeluxeBFS bw = getBFS(w);
        int anc = ancestor(v, w);
        if (anc != -1) return bv.distTo(anc) + bw.distTo(anc);
        return -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);

        DeluxeBFS bw = getBFS(w);
        return bfsAncestor(v, bw);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph dg = new Digraph(in);
        SAP sap = new SAP(dg);

        int v, w;
        for (int i = 0; i < 10; i++) {
            v = StdRandom.uniform(0, dg.V());
            w = StdRandom.uniform(0, dg.V());
            StdOut.println("v=" + v + " w=" + w);
            StdOut.println("length=" + sap.length(v, w));
            StdOut.println("ancestor=" + sap.ancestor(v, w));
        }

        // Queue<Integer> v = new Queue<>();
        // Queue<Integer> w = new Queue<>();
        // for (int i = 0; i < 1000; i++) {
        //     v.enqueue(StdRandom.uniform(0, dg.V()));
        //     w.enqueue(StdRandom.uniform(0, dg.V()));
        // }
        // StdOut.println("v=" + v + " w=" + w);
        // StdOut.println("length=" + sap.length(v, w));
        // StdOut.println("ancestor=" + sap.ancestor(v, w));

        // int v = Integer.parseInt(args[1]);
        // int w = Integer.parseInt(args[2]);
        // DeluxeBFS b = new DeluxeBFS(dg, v);
        // b.printPaths(v);
        // DeluxeBFS b2 = new DeluxeBFS(dg, w);
        // b2.printPaths(w);

        // Queue<Integer> v = new Queue<>();
        // v.enqueue(0);
        // v.enqueue(null);
        // v.enqueue(9);
        // v.enqueue(12);
        // v.enqueue(-1);
        // Queue<Integer> w = new Queue<>();
        // w.enqueue(-1);
        // w.enqueue(16);
        // w.enqueue(17);

    }
}
