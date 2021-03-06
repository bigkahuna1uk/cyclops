package com.aol.cyclops.streams.spliterators;

public interface ReversableSpliterator {

	boolean isReverse();
	void setReverse(boolean reverse);
	default ReversableSpliterator invert(){
		setReverse(!isReverse());
		return this;
	}
	ReversableSpliterator copy();
}

