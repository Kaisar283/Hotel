package kz.andersen.java_intensive_13.statics;

public class ApartmentHQL {

    public static final String FIND_ALL_APARTMENT = "from Apartment";

    public static final String SORTED_BY_ID_HQL = "from Apartment order by id";

    public static final String SORT_BY_PRICE_HQL = "from Apartment order by price";

    public static final String SORT_BY_USERNAME_HQL = "SELECT a FROM Apartment a JOIN a.user u ORDER BY u.firstName";

    public static final String SORT_BY_RESERVATION_STATUS_HQL = "from Apartment order by isReserved desc";

}
