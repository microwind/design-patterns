import com.github.microwind.springwind.boot.WebApplicationType;
import com.github.microwind.springwind.boot.WebApplicationTypeDeductor;

public class TestWebApplicationTypeDeductor {
    public static void main(String[] args) {
        // Test console mode
        String[] consoleArgs = {"--console"};
        WebApplicationType consoleType = WebApplicationTypeDeductor.deduce(consoleArgs);
        System.out.println("Console args: " + consoleType);
        
        // Test web mode
        String[] webArgs = {"--web"};
        WebApplicationType webType = WebApplicationTypeDeductor.deduce(webArgs);
        System.out.println("Web args: " + webType);
        
        // Test no args (should default based on classpath)
        String[] noArgs = {};
        WebApplicationType noArgsType = WebApplicationTypeDeductor.deduce(noArgs);
        System.out.println("No args: " + noArgsType);
    }
}
