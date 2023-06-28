import Dynamic.StationNotFound;
import com.zeroc.Ice.UserException;
import com.zeroc.Ice.UserExceptionFactory;

public class AddReadingGetReadingsExceptionFactory implements UserExceptionFactory {
    @Override
    public void createAndThrow(String typeId) throws StationNotFound {
        var e = new StationNotFound();
        if (e.ice_id().equals(typeId)) {
            throw e;
        }
    }
}
