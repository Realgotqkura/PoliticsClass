package com.realgotqkura.data;

import com.realgotqkura.utilities.Input;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DataMethods {

    private static File file;
    private static String path = "data";
    private static File existfile = new File("data");

    public static void createFile(){
        if(existfile == null){
            file = new File(path);
        }else{
            System.out.println("The File Already Exists!");
        }
    }

    public static void Save(){

        try{
            file = new File("data");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(Integer.toString(SaveableData.GOLD));
            bw.newLine();
            bw.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static File getFile(){
        return new File(path);
    }

    public static void load(){
        try {
            int index = 0;
            Scanner scanner = new Scanner(new File(path));
            while(scanner.hasNextLine()){
                if(index == 0){
                    SaveableData.GOLD = Integer.parseInt(scanner.nextLine());
                }
                index++;
            }
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    public static void SaveUponExit(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                Save();
            }
        }));
    }
}
