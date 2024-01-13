package Jeu.queue;

public class Noeud<T> {
	private T item;
	private Noeud<T> next;

	public Noeud(T item) {
		this.item = item;
		this.next = null;
	}

	public Noeud(T item, Noeud<T> next) {
		this.item = item;
		this.next = next;
	}

	public Noeud<T> getNext() {
		return this.next;
	}

	public void setNext(Noeud<T> next) {
		this.next = next;
	}

	public T getItem() {
		return this.item;
	}
}