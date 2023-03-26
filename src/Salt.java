import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * @author dev.rcastrucci
 * github: <a href="https://github.com/rcastrucci"> Github </a>
 */
public class Salt {

    private final String salt;
    private final String spice;

    public Salt() {
        this.salt = generateSpices().get(0);
        this.spice = generateSpices().get(1);
    }

    public String createSaltedKey(String privateKey) {
        return hash(privateKey+salt);
    }

    public boolean verifySaltedKey(String publicKey, String privateKey) {
        return (hash(privateKey+salt).equals(publicKey) || hash(privateKey+spice).equals(publicKey));
    }

    private String hash(final String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private ArrayList<String> generateSpices() {
        Long ts = new Date().getTime();
        return new ArrayList<>(Arrays.asList(
            String.valueOf(ts).substring(0, String.valueOf(ts).length() - 5),
            String.valueOf(ts+100000).substring(0, String.valueOf(ts+100000).length() - 5)
        ));
    }

}