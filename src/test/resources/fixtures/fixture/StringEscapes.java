package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;

@PactDslBodyBuilder
public class StringEscapes {
    @PactExample("quote: \" slash: \\ newline:\n tab:\t")
    public String text;

    @PactExample("\b")
    public char backspace;

    @PactExample("\f")
    public char formFeed;

    @PactExample("\001")
    public char control;

    @PactExample("'")
    public char quote;

    @PactExample("\\")
    public char backslash;

    @PactExample("\n")
    public char newline;
}
