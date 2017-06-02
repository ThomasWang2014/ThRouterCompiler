package com.thfund.client.throuter.api.facade.template;

import com.thfund.client.router.model.RouteMeta;

import java.util.Map;

/**
 * @author WayneWang
 * @since 2017/6/1 15:45
 */

public interface IActivityFormerRoute {
    void loadInto(Map<RouteMeta, String> formerRoutesAtlas);
}
