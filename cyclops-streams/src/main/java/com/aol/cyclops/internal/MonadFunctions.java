package com.aol.cyclops.internal;


import static com.aol.cyclops.internal.AsGenericMonad.asMonad;
import static com.aol.cyclops.internal.AsGenericMonad.fromStream;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jooq.lambda.tuple.Tuple2;

import com.aol.cyclops.lambda.monads.ComprehenderSelector;
import com.aol.cyclops.sequence.Monoid;

public interface MonadFunctions<MONAD,T>{
	<R> Monad<MONAD,T> bind(Function<? super T,? extends R> fn);
	
	public Stream<T> stream();
	public <MONAD,T> MONAD unit(T value);
	public Monad<Stream<T>,T> cycle(int times);
	
	/**
	 * Apply function/s inside supplied Monad to data in current Monad
	 * 
	 * e.g. with Streams
	 * <pre>
	 * {@code 
	 * 
	 * AnyM<Integer> applied = AsAnyM.anyM(Stream.of(1,2,3))
	 *                               .applyM(AsAnyM.anyM(Streamable.of( (Integer a)->a+1 ,(Integer a) -> a*2)));
	
	 	assertThat(applied.toSequence().toList(),equalTo(Arrays.asList(2, 2, 3, 4, 4, 6)));
	 }</pre>
	 * 
	 * with Optionals 
	 * 
	 * <pre>{@code
	 * 
	 *  AnyM<Integer> applied = AsAnyM.anyM(Optional.of(2))
	 *                                .applyM( AsAnyM.anyM(Optional.of( (Integer a)->a+1)) );
		
		assertThat(applied.toSequence().toList(),equalTo(Arrays.asList(3)));
		}
	 * </pre>
	 * @param fn
	 * @return
	 */
	default <NT,R> Monad<NT,R> applyM(Monad<?,Function<? super T,? extends R>> fn){
	
		return (Monad)this.bind(v-> fn.map(innerFn -> innerFn.apply(v))
							.unwrap());
		
	}
	/**
	 * Filter current monad by each element in supplied Monad
	 * 
	 * e.g.
	 * 
	 * <pre>{@code
	 *  AnyM<Integer> applied = AsAnyM.anyM(Stream.of(1,2,3))
	 *    									.filterM(AsAnyM.anyM(Streamable.of( (Integer a)->a>5 ,(Integer a) -> a<3)));
	 *    									
	 * 
	 * //results in Stream.of(Stream.of(1),Stream.of(2),Stream.of(())
	 * }</pre>
	 * 
	 * @param fn
	 * @return
	 */
	default <NT,R> Monad<NT,R> simpleFilter(Monad<?,Predicate<? super T>> fn){
		return  (Monad)this.bind(v-> fn.map(innerFn -> new Tuple2(v,innerFn.test(v)))
													.filter(p->(boolean)p.v2())
													.map(Tuple2::v1))
													.map(m -> ((Monad) m).unwrap());
		
		
	
	//	filterM((a: Int) => List(a > 2, a % 2 == 0), List(1, 2, 3), ListMonad),
	//List(List(3), Nil, List(2, 3), List(2), List(3),
	//	  Nil, List(2, 3), List(2))												
	}
	/**
	 * 
	 * Replicate given Monad
	 * 
	 * <pre>{@code 
	 * 	
	 *   AnyM<Integer> applied =AsAnyM.anyM(Optional.of(2))
	 *                                .replicateM(5);
	 *                                
		 assertThat(applied.unwrap(),equalTo(Optional.of(Arrays.asList(2,2,2,2,2))));
		 
		 }</pre>
	 * 
	 * 
	 * @param times number of times to replicate
	 * @return Replicated Monad
	 */
	default <NT,R> Monad<NT,R> replicateM(int times){
		
		return (Monad)asMonad (unit(1))
						.flatten()
						.bind(v-> cycle(times).unwrap());		
	}
	/**
	 * Perform a reduction where NT is a (native) Monad type
	 * e.g. 
	 * <pre>{@code 
	 * Monoid<Optional<Integer>> optionalAdd = Monoid.of(Optional.of(0), (a,b)-> Optional.of(a.get()+b.get()));
		
		assertThat(AsAnyM.anyM(Stream.of(2,8,3,1)).reduceM(optionalAdd).unwrap(),equalTo(Optional.of(14)));
		}</pre>
	 * 
	 * 
	 * @param reducer
	 * @return
	 */
	default <NT,R> Monad<NT,R> reduceM(Monoid<NT> reducer){
	//	List(2, 8, 3, 1).foldLeftM(0) {binSmalls} -> Optional(14)
	//	convert to list Optionals
		
		return asMonad(fromStream(stream()).map(value ->new ComprehenderSelector()
							.selectComprehender(reducer.zero().getClass()).of(value))
							.sequence().reduce((Monoid)reducer));		
	}
	
	
}
