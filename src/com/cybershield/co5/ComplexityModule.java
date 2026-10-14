package com.cybershield.co5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Educational NP-completeness and approximation examples; no malware inference is made. */
public final class ComplexityModule {
    private ComplexityModule() { }
    public static void run() {
        for (int choice = 1; choice <= 5; choice++) runChoice(choice);
    }
    public static void runChoice(int choice) {
        int[][] clauses={{1,-2,3},{-1,2,3},{1,2,-3}};
        boolean[] assignment={false,true,true,true}; // variable indexes begin at 1
        boolean[][] graph={{false,true,true,false},{true,false,true,true},{true,true,false,true},{false,true,true,false}};
        switch (choice) {
            case 1:
                runEvaluation(assignment);
                break;
            case 2:
                System.out.println("Brute-force 3-SAT assignment: " + Arrays.toString(solve3Sat(clauses, 3)) + " [O(2^n*m), O(n)]");
                break;
            case 3:
                boolean[][] literalGraph = buildLiteralCompatibilityGraph(clauses);
                System.out.println("3-SAT -> CLIQUE graph (one vertex per literal occurrence): " + literalGraph.length + " vertices");
                System.out.println("Clique of size " + clauses.length + " exists: " + hasClauseClique(clauses));
                break;
            case 4:
                int[] clique={0,1,2}; boolean[][] complement=complement(graph);
                boolean[] independent=cliqueToComplementIndependentSet(clique, graph.length);
                boolean[] cover=independentComplement(independent);
                System.out.println("Security-dependency adjacency:"); for(boolean[] row:graph)System.out.println(Arrays.toString(row));
                System.out.println("Clique {0,1,2} -> independent set in complement: " + indices(independent));
                System.out.println("Complement of that set is a vertex cover in the complement graph: " + indices(cover) + "; verified=" + coversAll(complement,cover));
                break;
            case 5:
                boolean[] approx=vertexCover2Approx(graph);
                System.out.println("2-approx vertex cover: " + indices(approx) + "; all edges covered=" + coversAll(graph,approx) + " [O(V^2*E), O(V^2)]");
                break;
            default:
                System.out.println("Unknown CO5 algorithm choice.");
        }
    }
    public static void runEvaluation(boolean[] assignment) {
        int[][] clauses={{1,-2,3},{-1,2,3},{1,2,-3}};
        System.out.println("3-SAT clauses: " + Arrays.deepToString(clauses));
        System.out.println("Supplied assignment satisfies formula: " + evaluate(clauses, assignment));
    }
    public static boolean evaluate(int[][] clauses,boolean[] values){for(int[] clause:clauses){boolean ok=false;for(int lit:clause){int v=Math.abs(lit);if(v>=values.length)return false;ok|=lit>0?values[v]:!values[v];}if(!ok)return false;}return true;}
    public static boolean[] solve3Sat(int[][] clauses,int variables){if(variables>=31)return null;for(int mask=0;mask<(1<<variables);mask++){boolean[] val=new boolean[variables+1];for(int v=1;v<=variables;v++)val[v]=(mask&(1<<(v-1)))!=0;if(evaluate(clauses,val))return val;}return null;}
    /** Builds one vertex per literal occurrence; edges only join compatible literals in distinct clauses. */
    public static boolean hasClauseClique(int[][] clauses){boolean[][] graph=buildLiteralCompatibilityGraph(clauses);int[][] vertices=new int[clauses.length][];int at=0;for(int i=0;i<clauses.length;i++){vertices[i]=new int[clauses[i].length];for(int j=0;j<clauses[i].length;j++)vertices[i][j]=at++;}return findGraphClique(graph,vertices,0,new int[clauses.length]);}
    public static boolean[][] buildLiteralCompatibilityGraph(int[][] clauses){int total=0;for(int[] c:clauses)total+=c.length;int[] literal=new int[total],clauseOf=new int[total];int at=0;for(int c=0;c<clauses.length;c++)for(int lit:clauses[c]){literal[at]=lit;clauseOf[at++]=c;}boolean[][] g=new boolean[total][total];for(int i=0;i<total;i++)for(int j=i+1;j<total;j++)if(clauseOf[i]!=clauseOf[j]&&literal[i]!=-literal[j])g[i][j]=g[j][i]=true;return g;}
    private static boolean findGraphClique(boolean[][] graph,int[][] clauseVertices,int level,int[] chosen){if(level==clauseVertices.length)return true;for(int vertex:clauseVertices[level]){boolean ok=true;for(int i=0;i<level;i++)if(!graph[vertex][chosen[i]])ok=false;if(ok){chosen[level]=vertex;if(findGraphClique(graph,clauseVertices,level+1,chosen))return true;}}return false;}
    public static boolean[][] complement(boolean[][] g){int n=g.length;boolean[][] h=new boolean[n][n];for(int i=0;i<n;i++)for(int j=0;j<n;j++)h[i][j]=i!=j&&!g[i][j];return h;}
    public static boolean[] cliqueToComplementIndependentSet(int[] clique){int max=-1;for(int v:clique)max=Math.max(max,v);return cliqueToComplementIndependentSet(clique,max+1);}
    public static boolean[] cliqueToComplementIndependentSet(int[] clique,int graphSize){boolean[] set=new boolean[graphSize];for(int v:clique)if(v>=0&&v<graphSize)set[v]=true;return set;}
    public static boolean[] independentComplement(boolean[] independent){boolean[] cover=new boolean[independent.length];for(int i=0;i<cover.length;i++)cover[i]=!independent[i];return cover;}
    public static boolean[] vertexCover2Approx(boolean[][] g){int n=g.length;boolean[] cover=new boolean[n];boolean[][] uncovered=new boolean[n][n];for(int i=0;i<n;i++)uncovered[i]=g[i].clone();while(true){int a=-1,b=-1;outer:for(int i=0;i<n;i++)for(int j=i+1;j<n;j++)if(uncovered[i][j]){a=i;b=j;break outer;}if(a<0)break;cover[a]=cover[b]=true;for(int i=0;i<n;i++){uncovered[a][i]=uncovered[i][a]=false;uncovered[b][i]=uncovered[i][b]=false;}}return cover;}
    public static boolean coversAll(boolean[][] g,boolean[] cover){for(int i=0;i<g.length;i++)for(int j=i+1;j<g.length;j++)if(g[i][j]&&!cover[i]&&!cover[j])return false;return true;}
    private static List<Integer> indices(boolean[] b){List<Integer> a=new ArrayList<>();for(int i=0;i<b.length;i++)if(b[i])a.add(i);return a;}
}
