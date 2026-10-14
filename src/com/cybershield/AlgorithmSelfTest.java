package com.cybershield;

import java.math.BigInteger;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.cybershield.co1.AhoCorasick;
import com.cybershield.co1.KMP;
import com.cybershield.co1.NaiveSearch;
import com.cybershield.co1.RabinKarp;
import com.cybershield.co1.ZAlgorithm;
import com.cybershield.co2.KasaiLCP;
import com.cybershield.co2.SuffixArray;
import com.cybershield.co2.SuffixAutomaton;
import com.cybershield.co3.BitmaskDP;
import com.cybershield.co3.DamerauLevenshtein;
import com.cybershield.co3.Levenshtein;
import com.cybershield.co3.WeightedEditDistance;
import com.cybershield.co4.NetworkFlowModule;
import com.cybershield.co5.ComplexityModule;
import com.cybershield.co6.RandomParallelModule;

/** Dependency-free regression checks. Run with: java com.cybershield.AlgorithmSelfTest */
public final class AlgorithmSelfTest {
    private static int checks;
    private AlgorithmSelfTest() { }
    public static void main(String[] args) {
        List<Integer> positions=Arrays.asList(1,3);
        check(NaiveSearch.search("ababa","ba").equals(positions),"naive matches");
        check(KMP.search("ababa","ba").equals(positions),"KMP matches");
        check(Arrays.equals(KMP.buildLPS("ababaca"),new int[]{0,0,1,2,3,0,1}),"KMP LPS");
        check(ZAlgorithm.search("ababa","ba").equals(positions),"Z matches");
        check(ZAlgorithm.search("$a$$","$$").equals(Arrays.asList(2)),"Z separator character edge case");
        check(RabinKarp.search("ababa","ba").equals(positions),"Rabin-Karp matches");
        check(RabinKarp.search("abc","abd").isEmpty(),"Rabin-Karp hash candidate verification");
        check(NaiveSearch.search("abc","longer").isEmpty()&&KMP.search("abc","longer").isEmpty()&&ZAlgorithm.search("abc","longer").isEmpty()&&RabinKarp.search("abc","longer").isEmpty(),"pattern longer than text");
        check(NaiveSearch.search("abc","").isEmpty()&&KMP.search("abc","").isEmpty()&&ZAlgorithm.search("abc","").isEmpty()&&RabinKarp.search("abc","").isEmpty(),"empty pattern safe handling");
        AhoCorasick ac=new AhoCorasick();for(String p:new String[]{"he","she","hers","his"})ac.addPattern(p);ac.build();
        check(ac.search("ushers").equals(java.util.Map.of("she",1,"he",1,"hers",1)),"Aho-Corasick suffix outputs");

        int[] sa=SuffixArray.build("banana");check(Arrays.equals(sa,new int[]{5,3,1,0,4,2}),"suffix array ordering");
        check(Arrays.equals(KasaiLCP.buildLCP("banana",sa),new int[]{1,3,0,0,2,0}),"Kasai LCP to next suffix");
        SuffixAutomaton sam=new SuffixAutomaton("banana");check(sam.contains("ana")&&!sam.contains("apple"),"suffix automaton membership");
        check(Arrays.equals(SuffixArray.build(""),new int[0]),"empty suffix array");

        check(Levenshtein.calculate("kitten","sitting")==3,"Levenshtein known case");
        check(DamerauLevenshtein.calculate("ca","ac")==1,"OSA adjacent transposition");
        check(DamerauLevenshtein.calculate("CA","ABC")==3,"OSA is restricted versus unrestricted variant");
        check(WeightedEditDistance.calculate("abc","adc",1,1,2)==2,"weighted substitution cost");
        ByteArrayOutputStream bitmaskText=new ByteArrayOutputStream();PrintStream original=System.out;try{System.setOut(new PrintStream(bitmaskText));BitmaskDP.solve();}finally{System.setOut(original);}check(bitmaskText.toString().contains("Minimum Analysis Cost: 10"),"bitmask rule-coverage optimum");
        check(NetworkFlowModule.fordFulkerson(flow(),0,5)==5,"Ford-Fulkerson demo flow");
        check(NetworkFlowModule.edmondsKarp(flow(),0,5)==5,"Edmonds-Karp demo flow");
        check(NetworkFlowModule.dinic(flow(),0,5)==5,"Dinic demo flow");
        check(NetworkFlowModule.maximumMatching(new boolean[][]{{true,true,false},{true,false,true},{false,true,true}})==3,"bipartite matching");

        int[][] clauses={{1,-2,3},{-1,2,3},{1,2,-3}};
        check(ComplexityModule.evaluate(clauses,new boolean[]{false,true,true,true}),"3-SAT assignment");
        check(ComplexityModule.solve3Sat(clauses,3)!=null&&ComplexityModule.hasClauseClique(clauses),"3-SAT enumeration and clique reduction");
        boolean[][] graph={{false,true,true,false},{true,false,true,true},{true,true,false,true},{false,true,true,false}};
        check(ComplexityModule.coversAll(graph,ComplexityModule.vertexCover2Approx(graph)),"2-approximate vertex cover covers edges");
        boolean[][] complement=ComplexityModule.complement(graph);boolean[] independent=ComplexityModule.cliqueToComplementIndependentSet(new int[]{0,1,2},4);check(ComplexityModule.coversAll(complement,ComplexityModule.independentComplement(independent)),"complement independent-set / cover relationship");
        check(RandomParallelModule.isProbablePrime(new BigInteger("1000000007"),20)&&!RandomParallelModule.isProbablePrime(new BigInteger("21"),20),"Miller-Rabin examples");
        int[] quick={9,4,7,3,8,2,6,1,5};RandomParallelModule.quickSort(quick,new Random(2026));check(Arrays.equals(quick,new int[]{1,2,3,4,5,6,7,8,9}),"randomized quicksort");
        check(Arrays.equals(RandomParallelModule.exclusiveBlelloch(new int[]{3,1,7,0,4,1,6,3}),new int[]{0,3,4,11,11,15,16,22}),"Blelloch exclusive scan");
        check(RandomParallelModule.parallelCount(Arrays.asList("a","b","c"))==3,"parallel count matches sequential fixture");
        System.out.println("Self-tests passed: "+checks+" / "+checks);
    }
    private static int[][] flow(){return new int[][]{{0,3,2,0,0,0},{0,0,1,2,0,0},{0,0,0,0,3,0},{0,0,0,0,1,2},{0,0,0,0,0,4},{0,0,0,0,0,0}};}
    private static void check(boolean passed,String name){checks++;System.out.println((passed?"PASS":"FAIL")+" - "+name);if(!passed)throw new AssertionError(name);}
}
