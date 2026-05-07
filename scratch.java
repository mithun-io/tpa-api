import com.fasterxml.jackson.databind.ObjectMapper;
public class scratch {
    public static void main(String[] args) {
        try {
            new ObjectMapper().readTree((String)null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("---");
        try {
            new ObjectMapper().readTree("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
