package com.aol.cyclops.comprehensions;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.aol.cyclops.comprehensions.ForComprehension2.ComphrensionData;
import com.aol.cyclops.comprehensions.ForComprehension3.Step2;
import com.aol.cyclops.comprehensions.ForComprehension3.Step3;
import com.aol.cyclops.comprehensions.ForComprehension3.Step4;



public class ForComprehension<T,R> {
	
	private final boolean convertCollections;
	
	public ForComprehension(boolean convertCollections) {
		super();
		this.convertCollections = convertCollections;
	}
	public ForComprehension(){
		this.convertCollections=false;
	}
	
	public static void main(String[] args){

		Optional<Integer> one = Optional.of(1);
		Optional<Integer> empty = Optional.of(3);
		BiFunction<Integer, Integer, Integer> f2 = (a, b) -> a * b;

		Object result =  ForComprehension.foreach(c -> c.flatMapAs$1(one)
														.flatMapAs$2(empty)
														.mapAs$3(Optional.empty())
													//	.guard(()->c.<Integer>$1()>2)
														.yield(()->{return f2.apply(c.$1(), c.$2());}));
	System.out.println(result);
}
	@SuppressWarnings("unchecked")
	public static <T,R> R foreach(Function<Step1<T,R>,R> fn){
		return Foreach.foreach(new ContextualExecutor<R,Foreach<R>>(new Foreach<R>()){
			public R execute(){
				return fn.apply(new ForComprehension<T,R>().getComphrensionData(this));
			}
		});
	}
	protected  Step1<T, R> getComphrensionData(ContextualExecutor<R,Foreach<R>> exec) {
		return new ComphrensionData<>(exec);
	}
	static interface Step1<T,R>{
		public  Step2<T,R> flatMapAs$1(Object f);
		public  <T> ForComprehension<T,R>.ComphrensionData<T,R> $(String name,Object f);
		public  <T> ForComprehension<T,R>.ComphrensionData<T,R>  $(String name,Supplier f);
		public <T> T $1();
		public <T> T $2();
		public <T> T $3();
		public <T> T $(String name);
	}
	static interface Step2<T,R>{
		public  Step3<T,R> flatMapAs$2(Object f);
		public  <T> ForComprehension<T,R>.ComphrensionData<T,R>  $(String name,Object f);
		public  <T> ForComprehension<T,R>.ComphrensionData<T,R>  $(String name,Supplier f);
		public  Step3<T,R> flatMapAs$2(Supplier f);
		public <R> R yield(Supplier s);
		
	}
	static interface Step3<T,R>{
		public  Step4<T,R> mapAs$3(Object f);
		public  <T> ForComprehension<T,R>.ComphrensionData<T,R>  $(String name,Object f);
		public <T> ForComprehension<T,R>.ComphrensionData<T,R>  $(String name,Supplier f);
		public  Step4<T,R> mapAs$3(Supplier f);
		//public R yield(Supplier s);
		
	}
	static interface Step4<T,R>{
		public  Step4<T,R> filter(Supplier<Boolean> s);
		public  <T> ForComprehension<T,R>.ComphrensionData<T,R>  $(String name,Object f);
		public <T> ForComprehension<T,R>.ComphrensionData<T,R>  $(String name,Supplier f);
		public <R> R yield(Supplier s);
		
	}
	static interface Step5<T,R>{
		public  <T> ForComprehension<T,R>.ComphrensionData<T,R>  $(String name,Object f);
		public <T> ForComprehension<T,R>.ComphrensionData<T,R>  $(String name,Supplier f);
		public <R> R yield(Supplier s);
		
	}

	class ComphrensionData<T,R> implements Step1<T,R>, Step2<T,R>,Step3<T,R>,Step4<T,R>,Step5<T,R>{
		BaseComprehensionData data;
		
		
		public ComphrensionData(ContextualExecutor delegate) {
			super();
			data = new BaseComprehensionData(delegate,convertCollections);
		}
		
		public  ComphrensionData<T,R> filter(Supplier<Boolean> s){
			data.guardInternal(s);
			return this;
			
		}
		
		public R yield(Supplier s){
			return data.yieldInternal(s);
			
		}
		public <T> T $(String name){
			return data.$Internal(name);
		
		}
		public <T> T $1(){
			return data.$Internal("_1");
		
		}
		public <T> T $2(){
			return data.$Internal("_2");
		
		}
		public <T> T $3(){
			return data.$Internal("_3");
		
		}
		public  <T> ForComprehension<T,R>.ComphrensionData<T,R> $(String name,Object f){
			data.$Internal(name, f);
			
			return (ComphrensionData)this;
		}
		public  <T> ForComprehension<T,R>.ComphrensionData<T,R> $(String name,Supplier f){
			data.$Internal(name, f);
			
			return (ComphrensionData)this;
		}
		public   Step2<T,R> flatMapAs$1(Object f){
			data.$Internal("_1", f);
			
			return (ComphrensionData)this;
		}
		public   Step3<T,R> flatMapAs$2(Object f){
			data.$Internal("_2", f);
			return (ComphrensionData)this;
		}
		public   Step3<T,R> flatMapAs$2(Supplier f){
			data.$Internal("_2", f);
			return (ComphrensionData)this;
		}
		public   Step4<T,R> mapAs$3(Object f){
			data.$Internal("_3", f);
			return (ComphrensionData)this;
		}
		public   Step4<T,R> mapAs$3(Supplier f){
			data.$Internal("_3", f);
			return (ComphrensionData)this;
		}
	}
}