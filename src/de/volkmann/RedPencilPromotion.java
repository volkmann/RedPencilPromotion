package de.volkmann;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author bvolkman
 */
public class RedPencilPromotion {

    public static final int MIN_REDUCTION = 5;
    public static final int MAX_REDUCTION = 30;
    public static final int STABLE_AT_LEAST = 30;
    public static final int MAX_DURATION = 30;

    private int originalPrice;
    private Calendar lastChange;
    private int priceReduction;

    private Calendar promotionStart;
    private int duration;

    public RedPencilPromotion() {
        this.priceReduction = 0;
    }

    public int getPriceReduction() {
        return priceReduction;
    }

    public void setPriceReduction(int priceReduction) {
        if (checkPriceReduction(priceReduction) && this.priceReduction <= priceReduction) {
            this.priceReduction = priceReduction;
        } else {
            this.priceReduction = 0;
        }
    }

    public int getDuration() {
        return duration;
    }

    public Calendar getPromotionStart() {
        return promotionStart;
    }

    public int getPrice(Calendar now) {
        int price = this.originalPrice;
        if (isActive(now)) {
            int factor = 100 - this.priceReduction;
            price = (this.originalPrice * factor) / 100;
        }
        return price;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    /**
     * Get the value of lastChange
     *
     * @return the value of lastChange
     */
    public Calendar getLastChange() {
        return lastChange;
    }

    /**
     *
     * @param price
     * @param timeOfChanging
     */
    public void setPrice(int price, Calendar timeOfChanging) {
        if (isActive(timeOfChanging)) {
            resetPromotion();
        }
        this.originalPrice = price;
        this.lastChange = calendarToDay(timeOfChanging);
    }

    /**
     *
     * @param priceReduction
     * @param start
     * @param duration
     * @return
     */
    public boolean initPromotion(int priceReduction, Calendar start, int duration) {
        boolean valid = false;
        Calendar rightNow = Calendar.getInstance();
        if (evaluateInitConditions(priceReduction, start, duration) && 
                !(isActive(Calendar.getInstance()))) {
            this.duration = duration;
            this.priceReduction = priceReduction;
            setPromotionStart(start);
            valid = true;
        }
        return valid;
    }

    public boolean isActive(Calendar now) {
        return (this.priceReduction != 0 && checkRuntime(now));
    }

    private void setLastChange(Calendar lastChange) {
        this.lastChange = calendarToDay(lastChange);
    }

    private void setPromotionStart(Calendar promotionStart) {
        this.promotionStart = calendarToDay(promotionStart);
    }

    private Calendar calendarToDay(Calendar in) {
        return new GregorianCalendar(in.get(Calendar.YEAR),
                in.get(Calendar.MONTH), in.get(Calendar.DAY_OF_MONTH));
    }

    private boolean checkPriceReduction(int priceReduction) {
        return MAX_REDUCTION >= priceReduction && MIN_REDUCTION <= priceReduction;
    }

    private boolean checkRuntime(Calendar now) {
        Calendar tmp = calendarToDay(now);
        Calendar end = new GregorianCalendar(this.promotionStart.get(Calendar.YEAR),
                this.promotionStart.get(Calendar.MONTH),
                this.promotionStart.get(Calendar.DAY_OF_MONTH));
        end.add(Calendar.DAY_OF_MONTH, this.duration);
        return (tmp.compareTo(this.promotionStart) >= 0 && tmp.compareTo(end) <= 0);
    }

    private boolean checkLastChange(Calendar now) {
        Calendar tmp = calendarToDay(now);
        tmp.add(Calendar.DAY_OF_MONTH, -STABLE_AT_LEAST);
        return (tmp.compareTo(lastChange) >= 0);
    }

    private void resetPromotion() {
        this.duration = 0;
        this.priceReduction = 0;
        this.promotionStart = null;
    }

    private boolean evaluateInitConditions(int priceReduction, Calendar start,
            int duration) {
        return (checkPriceReduction(priceReduction)
                && checkLastChange(start)
                && duration > 0 && duration <= MAX_DURATION);
    }
}
