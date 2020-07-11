#Rental
class Rental{
    private Movie _movie;
    private int _daysRented;

    public Rental(Movie movie, int _daysRented)
    {
        _movie = movie;
        _daysRented = daysRented;
    }

    public Movie getMovie()
    {
        return _movie;
    }

    private double getCharge()
    {
        return _movie.getCharge(int _daysRented);
    }

    private int getFrequentRenterPoints(int _daysRented)
    {
        return _movie.getFrequentRenterPoints();
    }
}