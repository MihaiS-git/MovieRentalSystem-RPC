package org.example.movierentals.common.domain;

import java.util.List;

public class MovieListDTO {
    List<Movie> list1;
    List<Movie> list2;

    public MovieListDTO(List<Movie> list1, List<Movie> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    public List<Movie> getList1() {
        return list1;
    }

    public void setList1(List<Movie> list1) {
        this.list1 = list1;
    }

    public List<Movie> getList2() {
        return list2;
    }

    public void setList2(List<Movie> list2) {
        this.list2 = list2;
    }
}
