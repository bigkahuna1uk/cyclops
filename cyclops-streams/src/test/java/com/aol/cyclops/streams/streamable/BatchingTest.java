package com.aol.cyclops.streams.streamable;


import static com.aol.cyclops.sequence.streamable.Streamable.fromIntStream;
import static com.aol.cyclops.sequence.streamable.Streamable.iterate;
import static com.aol.cyclops.sequence.streamable.Streamable.of;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Value;

import org.junit.Test;

import com.aol.cyclops.sequence.SequenceM;
import com.aol.cyclops.sequence.streamable.Streamable;
import com.aol.cyclops.streams.SimpleTimer;
public class BatchingTest {
	@Test
	public void batchUntil(){
		assertThat(of(1,2,3,4,5,6)
				.batchUntil(i->i%3==0)
				.toList().size(),equalTo(2));
		assertThat(Streamable.of(1,2,3,4,5,6)
				.batchUntil(i->i%3==0)
				.toList().get(0),equalTo(Arrays.asList(1,2,3)));
	}
	@Test
	public void batchWhile(){
		assertThat(Streamable.of(1,2,3,4,5,6)
				.batchWhile(i->i%3!=0)
				.toList()
				.size(),equalTo(2));
		assertThat(Streamable.of(1,2,3,4,5,6)
				.batchWhile(i->i%3!=0)
				.toList(),equalTo(Arrays.asList(Arrays.asList(1,2,3),Arrays.asList(4,5,6))));
	}
	@Test
	public void batchUntilCollection(){
		assertThat(Streamable.of(1,2,3,4,5,6)
				.batchUntil(i->i%3==0,()->new ArrayList<>())
				.toList().size(),equalTo(2));
		assertThat(Streamable.of(1,2,3,4,5,6)
				.batchUntil(i->i%3==0,()->new ArrayList<>())
				.toList().get(0),equalTo(Arrays.asList(1,2,3)));
	}
	@Test
	public void batchWhileCollection(){
		assertThat(Streamable.of(1,2,3,4,5,6)
				.batchWhile(i->i%3!=0,()->new ArrayList<>())
				.toList().size(),equalTo(2));
		assertThat(Streamable.of(1,2,3,4,5,6)
				.batchWhile(i->i%3!=0,()->new ArrayList<>())
				.toList(),equalTo(Arrays.asList(Arrays.asList(1,2,3),Arrays.asList(4,5,6))));
	}
	@Test
	public void batchByTime2(){
		for(int i=0;i<5;i++){
			System.out.println(i);
			assertThat(of(1,2,3,4,5, 6)
							.map(n-> n==6? sleep(1) : n)
							.batchByTime(10,TimeUnit.MICROSECONDS)
							.toList()
							.get(0)
							,not(hasItem(6)));
		}
	}
	private Integer sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}
	@Test
	public void windowwByTime2(){
		for(int i=0;i<5;i++){
			System.out.println(i);
			assertThat(of(1,2,3,4,5, 6)
							.map(n-> n==6? sleep(1) : n)
							.windowByTime(10,TimeUnit.MICROSECONDS)
							.toList()
							.get(0).sequenceM().toList()
							,not(hasItem(6)));
		}
	}
	@Test
	public void jitter() {
		
				fromIntStream(IntStream.range(0, 1000))
				.map(it -> it * 100)
				.jitter(100l)
				.peek(System.out::println)
				.forEach(a->{});
	}

	@Test
	public void fixedDelay2() {

		fromIntStream(IntStream.range(0, 1000))
				.fixedDelay(1l, TimeUnit.MICROSECONDS).peek(System.out::println)
				.forEach(a->{});
	}
	@Test
	public void onePerSecond() {

		
				iterate(0, it -> it + 1)
				.limit(100)
				.onePer(1, TimeUnit.MICROSECONDS)
				.map(seconds -> "hello!")
				.peek(System.out::println)
				.toList();

	}

	@Value
	static class Status{
		long id;
	}
	private String saveStatus(Status s) {
		//if (count++ % 2 == 0)
		//	throw new RuntimeException();

		return "Status saved:" + s.getId();
	}
	
	@Test
	public void batchBySize() {

		iterate("", last -> "next")
				.limit(100)
				.batchBySize(10)
				.onePer(1, TimeUnit.MICROSECONDS)
				.peek(batch -> System.out.println("batched : " + batch))
				.flatMapStream(Collection::stream)
				.peek(individual -> System.out.println("Flattened : "
						+ individual))
				.forEach(a->{});

	}
	
	private Object nextFile() {
		return "hello";
	}
	AtomicInteger count2 =new AtomicInteger(0);
	int count3 =0;
	volatile int otherCount;
	volatile int peek =0;
	@Test
	public void batchByTimeFiltered() throws IOException {
		for(int x=0;x<10;x++){
			count2=new AtomicInteger(0);
			List<Collection<Map>> result = new ArrayList<>();
					
					iterate("", last -> "hello")
					.limit(1000)
					
					.peek(i->System.out.println(++otherCount))
			
					.batchByTime(1, TimeUnit.MICROSECONDS)
					
					.peek(batch -> System.out.println("batched : " + batch + ":" + (++peek)))
				
					.peek(batch->count3= count3+batch.size())
					
					.forEach(next -> { 
					count2.getAndAdd(next.size());});
		
			
			System.out.println("In flight count " + count3 + " :" + otherCount);
			System.out.println(result.size());
			System.out.println(result);
			System.out.println("x" +x);
			assertThat(count2.get(),equalTo(1000));

		}
	}
	@Test
	public void windowByTimeFiltered() {

		for(int x=0;x<10;x++){
			count2=new AtomicInteger(0);
			List<Collection<Map>> result = new ArrayList<>();
					
					iterate("", last -> "hello")
					.limit(1000)
					
					.peek(i->System.out.println(++otherCount))
			
					.windowByTime(1, TimeUnit.MICROSECONDS)
					
					.peek(batch -> System.out.println("batched : " + batch + ":" + (++peek)))
				
					.peek(batch->count3= count3+(int)batch.stream().count())
					
					.forEach(next -> { 
					count2.getAndAdd((int)next.stream().count());});
		
			
			System.out.println("In flight count " + count3 + " :" + otherCount);
			System.out.println(result.size());
			System.out.println(result);
			System.out.println("x" +x);
			assertThat(count2.get(),equalTo(1000));

		}
	}
	@Test
	public void batchByTimex() {

		
				iterate("", last -> "next")
				.limit(100)
				
				
				.peek(next->System.out.println("Counter " +count2.incrementAndGet()))
				.batchByTime(10, TimeUnit.MICROSECONDS)
				.peek(batch -> System.out.println("batched : " + batch))
				.filter(c->!c.isEmpty())
				
				
				.forEach(System.out::println);
			

	}
	@Test
	public void windowByTimex() {

		
				iterate("", last -> "next")
				.limit(100)
				
				
				.peek(next->System.out.println("Counter " +count2.incrementAndGet()))
				.windowByTime(10, TimeUnit.MICROSECONDS)
				.peek(batch -> System.out.println("batched : " + batch))
				.filter(c->! (c.stream().count()==0))
				
				
				.forEach(System.out::println);
			

	}

	@Test
	public void batchBySize3(){
		System.out.println(of(1,2,3,4,5,6).batchBySize(3).collect(Collectors.toList()));
		assertThat(of(1,2,3,4,5,6).batchBySize(3).collect(Collectors.toList()).size(),is(2));
	}
	@Test
	public void batchBySizeAndTimeSizeCollection(){
		
		assertThat(of(1,2,3,4,5,6)
						.batchBySizeAndTime(3,10,TimeUnit.SECONDS,()->new ArrayList<>())
						.toList().get(0)
						.size(),is(3));
	}
	@Test
	public void batchBySizeAndTimeSize(){
		
		assertThat(of(1,2,3,4,5,6)
						.batchBySizeAndTime(3,10,TimeUnit.SECONDS)
						.toList().get(0)
						.size(),is(3));
	}
	@Test
	public void windowBySizeAndTimeSize(){
		
		assertThat(of(1,2,3,4,5,6)
						.windowBySizeAndTime(3,10,TimeUnit.SECONDS)
						.toList().get(0).stream()
						.count(),is(3l));
	}
	@Test
	public void windowBySizeAndTimeSizeEmpty(){
		
		assertThat(of()
						.windowBySizeAndTime(3,10,TimeUnit.SECONDS)
						.toList()
						.size(),is(0));
	}
	@Test
	public void batchBySizeAndTimeTime(){
		
		for(int i=0;i<10;i++){
			System.out.println(i);
			List<List<Integer>> list = of(1,2,3,4,5,6)
					.batchBySizeAndTime(10,1,TimeUnit.MICROSECONDS)
					.toList();
			
			assertThat(list
							.get(0)
							,not(hasItem(6)));
		}
	}
	@Test
	public void batchBySizeAndTimeTimeCollection(){
		
		for(int i=0;i<10;i++){
			System.out.println(i);
			List<ArrayList<Integer>> list = of(1,2,3,4,5,6)
					.batchBySizeAndTime(10,1,TimeUnit.MICROSECONDS,()->new ArrayList<>())
					.toList();
			
			assertThat(list
							.get(0)
							,not(hasItem(6)));
		}
	}
	@Test
	public void windowBySizeAndTimeTime(){
		
		for(int i=0;i<10;i++){
			System.out.println(i);
			List<Streamable<Integer>> list = of(1,2,3,4,5,6)
					.map(n-> n==6? sleep(1) : n)
					.windowBySizeAndTime(10,1,TimeUnit.MICROSECONDS)
					
					.toList();
			
			assertThat(list
							.get(0)
							.sequenceM()
							.toList()
							,not(hasItem(6)));
		}
	}
	
	
	@Test
	public void batchBySizeSet(){
		
		assertThat(of(1,1,1,1,1,1).batchBySize(3,()->new TreeSet<>()).toList().get(0).size(),is(1));
		assertThat(of(1,1,1,1,1,1).batchBySize(3,()->new TreeSet<>()).toList().get(1).size(),is(1));
	}
	@Test
	public void batchBySizeSetEmpty(){
		
		assertThat(of().batchBySize(3,()->new TreeSet<>()).toList().size(),is(0));
	}
	@Test
	public void batchBySizeInternalSize(){
		assertThat(of(1,2,3,4,5,6).batchBySize(3).collect(Collectors.toList()).get(0).size(),is(3));
	}
	@Test
	public void fixedDelay(){
		SimpleTimer timer = new SimpleTimer();
		
		assertThat(of(1,2,3,4,5,6).fixedDelay(10000,TimeUnit.NANOSECONDS).collect(Collectors.toList()).size(),is(6));
		assertThat(timer.getElapsedNanoseconds(),greaterThan(60000l));
	}
	@Test
	public void judder(){
		SimpleTimer timer = new SimpleTimer();
		
		assertThat(of(1,2,3,4,5,6).jitter(10000).collect(Collectors.toList()).size(),is(6));
		assertThat(timer.getElapsedNanoseconds(),greaterThan(20000l));
	}
	@Test
	public void debounce(){
		SimpleTimer timer = new SimpleTimer();
		
		
		assertThat(of(1,2,3,4,5,6).debounce(1000,TimeUnit.SECONDS).collect(Collectors.toList()).size(),is(1));
		
	}
	@Test
	public void debounceOk(){
		System.out.println(of(1,2,3,4,5,6).debounce(1,TimeUnit.NANOSECONDS).toList());
		assertThat(of(1,2,3,4,5,6).debounce(1,TimeUnit.NANOSECONDS).collect(Collectors.toList()).size(),is(6));
		
	}
	@Test
	public void onePer(){
		SimpleTimer timer = new SimpleTimer();
		System.out.println(of(1,2,3,4,5,6).onePer(1000,TimeUnit.NANOSECONDS).collect(Collectors.toList()));
		assertThat(of(1,2,3,4,5,6).onePer(1000,TimeUnit.NANOSECONDS).collect(Collectors.toList()).size(),is(6));
		assertThat(timer.getElapsedNanoseconds(),greaterThan(600l));
	}
	@Test
	public void xPer(){
		SimpleTimer timer = new SimpleTimer();
		System.out.println(of(1,2,3,4,5,6).xPer(6,1000,TimeUnit.NANOSECONDS).collect(Collectors.toList()));
		assertThat(of(1,2,3,4,5,6).xPer(6,100000000,TimeUnit.NANOSECONDS).collect(Collectors.toList()).size(),is(6));
		assertThat(timer.getElapsedNanoseconds(),lessThan(60000000l));
	}
	@Test
	public void batchByTime(){
		assertThat(of(1,2,3,4,5,6).batchByTime(1,TimeUnit.SECONDS).collect(Collectors.toList()).size(),is(1));
	}
	@Test
	public void batchByTimeSet(){
		
		assertThat(of(1,1,1,1,1,1).batchByTime(1500,TimeUnit.MICROSECONDS,()-> new TreeSet<>()).toList().get(0).size(),is(1));
	}
	@Test
	public void batchByTimeInternalSize(){
		assertThat(of(1,2,3,4,5,6).batchByTime(1,TimeUnit.NANOSECONDS).collect(Collectors.toList()).size(),greaterThan(5));
	}
	@Test
	public void batchByTimeInternalSizeCollection(){
		assertThat(of(1,2,3,4,5,6).batchByTime(1,TimeUnit.NANOSECONDS,()->new ArrayList<>()).collect(Collectors.toList()).size(),greaterThan(5));
	}
	@Test
	public void windowByTimeInternalSize(){
		assertThat(of(1,2,3,4,5,6).windowByTime(1,TimeUnit.NANOSECONDS).collect(Collectors.toList()).size(),greaterThan(5));
	}

}
