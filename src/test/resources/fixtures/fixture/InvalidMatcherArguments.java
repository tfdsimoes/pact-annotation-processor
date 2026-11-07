package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;

@PactDslBodyBuilder
public class InvalidMatcherArguments {
    @PactMatcher(value = PactMatcherKind.REGEX, regex = "[")
    public String malformedRegex;

    @PactMatcher(value = PactMatcherKind.REGEX, regex = "A+", example = "b")
    public String nonMatchingRegexExample;

    @PactMatcher(value = PactMatcherKind.URL, baseUrl = "https://example.test", pathFragments = {"orders"},
            example = "https://example.test/other")
    public String conflictingUrlExample;

    @PactMatcher(value = PactMatcherKind.URL, baseUrl = "https://example.test")
    public String missingUrlPath;

    @PactMatcher(value = PactMatcherKind.URL, pathFragments = {"orders"})
    public String missingUrlBase;

    @PactMatcher(value = PactMatcherKind.IP_V4, example = "127.0.0.1")
    public String customIpExample;

    @PactMatcher(value = PactMatcherKind.UUID, example = "not-a-uuid")
    public String invalidUuid;

    @PactMatcher(value = PactMatcherKind.HEX, example = "not-hex")
    public String invalidHex;

    @PactMatcher(value = PactMatcherKind.ID, example = "9223372036854775808")
    public long invalidId;

    @PactMatcher(value = PactMatcherKind.INCLUDES, example = "")
    public String emptyInclude;

    @PactMatcher(value = PactMatcherKind.STRING_TYPE, regex = ".*")
    public String unusedRegex;

    @PactMatcher(value = PactMatcherKind.STRING_TYPE, baseUrl = "https://example.test",
            pathFragments = {"orders"})
    public String unusedUrlArguments;

    @PactMatcher(value = PactMatcherKind.STRING_TYPE, format = "yyyy")
    public String unusedMatcherFormat;

    @PactExample(value = "text", format = "yyyy")
    public String unusedExampleFormat;
}
