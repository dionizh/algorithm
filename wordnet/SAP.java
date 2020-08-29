/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private Digraph dg;
    private DeluxeBFS[] bfsST;

    private class MiniBFS {
        boolean[] marked;  // marked[v] = is there an s->v path?
        int[] edgeTo;      // edgeTo[v] = last edge on shortest s->v path
        int[] distTo;      // distTo[v] = length of shortest s->v path
        Queue<Integer> q;

        public MiniBFS(int vertices, int s) {
            marked = new boolean[vertices];
            distTo = new int[vertices];
            edgeTo = new int[vertices];
            q = new Queue<Integer>();

            // initialisation
            marked[s] = true;
            distTo[s] = 0;
            q.enqueue(s);
        }

        public void resetQueue(int s) {
            q = new Queue<Integer>();
            q.enqueue(s);
        }

        public int getNext() {
            int v = q.dequeue();
            for (int w : dg.adj(v)) {
                if (!marked[w]) {
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    marked[w] = true;
                    q.enqueue(w);
                }
            }
            return v;
        }
    }

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Digraph is null");
        dg = G;
        bfsST = new DeluxeBFS[dg.V()];
        for (int i = 0; i < bfsST.length; i++) {
            bfsST[i] = null;
        }
    }

    private DeluxeBFS getBFS(int v) {
        if (bfsST[v] == null) bfsST[v] = new DeluxeBFS(dg, v);
        return bfsST[v];
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        DeluxeBFS bv = getBFS(v);
        DeluxeBFS bw = getBFS(w);

        int anc = ancestor(v, w);
        if (anc != -1) return bv.distTo(anc) + bw.distTo(anc);
        
        return -1; // no common anc
    }

    private int bfs(int s, DeluxeBFS bfsw) {
        Queue<Integer> q = new Queue<Integer>();
        boolean[] marked = new boolean[dg.V()];  // marked[v] = is there an s->v path?
        marked[s] = true;
        q.enqueue(s);
        while (!q.isEmpty()) {
            int v = q.dequeue();
            if (bfsw.hasPathTo(v)) return v;
            for (int w : dg.adj(v)) {
                if (!marked[w]) {
                    marked[w] = true;
                    q.enqueue(w);
                }
            }
        }
        return -1;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        DeluxeBFS bw = getBFS(w);
        // bw.printPaths();
        return bfs(v, bw);

        // int shortest = Integer.MAX_VALUE;
        // int commonanc = -1;
        // if (bv.ancestorCount() > bw.ancestorCount()) {
        //     for (int a : bv.ancestors()) {
        //         if (bw.hasPathTo(a)) {
        //             int dist = bv.distTo(a) + bw.distTo(a);
        //             if (dist < shortest) {
        //                 shortest = dist;
        //                 commonanc = a;
        //             }
        //         }
        //     }
        // }
        // else {
        //     for (int a : bw.ancestors()) {
        //         if (bv.hasPathTo(a)) {
        //             int dist = bv.distTo(a) + bw.distTo(a);
        //             if (dist < shortest) {
        //                 shortest = dist;
        //                 commonanc = a;
        //             }
        //         }
        //     }
        // }
        // StdOut.println("length=" + shortest);
        // return commonanc;

        // MiniBFS mbv = getBFS(v);
        // MiniBFS mbw = getBFS(w);
        //
        // int p1 = v;
        // int p2 = w;
        // boolean flip = true;
        // int length = 0;
        // while (!mbv.q.isEmpty() || !mbw.q.isEmpty()) {
        //     if (flip) {
        //         if (!mbv.q.isEmpty()) {
        //             p1 = mbv.getNext();
        //             // check p2 and follow its path to see if any matches
        //             int x;
        //             for (x = p2; mbw.distTo[x] != 0; x = mbw.edgeTo[x]) {
        //                 StdOut.println("p1 move | check p1=" + p1 + " p2=" + x);
        //                 if (p1 == x) return p1;
        //             }
        //             StdOut.println("p1 move | check p1=" + p1 + " p2=" + x);
        //             if (p1 == x) return p1;
        //         }
        //     }
        //     else {
        //         if (!mbw.q.isEmpty()) {
        //             p2 = mbw.getNext();
        //             int x;
        //             for (x = p1; mbv.distTo[x] != 0; x = mbv.edgeTo[x]) {
        //                 StdOut.println("p2 move | check p1=" + x + " p2=" + p2);
        //                 if (p2 == x) return p2;
        //             }
        //             StdOut.println("p2 move | check p1=" + x + " p2=" + p2);
        //             if (p2 == x) return p2;
        //         }
        //     }
        //     flip = !flip;
        // }

        // return -1; // no common ancestor
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("v/w is null");
        return 0;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("v/w is null");
        return 0;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        int v = Integer.parseInt(args[1]);
        int w = Integer.parseInt(args[2]);
        Digraph dg = new Digraph(in);
        SAP sap = new SAP(dg);
        StdOut.println("length=" + sap.length(v, w) + " ancestor=" + sap.ancestor(v, w));
    }
}
