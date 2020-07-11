#Movie
public class Movie
{
    public static final int CHILDREN = 2;
    public static final int REGULAR = 0;
    public static final int NEW_RELEASE = 1;
    
    private String _title;
    private int _priceCode;

    private Price price;

    public Movie(String title, int priceCode)
    {
        _title = title;
        setPriceCode(priceCode);
    }

    public int getPriceCode()
    {
        return _priceCode;
    }

    public void setPriceCode(int arg) 
    {
        switch(getMovie().getPriceCode())
        {
            case Movie.REGULAR:
                price = new RegularPrice();
            break;
            case Movie.NEW_RELEASE:
                price = new NewReleasePrice();
            break;
            case Movie.CHILDRENS:
                price = new ChildrensPrice();
            break;
        }
    }

    public String getTitle()
    {
        return _title;
    }

    private int getCharge(int _daysRented)
    {
        return price.getCharge(_daysRented);
    }

    private double getFrequentRenterPoints(int _daysRented)
    {
        return price.getFrequentRenterPoints(_daysRented);

    }
}