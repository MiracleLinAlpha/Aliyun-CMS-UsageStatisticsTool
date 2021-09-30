package util;

import java.io.IOException;

public class ProgressBar {

    public static int index = 0;
    public static String finish;
    public static String unFinish;
    public static String target;

    // 进度条粒度
    public static final int PROGRESS_SIZE = 50;
    public static int BITE = 2;

    public static String getNChar(int num, char ch){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < num; i++){
            builder.append(ch);
        }
        return builder.toString();
    }


    public static void printProgress_init() throws InterruptedException, IOException {
        cls();
        System.out.print("Progress:");

        finish = getNChar(index / BITE, '█');
        unFinish = getNChar(PROGRESS_SIZE - index / BITE, '—');
        target = String.format("%3d%%[%s%s]", index, finish, unFinish);
        System.out.print(target);


    }



    public static void printProgress_doing() throws InterruptedException, IOException {
        if(index >= 101)
            return;
        cls();
        finish = getNChar(index / BITE, '█');
        unFinish = getNChar(PROGRESS_SIZE - index / BITE, '—');

        target = String.format("%3d%%[%s%s]", index, finish, unFinish);
        System.out.print(getNChar(PROGRESS_SIZE + 6, '\b'));
        System.out.print("Progress:" + target);
        index += 10;
    }


    public static void cls() throws IOException, InterruptedException{
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }


//    public static void main(String[] args) throws InterruptedException {
//        printProgress_init();
//
//        for(int i=0;i<=110;i++) {
//            Thread.sleep(500);
//            printProgress_doing();
//        }
//    }

}
