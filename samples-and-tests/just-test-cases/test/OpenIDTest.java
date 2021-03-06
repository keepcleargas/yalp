import org.junit.*;
import java.util.*;
import yalp.test.*;
import models.*;

import yalp.libs.*;

public class OpenIDTest extends UnitTest {

    @Test
    public void normalization() {
        assertEquals("http://example.com/", OpenID.normalize("example.com"));
        assertEquals("http://example.com/", OpenID.normalize("http://example.com"));
        assertEquals("https://example.com/", OpenID.normalize("https://example.com/"));
        assertEquals("http://example.com/user", OpenID.normalize("http://example.com/user"));
        assertEquals("http://example.com/user/", OpenID.normalize("http://example.com/user/"));
        assertEquals("http://example.com/", OpenID.normalize("http://example.com/"));
    }

}
