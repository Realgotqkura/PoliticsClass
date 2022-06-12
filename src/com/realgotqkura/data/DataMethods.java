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
            bw.write(Integer.toString(SaveableData.VASKO));
            bw.newLine();
            bw.write(Integer.toString(SaveableData.MAGI));
            bw.newLine();
            bw.write(Integer.toString(SaveableData.EMO));
            bw.newLine();
            bw.write(Integer.toString(SaveableData.NAD_T));
            bw.newLine();
            bw.write(Integer.toString(SaveableData.DINAMIXO));
            bw.newLine();
            bw.write(Integer.toString(SaveableData.LUBUMIRA));
            bw.newLine();
            bw.write(Integer.toString(SaveableData.KRISTIAN));
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
                switch(index){
                    case 0:
                        SaveableData.GOLD = Integer.parseInt(scanner.nextLine());
                        break;
                    case 1:
                        SaveableData.VASKO = Integer.parseInt(scanner.nextLine());
                        break;
                    case 2:
                        SaveableData.MAGI = Integer.parseInt(scanner.nextLine());
                        break;
                    case 3:
                        SaveableData.EMO = Integer.parseInt(scanner.nextLine());
                        break;
                    case 4:
                        SaveableData.NAD_T = Integer.parseInt(scanner.nextLine());
                        break;
                    case 5:
                        SaveableData.DINAMIXO = Integer.parseInt(scanner.nextLine());
                        break;
                    case 6:
                        SaveableData.LUBUMIRA = Integer.parseInt(scanner.nextLine());
                        break;
                    case 7:
                        SaveableData.KRISTIAN = Integer.parseInt(scanner.nextLine());
                        break;
                    case 8:
                        SaveableData.GOLD = Integer.parseInt(scanner.nextLine());
                        break;
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
