package root;

import java.util.*;


/**
 * Created by MB on 10/9/2014.
 */
public enum Direction {


    RIGHT(){
        @Override public Direction opp() { return LEFT; }

        @Override
        public Set<Direction> getCompDir () {
            return EnumSet.of(RIGHT);
        }
    },

    LEFT{
        @Override public Direction opp() { return RIGHT; }
        @Override
        public Set<Direction> getCompDir () {
            return EnumSet.of(LEFT);
        }
    },
    DOWN{
        @Override public Direction opp() { return TOP; }

        @Override
        public Set<Direction> getCompDir () {
            return EnumSet.of(DOWN);
        }
    },

    TOP{
        @Override public Direction opp() { return DOWN; }

        @Override
        public Set<Direction> getCompDir () {
            return EnumSet.of(TOP);
        }
    },





    DOWNLEFT(){
        @Override public Direction opp() { return TOPRIGHT; }

        @Override
        public Set<Direction> getCompDir () {
            return EnumSet.of(DOWN,LEFT);
        }
    },

    TOPLEFT(){
        @Override public Direction opp() { return DOWNRIGHT; }

        @Override
        public Set<Direction> getCompDir () {
            return EnumSet.of(TOP,LEFT);
        }
    },

    DOWNRIGHT(){
        @Override public Direction opp() { return TOPLEFT; }

        @Override
        public Set<Direction> getCompDir () {
            return EnumSet.of(DOWN,RIGHT);
        }
    },

    TOPRIGHT(){
        @Override public Direction opp() { return DOWNLEFT; }

        @Override
        public Set<Direction> getCompDir () {
            return EnumSet.of(TOP,RIGHT);
        }
    };

    public int nbcol;

    private static final Set<Direction> direction4 = new LinkedHashSet<Direction>();
    public static final Set<Direction> direction8 = new LinkedHashSet<Direction>();
    static {
        /* Les 4 directions DOWN, RIGHT, DOWNRIGHT, TOPRIGHT d'un point */
        direction4.add(DOWN);
        direction4.add(RIGHT);
        direction4.add(DOWNRIGHT);
        direction4.add(TOPRIGHT);

        /* Toutes les directions d'un point */
        direction8.addAll(EnumSet.allOf(Direction.class));
    }

    Direction(){ }


    abstract public Set<Direction> getCompDir();
    abstract public Direction opp(); // direction opposee


    public enum Axes{
        VERTICAL(TOP, DOWN, 0),
        HORIZONTAL(LEFT, RIGHT, 1),
        DIAGR(DOWNLEFT, TOPRIGHT, 2), // Diagonale /
        DIAGL(TOPLEFT, DOWNRIGHT, 3); // Diagonale \

        public final Direction dirLeft;
        public final Direction dirRight;
        public final int i;

        Axes(Direction dir, Direction dir2, int i){
            this.dirLeft = dir;
            this.dirRight = dir2;
            this.i = i; //index
        }

        public static final Map<Direction, Axes> lookup = new EnumMap<Direction, Axes>(Direction.class);

        static {
            for(Direction dir : Direction.values()){
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

        public static Axes getAxe(Direction d){
            return lookup.get(d);
        }

    }

}
