# Refactoring

self study for refactoring. </br>
리팩토링에 관한 책을 공부하고 남겨두는 기록. </br></br>
2020. 07. 11 시작</br>
REFECTORING. 1999. by Martic. Fowler.

</br>

## :memo: Table of Contents

- [예제로 시작하는 리팩토링](#예제로-시작하는-리팩토링)

</br>

## 예제로 시작하는 리팩토링

### 리팩토링을 시작하기 전에

- 리팩토링을 안전하게 하기 위해서 **테스트 셋**을 준비해야 한다.

이 테스트 셋은 self-checking이다. </br>
이것이 **첫 번째 안전장치**이다.


### 리팩토링 중에

1. 값이 변하는 변수와 변하지 않는 변수를 구분한다.

값이 변하는 변수에 대해서 함수를 따로 만들어 리팩토링할 수 있다. 이 경우 리팩토링 도구의 도움을 받을 수 있다.</br>
리팩토링은 작은 규모에서 진행되기 때문에 리팩토링 과정에서 발생되는 오류를 테스트 셋을 통해 쉽게 찾을 수 있다. </br>
이것이 **두 번째 안전장치** 이다.

2. 이전의 메소드를 참조하는 모든 곳을 찾아서 리팩토링된 함수를 사용하게 한다.

이전의 메소드가 public이고 클래스의 인터페이스를 변경하고 싶지 않을 때, 기존 메소드가 새 메소드에 대해 작업을 위임하도록 남겨두기도 한다.

3. 임시변수는 가능하면 제거하는 것이 좋다.

퍼포먼스 측면에서는 여러 번 함수를 호출해야 하므로 손해지만 클래스에서 최적화 될 수 있다.  [리팩토링과 퍼포먼스](#리팩토링과-퍼포먼스) </br>
당장 최적화 작업을 하는 것이 아니라면 차후 최적화를 효과적으로 하기 위해서라도 리팩토링은 필수적이다. </br>
프로파일을 보기 전에는 루프에 대한 퍼포먼스 하락에 대해서 걱정하지 않아도 된다.`(case by case?)` </br>


### 조건문에 다형성 추가

switch문에 다른 클래스의 객체를 포함시키는 것은 좋지 않다.


### 클래스 상속

단순한 상속은 기능을 명확히 구분할 수 없다. </br>
Strategy/State 패턴을 이용해 클래스를 상속한다. </br>

get/set 메소드를 이용해서 접근하는 분리된 클래스를 만든다. </br>
분리된 클래스에서 추상메소드를 선언하고 상속하는 클래스에서 기능별 메소드를 오버라이드 한다.

## 리팩토링 전후 비교

### 리팩토링 전

```java
#Movie
public class Movie
{
    public static final int CHILDREN = 2;
    public static final int REGULAR = 0;
    public static final int NEW_RELEASE = 1;
    
    private String _title;
    private int _priceCode;

    public Movie(String title, int priceCode)
    {
        _title = title;
        _priceCode = priceCode;
    }

    public int getPriceCode()
    {
        return _priceCode;
    }

    public void setPriceCode(int arg) 
    {
        _priceCode = arg;
    }

    public String getTitle()
    {
        return _title;
    }
}
```

```java
#Rental
class Rental{
    private Movie _movie;
    private int _daysRented;

    public Rental(Movie movie, int _daysRented)
    {
        _movie = movie;
        _daysRented = daysRented;
    }
    public int getDaysRented()
    {
        return _daysRented;
    }
    public Movie getMovie()
    {
        return _movie;
    }
}
```

```java
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
            double thisAmount = 0;
            Rental each = (Rental)rentals.nextElement();

            switch(each.getMovie().getPriceCode())
            {
                case Movie.REGULAR:
                thisAmount += 2;
                if(each.getDaysRented() > 2)
                    thisAmount += (each.getDaysRented() - 2) * 1.5;
                break;
                case Movie.NEW_RELEASE:
                thisAmount += each.getDaysRented() * 3;
                break;
                case Movie.CHILDRENS:
                thisAmount += 1.5;
                if(each.getDaysRented() > 3)
                    thisAmount += (each.getDaysRented() - 3) * 1.5;
                break;
            }

            frequentRenterPoints++;
            if((each.getMovie().getPriceCode() == Movie.NEW_RELEASE) && each.getDaysRented() > 1)
                frequentRenterPoints++;

            result += "~~" + each.getMovie().getTitle() + "~~" + String.valueOf(thisAmount);
            totalAmount += thisAmount;
        }

        result += " ~~ " + String.valueOf(totalAmount) + String.valueOf(frequentRenterPoints);
        return result;
    }
}
```

### 리팩토링 중

- 기능 분리와 함께 중복변수, 임시변수 제거

switch, amount계산 분리 </br>
임시변수 totalAmount, frequentRenterPoints 분리

```java
//Rental
private double getCharge()
{
    double result = 0;
    switch(getMovie().getPriceCode())
    {
        case Movie.REGULAR:
        result += (getDaysRented() - 2) * 1.5;
        break;
        case Movie.NEW_RELEASE:
        result += getDaysRented() * 3;
        break;
        case Movie.CHILDRENS:
        result += 1.5;
        if(getDaysRented() > 3)
            result += (getDaysRented() - 3) * 1.5;
        break;
    }
    return result;
}

private int getFrequentRenterPoints()
{
    if((getMovie().getPriceCode() == Movie.NEW_RELEASE) && getDaysRented() > 1)
        return 2;
    else 
        return 1;
}
    
//Customer
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
```

### 리팩토링 후

- 다형성 추가

State패턴의 일환으로 Movie클래스에서 분리한 Price클래스 추가

```java
#price
abstract class Price
{
    abstract int getPriceCode();
    abstract double getCharge(int daysRented);
    private int getFrequentRenterPoints(int daysRented)
    {
        return 1;
    }
}
            
class ChildrensPrice extends Price
{
    int getPriceCode()
    {
        return Movie.CHILDRENS;
    }

    double getCharge(int daysRented)
    {
        double result = 1.5;
        if(daysRented > 3)
            result += (daysRented - 3) * 1.5;
        return result;
    }
}

class NewReleasePrice extends Price
{
    int getPriceCode()
    {
        return Movie.NEW_RELEASE;
    }

    double getCharge(int daysRented)
    {
        return daysRented * 3;
    }

    int getFrequentRenterPoints(int daysRented)
    {
        return (daysRented > 1) ? 2 : 1;
    }
}

class RegularPrice extends Price
{
    int getPriceCode()
    {
        return Movie.REGULAR;
    }

    double getCharge(int daysRented)
    {
        double result = 2;
        if(daysRented > 2)
            result += (daysRented - 2) * 1.5;
        return result;
    }
}
```

```java
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
```

```java
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
```

```java
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
```


## 리팩토링과 퍼포먼스
