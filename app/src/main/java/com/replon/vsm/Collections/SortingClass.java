package com.replon.vsm.Collections;


import com.replon.vsm.Utility.ContentsCatalogue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortingClass {

    List<ContentsCatalogue> sortingClass = new ArrayList<>();

    public SortingClass(List<ContentsCatalogue> priceSort) {
        this.sortingClass = priceSort;
    }
    public List<ContentsCatalogue> sortPriceLowToHigh() {
        Collections.sort(sortingClass, ContentsCatalogue.priceCompareLowToHigh);
        return sortingClass;
    }

    public List<ContentsCatalogue> sortPriceHighToLow() {
        Collections.sort(sortingClass, ContentsCatalogue.priceCompareHighToLow);
        return sortingClass;
    }



}