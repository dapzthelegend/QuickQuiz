package com.dapzthelegend.quickquiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Values {
    public static List<Subject> subList = new ArrayList<>();
    static Subject sub1 = new Subject("Physics", R.drawable.physics);
    static Subject sub2 = new Subject("Chemistry", R.drawable.chemistry);
    static Subject sub3 = new Subject("Mathematics", R.drawable.mathematics);
    static Subject sub4 = new Subject("Programming", R.drawable.computer);
    static Subject sub5 = new Subject("Law", R.drawable.law);
    static Subject sub6 = new Subject("Geography", R.drawable.geography);
    static Subject sub7 = new Subject("Biology", R.drawable.biology);
    static Subject sub8 = new Subject("English", R.drawable.english);
    static int[] images = {R.drawable.chemistry,
            R.drawable.chemistry1,
            R.drawable.chemistry2,
            R.drawable.chemistry3,
            R.drawable.chemistry5,
            R.drawable.biology
    };


    public static List<Subject> getList(){
        subList.add(sub1);
        subList.add(sub2);
        subList.add(sub3);
        subList.add(sub4);
        subList.add(sub5);
        subList.add(sub6);
        subList.add(sub7);
        subList.add(sub8);



        return subList;

    };


    public static int getImages(){
        Random rand = new Random();
        return  images[rand.nextInt(6)];
    }

}
