package prognet.common;

import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String animal;
    private boolean matched;
    private boolean flipped;

    public Card(int id, String animal) {
        this.id = id;
        this.animal = animal;
        this.matched = false;
        this.flipped = false;
    }

    public int getId() {
        return id;
    }

    public String getAnimal() {
        return animal;
    }

    public boolean isMatched() {
        return matched;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }
}
