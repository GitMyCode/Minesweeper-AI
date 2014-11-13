package root.ai.utilCSP;

import root.ENUM.CASE;
import root.Grid;

import java.util.*;

/**
 * Created by MB on 11/5/2014.
 */
public class Tarjan {

    static private Set<Integer> marked;
    static private Stack<Integer> stack;
    static private Map<Integer,Integer> low;
    static private Map<Integer,Integer> id;
    static private int itera=0;
    static private List<Integer> frontier;
    static private int count =0;
    private Grid gameGrid;

    public static List<Set<Integer>> findCycle(CASE[] grid,Grid gameGrid){
        Map<Integer,Set<Integer>> allPath = new HashMap<Integer, Set<Integer>>();
        marked = new HashSet<Integer>();
        low = new HashMap<Integer, Integer>();
        id = new HashMap<Integer, Integer>();
        frontier = new ArrayList<Integer>();
        itera =0;
        count =0;

        stack = new Stack<Integer>();
        for(int i=0; i<grid.length;i++){
            if(CASE.isIndicatorCase(grid[i])){
                frontier.add(i);
                low.put(i,0);
            }
        }
        for(Integer i : frontier){
            if(!marked.contains(i)){
                DFS(grid,i,gameGrid);
            }
        }


        for(Integer v : frontier){
            if(allPath.containsKey(id.get(v))){
                allPath.get(id.get(v)).add(v);
            }else{
                allPath.put(id.get(v),new HashSet<Integer>());
            }
        }

        return new ArrayList<Set<Integer>>();
    }

    static private void DFS(CASE[] grid,int index,Grid gameGrid){

        marked.add(index);
        low.put(index,itera);
        int minHere = itera;
        stack.add(index);

        itera++;
        List<Integer> voisins = getVoisin(grid,index,gameGrid);
        for(Integer v : voisins){
            if(!marked.contains(v)){
                DFS(grid,v,gameGrid);
            }
            if(low.get(v)< minHere) {
                minHere = low.get(v);
            }

        }
        if(minHere < low.get(index)){
            low.put(index,minHere);
            return;
        }
        int w;
        do{
            w = stack.pop();
            id.put(w,count);
            low.put(w,frontier.size());

        }while (w != index);
        count++;


    }


    static private List<Integer> getVoisin(CASE[] grid,int index,Grid gameGrid){
        List<Integer> list = new ArrayList<Integer>();

        for(Integer v: gameGrid.getSurroundingIndex(index)){
            if(CASE.isIndicatorCase(grid[v]) && !marked.contains(v)){
                list.add(v);
            }
        }
        return list;
    }

}
