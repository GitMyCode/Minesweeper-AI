package root;

import java.util.*;


/**
 * Created by MB on 10/9/2014.
 */
public enum Dir {

    DOWN{
        @Override public Dir opp() { return TOP; }
        @Override public int v() { return GLOBAL.NBCOL; }
        @Override public boolean boundaries(int index, int limit) { return checkBounderies(EnumSet.of(Cardinal.SUD), index, limit); }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(DOWN);
        }
    },

    TOP{
        @Override public Dir opp() { return DOWN; }
        @Override public int v() { return 0 - DOWN.v(); }
        @Override public boolean boundaries(int index, int limit) { return checkBounderies(EnumSet.of(Cardinal.NORD), index, limit); }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(TOP);
        }
    },

    LEFT{
        @Override public Dir opp() { return RIGHT; }  @Override public int v() { return -1; }
        @Override public boolean boundaries(int index, int limit) { return checkBounderies(EnumSet.of(Cardinal.OUEST), index, limit); }
        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(LEFT);
        }
    },


    RIGHT(){
        @Override public Dir opp() { return LEFT; }
        @Override public int v() { return 1; }
        @Override public boolean boundaries(int index, int limit) { return checkBounderies(EnumSet.of(Cardinal.EST), index, limit); }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(RIGHT);
        }
    },

    DOWNLEFT(){
        @Override public Dir opp() { return TOPRIGHT; }
        @Override public int v() { return DOWN.v() + LEFT.v(); }
        @Override public boolean boundaries(int index, int limit) { return DOWN.boundaries(index, limit) && LEFT.boundaries(index, limit); }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(DOWN,LEFT);
        }
    },

    TOPLEFT(Cardinal.OUEST){
        @Override public Dir opp() { return DOWNRIGHT; }
        @Override public int v() { return TOP.v() + LEFT.v(); }
        @Override public boolean boundaries(int index, int limit) { return TOP.boundaries(index, limit)&& LEFT.boundaries(index, limit); }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(TOP,LEFT);
        }
    },

    DOWNRIGHT(Cardinal.EST){
        @Override public Dir opp() { return TOPLEFT; }
        @Override public int v() { return DOWN.v() + RIGHT.v(); }
        @Override public boolean boundaries(int index, int limit) { return DOWN.boundaries(index, limit)&&RIGHT.boundaries(index,limit); }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(DOWN,RIGHT);
        }
    },

    TOPRIGHT(Cardinal.EST){
        @Override public Dir opp() { return DOWNLEFT; }
        @Override public int v() { return TOP.v() + RIGHT.v(); }
        @Override public boolean boundaries(int index, int limit) { return TOP.boundaries(index, limit)&& RIGHT.boundaries(index, limit); }

        @Override
        public Set<Dir> getCompDir () {
            return EnumSet.of(TOP,RIGHT);
        }
    };

    public int nbcol;
    public Cardinal cardinal = null;

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

    Dir(Cardinal side){
        this.cardinal = side;
    }

    abstract public Set<Dir> getCompDir();
    abstract public int v(); // index pour déplacer vers la direction
    abstract public Dir opp(); // direction opposee
    abstract public boolean boundaries (int index, int limit);

    public int step (int step){
        return this.v() * step;
    }

    private static boolean checkBounderies(Set<Cardinal> cardinaux, int index, int limit){
        for(Cardinal c: cardinaux){
            if(! c.validLimit(index, limit)){
                return false;
            }
        }
        return true;
    }

    /***
     * Valide la limite pour N, O, S, E
     */
    public enum Cardinal{
        NORD{
            @Override boolean validLimit (int index, int limit) {
                if(index < 0 || index >= GLOBAL.NBCOL * GLOBAL.NBLIGNE)
                    return false;

                return  (index + TOP.v() * (limit - 1)) >= 0;
            }
        },
        SUD{
            @Override boolean validLimit (int index, int limit) {
                int length = GLOBAL.NBCOL * GLOBAL.NBLIGNE;
                if(index < 0 || index >= length)
                    return false;
                return (index + DOWN.v() * (limit - 1)) < length;
            }
        },
        OUEST{
            @Override
            boolean validLimit (int index, int limit) {
                if(index < 0 || index >= GLOBAL.NBCOL * GLOBAL.NBLIGNE)
                    return false;
                if(!(((index%GLOBAL.NBCOL + 1) >= limit))) { return false; }
                return true;
            }
        },
        EST{
            @Override
            boolean validLimit (int index, int limit) {
                if(index < 0 || index >= GLOBAL.NBCOL * GLOBAL.NBLIGNE)
                    return false;
                if(!((GLOBAL.NBCOL - (index%GLOBAL.NBCOL)) >= limit)){ return false; }
                return true;
            }
        };

        /*Check if we can get a sequence of 'limit' from the point of the index */
        abstract boolean validLimit (int index, int limit);

    }

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
