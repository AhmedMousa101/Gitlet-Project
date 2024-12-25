package gitlet;
import java.io.File;
/** A class to handle and returns SHA of each file
 * @author Ahmed Mousa
 * */
public class SHA {
    public static String getSha(File file){
        return Utils.sha1(Utils.readContents(file));
    }
}
