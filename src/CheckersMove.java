import java.util.Objects;

public class CheckersMove {
    private int startingx, startingy, endingx,endingy;

    public CheckersMove(int startingX, int startingY, int endingX, int endingY){
        this.startingx = startingX;
        this.startingy = startingY;
        this.endingx = endingX;
        this.endingy = endingY;
    }

    public int getStartingX(){
        return startingx;
    }

    public int getStartingY(){
        return startingy;
    }

    public int getEndingX(){
        return endingx;
    }

    public int getEndingY(){
        return endingy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckersMove that = (CheckersMove) o;
        return startingx == that.startingx && startingy == that.startingy && endingx == that.endingx && endingy == that.endingy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingx, startingy, endingx, endingy);
    }

    @Override
    public String toString() {
        return "(" + startingx +"," + startingy + ") --> (" + endingx + "," + endingy + ")";
    }
}
