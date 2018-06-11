package com.vn.ntsc.core.views;

import com.vn.ntsc.di.components.ActivityMediaComponent;
import com.vn.ntsc.di.components.LiveStreamComponent;
import com.vn.ntsc.di.components.ModulesCommonComponent;
import com.vn.ntsc.di.components.TimelineComponent;
import com.vn.ntsc.di.modules.AppModule;
import com.vn.ntsc.di.modules.LiveStreamModule;
import com.vn.ntsc.di.modules.MediaModule;
import com.vn.ntsc.di.modules.NetModule;

/**
 * Định nghĩa các components cần sử dụng trong Activity
 * Created by nankai on 12/6/2017.
 */

interface IActivityComponentBuilder {

    /**
     * Component này chứa các module phổ biến hay được sử dụng thường xuyên trong app
     * module {@link NetModule} đang được mặc định default
     * Sử dụng 3 module {@link NetModule} vs @{@link AppModule}
     *
     * @return ModulesCommonComponent
     */
    ModulesCommonComponent getModulesCommonComponent();

    /**
     * 2 module {@link NetModule} vs @{@link AppModule}, {@link MediaModule} đang được mặc định default
     * Đang sử dụng 3 module {@link NetModule},@{@link AppModule} vs {@link MediaModule}
     *
     * @return ActivityMediaComponent
     */
    ActivityMediaComponent getMediaComponent();

    /**
     * Dùng để cài đặt các thông số của {@link MediaModule}
     * Đang sử dụng 3 module {@link NetModule},@{@link AppModule} vs {@link MediaModule}
     *
     * @param mediaModule {@link MediaModule}
     * @return ServiceMediaComponent
     */
    ActivityMediaComponent getMediaComponent(MediaModule mediaModule);

    /**
     * Dùng để cài đặt các thông số của {@link NetModule} và {@link MediaModule}
     * Đang sử dụng 3 module {@link NetModule},@{@link AppModule} vs {@link MediaModule}
     *
     * @param netModule   {@link NetModule}
     * @param mediaModule {@link MediaModule}
     * @return ServiceMediaComponent
     */
    ActivityMediaComponent getMediaComponent(NetModule netModule, MediaModule mediaModule);

    /**
     * Dùng để khai báo với DI class này sử dụng @{@link LiveStreamModule} vs @{@link AppModule}, {@link NetModule}
     *
     * @return LiveStreamComponent
     */
    LiveStreamComponent getLiveStreamComponent();

    /**
     * Dùng để khai báo với DI class này sử dụng @{@link com.vn.ntsc.di.modules.TimelineModule} vs @{@link AppModule}, {@link NetModule}
     *
     * @return TimelineComponent
     */
    TimelineComponent getTimelineComponent();
}
