package de.volkmann;

import java.util.Calendar;
import java.util.GregorianCalendar;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author bvolkman
 */
public class RedPencilPromotionTest {

    private static final int TEST_PRICE = 100;
    private static final int TEST_PRICE_REDUCED = 80;
    private static final int TEST_PRICE_REDUCTION = 20;

    @BeforeClass
    public static void setUpClass() {
    }

    private Calendar rightNow;
    private Calendar lastChange;
    private RedPencilPromotion instance;

    public RedPencilPromotionTest() {
    }

    @Before
    public void setUp() {
//        rightNow = new GregorianCalendar(NOW_YEAR, NOW_MONTH, NOW_DAY);
        rightNow = Calendar.getInstance();
        lastChange = new GregorianCalendar(rightNow.get(Calendar.YEAR), 
                rightNow.get(Calendar.MONTH), 
                rightNow.get(Calendar.DAY_OF_MONTH));
        lastChange.add(Calendar.DAY_OF_MONTH, 
                -(RedPencilPromotion.STABLE_AT_LEAST));
        instance = new RedPencilPromotion();
        instance.setPrice(TEST_PRICE, lastChange);
    }

    @After
    public void tearDown() {
    }

//    @Test
    public void testSomeMethod() {
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void initValidPromotion() {
        assertThat("Init Promotion", instance.initPromotion(TEST_PRICE_REDUCTION, 
                rightNow, RedPencilPromotion.MAX_DURATION), is(true));
        assertThat("setting Price", instance.getPrice(rightNow), 
                is(TEST_PRICE_REDUCED));
        assertThat("setting old price", instance.getOriginalPrice(), 
                is(TEST_PRICE));
        assertThat("setting Duration", instance.getDuration(), 
                is(RedPencilPromotion.MAX_DURATION));
    }

    @Test
    public void initPromotionReductionLow() {
        assertThat("Init Promotion with low reduction must fail", 
                instance.initPromotion(RedPencilPromotion.MIN_REDUCTION-1 , 
                        rightNow, RedPencilPromotion.MAX_DURATION), is(false));
    }

    @Test
    public void initPromotionReductionHigh() {
        assertThat("Init Promotion with high reduction must fail", 
                instance.initPromotion(RedPencilPromotion.MAX_REDUCTION+1, 
                        rightNow, RedPencilPromotion.MAX_DURATION), is(false));
    }

    @Test
    public void initPromotionLastChangeInvalid() {
        Calendar now = lastChange;
        lastChange.add(Calendar.DAY_OF_MONTH, RedPencilPromotion.STABLE_AT_LEAST-1);
        instance.setPrice(TEST_PRICE, lastChange);
        assertThat("last change of Price within 30 Days", 
                instance.initPromotion(TEST_PRICE_REDUCTION, rightNow, 
                        RedPencilPromotion.MAX_DURATION), is(false));
    }

    @Test
    public void initPromotionWithInvalidDuration() {
        int duration = RedPencilPromotion.MAX_DURATION + 1;
        assertThat("Init promotion with invalid duration", 
                instance.initPromotion(TEST_PRICE_REDUCTION, rightNow, duration), 
                is(false));
    }

    @Test
    public void setPriceFurtherReduction() {
        instance.initPromotion(TEST_PRICE_REDUCTION, rightNow, 
                RedPencilPromotion.MAX_DURATION);
        instance.setPriceReduction(30);
        assertThat("Start of promotion must not change", 
                instance.getPromotionStart(), is(giveDay(rightNow)));
        assertThat("new price set", instance.getPrice(rightNow), is(70));
    }
    
    @Test
    public void setPriceLessReduction() {
        instance.initPromotion(TEST_PRICE_REDUCTION, rightNow, 
                RedPencilPromotion.MAX_DURATION);
        instance.setPriceReduction(10);
        assertThat("Promotion must be ended", instance.isActive(rightNow), 
                is(false));
    }
    
    @Test
    public void increasePriseDuringPromotion() {
        instance.initPromotion(TEST_PRICE_REDUCTION, rightNow, 
                RedPencilPromotion.MAX_DURATION);
        rightNow.add(Calendar.DAY_OF_MONTH, 15);
        instance.setPrice(110, rightNow); 
        assertThat("Promotion must be ended", instance.isActive(rightNow), 
                is(false));
    }
    
    @Test
    public void increaseReductionAbove() {
        instance.initPromotion(TEST_PRICE_REDUCTION, rightNow, 
                RedPencilPromotion.MAX_DURATION);
        rightNow.add(Calendar.DAY_OF_MONTH, 15);
        instance.setPriceReduction(RedPencilPromotion.MAX_REDUCTION+1);
        assertThat("Promotion must be ended", instance.isActive(rightNow), 
                is(false));
    }
    
    @Test
    public void initNewPromotionDuringRunningPromotion() {
        instance.initPromotion(TEST_PRICE_REDUCTION, rightNow, 
                RedPencilPromotion.MAX_DURATION);
        rightNow.add(Calendar.DAY_OF_MONTH, 15);
        assertThat("Init new Promotion During another must fail", 
                instance.initPromotion(TEST_PRICE_REDUCTION, rightNow, 
                        RedPencilPromotion.MAX_DURATION), is(false));
    }
    
    @Test
    public void initPostPromotion() {
        RedPencilPromotion tmp = new RedPencilPromotion();
        lastChange.add(Calendar.DAY_OF_MONTH, -60);
        tmp.setPrice(TEST_PRICE, lastChange);
        lastChange.add(Calendar.DAY_OF_MONTH, 30);
        instance.initPromotion(TEST_PRICE_REDUCTION, lastChange, 15);
        rightNow.add(Calendar.DAY_OF_MONTH, 15);
        assertThat("Init new Promotion after another must work", 
                instance.initPromotion(TEST_PRICE_REDUCTION, rightNow, 
                        RedPencilPromotion.MAX_DURATION), is(true));
    }
    
    private Calendar giveDay(Calendar cal) {
        return new GregorianCalendar(cal.get(Calendar.YEAR), 
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }
}
