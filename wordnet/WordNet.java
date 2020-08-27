/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;

public class WordNet {

    // stores the nouns
    private ArrayList<SSNode> ssa = new ArrayList<>();
    private Digraph dg;

    private static class SSNode {
        int id;
        String[] nouns;

        public SSNode(int id, String[] nouns) {
            this.id = id;
            this.nouns = nouns;
        }
    }

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("WordNet constructor arguments are null");

        In in = new In(synsets);
        String line;

        int j = 0;
        while (!in.isEmpty()) {
            line = in.readLine();
            String[] fields = line.split(",");
            int idx = Integer.parseInt(fields[0]);
            String[] nouns = fields[1].split(" ");

            SSNode node = new SSNode(idx, nouns);
            ssa.add(node);

            j++;
            // if (j > 20) break;
        }

        for (int i = 0; i < ssa.size(); i++) {
            SSNode n = ssa.get(i);
            if (n.id != i) System.out.println("ERROR!!"); // sanity check
            // System.out.println(i + ":" + Arrays.toString(n.nouns));
        }

        // Now read hypernims and build Digraph
        dg = new Digraph(ssa.size());

        in = new In(hypernyms);
        j = 0;
        while (!in.isEmpty()) {
            line = in.readLine();
            String[] nums = line.split(",");
            int v = Integer.parseInt(nums[0]);
            for (int i = 1; i < nums.length; i++) {
                int w = Integer.parseInt(nums[i]);
                dg.addEdge(v, w);
                // System.out.println("add edge " + v + "->" + w);
            }
            j++;
            // if (j > 35) break;
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return null;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return false;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        return 0;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        return "";
    }

    // do unit testing of this class
    public static void main(String[] args) {
        String synsets = args[0];
        String hypernyms = args[1];
        WordNet wn = new WordNet(synsets, hypernyms);
    }
}
