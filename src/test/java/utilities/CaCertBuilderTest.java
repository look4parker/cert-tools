package utilities;

import org.junit.Before;
import org.junit.Test;

public class CaCertBuilderTest {
    private CaCertBuilder caCertBuilder;

    @Before
    public void setUp() {
        caCertBuilder = new CaCertBuilder();
    }

    @Test
    public void testBuildCaCert() throws Exception {
        caCertBuilder.buildCaCert();
    }

}
