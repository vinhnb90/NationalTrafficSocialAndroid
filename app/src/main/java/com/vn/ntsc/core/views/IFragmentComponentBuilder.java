package com.vn.ntsc.core.views;

import com.vn.ntsc.di.components.LiveStreamComponent;
import com.vn.ntsc.di.components.ModulesCommonComponent;
import com.vn.ntsc.di.components.TimelineComponent;
import com.vn.ntsc.di.modules.AppModule;
import com.vn.ntsc.di.modules.LiveStreamModule;
import com.vn.ntsc.di.modules.NetModule;

/**
 * //Định nghĩa các components cần sử dụng trong fragment
 * Created by nankai on 12/6/2017.
 */

interface IFragmentComponentBuilder {


    /**
     * Component này chứa các module phổ biến hay được sử dụng thường xuyên trong app
     * module {@link NetModule} đang được mặc định default
     * Sử dụng 3 module {@link NetModule} vs @{@link AppModule}
     *
     * @return ModulesCommonComponent
     */
    ModulesCommonComponent getModulesCommonComponent();

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
