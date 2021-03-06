package com.bukkit.xarinor.templecraft.util;

/**
* Pair.java
* This work is dedicated to the public domain.
* 
* @author bootscreen
* @author msingleton
*/
public class Pair<A,B> {

	/**
	 * @param p
	 * @param q
	 * @return
	 */
	public static <P, Q> Pair<P, Q> makePair(P p, Q q) {
		return new Pair<P, Q>(p, q);
	}

	public final A a;
	public final B b;

	/**
	 * @param a
	 * @param b
	 */
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Returns a hash code
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		return result;
	}

	/**
	 * This equals object?
	 * 
	 * @param obj -object
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		Pair other = (Pair) obj;
		if (a == null) {
			if (other.a != null) {
				return false;
			}
		} else if (!a.equals(other.a)) {
			return false;
		}
		if (b == null) {
			if (other.b != null) {
				return false;
			}
		} else if (!b.equals(other.b)) {
			return false;
		}
		return true;
	}

	/**
	 * Both values are instances?
	 * 
	 * @param classA
	 * @param classB
	 * @return
	 */
	public boolean isInstance(Class<?> classA, Class<?> classB) {
		return classA.isInstance(a) && classB.isInstance(b);
	}

	/**
	 * 
	 * @param pair
	 * @param pClass
	 * @param qClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <P, Q> Pair<P, Q> cast(Pair<?, ?> pair, Class<P> pClass, Class<Q> qClass) {

		if (pair.isInstance(pClass, qClass)) {
			return (Pair<P, Q>) pair;
		}
		throw new ClassCastException();
	}

}