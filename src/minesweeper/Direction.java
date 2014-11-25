package minesweeper;

import java.util.*;

/**
 * Projet de joueur artificiel de Minesweeper avec différents algorithmes
 * Dans le cadre du 3e TP en Intelligence Artificielle (INF4230)
 *
 * Automne 2014
 * Par l'équipe:
 *   Martin Bouchard
 *   Frédéric Vachon
 *   Louis-Bertrand Varin
 *   Geneviève Lalonde
 *   Nilovna Bascunan-Vasquez
 */
public enum Direction {

    RIGHT {
        @Override
        public Direction opposedDirection() {
            return LEFT;
        }

        @Override
        public Set<Direction> getCompDir() {
            return EnumSet.of(RIGHT);
        }
    },

    LEFT {
        @Override
        public Direction opposedDirection() {
            return RIGHT;
        }

        @Override
        public Set<Direction> getCompDir() {
            return EnumSet.of(LEFT);
        }
    },

    DOWN {
        @Override
        public Direction opposedDirection() {
            return TOP;
        }

        @Override
        public Set<Direction> getCompDir() {
            return EnumSet.of(DOWN);
        }
    },

    TOP {
        @Override
        public Direction opposedDirection() {
            return DOWN;
        }

        @Override
        public Set<Direction> getCompDir() {
            return EnumSet.of(TOP);
        }
    },

    DOWNLEFT {
        @Override
        public Direction opposedDirection() {
            return TOPRIGHT;
        }

        @Override
        public Set<Direction> getCompDir() {
            return EnumSet.of(DOWN, LEFT);
        }
    },

    TOPLEFT {
        @Override
        public Direction opposedDirection() {
            return DOWNRIGHT;
        }

        @Override
        public Set<Direction> getCompDir() {
            return EnumSet.of(TOP, LEFT);
        }
    },

    DOWNRIGHT {
        @Override
        public Direction opposedDirection() {
            return TOPLEFT;
        }

        @Override
        public Set<Direction> getCompDir() {
            return EnumSet.of(DOWN, RIGHT);
        }
    },

    TOPRIGHT {
        @Override
        public Direction opposedDirection() {
            return DOWNLEFT;
        }

        @Override
        public Set<Direction> getCompDir() {
            return EnumSet.of(TOP, RIGHT);
        }
    };

    private static final Set<Direction> QUATRE_DIRECTIONS = new LinkedHashSet<Direction>();
    public static final Set<Direction> HUIT_DIRECTIONS = new LinkedHashSet<Direction>();

    static {
        /* Les 4 directions DOWN, RIGHT, DOWNRIGHT, TOPRIGHT d'un point */
        QUATRE_DIRECTIONS.add(DOWN);
        QUATRE_DIRECTIONS.add(RIGHT);
        QUATRE_DIRECTIONS.add(DOWNRIGHT);
        QUATRE_DIRECTIONS.add(TOPRIGHT);

        /* Toutes les directions d'un point */
        HUIT_DIRECTIONS.addAll(EnumSet.allOf(Direction.class));
    }

    Direction() { }
    public abstract Set<Direction> getCompDir();
    public abstract Direction opposedDirection(); // direction opposee

    public enum Axe {
        VERTICAL(TOP, DOWN, 0),
        HORIZONTAL(LEFT, RIGHT, 1),
        DIAGR(DOWNLEFT, TOPRIGHT, 2), // Diagonale /
        DIAGL(TOPLEFT, DOWNRIGHT, 3); // Diagonale \

        public final Direction dirLeft;
        public final Direction dirRight;
        public final int i;

        Axe(Direction dir, Direction dir2, int i) {
            this.dirLeft = dir;
            this.dirRight = dir2;
            this.i = i; //index
        }

        public static final Map<Direction, Axe> LOOKUP = new EnumMap<Direction, Axe>(Direction.class);

        static {
            for (Direction dir : Direction.values()) {
                if (VERTICAL.dirLeft == dir || VERTICAL.dirRight == dir) {
                    LOOKUP.put(dir, VERTICAL);
                } else if (HORIZONTAL.dirLeft == dir || HORIZONTAL.dirRight == dir) {
                    LOOKUP.put(dir, HORIZONTAL);
                } else if (DIAGR.dirLeft == dir || DIAGR.dirRight == dir) {
                    LOOKUP.put(dir, DIAGR);
                } else if (DIAGL.dirLeft == dir || DIAGL.dirRight == dir) {
                    LOOKUP.put(dir, DIAGL);
                }
            }
        }

        public static Axe getAxe(Direction d) {
            return LOOKUP.get(d);
        }

    }

}
