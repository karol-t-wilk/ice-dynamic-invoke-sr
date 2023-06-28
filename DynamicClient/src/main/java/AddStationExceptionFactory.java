import Dynamic.StationAlreadyExists;
import Dynamic.StationNameEmpty;
import com.zeroc.Ice.InputStream;
import com.zeroc.Ice.OutputStream;
import com.zeroc.Ice.UserException;
import com.zeroc.Ice.UserExceptionFactory;

public class AddStationExceptionFactory implements UserExceptionFactory {
    @Override
    public void createAndThrow(String typeId) throws StationNameEmpty, StationAlreadyExists {
        var e1 = new StationNameEmpty();
        if (e1.ice_id().equals(typeId)) {
            throw e1;
        }
        var e2 = new StationAlreadyExists();
        if (e2.ice_id().equals(typeId)) {
            throw e2;
        }
    }
}
