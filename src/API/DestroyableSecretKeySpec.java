package API;

import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.util.Arrays;

public class DestroyableSecretKeySpec extends SecretKeySpec {
    public DestroyableSecretKeySpec( byte[] key, int offset, int len, String algorithm ) {
        super( key, offset, len, algorithm );
    }

    @Override
    public void destroy() {
        try {
            Field f = SecretKeySpec.class.getDeclaredField( "key" );
            f.setAccessible(true);
            byte[] key = (byte[]) f.get( this );
            Arrays.fill( key, (byte)0 );
        } catch( NoSuchFieldException | IllegalAccessException e ) {
            System.err.println("Cannot destroy secret key!");
        }
    }
}
