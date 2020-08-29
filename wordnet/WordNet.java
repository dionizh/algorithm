/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Topological;

import java.util.TreeMap;

public class WordNet {

    // stores nouns as keys with id as values
    private final TreeMap<String, Queue<Integer>> tm = new TreeMap<>();
    // stores the nouns with id as key
    private final String[] synsets;

    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("WordNet constructor arguments are null");

        In in = new In(synsets);
        String line;

        Queue<String> ssq = new Queue<>();
        while (!in.isEmpty()) {
            line = in.readLine();
            String[] fields = line.split(",");
            int idx = Integer.parseInt(fields[0]);
            ssq.enqueue(fields[1]);

            String[] nouns = fields[1].split(" ");
            for (int i = 0; i < nouns.length; i++) {
                String noun = nouns[i];
                if (tm.containsKey(noun)) tm.get(noun).enqueue(idx);
                else {
                    Queue<Integer> q = new Queue<>();
                    q.enqueue(idx);
                    tm.put(noun, q);
                }
            }
        }

        this.synsets = new String[ssq.size()];
        for (int i = 0; i < this.synsets.length; i++) {
            this.synsets[i] = ssq.dequeue();
        }

        // Now read hypernims and build Digraph
        Digraph dg = new Digraph(this.synsets.length);

        in = new In(hypernyms);
        while (!in.isEmpty()) {
            line = in.readLine();
            String[] nums = line.split(",");
            int v = Integer.parseInt(nums[0]);
            for (int i = 1; i < nums.length; i++) {
                int w = Integer.parseInt(nums[i]);
                dg.addEdge(v, w);
            }
        }

        // check cycle
        Topological topo = new Topological(dg);
        if (!topo.hasOrder()) throw new IllegalArgumentException("NOT a DAG!");
        int roots = 0;
        for (int i = 0; i < dg.V(); i++) {
            if (dg.outdegree(i) == 0) roots++;
        }
        if (roots > 1) throw new IllegalArgumentException("Not a rooted DAG!");

        sap = new SAP(dg);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return tm.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("word is null");
        return tm.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("distance: noun A or B is illegal");
        return sap.length(tm.get(nounA), tm.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("distance: noun A or B is illegal");
        int anc = sap.ancestor(tm.get(nounA), tm.get(nounB));
        if (anc != -1) return synsets[anc];
        return null;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        String synsets = args[0];
        String hypernyms = args[1];
        WordNet wn = new WordNet(synsets, hypernyms);
        // for (String noun : wn.nouns()) StdOut.println(noun);
        wn.sap("a", null);
    }
}
