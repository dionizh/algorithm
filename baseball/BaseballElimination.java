/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class BaseballElimination {

    private final String[] teams;
    private final int[] w;
    private final int[] lo;
    private final int[] r;
    private final int[][] g;

    private Bag<Integer>[] eliminations;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int teamcount = in.readInt();

        teams = new String[teamcount];
        w = new int[teamcount];
        lo = new int[teamcount];
        r = new int[teamcount];
        g = new int[teamcount][teamcount];
        eliminations = (Bag<Integer>[]) new Bag[teamcount];

        for (int i = 0; i < teamcount; i++) {
            teams[i] = in.readString();
            w[i] = in.readInt();
            lo[i] = in.readInt();
            r[i] = in.readInt();
            for (int j = 0; j < teamcount; j++) {
                g[i][j] = in.readInt();
            }
            eliminations[i] = null;
        }

        // for (int i = 0; i < teamcount; i++) {
        //     StdOut.printf("%s %15s %4d %4d %4d   ", i, teams[i], w[i], l[i], r[i]);
        //     for (int j = 0; j < teamcount; j++) {
        //         StdOut.printf("%4d ", g[i][j]);
        //     }
        //     StdOut.println();
        // }
    }

    // number of teams
    public int numberOfTeams() {
        return teams.length;
    }

    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }

    private int teamidx(String team) {
        int i = 0;
        for (String t : teams) {
            if (t.equals(team)) return i;
            i++;
        }
        throw new IllegalArgumentException("Team " + team + " does not exist");
    }

    // number of wins for given team
    public int wins(String team) {
        return w[teamidx(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return lo[teamidx(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return r[teamidx(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return g[teamidx(team1)][teamidx(team2)];
    }

    private void eliminate(int qteam) {
        if (eliminations[qteam] != null) return; // already done

        eliminations[qteam] = new Bag<>();

        // -------------------------------------------------------
        // trivial elimination
        Queue<Integer> tq = new Queue<>();
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == qteam) continue;
            // StdOut.println("w[q] + r[q]=" + (w[qteam] + r[qteam]) + " i=" + i + " w[i]=" + w[i]);
            if (w[qteam] + r[qteam] >= w[i]) {
                tq.enqueue(i);
            }
            else {
                eliminations[qteam].add(i);
            }
        }
        int[] oppTeams = new int[tq.size()];
        int p = 0;
        for (int t : tq) oppTeams[p++] = t;
        // StdOut.println("Opposing teams: " + Arrays.toString(oppTeams));

        if (oppTeams.length == 0)
            return; // no need to check further

        // -------------------------------------------------------
        // nontrivial elimination
        int oppTeamsCount = oppTeams.length;
        int gameVCount;
        if (oppTeamsCount == 1) gameVCount = 0;
        else if (oppTeamsCount == 2) gameVCount = 1;
        else gameVCount = (oppTeamsCount) * (oppTeamsCount - 1) / 2;

        // total vertices: 1 source + 1 target + # of games + # of opposing teams
        int vCount = 2 + gameVCount + oppTeamsCount;
        FlowNetwork fnet = new FlowNetwork(vCount);

        int sourceV = 0;
        int gameV = 1; // game vertices starting from this
        int teamV = gameV + gameVCount; // team vertices starting from this

        // loop through the set of just opposing teams
        for (int i = 0; i < oppTeamsCount; i++) {
            for (int j = i + 1; j < oppTeamsCount; j++) {
                FlowEdge fe = new FlowEdge(0, gameV, g[oppTeams[i]][oppTeams[j]]);
                fnet.addEdge(fe);

                fe = new FlowEdge(gameV, teamV + i, Double.POSITIVE_INFINITY);
                fnet.addEdge(fe);

                fe = new FlowEdge(gameV, teamV + j, Double.POSITIVE_INFINITY);
                fnet.addEdge(fe);

                gameV++;
            }
        }

        int targetV = teamV + oppTeamsCount;
        for (int i = 0; i < oppTeamsCount; i++) {
            int ot = oppTeams[i];
            int capacity = w[qteam] + r[qteam] - w[ot];
            // StdOut.println("team->target " + (teamV + i) + "->" + targetV + ": " + capacity);
            if (capacity < 0)
                StdOut.println("Team " + i + " should've been trivially eliminated");
            FlowEdge fe = new FlowEdge(teamV + i, targetV, capacity);
            fnet.addEdge(fe);
        }

        // StdOut.println(fnet.toString());
        FordFulkerson ff = new FordFulkerson(fnet, sourceV, targetV);
        // StdOut.println("ff value=" + ff.value());
        for (int i = teamV; i < teamV + oppTeamsCount; i++) {
            // StdOut.println("v " + i + " inCut=" + ff.inCut(i) + " team=" + oppTeams[i - teamV]);
            if (ff.inCut(i)) {
                eliminations[qteam].add(oppTeams[i - teamV]);
            }
        }
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        // team in question
        int qteam = teamidx(team);
        eliminate(qteam);
        // the certificate of elimination requires two or more teams
        if (eliminations[qteam].size() > 0)
            return true; // no chance for team to win (eliminated)
        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        int qteam = teamidx(team);
        eliminate(qteam);
        if (eliminations[qteam].size() == 0) return null;
        Queue<String> cert = new Queue<>();
        for (int t : eliminations[qteam]) {
            cert.enqueue(teams[t]);
        }
        return cert;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(
                        team + " is not eliminated " + division.certificateOfElimination(team));
            }
        }
    }
}
