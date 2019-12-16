package webapp.dso.db;

import webapp.model.AppxModel;

import java.util.Map;

public interface AppxMapper {
    AppxModel appx_get();
    AppxModel appx_get2(int app_id);
}
