package au.com.codeka.carrot.lib.filter;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import au.com.codeka.carrot.base.CarrotException;
import au.com.codeka.carrot.interpret.InterpretException;
import au.com.codeka.carrot.lib.Filter;

public class AddFilterTest extends ZzzBase {

  @Before
  public void setUp() throws Exception {
    filter = new AddFilter();
  }

  @Test
  public void testInt() throws CarrotException {
    Object res = filter.filter(562, compiler, "56");
    assertEquals(618, res);
  }

  @Test
  public void testInteger() throws CarrotException {
    Object res = filter.filter(new Integer(-298), compiler, "\"23\"");
    assertEquals(-275, res);
  }

  @Test(expected = InterpretException.class)
  public void testFloat() throws CarrotException {
    Object res = filter.filter(new Double(-20.24), compiler, "abc");
    assertEquals(20.24f, (Double) res, 0.01f);
  }

  @Test
  public void testDouble() throws CarrotException {
    compiler.assignRuntimeScope("var1", 25);
    Object res = filter.filter(new Double(-20.24), compiler, "var1");
    assertEquals(4.76f, (Double) res, 0.01f);
  }

  @Test
  public void testLong() throws CarrotException {
    Object res = filter.filter(new Long(-0), compiler, new String[] { "2" });
    assertEquals(2L, res);
  }

  @Test
  public void testShort() throws CarrotException {
    Object res = filter.filter(new Short((short) -22222222), compiler, "'2'");
    assertEquals((int) (short) -22222220, res); // short + short = int
  }

  @Test
  public void testByte() throws CarrotException {
    Object res = filter.filter(new Byte((byte) 222), compiler, "-3");
    assertEquals((int) (byte) 219, res); // byte + byte = int
  }

  @Test
  public void testString() throws CarrotException {
    Object res = filter.filter("-215.5256", compiler, "-15.2");
    assertEquals(-230.72559d, (Double) res, 0.0001d);
  }

  @Test
  public void testBigInt() throws CarrotException {
    Object res = filter.filter(BigInteger.valueOf(-1547898522234l), compiler, "2234");
    assertEquals(BigInteger.valueOf(-1547898520000l), res);
  }

  @Test(expected = InterpretException.class)
  public void testString2() throws CarrotException {
    Object res = filter.filter("abcd", compiler, "2");
    assertEquals(12, res);
  }

  @Test
  public void testOther() throws CarrotException {
    Filter af = new AbsFilter();
    Object res = filter.filter(af, compiler, "1");
    assertEquals(af, res);
  }

  @Ignore
  public void testGetName() {
    assertEquals("add", filter.getName());
  }
}
