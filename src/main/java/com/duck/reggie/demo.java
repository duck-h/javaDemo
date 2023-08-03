package com.duck.reggie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class demo {
    public static void main(String[] args) {
//        IntStream.range(0,10).forEach(System.out::println);


       /* List<String> names = Stream.of("a", "b", "c")
                .collect(Collectors.toList());
        List<String> names2= Arrays.asList("a","b","c");
        System.out.println(names.toString());
        System.out.println(names2.toString());*/

        //set會去除重複
        /*Set<String> name = Stream.of("duck", "jamie", "grace", "leo", "duck")
                .collect(Collectors.toSet());
        System.out.println(name.toString());*/


        List<String> names = Stream.of("a", "b", "c", "d")
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println(names.toString());
    }
}
