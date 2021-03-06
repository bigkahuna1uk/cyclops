package com.aol.cyclops.matcher.builders;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import com.aol.cyclops.sequence.SequenceM;
import com.aol.cyclops.streams.StreamUtils;

class SeqUtils {
/**
	public final static class EMPTY { }
	public static final EMPTY EMPTY = new EMPTY();
	public static LazySeq<Object> seq(Object t){
		return LazySeq.of(stream(t).iterator());
	}
		public static Stream<Object> stream(Object t){
		
			if(t instanceof Iterable){
				return Stream.concat(StreamUtils.stream((Iterable)t).map(SeqUtils::nonNull),(StreamUtils.cycle(Stream.of(EMPTY))));
			}
			if(t instanceof Stream){
				return Stream.concat( ((Stream)t).map(SeqUtils::nonNull),(StreamUtils.cycle(Stream.of(EMPTY))));
			}
			if(t instanceof Iterator){
				return Stream.concat( StreamUtils.stream((Iterator)t).map(SeqUtils::nonNull),(StreamUtils.cycle(Stream.of(EMPTY))));
			}
			if(t instanceof Map){
				return Stream.concat(StreamUtils.stream((Map)t).map(SeqUtils::nonNull),(StreamUtils.cycle(Stream.of(EMPTY))));
			}
			return Stream.concat(Stream.of(t).map(SeqUtils::nonNull),(StreamUtils.cycle(Stream.of(EMPTY))));
		}
		**/
		private static Object nonNull(Object in){
			if(in==null)
				return EMPTY;
			return in;
		}
		
		public final static class EMPTY { }
		public static final EMPTY EMPTY = new EMPTY();
		public static SequenceM<Object> seq(Object t){
			return SequenceM.fromStream(stream(t));
		}
			public static Stream<Object> stream(Object t){
			
				if(t instanceof Iterable){
					return Stream.concat(StreamUtils.stream((Iterable)t).map(SeqUtils::nonNull),(StreamUtils.cycle(Stream.of(EMPTY))));
				}
				if(t instanceof Stream){
					return Stream.concat( ((Stream)t).map(SeqUtils::nonNull),(StreamUtils.cycle(Stream.of(EMPTY))));
				}
				if(t instanceof Iterator){
					return Stream.concat( StreamUtils.stream((Iterator)t).map(SeqUtils::nonNull),(StreamUtils.cycle(Stream.of(EMPTY))));
				}
				if(t instanceof Map){
					return Stream.concat(StreamUtils.stream((Map)t).map(SeqUtils::nonNull),(StreamUtils.cycle(Stream.of(EMPTY))));
				}
				return Stream.concat(Stream.of(t).map(SeqUtils::nonNull),(StreamUtils.cycle(Stream.of(EMPTY))));
			}
	
}
