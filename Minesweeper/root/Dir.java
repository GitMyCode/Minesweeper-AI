package root;

import java.util.*;


/**
 * Created by MB on 10/9/2014.
 */
public enum Dir {

    DOWN{
        @Override public Dir opp() { return TOP; }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(DOWN);
        }
    },

    TOP{
        @Override public Dir opp() { return DOWN; }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(TOP);
        }
    },

    LEFT{
        @Override public Dir opp() { return RIGHT; }
        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(LEFT);
        }
    },


    RIGHT(){
        @Override public Dir opp() { return LEFT; }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(RIGHT);
        }
    },

    DOWNLEFT(){
        @Override public Dir opp() { return TOPRIGHT; }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(DOWN,LEFT);
        }
    },

    TOPLEFT(){
        @Override public Dir opp() { return DOWNRIGHT; }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(TOP,LEFT);
        }
    },

    DOWNRIGHT(){
        @Override public Dir opp() { return TOPLEFT; }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(DOWN,RIGHT);
        }
    },

    TOPRIGHT(){
        @Override public Dir opp() { return DOWNLEFT; }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(TOP,RIGHT);
        }
    };

    public int nbcol;

    public static final Set<Dir> direction4 = new HashSet<Dir>();
    public static final Set<Dir> direction8 = new HashSet<Dir>();
    static {
        /* Les 4 directions DOWN, RIGHT, DOWNRIGHT, TOPRIGHT d'un point */
        direction4.add(DOWN);
        direction4.add(RIGHT);
        direction4.add(DOWNRIGHT);
        direction4.add(TOPRIGHT);

        /* Toutes les directions d'un point */
        direction8.addAll(EnumSet.allOf(Dir.class));
    }

    Dir(){ }


    abstract public Set<Dir> getCompDir();
    abstract public Dir opp(); // direction opposee



    /***
     * Valide la limite pour N, O, S, E
     */


    public enum Axes{
        VERTICAL(TOP, DOWN, 0),
        HORIZONTAL(LEFT, RIGHT, 1),
        DIAGR(DOWNLEFT, TOPRIGHT, 2), // Diagonale /
        DIAGL(TOPLEFT, DOWNRIGHT, 3); // Diagonale \

        public Dir dirLeft;
        public Dir dirRight;
        public int i;

        Axes(Dir dir, Dir dir2, int i){
            this.dirLeft = dir;
            this.dirRight = dir2;
            this.i = i; //index
        }

        public static final Map<Dir, Axes> lookup = new EnumMap<Dir, Axes>(Dir.class);

        static {
            for(Dir dir : Dir.values()){
                if(VERTICAL.dirLeft == dir || VERTICAL.dirRight == dir)
                    lookup.put(dir, VERTICAL);
                else if(HORIZONTAL.dirLeft == dir || HORIZONTAL.dirRight == dir){
                    lookup.put(dir, HORIZONTAL);
                }else if(DIAGR.dirLeft == dir || DIAGR.dirRight == dir){
                    lookup.put(dir, DIAGR);
                }else if(DIAGL.dirLeft == dir || DIAGL.dirRight == dir){
                    lookup.put(dir, DIAGL);
                }
            }
        }

        public static Axes getAxe(Dir d){
            return lookup.get(d);
        }

    }

}
