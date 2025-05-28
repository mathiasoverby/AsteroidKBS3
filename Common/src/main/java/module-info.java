module Common {
    requires java.net.http;
    exports dk.sdu.cbse.common.services;
    exports dk.sdu.cbse.common.data;
    exports dk.sdu.cbse.common.util;

    uses dk.sdu.cbse.common.services.IGamePluginService;
    uses dk.sdu.cbse.common.services.IEntityProcessingService;
    uses dk.sdu.cbse.common.services.IPostEntityProcessingService;
}