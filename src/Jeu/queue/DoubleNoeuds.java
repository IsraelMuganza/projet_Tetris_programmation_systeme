package Jeu.queue;

public class DoubleNoeuds<T> extends Noeud<T> {
    private Noeud<T> prev;

    public DoubleNoeuds(T item, Noeud<T> next, Noeud<T> prev) {
        super(item, next);
        this.prev = prev;
        if (prev != null){
            prev.setNext(this);
        }
    }

    public Noeud<T> getPrev() {
        return this.prev;
    }

    public void setPrev(Noeud<T> prev) {
        this.prev = prev;
        prev.setNext(this);
    }
}