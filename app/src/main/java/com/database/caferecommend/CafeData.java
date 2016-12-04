package com.database.caferecommend;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-11-25.
 */

public class CafeData implements Serializable{

    private int cafe_num;
    private int image;
    private int openTime;
    private int closeTime;
    private String address;
    private String name;
    private String tel;
    private int avg;
                        //이미지  이름     전화번호     주소      오픈시간    마감시간    평점    카페번호
    public CafeData(int image, String name, String tel,String address,int openTime,int closeTime,int avg, int cafe_num){
        this.image = image;
        this.name = name;
        this.tel = tel;
        this.address=address;
        this.openTime=openTime;
        this.closeTime=closeTime;
        this.avg=avg;
        this.cafe_num = cafe_num;
    }

    public int getCafe_num() {
        return cafe_num;
    }

    public void setCafe_num(int cafe_num) {
        this.cafe_num = cafe_num;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getTel() {
        return tel;
    }

    public String getAddress(){
        return address;
    }

    public int getOpen(){
        return openTime;
    }

    public int getClose(){
        return closeTime;
    }

    public int getAvg(){
        return avg;
    }



}

