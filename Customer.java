#Customer
class Customer{
    private String _name;
    private Vector _rentals = new Vector();

    public Customer(String name)
    {
        _name = name;
    }

    public void addRental(Rental arg)
    {
        _rentals.addElement(arg);
    }

    public String getName()
    {
        return _name;
    }

    public String statement()
    {
        double totalAmount = 0;
        int frequentRenterPoints = 0;
        Enumeration rentals = _rentals.elements();
        String result = "~~~";
        while(rentals.hasMoreElements())
        {
            Rental each = (Rental)rentals.nextElement();
            result += "~~" + each.getMovie().getTitle() + "~~" + String.valueOf(each.getCharge(each));
        }

        result += " ~~ " + String.valueOf(getTotalCharge()) + String.valueOf(getTotalFrequentRenterPoints());
        return result;
    }

    private int getTotalFrequentRenterPoints()
    {
        int result = 0;
        Enumeration rentals = _rentals.elements();
        while(rentals.hasMoreElements())
        {
            Rental each = (Rental)rentals.nextElement();
            result += each.getFrequentRenterPoints();
        }
        return result;
    }

    private double getTotalCharge()
    {
        double result = 0;
        Enumeration rentals = _rentals.elements();
        while(rentals.hasMoreElements())
        {
            Rental each = (Rental)rentals.nextElement();
            result += each.getCharge(each);
        }
        return result;
    }
}