package com.aol.cyclops.control;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.aol.cyclops.lambda.monads.EagerApplicative;
import com.aol.cyclops.lambda.monads.Filterable;
import com.aol.cyclops.lambda.monads.Functor;
import com.aol.cyclops.value.Value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;


public interface Maybe<T> extends Value<T>, Supplier<T>, Functor<T>, Filterable<T>{

	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	public static class MaybeApplicative<T,R> implements EagerApplicative<T,R>{
		private final Maybe<Function<? super T,? extends R>> maybe;

		@Override
		public <U> Functor<U> map(Function<? super Function<? super T, ? extends R>, ? extends U> fn) {
			return maybe.map(fn);
		}
		
	
	}
	final static Maybe EMPTY = new Nothing();
	
	static <T> Maybe<T> none(){
		return EMPTY;
	}
	
	static <T> Maybe<T> fromOptional(Optional<T> opt){
		if(opt.isPresent())
			return Maybe.of(opt.get());
		return none();
	}
	static <T> Maybe<T> fromEvalSome(Eval<T> eval){
		return new Something<T>(eval);
	}
	static <T> Maybe<T> of(T value){
		Objects.requireNonNull(value);
		return new Something<T>(Eval.later(()->value));
	}
	static Integer add(Integer i){
		return i+1;
	}
	static <T> Maybe<T> ofNullable(T value){
		
		if(value!=null)
			return of(value);
		return none();	
	}

	static <T> Maybe<T> narrow(Maybe<? extends T> broad){
		return (Maybe<T>)broad;
	}
	
	/**
	 * Apply a function within the maybe context e.g. 
	 * 
	 * <pre>
	 * {@code 
	    
		applicative((Integer i)->i+2).ap(Maybe.of(3));
	 * }
	 * </pre>
	 * @param fn
	 * @return
	 */
	static <T,R> EagerApplicative<T,R> applicative(Function<? super T,? extends R> fn){
		return EagerApplicative.of(fromEvalSome(Eval.now(fn)));
	}
	
	
	Maybe<T> recover(Supplier<T> value);
	Maybe<T> recover(T value);
	
	<R> Maybe<R> when(Function<? super T,? extends R> some, 
						Supplier<? extends R> none);
	
	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	public static final class Something<T> implements Maybe<T>{
		
		private final Eval<T> lazy;
		
		
		public <R> Maybe<R> map(Function<? super T, ? extends R> mapper){
			return new Something<>(lazy.map(t->mapper.apply(t)));
		}
		public <R>  Maybe<R> flatMap(Function<? super T, ? extends Maybe<? extends R>> mapper){
			return narrow(mapper.apply(lazy.get()));
			
		}
		public Maybe<T> filter(Predicate<? super T> test){
			if(test.test(lazy.get()))
				return this;
			return EMPTY;
		}
		public <R> Maybe<R> when(Function<? super T,? extends R> some, 
				Supplier<? extends R> none){
			return map(some);
		}
		public Maybe<T> recover(T value){
			return this;
		}
		public Maybe<T> recover(Supplier<T> value){
			return this;
		}
		public String toString(){
			return mkString();
		}
		public T  get(){
			return lazy.get();
		}
	}
	
	public static class Nothing<T> implements Maybe<T>{
		
		public <R> Maybe<R> map(Function<? super T, ? extends R> mapper){
			return EMPTY;
		}
		public <R>  Maybe<R> flatMap(Function<? super T, ? extends Maybe<? extends R>> mapper){
			return EMPTY;
			
		}
		public Maybe<T> filter(Predicate<? super T> test){
			return EMPTY;
		}
		public T  get(){
			return  Optional.<T>ofNullable(null).get();
		}
		public Maybe<T> recover(T value){
			return Maybe.of(value);
		}
		public Maybe<T> recover(Supplier<T> value){
			return new Something<>(Eval.later(value));
		}
		public <R> Maybe<R> when(Function<? super T,? extends R> some, 
				Supplier<? extends R> none){
			return narrow(Value.of(none).toMaybe());
		}
		public Optional<T> toOptional(){
			return Optional.ofNullable(null);
		}
		public String toString(){
			return mkString();
		}
	}
	
}