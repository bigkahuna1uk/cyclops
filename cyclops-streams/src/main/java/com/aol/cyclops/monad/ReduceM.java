package com.aol.cyclops.monad;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.aol.cyclops.sequence.Monoid;
import com.aol.cyclops.sequence.streamable.Streamable;

public interface ReduceM<T> {
	/**
	 * Perform a reduction where NT is a (native) Monad type
	 * e.g. 
	 * <pre>{@code 
	 *   Monoid<Optional<Integer>> optionalAdd = Monoid.of(Optional.of(0), (a,b)-> Optional.of(a.get()+b.get()));
		
		AnyM.fromStream(Stream.of(2,8,3,1)).reduceM(optionalAdd);
		
		//AnyM[Optional(14)];
		}</pre>
	 * 
	 * 
	 * @param reducer An identity value (approx. a seed) and BiFunction with a single type to reduce this anyM
	 * @return Reduced AnyM
	 */
	 AnyM<T> reduceMOptional(Monoid<Optional<T>> reducer);
	 /**
		 * Perform a reduction where NT is a (native) Monad type
		 * e.g. 
		 * <pre>{@code 
		 *   Monoid<Optional<Integer>> streamableAdd = Monoid.of(Stream.of(0), (a,b)-> Stream.of(a.get()+b.get()));
			
			AnyM.fromStream(Stream.of(2,8,3,1)).reduceM(streamableAdd);
			
			//AnyM[Optional(14)];
			}</pre>
		 * 
		 * 
		 * @param reducer An identity value (approx. a seed) and BiFunction with a single type to reduce this anyM
		 * @return Reduced AnyM
		 */
	AnyM<T> reduceMStream(Monoid<Stream<T>> reducer);
	/**
	 * Perform a reduction where NT is a (native) Monad type
	 * e.g. 
	 * <pre>{@code 
	 *   Monoid<Optional<Integer>> streamableAdd = Monoid.of(Streamable.of(0), (a,b)-> Streamable.of(a.get()+b.get()));
		
		AnyM.fromStream(Stream.of(2,8,3,1)).reduceM(streamableAdd);
		
		//AnyM[Optional(14)];
		}</pre>
	 * 
	 * 
	 * @param reducer An identity value (approx. a seed) and BiFunction with a single type to reduce this anyM
	 * @return Reduced AnyM
	 */
	 AnyM<T> reduceMStreamable(Monoid<Streamable<T>> reducer);
	 /**
		 * Perform a reduction where NT is a (native) Monad type
		 * e.g. 
		 * <pre>{@code 
		 *   Monoid<Optional<Integer>> streamableAdd = Monoid.of(Streamable.of(0), (a,b)-> Streamable.of(a.get()+b.get()));
			
			AnyM.fromStream(Stream.of(2,8,3,1)).reduceM(streamableAdd);
			
			//AnyM[Optional(14)];
			}</pre>
		 * 
		 * 
		 * @param reducer An identity value (approx. a seed) and BiFunction with a single type to reduce this anyM
		 * @return Reduced AnyM
		 */
		 AnyM<T> reduceMIterable(Monoid<Iterable<T>> reducer);
		 /**
			 * Perform a reduction where NT is a (native) Monad type
			 * e.g. 
			 * <pre>{@code 
			 *   Monoid<Optional<Integer>> streamableAdd = Monoid.of(CompletableFuture.completedFuture(0), (a,b)-> CompletableFuture.supplyAsync(()->a.get()+b.get()));
				
				AnyM.fromStream(Stream.of(2,8,3,1)).reduceM(streamableAdd);
				
				//AnyM[Optional(14)];
				}</pre>
			 * 
			 * 
			 * @param reducer An identity value (approx. a seed) and BiFunction with a single type to reduce this anyM
			 * @return Reduced AnyM
			 */
	 AnyM<T> reduceMCompletableFuture(Monoid<CompletableFuture<T>> reducer);
}
