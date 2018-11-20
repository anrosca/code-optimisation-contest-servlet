package launch.domain;

import java.util.Objects;

public class BestBuy {

    private Double buyPoint;

    private Double sellPoint;

    private String batchName;

    public BestBuy(final Double buyPoint, final Double sellPoint, final String batchName) {
        this.buyPoint = buyPoint;
        this.sellPoint = sellPoint;
        this.batchName = batchName;
    }

    public Double getBuyPoint() {
        return buyPoint;
    }

    public Double getSellPoint() {
        return sellPoint;
    }

    public String getBatchName() {
        return batchName;
    }

    @Override
    public String toString() {
        return "BestBuy{" +
                "buyPoint=" + buyPoint +
                ", sellPoint=" + sellPoint +
                ", batchName='" + batchName + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BestBuy)) {
            return false;
        }
        final BestBuy bestBuy = (BestBuy) o;
        return Objects.equals(buyPoint, bestBuy.buyPoint) &&
                Objects.equals(sellPoint, bestBuy.sellPoint) &&
                Objects.equals(batchName, bestBuy.batchName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(buyPoint, sellPoint, batchName);
    }
}