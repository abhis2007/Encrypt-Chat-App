package com.example.routineapp;

public class encryptdecryptchat {
    private static int p=1000000007,q=7;
    private int t;

    private int gcd(int a,int b)
    {
        if(a>b) return gcd(a%b,b);
        return gcd(a,b%a);
    }


    static String encrypt(String input){
        int n=p*q;
        int t=(p-1)*(q-1);
        encKey(t);
        return "hello";
    }
    static int encKey(int t){
        int i=2;
        for(;i<=t;i++)
        {
//            if(gcd(i,t)==1) break;
        }
        return i;
    }
    private void decrypt(){}
}
