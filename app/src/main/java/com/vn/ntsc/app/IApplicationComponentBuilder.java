package com.vn.ntsc.app;

import com.vn.ntsc.di.components.LiveStreamComponent;
import com.vn.ntsc.di.components.ModulesCommonComponent;
import com.vn.ntsc.di.components.TimelineComponent;
import com.vn.ntsc.di.dependencies.AppComponent;
import com.vn.ntsc.di.dependencies.MediaComponent;
import com.vn.ntsc.di.modules.AppModule;
import com.vn.ntsc.di.modules.LiveStreamModule;
import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.di.modules.NetModule;
import com.vn.ntsc.di.modules.TimelineModule;

/**
 * Lơi cung cấp các Component cho service activity fragment là nhưng chỗ khác
 * Created by nankai on 12/6/2017.
 */

public interface IApplicationComponentBuilder {

    /**
     * module {@link NetModule} đang được mặc định default
     * Sử dụng 2 module {@link NetModule} vs @{@link AppModule}
     *
     * @return AppComponent
     */
    AppComponent getAppComponent();

    /**
     * module {@link NetModule} đang được mặc định default các thông số connectTimeout, readTimeout, writeTimeout
     * Sử dụng 2 module {@link NetModule} vs @{@link AppModule}
     *
     * @param url đặt lại url cho {@link NetModule}
     *
     * @return AppComponent
     */
    AppComponent getAppComponent(String url);

    /**
     * Sử dụng 2 module {@link NetModule} vs @{@link AppModule}
     *
     * @param url            đặt lại url cho {@link NetModule}
     * @param connectTimeout đặt lại connectTimeout cho {@link NetModule}
     * @param readTimeout    đặt lại readTimeout cho {@link NetModule}
     * @param writeTimeout   đặt lại writeTimeout cho {@link NetModule}
     *
     * @return AppComponent
     */
    AppComponent getAppComponent(String url, long connectTimeout, long readTimeout, long writeTimeout);

    /**
     * Chủ yếu dùng để tắt chế độ debug của {@link NetModule}
     * Sử dụng 3 module {@link NetModule},@{@link AppModule} vs {@link MediaModule}
     *
     * @param url             đặt lại url cho {@link NetModule}
     * @param connectTimeout  đặt lại connectTimeout cho {@link NetModule}
     * @param readTimeout     đặt lại readTimeout cho {@link NetModule}
     * @param writeTimeout    đặt lại writeTimeout cho {@link NetModule}
     * @param loggingPriority on off debug {@link NetModule}
     *
     * @return AppComponent
     */
    AppComponent getAppComponent(String url, long connectTimeout, long readTimeout, long writeTimeout, boolean loggingPriority);

    /**
     * Component này chứa các module phổ biến hay được sử dụng thường xuyên trong app
     * module {@link NetModule} đang được mặc định default
     * module {@link com.vn.ntsc.di.modules.SocketModule} đang được mặc định default
     * Sử dụng 3 module {@link NetModule} vs @{@link AppModule}
     *
     * @return ModulesCommonComponent
     */
    ModulesCommonComponent getModulesCommonComponent();

    /**
     * Component này chứa các module phổ biến hay được sử dụng thường xuyên trong app
     * module {@link NetModule} đang được mặc định default
     * module {@link com.vn.ntsc.di.modules.SocketModule} đang được mặc định default
     * Sử dụng 3 module {@link NetModule} vs @{@link AppModule}
     *
     * @param url đặt lại url cho {@link NetModule}
     *
     * @return ModulesCommonComponent
     */
    ModulesCommonComponent getModulesCommonComponent(String url);


    /**
     * Component này chứa các module phổ biến hay được sử dụng thường xuyên trong app
     * module {@link NetModule} đang được mặc định default
     * module {@link com.vn.ntsc.di.modules.SocketModule} đang được mặc định default
     * Sử dụng 3 module {@link NetModule} vs @{@link AppModule}
     *
     * @param url            đặt lại url cho {@link NetModule}
     * @param connectTimeout đặt lại connectTimeout cho {@link NetModule}
     * @param readTimeout    đặt lại readTimeout cho {@link NetModule}
     * @param writeTimeout   đặt lại writeTimeout cho {@link NetModule}
     *
     * @return ModulesCommonComponent
     */
    ModulesCommonComponent getModulesCommonComponent(String url, long connectTimeout, long readTimeout, long writeTimeout);

    /**
     * Component này chứa các module phổ biến hay được sử dụng thường xuyên trong app
     * module {@link NetModule} đang được mặc định default
     * module {@link com.vn.ntsc.di.modules.SocketModule} đang được mặc định default
     * Sử dụng 3 module {@link NetModule} vs @{@link AppModule}
     *
     * @param url             đặt lại url cho {@link NetModule}
     * @param connectTimeout  đặt lại connectTimeout cho {@link NetModule}
     * @param readTimeout     đặt lại readTimeout cho {@link NetModule}
     * @param writeTimeout    đặt lại writeTimeout cho {@link NetModule}
     * @param loggingPriority on off debug {@link NetModule}
     *
     * @return ModulesCommonComponent
     */
    ModulesCommonComponent getModulesCommonComponent(String url, long connectTimeout, long readTimeout, long writeTimeout, boolean loggingPriority);



    /**
     * 2 module {@link NetModule} và {@link MediaModule} đang được mặc định default
     * Sử dụng 3 module {@link NetModule},@{@link AppModule} vs {@link MediaModule}
     *
     * @return MediaComponent
     */
    MediaComponent getMediaComponent();

    /**
     * Dùng để cài đặt các thông số của {@link MediaModule}
     * Sử dụng 3 module {@link NetModule},@{@link AppModule} vs {@link MediaModule}
     *
     * @param mediaModule {@link MediaModule}
     * @return MediaComponent
     */
    MediaComponent getMediaComponent(MediaModule mediaModule);

    /**
     * Dùng để cài đặt các thông số của {@link NetModule}
     * Sử dụng 3 module {@link NetModule},@{@link AppModule} vs {@link MediaModule}
     *
     * @param netModule {@link NetModule}
     * @return MediaComponent
     */
    MediaComponent getMediaComponent(NetModule netModule);

    /**
     * Dùng để cài đặt các thông số của {@link NetModule} và {@link MediaModule}
     * Sử dụng 3 module {@link NetModule},@{@link AppModule} vs {@link MediaModule}
     *
     * @param netModule   {@link NetModule}
     * @param mediaModule {@link MediaModule}
     * @return ServiceMediaComponent
     */
    MediaComponent getMediaComponent(NetModule netModule, MediaModule mediaModule);

    /**
     * Sử dụng 2 module {@link NetModule} và {@link LiveStreamModule} đang được mặc định default
     * Đang sử dụng 3 module {@link NetModule},@{@link AppModule} vs {@link LiveStreamModule}
     *
     * @return LiveStreamComponent
     */
    LiveStreamComponent getLiveStreamComponent();

    /**
     * Sử dụng 2 module {@link NetModule} và {@link TimelineModule} đang được mặc định default
     * Đang sử dụng 3 module {@link NetModule},@{@link AppModule} vs {@link TimelineModule}
     *
     * @return TimelineComponent
     */
    TimelineComponent getTimelineComponent();
}
