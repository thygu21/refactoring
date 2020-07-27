# Refactoring

self study for refactoring. </br>
리팩토링에 관한 책을 공부하고 남겨두는 기록. </br></br>
2020. 07. 11 시작</br>
REFECTORING. 1999. by Martic. Fowler.

</br>

## :memo: Table of Contents

1. [예제로 시작하는 리팩토링](#예제로-시작하는-리팩토링)
2. [리팩토링을 해야하는 이유](#리팩토링을-해야하는-이유)
3. [코드속의 나쁜 냄새](#코드속의-나쁜-냄새)
4. [테스트](#테스트)
5. [리팩토링 카탈로그로](#리팩토링-카탈로그로)
6. [메소드 정리](#메소드-정리)

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

## 리팩토링을 해야하는 이유

리팩토링을 해야하는 두 가지 이유가 있다.</br>

첫 번째는 **이해하기 쉽고 수정하기 쉬운** 코드를 만들 수 있다.</br>
두 번째로는 **겉으로 보이는 동작을 변경시키지 않고** 내부 구조를 바꿀 수 있다.</br>

리팩토링은 별도의 시간을 내서 하는 것이 아니라 틈틈히 해야한다.</br>

### 언제 리팩토링을 해야 하는가?

1. 삼진 규칙 : 세번 중복 작업을 하면 그때 리팩토링한다.
2. 기능을 추가할 때 리팩토링을 하라
3. 버그를 수정해야 할 때 리팩토링을 하라
4. 코드 검토를 할 때 리팩토링을 하라

기능을 추가할 때나 새로운 아이디어를 적용할 때 리팩토링된 코드는 작업을 더 용이하게 만들어준다.

### 리팩토링과 퍼포먼스

리팩토링은 프로그램을 프로그램을 더 느리게 만든다.</br>
하지만, 퍼포먼스 튜닝을 더 쉽게 할수 있게 만들어준다.</br>

프로그램의 대부분의 시간을 잡아먹는 동작은 극히 작은 코드의 한 부분에서 발생한다. 리팩토링된 코드는 그 작은 부분을 발견하기 쉽게하여 최적화가 쉬워진다.

## 코드속의 나쁜 냄새

리팩토링을 하는 방법을 아는 것은 쉽지만, 언제 리팩토링을 해야 하는지 아는 것은 어렵다. 저자는 코드에서 나쁜 냄새가 나는 아래 몇 가지의 예시를 통해 리팩토링을 해야 할 시기가 언제인지 설명한다.

### 중복된 코드

중복된 코드가 한 곳 이상에서 발견된다면 합쳐서 프로그램을 계선할 수 있다.

### 긴 메소드

메소드가 길 때, 메소드를 리팩토링 할 수 있다.
1. 조건문이나 반복문
2. 주석이 포함되어있는 구문
3. 설명을 필요로하는 단 한줄의 코드

위의 경우에 해당되는 메소드는 리팩토링이 필요하다고 판단할 수 있다. </br>
또, 여기서의 핵심은 메소드를 리팩토링한 결과가 해당 코드의 목적을 잘 설명할 수 있어야 함을 염두해둔다.</br>

### 거대한 클래스

거대한 클래스의 경우 공통적인 변수들의 부분집합에 대해서 **별도의 클래스**를 만들거나 **서브클래스**로 만들수 있다.</br>

### 긴 파라미터 리스트

클래스의 객체는 많은 변수들을 포함하고 있기 때문에 메소드의 파라미터를 길게 할 필요가 없다. 단, 종속성 구조를 만들고 싶지 않다면 고통스럽지만 파라미터를 유지해야 한다.

### 확산적 변경

한 클래스가 다른 이유로 인해 자주 변경되는 경우에 발생한다. **특정 코드를 변경 했을 때 영향을 미치는 클래스나 메소드들이 고정적**이라면, 특정 원인에 대해 변해야 하는 것을 모두 찾고 하나로 묶어 클래스를 추출한다. 그래서 변화가 이루어 졌을때 별도의 수정없이도 변화가 반영될 수 있도록 리팩토링 한다.

### 산탄총 수술

확산적 변경과는 반대로 한 클래스의 변화가 다른 여러 클래스에 영향을 미칠 경우에 발생한다. 이런 경우 변경해야 할 부분을 모두 하나의 클래스로 만든다. 적당히 몰아넣을 클래스가 없다면 새로운 클래스를 만들어 몰아넣는다.

### 기능에 대한 욕심

메소드가 다른 클래스의 데이터에 관심을 가지고 있을 경우, 데이터의 중복을 최대한 피하기 위해 해당 메소드가 어떤 클래스의 데이터를 가장 많이 사용하는지를 보고 그 클래스로 옮긴다. 모든경우에 확실한 답이 정해져 있지 않기 때문에 이 경우에는 유의해야 한다.

### 데이터 덩어리

필드로 나타나는 데이터의 덩어리를 찾는다. 그리고 뭉쳐있는 데이터 덩어리들을 객체로 바꾸기위해 클래스를 추출하고 파라미터 리스트를 단순하게 한다. 결국 공통적으로 호출 되는 몇몇개의 클래스의 필드나 파라미터에 대해서 하나의 클래스로 추출하는 것을 말한다.

### 기본 타입에 대한 강박관념

int, float 같은 기본적인 타입에 대한 강박을 버리고 객체의 영역에서 타입을 활용하라. 여러 가지 방법을 통해 기본 타입에 대해서 객체화할 수 있다.

### switch 문

switch문은 기본적으로 중복적인 내용을 포괄하고 있다. 객체지향의 다형성을 이용하여 중복성을 제거하고 상속 구조등을 이용하여 중복을 제거한다.

### 평행 상속 구조

평행상속구조는 산탄총 수술의 특별한 경우이다. 한 클래스의 서브클래스를 만들면 다른 클래스에서도 서브 클래스를 만들어야 할 때 한쪽 구조의 인스턴스가 다른 쪽 구조의 인스턴스를 참조하도록 만들어 참조하는 쪽의 상속구조를 제거할 수 있다.

### 게으른 클래스

리팩토링 과정에서 혹은 변경사항에 의해서 작업이 사라진 클래스는 삭제해야 한다. 혹은 쓰이지 않는 서브클래스에 대해서도 정리하거나 인라인 클래스를 적용해야 한다.

### 추측성 일반화

게으른 클래스와 비슷한 내용. 하는 일이 없는 추상클래스에 대해 제거하라.

### 임시 필드

특정 알고리즘에서만 유용한 임시 필드를 만들어 사용할 때 필요한 변수와 메소드를 묶어 메소드 객체로 만든다. 특히 이런 필드들은 특정 알고리즘에서 사용될 때를 제외하고 값이 선언되지 않은 경우도 있기 때문에 구분해줄 필요가 있다.

### 메시지 체인

클라이언트가 클래스 구조와 결합되어 메시지 체인이 발생할 때 중간 지점의 변화가 클라이언트 코드에 영향을 줄 수 있다. 체인의 여러 지점에 [Hide Delegate](#Hide-Delegate)를 사용하여 이 문제를 해결할 수 있다.

### 미들 맨

지나치게 캡슐화되어 다른 클래스로 위임을 하고 있는 메소드가 태반인 클래스가 있다면 그 클래스의 객체에 실제로 어떻게 돌아가는지 알려주어야 한다. inline 메소드를 추가하거나 서브클래스화하여 위임을 하나하나 따라갈 필요없이 기능을 확장할 수 있게 한다.

### 부적절한 친밀

두 클래스가 너무 연관되어 있다면 공통된 부분을 빼내어 **별도의 클래스**를 만들거나 **중개하는 다른 클래스**를 만들어야한다.

### 다른 인터페이스를 가진 대체 클래스

같은 작업을 하지만 다른 시그니처를 가지는 메소드에 대해 이름을 바꾼다. 이것으로도 부족하다면 프로토콜이 같아질 때까지 [Move Method](#Move-Method)를 이용하여 동작을 이동시킨다. 너무 많은 코드를 옮겨야 할 때는 [Extract Superclass](#Extract-Superclass)를 사용할 수 있다.

### 불완전한 라이브러리 클래스

라이브러리는 수정하기 어렵다. 라이브러리 클래스를 수정하고 싶다면 특별한 방법([Introduce Foreign Method](#Introduce-Foreign-Method), [Introduce Local Extension](#Introduce-Local-Extension)을 활용할 수 있다.

### 데이터 클래스

데이터와 **get/set** 메소드만 가지는 클래스가 있따. 보통 public으로 구현된다. 그렇다면 필드나 컬렉션에 대해서 캡슐화가 되어있지 않다면 적용하고, 값이 변경되면 안되는 필드에 대해 [Remove setting Method](#Remove-setting-Method)를 사용한다.

### 거부된 유산

서브클래스에서 부모 클래스의 모든 기능을 사용하고 싶지 않다면 상속구조가 잘못된 것이다. 사용되지 않는 메소드를 구분하는 형제 클래스를 만들어 메소드를 옮겨야 한다. 단, 여기서의 냄새는 희미해서 반드시 해야 한다고 생각할 필요는 없다. 하지만 수퍼클래스의 인터페이스를 거부하는 것은 심각한 문제이기 때문에 해결해야 한다.

### 주석

주석이 필요하다고 생각이 드는 코드, 혹은 주석이 심하게 들어가 있는 코드는 리팩토링이 필요한 경우일 수 있으니 코드를 먼저 확인해본다.

## 테스트

테스트 코드를 만들어 두는 것은 버그를 탐색하는 시간을 단축시켜주는 강력한 도구이다.

특히 XP(e**X**tream **P**rogramming)를 사용할 때 테스트는 아주 중요한 부분이다. 개발 과정 중간 중간에 코드에 문제가 있는지 테스트로 쉽게 확인할 수 있다.

## 리팩토링 카탈로그로

먼저 리팩토링은 완벽한 것이 아닌 유용한 것임을 염두해야 한다. 그리고 아래의 예시들은 분산 소프트웨어 환경에서의 round trip에 대해서는 고려하지 않았기 때문에 해당 환경에서는 다른 리팩토링을 적용해야 한다.

리팩토링의 형식에 대해 아래 5가지로 구성한다.</br>
- **이름 - 요약 - 동기 - 절차 - 예제**</br>

## 메소드 정리

리팩토링의 많은 부분이 메소드를 정리해서 코드를 적절하게 포장하는 것이다. 대부분의 문제는 긴 메소드에서 나온다.

### Extract Method

1. 요약: 그룹으로 함께 묶을 수 있는 코드조각이 있으면, 목적이 드러나는 이름을 짓고 별도의 메소드로 뽑아낸다.

2. 동기: 긴 메소드나 목적을 이해하기위해 주석이 필요한 코드를 메소드로 뽑아낸다. 뽑아내는 것이 코드를 명확하게 하면 이름이 더 길어지더라도 뽑아낸다.

3. 절차

- 메소드를 새로 만들고 의도를 잘 나타내는 **이름**을 정한다. 이해하기 쉬운 이름을 정하기 어렵다면 뽑아내지 않는 것이 낫다.
- 새로 만든 메소드에 코드를 복사한다.
- 기존의 지역변수는 파라미터로, 임시변수는 새로운 메소드에서 선언한다.
- **지역변수**의 값이 하나만 수정된다면 리턴값으로 받을 수 있는지 보고, 어렵거나 수정되는 지역변수가 두 개 이상일 경우 [Split Temporary Variable](#Split-Temporary-Variable)를 사용한다.
- **임시변수**는 [Replace Temp with Query](#Replace-Temp-with-Query)로 제거할 수 있다.
- 너무 많은 임시변수로 인해 난처하다면 [Replace Method with Method Object](#Replace-Method-with-Method-Object)을 사용한다.
- 지역변수를 다룬 후에는 컴파일을 한다.
- 테스트 해본다.

### Inline Method

1. 요약: 메소드 몸체가 메소드의 이름 만큼이나 명확할 때는, 호출하는 곳에 메소드의 몸체를 넣고 메소드를 삭제하라.

2. 동기: 메소드로 따로 분류하지 않아도 설명히 가능한 코드에 대해서 메소드를 제거하여 불필요한 인디렉션을 줄인다.

3. 절차

- 메소드가 다형성을 가지고 있어 서브클래스에서 메소드를 사용하고 있지는 않은지 확인한다.
- 메소드를 호출하고 있는 부분을 찾고 몸체로 교체한다.
- 컴파일과 테스트를 한다.
- 메소드의 정의를 제거한다.
- **재귀, 리턴 포인트가 여러 곳이거나, 접근자가 없을 때는 사용하지 않는 것이 좋다.**

### Inline Temp

1. 요약: 간단한 수식의 결과값을 가지는 임시변수가 있고, 그 임시변수가 다른 리팩토링을 하는데 방해가 되면, 임시변수를 수식으로 대체한다.

2. 동기: 아래의 Replace Temp with Query의 한 부분으로 메소드의 리턴값이 임시변수에 대입되는 경우에 사용한다. 임시변수가 [Extract Method](#Extract-Method)와 같은 다른 리팩토링에 방해가 되면 이 방법을 사용한다.

3. 절차

- 임시변수가 한번만 대입되고 있는지를 확인하기 위해 final로 선언한 다음 컴파일 한다.
- 임시변수를 참조하고 있는 곳을 찾고 우변의 수식(메소드 리턴)으로 바꾼다.
- 컴파일과 테스트를 한다.
- 임시변수의 선언을 제거한다.

### Replace Temp with Query

1. 요약: 어떤 수식의 결과값을 저장하기 위해서 임시변수를 사용하고 있다면, 수식을 메소드로 만들고 임시변수를 참조하는 곳을 메소드 호출로 바꾼다.

2. 동기: 임시변수를 제거하기 위해 사용된다. 클래스 코드가 더 깔끔해지고 만들어진 메소드를 다른 곳에서도 사용할 수 있다는 장점도있다. [Extract Method](#Extract-Method)를 사용하기 전의 필수 단계이다. 임시변수에 값이 한 번만 대입되고, 대입문을 만드는 수식이 부작용을 초래하지 않는 경우에 쉽게 사용할 수 있다. 다른 경우에는 [Split Temporary Variable](#Split-Temporary-Variable)이나 [Separate Query from Modifier](#Separate-Query-from-Modifier)를 먼저 적용하는 것이 쉬울 것이다. 루프를 돌며 임시변수가 어떤 결과를 모으는 경우 질의 메소드 안으로 몇몇 로직을 복사할 필요가 있다.

3. 절차

- 임시변수에 값이 한 번만 대입되는지 확인하기 위해 final로 선언한 다음 컴파일 한다.
- 대입문의 우변을 메소드로 추출한다. 기본적으로 private으로 만들고 다른 곳에서 사용하는 것이 좋을 것 같으면 그 때 수정한다.
- 추출할 때 부작용이 없는지 확인하여 있다면 [Separate Query from Modifier](#Separate-Query-from-Modifier)를 사용한다.
- 컴파일과 테스트를 한다.
- 위의 Inline Temp가 적용 가능한지 확인한다.

### Introduce Explaining Variable

1. 요약: 복잡한 수식이 있는 경우에, 수식의 결과 또는 일부에 자신의 목적을 잘 성명하는 이름으로된 임시변수를 사용하라.

2. 동기: 수식이 복잡해져 알아보기 어려울 때 도움이 된다. 특히 조건문에서 각각의 조건의 뜻을 잘 설명하는 이름을 변수로 만들어 사용할 때 유용하다. 수많은 지역변수 때문에 Extract Method를 사용하기 어려울 때 사용할 수 있다. 만약 [Replace Method with Method Object](#Replace-Method-with-Method-Object)를 사용하게 된다면 임시변수도 유용하다.

3. 절차

- final 변수를 선언하여 복잡한 수식의 일부를 이 변수에 대입한다.
- 복잡한 수식을 final 임시변수로 바꾼다.
- 컴파일과 테스트를 한다.

### Split Temporary Variable

1. 요약: 루프 안에 있는 변수나 Sum과 같은 collecting temporary variable 같은 경우가 아니고, 값을 여러 번 대입하는 임시변수의 경우에는 각각 **따로 임시변수를 만들어라.**

2. 동기: 한 가지 임시변수로 여러 번의 값을 대입하여 사용하는 경우에 적용한다. 분할 후에 [Replace Method with Method Object](#Replace-Method-with-Method-Object)를 각각 적용 가능하다.

3. 절차

- 임시변수가 처음 선언된 곳과 대입하는 곳에서 이름을 바꾼다.
- 새로 만든 임시변수를 final로 선언한다.
- 두 번째로 대입되는 곳 이전까지 임시변수를 참조하는 곳을 새로 만든 변수로 바꾼다.
- 각 단계를 반복하여 임시변수를 여러 개로 분할 한다.

### Remove Assignments to Parameters

1. 요약: 파라미터에 값을 대입하는 코드가 있으면, 대신 임시변수를 사용해라.

2. 동기: 파라미터로 넘겨진 객체로 읽는 작업은 괜찮지만 파라 미터를 통해 다른 객체를 참조하여 쓰는 작업을 하는 것은 좋지 않다. 파라미터는 전달된 그대로 쓰는 것이 훨씬 명확하고 **값에 의한 전달**과 **참조에 의한 전달**을 혼동하는 것을 막는다. 

3. 절차

- 파라미터를 위한 임시변수를 만든다.
- 파라미터에 값을 대입한 코드를 임시변수로 바꾼다.
- 컴파일과 테스트를 한다.

파라미터에 **final**을 붙이면 이 관례를 강제할 수 있다.

### Replace Method with Method Object



