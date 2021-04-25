package br.sergio.mcsc.utils;

public class Pair<M, F> {

	private M male;
	private F female;
	
	public Pair(M male, F female) {
		this.male = male;
		this.female = female;
	}
	
	public M getMale() {
		return male;
	}
	
	public void setMale(M male) {
		this.male = male;
	}
	
	public F getFemale() {
		return female;
	}
	
	public void setFemale(F female) {
		this.female = female;
	}
	
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(o instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) o;
			boolean maleEquals = pair.getMale().equals(male);
			boolean femaleEquals = pair.getFemale().equals(female);
			return maleEquals && femaleEquals;
		}
		return false;
	}
	
	public int hashCode() {
		int maleHash = male != null ? male.hashCode() : 0;
		int femaleHash = female != null ? female.hashCode() : 0;
		return (maleHash + femaleHash) * maleHash * femaleHash;
	}
}
