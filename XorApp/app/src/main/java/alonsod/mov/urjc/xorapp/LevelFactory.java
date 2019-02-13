package alonsod.mov.urjc.xorapp;


public class LevelFactory {
    public static Level produce(int nlevel) {
        Level mylevel;
        switch (nlevel){
            case 0:
                mylevel = new Level0();
                return mylevel;
            case 1:
                mylevel = new Level1();
                return mylevel;
            default:
                mylevel = new Level0();
                return mylevel;
        }
    }
}
