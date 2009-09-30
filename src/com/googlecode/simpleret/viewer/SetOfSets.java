package com.googlecode.simpleret.viewer;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.googlecode.simpleret.Constants;

public class SetOfSets<E> implements Iterable<E> {

	private LinkedHashSet<E> set = new LinkedHashSet<E>();

	public void add(E object) {
		set.add(object);
	}

	public void addAll(Collection<E> collection) {
		set.addAll(collection);
	}

	public Iterator<E> iterator() {
		return set.iterator();
	}

	public Iterator<SetOfSets<E>> iteratorSectionBySection() {
		return new SetOfSetsIterator<SetOfSets<E>>(set);
	}

	/**
	 * @return
	 * 			a number of sets;
	 */
	public int number() {
		return ( ( set.size() + Constants.SQL_CAPACITY - 1 ) / Constants.SQL_CAPACITY ); 
	}

	private class SetOfSetsIterator<F> implements Iterator<SetOfSets<E>> {

		private int last = 0;
		private int pointer = 0;
		private Iterator<E> main = null;

		private SetOfSetsIterator(LinkedHashSet<E> objects) {
			main = objects.iterator();
			this.last = objects.size() - 1;
		}

		public boolean hasNext() {
			if (pointer <= last)
				return true;
			return false;
		}
		
		public SetOfSets<E> next() {
			int n = Constants.SQL_CAPACITY;
			SetOfSets<E> some = new SetOfSets<E>();
			while ((n > 0) && (main.hasNext())) {
				some.add(main.next());
				pointer++;
				n--;
			}
			return some;
		}

		public void remove() {
			throw new RuntimeException("Not implemented.");
		}

	}

}
