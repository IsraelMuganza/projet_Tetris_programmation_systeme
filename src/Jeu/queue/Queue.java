package Jeu.queue;

public class Queue<T> {
	private DoubleNoeuds<T> head = null;
	private DoubleNoeuds<T> tail = null;
	
	public void enqueue(T item){
		if (this.head == null){
			this.head = new DoubleNoeuds<>(item, this.tail, null);
		} else if (this.tail == null){
			this.tail = new DoubleNoeuds<>(item, null, this.head);
		} else {
			DoubleNoeuds<T> node = new DoubleNoeuds<>(item, null, this.tail);
			this.tail = node;
		}
	}
	
	public T dequeue(){
		T item = this.head.getItem();
		this.head = (DoubleNoeuds<T>)this.head.getNext();
		return item;
	}

    public Object[] peek(int amount){
		T[] items = (T[])new Object[amount];
		DoubleNoeuds<T> node = this.head;
		for (int i = 0; i < amount; i++){
			items[i] = node.getItem();
			node = (DoubleNoeuds<T>)node.getNext();
		}
		return items;
	}
}