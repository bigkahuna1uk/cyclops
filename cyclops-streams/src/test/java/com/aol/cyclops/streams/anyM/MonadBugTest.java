package com.aol.cyclops.streams.anyM;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

import com.aol.cyclops.internal.Monad;
import com.aol.cyclops.monad.AnyM;
public class MonadBugTest {
	@Test
	 public void test() {
	    List<Integer> list = Arrays.asList(1,2,3);
	    Monad<Stream<Integer>, Integer> m = Monad.of(list.stream());
	    AnyM<Integer> any = m.anyM();
	    AnyM<Integer> mapped = any.flatMap(e -> any.unit(e));
	    List<Integer> unwrapped = mapped.asSequence().toList();
	    assertEquals(list, unwrapped);
	}
}
