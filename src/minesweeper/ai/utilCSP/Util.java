package minesweeper.ai.utilCSP;

import minesweeper.Case;
import minesweeper.Coup;
import minesweeper.Move;
import minesweeper.ai.dataRepresentation.FringeNode;
import minesweeper.ai.dataRepresentation.Node;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Projet de joueur artificiel de Minesweeper avec différents algorithmes
 * Dans le cadre du 3e TP en Intelligence Artificielle (INF4230)
 * <p/>
 * Automne 2014
 * Par l'équipe:
 * Martin Bouchard
 * Frédéric Vachon
 * Louis-Bertrand Varin
 * Geneviève Lalonde
 * Nilovna Bascunan-Vasquez
 */
class Util {


    public static class BuildPrintGrid {

        char[] charGrid;
        int cols;

        public BuildPrintGrid(Case[] grid, int nbcol) {
            cols = nbcol;
            charGrid = getGridToChar(grid);
        }

        public void print() {
            printGrid(charGrid, cols);
        }

        public BuildPrintGrid showFLagedFringe(Collection<Node> nodes) {
            for (Node node : nodes) {
                if (((FringeNode) node).state == Case.FLAGED) {
                    charGrid[node.indexInGrid] = '⚐';
                }
            }
            return this;
        }

        public BuildPrintGrid showDeactivatedFringe(Collection<Node> nodes) {
            for (Node node : nodes) {
                if (((FringeNode) node).isDeactivated) {
                    charGrid[node.indexInGrid] = 'X';
                }
            }
            return this;
        }

        public BuildPrintGrid showThisIndex(int index) {
            charGrid[index] = '★';
            return this;
        }


    }

    public static BuildPrintGrid command(Case[] grid, int nbcol) {
        return new BuildPrintGrid(grid, nbcol);
    }

    public static void printGrid(Case[] grid, int nbcol) {
        String print = "";

        int i = 1;
        for (Case c : grid) {
            if (c == Case.UNDISCOVERED) {
                print += '■';
            } else if (c == Case.EMPTY) {
                print += '_';
            } else if (c == Case.FLAGED) {
                print += '⚑';

            } else if (c == Case.BLOW) {
                print += '●';
            } else {
                print += c.indexValue;
            }
            if (i % nbcol == 0 && i != 0) {
                print += "\n";
            }
            i++;
        }
        System.out.println(print);
    }

    public static void printGrid(char[] charGrid, int nbcol) {
        String print = "";
        int i = 1;
        for (char c : charGrid) {
            print += c;
            if (i % nbcol == 0 && i != 0) {
                print += "\n";
            }
            i++;
        }
        System.out.println(print);
    }

    public static char[] getGridToChar(Case[] grid) {
        char[] charGrid = new char[grid.length];
        int i = 0;
        for (Case c : grid) {
            if (c == Case.UNDISCOVERED) {

                charGrid[i] = '□';
            } else if (c == Case.EMPTY) {
                charGrid[i] = '_';
            } else if (c == Case.FLAGED) {
                charGrid[i] = '⚑';

            } else if (c == Case.BLOW) {
                charGrid[i] = '●';
            } else {
                charGrid[i] = (char) ('0' + c.indexValue);
            }
            i++;
        }

        return charGrid;
    }

    public static void printAllCoup(Case[] gridOriginal, int nbcol, Set<Move> coups) {

        String print = "";

        int i = 1;
        Case[] grid = gridOriginal.clone();

        for (Move m : coups) {
            if (m.coup == Coup.SHOW) {

                grid[m.index] = Case.BLOW; //Just pour une valeur quelquonc
            } else if (m.coup == Coup.FLAG) {
                grid[m.index] = Case.DEFUSED; //Just pour une valeur quelquonc
            }
        }


        for (Case c : grid) {
            if (c == Case.UNDISCOVERED) {
                print += '□';
            } else if (c == Case.EMPTY) {
                print += '_';
            } else if (c == Case.FLAGED) {
                print += '⚑';

            } else if (c == Case.BLOW) {
                print += '●';
            } else if (c == Case.DEFUSED) {
                print += '◯'; //○
            } else {
                print += c.indexValue;
            }
            if (i % nbcol == 0 && i != 0) {
                print += "\n";
            }
            i++;
        }
        System.out.println(print);

    }

    public static void printFrontiereInOrder(Case[] gridOrigin, int nbcol, List<Integer> frontiere) {
        Case[] cpy = gridOrigin.clone();
        for (Integer i : frontiere) {
            printIndex(cpy, nbcol, i);
        }
    }

    public static void printFrontiereNodeInOrder(Case[] gridOrigin, int nbcol, List<Node> frontiere) {
        Case[] cpy = gridOrigin.clone();
        for (Node i : frontiere) {
            printIndex(cpy, nbcol, i.indexInGrid);
        }
    }

    public static void printDeactivatedFringe(Case[] gridOrigin, int nbcol, Collection<Node> nodes) {

        char[] gridChar = getGridToChar(gridOrigin);
        for (Node node : nodes) {
            if (((FringeNode) node).isDeactivated) {
                gridChar[node.indexInGrid] = 'X';
            }
        }
        printGrid(gridChar, nbcol);
    }

    public static void printFlaggedFringe(Case[] gridOrigin, int nbcol, Collection<Node> nodes) {

        char[] gridChar = getGridToChar(gridOrigin);
        for (Node node : nodes) {
            if (((FringeNode) node).state == Case.FLAGED) {
                gridChar[node.indexInGrid] = '⚐';
            }
        }
        printGrid(gridChar, nbcol);
    }

    public static void printFrontiere(Case[] gridOrigin, int nbcol, Set<Integer> frontiere) {

        String print = "";

        int i = 1;
        Case[] grid = gridOrigin.clone();

        for (Integer f : frontiere) {
            grid[f] = Case.BLOW;
        }


        for (Case c : grid) {
            if (c == Case.UNDISCOVERED) {
                print += '□';
            } else if (c == Case.EMPTY) {
                print += '_';
            } else if (c == Case.FLAGED) {
                print += '⚑';

            } else if (c == Case.BLOW) {
                print += '●';
            } else if (c == Case.DEFUSED) {
                print += '◯'; //○
            } else {
                print += c.indexValue;
            }
            if (i % nbcol == 0 && i != 0) {
                print += "\n";
            }
            i++;
        }
        System.out.println(print);

    }

    public static void printFrontiereNode(Case[] gridOrigin, int nbcol, Collection<? extends Node> frontiere) {

        String print = "";

        int i = 1;
        Case[] grid = gridOrigin.clone();

        for (Node f : frontiere) {
            Node fn = Node.class.cast(f);
            grid[fn.indexInGrid] = Case.BLOW;
        }


        for (Case c : grid) {
            if (c == Case.UNDISCOVERED) {
                print += '□';
            } else if (c == Case.EMPTY) {
                print += '_';
            } else if (c == Case.FLAGED) {
                print += '⚑';

            } else if (c == Case.BLOW) {
                print += '●';
            } else if (c == Case.DEFUSED) {
                print += '◯'; //○
            } else {
                print += c.indexValue;
            }
            if (i % nbcol == 0 && i != 0) {
                print += "\n";
            }
            i++;
        }
        System.out.println(print);

    }


    public static void printFrontiere(Case[] gridOrigin, int nbcol, List<Integer> frontiere) {

        String print = "";

        int i = 1;
        Case[] grid = gridOrigin.clone();

        for (Integer f : frontiere) {
            grid[f] = Case.BLOW;
        }


        for (Case c : grid) {
            if (c == Case.UNDISCOVERED) {
                print += '□';
            } else if (c == Case.EMPTY) {
                print += '_';
            } else if (c == Case.FLAGED) {
                print += '⚑';

            } else if (c == Case.BLOW) {
                print += '●';
            } else if (c == Case.DEFUSED) {
                print += '◯'; //○
            } else {
                print += c.indexValue;
            }
            if (i % nbcol == 0 && i != 0) {
                print += "\n";
            }
            i++;
        }
        System.out.println(print);

    }

    public static void printIndex(Case[] gridOrigin, int nbcol, Integer toShow) {

        String print = "";

        int i = 1;
        Case[] grid = gridOrigin.clone();

        grid[toShow] = Case.BLOW;


        for (Case c : grid) {
            if (c == Case.UNDISCOVERED) {
                print += '□';
            } else if (c == Case.EMPTY) {
                print += '_';
            } else if (c == Case.FLAGED) {
                print += '⚑';

            } else if (c == Case.BLOW) {
                print += '●';
            } else if (c == Case.DEFUSED) {
                print += '◯'; //○
            } else {
                print += c.indexValue;
            }
            if (i % nbcol == 0 && i != 0) {
                print += "\n";
            }
            i++;
        }
        System.out.println(print);

    }


}
