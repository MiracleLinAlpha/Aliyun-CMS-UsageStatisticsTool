import entity.requestParams;


public class start {
    public static requestParams rp = new requestParams();


    public static void main(String[] args) {
        go Go = new go();
        try{
            cls();
            rp = Go.goCheckIn();
            if(!rp.getDisplayName().equals("null")) {
                if(!Go.goNavi(rp))
                    System.out.println("脚本运行错误！");
            }
            //错误退出
            System.out.println("请检查conf.json文件后重新运行！");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cls() throws Exception{
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }
}
